/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-09
 */
package org.swisseph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.swisseph.api.ISweObjects;
import org.swisseph.api.ISweObjectsOptions;
import org.swisseph.app.SweAyanamsa;
import org.swisseph.app.SweGeoLocation;
import org.swisseph.app.SweJulianDate;
import org.swisseph.app.SweObjects;
import org.swisseph.app.SweObjectsOptions;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.swisseph.Swetest.available;
import static org.swisseph.Swetest.house;
import static org.swisseph.Swetest.values;
import static org.swisseph.api.ISweJulianDate.UT_TMZ;
import static org.swisseph.api.ISweObjects.BU;
import static org.swisseph.api.ISweObjects.CH;
import static org.swisseph.api.ISweObjects.FIRST_OBJECT_ID;
import static org.swisseph.api.ISweObjects.GU;
import static org.swisseph.api.ISweObjects.KE;
import static org.swisseph.api.ISweObjects.LAST_OBJECT_ID;
import static org.swisseph.api.ISweObjects.LG;
import static org.swisseph.api.ISweObjects.MA;
import static org.swisseph.api.ISweObjects.NE;
import static org.swisseph.api.ISweObjects.OBJECTS_COUNT;
import static org.swisseph.api.ISweObjects.PL;
import static org.swisseph.api.ISweObjects.RA;
import static org.swisseph.api.ISweObjects.SA;
import static org.swisseph.api.ISweObjects.SK;
import static org.swisseph.api.ISweObjects.SY;
import static org.swisseph.api.ISweObjects.UR;
import static org.swisseph.app.SweHouseSystem.PLACIDUS;
import static org.swisseph.app.SweObjectsOptions.TROPICAL_ZODIAC;
import static swisseph.SweConst.OK;

/**
 * Proves two things about a tropical (Sayana) {@link SweObjects}: that one can be built at all,
 * and that every object it reports - the nine classical grahas, both lunar nodes, and the
 * ascendant/houses - is numerically correct.
 * <p>
 * <b>Construction and flag derivation is already proven elsewhere</b> -
 * {@link SweObjectsOptionsContractTest#siderealFollowsTheAyanamsaInEveryFlagSet()} shows that
 * {@code TROPICAL_ZODIAC} and {@code ayanamsa(none)} clear {@code SEFLG_SIDEREAL} on all four
 * flag sets, survive whatever order the builder is called in, and copy correctly through
 * {@code options(other)}. This class does not repeat that; it is about what comes out the far
 * end once a tropical chart is actually built - a question the flag tests cannot answer, since
 * a flag being clear does not by itself prove the resulting longitude is the right number.
 * <p>
 * <b>The live cross-check against swetest closes two real gaps</b>, neither
 * {@link SwetestCrossCheckTest} nor {@link SwetestEpochsCrossCheckTest} covers: the lunar nodes
 * were never independently verified in tropical mode (only sidereal, in that class's own
 * {@code meanAndTrueNodesAcrossEpochs}), and the ten-epoch tropical sweep in
 * {@code tropicalPositionsAcrossTenEpochs} checks only Sun..Pluto - not the node, not Ketu, not
 * the ascendant. Everything here does, together, at every epoch.
 * <p>
 * Skips the live half of itself when {@code swetest64.exe} / {@code ephe/} are not present, the
 * same convention every other cross-check in this module uses; the self-consistency test below
 * needs no external program and always runs.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-09
 */
public class TropicalZodiacTest extends AbstractTest {

    /** Kyiv - the same reference place {@link SwetestEpochsCrossCheckTest} uses */
    static final double[] KYIV = {30.523333, 50.450000};

    static final int[] YEARS = {1000, 1500, 1800, 1900, 1950, 1999, 2027, 2035, 2066, 2099};
    static final String NOON = "12:00:00";

    private static int[] date(int year) {
        return new int[]{year, 6, 15, 12, 0};
    }

    private static ISweObjects tropicalChart(int[] date, boolean trueNode) {
        return new SweObjects(getSwephExp(), new SweJulianDate(date, UT_TMZ, date[3]),
                new SweGeoLocation(KYIV[0], KYIV[1], 0.),
                new SweObjectsOptions.Builder().options(TROPICAL_ZODIAC)
                        .trueNode(trueNode).houseSystem(PLACIDUS).build())
                .completeBuild();
    }

    private static ISweObjects siderealChart(int[] date, boolean trueNode) {
        return new SweObjects(getSwephExp(), new SweJulianDate(date, UT_TMZ, date[3]),
                new SweGeoLocation(KYIV[0], KYIV[1], 0.),
                new SweObjectsOptions.Builder().ayanamsa(SweAyanamsa.LAHIRI)
                        .trueNode(trueNode).houseSystem(PLACIDUS).build())
                .completeBuild();
    }

    // ------------------------------------------------------------------ can it be built at all

    @Test
    @DisplayName("a tropical SweObjects builds without error, from the TROPICAL_ZODIAC constant")
    void aTropicalSweObjectsBuildsCleanlyFromTheConstant() {
        final ISweObjects o = new SweObjects(getSwephExp(),
                new SweJulianDate(new int[]{1976, 4, 18, 23, 21}, 3f, 23.35),
                GEO_LUCKNOW, TROPICAL_ZODIAC).completeBuild();

        assertEquals(OK, o.sweError().length(), "no error building a tropical chart: " + o.sweError());
        assertEquals(OBJECTS_COUNT, o.longitudes().length);
    }

    @Test
    @DisplayName("a tropical SweObjects also builds cleanly through the Builder, with an explicit house system")
    void aTropicalSweObjectsBuildsCleanlyFromTheBuilder() {
        final ISweObjectsOptions options = new SweObjectsOptions.Builder()
                .ayanamsa(SweAyanamsa.getNone())
                .houseSystem(PLACIDUS)
                .build();

        final ISweObjects o = new SweObjects(getSwephExp(),
                new SweJulianDate(new int[]{1976, 4, 18, 23, 21}, 3f, 23.35),
                GEO_LUCKNOW, options).completeBuild();

        assertEquals(OK, o.sweError().length(), "no error building a tropical chart: " + o.sweError());
        assertFalse(options.ayanamsa().sidereal(), "getNone() is not a sidereal ayanamsa");
    }

    @Test
    @DisplayName("every classical object, both nodes and the ascendant come back a real in-range longitude")
    void everyObjectComesBackARealInRangeLongitude() {
        final ISweObjects o = tropicalChart(new int[]{1976, 4, 18, 23, 21}, true);

        for (int uid = FIRST_OBJECT_ID; uid <= LAST_OBJECT_ID; uid++) {
            final double lon = o.longitudes()[uid];
            assertFalse(Double.isNaN(lon), "object " + uid + " is NaN");
            assertTrue(lon >= 0. && lon < 360., "object " + uid + " out of [0,360): " + lon);
        }

        final double asc = o.longitudes()[LG];
        assertFalse(Double.isNaN(asc), "the ascendant is NaN");
        assertTrue(asc >= 0. && asc < 360., "the ascendant is out of [0,360): " + asc);

        for (int h = 1; h <= 12; h++) {
            assertTrue(o.cusps()[h] >= 0. && o.cusps()[h] < 360., "cusp " + h + " out of range");
        }
    }

    // ---------------------------------------------------------- self-consistency, no swetest needed

    @ParameterizedTest(name = "{0}")
    @ValueSource(ints = {1000, 1500, 1800, 1900, 1950, 1999, 2027, 2035, 2066, 2099})
    @DisplayName("tropical longitude minus sidereal longitude equals the ayanamsa, for every object")
    void tropicalMinusSiderealEqualsTheAyanamsa(int year) {
        // this needs no external reference at all: the sidereal frame is defined as the
        // tropical one shifted by a single number, so if SweObjects computes both correctly
        // the SAME offset - the ayanamsa - has to separate every single object's longitude,
        // the ascendant included, whichever node type is in effect
        for (boolean trueNode : new boolean[]{false, true}) {
            final ISweObjects tropical = tropicalChart(date(year), trueNode);
            final ISweObjects sidereal = siderealChart(date(year), trueNode);

            final double ayanamsa = sidereal.ayanamsa();
            assertNotEquals(0., ayanamsa, "Lahiri is never exactly zero in this epoch range");

            for (int uid = FIRST_OBJECT_ID; uid <= LAST_OBJECT_ID; uid++) {
                final double expected = (tropical.longitudes()[uid] - ayanamsa + 360.) % 360.;
                assertEquals(expected, sidereal.longitudes()[uid], 1e-9,
                        year + " object " + uid + " trueNode=" + trueNode);
            }

            final double expectedAsc = (tropical.longitudes()[LG] - ayanamsa + 360.) % 360.;
            assertEquals(expectedAsc, sidereal.longitudes()[LG], 1e-9,
                    year + " ascendant trueNode=" + trueNode);
        }
    }

    // -------------------------------------------------------------------------- live, vs swetest

    /** planet letters in swetest order, mapped onto ISweObjects indices */
    static final String BODIES = "0123456789mt";
    static final String[] NAMES = {"Sun", "Moon", "Mercury", "Venus", "Mars", "Jupiter",
            "Saturn", "Uranus", "Neptune", "Pluto", "mean Node", "true Node"};
    static final int[] TO_OBJECT = {SY, CH, BU, SK, MA, GU, SA, UR, NE, PL};

    @ParameterizedTest(name = "{0} tropical, live vs swetest")
    @ValueSource(ints = {1000, 1500, 1800, 1900, 1950, 1999, 2027, 2035, 2066, 2099})
    @DisplayName("every planet, both nodes, Ketu and the ascendant/houses match swetest's own tropical output")
    void everyObjectMatchesSwetestLive(int year) {
        assumeTrue(available(), "swetest64.exe / ephe not found - skipping live cross check");

        // one call: -p asks for the ten planets plus mean(m) and true(t) node, -house adds the
        // Ascendant/MC/ARMC/Vertex and the twelve cusps to the same output, and NEITHER -sid
        // is given, so swetest answers tropical - exactly like TROPICAL_ZODIAC does
        final Map<String, Double> ref = values(date(year), NOON, "-p" + BODIES, "-true", "-fPl",
                house(KYIV[0], KYIV[1], (char) PLACIDUS.fid()));

        final double[] cusps = Swetest.cusps(ref);
        assertNotNull(cusps, year + ": swetest printed no cusps");

        final ISweObjects mean = tropicalChart(date(year), false);
        final ISweObjects trueN = tropicalChart(date(year), true);

        for (int i = 0; i < NAMES.length - 2; i++) {
            final Double expected = ref.get(NAMES[i]);
            assertNotNull(expected, year + ": " + NAMES[i] + " missing from swetest output");
            assertEquals(expected, mean.longitudes()[TO_OBJECT[i]], DELTA, year + " " + NAMES[i]);
        }

        final Double meanNode = ref.get("mean Node");
        final Double trueNode = ref.get("true Node");
        assertNotNull(meanNode, year + ": mean Node missing from swetest output");
        assertNotNull(trueNode, year + ": true Node missing from swetest output");

        assertEquals(meanNode, mean.longitudes()[RA], DELTA, year + " mean node");
        assertEquals((meanNode + 180.) % 360., mean.longitudes()[KE], DELTA, year + " ketu (mean)");

        assertEquals(trueNode, trueN.longitudes()[RA], DELTA, year + " true node");
        assertEquals((trueNode + 180.) % 360., trueN.longitudes()[KE], DELTA, year + " ketu (true)");

        for (int h = 1; h <= 12; h++) {
            assertEquals(cusps[h], mean.cusps()[h], DELTA, year + " house " + h);
        }
        assertEquals(ref.get("Ascendant"), mean.longitudes()[LG], DELTA, year + " Ascendant");
        assertEquals(ref.get("MC"), mean.ascmc()[1], DELTA, year + " MC");
        assertEquals(ref.get("ARMC"), mean.ascmc()[2], DELTA, year + " ARMC");
        assertEquals(ref.get("Vertex"), mean.ascmc()[3], DELTA, year + " Vertex");
    }

    static final double DELTA = 1e-7;
}
