/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2020-05
 */

package org.swisseph.api;


import java.io.Serializable;

import static swisseph.SweConst.*;

/**
 * @author Yura Krymlov
 * @version 1.0, 2020-05
 */
public interface ISweObjectsOptions extends Serializable, Cloneable {
    int DEFAULT_SS_RISE_SET_FLAGS = SE_BIT_DISC_CENTER | SE_BIT_NO_REFRACTION;

    /**
     * Special preset of flags for planets calculation
     */
    int DEFAULT_SS_TRUEPOS_NONUT_SPEED_FLAGS =
            SEFLG_SIDEREAL  |   // sidereal zodiac
            SEFLG_SWIEPH    |   // fastest method, requires data files
            SEFLG_TRUEPOS   |   // true position of the planet versus the apparent position
            SEFLG_NONUT     |   // calculate the position of the planet without considering the nutation
            SEFLG_SPEED;        // to determine retrograde vs direct motion

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
        return DEFAULT_SS_RISE_SET_FLAGS;
    }
}