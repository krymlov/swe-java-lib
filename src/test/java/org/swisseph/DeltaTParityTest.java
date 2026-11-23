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
 * Delta t with {@code iflag} ({@code swe_deltat_ex}), pure Java against native, and the functions
 * whose result depends on which tidal acceleration it takes.
 * <p>
 * Until 2026-11-23 the Java engine ignored {@code iflag}: it took the tidal acceleration of the
 * last {@code swi_set_tid_acc} (or DE404) for every call. At -3000 that put delta t up to 795 s
 * off, {@code swe_calc_ut} of the Moon with the Moshier fallback 0.11 deg, the sidereal time 0.8 s
 * and {@code utc_to_jd} 304 s. It is now the swephlib.c 2.10.03 {@code calc_deltat}:
 * {@code -1} takes the default tidal acceleration without saving it; any other flag takes that of
 * the ephemeris it names (the Moon file's DE number for SWIEPH, DE404 for Moshier) and saves it.
 * The {@code *_ut} functions retry when the ephemeris returned is not the one asked for, and
 * {@code swe_calc} closes the ephemeris files when the ephemeris changes, as the C does — so the
 * first Swiss Eph call after a Moshier one sees the default DE431 value, exactly as native.
 * <p>
 * {@code @Isolated}: the tidal acceleration is JVM-global in the Java engine.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
@Isolated
public class DeltaTParityTest extends AbstractTest {

    static final double[] DATES = {625673.5 /* -3000 */, 1757583.5 /* 100 */, 2086307.5 /* 1000 */,
            2268923.5 /* 1500 */, 2312752.5 /* 1620 */, 2415020.5 /* 1900 */, 2434500.5 /* 1953 */,
            2451545.0, 2461041.5, 2488069.5 /* 2100 */, 2670000.5};
    static final int[] FLAGS = {-1, 0, SEFLG_SWIEPH, SEFLG_MOSEPH, SEFLG_JPLEPH, SEFLG_SWIEPH | SEFLG_SPEED};
    static final double DT_TOL_S = 1e-6, TID_TOL = 1e-12, DEG_TOL = 1e-9, SID_TOL_S = 1e-7;

    private static double diff360(double a, double b) {
        double d = Math.abs(a - b);
        return d > 180 ? 360 - d : d;
    }

    /** deltat_ex for every flag and date; the saved tidal acceleration after each call */
    private static void compareDeltaT(ISwissEph cn, ISwissEph jv, String ctx) {
        for (int fl : FLAGS) {
            for (double jd : DATES) {
                String what = ctx + " iflag " + fl + " jd " + jd;
                StringBuilder e1 = new StringBuilder(), e2 = new StringBuilder();
                double d1 = cn.swe_deltat_ex(jd, fl, e1), d2 = jv.swe_deltat_ex(jd, fl, e2);
                assertTrue(Math.abs(d1 - d2) * 86400 <= DT_TOL_S, what + " delta t native " + d1 * 86400 + " s Java " + d2 * 86400 + " s");
                assertEquals(e1.toString(), e2.toString(), what + " serr");
                assertEquals(cn.swe_get_tid_acc(), jv.swe_get_tid_acc(), TID_TOL, what + " tid_acc after the call");
            }
        }
        for (double jd : DATES) {
            assertTrue(Math.abs(cn.swe_deltat(jd) - jv.swe_deltat(jd)) * 86400 <= DT_TOL_S, ctx + " swe_deltat jd " + jd);
        }
    }

    /** the -1 callers (sidtime, plain swe_houses, utc_to_jd) and the iflag ones (calc_ut, ayanamsa) */
    private static void compareCallers(ISwissEph cn, ISwissEph jv, String ctx) {
        for (double jd : DATES) {
            String what = ctx + " jd " + jd;
            assertTrue(Math.abs(cn.swe_sidtime(jd) - jv.swe_sidtime(jd)) * 3600 <= SID_TOL_S, what + " sidtime");
            double[] c1 = new double[13], c2 = new double[13], a1 = new double[10], a2 = new double[10];
            cn.swe_houses(jd, 50.45, 30.52, 'P', c1, a1);
            jv.swe_houses(jd, 50.45, 30.52, 'P', c2, a2);
            for (int i = 1; i <= 12; i++) {
                assertTrue(diff360(c1[i], c2[i]) <= DEG_TOL, what + " swe_houses cusp " + i + " native " + c1[i] + " Java " + c2[i]);
            }
            assertEquals(cn.swe_get_tid_acc(), jv.swe_get_tid_acc(), TID_TOL, what + " tid_acc after sidtime and swe_houses (-1 saves nothing)");
            for (int fl : new int[]{SEFLG_SWIEPH, SEFLG_MOSEPH, SEFLG_SWIEPH | SEFLG_SIDEREAL}) {
                double[] x1 = new double[6], x2 = new double[6];
                StringBuilder e1 = new StringBuilder(), e2 = new StringBuilder();
                int r1 = cn.swe_calc_ut(jd, SE_MOON, fl, x1, e1), r2 = jv.swe_calc_ut(jd, SE_MOON, fl, x2, e2);
                assertEquals(r1, r2, what + " calc_ut Moon fl " + fl + " return; native '" + e1 + "' Java '" + e2 + "'");
                assertTrue(diff360(x1[0], x2[0]) <= DEG_TOL, what + " calc_ut Moon fl " + fl + " native " + x1[0] + " Java " + x2[0]);
                assertEquals(cn.swe_get_tid_acc(), jv.swe_get_tid_acc(), TID_TOL, what + " tid_acc after calc_ut fl " + fl);
            }
            assertTrue(Math.abs(cn.swe_get_ayanamsa_ut(jd) - jv.swe_get_ayanamsa_ut(jd)) <= DEG_TOL, what + " get_ayanamsa_ut");
            double[] y1 = new double[1], y2 = new double[1];
            cn.swe_get_ayanamsa_ex_ut(jd, SEFLG_MOSEPH, y1, new StringBuilder());
            jv.swe_get_ayanamsa_ex_ut(jd, SEFLG_MOSEPH, y2, new StringBuilder());
            assertTrue(Math.abs(y1[0] - y2[0]) <= DEG_TOL, what + " get_ayanamsa_ex_ut Moshier");
        }
        for (int y : new int[]{-3000, 100, 1000, 1500, 1900, 2000, 2100, 2600}) {
            int cal = y < 1582 ? SE_JUL_CAL : SE_GREG_CAL;
            double[] d1 = new double[2], d2 = new double[2];
            cn.swe_utc_to_jd(y, 3, 1, 12, 0, 0, cal, d1, new StringBuilder());
            jv.swe_utc_to_jd(y, 3, 1, 12, 0, 0, cal, d2, new StringBuilder());
            assertTrue(Math.abs(d1[0] - d2[0]) * 86400 <= DT_TOL_S, ctx + " utc_to_jd TT year " + y);
            assertTrue(Math.abs(d1[1] - d2[1]) * 86400 <= DT_TOL_S, ctx + " utc_to_jd UT year " + y);
        }
    }

    @Test
    void deltatEx_everyFlag_freshEngines_matchNative() {
        ISwissEph cn = getSwephExp(), jv = getSwissEph();
        compareDeltaT(cn, jv, "fresh");
        compareCallers(cn, jv, "fresh");
    }

    @Test
    void deltatEx_everyFlag_moonFileOpen_matchNative() {
        ISwissEph cn = getSwephExp(), jv = getSwissEph();
        double[] xx = new double[6];
        cn.swe_calc_ut(2451545.0, SE_MOON, SEFLG_SWIEPH, xx, new StringBuilder());
        jv.swe_calc_ut(2451545.0, SE_MOON, SEFLG_SWIEPH, xx, new StringBuilder());
        compareDeltaT(cn, jv, "moon file open");
        compareCallers(cn, jv, "moon file open");
    }

    @Test
    void deltatEx_manualTidalAcceleration_isKept() {
        ISwissEph cn = getSwephExp(), jv = getSwissEph();
        cn.swe_set_tid_acc(SE_TIDAL_DE200);
        jv.swe_set_tid_acc(SE_TIDAL_DE200);
        try {
            compareDeltaT(cn, jv, "manual DE200");
            compareCallers(cn, jv, "manual DE200");
            assertEquals(SE_TIDAL_DE200, jv.swe_get_tid_acc(), 0, "a manual tidal acceleration survives every call");
        } finally {
            cn.swe_set_tid_acc(SE_TIDAL_AUTOMATIC);
            jv.swe_set_tid_acc(SE_TIDAL_AUTOMATIC);
        }
    }

    /** swe_calc closes the files on a change of ephemeris, so the Moon file's DE number is gone */
    @Test
    void calcUt_afterMoshier_takesDefaultTidalAcceleration_asNative() {
        ISwissEph cn = getSwephExp(), jv = getSwissEph();
        final double jd = 1757583.5; // 100 CE: DE431 vs DE441 tidal acceleration differ by 6e-3 deg of the Moon
        for (int fl : new int[]{SEFLG_SWIEPH, SEFLG_MOSEPH, SEFLG_SWIEPH, SEFLG_SWIEPH, SEFLG_MOSEPH, SEFLG_SWIEPH | SEFLG_SPEED}) {
            double[] x1 = new double[6], x2 = new double[6];
            int r1 = cn.swe_calc_ut(jd, SE_MOON, fl, x1, new StringBuilder());
            int r2 = jv.swe_calc_ut(jd, SE_MOON, fl, x2, new StringBuilder());
            assertEquals(r1, r2, "fl " + fl + " return");
            assertTrue(diff360(x1[0], x2[0]) <= DEG_TOL, "fl " + fl + " Moon native " + x1[0] + " Java " + x2[0]);
            assertEquals(cn.swe_get_tid_acc(), jv.swe_get_tid_acc(), TID_TOL, "fl " + fl + " tid_acc");
        }
    }
}
