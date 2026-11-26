/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-11
 */
package org.swisseph;

import org.junit.jupiter.api.Test;
import swisseph.SwissEph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.swisseph.api.ISweConstants.EPHE_PATH;
import static swisseph.SweConst.*;

/**
 * Three differences of the Java port found by {@link SiderealAccuracyTest} on 2026-11-26, each
 * pinned against the native library:
 * <ul>
 * <li>{@code swe_get_ayanamsa_ex} without an ephemeris bit takes the default one, as C's
 * {@code plaus_iflag()} does; {@code swe_houses_ex2(SEFLG_SIDEREAL)} passes no ephemeris bit,
 * and the sidereal ascendant of Babylonian (eta Piscium) was 2.4e-8 deg off;</li>
 * <li>with {@code SEFLG_NONUT} the return value keeps the {@code SEFLG_NONUT} that C's
 * {@code swi_get_ayanamsa_ex()} adds for the precession-based modes, not for the star-based ones;</li>
 * <li>a fixed star with another ephemeris than the previous call closes the files, as
 * {@code swe_calc()} does: afterwards {@code swe_deltat_ex(SEFLG_SWIEPH)} must use the default
 * tidal acceleration, not the one of the file that was open before.</li>
 * </ul>
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
public class AyanamsaFlagsParityTest {

    @Test
    void noEphemerisBit_isTheDefault() {
        AccuracySurvey.inFreshThread(() -> {
            try (ISwissEph cn = new SwephNative(EPHE_PATH); ISwissEph jv = new SwissEph(EPHE_PATH)) {
                final double t = 1977351.0555682252;
                for (int mode : new int[]{SE_SIDM_BABYL_ETPSC, SE_SIDM_LAHIRI, SE_SIDM_TRUE_CITRA}) {
                    cn.swe_set_sid_mode(mode, 0, 0);
                    jv.swe_set_sid_mode(mode, 0, 0);
                    for (int iflag : new int[]{0, SEFLG_SIDEREAL, SEFLG_SWIEPH, SEFLG_MOSEPH}) {
                        final double[] ac = new double[1], aj = new double[1];
                        final int rc = cn.swe_get_ayanamsa_ex(t, iflag, ac, null);
                        final int rj = jv.swe_get_ayanamsa_ex(t, iflag, aj, null);
                        assertEquals(rc, rj, "mode " + mode + " iflag " + iflag);
                        assertEquals(ac[0], aj[0], 1e-11, "mode " + mode + " iflag " + iflag);
                    }
                    final double[] cc = new double[13], cj = new double[13], sc = new double[10], sj = new double[10];
                    cn.swe_houses_ex2(t, SEFLG_SIDEREAL, 50.45, 30.52, 'W', cc, sc, null, null, null);
                    jv.swe_houses_ex2(t, SEFLG_SIDEREAL, 50.45, 30.52, 'W', cj, sj, null, null, null);
                    assertEquals(sc[0], sj[0], 1e-10, "ascendant, mode " + mode);
                }
                cn.swe_set_sid_mode(SE_SIDM_FAGAN_BRADLEY, 0, 0);
                jv.swe_set_sid_mode(SE_SIDM_FAGAN_BRADLEY, 0, 0);
            }
            return null;
        });
    }

    @Test
    void nonut_returnFlagAsNative() {
        AccuracySurvey.inFreshThread(() -> {
            try (ISwissEph cn = new SwephNative(EPHE_PATH); ISwissEph jv = new SwissEph(EPHE_PATH)) {
                for (int mode = 0; mode < SE_NSIDM_PREDEF; mode++) {
                    cn.swe_set_sid_mode(mode, 0, 0);
                    jv.swe_set_sid_mode(mode, 0, 0);
                    for (int eph : new int[]{SEFLG_SWIEPH, SEFLG_MOSEPH}) {
                        final double[] ac = new double[1], aj = new double[1];
                        assertEquals(cn.swe_get_ayanamsa_ex(2451545.0, eph | SEFLG_NONUT, ac, null),
                                jv.swe_get_ayanamsa_ex(2451545.0, eph | SEFLG_NONUT, aj, null), "mode " + mode);
                        assertEquals(cn.swe_get_ayanamsa_ex(2451545.0, eph, ac, null),
                                jv.swe_get_ayanamsa_ex(2451545.0, eph, aj, null), "mode " + mode);
                    }
                }
                cn.swe_set_sid_mode(SE_SIDM_FAGAN_BRADLEY, 0, 0);
                jv.swe_set_sid_mode(SE_SIDM_FAGAN_BRADLEY, 0, 0);
            }
            return null;
        });
    }

    @Test
    void moshierStar_closesTheFiles() {
        assumeTrue(null != AccuracySurvey.REF_EPHE, "SWISSEPH_HOME/ephe");
        AccuracySurvey.inFreshThread(() -> {
            try (ISwissEph cn = new SwephNative(AccuracySurvey.REF_EPHE); ISwissEph jv = new SwissEph(AccuracySurvey.REF_EPHE)) {
                for (ISwissEph e : new ISwissEph[]{cn, jv}) {
                    e.swe_calc(2818000.5, SE_MOON, SEFLG_SWIEPH, new double[6], null);
                    e.swe_fixstar(new StringBuilder("Spica"), 2818000.5, SEFLG_MOSEPH, new double[6], null);
                }
                assertEquals(cn.swe_deltat_ex(625000.5, SEFLG_SWIEPH, null), jv.swe_deltat_ex(625000.5, SEFLG_SWIEPH, null), 1e-12);
                assertEquals(cn.swe_get_tid_acc(), jv.swe_get_tid_acc(), 0);
            }
            return null;
        });
    }
}
