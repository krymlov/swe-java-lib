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
 * The heliacal functions, pure Java against native: return value, {@code serr}, the object name
 * and everything written to the arrays ({@code datm} and {@code dobs} included, their defaults are
 * filled in).
 * <p>
 * Until 2026-11-24 the Java engine had the swehel.c of release 2.07: another limiting magnitude,
 * another search for the day and for the optimum, and the {@code SE_HELFLAG_AV*} flags one bit
 * lower, so that the arcus visionis kinds asked for were other ones than in C.
 * <p>
 * {@code swephexp.h} defines {@code SIMULATE_VICTORVB}: the right ascension of the Sun is the
 * approximation from the date, and {@code ka()} limits the humidity.
 * <p>
 * The C keeps the last extinction coefficients by the altitude of the Sun only. The result
 * depends on the calls before, so both engines get the same ones, from {@link #prime} on.
 * <p>
 * {@code @Isolated}: the tidal acceleration is JVM-global in the Java engine.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
@Isolated
public class HeliacalParityTest extends AbstractTest {

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
    private interface Call {
        int on(ISwissEph engine, double[] air, double[] observer, StringBuilder name, double[] ret, StringBuilder serr);
    }

    private static String text(Object o) {
        return String.valueOf(o);
    }

    /** one call on both engines; returns what the native one wrote, with its return value as the last element */
    private double[] compare(String what, double[] air, double[] observer, String name, boolean withSerr, double tol, Call call) {
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
    private void prime() {
        visLimit(2447000.123, PLACES[0], AIRS[0], OBSERVERS[0], "Vega", SEFLG_SWIEPH, true);
        visLimit(2447000.623, PLACES[0], AIRS[0], OBSERVERS[0], "Vega", SEFLG_SWIEPH, true);
    }

    private double[] visLimit(double t, double[] geo, double[] air, double[] obs, String name, int fl, boolean withSerr) {
        return compare("vis_limit_mag fl " + fl + " jd " + t + " geo " + Arrays.toString(geo) + " air " + Arrays.toString(air)
                        + " obs " + Arrays.toString(obs), air, obs, name, withSerr, TOL,
                (e, a, o, n, r, s) -> e.swe_vis_limit_mag(t, geo.clone(), a, o, n, fl, r, s));
    }

    private double[] event(double t, double[] geo, double[] air, double[] obs, String name, int type, int fl, boolean withSerr) {
        return compare("heliacal_ut type " + type + " fl " + fl + " jd " + t + " geo " + Arrays.toString(geo) + " air "
                        + Arrays.toString(air) + " obs " + Arrays.toString(obs), air, obs, name, withSerr, TOL_DATE,
                (e, a, o, n, r, s) -> e.swe_heliacal_ut(t, geo.clone(), a, o, name, type, fl, r, s));
    }

    private double[] details(double t, double[] geo, double[] air, double[] obs, String name, int type, int fl, boolean withSerr) {
        return compare("heliacal_pheno_ut type " + type + " fl " + fl + " jd " + t + " geo " + Arrays.toString(geo) + " air "
                        + Arrays.toString(air) + " obs " + Arrays.toString(obs), air, obs, name, withSerr, TOL_DATE,
                (e, a, o, n, r, s) -> e.swe_heliacal_pheno_ut(t, geo.clone(), a, o, name, type, fl, r, s));
    }

    /** every SE_HELFLAG_*, SE_HELIACAL_* and event number is that of swephexp.h */
    @Test
    void constants_areThoseOfSwephexp() throws Exception {
        final String home = System.getenv("SWISSEPH_HOME");
        assumeTrue(null != home, "SWISSEPH_HOME is not set");
        final Path header = Paths.get(home, "swephexp.h");
        assumeTrue(Files.isRegularFile(header), "no swephexp.h in $SWISSEPH_HOME");
        final Matcher m = Pattern.compile("#define\\s+(SE_HEL(?:FLAG|IACAL)_\\w+|SE_(?:EVENING|MORNING)_\\w+"
                + "|SE_(?:ACRONYCHAL|COSMICAL)_\\w+|SE_\\w+OPIC_FLAG)\\s+(\\(1 << (\\d+)\\)|\\d+)")
                .matcher(new String(Files.readAllBytes(header), StandardCharsets.ISO_8859_1));
        int n = 0, flags = 0;
        while (m.find()) {
            final String name = m.group(1);
            // the second, unused set of names has kept the bits of an older release
            if (name.startsWith("SE_HELIACAL_") && !name.matches("SE_HELIACAL_(RIS|SETT)ING")) continue;
            final int value = null != m.group(3) ? 1 << Integer.parseInt(m.group(3)) : Integer.parseInt(m.group(2));
            assertEquals(value, SweConst.class.getField(name).getInt(null), name);
            if (name.startsWith("SE_HELFLAG_")) {
                flags++;
                final String old = name.replace("SE_HELFLAG_", "SE_HELIACAL_");
                if (Arrays.stream(SweConst.class.getFields()).anyMatch(f -> f.getName().equals(old))) {
                    assertEquals(value, SweConst.class.getField(old).getInt(null), old);
                }
            }
            n++;
        }
        assertTrue(flags >= 14 && n >= 22, flags + " flags of " + n + " constants found");
        assertEquals(SE_HELFLAG_AVKIND, SE_HELIACAL_AVKIND);
        assertEquals(0xF << 16, SE_HELFLAG_AVKIND);
    }

    @Test
    void limitingMagnitude_everyObjectAndFlag_matchesNative() {
        prime();
        int visible = 0, scotopic = 0, mixed = 0, below = 0;
        for (int fl : FLAGS) {
            for (int d = 0; d < DATES.length; d++) {
                for (int g = 0; g < PLACES.length; g++) {
                    for (int k = 0; k < 4; k++) {
                        for (int o = 0; o < OBJECTS.length; o++) {
                            final double[] r = visLimit(DATES[d] + k * 7.137 + g * 0.011, PLACES[g], AIRS[(k + g + d) % AIRS.length],
                                    OBSERVERS[(k + o) % OBSERVERS.length], OBJECTS[o], fl, 0 != (d + o + k) % 5);
                            if (r[50] >= 0) visible++;
                            if (r[50] > 0 && 0 != ((int) r[50] & SE_SCOTOPIC_FLAG)) scotopic++;
                            if (r[50] > 0 && 0 != ((int) r[50] & SE_MIXEDOPIC_FLAG)) mixed++;
                            if (-2 == r[50]) below++;
                        }
                    }
                }
            }
        }
        final String seen = visible + " visible, " + scotopic + " scotopic, " + mixed + " mixed, " + below + " below";
        assertTrue(visible > 1000 && scotopic > 200 && below > 1000 && visible - scotopic > 200, seen);
    }

    /** a Snellen ratio of nothing is 1e-8 for C; 0 is the default of 1 */
    @Test
    void limitingMagnitude_withoutAcuity_matchesNative() {
        prime();
        for (double acuity : new double[]{-1, 1e-12, 1e-8, 0, 3}) {
            for (int fl : FLAGS) {
                for (double h = 0; h < 1; h += 0.11) {
                    visLimit(2451550.1 + h, PLACES[1], AIRS[0], new double[]{36, acuity, 0, 4, 30, 0.7}, "venus", fl, true);
                }
            }
        }
    }

    /** minute by minute through dusk and dawn: the borders of 1394 and 1645 nL are passed here */
    @Test
    void limitingMagnitude_throughTwilight_matchesNative() {
        prime();
        for (int fl : FLAGS) {
            for (String name : new String[]{"venus", "jupiter", "sirius", "moon"}) {
                for (int i = 0; i < 1440; i += 3) {
                    visLimit(2451554.5 + i / 1440., PLACES[1], AIRS[0], OBSERVERS[2], name, fl, true);
                }
            }
        }
    }

    /** objects on, behind and beside the Moon: its light has a formula with the distance in it */
    @Test
    void limitingMagnitude_besideTheMoon_matchesNative() {
        prime();
        final double[] moon = new double[6];
        int near = 0;
        // Aldebaran is occulted by the Moon every month in 2015-2018
        for (double t = 2457100.5; t < 2458000; t += 0.0104) {
            getSwephExp().swe_calc_ut(t, SE_MOON, SEFLG_MOSEPH, moon, null);
            if (Math.abs(moon[0] - 69.97) > 0.6) continue;
            for (double[] geo : PLACES) {
                visLimit(t, geo, AIRS[0], OBSERVERS[0], "Aldebaran", SEFLG_SWIEPH, true);
            }
            near++;
        }
        assertTrue(near > 100, near + " dates near the Moon");
    }

    @Test
    void angles_matchNative() {
        prime();
        for (int fl : FLAGS) {
            for (double t0 : DATES) {
                for (int g = 0; g < PLACES.length; g++) {
                    final double[] geo = PLACES[g];
                    final double altMoon = 0 == g % 2 ? -10 : 35, altObj = 5 + g * 3;
                    for (double mag : new double[]{-4, 1, 6}) {
                        for (int az = 0; az < 360; az += 110) {
                            final double t = t0 + az / 100., azi = az;
                            final double[] air = AIRS[(g + az / 110) % AIRS.length], obs = OBSERVERS[(g + az / 110) % OBSERVERS.length];
                            final String what = " fl " + fl + " jd " + t + " geo " + g + " mag " + mag + " az " + az;
                            compare("heliacal_angle" + what, air, obs, "", true, TOL, (e, a, o, n, r, s) ->
                                    e.swe_heliacal_angle(t, geo, a, o, fl, mag, azi, azi + 10, 120, altMoon, r, s));
                            // a null serr is dereferenced by SunRA() in C; the bridge has one of its own
                            compare("topo_arcus_visionis" + what, air, obs, "", 110 != az, TOL, (e, a, o, n, r, s) ->
                                    e.swe_topo_arcus_visionis(t, geo, a, o, fl, mag, azi, altObj, azi + 10, 120, altMoon, r, s));
                        }
                    }
                }
            }
        }
    }

    /** the object on the Moon, within 0.0044 degrees (the radius which C takes) of its centre, and beside that */
    @Test
    void angles_behindTheMoon_matchNative() {
        prime();
        for (int fl : FLAGS) {
            for (double d : new double[]{0, 1e-9, 0.001, 0.004, 0.0045, 0.01, 0.25, 1}) {
                for (double altMoon : new double[]{5, 40}) {
                    for (double mag : new double[]{1, -5, -9, -12}) {
                        final double azi = -5 == mag ? 120 + d : 120;
                        compare("topo_arcus_visionis behind the Moon " + d + " fl " + fl + " mag " + mag, AIRS[0], OBSERVERS[0], "", true, TOL, (e, a, o, n, r, s) ->
                                e.swe_topo_arcus_visionis(2451550.1, PLACES[1], a, o, fl, mag, azi, altMoon + d, 250, 120, altMoon, r, s));
                    }
                }
            }
        }
    }

    @Test
    void details_matchNative() {
        prime();
        int i = 0, pairs = 0;
        for (int fl : new int[]{SEFLG_SWIEPH, SEFLG_MOSEPH, SEFLG_SWIEPH | SE_HELFLAG_HIGH_PRECISION,
                SEFLG_SWIEPH | SE_HELFLAG_OPTICAL_PARAMS, SEFLG_SWIEPH | SE_HELFLAG_VISLIM_SCOTOPIC,
                SEFLG_SWIEPH | SE_HELFLAG_AVKIND_VR}) {
            for (int type = 1; type <= 4; type++) {
                for (int o = 0; o < OBJECTS.length; o++) {
                    i++;
                    final double[] r = details(DATES[i % DATES.length] + o * 3.7, PLACES[(i / 3) % PLACES.length], AIRS[(i / 2) % AIRS.length],
                            OBSERVERS[i % OBSERVERS.length], OBJECTS[o], type, fl, 0 != i % 5);
                    if (0 == r[50] && 7 != r[0]) pairs++;
                }
            }
        }
        assertTrue(pairs > 200, pairs + " with a result");
    }

    /** the object does not rise: the C writes what it has and returns */
    @Test
    void details_whenTheObjectDoesNotRise_matchNative() {
        prime();
        final double[] north = {18, 68, 50};
        int invalid = 0;
        for (String name : new String[]{"venus", "mars", "sirius", "fomalhaut", "capella", "moon"}) {
            for (int type = 1; type <= 4; type++) {
                for (double t = 2451545; t < 2451545 + 365; t += 73) {
                    final double[] r = details(t, north, AIRS[0], OBSERVERS[0], name, type, SEFLG_SWIEPH, true);
                    if (TJD_INVALID == r[14]) invalid++;
                }
            }
        }
        assertTrue(invalid > 10, invalid + " without a rising");
    }

    private void events(String[] objects, int[] types, int[] flags, int step) {
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

    @Test
    void events_ofPlanets_matchNative() {
        events(new String[]{"venus", "Mercury", "mars", "JUPITER", "saturn"}, new int[]{1, 2, 3, 4},
                new int[]{SEFLG_SWIEPH, SEFLG_MOSEPH, SEFLG_SWIEPH | SE_HELFLAG_NO_DETAILS, SEFLG_SWIEPH | SE_HELFLAG_SEARCH_1_PERIOD,
                        SEFLG_SWIEPH | SE_HELFLAG_HIGH_PRECISION, SEFLG_SWIEPH | SE_HELFLAG_OPTICAL_PARAMS,
                        SEFLG_SWIEPH | SE_HELFLAG_VISLIM_NOMOON, SEFLG_SWIEPH | SE_HELFLAG_VISLIM_PHOTOPIC,
                        SEFLG_SWIEPH | SE_HELFLAG_VISLIM_SCOTOPIC}, 1);
    }

    @Test
    void events_ofStars_matchNative() {
        events(new String[]{"sirius", "Aldebaran", ",alTau", "Regulus"}, new int[]{1, 2},
                new int[]{SEFLG_SWIEPH, SEFLG_MOSEPH, SEFLG_SWIEPH | SE_HELFLAG_HIGH_PRECISION, SEFLG_SWIEPH | SE_HELFLAG_VISLIM_NOMOON}, 1);
    }

    /** more dates: the steps of the search for the day, and the warnings at the border of scotopic vision */
    @Test
    void events_ofVenusAndMars_fromManyDates_matchNative() {
        prime();
        int found = 0, i = 0;
        for (String name : new String[]{"venus", "mars"}) {
            for (int type = 1; type <= ("venus".equals(name) ? 4 : 2); type++) {
                for (double t = 2451545.2; t < 2451545 + 800; t += 19.3) {
                    i++;
                    final double[] r = event(t, PLACES[i % PLACES.length], AIRS[i % 3], OBSERVERS[i % 4], name, type,
                            SEFLG_SWIEPH, true);
                    if (0 == r[50]) found++;
                }
            }
        }
        assertTrue(found > 60, found + " events of " + i);
    }

    /**
     * North of 65 degrees, where the search for rising and setting is the slow one in C, too. It is the same bit
     * for bit, but its bisection starts at a culmination: the native engine itself moves by 1.6e-7 day when the
     * date it is given moves by one ulp, and the dates given here come from a search of their own.
     */
    @Test
    void events_inTheNorth_matchNative() {
        prime();
        final double[] north = {18, 66, 50};
        for (String name : new String[]{"venus", "mercury", "sirius"}) {
            for (int type = 1; type <= 2; type++) {
                final int kind = type;
                compare("heliacal_ut type " + type, AIRS[0], OBSERVERS[0], name, true, TOL_NORTH, (e, a, o, n, r, s) ->
                        e.swe_heliacal_ut(DATES[kind], north.clone(), a, o, name, kind, SEFLG_SWIEPH, r, s));
            }
        }
    }

    /**
     * Between 63 and 65 degrees the Sun's rising is one of {@code rise_set_fast()} of {@code swe_rise_trans()},
     * which the Java engine did not have until 2026-11-24 (it was off by 4 s), and the planet's a slow one.
     */
    @Test
    void events_between63and65degrees_matchNative() {
        prime();
        final double[] north = {18, 64.5, 50};
        for (String name : new String[]{"mercury", "mars", "regulus"}) {
            for (int type = 1; type <= 2; type++) {
                final int kind = type;
                compare("heliacal_ut type " + type, AIRS[2], OBSERVERS[1], name, true, TOL_DATE, (e, a, o, n, r, s) ->
                        e.swe_heliacal_ut(2455197.3, north.clone(), a, o, name, kind, SEFLG_SWIEPH, r, s));
            }
        }
    }

    @Test
    void events_ofTheMoon_matchNative() {
        events(new String[]{"moon", "Moon"}, new int[]{1, 2, 3, 4},
                new int[]{SEFLG_SWIEPH, SEFLG_MOSEPH, SEFLG_SWIEPH | SE_HELFLAG_HIGH_PRECISION,
                        SEFLG_SWIEPH | SE_HELFLAG_VISLIM_NOMOON, SEFLG_SWIEPH | SE_HELFLAG_AVKIND_VR}, 1);
    }

    @Test
    void events_ofTheArcusVisionisKinds_matchNative() {
        final int[] flags = new int[AV_KINDS.length];
        for (int i = 0; i < flags.length; i++) flags[i] = SEFLG_SWIEPH | AV_KINDS[i];
        events(new String[]{"venus", "mercury", "mars", "jupiter", "sirius", "regulus"}, new int[]{1, 2, 3, 4, 5, 6}, flags, 5);
    }

    /** what is refused, and how: the Sun, a star which does not exist, types which an object does not have */
    @Test
    void refusals_matchNative() {
        prime();
        for (String name : new String[]{"sun", "SUN", "Sunday", "nosuchstar", "nosuchstarwithalongername", "nosuchstarwiththirtyormorecharacters", "", "moon", "mars", "sirius", "Regulus,alLeo", "venus;x"}) {
            for (int type = 1; type <= 6; type++) {
                // mars and the stars have morning first and evening last, which are not refused
                if (type <= 2 && name.matches("mars|sirius|Regulus.*|venus.*") || type <= 4 && name.startsWith("venus")) continue;
                if (type >= 3 && type <= 4 && name.equals("moon")) continue;
                final double[] r = event(2451545.3, PLACES[1], AIRS[0], OBSERVERS[0], name, type, SEFLG_SWIEPH, true);
                assertTrue(r[50] < 0, name + " type " + type + " " + r[50]);
            }
        }
        final double[] high = {12.5, 41.9, 30000}, low = {12.5, 41.9, -900};
        for (double[] geo : new double[][]{high, low}) {
            assertEquals(ERR, event(2451545.3, geo, AIRS[0], OBSERVERS[0], "venus", 1, SEFLG_SWIEPH, true)[50]);
            assertEquals(ERR, details(2451545.3, geo, AIRS[0], OBSERVERS[0], "venus", 1, SEFLG_SWIEPH, true)[50]);
            compare("heliacal_angle", AIRS[0], OBSERVERS[0], "", true, TOL, (e, a, o, n, r, s) ->
                    e.swe_heliacal_angle(2451545.3, geo, a, o, SEFLG_SWIEPH, 1, 10, 20, 120, 30, r, s));
        }
        final StringBuilder serr = new StringBuilder("what another call has left");
        assertEquals(ERR, getSwissEph().swe_heliacal_ut(2451545.3, PLACES[1], AIRS[0].clone(), OBSERVERS[0].clone(), "Sun", 1,
                SEFLG_SWIEPH, new double[50], serr));
        assertEquals("the sun has no heliacal rising or setting\n", serr.toString());
        assertEquals(ERR, getSwissEph().swe_vis_limit_mag(2451545.3, PLACES[1], AIRS[0].clone(), OBSERVERS[0].clone(),
                new StringBuilder("Sun"), SEFLG_SWIEPH, new double[50], serr));
        assertEquals("it makes no sense to call swe_vis_limit_mag() for the Sun", serr.toString());
    }

    /** lower case up to the comma, no more than 30 characters, up to the first one a name cannot have */
    @Test
    void names_matchNative() {
        prime();
        for (String name : new String[]{"VENUS", "VenUS", "Aldebaran", "ALDEBARAN", ",alTau", "Aldebaran,alTau", "REGULUS,alLeo",
                "Barnard's Star", "venus_and_more", "jupiter and a name of more than thirty characters", "Antares/x"}) {
            for (double t : DATES) {
                visLimit(t + 0.4, PLACES[2], AIRS[0], OBSERVERS[0], name, SEFLG_SWIEPH, true);
            }
            details(2451545.4, PLACES[2], AIRS[0], OBSERVERS[0], name, 1, SEFLG_SWIEPH, true);
        }
        final Locale before = Locale.getDefault();
        Locale.setDefault(new Locale("tr"));
        try {
            for (String name : new String[]{"JUPITER", "SIRIUS", "VENUS"}) {
                visLimit(2451545.4, PLACES[2], AIRS[0], OBSERVERS[0], name, SEFLG_SWIEPH, true);
            }
        } finally {
            Locale.setDefault(before);
        }
    }

    /** the magnitude of the last star is kept, and read again after another one */
    @Test
    void starsInTurn_matchNative() {
        prime();
        for (String name : new String[]{"sirius", "sirius", "aldebaran", "sirius", "nosuchstar", "nosuchstar", "sirius", ",alTau", "aldebaran"}) {
            for (double h = 0; h < 1; h += 0.25) {
                visLimit(2451545.1 + h, PLACES[1], AIRS[0], OBSERVERS[0], name, SEFLG_SWIEPH, true);
            }
        }
    }
}
