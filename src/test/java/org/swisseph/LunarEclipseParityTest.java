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
 * Lunar eclipses, pure Java against native: {@code swe_lun_eclipse_when(), _how(), _when_loc()} - return
 * value, {@code serr} and every element of the arrays, those which C leaves alone included.
 * <p>
 * Until 2026-11-24 the Java engine did not know the polar Moon which neither rises nor sets
 * (-2 of {@code swe_rise_trans()}) and took an eclipse years away; it looked for an annular lunar eclipse to the
 * end of the ephemeris; it began with too short a step between the years 763 and 1037 (2000000, 2100000), and gave no
 * Saros number to an eclipse shortly before the first date of its series.
 * <p>
 * Measured: 4.7e-10 of a day with the files, 1.5e-9 in Moshier's ephemeris; attributes 2.2e-7.
 * <p>
 * {@code @Isolated}: the tidal acceleration and the lapse rate are JVM-global in the Java engine.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
@Isolated
public class LunarEclipseParityTest extends AbstractTest {

    static final int[] EPHEMERIDES = {SEFLG_SWIEPH, SEFLG_MOSEPH};
    /** -3000 to 2160; the third is between the two limits of the first step */
    static final double[] DATES = {2451545.3, 2415100.1, 2050000.5, 1757700.7, 626000.3, 2510000.5};
    static final double[][] PLACES = {{30.5, 50.45, 100}, {-70, -33, 2400}, {140, 68, 0}, {20, 85, 10}, {-60, -85, 3000}};
    static final int ANY = 0;
    static final int[] TYPES = {ANY, SE_ECL_TOTAL, SE_ECL_PARTIAL, SE_ECL_PENUMBRAL, SE_ECL_TOTAL | SE_ECL_PARTIAL,
            SE_ECL_ANNULAR, SE_ECL_ANNULAR_TOTAL, SE_ECL_ANNULAR | SE_ECL_TOTAL, SE_ECL_TOTAL | SE_ECL_CENTRAL,
            SE_ECL_PARTIAL | SE_ECL_NONCENTRAL, SE_ECL_CENTRAL, SE_ECL_ANNULAR | SE_ECL_NONCENTRAL};
    static final int[] DIRECTIONS = {0, 1};
    /** metres; SEI_ECL_GEOALT_MIN and SEI_ECL_GEOALT_MAX of sweph.h */
    static final double LOWEST = -500, HIGHEST = 25000;
    static final double UNTOUCHED = 7;
    static final double TOL_T = 1e-8, TOL_ATTR = 1e-6;
    static final int SAROS = 9;

    @BeforeEach
    void theDefaultLapseRate() {
        getSwephExp().swe_set_lapse_rate(0.0065);
        getSwissEph().swe_set_lapse_rate(0.0065);
    }

    @FunctionalInterface
    private interface Call {
        int on(ISwissEph engine, double[] geo, double[] tret, double[] attr, StringBuilder serr);
    }

    private static final class Result {
        int ret;
        double[] tret, attr;
        String serr;
    }

    /** one call on both engines; returns what native gives */
    private Result compare(String what, double[] geo, boolean withSerr, Call call) {
        final ISwissEph[] engines = {getSwephExp(), getSwissEph()};
        final Result[] r = {new Result(), new Result()};
        for (int i = 0; i < 2; i++) {
            final StringBuilder err = withSerr ? new StringBuilder() : null;
            r[i].tret = new double[10];
            r[i].attr = new double[20];
            Arrays.fill(r[i].tret, UNTOUCHED);
            Arrays.fill(r[i].attr, UNTOUCHED);
            r[i].ret = call.on(engines[i], null == geo ? null : geo.clone(), r[i].tret, r[i].attr, err);
            r[i].serr = String.valueOf(err);
        }
        final String ctx = what + " geo " + Arrays.toString(geo);
        assertEquals(r[0].ret, r[1].ret, () -> ctx + " return; native '" + r[0].serr + "' Java '" + r[1].serr + "'");
        assertEquals(r[0].serr, r[1].serr, ctx + " serr");
        assertArrayEquals(r[0].tret, r[1].tret, TOL_T, ctx + " tret");
        assertArrayEquals(r[0].attr, r[1].attr, TOL_ATTR, ctx + " attr");
        return r[0];
    }

    private Result when(double t, int ifl, int type, int backward) {
        return compare("when " + t + " ifl " + ifl + " type " + type + " back " + backward, null, true,
                (e, g, r, a, s) -> e.swe_lun_eclipse_when(t, ifl, type, r, backward, s));
    }

    private Result how(double t, int ifl, double[] place) {
        return compare("how " + t + " ifl " + ifl, place, true, (e, g, r, a, s) -> e.swe_lun_eclipse_how(t, ifl, g, a, s));
    }

    private Result whenLoc(double t, int ifl, double[] place, int backward) {
        return compare("when_loc " + t + " ifl " + ifl + " back " + backward, place, true,
                (e, g, r, a, s) -> e.swe_lun_eclipse_when_loc(t, ifl, g, r, a, backward, s));
    }

    /** every type; how the eclipse looks, near its maximum, outside of it, and below the horizon */
    @Test
    void theSearch_andTheLook_matchNative() {
        final Set<Integer> kinds = new TreeSet<>();
        int none = 0, below = 0;
        for (double d : DATES) {
            for (int eph : EPHEMERIDES) {
                for (int type : TYPES) {
                    for (int back : DIRECTIONS) {
                        final Result g = when(d, eph, type, back);
                        kinds.add(g.ret);
                        if (ERR == g.ret) continue;
                        for (double off : new double[]{0, -0.06, 0.1, 3}) {
                            final Result w = how(g.tret[0] + off, eph, null);
                            if (0 == w.ret) none++;
                            for (double[] p : PLACES) if (0 == how(g.tret[0] + off, eph, p).ret && 0 != w.ret) below++;
                        }
                    }
                }
            }
        }
        assertEquals(new TreeSet<>(Arrays.asList(ERR, SE_ECL_TOTAL, SE_ECL_PARTIAL, SE_ECL_PENUMBRAL)), kinds);
        assertTrue(none > 200 && below > 300, none + " " + below);
    }

    /** an annular one alone is refused; it and the solar kinds are forgotten in a combination */
    @Test
    void theAnnularOnes_areRefused_asInNative() {
        for (int type : new int[]{SE_ECL_ANNULAR, SE_ECL_ANNULAR_TOTAL, SE_ECL_ANNULAR | SE_ECL_ANNULAR_TOTAL, SE_ECL_ANNULAR | SE_ECL_CENTRAL}) {
            final Result r = when(2451545.3, SEFLG_SWIEPH, type, 0);
            assertEquals(ERR, r.ret);
            assertEquals("annular lunar eclipses don't exist", r.serr);
            assertEquals(UNTOUCHED, r.tret[0]);
        }
        assertEquals(when(2451545.3, SEFLG_SWIEPH, ANY, 0).tret[0], when(2451545.3, SEFLG_SWIEPH, SE_ECL_CENTRAL, 0).tret[0]);
    }

    /** a place where the Moon rises or sets during the eclipse, and one where it does neither for days */
    @Test
    void theLocalSearch_matchesNative() {
        int rises = 0, sets = 0, polar = 0;
        for (double d : DATES) {
            for (int eph : EPHEMERIDES) {
                for (double[] p : PLACES) {
                    for (int back : DIRECTIONS) {
                        for (int k = 0; k < 3; k++) {
                            final Result r = whenLoc(d + k * 397.1, eph, p, back);
                            if (r.tret[8] > 0) rises++;
                            if (r.tret[9] > 0) sets++;
                            if (Math.abs(p[1]) > 80 && 0 == r.tret[8] && 0 == r.tret[9]) polar++;
                        }
                    }
                }
            }
        }
        assertTrue(rises > 30 && sets > 30 && polar > 50, rises + " " + sets + " " + polar);
    }

    /** the contacts and the Saros numbers of thirty years of eclipses in turn */
    @Test
    void thirtyYearsInTurn_matchNative() {
        int n = 0;
        for (double t = 2451545; t < 2462502; n++) {
            t = when(t, SEFLG_SWIEPH, ANY, 0).tret[0];
            assertTrue(how(t, SEFLG_SWIEPH, null).attr[SAROS] > 0);
        }
        assertTrue(n > 60, n + " eclipses");
    }

    /** the first of a series comes before the date of the table, and C allows for two days */
    @Test
    void theSaros_beforeTheTable_matchesNative() {
        for (double t : EARLY) {
            final Result g = when(t - 5, SEFLG_MOSEPH, ANY, 0);
            assertEquals(t, g.tret[0], 0.02);
            assertTrue(how(g.tret[0], SEFLG_MOSEPH, null).attr[SAROS] > 0, "Saros at " + t);
        }
    }

    static final double[] EARLY = {782437.10, 783824.36, 762857.18, 855229.38, 958173.21, 976895.44, 1058695.43, 1131311.18, 1186946.41,
            1276719.50, 1375676.46, 1503928.39, 1579142.46, 1942575.46, 2083820.49, 2142054.49};

    /** heights, flags which C removes or not, no serr, dates outside the ephemeris */
    @Test
    void theRefusals_theFlags_andTheEnd_matchNative() {
        final double[] kyiv = {30.5, 50.45, 100};
        for (double h : new double[]{LOWEST - 0.1, HIGHEST + 0.1, 1e6, -1e6}) {
            for (Result r : new Result[]{how(2451564.7, SEFLG_SWIEPH, new double[]{30.5, 50.45, h}),
                    whenLoc(2451545.3, SEFLG_SWIEPH, new double[]{30.5, 50.45, h}, 0)}) {
                assertEquals(ERR, r.ret);
                assertTrue(r.serr.startsWith("location for eclipses"), r.serr);
            }
        }
        for (double h : new double[]{LOWEST, HIGHEST}) {
            assertNotEquals(ERR, whenLoc(2451545.3, SEFLG_SWIEPH, new double[]{30.5, 50.45, h}, 0).ret);
        }
        for (int extra : new int[]{SEFLG_SPEED, SEFLG_NONUT, SEFLG_TRUEPOS, SEFLG_TOPOCTR, SEFLG_XYZ, SEFLG_RADIANS, SEFLG_JPLHOR, SEFLG_JPLHOR_APPROX}) {
            for (int eph : EPHEMERIDES) {
                final int f = eph | extra;
                when(2451545.3, f, ANY, 0);
                how(2451564.7, f, kyiv);
                whenLoc(2451545.3, f, kyiv, 1);
                compare("no serr " + f, kyiv, false, (e, g, r, a, s) -> e.swe_lun_eclipse_when_loc(2451545.3, f, g, r, a, 0, s));
                compare("no serr " + f, null, false, (e, g, r, a, s) -> e.swe_lun_eclipse_when(2451545.3, f, SE_ECL_ANNULAR, r, 0, s));
                compare("no serr " + f, kyiv, false, (e, g, r, a, s) -> e.swe_lun_eclipse_how(2451600.7, f, g, a, s));
            }
        }
        for (double d : new double[]{-3100000.5, 8100000.5}) {
            for (int eph : EPHEMERIDES) {
                when(d, eph, ANY, 0);
                how(d, eph, kyiv);
                whenLoc(d, eph, kyiv, 0);
            }
        }
    }
}
