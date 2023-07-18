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
import static org.swisseph.app.SweObjectsOptions.TROPICAL_ZODIAC;
import static swisseph.SweConst.SEFLG_TRUEPOS;

/**
 * @author Yura Krymlov
 * @version 1.0, 2023-07
 */
public class SayanaTest extends AbstractTest {
    final int[] date1947 = new int[]{1947, 8, 15, 10, 30};

    @Test
    void testObjects_WITHOUT_SEFLG_TRUEPOS() {
        ISweObjectsOptions sweObjectsOptions = new SweObjectsOptions.Builder()
                .calcFlags(DEFAULT_SS_CALC_FLAGS ^ SEFLG_TRUEPOS)
                .ayanamsa(null)
                .build();

        ISweObjects sweObjects = new SweObjects(getSwephExp(), new SweJulianDate(date1947, 0f, 10.5),
                GEO_LUCKNOW, sweObjectsOptions).completeBuild();

        Assertions.assertEquals(2432412.9375, sweObjects.sweJulianDate().julianDay());
        Assertions.assertEquals(28.108929, sweObjects.sweJulianDate().deltaT() * d86400, DELTA_D000001);
        Assertions.assertEquals(2432412.937825335, sweObjects.sweJulianDate().epheTime(), DELTA_D0000001);

        Assertions.assertEquals(279.4986707, sweObjects.longitudes()[LG], DELTA_D0000001);
        Assertions.assertEquals(141.7515650, sweObjects.longitudes()[SY], DELTA_D0000001);
        Assertions.assertEquals(127.1834532, sweObjects.longitudes()[CH], DELTA_D0000001);
        Assertions.assertEquals(128.0103526, sweObjects.longitudes()[BU], DELTA_D0000001);
        Assertions.assertEquals(136.5065020, sweObjects.longitudes()[SK], DELTA_D0000001);
        Assertions.assertEquals( 91.0162867, sweObjects.longitudes()[MA], DELTA_D0000001);
        Assertions.assertEquals(229.0570298, sweObjects.longitudes()[GU], DELTA_D0000001);
        Assertions.assertEquals(133.6800857, sweObjects.longitudes()[SA], DELTA_D0000001);
        Assertions.assertEquals(85.2010449, sweObjects.longitudes()[UR], DELTA_D0000001);
        Assertions.assertEquals(188.8473463, sweObjects.longitudes()[NE], DELTA_D0000001);
        Assertions.assertEquals(133.2496375, sweObjects.longitudes()[PL], DELTA_D0000001);
        Assertions.assertEquals(58.1562615, sweObjects.longitudes()[RA], DELTA_D0000001);
    }

    @Test
    void testObjects_WITH_SEFLG_TRUEPOS() {
        ISweObjects sweObjects = new SweObjects(getSwephExp(), new SweJulianDate(date1947, 0f, 10.5),
                GEO_LUCKNOW, TROPICAL_ZODIAC).completeBuild();

        Assertions.assertEquals(2432412.9375, sweObjects.sweJulianDate().julianDay());
        Assertions.assertEquals(28.108929, sweObjects.sweJulianDate().deltaT() * d86400, DELTA_D000001);
        Assertions.assertEquals(2432412.937825335, sweObjects.sweJulianDate().epheTime(), DELTA_D0000001);

        Assertions.assertEquals(279.4986707, sweObjects.longitudes()[LG], DELTA_D0000001);
        Assertions.assertEquals(141.7571872, sweObjects.longitudes()[SY], DELTA_D0000001);
        Assertions.assertEquals(127.1836632, sweObjects.longitudes()[CH], DELTA_D0000001);
        Assertions.assertEquals(128.0228216, sweObjects.longitudes()[BU], DELTA_D0000001);
        Assertions.assertEquals(136.5187867, sweObjects.longitudes()[SK], DELTA_D0000001);
        Assertions.assertEquals( 91.0235540, sweObjects.longitudes()[MA], DELTA_D0000001);
        Assertions.assertEquals(229.0597093, sweObjects.longitudes()[GU], DELTA_D0000001);
        Assertions.assertEquals(133.6875775, sweObjects.longitudes()[SA], DELTA_D0000001);
        Assertions.assertEquals(85.20539927, sweObjects.longitudes()[UR], DELTA_D0000001);
        Assertions.assertEquals(188.8522501, sweObjects.longitudes()[NE], DELTA_D0000001);
        Assertions.assertEquals(133.2561482, sweObjects.longitudes()[PL], DELTA_D0000001);
        Assertions.assertEquals(58.1562615, sweObjects.longitudes()[RA], DELTA_D0000001);
    }

}
