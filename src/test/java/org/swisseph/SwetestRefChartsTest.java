/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.swisseph.api.*;
import org.swisseph.app.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.swisseph.Swetest.*;
import static org.swisseph.api.ISweJulianDate.SE_GREG_CAL;
import static org.swisseph.api.ISweJulianDate.SE_JUL_CAL;
import static org.swisseph.api.ISweObjects.*;
import static org.swisseph.app.SweAyanamsa.*;
import static org.swisseph.app.SweHouseSystem.*;
import static org.swisseph.app.SweObjectsOptions.TROPICAL_ZODIAC;

/**
 * The reference charts used to validate Jagannatha Hora
 * (<code>jyotisa-uajhora/etc/v8.0/uk_exe/jhora8uk/test-data/ref&lt;year&gt;.jhd</code>),
 * here diffed against <code>swetest64.exe</code> instead.
 * <p>
 * One place and time of day, seventeen epochs: <b>4 April, 17:50:40 local, time zone
 * 5:30 East, 81&deg;08'E 16&deg;10'N (Machilipatnam, India)</b>. Only what was asked for is
 * compared - lagna, planets, ayanamsa and house cusps in every house system.
 * <p>
 * The epochs span four ephemeris files (<code>sepl_00</code> … <code>sepl_18</code>) and
 * both calendars. <b>The calendar convention is not the same as Jagannatha Hora's</b> -
 * see {@link #theJulianCalendarConventionDiffersFromJagannathaHora()}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
public class SwetestRefChartsTest extends AbstractTest {

    static final double DELTA = 1e-7;

    /** the years of the ref&lt;year&gt;.jhd reference set */
    static final int[] REF_YEARS = {0, 100, 500, 1000, 1500, 1700, 1800, 1900,
            1970, 1990, 2000, 2010, 2030, 2050, 2070, 2090, 2100};

    // Machilipatnam: 81 E 08' 00", 16 N 10' 00"
    static final double GEO_LON = 81 + 8 / 60.;
    static final double GEO_LAT = 16 + 10 / 60.;

    static final float TIME_ZONE = 5.5f;
    static final double LOCAL_TIME = 17 + 50 / 60. + 40 / 3600.;   // 17:50:40
    static final String UTC_TIME = "12:20:40";                     // 17:50:40 - 5:30

    static final int[] SWETEST_TO_OBJECT = {SY, CH, BU, SK, MA, GU, SA, UR, NE, PL};

    private static int[] date(int year) {
        return new int[]{year, 4, 4, 17, 50};
    }

    private ISweObjects chart(int year, ISweObjectsOptions options) {
        return new SweObjects(getSwephExp(), new SweJulianDate(date(year), TIME_ZONE, LOCAL_TIME),
                new SweGeoLocation(GEO_LON, GEO_LAT, 0.), options).completeBuild();
    }

    private static ISweObjectsOptions sidereal(SweAyanamsa ayanamsa, SweHouseSystem hsys) {
        return new SweObjectsOptions.Builder().ayanamsa(ayanamsa).houseSystem(hsys).build();
    }

    private static ISweObjectsOptions sidereal(SweAyanamsa ayanamsa, SweHouseSystem hsys, boolean trueNode) {
        return new SweObjectsOptions.Builder().ayanamsa(ayanamsa)
                .houseSystem(hsys).trueNode(trueNode).build();
    }

    private static SweAyanamsa ayanamsaOf(int sid) {
        for (SweAyanamsa a : SweAyanamsa.values()) if (a.fid() == sid) return a;
        throw new IllegalArgumentException("no ayanamsa with sid " + sid);
    }

    private Map<String, Double> swetest(int year, String... extra) {
        return values(date(year), UTC_TIME, extra);
    }

    /** "23°50'41.83" as swetest prints it after "ayanamsa =" */
    private double swetestAyanamsa(int year, int sid) {
        for (String line : lines(date(year), UTC_TIME, "-p0", "-true", "-sid" + sid, "-fPl")) {
            final int at = line.indexOf("ayanamsa =");
            if (at < 0) continue;
            return SwetestEpochsCrossCheckTest.parseDms(line.substring(at + 10, line.indexOf('(', at)));
        }
        throw new AssertionError("swetest printed no ayanamsa for sid" + sid + " in " + year);
    }

    // ================================================================= ayanamsa

    @ParameterizedTest(name = "{0} True Citra ayanamsa")
    @ValueSource(ints = {0, 100, 500, 1000, 1500, 1700, 1800, 1900,
            1970, 1990, 2000, 2010, 2030, 2050, 2070, 2090, 2100})
    void ayanamsaMatchesSwetestAcrossEpochs(int year) {
        assumeTrue(available());
        assertEquals(swetestAyanamsa(year, TRUE_CITRA.fid()),
                chart(year, sidereal(TRUE_CITRA, PLACIDUS)).ayanamsa(), 1e-6, "ayanamsa " + year);
    }

    @ParameterizedTest(name = "sid{0} in {1}")
    @CsvSource({"0,500", "1,500", "3,1000", "5,1500", "7,1700", "16,1900",
            "21,2000", "23,2030", "27,2100", "1,0", "1,2090", "5,100"})
    void ayanamsaTypesMatchSwetest(int sid, int year) {
        assumeTrue(available());
        final SweAyanamsa ayanamsa = ayanamsaOf(sid);
        assertEquals(swetestAyanamsa(year, sid),
                chart(year, sidereal(ayanamsa, PLACIDUS)).ayanamsa(), 1e-6, ayanamsa + " " + year);
    }

    // ================================================================== planets

    @ParameterizedTest(name = "{0} planets")
    @ValueSource(ints = {0, 100, 500, 1000, 1500, 1700, 1800, 1900,
            1970, 1990, 2000, 2010, 2030, 2050, 2070, 2090, 2100})
    void planetsMatchSwetestAcrossEpochs(int year) {
        assumeTrue(available());
        final Map<String, Double> ref = swetest(year, "-p" + BODIES, "-true", "-sid27", "-fPl");
        final ISweObjects o = chart(year, sidereal(TRUE_CITRA, PLACIDUS));

        for (int i = 0; i < BODY_NAMES.length; i++) {
            final Double expected = ref.get(BODY_NAMES[i]);
            assertNotNull(expected, year + ": " + BODY_NAMES[i] + " missing from swetest");
            assertEquals(expected, o.longitudes()[SWETEST_TO_OBJECT[i]], DELTA,
                    year + " " + BODY_NAMES[i]);
        }
    }

    @ParameterizedTest(name = "{0} Rahu/Ketu")
    @ValueSource(ints = {0, 100, 500, 1000, 1500, 1700, 1800, 1900,
            1970, 1990, 2000, 2010, 2030, 2050, 2070, 2090, 2100})
    void lunarNodesMatchSwetestAcrossEpochs(int year) {
        assumeTrue(available());
        final Map<String, Double> ref = swetest(year, "-pmt", "-true", "-sid27", "-fPl");

        final ISweObjects mean = chart(year, sidereal(TRUE_CITRA, PLACIDUS, false));
        assertEquals(ref.get("mean Node"), mean.longitudes()[RA], DELTA, year + " mean Rahu");
        assertEquals((ref.get("mean Node") + 180.) % 360., mean.longitudes()[KE], DELTA, year + " mean Ketu");

        final ISweObjects node = chart(year, sidereal(TRUE_CITRA, PLACIDUS, true));
        assertEquals(ref.get("true Node"), node.longitudes()[RA], DELTA, year + " true Rahu");
        assertEquals((ref.get("true Node") + 180.) % 360., node.longitudes()[KE], DELTA, year + " true Ketu");
    }

    // ==================================================================== lagna

    @ParameterizedTest(name = "{0} lagna")
    @ValueSource(ints = {0, 100, 500, 1000, 1500, 1700, 1800, 1900,
            1970, 1990, 2000, 2010, 2030, 2050, 2070, 2090, 2100})
    void lagnaMatchesSwetestAcrossEpochs(int year) {
        assumeTrue(available());
        final Map<String, Double> ref = swetest(year, "-p0", "-true", "-sid27", "-fPl",
                house(GEO_LON, GEO_LAT, (char) PLACIDUS.fid()));
        final ISweObjects o = chart(year, sidereal(TRUE_CITRA, PLACIDUS));

        assertEquals(ref.get("Ascendant"), o.longitudes()[LG], DELTA, year + " lagna");
        assertEquals(ref.get("Ascendant"), o.ascmc()[0], DELTA, year + " ascmc[SE_ASC]");
        assertEquals(ref.get("MC"), o.ascmc()[1], DELTA, year + " MC");
        assertEquals(ref.get("ARMC"), o.ascmc()[2], DELTA, year + " ARMC");
        assertEquals(ref.get("Vertex"), o.ascmc()[3], DELTA, year + " Vertex");
    }

    /**
     * The lagna itself does not depend on the house system, but it is read out of the same
     * <code>ascmc[]</code> that <code>swe_houses_ex()</code> fills for the chosen system,
     * so it is worth checking that every system still reports the same ascendant.
     */
    @ParameterizedTest(name = "{0} lagna")
    @EnumSource(SweHouseSystem.class)
    void lagnaIsTheSameInEveryHouseSystem(SweHouseSystem hsys) {
        assumeTrue(available());
        if (NIL == hsys) return;

        final Map<String, Double> ref = swetest(2000, "-p0", "-true", "-sid27", "-fPl",
                house(GEO_LON, GEO_LAT, (char) hsys.fid()));
        assertEquals(ref.get("Ascendant"), chart(2000, sidereal(TRUE_CITRA, hsys)).longitudes()[LG],
                DELTA, hsys + " lagna");
    }

    // =================================================================== houses

    private void assertCuspsMatch(int year, SweHouseSystem hsys, ISweObjectsOptions options, String... zodiac) {
        final String[] extra = new String[zodiac.length + 4];
        extra[0] = "-p0";
        extra[1] = "-true";
        extra[2] = "-fPl";
        extra[3] = house(GEO_LON, GEO_LAT, (char) hsys.fid());
        System.arraycopy(zodiac, 0, extra, 4, zodiac.length);

        final Map<String, Double> ref = swetest(year, extra);
        final double[] refCusps = cusps(ref);
        final String where = hsys + " " + year;
        assertNotNull(refCusps, where + ": swetest printed no cusps");

        final ISweObjects o = chart(year, options);
        for (int h = 1; h <= 12; h++) {
            assertEquals(refCusps[h], o.cusps()[h], DELTA, where + " house " + h);
        }
    }

    @ParameterizedTest(name = "{0} Placidus cusps")
    @ValueSource(ints = {0, 100, 500, 1000, 1500, 1700, 1800, 1900,
            1970, 1990, 2000, 2010, 2030, 2050, 2070, 2090, 2100})
    void placidusCuspsMatchSwetestAcrossEpochs(int year) {
        assumeTrue(available());
        assertCuspsMatch(year, PLACIDUS, sidereal(TRUE_CITRA, PLACIDUS), "-sid27");
    }

    @ParameterizedTest(name = "{0} cusps")
    @EnumSource(SweHouseSystem.class)
    void cuspsMatchSwetestInEveryHouseSystem(SweHouseSystem hsys) {
        assumeTrue(available());
        if (NIL == hsys) return;

        for (int year : new int[]{500, 1500, 2000, 2100}) {
            assertCuspsMatch(year, hsys, sidereal(TRUE_CITRA, hsys), "-sid27");
        }
    }

    // ================================================================= tropical

    @ParameterizedTest(name = "{0} tropical")
    @ValueSource(ints = {0, 500, 1000, 1500, 1800, 1970, 2000, 2050, 2100})
    void tropicalPlanetsLagnaAndCuspsMatchSwetest(int year) {
        assumeTrue(available());
        final ISweObjectsOptions options = new SweObjectsOptions.Builder()
                .options(TROPICAL_ZODIAC).houseSystem(KOCH).build();

        final Map<String, Double> ref = swetest(year, "-p" + BODIES, "-true", "-fPl",
                house(GEO_LON, GEO_LAT, (char) KOCH.fid()));
        final ISweObjects o = chart(year, options);

        for (int i = 0; i < BODY_NAMES.length; i++) {
            assertEquals(ref.get(BODY_NAMES[i]), o.longitudes()[SWETEST_TO_OBJECT[i]], DELTA,
                    year + " tropical " + BODY_NAMES[i]);
        }
        assertEquals(ref.get("Ascendant"), o.longitudes()[LG], DELTA, year + " tropical lagna");

        final double[] refCusps = cusps(ref);
        assertNotNull(refCusps, year + ": no cusps");
        for (int h = 1; h <= 12; h++) {
            assertEquals(refCusps[h], o.cusps()[h], DELTA, year + " tropical house " + h);
        }
    }

    // ================================================================= calendar

    /**
     * Worth knowing before these numbers are compared with the Jagannatha Hora dumps:
     * the reference set was taken with <code>-b4.4.&lt;year&gt;greg</code>, i.e. the
     * <b>proleptic Gregorian</b> calendar, because that is what Jagannatha Hora uses.
     * {@link ISweJulianDate#gregorianCalendar} switches to the Julian calendar before
     * 1582-10-15, and so does swetest when it is not told otherwise, so this library and
     * the reference dumps are reading "4 April 1000" as two different days.
     * <p>
     * Six days apart in the year 1000 - not a rounding difference, a different chart.
     * Everything else in this class compares like with like, because both sides are left
     * to auto-select. To reproduce the Jagannatha Hora dumps exactly, the library would
     * need a way to force the proleptic Gregorian calendar for a pre-1582 date, which
     * {@link ISweJulianDate} does not currently offer.
     */
    @Test
    void theJulianCalendarConventionDiffersFromJagannathaHora() {
        assumeTrue(available());

        final ISweJulianDate julian = getSwephExp().initJulianDate(
                new SweJulianDate(date(1000), TIME_ZONE, LOCAL_TIME));
        assertFalse(julian.gregorianCalendar(), "the library reads 4.4.1000 as a Julian date");

        final double swetestJulian = swetestJulianDay("-b4.4.1000");
        final double swetestGregorian = swetestJulianDay("-b4.4.1000greg");

        assertEquals(swetestJulian, julian.julianDay(), 1e-6, "auto-selected calendars agree");
        assertEquals(-6., swetestGregorian - swetestJulian, 1e-6,
                "proleptic Gregorian 4 April 1000 is 6 days earlier than the Julian one");

        // and from 1583 on the question does not arise
        final ISweJulianDate modern = getSwephExp().initJulianDate(
                new SweJulianDate(date(2000), TIME_ZONE, LOCAL_TIME));
        assertTrue(modern.gregorianCalendar());
        assertEquals(swetestJulianDay("-b4.4.2000"), modern.julianDay(), 1e-6);
    }

    /**
     * With {@link SweJulianDate#calendar(Boolean)} the pre-1582 charts can be reproduced
     * the way Jagannatha Hora computes them, i.e. in the proleptic Gregorian calendar.
     */
    @ParameterizedTest(name = "{0} proleptic gregorian")
    @ValueSource(ints = {0, 100, 500, 1000, 1500})
    void aProlepticGregorianDateMatchesSwetestGreg(int year) {
        assumeTrue(available());

        final ISweJulianDate proleptic = getSwephExp().initJulianDate(
                new SweJulianDate(date(year), TIME_ZONE, LOCAL_TIME).calendar(SE_GREG_CAL));

        assertTrue(proleptic.gregorianCalendar(), "the override wins over the 1582 rule");
        assertEquals(swetestJulianDay("-b4.4." + year + "greg"), proleptic.julianDay(), 1e-6,
                year + " proleptic gregorian julian day");

        // and the date fields still read back as the same calendar day
        assertArrayEquals(new int[]{year, 4, 4, 17, 50}, proleptic.date());

        // the whole chart then matches the swetest run with greg
        final Map<String, Double> ref = values(new int[]{year, 4, 4}, UTC_TIME,
                "-b4.4." + year + "greg", "-p" + BODIES, "-true", "-sid27", "-fPl",
                house(GEO_LON, GEO_LAT, (char) PLACIDUS.fid()));

        final ISweObjects o = new SweObjects(getSwephExp(),
                new SweJulianDate(date(year), TIME_ZONE, LOCAL_TIME).calendar(SE_GREG_CAL),
                new SweGeoLocation(GEO_LON, GEO_LAT, 0.), sidereal(TRUE_CITRA, PLACIDUS)).completeBuild();

        for (int i = 0; i < BODY_NAMES.length; i++) {
            assertEquals(ref.get(BODY_NAMES[i]), o.longitudes()[SWETEST_TO_OBJECT[i]], DELTA,
                    year + " greg " + BODY_NAMES[i]);
        }
        assertEquals(ref.get("Ascendant"), o.longitudes()[LG], DELTA, year + " greg lagna");

        final double[] refCusps = cusps(ref);
        assertNotNull(refCusps, year + ": no cusps");
        for (int h = 1; h <= 12; h++) {
            assertEquals(refCusps[h], o.cusps()[h], DELTA, year + " greg house " + h);
        }
    }

    /**
     * The other direction: Old Style dates. Russia kept the Julian calendar until 1918, so
     * a birth date recorded there in 1900 is 13 days away from the Gregorian reading.
     */
    @ParameterizedTest(name = "{0} old style")
    @ValueSource(ints = {1700, 1800, 1900})
    void anOldStyleDateMatchesSwetestJul(int year) {
        assumeTrue(available());

        final ISweJulianDate oldStyle = getSwephExp().initJulianDate(
                new SweJulianDate(date(year), TIME_ZONE, LOCAL_TIME).calendar(SE_JUL_CAL));
        final ISweJulianDate newStyle = getSwephExp().initJulianDate(
                new SweJulianDate(date(year), TIME_ZONE, LOCAL_TIME));

        assertFalse(oldStyle.gregorianCalendar(), "forced Julian after 1582");
        assertTrue(newStyle.gregorianCalendar(), "deduced Gregorian after 1582");

        assertEquals(swetestJulianDay("-b4.4." + year + "jul"), oldStyle.julianDay(), 1e-6,
                year + " old style julian day");

        // the Julian calendar runs behind, so the same label lands on a later day:
        // 4 April 1900 Old Style is 17 April 1900 New Style
        assertTrue(oldStyle.julianDay() - newStyle.julianDay() >= 10.,
                year + ": old style is at least 10 days later, got "
                        + (oldStyle.julianDay() - newStyle.julianDay()));
    }

    /**
     * Building the date from a julian day has to keep the caller's calendar too, otherwise
     * the result would report the other one from its own date fields.
     */
    @Test
    void theCalendarOverrideSurvivesTheJulianDayToDateDirection() {
        assumeTrue(available());

        final double prolepticJd = swetestJulianDay("-b4.4.1000greg");
        final ISweJulianDate fromJd = getSwephExp().initJulianDate(
                new SweJulianDate(prolepticJd, TIME_ZONE).calendar(SE_GREG_CAL));

        assertTrue(fromJd.gregorianCalendar(), "still Gregorian");
        assertEquals(SE_GREG_CAL, fromJd.calendar());
        assertArrayEquals(new int[]{1000, 4, 4}, new int[]{fromJd.year(), fromJd.month(), fromJd.day()});

        // and without the override the very same julian day reads as a Julian date
        final ISweJulianDate deduced = getSwephExp().initJulianDate(
                new SweJulianDate(prolepticJd, TIME_ZONE));
        assertFalse(deduced.gregorianCalendar());
        assertEquals(29, deduced.day(), "4 April 1000 proleptic Gregorian is 29 March Julian");
        assertEquals(3, deduced.month());
    }

    @Test
    void withoutAnOverrideNothingChanges() {
        final SweJulianDate auto = new SweJulianDate(date(1000), TIME_ZONE, LOCAL_TIME);
        assertNull(auto.calendar(), "no override by default");
        assertFalse(auto.gregorianCalendar(), "1000 is deduced as Julian");

        assertNull(new SweJulianDate(date(2000), TIME_ZONE, LOCAL_TIME).calendar());
        assertTrue(new SweJulianDate(date(2000), TIME_ZONE, LOCAL_TIME).gregorianCalendar());
    }

    private double swetestJulianDay(String dateArg) {
        for (String line : lines(date(1000), UTC_TIME, dateArg, "-p0", "-fPl")) {
            if (line.startsWith("UT:")) {
                return Double.parseDouble(line.substring(3).trim().split("\\s+")[0]);
            }
        }
        throw new AssertionError("swetest printed no UT: line for " + dateArg);
    }
}
