package org.swisseph;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import static org.junit.jupiter.api.Assertions.*;
import static swisseph.SweConst.*;

/**
 * {@code swe_solcross}, {@code swe_mooncross}, {@code swe_mooncross_node}, {@code swe_helio_cross} (all with {@code _ut}), pure Java
 * against native: date, position, return value and {@code serr}.
 * <p>
 * Until 2026-11-24 the Java engine used its transit search: up to 0.011 s off, only the ascending node, other texts.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
@Isolated
public class CrossingParityTest extends AbstractTest {

    static final double[] DATES = {2451545.3, 2415020.5, 2460000.1, 1757700.7};
    static final double[] LONGITUDES = {0, 123.4, 270};
    static final int[] FLAGS = {SEFLG_SWIEPH, SEFLG_MOSEPH, SEFLG_SWIEPH | SEFLG_SIDEREAL, SEFLG_SWIEPH | SEFLG_TRUEPOS | SEFLG_NONUT,
            SEFLG_SWIEPH | SEFLG_EQUATORIAL, SEFLG_SWIEPH | SEFLG_J2000};
    /** 4 ulps of a date: 0.0002 s */
    static final double TOL = 2e-9;
    /** a planet moves down to 0.004 degrees a day: 1e-10 degrees are 2.5e-8 days; more where native falls back to Moshier */
    static final double TOL_HELIO = 5e-8;

    interface Call {
        double on(ISwissEph eph, StringBuilder serr);
    }

    private double compare(String ctx, Call call) {
        return compare(ctx, TOL, call);
    }

    private double compare(String ctx, double tol, Call call) {
        final StringBuilder sn = new StringBuilder(), sj = new StringBuilder();
        final double n = call.on(getSwephExp(), sn), j = call.on(getSwissEph(), sj);
        assertEquals(sn.toString(), sj.toString(), ctx);
        assertEquals(n, j, tol, ctx);
        return n;
    }

    @Test
    void sunAndMoon() {
        for (double jd : DATES) for (double lon : LONGITUDES) for (int flag : FLAGS) {
            final String ctx = jd + " " + lon + " " + flag;
            assertTrue(compare(ctx, (e, s) -> e.swe_solcross(lon, jd, flag, s)) > jd, ctx);
            assertTrue(compare(ctx, (e, s) -> e.swe_solcross_ut(lon, jd, flag, s)) > jd, ctx);
            assertTrue(compare(ctx, (e, s) -> e.swe_mooncross(lon, jd, flag, s)) > jd, ctx);
            assertTrue(compare(ctx, (e, s) -> e.swe_mooncross_ut(lon, jd, flag, s)) > jd, ctx);
        }
    }

    /** both nodes: a month of start dates, so the next crossing is now the ascending, now the descending one */
    @Test
    void bothNodesOfTheMoon() {
        int descending = 0, ascending = 0;
        for (double jd0 : DATES) for (int k = 0; k < 5; k++) for (int flag : new int[]{SEFLG_SWIEPH, SEFLG_MOSEPH}) for (int ut = 0; ut < 2; ut++) {
            final double jd = jd0 + k * 6.5;
            final boolean isUt = ut == 1;
            final double[][] lon = new double[2][1], lat = new double[2][1];
            final double cross = compare(jd + " " + flag + " " + ut, (e, s) -> {
                final int i = e == getSwissEph() ? 1 : 0;
                return isUt ? e.swe_mooncross_node_ut(jd, flag, lon[i], lat[i], s) : e.swe_mooncross_node(jd, flag, lon[i], lat[i], s);
            });
            assertTrue(cross > jd && cross < jd + 15, "half a month: " + (cross - jd));
            assertEquals(lon[0][0], lon[1][0], 1e-8);
            assertEquals(lat[0][0], lat[1][0], 1e-8);
            final double[] x = new double[6];
            getSwissEph().swe_calc(cross, SE_MOON, flag | SEFLG_SPEED, x, new StringBuilder());
            if (x[4] < 0) descending++; else ascending++;
        }
        assertTrue(descending > 5 && ascending > 5, descending + " / " + ascending);
    }

    @Test
    void heliocentric_bothDirections() {
        for (double jd : DATES) for (double lon : LONGITUDES)
            for (int body : new int[]{SE_MERCURY, SE_MARS, SE_JUPITER, SE_SATURN, SE_PLUTO, SE_EARTH, SE_CHIRON, SE_FICT_OFFSET})
                for (int dir : new int[]{1, 0, -1}) for (int ut = 0; ut < 2; ut++) {
                    final boolean isUt = ut == 1;
                    final double[][] r = new double[2][1];
                    final int[] ret = new int[2];
                    final String ctx = jd + " " + lon + " " + body + " " + dir + " " + ut;
                    compare(ctx, jd < 2e6 ? 1e-6 : TOL_HELIO, (e, s) -> {
                        final int i = e == getSwissEph() ? 1 : 0;
                        ret[i] = isUt ? e.swe_helio_cross_ut(body, lon, jd, SEFLG_SWIEPH, dir, r[i], s)
                                : e.swe_helio_cross(body, lon, jd, SEFLG_SWIEPH, dir, r[i], s);
                        return r[i][0];
                    });
                    assertEquals(ret[0], ret[1], ctx);
                }
    }

    @Test
    void refusedBodiesAndErrors() {
        for (int body : new int[]{SE_SUN, SE_MOON, SE_MEAN_NODE, SE_TRUE_NODE, SE_MEAN_APOG, SE_OSCU_APOG, SE_INTP_APOG, SE_INTP_PERG, -2, 24}) {
            final double[][] r = {{7}, {7}};
            final StringBuilder sn = new StringBuilder(), sj = new StringBuilder();
            assertEquals(ERR, getSwephExp().swe_helio_cross(body, 10, 2451545, SEFLG_SWIEPH, 1, r[0], sn));
            assertEquals(ERR, getSwissEph().swe_helio_cross(body, 10, 2451545, SEFLG_SWIEPH, 1, r[1], sj));
            assertEquals(sn.toString(), sj.toString());
            assertFalse(sj.length() == 0);
            assertEquals(7, r[1][0]);
        }
        // a date outside every ephemeris: the date before the start, with the text of swe_calc()
        assertEquals(1e8 - 1, compare("1e8", (e, s) -> e.swe_solcross(10, 1e8, SEFLG_SWIEPH, s)));
        assertEquals(1e8 - 1, compare("1e8", (e, s) -> e.swe_mooncross_node(1e8, SEFLG_SWIEPH, new double[1], new double[1], s)));
    }

    /** C never returns from a crossing of a body without speed, so Java alone is asked: it says so and comes back */
    @Test
    void theHeliocentricSun_isRefusedByJava() {
        final StringBuilder serr = new StringBuilder();
        assertEquals(2451544., getSwissEph().swe_solcross(10, 2451545, SEFLG_SWIEPH | SEFLG_HELCTR, serr));
        assertEquals("no crossing: object 0 has no speed", serr.toString());
    }
}
