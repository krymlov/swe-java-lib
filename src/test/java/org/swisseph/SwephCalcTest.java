/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Ported from {@code swe-java-api}'s {@code swisseph.SwephCalcTest}: the same checks, but
 * every call goes through {@link ISwissEph} ({@link #getSwephExp()}, the native-backed
 * {@link SwephNative}) instead of straight to {@code swisseph.SwephExp}. {@code swe_calc},
 * {@code swe_calc_ut}, {@code swe_calc_pctr}, {@code swe_set_topo}, the
 * {@code swe_fixstar*} family, and the small metadata calls: {@code swe_get_planet_name},
 * {@code swe_version}, {@code swe_get_library_path}, {@code swe_get_current_file_data},
 * {@code swe_set_jpl_file}, {@code swe_close}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
class SwephCalcTest extends AIswTest {

    @Test
    void swe_calc_returnsAPlausibleGeocentricPosition() {
        double[] xx = calc(J2000, SE_SUN, SEFLG_SWIEPH | SEFLG_SPEED);

        assertTrue(xx[0] >= 0. && xx[0] < 360., "longitude: " + xx[0]);
        assertTrue(Math.abs(xx[1]) < 1., "the Sun's latitude is near zero: " + xx[1]);
        assertTrue(xx[2] > 0.9 && xx[2] < 1.1, "distance in AU: " + xx[2]);
        assertTrue(xx[3] > 0.9 && xx[3] < 1.1, "the Sun's speed in longitude, deg/day: " + xx[3]);
    }

    @Test
    void swe_calc_ut_agreesWithSweCalcOnceDeltaTIsApplied() {
        double jdET = J2000;
        double jdUT = jdET - getSwephExp().swe_deltat(jdET);

        double[] et = calc(jdET, SE_MARS, SEFLG_SWIEPH);
        double[] ut = new double[6];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_calc_ut(jdUT, SE_MARS, SEFLG_SWIEPH, ut, serr);

        assertTrue(ret >= 0, "" + serr);
        assertEquals(et[0], ut[0], 1e-6, "same instant in ET and UT must give the same longitude");
    }

    @Test
    void swe_calc_reportsAnErrorForAnUnsupportedPlanetNumber() {
        double[] xx = new double[6];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_calc(J2000, 99999, SEFLG_SWIEPH, xx, serr);

        assertTrue(ret < 0, "an absurd planet number must fail");
        assertTrue(serr.length() > 0, "and say why");
    }

    @Test
    void swe_calc_heliocentricEarthIsOppositeTheGeometricGeocentricSun() {
        // heliocentric Earth and the (geometric, unaberrated) geocentric Sun describe the
        // same Sun-Earth line seen from either end, so they are 180 degrees apart. TRUEPOS
        // is needed on both sides: apparent positions carry light-time/aberration
        // corrections that break the exact relationship.
        double[] sun = calc(J2000, SE_SUN, SEFLG_SWIEPH | SEFLG_TRUEPOS);
        double[] earth = calc(J2000, 14 /* SE_EARTH */, SEFLG_SWIEPH | SEFLG_TRUEPOS | SEFLG_HELCTR);

        assertEquals(180., normDeg(sun[0] - earth[0]), 1e-6);
    }

    @Test
    void swe_set_topo_givesTheMoonATopocentricParallaxAgainstTheGeocentricPosition() {
        double[] geocentric = calc(J2000, SE_MOON, SEFLG_SWIEPH);

        getSwephExp().swe_set_topo(GEOLON, GEOLAT, GEOALT);
        double[] topocentric = calc(J2000, SE_MOON, SEFLG_SWIEPH | SEFLG_TOPOCTR);

        // the Moon is close enough that topocentric parallax is easily measurable, unlike
        // for any other body swe_calc offers
        assertTrue(Math.abs(normDeg(topocentric[0] - geocentric[0])) > 0.1,
                "topocentric parallax should shift the Moon's longitude: geocentric="
                        + geocentric[0] + " topocentric=" + topocentric[0]);
    }

    @Test
    void swe_calc_pctr_locatesOneBodyAsSeenFromAnother() {
        // Mars as seen from Jupiter must differ from the geocentric Mars, since the two
        // reference points are not the same
        double[] geo = calc(J2000, SE_MARS, SEFLG_SWIEPH);

        double[] xx = new double[6];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_calc_pctr(J2000, SE_MARS, SE_JUPITER, SEFLG_SWIEPH, xx, serr);

        assertTrue(ret >= 0, "" + serr);
        assertTrue(xx[0] >= 0. && xx[0] < 360.);
        assertTrue(Math.abs(normDeg(xx[0] - geo[0])) > 1., "planetocentric must differ from geocentric");
    }

    // ============================================================ fixstar family

    @Test
    void swe_fixstar_findsSpicaNearTheKnownLongitude() {
        StringBuilder star = new StringBuilder("Spica");
        double[] xx = new double[6];
        StringBuilder serr = new StringBuilder();

        int ret = getSwephExp().swe_fixstar(star, J2000, SEFLG_SWIEPH, xx, serr);

        assertTrue(ret >= 0, "" + serr);
        // Spica sits at roughly 203-204 degrees tropical longitude around the year 2000
        assertTrue(xx[0] > 200. && xx[0] < 207., "Spica longitude: " + xx[0]);
        assertTrue(star.toString().toLowerCase().contains("spica"),
                "the star name is resolved to its catalogue form: " + star);
    }

    @Test
    void swe_fixstar_ut_agreesWithSweFixstar() {
        double jdET = J2000;
        double jdUT = jdET - getSwephExp().swe_deltat(jdET);

        double[] et = new double[6];
        getSwephExp().swe_fixstar(new StringBuilder("Spica"), jdET, SEFLG_SWIEPH, et, new StringBuilder());

        double[] ut = new double[6];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_fixstar_ut(new StringBuilder("Spica"), jdUT, SEFLG_SWIEPH, ut, serr);

        assertTrue(ret >= 0, "" + serr);
        assertEquals(et[0], ut[0], 1e-6);
    }

    @Test
    void swe_fixstar_mag_returnsAPlausibleMagnitudeForSpica() {
        double[] mag = new double[1];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_fixstar_mag(new StringBuilder("Spica"), mag, serr);

        assertTrue(ret >= 0, "" + serr);
        assertTrue(mag[0] > 0.5 && mag[0] < 1.5, "Spica's magnitude is about 1.0: " + mag[0]);
    }

    @Test
    void swe_fixstar_reportsAnErrorForAnUnknownStarName() {
        double[] xx = new double[6];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_fixstar(new StringBuilder("NoSuchStarAtAllXyz"), J2000,
                SEFLG_SWIEPH, xx, serr);

        assertTrue(ret < 0, "an unknown star must fail");
        assertTrue(serr.length() > 0);
    }

    @Test
    void swe_fixstar2_agreesWithSweFixstar() {
        // "2" is only a faster indexed lookup into the same sefstars.txt - same star data,
        // same position
        double[] x1 = new double[6];
        getSwephExp().swe_fixstar(new StringBuilder("Spica"), J2000, SEFLG_SWIEPH, x1, new StringBuilder());

        double[] x2 = new double[6];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_fixstar2(new StringBuilder("Spica"), J2000, SEFLG_SWIEPH, x2, serr);

        assertTrue(ret >= 0, "" + serr);
        assertEquals(x1[0], x2[0], 1e-9);
    }

    @Test
    void swe_fixstar2_ut_agreesWithSweFixstar2() {
        double jdET = J2000;
        double jdUT = jdET - getSwephExp().swe_deltat(jdET);

        double[] et = new double[6];
        getSwephExp().swe_fixstar2(new StringBuilder("Spica"), jdET, SEFLG_SWIEPH, et, new StringBuilder());

        double[] ut = new double[6];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_fixstar2_ut(new StringBuilder("Spica"), jdUT, SEFLG_SWIEPH, ut, serr);

        assertTrue(ret >= 0, "" + serr);
        assertEquals(et[0], ut[0], 1e-6);
    }

    @Test
    void swe_fixstar2_mag_agreesWithSweFixstarMag() {
        double[] mag1 = new double[1];
        getSwephExp().swe_fixstar_mag(new StringBuilder("Spica"), mag1, new StringBuilder());

        double[] mag2 = new double[1];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_fixstar2_mag(new StringBuilder("Spica"), mag2, serr);

        assertTrue(ret >= 0, "" + serr);
        assertEquals(mag1[0], mag2[0], 1e-9);
    }

    // ==================================================================== metadata

    @Test
    void swe_version_reportsTheKnownSwissEphemerisVersion() {
        String v = getSwephExp().swe_version();
        assertNotNull(v);
        assertTrue(v.startsWith("2.10"), v);
    }

    @Test
    void swe_get_library_path_reportsSomePath() {
        String p = getSwephExp().swe_get_library_path();
        assertNotNull(p);
        assertFalse(p.isEmpty());
    }

    @Test
    void swe_get_planet_name_namesTheClassicalPlanets() {
        assertEquals("Sun", getSwephExp().swe_get_planet_name(SE_SUN));
        assertEquals("Moon", getSwephExp().swe_get_planet_name(SE_MOON));
        assertEquals("Mars", getSwephExp().swe_get_planet_name(SE_MARS));
        assertTrue(getSwephExp().swe_get_planet_name(SE_TRUE_NODE).toLowerCase().contains("node"));
    }

    @Test
    void swe_get_current_file_data_namesThePlanetFileOnceOneHasBeenRead() {
        calc(J2000, SE_MARS, SEFLG_SWIEPH);   // make sure a planet file is open

        double[] tfstart = new double[1];
        double[] tfend = new double[1];
        int[] denum = new int[1];
        String name = getSwephExp().swe_get_current_file_data(0, tfstart, tfend, denum);

        assertNotNull(name, "a planet file should be open");
        assertTrue(name.toLowerCase().contains("sepl"), name);
        assertTrue(tfstart[0] < J2000 && tfend[0] > J2000,
                "the file's range should cover J2000: " + tfstart[0] + ".." + tfend[0]);
        assertTrue(denum[0] > 0, "a DE number should be reported: " + denum[0]);
    }

    /** no real .jpl file is shipped here; the call itself must not throw or corrupt state */
    @Test
    void swe_set_jpl_file_acceptsAFileNameWithoutThrowing() {
        getSwephExp().swe_set_jpl_file("de431.eph");
        // switch back to Swiss Ephemeris data so later tests are unaffected
        double[] xx = calc(J2000, SE_SUN, SEFLG_SWIEPH);
        assertTrue(xx[0] >= 0. && xx[0] < 360.);
    }

    /**
     * {@code swe_close()} releases the open ephemeris files; the library must still work
     * afterwards, reopening whatever it needs.
     */
    @Test
    void swe_close_leavesTheLibraryUsableAfterwards() {
        double[] before = calc(J2000, SE_SUN, SEFLG_SWIEPH);

        getSwephExp().swe_close();
        getSwephExp().swe_set_ephe_path(System.getProperty("swe.ephe", "ephe"));

        double[] after = calc(J2000, SE_SUN, SEFLG_SWIEPH);
        assertEquals(before[0], after[0], 1e-9, "the same calculation must still agree");
    }
}
