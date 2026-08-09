/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */

package org.swisseph.app;

import org.swisseph.ISwissEph;
import org.swisseph.api.ISweStation;
import swisseph.SwissephException;
import swisseph.TCPlanet;
import swisseph.TransitCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static swisseph.SweConst.*;

/**
 * Finds the stations of a planet - the moments it turns <b>retrograde</b> or <b>direct</b>.
 * <p>
 * A station is where the planet's speed in longitude crosses zero, so it is exactly the
 * transit of that speed over the value 0. That is what
 * {@code SEFLG_TRANSIT_LONGITUDE | SEFLG_TRANSIT_SPEED} with an offset of 0 computes, and
 * this class is a thin, typed layer over it: it runs the search, works out which of the two
 * kinds of station it found, and reports the longitude the planet turns at.
 * <p>
 * <b>Which objects have stations.</b> The Sun, the Moon, the mean lunar node and the mean
 * apogee never reverse, so they have none; {@link #hasStations(int)} answers that from the
 * same extreme-speed tables the search itself uses, rather than from a hard-coded list. The
 * <i>true</i> node does have stations, and they come in closely spaced pairs - it turns direct
 * for only a few days at a time.
 * <p>
 * <b>The frame matters.</b> By default the calculation is tropical, which is the conventional
 * definition of a station. Passing {@code SEFLG_SIDEREAL} looks for the zero of the
 * <i>sidereal</i> speed instead, and that is not the same instant: the ayanamsa moves about
 * 3.8e-5 deg/day, and for a slow planet whose speed crawls through zero that offset shifts
 * the station by hours. Set the sidereal mode on the {@link ISwissEph} instance before
 * calculating if you use it.
 * <p>
 * Dates in and out are <b>universal time</b>.
 *
 * <pre>
 * SweStations stations = new SweStations(swissEph);
 * ISweStation next = stations.next(SE_MERCURY, jdUT);
 * if (next.retrograde()) { ... }
 *
 * for (ISweStation s : stations.between(SE_MARS, fromJdUT, toJdUT)) { ... }
 * </pre>
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 * @see swisseph.TCPlanet
 */
public class SweStations {

    /**
     * How far past a found station the next search starts. Stations are never closer together
     * than a few days - even for the true node, whose direct spells are the shortest - so this
     * cannot skip one, while being far larger than the accuracy of the search itself.
     */
    protected static final double SEARCH_STEP = 0.05;

    /** two results closer than this are the same station found twice */
    protected static final double SAME_STATION = 0.01;

    /** offset used to look at the speed just before and just after the station */
    protected static final double SPEED_PROBE = 0.25;

    /** guards {@link #between(int, double, double)} against a non-advancing search */
    protected static final int MAX_ITERATIONS = 10000;

    protected final ISwissEph swissEph;
    protected final int flags;

    /**
     * @param swissEph the engine to calculate with; either implementation will do, but the
     *            native one agrees with {@code swetest} exactly
     */
    public SweStations(ISwissEph swissEph) {
        this(swissEph, SEFLG_SWIEPH);
    }

    /**
     * @param swissEph the engine to calculate with
     * @param flags position flags for {@code swe_calc()} - the ephemeris to use plus any of
     *            {@code SEFLG_SIDEREAL}, {@code SEFLG_TRUEPOS}, {@code SEFLG_TOPOCTR},
     *            {@code SEFLG_EQUATORIAL}, {@code SEFLG_NOABERR}, {@code SEFLG_NOGDEFL}.
     *            The transit flags are added by this class and must not be passed here.
     */
    public SweStations(ISwissEph swissEph, int flags) {
        if (null == swissEph) throw new IllegalArgumentException("ISwissEph cannot be NULL");
        this.swissEph = swissEph;
        this.flags = flags;
    }

    // ------------------------------------------------------------------ queries

    /**
     * Whether the object can reverse direction at all. Derived from the extreme longitudinal
     * speeds Swiss Ephemeris has tabulated: a station exists only if zero lies between them.
     *
     * @param planet Swiss Ephemeris object number
     * @return {@code false} for the Sun, the Moon, the mean node and the mean apogee, and for
     *         anything whose speeds are not available
     */
    public boolean hasStations(int planet) {
        try {
            TransitCalculator tc = newCalculator(planet);
            return tc.getMinOffset() < 0. && tc.getMaxOffset() > 0.;
        } catch (RuntimeException notSupported) {
            return false;
        }
    }

    /**
     * The first station at or after {@code jdUT}.
     *
     * @param planet Swiss Ephemeris object number
     * @param jdUT where to start looking, universal time
     * @return the station, or {@code null} if this object has none
     */
    public ISweStation next(int planet, double jdUT) {
        return find(planet, jdUT, false);
    }

    /**
     * The last station at or before {@code jdUT}.
     *
     * @param planet Swiss Ephemeris object number
     * @param jdUT where to start looking, universal time
     * @return the station, or {@code null} if this object has none
     */
    public ISweStation previous(int planet, double jdUT) {
        return find(planet, jdUT, true);
    }

    /**
     * Every station in a date range, in chronological order.
     *
     * @param planet Swiss Ephemeris object number
     * @param fromJdUT start of the range, universal time, inclusive
     * @param toJdUT end of the range, universal time, inclusive
     * @return the stations found; empty when the object has none or the range holds none
     */
    public List<ISweStation> between(int planet, double fromJdUT, double toJdUT) {
        if (toJdUT < fromJdUT) return between(planet, toJdUT, fromJdUT);
        if (!hasStations(planet)) return Collections.emptyList();

        final TransitCalculator tc = newCalculator(planet);
        final List<ISweStation> found = new ArrayList<>();
        double jd = fromJdUT, previous = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < MAX_ITERATIONS && jd <= toJdUT; i++) {
            final double station;
            try {
                station = TransitCalculator.getTransitUT(tc, jd, false);
            } catch (SwissephException outOfRange) {
                break;
            }
            if (station > toJdUT) break;

            if (station - previous <= SAME_STATION) {
                // the interpolated answer landed a hair before the crossing and the search
                // returned it again - step past it rather than loop
                jd = previous + SAME_STATION + SEARCH_STEP;
                continue;
            }

            found.add(station(planet, station));
            previous = station;
            jd = station + SEARCH_STEP;
        }

        return found;
    }

    // ------------------------------------------------------------------ internals

    protected ISweStation find(int planet, double jdUT, boolean backwards) {
        if (!hasStations(planet)) return null;
        try {
            return station(planet, TransitCalculator.getTransitUT(newCalculator(planet), jdUT, backwards));
        } catch (SwissephException outOfRange) {
            return null;
        }
    }

    /**
     * A calculator for the transit of the longitudinal speed over zero, i.e. for a station.
     */
    protected TransitCalculator newCalculator(int planet) {
        return new TCPlanet(swissEph, planet,
                flags | SEFLG_TRANSIT_LONGITUDE | SEFLG_TRANSIT_SPEED, 0.);
    }

    /**
     * Builds the result: the longitude at the station, and which kind of station it is.
     * <p>
     * The kind follows from the sign of the speed on either side. Sampling both sides rather
     * than reading the speed at the station itself is deliberate - at the station the speed is
     * zero by construction, so its sign carries no information.
     */
    protected ISweStation station(int planet, double jdUT) {
        final double[] before = position(planet, jdUT - SPEED_PROBE);
        final double[] at = position(planet, jdUT);
        final double[] after = position(planet, jdUT + SPEED_PROBE);

        // direct -> retrograde means the speed goes from positive to negative
        final boolean retrograde = after[3] < before[3];

        return new SweStation(planet, swissEph.getJulianDate(jdUT), at[0], retrograde);
    }

    /**
     * {@code swe_calc()} at a universal time, with speed.
     */
    protected double[] position(int planet, double jdUT) {
        final double[] xx = new double[6];
        final StringBuilder serr = new StringBuilder();
        final double jdET = jdUT + swissEph.swe_deltat(jdUT);

        if (swissEph.swe_calc(jdET, planet, flags | SEFLG_SPEED, xx, serr) < 0) {
            throw new SweRuntimeException("swe_calc failed for object " + planet
                    + " at JD " + jdET + ": " + serr);
        }

        return xx;
    }
}
