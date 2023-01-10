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
import static org.swisseph.api.ISweObjects.LG;
import static org.swisseph.api.ISweObjects.SY;
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
 * swetest -b15.08.1947 -ut10:30 -p0 -fPl -sid1 -house81.83,25.95,P
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

    @Test
    void testObjects() {
        ISweObjectsOptions sweObjectsOptions = new SweObjectsOptions.Builder()
                .options(LAHIRI_AYANAMSA).calcFlags(DEFAULT_SS_CALC_FLAGS ^ SEFLG_TRUEPOS).build();

        ISweObjects sweObjects = new SweObjects(getSwephExp(), new SweJulianDate(new int[]
                {1947, 8, 15, 10, 30, 0}, 0f), GEO_LUCKNOW, sweObjectsOptions).completeBuild();

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
    }
}
