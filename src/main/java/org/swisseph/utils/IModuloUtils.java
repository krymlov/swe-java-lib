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
        double rem = d % mod;
        if (Math.abs(rem) < MODULO_TOLERANCE) return d0;
        if (rem < d0) rem += mod;
        return rem < mod ? rem : d0;
    }

}
