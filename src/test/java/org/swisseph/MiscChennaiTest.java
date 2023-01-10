/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2021-02
 */
package org.swisseph;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.swisseph.api.*;
import org.swisseph.app.*;

import java.util.Calendar;

import static java.util.Calendar.*;
import static org.swisseph.app.SweAyanamsa.LAHIRI;
import static org.swisseph.app.SweHouseSystem.WHOLE_SIGN;
import static org.swisseph.utils.IModuloUtils.modulo;

/**
 * @author Yura Krymlov
 * @version 1.0, 2021-02
 */
public class MiscChennaiTest extends AbstractTest {
    public static final double DELTA_MIN = 0.002;
    public static final double DELTA_MAX = 0.2;

    @Test
    void testSweVersion() {
        Assertions.assertEquals(getSwephExp().swe_version(), "2.10.03");
        Assertions.assertEquals(getSwissEph().swe_version(), "2.01.00");
    }

    @ParameterizedTest()
    @EnumSource(SweHouseSystem.class)
    void testHouseSystem(SweHouseSystem houseSystem) {
        if (SweHouseSystem.NIL.equals(houseSystem)) return;
        assertEquals(newSweObjects(1900 + houseSystem.fid(), LAHIRI, houseSystem, true));
    }

    @RepeatedTest(150)
    void testSweDates(RepetitionInfo info) {
        assertEquals(newSweObjects(1900 + info.getCurrentRepetition(), LAHIRI, WHOLE_SIGN, false));
    }

    @ParameterizedTest()
    @EnumSource(SweAyanamsa.class)
    void testAyanamsa(SweAyanamsa ayanamsa) {
        if (SweAyanamsa.AY_USER.equals(ayanamsa)) return;
        assertEquals(newSweObjects(2000 + ayanamsa.fid(), ayanamsa, WHOLE_SIGN, false));
    }

    ISweObjects[] newSweObjects(int year, ISweAyanamsa ayanamsa, ISweHouseSystem houseSystem, boolean trueNode) {
        final Calendar calendar = Calendar.getInstance();

        calendar.set(YEAR, year);
        calendar.set(MONTH, modulo(11, ayanamsa.fid()));
        calendar.set(DAY_OF_MONTH, modulo(28, ayanamsa.fid()));

        calendar.set(HOUR_OF_DAY, 12);
        calendar.set(MILLISECOND, 0);
        calendar.set(MINUTE, 0);
        calendar.set(SECOND, 0);

        final ISweJulianDate julianDate1 = new SweJulianDate(calendar);
        final ISweJulianDate julianDate2 = new SweJulianDate(calendar);
        final ISweObjectsOptions objectsOptions = new SweObjectsOptions.Builder()
            .ayanamsa(ayanamsa).houseSystem(houseSystem).trueNode(trueNode).build();

        final ISweObjects objects1 = new SweObjects(getSwephExp(),
                julianDate1, GEO_CHENNAI, objectsOptions).completeBuild();

        final ISweObjects objects2 = new SweObjects(getSwissEph(),
                julianDate2, GEO_CHENNAI, objectsOptions).completeBuild();

        return new ISweObjects[]{objects1, objects2};
    }

    void assertEquals(ISweObjects[] sweObjects) {
        final ISweObjects objects1 = sweObjects[0], objects2 = sweObjects[1];
        final ISweJulianDate julianDate1 = objects1.sweJulianDate(),
                julianDate2 = objects2.sweJulianDate();

        Assertions.assertArrayEquals(julianDate1.date(), julianDate2.date());
        Assertions.assertEquals(julianDate1.julianDay(), julianDate2.julianDay());
        Assertions.assertArrayEquals(julianDate1.values(), julianDate2.values(), DELTA_MIN);

        Assertions.assertArrayEquals(objects1.longitudes(), objects2.longitudes(), DELTA_MAX);
        Assertions.assertArrayEquals(objects1.latitudes(), objects2.latitudes(), DELTA_MAX);
        Assertions.assertEquals(objects1.ayanamsa(), objects2.ayanamsa(), DELTA_MAX);

        Assertions.assertArrayEquals(objects1.retrogrades(), objects2.retrogrades());
        Assertions.assertArrayEquals(objects1.houses(), objects2.houses());
        Assertions.assertArrayEquals(objects1.signs(), objects2.signs());

        Assertions.assertArrayEquals(objects1.sweSequence().objects(),
                objects2.sweSequence().objects());

        Assertions.assertArrayEquals(objects1.sweSequence().degrees(),
                objects2.sweSequence().degrees(), DELTA_MAX);
    }

}
