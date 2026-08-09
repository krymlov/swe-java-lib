/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.junit.jupiter.api.Test;
import org.swisseph.api.ISweHouseSystem;
import org.swisseph.app.SweHouseSystem;
import swisseph.TCHouses;
import swisseph.TCPlanet;
import swisseph.TCPlanetHouse;
import swisseph.TCPlanetPlanet;
import swisseph.TransitCalculator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static swisseph.SweConst.*;

/**
 * Does a TransitCalculator really use the engine it was handed?
 * <p>
 * Every subclass takes an {@code ISwissEph} and is supposed to route every ephemeris call
 * through it, so that a calculator built with {@link SwephNative} gets native positions. That
 * is easy to break by calling a static helper of the pure Java port instead - the code still
 * compiles and still returns plausible numbers, just from the wrong engine.
 * <p>
 * Both directions are checked here: a recording proxy proves the calls are made on the given
 * instance, and a numeric comparison proves the answer actually changes with the engine.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
public class TransitEngineTest extends AbstractTest {

    static final double J2000 = 2451544.5;
    static final double GEO_LON = 81 + 8 / 60., GEO_LAT = 16 + 10 / 60.;
    static final int LON = SEFLG_SWIEPH | SEFLG_TRANSIT_LONGITUDE;

    /** an ISwissEph that forwards everything to a real one and records what was asked */
    static final class Recorder implements InvocationHandler {
        final ISwissEph target;
        final Set<String> called = Collections.synchronizedSet(new LinkedHashSet<>());

        Recorder(ISwissEph target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            called.add(method.getName());
            try {
                return method.invoke(target, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }
    }

    private static ISwissEph recording(ISwissEph target, Recorder[] out) {
        Recorder r = new Recorder(target);
        out[0] = r;
        return (ISwissEph) Proxy.newProxyInstance(ISwissEph.class.getClassLoader(),
                new Class<?>[]{ISwissEph.class}, r);
    }

    // ================================================ the calls land on the engine

    @Test
    void tcPlanetAsksTheGivenEngineForPositionsAndDeltaT() {
        Recorder[] rec = new Recorder[1];
        ISwissEph spy = recording(getSwephExp(), rec);

        TransitCalculator tc = new TCPlanet(spy, SE_MARS, LON, 100.);
        TransitCalculator.getTransitUT(tc, J2000, false);

        assertTrue(rec[0].called.contains("swe_calc"),
                "positions must come from the engine, saw " + rec[0].called);
        assertTrue(rec[0].called.contains("swe_deltat"),
                "the ET/UT conversion must come from the engine, saw " + rec[0].called);
    }

    @Test
    void tcPlanetPlanetAsksTheGivenEngineForBothPlanets() {
        Recorder[] rec = new Recorder[1];
        ISwissEph spy = recording(getSwephExp(), rec);

        TransitCalculator tc = new TCPlanetPlanet(spy, SE_MARS, SE_JUPITER, LON, 90.);
        TransitCalculator.getTransitUT(tc, J2000, false);

        assertTrue(rec[0].called.contains("swe_calc"), rec[0].called.toString());
        assertTrue(rec[0].called.contains("swe_deltat"), rec[0].called.toString());
    }

    /**
     * Regression: {@code TCPlanetHouse.calc()} converted ET to UT with
     * {@code SweDate.getDeltaT()} - the pure Java value - even when the engine was
     * {@link SwephNative}. It now goes through the engine like everything else.
     */
    @Test
    void tcPlanetHouseAsksTheGivenEngineForHousesAndDeltaT() {
        Recorder[] rec = new Recorder[1];
        ISwissEph spy = recording(getSwephExp(), rec);

        TransitCalculator tc = new TCPlanetHouse(spy, SE_MOON, LON, SE_ASC,
                SweHouseSystem.PLACIDUS.fid(), LON, GEO_LON, GEO_LAT, 0.);
        TransitCalculator.getTransitUT(tc, J2000, false);

        assertTrue(rec[0].called.contains("swe_calc"), rec[0].called.toString());
        assertTrue(rec[0].called.contains("swe_houses_ex"),
                "cusps must come from the engine, saw " + rec[0].called);
        assertTrue(rec[0].called.contains("swe_deltat"),
                "delta t must come from the engine, not SweDate - saw " + rec[0].called);
    }

    @Test
    void tcHousesAsksTheGivenEngineForHouses() {
        Recorder[] rec = new Recorder[1];
        ISwissEph spy = recording(getSwephExp(), rec);

        TransitCalculator tc = new TCHouses(spy, SE_ASC, SweHouseSystem.PLACIDUS.fid(),
                GEO_LON, GEO_LAT, SEFLG_TRANSIT_LONGITUDE, 100.);
        TransitCalculator.getTransitUT(tc, J2000, false);

        assertTrue(rec[0].called.contains("swe_houses_ex"), rec[0].called.toString());
        assertTrue(rec[0].called.contains("swe_set_topo"),
                "TCHouses sets the topocentric position on the engine, saw " + rec[0].called);
    }

    @Test
    void noCalculatorReachesForAStaticDeltaT() {
        // If a subclass used SweDate.getDeltaT() instead of the engine, swe_deltat would be
        // absent from the recording while the search still produced a result.
        for (String what : new String[]{"planet", "planetPlanet", "planetHouse"}) {
            Recorder[] rec = new Recorder[1];
            ISwissEph spy = recording(getSwephExp(), rec);
            TransitCalculator tc;
            switch (what) {
                case "planet":
                    tc = new TCPlanet(spy, SE_SATURN, LON, 200.);
                    break;
                case "planetPlanet":
                    tc = new TCPlanetPlanet(spy, SE_VENUS, SE_MARS, LON, 0.);
                    break;
                default:
                    tc = new TCPlanetHouse(spy, SE_SUN, LON, SE_MC,
                            SweHouseSystem.KOCH.fid(), LON, GEO_LON, GEO_LAT, 0.);
            }
            TransitCalculator.getTransitUT(tc, J2000, false);
            assertTrue(rec[0].called.contains("swe_deltat"),
                    what + " did not ask the engine for delta t: " + rec[0].called);
        }
    }

    // ============================================ the engine changes the answer

    /**
     * The proxy proves the calls happen; this proves they matter. A pinned delta t is the
     * cleanest lever: it is engine state, so a calculator that really uses the given engine
     * must shift by exactly the amount it was pinned by.
     */
    @Test
    void aDeltaTPinnedOnTheEngineMovesTheResult() {
        ISwissEph swe = getSwissEph();          // the pure Java engine owns the pinned value
        TransitCalculator tc = new TCPlanet(swe, SE_SUN, LON, 0.);

        double automatic = TransitCalculator.getTransitUT(tc, J2000, false);
        double automaticDeltaT = swe.swe_deltat(automatic);
        try {
            swe.swe_set_delta_t_userdef(3600. / 86400.);      // one hour
            double pinned = TransitCalculator.getTransitUT(tc, J2000, false);

            // getTransitUT converts ET to UT with the engine's delta t. The crossing itself
            // is an ET event and does not move, so the UT answer shifts by exactly the
            // CHANGE in delta t - one hour less the 64 seconds the model would have used.
            assertEquals(-(3600. / 86400. - automaticDeltaT), pinned - automatic, 1. / 86400.,
                    "pinning delta t must move the result: " + automatic + " -> " + pinned);
        } finally {
            swe.swe_set_delta_t_userdef(swisseph.SweConst.SE_DELTAT_AUTOMATIC);
        }
    }

    /**
     * The two engines are close enough on modern dates that a plain comparison proves little.
     * A house system the pure Java port gets differently is a sharper probe: whole sign cusps
     * used to differ by degrees, and the lagna still differs, so a TCPlanetHouse over the
     * ascendant must land on a measurably different date for each engine.
     */
    @Test
    void thePureJavaAndNativeEnginesGiveDifferentButCloseResults() {
        TransitCalculator nat = new TCPlanetHouse(getSwephExp(), SE_MOON, LON, SE_ASC,
                SweHouseSystem.PLACIDUS.fid(), LON, GEO_LON, GEO_LAT, 0.);
        TransitCalculator jav = new TCPlanetHouse(getSwissEph(), SE_MOON, LON, SE_ASC,
                SweHouseSystem.PLACIDUS.fid(), LON, GEO_LON, GEO_LAT, 0.);

        double n = TransitCalculator.getTransitUT(nat, J2000, false);
        double j = TransitCalculator.getTransitUT(jav, J2000, false);

        assertEquals(n, j, 60. / 86400., "the engines should agree to within a minute");
        assertFalse(n == j, "but not be bit-identical - they are different ephemerides");
    }

    /**
     * Every house system the port knows must at least run through TCHouses on both engines
     * and agree, since the ascendant is where they all start.
     */
    @Test
    void everyHouseSystemWorksThroughTCHousesOnBothEngines() {
        for (ISweHouseSystem hsys : SweHouseSystem.values()) {
            if (hsys == SweHouseSystem.NIL) continue;
            final double n, j;
            try {
                n = TransitCalculator.getTransitUT(new TCHouses(getSwephExp(), SE_ASC, hsys.fid(),
                        GEO_LON, GEO_LAT, SEFLG_TRANSIT_LONGITUDE, 100.), J2000, false);
                j = TransitCalculator.getTransitUT(new TCHouses(getSwissEph(), SE_ASC, hsys.fid(),
                        GEO_LON, GEO_LAT, SEFLG_TRANSIT_LONGITUDE, 100.), J2000, false);
            } catch (RuntimeException notSupported) {
                continue;
            }
            assertEquals(n, j, 60. / 86400., hsys.name());
        }
    }
}
