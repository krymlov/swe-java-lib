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
 * Solar eclipses and occultations by the Moon, pure Java against native:
 * {@code swe_sol_eclipse_when_glob(), _where(), _how(), _when_loc()} and
 * {@code swe_lun_occult_when_glob(), _where(), _when_loc()} - return value, {@code serr}, name of the
 * star and every element of the arrays, those which C leaves alone included.
 * <p>
 * Until 2026-11-24 the Java engine looked for an eclipse of a given type without
 * {@code SE_ECL_CENTRAL} and {@code SE_ECL_NONCENTRAL}, which C adds, and ran to the end of the
 * ephemeris; it gave a magnitude of 100 instead of 1 for a star or a point; and the local search for
 * an occultation was that of 2.07.
 * <p>
 * The measured differences are 3e-9 of a day with the files (1.4e-8 for a grazing star) and 3e-7 in Moshier's ephemeris. The time
 * of a maximum is where a parabola through three distances has its vertex, and is as noisy for native
 * when its place is moved by as much as the two Moons differ (1e-12 of a degree). The distance of the
 * centres is an arc cosine near 1.
 * <p>
 * {@code @Isolated}: the tidal acceleration and the lapse rate are JVM-global in the Java engine.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
@Isolated
public class EclipseParityTest extends EclipseParityTestBase {
    /** every type which C knows, then where it is, and how it is there and elsewhere */
    @Test
    void theGlobalSearch_itsPlace_andItsLook_matchNative() {
        final Set<Integer> kinds = new TreeSet<>();
        int noEclipse = 0, belowHorizon = 0;
        for (double d : DATES) {
            for (int eph : EPHEMERIDES) {
                for (int type : TYPES) {
                    for (int back : DIRECTIONS) {
                        final Result g = solGlob(d, eph, type, back);
                        if (g.ret <= 0) continue;
                        kinds.add(g.ret);
                        for (double off : new double[]{0, -0.06, 0.02, 3}) {
                            final double t = g.tret[0] + off;
                            final Result w = solWhere(t, eph);
                            if (0 == w.ret) noEclipse++;
                            solHow(t, eph, new double[]{w.geo[0], w.geo[1], 0});
                            for (double[] p : PLACES) if (0 == solHow(t, eph, p).ret) belowHorizon++;
                        }
                    }
                }
            }
        }
        assertTrue(kinds.contains(SE_ECL_TOTAL | SE_ECL_CENTRAL) && kinds.contains(SE_ECL_ANNULAR | SE_ECL_CENTRAL)
                && kinds.contains(SE_ECL_ANNULAR_TOTAL | SE_ECL_CENTRAL) && kinds.contains(SE_ECL_PARTIAL | SE_ECL_NONCENTRAL), kinds.toString());
        assertTrue(noEclipse > 300 && belowHorizon > 1000, noEclipse + " " + belowHorizon);
    }

    /** the rare ones: total or annular, and the axis of the shadow misses the Earth */
    @Test
    void theNonCentralEclipses_matchNative() {
        for (int type : new int[]{SE_ECL_TOTAL | SE_ECL_NONCENTRAL, SE_ECL_ANNULAR | SE_ECL_NONCENTRAL}) {
            for (int eph : EPHEMERIDES) {
                for (int back : DIRECTIONS) {
                    final Result g = solGlob(2440000.5, eph, type, back);
                    assertEquals(type, g.ret);
                    solWhere(g.tret[0], eph);
                    for (double[] p : PLACES) solHow(g.tret[0], eph, p);
                }
            }
        }
    }

    /** every eclipse of a century, one after the other in both directions */
    @Test
    void theChainOfEclipses_matchesNative() {
        int n = 0;
        for (int back : DIRECTIONS) {
            for (double t = 2415020.5; Math.abs(t - 2415020.5) < 36525; n++) {
                t = solGlob(t, SEFLG_SWIEPH, ANY, back).tret[0];
                solWhere(t, SEFLG_SWIEPH);
                t += 0 == back ? 1 : -1;
            }
        }
        assertTrue(n > 440, n + " eclipses");
    }

    /** hybrid eclipses must be central ones, and C says so */
    @Test
    void theImpossibleType_isRefusedAsInNative() {
        for (int back : DIRECTIONS) {
            final Result r = solGlob(2451545, SEFLG_SWIEPH, SE_ECL_ANNULAR_TOTAL | SE_ECL_NONCENTRAL, back);
            assertEquals(ERR, r.ret);
            assertEquals("non-central hybrid (annular-total) eclipses do not exist", r.serr);
        }
        assertEquals(ERR, solGlob(2451545, SEFLG_SWIEPH, SE_ECL_PARTIAL | SE_ECL_CENTRAL, 0).ret);
    }

    /** total, annular and hybrid ones, seen from where they are greatest and from near the limits */
    @Test
    void theLocalSearch_onTheCentralLine_matchesNative() {
        int total = 0, annular = 0, central = 0, all = 0;
        for (double d : new double[]{2451545.3, 2433282.6, 2415100.1, 1757700.7}) {
            for (int type : new int[]{SE_ECL_TOTAL, SE_ECL_ANNULAR, SE_ECL_ANNULAR_TOTAL}) {
                double t = d;
                for (int k = 0; k < 5; k++) {
                    t = solGlob(t, SEFLG_SWIEPH, type, 0).tret[0];
                    final double[] g = solWhere(t, SEFLG_SWIEPH).geo;
                    for (double off : new double[]{0, 0.2, -0.4, 0.8, -1.6}) {
                        final double lat = g[1] + off;
                        if (Math.abs(lat) > 89) continue;
                        final Result r = solLoc(t - 1, SEFLG_SWIEPH, new double[]{g[0], lat, 0}, 0);
                        solHow(t, SEFLG_SWIEPH, new double[]{g[0], lat, 0});
                        all++;
                        if (r.is(SE_ECL_TOTAL)) total++;
                        if (r.is(SE_ECL_ANNULAR)) annular++;
                        if (r.is(SE_ECL_2ND_VISIBLE) && r.is(SE_ECL_3RD_VISIBLE)) central++;
                    }
                    t++;
                }
            }
        }
        assertTrue(total > 60 && annular > 60 && central > 120, total + " " + annular + " " + central + " of " + all);
    }

    /** the Saros number of an eclipse, the first of a series which comes before the date of the table included */
    @Test
    void theSaros_matchesNative() {
        int n = 0, early = 0;
        for (double t = 990000; t < 2800000; n++) {
            t = theSarosOfTheNext(t).tret[0] + 6000;
        }
        // up to half a day before the first date of its series, and C allows for two
        for (double t : new double[]{787294.07, 834779.04, 838765.99, 886250.95, 890237.94, 997345.03, 1038245.06, 1492485.32, 1935296.49}) {
            final Result g = theSarosOfTheNext(t - 5);
            assertEquals(t, g.tret[0], 0.02);
            final double[] w = solWhere(g.tret[0], SEFLG_MOSEPH).geo;
            if (solHow(g.tret[0], SEFLG_MOSEPH, new double[]{w[0], w[1], 0}).attr[9] > 0) early++;
        }
        assertTrue(n > 290 && early > 5, n + " eclipses, " + early + " before the table");
    }

    /** the last days of a polar day or night, when the Sun rises but does not set any more, or the reverse */
    @Test
    void theDaylight_inThePolarRegions_matchesNative() {
        final double[][] cases = {{SE_MARS, 2432381.679, 270, 67.5}, {SE_MARS, 2431755.721, 90, 78}, {SE_VENUS, 2439091.737, 270, -68},
                {SE_MARS, 2421194.496, 0, -68}, {SE_VENUS, 2431672.14, 90, 72}, {SE_SATURN, 2431645.894, 180, 66.8}};
        for (double[] c : cases) {
            for (int eph : EPHEMERIDES) {
                final Result r = occLoc(c[1] - 0.9, (int) c[0], null, eph, new double[]{c[2], c[3], 0}, 0);
                assertEquals(c[1], r.tret[1], 0.01);
            }
        }
    }

    /** started when the greatest moment has just passed: it is not found again, and the date for the next try is given */
    @Test
    void oneTry_fromWithinAnOccultation_matchesNative() {
        int late = 0;
        for (int back : DIRECTIONS) {
            for (double lon = 0; lon < 360; lon += 30) {
                for (double lat = -60; lat <= 60; lat += 30) {
                    final double[] p = {lon, lat, 0};
                    final Result r = occLoc(2457106.3, SE_SUN, "Aldebaran", SEFLG_SWIEPH, p, SE_ECL_ONE_TRY);
                    if (r.ret <= 0) continue;
                    for (double off : new double[]{0.001, 0.02, 0.05}) {
                        if (occLoc(r.tret[0] + (0 == back ? off : -off), SE_SUN, "Aldebaran", SEFLG_SWIEPH, p, SE_ECL_ONE_TRY | back).ret == 0) late++;
                    }
                }
            }
        }
        assertTrue(late > 20, late + "");
    }

    /** the search from conjunction to conjunction */
    @Test
    void theLocalSearch_ofOccultations_matchesNative() {
        int hit = 0;
        for (int ipl : new int[]{SE_SUN, SE_VENUS, SE_JUPITER}) {
            for (int eph : EPHEMERIDES) {
                for (int back : DIRECTIONS) {
                    for (double[] p : new double[][]{PLACES[0], PLACES[2], PLACES[3]}) {
                        if (occLoc(2455197.3, ipl, null, eph, p, back).ret > 0) hit++;
                    }
                }
            }
        }
        for (int back : DIRECTIONS) {
            if (occLoc(2457100.7, SE_SUN, "Aldebaran", SEFLG_SWIEPH, PLACES[0], back).ret > 0) hit++;
        }
        assertTrue(hit >= 20, hit + " occultations");
    }

    @Test
    void theRefusals_ofOccultations_matchNative() {
        for (int back : new int[]{0, 1, SE_ECL_ONE_TRY}) {
            for (int eph : EPHEMERIDES) {
                // a star which the Moon never reaches, a missing one, a name which is none
                for (String star : new String[]{"Sirius", "Polaris", ",alLyr", "nosuchstar", ",xxYyy", "0"}) {
                    assertTrue(occGlob(2451545.3, SE_SUN, star, eph, ANY, back).ret <= 0, star);
                    assertTrue(occLoc(2451545.3, SE_SUN, star, eph, PLACES[0], back).ret <= 0, star);
                }
                // an annular one of a planet or a star
                for (int type : new int[]{SE_ECL_ANNULAR, SE_ECL_ANNULAR | SE_ECL_CENTRAL, SE_ECL_ANNULAR | SE_ECL_NONCENTRAL,
                        SE_ECL_ANNULAR_TOTAL}) {
                    final Result r = occGlob(2451545.3, SE_VENUS, null, eph, type, back);
                    assertEquals(ERR, r.ret);
                    assertEquals("annular occulation do not exist for object 3 \n", r.serr);
                    assertEquals(ERR, occGlob(2451545.3, SE_VENUS, "Regulus", eph, type, back).ret);
                }
                // not refused, but without the annular ones
                for (int type : new int[]{SE_ECL_ANNULAR_TOTAL | SE_ECL_TOTAL, SE_ECL_ANNULAR | SE_ECL_TOTAL,
                        SE_ECL_TOTAL | SE_ECL_PARTIAL | SE_ECL_ANNULAR | SE_ECL_ANNULAR_TOTAL}) {
                    occGlob(2451545.3, SE_VENUS, null, eph, type, back);
                }
                // the Sun is occulted in every way
                for (int type : new int[]{SE_ECL_ANNULAR, SE_ECL_ANNULAR | SE_ECL_CENTRAL, SE_ECL_ANNULAR_TOTAL, SE_ECL_PARTIAL}) {
                    occGlob(2451545.3, SE_SUN, null, eph, type, back);
                }
                // no body of this number
                for (int ipl : new int[]{-1, SE_NPLANETS + 5}) {
                    occGlob(2451545.3, ipl, null, eph, ANY, SE_ECL_ONE_TRY);
                    occLoc(2451545.3, ipl, null, eph, PLACES[0], SE_ECL_ONE_TRY);
                    occWhere(2451545.3, ipl, null, eph);
                }
            }
            occWhere(2451545.3, SE_SUN, "nosuchstar", SEFLG_SWIEPH);
        }
    }

    /** Pluto is a minor planet as well; an asteroid without a file is an error of C's */
    @Test
    void theAsteroids_matchNative() {
        for (int eph : EPHEMERIDES) {
            occWhere(2451545.3, SE_AST_OFFSET + 134340, null, eph);
            occWhere(2451545.3, SE_PLUTO, null, eph);
        }
        for (int ipl : new int[]{SE_CERES, SE_VESTA, SE_AST_OFFSET + 1, SE_AST_OFFSET + 987654}) {
            for (int one : ONE_TRY) {
                occGlob(2451545.3, ipl, null, SEFLG_SWIEPH, ANY, one);
                occLoc(2451545.3, ipl, null, SEFLG_SWIEPH, PLACES[0], one);
            }
            occWhere(2451545.3, ipl, null, SEFLG_SWIEPH);
        }
    }

    /** a height out of the range of C's refraction */
    @Test
    void theHeight_isRefusedAsInNative() {
        for (double h : new double[]{LOWEST - 0.1, HIGHEST + 0.1, 1e6, -1e6}) {
            final double[] p = {30.5, 50.45, h};
            final Result[] r = {solHow(2451545.3, SEFLG_SWIEPH, p), solLoc(2451545.3, SEFLG_SWIEPH, p, 0),
                    occLoc(2451545.3, SE_VENUS, null, SEFLG_SWIEPH, p, 0), occLoc(2451545.3, SE_SUN, "Regulus", SEFLG_SWIEPH, p, 1)};
            for (Result x : r) {
                assertEquals(ERR, x.ret);
                assertTrue(x.serr.startsWith("location for "), x.serr);
            }
        }
        for (double h : new double[]{LOWEST, HIGHEST}) {
            final double[] p = {30.5, 50.45, h};
            assertNotEquals(ERR, solHow(2451545.3, SEFLG_SWIEPH, p).ret);
            assertNotEquals(ERR, solLoc(2451545.3, SEFLG_SWIEPH, p, 0).ret);
            occLoc(2451545.3, SE_VENUS, null, SEFLG_SWIEPH, p, SE_ECL_ONE_TRY);
        }
    }

    /** flags which C removes; no serr; a date before every ephemeris and one after */
    @Test
    void theFlags_theMissingSerr_andTheEnd_matchNative() {
        for (int extra : new int[]{SEFLG_SPEED, SEFLG_SIDEREAL, SEFLG_HELCTR, SEFLG_NONUT, SEFLG_TRUEPOS, SEFLG_TOPOCTR, SEFLG_XYZ, SEFLG_RADIANS}) {
            for (int eph : EPHEMERIDES) {
                final int f = eph | extra;
                compare("sol when_glob " + extra, eph, null, new double[0], false, (e, n, g, r, a, s) -> e.swe_sol_eclipse_when_glob(2451545.3, f, 0, r, 0, s));
                compare("sol where " + extra, eph, null, new double[2], false, (e, n, g, r, a, s) -> e.swe_sol_eclipse_where(2451401.96, f, g, a, s));
                compare("sol how " + extra, eph, null, new double[]{24, 45, 0}, false, (e, n, g, r, a, s) -> e.swe_sol_eclipse_how(2451401.96, f, g, a, s));
                compare("sol when_loc " + extra, eph, null, PLACES[0], false, (e, n, g, r, a, s) -> e.swe_sol_eclipse_when_loc(2451545.3, f, g, r, a, 0, s));
                compare("occ when_glob " + extra, eph, null, new double[0], false, (e, n, g, r, a, s) -> e.swe_lun_occult_when_glob(2451545.3, SE_MARS, n, f, 0, r, 0, s));
                compare("occ where " + extra, eph, null, new double[2], false, (e, n, g, r, a, s) -> e.swe_lun_occult_where(2451545.3, SE_MARS, n, f, g, a, s));
                compare("occ when_loc " + extra, eph, null, PLACES[0], false, (e, n, g, r, a, s) -> e.swe_lun_occult_when_loc(2451545.3, SE_MARS, n, f, g, r, a, SE_ECL_ONE_TRY, s));
            }
        }
        for (double t : new double[]{-3100000, -3027300.5, 5373400, 5500000}) {
            for (int back : DIRECTIONS) {
                solGlob(t, SEFLG_SWIEPH, ANY, back);
                solLoc(t, SEFLG_SWIEPH, PLACES[0], back);
                occGlob(t, SE_VENUS, null, SEFLG_SWIEPH, ANY, SE_ECL_ONE_TRY | back);
            }
            solWhere(t, SEFLG_SWIEPH);
            solHow(t, SEFLG_SWIEPH, PLACES[0]);
            occWhere(t, SE_VENUS, null, SEFLG_SWIEPH);
        }
    }
}
