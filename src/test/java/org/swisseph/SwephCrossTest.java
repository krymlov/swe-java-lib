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
 * The analytic longitude crossing solvers of {@code swephlib.c}: {@code swe_solcross}(+
 * {@code _ut}), {@code swe_mooncross}(+{@code _ut}), {@code swe_mooncross_node}(+{@code _ut}),
 * {@code swe_helio_cross}(+{@code _ut}).
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
class SwephCrossTest extends AIswTest {

    @Test
    void swe_solcross_ut_findsTheSunAtTheRequestedLongitude() {
        StringBuilder serr = new StringBuilder();
        double jdUT = J2000 - getSwephExp().swe_deltat(J2000);
        double cross = getSwephExp().swe_solcross_ut(100., jdUT, SEFLG_SWIEPH, serr);

        assertTrue(cross > jdUT, "" + serr);
        double[] xx = calc(cross + getSwephExp().swe_deltat(cross), SE_SUN, SEFLG_SWIEPH);
        assertEquals(100., xx[0], 1e-4, "the Sun's longitude at the reported crossing");
    }

    @Test
    void swe_solcross_agreesWithSweSolcrossUtOnceDeltaTIsApplied() {
        double jdUT = J2000 - getSwephExp().swe_deltat(J2000);
        double crossUt = getSwephExp().swe_solcross_ut(200., jdUT, SEFLG_SWIEPH, new StringBuilder());

        double jdET = J2000;
        double crossEt = getSwephExp().swe_solcross(200., jdET, SEFLG_SWIEPH, new StringBuilder());

        assertEquals(crossUt, crossEt - getSwephExp().swe_deltat(crossEt), 1e-6);
    }

    @Test
    void swe_mooncross_ut_findsTheMoonAtTheRequestedLongitude() {
        StringBuilder serr = new StringBuilder();
        double jdUT = J2000 - getSwephExp().swe_deltat(J2000);
        double cross = getSwephExp().swe_mooncross_ut(50., jdUT, SEFLG_SWIEPH, serr);

        assertTrue(cross > jdUT && cross < jdUT + 30., "" + serr + " cross=" + cross);
        double[] xx = calc(cross + getSwephExp().swe_deltat(cross), SE_MOON, SEFLG_SWIEPH);
        assertEquals(50., xx[0], 1e-3);
    }

    @Test
    void swe_mooncross_agreesWithSweMooncrossUt() {
        double jdUT = J2000 - getSwephExp().swe_deltat(J2000);
        double crossUt = getSwephExp().swe_mooncross_ut(150., jdUT, SEFLG_SWIEPH, new StringBuilder());

        double crossEt = getSwephExp().swe_mooncross(150., J2000, SEFLG_SWIEPH, new StringBuilder());

        assertEquals(crossUt, crossEt - getSwephExp().swe_deltat(crossEt), 1e-6);
    }

    @Test
    void swe_mooncross_node_ut_findsTheMoonOnItsOwnNode() {
        double[] xlon = new double[1];
        double[] xlat = new double[1];
        StringBuilder serr = new StringBuilder();
        double jdUT = J2000 - getSwephExp().swe_deltat(J2000);

        double cross = getSwephExp().swe_mooncross_node_ut(jdUT, SEFLG_SWIEPH, xlon, xlat, serr);

        assertTrue(cross > jdUT && cross < jdUT + 30., "" + serr);
        assertEquals(0., xlat[0], 0.05, "latitude at the node should be zero");
        assertTrue(xlon[0] >= 0. && xlon[0] < 360.);
    }

    @Test
    void swe_mooncross_node_agreesWithSweMoonCrossNodeUt() {
        double jdUT = J2000 - getSwephExp().swe_deltat(J2000);
        double crossUt = getSwephExp().swe_mooncross_node_ut(jdUT, SEFLG_SWIEPH,
                new double[1], new double[1], new StringBuilder());

        double crossEt = getSwephExp().swe_mooncross_node(J2000, SEFLG_SWIEPH,
                new double[1], new double[1], new StringBuilder());

        assertEquals(crossUt, crossEt - getSwephExp().swe_deltat(crossEt), 1e-6);
    }

    @Test
    void swe_helio_cross_ut_findsAPlanetAtTheRequestedHeliocentricLongitude() {
        double jdUT = J2000 - getSwephExp().swe_deltat(J2000);
        double[] jdCross = new double[1];
        StringBuilder serr = new StringBuilder();

        int ret = getSwephExp().swe_helio_cross_ut(SE_MARS, 0., jdUT, SEFLG_SWIEPH, 1, jdCross, serr);

        assertTrue(ret >= 0, "" + serr);
        assertTrue(jdCross[0] > jdUT, "the crossing should be after the start date");

        double jdET = jdCross[0] + getSwephExp().swe_deltat(jdCross[0]);
        double[] xx = calc(jdET, SE_MARS, SEFLG_SWIEPH | SEFLG_HELCTR | SEFLG_TRUEPOS);
        double arcToZero = Math.min(normDeg(xx[0]), 360. - normDeg(xx[0]));
        assertEquals(0., arcToZero, 1e-2, "heliocentric longitude at the crossing: " + xx[0]);
    }

    @Test
    void swe_helio_cross_ut_rejectsTheSunAndMoon() {
        double[] jdCross = new double[1];
        StringBuilder serr = new StringBuilder();

        int ret = getSwephExp().swe_helio_cross_ut(SE_SUN, 0., J2000, SEFLG_SWIEPH, 1, jdCross, serr);
        assertTrue(ret < 0, "heliocentric crossings are not defined for the Sun");
    }

    @Test
    void swe_helio_cross_agreesWithSweHelioCrossUt() {
        double jdUT = J2000 - getSwephExp().swe_deltat(J2000);
        double[] jdCrossUt = new double[1];
        getSwephExp().swe_helio_cross_ut(SE_VENUS, 0., jdUT, SEFLG_SWIEPH, 1, jdCrossUt, new StringBuilder());

        double[] jdCrossEt = new double[1];
        int ret = getSwephExp().swe_helio_cross(SE_VENUS, 0., J2000, SEFLG_SWIEPH, 1, jdCrossEt, new StringBuilder());

        assertTrue(ret >= 0);
        assertEquals(jdCrossUt[0], jdCrossEt[0] - getSwephExp().swe_deltat(jdCrossEt[0]), 1e-3);
    }
}
