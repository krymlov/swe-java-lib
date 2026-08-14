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
 * The Placalc-compatibility helpers of {@code swephlib.c}: a {@code centisec} is a signed
 * 32-bit integer holding 1/100 of an arc second (or of a second of time), so it maps onto a
 * Java {@code int} directly. {@code swe_csnorm}, {@code swe_difcsn}, {@code swe_difdegn},
 * {@code swe_difcs2n}, {@code swe_difdeg2n}, {@code swe_difrad2n}, {@code swe_csroundsec},
 * {@code swe_d2l}, {@code swe_day_of_week}, {@code swe_cs2timestr}, {@code swe_cs2lonlatstr},
 * {@code swe_cs2degstr}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
class SwephPlacalcTest extends AIswTest {

    /** centiseconds of arc in one degree: 3600 arcsec/deg * 100 centisec/arcsec */
    static final int CS_PER_DEG = 360000;
    /** centiseconds of arc in one arcminute */
    static final int CS_PER_ARCMIN = 6000;
    /** centiseconds of time in one second of time */
    static final int CS_PER_SEC = 100;

    static int deg(double d) {
        return (int) Math.round(d * CS_PER_DEG);
    }

    @Test
    void swe_csnorm_wrapsIntoZeroToThreeSixtyDegrees() {
        assertEquals(deg(10), getSwephExp().swe_csnorm(deg(370)));
        assertEquals(deg(350), getSwephExp().swe_csnorm(deg(-10)));
        assertEquals(0, getSwephExp().swe_csnorm(deg(360)));
    }

    @Test
    void swe_difcsn_isTheForwardDistanceModuloThreeSixty() {
        // from 350 to 10 degrees, going forward (wrapping through 0), is 20 degrees
        assertEquals(deg(20), getSwephExp().swe_difcsn(deg(10), deg(350)));
        assertEquals(deg(340), getSwephExp().swe_difcsn(deg(350), deg(10)));
    }

    @Test
    void swe_difdegn_agreesWithSweDifcsnInDegrees() {
        double d = getSwephExp().swe_difdegn(10., 350.);
        assertEquals(20., d, 1e-6);
    }

    @Test
    void swe_difcs2n_isTheShortestSignedDistance() {
        // the shortest way from 350 to 10 is +20 (through 0), not -340
        assertEquals(deg(20), getSwephExp().swe_difcs2n(deg(10), deg(350)));
        assertEquals(deg(-20), getSwephExp().swe_difcs2n(deg(350), deg(10)));
    }

    @Test
    void swe_difdeg2n_agreesWithSweDifcs2nInDegrees() {
        assertEquals(20., getSwephExp().swe_difdeg2n(10., 350.), 1e-6);
        assertEquals(-20., getSwephExp().swe_difdeg2n(350., 10.), 1e-6);
    }

    @Test
    void swe_difrad2n_agreesWithSweDifdeg2nInRadians() {
        double rad = getSwephExp().swe_difrad2n(Math.toRadians(10.), Math.toRadians(350.));
        assertEquals(20., Math.toDegrees(rad), 1e-6);
    }

    @Test
    void swe_csroundsec_roundsToTheNearestWholeSecond() {
        // 10 deg 0' 0.6" rounds up to 10 deg 0' 1"
        int cs = deg(10) + 60;
        int rounded = getSwephExp().swe_csroundsec(cs);
        assertEquals(deg(10) + 100, rounded, "0.6\" rounds up to the next whole second");
    }

    @Test
    void swe_csroundsec_neverRoundsPast59Point59MinutesOfTheSign() {
        // swephexp.h: "round second, but at 29.5959 always down" - the last second of a
        // zodiac sign never rounds forward into the next sign
        int cs = 29 * CS_PER_DEG + 59 * CS_PER_ARCMIN + 59 * CS_PER_SEC + 60;   // 29d59'59.6"
        int rounded = getSwephExp().swe_csroundsec(cs);
        assertTrue(rounded < 30 * CS_PER_DEG, "must not cross into the next sign: " + rounded);
    }

    @Test
    void swe_d2l_roundsToTheNearestInteger() {
        assertEquals(3, getSwephExp().swe_d2l(3.4));
        assertEquals(4, getSwephExp().swe_d2l(3.6));
        assertEquals(-3, getSwephExp().swe_d2l(-3.4));
    }

    @Test
    void swe_day_of_week_knowsTheReferenceInstantWasASaturday() {
        // 1 Jan 2000, 12:00 was a Saturday; swephexp.h: Monday = 0 ... Sunday = 6
        int dow = getSwephExp().swe_day_of_week(J2000);
        assertEquals(5, dow, "1 Jan 2000 was a Saturday (Monday=0..Sunday=6)");
    }

    @Test
    void swe_day_of_week_advancesByOneEachDay() {
        int today = getSwephExp().swe_day_of_week(J2000);
        int tomorrow = getSwephExp().swe_day_of_week(J2000 + 1.);
        assertEquals((today + 1) % 7, tomorrow);
    }

    @Test
    void swe_cs2timestr_formatsHoursMinutesSeconds() {
        int cs = (12 * 3600 + 34 * 60 + 56) * CS_PER_SEC;
        String s = getSwephExp().swe_cs2timestr(cs, ':', false);
        assertEquals("12:34:56", s);
    }

    @Test
    void swe_cs2timestr_suppressesZeroSeconds() {
        int cs = (12 * 3600 + 34 * 60) * CS_PER_SEC;
        String s = getSwephExp().swe_cs2timestr(cs, ':', true);
        assertEquals("12:34", s);
    }

    @Test
    void swe_cs2lonlatstr_usesTheGivenDirectionLetters() {
        String pos = getSwephExp().swe_cs2lonlatstr(deg(30), 'E', 'W');
        assertTrue(pos.contains("E"), pos);

        String neg = getSwephExp().swe_cs2lonlatstr(deg(-30), 'E', 'W');
        assertTrue(neg.contains("W"), neg);
    }

    @Test
    void swe_cs2degstr_formatsWithinAZodiacSign() {
        // 190 degrees is 10 degrees into the sign starting at 180
        String s = getSwephExp().swe_cs2degstr(deg(190));
        assertNotNull(s);
        assertTrue(s.trim().startsWith("10"), s);
    }
}
