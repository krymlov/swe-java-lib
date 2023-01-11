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

import static org.swisseph.api.ISweConstants.DELTA_D0000001;
import static org.swisseph.api.ISweObjects.LG;
import static org.swisseph.api.ISweObjects.SY;
import static org.swisseph.api.ISweObjectsOptions.*;
import static org.swisseph.app.SweObjectsOptions.LAHIRI_AYANAMSA;
import static org.swisseph.app.SweObjectsOptions.TRUECITRA_AYANAMSA;
import static org.swisseph.utils.IDegreeUtils.toDMSms;
import static swisseph.SweConst.SEFLG_NONUT;
import static swisseph.SweConst.SEFLG_TRUEPOS;

/**
 * <pre>
 * Alois Treindl <alois@astro.ch>
 *
 * What is called MEAN ayanamsha in your ephemeris:
 *  swetest -b1.1.2000 -sid1 -pb -nonut -fPTL
 *      date (dmy) 1.1.2000 greg. 0:00:00 TT version 2.10.01a
 *      Ayanamsha 01.01.2000 23°51'25.4635
 *  <br>
 * What is called TRUE aynanamsha in your ephemeris:
 *  swetest -b1.1.2000 -sid1 -pb -fPTL
 *      date (dmy) 1.1.2000 greg. 0:00:00 TT version 2.10.01a
 *      Ayanamsha 01.01.2000 23°51'11.5325
 * </pre>
 * <p>
 * // ------------------------------------------------------------------
 *
 * <pre>
 * If you want true chitra paksha ayanamsha, you have to use the -sid27 .
 *  If you add the parameter -true (with any ayanamsha), it suppresses light-time, aberration and light deflection. This is
 *  clearly stated in my mail further below, but apparently not clearly enough in the documentation of swetest. If you want
 *  observable positions, then you should *not* use -true.
 *  The difference between positions with and without -true (i.e. between apparent and geometric/"true" positions) is not
 *  greater than 20 arcsec, if I'm right.
 *  I recommend you to use -sid27 (for true chitrapaksha) or sid1 (for Lahiri), but without -true. Most ephemerides are
 *  apparent, not geometric.
 * Best wishes,
 * Dieter
 * </pre>
 *
 * @author Yura Krymlov
 * @version 1.0, 2023-01
 */
public class MiscAyanamsaTest extends AbstractTest {
    final int[] date2000 = new int[]{2000, 1, 1, 0, 0, 0};
    final int[] date1947 = new int[]{1947, 8, 15, 10, 30, 0};

    @Test
    /**
     * What is called MEAN ayanamsha in your ephemeris:
     *  swetest -b1.1.2000 -sid1 -pb -nonut -fPTL
     *      date (dmy) 1.1.2000 greg. 0:00:00 TT version 2.10.01a
     *      Ayanamsha 01.01.2000 23°51'25.4635
     */
    void test2000MeanAyanamsha() {
        ISweObjectsOptions sweObjectsOptions = new SweObjectsOptions.Builder()
                .options(LAHIRI_AYANAMSA).mainFlags(DEFAULT_SS_MAIN_FLAGS | SEFLG_NONUT).build();

        ISweObjects sweObjects = new SweObjects(getSwephExp(), new SweJulianDate(date2000, 0f),
                GEO_GREENWICH, sweObjectsOptions).completeBuild();

        // Ayanamsa
        Assertions.assertEquals("23°51'25.46\"", toDMSms(sweObjects.ayanamsa()).toString());
        Assertions.assertEquals(23.857073231355002, sweObjects.ayanamsa());
    }

    @Test
    /**
     * What is called TRUE aynanamsha in your ephemeris:
     *  swetest -b1.1.2000 -sid1 -pb -fPTL
     *      date (dmy) 1.1.2000 greg. 0:00:00 TT version 2.10.01a
     *      Ayanamsha 01.01.2000 23°51'11.5325
     */
    void test2000TrueAyanamsha() {
        ISweObjects sweObjects = new SweObjects(getSwephExp(), new SweJulianDate(date2000, 0f),
                GEO_GREENWICH, LAHIRI_AYANAMSA).completeBuild();

        // Ayanamsa
        Assertions.assertEquals("23°51'11.53\"", toDMSms(sweObjects.ayanamsa()).toString());
        Assertions.assertEquals(23.853203493056615, sweObjects.ayanamsa());
    }

    @Test
    void test1947MeanAyanamsha() {
        ISweObjectsOptions sweObjectsOptions = new SweObjectsOptions.Builder().options(TRUECITRA_AYANAMSA)
                .mainFlags(DEFAULT_SS_MAIN_FLAGS | SEFLG_NONUT)
                .houseFlags(DEFAULT_SS_HOUSE_FLAGS | SEFLG_NONUT)
                .calcFlags((DEFAULT_SS_CALC_FLAGS | SEFLG_NONUT) ^ SEFLG_TRUEPOS)
                .build();

        ISweObjects sweObjects = new SweObjects(getSwephExp(), new SweJulianDate(date1947, 0f),
                GEO_LUCKNOW, sweObjectsOptions).completeBuild();

        // Ayanamsa
        Assertions.assertEquals("23°06'27.40\"", toDMSms(sweObjects.ayanamsa()).toString());
        Assertions.assertEquals(23.10761141107119, sweObjects.ayanamsa());

        // Lagna
        Assertions.assertEquals(256.3951080, sweObjects.longitudes()[LG], DELTA_D0000001);
        Assertions.assertEquals("256°23'42.39\"", toDMSms(sweObjects.longitudes()[LG]).toString());

        // Sun
        Assertions.assertEquals(118.6475653, sweObjects.longitudes()[SY], DELTA_D0000001);
        Assertions.assertEquals("118°38'51.24\"", toDMSms(sweObjects.longitudes()[SY]).toString());
    }

    @Test
    void test1947TrueAyanamsha() {
        ISweObjects sweObjects = new SweObjects(getSwephExp(), new SweJulianDate(date1947, 0f),
                GEO_LUCKNOW, TRUECITRA_AYANAMSA).completeBuild();

        // Ayanamsa
        Assertions.assertEquals("23°06'14.40\"", toDMSms(sweObjects.ayanamsa()).toString());
        Assertions.assertEquals(23.103999730687732, sweObjects.ayanamsa());

        // Lagna
        Assertions.assertEquals(256.3946709789843, sweObjects.longitudes()[LG], DELTA_D0000001);
        Assertions.assertEquals("256°23'40.82\"", toDMSms(sweObjects.longitudes()[LG]).toString());

        // Sun
        Assertions.assertEquals(118.65043693114977, sweObjects.longitudes()[SY], DELTA_D0000001);
        Assertions.assertEquals("118°39'01.57\"", toDMSms(sweObjects.longitudes()[SY]).toString());
    }

}
