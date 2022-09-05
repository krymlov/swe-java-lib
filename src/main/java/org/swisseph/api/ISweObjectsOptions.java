/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2020-05
 */

package org.swisseph.api;


import java.io.Serializable;

import static org.swisseph.api.ISweConstants.d0;
import static swisseph.SweConst.*;

/**
 * @author Yura Krymlov
 * @version 1.0, 2020-05
 */
public interface ISweObjectsOptions extends Serializable, Cloneable {
    int DEFAULT_SS_RISE_SET_FLAGS = SE_BIT_HINDU_RISING;

    /**
     * Special preset of flags for planets calculation
     */
    int DEFAULT_SS_TRUEPOS_NONUT_SPEED_FLAGS =
            SEFLG_SIDEREAL  |   // sidereal zodiac
            SEFLG_SWIEPH    |   // fastest method, requires data files
            SEFLG_TRUEPOS   |   // true position of the planet versus the apparent position
            SEFLG_NONUT     |   // calculate the position of the planet without considering the nutation
            SEFLG_SPEED;        // to determine retrograde vs direct motion

    ISweHouseSystem houseSystem();
    ISweAyanamsa ayanamsa();

    default boolean trueNode() {
        return false;
    }

    default int calcFlags() {
        return DEFAULT_SS_TRUEPOS_NONUT_SPEED_FLAGS;
    }

    /**
     * This is a flag to swe_rise_trans(), by default rise/set of disc
     * center is set and refraction is not considered
     */
    default int riseSetFlags() {
        return DEFAULT_SS_RISE_SET_FLAGS;
    }

    default double initialAyanamsa() {
        return d0;
    }

    default double initialJulianDay() {
        return d0;
    }

    ISweObjectsOptions clone() throws CloneNotSupportedException;
}