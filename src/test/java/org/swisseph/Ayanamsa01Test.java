/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2023-01
 */

package org.swisseph;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.swisseph.api.ISweObjects;
import org.swisseph.api.ISweObjectsOptions;
import org.swisseph.app.SweJulianDate;
import org.swisseph.app.SweObjects;
import org.swisseph.app.SweObjectsOptions;

import static org.swisseph.api.ISweConstants.*;
import static org.swisseph.api.ISweObjects.*;
import static org.swisseph.api.ISweObjectsOptions.DEFAULT_SS_CALC_FLAGS;
import static org.swisseph.app.SweObjectsOptions.LAHIRI_AYANAMSA;
import static org.swisseph.utils.IDegreeUtils.toDMSms;
import static swisseph.SweConst.SEFLG_TRUEPOS;

/**
 * <pre>
 * On 20.08.22 19:20, Alois Treindl wrote:
 * I understand your problem now, I think.
 * For sidereal zodiac, you cannot use function swe_houses().
 * You must use sew_houses_ex() because only that way you can set iflag = SEFLG_SIDEREAL
 *
 * swetest -b15.08.1947 -ut10:30 -fPl -sid1 -house81.83,25.95,P
 *  date (dmy) 15.8.1947 greg. 10:30:00 UT version 2.10.02
 *  UT: 2432412.937500000 delta t: 28.108929 sec
 *  TT: 2432412.937825335 ayanamsa = 23° 7'18.7132 (Lahiri)
 *  geo. long 81.830000, lat 25.950000, alt 0.000000
 *  Epsilon (m) 23°26'45.9391
 *  Houses system P (Placidus) for long= 81°49'48.0000, lat= 25°57' 0.0000
 *  Sun 118.6297002
 *  house 1 256.3768059
 *  house 2 291.1902485
 *  house 3 327.8877468
 *  house 4 0.9865961
 *  house 5 28.5548052
 *  house 6 52.6005999
 *  house 7 76.3768059
 *  house 8 111.1902485
 *  house 9 147.8877468
 *  house 10 180.9865961
 *  house 11 208.5548052
 *  house 12 232.6005999
 *  Ascendant 256.3768059
 *  MC 180.9865961
 *  ARMC 202.3204410
 *  Vertex 118.4521977
 *  equat. Asc. 267.5168131
 *  co-Asc. W.Koch 277.2472893
 *  co-Asc Munkasey 219.9830309
 *  Polar Asc. 97.2472893
 * <br>
 * <br>
 * The difference in Ascendant is 0.13" and might be caused by the fact that the version of pyswisseph pip
 * installed for me is 2.08.
 * </pre>
 *
 * @author Yura Krymlov
 * @version 1.0, 2023-01
 */
public class Ayanamsa01Test extends AbstractTest {
    final int[] date1947 = new int[]{1947, 8, 15, 10, 30, 0};

    @Test
    void testObjects_WITHOUT_SEFLG_TRUEPOS() {
        ISweObjectsOptions sweObjectsOptions = new SweObjectsOptions.Builder()
                .options(LAHIRI_AYANAMSA).calcFlags(DEFAULT_SS_CALC_FLAGS ^ SEFLG_TRUEPOS).build();

        ISweObjects sweObjects = new SweObjects(getSwephExp(), new SweJulianDate(date1947, 0f),
                GEO_LUCKNOW, sweObjectsOptions).completeBuild(); // - SEFLG_TRUEPOS

        Assertions.assertEquals(2432412.9375, sweObjects.sweJulianDate().julianDay());
        Assertions.assertEquals(28.108929, sweObjects.sweJulianDate().deltaT() * d86400, DELTA_D000001);
        Assertions.assertEquals(2432412.937825335, sweObjects.sweJulianDate().ephemerisTime(), DELTA_D0000001);

        // Ayanamsa
        Assertions.assertEquals("23°07'18.71\"", toDMSms(sweObjects.ayanamsa()).toString());
        Assertions.assertEquals(23.12186478492945, sweObjects.ayanamsa());

        // Lagna
        Assertions.assertEquals(256.3768059, sweObjects.longitudes()[LG], DELTA_D0000001);
        Assertions.assertEquals("256°22'36.50\"", toDMSms(sweObjects.longitudes()[LG]).toString());

        // Sun
        Assertions.assertEquals(118.6297002, sweObjects.longitudes()[SY], DELTA_D0000001);
        Assertions.assertEquals("118°37'46.92\"", toDMSms(sweObjects.longitudes()[SY]).toString());

        // Moon
        Assertions.assertEquals(104.0615885, sweObjects.longitudes()[CH], DELTA_D0000001);
        Assertions.assertEquals("104°03'41.72\"", toDMSms(sweObjects.longitudes()[CH]).toString());

        // Mercury
        Assertions.assertEquals(104.8884878, sweObjects.longitudes()[BU], DELTA_D0000001);
        Assertions.assertEquals("104°53'18.56\"", toDMSms(sweObjects.longitudes()[BU]).toString());

        // Venus
        Assertions.assertEquals(113.3846373, sweObjects.longitudes()[SK], DELTA_D0000001);
        Assertions.assertEquals("113°23'04.69\"", toDMSms(sweObjects.longitudes()[SK]).toString());

        // Mars
        Assertions.assertEquals(67.8944219, sweObjects.longitudes()[MA], DELTA_D0000001);
        Assertions.assertEquals("67°53'39.92\"", toDMSms(sweObjects.longitudes()[MA]).toString());

        // Jupiter
        Assertions.assertEquals(205.9351650, sweObjects.longitudes()[GU], DELTA_D0000001);
        Assertions.assertEquals("205°56'06.59\"", toDMSms(sweObjects.longitudes()[GU]).toString());

        // Saturn
        Assertions.assertEquals(110.5582209, sweObjects.longitudes()[SA], DELTA_D0000001);
        Assertions.assertEquals("110°33'29.60\"", toDMSms(sweObjects.longitudes()[SA]).toString());

        // Uranus
        Assertions.assertEquals(62.0791801, sweObjects.longitudes()[UR], DELTA_D0000001);
        Assertions.assertEquals("62°04'45.05\"", toDMSms(sweObjects.longitudes()[UR]).toString());

        // Neptune
        Assertions.assertEquals(165.7254815, sweObjects.longitudes()[NE], DELTA_D0000001);
        Assertions.assertEquals("165°43'31.73\"", toDMSms(sweObjects.longitudes()[NE]).toString());

        // Pluto
        Assertions.assertEquals(110.1277727, sweObjects.longitudes()[PL], DELTA_D0000001);
        Assertions.assertEquals("110°07'39.98\"", toDMSms(sweObjects.longitudes()[PL]).toString());

        // mean Node
        Assertions.assertEquals(35.0343967, sweObjects.longitudes()[RA], DELTA_D0000001);
        Assertions.assertEquals("35°02'03.83\"", toDMSms(sweObjects.longitudes()[RA]).toString());
    }

    @Test
    void testObjects_WITH_SEFLG_TRUEPOS() {
        ISweObjects sweObjects = new SweObjects(getSwephExp(), new SweJulianDate(date1947, 0f),
                GEO_LUCKNOW, LAHIRI_AYANAMSA).completeBuild(); // + SEFLG_TRUEPOS

        Assertions.assertEquals(2432412.9375, sweObjects.sweJulianDate().julianDay());
        Assertions.assertEquals(28.108929, sweObjects.sweJulianDate().deltaT() * d86400, DELTA_D000001);
        Assertions.assertEquals(2432412.937825335, sweObjects.sweJulianDate().ephemerisTime(), DELTA_D0000001);

        // Ayanamsa
        Assertions.assertEquals("23°07'18.71\"", toDMSms(sweObjects.ayanamsa()).toString());
        Assertions.assertEquals(23.12186478492945, sweObjects.ayanamsa());

        // Lagna
        Assertions.assertEquals(256.3768059, sweObjects.longitudes()[LG], DELTA_D0000001);
        Assertions.assertEquals("256°22'36.50\"", toDMSms(sweObjects.longitudes()[LG]).toString());

        // Sun
        Assertions.assertEquals(118.6353224, sweObjects.longitudes()[SY], DELTA_D0000001);
        Assertions.assertEquals("118°38'07.16\"", toDMSms(sweObjects.longitudes()[SY]).toString());

        // Moon
        Assertions.assertEquals(104.0617984, sweObjects.longitudes()[CH], DELTA_D0000001);
        Assertions.assertEquals("104°03'42.47\"", toDMSms(sweObjects.longitudes()[CH]).toString());

        // Mercury
        Assertions.assertEquals(104.9009568, sweObjects.longitudes()[BU], DELTA_D0000001);
        Assertions.assertEquals("104°54'03.44\"", toDMSms(sweObjects.longitudes()[BU]).toString());

        // Venus
        Assertions.assertEquals(113.3969220, sweObjects.longitudes()[SK], DELTA_D0000001);
        Assertions.assertEquals("113°23'48.92\"", toDMSms(sweObjects.longitudes()[SK]).toString());

        // Mars
        Assertions.assertEquals(67.9016892, sweObjects.longitudes()[MA], DELTA_D0000001);
        Assertions.assertEquals("67°54'06.08\"", toDMSms(sweObjects.longitudes()[MA]).toString());

        // Jupiter
        Assertions.assertEquals(205.9378445, sweObjects.longitudes()[GU], DELTA_D0000001);
        Assertions.assertEquals("205°56'16.24\"", toDMSms(sweObjects.longitudes()[GU]).toString());

        // Saturn
        Assertions.assertEquals(110.5657128, sweObjects.longitudes()[SA], DELTA_D0000001);
        Assertions.assertEquals("110°33'56.57\"", toDMSms(sweObjects.longitudes()[SA]).toString());

        // Uranus
        Assertions.assertEquals(62.0835345, sweObjects.longitudes()[UR], DELTA_D0000001);
        Assertions.assertEquals("62°05'00.72\"", toDMSms(sweObjects.longitudes()[UR]).toString());

        // Neptune
        Assertions.assertEquals(165.7303854, sweObjects.longitudes()[NE], DELTA_D0000001);
        Assertions.assertEquals("165°43'49.39\"", toDMSms(sweObjects.longitudes()[NE]).toString());

        // Pluto
        Assertions.assertEquals(110.1342835, sweObjects.longitudes()[PL], DELTA_D0000001);
        Assertions.assertEquals("110°08'03.42\"", toDMSms(sweObjects.longitudes()[PL]).toString());

        // mean Node
        Assertions.assertEquals(35.0343967, sweObjects.longitudes()[RA], DELTA_D0000001);
        Assertions.assertEquals("35°02'03.83\"", toDMSms(sweObjects.longitudes()[RA]).toString());
    }

}
