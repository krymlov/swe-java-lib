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
import static org.swisseph.app.SweObjectsOptions.TRUECITRA_AYANAMSA;
import static org.swisseph.utils.IDegreeUtils.toDMSms;
import static swisseph.SweConst.*;

/**
 * @author Yura Krymlov
 * @version 1.0, 2023-01
 */
public class JagannathaHoraTest extends AbstractTest {
    final int[] date1947 = new int[]{1947, 8, 15, 10, 30, 0};

    @Test
    void test1947TruePosMeanAyanamsha() {
        ISweObjectsOptions sweObjectsOptions = new SweObjectsOptions.Builder().options(TRUECITRA_AYANAMSA)
                .mainFlags(DEFAULT_SS_MAIN_FLAGS | SEFLG_NONUT | SEFLG_TRUEPOS)
                .houseFlags(DEFAULT_SS_HOUSE_FLAGS)
                .calcFlags(DEFAULT_SS_CALC_FLAGS)
                .build();

        ISweObjects sweObjects = new SweObjects(getSwephExp(), new SweJulianDate(date1947, 0f),
                GEO_LUCKNOW, sweObjectsOptions).completeBuild();

        // Ayanamsa
        Assertions.assertEquals("23°06'37.30\"", toDMSms(sweObjects.ayanamsa()).toString());
        Assertions.assertEquals(23.110361977858815, sweObjects.ayanamsa());

        // FIXME: Lagna is not equal to the one from the APP: JagannathaHora 8.x
        Assertions.assertEquals(256.3946709789843, sweObjects.longitudes()[LG], DELTA_D0000001);
        Assertions.assertEquals("256°23'40.82\"", toDMSms(sweObjects.longitudes()[LG]).toString());

        // Sun
        Assertions.assertEquals(118.65043693114977, sweObjects.longitudes()[SY], DELTA_D0000001);
        Assertions.assertEquals("118°39'01.57\"", toDMSms(sweObjects.longitudes()[SY]).toString());
    }
}
