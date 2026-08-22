/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2019-11
 */

package org.swisseph.api;

import org.swisseph.ISwissEph;
import org.swisseph.app.SweRuntimeException;

import java.io.Serializable;

import static org.swisseph.api.ISweConstants.*;
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
     * What {@link #signs()} and {@link #houses()} hold for an object that has not been built.
     * <p>
     * Both arrays are 1-based - signs and bhavas run 1..12 - so zero is the natural "no answer
     * yet", and {@code houses[id] != 0} is what every {@code buildXxx()} uses as its
     * already-built sentinel. It was never named, and that is how it caused trouble: a caller
     * reading {@code signs()[LG]} on a chart built with {@code buildAscendant = false} gets 0 and,
     * unless it checks, feeds it into arithmetic that produces a <b>plausible wrong answer</b>
     * rather than a failure. {@code Ashtakavarga} hit the loud version of this
     * ({@code signs[LG] - 1} indexed an array at -1); the bhava columns of the upagraha and
     * special-lagna rows hit the silent one, reporting a real-looking bhava computed from a
     * lagna that does not exist.
     *
     * @see #isCalculated(int)
     */
    int NOT_CALCULATED = 0;

    /**
     * Whether the given object id has actually been built on this chart.
     * <p>
     * Read off {@link #signs()} rather than {@link #houses()} because a sign is set for every
     * object a build touches, while a house is not meaningful for a chart built without an
     * ascendant.
     *
     * @param objectId one of {@link #LG}..{@link #PL}
     */
    default boolean isCalculated(final int objectId) {
        if (objectId < LG || objectId > LAST_OBJECT_ID) return false;

        final int sign = signs()[objectId];
        return sign > NOT_CALCULATED && sign <= 12;
    }

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

    /**
     * Throw swe-error during an object calculation or just set it and stop further processing
     */
    void throwSweError(boolean throwSweError);

    StringBuilder sweError();

    boolean throwSweError();

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

    /**
     * @return the house the object falls into, 1 to 12
     * @see #calculatePlanetHousePosition(int)
     */
    default int calculatePlanetHouse(final int objId) {
        return (int) calculatePlanetHousePosition(objId);
    }

    /**
     * The house position of an object as Swiss Ephemeris expresses it: 1.0 exactly on the
     * cusp of the first house, 1.5 in the middle of it, 12.99... just short of the
     * ascendant. The integer part is the house number.
     * <p>
     * Everything except whole sign houses goes through <code>swe_house_pos()</code>, the
     * function Swiss Ephemeris provides for precisely this and the one behind the house
     * position column of <code>swetest -fPj</code>. Two details it needs care with:
     * <ul>
     * <li><b>Frame.</b> <code>swe_house_pos()</code> rebuilds the cusps itself out of the
     * ARMC, so it works in the tropical frame. For a sidereal chart
     * <code>swe_houses_ex()</code> subtracts the ayanamsa from every element of
     * <code>ascmc[]</code> <i>except</i> the ARMC (<code>sidereal_houses_trad()</code>
     * skips index 2 explicitly), so the ARMC handed back is still tropical and the object
     * has to be made tropical too - sidereal longitude plus ayanamsa. Both shift by the
     * same amount, so the resulting house is the same one the sidereal cusps give.</li>
     * <li><b>Latitude.</b> The ecliptic latitude is passed as 0, i.e. the object is
     * projected onto the ecliptic. That is what the cusp comparison this method used to
     * perform did, and what <code>swetest -hpos_meth 1</code> does. Feeding the real
     * latitude instead (swetest's default) is also defensible but is a different
     * convention, and it moves objects that are far off the ecliptic: in the reference
     * chart Pluto sits at 17 deg latitude and its Koch house position goes from 10.48 to
     * 10.06 - one bad day away from landing in another house.</li>
     * </ul>
     * Whole sign houses cannot use <code>swe_house_pos()</code>. They are the one system
     * whose sidereal cusps are not simply the tropical cusps shifted by the ayanamsa:
     * Swiss Ephemeris snaps each cusp down to the start of its sign <i>after</i>
     * subtracting the ayanamsa, which is not invariant under the shift. There the house is
     * just the distance in signs from the ascendant.
     *
     * @return house position in [1, 13)
     */
    default double calculatePlanetHousePosition(final int objId) {
        final ISweHouseSystem houseSystem = sweOptions().houseSystem();

        if (SE_HSYS_WHOLE_SIGN == houseSystem.fid()) {
            final double longitude = longitudes()[objId];
            final int house = ((signs()[objId] - signs()[LG] + i12) % i12) + i1;
            return house + (longitude % d30) / d30;
        }

        // a tropical chart has no ayanamsa to add back
        final double ayanamsa = sweOptions().ayanamsa().sidereal() ? ayanamsa() : d0;

        final double[] xpin = new double[]{longitudes()[objId] + ayanamsa, d0};
        final double house = swissEph().swe_house_pos(ascmc()[SE_ARMC], sweLocation().latitude(),
                trueObliquity(), houseSystem.fid(), xpin, sweError());

        if (house < d1) {
            throw new SweRuntimeException("Cannot determine the house of object " + objId
                    + " in house system " + houseSystem.code() + ": " + sweError());
        }

        return house;
    }

    /**
     * The true obliquity of the ecliptic of date, in degrees - mean obliquity plus the
     * nutation in obliquity. This is what <code>swe_houses_ex()</code> builds the cusps
     * with, unconditionally, so it is what <code>swe_house_pos()</code> has to be given.
     * Passing the mean obliquity instead shifts a house position by about 1e-5 of a house.
     * <p>
     * Only the ephemeris selection is taken from the options. <code>SEFLG_SIDEREAL</code>
     * and <code>SEFLG_NONUT</code> must not be passed on: nutation is cached per date
     * inside Swiss Ephemeris, and any call made with <code>SEFLG_NONUT</code> - which
     * <code>swe_get_ayanamsa_ex()</code> sets internally - leaves that cache zeroed, so
     * <code>SE_ECL_NUT</code> would then hand back the mean obliquity in xx[0].
     *
     * @return true obliquity of date, in degrees
     */
    default double trueObliquity() {
        final double[] xx = new double[6];
        final int result = swissEph().swe_calc(sweJulianDate().epheTime(),
                SE_ECL_NUT, sweOptions().mainFlags() & SEFLG_EPHMASK, xx, sweError());

        if (ERR == result) {
            throw new SweRuntimeException("Cannot obtain the obliquity: " + sweError());
        }

        return xx[0];
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
            ISweAyanamsa ayanamsa = sweOptions.ayanamsa();
            if (ayanamsa != null && ayanamsa.sidereal()) {
                swissEph.swe_set_sid_mode(ayanamsa.fid(), sweOptions
                        .initialJulianDay(), sweOptions.initialAyanamsa());
            }
        }

        return swissEph;
    }
}
