/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-11
 */
package org.swisseph;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Isolated;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.swisseph.api.ISweConstants.EPHE_PATH;
import static swisseph.SweConst.*;

/**
 * Fictitious bodies and {@code seorbel.txt}, pure Java against native: return value, {@code serr}, positions, names.
 * <p>
 * Until 2026-11-24 the Java engine gave ERR without a text for a body beyond the end of the file, no line numbers, an exception
 * at an empty line; it read the name "Korè" byte by byte, and on Windows cut an ephemeris path at the drive letter.
 * <p>
 * No field here may hold a character that belongs to no term ("abc", "10e5") except in
 * {@link #aWordInATerm_isRefusedByJava}: {@code check_t_terms()} of the C never returns from it.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
@Isolated
public class FictitiousBodyParityTest extends AbstractTest {

    static final String GOOD = "J2000,J2000,10.5,20.25,0.1,30,40,5,Good # 0\n";
    static final String HEAD = "# comment\n\n   \t# another\r\n";
    static final int FIRST = SE_FICT_OFFSET;
    static final double[] DATES = {2451545.3, 2415020.5};
    static final int[] FLAGS = {SEFLG_SWIEPH, SEFLG_SWIEPH | SEFLG_SPEED, SEFLG_MOSEPH | SEFLG_SPEED, SEFLG_SWIEPH | SEFLG_HELCTR,
            SEFLG_SWIEPH | SEFLG_BARYCTR | SEFLG_SPEED};
    static final double UNTOUCHED = 7;
    static final double TOL_POS = 1e-8, TOL_SPEED = 1e-6;

    private int files;

    /** one call on both engines; what native says */
    private String compare(double jd, int body, int flags, int wanted) {
        final ISwissEph[] engines = {getSwephExp(), getSwissEph()};
        final double[][] xx = new double[2][8];
        final int[] ret = new int[2];
        final String[] serr = new String[2];
        for (int i = 0; i < 2; i++) {
            final StringBuilder err = new StringBuilder();
            Arrays.fill(xx[i], UNTOUCHED);
            ret[i] = engines[i].swe_calc(jd, body, flags, xx[i], err);
            serr[i] = err.toString();
        }
        final String ctx = jd + " body " + body + " flags " + flags;
        assertEquals(serr[0], serr[1], ctx);
        assertEquals(ret[0], ret[1], ctx);
        for (int k = 0; k < 8; k++) assertEquals(xx[0][k], xx[1][k], k < 3 ? TOL_POS : TOL_SPEED, ctx + " xx[" + k + "]");
        if (ERR == wanted) assertEquals(ERR, ret[0], ctx); else assertTrue(ret[0] >= 0, ctx + " " + serr[0]);
        return serr[0];
    }

    private void path(String path) {
        getSwephExp().swe_set_ephe_path(path);
        getSwissEph().swe_set_ephe_path(path);
    }

    /** a directory of its own for each file, or the engines would keep the last one */
    private void file(Path dir, String text) throws IOException {
        final Path own = Files.createDirectory(dir.resolve("f" + files++));
        Files.write(own.resolve(SE_FICTFILE), text.getBytes(StandardCharsets.UTF_8));
        path(own.toAbsolutePath() + ";" + EPHE_PATH);
    }

    private void names(int count) {
        for (int b = FIRST; b < FIRST + count; b++) {
            assertEquals(getSwephExp().swe_get_planet_name(b), getSwissEph().swe_get_planet_name(b), "name of " + b);
        }
    }

    private void refused(Path dir, String text, int body, String message) throws IOException {
        file(dir, text);
        for (double d : DATES) for (int f : FLAGS) assertEquals(message, compare(d, FIRST + body, f, ERR));
        names(3);
    }

    @Test
    void theRealFile_matchesNative() {
        path(EPHE_PATH);
        for (double d : DATES) {
            for (int f : FLAGS) {
                for (int b = FIRST; b < FIRST + 24; b++) compare(d, b, f, OK);
                for (int b : new int[]{FIRST + 30, 999}) {
                    assertTrue(compare(d, b, f, ERR).endsWith("elements for planet " + String.format("%7d", b - FIRST) + " not found"));
                }
            }
        }
        names(40);
        assertEquals("Korè", getSwissEph().swe_get_planet_name(FIRST + 23), "UTF-8 in the file");
        assertEquals("Korè", getSwissEph().swe_get_planet_name(FIRST + 23), "from the store");
    }

    @Test
    void theErrors_matchNative(@TempDir Path dir) throws IOException {
        refused(dir, HEAD + GOOD, 1, "error in file seorbel.txt, line       4: elements for planet       1 not found");
        refused(dir, HEAD + GOOD + "1,2,3\n" + GOOD, 1, "error in file seorbel.txt, line       5: nine elements required");
        refused(dir, GOOD + "x\n", 1, "error in file seorbel.txt, line       2: nine elements required");
        refused(dir, HEAD + "J2100,J2000,10.5,20.25,0.1,30,40,5,N\n", 0, "error in file seorbel.txt, line       4: invalid epoch");
        refused(dir, GOOD + "b1900,J2000,10.5,20.25,0.1,30,40,5,N\n", 1, "error in file seorbel.txt, line       2: invalid epoch");
        refused(dir, GOOD + "J2000,  jxxxx,10.5,20.25,0.1,30,40,5,N\n", 1, "error in file seorbel.txt, line       2: invalid equinox");
        for (String axis : new String[]{"0", "-3", "0*T"}) {
            refused(dir, "J2000,J2000,10.5," + axis + ",0.1,30,40,5,N\n", 0, "error in file seorbel.txt, line       1: semi-axis value invalid");
        }
        for (String ecc : new String[]{"1", "1.5", "-0.1"}) {
            refused(dir, "J2000,J2000,10.5,20," + ecc + ",30,40,5,N\n", 0,
                    "error in file seorbel.txt, line       1: eccentricity invalid (no parabolic or hyperbolic orbits allowed)");
        }
        // a line of more than 255 bytes is several lines to fgets()
        refused(dir, "J2000,J2000,1,2,0.1,3,4,5,Long " + new String(new char[500]).replace('\0', 'a') + "\n" + GOOD, 1,
                "error in file seorbel.txt, line       2: nine elements required");
    }

    @Test
    void theForms_matchNative(@TempDir Path dir) throws IOException {
        file(dir, HEAD + "  \t J2000 , J2000 , 10.5 , 20.25 , 0.1 , 30 , 40 , 5 ,  Spaces here  \r\n"
                + "b1950,jdate,10.5 + 3.5 * T - 0.5 * T2,20.25 + 0.001*T,0.1,30+T,40-1.5*T3,5,Terms, GEO\r\n"
                + "J1900,B1950,-10.5,20.25,0.1,-30,40 + 2*T4,-5,Geo2,x geo y\r\n"
                + "2451545,2451545,10.5,20.25,0.1,30,40,5,Num#ber\n"
                + "2451545,2451545,10.5,20.25,0.1,30,40,5,Eleven, a, b\n"
                + "1e3,2.4515E+6,1,2,0.1,3,4,5,Exponent\n"
                + "J2000,J2000,10.5*T-2,20,0.98,30,40,5,Кора\n"
                + "J2000,J2000,10.5,20.25,0.1,30,40,5,The last line has no end");
        for (double d : DATES) for (int f : FLAGS) for (int b = 0; b < 8; b++) compare(d, FIRST + b, f, OK);
        names(9);
        assertEquals("Spaces here", getSwissEph().swe_get_planet_name(FIRST));
        assertEquals("Num", getSwissEph().swe_get_planet_name(FIRST + 3));
        assertEquals("Кора", getSwissEph().swe_get_planet_name(FIRST + 6));
    }

    /** no seorbel.txt: the built-in elements for the first bodies, and where the file was looked for; an absolute path */
    @Test
    void withoutTheFile_matchesNative(@TempDir Path dir) {
        path(dir.toAbsolutePath().toString());
        for (double d : DATES) {
            for (int f : FLAGS) {
                assertTrue(compare(d, FIRST, f, OK).contains("not found in PATH '" + dir.toAbsolutePath()));
                assertEquals("error no elements for fictitious body no      20", compare(d, FIRST + 20, f, ERR));
            }
        }
        names(25);
    }

    /** native would hang: Java says which value is wrong */
    @Test
    void aWordInATerm_isRefusedByJava(@TempDir Path dir) throws IOException {
        final String[][] cases = {{"abc,20,0.1,30,40,5", "mean anomaly value invalid"}, {"1,10e5,0.1,30,40,5", "semi-axis value invalid"},
                {"1,20,x,30,40,5", "eccentricity invalid (no parabolic or hyperbolic orbits allowed)"},
                {"1,20,0.1,x,40,5", "perihelion argument value invalid"}, {"1,20,0.1,30,4y,5", "node value invalid"},
                {"1,20,0.1,30,40,z", "inclination value invalid"}};
        for (String[] c : cases) {
            file(dir, "J2000,J2000," + c[0] + ",N\n");
            final StringBuilder serr = new StringBuilder();
            assertEquals(ERR, getSwissEph().swe_calc(DATES[0], FIRST, SEFLG_SWIEPH, new double[6], serr));
            assertEquals("error in file seorbel.txt, line       1: " + c[1], serr.toString());
        }
    }
}
