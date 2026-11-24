package org.swisseph;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import static org.junit.jupiter.api.Assertions.*;
import static swisseph.SweConst.*;

/**
 * {@code swe_nod_aps(_ut)}, pure Java against native, and two names.
 * <p>
 * Until 2026-11-24 the Java engine left the Moon's speed out of the Earth's (osculating apsides of the Sun 3.2 degrees off), had
 * older planet masses (Neptune 2.5e-3 degrees), and gave other texts. What is left is the noise of osculating
 * elements: with Moshier native itself moves by 1e-2 degrees within 1e-9 day.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
@Isolated
public class NodApsParityTest extends AbstractTest {

    static final double[] DATES = {2451545.3, 2415020.5, 2460000.1, 1757700.7};
    static final int[] METHODS = {0, SE_NODBIT_MEAN, SE_NODBIT_OSCU, SE_NODBIT_OSCU_BAR, SE_NODBIT_FOPOINT | SE_NODBIT_MEAN, SE_NODBIT_FOPOINT | SE_NODBIT_OSCU};
    static final int[] FLAGS = {SEFLG_SPEED, 0, SEFLG_SPEED | SEFLG_EQUATORIAL, SEFLG_HELCTR, SEFLG_SPEED | SEFLG_SIDEREAL, SEFLG_J2000 | SEFLG_TRUEPOS};

    private void compare(double jd, int body, int flag, int method, double tolPos, double tolSpeed) {
        final String ctx = jd + " body " + body + " flag " + flag + " method " + method;
        for (int ut = 0; ut < 2; ut++) {
            final ISwissEph[] engines = {getSwephExp(), getSwissEph()};
            final double[][][] x = new double[2][4][6];
            final int[] ret = new int[2];
            final String[] serr = new String[2];
            for (int i = 0; i < 2; i++) {
                final StringBuilder err = new StringBuilder();
                ret[i] = ut == 1 ? engines[i].swe_nod_aps_ut(jd, body, flag, method, x[i][0], x[i][1], x[i][2], x[i][3], err)
                        : engines[i].swe_nod_aps(jd, body, flag, method, x[i][0], x[i][1], x[i][2], x[i][3], err);
                serr[i] = err.toString();
            }
            assertEquals(serr[0], serr[1], ctx);
            assertEquals(ret[0], ret[1], ctx);
            for (int p = 0; p < 4; p++) for (int k = 0; k < 6; k++) {
                double d = Math.abs(x[0][p][k] - x[1][p][k]);
                if (k == 0) d = Math.min(d, 360 - d);
                assertTrue(d <= (k < 3 ? tolPos : tolSpeed), ctx + " point " + p + " x[" + k + "] " + x[0][p][k] + " " + x[1][p][k]);
            }
        }
    }

    @Test
    void swissEphemeris() {
        for (double jd : DATES) for (int body = SE_SUN; body <= SE_EARTH; body++) for (int method : METHODS) for (int flag : FLAGS)
            compare(jd, body, SEFLG_SWIEPH | flag, method, 1e-8, 2e-6);
    }

    @Test
    void moshier() {
        for (double jd : DATES) for (int body : new int[]{SE_SUN, SE_MOON, SE_JUPITER, SE_NEPTUNE, SE_EARTH})
            for (int method : METHODS) compare(jd, body, SEFLG_MOSEPH | SEFLG_SPEED, method, 2e-4, 2e-3);
    }

    /** the text has "%5.0f"; nothing is computed */
    @Test
    void refusedBodies() {
        for (int body : new int[]{-5, SE_MEAN_NODE, SE_TRUE_NODE, SE_MEAN_APOG, SE_OSCU_APOG, 24, SE_FICT_OFFSET, 9401})
            compare(2451545, body, SEFLG_SWIEPH, SE_NODBIT_MEAN, 0, 0);
    }

    @Test
    void names() {
        for (int body : new int[]{SE_AST_OFFSET + 1, SE_AST_OFFSET + 433, SE_AST_OFFSET + 99999, 9401, 9599, 24, SE_FICT_OFFSET + 3})
            assertEquals(getSwephExp().swe_get_planet_name(body), getSwissEph().swe_get_planet_name(body));
        for (int mode = 0; mode < SE_NSIDM_PREDEF + 5; mode++)
            assertEquals(getSwephExp().swe_get_ayanamsa_name(mode), getSwissEph().swe_get_ayanamsa_name(mode));
        assertNull(getSwissEph().swe_get_ayanamsa_name(-1)); // C reads before its table
    }
}
