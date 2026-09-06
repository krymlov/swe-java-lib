/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2019-09
 */

package org.swisseph.utils;


import static org.swisseph.api.ISweConstants.*;

/**
 * @author Yura Krymlov
 * @version 1.1, 2019-10
 */
public interface IModuloUtils {

    /**
     * Remainders closer than this to a whole multiple of the modulus are treated as 0.
     * Same value Swiss Ephemeris uses in swe_degnorm()/swe_radnorm().
     */
    double MODULO_TOLERANCE = 1e-13;

    static int fix360(final int n) {
        return modulo(i360, n);
    }

    /**
     * Reduces a given int value modulo mod.
     * @return a value in [0, mod)
     */
    static int modulo(final int mod, final int n) {
        final int rem = n % mod;
        return rem < i0 ? rem + mod : rem;
    }

    /**
     * Reduces a given double value modulo 360.
     * @return a value in [0, 360) - 360. is never returned
     */
    static double fix360(final double d) {
        return modulo(d360, d);
    }

    /**
     * Reduces a given double value modulo 30.
     * <p>
     * This is plain arithmetic and stays that way: it is also used to ask <i>how close</i> a
     * graha is to a sign boundary, which is symmetric and must not snap. For the degree of a
     * graha <i>within</i> its rasi - the value rendered beside a snapped sign - use
     * {@link #segmentDegree(double, double)}, or {@code IRasi.rasiDegree} which is that.
     *
     * @return a value in [0, 30) - 30. is never returned
     */
    static double fix30(final double d) {
        return modulo(d30, d);
    }

    /**
     * Reduces a given double value d modulo the double mod.
     * <p>
     * A remainder within {@link #MODULO_TOLERANCE} of a whole multiple of mod is
     * snapped to 0, exactly as Swiss Ephemeris does in swe_degnorm() ("Alois fix
     * 11-dec-1999"). Without it an input such as -1e-15 - which swe_calc can
     * legitimately hand back for a longitude - comes back as exactly mod instead of
     * 0: longitude 360. instead of 0., i.e. rasi index 12 instead of 0.
     * <p>
     * The tolerance is absolute rather than scaled to mod, because the rounding
     * noise it absorbs comes from the input (a longitude in degrees), not from the
     * modulus - reducing the same value mod 30 has to forgive the same slop as
     * reducing it mod 360.
     *
     * @return a value in [0, mod) - mod is never returned
     */
    static double modulo(final double mod, final double d) {
        // NaN has to survive. Every comparison against it is false, so without this guard the
        // closing ternary took its else branch and turned "undetermined" into a perfectly
        // in-range 0 - which downstream reads as 0 degrees, i.e. the first rasi, the first
        // naksatra, the first tithi. A caller that marks a quantity as unknown by writing NaN
        // (real consumers do, for a date with no time) got Aries instead of an empty answer.
        if (Double.isNaN(d)) return d;

        double rem = d % mod;
        if (Math.abs(rem) < MODULO_TOLERANCE) return d0;
        if (rem < d0) rem += mod;
        return rem < mod ? rem : d0;
    }

    /**
     * A longitude this close below a segment boundary is treated as being <b>on</b> it.
     * <p>
     * Chosen from measurement, not taste. A transit search returns the moment a graha reaches a
     * boundary; recomputing the longitude for that moment lands a little either side of it, by
     * the search's own convergence. Over 600 ingresses (10 grahas x rasi, naksatra and pada
     * boundaries) the worst residual was <b>3.2e-9 deg</b> and <b>47%</b> of them fell on the low
     * side - a coin flip. This tolerance is 30x that worst residual, and still below Swiss
     * Ephemeris' own accuracy of about 1e-3 arcsec (2.8e-7 deg): the snap can never move a graha
     * across a boundary by more than the ephemeris is uncertain about anyway.
     * <p>
     * It is deliberately much larger than {@link #MODULO_TOLERANCE}, which absorbs the last ULP
     * of a value that is already exact; this one absorbs the convergence of an iterative search.
     */
    double SEGMENT_TOLERANCE = 1e-7;

    /**
     * Which segment of the given length a longitude falls in, counted from 0.
     * <p>
     * <b>Why this is not {@code (int) (fix360(longitude) / length)}.</b> A sign boundary is a
     * multiple of 30, a naksatra boundary a multiple of 13&deg;20', and a transit search exists
     * precisely to find the moment a graha is <i>on</i> one. Recomputing the longitude for that
     * moment gives back the boundary to about 3e-9 of a degree - on either side, unpredictably.
     * A bare floor turns that coin flip into a whole sign: a chart built on the reported ingress
     * moment showed the graha at 29&deg;59'59.99" of the sign it was leaving about half the time.
     * <p>
     * Snapping up when the value is within {@link #SEGMENT_TOLERANCE} of the next boundary makes
     * the answer agree with the search that produced the moment. NaN survives, as it does through
     * {@link #modulo(double, double)}: an undetermined longitude must not become segment 0.
     *
     * @param length the segment width in degrees - 30 for a rasi, 13&deg;20' for a naksatra, ...
     * @param longitude a longitude in degrees, not necessarily normalised
     * @return the 0-based segment index, or -1 for NaN
     */
    static int segment(final double length, final double longitude) {
        final double snapped = snapToSegment(length, longitude);
        if (Double.isNaN(snapped)) return -1;
        return (int) (snapped / length);
    }

    /**
     * The position within its own segment, measured from the same snapped value
     * {@link #segment(double, double)} takes the index from.
     * <p>
     * The two are the integer and the fractional part of one answer and have to agree. Plain
     * {@code modulo(length, longitude)} is the half that does not snap, and at an ingress it
     * reported the graha at 29&deg;59'59.99" <i>of the sign the index had already moved on
     * from</i> - a position that does not exist. Every "how far through" reading in the jyotisa
     * layer - the degree in the rasi, the progress through a naksatra, tithi, karana or nitya
     * yoga - is this quantity.
     *
     * @return NaN if the input is NaN, otherwise a value in [0, length)
     */
    static double segmentDegree(final double length, final double longitude) {
        return modulo(length, snapToSegment(length, longitude));
    }

    /**
     * The same longitude, moved up to the next segment boundary when it is only a rounding
     * artefact short of it, and normalised to [0, 360).
     * <p>
     * Use this where the boundary is compared against directly rather than divided by - the
     * karana lookup, for instance, tests four fixed ranges before dividing. Snapping there keeps
     * every branch seeing the value the transit search was aiming at.
     *
     * @return NaN if the input is NaN, otherwise a value in [0, 360)
     */
    static double snapToSegment(final double length, final double longitude) {
        final double norm = modulo(d360, longitude);
        if (Double.isNaN(norm)) return norm;

        final double rem = norm % length;
        // The wrap through modulo() matters: a longitude like 359.99999995 snaps to 360 and has
        // to come back as 0, not as a segment index one past the last.
        return (rem != d0 && (length - rem) <= SEGMENT_TOLERANCE)
                ? modulo(d360, norm + (length - rem)) : norm;
    }

}
