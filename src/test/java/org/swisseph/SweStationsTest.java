/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.junit.jupiter.api.Test;
import org.swisseph.api.ISweStation;
import org.swisseph.app.SweStations;
import swisseph.SweDate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static swisseph.SweConst.*;

/**
 * {@link SweStations} - the dates a planet turns retrograde or direct.
 * <p>
 * The published retrograde periods of Mercury and Mars are used as the external reference:
 * they are widely tabulated to the day, so an error of the kind that matters here - the wrong
 * date, or the two kinds of station swapped - could not survive them. Everything else is
 * checked against the definition: the speed is zero at the returned date and has opposite
 * signs on either side of it.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
public class SweStationsTest extends AbstractTest {

    static final double Y2000 = 2451544.5;
    static final double Y2001 = 2451910.5;


    /**
     * How closely the two engines have to agree. Not a precision claim: near a station of a
     * slow planet the speed crawls through zero, so the instant itself is only determined to
     * within minutes by the accuracy of the speed. Fast objects are held much tighter below.
     */
    static final double DELTA_ENGINES = 60. / 86400.;           // 1 minute

    private SweStations stations() {
        return new SweStations(getSwephExp());
    }

    private static double jd(int y, int m, int d) {
        return new SweDate(y, m, d, 0.).getJulDay();
    }

    /** the calendar day a julian day falls on, as yyyymmdd - published tables give the day */
    private static int day(double jdUT) {
        SweDate d = new SweDate(jdUT);
        return d.getYear() * 10000 + d.getMonth() * 100 + d.getDay();
    }

    // ============================================================== which objects

    @Test
    void objectsThatCannotReverseHaveNoStations() {
        SweStations st = stations();
        assertAll(
                () -> assertFalse(st.hasStations(SE_SUN), "the Sun never turns"),
                () -> assertFalse(st.hasStations(SE_MOON), "nor the Moon"),
                () -> assertFalse(st.hasStations(SE_MEAN_NODE), "the mean node is always retrograde"),
                () -> assertFalse(st.hasStations(SE_MEAN_APOG), "nor does the mean apogee turn"));
    }

    @Test
    void everyPlanetFromMercuryOutwardsHasStations() {
        SweStations st = stations();
        for (int planet : new int[]{SE_MERCURY, SE_VENUS, SE_MARS, SE_JUPITER, SE_SATURN,
                SE_URANUS, SE_NEPTUNE, SE_PLUTO, SE_TRUE_NODE, SE_CHIRON}) {
            assertTrue(st.hasStations(planet), "object " + planet);
        }
    }

    @Test
    void anObjectWithoutStationsAnswersEmptyRatherThanThrowing() {
        SweStations st = stations();
        assertNull(st.next(SE_SUN, Y2000));
        assertNull(st.previous(SE_SUN, Y2000));
        assertTrue(st.between(SE_SUN, Y2000, Y2001).isEmpty());
    }

    @Test
    void theEngineIsRequired() {
        assertThrows(IllegalArgumentException.class, () -> new SweStations(null));
    }

    // ================================================== against published tables

    /**
     * Mercury was retrograde three times in 2000: 21 Feb - 14 Mar, 23 Jun - 17 Jul and
     * 18 Oct - 8 Nov. Those dates come from published ephemerides, not from this library.
     */
    @Test
    void mercuryRetrogradePeriodsOf2000MatchThePublishedDates() {
        List<ISweStation> found = stations().between(SE_MERCURY, Y2000, Y2001);
        assertEquals(6, found.size(), "three retrograde periods means six stations");

        int[] expected = {20000221, 20000314, 20000623, 20000717, 20001018, 20001108};
        boolean[] retrograde = {true, false, true, false, true, false};

        for (int i = 0; i < expected.length; i++) {
            ISweStation s = found.get(i);
            assertEquals(expected[i], day(s.julianDate().julianDay()), "station " + i);
            assertEquals(retrograde[i], s.retrograde(), "kind of station " + i);
        }
    }

    /**
     * Mars turns far less often, and each retrograde period is well known: 11 May - 19 Jul
     * 2001, 29 Jul - 27 Sep 2003, 1 Oct - 10 Dec 2005.
     */
    @Test
    void marsRetrogradePeriodsMatchThePublishedDates() {
        List<ISweStation> found = stations().between(SE_MARS, Y2000, jd(2006, 1, 1));
        assertEquals(6, found.size(), "three retrograde periods between 2000 and 2006");

        int[] expected = {20010511, 20010719, 20030729, 20030927, 20051001, 20051210};
        boolean[] retrograde = {true, false, true, false, true, false};
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], day(found.get(i).julianDate().julianDay()),
                    "Mars station " + i);
            assertEquals(retrograde[i], found.get(i).retrograde(), "kind of Mars station " + i);
        }
    }

    // ================================================== against the definition

    @Test
    void theSpeedIsZeroAtEveryStationAndReversesAcrossIt() {
        SweStations st = stations();
        for (int planet : new int[]{SE_MERCURY, SE_VENUS, SE_MARS, SE_JUPITER, SE_SATURN,
                SE_URANUS, SE_NEPTUNE, SE_PLUTO}) {
            ISweStation s = st.next(planet, Y2000);
            assertNotNull(s, "object " + planet);
            double jdUT = s.julianDate().julianDay();

            assertEquals(0., speedAt(planet, jdUT), 1e-5, "speed at the station, object " + planet);

            double before = speedAt(planet, jdUT - 1.);
            double after = speedAt(planet, jdUT + 1.);
            assertTrue(before * after < 0.,
                    "object " + planet + ": speed must change sign, " + before + " -> " + after);
            assertEquals(after < 0., s.retrograde(),
                    "object " + planet + ": retrograde() must say which way it turned");
        }
    }

    @Test
    void theReportedLongitudeIsThePlanetsLongitudeThere() {
        SweStations st = stations();
        for (int planet : new int[]{SE_MERCURY, SE_MARS, SE_SATURN, SE_PLUTO}) {
            ISweStation s = st.next(planet, Y2000);
            assertEquals(longitudeAt(planet, s.julianDate().julianDay()), s.longitude(), 1e-9,
                    "object " + planet);
        }
    }

    @Test
    void stationsAlternateBetweenRetrogradeAndDirect() {
        SweStations st = stations();
        for (int planet : new int[]{SE_MERCURY, SE_VENUS, SE_MARS, SE_JUPITER, SE_SATURN,
                SE_URANUS, SE_NEPTUNE, SE_PLUTO, SE_TRUE_NODE}) {
            List<ISweStation> found = st.between(planet, Y2000, Y2000 + 3000);
            assertTrue(found.size() > 2, "object " + planet + " produced " + found.size());

            for (int i = 1; i < found.size(); i++) {
                assertFalse(found.get(i).retrograde() == found.get(i - 1).retrograde(),
                        "object " + planet + ": two " + (found.get(i).retrograde()
                                ? "retrograde" : "direct") + " stations in a row at index " + i);
            }
        }
    }

    @Test
    void betweenReturnsStationsInOrderAndInsideTheRange() {
        double from = Y2000, to = Y2000 + 4000;
        for (int planet : new int[]{SE_MERCURY, SE_MARS, SE_SATURN, SE_TRUE_NODE}) {
            List<ISweStation> found = stations().between(planet, from, to);
            double previous = from;
            for (ISweStation s : found) {
                double jdUT = s.julianDate().julianDay();
                assertTrue(jdUT >= from && jdUT <= to, "outside the range: " + jdUT);
                assertTrue(jdUT > previous, "not in order at " + jdUT);
                previous = jdUT;
            }
        }
    }

    @Test
    void aReversedRangeGivesTheSameStations() {
        List<ISweStation> forward = stations().between(SE_MARS, Y2000, Y2000 + 2000);
        List<ISweStation> reversed = stations().between(SE_MARS, Y2000 + 2000, Y2000);

        assertEquals(forward.size(), reversed.size());
        for (int i = 0; i < forward.size(); i++) {
            assertEquals(forward.get(i).julianDate().julianDay(),
                    reversed.get(i).julianDate().julianDay(), 1e-9, "station " + i);
        }
    }

    /**
     * The true node is the awkward case: it is retrograde most of the time and turns direct
     * for only a few days, so its stations come in closely spaced pairs. A search that stepped
     * forward by a fixed week would step straight over them.
     */
    @Test
    void theTrueNodeStationsComeInCloselySpacedPairs() {
        List<ISweStation> found = stations().between(SE_TRUE_NODE, Y2000, Y2000 + 200);
        assertTrue(found.size() > 20, "expected many stations in 200 days, got " + found.size());

        double shortest = Double.MAX_VALUE;
        for (int i = 1; i < found.size(); i++) {
            shortest = Math.min(shortest, found.get(i).julianDate().julianDay()
                    - found.get(i - 1).julianDate().julianDay());
        }
        assertTrue(shortest < 3., "the closest pair should be days apart, was " + shortest);
        assertTrue(shortest > 0., "and still strictly increasing");
    }

    // ============================================================== consistency

    @Test
    void nextAndPreviousAreConsistent() {
        SweStations st = stations();
        for (int planet : new int[]{SE_MERCURY, SE_MARS, SE_JUPITER, SE_SATURN}) {
            ISweStation next = st.next(planet, Y2000);
            assertTrue(next.julianDate().julianDay() >= Y2000, "next must not be in the past");

            ISweStation previous = st.previous(planet, Y2000);
            assertTrue(previous.julianDate().julianDay() <= Y2000, "previous must not be later");

            // stepping forward from just after the previous station returns to the next one
            ISweStation again = st.next(planet, previous.julianDate().julianDay() + 1.);
            assertEquals(next.julianDate().julianDay(), again.julianDate().julianDay(),
                    DELTA_ENGINES, "object " + planet + " round trip");
            assertEquals(next.retrograde(), again.retrograde());
        }
    }

    @Test
    void theSameQueryGivesTheSameAnswer() {
        SweStations st = stations();
        ISweStation first = st.next(SE_SATURN, Y2000);
        for (int i = 0; i < 3; i++) {
            ISweStation again = st.next(SE_SATURN, Y2000);
            assertEquals(first.julianDate().julianDay(), again.julianDate().julianDay(), 1e-9);
        }
    }

    @Test
    void bothEnginesFindTheSameStations() {
        SweStations nat = new SweStations(getSwephExp());
        SweStations jav = new SweStations(getSwissEph());

        for (int planet : new int[]{SE_MERCURY, SE_VENUS, SE_MARS, SE_JUPITER, SE_SATURN,
                SE_URANUS, SE_NEPTUNE, SE_PLUTO}) {
            List<ISweStation> a = nat.between(planet, Y2000, Y2000 + 4000);
            List<ISweStation> b = jav.between(planet, Y2000, Y2000 + 4000);

            assertEquals(a.size(), b.size(), "object " + planet + ": different number of stations");
            for (int i = 0; i < a.size(); i++) {
                assertEquals(a.get(i).retrograde(), b.get(i).retrograde(),
                        "object " + planet + " station " + i + ": different kind");
                assertEquals(a.get(i).julianDate().julianDay(), b.get(i).julianDate().julianDay(),
                        DELTA_ENGINES, "object " + planet + " station " + i);
            }
        }
    }

    /** the inner planets are fast enough that the two engines should agree far more tightly */
    @Test
    void bothEnginesAgreeOnMercuryToWithinASecond() {
        List<ISweStation> a = new SweStations(getSwephExp()).between(SE_MERCURY, Y2000, Y2000 + 2000);
        List<ISweStation> b = new SweStations(getSwissEph()).between(SE_MERCURY, Y2000, Y2000 + 2000);

        assertEquals(a.size(), b.size());
        for (int i = 0; i < a.size(); i++) {
            assertEquals(a.get(i).julianDate().julianDay(), b.get(i).julianDate().julianDay(),
                    1. / 86400., "Mercury station " + i);
        }
    }

    @Test
    void toStringSaysWhichKindOfStationItIs() {
        ISweStation retro = null, direct = null;
        for (ISweStation s : stations().between(SE_MERCURY, Y2000, Y2001)) {
            if (s.retrograde()) retro = s; else direct = s;
        }
        assertNotNull(retro);
        assertNotNull(direct);
        assertTrue(retro.toString().contains("retrograde"), retro.toString());
        assertTrue(direct.toString().contains("direct"), direct.toString());
        assertEquals(SE_MERCURY, retro.planet());
    }

    // ================================================================== helpers

    private double[] positionAt(int planet, double jdUT) {
        double[] xx = new double[6];
        StringBuilder serr = new StringBuilder();
        double jdET = jdUT + getSwephExp().swe_deltat(jdUT);
        assertTrue(getSwephExp().swe_calc(jdET, planet, SEFLG_SWIEPH | SEFLG_SPEED, xx, serr) >= 0,
                "swe_calc failed: " + serr);
        return xx;
    }

    private double speedAt(int planet, double jdUT) {
        return positionAt(planet, jdUT)[3];
    }

    private double longitudeAt(int planet, double jdUT) {
        return positionAt(planet, jdUT)[0];
    }
}
