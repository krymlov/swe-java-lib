/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-11
 */
package org.swisseph;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.swisseph.api.ISweConstants.EPHE_PATH;
import static swisseph.SweConst.*;

/**
 * The Moshier ephemeris in the pure-Java engine ({@code swisseph.SwissEph}), restored on
 * 2026-11-23 from {@code swemplan.c} / {@code sweph.c} of 2.10.03.
 * <p>
 * Why it matters: the library often runs in apps and APKs that ship only {@code sepl_18.se1}
 * and {@code semo_18.se1} (1800-2400). Outside that range the C library falls back to Moshier
 * silently - a valid return with {@link swisseph.SweConst#SEFLG_MOSEPH} set and a warning in
 * {@code serr} - and the pure-Java engine must do exactly the same, not return {@code ERR}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
public class SwephMoshierFallbackTest extends AbstractTest {

    /** -3000-ish, 0 CE, 1000 CE, 1500, 1700, J2000, 2500, 2800, 3000 CE - all inside Moshier's range */
    static final double[] DATES = {
            700000.5, 1721423.5, 2086307.5, 2268923.5, 2341972.5,
            2451545.0, 2634166.5, 2743737.5, 2816787.5,
    };

    static final int[] BODIES = {
            SE_SUN, SE_MOON, SE_MERCURY, SE_VENUS, SE_MARS, SE_JUPITER, SE_SATURN,
            SE_URANUS, SE_NEPTUNE, SE_PLUTO, SE_MEAN_NODE, SE_TRUE_NODE, SE_MEAN_APOG,
            SE_OSCU_APOG, SE_EARTH,
    };

    /** Moshier: the same series evaluated by C and by Java, a few dozen nano-degrees apart */
    static final double POS_TOL = 1e-7, SPEED_TOL = 1e-6;

    /**
     * Topocentric positions: delta t still differs by ~0.03 s after 2400 even with the tidal
     * acceleration pinned, which moves the Moon's parallax by ~1.3e-6 deg - with the files too.
     */
    static final double TOPO_POS_TOL = 5e-6;

    private static void assertSame(String what, double[] c, double[] j) {
        assertSame(what, c, j, POS_TOL, SPEED_TOL);
    }

    /** a NaN speed tolerance leaves the speeds out */
    private static void assertSame(String what, double[] c, double[] j, double posTol, double speedTol) {
        for (int k = 0; k < 6; k++) {
            if (k >= 3 && Double.isNaN(speedTol)) break;
            double d = Math.abs(c[k] - j[k]);
            if (k == 0 && d > 180) d = 360 - d;
            assertTrue(d <= (k < 3 ? posTol : speedTol),
                    what + " [" + k + "] native " + c[k] + " vs Java " + j[k] + " (diff " + d + ")");
        }
    }

    private static void parity(String label, int iflag) {
        parity(label, iflag, SPEED_TOL);
    }

    private static void parity(String label, int iflag, double speedTol) {
        parity(label, iflag, POS_TOL, speedTol);
    }

    private static void parity(String label, int iflag, double posTol, double speedTol) {
        ISwissEph cn = getSwephExp(), jv = getSwissEph();
        boolean sidereal = (iflag & SEFLG_SIDEREAL) != 0;
        for (double tjd : DATES) {
            for (int ipl : BODIES) {
                double[] xc = new double[6], xj = new double[6];
                StringBuilder ec = new StringBuilder(), ej = new StringBuilder();
                int rc = cn.swe_calc(tjd, ipl, iflag, xc, ec);
                int rj = jv.swe_calc(tjd, ipl, iflag, xj, ej);
                String what = label + " ipl=" + ipl + " jd=" + tjd;
                assertEquals(rc, rj, what + " flags; native '" + ec + "', Java '" + ej + "'");
                if (rc < 0) continue;
                if (sidereal && ipl == SE_EARTH) continue; // the geocentric Earth: degenerate
                // sidereal longitudes compared directly since the ayanamsa port of 2026-11-23
                // (Java's Lahiri had been 3.6e-5 deg off; the ayanamsa was added back here)
                assertSame(what, xc, xj, posTol, speedTol);
            }
        }
    }

    @Test
    void explicitMoseph_geocentric_matchesNative() {
        parity("MOSEPH", SEFLG_MOSEPH | SEFLG_SPEED);
    }

    @Test
    void explicitMoseph_equatorialTruePosNoNut_matchesNative() {
        parity("MOSEPH|EQU|TRUEPOS|NONUT",
                SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_EQUATORIAL | SEFLG_TRUEPOS | SEFLG_NONUT);
    }

    @Test
    void explicitMoseph_heliocentricAndTopocentric_matchNative() {
        parity("MOSEPH|HELCTR", SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_HELCTR);
        try {
            // The topocentre needs sidereal time, so delta t: pin the Moshier tidal
            // acceleration on both engines, as SweObjects does (delta t is not reproducible
            // otherwise - see swe-java-lib.md, "Delta t is pinned").
            getSwephExp().swe_set_tid_acc(SE_TIDAL_MOSEPH);
            getSwissEph().swe_set_tid_acc(SE_TIDAL_MOSEPH);
            getSwephExp().swe_set_topo(30.52, 50.45, 180);
            getSwissEph().swe_set_topo(30.52, 50.45, 180);
            // Speeds left out: the topocentric speed differs between the engines by up to
            // 3.3e-4 deg/day with the files as well (1.8e-4 for the Sun at J2000) - older
            // than this port and not about Moshier.
            parity("MOSEPH|TOPOCTR", SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_TOPOCTR, TOPO_POS_TOL, Double.NaN);
        } finally {
            getSwephExp().swe_set_topo(0, 0, 0);
            getSwissEph().swe_set_topo(0, 0, 0);
            getSwephExp().swe_set_tid_acc(SE_TIDAL_AUTOMATIC);
            getSwissEph().swe_set_tid_acc(SE_TIDAL_AUTOMATIC);
        }
    }

    @Test
    void explicitMoseph_siderealLahiri_matchesNative() {
        try {
            getSwephExp().swe_set_sid_mode(SE_SIDM_LAHIRI, 0, 0);
            getSwissEph().swe_set_sid_mode(SE_SIDM_LAHIRI, 0, 0);
            parity("MOSEPH|SIDEREAL", SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL);
        } finally {
            getSwephExp().swe_set_sid_mode(SE_SIDM_FAGAN_BRADLEY, 0, 0);
            getSwissEph().swe_set_sid_mode(SE_SIDM_FAGAN_BRADLEY, 0, 0);
        }
    }

    /** relative on purpose: the pure-Java engine splits the path on ':' as well as ';' */
    private static String epheDir(String name, String... files) throws IOException {
        Path dir = Files.createDirectories(Paths.get("target", name));
        for (String f : files)
            Files.copy(Paths.get(EPHE_PATH, f), dir.resolve(f), StandardCopyOption.REPLACE_EXISTING);
        return dir.toString();
    }

    /**
     * Fallback with the given ephemeris directory, on both engines: same return flags, same
     * numbers, never ERR inside Moshier's range, and a warning that names the fallback.
     */
    private static void fallback(String dir, boolean expectMoshierEverywhere) {
        ISwissEph cn = getSwephExp(), jv = getSwissEph();
        try {
            cn.swe_set_ephe_path(dir);
            jv.swe_set_ephe_path(dir);
            for (double tjd : DATES) {
                boolean inFiles = !expectMoshierEverywhere && tjd > 2378496.5 && tjd < 2597641.5;
                for (int ipl : BODIES) {
                    if (ipl == SE_EARTH) continue;
                    double[] xc = new double[6], xj = new double[6];
                    StringBuilder ec = new StringBuilder(), ej = new StringBuilder();
                    int iflag = SEFLG_SWIEPH | SEFLG_SPEED;
                    int rc = cn.swe_calc(tjd, ipl, iflag, xc, ec);
                    int rj = jv.swe_calc(tjd, ipl, iflag, xj, ej);
                    String what = dir + " ipl=" + ipl + " jd=" + tjd;
                    assertTrue(rj >= 0, what + ": Java returned ERR: " + ej);
                    assertEquals(rc, rj, what + " flags; native '" + ec + "', Java '" + ej + "'");
                    boolean moshier = (rj & SEFLG_MOSEPH) != 0;
                    // File-based numbers are not compared: the engines differed there before
                    // this port (the true node by up to 1.3e-4 deg, the Moon's latitude by
                    // 8e-6 at J2000), which is the business of the file-based suites.
                    if (moshier) assertSame(what, xc, xj);
                    if (ipl == SE_MEAN_NODE || ipl == SE_MEAN_APOG) continue; // analytical, no file
                    assertEquals(!inFiles, moshier, what + " Moshier flag, serr '" + ej + "'");
                    if (moshier)
                        assertTrue(ej.toString().contains("Moshier"), what + ": warning '" + ej + "'");
                }
            }
        } finally {
            cn.swe_set_ephe_path(EPHE_PATH);
            jv.swe_set_ephe_path(EPHE_PATH);
        }
    }

    @Test
    void noEphemerisFiles_bothEnginesFallBackToMoshierSilently() throws IOException {
        fallback(epheDir("empty-ephe"), true);
    }

    /** the APK case: only sepl_18.se1 and semo_18.se1 */
    @Test
    void onlyThe18Files_outside1800to2400_fallsBackToMoshier() throws IOException {
        fallback(epheDir("apk-ephe", "sepl_18.se1", "semo_18.se1"), false);
    }

    /**
     * Two engines, two threads, the Moshier moon at the same time. Swemmoon kept 15 working
     * variables in static fields, so the threads overwrote each other's terms - seen as a NaN
     * true node in a Kundali built next to another one (2026-11-23).
     */
    @Test
    void theMoshierMoonIsSafeOnTwoEnginesInTwoThreads() throws Exception {
        final int flags = SEFLG_MOSEPH | SEFLG_SPEED;
        final double[][] expected = new double[DATES.length][6];
        try (ISwissEph sw = new swisseph.SwissEph(EPHE_PATH)) {
            for (int d = 0; d < DATES.length; d++)
                sw.swe_calc(DATES[d], SE_TRUE_NODE, flags, expected[d], new StringBuilder());
        }
        ExecutorService pool = Executors.newFixedThreadPool(4);
        try {
            List<Future<String>> runs = new ArrayList<>();
            for (int t = 0; t < 4; t++) {
                runs.add(pool.submit(() -> {
                    try (ISwissEph sw = new swisseph.SwissEph(EPHE_PATH)) {
                        for (int n = 0; n < 200; n++) {
                            for (int d = 0; d < DATES.length; d++) {
                                double[] x = new double[6];
                                sw.swe_calc(DATES[d], SE_TRUE_NODE, flags, x, new StringBuilder());
                                for (int k = 0; k < 6; k++)
                                    if (Double.doubleToLongBits(x[k]) != Double.doubleToLongBits(expected[d][k]))
                                        return "jd " + DATES[d] + " [" + k + "] " + x[k] + " vs " + expected[d][k];
                            }
                        }
                    }
                    return null;
                }));
            }
            for (Future<String> run : runs) {
                String failure = run.get();
                assertTrue(failure == null, failure);
            }
        } finally {
            pool.shutdownNow();
        }
    }
}
