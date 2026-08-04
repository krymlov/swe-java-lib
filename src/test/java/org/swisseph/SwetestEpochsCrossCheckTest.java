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
import static org.swisseph.api.ISweJulianDate.UT_TMZ;
import static org.swisseph.api.ISweObjects.*;
import static org.swisseph.app.SweHouseSystem.*;
import static org.swisseph.app.SweObjectsOptions.TROPICAL_ZODIAC;
import static swisseph.SweConst.SE_MOON;
import static swisseph.SweConst.SE_TIDAL_AUTOMATIC;

/**
 * Live cross-check of {@link org.swisseph.app.SweObjects} against
 * <code>swetest64.exe</code> over a wide spread of epochs, places, zodiacs, ayanamsas,
 * house systems and node types. Everything is compared against the reference program at
 * the precision it prints, 1e-7 degrees.
 * <p>
 * Epochs run from 1000 to 2099, which crosses the Julian/Gregorian calendar boundary
 * (1000 and 1500 are Julian dates on both sides) and spans three ephemeris files -
 * <code>sepl_06</code>, <code>sepl_12</code> and <code>sepl_18</code>. Places run from the
 * equator to 89.9 degrees and across the date line.
 * <p>
 * The whole class skips itself when <code>swetest64.exe</code> is not present;
 * {@link JhdIuriiKTest} keeps a set of pasted reference values for that case.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
public class SwetestEpochsCrossCheckTest extends AbstractTest {

    static final double DELTA = 1e-7;
    static final String NOON = "12:00:00";

    /** the epochs asked for, all at 15 June 12:00 UT */
    static final int[] YEARS = {1000, 1500, 1800, 1900, 1950, 1999, 2027, 2035, 2066, 2099};

    // longitude, latitude
    static final double[] KYIV = {30.523333, 50.450000};        // Europe
    static final double[] NEW_YORK = {-74.006000, 40.712800};   // USA, western longitude
    static final double[] CHENNAI = {80.270000, 13.080000};     // Asia, near the equator
    static final double[] TROMSO = {18.955000, 69.649000};      // Scandinavia, above the polar circle
    static final double[] REYKJAVIK = {-21.827774, 64.128288};  // Scandinavia, western longitude
    static final double[] NORTH_POLE = {0., 89.900000};         // extreme north
    static final double[] SOUTH_POLE = {0., -89.900000};        // extreme south
    static final double[] DATE_LINE_E = {179.900000, 1.000000}; // extreme east
    static final double[] DATE_LINE_W = {-179.900000, -1.};     // extreme west

    private static int[] date(int year) {
        return new int[]{year, 6, 15, 12, 0};
    }

    private ISweObjects chart(int year, double[] place, ISweObjectsOptions options) {
        return new SweObjects(getSwephExp(), new SweJulianDate(date(year), UT_TMZ, 12.),
                new SweGeoLocation(place[0], place[1], 0.), options).completeBuild();
    }

    private static ISweObjectsOptions sidereal(SweAyanamsa ayanamsa, SweHouseSystem hsys, boolean trueNode) {
        return new SweObjectsOptions.Builder().ayanamsa(ayanamsa)
                .houseSystem(hsys).trueNode(trueNode).build();
    }

    /**
     * Builder order matters: {@code ayanamsa(none)} clears SEFLG_SIDEREAL and also forces
     * the house system to whole sign, so the house system has to be set afterwards.
     */
    private static ISweObjectsOptions tropical(SweHouseSystem hsys, boolean trueNode) {
        return new SweObjectsOptions.Builder().options(TROPICAL_ZODIAC)
                .houseSystem(hsys).trueNode(trueNode).build();
    }

    private static SweAyanamsa ayanamsaOf(int sid) {
        for (SweAyanamsa a : SweAyanamsa.values()) if (a.fid() == sid) return a;
        throw new IllegalArgumentException("no ayanamsa with sid " + sid);
    }

    /** swetest needs -true because the library computes with SEFLG_TRUEPOS */
    private void assertBodiesMatch(ISweObjects o, Map<String, Double> ref, String where) {
        for (int i = 0; i < BODY_NAMES.length; i++) {
            final Double expected = ref.get(BODY_NAMES[i]);
            assertNotNull(expected, where + ": " + BODY_NAMES[i] + " missing from swetest");
            assertEquals(expected, o.longitudes()[SWETEST_TO_OBJECT[i]], DELTA,
                    where + ": " + BODY_NAMES[i]);
        }
    }

    static final int[] SWETEST_TO_OBJECT = {SY, CH, BU, SK, MA, GU, SA, UR, NE, PL};

    // =====================================================================  1

    @ParameterizedTest(name = "{0} tropical Kyiv")
    @ValueSource(ints = {1000, 1500, 1800, 1900, 1950, 1999, 2027, 2035, 2066, 2099})
    void tropicalPositionsAcrossTenEpochs(int year) {
        assumeTrue(available());
        final Map<String, Double> ref = values(date(year), NOON, "-p" + BODIES, "-true", "-fPl");
        assertBodiesMatch(chart(year, KYIV, tropical(PLACIDUS, false)), ref, "tropical " + year);
    }

    // =====================================================================  2

    @ParameterizedTest(name = "{0} sidereal Lahiri Kyiv")
    @ValueSource(ints = {1000, 1500, 1800, 1900, 1950, 1999, 2027, 2035, 2066, 2099})
    void siderealLahiriPositionsAcrossTenEpochs(int year) {
        assumeTrue(available());
        final Map<String, Double> ref = values(date(year), NOON,
                "-p" + BODIES, "-true", "-sid1", "-fPl");
        assertBodiesMatch(chart(year, KYIV, sidereal(SweAyanamsa.LAHIRI, PLACIDUS, false)),
                ref, "sidereal " + year);
    }

    // =====================================================================  3

    @ParameterizedTest(name = "{0} mean vs true node")
    @ValueSource(ints = {1000, 1500, 1800, 1900, 1950, 1999, 2027, 2035, 2066, 2099})
    void meanAndTrueNodesAcrossEpochs(int year) {
        assumeTrue(available());
        final Map<String, Double> ref = values(date(year), NOON, "-pmt", "-true", "-sid1", "-fPl");

        final Double mean = ref.get("mean Node");
        final Double node = ref.get("true Node");
        assertNotNull(mean, "mean Node missing");
        assertNotNull(node, "true Node missing");
        assertNotEquals(mean, node, "the two nodes must differ at " + year);

        final ISweObjects meanChart = chart(year, KYIV, sidereal(SweAyanamsa.LAHIRI, PLACIDUS, false));
        assertEquals(mean, meanChart.longitudes()[RA], DELTA, year + " mean node");
        assertEquals((mean + 180.) % 360., meanChart.longitudes()[KE], DELTA, year + " ketu");

        final ISweObjects trueChart = chart(year, KYIV, sidereal(SweAyanamsa.LAHIRI, PLACIDUS, true));
        assertEquals(node, trueChart.longitudes()[RA], DELTA, year + " true node");
        assertEquals((node + 180.) % 360., trueChart.longitudes()[KE], DELTA, year + " true ketu");
    }

    // =====================================================================  4

    /**
     * The reported ayanamsa follows the true/apparent choice of the planets, so swetest is
     * run with -true here as well.
     */
    @ParameterizedTest(name = "sid{0} in {1}")
    @CsvSource({"0,1000", "1,1000", "3,1500", "5,1800", "7,1900", "16,1950",
            "21,1999", "23,2027", "27,2035", "1,2066", "3,2099", "5,2099"})
    void ayanamsaValuesAcrossTypesAndEpochs(int sid, int year) {
        assumeTrue(available());
        final SweAyanamsa ayanamsa = ayanamsaOf(sid);
        final Map<String, Double> ref = values(date(year), NOON,
                "-p" + BODIES, "-true", "-sid" + sid, "-fPl");

        final ISweObjects o = chart(year, KYIV, sidereal(ayanamsa, PLACIDUS, false));
        assertBodiesMatch(o, ref, ayanamsa + " " + year);

        // and the ayanamsa itself, read back out of swetest's header line
        for (String line : lines(date(year), NOON, "-p0", "-true", "-sid" + sid, "-fPl")) {
            final int at = line.indexOf("ayanamsa =");
            if (at < 0) continue;
            final String dms = line.substring(at + 10, line.indexOf('(', at)).trim();
            assertEquals(parseDms(dms), o.ayanamsa(), 1e-6, ayanamsa + " " + year + " ayanamsa");
            return;
        }
        fail("swetest printed no ayanamsa for sid" + sid);
    }

    /** "23°31'44.7692" as printed by swetest */
    static double parseDms(String dms) {
        final String[] parts = dms.split("[°'\"]");
        double value = Math.abs(Double.parseDouble(parts[0].trim()));
        if (parts.length > 1) value += Double.parseDouble(parts[1].trim()) / 60.;
        if (parts.length > 2) value += Double.parseDouble(parts[2].trim()) / 3600.;
        return dms.trim().startsWith("-") ? -value : value;
    }

    // =====================================================================  5

    @ParameterizedTest(name = "{0} cusps at Kyiv 1999")
    @EnumSource(SweHouseSystem.class)
    void houseCuspsAcrossHouseSystems(SweHouseSystem hsys) {
        assumeTrue(available());
        if (NIL == hsys) return;
        assertCuspsMatch(1999, KYIV, hsys, sidereal(SweAyanamsa.LAHIRI, hsys, false), "-sid1");
    }

    private void assertCuspsMatch(int year, double[] place, SweHouseSystem hsys,
                                  ISweObjectsOptions options, String... zodiac) {
        final String[] extra = new String[zodiac.length + 4];
        extra[0] = "-p0";
        extra[1] = "-true";
        extra[2] = "-fPl";
        extra[3] = house(place[0], place[1], (char) hsys.fid());
        System.arraycopy(zodiac, 0, extra, 4, zodiac.length);

        final Map<String, Double> ref = values(date(year), NOON, extra);
        final double[] refCusps = cusps(ref);
        final String where = hsys + " " + year + " lat=" + place[1];
        assertNotNull(refCusps, where + ": swetest printed no cusps");

        final ISweObjects o = chart(year, place, options);
        for (int h = 1; h <= 12; h++) {
            assertEquals(refCusps[h], o.cusps()[h], DELTA, where + " house " + h);
        }
        assertEquals(ref.get("Ascendant"), o.longitudes()[LG], DELTA, where + " Ascendant");
        assertEquals(ref.get("MC"), o.ascmc()[1], DELTA, where + " MC");
        assertEquals(ref.get("ARMC"), o.ascmc()[2], DELTA, where + " ARMC");
        assertEquals(ref.get("Vertex"), o.ascmc()[3], DELTA, where + " Vertex");
    }

    // =====================================================================  6

    @ParameterizedTest(name = "{0} above the polar circle")
    @EnumSource(SweHouseSystem.class)
    void houseCuspsAtScandinavianLatitudes(SweHouseSystem hsys) {
        assumeTrue(available());
        if (NIL == hsys) return;
        // Placidus and Koch are undefined above the polar circle - see
        // houseSystemsFailTogetherBeyondThePolarCircle
        if (PLACIDUS == hsys || KOCH == hsys) return;

        assertCuspsMatch(1950, TROMSO, hsys, sidereal(SweAyanamsa.LAHIRI, hsys, false), "-sid1");
        assertCuspsMatch(2027, REYKJAVIK, hsys, tropical(hsys, false));
    }

    // =====================================================================  7

    @ParameterizedTest(name = "{0} at +-89.9 deg")
    @EnumSource(SweHouseSystem.class)
    void houseCuspsAtThePoles(SweHouseSystem hsys) {
        assumeTrue(available());
        if (NIL == hsys || PLACIDUS == hsys || KOCH == hsys) return;

        assertCuspsMatch(1900, NORTH_POLE, hsys, sidereal(SweAyanamsa.KRISHNAMURTI, hsys, false), "-sid5");
        assertCuspsMatch(2066, SOUTH_POLE, hsys, tropical(hsys, true));
    }

    // =====================================================================  8

    /**
     * Placidus and Koch have no solution beyond the polar circle. Swiss Ephemeris reports
     * that by returning ERR, and the library turns it into an exception - the point here
     * is that both sides agree on <i>when</i> it happens.
     */
    @Test
    void houseSystemsFailTogetherBeyondThePolarCircle() {
        assumeTrue(available());
        for (SweHouseSystem hsys : new SweHouseSystem[]{PLACIDUS, KOCH}) {
            for (double[] place : new double[][]{TROMSO, NORTH_POLE, SOUTH_POLE}) {
                final Map<String, Double> ref = values(date(1999), NOON, "-p0", "-true", "-fPl",
                        house(place[0], place[1], (char) hsys.fid()));

                boolean libraryFailed = false;
                try {
                    chart(1999, place, tropical(hsys, false));
                } catch (SweRuntimeException expected) {
                    libraryFailed = true;
                }

                // swetest substitutes Porphyry and says so in the house system header
                boolean swetestFailed = lines(date(1999), NOON, "-p0", "-true", "-fPl",
                        house(place[0], place[1], (char) hsys.fid()))
                        .stream().anyMatch(l -> l.contains("Porphyry") || l.contains("out of range"));

                assertEquals(swetestFailed, libraryFailed,
                        hsys + " at lat=" + place[1] + ": swetest fell back=" + swetestFailed
                                + ", library threw=" + libraryFailed + " (cusps " + cusps(ref) + ")");
            }
        }
    }

    // =====================================================================  9

    @ParameterizedTest(name = "{0} across the date line")
    @ValueSource(ints = {1800, 1999, 2099})
    void positionsAndHousesAcrossTheDateLine(int year) {
        assumeTrue(available());
        assertCuspsMatch(year, DATE_LINE_E, CAMPANUS, sidereal(SweAyanamsa.LAHIRI, CAMPANUS, false), "-sid1");
        assertCuspsMatch(year, DATE_LINE_W, CAMPANUS, tropical(CAMPANUS, false));
    }

    // ===================================================================== 10

    @ParameterizedTest(name = "{0} Europe/USA/Asia/Scandinavia")
    @ValueSource(ints = {1500, 1900, 2035})
    void ascendantAndMcAroundTheWorld(int year) {
        assumeTrue(available());
        for (double[] place : new double[][]{KYIV, NEW_YORK, CHENNAI, REYKJAVIK}) {
            assertCuspsMatch(year, place, EQUAL, sidereal(SweAyanamsa.LAHIRI, EQUAL, false), "-sid1");
        }
    }

    // ===================================================================== 11

    /**
     * House positions, swetest's -fPj column. -hsy&lt;letter&gt;1 selects hpos_meth 1, the
     * ecliptic projection {@link ISweObjects#calculatePlanetHousePosition(int)} uses.
     */
    @ParameterizedTest(name = "{0} house positions")
    @EnumSource(SweHouseSystem.class)
    void housePositionsAcrossHouseSystems(SweHouseSystem hsys) {
        assumeTrue(available());
        if (NIL == hsys || WHOLE_SIGN == hsys) return;   // whole sign is not swe_house_pos based

        final char letter = (char) hsys.fid();
        final Map<String, Double> ref = values(date(1999), NOON, "-p" + BODIES, "-true", "-fPj",
                house(KYIV[0], KYIV[1], letter), "-hsy" + letter + "1");

        final ISweObjects o = chart(1999, KYIV, sidereal(SweAyanamsa.LAHIRI, hsys, false));
        for (int i = 0; i < BODY_NAMES.length; i++) {
            final Double expected = ref.get(BODY_NAMES[i]);
            assertNotNull(expected, hsys + ": " + BODY_NAMES[i] + " missing");
            final int obj = SWETEST_TO_OBJECT[i];
            assertEquals(expected, o.calculatePlanetHousePosition(obj), 1e-6, hsys + " " + BODY_NAMES[i]);
            assertEquals(expected.intValue(), o.houses()[obj], hsys + " " + BODY_NAMES[i] + " house");
        }
    }

    // ===================================================================== 12

    /**
     * Found while building this class. <code>swe_deltat_ex()</code> takes the tidal
     * acceleration from the DE number of the ephemeris file that happens to be open, so
     * for an old date it answers differently before and after anything has been
     * calculated. {@link org.swisseph.app.SweObjects} fixes delta t in its constructor,
     * before any planet is computed, so two identical charts built one after the other on
     * the same thread can disagree - 11.3 s of delta t at year 1000 is 2e-3 degrees of
     * Moon.
     * <p>
     * Every other test in this class pins the tidal acceleration to sidestep it. This one
     * measures it, so the day it is fixed the numbers here say so.
     */
    @Test
    void deltaTIsStableWhateverWasComputedBefore() {
        assumeTrue(available());

        // the raw swisseph behaviour this guards against: swe_deltat_ex() takes the tidal
        // acceleration from the DE number of the ephemeris file that is open, so opening
        // the year 1000 moon file moves delta t by 11 s - 2e-3 degrees of Moon
        assertEquals(11.276125, warmMinusCold(2086474.), 1e-5, "raw swe_deltat_ex at year 1000");
        for (double jd : new double[]{2378662., 2451345., 2488070.}) {
            assertEquals(0., warmMinusCold(jd), 0., "raw swe_deltat_ex at jd " + jd);
        }

        // SweObjects pins the tidal acceleration around that call, so a chart no longer
        // depends on what was built before it - the same chart twice, with a year 1000
        // chart of a different epoch in between, has to come out identical
        coldStart();
        final ISweObjects first = chart(1500, KYIV, sidereal(SweAyanamsa.LAHIRI, PLACIDUS, false));
        chart(1000, TROMSO, tropical(CAMPANUS, true));
        final ISweObjects again = chart(1500, KYIV, sidereal(SweAyanamsa.LAHIRI, PLACIDUS, false));

        assertEquals(first.sweJulianDate().deltaT(), again.sweJulianDate().deltaT(), 0., "delta t");
        for (int i = LG; i <= PL; i++) {
            assertEquals(first.longitudes()[i], again.longitudes()[i], 0., "object " + i);
        }
    }

    /** forgets every open ephemeris file and any pinned tidal acceleration */
    private void coldStart() {
        getSwephExp().swe_close();
        getSwephExp().swe_set_ephe_path("ephe");
        getSwephExp().swe_set_tid_acc(SE_TIDAL_AUTOMATIC);
    }

    /** @return how much delta t moves once the ephemeris file for that date is open, seconds */
    private double warmMinusCold(double jd) {
        final int flags = ISweObjectsOptions.DEFAULT_SS_MAIN_FLAGS;
        coldStart();
        final double cold = getSwephExp().swe_deltat_ex(jd, flags, null);
        getSwephExp().swe_calc(jd + cold, SE_MOON, flags, new double[6], null);
        return (getSwephExp().swe_deltat_ex(jd, flags, null) - cold) * 86400.;
    }

    /**
     * 1000 and 1500 predate the Gregorian reform, so both swetest and
     * {@link ISweJulianDate#gregorianCalendar} must read the date as Julian. Getting that
     * wrong shifts the julian day by days, not fractions.
     */
    @ParameterizedTest(name = "{0} julian/gregorian calendar")
    @CsvSource({"1000, 2086474.0", "1500, 2269099.0", "1800, 2378662.0", "1999, 2451345.0"})
    void datesBeforeTheGregorianReformAreJulian(int year, double expectedJulianDay) {
        assumeTrue(available());

        final ISweJulianDate jd = getSwephExp().initJulianDate(new SweJulianDate(date(year), UT_TMZ, 12.));
        assertEquals(expectedJulianDay, jd.julianDay(), 1e-6, year + " julian day");
        assertEquals(year < 1582, !jd.gregorianCalendar(), year + " calendar type");

        // swetest prints the julian day it used in its "UT:" line
        for (String line : lines(date(year), NOON, "-p0", "-fPl")) {
            if (!line.startsWith("UT:")) continue;
            final double swetestJd = Double.parseDouble(line.substring(3).trim().split("\\s+")[0]);
            assertEquals(swetestJd, jd.julianDay(), 1e-6, year + ": swetest julian day");
            return;
        }
        fail("swetest printed no UT: line");
    }
}
