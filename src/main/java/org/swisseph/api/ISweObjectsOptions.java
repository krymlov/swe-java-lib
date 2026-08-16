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
    /**
     * Sunrise/sunset definition: the centre of the solar disc geometrically on the horizon,
     * refraction ignored.
     * <p>
     * Deliberately <b>not</b> {@code SE_BIT_HINDU_RISING}, which is these same two bits plus
     * {@code SE_BIT_GEOCTR_NO_ECL_LAT} - that third bit drops the observer's parallax and
     * computes the rising as if seen from the centre of the Earth. It is a convention of Indian
     * panchangas rather than a more accurate result, and it shifts sunrise by ~0.6 s (measured
     * at 81&deg;08'E for 1970: 00:32:21.2 UT geocentric against 00:32:21.8 UT topocentric).
     * Everything derived from sunrise inherits that shift magnified by its own speed - 0.6 s is
     * three arcminutes of Vighati Lagna, which crosses a whole sign in two minutes.
     * <p>
     * Jagannatha Hora offers four sunrise definitions and is set to this one, so matching it
     * keeps every sunrise-derived point (Gulika, Maandi, the Kalavela upagrahas, the time
     * lagnas) directly comparable. Changed 2026-08-16 on the author's decision; pass
     * {@code riseSetFlags(SE_BIT_HINDU_RISING)} to a {@code SweObjectsOptions.Builder} to get
     * the geocentric panchanga convention back.
     */
    int DEFAULT_SS_RISE_SET_FLAGS = SE_BIT_DISC_CENTER | SE_BIT_NO_REFRACTION;

    int DEFAULT_SS_MAIN_FLAGS =
            SEFLG_SIDEREAL |        // sidereal zodiac
                    SEFLG_SWIEPH;   // fastest method, requires data files

    /**
     * <code>SEFLG_TRUEPOS</code> does not change the house geometry itself, but
     * <code>swe_houses_ex()</code> passes the flags on to <code>swe_get_ayanamsa_ex()</code>.
     * For star-based ayanamsas (True Chitrapaksha and alike) the ayanamsa is derived from
     * the computed position of the star, so the flag decides whether that position is the
     * true (geometric) or the apparent one.
     * <p>
     * Keep it here and not in {@link #DEFAULT_SS_MAIN_FLAGS}, because the main flags are
     * also used for things that must stay apparent. The ayanamsa reported by
     * {@link ISweObjects#ayanamsa()} adds <code>SEFLG_TRUEPOS</code> itself, so the value
     * shown to the user is the one the houses and the planets were built on, and matches
     * <code>swetest ... -true</code>.
     */
    int DEFAULT_SS_HOUSE_FLAGS =
            DEFAULT_SS_MAIN_FLAGS |
                    SEFLG_TRUEPOS;  // true position of the star behind the ayanamsa

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