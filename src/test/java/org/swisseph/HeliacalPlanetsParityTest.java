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
 * Heliacal events of the planets, also in the north, split off from {@link HeliacalParityTest}.
 * The cases, tolerances and helpers are in {@link HeliacalParityTestBase}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
public class HeliacalPlanetsParityTest extends HeliacalParityTestBase {
    @Test
    void events_ofPlanets_matchNative() {
        events(new String[]{"venus", "Mercury", "mars", "JUPITER", "saturn"}, new int[]{1, 2, 3, 4},
                new int[]{SEFLG_SWIEPH, SEFLG_MOSEPH, SEFLG_SWIEPH | SE_HELFLAG_NO_DETAILS, SEFLG_SWIEPH | SE_HELFLAG_SEARCH_1_PERIOD,
                        SEFLG_SWIEPH | SE_HELFLAG_HIGH_PRECISION, SEFLG_SWIEPH | SE_HELFLAG_OPTICAL_PARAMS,
                        SEFLG_SWIEPH | SE_HELFLAG_VISLIM_NOMOON, SEFLG_SWIEPH | SE_HELFLAG_VISLIM_PHOTOPIC,
                        SEFLG_SWIEPH | SE_HELFLAG_VISLIM_SCOTOPIC}, 1);
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
}
