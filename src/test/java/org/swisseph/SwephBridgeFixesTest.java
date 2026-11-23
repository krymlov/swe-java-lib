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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.swisseph.api.ISweConstants.EPHE_PATH;
import static swisseph.SweConst.*;

/**
 * The three JNI bridge defects fixed on 2026-11-23, pinned on both engines where the pure-Java
 * port can do the same thing (it falls back to Moshier too since the same day - see the last test):
 * <ol>
 *   <li>{@code swe_set_jpl_file(null)} took the VM down (strlen(NULL) in C) - now
 *       {@link NullPointerException} on both;</li>
 *   <li>a warning with a non-negative return code (the fallback to Moshier) never reached
 *       {@code serr} through the bridge, which appended only when {@code retc <= ERR};</li>
 *   <li>{@code swe_get_denum} was not wrapped.</li>
 * </ol>
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
public class SwephBridgeFixesTest extends AbstractTest {

    private static final double J2000 = 2451545.;

    /**
     * The value is whatever the open file's header says: the sepl_*.se1 files of swisseph-2026
     * read "based on JPL Ephemeris DE441" (2026-05-26), so it is 441 today. SE_DE_NUMBER (431)
     * is only the answer while no planetary file has been opened.
     */
    @Test
    void swe_get_denum_isTheSameOnBothEnginesAndMatchesTheFileHeader() {
        int[] seen = new int[2];
        ISwissEph[] engines = {getSwephExp(), getSwissEph()};
        for (int i = 0; i < engines.length; i++) {
            ISwissEph sw = engines[i];
            double[] xx = new double[6];
            StringBuilder serr = new StringBuilder();
            int ret = sw.swe_calc(J2000, SE_MARS, SEFLG_SWIEPH, xx, serr);
            assertTrue(ret >= 0 && (ret & SEFLG_SWIEPH) != 0, sw.getClass() + ": " + serr);

            int[] denum = new int[1];
            sw.swe_get_current_file_data(0, new double[1], new double[1], denum);
            assertTrue(denum[0] > 0, sw.getClass() + ": no DE number in the header");
            assertEquals(denum[0], sw.swe_get_denum(), sw.getClass().getName());
            seen[i] = sw.swe_get_denum();
        }
        assertEquals(seen[0], seen[1], "swe_get_denum, native vs Java");
    }

    @Test
    void swe_set_jpl_file_null_throwsNullPointerExceptionOnBothEngines() {
        for (ISwissEph sw : new ISwissEph[]{getSwephExp(), getSwissEph()}) {
            assertThrows(NullPointerException.class, () -> sw.swe_set_jpl_file(null),
                    sw.getClass().getName());

            double[] xx = new double[6];
            StringBuilder serr = new StringBuilder();
            int ret = sw.swe_calc(J2000, SE_SUN, SEFLG_SWIEPH, xx, serr);
            assertTrue(ret >= 0 && (ret & SEFLG_SWIEPH) != 0,
                    sw.getClass() + " must be unaffected: " + serr);
        }
    }

    /** relative on purpose: the pure-Java engine splits the path on ':' as well as ';', so an
     *  absolute Windows path (T:\...) would be cut at the drive letter */
    private static String emptyEpheDir() throws IOException {
        Path empty = Files.createDirectories(Paths.get("target", "empty-ephe"));
        return empty.toString();
    }

    /**
     * With no ephemeris files in the path, C's swe_calc falls back to Moshier: the return code
     * is a valid flag set (SEFLG_MOSEPH, not ERR) and serr carries the warning. The bridge used
     * to drop that warning, because it appended serr only when retc &lt;= ERR.
     */
    @Test
    void theMoshierFallbackWarningReachesSerrThroughTheBridge() throws IOException {
        ISwissEph sw = getSwephExp();
        try {
            sw.swe_set_ephe_path(emptyEpheDir());
            double[] xx = new double[6];
            StringBuilder serr = new StringBuilder();
            int ret = sw.swe_calc(J2000, SE_MARS, SEFLG_SWIEPH, xx, serr);

            assertTrue(ret >= 0, "a fallback is not an error: " + serr);
            assertTrue((ret & SEFLG_MOSEPH) != 0, "Moshier expected, flags " + ret);
            assertTrue(serr.toString().contains("not found"), "which file is missing: '" + serr + "'");
            assertTrue(serr.toString().contains("using Moshier"), "the warning: '" + serr + "'");
        } finally {
            sw.swe_set_ephe_path(EPHE_PATH);
        }
    }

    /**
     * The pure-Java engine falls back the same way since 2026-11-23, when the Moshier planetary
     * theory (swemplan.c, swemptab.h) was ported: a SEFLG_MOSEPH return, not ERR, and serr says
     * both which file is missing and that Moshier was used. Parity in SwephMoshierFallbackTest.
     */
    @Test
    void thePureJavaEngineFallsBackToMoshierToo() throws IOException {
        ISwissEph sw = getSwissEph();
        try {
            sw.swe_set_ephe_path(emptyEpheDir());
            double[] xx = new double[6];
            StringBuilder serr = new StringBuilder();
            int ret = sw.swe_calc(J2000, SE_MARS, SEFLG_SWIEPH, xx, serr);

            assertTrue(ret >= 0, "a fallback is not an error: " + serr);
            assertTrue((ret & SEFLG_MOSEPH) != 0, "Moshier expected, flags " + ret);
            assertTrue(serr.toString().contains("not found"), "which file is missing: '" + serr + "'");
            assertTrue(serr.toString().contains("using Moshier"), "the warning: '" + serr + "'");
        } finally {
            sw.swe_set_ephe_path(EPHE_PATH);
        }
    }
}
