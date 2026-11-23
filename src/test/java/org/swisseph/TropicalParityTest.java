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
 * {@code swe_calc()} of the pure-Java engine against the native one: every body, 0 CE to 2300,
 * geocentric, topocentric, heliocentric and barycentric, from the files and from Moshier.
 * <p>
 * What it pins, all of it fixed on 2026-11-23:
 * <ul>
 * <li>{@code FileData.read8()} swapped bytes by hand with a signed {@code >> 56}; for half of all
 * doubles the result was 256 ulp low. The start time of the Earth segments moved by 1.2e-7 day,
 * so the Earth was 0.3 km off and every geocentric body with it;</li>
 * <li>{@code AUNIT}: 2.10.03 uses the DE431 value, the port had DE406's (osculating apogee
 * 8.5e-7 deg, the Moon 1e-8);</li>
 * <li>{@code rot_back()}: its obliquity constants are 2.10.03's (the Moon 1.3e-5 deg, and the true
 * node and the osculating apogee with it);</li>
 * <li>a topocentric speed with aberration is taken from three positions ({@code use_speed3}); the
 * 2.07 scheme was off by up to 4e-3 deg/day for the Moon, 0.1 for the osculating apogee;</li>
 * <li>delta t from the table: {@code Y} is counted from 1 Jan 2000, 0h, in Julian years
 * ({@code deltat_aa()}); the port was 4e-4 s off at J2000 and 8e-3 s in 2300;</li>
 * <li>the leap second of 31 Dec 2016 was missing;</li>
 * <li>{@code swe_set_interpolate_nut()} was only recorded, never applied;</li>
 * <li>{@code swe_houses_ex()} ignored {@code SEFLG_NONUT}.</li>
 * </ul>
 * {@code @Isolated}: the tidal acceleration is pinned, and it is JVM-global in the Java engine.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
@Isolated
public class TropicalParityTest extends AbstractTest {

    /** 0 CE, 1000 CE, 1600, 1900, J2000, 2026, 2300 */
    static final double[] DATES = {1721423.5, 2086307.5, 2305447.6, 2415020.4, 2451545.0, 2461041.5, 2561117.5};

    static final int B = SEFLG_SPEED;
    static final int[] FLAGS = {
            0, B, B | SEFLG_EQUATORIAL, B | SEFLG_TRUEPOS, B | SEFLG_NOABERR, B | SEFLG_NOGDEFL,
            B | SEFLG_NONUT, B | SEFLG_J2000 | SEFLG_NONUT, B | SEFLG_ICRS | SEFLG_J2000 | SEFLG_NONUT,
            B | SEFLG_HELCTR, B | SEFLG_BARYCTR, B | SEFLG_RADIANS, SEFLG_SPEED3,
            SEFLG_TOPOCTR, B | SEFLG_TOPOCTR, B | SEFLG_TOPOCTR | SEFLG_EQUATORIAL,
            B | SEFLG_TOPOCTR | SEFLG_TRUEPOS, B | SEFLG_TOPOCTR | SEFLG_NOABERR,
            B | SEFLG_TOPOCTR | SEFLG_J2000 | SEFLG_NONUT,
    };

    static final int[] BODIES = {
            SE_SUN, SE_MOON, SE_MERCURY, SE_VENUS, SE_MARS, SE_JUPITER, SE_SATURN, SE_URANUS, SE_NEPTUNE, SE_PLUTO,
            SE_MEAN_NODE, SE_TRUE_NODE, SE_MEAN_APOG, SE_OSCU_APOG, SE_EARTH, SE_CHIRON, SE_PHOLUS, SE_CERES,
            SE_PALLAS, SE_JUNO, SE_VESTA, SE_INTP_APOG, SE_INTP_PERG, SE_CUPIDO, SE_POSEIDON,
    };

    @Test
    void everyBody_fromTheFiles_matchesNative() {
        compare(SEFLG_SWIEPH, 1e-10, 3e-7, 1e-12, 1e-9);
    }

    /**
     * Moshier speeds are differences over 1e-4 day, so a 1e-13 deg rounding difference becomes
     * 1e-9 deg/day, the true node and the osculating apogee divide that by sin i and e (2e-8 deg),
     * and the topocentric speed differences it once more (4e-6 deg/day for Pluto).
     */
    @Test
    void everyBody_fromMoshier_matchesNative() {
        compare(SEFLG_MOSEPH, 5e-8, 1e-5, 1e-9, 1e-6);
    }

    private static void compare(int ephe, double posTol, double speedTol, double distRelTol, double distSpeedTol) {
        ISwissEph cn = getSwephExp(), jv = getSwissEph();
        int compared = 0;
        StringBuilder bad = new StringBuilder();
        try {
            pin(cn, jv);
            for (int ipl : BODIES) {
                for (double tjd : DATES) {
                    for (int fl : FLAGS) {
                        int iflag = ephe | fl;
                        String what = "ipl " + ipl + " jd " + tjd + " iflag " + iflag;
                        double[] xc = new double[6], xj = new double[6];
                        StringBuilder ec = new StringBuilder(), ej = new StringBuilder();
                        int rc = cn.swe_calc(tjd, ipl, iflag, xc, ec), rj = jv.swe_calc(tjd, ipl, iflag, xj, ej);
                        if (rc != rj) {
                            bad.append(what).append(" return native ").append(rc).append(" '").append(ec)
                                    .append("' vs Java ").append(rj).append(" '").append(ej).append("'\n");
                            continue;
                        }
                        if (rc < 0) continue;
                        double u = (fl & SEFLG_RADIANS) != 0 ? 180 / Math.PI : 1;
                        for (int k = 0; k < 2; k++) {
                            double d = Math.abs(xc[k] - xj[k]) * u;
                            if (k == 0 && d > 180) d = 360 - d;
                            if (!(d <= posTol)) bad.append(what).append(" [").append(k).append("] diff ").append(d).append('\n');
                            double v = Math.abs(xc[k + 3] - xj[k + 3]) * u;
                            if (!(v <= speedTol)) bad.append(what).append(" [").append(k + 3).append("] diff ").append(v).append('\n');
                        }
                        double r = Math.abs(xc[2] - xj[2]);
                        if (!(r <= distRelTol * xc[2])) bad.append(what).append(" [2] diff ").append(r).append('\n');
                        double w = Math.abs(xc[5] - xj[5]);
                        if (!(w <= distSpeedTol * Math.max(1, xc[2]))) bad.append(what).append(" [5] diff ").append(w).append('\n');
                        compared++;
                    }
                }
            }
        } finally {
            unpin(cn, jv);
        }
        assertEquals("", bad.toString());
        assertTrue(compared > 2000, "compared " + compared);
    }

    /**
     * swe_set_interpolate_nut(): nutation from three points in 1-day steps. The port only recorded
     * the flag (up to 3e-4" in position, 4e-3"/day in speed).
     */
    @Test
    void everyBody_withInterpolatedNutation_matchesNative() {
        ISwissEph cn = getSwephExp(), jv = getSwissEph();
        try {
            cn.swe_set_interpolate_nut(1);
            jv.swe_set_interpolate_nut(1);
            compare(SEFLG_SWIEPH, 1e-10, 3e-7, 1e-12, 1e-9);
            for (int i = 0; i < 40; i++) {
                double tjd = 2451545.0 + i * 0.37;
                double[] c = new double[6], j = new double[6];
                cn.swe_calc(tjd, SE_ECL_NUT, SEFLG_SWIEPH, c, new StringBuilder());
                jv.swe_calc(tjd, SE_ECL_NUT, SEFLG_SWIEPH, j, new StringBuilder());
                for (int k = 0; k < 4; k++) assertEquals(c[k], j[k], 1e-13 * Math.max(1, Math.abs(c[k])), "jd " + tjd + " [" + k + "]");
            }
        } finally {
            cn.swe_set_interpolate_nut(0);
            jv.swe_set_interpolate_nut(0);
        }
    }

    /**
     * Cusps and angles: the port ignored SEFLG_NONUT (up to 9e-3 deg). Placidus is iterated, and
     * the two engines stop at slightly different points (2e-7 deg).
     */
    @Test
    void houses_tropicalSiderealAndNonut_matchNative() {
        ISwissEph cn = getSwephExp(), jv = getSwissEph();
        try {
            pin(cn, jv);
            cn.swe_set_sid_mode(SE_SIDM_LAHIRI, 0, 0);
            jv.swe_set_sid_mode(SE_SIDM_LAHIRI, 0, 0);
            for (char hsys : "PKORCEBWTMX".toCharArray()) {
                for (int iflag : new int[]{0, SEFLG_NONUT, SEFLG_SIDEREAL, SEFLG_SIDEREAL | SEFLG_NONUT}) {
                    for (double tjd : DATES) {
                        String what = "hsys " + hsys + " iflag " + iflag + " jd " + tjd;
                        double[] c = new double[37], j = new double[37], ac = new double[10], aj = new double[10];
                        assertEquals(cn.swe_houses_ex(tjd, iflag, 49.5, 12.05, hsys, c, ac),
                                jv.swe_houses_ex(tjd, iflag, 49.5, 12.05, hsys, j, aj), what);
                        for (int k = 1; k <= 12; k++) assertDeg(c[k], j[k], 5e-7, what + " cusp " + k);
                        for (int k = 0; k < 8; k++) assertDeg(ac[k], aj[k], 1e-9, what + " ascmc " + k);
                    }
                }
            }
        } finally {
            cn.swe_set_sid_mode(SE_SIDM_FAGAN_BRADLEY, 0, 0);
            jv.swe_set_sid_mode(SE_SIDM_FAGAN_BRADLEY, 0, 0);
            unpin(cn, jv);
        }
    }

    private static void assertDeg(double c, double j, double tol, String what) {
        double d = Math.abs(c - j);
        if (d > 180) d = 360 - d;
        assertTrue(d <= tol, what + " native " + c + " vs Java " + j);
    }

    /** delta t over the whole range, with the same tidal acceleration on both sides */
    @Test
    void deltaT_matchesNative_everyYear() {
        ISwissEph cn = getSwephExp(), jv = getSwissEph();
        try {
            pin(cn, jv);
            for (int y = -3000; y <= 3000; y++) {
                double tjd = 2451545.0 + (y - 2000) * 365.2425 + 17.3;
                double c = cn.swe_deltat(tjd) * 86400, j = jv.swe_deltat(tjd) * 86400;
                assertEquals(c, j, 1e-9 * Math.max(1, Math.abs(c)), "delta t (s) in " + y);
            }
        } finally {
            unpin(cn, jv);
        }
    }

    /** UTC: the leap second table ends with 31 Dec 2016 on both sides */
    @Test
    void utcToJd_matchesNative_acrossTheLastLeapSecond() {
        ISwissEph cn = getSwephExp(), jv = getSwissEph();
        int[][] days = {{1973, 1, 1}, {2012, 7, 1}, {2015, 7, 1}, {2016, 12, 31}, {2017, 1, 1}, {2026, 11, 23}};
        try {
            pin(cn, jv);
            for (int[] d : days) {
                double[] c = new double[2], j = new double[2];
                int rc = cn.swe_utc_to_jd(d[0], d[1], d[2], 0, 0, 10.5, SE_GREG_CAL, c, new StringBuilder());
                int rj = jv.swe_utc_to_jd(d[0], d[1], d[2], 0, 0, 10.5, SE_GREG_CAL, j, new StringBuilder());
                String what = d[0] + "-" + d[1] + "-" + d[2];
                assertEquals(rc, rj, what);
                assertEquals(c[0], j[0], 1e-9, what + " ET");
                assertEquals(c[1], j[1], 1e-9, what + " UT1");
            }
        } finally {
            unpin(cn, jv);
        }
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
