/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.junit.jupiter.api.Test;
import swisseph.SwissEph;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The 12 swephlib.c helpers added to {@link ISwissEph}. Each one is checked against
 * the native library and against the pure Java {@link SwissEph}, which must not fall
 * through to the native defaults.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
public class PlacalcHelpersTest extends AbstractTest {

    /** 360 degrees expressed in centiseconds (DEG360 is package private) */
    private static final int DEG360 = 360 * 360000;

    private static final int CS_49_45_27_50 = 49 * 360000 + 45 * 6000 + 27 * 100 + 50;

    @Test
    void csnorm() {
        for (int p : new int[]{0, 1, DEG360 - 1, DEG360,
                DEG360 + 1, -1, -DEG360, -DEG360 - 1}) {
            assertEquals(getSwephExp().swe_csnorm(p), getSwissEph().swe_csnorm(p), "csnorm(" + p + ")");
        }
        assertEquals(0, getSwephExp().swe_csnorm(DEG360));
        assertEquals(DEG360 - 1, getSwephExp().swe_csnorm(-1));
    }

    @Test
    void differences() {
        final int a = 350 * 360000, b = 10 * 360000;

        assertEquals(getSwephExp().swe_difcsn(a, b), getSwissEph().swe_difcsn(a, b));
        assertEquals(340 * 360000, getSwephExp().swe_difcsn(a, b));

        assertEquals(getSwephExp().swe_difcs2n(a, b), getSwissEph().swe_difcs2n(a, b));
        assertEquals(-20 * 360000, getSwephExp().swe_difcs2n(a, b));

        assertEquals(340., getSwephExp().swe_difdegn(350., 10.), 1e-12);
        assertEquals(getSwephExp().swe_difdegn(350., 10.), getSwissEph().swe_difdegn(350., 10.), 1e-12);

        assertEquals(-20., getSwephExp().swe_difdeg2n(350., 10.), 1e-12);
        assertEquals(getSwephExp().swe_difdeg2n(350., 10.), getSwissEph().swe_difdeg2n(350., 10.), 1e-12);

        final double r1 = Math.toRadians(350.), r2 = Math.toRadians(10.);
        assertEquals(getSwephExp().swe_difrad2n(r1, r2), getSwissEph().swe_difrad2n(r1, r2), 1e-12);
    }

    @Test
    void csroundsecNeverRoundsUpIntoTheNextSign() {
        final int last = 29 * 360000 + 59 * 6000 + 59 * 100 + 59;  // 29°59'59.59"
        assertEquals(getSwephExp().swe_csroundsec(last), getSwissEph().swe_csroundsec(last));
        assertEquals(29 * 360000 + 59 * 6000 + 59 * 100, getSwephExp().swe_csroundsec(last));

        final int mid = 12 * 360000 + 30 * 6000 + 10 * 100 + 50;
        assertEquals(getSwephExp().swe_csroundsec(mid), getSwissEph().swe_csroundsec(mid));
    }

    @Test
    void d2lRoundsAwayFromZero() {
        for (double x : new double[]{0., .4, .5, .6, 1.5, -.4, -.5, -.6, -1.5}) {
            assertEquals(getSwephExp().swe_d2l(x), getSwissEph().swe_d2l(x), "d2l(" + x + ")");
        }
        assertEquals(1, getSwephExp().swe_d2l(.5));
        assertEquals(-1, getSwephExp().swe_d2l(-.5));
    }

    @Test
    void dayOfWeek() {
        // 2000-01-01 12:00 UT was a Saturday -> monday = 0 .. sunday = 6 -> 5
        assertEquals(5, getSwephExp().swe_day_of_week(2451545.));
        for (double jd = 2451545.; jd < 2451545. + 14; jd += 1.) {
            assertEquals(getSwephExp().swe_day_of_week(jd), getSwissEph().swe_day_of_week(jd), "dow(" + jd + ")");
        }
    }

    @Test
    void cs2timestr() {
        final int t = (12 * 3600 + 34 * 60 + 56) * 100;
        assertEquals("12:34:56", getSwephExp().swe_cs2timestr(t, ':', false));
        assertEquals(getSwephExp().swe_cs2timestr(t, ':', false), getSwissEph().swe_cs2timestr(t, ':', false));

        final int whole = (12 * 3600 + 34 * 60) * 100;
        assertEquals("12:34", getSwephExp().swe_cs2timestr(whole, ':', true));
        assertEquals(getSwephExp().swe_cs2timestr(whole, ':', true), getSwissEph().swe_cs2timestr(whole, ':', true));
    }

    @Test
    void cs2lonlatstr() {
        // the input is 49°45'27.50" and swe_cs2lonlatstr rounds to whole seconds
        assertEquals("49E45'28", getSwephExp().swe_cs2lonlatstr(CS_49_45_27_50, 'E', 'W'));
        assertEquals(getSwephExp().swe_cs2lonlatstr(CS_49_45_27_50, 'E', 'W'),
                getSwissEph().swe_cs2lonlatstr(CS_49_45_27_50, 'E', 'W'));

        assertEquals(getSwephExp().swe_cs2lonlatstr(-CS_49_45_27_50, 'E', 'W'),
                getSwissEph().swe_cs2lonlatstr(-CS_49_45_27_50, 'E', 'W'));
    }

    @Test
    void cs2degstr() {
        assertEquals(getSwephExp().swe_cs2degstr(CS_49_45_27_50), getSwissEph().swe_cs2degstr(CS_49_45_27_50));
    }
}
