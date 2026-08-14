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
 * {@code swe_nod_aps}(+{@code _ut}), {@code swe_get_orbital_elements},
 * {@code swe_orbit_max_min_true_distance}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
class SwephOrbitTest extends AIswTest {

    /** ascending/descending node and perihelion/aphelion by mean elements */
    static final int SE_NODBIT_MEAN = 1;

    /**
     * The 180-degree symmetry of a node/apsis pair is a property of the orbit itself, so it
     * only shows up cleanly in heliocentric longitude. The default (geocentric) frame
     * projects both points from an Earth that is not at the Sun, and that parallax breaks
     * the exact opposition - confirmed by {@link #swe_nod_aps_geocentricNodesAreNotExactlyOpposite}.
     */
    @Test
    void swe_nod_aps_placesTheAscendingAndDescendingNodesOppositeEachOtherHeliocentrically() {
        double[] xnasc = new double[6], xndsc = new double[6];
        double[] xperi = new double[6], xaphe = new double[6];
        StringBuilder serr = new StringBuilder();

        int ret = getSwephExp().swe_nod_aps(J2000, SE_MARS, SEFLG_SWIEPH | SEFLG_HELCTR,
                SE_NODBIT_MEAN, xnasc, xndsc, xperi, xaphe, serr);

        assertTrue(ret >= 0, "" + serr);
        assertEquals(180., normDeg(xndsc[0] - xnasc[0]), 1e-4,
                "ascending and descending node are 180 degrees apart");
    }

    @Test
    void swe_nod_aps_placesPerihelionAndAphelionOppositeEachOtherHeliocentrically() {
        double[] xnasc = new double[6], xndsc = new double[6];
        double[] xperi = new double[6], xaphe = new double[6];
        StringBuilder serr = new StringBuilder();

        int ret = getSwephExp().swe_nod_aps(J2000, SE_MARS, SEFLG_SWIEPH | SEFLG_HELCTR,
                SE_NODBIT_MEAN, xnasc, xndsc, xperi, xaphe, serr);

        assertTrue(ret >= 0, "" + serr);
        assertEquals(180., normDeg(xaphe[0] - xperi[0]), 1e-4);
        // aphelion is farther from the Sun than perihelion, by definition
        assertTrue(xaphe[2] > xperi[2], "aphelion distance " + xaphe[2]
                + " should exceed perihelion distance " + xperi[2]);
    }

    /**
     * The default frame is geocentric, so the node points are projected from an Earth that
     * is not at the Sun - the parallax this introduces is what makes
     * {@link #swe_nod_aps_placesTheAscendingAndDescendingNodesOppositeEachOtherHeliocentrically}
     * need {@code SEFLG_HELCTR} in the first place. Pinned here so the difference is not
     * mistaken for a defect later.
     */
    @Test
    void swe_nod_aps_geocentricNodesAreNotExactlyOpposite() {
        double[] xnasc = new double[6], xndsc = new double[6];
        double[] xperi = new double[6], xaphe = new double[6];

        getSwephExp().swe_nod_aps(J2000, SE_MARS, SEFLG_SWIEPH, SE_NODBIT_MEAN,
                xnasc, xndsc, xperi, xaphe, new StringBuilder());

        assertTrue(Math.abs(normDeg(xndsc[0] - xnasc[0]) - 180.) > 1.,
                "geocentric parallax should move the nodes away from an exact 180 degrees");
    }

    @Test
    void swe_nod_aps_ut_agreesWithSweNodAps() {
        double jdUT = J2000 - getSwephExp().swe_deltat(J2000);
        double[] a1 = new double[6], d1 = new double[6], p1 = new double[6], h1 = new double[6];
        getSwephExp().swe_nod_aps(J2000, SE_JUPITER, SEFLG_SWIEPH, SE_NODBIT_MEAN, a1, d1, p1, h1,
                new StringBuilder());

        double[] a2 = new double[6], d2 = new double[6], p2 = new double[6], h2 = new double[6];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_nod_aps_ut(jdUT, SE_JUPITER, SEFLG_SWIEPH, SE_NODBIT_MEAN,
                a2, d2, p2, h2, serr);

        assertTrue(ret >= 0, "" + serr);
        assertEquals(a1[0], a2[0], 1e-4, "ascending node longitude");
    }

    @Test
    void swe_get_orbital_elements_reportsAPlausibleEccentricityAndSemiMajorAxis() {
        double[] dret = new double[50];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_get_orbital_elements(J2000, SE_MARS, SEFLG_SWIEPH, dret, serr);

        assertTrue(ret >= 0, "" + serr);
        // dret[0] semi-major axis (AU), dret[1] eccentricity, per swephexp.h's documentation
        // of the seventeen returned values
        assertTrue(dret[0] > 1.3 && dret[0] < 1.7, "Mars semi-major axis: " + dret[0]);
        assertTrue(dret[1] > 0. && dret[1] < 0.5, "Mars eccentricity: " + dret[1]);
    }

    @Test
    void swe_orbit_max_min_true_distance_boundsTheActualDistance() {
        double[] dmax = new double[1], dmin = new double[1], dtrue = new double[1];
        StringBuilder serr = new StringBuilder();

        int ret = getSwephExp().swe_orbit_max_min_true_distance(J2000, SE_MARS, SEFLG_SWIEPH,
                dmax, dmin, dtrue, serr);

        assertTrue(ret >= 0, "" + serr);
        assertTrue(dmin[0] < dmax[0], "min " + dmin[0] + " should be below max " + dmax[0]);
        assertTrue(dtrue[0] >= dmin[0] * 0.99 && dtrue[0] <= dmax[0] * 1.01,
                "the true distance today should lie within the orbit's own range: "
                        + dmin[0] + " <= " + dtrue[0] + " <= " + dmax[0]);
    }
}
