/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-11
 */
package org.swisseph;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static swisseph.SweConst.*;

/**
 * The pure-Java {@code swe_fixstar*} against the native one, over the whole of
 * {@code sefstars.txt}.
 * <p>
 * Until 2026-11-23 the Java fixstar was the SE 2.07 algorithm: a unit vector, distance 1,
 * speed 0 and the {@code SEFLG_SPEED} bit cleared from the return. It is now a port of
 * {@code swi_fixstar_calc_from_record()} / {@code fixstar_calc_from_struct()} of 2.10.03 (real
 * distance from the parallax, computed speed, aberration of the speed from the Earth at
 * {@code t - dt}), with C's record splitting ({@code ",omeDra,..."}, a star without a
 * traditional name, had every field shifted by one) and C's FK4-to-FK5 conversion of the
 * speed (the 1950 star "Apex" was 1e-2 deg off at 0 CE).
 *
 * <p>
 * Tolerances, measured: positions agree to 1e-9 deg (stars near the celestial pole, in right
 * ascension; 1e-12 with {@code SEFLG_TRUEPOS}). The speed agrees to 3e-9 deg/day without the
 * light deflection; with it, to 4e-7 - C differentiates the deflection over 5e-7 day on
 * vectors 1e7..1e9 AU long, so its last digits are rounding, largest in longitude near the
 * ecliptic pole (delta Doradus).
 * <p>
 * {@code @Isolated}: the tidal acceleration is pinned, and it is JVM-global in the Java engine.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
@Isolated
public class FixstarParityTest extends AbstractTest {

    /** 0 CE, 1000 CE, J2000, 2026, 2300 */
    static final double[] DATES = {1721423.5, 2086307.5, 2451545.0, 2461041.5, 2561117.5};

    static final int B = SEFLG_SWIEPH | SEFLG_SPEED;
    static final int[] FLAGS = {
            B, B | SEFLG_EQUATORIAL, B | SEFLG_TRUEPOS, B | SEFLG_HELCTR, B | SEFLG_TOPOCTR,
            B | SEFLG_J2000 | SEFLG_NONUT, B | SEFLG_BARYCTR, B | SEFLG_XYZ, SEFLG_SWIEPH,
    };

    static final double POS_TOL = 2e-9, SPEED_TOL = 1e-6, DIST_REL_TOL = 1e-12;

    /** the sefstars.txt records, by sequential number (C stops at the last one with ERR) */
    static final int MAX_STAR = 1200;

    @Test
    void everyCatalogueStar_matchesNative_positionSpeedDistanceFlagsName() {
        ISwissEph cn = getSwephExp(), jv = getSwissEph();
        int compared = 0;
        try {
            pin(cn, jv);
            for (int n = 1; n <= MAX_STAR; n++) {
                for (double tjd : DATES) {
                    for (int iflag : FLAGS) {
                        String what = "star " + n + " jd " + tjd + " iflag " + iflag;
                        double[] xc = new double[6], xj = new double[6];
                        StringBuilder sc = new StringBuilder("" + n), sj = new StringBuilder("" + n);
                        StringBuilder ec = new StringBuilder(), ej = new StringBuilder();
                        int rc = cn.swe_fixstar(sc, tjd, iflag, xc, ec);
                        int rj = jv.swe_fixstar(sj, tjd, iflag, xj, ej);
                        assertEquals(rc, rj, what + " return; native '" + ec + "', Java '" + ej + "'");
                        if (rc < 0) continue;
                        assertEquals(sc.toString(), sj.toString(), what + " name");
                        same(what, xc, xj, (iflag & SEFLG_XYZ) != 0);
                        compared++;
                    }
                }
            }
        } finally {
            unpin(cn, jv);
        }
        assertTrue(compared > 40000, "compared " + compared);
    }

    /** swe_fixstar_mag: the same record splitting (",omeDra" threw "field 13 is not a double") */
    @Test
    void everyCatalogueStar_magnitudeAndName_matchNative() {
        ISwissEph cn = getSwephExp(), jv = getSwissEph();
        int compared = 0;
        for (int n = 1; n < MAX_STAR; n++) {
            double[] mc = new double[1], mj = new double[1];
            StringBuilder sc = new StringBuilder("" + n), sj = new StringBuilder("" + n);
            StringBuilder ec = new StringBuilder(), ej = new StringBuilder();
            int rc = cn.swe_fixstar_mag(sc, mc, ec);
            if (rc < 0) break;
            int rj = jv.swe_fixstar_mag(sj, mj, ej);
            assertEquals(rc, rj, "star " + n + " return; Java '" + ej + "'");
            assertEquals(sc.toString(), sj.toString(), "star " + n + " name");
            assertEquals(mc[0], mj[0], 0.0, "star " + n + " magnitude");
            compared++;
        }
        assertTrue(compared > 1000, "compared " + compared);
    }

    /**
     * Sidereal: in the traditional modes swe_fixstar subtracts the ayanamsa from the position
     * only, swe_fixstar2 from the speed too (C 2.10.03 does exactly that); the _ut forms take
     * C's plaus_iflag() flags. The rigorous ECL_T0 / SSY_PLANE modes are in
     * {@link RigorousSiderealParityTest}.
     */
    @Test
    void siderealStars_fixstarAndFixstar2_andUt_matchNative() {
        ISwissEph cn = getSwephExp(), jv = getSwissEph();
        String[] stars = {"Aldebaran", "Sirius", "Spica", "Regulus", "Antares", ",GP1958", ",GPol", ",SgrA*"};
        int[] modes = {SE_SIDM_LAHIRI, SE_SIDM_FAGAN_BRADLEY, SE_SIDM_TRUE_CITRA, SE_SIDM_GALEQU_IAU1958, SE_SIDM_J2000};
        try {
            pin(cn, jv);
            for (int mode : modes) {
                cn.swe_set_sid_mode(mode, 0, 0);
                jv.swe_set_sid_mode(mode, 0, 0);
                for (String star : stars) {
                    for (double tjd : DATES) {
                        for (int iflag : new int[]{B | SEFLG_SIDEREAL, B | SEFLG_SIDEREAL | SEFLG_EQUATORIAL}) {
                            for (int api = 0; api < 4; api++) {
                                String what = "api " + api + " " + star + " mode " + mode + " jd " + tjd + " iflag " + iflag;
                                double[] xc = new double[6], xj = new double[6];
                                StringBuilder sc = new StringBuilder(star), sj = new StringBuilder(star);
                                StringBuilder ec = new StringBuilder(), ej = new StringBuilder();
                                int rc, rj;
                                switch (api) {
                                    case 0:
                                        rc = cn.swe_fixstar(sc, tjd, iflag, xc, ec);
                                        rj = jv.swe_fixstar(sj, tjd, iflag, xj, ej);
                                        break;
                                    case 1:
                                        rc = cn.swe_fixstar2(sc, tjd, iflag, xc, ec);
                                        rj = jv.swe_fixstar2(sj, tjd, iflag, xj, ej);
                                        break;
                                    case 2:
                                        rc = cn.swe_fixstar_ut(sc, tjd, iflag, xc, ec);
                                        rj = jv.swe_fixstar_ut(sj, tjd, iflag, xj, ej);
                                        break;
                                    default:
                                        rc = cn.swe_fixstar2_ut(sc, tjd, iflag, xc, ec);
                                        rj = jv.swe_fixstar2_ut(sj, tjd, iflag, xj, ej);
                                }
                                assertEquals(rc, rj, what + " return; native '" + ec + "', Java '" + ej + "'");
                                assertEquals(sc.toString(), sj.toString(), what + " name");
                                same(what, xc, xj, false);
                            }
                        }
                    }
                }
            }
        } finally {
            cn.swe_set_sid_mode(SE_SIDM_FAGAN_BRADLEY, 0, 0);
            jv.swe_set_sid_mode(SE_SIDM_FAGAN_BRADLEY, 0, 0);
            unpin(cn, jv);
        }
    }

    private static void same(String what, double[] c, double[] j, boolean xyz) {
        if (xyz) {
            double r = Math.sqrt(c[0] * c[0] + c[1] * c[1] + c[2] * c[2]);
            for (int k = 0; k < 3; k++) {
                double dp = Math.toDegrees(Math.abs(c[k] - j[k]) / r);
                double dv = Math.toDegrees(Math.abs(c[k + 3] - j[k + 3]) / r);
                assertTrue(dp <= POS_TOL, what + " xyz[" + k + "] native " + c[k] + " vs Java " + j[k]);
                assertTrue(dv <= SPEED_TOL, what + " xyz[" + (k + 3) + "] native " + c[k + 3] + " vs Java " + j[k + 3]);
            }
            return;
        }
        for (int k = 0; k < 2; k++) {
            double d = Math.abs(c[k] - j[k]);
            if (k == 0 && d > 180) d = 360 - d;
            assertTrue(d <= POS_TOL, what + " [" + k + "] native " + c[k] + " vs Java " + j[k] + " (diff " + d + ")");
            double v = Math.abs(c[k + 3] - j[k + 3]);
            assertTrue(v <= SPEED_TOL, what + " [" + (k + 3) + "] native " + c[k + 3] + " vs Java " + j[k + 3] + " (diff " + v + ")");
        }
        assertTrue(Math.abs(c[2] - j[2]) <= DIST_REL_TOL * c[2],
                what + " distance native " + c[2] + " vs Java " + j[2]);
        assertTrue(Math.abs(c[5] - j[5]) <= 1e-8 * c[2],
                what + " distance speed native " + c[5] + " vs Java " + j[5]);
    }

    private static void pin(ISwissEph cn, ISwissEph jv) {
        cn.swe_set_tid_acc(SE_TIDAL_MOSEPH);
        jv.swe_set_tid_acc(SE_TIDAL_MOSEPH);
        cn.swe_set_topo(30.52, 50.45, 180);
        jv.swe_set_topo(30.52, 50.45, 180);
    }

    private static void unpin(ISwissEph cn, ISwissEph jv) {
        cn.swe_set_topo(0, 0, 0);
        jv.swe_set_topo(0, 0, 0);
        cn.swe_set_tid_acc(SE_TIDAL_AUTOMATIC);
        jv.swe_set_tid_acc(SE_TIDAL_AUTOMATIC);
    }
}
