/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-11
 */
package org.swisseph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static swisseph.SweConst.*;

/**
 * The local search of solar eclipses and one try of an occultation from everywhere, split off from {@link EclipseParityTest}.
 * The cases, tolerances and helpers are in {@link EclipseParityTestBase}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
public class EclipseLocalParityTest extends EclipseParityTestBase {
    @Test
    void theLocalSearch_matchesNative() {
        int rise = 0, partlyVisible = 0, all = 0, central = 0;
        final Set<Integer> kinds = new TreeSet<>();
        for (double d : DATES) {
            for (int eph : EPHEMERIDES) {
                for (double[] p : PLACES) {
                    for (int k = 0; k < 2; k++) {
                        for (int back : DIRECTIONS) {
                            if (d < 700000 && 1 == back) continue;
                            final Result r = solLoc(d + k * 397.1, eph, p, back);
                            all++;
                            if (r.tret[5] > 100 || r.tret[6] > 100) rise++;
                            if (r.ret > 0 && !r.is(SE_ECL_1ST_VISIBLE) || !r.is(SE_ECL_4TH_VISIBLE)) partlyVisible++;
                            if (r.is(SE_ECL_TOTAL | SE_ECL_ANNULAR)) central++;
                            kinds.add(r.ret);
                        }
                    }
                }
            }
        }
        assertTrue(all > 500 && rise > 30 && partlyVisible > 30 && central > 0 && kinds.size() > 8,
                all + " " + rise + " " + partlyVisible + " " + central + " " + kinds);
    }

    /** one conjunction, tried or not, from everywhere; when it fails, the date for the next try */
    @Test
    void oneTry_matchesNative() {
        int hit = 0, missed = 0, day = 0;
        final Object[][] bodies = {{SE_VENUS, null}, {SE_MARS, null}, {SE_JUPITER, null}, {SE_PLUTO, null}, {SE_AST_OFFSET + 134340, null},
                {SE_MEAN_NODE, null}, {SE_SUN, null}, {SE_SUN, "Aldebaran"}, {SE_SUN, "Regulus"}, {SE_SUN, "Spica"}, {SE_SUN, ",alSco"},
                {SE_MARS, "Pollux"}};
        for (Object[] b : bodies) {
            final int ipl = (int) b[0];
            final String star = (String) b[1];
            for (double d : new double[]{2451545.3, 2457100.7, 2457800.2, 2086400.4}) {
                for (int eph : EPHEMERIDES) {
                    for (int one : ONE_TRY) {
                        for (int type : new int[]{ANY, SE_ECL_TOTAL, SE_ECL_PARTIAL}) {
                            if (occGlob(d, ipl, star, eph, type, one).ret > 0) hit++;
                            else missed++;
                        }
                        for (double[] p : PLACES) {
                            for (int k = 0; k < 2; k++) {
                                final Result r = occLoc(d + 27.3 * k, ipl, star, eph, p, one);
                                if (r.ret > 0) hit++;
                                else missed++;
                                if (r.is(SE_ECL_OCC_BEG_DAYLIGHT) || r.is(SE_ECL_OCC_END_DAYLIGHT)) day++;
                            }
                        }
                    }
                }
            }
        }
        assertTrue(hit > 80 && missed > 2500 && day > 20, hit + " " + missed + " " + day);
    }
}
