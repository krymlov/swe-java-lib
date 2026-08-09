/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */

package org.swisseph.api;

import java.io.Serializable;

/**
 * One station of a planet: the moment its apparent motion in longitude reverses.
 * <p>
 * A planet is <i>stationary retrograde</i> when it stops moving forward and starts moving
 * backward, and <i>stationary direct</i> when it does the opposite. Both are the instant the
 * longitudinal speed crosses zero; {@link #retrograde()} says which of the two it is.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 * @see org.swisseph.app.SweStations
 */
public interface ISweStation extends Serializable {

    /**
     * @return the Swiss Ephemeris object number, e.g. {@code SE_MERCURY}
     */
    int planet();

    /**
     * @return the moment of the station, universal time
     */
    ISweJulianDate julianDate();

    /**
     * @return the longitude the planet turns at, in the frame the calculator was configured
     *         with (tropical unless {@code SEFLG_SIDEREAL} was given)
     */
    double longitude();

    /**
     * @return {@code true} when the planet turns <b>retrograde</b> here (it was direct and
     *         starts moving backward), {@code false} when it turns <b>direct</b>
     */
    boolean retrograde();
}
