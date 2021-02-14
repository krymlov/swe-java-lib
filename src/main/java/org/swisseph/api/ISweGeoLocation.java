/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2020-05
 */

package org.swisseph.api;

import java.io.Serializable;

import static org.swisseph.api.ISweConstants.d8;

/**
 * @author Yura Krymlov
 * @version 1.0, 2020-05
 */
public interface ISweGeoLocation extends Serializable {

    /**
     * Default atmospheric pressure in mBar (hPa)- 1013.25 mbar for sea level
     */
    double DEFAULT_ATMOS_PRESSURE = 1013.25D;

    double DEFAULT_ATMOS_TEMPERATURE = d8;

    /**
     * @return a double[3] containing the longitude, latitude and altitude
     */
    double[] coordinates();

    double temperature();

    double longitude();

    double latitude();

    double altitude();

    double pressure();

    boolean withinPolarCircle();
}