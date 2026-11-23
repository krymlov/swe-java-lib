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
 * Houses of every house system, pure Java against native: {@code swe_houses_ex2} (cusps, ascmc
 * and their speeds) tropical and sidereal, {@code swe_houses_armc_ex2}, {@code swe_house_pos} and
 * {@code swe_house_name}.
 * <p>
 * Until 2026-11-23 the Java engine was the SE 2.07 port of {@code swehouse.c}: the speeds were a
 * central difference over one second (off by up to 6e-3 deg/day, and 0 for P/K/G inside the polar
 * circle), the systems D/F/I/i/J/L/N/Q/S were missing (Placidus came back instead), and the
 * Gauquelin sectors were up to 167 deg off there. It is now a port of the swisseph-2026 2.10.03
 * {@code swehouse.c} with its analytic {@code cusp_speed}; measured: cusps 3.7e-12 deg, speeds
 * 1.6e-10 deg/day.
 * <p>
 * {@code swe_house_pos} of Krusinski ('U') is not compared at the equator: the C moves
 * {@code geolat} to 1e-10 there, which amplifies the libm-vs-StrictMath rounding to 8e-5 of a
 * house (5e-12 at 0.001 deg).
 * <p>
 * {@code @Isolated}: the tidal acceleration and the sidereal mode are JVM-global in the Java engine.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
@Isolated
public class HouseParityTest extends AbstractTest {

    static final String SYSTEMS = "ABCDEFGHIiJKLMNOPQRSTUVWXY";
    static final double[] LATS = {0, 23.4, 50.45, -33.9, 64.0, 70.5, -80};
    static final double[] DATES = {2086307.5, 2451545.0, 2461041.5, 2561117.5};
    static final double POS_TOL = 1e-9, SPEED_TOL = 1e-8, HOUSE_POS_TOL = 1e-9;

    private static double diff360(double a, double b) {
        double d = Math.abs(a - b);
        return d > 180 ? 360 - d : d;
    }

    private static int compareHouses(ISwissEph cn, ISwissEph jv, int iflag, String ctx) {
        int compared = 0;
        double full = (iflag & SEFLG_RADIANS) != 0 ? 2 * Math.PI : 360;
        for (char h : SYSTEMS.toCharArray()) {
            for (double lat : LATS) {
                for (double jd : DATES) {
                    String what = ctx + " hsys " + h + " lat " + lat + " jd " + jd;
                    double[] c1 = new double[37], c2 = new double[37], a1 = new double[10], a2 = new double[10];
                    double[] s1 = new double[37], s2 = new double[37], b1 = new double[10], b2 = new double[10];
                    StringBuilder e1 = new StringBuilder(), e2 = new StringBuilder();
                    int r1 = cn.swe_houses_ex2(jd, iflag, lat, 30.52, h, c1, a1, s1, b1, e1);
                    int r2 = jv.swe_houses_ex2(jd, iflag, lat, 30.52, h, c2, a2, s2, b2, e2);
                    assertEquals(r1, r2, what + " return; native '" + e1 + "', Java '" + e2 + "'");
                    int n = h == 'G' ? 36 : 12;
                    for (int i = 1; i <= n; i++) {
                        double d = Math.abs(c1[i] - c2[i]);
                        if (d > full / 2) d = full - d;
                        assertTrue(d <= POS_TOL, what + " cusp " + i + " native " + c1[i] + " Java " + c2[i]);
                        assertTrue(Math.abs(s1[i] - s2[i]) <= SPEED_TOL, what + " cusp speed " + i + " native " + s1[i] + " Java " + s2[i]);
                    }
                    for (int i = 0; i < 8; i++) {
                        double d = Math.abs(a1[i] - a2[i]);
                        if (d > full / 2) d = full - d;
                        assertTrue(d <= POS_TOL, what + " ascmc " + i + " native " + a1[i] + " Java " + a2[i]);
                        assertTrue(Math.abs(b1[i] - b2[i]) <= SPEED_TOL, what + " ascmc speed " + i + " native " + b1[i] + " Java " + b2[i]);
                    }
                    compared++;
                }
            }
        }
        return compared;
    }

    @Test
    void housesEx2_everySystem_tropicalAndFlags_matchNative() {
        ISwissEph cn = getSwephExp(), jv = getSwissEph();
        cn.swe_set_tid_acc(SE_TIDAL_MOSEPH);
        jv.swe_set_tid_acc(SE_TIDAL_MOSEPH);
        int compared = 0;
        try {
            for (int fl : new int[]{0, SEFLG_RADIANS, SEFLG_NONUT}) {
                compared += compareHouses(cn, jv, fl, "iflag " + fl);
            }
        } finally {
            cn.swe_set_tid_acc(SE_TIDAL_AUTOMATIC);
            jv.swe_set_tid_acc(SE_TIDAL_AUTOMATIC);
        }
        assertEquals(3 * SYSTEMS.length() * LATS.length * DATES.length, compared);
    }

    @Test
    void housesEx2_everySystem_sidereal_matchNative() {
        ISwissEph cn = getSwephExp(), jv = getSwissEph();
        cn.swe_set_tid_acc(SE_TIDAL_MOSEPH);
        jv.swe_set_tid_acc(SE_TIDAL_MOSEPH);
        int compared = 0;
        try {
            int[] modes = {SE_SIDM_LAHIRI, SE_SIDM_LAHIRI | SE_SIDBIT_ECL_T0, SE_SIDM_LAHIRI | SE_SIDBIT_SSY_PLANE,
                    SE_SIDM_FAGAN_BRADLEY};
            int[] flags = {SEFLG_SIDEREAL, SEFLG_SIDEREAL, SEFLG_SIDEREAL,
                    SEFLG_SIDEREAL | SEFLG_NONUT | SEFLG_RADIANS};
            for (int k = 0; k < modes.length; k++) {
                cn.swe_set_sid_mode(modes[k], 0, 0);
                jv.swe_set_sid_mode(modes[k], 0, 0);
                compared += compareHouses(cn, jv, flags[k], "sidmode " + modes[k] + " iflag " + flags[k]);
            }
        } finally {
            cn.swe_set_sid_mode(SE_SIDM_FAGAN_BRADLEY, 0, 0);
            jv.swe_set_sid_mode(SE_SIDM_FAGAN_BRADLEY, 0, 0);
            cn.swe_set_tid_acc(SE_TIDAL_AUTOMATIC);
            jv.swe_set_tid_acc(SE_TIDAL_AUTOMATIC);
        }
        assertEquals(4 * SYSTEMS.length() * LATS.length * DATES.length, compared);
    }

    @Test
    void housesArmcEx2_housePos_houseName_everySystem_matchNative() {
        ISwissEph cn = getSwephExp(), jv = getSwissEph();
        double eps = 23.4392911;
        int compared = 0;
        for (char h : SYSTEMS.toCharArray()) {
            assertEquals(cn.swe_house_name(h), jv.swe_house_name(h), "house name " + h);
            int n = h == 'G' ? 36 : 12;
            for (double lat : LATS) {
                for (double armc = 3.7; armc < 360; armc += 29.3) {
                    String what = "hsys " + h + " lat " + lat + " armc " + armc;
                    double[] c1 = new double[37], c2 = new double[37], a1 = new double[10], a2 = new double[10];
                    double[] s1 = new double[37], s2 = new double[37], b1 = new double[10], b2 = new double[10];
                    a1[9] = a2[9] = 12.5;   // Sunshine: the declination of the Sun
                    int r1 = cn.swe_houses_armc_ex2(armc, lat, eps, h, c1, a1, s1, b1, new StringBuilder());
                    int r2 = jv.swe_houses_armc_ex2(armc, lat, eps, h, c2, a2, s2, b2, new StringBuilder());
                    assertEquals(r1, r2, what + " return");
                    for (int i = 1; i <= n; i++) {
                        assertTrue(diff360(c1[i], c2[i]) <= POS_TOL, what + " cusp " + i + " native " + c1[i] + " Java " + c2[i]);
                        assertTrue(Math.abs(s1[i] - s2[i]) <= SPEED_TOL, what + " cusp speed " + i + " native " + s1[i] + " Java " + s2[i]);
                    }
                    if (h == 'U' && lat == 0) continue;   // see the class comment
                    for (double lon = 1.1; lon < 360; lon += 13.7) {
                        for (double blat = -5; blat <= 5; blat += 5) {
                            StringBuilder e1 = new StringBuilder(), e2 = new StringBuilder();
                            double p1 = cn.swe_house_pos(armc, lat, eps, h, new double[]{lon, blat, 1, 0, 0, 0}, e1);
                            double p2 = jv.swe_house_pos(armc, lat, eps, h, new double[]{lon, blat, 1, 0, 0, 0}, e2);
                            double d = Math.abs(p1 - p2);
                            if (d > n / 2.0) d = n - d;
                            assertTrue(d <= HOUSE_POS_TOL, what + " lon " + lon + " lat " + blat + " house_pos native " + p1 + " Java " + p2);
                            assertEquals(e1.toString().trim(), e2.toString().trim(), what + " lon " + lon + " house_pos serr");
                            compared++;
                        }
                    }
                }
            }
        }
        assertTrue(compared > 40000, "compared " + compared);
    }
}
