/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2020-05
 */

package org.swisseph.api;


import swisseph.SweConst;

import java.io.Serializable;

import static swisseph.SweConst.*;

/**
 * @author Yura Krymlov
 * @version 1.0, 2020-05
 */
public interface ISweObjectsOptions extends Serializable, Cloneable {
    int SS_RISE_SET_FLAGS = SE_BIT_DISC_CENTER | SE_BIT_NO_REFRACTION;

    /**
     * Special preset of flags for planets calculation
     */
    int SS_TRUEPOS_SPEED_FLAGS =
            SweConst.SEFLG_TRUEPOS |   // true position of the planet versus the apparent position
            SweConst.SEFLG_NONUT |     // will be set automatically for sidereal calculations
            SweConst.SEFLG_SPEED |     // to determine retrograde vs direct motion
            SweConst.SEFLG_SWIEPH |    // fastest method, requires data files
            SweConst.SEFLG_SIDEREAL;   // sidereal zodiac

    /**
     * Special preset of flags for houses calculation
     */
    int SS_TRUEPOS_FLAGS = SEFLG_SIDEREAL | SEFLG_SWIEPH | SEFLG_TRUEPOS;

    /**
     * Special preset of flags for transit-longitude calculation
     */
    int SS_TRUEPOS_TRANSIT_FLAGS = SEFLG_SIDEREAL | SEFLG_SWIEPH | SEFLG_TRUEPOS | SEFLG_TRANSIT_LONGITUDE;

    ISweObjectsOptions clone() throws CloneNotSupportedException;

    ISweHouseSystem houseSystem();

    ISweAyanamsa ayanamsa();

    boolean trueNode();

    int calcFlags();

    double initialAyanamsa();

    double initialJulianDay();

    /**
     * This is a flag to swe_rise_trans(), by default rise/set of disc
     * center is set and refraction is not considered
     */
    default int riseSetFlags() {
        return SS_RISE_SET_FLAGS;
    }
}