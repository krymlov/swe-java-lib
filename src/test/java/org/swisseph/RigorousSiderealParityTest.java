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
 * The rigorous sidereal modes ({@code SE_SIDBIT_ECL_T0}, {@code SE_SIDBIT_SSY_PLANE}), pure Java
 * against native, for every predefined ayanamsa.
 * <p>
 * Until 2026-11-23 the Java {@code swi_trop_ra2sid_lon} / {@code swi_trop_ra2sid_lon_sosy} did not
 * add C 2.10.03's {@code get_aya_correction()}, so an ayanamsa defined with an older precession
 * model was off by that correction: Lahiri 3.6e-5 deg, Fagan/Bradley 1.15e-4 deg, for planets
 * and stars alike.
 * <p>
 * The mean node, the mean apogee and two stars were the only bodies whose tropical J2000 position
 * the engines agreed on when this was written (measured 1.3e-11 deg); since the tropical fixes of
 * the same day (see {@link TropicalParityTest}) the Sun, the Moon, the true node and the
 * osculating apogee are compared as well.
 * <p>
 * {@code @Isolated}: the tidal acceleration is pinned, and it is JVM-global in the Java engine.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
@Isolated
public class RigorousSiderealParityTest extends AbstractTest {

    static final double[] DATES = {1721423.5, 2086307.5, 2451545.0, 2461041.5, 2561117.5};
    static final int B = SEFLG_SWIEPH | SEFLG_SPEED | SEFLG_SIDEREAL;
    static final double POS_TOL = 1e-10, SPEED_TOL = 1e-7;

    @Test
    void eclT0AndSsyPlane_everyMode_matchNative() {
        ISwissEph cn = getSwephExp(), jv = getSwissEph();
        int compared = 0;
        cn.swe_set_tid_acc(SE_TIDAL_MOSEPH);
        jv.swe_set_tid_acc(SE_TIDAL_MOSEPH);
        try {
            for (int bit : new int[]{SE_SIDBIT_ECL_T0, SE_SIDBIT_SSY_PLANE}) {
                for (int mode = 0; mode < SE_NSIDM_PREDEF; mode++) {
                    if (mode == SE_SIDM_USER) continue;
                    cn.swe_set_sid_mode(mode | bit, 0, 0);
                    jv.swe_set_sid_mode(mode | bit, 0, 0);
                    for (double tjd : DATES) {
                        for (int iflag : new int[]{B, B | SEFLG_EQUATORIAL}) {
                            for (Object body : new Object[]{SE_MEAN_NODE, SE_MEAN_APOG, SE_SUN, SE_MOON, SE_TRUE_NODE, SE_OSCU_APOG, "Spica", "Aldebaran"}) {
                                String what = "mode " + (mode | bit) + " " + body + " jd " + tjd + " iflag " + iflag;
                                double[] xc = new double[6], xj = new double[6];
                                StringBuilder ec = new StringBuilder(), ej = new StringBuilder();
                                int rc, rj;
                                if (body instanceof String) {
                                    rc = cn.swe_fixstar(new StringBuilder((String) body), tjd, iflag, xc, ec);
                                    rj = jv.swe_fixstar(new StringBuilder((String) body), tjd, iflag, xj, ej);
                                } else {
                                    rc = cn.swe_calc(tjd, (Integer) body, iflag, xc, ec);
                                    rj = jv.swe_calc(tjd, (Integer) body, iflag, xj, ej);
                                }
                                assertEquals(rc, rj, what + " return; native '" + ec + "', Java '" + ej + "'");
                                if (rc < 0) continue;
                                for (int k = 0; k < 2; k++) {
                                    double d = Math.abs(xc[k] - xj[k]);
                                    if (k == 0 && d > 180) d = 360 - d;
                                    assertTrue(d <= POS_TOL, what + " [" + k + "] native " + xc[k] + " vs Java " + xj[k] + " (diff " + d + ")");
                                    double v = Math.abs(xc[k + 3] - xj[k + 3]);
                                    assertTrue(v <= SPEED_TOL, what + " [" + (k + 3) + "] native " + xc[k + 3] + " vs Java " + xj[k + 3]);
                                }
                                compared++;
                            }
                        }
                    }
                }
            }
        } finally {
            cn.swe_set_sid_mode(SE_SIDM_FAGAN_BRADLEY, 0, 0);
            jv.swe_set_sid_mode(SE_SIDM_FAGAN_BRADLEY, 0, 0);
            cn.swe_set_tid_acc(SE_TIDAL_AUTOMATIC);
            jv.swe_set_tid_acc(SE_TIDAL_AUTOMATIC);
        }
        assertTrue(compared > 3000, "compared " + compared);
    }
}
