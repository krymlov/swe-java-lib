/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-09
 */

package org.swisseph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.swisseph.api.ISweObjects;
import org.swisseph.api.ISweObjectsOptions;
import org.swisseph.app.SweGeoLocation;
import org.swisseph.app.SweHouseSystem;
import org.swisseph.app.SweJulianDate;
import org.swisseph.app.SweObjects;
import org.swisseph.app.SweObjectsOptions;
import org.swisseph.app.SweRuntimeException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.swisseph.api.ISweObjects.LG;
import static org.swisseph.api.ISweObjects.NOT_CALCULATED;
import static org.swisseph.api.ISweObjects.OBJECTS_COUNT;
import static org.swisseph.api.ISweObjects.SY;

/**
 * {@code throwSweError = false} means <b>a Swiss Ephemeris failure is reported rather than
 * thrown</b>: through {@link ISweObjects#sweError()} and the {@link ISweObjects#NOT_CALCULATED}
 * sentinels. Nothing may throw {@link SweRuntimeException} while it is off.
 * <p>
 * It did not hold, in two independent ways, and both are pinned below.
 * <p>
 * <b>Two failure sites ignored the flag outright.</b> {@code calculatePlanetHousePosition()} and
 * {@code trueObliquity()} - both on {@code ISweObjects} rather than on {@code SweObjects}, which
 * is presumably how they were missed - threw unconditionally. A chart built with the flag
 * deliberately off still threw the moment a house position was asked for.
 * <p>
 * <b>And the flag could not be set in time.</b> It is a setter, so it cannot be reached until
 * the constructor has returned - and the four-argument constructor builds the ascendant before
 * that. So
 *
 * <pre>
 *   objects = new SweObjects(...);   // throws HERE
 *   objects.throwSweError(false);    // never reached
 * </pre>
 *
 * threw for exactly the charts the flag exists for. There is now a constructor that takes it.
 * <p>
 * The failure used throughout is Placidus near the pole, which Swiss Ephemeris cannot compute -
 * the one documented, reproducible {@code swe_houses_ex} error.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-09
 */
class ThrowSweErrorTest extends AbstractTest {

    /** 89.9N - Placidus and Koch have no solution this close to the pole */
    static final SweGeoLocation POLAR = new SweGeoLocation(0., 89.9, 0.);
    static final SweGeoLocation KYIV = new SweGeoLocation(30.5234, 50.4501, 180);

    static final double JD_2025 = 2460676.5;

    static final ISweObjectsOptions PLACIDUS = new SweObjectsOptions.Builder()
            .options(SweObjectsOptions.TRUECITRA_AYANAMSA_TRUE_NODE)
            .houseSystem(SweHouseSystem.PLACIDUS).build();

    private ISweObjects polarChart(final boolean throwSweError) {
        return new SweObjects(getSwissEph(), new SweJulianDate(JD_2025), POLAR, PLACIDUS,
                true, throwSweError);
    }

    // ------------------------------------------------------------------ the flag is respected

    @Test
    @DisplayName("by default a failure throws, which is what every existing caller relies on")
    void byDefaultAFailureThrows() {
        assertThrows(SweRuntimeException.class, () ->
                new SweObjects(getSwissEph(), new SweJulianDate(JD_2025), POLAR, PLACIDUS));

        assertThrows(SweRuntimeException.class, () -> polarChart(true).completeBuild());
    }

    @Test
    @DisplayName("with the flag off the same chart builds without throwing")
    void withTheFlagOffTheSameChartDoesNotThrow() {
        final ISweObjects objects = assertDoesNotThrow(() -> polarChart(false));
        assertDoesNotThrow(objects::completeBuild);
    }

    @Test
    @DisplayName("a house position does not throw either - it used to, unconditionally")
    void aHousePositionDoesNotThrow() {
        final ISweObjects objects = polarChart(false);
        objects.completeBuild();

        for (int objId = 0; objId < OBJECTS_COUNT; objId++) {
            final int id = objId;
            assertDoesNotThrow(() -> objects.calculatePlanetHousePosition(id),
                    "house position of object " + id);
            assertDoesNotThrow(() -> objects.calculatePlanetHouse(id),
                    "house of object " + id);
        }
    }

    @Test
    @DisplayName("and neither does the obliquity")
    void theObliquityDoesNotThrow() {
        assertDoesNotThrow(() -> polarChart(false).trueObliquity());
    }

    @Test
    @DisplayName("every accessor still answers, so a caller can read what did work")
    void everyAccessorStillAnswers() {
        final ISweObjects objects = polarChart(false).completeBuild();

        assertNotNull(objects.signs());
        assertNotNull(objects.houses());
        assertNotNull(objects.longitudes());
        assertNotNull(objects.latitudes());
        assertNotNull(objects.retrogrades());
        assertNotNull(objects.cusps());
        assertNotNull(objects.ascmc());
        assertNotNull(objects.sweJulianDate());
        assertNotNull(objects.sweLocation());
        assertNotNull(objects.sweOptions());

        assertEquals(OBJECTS_COUNT, objects.signs().length);
        assertEquals(OBJECTS_COUNT, objects.longitudes().length);
    }

    // ------------------------------------------------------------- what a failure reports back

    @Test
    @DisplayName("the ascendant reads NOT_CALCULATED, which is how the failure is reported")
    void theAscendantReadsNotCalculated() {
        final ISweObjects objects = polarChart(false).completeBuild();

        assertEquals(NOT_CALCULATED, objects.signs()[LG],
                "the houses failed, so there is no ascendant");
        assertFalse(objects.isCalculated(LG));
        assertTrue(objects.sweError().length() > 0, "and the reason is in sweError()");
    }

    @Test
    @DisplayName("how much of the chart survives depends on WHERE the ascendant was built")
    void howMuchSurvivesDependsOnWhereTheAscendantWasBuilt() {
        // completeBuild() opens with `if (sweError.length() != OK) return this;` - one failure
        // aborts the whole build, and sweError is never cleared. So:
        //
        //   ascendant built in the constructor -> sweError is already set when completeBuild()
        //                                         runs, and it does NOTHING: the chart comes
        //                                         back completely empty, every sign 0
        //   ascendant deferred                 -> completeBuild() starts clean, the ascendant
        //                                         fails, and the grahas are still computed
        //
        // swe_calc has no trouble at the pole; only swe_houses_ex does. So the second order
        // gives a caller its positions and the first gives it nothing, for the same chart and
        // the same flag - which is worth knowing before choosing a constructor.
        final ISweObjects eager = polarChart(false).completeBuild();
        assertEquals(NOT_CALCULATED, eager.signs()[SY],
                "built eagerly, the abort gate swallows the grahas too");
        assertEquals(0., eager.longitudes()[SY], 0.);

        final ISweObjects deferred = new SweObjects(getSwissEph(), new SweJulianDate(JD_2025),
                POLAR, PLACIDUS, false, false);
        deferred.completeBuild();

        assertTrue(deferred.longitudes()[SY] > 0. && deferred.longitudes()[SY] < 360.,
                "deferred, the Sun is computed: " + deferred.longitudes()[SY]);
        assertTrue(deferred.signs()[SY] >= 1 && deferred.signs()[SY] <= 12);
        assertEquals(NOT_CALCULATED, deferred.signs()[LG], "and the ascendant is still missing");
    }

    @Test
    @DisplayName("a house position without an ascendant is a number, and it means nothing")
    void aHousePositionWithoutAnAscendantMeansNothing() {
        // swe_houses_ex fills ascmc even when it answers ERR, so swe_house_pos gets an ARMC and
        // returns a perfectly well-formed position in [1, 13) computed from a house frame that
        // was never solved. Not throwing does not make it meaningful - a caller has to test
        // isCalculated(LG) before reading any of these.
        final ISweObjects objects = polarChart(false).completeBuild();

        final double position = objects.calculatePlanetHousePosition(SY);
        assertTrue(position >= 1. && position < 13.,
                "well-formed, and not to be trusted: " + position);
        assertFalse(objects.isCalculated(LG), "which isCalculated(LG) is how you find out");
    }

    // ------------------------------------------------------------------ the constructor window

    @Test
    @DisplayName("setting the flag after construction is too late for the ascendant")
    void settingTheFlagAfterConstructionIsTooLate() {
        // the trap this test exists for: the four-argument constructor builds the ascendant,
        // so the exception is thrown before any setter can be called
        assertThrows(SweRuntimeException.class, () -> {
            final ISweObjects objects =
                    new SweObjects(getSwissEph(), new SweJulianDate(JD_2025), POLAR, PLACIDUS);
            objects.throwSweError(false);   // never reached
        });
    }

    @Test
    @DisplayName("deferring the ascendant is the other way round it")
    void deferringTheAscendantIsTheOtherWayRoundIt() {
        // what a caller had to do before the constructor took the flag, and still may
        final ISweObjects objects = new SweObjects(getSwissEph(), new SweJulianDate(JD_2025),
                POLAR, PLACIDUS, false);
        objects.throwSweError(false);

        assertDoesNotThrow(objects::completeBuild);
        assertEquals(NOT_CALCULATED, objects.signs()[LG]);
    }

    @Test
    @DisplayName("the flag is readable and defaults to on")
    void theFlagIsReadableAndDefaultsToOn() {
        assertTrue(new SweObjects(getSwissEph(), new SweJulianDate(JD_2025), KYIV,
                SweObjectsOptions.TRUECITRA_AYANAMSA_TRUE_NODE).throwSweError());

        assertFalse(new SweObjects(getSwissEph(), new SweJulianDate(JD_2025), KYIV,
                SweObjectsOptions.TRUECITRA_AYANAMSA_TRUE_NODE, true, false).throwSweError());
    }

    // ------------------------------------------------------- a chart that works is unaffected

    @Test
    @DisplayName("a chart that succeeds is identical whichever way the flag is set")
    void aChartThatSucceedsIsIdenticalEitherWay() {
        // the flag must not be a second mode of operation - it decides only what happens on a
        // failure, and there is none here
        final ISweObjects throwing = new SweObjects(getSwissEph(), new SweJulianDate(JD_2025),
                KYIV, PLACIDUS, true, true).completeBuild();
        final ISweObjects quiet = new SweObjects(getSwissEph(), new SweJulianDate(JD_2025),
                KYIV, PLACIDUS, true, false).completeBuild();

        for (int objId = 0; objId < OBJECTS_COUNT; objId++) {
            assertEquals(throwing.longitudes()[objId], quiet.longitudes()[objId], 0.,
                    "longitude of object " + objId);
            assertEquals(throwing.signs()[objId], quiet.signs()[objId], "sign " + objId);
            assertEquals(throwing.houses()[objId], quiet.houses()[objId], "house " + objId);
            assertEquals(throwing.calculatePlanetHousePosition(objId),
                    quiet.calculatePlanetHousePosition(objId), 0., "position " + objId);
        }

        assertEquals(throwing.trueObliquity(), quiet.trueObliquity(), 0.);
        assertEquals(throwing.ayanamsa(), quiet.ayanamsa(), 0.);
    }

    @Test
    @DisplayName("whole sign has no polar problem at all, with or without the flag")
    void wholeSignHasNoPolarProblem() {
        // the sign distance from the ascendant needs no house solution, so the same place that
        // defeats Placidus is fine here - worth pinning so a future failure there is not
        // mistaken for the same cause
        assertDoesNotThrow(() -> new SweObjects(getSwissEph(), new SweJulianDate(JD_2025),
                POLAR, SweObjectsOptions.TRUECITRA_AYANAMSA_TRUE_NODE).completeBuild());
    }
}
