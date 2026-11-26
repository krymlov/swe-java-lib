/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-11
 */
package org.swisseph;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import swisseph.SweConst;
import swisseph.SwissEph;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static swisseph.SweConst.*;

/**
 * What the {@code Heliacal*} classes share: the cases, the tolerances and the helpers. The
 * tests are split into classes of 20 - 30 seconds so that surefire runs them in parallel forks.
 * See {@link HeliacalParityTest} for what is tested.
 */
@Isolated
abstract class HeliacalParityTestBase extends AbstractTest {

    /** longitude, latitude, height */
    static final double[][] PLACES = {{30.52, 50.45, 180}, {12.5, 41.9, 100}, {77.2, 28.6, 200},
            {-70, -33, 500}, {18, 62.5, 50}, {31.2, 30, 0}};
    /** pressure, temperature, humidity, meteorological range or extinction coefficient */
    static final double[][] AIRS = {{1013.25, 15, 40, 0}, {0, 0, 0, 0}, {1000, 25, 70, 20},
            {900, 5, 100, 0.25}, {1013.25, 15, 0, 0}, {1013.25, 15, 30, 0.02}, {1013.25, 10, 50, 600}};
    /** age, Snellen ratio, binocular, magnification, aperture, transmission */
    static final double[][] OBSERVERS = {{36, 1, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}, {60, 1.4, 1, 10, 50, 0.8},
            {23, 0.5, 0, 4, 30, 0.7}};
    static final String[] OBJECTS = {"venus", "moon", "Mercury", "mars", "JUPITER", "saturn", "uranus", "neptune",
            "sirius", "Aldebaran", ",alTau", "Regulus", "sun", "nosuchstar"};
    /** -3000 to 2100 */
    static final double[] DATES = {2451545.0, 2455197.3, 2433282.6, 2415100.1, 2305500.2, 2086400.4, 1757700.7, 626000.3};
    static final int[] FLAGS = {SEFLG_SWIEPH, SEFLG_MOSEPH, SEFLG_SWIEPH | SE_HELFLAG_HIGH_PRECISION,
            SEFLG_SWIEPH | SE_HELFLAG_OPTICAL_PARAMS, SEFLG_SWIEPH | SE_HELFLAG_VISLIM_DARK,
            SEFLG_SWIEPH | SE_HELFLAG_VISLIM_NOMOON, SEFLG_SWIEPH | SE_HELFLAG_VISLIM_PHOTOPIC,
            SEFLG_SWIEPH | SE_HELFLAG_VISLIM_SCOTOPIC,
            SEFLG_SWIEPH | SE_HELFLAG_OPTICAL_PARAMS | SE_HELFLAG_VISLIM_PHOTOPIC | SE_HELFLAG_VISLIM_SCOTOPIC};
    static final int[] AV_KINDS = {SE_HELFLAG_AVKIND_VR, SE_HELFLAG_AVKIND_PTO, SE_HELFLAG_AVKIND_MIN7, SE_HELFLAG_AVKIND_MIN9};
    static final double TOL = 1e-9, TOL_DATE = 1e-8, TOL_NORTH = 2.5e-7;

    @FunctionalInterface
    interface Call {
        int on(ISwissEph engine, double[] air, double[] observer, StringBuilder name, double[] ret, StringBuilder serr);
    }

    static String text(Object o) {
        return String.valueOf(o);
    }

    /** one call on both engines; returns what the native one wrote, with its return value as the last element */
    double[] compare(String what, double[] air, double[] observer, String name, boolean withSerr, double tol, Call call) {
        final ISwissEph[] engines = {getSwephExp(), getSwissEph()};
        final double[][] ret = new double[2][51], airs = new double[2][], observers = new double[2][];
        final StringBuilder[] names = new StringBuilder[2], errors = new StringBuilder[2];
        final int[] retval = new int[2];
        for (int i = 0; i < 2; i++) {
            airs[i] = air.clone();
            observers[i] = observer.clone();
            names[i] = new StringBuilder(name);
            errors[i] = withSerr ? new StringBuilder() : null;
            Arrays.fill(ret[i], 7);
            retval[i] = call.on(engines[i], airs[i], observers[i], names[i], ret[i], errors[i]);
        }
        final String ctx = what + " " + name;
        assertEquals(retval[0], retval[1], () -> ctx + " return; native '" + errors[0] + "' Java '" + errors[1] + "'");
        if (retval[0] >= 0 && name.toLowerCase(Locale.ROOT).startsWith("moon") && what.startsWith("heliacal_ut")) {
            // swe_heliacal_ut() of C hands over its uninitialised serr when the Moon's event is found
            assertEquals(withSerr ? "" : "null", text(errors[1]), ctx + " serr");
        } else {
            assertEquals(text(errors[0]), text(errors[1]), ctx + " serr");
        }
        assertEquals(names[0].toString(), names[1].toString(), ctx + " object name");
        for (int i = 0; i < 50; i++) {
            final int n = i;
            assertEquals(ret[0][i], ret[1][i], tol, () -> ctx + " [" + n + "]");
        }
        for (int i = 0; i < air.length; i++) assertEquals(airs[0][i], airs[1][i], 1e-12, ctx + " datm[" + i + "]");
        for (int i = 0; i < observer.length; i++) assertEquals(observers[0][i], observers[1][i], 1e-12, ctx + " dobs[" + i + "]");
        ret[0][50] = retval[0];
        return ret[0];
    }

    /** the last values kept in C are of a date, of a solar altitude and of a star which no test has */
    void prime() {
        visLimit(2447000.123, PLACES[0], AIRS[0], OBSERVERS[0], "Vega", SEFLG_SWIEPH, true);
        visLimit(2447000.623, PLACES[0], AIRS[0], OBSERVERS[0], "Vega", SEFLG_SWIEPH, true);
    }

    double[] visLimit(double t, double[] geo, double[] air, double[] obs, String name, int fl, boolean withSerr) {
        return compare("vis_limit_mag fl " + fl + " jd " + t + " geo " + Arrays.toString(geo) + " air " + Arrays.toString(air)
                        + " obs " + Arrays.toString(obs), air, obs, name, withSerr, TOL,
                (e, a, o, n, r, s) -> e.swe_vis_limit_mag(t, geo.clone(), a, o, n, fl, r, s));
    }

    double[] event(double t, double[] geo, double[] air, double[] obs, String name, int type, int fl, boolean withSerr) {
        return compare("heliacal_ut type " + type + " fl " + fl + " jd " + t + " geo " + Arrays.toString(geo) + " air "
                        + Arrays.toString(air) + " obs " + Arrays.toString(obs), air, obs, name, withSerr, TOL_DATE,
                (e, a, o, n, r, s) -> e.swe_heliacal_ut(t, geo.clone(), a, o, name, type, fl, r, s));
    }

    double[] details(double t, double[] geo, double[] air, double[] obs, String name, int type, int fl, boolean withSerr) {
        return compare("heliacal_pheno_ut type " + type + " fl " + fl + " jd " + t + " geo " + Arrays.toString(geo) + " air "
                        + Arrays.toString(air) + " obs " + Arrays.toString(obs), air, obs, name, withSerr, TOL_DATE,
                (e, a, o, n, r, s) -> e.swe_heliacal_pheno_ut(t, geo.clone(), a, o, name, type, fl, r, s));
    }

    void events(String[] objects, int[] types, int[] flags, int step) {
        prime();
        int i = 0, found = 0;
        for (int fl : flags) {
            for (int type : types) {
                for (String name : objects) {
                    if (0 != i++ % step) continue;
                    final double[] r = event(DATES[i % (DATES.length - 1)], PLACES[(i / 2) % PLACES.length], AIRS[(i / 3) % 3],
                            OBSERVERS[(i / 5) % OBSERVERS.length], name, type, fl, 3 != i % 7);
                    if (0 == r[50]) found++;
                }
            }
        }
        assertTrue(found > 3, found + " events of " + i / step);
    }
}
