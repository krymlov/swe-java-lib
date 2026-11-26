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
 * The fast method of {@code swe_rise_trans} against the native library, split off from {@link RiseSetParityTest}.
 * The cases, tolerances and helpers are in {@link RiseSetParityTestBase}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
public class RiseSetFastParityTest extends RiseSetParityTestBase {
    /** every event, the flags which the fast method knows, both sides of its borders */
    @Test
    void theFastMethod_matchesNative() {
        int i = 0, never = 0, found = 0;
        for (double lat : LATITUDES) {
            for (int ipl = SE_SUN; ipl <= SE_TRUE_NODE; ipl++) {
                for (int flag : FAST_FLAGS) {
                    for (int ev : new int[]{SE_CALC_RISE, SE_CALC_SET}) {
                        for (int eph : EPHEMERIDES) {
                            i++;
                            final double t = DATES[i % DATES.length] + (i / 7) % 400 * 0.913;
                            final double[] r = rise(t, ipl, null, eph, ev | flag, place(i, lat), 0 == i % 4 ? 0 : 1013.25 - i % 4 * 60,
                                    15 - i % 5 * 8, 0 != i % 6);
                            if (CIRCUMPOLAR == r[0]) never++;
                            if (0 == r[0]) found++;
                        }
                    }
                }
            }
        }
        assertTrue(found > 5000 && never > 300, found + " events, " + never + " circumpolar");
    }
}
