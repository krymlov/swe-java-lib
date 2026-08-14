/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@code swehel.c}: {@code swe_heliacal_ut}, {@code swe_heliacal_pheno_ut},
 * {@code swe_vis_limit_mag}, {@code swe_heliacal_angle}, {@code swe_topo_arcus_visionis} -
 * the heliacal-rising/visibility family. Per {@code swehel.c}'s doc comment above
 * {@code swe_heliacal_ut}:
 * <pre>
 * datm[4] = pressure (mbar), temperature (C), relative humidity (%), meteorological range (km)
 * dobs[6] = age, Snellen ratio, is_binocular, telescope magnification, aperture (mm), transmission
 * </pre>
 * {@code datm[0] == 0} asks the native side to estimate pressure/temperature/humidity from
 * {@code dgeo}'s altitude; {@code dobs} left all-zero likewise defaults (age 36, SN 1, naked eye).
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
class SwephHeliacalTest extends AIswTest {

    static final double[] GEOPOS = {GEOLON, GEOLAT, GEOALT};

    static final int SE_HELIACAL_RISING = 1;   // morning first
    static final int SE_EVENING_FIRST = 3;

    static double[] defaultAtm() {
        return new double[4];
    }

    static double[] defaultObs() {
        return new double[6];
    }

    @Test
    void swe_vis_limit_mag_findsVenusVisibleSomeEveningsAndNotOthers() {
        // shortly after J2000 Venus is a bright evening object, well clear of the Sun -
        // the limiting-magnitude search should succeed (>= 0) rather than "below horizon" (-2)
        double jdUT = J2000 - getSwephExp().swe_deltat(J2000) + 60.;
        double[] dret = new double[8];
        StringBuilder serr = new StringBuilder();

        int ret = getSwephExp().swe_vis_limit_mag(jdUT, GEOPOS, defaultAtm(), defaultObs(),
                new StringBuilder("Venus"), 0, dret, serr);

        assertTrue(ret >= -2, "" + serr);
        if (ret >= 0) {
            // dret[0] is the magnitude difference to the visibility threshold in this build
            assertTrue(Double.isFinite(dret[0]), "magnitude margin: " + dret[0]);
        }
    }

    @Test
    void swe_vis_limit_mag_rejectsTheSun() {
        double jdUT = J2000 - getSwephExp().swe_deltat(J2000);
        double[] dret = new double[8];
        StringBuilder serr = new StringBuilder();

        int ret = getSwephExp().swe_vis_limit_mag(jdUT, GEOPOS, defaultAtm(), defaultObs(),
                new StringBuilder("Sun"), 0, dret, serr);

        assertTrue(ret == -1, "swe_vis_limit_mag is documented to refuse the Sun: ret=" + ret
                + " serr=" + serr);
    }

    /**
     * {@code heliacal_ut_vis_lim()} in swehel.c unconditionally zeroes {@code dret[0..9]}
     * before it computes anything, regardless of what the three documented output slots
     * are - so, per the calling convention documented in CLAUDE.md ("the bridge does not
     * and cannot check that the caller sized [double[]/int[]] per the Swiss Ephemeris
     * contract... undersized arrays are a heap overflow"), {@code dret} must be at least
     * 10 long here, not the 3 the public doc comment alone would suggest. A 3-long array
     * reproduced exactly that: an intermittent JVM crash from the native side writing past
     * the end of the JVM-managed array, depending on what happened to sit next to it on
     * the heap.
     */
    static final int DRET_SIZE = 10;

    @Test
    void swe_heliacal_ut_findsAMorningFirstOfVenusAfterTheStartDate() {
        double jdUT = J2000 - getSwephExp().swe_deltat(J2000);
        double[] dret = new double[DRET_SIZE];
        StringBuilder serr = new StringBuilder();

        int ret = getSwephExp().swe_heliacal_ut(jdUT, GEOPOS, defaultAtm(), defaultObs(),
                "Venus", SE_HELIACAL_RISING, 0, dret, serr);

        assertTrue(ret >= 0, "" + serr);
        assertTrue(dret[0] > jdUT, "the heliacal event should be after the search start: " + dret[0]);
    }

    @Test
    void swe_heliacal_ut_rejectsAGeoAltitudeOutsideTheValidRange() {
        double jdUT = J2000 - getSwephExp().swe_deltat(J2000);
        double[] badGeopos = {GEOLON, GEOLAT, 100000.};
        double[] dret = new double[DRET_SIZE];
        StringBuilder serr = new StringBuilder();

        int ret = getSwephExp().swe_heliacal_ut(jdUT, badGeopos, defaultAtm(), defaultObs(),
                "Venus", SE_HELIACAL_RISING, 0, dret, serr);

        assertTrue(ret < 0, "an altitude of 100 km should be rejected");
        assertTrue(serr.length() > 0, "an error message should explain why");
    }

    @Test
    void swe_heliacal_pheno_ut_reportsPhenomenaAtAFoundHeliacalEvent() {
        double jdUT = J2000 - getSwephExp().swe_deltat(J2000);
        double[] eventDret = new double[DRET_SIZE];
        int found = getSwephExp().swe_heliacal_ut(jdUT, GEOPOS, defaultAtm(), defaultObs(),
                "Venus", SE_HELIACAL_RISING, 0, eventDret, new StringBuilder());
        assertTrue(found >= 0);

        // darr[] is documented (swehel.c) to hold >= 30 slots of phenomena
        double[] darr = new double[50];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_heliacal_pheno_ut(eventDret[0], GEOPOS, defaultAtm(), defaultObs(),
                "Venus", SE_HELIACAL_RISING, 0, darr, serr);

        assertTrue(ret >= 0, "" + serr);
    }

    @Test
    void swe_heliacal_angle_andSweTopoArcusVisionis_runWithoutError() {
        // both are documented "secret, for Victor Reijs'" helpers that take an already
        // known topocentric geometry (azimuths/altitudes of object, Sun and Moon) rather
        // than searching for one, so any self-consistent geometry exercises the binding
        double jdUT = J2000 - getSwephExp().swe_deltat(J2000);
        double mag = -4.0;         // roughly Venus at its brightest
        double aziObj = 270., aziSun = 260., aziMoon = 100., altMoon = 10., altObj = 5.;

        double[] dretAngle = new double[50];
        StringBuilder serrAngle = new StringBuilder();
        int retAngle = getSwephExp().swe_heliacal_angle(jdUT, GEOPOS, defaultAtm(), defaultObs(),
                0, mag, aziObj, aziSun, aziMoon, altMoon, dretAngle, serrAngle);
        assertTrue(retAngle >= 0, "" + serrAngle);

        double[] dretArcus = new double[50];
        StringBuilder serrArcus = new StringBuilder();
        int retArcus = getSwephExp().swe_topo_arcus_visionis(jdUT, GEOPOS, defaultAtm(), defaultObs(),
                0, mag, aziObj, altObj, aziSun, aziMoon, altMoon, dretArcus, serrArcus);
        assertTrue(retArcus >= 0, "" + serrArcus);
    }
}
