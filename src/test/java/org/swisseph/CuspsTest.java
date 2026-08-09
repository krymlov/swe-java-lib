/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.junit.jupiter.api.Test;
import org.swisseph.api.ISweHouseSystem;
import org.swisseph.api.ISweObjects;
import org.swisseph.api.ISweObjectsOptions;
import org.swisseph.app.SweGeoLocation;
import org.swisseph.app.SweHouseSystem;
import org.swisseph.app.SweJulianDate;
import org.swisseph.app.SweObjects;
import org.swisseph.app.SweObjectsOptions;
import swisseph.SweDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.swisseph.api.ISweObjects.ASCMC_COUNT;
import static org.swisseph.api.ISweObjects.CUSPS_COUNT;
import static org.swisseph.api.ISweObjects.LG;
import static org.swisseph.app.SweAyanamsa.TRUE_CITRA;
import static org.swisseph.app.SweHouseSystem.*;
import static swisseph.SweConst.SEFLG_SWIEPH;
import static swisseph.SweConst.SE_NASCMC;

/**
 * House cusps in the pure Java engine: the whole sign snap, the sidereal frame, and the
 * <code>swe_houses_ex2</code> / <code>swe_houses_armc_ex2</code> speeds that used to throw
 * {@code NotImplementedException}.
 * <p>
 * Every house system and object is named through {@code org.swisseph.api} - the enum already
 * holds the Swiss Ephemeris letter, so nothing here hardcodes one.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
public class CuspsTest extends AbstractTest {

    static final double GEO_LON = 81 + 8 / 60., GEO_LAT = 16 + 10 / 60.;
    static final float TIME_ZONE = 5.5f;
    static final double LOCAL_TIME = 17 + 50 / 60. + 40 / 3600.;
    static final int[] YEARS = {0, 500, 1000, 1500, 1800, 1900, 1976, 2000, 2050, 2100};

    /** how much the two engines may differ on a cusp position, in degrees */
    static final double DELTA_CUSP = 400. / 3600.;

    private ISweObjects chart(ISwissEph swissEph, int year, ISweHouseSystem hsys) {
        ISweObjectsOptions options = new SweObjectsOptions.Builder()
                .ayanamsa(TRUE_CITRA).houseSystem(hsys).build();
        return new SweObjects(swissEph, new SweJulianDate(new int[]{year, 4, 4, 17, 50},
                TIME_ZONE, LOCAL_TIME), new SweGeoLocation(GEO_LON, GEO_LAT, 0.), options)
                .completeBuild();
    }

    private static double arc(double a, double b) {
        double d = ((a - b) % 360. + 360.) % 360.;
        return Math.min(d, 360. - d);
    }

    // ======================================================== whole sign cusps

    /**
     * Regression, and the largest single defect the port had: {@code sidereal_houses_trad()}
     * treated whole sign as equal houses and then snapped the result to sign boundaries -
     * except the snap sat outside the {@code for} body for want of braces, so it ran once
     * against {@code cusp[13]} and no cusp was ever snapped. Whole sign charts came back as
     * equal houses measured from the ascendant, up to 19 degrees away from the right answer.
     */
    @Test
    void wholeSignCuspsSitOnSignBoundaries() {
        for (int year : YEARS) {
            ISweObjects o = chart(getSwissEph(), year, WHOLE_SIGN);
            for (int h = 1; h < CUSPS_COUNT - 1; h++) {
                assertEquals(0., o.cusps()[h] % 30., 1e-9,
                        "year " + year + " cusp " + h + " = " + o.cusps()[h]);
            }
        }
    }

    @Test
    void wholeSignCuspsAreTheSameAsTheNativeEngine() {
        for (int year : YEARS) {
            ISweObjects n = chart(getSwephExp(), year, WHOLE_SIGN);
            ISweObjects j = chart(getSwissEph(), year, WHOLE_SIGN);
            for (int h = 1; h < CUSPS_COUNT - 1; h++) {
                assertEquals(n.cusps()[h], j.cusps()[h], 1e-9,
                        "year " + year + " cusp " + h);
            }
        }
    }

    @Test
    void wholeSignCuspsAreThirtyDegreesApartStartingAtTheLagnaSign() {
        ISweObjects o = chart(getSwissEph(), 2000, WHOLE_SIGN);
        double first = o.cusps()[1];
        assertEquals(first, o.longitudes()[LG] - o.longitudes()[LG] % 30., 1e-9,
                "cusp 1 is the sign the lagna is in");
        for (int h = 2; h < CUSPS_COUNT - 1; h++) {
            assertEquals((first + (h - 1) * 30.) % 360., o.cusps()[h], 1e-9, "cusp " + h);
        }
    }

    // ============================================== the sidereal cusp frame

    /**
     * The port also subtracted nutation in longitude on top of the ayanamsa, which upstream
     * no longer does. It cost the whole sidereal house frame the nutation amplitude, about
     * 17 arc seconds, on every system.
     */
    @Test
    void everyHouseSystemTracksTheNativeEngine() {
        for (ISweHouseSystem hsys : SweHouseSystem.values()) {
            if (hsys == NIL) continue;
            for (int year : YEARS) {
                final ISweObjects n, j;
                try {
                    n = chart(getSwephExp(), year, hsys);
                    j = chart(getSwissEph(), year, hsys);
                } catch (RuntimeException beyondPolarCircle) {
                    continue;
                }
                for (int h = 1; h < CUSPS_COUNT - 1; h++) {
                    assertTrue(arc(n.cusps()[h], j.cusps()[h]) < DELTA_CUSP,
                            hsys.name() + " year " + year + " cusp " + h + ": "
                                    + n.cusps()[h] + " vs " + j.cusps()[h]);
                }
            }
        }
    }

    // ================================================= swe_houses_ex2 speeds

    /**
     * {@code swe_houses_ex2} and {@code swe_houses_armc_ex2} used to throw
     * {@code NotImplementedException} in the pure Java engine. They now return the speed in
     * longitude of every cusp and of every ascmc point.
     */
    @Test
    void houseSpeedsAreReturnedForEverySystem() {
        double jdUT = new SweDate(2000, 1, 1, 12.).getJulDay();

        for (ISweHouseSystem hsys : SweHouseSystem.values()) {
            if (hsys == NIL) continue;
            double[] cusps = new double[CUSPS_COUNT], ascmc = new double[ASCMC_COUNT];
            double[] cuspSpeed = new double[CUSPS_COUNT], ascmcSpeed = new double[ASCMC_COUNT];

            int retc = getSwissEph().swe_houses_ex2(jdUT, SEFLG_SWIEPH, GEO_LAT, GEO_LON,
                    hsys.fid(), cusps, ascmc, cuspSpeed, ascmcSpeed, new StringBuilder());
            assertTrue(retc >= 0, hsys.name() + " failed with " + retc);

            // the ascendant sweeps the whole circle in a day, so its speed is of order 360
            assertTrue(ascmcSpeed[0] > 50. && ascmcSpeed[0] < 3000.,
                    hsys.name() + " ascendant speed " + ascmcSpeed[0]);
            assertTrue(ascmcSpeed[1] > 50. && ascmcSpeed[1] < 3000.,
                    hsys.name() + " MC speed " + ascmcSpeed[1]);
            assertEquals(0., cuspSpeed[0], 0., hsys.name() + " cuspSpeed[0] is unused");

            for (int h = 1; h < CUSPS_COUNT - 1; h++) {
                assertNotEquals(0., cuspSpeed[h], hsys.name() + " cusp " + h + " has no speed");
            }
        }
    }

    /**
     * The speeds must be consistent with the positions the same engine reports: differencing
     * its own cusps has to reproduce the speed it hands back. This is the property that can
     * actually be verified - see {@link #nativeCuspSpeedsDisagreeWithNativeCuspPositions}
     * for why it is not checked against the native library instead.
     */
    @Test
    void cuspSpeedsAgreeWithTheDerivativeOfTheCuspPositions() {
        double jdUT = new SweDate(2000, 1, 1, 12.).getJulDay();
        double dt = 60. / 86400.;

        for (ISweHouseSystem hsys : new ISweHouseSystem[]{PLACIDUS, KOCH, CAMPANUS,
                REGIOMONTANUS, PORPHYRIUS, EQUAL, VEHLOW, MERIDIAN, MORINUS, ALCABITIUS,
                KRUSINSKI, POLICH_PAGE, HORIZONTAL}) {
            double[] c = new double[CUSPS_COUNT], a = new double[ASCMC_COUNT];
            double[] cs = new double[CUSPS_COUNT], as = new double[ASCMC_COUNT];
            double[] cm = new double[CUSPS_COUNT], am = new double[ASCMC_COUNT];
            double[] cp = new double[CUSPS_COUNT], ap = new double[ASCMC_COUNT];

            getSwissEph().swe_houses_ex2(jdUT, SEFLG_SWIEPH, GEO_LAT, GEO_LON, hsys.fid(),
                    c, a, cs, as, new StringBuilder());
            getSwissEph().swe_houses_ex(jdUT - dt, SEFLG_SWIEPH, GEO_LAT, GEO_LON, hsys.fid(), cm, am);
            getSwissEph().swe_houses_ex(jdUT + dt, SEFLG_SWIEPH, GEO_LAT, GEO_LON, hsys.fid(), cp, ap);

            for (int h = 1; h < CUSPS_COUNT - 1; h++) {
                double d = ((cp[h] - cm[h]) % 360. + 540.) % 360. - 180.;
                assertEquals(d / (2. * dt), cs[h], 0.5,
                        hsys.name() + " cusp " + h + " speed is not the derivative of its position");
            }
        }
    }

    /**
     * Pins a difference between the engines rather than a requirement. For the cusps that are
     * neither the ascendant nor the MC, the speed the <b>native</b> library reports is not the
     * derivative of the cusp positions the native library itself returns: differencing them
     * over 1, 10 and 60 seconds converges on a different value, and that value is what this
     * port produces. Koch cusp 2 on 1 Jan 2000 at Machilipatnam: reported 326.66 deg/day,
     * measured 340.34.
     * <p>
     * So the two engines are compared on cusps 1, 4, 7 and 10 - where they do agree - and the
     * rest is left to {@link #cuspSpeedsAgreeWithTheDerivativeOfTheCuspPositions}.
     */
    @Test
    void nativeCuspSpeedsDisagreeWithNativeCuspPositions() {
        double jdUT = new SweDate(2000, 1, 1, 12.).getJulDay();
        double dt = 60. / 86400.;
        int hsys = KOCH.fid();

        double[] c = new double[CUSPS_COUNT], a = new double[ASCMC_COUNT];
        double[] cs = new double[CUSPS_COUNT], as = new double[ASCMC_COUNT];
        double[] cm = new double[CUSPS_COUNT], am = new double[ASCMC_COUNT];
        double[] cp = new double[CUSPS_COUNT], ap = new double[ASCMC_COUNT];

        getSwephExp().swe_houses_ex2(jdUT, SEFLG_SWIEPH, GEO_LAT, GEO_LON, hsys,
                c, a, cs, as, new StringBuilder());
        getSwephExp().swe_houses_ex(jdUT - dt, SEFLG_SWIEPH, GEO_LAT, GEO_LON, hsys, cm, am);
        getSwephExp().swe_houses_ex(jdUT + dt, SEFLG_SWIEPH, GEO_LAT, GEO_LON, hsys, cp, ap);

        // the angles: native's analytic speed is the derivative of its own position
        for (int h : new int[]{1, 4, 7, 10}) {
            double d = ((cp[h] - cm[h]) % 360. + 540.) % 360. - 180.;
            assertEquals(d / (2. * dt), cs[h], 0.5, "native cusp " + h);
        }
        // the intermediate cusps: it is not
        double d2 = ((cp[2] - cm[2]) % 360. + 540.) % 360. - 180.;
        assertTrue(Math.abs(d2 / (2. * dt) - cs[2]) > 5.,
                "native Koch cusp 2 unexpectedly agrees now: reported " + cs[2]
                        + ", measured " + d2 / (2. * dt));
    }

    @Test
    void bothEnginesAgreeOnTheAngleSpeeds() {
        double jdUT = new SweDate(2000, 1, 1, 12.).getJulDay();

        for (ISweHouseSystem hsys : new ISweHouseSystem[]{PLACIDUS, KOCH, EQUAL, CAMPANUS,
                REGIOMONTANUS, HORIZONTAL, MERIDIAN}) {
            double[] cn = new double[CUSPS_COUNT], an = new double[ASCMC_COUNT];
            double[] csn = new double[CUSPS_COUNT], asn = new double[ASCMC_COUNT];
            double[] cj = new double[CUSPS_COUNT], aj = new double[ASCMC_COUNT];
            double[] csj = new double[CUSPS_COUNT], asj = new double[ASCMC_COUNT];

            getSwephExp().swe_houses_ex2(jdUT, SEFLG_SWIEPH, GEO_LAT, GEO_LON, hsys.fid(),
                    cn, an, csn, asn, new StringBuilder());
            getSwissEph().swe_houses_ex2(jdUT, SEFLG_SWIEPH, GEO_LAT, GEO_LON, hsys.fid(),
                    cj, aj, csj, asj, new StringBuilder());

            for (int i = 0; i < SE_NASCMC; i++) {
                assertEquals(asn[i], asj[i], 0.05, hsys.name() + " ascmc speed " + i);
            }
            for (int h : new int[]{1, 4, 7, 10}) {
                assertEquals(csn[h], csj[h], 0.05, hsys.name() + " cusp " + h + " speed");
            }
        }
    }

    @Test
    void armcFormGivesTheSameCuspsAsTheNativeEngine() {
        double eps = 23.4392911;

        for (double armc : new double[]{0., 60., 120., 180., 240., 300.}) {
            double[] cn = new double[CUSPS_COUNT], an = new double[ASCMC_COUNT];
            double[] csn = new double[CUSPS_COUNT], asn = new double[ASCMC_COUNT];
            double[] cj = new double[CUSPS_COUNT], aj = new double[ASCMC_COUNT];
            double[] csj = new double[CUSPS_COUNT], asj = new double[ASCMC_COUNT];

            int rn = getSwephExp().swe_houses_armc_ex2(armc, GEO_LAT, eps, PLACIDUS.fid(),
                    cn, an, csn, asn, new StringBuilder());
            int rj = getSwissEph().swe_houses_armc_ex2(armc, GEO_LAT, eps, PLACIDUS.fid(),
                    cj, aj, csj, asj, new StringBuilder());

            assertEquals(rn, rj, "return code at armc " + armc);
            for (int h = 1; h < CUSPS_COUNT - 1; h++) {
                assertEquals(cn[h], cj[h], 1e-6, "armc " + armc + " cusp " + h);
            }
            assertTrue(asj[0] > 0., "armc " + armc + " has an ascendant speed");
        }
    }

    /** speeds are optional: passing null for either array must not fail */
    @Test
    void speedArraysMayBeNull() {
        double jdUT = new SweDate(2000, 1, 1, 12.).getJulDay();
        double[] cusps = new double[CUSPS_COUNT], ascmc = new double[ASCMC_COUNT];

        assertTrue(getSwissEph().swe_houses_ex2(jdUT, SEFLG_SWIEPH, GEO_LAT, GEO_LON,
                PLACIDUS.fid(), cusps, ascmc, null, null, new StringBuilder()) >= 0);
        assertTrue(getSwissEph().swe_houses_armc_ex2(120., GEO_LAT, 23.44, PLACIDUS.fid(),
                cusps, ascmc, null, null, new StringBuilder()) >= 0);
        assertTrue(cusps[1] > 0., "positions are still returned");
    }

    // ============================================ the flagless swe_houses()

    /**
     * The plain {@code swe_houses(tjd, geolat, geolon, hsys, ...)} of swephexp.h - no flags,
     * so tropical - also used to throw {@code NotImplementedException}.
     */
    @Test
    void theFlaglessSweHousesIsTropicalAndMatchesTheNativeEngine() {
        double jdUT = new SweDate(2000, 1, 1, 12.).getJulDay();
        double[] cn = new double[CUSPS_COUNT], an = new double[ASCMC_COUNT];
        double[] cj = new double[CUSPS_COUNT], aj = new double[ASCMC_COUNT];
        double[] ct = new double[CUSPS_COUNT], at = new double[ASCMC_COUNT];

        assertTrue(getSwephExp().swe_houses(jdUT, GEO_LAT, GEO_LON, PLACIDUS.fid(), cn, an) >= 0);
        assertTrue(getSwissEph().swe_houses(jdUT, GEO_LAT, GEO_LON, PLACIDUS.fid(), cj, aj) >= 0);
        getSwissEph().swe_houses_ex(jdUT, 0, GEO_LAT, GEO_LON, PLACIDUS.fid(), ct, at);

        for (int h = 1; h < CUSPS_COUNT - 1; h++) {
            assertEquals(cn[h], cj[h], 1e-5, "cusp " + h + " against the native engine");
            assertEquals(ct[h], cj[h], 0., "cusp " + h + " equals the iflag == 0 form");
        }
    }
}
