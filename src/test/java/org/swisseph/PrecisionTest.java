/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.swisseph.api.ISweJulianDate;
import org.swisseph.app.SweJulianDate;
import org.swisseph.utils.IDateUtils;
import org.swisseph.utils.IDegreeUtils;
import org.swisseph.utils.IModuloUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins the rounding/precision behaviour of the conversion helpers. Every case here
 * failed before 2026-08.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
@Execution(ExecutionMode.CONCURRENT)
public class PrecisionTest {

    // ------------------------------------------------------------------ modulo

    @Test
    void moduloIntNeverReturnsTheModulus() {
        for (int k = -10; k <= 10; k++) {
            assertEquals(0, IModuloUtils.fix360(k * 360), "fix360(" + (k * 360) + ")");
        }
        assertEquals(1, IModuloUtils.fix360(-359));
        assertEquals(359, IModuloUtils.fix360(-1));
        assertEquals(359, IModuloUtils.fix360(359));
    }

    @Test
    void moduloDoubleNeverReturnsTheModulus() {
        // swe_calc can hand back a longitude a few ULPs below zero; normalizing it
        // must not yield 360. (which would index rasi 12 instead of rasi 0)
        for (double d : new double[]{-1e-18, -1e-16, -1e-15, -1e-14, -0.0, 0.0}) {
            assertEquals(0., IModuloUtils.fix360(d), "fix360(" + d + ")");
            assertEquals(0., IModuloUtils.fix30(d), "fix30(" + d + ")");
        }
        assertEquals(0., IModuloUtils.fix360(360.));
        assertEquals(0., IModuloUtils.fix360(-360.));
        assertEquals(0., IModuloUtils.fix30(30.));
    }

    @Test
    void moduloDoubleAgreesWithSwissEphemerisDegnorm() {
        // same snapping rule as swe_degnorm(): |y| < ~1e-13 collapses to 0
        assertEquals(0., IModuloUtils.fix360(-1e-14));
        assertTrue(IModuloUtils.fix360(-1e-12) > 359.9);
        assertEquals(180., IModuloUtils.fix360(-180.), 1e-12);
        assertEquals(40., IModuloUtils.fix360(400.), 1e-12);
        assertEquals(320., IModuloUtils.fix360(-400.), 1e-12);
    }

    // ------------------------------------------------------------------ degrees

    @Test
    void toDMSmsRendersWholeArcMinutesExactly() {
        // deg + min/60. is not exact in binary; truncating the decomposition used to
        // turn 1 + 1/60. into 01°00'59.99"
        for (int deg = 0; deg < 360; deg++) {
            for (int min = 0; min < 60; min++) {
                String expected = String.format("%02d°%02d'00.00\"", deg, min);
                assertEquals(expected, IDegreeUtils.toDMSms(deg + min / 60.).toString());
            }
        }
    }

    @Test
    void toDMSmsRendersWholeArcSecondsExactly() {
        for (int min = 0; min < 60; min++) {
            for (int sec = 0; sec < 60; sec++) {
                String expected = String.format("12°%02d'%02d.00\"", min, sec);
                assertEquals(expected, IDegreeUtils.toDMSms(12 + min / 60. + sec / 3600.).toString());
            }
        }
    }

    @Test
    void toDDmsIsExactAndRoundTrips() {
        assertEquals(49 + 45 / 60. + 27.50 / 3600., IDegreeUtils.toDDms(49452750), 0.);

        for (int deg = 0; deg < 360; deg += 7) {
            for (int min = 0; min < 60; min += 7) {
                for (int sec = 0; sec < 60; sec += 7) {
                    for (int cs : new int[]{0, 1, 25, 50, 99}) {
                        int idms = deg * 1000000 + min * 10000 + sec * 100 + cs;
                        assertEquals(idms, IDegreeUtils.toIDMSms(IDegreeUtils.toDDms(idms)),
                                "round trip of " + idms);
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------------- dates

    @Test
    void decimalHoursSplitBackIntoTheSameHoursMinutesSeconds() {
        for (int hh = 0; hh < 24; hh++) {
            for (int mm = 0; mm < 60; mm++) {
                for (int ss = 0; ss < 60; ss += 7) {
                    double lt = hh + mm / 60. + ss / 3600.;
                    ISweJulianDate jd = new SweJulianDate(new int[]{2024, 3, 7}, 0f, lt);
                    String at = hh + ":" + mm + ":" + ss;
                    assertEquals(hh, jd.hours(), at);
                    assertEquals(mm, jd.minutes(), at);
                    assertEquals(ss, jd.dseconds(), 1e-6, at);
                }
            }
        }
    }

    @Test
    void aTimeJustBelowAWholeHourDoesNotFallIntoThePreviousHour() {
        ISweJulianDate jd = new SweJulianDate(new int[]{2024, 3, 7, 10, 0}, 0f, 10. - 1e-15);
        assertEquals(10, jd.hours());
        assertEquals(0, jd.minutes());
        assertEquals(0., jd.dseconds(), 1e-9);
    }

    @Test
    void utimeSplitsTheSameWayAsLocalTime() {
        double t = 23 + 59 / 60. + 59 / 3600.;
        ISweJulianDate jd = new SweJulianDate(2451545., new int[]{2000, 1, 1}, t);
        assertEquals(23, jd.uhours());
        assertEquals(59, jd.uminutes());
        assertEquals(59., jd.useconds(), 1e-6);
    }

    @Test
    void fractionalSecondsAreRenderedAsTwoDigitsAndCarry() {
        assertEquals("2024–03–07 10:30:00.00", format7(10, 30, 0.0));
        assertEquals("2024–03–07 10:30:05.00", format7(10, 30, 5.0));
        assertEquals("2024–03–07 10:30:05.50", format7(10, 30, 5.5));
        assertEquals("2024–03–07 10:30:05.55", format7(10, 30, 5.55));
        // 59.999 s must carry into the next minute, not print an impossible "60.00"
        assertEquals("2024–03–07 10:31:00.00", format7(10, 30, 59.999));
    }

    private static String format7(int h, int m, double s) {
        return IDateUtils.format7(new SweJulianDate(new int[]{2024, 3, 7}, 0f,
                h + m / 60. + s / 3600.)).toString();
    }

    @Test
    void datetimeLongKeepsTheLeadingZerosOfEarlyYears() throws Exception {
        // Long.toString(5000101120000) has 13 digits - without padding the year 500
        // silently becomes the year 5000
        java.util.Calendar cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"));
        cal.setTime(IDateUtils.convert(5000101120000L));
        assertEquals(500, cal.get(java.util.Calendar.YEAR));
        assertEquals(1, cal.get(java.util.Calendar.MONTH) + 1);
        assertEquals(1, cal.get(java.util.Calendar.DAY_OF_MONTH));
        assertEquals(12, cal.get(java.util.Calendar.HOUR_OF_DAY));
    }
}
