/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2020-01
 */

package org.swisseph.api;

import org.swisseph.ISwissEph;

import java.io.Serializable;

/**
 * @author Yura Krymlov
 * @version 1.0, 2020-01
 */
public interface ISweContext {
    /**
     * @return {@link ISwissEph}
     */
    ISwissEph swissEph();

    /**
     * @return {@link ISweJulianDate}
     */
    ISweJulianDate sweJulianDate();

    /**
     * @return {@link ISweGeoLocation}
     */
    ISweGeoLocation sweLocation();

    /**
     * @return {@link ISweObjectsOptions}
     */
    ISweObjectsOptions sweOptions();

    /**
     * @return sorted sequence of {@link ISweObjects} by longitude
     */
    ISweObjectsSequence sweSequence();
}
