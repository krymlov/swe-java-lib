/*
   This is an utility class based on port of the Swiss Ephemeris Free Edition

   For any questions or comments regarding this port to Java,
   you should ONLY contact me and not Astrodienst, 
   as the company is not involved in this port.

   Thomas Mack, mack@ifis.cs.tu-bs.de, 2001
*/

package org.swisseph.app;

import org.apache.commons.lang3.NotImplementedException;
import org.swisseph.ISwissEph;
import org.swisseph.api.ISweGeoLocation;
import org.swisseph.api.ISweJulianDate;
import org.swisseph.api.ISweObjects;
import org.swisseph.api.ISweObjectsOptions;

import java.util.Arrays;

import static java.lang.Double.isNaN;
import static org.swisseph.api.ISweConstants.*;
import static org.swisseph.api.ISweJulianDate.IDXD_DELTAT;
import static org.swisseph.api.ISweJulianDate.IDXD_ETIME;
import static swisseph.SweConst.*;

/**
 * <a href="http://www.th-mack.de/download/jyotish-0.21a-bin.zip">...</a>
 *
 * @author Thomas Mack (December 14, 2002)
 * @version 0.21a (adjusted by Yura Krymlov in 2019-10)
 */
public class SweObjects implements ISweObjects {
    private static final long serialVersionUID = -6853364529953498713L;

    protected final boolean[] retrogrades = new boolean[objectsCount()];
    protected final double[] longitudes = new double[objectsCount()];
    protected final double[] latitudes = new double[objectsCount()];
    protected final int[] houses = new int[objectsCount()];
    protected final int[] signs = new int[objectsCount()];

    // The special points like ascendant etc. are here
    protected final double[] ascmc = new double[ascmcCount()];

    // The house cusps are returned here in cusp[1...12]
    protected final double[] cusps = new double[cuspsCount()];

    protected final ISweObjectsOptions options;
    protected final ISweJulianDate julianDate;
    protected final ISweGeoLocation location;

    protected double ayanamsa = Double.NaN;

    // ------------------------------------------------------------------------

    protected transient SweObjectsSequence sequence;
    protected transient ISwissEph swissEph;

    // ------------------------------------------------------------------------

    public SweObjects(ISwissEph swissEph, ISweJulianDate sweJulianDate, ISweObjects sweObjects) {
        this(swissEph, sweJulianDate, sweObjects.sweLocation(), sweObjects.sweOptions());
    }

    public SweObjects(ISwissEph swissEph, ISweJulianDate sweJulianDate,
                      ISweGeoLocation sweLocation, ISweObjectsOptions sweOptions) {
        this(swissEph, sweJulianDate, sweLocation, sweOptions, true);
    }

    public SweObjects(ISwissEph swissEph, ISweJulianDate sweJulianDate, ISweGeoLocation sweLocation,
                      ISweObjectsOptions sweOptions, boolean buildAscendant) {
        this.location = sweLocation;
        this.options = sweOptions;

        // init swiss-ephemeris
        this.swissEph = initialization(swissEph);

        // and julian date object
        this.julianDate = initJulianDate(sweJulianDate);

        initJulianDateDeltaT();
        initEphemerisTime();

        // pre-calc ascendant 
        if (buildAscendant) {
            buildAscendant();
        }
    }

    @Override
    public ISwissEph initialization(ISwissEph swissEph) {
        if (null == swissEph) throw new SweRuntimeException("ISwissEph is mandatory parameter");
        return ISweObjects.initSwissEph(swissEph, location, options);
    }

    protected ISweJulianDate initJulianDate(ISweJulianDate sweJulianDate) {
        return this.swissEph.initJulianDate(sweJulianDate);
    }

    protected void initJulianDateDeltaT() {
        julianDate.values()[IDXD_DELTAT] = swissEph.swe_deltat_ex
                (julianDate.julianDay(), options.mainFlags(), null);
    }

    protected void initEphemerisTime() {
        julianDate.values()[IDXD_ETIME] = julianDate.julianDay() + julianDate.deltaT() ;
    }

    @Override
    public double ayanamsa() {
        // we need to calc ayanamsa on demand 
        // because it is slow operation

        if (!isNaN(ayanamsa) || !options.ayanamsa().sidereal()) {
            return ayanamsa;
        }

        // To calculate true ayanamsha using swetest, one should just write:
        // > swetest -b1.1.2000 -pb -sid27 -fPL (do NOT write -true)
        // > For mean ayanamsha
        // > swetest -b1.1.2000 -pb -sid27 -fPL -nonut

        // > Maybe we should omit the statement about the confusions. However, confusion can arise if you use the ayanamsha
        // functions with an unfortunate iflag. E.g., see the following values for True Chitra ayanamsha:
        // > [dieter@as80 uds]$ swetest -b1.1.2000 -pb -sid27 -fPL -head
        // > Ayanamsha 23°50' 9.8871
        // > [dieter@as80 uds]$ swetest -b1.1.2000 -pb -sid27 -fPL -head -true
        // > Ayanamsha 23°50'14.9061
        // > [dieter@as80 uds]$ swetest -b1.1.2000 -pb -sid27 -fPL -head -noaberr
        // > Ayanamsha 23°50'14.9008
        // > [dieter@as80 uds]$ swetest -b1.1.2000 -pb -sid27 -fPL -head -nonut
        // > Ayanamsha 23°50'23.8182
        // > [dieter@as80 uds]$ swetest -b1.1.2000 -pb -sid27 -fPL -head -true -nonut
        // > Ayanamsha 23°50'28.8371
        // > [dieter@as80 uds]$ swetest -b1.1.2000 -pb -sid27 -fPL -head -noaberr -nonut
        // > Ayanamsha 23°50'28.8318
        // >
        // > This is not documented. But I'm not sure we should do so because it may create confusion.

        try {
            final double[] daya = new double[]{0};
            final StringBuilder serr = new StringBuilder(0);
            final int result = swissEph.swe_get_ayanamsa_ex(julianDate.epheTime(), options.mainFlags(), daya, serr);
            if (result != ERR) return this.ayanamsa = daya[0];
        } catch (NotImplementedException nie) {
            // ignore
        }

        // calculates the ayanamsa for a given date.
        return this.ayanamsa = swissEph.swe_get_ayanamsa(julianDate.epheTime());
    }

    @Override
    public ISweObjects buildAscendant() {
        if (0 != houses[LG]) return this;

        int result = swissEph.swe_houses_ex(julianDate.julianDay(), options.houseFlags(),
                location.latitude(), location.longitude(), options.houseSystem().fid(), cusps, ascmc);

        if (result == ERR) throw new SweRuntimeException(CALC_FAILED);

        houses[LG] = i1;
        longitudes[LG] = ascmc[LG];
        signs[LG] = (int) (ascmc[LG] / d30) + i1;

        return this;
    }

    protected ISweObjects buildObject(final int objId, final StringBuilder serr) {
        if (i0 != houses[objId]) return this;
        if (objId == KE) return buildLunarNodes();

        final double[] dres = new double[6];
        final int result = swissEph.swe_calc(julianDate.epheTime(),
                getSupportedObjects()[objId], options.calcFlags(), dres, serr);

        if (result == ERR) throw new SweRuntimeException(serr.toString());

        latitudes[objId] = dres[1];
        longitudes[objId] = dres[0];
        retrogrades[objId] = dres[3] < d0;
        signs[objId] = (int) (dres[0] / d30) + i1;

        if (i0 != signs[LG]) {
            houses[objId] = calculatePlanetHouse(objId);
        }

        return this;
    }

    @Override
    public ISweObjects buildObject(final int objectId) {
        return buildObject(objectId, new StringBuilder(0));
    }

    @Override
    public ISweObjects buildLunarNodes() {
        if (i0 != houses[KE]) return this;

        buildObject(RA, new StringBuilder(0));

        latitudes[KE] = latitudes[RA];
        retrogrades[KE] = retrogrades[RA];
        longitudes[KE] = (longitudes[RA] + d180) % d360;

        signs[KE] = (int) (longitudes[KE] / d30) + i1;
        houses[KE] = calculatePlanetHouse(KE);

        return this;
    }

    @Override
    public ISweObjects buildSunMoon() {
        if (i0 != houses[CH]) return this;

        final StringBuilder serr = new StringBuilder(0);
        buildObject(SY, serr);
        buildObject(CH, serr);
        return this;
    }

    @Override
    public ISweObjects buildMarsKetu() {
        if (i0 != houses[KE]) return this;
        final StringBuilder serr = new StringBuilder(0);
        for (int objId = MA; objId < RA; objId++) buildObject(objId, serr);
        return buildLunarNodes();
    }

    @Override
    public ISweObjects buildJupiterSaturn() {
        if (i0 != houses[SA]) return this;
        final StringBuilder serr = new StringBuilder(0);
        for (int objId = GU; objId <= SA; objId++) buildObject(objId, serr);
        return this;
    }

    @Override
    public ISweObjects buildUranusPluto() {
        if (i0 != houses[PL]) return this;
        final StringBuilder serr = new StringBuilder(0);
        for (int objId = UR; objId < OBJECTS_COUNT; objId++) buildObject(objId,  serr);
        return this;
    }

    @Override
    public SweObjects completeBuild() {
        if (i0 == houses[LG]) buildAscendant();
        if (i0 == houses[CH]) buildSunMoon();
        if (i0 == houses[KE]) buildMarsKetu();
        if (i0 == houses[PL]) buildUranusPluto();

        return this;
    }

    public SweObjects completeRebuild(ISwissEph swissEph) {
        if (null != this.sequence) this.sequence.sorted = false;

        this.ayanamsa = Double.NaN;
        Arrays.fill(this.houses, i0);

        this.swissEph = initialization(swissEph);

        initJulianDateDeltaT();
        initEphemerisTime();
        completeBuild();

        return this;
    }

    protected int[] getSupportedObjects() {
        return options.trueNode() ? OBJECTS_TRUE_NODE : OBJECTS_MEAN_NODE;
    }

    @Override
    public ISwissEph swissEph() {
        return swissEph;
    }

    @Override
    public ISweObjectsOptions sweOptions() {
        return options;
    }

    @Override
    public ISweJulianDate sweJulianDate() {
        return julianDate;
    }

    @Override
    public ISweGeoLocation sweLocation() {
        return location;
    }

    @Override
    public SweObjectsSequence sweSequence() {
        if (null == sequence) sequence = new SweObjectsSequence(longitudes);
        if (!sequence.sorted()) this.sequence.sort();
        return this.sequence;
    }


    @Override
    public boolean[] retrogrades() {
        return retrogrades;
    }

    @Override
    public double[] longitudes() {
        return longitudes;
    }

    @Override
    public double[] latitudes() {
        return latitudes;
    }

    @Override
    public int[] houses() {
        return houses;
    }

    @Override
    public int[] signs() {
        return signs;
    }

    /**
     * @return the house cusps are returned here in cusp[1...12] for the houses 1 to 12
     */
    @Override
    public double[] cusps() {
        return cusps;
    }

    /**
     * The parameter ascmc is defined as double[10] and will return the following points:
     * <BLOCKQUOTE><CODE>
     * ascmc[0] = ascendant<BR>
     * ascmc[1] = mc<BR>
     * ascmc[2] = sidereal time = armc (in angle units, not in time units) at the Greenwich meridian.
     * To get local sidereal time, add geographic longitude to the result. <BR>
     * ascmc[3] = vertex<BR>
     * ascmc[4] = equatorial ascendant<BR>
     * ascmc[5] = co-ascendant (Walter Koch)<BR>
     * ascmc[6] = co-ascendant (Michael Munkasey)<BR>
     * ascmc[7] = polar ascendant (Michael Munkasey)<BR>
     * ascmc[8] = reserved for future use<BR>
     * ascmc[9] = reserved for future use
     * </CODE></BLOCKQUOTE>
     */
    @Override
    public double[] ascmc() {
        return ascmc;
    }
}