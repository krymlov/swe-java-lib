/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2019-11
 */

package org.swisseph.api;

import org.swisseph.ISwissEph;

import java.io.Serializable;

import static org.swisseph.api.ISweConstants.i1;
import static org.swisseph.api.ISweConstants.i12;
import static swisseph.SweConst.*;

/**
 * @author Yura Krymlov
 * @version 1.1, 2019-11
 */
public interface ISweObjects extends ISweContext, Serializable {
    String CALC_FAILED = "Calculation is failed probably due to nearness to the polar circle";

    int[] OBJECTS_TRUE_NODE = {
            ERR, // 0
            SE_SUN, SE_MOON, SE_MARS, SE_MERCURY, SE_JUPITER, SE_VENUS, SE_SATURN,
            SE_TRUE_NODE, // 8
            ERR, // 9
            SE_URANUS, SE_NEPTUNE, SE_PLUTO};

    int[] OBJECTS_MEAN_NODE = {
            ERR, // 0
            SE_SUN, SE_MOON, SE_MARS, SE_MERCURY, SE_JUPITER, SE_VENUS, SE_SATURN,
            SE_MEAN_NODE, // 8
            ERR, // 9
            SE_URANUS, SE_NEPTUNE, SE_PLUTO};

    int LG = 0, // Lagna
        SY = 1, // Surya
        CH = 2, // Chandra
        MA = 3, // Mangala
        BU = 4, // Budha
        GU = 5, // Guru
        SK = 6, // Shukra
        SA = 7, // Shani
        RA = 8, // Rahu
        KE = 9, // Ketu
        UR = 10,// Uranus or Sweta
        NE = 11,// Neptune or Syama
        PL = 12;// Pluto or Teekshana/Teevra

    int FIRST_OBJECT_ID = SY;
    int LAST_OBJECT_ID = PL;

    int ASCMC_COUNT = 10;
    int CUSPS_COUNT = 14;

    /**
     * Number of planets + ascendant
     */
    int OBJECTS_COUNT = LAST_OBJECT_ID + 1;

    /**
     * Builds the {@link ISweObjects} instance with calculated OBJECT ID.
     */
    ISweObjects buildObject(final int objectId);

    /**
     * Builds the {@link ISweObjects} instance with calculated Ascendant.
     */
    ISweObjects buildAscendant();

    /**
     * Builds the {@link ISweObjects} instance with calculated Lunar Nodes.
     */
    ISweObjects buildLunarNodes();

    /**
     * Builds the {@link ISweObjects} instance with only calculated Sun and Moon data.
     */
    ISweObjects buildSunMoon();

    /**
     * Builds the {@link ISweObjects} instance with only calculated Mars and up to Ketu data.
     */
    ISweObjects buildMarsKetu();

    /**
     * Builds the {@link ISweObjects} instance with only calculated Jupiter and up to Saturn data.
     */
    ISweObjects buildJupiterSaturn();

    /**
     * Builds the {@link ISweObjects} instance with only calculated Uranus and up to Pluto data.
     */
    ISweObjects buildUranusPluto();

    /**
     * Finish building the {@link ISweObjects} instance - calculates all planets.
     */
    ISweObjects completeBuild();

    /**
     * Inits the given {@link ISwissEph} instance with sidereal options and geographic location
     */
    ISwissEph initialization(ISwissEph swissEph);

    /**
     * Completely rebuild the {@link ISweObjects} instance using the given {@link ISwissEph}.<br>
     * Before rebuild the given {@link ISwissEph} instance will be re-initialized
     */
    ISweObjects completeRebuild(ISwissEph swissEph);

    boolean[] retrogrades();

    double[] longitudes();

    double[] latitudes();

    int[] houses();

    int[] signs();

    /**
     * @return the house cusps are returned here in cusp[1...12] for the houses 1 to 12
     */
    double[] cusps();

    /**
     * The parameter ascmc is defined as double[10] and will return the following points:
     * <BLOCKQUOTE><CODE>
     * ascmc[0] = ascendant<BR>
     * ascmc[1] = mc<BR>
     * ascmc[2] = armc (= sidereal time)<BR>
     * ascmc[3] = vertex<BR>
     * ascmc[4] = equatorial ascendant<BR>
     * ascmc[5] = co-ascendant (Walter Koch)<BR>
     * ascmc[6] = co-ascendant (Michael Munkasey)<BR>
     * ascmc[7] = polar ascendant (Michael Munkasey)<BR>
     * ascmc[8] = reserved for future use<BR>
     * ascmc[9] = reserved for future use
     * </CODE></BLOCKQUOTE>
     */
    double[] ascmc();

    double ayanamsa();

    @Override
    ISweObjectsSequence sweSequence();

    default int ascmcCount() {
        return ASCMC_COUNT;
    }

    default int cuspsCount() {
        return CUSPS_COUNT;
    }

    default int objectsCount() {
        return OBJECTS_COUNT;
    }

    static int calculatePlanetHouse(final int ascendantSign, final int planetSign) {
        int planetHouse = planetSign;
        planetHouse += i12;
        planetHouse -= ascendantSign;
        planetHouse %= i12;
        planetHouse += i1;
        return planetHouse;
    }

    /**
     * Sets geographic position and altitude of observer and ayanamsha mode for sidereal planet calculations
     */
    static ISwissEph initSwissEph(ISwissEph swissEph, ISweGeoLocation sweLocation, ISweObjectsOptions sweOptions) {
        if (null == swissEph) return null;

        if (null != sweLocation) {
            swissEph.swe_set_topo(sweLocation.longitude(),
                    sweLocation.latitude(), sweLocation.altitude());
        }

        if (null != sweOptions) {
            swissEph.swe_set_sid_mode(sweOptions.ayanamsa().fid(),
                    sweOptions.initialJulianDay(), sweOptions.initialAyanamsa());
        }

        return swissEph;
    }
}
