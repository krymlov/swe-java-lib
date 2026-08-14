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
 * The small angle/coordinate utilities of {@code swephlib.c}: {@code swe_degnorm},
 * {@code swe_radnorm}, {@code swe_rad_midp}, {@code swe_deg_midp}, {@code swe_split_deg},
 * {@code swe_cotrans}, {@code swe_cotrans_sp}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
class SwephCoordTest extends AIswTest {

    static final double TWO_PI = 2. * Math.PI;

    @Test
    void swe_degnorm_wrapsIntoZeroToThreeSixty() {
        assertEquals(10., getSwephExp().swe_degnorm(370.), 1e-9);
        assertEquals(350., getSwephExp().swe_degnorm(-10.), 1e-9);
        assertEquals(0., getSwephExp().swe_degnorm(360.), 1e-9);
        assertEquals(0., getSwephExp().swe_degnorm(0.), 1e-9);
        assertEquals(180., getSwephExp().swe_degnorm(-180.), 1e-9);
    }

    @Test
    void swe_radnorm_wrapsIntoZeroToTwoPi() {
        assertEquals(0.1, getSwephExp().swe_radnorm(TWO_PI + 0.1), 1e-9);
        assertEquals(TWO_PI - 0.1, getSwephExp().swe_radnorm(-0.1), 1e-9);
    }

    @Test
    void swe_deg_midp_findsTheMidpointTheShortWayAround() {
        assertEquals(15., getSwephExp().swe_deg_midp(10., 20.), 1e-9);
        // the short way around the 0/360 seam
        assertEquals(0., getSwephExp().swe_deg_midp(350., 10.), 1e-9);
    }

    @Test
    void swe_rad_midp_agreesWithSweDegMidpInRadians() {
        double degMid = getSwephExp().swe_deg_midp(10., 20.);
        double radMid = getSwephExp().swe_rad_midp(Math.toRadians(10.), Math.toRadians(20.));
        assertEquals(degMid, Math.toDegrees(radMid), 1e-6);
    }

    /**
     * With no rounding flag the fractional second is returned separately rather than
     * rounded in, so the whole seconds truncate down - and 10 + 30/60 + 15/3600 is a hair
     * below the exact value in binary, which truncates to 14 rather than 15. Using
     * {@code SE_SPLIT_DEG_ROUND_SEC} (1) is the correct way to ask for a whole second, and
     * is what {@code ISweJulianDate.splitTime} does for exactly this reason.
     */
    @Test
    void swe_split_deg_decomposesADegreeIntoDegMinSec() {
        final int SE_SPLIT_DEG_ROUND_SEC = 1;
        int[] dms = new int[3];
        double[] secFraction = new double[1];
        int[] sign = new int[1];

        getSwephExp().swe_split_deg(10 + 30 / 60. + 15 / 3600., SE_SPLIT_DEG_ROUND_SEC,
                dms, secFraction, sign);

        assertEquals(10, dms[0]);
        assertEquals(30, dms[1]);
        assertEquals(15, dms[2]);
        assertEquals(1, sign[0]);
    }

    @Test
    void swe_split_deg_withoutRoundingLeavesTheSubSecondFraction() {
        int[] dms = new int[3];
        double[] secFraction = new double[1];
        int[] sign = new int[1];

        getSwephExp().swe_split_deg(10.5, 0, dms, secFraction, sign);

        assertEquals(10, dms[0]);
        assertEquals(30, dms[1]);
        assertEquals(0, dms[2]);
        assertEquals(0., secFraction[0], 1e-6, "exactly on a minute, no leftover fraction");
    }

    @Test
    void swe_split_deg_reportsANegativeSign() {
        int[] dms = new int[3];
        double[] secFraction = new double[1];
        int[] sign = new int[1];

        getSwephExp().swe_split_deg(-10.5, 0, dms, secFraction, sign);

        assertEquals(-1, sign[0]);
        assertEquals(10, dms[0]);
    }

    /**
     * The vernal equinox point sits on both the ecliptic and the equator, so ecliptic
     * (0, 0) must map to equatorial (0, 0) whatever the obliquity is - unlike the pole,
     * which is offset from the equatorial pole by the obliquity and is not a fixed point of
     * this rotation.
     */
    @Test
    void swe_cotrans_keepsTheVernalEquinoxFixed() {
        double eps = 23.4392911;
        double[] equinox = {0., 0., 1.};
        double[] out = new double[3];

        getSwephExp().swe_cotrans(equinox, out, eps);

        assertEquals(0., out[0], 1e-9);
        assertEquals(0., out[1], 1e-9);
        assertEquals(1., out[2], 1e-9, "distance is preserved");
    }

    @Test
    void swe_cotrans_withZeroObliquityIsTheIdentity() {
        double[] in = {123.456, 12.345, 1.};
        double[] out = new double[3];

        getSwephExp().swe_cotrans(in, out, 0.);

        assertEquals(in[0], out[0], 1e-9);
        assertEquals(in[1], out[1], 1e-9);
    }

    @Test
    void swe_cotrans_roundTripsWithItsOwnInverse() {
        double eps = 23.4392911;
        double[] in = {123.456, 12.345, 1.};
        double[] rotated = new double[3];
        double[] back = new double[3];

        getSwephExp().swe_cotrans(in, rotated, eps);
        getSwephExp().swe_cotrans(rotated, back, -eps);

        assertEquals(in[0], back[0], 1e-6);
        assertEquals(in[1], back[1], 1e-6);
    }

    @Test
    void swe_cotrans_sp_alsoRotatesTheSpeedComponents() {
        double eps = 23.4392911;
        // longitude, latitude, distance, speed in longitude, speed in latitude, speed in distance
        double[] in = {45., 10., 1., 1., 0.1, 0.};
        double[] out = new double[6];

        getSwephExp().swe_cotrans_sp(in, out, eps);

        assertEquals(1., out[2], 1e-9, "distance unaffected");
        assertTrue(out[3] != 0. || out[4] != 0., "some speed must come through the rotation");
    }
}
