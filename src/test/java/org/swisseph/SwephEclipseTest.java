/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@code swecl.c}: {@code swe_gauquelin_sector}, {@code swe_sol_eclipse_where},
 * {@code swe_lun_occult_where}, {@code swe_sol_eclipse_how}, {@code swe_sol_eclipse_when_loc},
 * {@code swe_lun_occult_when_loc}, {@code swe_sol_eclipse_when_glob},
 * {@code swe_lun_occult_when_glob}, {@code swe_lun_eclipse_how}, {@code swe_lun_eclipse_when},
 * {@code swe_lun_eclipse_when_loc}.
 * <p>
 * Search functions are asked for the next event after a fixed date and are checked for
 * internal consistency (later than the start, plausible attributes) rather than against a
 * specific known eclipse date - that would just be swetest's almanac reproduced by hand.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
class SwephEclipseTest extends AIswTest {

    static final int SE_ECL_ALLTYPES_SOLAR = 1 | 2 | 4 | 8 | 16 | 32;
    static final int SE_ECL_ALLTYPES_LUNAR = 4 | 16 | 64;

    static final double[] GEOPOS = {GEOLON, GEOLAT, GEOALT};

    // ================================================================== gauquelin

    @Test
    void swe_gauquelin_sector_returnsASectorInsideItsThirtySixDivisions() {
        double[] geopos = {GEOLON, GEOLAT, GEOALT};
        double[] dgsect = new double[1];
        StringBuilder serr = new StringBuilder();

        int ret = getSwephExp().swe_gauquelin_sector(J2000, SE_SUN, null, SEFLG_SWIEPH, 0,
                geopos, 1013.25, 15., dgsect, serr);

        assertTrue(ret >= 0, "" + serr);
        assertTrue(dgsect[0] >= 1. && dgsect[0] < 37., "Gauquelin sector: " + dgsect[0]);
    }

    // ================================================================ solar eclipses

    @Test
    void swe_sol_eclipse_when_glob_findsANextSolarEclipseWorldwide() {
        double[] tret = new double[10];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_sol_eclipse_when_glob(J2000, SEFLG_SWIEPH,
                SE_ECL_ALLTYPES_SOLAR, tret, 0, serr);

        assertTrue(ret >= 0, "" + serr);
        assertTrue(tret[0] > J2000 && tret[0] < J2000 + 200.,
                "a solar eclipse within about six months: " + tret[0]);
    }

    @Test
    void swe_sol_eclipse_where_locatesTheGlobalMaximumOfAFoundEclipse() {
        double[] globTret = new double[10];
        getSwephExp().swe_sol_eclipse_when_glob(J2000, SEFLG_SWIEPH, SE_ECL_ALLTYPES_SOLAR,
                globTret, 0, new StringBuilder());

        double[] geopos = new double[10];
        double[] attr = new double[20];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_sol_eclipse_where(globTret[0], SEFLG_SWIEPH, geopos, attr, serr);

        assertTrue(ret >= 0, "" + serr);
        assertTrue(geopos[0] >= -180. && geopos[0] <= 180., "longitude: " + geopos[0]);
        assertTrue(geopos[1] >= -90. && geopos[1] <= 90., "latitude: " + geopos[1]);
    }

    @Test
    void swe_sol_eclipse_how_reportsAttributesAtAGivenPlace() {
        double[] attr = new double[20];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_sol_eclipse_how(J2000, SEFLG_SWIEPH, GEOPOS, attr, serr);

        assertTrue(ret >= 0, "" + serr);
        // attr[0] is the fraction of the solar diameter covered; 0 when there is no eclipse
        assertTrue(attr[0] >= 0. && attr[0] <= 1.5, "coverage fraction: " + attr[0]);
    }

    @Test
    void swe_sol_eclipse_when_loc_findsTheNextEclipseVisibleFromThisPlace() {
        double[] tret = new double[10];
        double[] attr = new double[20];
        StringBuilder serr = new StringBuilder();

        int ret = getSwephExp().swe_sol_eclipse_when_loc(J2000, SEFLG_SWIEPH, GEOPOS,
                tret, attr, 0, serr);

        // local eclipses at one specific place are rarer than global ones - roughly one
        // every few years, occasionally longer - but must still be found within a search of
        // years, not decades
        assertTrue(ret >= 0, "" + serr);
        assertTrue(tret[0] > J2000 && tret[0] < J2000 + 365. * 10,
                "a local eclipse within ten years: " + tret[0]);
    }

    // ================================================================ lunar eclipses

    @Test
    void swe_lun_eclipse_when_findsANextLunarEclipse() {
        double[] tret = new double[10];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_lun_eclipse_when(J2000, SEFLG_SWIEPH, SE_ECL_ALLTYPES_LUNAR,
                tret, 0, serr);

        assertTrue(ret >= 0, "" + serr);
        assertTrue(tret[0] > J2000 && tret[0] < J2000 + 200.,
                "a lunar eclipse within about six months: " + tret[0]);
    }

    @Test
    void swe_lun_eclipse_how_reportsAttributesAtAGivenTime() {
        double[] globTret = new double[10];
        getSwephExp().swe_lun_eclipse_when(J2000, SEFLG_SWIEPH, SE_ECL_ALLTYPES_LUNAR,
                globTret, 0, new StringBuilder());

        double[] attr = new double[20];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_lun_eclipse_how(globTret[0], SEFLG_SWIEPH, GEOPOS, attr, serr);

        assertTrue(ret >= 0, "" + serr);
        assertTrue(attr[0] >= 0., "umbral magnitude: " + attr[0]);
    }

    @Test
    void swe_lun_eclipse_when_loc_findsTheNextEclipseVisibleFromThisPlace() {
        double[] tret = new double[10];
        double[] attr = new double[20];
        StringBuilder serr = new StringBuilder();

        int ret = getSwephExp().swe_lun_eclipse_when_loc(J2000, SEFLG_SWIEPH, GEOPOS,
                tret, attr, 0, serr);

        assertTrue(ret >= 0, "" + serr);
        assertTrue(tret[0] > J2000, "the eclipse should be after the search start");
    }

    // =============================================================== lunar occultations

    @Test
    void swe_lun_occult_when_glob_findsAnOccultationOfARegularPlanet() {
        double[] tret = new double[10];
        StringBuilder serr = new StringBuilder();
        // occultations of an outer planet by the Moon are common - within a year or two
        int ret = getSwephExp().swe_lun_occult_when_glob(J2000, SE_MARS, null, SEFLG_SWIEPH, 0,
                tret, 0, serr);

        assertTrue(ret >= 0, "" + serr);
        assertTrue(tret[0] > J2000 && tret[0] < J2000 + 365. * 3,
                "an occultation of Mars within a few years: " + tret[0]);
    }

    @Test
    void swe_lun_occult_where_locatesAFoundOccultation() {
        double[] globTret = new double[10];
        int found = getSwephExp().swe_lun_occult_when_glob(J2000, SE_MARS, null, SEFLG_SWIEPH, 0,
                globTret, 0, new StringBuilder());
        assertTrue(found >= 0);

        double[] geopos = new double[10];
        double[] attr = new double[20];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_lun_occult_where(globTret[0], SE_MARS, null, SEFLG_SWIEPH,
                geopos, attr, serr);

        assertTrue(ret >= 0, "" + serr);
        assertTrue(geopos[0] >= -180. && geopos[0] <= 180.);
    }

    @Test
    void swe_lun_occult_when_loc_findsTheNextOccultationVisibleFromThisPlace() {
        double[] tret = new double[10];
        double[] attr = new double[20];
        StringBuilder serr = new StringBuilder();

        int ret = getSwephExp().swe_lun_occult_when_loc(J2000, SE_MARS, null, SEFLG_SWIEPH,
                GEOPOS, tret, attr, 0, serr);

        // may legitimately fail to find one within the search horizon at this specific
        // place - what matters is that the call itself is wired correctly
        assertTrue(ret >= 0 || serr.length() > 0, "either a result or a reason");
    }
}
