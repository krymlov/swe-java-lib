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

    int DEFAULT_SS_MAIN_FLAGS =
            SEFLG_SIDEREAL |        // sidereal zodiac
                    SEFLG_SWIEPH;   // fastest method, requires data files

    int DEFAULT_SS_HOUSE_FLAGS =
            DEFAULT_SS_MAIN_FLAGS;

    int DEFAULT_SS_CALC_FLAGS =
            DEFAULT_SS_MAIN_FLAGS |
                    SEFLG_TRUEPOS |   // true position of the planet versus the apparent position
                    SEFLG_SPEED;      // to determine retrograde vs direct motion

    int DEFAULT_SS_TRANSIT_FLAGS =
            DEFAULT_SS_MAIN_FLAGS |
                    SEFLG_TRANSIT_LONGITUDE |   // calculate transits over a longitude in TransitCalculator
                    SEFLG_TRUEPOS;              // true position of the planet versus the apparent position


    ISweHouseSystem houseSystem();

    ISweAyanamsa ayanamsa();

    default boolean trueNode() {
        return false;
    }

    default int mainFlags() {
        return DEFAULT_SS_MAIN_FLAGS;
    }

    default int houseFlags() {
        return DEFAULT_SS_HOUSE_FLAGS;
    }

    default int calcFlags() {
        return DEFAULT_SS_CALC_FLAGS;
    }

    default int transitFlags() {
        return DEFAULT_SS_TRANSIT_FLAGS;
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