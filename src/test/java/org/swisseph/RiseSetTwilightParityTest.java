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
 * Twilights and the height of the horizon of {@code swe_rise_trans}, split off from {@link RiseSetParityTest}.
 * The cases, tolerances and helpers are in {@link RiseSetParityTestBase}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
public class RiseSetTwilightParityTest extends RiseSetParityTestBase {
    /** a twilight is one of the Sun's; for another body its bit only means the slow method */
    @Test
    void twilights_matchNative() {
        int i = 0, never = 0, found = 0;
        for (double lat : LATITUDES) {
            for (int bit : TWILIGHTS) {
                for (int ev : new int[]{SE_CALC_RISE, SE_CALC_SET}) {
                    for (int ipl : new int[]{SE_SUN, SE_MOON, SE_MARS}) {
                        for (int k = 0; k < 8; k++) {
                            i++;
                            final double[] r = rise(DATES[k] + i * 1.37, ipl, null, EPHEMERIDES[i % 2], ev | bit, place(i, lat), 1013.25, 10, true);
                            if (CIRCUMPOLAR == r[0]) never++;
                            if (0 == r[0]) found++;
                        }
                    }
                }
            }
        }
        // all three at once
        rise(2451545.3, SE_SUN, null, SEFLG_SWIEPH, SE_CALC_RISE | TWILIGHTS[0] | TWILIGHTS[1] | TWILIGHTS[2], place(1, 40), 1013.25, 10, true);
        assertTrue(found > 1000 && never > 100, found + " twilights, " + never + " nights without");
    }

    /** a horizon of its own height; -100 is the sea at the dip of a high place */
    @Test
    void theHeightOfTheHorizon_matchesNative() {
        int i = 0, dip = 0;
        for (double lat : new double[]{0, 28.6, -50, 62, 66, -70, 85}) {
            for (double height : new double[]{0, -100, -99.9, 2.5, -1.5, 10, 50}) {
                for (int ipl : new int[]{SE_SUN, SE_MOON, SE_VENUS, SE_MEAN_NODE}) {
                    for (int flag : new int[]{0, SE_BIT_HINDU_RISING, SE_BIT_DISC_BOTTOM, SE_BIT_NO_REFRACTION, SE_BIT_CIVIL_TWILIGHT}) {
                        for (int ev : new int[]{SE_CALC_RISE, SE_CALC_SET}) {
                            i++;
                            final double[] r = horizon(DATES[i % DATES.length] + i * 0.913, ipl, null, EPHEMERIDES[i % 2], ev | flag,
                                    place(i, lat), 0 == i % 3 ? 0 : 1013.25, 10 - i % 4 * 10, height);
                            if (-100 == height && 0 == r[0]) dip++;
                        }
                    }
                }
            }
            horizon(2451545.3, SE_SUN, "sirius", SEFLG_SWIEPH, SE_CALC_SET, place(2, lat), 1013.25, 10, -100);
        }
        assertTrue(dip > 100, dip + " on a sea horizon");
    }
}
