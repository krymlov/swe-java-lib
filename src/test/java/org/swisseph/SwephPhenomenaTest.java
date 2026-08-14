/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@code swecl.c}: {@code swe_pheno}(+{@code _ut}), {@code swe_refrac},
 * {@code swe_refrac_extended}, {@code swe_set_lapse_rate}, {@code swe_azalt},
 * {@code swe_azalt_rev}, {@code swe_rise_trans}, {@code swe_rise_trans_true_hor}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
class SwephPhenomenaTest extends AIswTest {

    static final int SE_ECL2HOR = 0;
    static final int SE_TRUE_TO_APP = 0;
    static final int SE_APP_TO_TRUE = 1;

    static final double[] GEOPOS = {GEOLON, GEOLAT, GEOALT};

    // ==================================================================== phenomena

    @Test
    void swe_pheno_reportsAPlausiblePhaseAngleAndIllumination() {
        // attr[]: 0 phase angle, 1 illuminated fraction, 2 elongation, 3 apparent diameter,
        // 4 apparent magnitude - swecl.c's comment right above swe_pheno()
        double[] attr = new double[20];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_pheno(J2000, SE_VENUS, SEFLG_SWIEPH, attr, serr);

        assertTrue(ret >= 0, "" + serr);
        assertTrue(attr[0] >= 0. && attr[0] <= 180., "phase angle: " + attr[0]);
        assertTrue(attr[1] >= 0. && attr[1] <= 1., "illuminated fraction: " + attr[1]);
        assertTrue(attr[2] >= 0. && attr[2] <= 180., "elongation: " + attr[2]);
        assertTrue(attr[4] > -10. && attr[4] < 10., "apparent magnitude: " + attr[4]);
    }

    @Test
    void swe_pheno_ut_agreesWithSwePheno() {
        double jdUT = J2000 - getSwephExp().swe_deltat(J2000);
        double[] attrEt = new double[20];
        getSwephExp().swe_pheno(J2000, SE_MARS, SEFLG_SWIEPH, attrEt, new StringBuilder());

        double[] attrUt = new double[20];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_pheno_ut(jdUT, SE_MARS, SEFLG_SWIEPH, attrUt, serr);

        assertTrue(ret >= 0, "" + serr);
        assertEquals(attrEt[0], attrUt[0], 1e-4, "phase angle");
    }

    // =============================================================== refraction

    @Test
    void swe_refrac_bendsALowAltitudeStarUpward() {
        // apparent-to-true and true-to-apparent must be each other's rough inverse near the
        // horizon, where refraction is largest
        double trueAlt = 0.5;
        double apparent = getSwephExp().swe_refrac(trueAlt, 1013.25, 15., SE_TRUE_TO_APP);
        assertTrue(apparent > trueAlt, "refraction lifts a low object: " + apparent);

        double backToTrue = getSwephExp().swe_refrac(apparent, 1013.25, 15., SE_APP_TO_TRUE);
        assertEquals(trueAlt, backToTrue, 0.01);
    }

    @Test
    void swe_refrac_isNegligibleStraightOverhead() {
        double apparent = getSwephExp().swe_refrac(89.9, 1013.25, 15., SE_TRUE_TO_APP);
        assertEquals(89.9, apparent, 0.01, "no refraction near the zenith");
    }

    @Test
    void swe_refrac_extended_agreesWithSweRefracAtSeaLevel() {
        double plain = getSwephExp().swe_refrac(1., 1013.25, 15., SE_TRUE_TO_APP);

        double[] dret = new double[4];
        double extended = getSwephExp().swe_refrac_extended(1., 0., 1013.25, 15., 0.0065,
                SE_TRUE_TO_APP, dret);

        assertEquals(plain, extended, 0.05, "close agreement at sea level");
    }

    @Test
    void swe_set_lapse_rate_doesNotDisruptLaterRefractionCalls() {
        getSwephExp().swe_set_lapse_rate(0.0065);
        double a1 = getSwephExp().swe_refrac(1., 1013.25, 15., SE_TRUE_TO_APP);

        getSwephExp().swe_set_lapse_rate(0.01);
        double a2 = getSwephExp().swe_refrac_extended(1., 0., 1013.25, 15., 0.01,
                SE_TRUE_TO_APP, new double[4]);

        assertTrue(a1 > 0. && a2 > 0.);
    }

    // ============================================================= azimuth/altitude

    @Test
    void swe_azalt_andSweAzaltRev_roundTrip() {
        double jdUT = J2000 - getSwephExp().swe_deltat(J2000);
        double[] xin = {123.4, 5.6};   // ecliptic longitude, latitude
        double[] xaz = new double[3];

        getSwephExp().swe_azalt(jdUT, SE_ECL2HOR, GEOPOS, 1013.25, 15., xin, xaz);
        assertTrue(xaz[1] >= -90. && xaz[1] <= 90., "altitude: " + xaz[1]);

        double[] back = new double[2];
        getSwephExp().swe_azalt_rev(jdUT, SE_ECL2HOR, GEOPOS, new double[]{xaz[0], xaz[1]}, back);

        assertEquals(xin[0], back[0], 1e-3, "longitude");
        assertEquals(xin[1], back[1], 1e-3, "latitude");
    }

    // ================================================================ rise/transit

    @Test
    void swe_rise_trans_findsASunriseAfterTheStartDate() {
        double jdUT = J2000 - getSwephExp().swe_deltat(J2000);
        double[] tret = new double[1];
        StringBuilder serr = new StringBuilder();

        int ret = getSwephExp().swe_rise_trans(jdUT, SE_SUN, null, SEFLG_SWIEPH, SE_CALC_RISE,
                GEOPOS, 1013.25, 15., tret, serr);

        assertTrue(ret >= 0, "" + serr);
        assertTrue(tret[0] > jdUT && tret[0] < jdUT + 2., "a sunrise within two days: " + tret[0]);
    }

    @Test
    void swe_rise_trans_true_hor_agreesWithSweRiseTransAtZeroHorizonHeight() {
        double jdUT = J2000 - getSwephExp().swe_deltat(J2000);

        double[] plain = new double[1];
        getSwephExp().swe_rise_trans(jdUT, SE_SUN, null, SEFLG_SWIEPH, SE_CALC_RISE, GEOPOS,
                1013.25, 15., plain, new StringBuilder());

        double[] withHor = new double[1];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_rise_trans_true_hor(jdUT, SE_SUN, null, SEFLG_SWIEPH,
                SE_CALC_RISE, GEOPOS, 1013.25, 15., 0., withHor, serr);

        assertTrue(ret >= 0, "" + serr);
        assertEquals(plain[0], withHor[0], 1e-4, "zero horizon height reproduces the plain call");
    }

    @Test
    void swe_rise_trans_setAndRiseAreAboutHalfADayApart() {
        double jdUT = J2000 - getSwephExp().swe_deltat(J2000);
        double[] rise = new double[1];
        double[] set = new double[1];

        getSwephExp().swe_rise_trans(jdUT, SE_SUN, null, SEFLG_SWIEPH, SE_CALC_RISE, GEOPOS,
                1013.25, 15., rise, new StringBuilder());
        getSwephExp().swe_rise_trans(jdUT, SE_SUN, null, SEFLG_SWIEPH, SE_CALC_SET, GEOPOS,
                1013.25, 15., set, new StringBuilder());

        double gap = Math.abs(set[0] - rise[0]);
        assertTrue(gap > 0.2 && gap < 0.8, "rise/set gap in days: " + gap);
    }
}
