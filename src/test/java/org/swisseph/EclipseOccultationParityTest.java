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
 * Occultations of planets and stars, also a series from everywhere, split off from {@link EclipseParityTest}.
 * The cases, tolerances and helpers are in {@link EclipseParityTestBase}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
public class EclipseOccultationParityTest extends EclipseParityTestBase {
    /** Moon over planet, Sun and star; where the occultation is greatest; and how it begins there */
    @Test
    void theOccultations_ofPlanetsAndStars_matchNative() {
        final Set<Integer> kinds = new TreeSet<>();
        final Object[][] bodies = {{SE_VENUS, null}, {SE_MERCURY, null}, {SE_MARS, null}, {SE_JUPITER, null}, {SE_SATURN, ""},
                {SE_URANUS, null}, {SE_NEPTUNE, null}, {SE_SUN, null}, {SE_SUN, "Aldebaran"}, {SE_SUN, ",alTau"},
                {SE_VENUS, "Regulus"}, {SE_SUN, "Spica"}, {SE_SUN, "antares"}};
        int n = 0;
        for (Object[] b : bodies) {
            final int ipl = (int) b[0];
            final String star = (String) b[1];
            for (double d : new double[]{2451545.3, 2086400.4}) {
                for (int eph : EPHEMERIDES) {
                    for (int back : DIRECTIONS) {
                        for (int type : SEFLG_MOSEPH == eph ? new int[]{ANY} : new int[]{ANY, SE_ECL_TOTAL, SE_ECL_TOTAL | SE_ECL_CENTRAL}) {
                            final Result g = occGlob(d, ipl, star, eph, type, back);
                            if (g.ret <= 0) continue;
                            kinds.add(g.ret);
                            n++;
                            for (double off : new double[]{0, 0.02, -0.05, 2}) {
                                final Result w = occWhere(g.tret[0] + off, ipl, star, eph);
                                if (0 != off || w.ret <= 0) continue;
                                for (int one : ONE_TRY) {
                                    occLoc(g.tret[0] + (SE_ECL_ONE_TRY == one ? -0.3 : 0.3), ipl, star, eph, new double[]{w.geo[0], w.geo[1], 0}, one);
                                }
                            }
                        }
                    }
                }
            }
        }
        assertTrue(n > 100 && kinds.contains(SE_ECL_TOTAL | SE_ECL_CENTRAL) && kinds.contains(SE_ECL_TOTAL | SE_ECL_NONCENTRAL), n + " " + kinds);
    }

    /** the whole series of 2015-2018 of Aldebaran and of 2017 of Regulus, and Venus and Mars, seen from everywhere */
    @Test
    void theOccultations_ofASeries_fromEverywhere_matchNative() {
        int hit = 0, beg = 0, end = 0, rise = 0, all = 0, grazing = 0;
        final Object[][] bodies = {{SE_SUN, "Aldebaran", 2457050.5}, {SE_SUN, "Regulus", 2457800.5}, {SE_VENUS, null, 2451545.3},
                {SE_MARS, null, 2451545.3}, {SE_JUPITER, null, 2457550.5}, {SE_SATURN, null, 2458500.5}};
        for (Object[] b : bodies) {
            final int ipl = (int) b[0];
            final String star = (String) b[1];
            double t = (double) b[2];
            for (int k = 0; k < 3; k++) {
                t = occGlob(t, ipl, star, SEFLG_SWIEPH, ANY, 0).tret[0];
                final double[] w = occWhere(t, ipl, star, SEFLG_SWIEPH).geo;
                for (int lat = -84; lat <= 84; lat += 12) {
                    for (int lon = -120; lon <= 120; lon += 60) {
                        final Result r = occLoc(t - 0.4, ipl, star, 0 == (lat / 12 + k) % 2 ? SEFLG_SWIEPH : SEFLG_MOSEPH,
                                new double[]{w[0] + lon, lat, 50}, SE_ECL_ONE_TRY);
                        all++;
                        if (r.ret <= 0) continue;
                        hit++;
                        if (r.is(SE_ECL_OCC_BEG_DAYLIGHT)) beg++;
                        if (r.is(SE_ECL_OCC_END_DAYLIGHT)) end++;
                        if (r.tret[5] > 100 || r.tret[6] > 100) rise++;
                        if (r.is(SE_ECL_PARTIAL)) grazing++;
                    }
                }
                t++;
            }
        }
        assertTrue(all > 1000 && hit > 150 && beg > 60 && end > 60 && rise > 15 && grazing >= 0, all + " " + hit + " " + beg + " " + end + " " + rise + " " + grazing);
    }
}
