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
 * The slow method of {@code swe_rise_trans} and a second run, split off from {@link RiseSetParityTest}.
 * The cases, tolerances and helpers are in {@link RiseSetParityTestBase}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
public class RiseSetSlowParityTest extends RiseSetParityTestBase {
    /** beyond 60 and 65 degrees, or when it is asked for; the bodies after the nodes always */
    @Test
    void theSlowMethod_matchesNative() {
        int i = 0, never = 0, found = 0;
        for (double lat : LATITUDES) {
            for (int ipl : new int[]{SE_SUN, SE_MOON, SE_MERCURY, SE_JUPITER, SE_PLUTO, SE_MEAN_NODE, SE_TRUE_NODE, SE_MEAN_APOG, SE_CHIRON,
                    SE_AST_OFFSET + 134340, SE_CERES, SE_CUPIDO, SE_WALDEMATH}) {
                for (int flag : FAST_FLAGS) {
                    for (int ev : new int[]{SE_CALC_RISE, SE_CALC_SET}) {
                        i++;
                        // Chiron and Ceres are not in Moshier's ephemeris, and no file of theirs may be here
                        final boolean moshier = 0 == i % 2 && SE_CHIRON != ipl && SE_CERES != ipl;
                        final double t = DATES[i % 4] + (i / 7) % 400 * 0.913;
                        final double[] r = rise(t, ipl, null, moshier ? SEFLG_MOSEPH : SEFLG_SWIEPH,
                                ev | flag | SE_BIT_FORCE_SLOW_METHOD, place(i, lat), 0 == i % 4 ? 0 : 1013.25, 15 - i % 5 * 8, 0 != i % 6);
                        if (CIRCUMPOLAR == r[0]) never++;
                        if (0 == r[0]) found++;
                    }
                }
            }
        }
        assertTrue(found > 1500 && never > 200, found + " events, " + never + " circumpolar");
    }

    /** the hours around an event, where the first run finds one of before and a second one is made */
    @Test
    void theSecondRun_matchesNative() {
        int before = 0, after = 0;
        for (int ipl : new int[]{SE_SUN, SE_MOON, SE_VENUS, SE_PLUTO, SE_MEAN_NODE}) {
            for (int flag : new int[]{0, SE_BIT_HINDU_RISING, SE_BIT_GEOCTR_NO_ECL_LAT, SE_BIT_DISC_BOTTOM}) {
                for (int ev : new int[]{SE_CALC_RISE, SE_CALC_SET}) {
                    for (double lat : new double[]{10, -45, 59.5, 64.9}) {
                        final double[] geo = {12.5, lat, 100};
                        final double tr = rise(2457500.1 + ipl * 11, ipl, null, SEFLG_SWIEPH, ev | flag, geo, 1013.25, 10, true)[1];
                        if (tr <= 0) continue;
                        for (int m = -20; m <= 20; m++) {
                            final double t = tr + m * Math.abs(m) / 14400.0 + 1e-6;
                            if (tr + 0.5 < rise(t, ipl, null, SEFLG_SWIEPH, ev | flag, geo, 1013.25, 10, true)[1]) after++;
                            else before++;
                        }
                    }
                }
            }
        }
        assertTrue(before > 1000 && after > 1000, before + " of the day, " + after + " of the next one");
    }
}
