/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-11
 */
package org.swisseph;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import swisseph.SweConst;
import swisseph.SwissEph;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static swisseph.SweConst.*;

/**
 * Heliacal events of every arcus visionis kind and {@code swe_vis_limit_mag} angles, split off from {@link HeliacalParityTest}.
 * The cases, tolerances and helpers are in {@link HeliacalParityTestBase}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
public class HeliacalArcusParityTest extends HeliacalParityTestBase {
    @Test
    void events_ofTheArcusVisionisKinds_matchNative() {
        final int[] flags = new int[AV_KINDS.length];
        for (int i = 0; i < flags.length; i++) flags[i] = SEFLG_SWIEPH | AV_KINDS[i];
        events(new String[]{"venus", "mercury", "mars", "jupiter", "sirius", "regulus"}, new int[]{1, 2, 3, 4, 5, 6}, flags, 5);
    }

    @Test
    void angles_matchNative() {
        prime();
        for (int fl : FLAGS) {
            for (double t0 : DATES) {
                for (int g = 0; g < PLACES.length; g++) {
                    final double[] geo = PLACES[g];
                    final double altMoon = 0 == g % 2 ? -10 : 35, altObj = 5 + g * 3;
                    for (double mag : new double[]{-4, 1, 6}) {
                        for (int az = 0; az < 360; az += 110) {
                            final double t = t0 + az / 100., azi = az;
                            final double[] air = AIRS[(g + az / 110) % AIRS.length], obs = OBSERVERS[(g + az / 110) % OBSERVERS.length];
                            final String what = " fl " + fl + " jd " + t + " geo " + g + " mag " + mag + " az " + az;
                            compare("heliacal_angle" + what, air, obs, "", true, TOL, (e, a, o, n, r, s) ->
                                    e.swe_heliacal_angle(t, geo, a, o, fl, mag, azi, azi + 10, 120, altMoon, r, s));
                            // a null serr is dereferenced by SunRA() in C; the bridge has one of its own
                            compare("topo_arcus_visionis" + what, air, obs, "", 110 != az, TOL, (e, a, o, n, r, s) ->
                                    e.swe_topo_arcus_visionis(t, geo, a, o, fl, mag, azi, altObj, azi + 10, 120, altMoon, r, s));
                        }
                    }
                }
            }
        }
    }
}
