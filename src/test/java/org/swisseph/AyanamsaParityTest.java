/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-11
 */
package org.swisseph;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static swisseph.SweConst.*;

/**
 * Every predefined ayanamsa of the pure-Java engine against the native one, and the sidereal
 * Sun (position and speed) that uses it.
 * <p>
 * Until 2026-11-23 the Java engine had an old {@code ayanamsa[]} table and no
 * {@code get_aya_correction()}: Lahiri was 3.6e-5 deg off native, Fagan, Raman and KP likewise,
 * the galactic-equator modes were garbage, and every sidereal speed lacked the ayanamsa rate
 * (3.8e-5 deg/day). The table is now generated from {@code sweph.h}
 * ({@code ai-github-projects/swe-java-lib/tools/gen_ayanamsa_table.py}) and
 * {@code swi_get_ayanamsa_ex()} / {@code swi_get_ayanamsa_with_speed()} are ported.
 *
 * <p>
 * {@code @Isolated}: the Java engine's tidal acceleration is JVM-global ({@code SweDate.TID_ACC}),
 * and test classes run in parallel - another class's {@code swe_close()} resets it to automatic
 * in the middle of this one (seen as DeLuce, whose t0 is UT, 5.4e-8 deg off at 1000 CE).
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
@Isolated
public class AyanamsaParityTest extends AbstractTest {

    /** 1000 CE, 1900, J2000, 2026, 2300 */
    static final double[] DATES = {2086307.5, 2415020.5, 2451545.0, 2461041.5, 2561117.5};

    /**
     * all 47 modes agree to ~1.3e-11 deg since the fixstar port (precession modes ~3e-13; the
     * star modes had been ~2e-9 with the old Java fixstar)
     */
    static final double AYA_TOL = 1e-10;

    /**
     * The galactic-equator modes read GP1958 / GPol through swe_fixstar. Until the fixstar port
     * of 2026-11-23 (2.07 algorithm: unit vector, separate space motion, no rdist) they were
     * 1.8e-5..5.1e-5 deg (GP1958, B1950) and 6.4e-8 (GPol) off; now within AYA_TOL like the rest.
     */
    static double ayaTol(int mode) {
        return AYA_TOL;
    }

    /** the tropical Sun itself differs by 1.26e-7 deg between the engines with the files */
    static final double SUN_POS_TOL = 5e-7, SUN_SPEED_TOL = 1e-6;

    @Test
    void everyPredefinedAyanamsa_matchesNative() {
        ISwissEph cn = getSwephExp(), jv = getSwissEph();
        try {
            // delta t enters through t0_is_UT and the star modes' Sun: pin it on both engines
            cn.swe_set_tid_acc(SE_TIDAL_MOSEPH);
            jv.swe_set_tid_acc(SE_TIDAL_MOSEPH);
            for (int mode = 0; mode < SE_NSIDM_PREDEF; mode++) {
                cn.swe_set_sid_mode(mode, 0, 0);
                jv.swe_set_sid_mode(mode, 0, 0);
                double tol = ayaTol(mode);
                for (double tjd : DATES) {
                    String what = "mode " + mode + " jd " + tjd;
                    double[] ac = new double[1], aj = new double[1];
                    StringBuilder ec = new StringBuilder(), ej = new StringBuilder();
                    cn.swe_get_ayanamsa_ex(tjd, SEFLG_SWIEPH, ac, ec);
                    jv.swe_get_ayanamsa_ex(tjd, SEFLG_SWIEPH, aj, ej);
                    assertTrue(near(ac[0], aj[0], tol),
                            what + " ayanamsa native " + ac[0] + " vs Java " + aj[0]
                                    + " (diff " + Math.abs(ac[0] - aj[0]) + ")");

                    double[] xc = new double[6], xj = new double[6];
                    int iflag = SEFLG_SWIEPH | SEFLG_SPEED | SEFLG_SIDEREAL;
                    cn.swe_calc(tjd, SE_SUN, iflag, xc, ec);
                    jv.swe_calc(tjd, SE_SUN, iflag, xj, ej);
                    assertTrue(near(xc[0], xj[0], tol + SUN_POS_TOL),
                            what + " sidereal Sun native " + xc[0] + " vs Java " + xj[0]);
                    assertTrue(Math.abs(xc[3] - xj[3]) <= SUN_SPEED_TOL,
                            what + " sidereal Sun speed native " + xc[3] + " vs Java " + xj[3]
                                    + " (diff " + Math.abs(xc[3] - xj[3]) + ")");
                }
            }
        } finally {
            cn.swe_set_sid_mode(SE_SIDM_FAGAN_BRADLEY, 0, 0);
            jv.swe_set_sid_mode(SE_SIDM_FAGAN_BRADLEY, 0, 0);
            cn.swe_set_tid_acc(SE_TIDAL_AUTOMATIC);
            jv.swe_set_tid_acc(SE_TIDAL_AUTOMATIC);
        }
    }

    private static boolean near(double a, double b, double tol) {
        double d = Math.abs(a - b);
        if (d > 180) d = 360 - d;
        return d <= tol;
    }
}
