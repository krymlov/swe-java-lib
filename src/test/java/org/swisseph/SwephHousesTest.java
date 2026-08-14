/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@code swehouse.c}: {@code swe_houses}, {@code swe_houses_ex}, {@code swe_houses_ex2},
 * {@code swe_houses_armc}, {@code swe_houses_armc_ex2}, {@code swe_house_pos},
 * {@code swe_house_name}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
class SwephHousesTest extends AIswTest {

    static final int SE_ASC = 0;
    static final int SE_MC = 1;
    static final int SE_ARMC = 2;
    static final char PLACIDUS = 'P';

    @Test
    void swe_houses_placesTheAscendantInsideTheFirstCusp() {
        double[] cusps = new double[13];
        double[] ascmc = new double[10];
        int ret = getSwephExp().swe_houses(J2000, GEOLAT, GEOLON, PLACIDUS, cusps, ascmc);

        assertTrue(ret >= 0);
        // house 1 always starts exactly at the ascendant
        assertEquals(ascmc[SE_ASC], cusps[1], 1e-9);
        for (int h = 1; h <= 12; h++) {
            assertTrue(cusps[h] >= 0. && cusps[h] < 360., "cusp " + h + ": " + cusps[h]);
        }
    }

    @Test
    void swe_houses_ex_withNoFlagsAgreesWithThePlainForm() {
        double[] c1 = new double[13], a1 = new double[10];
        getSwephExp().swe_houses(J2000, GEOLAT, GEOLON, PLACIDUS, c1, a1);

        double[] c2 = new double[13], a2 = new double[10];
        int ret = getSwephExp().swe_houses_ex(J2000, 0, GEOLAT, GEOLON, PLACIDUS, c2, a2);

        assertTrue(ret >= 0);
        for (int h = 1; h <= 12; h++) {
            assertEquals(c1[h], c2[h], 1e-9, "cusp " + h);
        }
        assertEquals(a1[SE_ASC], a2[SE_ASC], 1e-9);
    }

    @Test
    void swe_houses_ex2_alsoReturnsCuspSpeeds() {
        double[] cusps = new double[13], ascmc = new double[10];
        double[] cuspSpeed = new double[13], ascmcSpeed = new double[10];
        StringBuilder serr = new StringBuilder();

        int ret = getSwephExp().swe_houses_ex2(J2000, 0, GEOLAT, GEOLON, PLACIDUS,
                cusps, ascmc, cuspSpeed, ascmcSpeed, serr);

        assertTrue(ret >= 0, "" + serr);
        // the ascendant sweeps the whole circle in a day, so its speed is on that order
        assertTrue(ascmcSpeed[SE_ASC] > 50. && ascmcSpeed[SE_ASC] < 3000.,
                "ascendant speed: " + ascmcSpeed[SE_ASC]);
        assertTrue(ascmcSpeed[SE_MC] > 50. && ascmcSpeed[SE_MC] < 3000.);
    }

    @Test
    void swe_houses_armc_reproducesTheArmcThatSweHousesComputed() {
        double[] cusps = new double[13], ascmc = new double[10];
        getSwephExp().swe_houses(J2000, GEOLAT, GEOLON, PLACIDUS, cusps, ascmc);
        double armc = ascmc[SE_ARMC];

        // the true obliquity of date, as swe_houses used internally: pull it via swe_calc
        double eps = calc(J2000, SE_ECL_NUT, SEFLG_SWIEPH)[0];

        double[] c2 = new double[13], a2 = new double[10];
        int ret = getSwephExp().swe_houses_armc(armc, GEOLAT, eps, PLACIDUS, c2, a2);

        assertTrue(ret >= 0);
        assertEquals(cusps[1], c2[1], 1e-4, "cusp 1 (the ascendant) should reproduce");
        assertEquals(cusps[10], c2[10], 1e-4, "cusp 10 (the MC) should reproduce");
    }

    @Test
    void swe_houses_armc_ex2_alsoReturnsSpeeds() {
        double eps = calc(J2000, SE_ECL_NUT, SEFLG_SWIEPH)[0];
        double[] cusps = new double[13], ascmc = new double[10];
        double[] cuspSpeed = new double[13], ascmcSpeed = new double[10];
        StringBuilder serr = new StringBuilder();

        int ret = getSwephExp().swe_houses_armc_ex2(100., GEOLAT, eps, PLACIDUS,
                cusps, ascmc, cuspSpeed, ascmcSpeed, serr);

        assertTrue(ret >= 0, "" + serr);
        assertTrue(ascmcSpeed[SE_ASC] > 0., "the ascendant speed should be positive: "
                + ascmcSpeed[SE_ASC]);
    }

    @Test
    void swe_house_pos_placesTheAscendantAtTheStartOfHouseOne() {
        double[] cusps = new double[13], ascmc = new double[10];
        getSwephExp().swe_houses(J2000, GEOLAT, GEOLON, PLACIDUS, cusps, ascmc);
        double eps = calc(J2000, SE_ECL_NUT, SEFLG_SWIEPH)[0];

        double[] xpin = {ascmc[SE_ASC], 0.};
        StringBuilder serr = new StringBuilder();
        double pos = getSwephExp().swe_house_pos(ascmc[SE_ARMC], GEOLAT, eps, PLACIDUS, xpin, serr);

        assertTrue(serr.length() == 0, "" + serr);
        assertEquals(1.0, pos, 1e-4, "the ascendant itself must sit at house position 1.0");
    }

    @Test
    void swe_house_pos_placesAPlanetInThePlausibleHouseRange() {
        double[] cusps = new double[13], ascmc = new double[10];
        getSwephExp().swe_houses(J2000, GEOLAT, GEOLON, PLACIDUS, cusps, ascmc);
        double eps = calc(J2000, SE_ECL_NUT, SEFLG_SWIEPH)[0];

        double[] mars = calc(J2000, SE_MARS, SEFLG_SWIEPH);
        double[] xpin = {mars[0], 0.};
        StringBuilder serr = new StringBuilder();
        double pos = getSwephExp().swe_house_pos(ascmc[SE_ARMC], GEOLAT, eps, PLACIDUS, xpin, serr);

        assertTrue(pos >= 1.0 && pos < 13.0, "house position out of range: " + pos);
    }

    @Test
    void swe_house_name_namesPlacidusAndKoch() {
        assertNotNull(getSwephExp().swe_house_name('P'));
        assertTrue(getSwephExp().swe_house_name('P').toLowerCase().contains("placidus"));
        assertTrue(getSwephExp().swe_house_name('K').toLowerCase().contains("koch"));
        assertTrue(getSwephExp().swe_house_name('W').toLowerCase().contains("sign")
                || getSwephExp().swe_house_name('W').toLowerCase().contains("equal"));
    }

    @Test
    void swe_houses_returnsAnErrorPastThePolarCircleForPlacidus() {
        // Placidus and Koch are undefined at extreme latitudes
        double[] cusps = new double[13], ascmc = new double[10];
        int ret = getSwephExp().swe_houses(J2000, 89., 0., PLACIDUS, cusps, ascmc);
        assertTrue(ret < 0, "Placidus at 89 degrees latitude should fail");
    }
}
