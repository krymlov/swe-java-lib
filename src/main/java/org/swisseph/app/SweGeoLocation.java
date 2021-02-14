/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2019-12
 */

package org.swisseph.app;

import org.swisseph.api.ISweGeoLocation;
import swisseph.SMath;

import java.util.StringJoiner;

import static java.lang.String.valueOf;
import static org.swisseph.api.ISweConstants.ARCTIC_CIRCLE_LATITUDE;
import static org.swisseph.api.ISweConstants.d0;
import static org.swisseph.utils.IDegreeUtils.toLAT;
import static org.swisseph.utils.IDegreeUtils.toLON;

/**
 * @author Yura Krymlov
 * @version 1.1, 2019-12
 */
public class SweGeoLocation implements Cloneable, ISweGeoLocation {
    private static final long serialVersionUID = 2994093829689593444L;

    /**
     * A double[3] containing the longitude, latitude and height of the observer.
     * Eastern longitude and northern latitude is given by positive values,
     * western longitude and southern latitude by negative values.
     */
    protected final double[] geopos = new double[3];

    /**
     * Atmospheric pressure in mBar (hPa). 1013.25 mbar for sea level
     */
    protected final double pressure;

    /**
     * Atmospheric temperature in degrees Celsius.
     */
    protected final double temperature;

    /**
     * @param longitude   The longitude on earth, for which the calculation has to be
     *                    done. Eastern longitude and northern latitude is given by positive values,
     *                    western longitude and southern latitude by negative values.
     * @param latitude    The latitude on earth, for which the calculation has to be
     *                    done.
     * @param altitude    The height above sea level in meters
     * @param temperature atmospheric temperature in degrees Celsius
     */
    public SweGeoLocation(final double longitude, final double latitude,
                          final double altitude, final double temperature, final double pressure) {
        this.temperature = temperature;
        this.geopos[0] = longitude;
        this.geopos[1] = latitude;
        this.geopos[2] = altitude;
        this.pressure = pressure;
    }

    /**
     * @param longitude   The longitude on earth, for which the calculation has to be
     *                    done. Eastern longitude and northern latitude is given by positive values,
     *                    western longitude and southern latitude by negative values.
     * @param latitude    The latitude on earth, for which the calculation has to be
     *                    done.
     * @param altitude    The height above sea level in meters
     * @param temperature atmospheric temperature in degrees Celsius
     */
    public SweGeoLocation(final double longitude, final double latitude, final double altitude, final double temperature) {
        this(longitude, latitude, altitude, temperature, DEFAULT_ATMOS_PRESSURE);
    }

    public SweGeoLocation(final double longitude, final double latitude, final double altitude) {
        this(longitude, latitude, altitude, DEFAULT_ATMOS_TEMPERATURE, DEFAULT_ATMOS_PRESSURE);
    }

    public SweGeoLocation(final double longitude, final double latitude) {
        this(longitude, latitude, d0, DEFAULT_ATMOS_TEMPERATURE, DEFAULT_ATMOS_PRESSURE);
    }

    public SweGeoLocation(final double[] geopos) {
        this(geopos[0], geopos[1], geopos[2], DEFAULT_ATMOS_TEMPERATURE, DEFAULT_ATMOS_PRESSURE);
    }

    /**
     * @return a double[3] containing the longitude, latitude and altitude
     */
    @Override
    public double[] coordinates() {
        return geopos;
    }

    @Override
    public double temperature() {
        return temperature;
    }

    @Override
    public double pressure() {
        return pressure;
    }

    @Override
    public double longitude() {
        return geopos[0];
    }

    @Override
    public double latitude() {
        return geopos[1];
    }

    @Override
    public double altitude() {
        return geopos[2];
    }

    @Override
    public boolean withinPolarCircle() {
        return SMath.abs(latitude()) >= ARCTIC_CIRCLE_LATITUDE;
    }

    @Override
    public String toString() {
        return new StringJoiner("/")
                .add(toLON(geopos[0])).add(toLAT(geopos[1])).add(valueOf(geopos[2]))
                .add(valueOf(temperature)).add(valueOf(pressure)).toString();
    }

}
