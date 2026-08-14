/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@code swe_deltat}, {@code swe_deltat_ex}, {@code swe_time_equ}, {@code swe_lmt_to_lat},
 * {@code swe_lat_to_lmt}, {@code swe_sidtime0}, {@code swe_sidtime},
 * {@code swe_set_interpolate_nut}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
class SwephTimeTest extends AIswTest {

    @AfterEach
    void turnInterpolationOff() {
        getSwephExp().swe_set_interpolate_nut(0);
    }

    @Test
    void swe_deltat_isASmallFractionOfADayAroundTheYear2000() {
        double dt = getSwephExp().swe_deltat(J2000);
        // delta t is on the order of tens of seconds around 2000 - well under a minute
        assertTrue(dt > 0. && dt < 120. / 86400., "delta t: " + dt * 86400. + " s");
    }

    @Test
    void swe_deltat_ex_agreesWithSweDeltat() {
        StringBuilder serr = new StringBuilder();
        double dtEx = getSwephExp().swe_deltat_ex(J2000, SEFLG_SWIEPH, serr);
        double dt = getSwephExp().swe_deltat(J2000);

        assertEquals(dt, dtEx, 1e-9, "" + serr);
    }

    @Test
    void swe_time_equ_isASmallFractionOfADay() {
        double[] te = new double[1];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_time_equ(J2000, te, serr);

        assertTrue(ret >= 0, "" + serr);
        // the equation of time never exceeds about 16 minutes
        assertTrue(Math.abs(te[0]) < 20. / 1440., "equation of time: " + te[0] * 1440. + " min");
    }

    @Test
    void swe_lmt_to_lat_andSweLatToLmt_roundTrip() {
        double[] lat = new double[1];
        StringBuilder serr = new StringBuilder();
        int ret1 = getSwephExp().swe_lmt_to_lat(J2000, GEOLON, lat, serr);
        assertTrue(ret1 >= 0, "" + serr);

        double[] lmt = new double[1];
        int ret2 = getSwephExp().swe_lat_to_lmt(lat[0], GEOLON, lmt, serr);
        assertTrue(ret2 >= 0, "" + serr);

        assertEquals(J2000, lmt[0], 1e-6, "LMT -> LAT -> LMT must return the same instant");
    }

    @Test
    void swe_sidtime0_isWithinTheDailyRangeOfSiderealTime() {
        double eps = calc(J2000, SE_ECL_NUT, SEFLG_SWIEPH)[0];
        double nutl = 0.;   // an approximation is fine, this only checks the range
        double st = getSwephExp().swe_sidtime0(J2000 - getSwephExp().swe_deltat(J2000), eps, nutl);

        assertTrue(st >= 0. && st < 24., "sidereal time in hours: " + st);
    }

    @Test
    void swe_sidtime_isWithinTheDailyRangeOfSiderealTime() {
        double st = getSwephExp().swe_sidtime(J2000 - getSwephExp().swe_deltat(J2000));
        assertTrue(st >= 0. && st < 24., "sidereal time in hours: " + st);
    }

    @Test
    void swe_sidtime_andSweSidtime0_agreeAtTheirOwnObliquityAndNutation() {
        double jdUT = J2000 - getSwephExp().swe_deltat(J2000);
        double eps = calc(J2000, SE_ECL_NUT, SEFLG_SWIEPH)[0];
        // swe_calc(SE_ECL_NUT) returns true obliquity in xx[0] and nutation in longitude in
        // xx[2], in degrees
        double[] xx = calc(J2000, SE_ECL_NUT, SEFLG_SWIEPH);

        double st = getSwephExp().swe_sidtime(jdUT);
        double st0 = getSwephExp().swe_sidtime0(jdUT, xx[0], xx[2]);

        assertEquals(st, st0, 1e-6);
    }

    /** the call itself must not throw or corrupt subsequent calculations */
    @Test
    void swe_set_interpolate_nut_doesNotDisruptLaterCalculations() {
        getSwephExp().swe_set_interpolate_nut(1);
        double[] xx = calc(J2000, SE_MOON, SEFLG_SWIEPH);
        assertTrue(xx[0] >= 0. && xx[0] < 360.);

        getSwephExp().swe_set_interpolate_nut(0);
        double[] xx2 = calc(J2000, SE_MOON, SEFLG_SWIEPH);
        assertEquals(xx[0], xx2[0], 1e-6,
                "interpolated nutation is an approximation, but not a several-arcsecond one");
    }
}
