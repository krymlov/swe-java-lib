/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@code swe_set_sid_mode}, {@code swe_get_ayanamsa_ex}(+{@code _ut}), {@code swe_get_ayanamsa}
 * (+{@code _ut}), {@code swe_get_ayanamsa_name}.
 * <p>
 * {@code swe_set_sid_mode} is process-global state, so every test resets it afterwards to
 * avoid leaking the sidereal mode into unrelated tests elsewhere in the suite.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
class SwephAyanamsaTest extends AIswTest {

    @AfterEach
    void backToTropical() {
        getSwephExp().swe_set_sid_mode(SE_SIDM_FAGAN_BRADLEY, 0., 0.);
    }

    @Test
    void swe_get_ayanamsa_lahiriIsAboutTwentyFourDegreesInTheYear2000() {
        getSwephExp().swe_set_sid_mode(SE_SIDM_LAHIRI, 0., 0.);
        double aya = getSwephExp().swe_get_ayanamsa(J2000);
        assertTrue(aya > 23. && aya < 25., "Lahiri ayanamsa at J2000: " + aya);
    }

    @Test
    void swe_get_ayanamsa_ut_agreesWithSweGetAyanamsa() {
        getSwephExp().swe_set_sid_mode(SE_SIDM_LAHIRI, 0., 0.);
        double jdET = J2000;
        double jdUT = jdET - getSwephExp().swe_deltat(jdET);

        double et = getSwephExp().swe_get_ayanamsa(jdET);
        double ut = getSwephExp().swe_get_ayanamsa_ut(jdUT);
        assertEquals(et, ut, 1e-6);
    }

    /**
     * {@code swe_get_ayanamsa()} never includes nutation (sweph.c hardcodes it), so it agrees
     * with {@code swe_get_ayanamsa_ex()} only when the caller also asks for
     * {@code SEFLG_NONUT}. Without that flag {@code _ex} adds nutation and the two must
     * differ by the nutation amplitude - never be equal.
     */
    @Test
    void swe_get_ayanamsa_isTheNonutFormOfSweGetAyanamsaEx() {
        getSwephExp().swe_set_sid_mode(SE_SIDM_LAHIRI, 0., 0.);
        double plain = getSwephExp().swe_get_ayanamsa(J2000);

        double[] noNut = new double[1];
        getSwephExp().swe_get_ayanamsa_ex(J2000, SEFLG_SWIEPH | SEFLG_NONUT, noNut, new StringBuilder());
        assertEquals(plain, noNut[0], 1e-6, "swe_get_ayanamsa() is the SEFLG_NONUT form");

        double[] withNut = new double[1];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_get_ayanamsa_ex(J2000, SEFLG_SWIEPH, withNut, serr);
        assertTrue(ret >= 0, "" + serr);
        assertTrue(Math.abs(plain - withNut[0]) * 3600. > 1.,
                "without SEFLG_NONUT the two must differ by roughly the nutation amplitude");
    }

    @Test
    void swe_get_ayanamsa_ex_ut_agreesWithSweGetAyanamsaEx() {
        getSwephExp().swe_set_sid_mode(SE_SIDM_LAHIRI, 0., 0.);
        double jdET = J2000;
        double jdUT = jdET - getSwephExp().swe_deltat(jdET);

        double[] et = new double[1];
        getSwephExp().swe_get_ayanamsa_ex(jdET, SEFLG_SWIEPH, et, new StringBuilder());

        double[] ut = new double[1];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_get_ayanamsa_ex_ut(jdUT, SEFLG_SWIEPH, ut, serr);

        assertTrue(ret >= 0, "" + serr);
        assertEquals(et[0], ut[0], 1e-6);
    }

    /**
     * {@code SEFLG_NONUT} removes the nutation term, so the mean and true forms of the same
     * ayanamsa must differ by roughly the nutation amplitude - a few tens of arc seconds,
     * never zero.
     */
    @Test
    void swe_get_ayanamsa_ex_nonutDiffersFromTheDefaultByTheNutationAmplitude() {
        getSwephExp().swe_set_sid_mode(SE_SIDM_LAHIRI, 0., 0.);
        double[] withNut = new double[1];
        getSwephExp().swe_get_ayanamsa_ex(J2000, SEFLG_SWIEPH, withNut, new StringBuilder());

        double[] noNut = new double[1];
        getSwephExp().swe_get_ayanamsa_ex(J2000, SEFLG_SWIEPH | SEFLG_NONUT, noNut, new StringBuilder());

        double diffArcsec = Math.abs(withNut[0] - noNut[0]) * 3600.;
        assertTrue(diffArcsec > 0.1 && diffArcsec < 30.,
                "nutation-sized difference expected, got " + diffArcsec + "\"");
    }

    @Test
    void swe_get_ayanamsa_name_namesLahiriAndFaganBradley() {
        assertNotNull(getSwephExp().swe_get_ayanamsa_name(SE_SIDM_LAHIRI));
        assertTrue(getSwephExp().swe_get_ayanamsa_name(SE_SIDM_LAHIRI).toLowerCase().contains("lahiri"));
        assertTrue(getSwephExp().swe_get_ayanamsa_name(SE_SIDM_FAGAN_BRADLEY).toLowerCase()
                .contains("fagan"));
    }

    /**
     * Switching {@code swe_set_sid_mode} changes what {@code SEFLG_SIDEREAL} subtracts from a
     * tropical longitude - the whole point of the call - so two different ayanamsas must move
     * the same planet's sidereal longitude by their own difference, with the sign that
     * {@code sidereal = tropical - ayanamsa} implies: a larger ayanamsa gives a smaller
     * sidereal longitude.
     */
    @Test
    void swe_set_sid_mode_changesTheSiderealLongitudeByTheAyanamsaDifference() {
        double[] daya = new double[1];

        getSwephExp().swe_set_sid_mode(SE_SIDM_LAHIRI, 0., 0.);
        getSwephExp().swe_get_ayanamsa_ex(J2000, SEFLG_SWIEPH, daya, new StringBuilder());
        double lahiriAya = daya[0];
        double[] lahiri = calc(J2000, SE_SUN, SEFLG_SWIEPH | SEFLG_SIDEREAL);

        getSwephExp().swe_set_sid_mode(SE_SIDM_FAGAN_BRADLEY, 0., 0.);
        getSwephExp().swe_get_ayanamsa_ex(J2000, SEFLG_SWIEPH, daya, new StringBuilder());
        double faganAya = daya[0];
        double[] fagan = calc(J2000, SE_SUN, SEFLG_SWIEPH | SEFLG_SIDEREAL);

        double expectedShift = lahiriAya - faganAya;   // fagan.lon - lahiri.lon
        double actualShift = normDeg(fagan[0] - lahiri[0]);
        actualShift = actualShift > 180. ? actualShift - 360. : actualShift;

        assertEquals(expectedShift, actualShift, 1e-4);
    }
}
