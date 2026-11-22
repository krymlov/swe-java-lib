/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-11
 */
package swisseph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.swisseph.AbstractTest;
import org.swisseph.ISwissEph;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link SwissLib} - the pure-Java port of {@code swephlib.c} - against the native library,
 * call for call, over the years 0 to 2100.
 * <p>
 * The astronomy in {@code SwissLib} is compared directly: sidereal time, mean obliquity,
 * nutation, precession and the ICRS frame bias. Where the native library has no export of its
 * own ({@code swi_epsiln}, {@code swi_nutation}, {@code swi_precess}, {@code swi_bias}), its
 * {@code swe_calc} supplies the same quantity: {@code SE_ECL_NUT} gives the obliquity and the
 * nutation, and the same body asked for in two frames gives the precession (J2000 vs of date)
 * and the bias (ICRS vs J2000). The test sits in package {@code swisseph} because those
 * {@code swi_*} methods are package private.
 * <p>
 * Why it exists: until 2026-11-22 {@code swe_sidtime} was 8"-240" off before 1850 and after
 * 2050 - {@code sidtime_long_term()} converted eps and nutation to degrees twice, and the
 * {@code SIDT_LTERM_OFS0/1} joining steps were the 2.01.00 values. No test caught it, because
 * every cross-check of the pure-Java engine sat inside 1900-2030 or went through the houses
 * against swetest, not through {@code SwissLib} itself. Here the epochs straddle both
 * {@code SIDT_LTERM_T0} and {@code SIDT_LTERM_T1} by a second, half a day, a day and a month.
 * <p>
 * The coordinate utilities are swept with seeded random inputs, plus the edge values where the
 * rounding rules matter (29°59'59.9", 359.99999°, a nakshatra boundary). {@code swe_split_deg}
 * was the 2.01.00 version until 2026-11-22: it had no {@code SE_SPLIT_DEG_NAKSHATRA} at all.
 * <p>
 * Both engines get the same tidal acceleration ({@code SE_TIDAL_DEFAULT}), so delta t - which
 * sidereal time and the long-term branch depend on - is the same in both.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
class SwissLibVsNativeTest extends AbstractTest {

    /**
     * 1e-4" is well below anything swetest prints. Measured 2026-11-22: sidereal time 1.3e-5"
     * (double rounding at the end of 2100), obliquity, nutation, precession and bias &lt; 5e-7".
     */
    static final double ARCSEC_TOL = 1e-4;
    static final int BASE = SweConst.SEFLG_SWIEPH | SweConst.SEFLG_TRUEPOS | SweConst.SEFLG_NONUT
            | SweConst.SEFLG_EQUATORIAL | SweConst.SEFLG_XYZ;
    static final int[] BODIES = {SweConst.SE_SUN, SweConst.SE_MOON, SweConst.SE_MARS,
            SweConst.SE_SATURN};

    static final List<Double> EPOCHS = epochs();

    ISwissEph nat;
    SwissEph jav;
    SwissLib sl;

    /** every 5 years from 0 to 2100 at a varying month, day and hour, plus the T0/T1 seams */
    static List<Double> epochs() {
        List<Double> jds = new ArrayList<>();
        for (int y = 0; y <= 2100; y += 5) {
            jds.add(SweDate.getJulDay(y, 1 + y % 12, 1 + y % 28, (y * 7 % 24) + 0.37, true));
        }
        for (double seam : new double[]{SwissLib.SIDT_LTERM_T0, SwissLib.SIDT_LTERM_T1}) {
            for (double d : new double[]{1. / 86400, 0.5, 1, 30}) {
                jds.add(seam - d);
                jds.add(seam + d);
            }
            jds.add(seam);
        }
        jds.add(SweDate.getJulDay(2100, 12, 31, 23.99, true));
        return jds;
    }

    @BeforeEach
    void engines() {
        nat = getSwephExp();
        jav = (SwissEph) getSwissEph();
        sl = jav.getSwissLib();
        nat.swe_set_tid_acc(SweConst.SE_TIDAL_DEFAULT);
        jav.swe_set_tid_acc(SweConst.SE_TIDAL_DEFAULT);
    }

    // ------------------------------------------------------------------ astronomy

    @Test
    void sidtimeMatchesNativeFrom0To2100() {
        Worst w = new Worst("swe_sidtime");
        for (double jd : EPOCHS) {
            w.at(jd, hoursToArcsec(sl.swe_sidtime(jd) - nat.swe_sidtime(jd)));
            // the ISwissEph route of the pure-Java engine must be the same function
            assertEquals(sl.swe_sidtime(jd), jav.swe_sidtime(jd), 0.);
        }
        w.check(ARCSEC_TOL);
    }

    @Test
    void sidtime0WithTheCallersObliquityAndNutationMatchesNative() {
        Worst w = new Worst("swe_sidtime0");
        for (double jd : EPOCHS) {
            double[] en = eclNut(jd + nat.swe_deltat(jd));
            double eps = en[0], nut = en[2];
            w.at(jd, hoursToArcsec(sl.swe_sidtime0(jd, eps, nut) - nat.swe_sidtime0(jd, eps, nut)));
            // and with eps = 0, which the long-term branch treats as "compute it yourself"
            w.at(jd, hoursToArcsec(sl.swe_sidtime0(jd, 0, 0) - nat.swe_sidtime0(jd, 0, 0)));
        }
        w.check(ARCSEC_TOL);
    }

    /** Pinned so the seam stays where swephlib.c has it - a shifted T0/T1 moves the lagna. */
    @Test
    void sidtimeLongTermSeamsAreTheSwephlibOnes() {
        assertEquals(2396758.5, SwissLib.SIDT_LTERM_T0, 0.);
        assertEquals(2469807.5, SwissLib.SIDT_LTERM_T1, 0.);
        assertEquals(0.000378172 / 15.0, SwissLib.SIDT_LTERM_OFS0, 0.);
        assertEquals(0.001385646 / 15.0, SwissLib.SIDT_LTERM_OFS1, 0.);
    }

    @Test
    void meanObliquityMatchesNative() {
        Worst w = new Worst("swi_epsiln");
        for (double jd : EPOCHS) {
            double et = jd + nat.swe_deltat(jd);
            w.at(jd, (sl.swi_epsiln(et, 0) * SwissData.RADTODEG - eclNut(et)[1]) * 3600);
        }
        w.check(ARCSEC_TOL);
    }

    @Test
    void nutationMatchesNative() {
        Worst dpsi = new Worst("swi_nutation dpsi"), deps = new Worst("swi_nutation deps"),
                eps = new Worst("true obliquity");
        double[] nutlo = new double[2];
        for (double jd : EPOCHS) {
            double et = jd + nat.swe_deltat(jd);
            double[] en = eclNut(et);
            sl.swi_nutation(et, 0, nutlo);
            dpsi.at(jd, (nutlo[0] * SwissData.RADTODEG - en[2]) * 3600);
            deps.at(jd, (nutlo[1] * SwissData.RADTODEG - en[3]) * 3600);
            eps.at(jd, ((sl.swi_epsiln(et, 0) + nutlo[1]) * SwissData.RADTODEG - en[0]) * 3600);
        }
        dpsi.check(ARCSEC_TOL);
        deps.check(ARCSEC_TOL);
        eps.check(ARCSEC_TOL);
    }

    /** native J2000 vector, precessed by SwissLib, against the native vector of date */
    @Test
    void precessionMatchesNative() {
        Worst w = new Worst("swi_precess J2000 -> date");
        Worst back = new Worst("swi_precess date -> J2000");
        for (double jd : EPOCHS) {
            double et = jd + nat.swe_deltat(jd);
            for (int ipl : BODIES) {
                double[] j2000 = calc(et, ipl, BASE | SweConst.SEFLG_J2000);
                double[] ofDate = calc(et, ipl, BASE);
                double[] x = j2000.clone();
                sl.swi_precess(x, et, 0, SwephData.J2000_TO_J);
                w.at(jd, angleArcsec(x, ofDate));
                x = ofDate.clone();
                sl.swi_precess(x, et, 0, SwephData.J_TO_J2000);
                back.at(jd, angleArcsec(x, j2000));
            }
        }
        w.check(ARCSEC_TOL);
        back.check(ARCSEC_TOL);
    }

    /** native ICRS vector, rotated by SwissLib's frame bias, against the native J2000 vector */
    @Test
    void frameBiasMatchesNative() {
        Worst w = new Worst("swi_bias ICRS -> J2000");
        double biasSize = 0;
        for (double jd : EPOCHS) {
            double et = jd + nat.swe_deltat(jd);
            for (int ipl : BODIES) {
                double[] icrs = calc(et, ipl, BASE | SweConst.SEFLG_J2000 | SweConst.SEFLG_ICRS);
                double[] j2000 = calc(et, ipl, BASE | SweConst.SEFLG_J2000);
                double[] x = icrs.clone();
                sl.swi_bias(x, et, BASE | SweConst.SEFLG_J2000, false);
                w.at(jd, angleArcsec(x, j2000));
                biasSize = Math.max(biasSize, angleArcsec(icrs, j2000));
            }
        }
        // the frame bias is ~0.02"; without this a no-op swi_bias would pass as well
        assertTrue(biasSize > 0.005, "native ICRS and J2000 do not differ: " + biasSize + "\"");
        w.check(ARCSEC_TOL);
    }

    // ------------------------------------------------------------------ utilities

    @Test
    void angleNormalisationAndMidpointsMatchNative() {
        Random r = new Random(20261122);
        for (int i = 0; i < 20000; i++) {
            double a = (r.nextDouble() - 0.5) * 2e4, b = (r.nextDouble() - 0.5) * 2e4;
            double ra = Math.toRadians(a), rb = Math.toRadians(b);
            assertEquals(nat.swe_degnorm(a), jav.swe_degnorm(a), 1e-9, "degnorm " + a);
            assertEquals(nat.swe_radnorm(ra), jav.swe_radnorm(ra), 1e-12, "radnorm " + ra);
            assertEquals(nat.swe_deg_midp(a, b), jav.swe_deg_midp(a, b), 1e-9, "deg_midp " + a + " " + b);
            assertEquals(nat.swe_rad_midp(ra, rb), jav.swe_rad_midp(ra, rb), 1e-12, "rad_midp " + ra + " " + rb);
            assertEquals(nat.swe_difdeg2n(a, b), jav.swe_difdeg2n(a, b), 1e-9, "difdeg2n " + a + " " + b);
            assertEquals(nat.swe_difdegn(a, b), jav.swe_difdegn(a, b), 1e-9, "difdegn " + a + " " + b);
            assertEquals(nat.swe_difrad2n(ra, rb), jav.swe_difrad2n(ra, rb), 1e-12, "difrad2n " + ra + " " + rb);
        }
        for (double a : new double[]{0, 360, -360, 720, 1e-14, -1e-14, 359.9999999999, -0.0}) {
            assertEquals(nat.swe_degnorm(a), jav.swe_degnorm(a), 1e-12, "degnorm " + a);
        }
    }

    @Test
    void cotransMatchesNative() {
        Random r = new Random(1850);
        for (int i = 0; i < 5000; i++) {
            double eps = (r.nextDouble() - 0.5) * 60;
            double[] in = {r.nextDouble() * 360, (r.nextDouble() - 0.5) * 180, 0.1 + r.nextDouble() * 40,
                    (r.nextDouble() - 0.5) * 30, (r.nextDouble() - 0.5) * 10, (r.nextDouble() - 0.5)};
            double[] n = new double[6], j = new double[6];
            nat.swe_cotrans(in, n, eps);
            jav.swe_cotrans(in, j, eps);
            for (int k = 0; k < 3; k++) assertEquals(n[k], j[k], 1e-9, "cotrans[" + k + "] eps " + eps);
            nat.swe_cotrans_sp(in, n, eps);
            jav.swe_cotrans_sp(in, j, eps);
            for (int k = 0; k < 6; k++) assertEquals(n[k], j[k], 1e-9, "cotrans_sp[" + k + "] eps " + eps);
        }
    }

    @Test
    void splitDegMatchesNativeForEveryFlagCombination() {
        final int[] round = {0, SweConst.SE_SPLIT_DEG_ROUND_SEC, SweConst.SE_SPLIT_DEG_ROUND_MIN,
                SweConst.SE_SPLIT_DEG_ROUND_DEG};
        final int[] keep = {0, SweConst.SE_SPLIT_DEG_KEEP_SIGN, SweConst.SE_SPLIT_DEG_KEEP_DEG};
        final int[] kind = {0, SweConst.SE_SPLIT_DEG_ZODIACAL, SweConst.SE_SPLIT_DEG_NAKSHATRA};
        List<Double> values = new ArrayList<>();
        Random r = new Random(2050);
        for (int i = 0; i < 3000; i++) values.add((r.nextDouble() - 0.1) * 400);
        for (double v : new double[]{0, 29.99999999, 30, 59.9999, 13.33333333333, 13.3333334, 26.6666666,
                359.9999999, 359.99999, 360, -0.00000001, -29.9999999, 3.3333333}) values.add(v);
        int[] dmsN = new int[3], dmsJ = new int[3], sgnN = new int[1], sgnJ = new int[1];
        double[] frN = new double[1], frJ = new double[1];
        for (int rf : round) for (int kf : keep) for (int zf : kind) {
            int flag = rf | kf | zf;
            for (double v : values) {
                nat.swe_split_deg(v, flag, dmsN, frN, sgnN);
                jav.swe_split_deg(v, flag, dmsJ, frJ, sgnJ);
                String where = String.format(Locale.ROOT, "split_deg(%.10f, %d)", v, flag);
                assertEquals(dmsN[0], dmsJ[0], where + " deg");
                assertEquals(dmsN[1], dmsJ[1], where + " min");
                assertEquals(dmsN[2], dmsJ[2], where + " sec");
                assertEquals(sgnN[0], sgnJ[0], where + " sign");
                assertEquals(frN[0], frJ[0], 1e-6, where + " fraction");
            }
        }
    }

    // ------------------------------------------------------------------ helpers

    /** native SE_ECL_NUT: true obliquity, mean obliquity, dpsi, deps, in degrees */
    double[] eclNut(double et) {
        double[] x = new double[6];
        StringBuilder serr = new StringBuilder();
        assertTrue(nat.swe_calc(et, SweConst.SE_ECL_NUT, 0, x, serr) >= 0, serr::toString);
        return x;
    }

    double[] calc(double et, int ipl, int iflag) {
        double[] x = new double[6];
        StringBuilder serr = new StringBuilder();
        int ret = nat.swe_calc(et, ipl, iflag, x, serr);
        assertTrue(ret >= 0, () -> "swe_calc(" + ipl + ") " + serr);
        assertEquals(0, ret & SweConst.SEFLG_MOSEPH, () -> "fell back to Moshier: " + serr);
        return x;
    }

    static double hoursToArcsec(double dh) {
        dh = dh - 24 * Math.rint(dh / 24);
        return dh * 15 * 3600;
    }

    /** the angle between the position parts of two cartesian vectors */
    static double angleArcsec(double[] a, double[] b) {
        double cx = a[1] * b[2] - a[2] * b[1], cy = a[2] * b[0] - a[0] * b[2], cz = a[0] * b[1] - a[1] * b[0];
        double dot = a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
        return Math.toDegrees(Math.atan2(Math.sqrt(cx * cx + cy * cy + cz * cz), dot)) * 3600;
    }

    /** the largest |difference| and the julian day it occurred at */
    static final class Worst {
        final String what;
        double max = -1, jd;

        Worst(String what) { this.what = what; }

        void at(double jd, double diffArcsec) {
            assertTrue(Double.isFinite(diffArcsec), what + " not finite at jd " + jd);
            if (Math.abs(diffArcsec) > max) { max = Math.abs(diffArcsec); this.jd = jd; }
        }

        void check(double tol) {
            assertTrue(max <= tol, () -> String.format(Locale.ROOT,
                    "%s: pure Java vs native %.7f\" at jd %.6f (year %d), tolerance %.7f\"",
                    what, max, jd, new SweDate(jd).getYear(), tol));
        }
    }
}
