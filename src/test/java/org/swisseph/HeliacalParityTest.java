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
public class HeliacalParityTest extends HeliacalParityTestBase {
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
