/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import swisseph.SweConst;

/**
 * Shared base for the {@code SwephXxxTest} family ported from {@code swe-java-api}'s
 * {@code swisseph.SwephExp} suite. There every call went straight to the native binding
 * ({@code SwephExp.swe_xxx(...)}); here the exact same calls, with the exact same
 * assertions, go through {@link ISwissEph#swe_calc} and friends via
 * {@link AbstractTest#getSwephExp()} - the native-backed {@link SwephNative} instance - so
 * that the interface delegation layer is exercised by the same 106-method coverage rather
 * than trusting it compiles.
 * <p>
 * {@code getSwephExp()}'s constants ({@link SweConst}) are used throughout instead of
 * hand-typed numbers, per this workspace's convention - with one documented exception, see
 * {@code SwephAstroModelsTest}'s {@code SE_MODEL_*} note.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
public abstract class AIswTest extends AbstractTest implements SweConst {
    static final double J2000 = 2451545.0;
    static final double GEOLON = 81 + 8 / 60., GEOLAT = 16 + 10 / 60., GEOALT = 0.;
    static final double DELTA = 1e-9;

    static double[] calc(double tjdET, int planet, int flags) {
        double[] xx = new double[6];
        StringBuilder serr = new StringBuilder();
        int ret = getSwephExp().swe_calc(tjdET, planet, flags, xx, serr);
        if (ret < 0) throw new AssertionError("swe_calc(" + planet + ") failed: " + serr);
        return xx;
    }

    static double normDeg(double x) {
        double d = x % 360.;
        return d < 0 ? d + 360. : d;
    }
}
