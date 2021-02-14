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

    static int fix360(final int n) {
        return modulo(i360, n);
    }

    static int modulo(final int mod, final int n) {
        if (n < i0) return (n % mod) + mod;
        if (n >= mod) return n % mod;
        return n;
    }

    /**
     * Reduces a given double value modulo 360.
     * @return a value between 0 and 360.
     */
    static double fix360(final double d) {
        return modulo(d360, d);
    }

    /**
     * Reduces mod given double value d modulo the double mod
     * @return value between 0 and mod.
     */
    static double modulo(final double mod, final double d) {
        return (d - Math.floor(d / mod) * mod);
    }

}
