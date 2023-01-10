/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2023-01
 */

package org.swisseph;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.swisseph.api.ISweObjects;
import org.swisseph.api.ISweObjectsOptions;
import org.swisseph.app.SweJulianDate;
import org.swisseph.app.SweObjects;
import org.swisseph.app.SweObjectsOptions;

import static org.swisseph.api.ISweObjectsOptions.DEFAULT_SS_CALC_FLAGS;
import static org.swisseph.app.SweObjectsOptions.LAHIRI_AYANAMSA;
import static org.swisseph.utils.IDegreeUtils.toDMSms;
import static swisseph.SweConst.SEFLG_NONUT;

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
 *
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
@Execution(ExecutionMode.SAME_THREAD)
public class MiscAyanamsaTest extends AbstractTest {
    final int[] date2000 = new int[]{2000, 1, 1, 0, 0, 0};

    @Test
    /**
     * What is called MEAN ayanamsha in your ephemeris:
     *  swetest -b1.1.2000 -sid1 -pb -nonut -fPTL
     *      date (dmy) 1.1.2000 greg. 0:00:00 TT version 2.10.01a
     *      Ayanamsha 01.01.2000 23°51'25.4635
     */
    void testMeanAyanamsha() {
        ISweObjectsOptions sweObjectsOptions = new SweObjectsOptions.Builder()
                .options(LAHIRI_AYANAMSA).mainFlags(DEFAULT_SS_CALC_FLAGS | SEFLG_NONUT).build();

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
    void testTrueAyanamsha() {
        ISweObjects sweObjects = new SweObjects(getSwephExp(), new SweJulianDate(date2000, 0f),
                GEO_GREENWICH, LAHIRI_AYANAMSA).completeBuild();

        // Ayanamsa
        Assertions.assertEquals("23°51'11.53\"", toDMSms(sweObjects.ayanamsa()).toString());
        Assertions.assertEquals(23.853203493056615, sweObjects.ayanamsa());
    }
}
