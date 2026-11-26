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
import static swisseph.SweConst.*;

/**
 * Every predefined ayanamsa (0 .. 46), the user-defined one and the five option bits (the precession
 * model of the original for four of them), 3000 BCE to
 * 3000 CE, with the files of the reference library and with Moshier: {@code swe_get_ayanamsa_ex(_ut)},
 * the sidereal Sun, Moon and Saturn, the sidereal ascendant and MC. Every difference goes to
 * {@code target/accuracy/SiderealAccuracyTest.tsv}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
public class SiderealAccuracyTest {

    /** SE_SIDBIT_PREC_ORIG is not here: it switches the precession and nutation models of the
     *  whole engine for good (sweph.c swe_set_sid_mode), so it gets fresh engines of its own */
    static final int[] BITS = {0, SE_SIDBIT_ECL_T0, SE_SIDBIT_SSY_PLANE, SE_SIDBIT_ECL_DATE, SE_SIDBIT_NO_PREC_OFFSET};
    static final int[] BODIES = {SE_SUN, SE_MOON, SE_SATURN, SE_MEAN_NODE};

    @Test
    void everyAyanamsa_matchesNative() {
        assumeTrue(null != AccuracySurvey.REF_EPHE, "SWISSEPH_HOME/ephe");
        final AccuracySurvey s = new AccuracySurvey("SiderealAccuracyTest");
        final double[] dates = AccuracySurvey.dates(625000.5, 2818000.5, 61);
        AccuracySurvey.inFreshThread(() -> {
            try (ISwissEph cn = new SwephNative(AccuracySurvey.REF_EPHE); ISwissEph jv = new SwissEph(AccuracySurvey.REF_EPHE)) {
                for (int mode = 0; mode <= SE_NSIDM_PREDEF; mode++) {
                    final int sid = mode == SE_NSIDM_PREDEF ? SE_SIDM_USER : mode;
                    for (int bit : BITS) {
                        if (0 != bit && sid % 3 != 1 && sid != SE_SIDM_LAHIRI && sid != SE_SIDM_USER) continue;
                        survey(s, cn, jv, sid, bit, dates);
                    }
                }
            }
            return null;
        });
        for (int sid : new int[]{SE_SIDM_FAGAN_BRADLEY, SE_SIDM_LAHIRI, SE_SIDM_RAMAN, SE_SIDM_KRISHNAMURTI}) {
            AccuracySurvey.inFreshThread(() -> {
                try (ISwissEph cn = new SwephNative(AccuracySurvey.REF_EPHE); ISwissEph jv = new SwissEph(AccuracySurvey.REF_EPHE)) {
                    survey(s, cn, jv, sid, SE_SIDBIT_PREC_ORIG, dates);
                }
                return null;
            });
        }
        s.write();
        assertEquals("", s.retDiffList(), "return values");
        // measured 2026-11-26: see target/accuracy; star-based modes go through swe_fixstar
        assertEquals("", s.above("files|", "aya", 1e-10) + s.above("files|", "aya_nonut", 1e-10)
                + s.above("files|", "aya_ut", 1e-10) + s.above("moshier|", "aya", 1e-8) + s.above("moshier|", "aya_ut", 1e-8));
        assertEquals("", s.above("files|", "|lon", 1e-9) + s.above("files|", "|vlon", 1e-6)
                + s.above("files|", "|asc", 1e-9) + s.above("files|", "|mc", 1e-9));
    }

    private static void survey(AccuracySurvey s, ISwissEph cn, ISwissEph jv, int sid, int bit, double[] dates) {
        cn.swe_set_sid_mode(sid | bit, 2415020.5, 22.46);
        jv.swe_set_sid_mode(sid | bit, 2415020.5, 22.46);
        try {
            final String m = sid + (0 == bit ? "" : "+" + bit) + "|";
            for (int eph : new int[]{SEFLG_SWIEPH, SEFLG_MOSEPH}) {
                final String e = (eph == SEFLG_SWIEPH ? "files" : "moshier") + "|" + m;
                for (double tjd : dates) {
                    ayanamsa(s, cn, jv, e + "aya", tjd, eph, false);
                    ayanamsa(s, cn, jv, e + "aya_nonut", tjd, eph | SEFLG_NONUT, false);
                    ayanamsa(s, cn, jv, e + "aya_ut", tjd, eph, true);
                    if (0 != bit && eph == SEFLG_MOSEPH) continue;
                    for (int ipl : BODIES) calc(s, cn, jv, e + "calc|" + ipl, tjd, ipl, eph | SEFLG_SIDEREAL | SEFLG_SPEED);
                    if (eph == SEFLG_SWIEPH) houses(s, cn, jv, e + "houses", tjd);
                }
            }
        } finally {
            cn.swe_set_sid_mode(SE_SIDM_FAGAN_BRADLEY, 0, 0);
            jv.swe_set_sid_mode(SE_SIDM_FAGAN_BRADLEY, 0, 0);
        }
    }

    private static void ayanamsa(AccuracySurvey s, ISwissEph cn, ISwissEph jv, String key, double tjd, int iflag, boolean ut) {
        final double[] ac = new double[1], aj = new double[1];
        final StringBuilder ec = new StringBuilder(), ej = new StringBuilder();
        final int rc = ut ? cn.swe_get_ayanamsa_ex_ut(tjd, iflag, ac, ec) : cn.swe_get_ayanamsa_ex(tjd, iflag, ac, ec);
        final int rj = ut ? jv.swe_get_ayanamsa_ex_ut(tjd, iflag, aj, ej) : jv.swe_get_ayanamsa_ex(tjd, iflag, aj, ej);
        // the return value carries the ephemeris actually used; Moshier falls back silently in both
        if (!s.call(key + "|ret", rc, rj, ec, ej, () -> "jd " + tjd) || rc < 0) return;
        s.diff(key, AccuracySurvey.angle(ac[0], aj[0]), () -> "jd " + tjd + " native " + ac[0] + " Java " + aj[0]);
    }

    private static void calc(AccuracySurvey s, ISwissEph cn, ISwissEph jv, String key, double tjd, int ipl, int iflag) {
        final double[] xc = new double[6], xj = new double[6];
        final StringBuilder ec = new StringBuilder(), ej = new StringBuilder();
        final int rc = cn.swe_calc(tjd, ipl, iflag, xc, ec), rj = jv.swe_calc(tjd, ipl, iflag, xj, ej);
        if (!s.call(key + "|ret", rc, rj, ec, ej, () -> "jd " + tjd) || rc < 0) return;
        s.diff(key + "|lon", AccuracySurvey.angle(xc[0], xj[0]), () -> "jd " + tjd + " native " + xc[0] + " Java " + xj[0]);
        s.diff(key + "|vlon", Math.abs(xc[3] - xj[3]), () -> "jd " + tjd + " native " + xc[3] + " Java " + xj[3]);
    }

    private static void houses(AccuracySurvey s, ISwissEph cn, ISwissEph jv, String key, double tjd) {
        final double[] cc = new double[13], cj = new double[13], ac = new double[10], aj = new double[10];
        final StringBuilder ec = new StringBuilder(), ej = new StringBuilder();
        final int rc = cn.swe_houses_ex2(tjd, SEFLG_SIDEREAL, 50.45, 30.52, 'W', cc, ac, null, null, ec);
        final int rj = jv.swe_houses_ex2(tjd, SEFLG_SIDEREAL, 50.45, 30.52, 'W', cj, aj, null, null, ej);
        if (!s.call(key + "|ret", rc, rj, ec, ej, () -> "jd " + tjd) || rc < 0) return;
        s.diff(key + "|asc", AccuracySurvey.angle(ac[0], aj[0]), () -> "jd " + tjd + " native " + ac[0] + " Java " + aj[0]);
        s.diff(key + "|mc", AccuracySurvey.angle(ac[1], aj[1]), () -> "jd " + tjd + " native " + ac[1] + " Java " + aj[1]);
    }
}
