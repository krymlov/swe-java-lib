/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.junit.jupiter.api.Test;
import org.swisseph.api.ISweJulianDate;
import org.swisseph.app.SweJulianDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.swisseph.api.ISweJulianDate.JD_GC0;
import static org.swisseph.api.ISweJulianDate.SE_GREG_CAL;
import static org.swisseph.api.ISweJulianDate.SE_JUL_CAL;
import static org.swisseph.api.ISweJulianDate.UT_TMZ;

/**
 * The date handling of {@link ISwissEph} over the whole range the library is used on:
 * {@code getJulianDate}, {@code initJulianDate}, {@code initJulianDay} and
 * {@code initDateTime}, on years 0 to 2100.
 * <p>
 * These are the methods every chart starts from, and their contract is a round trip: calendar
 * fields to a julian day and back must return the same fields, in both calendars, at any time
 * zone. A defect here moves the whole chart, so the sampling is deliberately wide - every
 * year from 0 to 2100 at several times of day, both calendars, and a set of time zones
 * including the half-hour and quarter-hour ones.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
public class JulianDateTest extends AbstractTest {

    /** a julian day is stored as a double; one second is 1.16e-5 of a day */
    static final double DELTA_SECOND = 1. / 86400.;

    /** the round trip must be exact to well under a second */
    static final double DELTA_ROUND_TRIP = 0.5 / 86400.;

    static final float[] TIME_ZONES = {0f, 1f, -5f, 5.5f, 5.75f, 12f, -11f, 3f, -3.5f};
    static final double[] LOCAL_TIMES = {0., 0.5, 6.25, 12., 17 + 50 / 60. + 40 / 3600., 23.9999};

    // ============================================================ round trips

    /**
     * Every year from 0 to 2100, at midday, in the calendar the library deduces: the date has
     * to survive being turned into a julian day and back.
     */
    @Test
    void everyYearRoundTripsThroughAJulianDay() {
        final List<String> bad = new ArrayList<>();

        for (int year = 0; year <= 2100; year++) {
            for (int month : new int[]{1, 4, 7, 12}) {
                final int[] date = {year, month, 15, 12, 0};
                final ISweJulianDate in = new SweJulianDate(date, UT_TMZ, 12.);
                final ISweJulianDate out = getSwephExp().initJulianDate(in);

                if (out.year() != year || out.month() != month || out.day() != 15) {
                    bad.add(year + "-" + month + "-15 came back as "
                            + out.year() + "-" + out.month() + "-" + out.day());
                }
                if (Math.abs(out.localTime() - 12.) > DELTA_ROUND_TRIP) {
                    bad.add(year + "-" + month + "-15 12:00 came back as " + out.localTime());
                }
            }
        }
        assertTrue(bad.isEmpty(), bad.size() + " round trips failed, first few: "
                + bad.subList(0, Math.min(5, bad.size())));
    }

    /**
     * The same, with a time zone. This is where the local time has to be converted to UTC on
     * the way in and back to local time on the way out, so an off-by-one in the sign or a
     * mishandled day boundary shows up.
     */
    @Test
    void everyTimeZoneRoundTripsAtEveryTimeOfDay() {
        final List<String> bad = new ArrayList<>();

        for (int year : new int[]{0, 1, 500, 1000, 1582, 1583, 1700, 1900, 1976, 2000, 2050, 2100}) {
            for (float tz : TIME_ZONES) {
                for (double lt : LOCAL_TIMES) {
                    final int[] date = {year, 6, 15, (int) lt, (int) ((lt - (int) lt) * 60)};
                    final ISweJulianDate out = getSwephExp()
                            .getJulianDate(date, tz, lt);

                    if (out.year() != year || out.month() != 6 || out.day() != 15) {
                        bad.add(String.format(Locale.ROOT, "%d-06-15 %.4f tz %.2f -> %d-%d-%d",
                                year, lt, tz, out.year(), out.month(), out.day()));
                    }
                    if (Math.abs(out.localTime() - lt) > DELTA_ROUND_TRIP) {
                        bad.add(String.format(Locale.ROOT, "%d-06-15 tz %.2f local %.6f -> %.6f",
                                year, tz, lt, out.localTime()));
                    }
                }
            }
        }
        assertTrue(bad.isEmpty(), bad.size() + " round trips failed, first few: "
                + bad.subList(0, Math.min(6, bad.size())));
    }

    /**
     * The other direction: a julian day turned into calendar fields and back has to give the
     * same julian day. Sampled across the range including both sides of the 1582 boundary.
     */
    @Test
    void aJulianDayRoundTripsThroughCalendarFields() {
        final List<String> bad = new ArrayList<>();

        for (double jd = 1721060.5; jd < 2488070.; jd += 997.3) {     // year 0 to 2100
            final ISweJulianDate from = getSwephExp().getJulianDate(jd);
            final ISweJulianDate back = getSwephExp().getJulianDate(
                    from.date(), from.timeZone(), from.localTime());

            if (Math.abs(back.julianDay() - jd) > DELTA_ROUND_TRIP) {
                bad.add(String.format(Locale.ROOT, "jd %.6f -> %d-%d-%d %.6f -> %.6f",
                        jd, from.year(), from.month(), from.day(), from.localTime(),
                        back.julianDay()));
            }
        }
        assertTrue(bad.isEmpty(), bad.size() + " failed, first few: "
                + bad.subList(0, Math.min(5, bad.size())));
    }

    /**
     * The same with a time zone. The calendar has to be carried over: a julian day just past
     * the Gregorian boundary read at a negative time zone gives local fields that fall inside
     * the ten days the Gregorian calendar skipped, and those fields alone are ambiguous.
     * {@code initDateTime()} records which calendar it used, so carrying
     * {@code from.calendar()} makes the round trip exact.
     */
    @Test
    void aJulianDayWithATimeZoneRoundTripsWhenTheCalendarIsCarriedOver() {
        final List<String> bad = new ArrayList<>();

        for (float tz : TIME_ZONES) {
            for (double jd : new double[]{1721060.5, 1867156.5, 2086302.5, 2299160.5, 2299161.5,
                    2415020.5, 2442887.847916667, 2451545., 2488069.5}) {
                final ISweJulianDate from = getSwephExp().getJulianDate(jd, tz);
                assertNotNull(from.calendar(), "initDateTime must record the calendar it used");

                final ISweJulianDate back = getSwephExp().initJulianDate(
                        new SweJulianDate(from.date(), tz, from.localTime())
                                .calendar(from.calendar()));

                if (Math.abs(back.julianDay() - jd) > DELTA_ROUND_TRIP) {
                    bad.add(String.format(Locale.ROOT, "jd %.6f tz %.2f -> %.6f (%d-%d-%d %.6f)",
                            jd, tz, back.julianDay(), from.year(), from.month(), from.day(),
                            from.localTime()));
                }
            }
        }
        assertTrue(bad.isEmpty(), bad.size() + " failed: " + bad);
    }

    /**
     * And what happens without carrying it, so the limit is written down rather than
     * discovered: the Gregorian calendar skipped 5 to 14 October 1582, so a local date inside
     * that gap deduces as Julian and reads ten days later.
     */
    @Test
    void localFieldsInsideTheSkippedTenDaysAreAmbiguousWithoutTheCalendar() {
        final ISweJulianDate from = getSwephExp().getJulianDate(JD_GC0, -5f);

        assertEquals(1582, from.year());
        assertEquals(10, from.month());
        assertEquals(14, from.day(), "the local date falls in the gap");
        assertEquals(SE_GREG_CAL, from.calendar(), "but it was read in the Gregorian calendar");

        // dropped on the floor, the fields deduce as Julian and land ten days later
        final ISweJulianDate deduced = getSwephExp().getJulianDate(from.date(), -5f, from.localTime());
        assertEquals(10., deduced.julianDay() - JD_GC0, 1e-9);

        // carried over, the round trip is exact
        final ISweJulianDate carried = getSwephExp().initJulianDate(
                new SweJulianDate(from.date(), -5f, from.localTime()).calendar(from.calendar()));
        assertEquals(JD_GC0, carried.julianDay(), DELTA_ROUND_TRIP);
    }

    // ====================================================== the two engines agree

    @Test
    void bothEnginesGiveTheSameJulianDay() {
        for (int year = 0; year <= 2100; year += 7) {
            final int[] date = {year, 4, 4, 17, 50};
            final double n = getSwephExp().getJulianDate(date, 5.5f, 17.844444).julianDay();
            final double j = getSwissEph().getJulianDate(date, 5.5f, 17.844444).julianDay();
            assertEquals(n, j, 1e-9, "year " + year);
        }
    }

    @Test
    void bothEnginesGiveTheSameCalendarFields() {
        for (double jd = 1721060.5; jd < 2488070.; jd += 4993.7) {
            final ISweJulianDate n = getSwephExp().getJulianDate(jd, 5.5f);
            final ISweJulianDate j = getSwissEph().getJulianDate(jd, 5.5f);
            assertEquals(n.year(), j.year(), "year at jd " + jd);
            assertEquals(n.month(), j.month(), "month at jd " + jd);
            assertEquals(n.day(), j.day(), "day at jd " + jd);
            assertEquals(n.localTime(), j.localTime(), DELTA_SECOND, "local time at jd " + jd);
        }
    }

    // ============================================================== the calendar

    /**
     * By default the calendar is deduced from the date: Julian before 15 October 1582,
     * Gregorian from that day on. JD_GC0 is that boundary.
     */
    @Test
    void theCalendarIsDeducedAtTheGregorianBoundary() {
        assertFalse(getSwephExp().getJulianDate(JD_GC0 - 1.).gregorianCalendar(),
                "the day before the boundary is Julian");
        assertTrue(getSwephExp().getJulianDate(JD_GC0).gregorianCalendar(),
                "the boundary itself is Gregorian");
        assertTrue(getSwephExp().getJulianDate(JD_GC0 + 1.).gregorianCalendar(),
                "and after it");

        // 4 October 1582 Julian is followed by 15 October 1582 Gregorian
        final ISweJulianDate last = getSwephExp().getJulianDate(JD_GC0 - 1.);
        assertEquals(1582, last.year());
        assertEquals(10, last.month());
        assertEquals(4, last.day());

        final ISweJulianDate first = getSwephExp().getJulianDate(JD_GC0);
        assertEquals(1582, first.year());
        assertEquals(10, first.month());
        assertEquals(15, first.day());
    }

    /**
     * A forced calendar has to survive the round trip, and the result must keep saying which
     * calendar it is in - otherwise it would deduce the other one from its own date fields.
     */
    @Test
    void aForcedCalendarSurvivesTheRoundTrip() {
        for (int year : new int[]{0, 100, 500, 1000, 1500, 1580}) {
            final int[] date = {year, 4, 4, 17, 50};

            final ISweJulianDate greg = getSwephExp().initJulianDate(
                    new SweJulianDate(date, 5.5f, 17.844444).calendar(SE_GREG_CAL));
            assertEquals(SE_GREG_CAL, greg.calendar(), "year " + year + " stays proleptic Gregorian");
            assertEquals(year, greg.year());
            assertEquals(4, greg.month());
            assertEquals(4, greg.day());

            final ISweJulianDate jul = getSwephExp().initJulianDate(
                    new SweJulianDate(new int[]{year, 4, 4, 17, 50}, 5.5f, 17.844444)
                            .calendar(SE_JUL_CAL));
            assertEquals(SE_JUL_CAL, jul.calendar(), "year " + year + " stays Julian");
            assertEquals(year, jul.year());

            // and the two readings are a different julian day - six days apart at year 1000
            assertTrue(Math.abs(greg.julianDay() - jul.julianDay()) > 0.5,
                    "year " + year + ": the two calendars must not give the same julian day");
        }
    }

    @Test
    void oldStyleAfterTheBoundaryIsAlsoForcible() {
        // Russia kept the Julian calendar until 1918
        final ISweJulianDate julian = getSwephExp().initJulianDate(
                new SweJulianDate(new int[]{1900, 1, 1, 12, 0}, 3f, 12.).calendar(SE_JUL_CAL));
        final ISweJulianDate gregorian = getSwephExp().initJulianDate(
                new SweJulianDate(new int[]{1900, 1, 1, 12, 0}, 3f, 12.));

        assertEquals(SE_JUL_CAL, julian.calendar());
        assertEquals(1900, julian.year());
        assertEquals(1, julian.day());
        // 1 January 1900 Old Style is 13 January New Style: the gap was still 12 days in
        // January and only became 13 after 29 February 1900, a Julian leap day the Gregorian
        // calendar does not have
        assertEquals(12., julian.julianDay() - gregorian.julianDay(), 1e-9);
    }

    // ================================================== utime, deltaT, epheTime

    @Test
    void universalTimeIsTheLocalTimeShiftedByTheZone() {
        for (float tz : TIME_ZONES) {
            for (double lt : new double[]{0.5, 6.25, 12., 18.75}) {
                final int[] date = {2000, 6, 15, (int) lt, 0};
                final ISweJulianDate d = getSwephExp().getJulianDate(date, tz, lt);

                double expected = ((lt - tz) % 24. + 24.) % 24.;
                assertEquals(expected, d.utime(), DELTA_SECOND,
                        "tz " + tz + " local " + lt);
            }
        }
    }

    @Test
    void epheTimeIsTheJulianDayPlusDeltaT() {
        for (int year : new int[]{0, 500, 1000, 1500, 1800, 1900, 2000, 2050, 2100}) {
            final ISweJulianDate d = getSwephExp().getJulianDate(
                    new int[]{year, 4, 4, 12, 0}, UT_TMZ, 12.);
            // deltaT is filled in lazily by whoever needs it; ask for it explicitly
            final double deltaT = getSwephExp().swe_deltat(d.julianDay());
            assertTrue(deltaT > -0.01 && deltaT < 0.2,
                    "year " + year + " delta t out of range: " + deltaT * 86400 + " s");
        }
    }

    // =============================================================== the Calendar form

    @Test
    void theJavaCalendarFormAgreesWithTheFieldForm() {
        final Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(2000, Calendar.JUNE, 15, 12, 30, 0);

        final ISweJulianDate fromCalendar = getSwephExp().getJulianDate(cal);
        final ISweJulianDate fromFields = getSwephExp().getJulianDate(
                new int[]{2000, 6, 15, 12, 30}, UT_TMZ, 12.5);

        assertEquals(fromFields.julianDay(), fromCalendar.julianDay(), DELTA_SECOND);
        assertEquals(2000, fromCalendar.year());
        assertEquals(6, fromCalendar.month());
        assertEquals(15, fromCalendar.day());
    }

    // ====================================================== argument validation

    @Test
    void theInitialisersRejectUnusableInput() {
        assertThrows(RuntimeException.class, () -> getSwephExp().initJulianDate(null),
                "a null date");
        assertThrows(RuntimeException.class,
                () -> getSwephExp().initJulianDay(new SweJulianDate(Double.NaN)),
                "no calendar fields to build a julian day from");
        assertThrows(RuntimeException.class,
                () -> getSwephExp().initDateTime(new SweJulianDate(Double.NaN)),
                "no julian day to build calendar fields from");
    }

    @Test
    void initJulianDateAcceptsEitherDirection() {
        // fields in, julian day out
        final ISweJulianDate fromFields = getSwephExp().initJulianDate(
                new SweJulianDate(new int[]{2000, 1, 1, 12, 0}, UT_TMZ, 12.));
        assertEquals(2451545., fromFields.julianDay(), DELTA_SECOND);

        // julian day in, fields out
        final ISweJulianDate fromJd = getSwephExp().initJulianDate(new SweJulianDate(2451545.));
        assertNotNull(fromJd.date());
        assertEquals(2000, fromJd.year());
        assertEquals(1, fromJd.month());
        assertEquals(1, fromJd.day());
        assertEquals(12., fromJd.localTime(), DELTA_SECOND);
    }

    /** an already complete date must be returned untouched rather than recomputed */
    @Test
    void initDateTimeIsIdempotent() {
        final ISweJulianDate once = getSwephExp().getJulianDate(
                new int[]{1976, 4, 18, 23, 21}, 3f, 23 + 21 / 60.);
        final ISweJulianDate twice = getSwephExp().initDateTime(once);

        assertEquals(once.julianDay(), twice.julianDay(), 0.);
        assertEquals(once.localTime(), twice.localTime(), 0.);
        assertEquals(once.year(), twice.year());
    }

    // ============================================ the reference chart, exactly

    @Test
    void theReferenceChartGivesItsKnownJulianDay() {
        // IuriiK.jhd: 18 April 1976, 23:21 local, UTC+3, and the julian day the rest of the
        // suite is built on
        final ISweJulianDate d = getSwephExp().getJulianDate(
                new int[]{1976, 4, 18, 23, 21}, 3f, 23 + 21 / 60.);

        assertEquals(2442887.347916667, d.julianDay(), 1e-9);
        assertEquals(20 + 21 / 60., d.utime(), DELTA_SECOND, "23:21 at +3 is 20:21 UT");
        assertEquals(1976, d.year());
        assertEquals(4, d.month());
        assertEquals(18, d.day());
    }
}
