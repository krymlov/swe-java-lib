/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.swisseph.api.*;
import org.swisseph.app.SweJulianDate;
import org.swisseph.app.SweObjects;
import org.swisseph.app.SweObjectsOptions;

import static org.junit.jupiter.api.Assertions.*;
import static org.swisseph.api.ISweObjects.*;
import static org.swisseph.app.SweAyanamsa.*;
import static org.swisseph.app.SweHouseSystem.PLACIDUS;
import static org.swisseph.app.SweHouseSystem.WHOLE_SIGN;
import static org.swisseph.utils.IDegreeUtils.toDMSms;
import static swisseph.SweConst.*;

/**
 * Reference chart from <code>IuriiK.jhd</code> (Jagannatha Hora birth data):
 * 18 April 1976, 23:21 local, UTC+3, Starokostyantyniv 27&deg;13'E 49&deg;45'N.
 * <p>
 * Every expected value below was produced by the Swiss Ephemeris reference program
 * <code>e:\Github\swisseph\windows\programs\swetest64.exe</code> against the same
 * ephemeris files this project ships in <code>ephe/</code>, e.g.
 * <pre>
 * swetest64.exe -b18.4.1976 -ut20:21:00 -p0123456789m -eswe -sid1 -true \
 *               -fPl -house27.2166666666667,49.75,P -edir&lt;repo&gt;\ephe
 * </pre>
 * The same numbers are asserted for the native library and for the pure Java port,
 * so a drift in either is visible immediately.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
public class JhdIuriiKTest extends AbstractTest {

    static final JhdChart JHD = JhdChart.read("IuriiK.jhd");

    /** swetest prints 7 decimals, so half of the last digit is the honest tolerance */
    static final double DELTA_SWETEST = 1e-7;

    /**
     * The pure Java port is Swiss Ephemeris 2.01.00 while the native library is
     * 2.10.03; positions drift by well under an arc second, the Lahiri ayanamsa
     * definition changed by ~11 arc seconds between the two.
     */
    static final double DELTA_JAVA_POS = 1e-3;
    static final double DELTA_JAVA_AYA = 5e-3;

    // ---- swetest -b18.4.1976 -ut20:21:00 -eswe -sid1 -true -----------------
    static final double SWETEST_JULDAY = 2442887.347916667;
    static final double SWETEST_AYANAMSA_LAHIRI = 23 + 31 / 60. + 44.7692 / 3600.;

    static final double[] SWETEST_LAHIRI = {
            /* LG */ 220.9553945, /* SY */ 5.3904126, /* CH */ 245.1054009,
            /* MA */ 81.5719355, /* BU */ 22.4498207, /* GU */ 11.9872727,
            /* SK */ 349.3837048, /* SA */ 92.9415910, /* RA */ 199.9739280,
            /* KE */ 19.9739280, /* UR */ 191.8567073, /* NE */ 230.1361038,
            /* PL */ 166.2960205};

    static final double[] SWETEST_PLACIDUS = {
            220.9553945, 255.0133941, 297.7343645, 335.9675613, 3.7508716, 24.2385302,
            40.9553945, 75.0133941, 117.7343645, 155.9675613, 183.7508716, 204.2385302};

    static final double SWETEST_MC = 155.9675613;
    static final double SWETEST_ARMC = 179.5382007;
    static final double SWETEST_VERTEX = 84.7015691;

    // ------------------------------------------------------------------ input

    @Test
    void jhdFileIsParsedAsExpected() {
        assertArrayEquals(new int[]{1976, 4, 18, 23, 21}, JHD.date());
        assertEquals(23.35, JHD.localTime(), 1e-12);
        assertEquals(3f, JHD.timeZone());
        assertEquals(27 + 13 / 60., JHD.longitude(), 1e-12);
        assertEquals(49 + 45 / 60., JHD.latitude(), 1e-12);
        assertEquals(265., JHD.altitude(), 1e-12);
        assertEquals("Starokostyantyniv", JHD.city());
        assertEquals("Ukraine", JHD.country());
    }

    @Test
    void degreesMinutesConversion() {
        assertEquals(49.75, JhdChart.degreesMinutes(49.45), 1e-12);
        assertEquals(27.2166666666667, JhdChart.degreesMinutes(27.13), 1e-12);
        assertEquals(-27.2166666666667, JhdChart.degreesMinutes(-27.13), 1e-12);
        assertEquals(0., JhdChart.degreesMinutes(0.), 1e-12);
    }

    // ------------------------------------------------------- date conversion

    @Test
    void localTimeConvertsToTheJulianDaySwetestUsed() {
        final ISweJulianDate jd = getSwephExp().initJulianDate(JHD.julianDate());
        assertEquals(SWETEST_JULDAY, jd.julianDay(), 1e-9);

        // 23:21 local at UTC+3 is 20:21 UT
        assertEquals(20, jd.uhours());
        assertEquals(21, jd.uminutes());
        assertEquals(0., jd.useconds(), 1e-6);

        // and the local time survives the round trip unchanged
        assertEquals(23, jd.hours());
        assertEquals(21, jd.minutes());
        assertEquals(0., jd.dseconds(), 1e-6);
    }

    @Test
    void julianDayIsTheSameThroughBothImplementations() {
        assertEquals(getSwephExp().initJulianDate(JHD.julianDate()).julianDay(),
                getSwissEph().initJulianDate(JHD.julianDate()).julianDay(), 1e-9);
    }

    @Test
    void revjulRoundTripsBackToTheUtcDate() {
        final ISweJulianDate ut = getSwephExp().swe_revjul(SWETEST_JULDAY, SE_GREG_CAL);
        assertArrayEquals(new int[]{1976, 4, 18}, ut.date());
        assertEquals(20, ut.uhours());
        assertEquals(21, ut.uminutes());
        // swetest prints the julian day to 9 decimals, which is only worth 1e-4 s
        assertEquals(0., ut.useconds(), 1e-4);
    }

    /**
     * For a star based ayanamsa the reported (apparent) value and the one the houses
     * are built with (SEFLG_TRUEPOS) are genuinely different numbers.
     */
    @Test
    void starBasedAyanamsaIsReportedApparentButUsedTrue() {
        final ISweObjects o = objects(getSwephExp(), TRUE_CITRA, PLACIDUS);
        final double[] daya = new double[1];
        final StringBuilder serr = new StringBuilder();

        assertEquals(23 + 31 / 60. + 9.4986 / 3600., o.ayanamsa(), DELTA_SWETEST, "apparent");

        getSwephExp().swe_set_sid_mode(TRUE_CITRA.fid(), 0., 0.);
        getSwephExp().swe_get_ayanamsa_ex_ut(SWETEST_JULDAY, ISweObjectsOptions.DEFAULT_SS_HOUSE_FLAGS, daya, serr);
        assertEquals(23 + 30 / 60. + 49.1419 / 3600., daya[0], DELTA_SWETEST, "true");
    }

    // ---------------------------------------------------------- the chart

    private ISweObjects objects(ISwissEph swe, ISweAyanamsa ayanamsa, ISweHouseSystem hsys) {
        return new SweObjects(swe, new SweJulianDate(JHD.date(), JHD.timeZone(), JHD.localTime()),
                JHD.geoLocation(),
                new SweObjectsOptions.Builder().ayanamsa(ayanamsa).houseSystem(hsys).build())
                .completeBuild();
    }

    @Test
    void nativeLongitudesMatchSwetest() {
        final ISweObjects o = objects(getSwephExp(), LAHIRI, WHOLE_SIGN);
        for (int i = LG; i <= PL; i++) {
            assertEquals(SWETEST_LAHIRI[i], o.longitudes()[i], DELTA_SWETEST, "object " + i);
        }
    }

    @Test
    void nativeAyanamsaMatchesSwetest() {
        assertEquals(SWETEST_AYANAMSA_LAHIRI, objects(getSwephExp(), LAHIRI, WHOLE_SIGN).ayanamsa(), DELTA_SWETEST);
    }

    @Test
    void nativePlacidusCuspsMatchSwetest() {
        final ISweObjects o = objects(getSwephExp(), LAHIRI, PLACIDUS);
        for (int h = 1; h <= 12; h++) {
            assertEquals(SWETEST_PLACIDUS[h - 1], o.cusps()[h], DELTA_SWETEST, "house " + h);
        }
    }

    @Test
    void nativeWholeSignCuspsStartAtTheAscendantSign() {
        final ISweObjects o = objects(getSwephExp(), LAHIRI, WHOLE_SIGN);
        // the ascendant is at 220.955 -> sign 8 (Scorpio) starting at 210
        for (int h = 1; h <= 12; h++) {
            assertEquals((210. + (h - 1) * 30.) % 360., o.cusps()[h], DELTA_SWETEST, "house " + h);
        }
    }

    @Test
    void nativeAscmcMatchesSwetest() {
        final ISweObjects o = objects(getSwephExp(), LAHIRI, PLACIDUS);
        assertEquals(SWETEST_LAHIRI[LG], o.ascmc()[SE_ASC], DELTA_SWETEST);
        assertEquals(SWETEST_MC, o.ascmc()[SE_MC], DELTA_SWETEST);
        assertEquals(SWETEST_ARMC, o.ascmc()[SE_ARMC], DELTA_SWETEST);
        assertEquals(SWETEST_VERTEX, o.ascmc()[SE_VERTEX], DELTA_SWETEST);
    }

    /**
     * swetest -sid&lt;n&gt;, same date and place.
     * <p>
     * Positions come from the run with <code>-true</code> (the library computes them
     * with SEFLG_TRUEPOS). The ayanamsa column comes from the run <b>without</b>
     * <code>-true</code>, because {@link ISweObjects#ayanamsa()} deliberately reports
     * the apparent value - see the comment on
     * {@link ISweObjectsOptions#DEFAULT_SS_HOUSE_FLAGS}. The two differ only for
     * star based ayanamsas; here only True Citra (sid 27) moves, by 55 arc seconds.
     */
    @ParameterizedTest(name = "sid{0}")
    @CsvSource({
            //  sid, apparent ayanamsa d, m, s,   Sun,         Moon,        Ascendant
            " 0, 24, 24, 44.3167,   4.5072050, 244.2221932, 220.0721869",
            " 1, 23, 31, 44.7692,   5.3904126, 245.1054009, 220.9553945",
            " 3, 22,  4, 58.0844,   6.8367139, 246.5517022, 222.4016959",
            " 5, 23, 25, 56.1008,   5.4872649, 245.2022532, 221.0522469",
            "27, 23, 31,  9.4986,   5.4058646, 245.1208529, 220.9708466"})
    void nativeAyanamsasMatchSwetest(int sid, int deg, int min, double sec,
                                     double sun, double moon, double asc) {
        final ISweAyanamsa ayanamsa = SweAyanamsaOf(sid);
        final ISweObjects o = objects(getSwephExp(), ayanamsa, PLACIDUS);

        assertEquals(deg + min / 60. + sec / 3600., o.ayanamsa(), DELTA_SWETEST, "ayanamsa");
        assertEquals(sun, o.longitudes()[SY], DELTA_SWETEST, "Sun");
        assertEquals(moon, o.longitudes()[CH], DELTA_SWETEST, "Moon");
        assertEquals(asc, o.longitudes()[LG], DELTA_SWETEST, "Ascendant");
    }

    private static ISweAyanamsa SweAyanamsaOf(int sid) {
        for (org.swisseph.app.SweAyanamsa a : org.swisseph.app.SweAyanamsa.values()) {
            if (a.fid() == sid) return a;
        }
        throw new IllegalArgumentException("no ayanamsa with sid " + sid);
    }

    // ------------------------------------------- native vs pure Java port

    @Test
    void pureJavaAgreesWithTheNativeLibrary() {
        final ISweObjects n = objects(getSwephExp(), LAHIRI, PLACIDUS);
        final ISweObjects j = objects(getSwissEph(), LAHIRI, PLACIDUS);

        assertEquals(n.ayanamsa(), j.ayanamsa(), DELTA_JAVA_AYA, "ayanamsa");
        for (int i = LG; i <= PL; i++) {
            assertEquals(n.longitudes()[i], j.longitudes()[i], DELTA_JAVA_POS, "object " + i);
        }
        for (int h = 1; h <= 12; h++) {
            assertEquals(n.cusps()[h], j.cusps()[h], DELTA_JAVA_POS, "house " + h);
        }
    }

    @Test
    void pureJavaAndNativePlaceEveryObjectInTheSameSign() {
        final ISweObjects n = objects(getSwephExp(), LAHIRI, WHOLE_SIGN);
        final ISweObjects j = objects(getSwissEph(), LAHIRI, WHOLE_SIGN);
        assertArrayEquals(n.signs(), j.signs());
        assertArrayEquals(n.houses(), j.houses());
    }

    // -------------------------------------------------------- presentation

    @Test
    void positionsRenderAsTheExpectedDegreeStrings() {
        final ISweObjects o = objects(getSwephExp(), LAHIRI, PLACIDUS);
        assertEquals("220°57'19.42\"", toDMSms(o.longitudes()[LG]).toString());
        assertEquals("05°23'25.49\"", toDMSms(o.longitudes()[SY]).toString());
        assertEquals("245°06'19.44\"", toDMSms(o.longitudes()[CH]).toString());
        // swetest prints the ayanamsa as 23°31'44.7692 - rounded to the centisecond
        // toDMSms renders 44.77, not the 44.76 plain truncation used to produce
        assertEquals("23°31'44.77\"", toDMSms(o.ayanamsa()).toString());
    }
}
