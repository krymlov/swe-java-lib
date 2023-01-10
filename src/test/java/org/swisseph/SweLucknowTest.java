/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2023-01
 */

package org.swisseph;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.swisseph.app.SweJulianDate;
import org.swisseph.app.SweObjects;

import static org.swisseph.api.ISweConstants.*;
import static org.swisseph.api.ISweObjects.LG;
import static org.swisseph.app.SweObjectsOptions.LAHIRI_AYANAMSA;
import static org.swisseph.app.SweObjectsOptions.TRUECITRA_AYANAMSA;
import static org.swisseph.utils.IDegreeUtils.toDMSms;

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
public class SweLucknowTest extends AbstractTest {
    private SweObjects truecitraAyanamsaObjects;
    private SweObjects lahiriAyanamsaObjects;

    @BeforeAll
    void beforeAll() {
        truecitraAyanamsaObjects = new SweObjects(newSwephExp(),
                new SweJulianDate(new int[]{1947, 8, 15, 10, 30, 0}, 0f), GEO_LUCKNOW, TRUECITRA_AYANAMSA);

        lahiriAyanamsaObjects = new SweObjects(newSwephExp(),
                new SweJulianDate(new int[]{1947, 8, 15, 10, 30, 0}, 0f), GEO_LUCKNOW, LAHIRI_AYANAMSA);
    }

    @AfterAll
    void afterAll() {
        lahiriAyanamsaObjects.swissEph().close();
        truecitraAyanamsaObjects.swissEph().close();
    }

    @Test
    void testJD() {
        Assertions.assertEquals(2432412.9375, lahiriAyanamsaObjects.sweJulianDate().julianDay());
        Assertions.assertEquals(2432412.9375, truecitraAyanamsaObjects.sweJulianDate().julianDay());
    }

    @Test
    void testDeltaT() {
        Assertions.assertEquals(28.108929, lahiriAyanamsaObjects.sweJulianDate().deltaT() * d86400, DELTA_D000001);
        Assertions.assertEquals(28.108929, truecitraAyanamsaObjects.sweJulianDate().deltaT() * d86400, DELTA_D000001);
    }

    @Test
    void testAscendant() {
        Assertions.assertEquals(256.3768059, lahiriAyanamsaObjects.longitudes()[LG], DELTA_D0000001);
        Assertions.assertEquals(256.3946710, truecitraAyanamsaObjects.longitudes()[LG], DELTA_D0000001);

        Assertions.assertEquals("256°22'36.50\"", toDMSms(lahiriAyanamsaObjects.longitudes()[LG]).toString());
        Assertions.assertEquals("256°23'40.81\"", toDMSms(truecitraAyanamsaObjects.longitudes()[LG]).toString());
    }

    @Test
    void testAyanamsa() {
        System.out.println(toDMSms(lahiriAyanamsaObjects.ayanamsa()));
        System.out.println(toDMSms(truecitraAyanamsaObjects.ayanamsa()));
    }
}
