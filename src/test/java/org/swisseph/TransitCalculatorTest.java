/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.junit.jupiter.api.Test;
import swisseph.SwissephException;
import swisseph.TCHouses;
import swisseph.TCPlanet;
import swisseph.TCPlanetHouse;
import swisseph.TCPlanetPlanet;
import swisseph.TransitCalculator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static swisseph.SweConst.*;

/**
 * Covers every TransitCalculator implementation: {@link TCPlanet},
 * {@link TCPlanetPlanet}, {@link TCPlanetHouse} and {@link TCHouses}.
 * <p>
 * Two kinds of check are used, because a transit search has no closed form to compare with:
 * <ul>
 * <li><b>against an independent reference.</b> The native library solves the Sun's and the
 * Moon's longitude crossings analytically in {@code swe_solcross_ut()} and
 * {@code swe_mooncross_ut()}, which owe nothing to TransitCalculator. Those are the only
 * absolute references available and the tightest checks here rest on them.</li>
 * <li><b>against the definition.</b> For everything else the transit is verified by
 * evaluating the position at the returned date and asserting it really is the requested one,
 * and by checking that the search is self-consistent - forward and backward agree, the answer
 * does not depend on where the search started, and the same query repeats.</li>
 * </ul>
 * The two implementations of {@code ISwissEph} are compared as well, since the same search
 * runs on either engine.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
public class TransitCalculatorTest extends AbstractTest {

    static final double J2000 = 2451544.5;

    /** a transit date is a julian day, so anything below this is not one */
    static final double NOT_A_JULIAN_DAY = 1e6;

    /** how exactly the search must reproduce swe_solcross_ut / swe_mooncross_ut, in days */
    static final double DELTA_REFERENCE = 1. / 86400.;          // 1 second

    /** agreement required between the two engines for a fast object, in days */
    static final double DELTA_ENGINES = 5. / 86400.;            // 5 seconds

    /** the position at the returned date must be the requested one, in degrees */
    static final double DELTA_DEGREE = 1e-5;

    static final int LON = SEFLG_SWIEPH | SEFLG_TRANSIT_LONGITUDE;
    static final int SPEED = SEFLG_SWIEPH | SEFLG_TRANSIT_LONGITUDE | SEFLG_TRANSIT_SPEED;

    // =============================================================== TCPlanet

    @Test
    void tcPlanet_rejectsInvalidFlagCombinations() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new TCPlanet(getSwephExp(), SE_MARS, SEFLG_SWIEPH, 0.),
                        "no SEFLG_TRANSIT_* type at all"),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new TCPlanet(getSwephExp(), SE_MARS,
                                LON | SEFLG_TRANSIT_LATITUDE, 0.),
                        "two types at once"),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new TCPlanet(getSwephExp(), SE_MARS, LON | SEFLG_JPLHOR, 0.),
                        "a flag that is not allowed here"),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new TCPlanet(getSwephExp(), SE_MEAN_NODE, LON | SEFLG_HELCTR, 0.),
                        "heliocentric mean node"),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new TCPlanet(null, SE_MARS, LON, 0.),
                        "no engine"));
    }

    @Test
    void tcPlanet_normalisesTheOffsetAndReportsItsRange() {
        TransitCalculator lon = new TCPlanet(getSwephExp(), SE_MARS, LON, 0.);

        assertTrue(lon.getRollover(), "longitude rolls over");
        assertEquals(360., lon.getRolloverVal());
        assertEquals(0., lon.getMinOffset());
        assertEquals(360., lon.getMaxOffset());

        lon.setOffset(370.);
        assertEquals(10., lon.getOffset(), 1e-12, "370 deg is 10 deg");
        lon.setOffset(-10.);
        assertEquals(350., lon.getOffset(), 1e-12, "-10 deg is 350 deg");
        lon.setOffset(360.);
        assertEquals(0., lon.getOffset(), 1e-12, "360 deg is 0 deg");

        TransitCalculator lat = new TCPlanet(getSwephExp(), SE_MARS,
                SEFLG_SWIEPH | SEFLG_TRANSIT_LATITUDE, 0.);
        assertFalse(lat.getRollover(), "latitude does not roll over");
    }

    @Test
    void tcPlanet_speedOffsetsAreBoundedByTheExtremeSpeeds() {
        // a speed transit can only be asked for a speed the planet actually reaches
        TransitCalculator mars = new TCPlanet(getSwephExp(), SE_MARS, SPEED, 0.);
        assertTrue(mars.getMinOffset() < 0., "Mars goes retrograde");
        assertTrue(mars.getMaxOffset() > 0., "and direct");

        TransitCalculator sun = new TCPlanet(getSwephExp(), SE_SUN, SPEED, 0.);
        assertTrue(sun.getMinOffset() > 0., "the Sun never goes retrograde");

        // zero speed is therefore outside the Sun's range and no transit exists
        assertThrows(SwissephException.class,
                () -> TransitCalculator.getTransitUT(sun, J2000, false));
    }

    @Test
    void tcPlanet_identifiesItsObjectAndKeepsThePrecisionFactor() {
        TransitCalculator tc = new TCPlanet(getSwephExp(), SE_SATURN, LON, 120.);

        assertEquals(1., tc.getPrecisionFactor(), "default precision factor");
        tc.setPrecisionFactor(100.);
        assertEquals(100., tc.getPrecisionFactor());

        Object[] ids = tc.getObjectIdentifiers();
        assertNotNull(ids);
        assertEquals(1, ids.length);
        assertEquals(SE_SATURN, ids[0]);
        assertTrue(tc.toString().contains("120"), "toString mentions the offset: " + tc);
    }

    /**
     * The strongest check in this class: the native library computes the Sun's crossing of a
     * longitude analytically, so the search can be held to it absolutely.
     */
    @Test
    void tcPlanet_sunLongitudeMatchesSweSolcross() {
        StringBuilder serr = new StringBuilder();
        for (int k = 0; k < 12; k++) {
            double deg = k * 30.;
            double expected = getSwephExp().swe_solcross_ut(deg, J2000, SEFLG_SWIEPH, serr);
            TransitCalculator tc = new TCPlanet(getSwephExp(), SE_SUN, LON, deg);
            double actual = TransitCalculator.getTransitUT(tc, J2000, false);
            assertEquals(expected, actual, DELTA_REFERENCE, "Sun over " + deg + " deg");
        }
    }

    @Test
    void tcPlanet_moonLongitudeMatchesSweMooncross() {
        StringBuilder serr = new StringBuilder();
        for (int k = 0; k < 12; k++) {
            double deg = k * 30.;
            double expected = getSwephExp().swe_mooncross_ut(deg, J2000, SEFLG_SWIEPH, serr);
            TransitCalculator tc = new TCPlanet(getSwephExp(), SE_MOON, LON, deg);
            double actual = TransitCalculator.getTransitUT(tc, J2000, false);
            assertEquals(expected, actual, DELTA_REFERENCE, "Moon over " + deg + " deg");
        }
    }

    /**
     * Before 1955 the two engines used different delta t models, which showed up here as a
     * multi-second error against swe_solcross_ut for dates in the 18th and 19th century.
     */
    @Test
    void tcPlanet_agreesWithSweSolcrossAtOldEpochsToo() {
        StringBuilder serr = new StringBuilder();
        for (double start : new double[]{2360000.5, 2396000.5, 2415020.5}) {   // 1750, 1850, 1900
            double expected = getSwephExp().swe_solcross_ut(0., start, SEFLG_SWIEPH, serr);
            TransitCalculator tc = new TCPlanet(getSwephExp(), SE_SUN, LON, 0.);
            double actual = TransitCalculator.getTransitUT(tc, start, false);
            assertEquals(expected, actual, DELTA_REFERENCE, "Sun over 0 deg from JD " + start);
        }
    }

    @Test
    void tcPlanet_theReturnedDateReallyHasTheRequestedLongitude() {
        for (int planet : new int[]{SE_SUN, SE_MOON, SE_MERCURY, SE_MARS, SE_JUPITER,
                SE_SATURN, SE_URANUS, SE_PLUTO, SE_MEAN_NODE, SE_TRUE_NODE}) {
            for (double deg : new double[]{0., 137.5, 359.9}) {
                TransitCalculator tc = new TCPlanet(getSwephExp(), planet, LON, deg);
                double jdUT = TransitCalculator.getTransitUT(tc, J2000, false);
                assertEquals(0., arcTo(deg, longitudeAt(planet, jdUT)), DELTA_DEGREE,
                        "object " + planet + " over " + deg + " deg at JD " + jdUT);
            }
        }
    }

    @Test
    void tcPlanet_backwardSearchFindsTheTransitBeforeTheStartDate() {
        for (int planet : new int[]{SE_SUN, SE_MOON, SE_MARS, SE_SATURN}) {
            TransitCalculator tc = new TCPlanet(getSwephExp(), planet, LON, 42.);
            double back = TransitCalculator.getTransitUT(tc, J2000, true);
            double forward = TransitCalculator.getTransitUT(tc, J2000, false);

            assertTrue(back <= J2000, "backward result must not be later than the start: " + back);
            assertTrue(forward >= J2000, "forward result must not be earlier: " + forward);
            assertEquals(42., longitudeAt(planet, back), DELTA_DEGREE, "backward, object " + planet);
            assertEquals(42., longitudeAt(planet, forward), DELTA_DEGREE, "forward, object " + planet);
        }
    }

    /**
     * Searching forward from a point just after the previous transit has to land on the same
     * date as searching forward from further back. A search whose answer depended on where it
     * started would make every result unreproducible.
     */
    @Test
    void tcPlanet_theAnswerDoesNotDependOnWhereTheSearchStarted() {
        TransitCalculator tc = new TCPlanet(getSwephExp(), SE_MARS, LON, 200.);
        double reference = TransitCalculator.getTransitUT(tc, J2000, false);

        for (double back : new double[]{1., 10., 50., 100.}) {
            double again = TransitCalculator.getTransitUT(tc, reference - back, false);
            assertEquals(reference, again, DELTA_ENGINES,
                    "starting " + back + " days earlier changed the answer");
        }
    }

    @Test
    void tcPlanet_bothEnginesFindTheSameTransit() {
        for (int planet : new int[]{SE_SUN, SE_MOON, SE_MERCURY, SE_MARS, SE_JUPITER, SE_SATURN}) {
            TransitCalculator nat = new TCPlanet(getSwephExp(), planet, LON, 77.);
            TransitCalculator jav = new TCPlanet(getSwissEph(), planet, LON, 77.);
            assertEquals(TransitCalculator.getTransitUT(nat, J2000, false),
                    TransitCalculator.getTransitUT(jav, J2000, false),
                    DELTA_ENGINES, "object " + planet);
        }
    }

    @Test
    void tcPlanet_latitudeAndDistanceTransitsWork() {
        // the Moon crosses the ecliptic roughly every fortnight
        TransitCalculator lat = new TCPlanet(getSwephExp(), SE_MOON,
                SEFLG_SWIEPH | SEFLG_TRANSIT_LATITUDE, 0.);
        double jdUT = TransitCalculator.getTransitUT(lat, J2000, false);
        assertTrue(jdUT > J2000 && jdUT < J2000 + 20, "a node within a fortnight: " + jdUT);
        assertEquals(0., positionAt(SE_MOON, jdUT)[1], DELTA_DEGREE, "latitude at the node");

        // and its distance passes through 0.0025 AU every month
        TransitCalculator dist = new TCPlanet(getSwephExp(), SE_MOON,
                SEFLG_SWIEPH | SEFLG_TRANSIT_DISTANCE, 0.0025);
        jdUT = TransitCalculator.getTransitUT(dist, J2000, false);
        assertEquals(0.0025, positionAt(SE_MOON, jdUT)[2], 1e-8, "distance at JD " + jdUT);
    }

    /** the speed transit over zero is a station - the class doc calls this out explicitly */
    @Test
    void tcPlanet_speedTransitOverZeroIsAStation() {
        TransitCalculator tc = new TCPlanet(getSwephExp(), SE_MERCURY, SPEED, 0.);
        double jdUT = TransitCalculator.getTransitUT(tc, J2000, false);

        assertEquals(0., positionAt(SE_MERCURY, jdUT)[3], 1e-6, "speed at the station");
        // and the speed really reverses across it
        double before = positionAt(SE_MERCURY, jdUT - 1)[3];
        double after = positionAt(SE_MERCURY, jdUT + 1)[3];
        assertTrue(before * after < 0, "speed changes sign: " + before + " -> " + after);
    }

    // ========================================================= TCPlanetPlanet

    @Test
    void tcPlanetPlanet_rejectsInvalidArguments() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new TCPlanetPlanet(getSwephExp(), SE_MARS, SE_MARS, LON, 0.),
                        "a planet cannot aspect itself"),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new TCPlanetPlanet(getSwephExp(), SE_MARS, SE_VENUS, SEFLG_SWIEPH, 0.),
                        "no SEFLG_TRANSIT_* type"),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new TCPlanetPlanet(getSwephExp(), SE_MARS, SE_VENUS,
                                LON | SEFLG_JPLHOR, 0.),
                        "a flag that is not allowed here"));
    }

    @Test
    void tcPlanetPlanet_findsAnAspectAndTheAngleReallyHolds() {
        for (double aspect : new double[]{0., 60., 90., 120., 180.}) {
            TransitCalculator tc = new TCPlanetPlanet(getSwephExp(), SE_MOON, SE_SUN, LON, aspect);
            double jdUT = TransitCalculator.getTransitUT(tc, J2000, false);

            double d = longitudeAt(SE_MOON, jdUT) - longitudeAt(SE_SUN, jdUT);
            d = ((d % 360.) + 360.) % 360.;
            double separation = Math.min(d, 360. - d);
            assertEquals(Math.min(aspect, 360. - aspect), separation, DELTA_DEGREE,
                    "Moon-Sun " + aspect + " deg at JD " + jdUT);
        }
    }

    /**
     * A Moon-Sun conjunction is a new moon, so the result can be checked against the
     * synodic month rather than against the search itself.
     */
    @Test
    void tcPlanetPlanet_conjunctionsOfTheMoonAndSunAreNewMoonsOneSynodicMonthApart() {
        TransitCalculator tc = new TCPlanetPlanet(getSwephExp(), SE_MOON, SE_SUN, LON, 0.);

        double previous = 0;
        for (int i = 0; i < 6; i++) {
            double jdUT = TransitCalculator.getTransitUT(tc, (i == 0 ? J2000 : previous + 1), false);
            assertEquals(0., angularSeparation(SE_MOON, SE_SUN, jdUT), DELTA_DEGREE,
                    "not a conjunction at JD " + jdUT);
            if (i > 0) {
                assertEquals(29.53, jdUT - previous, 1.,
                        "consecutive new moons are a synodic month apart");
            }
            previous = jdUT;
        }
    }

    /**
     * Pins an inconsistency rather than a requirement: {@code getObjectIdentifiers()} returns
     * {@code Integer[]} from TCPlanet but {@code String[]} from TCPlanetPlanet and
     * TCPlanetHouse. Callers that switch between the calculators have to cope with both, so
     * the difference is recorded here instead of being silently changed.
     */
    @Test
    void tcPlanetPlanet_identifiesBothObjectsAsStrings() {
        Object[] ids = new TCPlanetPlanet(getSwephExp(), SE_MARS, SE_JUPITER, LON, 90.)
                .getObjectIdentifiers();
        assertNotNull(ids);
        assertEquals(2, ids.length);
        assertEquals(String.valueOf(SE_MARS), ids[0]);
        assertEquals(String.valueOf(SE_JUPITER), ids[1]);

        Object[] planetIds = new TCPlanet(getSwephExp(), SE_MARS, LON, 0.).getObjectIdentifiers();
        assertEquals(SE_MARS, planetIds[0], "TCPlanet uses Integer, unlike the two above");
    }

    @Test
    void tcPlanetPlanet_bothEnginesFindTheSameAspect() {
        TransitCalculator nat = new TCPlanetPlanet(getSwephExp(), SE_MARS, SE_JUPITER, LON, 90.);
        TransitCalculator jav = new TCPlanetPlanet(getSwissEph(), SE_MARS, SE_JUPITER, LON, 90.);
        assertEquals(TransitCalculator.getTransitUT(nat, J2000, false),
                TransitCalculator.getTransitUT(jav, J2000, false), DELTA_ENGINES);
    }

    /**
     * Regression: {@code Extensions.getTransit()} returned {@code val} - a longitude
     * difference - instead of the julian day whenever the stop condition was already satisfied
     * at the start date. With {@code SEFLG_PARTILE_TRANSIT_END} that is the normal case, so
     * getTransitUT() handed back values such as 88.4 and 178.4 as if they were dates.
     */
    @Test
    void tcPlanetPlanet_partileTransitReturnsAJulianDayNotALongitude() {
        for (int mode : new int[]{SEFLG_PARTILE_TRANSIT_START, SEFLG_PARTILE_TRANSIT_END}) {
            for (double aspect : new double[]{0., 90., 180.}) {
                TransitCalculator tc = new TCPlanetPlanet(getSwephExp(), SE_MOON, SE_SUN,
                        LON | mode, aspect);
                double jdUT = TransitCalculator.getTransitUT(tc, J2000, false);
                assertTrue(jdUT > NOT_A_JULIAN_DAY,
                        "mode " + mode + ", aspect " + aspect + ": got " + jdUT);
                assertTrue(jdUT >= J2000, "and it must not be before the start date: " + jdUT);
            }
        }
    }

    // ========================================================== TCPlanetHouse

    @Test
    void tcPlanetHouse_findsThePlanetOnTheAscendant() {
        TransitCalculator tc = new TCPlanetHouse(getSwephExp(), SE_MOON, LON,
                SE_ASC, SE_HSYS_PLACIDUS, LON,
                GEO_GREENWICH.longitude(), GEO_GREENWICH.latitude(), 0.);
        double jdUT = TransitCalculator.getTransitUT(tc, J2000, false);

        assertTrue(jdUT > J2000 && jdUT < J2000 + 2, "the Moon meets the ascendant daily: " + jdUT);
        assertEquals(0., Math.abs(longitudeAt(SE_MOON, jdUT) - ascendantAt(jdUT)) % 360., 1e-3,
                "the Moon is on the ascendant at JD " + jdUT);
    }

    @Test
    void tcPlanetHouse_rejectsInvalidArguments() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new TCPlanetHouse(getSwephExp(), SE_MOON, LON, SE_ASC,
                                'Z', LON, 0., 51.5, 0.),
                        "unknown house system"),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new TCPlanetHouse(getSwephExp(), SE_MOON, LON, 4242,
                                SE_HSYS_PLACIDUS, LON, 0., 51.5, 0.),
                        "not a house object"),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new TCPlanetHouse(getSwephExp(), SE_MOON, LON | SEFLG_JPLHOR,
                                SE_ASC, SE_HSYS_PLACIDUS, LON, 0., 51.5, 0.),
                        "a planet flag that is not allowed here"));
    }

    /**
     * Regression: this class sets {@code rollover = true} but used to store the offset
     * verbatim, and an offset outside [0..360) made the search loop forever instead of
     * answering - {@code above = (val >= 370)} can never become true.
     */
    @Test
    void tcPlanetHouse_normalisesTheOffsetAndIdentifiesItsObjects() {
        TransitCalculator tc = new TCPlanetHouse(getSwephExp(), SE_MARS, LON,
                SE_MC, SE_HSYS_KOCH, LON, 0., 51.5, 370.);

        assertTrue(tc.getRollover());
        assertEquals(10., tc.getOffset(), 1e-12, "370 deg is 10 deg");

        tc.setOffset(-10.);
        assertEquals(350., tc.getOffset(), 1e-12, "-10 deg is 350 deg");

        Object[] ids = tc.getObjectIdentifiers();
        assertNotNull(ids);
        assertEquals(3, ids.length);
        assertEquals(String.valueOf(SE_MARS), ids[0]);
    }

    /** the whole point of the fix above: an un-normalised offset must still terminate */
    @Test
    void tcPlanetHouse_anOffsetBeyond360FindsTheSameDateAsItsNormalisedForm() {
        double normalised = transitOfPlanetHouse(10.);
        assertEquals(normalised, transitOfPlanetHouse(370.), 1e-9, "370 deg");
        assertEquals(normalised, transitOfPlanetHouse(-350.), 1e-9, "-350 deg");
    }

    private double transitOfPlanetHouse(double offset) {
        TransitCalculator tc = new TCPlanetHouse(getSwephExp(), SE_MARS, LON,
                SE_ASC, SE_HSYS_PLACIDUS, LON,
                GEO_GREENWICH.longitude(), GEO_GREENWICH.latitude(), offset);
        return TransitCalculator.getTransitUT(tc, J2000, false);
    }

    // =============================================================== TCHouses

    @Test
    void tcHouses_ascendantPassesEveryDegreeEveryDay() {
        TransitCalculator tc = new TCHouses(getSwephExp(), SE_ASC, SE_HSYS_PLACIDUS,
                GEO_GREENWICH.longitude(), GEO_GREENWICH.latitude(), SEFLG_TRANSIT_LONGITUDE, 100.);
        double jdUT = TransitCalculator.getTransitUT(tc, J2000, false);

        assertTrue(jdUT > J2000 && jdUT < J2000 + 1.05,
                "the ascendant covers the whole circle in a day: " + jdUT);
        assertEquals(100., ascendantAt(jdUT), 1e-3, "ascendant at JD " + jdUT);
    }

    @Test
    void tcHouses_reportsAndUpdatesItsGeographicPosition() {
        TCHouses tc = new TCHouses(getSwephExp(), SE_MC, SE_HSYS_PLACIDUS, 13.4, 52.5,
                SEFLG_TRANSIT_LONGITUDE, 0.);

        assertEquals(13.4, tc.getLongitude(), 1e-12);
        assertEquals(52.5, tc.getLatitude(), 1e-12);
        assertTrue(tc.getRollover());

        tc.setGeopos(0., 51.5);
        assertEquals(0., tc.getLongitude(), 1e-12);
        assertEquals(51.5, tc.getLatitude(), 1e-12);
    }

    @Test
    void tcHouses_rejectsInvalidArguments() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new TCHouses(getSwephExp(), SE_ASC, 'Z', 0., 51.5,
                                SEFLG_TRANSIT_LONGITUDE, 0.),
                        "unknown house system"),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new TCHouses(getSwephExp(), 4242, SE_HSYS_PLACIDUS,
                                0., 51.5, SEFLG_TRANSIT_LONGITUDE, 0.),
                        "not a house object"),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new TCHouses(getSwephExp(), SE_ASC, SE_HSYS_PLACIDUS, 0., 51.5,
                                SEFLG_TRANSIT_LONGITUDE | SEFLG_TRANSIT_SPEED, 0.),
                        "TCHouses has no speed transits"));
    }

    @Test
    void tcHouses_bothEnginesAgreeOnTheAscendant() {
        TransitCalculator nat = new TCHouses(getSwephExp(), SE_ASC, SE_HSYS_PLACIDUS,
                0., 51.5, SEFLG_TRANSIT_LONGITUDE, 250.);
        TransitCalculator jav = new TCHouses(getSwissEph(), SE_ASC, SE_HSYS_PLACIDUS,
                0., 51.5, SEFLG_TRANSIT_LONGITUDE, 250.);
        assertEquals(TransitCalculator.getTransitUT(nat, J2000, false),
                TransitCalculator.getTransitUT(jav, J2000, false), DELTA_ENGINES);
    }

    // ================================================================ helpers

    private double[] positionAt(int planet, double jdUT) {
        double[] xx = new double[6];
        StringBuilder serr = new StringBuilder();
        double jdET = jdUT + getSwephExp().swe_deltat(jdUT);
        int ret = getSwephExp().swe_calc(jdET, planet, SEFLG_SWIEPH | SEFLG_SPEED, xx, serr);
        assertTrue(ret >= 0, "swe_calc failed: " + serr);
        return xx;
    }

    private double longitudeAt(int planet, double jdUT) {
        return positionAt(planet, jdUT)[0];
    }

    /** shortest arc between two longitudes - 0 and 359.999999 are the same point */
    private static double arcTo(double a, double b) {
        double d = ((a - b) % 360. + 360.) % 360.;
        return Math.min(d, 360. - d);
    }

    private double angularSeparation(int p1, int p2, double jdUT) {
        double d = ((longitudeAt(p1, jdUT) - longitudeAt(p2, jdUT)) % 360. + 360.) % 360.;
        return Math.min(d, 360. - d);
    }

    private double ascendantAt(double jdUT) {
        double[] cusps = new double[13];
        double[] ascmc = new double[10];
        getSwephExp().swe_houses(jdUT, GEO_GREENWICH.latitude(), GEO_GREENWICH.longitude(),
                SE_HSYS_PLACIDUS, cusps, ascmc);
        return ascmc[0];
    }
}
