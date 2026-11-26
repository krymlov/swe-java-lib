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
 * What the {@code Eclipse*} classes share: the cases, the tolerances and the helpers. The
 * tests are split into classes of 20 - 30 seconds so that surefire runs them in parallel forks.
 * See {@link EclipseParityTest} for what is tested.
 */
@Isolated
abstract class EclipseParityTestBase extends AbstractTest {

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
    interface Call {
        int on(ISwissEph engine, StringBuilder star, double[] geo, double[] tret, double[] attr, StringBuilder serr);
    }

    static final class Result {
        int ret;
        double[] geo, tret, attr;
        String serr, star;

        boolean is(int flag) {
            return ret > 0 && 0 != (ret & flag);
        }
    }

    /** one call on both engines; returns what native gives */
    Result compare(String what, int eph, String star, double[] geo, boolean withSerr, Call call) {
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
            if (isWild(r[0].tret, i) && isWild(r[1].tret, i)) continue;
            assertEquals(r[0].tret[i], r[1].tret[i], moshier ? TOL_T_MOSHIER : TOL_T, ctx + " tret " + i);
        }
        for (int i = 0; i < 20; i++) {
            final boolean azalt = moshier && CORE_SHADOW <= i && i <= APP_ALTITUDE;
            final double big = Math.max(1, Math.abs(r[0].attr[i]));
            assertEquals(r[0].attr[i], r[1].attr[i], big * (azalt ? TOL_AZALT_MOSHIER : TOL_ATTR), ctx + " attr " + i);
        }
        return r[0];
    }

    /**
     * C's search for a contact of a grazing occultation may run away for weeks, to where a place moved by
     * 1e-9 of a degree moves native by half an hour
     */
    static boolean isWild(double[] tret, int i) {
        return 1 <= i && i <= 4 && tret[i] > 100000 && Math.abs(tret[i] - tret[0]) > 1;
    }

    Result solGlob(double t, int eph, int type, int backward) {
        return compare("sol when_glob " + t + " type " + type + " back " + backward, eph, null, new double[0], true,
                (e, n, g, r, a, s) -> e.swe_sol_eclipse_when_glob(t, eph, type, r, backward, s));
    }

    Result solWhere(double t, int eph) {
        return compare("sol where " + t, eph, null, new double[]{UNTOUCHED, UNTOUCHED}, true,
                (e, n, g, r, a, s) -> e.swe_sol_eclipse_where(t, eph, g, a, s));
    }

    Result solHow(double t, int eph, double[] place) {
        return compare("sol how " + t, eph, null, place, true, (e, n, g, r, a, s) -> e.swe_sol_eclipse_how(t, eph, g, a, s));
    }

    Result solLoc(double t, int eph, double[] place, int backward) {
        return compare("sol when_loc " + t + " back " + backward, eph, null, place, true,
                (e, n, g, r, a, s) -> e.swe_sol_eclipse_when_loc(t, eph, g, r, a, backward, s));
    }

    Result occGlob(double t, int ipl, String star, int eph, int type, int backward) {
        return compare("occ when_glob " + t + " ipl " + ipl + " type " + type + " back " + backward, eph, star, new double[0], true,
                (e, n, g, r, a, s) -> e.swe_lun_occult_when_glob(t, ipl, n, eph, type, r, backward, s));
    }

    Result occWhere(double t, int ipl, String star, int eph) {
        return compare("occ where " + t + " ipl " + ipl, eph, star, new double[]{UNTOUCHED, UNTOUCHED}, true,
                (e, n, g, r, a, s) -> e.swe_lun_occult_where(t, ipl, n, eph, g, a, s));
    }

    Result occLoc(double t, int ipl, String star, int eph, double[] place, int backward) {
        return compare("occ when_loc " + t + " ipl " + ipl + " back " + backward, eph, star, place, true,
                (e, n, g, r, a, s) -> e.swe_lun_occult_when_loc(t, ipl, n, eph, g, r, a, backward, s));
    }

    // solar eclipses

    Result theSarosOfTheNext(double t) {
        final Result g = solGlob(t, SEFLG_MOSEPH, ANY, 0);
        final double[] w = solWhere(g.tret[0], SEFLG_MOSEPH).geo;
        solHow(g.tret[0], SEFLG_MOSEPH, new double[]{w[0], w[1], 0});
        return g;
    }

    // occultations

    // both
}
