/*
 * Copyright (C) By the Author
* Author    Yura Krymlov
 * Created   2019-10
 */

package org.swisseph.app;


import org.swisseph.ISwissEph;
import org.swisseph.api.ISweGeoLocation;
import org.swisseph.api.ISweJulianDate;
import org.swisseph.api.ISweObjects;
import org.swisseph.api.ISweObjectsOptions;


/**
 * The class is intended to build {@link SweObjects} instance
 *
 * @author Yura Krymlov
 * @version 1.1, 2019-10
 */
public class SweObjectsBuilder {
    protected final SweObjects sweObjects;

    public SweObjectsBuilder(ISwissEph swissEph, ISweJulianDate julianDate, ISweGeoLocation geolocation, ISweObjectsOptions objectsOptions) {
        this.sweObjects = new SweObjects(swissEph, julianDate, geolocation, objectsOptions);
    }

    public SweObjectsBuilder(ISwissEph swissEph, ISweJulianDate julianDate, ISweObjects sweObjects) {
        this.sweObjects = new SweObjects(swissEph, julianDate, sweObjects);
    }

    /**
     * Calculates only Ascendant
     */
    public ISweObjects buildAscendant() {
        return sweObjects.buildAscendant();
    }

    /**
     * Calculates only SUN and MOON
     */
    public ISweObjects buildSunMoon() {
        return sweObjects.buildSunMoon();
    }

    /**
     * Calculates only the given Object
     */
    public ISweObjects buildObject(final int objectId) {
        return sweObjects.buildObject(objectId);
    }

    /**
     * Calculates only Mars and up to Ketu
     */
    public ISweObjects buildMarsKetu() {
        return sweObjects.buildMarsKetu();
    }

    /**
     * Calculates only the Lunar Nodes
     */
    public ISweObjects buildLunarNodes() {
        return sweObjects.buildLunarNodes();
    }

    /**
     * Calculates only Uranus and up to Pluto
     */
    public ISweObjects buildUranusPluto() {
        return sweObjects.buildUranusPluto();
    }

    /**
     * Calculate all Objects (complete build)
     */
    public ISweObjects completeBuild() {
        return sweObjects.completeBuild();
    }
}
