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
import static org.swisseph.app.SweObjectsOptions.TRUECITRA_AYANAMSA;
import static org.swisseph.utils.IDegreeUtils.*;
import static swisseph.SweConst.SEFLG_TRUEPOS;

/**
 * <pre>
 * swetest -b15.08.1947 -ut10:30 -fPl -sid27 -house81.83,25.95,P
 *  date (dmy) 15.8.1947 greg.   10:30:00 UT                version 2.10.03
 *  UT:  2432412.937500000     delta t: 28.108929 sec
 *  TT:  2432412.937825335   ayanamsa =   23° 6'14.3990 (True Citra)
 *  geo. long 81.830000, lat 25.950000, alt 0.000000
 *  Epsilon (m)       23°26'45.9391
 *  Houses system P (Placidus) for long=  81°49'48.0000, lat=  25°57' 0.0000
 *  Sun              118.6475653
 *  house  1         256.3946710
 *  house  2         291.2081136
 *  house  3         327.9056119
 *  house  4          1.0044612
 *  house  5         28.5726703
 *  house  6         52.6184650
 *  house  7         76.3946710
 *  house  8         111.2081136
 *  house  9         147.9056119
 *  house 10         181.0044612
 *  house 11         208.5726703
 *  house 12         232.6184650
 *  Ascendant        256.3946710
 *  MC               181.0044612
 *  ARMC             202.3204410
 *  Vertex           118.4700627
 *  equat. Asc.      267.5346781
 *  co-Asc. W.Koch   277.2651543
 *  co-Asc Munkasey  220.0008960
 *  Polar Asc.       97.2651543
 * </pre>
 *
 * @author Yura Krymlov
 * @version 1.0, 2023-01
 */
public class Ayanamsa27Test extends AbstractTest {
    final int[] date1947 = new int[]{1947, 8, 15, 10, 30, 0};

    @Test
    void testObjects_WITHOUT_SEFLG_TRUEPOS() {
        ISweObjectsOptions sweObjectsOptions = new SweObjectsOptions.Builder()
                .options(TRUECITRA_AYANAMSA).calcFlags(DEFAULT_SS_CALC_FLAGS ^ SEFLG_TRUEPOS).build();

        ISweObjects sweObjects = new SweObjects(getSwephExp(), new SweJulianDate(date1947, 0f),
                GEO_LUCKNOW, sweObjectsOptions).completeBuild(); // - SEFLG_TRUEPOS

        Assertions.assertEquals(2432412.9375, sweObjects.sweJulianDate().julianDay());
        Assertions.assertEquals(28.108929, sweObjects.sweJulianDate().deltaT() * d86400, DELTA_D000001);
        Assertions.assertEquals(2432412.937825335, sweObjects.sweJulianDate().ephemerisTime(), DELTA_D0000001);

        // Ayanamsa
        Assertions.assertEquals(23.103999730687732, sweObjects.ayanamsa());
        Assertions.assertEquals(23061440, toIDMSms(sweObjects.ayanamsa()));
        Assertions.assertEquals("23°06'14.40\"", toDMSms(23061440).toString());
        Assertions.assertEquals("23°06'14.40\"", toDMSms(sweObjects.ayanamsa()).toString());
        Assertions.assertEquals("23°06'14.40\"", toDMSms(toDDms(23061440)).toString());

        // Lagna
        Assertions.assertEquals(256.3946710, sweObjects.longitudes()[LG], DELTA_D0000001);
        Assertions.assertEquals("256°23'40.82\"", toDMSms(sweObjects.longitudes()[LG]).toString());

        // Sun
        Assertions.assertEquals(118.6475653, sweObjects.longitudes()[SY], DELTA_D0000001);
        Assertions.assertEquals("118°38'51.24\"", toDMSms(sweObjects.longitudes()[SY]).toString());
    }

    @Test
    void testObjects_WITH_SEFLG_TRUEPOS() {
        ISweObjectsOptions sweObjectsOptions = new SweObjectsOptions.Builder().options(TRUECITRA_AYANAMSA).build();
        ISweObjects sweObjects = new SweObjects(getSwephExp(), new SweJulianDate(date1947, 0f),
                GEO_LUCKNOW, sweObjectsOptions).completeBuild();

        // Lagna
        Assertions.assertEquals(256.3946710, sweObjects.longitudes()[LG], DELTA_D0000001);
        Assertions.assertEquals("256°23'40.82\"", toDMSms(sweObjects.longitudes()[LG]).toString());

        // Sun
        Assertions.assertEquals(118.6504369, sweObjects.longitudes()[SY], DELTA_D0000001);
        Assertions.assertEquals("118°39'01.57\"", toDMSms(sweObjects.longitudes()[SY]).toString());
    }
}
