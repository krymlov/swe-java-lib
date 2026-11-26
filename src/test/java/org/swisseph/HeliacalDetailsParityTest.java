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
 * {@code swe_heliacal_pheno_ut} and the limiting magnitude of every object, split off from {@link HeliacalParityTest}.
 * The cases, tolerances and helpers are in {@link HeliacalParityTestBase}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
public class HeliacalDetailsParityTest extends HeliacalParityTestBase {
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
}
