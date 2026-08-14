/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@code swedate.c}: {@code swe_date_conversion}, {@code swe_julday}, {@code swe_revjul},
 * {@code swe_utc_to_jd}, {@code swe_jdet_to_utc}, {@code swe_jdut1_to_utc},
 * {@code swe_utc_time_zone}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
class SwephDateTest extends AIswTest {

    @Test
    void swe_julday_matchesTheKnownJ2000Instant() {
        double jd = getSwephExp().swe_julday(2000, 1, 1, 12., SE_GREG_CAL);
        assertEquals(J2000, jd, 1e-9);
    }

    @Test
    void swe_julday_andSweRevjul_roundTrip() {
        double jd = getSwephExp().swe_julday(1976, 4, 18, 23 + 21 / 60., SE_GREG_CAL);

        int[] ymd = new int[3];
        double[] jut = new double[1];
        getSwephExp().swe_revjul(jd, SE_GREG_CAL, ymd, jut);

        assertEquals(1976, ymd[0]);
        assertEquals(4, ymd[1]);
        assertEquals(18, ymd[2]);
        assertEquals(23 + 21 / 60., jut[0], 1e-6);
    }

    @Test
    void swe_revjul_marksTheGregorianJulianBoundaryCorrectly() {
        // JD 2299160.5 is 15 Oct 1582 read in the Gregorian calendar - the day the reform
        // took effect - and the immediately preceding julian day is 4 Oct 1582 Julian, since
        // the reform skipped the ten days in between.
        int[] julian = new int[3];
        double[] jut = new double[1];
        getSwephExp().swe_revjul(2299160.5 - 1., SE_JUL_CAL, julian, jut);
        assertEquals(1582, julian[0]);
        assertEquals(10, julian[1]);
        assertEquals(4, julian[2]);

        int[] greg = new int[3];
        getSwephExp().swe_revjul(2299160.5, SE_GREG_CAL, greg, jut);
        assertEquals(1582, greg[0]);
        assertEquals(10, greg[1]);
        assertEquals(15, greg[2]);
    }

    @Test
    void swe_date_conversion_returnsTheJulianDayAndSaysWhetherTheDateExists() {
        double[] tjd = new double[1];
        int ret = getSwephExp().swe_date_conversion(2000, 1, 1, 12., 'g', tjd);
        assertTrue(ret >= 0);
        assertEquals(J2000, tjd[0], 1e-9);

        // 31 February does not exist in any calendar
        double[] tjdBad = new double[1];
        int retBad = getSwephExp().swe_date_conversion(2000, 2, 31, 12., 'g', tjdBad);
        assertTrue(retBad < 0, "31 February should be reported as not existing");
        assertTrue(tjdBad[0] > 0, "but a julian day is still returned: " + tjdBad[0]);
    }

    @Test
    void swe_utc_to_jd_returnsBothEtAndUt1JulianDays() {
        double[] dret = new double[2];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_utc_to_jd(2000, 1, 1, 12, 0, 0., SE_GREG_CAL, dret, serr);

        assertTrue(ret >= 0, "" + serr);
        // dret[0] = ET (TT), dret[1] = UT1; ET runs ahead of UT1 in the year 2000
        assertTrue(dret[0] > dret[1], "TT is ahead of UT1: " + dret[0] + " vs " + dret[1]);
        assertEquals(J2000, dret[1], 0.01, "UT1 should be close to noon on 1 Jan 2000");
    }

    @Test
    void swe_jdet_to_utc_andSweUtcToJd_roundTrip() {
        double[] dret = new double[2];
        getSwephExp().swe_utc_to_jd(1990, 6, 15, 10, 30, 0., SE_GREG_CAL, dret, new StringBuilder());
        double jdET = dret[0];

        int[] out = new int[6];
        double[] dsec = new double[1];
        getSwephExp().swe_jdet_to_utc(jdET, SE_GREG_CAL, out, dsec);

        assertEquals(1990, out[0]);
        assertEquals(6, out[1]);
        assertEquals(15, out[2]);
        assertEquals(10, out[3]);
        assertEquals(30, out[4]);
        assertEquals(0., dsec[0], 1e-3);
    }

    @Test
    void swe_jdut1_to_utc_andSweUtcToJd_roundTrip() {
        double[] dret = new double[2];
        getSwephExp().swe_utc_to_jd(2010, 3, 20, 6, 15, 30., SE_GREG_CAL, dret, new StringBuilder());
        double jdUT1 = dret[1];

        int[] out = new int[6];
        double[] dsec = new double[1];
        getSwephExp().swe_jdut1_to_utc(jdUT1, SE_GREG_CAL, out, dsec);

        assertEquals(2010, out[0]);
        assertEquals(3, out[1]);
        assertEquals(20, out[2]);
        assertEquals(6, out[3]);
        assertEquals(15, out[4]);
        assertEquals(30., dsec[0], 1e-3);
    }

    /**
     * A positive {@code d_timezone} converts local time to UTC; that is the sign convention
     * swephexp.h documents for the raw call.
     */
    @Test
    void swe_utc_time_zone_convertsLocalTimeToUtcWithAPositiveOffset() {
        int[] out = new int[6];
        double[] dsec = new double[1];
        // 12:00 local at +2 hours must be 10:00 UTC
        getSwephExp().swe_utc_time_zone(2000, 1, 1, 12, 0, 0., 2., out, dsec);

        assertEquals(2000, out[0]);
        assertEquals(1, out[1]);
        assertEquals(1, out[2]);
        assertEquals(10, out[3]);
        assertEquals(0, out[4]);
    }

    @Test
    void swe_utc_time_zone_crossesTheDayBoundary() {
        int[] out = new int[6];
        double[] dsec = new double[1];
        // 01:00 local at +5 must be 20:00 UTC on the PREVIOUS day
        getSwephExp().swe_utc_time_zone(2000, 1, 1, 1, 0, 0., 5., out, dsec);

        assertEquals(1999, out[0]);
        assertEquals(12, out[1]);
        assertEquals(31, out[2]);
        assertEquals(20, out[3]);
    }

    @Test
    void swe_utc_time_zone_withANegativeOffsetConvertsUtcToLocalTime() {
        int[] out = new int[6];
        double[] dsec = new double[1];
        // 10:00 UTC converted with -2 (i.e. UTC to local at +2) must give 12:00 local
        getSwephExp().swe_utc_time_zone(2000, 1, 1, 10, 0, 0., -2., out, dsec);

        assertEquals(12, out[3]);
        assertEquals(0, out[4]);
    }
}
