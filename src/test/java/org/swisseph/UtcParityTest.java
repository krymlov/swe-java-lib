package org.swisseph;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.swisseph.api.ISweJulianDate;
import swisseph.SweDate;
import swisseph.SwissephException;

import static org.junit.jupiter.api.Assertions.*;
import static swisseph.SweConst.*;

/**
 * {@code swe_utc_to_jd}, {@code swe_jdet_to_utc}, {@code swe_jdut1_to_utc}, pure Java against native, in both calendars and around
 * the leap seconds.
 * <p>
 * Until 2026-11-24 the Java engine gave no Julian calendar date, one second too much for a day after a leap second, and
 * accepted an impossible date or time.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
@Isolated
public class UtcParityTest extends AbstractTest {

    /** UTC days: with a leap second at the end, without, before 1972, after the table */
    static final int[][] DAYS = {{1972, 6, 30}, {1990, 12, 31}, {2012, 6, 30}, {2016, 12, 31}, {2017, 12, 31}, {1971, 12, 31}, {1500, 3, 1}, {2050, 12, 31}};
    /** days after noon of such a day, through the leap second */
    static final double[] AFTER = {-2, -.5, 0, .5 - 3e-5, .5 - 1.2e-5, .5 - 6e-6, .5, .5 + 6e-6, .5 + 1.2e-5, 1, 1.4, 1.5, 2, 30};

    @Test
    void toUtc() {
        for (int calendar : new int[]{SE_GREG_CAL, SE_JUL_CAL}) for (int[] day : DAYS) {
            final double[] noon = new double[2];
            getSwephExp().swe_utc_to_jd(day[0], day[1], day[2], 12, 0, 0, SE_GREG_CAL, noon, new StringBuilder());
            for (double t : AFTER) for (int ut = 0; ut < 2; ut++) {
                final double jd = noon[ut] + t;
                final String ctx = jd + " calendar " + calendar + " " + ut;
                final int[] n = new int[5], j = new int[5];
                final double[] sn = new double[1], sj = new double[1];
                final ISweJulianDate dn, dj;
                if (ut == 0) {
                    getSwephExp().swe_jdet_to_utc(jd, calendar, n, sn);
                    getSwissEph().swe_jdet_to_utc(jd, calendar, j, sj);
                    dn = getSwephExp().swe_jdet_to_utc(jd, calendar);
                    dj = getSwissEph().swe_jdet_to_utc(jd, calendar);
                } else {
                    getSwephExp().swe_jdut1_to_utc(jd, calendar, n, sn);
                    getSwissEph().swe_jdut1_to_utc(jd, calendar, j, sj);
                    dn = getSwephExp().swe_jdut1_to_utc(jd, calendar);
                    dj = getSwissEph().swe_jdut1_to_utc(jd, calendar);
                }
                assertArrayEquals(n, j, ctx);
                assertEquals(sn[0], sj[0], 1e-9, ctx);
                assertArrayEquals(dn.date(), dj.date(), ctx);
                assertEquals(dn.localTime(), dj.localTime(), 1e-12, ctx);
            }
        }
    }

    @Test
    void toJulianDay_validOrNot() {
        final double[][] times = {{0, 0, 0}, {12, 30, 15.25}, {23, 59, 59.9}, {23, 59, 60}, {23, 59, 60.125}, {23, 59, 61}, {23, 58, 60}, {24, 0, 0}, {-1, 0, 0}, {3, 60, 0}, {3, 5, -.125}};
        int refused = 0;
        for (int calendar : new int[]{SE_GREG_CAL, SE_JUL_CAL}) for (int[] day : DAYS) for (int shift : new int[]{-13, -1, 0, 1}) for (double[] t : times) {
            final int d = day[2] + shift;
            final String ctx = day[0] + "-" + day[1] + "-" + d + " " + t[0] + ":" + t[1] + ":" + t[2] + " calendar " + calendar;
            final double[] n = {7, 7}, j = {7, 7};
            final StringBuilder sn = new StringBuilder(), sj = new StringBuilder();
            final int rn = getSwephExp().swe_utc_to_jd(day[0], day[1], d, (int) t[0], (int) t[1], t[2], calendar, n, sn);
            final int rj = getSwissEph().swe_utc_to_jd(day[0], day[1], d, (int) t[0], (int) t[1], t[2], calendar, j, sj);
            assertEquals(sn.toString(), sj.toString(), ctx);
            assertEquals(rn, rj, ctx);
            assertArrayEquals(n, j, 1e-9, ctx);
            final ISweJulianDate date = getSwissEph().swe_utc_to_jd(day[0], day[1], d, (int) t[0], (int) t[1], t[2], calendar, new StringBuilder());
            assertEquals(rn == ERR, date == null, ctx);
            if (rn == ERR) refused++;
        }
        assertTrue(refused > 100, "refused " + refused);
    }

    /** SweDate's own forms: the text of C, and the exception only when asked for */
    @Test
    void theFormsOfSweDate() {
        assertNull(SweDate.getInvalidUTCDateError(2016, 12, 31, 23, 59, 60, SweDate.SE_GREG_CAL));
        assertEquals("invalid date: year = 2017, month = 2, day = 30", SweDate.getInvalidUTCDateError(2017, 2, 30, 0, 0, 0, SweDate.SE_GREG_CAL));
        assertEquals("invalid time (no leap second!): 23:59:60.00", SweDate.getInvalidUTCDateError(2015, 12, 31, 23, 59, 60, SweDate.SE_GREG_CAL));
        assertTrue(SweDate.isValidUTCDate(2016, 12, 18, 23, 59, 60, SweDate.SE_JUL_CAL), "18 December of the Julian calendar");
        assertThrows(SwissephException.class, () -> SweDate.getJDfromUTC(2017, 2, 30, 0, 0, 0, SweDate.SE_GREG_CAL, true));
        assertArrayEquals(SweDate.getJDfromUTC(2017, 3, 2, 0, 0, 0, SweDate.SE_GREG_CAL, true),
                SweDate.getJDfromUTC(2017, 2, 30, 0, 0, 0, SweDate.SE_GREG_CAL, false), 1e-9);
    }
}
