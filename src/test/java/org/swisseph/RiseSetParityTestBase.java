/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-11
 */
package org.swisseph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import swisseph.DblObj;
import swisseph.SweConst;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static swisseph.SweConst.*;

/**
 * What the {@code RiseSet*} classes share: the cases, the tolerances and the helpers. The
 * tests are split into classes of 20 - 30 seconds so that surefire runs them in parallel forks.
 * See {@link RiseSetParityTest} for what is tested.
 */
@Isolated
abstract class RiseSetParityTestBase extends AbstractTest {

    /** the borders of the fast method are 60 and, for the Sun, 65 degrees */
    static final double[] LATITUDES = {0, 28.6, -33, 50.45, 59.9, 60, 60.1, -60, -60.1, 62, 64.5, 65, 65.1, -65, -65.1, 68, 85, -85};
    static final int[] FAST_FLAGS = {0, SE_BIT_DISC_CENTER, SE_BIT_DISC_BOTTOM, SE_BIT_NO_REFRACTION,
            SE_BIT_DISC_CENTER | SE_BIT_NO_REFRACTION, SE_BIT_FIXED_DISC_SIZE, SE_BIT_FIXED_DISC_SIZE | SE_BIT_DISC_BOTTOM,
            SE_BIT_GEOCTR_NO_ECL_LAT, SE_BIT_HINDU_RISING};
    static final int[] TWILIGHTS = {SE_BIT_CIVIL_TWILIGHT, SE_BIT_NAUTIC_TWILIGHT, SE_BIT_ASTRO_TWILIGHT};
    static final int[] EPHEMERIDES = {SEFLG_SWIEPH, SEFLG_MOSEPH};
    /** -3000 to 2100 */
    static final double[] DATES = {2451545.0, 2455197.3, 2433282.6, 2415100.1, 2305500.2, 2086400.4, 1757700.7, 626000.3};
    static final String[] STARS = {"Aldebaran", ",alTau", "sirius", "Polaris", "Canopus", "Regulus"};
    static final int CIRCUMPOLAR = -2;
    /** days; the measured differences are 0, 4e-9, 1.1e-7 and 3.8e-5 */
    static final double TOL = 1e-10, TOL_STAR = 1e-8, TOL_TRUE_NODE = 2.5e-7, TOL_OSCU_APOG = 1e-4;
    static final double UNTOUCHED = 7;

    /** the lapse rate is per thread and stays set (another test leaves 0.01 in native) */
    @BeforeEach
    void theDefaultLapseRate() {
        getSwephExp().swe_set_lapse_rate(0.0065);
        getSwissEph().swe_set_lapse_rate(0.0065);
    }

    @FunctionalInterface
    interface Call {
        int on(ISwissEph engine, StringBuilder star, double[] geo, double[] tret, StringBuilder serr);
    }

    /** one call on both engines; returns the native return value and time */
    double[] compare(String what, String star, double[] geo, boolean withSerr, double tol, Call call) {
        final ISwissEph[] engines = {getSwephExp(), getSwissEph()};
        final double[][] tret = new double[2][];
        final StringBuilder[] stars = new StringBuilder[2], errors = new StringBuilder[2];
        final int[] retval = new int[2];
        for (int i = 0; i < 2; i++) {
            stars[i] = null == star ? null : new StringBuilder(star);
            errors[i] = withSerr ? new StringBuilder() : null;
            tret[i] = new double[]{UNTOUCHED};
            final double[] g = geo.clone();
            retval[i] = call.on(engines[i], stars[i], g, tret[i], errors[i]);
            assertEquals(Arrays.toString(geo), Arrays.toString(g), what + ": the place is not changed");
        }
        final String ctx = what + " geo " + Arrays.toString(geo) + " star " + star;
        assertEquals(retval[0], retval[1], () -> ctx + " return; native '" + errors[0] + "' Java '" + errors[1] + "'");
        assertEquals(String.valueOf(errors[0]), String.valueOf(errors[1]), ctx + " serr");
        assertEquals(String.valueOf(stars[0]), String.valueOf(stars[1]), ctx + " star name");
        assertEquals(tret[0][0], tret[1][0], tol, ctx + " time");
        return new double[]{retval[0], tret[0][0]};
    }

    static double tolerance(int ipl, String star) {
        if (null != star && !star.isEmpty()) return TOL_STAR;
        return SE_TRUE_NODE == ipl ? TOL_TRUE_NODE : SE_OSCU_APOG == ipl ? TOL_OSCU_APOG : TOL;
    }

    double[] rise(double t, int ipl, String star, int eph, int rsmi, double[] geo, double press, double temp, boolean withSerr) {
        return compare("rise_trans jd " + t + " ipl " + ipl + " eph " + eph + " rsmi " + rsmi + " air " + press + " " + temp, star, geo,
                withSerr, tolerance(ipl, star),
                (e, n, g, r, s) -> e.swe_rise_trans(t, ipl, n, eph, rsmi, g, press, temp, r, s));
    }

    double[] horizon(double t, int ipl, String star, int eph, int rsmi, double[] geo, double press, double temp, double height) {
        return compare("rise_trans_true_hor " + height + " jd " + t + " ipl " + ipl + " eph " + eph + " rsmi " + rsmi + " air "
                + press + " " + temp, star, geo, true, tolerance(ipl, star),
                (e, n, g, r, s) -> e.swe_rise_trans_true_hor(t, ipl, n, eph, rsmi, g, press, temp, height, r, s));
    }

    static double[] place(int i, double lat) {
        return new double[]{-170 + 61.7 * i % 340, lat, 0 == i % 3 ? 0 : 1 == i % 3 ? 180 : 2500};
    }
}
