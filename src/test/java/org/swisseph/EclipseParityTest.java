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
public class EclipseParityTest extends AbstractTest {

    static final int[] EPHEMERIDES = {SEFLG_SWIEPH, SEFLG_MOSEPH};
    /** -3000 to 2100 */
    static final double[] DATES = {2451545.3, 2455197.3, 2433282.6, 2415100.1, 2305500.2, 2086400.4, 1757700.7, 626000.3, 2488000.5};
    /** the last one is in the polar night, or day, half of the time */
    static final double[][] PLACES = {{30.5, 50.45, 100}, {0, 0, 0}, {-70, -33, 2400}, {140, 68, 0}, {-60, -85, 3000},
            {-75, 40.7, -100}, {77.2, 28.6, 200}, {20, 85, 10}};
    static final int ANY = 0;
    static final int[] TYPES = {ANY, SE_ECL_TOTAL, SE_ECL_ANNULAR, SE_ECL_PARTIAL, SE_ECL_ANNULAR_TOTAL,
            SE_ECL_TOTAL | SE_ECL_CENTRAL, SE_ECL_ANNULAR | SE_ECL_CENTRAL, SE_ECL_ANNULAR_TOTAL | SE_ECL_CENTRAL,
            SE_ECL_PARTIAL | SE_ECL_NONCENTRAL, SE_ECL_PARTIAL | SE_ECL_CENTRAL, SE_ECL_ANNULAR_TOTAL | SE_ECL_NONCENTRAL,
            SE_ECL_TOTAL | SE_ECL_ANNULAR | SE_ECL_CENTRAL, SE_ECL_TOTAL | SE_ECL_PARTIAL | SE_ECL_CENTRAL | SE_ECL_NONCENTRAL};
    static final int[] DIRECTIONS = {0, 1};
    static final int[] ONE_TRY = {SE_ECL_ONE_TRY, SE_ECL_ONE_TRY | 1};
    /** metres; SEI_ECL_GEOALT_MIN and SEI_ECL_GEOALT_MAX of sweph.h */
    static final double LOWEST = -500, HIGHEST = 25000;
    static final double UNTOUCHED = 7;
    /** days; measured 1.4e-8 and 4e-7; native itself moves by as much */
    static final double TOL_T = 5e-8, TOL_T_MOSHIER = 1e-6;
    /** of the value or of 1; measured 6.7e-7; kilometres of shadow, azimuth and altitude are those of the time */
    static final double TOL_ATTR = 2e-6, TOL_AZALT_MOSHIER = 3e-4;
    static final int CORE_SHADOW = 3, APP_ALTITUDE = 6;
    static final double TOL_GEO = 1e-8;

    @BeforeEach
    void theDefaultLapseRate() {
        getSwephExp().swe_set_lapse_rate(0.0065);
        getSwissEph().swe_set_lapse_rate(0.0065);
    }

    @FunctionalInterface
    private interface Call {
        int on(ISwissEph engine, StringBuilder star, double[] geo, double[] tret, double[] attr, StringBuilder serr);
    }

    private static final class Result {
        int ret;
        double[] geo, tret, attr;
        String serr, star;

        boolean is(int flag) {
            return ret > 0 && 0 != (ret & flag);
        }
    }

    /** one call on both engines; returns what native gives */
    private Result compare(String what, int eph, String star, double[] geo, boolean withSerr, Call call) {
        final ISwissEph[] engines = {getSwephExp(), getSwissEph()};
        final Result[] r = {new Result(), new Result()};
        for (int i = 0; i < 2; i++) {
            final StringBuilder name = null == star ? null : new StringBuilder(star), err = withSerr ? new StringBuilder() : null;
            r[i].geo = geo.clone();
            r[i].tret = new double[10];
            r[i].attr = new double[20];
            Arrays.fill(r[i].tret, UNTOUCHED);
            Arrays.fill(r[i].attr, UNTOUCHED);
            r[i].ret = call.on(engines[i], name, r[i].geo, r[i].tret, r[i].attr, err);
            r[i].serr = String.valueOf(err);
            r[i].star = String.valueOf(name);
        }
        final String ctx = what + " eph " + eph + " geo " + Arrays.toString(geo) + " star " + star;
        assertEquals(r[0].ret, r[1].ret, () -> ctx + " return; native '" + r[0].serr + "' Java '" + r[1].serr + "'");
        assertEquals(r[0].serr, r[1].serr, ctx + " serr");
        assertEquals(r[0].star, r[1].star, ctx + " star name");
        // the files here begin after -3000, and C takes Moshier's ephemeris without a word
        final boolean moshier = SEFLG_MOSEPH == eph || UNTOUCHED != r[0].tret[0]
                && 0 != (SEFLG_MOSEPH & getSwephExp().swe_calc_ut(r[0].tret[0], SE_MOON, eph, new double[6], null));
        for (int i = 0; i < geo.length; i++) {
            assertEquals(r[0].geo[i], r[1].geo[i], TOL_GEO, ctx + " geopos " + i);
        }
        for (int i = 0; i < 10; i++) {
            assertEquals(r[0].tret[i], r[1].tret[i], moshier ? TOL_T_MOSHIER : TOL_T, ctx + " tret " + i);
        }
        for (int i = 0; i < 20; i++) {
            final boolean azalt = moshier && CORE_SHADOW <= i && i <= APP_ALTITUDE;
            final double big = Math.max(1, Math.abs(r[0].attr[i]));
            assertEquals(r[0].attr[i], r[1].attr[i], big * (azalt ? TOL_AZALT_MOSHIER : TOL_ATTR), ctx + " attr " + i);
        }
        return r[0];
    }

    private Result solGlob(double t, int eph, int type, int backward) {
        return compare("sol when_glob " + t + " type " + type + " back " + backward, eph, null, new double[0], true,
                (e, n, g, r, a, s) -> e.swe_sol_eclipse_when_glob(t, eph, type, r, backward, s));
    }

    private Result solWhere(double t, int eph) {
        return compare("sol where " + t, eph, null, new double[]{UNTOUCHED, UNTOUCHED}, true,
                (e, n, g, r, a, s) -> e.swe_sol_eclipse_where(t, eph, g, a, s));
    }

    private Result solHow(double t, int eph, double[] place) {
        return compare("sol how " + t, eph, null, place, true, (e, n, g, r, a, s) -> e.swe_sol_eclipse_how(t, eph, g, a, s));
    }

    private Result solLoc(double t, int eph, double[] place, int backward) {
        return compare("sol when_loc " + t + " back " + backward, eph, null, place, true,
                (e, n, g, r, a, s) -> e.swe_sol_eclipse_when_loc(t, eph, g, r, a, backward, s));
    }

    private Result occGlob(double t, int ipl, String star, int eph, int type, int backward) {
        return compare("occ when_glob " + t + " ipl " + ipl + " type " + type + " back " + backward, eph, star, new double[0], true,
                (e, n, g, r, a, s) -> e.swe_lun_occult_when_glob(t, ipl, n, eph, type, r, backward, s));
    }

    private Result occWhere(double t, int ipl, String star, int eph) {
        return compare("occ where " + t + " ipl " + ipl, eph, star, new double[]{UNTOUCHED, UNTOUCHED}, true,
                (e, n, g, r, a, s) -> e.swe_lun_occult_where(t, ipl, n, eph, g, a, s));
    }

    private Result occLoc(double t, int ipl, String star, int eph, double[] place, int backward) {
        return compare("occ when_loc " + t + " ipl " + ipl + " back " + backward, eph, star, place, true,
                (e, n, g, r, a, s) -> e.swe_lun_occult_when_loc(t, ipl, n, eph, g, r, a, backward, s));
    }

    // solar eclipses

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

    @Test
    void theLocalSearch_matchesNative() {
        int rise = 0, partlyVisible = 0, all = 0, central = 0;
        final Set<Integer> kinds = new TreeSet<>();
        for (double d : DATES) {
            for (int eph : EPHEMERIDES) {
                for (double[] p : PLACES) {
                    for (int k = 0; k < 4; k++) {
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
        assertTrue(all > 1000 && rise > 60 && partlyVisible > 60 && central > 0 && kinds.size() > 8,
                all + " " + rise + " " + partlyVisible + " " + central + " " + kinds);
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

    /** the Saros number of an eclipse at the edge of its series, one which comes two days early included */
    @Test
    void theSaros_matchesNative() {
        int n = 0, none = 0;
        for (double t = 990000; t < 2800000; n++) {
            final Result g = solGlob(t, SEFLG_MOSEPH, ANY, 0);
            final double[] w = solWhere(g.tret[0], SEFLG_MOSEPH).geo;
            if (solHow(g.tret[0], SEFLG_MOSEPH, new double[]{w[0], w[1], 0}).attr[9] < -100) none++;
            t = g.tret[0] + 1500;
        }
        assertTrue(n > 1100, n + " eclipses, " + none + " without a series");
    }

    // occultations

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
            for (double d : new double[]{2451545.3, 2455197.3, 2086400.4}) {
                for (int eph : EPHEMERIDES) {
                    for (int back : DIRECTIONS) {
                        for (int type : new int[]{ANY, SE_ECL_TOTAL, SE_ECL_TOTAL | SE_ECL_CENTRAL}) {
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
        assertTrue(n > 400 && kinds.contains(SE_ECL_TOTAL | SE_ECL_CENTRAL) && kinds.contains(SE_ECL_TOTAL | SE_ECL_NONCENTRAL), n + " " + kinds);
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
                            for (int k = 0; k < 3; k++) {
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
        assertTrue(hit > 150 && missed > 4000 && day > 40, hit + " " + missed + " " + day);
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
            for (int k = 0; k < 6; k++) {
                t = occGlob(t, ipl, star, SEFLG_SWIEPH, ANY, 0).tret[0];
                final double[] w = occWhere(t, ipl, star, SEFLG_SWIEPH).geo;
                for (int lat = -84; lat <= 84; lat += 12) {
                    for (int lon = -120; lon <= 120; lon += 40) {
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
        assertTrue(all > 3000 && hit > 400 && beg > 200 && end > 200 && rise > 40 && grazing > 0, all + " " + hit + " " + beg + " " + end + " " + rise + " " + grazing);
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

    // both

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
