/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.swisseph.api.ISweObjects;
import org.swisseph.app.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.swisseph.api.ISweObjects.*;
import static org.swisseph.app.SweAyanamsa.LAHIRI;
import static org.swisseph.app.SweHouseSystem.*;

/**
 * {@link ISweObjects#calculatePlanetHousePosition(int)} and the house numbers derived
 * from it.
 * <p>
 * The invariant checked everywhere here is the one the old hand-rolled cusp scan was
 * trying to express: an object assigned to house <i>h</i> must lie between cusp
 * <i>h</i> and cusp <i>h+1</i> going forward around the circle. Written with
 * <code>swe_difdegn()</code> it is wrap safe, which the old scan was not - it guessed the
 * wrap point by looking for a cusp below 30 degrees and gave up if the first cusp was
 * already that small.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
public class PlanetHouseTest extends AbstractTest {

    static final JhdChart JHD = JhdChart.read("IuriiK.jhd");

    private ISweObjects chart(SweHouseSystem hsys, double geolon, double geolat, int year) {
        return new SweObjects(getSwephExp(),
                new SweJulianDate(new int[]{year, 4, 18, 23, 21}, 3f, 23.35),
                new SweGeoLocation(geolon, geolat, 0),
                new SweObjectsOptions.Builder().ayanamsa(LAHIRI).houseSystem(hsys).build())
                .completeBuild();
    }

    /**
     * @return distance from a to b, normalized to [0, 360)
     */
    private static double arc(double from, double to) {
        return getSwephExp().swe_difdegn(to, from);
    }

    /**
     * Checked from SY, not from LG: {@link SweObjects#buildAscendant()} assigns the
     * ascendant to house 1 by fiat. That holds for every system whose first cusp is the
     * ascendant, but Meridian, Horizontal and Morinus start house 1 somewhere else (the
     * equatorial ascendant, the north point, ...), and there the ascendant genuinely
     * falls in another house. That is a pre-existing property of buildAscendant(), not of
     * the house position calculation.
     */
    private static void assertObjectsSitBetweenTheirCusps(ISweObjects o, String where) {
        for (int i = SY; i <= PL; i++) {
            final int house = o.houses()[i];
            assertTrue(house >= 1 && house <= 12, where + " object " + i + " house=" + house);

            final double cuspFrom = o.cusps()[house];
            final double cuspTo = o.cusps()[house % 12 + 1];
            final double intoHouse = arc(cuspFrom, o.longitudes()[i]);
            final double houseSize = arc(cuspFrom, cuspTo);

            assertTrue(intoHouse < houseSize || houseSize == 0.,
                    where + " object " + i + " at " + o.longitudes()[i] + " is " + intoHouse
                            + " deg into house " + house + " which is only " + houseSize + " deg wide"
                            + " (cusps " + cuspFrom + " .. " + cuspTo + ")");
        }
    }

    @ParameterizedTest
    @EnumSource(SweHouseSystem.class)
    void objectsSitBetweenTheirCuspsForEveryHouseSystem(SweHouseSystem hsys) {
        if (NIL == hsys) return;
        assertObjectsSitBetweenTheirCusps(
                chart(hsys, JHD.longitude(), JHD.latitude(), 1976), hsys.name());
    }

    /**
     * High latitudes are where the cusps become wildly unequal and the old wrap guess
     * broke. Placidus and Koch fail outright above the polar circle, so a chart that
     * cannot be built is skipped rather than asserted on.
     */
    @ParameterizedTest
    @EnumSource(SweHouseSystem.class)
    void objectsSitBetweenTheirCuspsAtHighLatitudes(SweHouseSystem hsys) {
        if (NIL == hsys) return;
        // see houseNumberingRunsBackwardsForHorizontalHousesNearThePole
        if (HORIZONTAL == hsys) return;
        for (double lat : new double[]{-66, -60, -45, 45, 60, 66}) {
            for (int year = 1970; year <= 1990; year += 5) {
                final ISweObjects o;
                try {
                    o = chart(hsys, 0., lat, year);
                } catch (SweRuntimeException tooCloseToThePole) {
                    continue;
                }
                assertObjectsSitBetweenTheirCusps(o, hsys.name() + " lat=" + lat + " year=" + year);
            }
        }
    }

    /**
     * The exact cusp layout the previous implementation got wrong: Placidus at latitude
     * -66, ARMC 300. The first cusp is 16.50, so the old scan hit its
     * "if (i == 1) break" and never unwrapped, even though cusp 12 has wrapped past 360
     * back to 0.00. Everything from cusp 11 up to 360 was then reported as house 12
     * instead of house 11 - a whole house wide, 48 of 720 sampled longitudes.
     */
    @Test
    void housePositionIsCorrectWhereTheOldCuspScanWrapped() {
        final double armc = 300., geolat = -66., eps = 23.44;
        final double[] cusps = new double[14], ascmc = new double[10];
        assertNotEquals(-1, getSwephExp().swe_houses_armc(armc, geolat, eps, PLACIDUS.fid(), cusps, ascmc));

        // the layout that trips the "first cusp <= 30" shortcut
        assertTrue(cusps[1] <= 30., "cusp 1 = " + cusps[1]);
        assertTrue(cusps[12] < cusps[11], "cusps " + cusps[11] + " .. " + cusps[12] + " must wrap");

        final StringBuilder serr = new StringBuilder();
        for (double longitude = 336.; longitude < 360.; longitude += 0.5) {
            final double[] xpin = {longitude, 0.};
            final double hpos = getSwephExp().swe_house_pos(armc, geolat, eps, PLACIDUS.fid(), xpin, serr);
            assertEquals(11, (int) hpos, "longitude " + longitude + " lies between cusp 11 ("
                    + cusps[11] + ") and cusp 12 (" + cusps[12] + " + 360)");
        }
    }

    /**
     * The strongest argument for delegating to <code>swe_house_pos()</code>: for the
     * horizontal (azimuthal) system the houses are cut along azimuth, and near the polar
     * circle the ecliptic runs through those azimuths in the opposite direction. The
     * house position then <b>decreases</b> as ecliptic longitude increases, so "find the
     * cusp interval that contains this longitude" - what this class did before - has no
     * correct answer to give. At mid latitude the same system is well behaved.
     */
    @Test
    void houseNumberingRunsBackwardsForHorizontalHousesNearThePole() {
        assertEquals(1, descentsOverTheCircle(HORIZONTAL, 49.75), "mid latitude: only the 12 -> 1 wrap");
        assertTrue(descentsOverTheCircle(HORIZONTAL, -66.) > 1000, "near the pole: runs backwards");

        for (SweHouseSystem hsys : new SweHouseSystem[]{PLACIDUS, KOCH, CAMPANUS, EQUAL, MERIDIAN, MORINUS}) {
            assertEquals(1, descentsOverTheCircle(hsys, -66.), hsys + " stays monotone");
        }
    }

    /**
     * @return how many times the house position drops as the longitude is walked forward
     * around the circle; exactly one for a system that partitions the ecliptic in order
     */
    private int descentsOverTheCircle(SweHouseSystem hsys, double geolat) {
        final StringBuilder serr = new StringBuilder();
        double previous = -1.;
        int descents = 0;

        for (double longitude = 0.; longitude < 360.; longitude += 0.25) {
            final double[] xpin = {longitude, 0.};
            final double position = getSwephExp().swe_house_pos(300., geolat, 23.44, hsys.fid(), xpin, serr);
            if (previous >= 0. && position < previous - 1e-9) descents++;
            previous = position;
        }

        return descents;
    }

    // ------------------------------------------------------------- whole sign

    @Test
    void wholeSignHouseIsTheDistanceInSignsFromTheAscendant() {
        final ISweObjects o = chart(WHOLE_SIGN, JHD.longitude(), JHD.latitude(), 1976);
        for (int i = LG; i <= PL; i++) {
            final int expected = ((o.signs()[i] - o.signs()[LG] + 12) % 12) + 1;
            assertEquals(expected, o.houses()[i], "object " + i);
        }
        assertEquals(1, o.houses()[LG]);
    }

    /**
     * Whole sign is the one system that cannot go through swe_house_pos(): its sidereal
     * cusps are snapped to sign boundaries after the ayanamsa is subtracted, so they are
     * not the tropical cusps shifted. The cusps must therefore be exact multiples of 30.
     */
    @Test
    void wholeSignCuspsAreSignBoundaries() {
        final ISweObjects o = chart(WHOLE_SIGN, JHD.longitude(), JHD.latitude(), 1976);
        for (int h = 1; h <= 12; h++) {
            assertEquals(0., o.cusps()[h] % 30., 1e-9, "cusp " + h + " = " + o.cusps()[h]);
        }
    }

    // ------------------------------------------------------- position vs house

    @Test
    void houseIsTheIntegerPartOfTheHousePosition() {
        for (SweHouseSystem hsys : new SweHouseSystem[]{PLACIDUS, KOCH, CAMPANUS, EQUAL, WHOLE_SIGN}) {
            final ISweObjects o = chart(hsys, JHD.longitude(), JHD.latitude(), 1976);
            for (int i = LG; i <= PL; i++) {
                final double position = o.calculatePlanetHousePosition(i);
                assertTrue(position >= 1. && position < 13., hsys + " object " + i + " = " + position);
                assertEquals(o.houses()[i], (int) position, hsys + " object " + i);
            }
        }
    }

    @Test
    void theAscendantSitsExactlyOnTheFirstCusp() {
        for (SweHouseSystem hsys : new SweHouseSystem[]{PLACIDUS, KOCH, CAMPANUS, EQUAL}) {
            final ISweObjects o = chart(hsys, JHD.longitude(), JHD.latitude(), 1976);
            assertEquals(1., o.calculatePlanetHousePosition(LG), 1e-3, hsys.name());
        }
    }

    /**
     * Meridian, Horizontal and Morinus do not start house 1 at the ascendant, so the
     * house 1 that buildAscendant() records for LG disagrees with where the ascendant
     * actually falls. Documented here rather than changed - which of the two the library
     * should report is a semantic decision, not a bug in the house position.
     */
    @Test
    void ascendantIsNotOnTheFirstCuspOfMeridianHorizontalMorinus() {
        for (SweHouseSystem hsys : new SweHouseSystem[]{MERIDIAN, HORIZONTAL, MORINUS}) {
            final ISweObjects o = chart(hsys, JHD.longitude(), JHD.latitude(), 1976);
            assertEquals(1, o.houses()[LG], hsys + ": buildAscendant() always records house 1");
            assertNotEquals(1, (int) o.calculatePlanetHousePosition(LG),
                    hsys + ": but the ascendant is not inside house 1 there");
        }
    }

    @Test
    void obliquityIsTheTrueOneAndIsCached() {
        final ISweObjects o = chart(PLACIDUS, JHD.longitude(), JHD.latitude(), 1976);
        // swetest for this date prints Epsilon (t/m) 23°26'26.0489 23°26'32.5079
        assertEquals(23 + 26 / 60. + 26.0489 / 3600., o.trueObliquity(), 1e-7);
        assertEquals(o.trueObliquity(), o.trueObliquity());
    }
}
