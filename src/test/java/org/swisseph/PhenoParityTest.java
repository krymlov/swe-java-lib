/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-11
 */
package org.swisseph;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import swisseph.SwissEph;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static swisseph.SweConst.*;

/**
 * {@code swe_pheno} and {@code swe_pheno_ut}, pure Java against native: every {@code attr}, the
 * return flag and the {@code serr} text.
 * <p>
 * Until 2026-11-24 the Java engine had the phenomena of release 2.07: it returned {@code OK}
 * instead of the flags used (so {@code swe_pheno_ut} always computed twice, the second time with
 * the delta t of flag 0), kept the ephemeris asked for after a fallback to Moshier, and had the
 * older magnitudes and diameters. The Moon was 3.3 mag off near the new moon, Venus 0.22 mag,
 * Saturn 0.14 mag. It is now the swecl.c 2.10.03 code: Mallama and Hilton 2018 for Mercury to
 * Neptune, Vreijs/Samaha for the Moon above 147.14 deg, the diameters of sweph.h.
 * <p>
 * The module's own {@code ephe} has planets and Moon only, so the main asteroids and the whole
 * date range are tested with {@code $SWISSEPH_HOME/ephe}, and skipped without it. No numbered
 * asteroid file exists anywhere here: {@link #asteroidFile} makes them from the Ceres of
 * {@code seas_18.se1}.
 * <p>
 * {@code @Isolated}: the tidal acceleration is JVM-global in the Java engine.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
@Isolated
public class PhenoParityTest extends AbstractTest {

    /** Sun to Vesta (nodes, apogees and the Earth among them), interpolated apsides, fictitious */
    static final int[] BODIES = {SE_SUN, SE_MOON, SE_MERCURY, SE_VENUS, SE_MARS, SE_JUPITER,
            SE_SATURN, SE_URANUS, SE_NEPTUNE, SE_PLUTO, SE_MEAN_NODE, SE_TRUE_NODE, SE_MEAN_APOG,
            SE_OSCU_APOG, SE_EARTH, SE_CHIRON, SE_PHOLUS, SE_CERES, SE_PALLAS, SE_JUNO, SE_VESTA,
            SE_INTP_APOG, SE_INTP_PERG,
            SE_CUPIDO, SE_POSEIDON, SE_ISIS, SE_NIBIRU, SE_VULCAN, SE_WHITE_MOON, SE_WALDEMATH,
            SE_AST_OFFSET + 1, SE_AST_OFFSET + 4, SE_AST_OFFSET + 134340};
    static final double[] DATES = {625673.5 /* -3000 */, 1356200.5, 1757583.5 /* 100 */,
            2086307.5 /* 1000 */, 2268923.5 /* 1500 */, 2305447.6, 2415020.4, 2434500.5,
            2444239.5, 2447892.5, 2451544.5, 2451545.0, 2454800.5, 2461041.5, 2488069.5, 2561117.5,
            2670000.5, -3100100.5 /* outside of Moshier */, 8000100.5};
    static final int[] FLAGS = {0, SEFLG_SWIEPH, SEFLG_MOSEPH, SEFLG_JPLEPH,
            SEFLG_SWIEPH | SEFLG_TRUEPOS, SEFLG_SWIEPH | SEFLG_NOABERR, SEFLG_SWIEPH | SEFLG_NOGDEFL,
            SEFLG_SWIEPH | SEFLG_HELCTR, SEFLG_SWIEPH | SEFLG_TOPOCTR,
            SEFLG_MOSEPH | SEFLG_TOPOCTR | SEFLG_TRUEPOS, SEFLG_SWIEPH | SEFLG_J2000 | SEFLG_NONUT,
            SEFLG_SWIEPH | SEFLG_SPEED | SEFLG_EQUATORIAL, SEFLG_SWIEPH | SEFLG_SIDEREAL,
            SEFLG_SWIEPH | SEFLG_XYZ | SEFLG_RADIANS | SEFLG_BARYCTR,
            SEFLG_SWIEPH | SEFLG_JPLHOR, SEFLG_SWIEPH | SEFLG_JPLHOR_APPROX};
    static final double TOL = 1e-9;
    /** the osculating node and apogee come from a speed, which is only so exact, a Moshier one least */
    static final double TOL_APOG = 1e-8, TOL_MOSHIER_NODE = 5e-8, TOL_MOSHIER_APOG = 5e-7;

    /** SEI_CERES of sweph.h, the number of Ceres in the main asteroid files, and its ellipse flag */
    private static final int SEI_CERES = 14, SEI_FLG_ELLIPSE = 4;

    private static double tolerance(int ipl, int flagUsed) {
        if (0 == (flagUsed & SEFLG_MOSEPH)) return SE_OSCU_APOG == ipl ? TOL_APOG : TOL;
        return SE_TRUE_NODE == ipl ? TOL_MOSHIER_NODE : SE_OSCU_APOG == ipl ? TOL_MOSHIER_APOG : TOL;
    }

    /** one call of either function on both engines; returns the native attr */
    private static double[] compare(ISwissEph cn, ISwissEph jv, boolean ut, double t, int ipl, int fl, String ctx) {
        final double[] x = new double[20], y = new double[20];
        Arrays.fill(x, 7); // all 20 are cleared, whatever is written then
        Arrays.fill(y, 7);
        final StringBuilder e = new StringBuilder(), f = new StringBuilder();
        final int r = ut ? cn.swe_pheno_ut(t, ipl, fl, x, e) : cn.swe_pheno(t, ipl, fl, x, e);
        final int s = ut ? jv.swe_pheno_ut(t, ipl, fl, y, f) : jv.swe_pheno(t, ipl, fl, y, f);
        final String what = ctx + (ut ? " pheno_ut" : " pheno") + " ipl " + ipl + " fl " + fl + " jd " + t;
        assertEquals(r, s, () -> what + " return; native '" + e + "' Java '" + f + "'");
        assertEquals(e.toString(), f.toString(), what + " serr");
        for (int i = 0; i < 20; i++) {
            final int n = i;
            assertEquals(x[i], y[i], tolerance(ipl, r), () -> what + " attr[" + n + "]");
        }
        return x;
    }

    private static void compareAll(ISwissEph cn, ISwissEph jv, int[] bodies, double[] dates, int[] flags, String ctx) {
        cn.swe_set_topo(30.52, 50.45, 180);
        jv.swe_set_topo(30.52, 50.45, 180);
        for (int ipl : bodies) {
            for (int fl : flags) {
                for (double t : dates) {
                    compare(cn, jv, false, t, ipl, fl, ctx);
                    compare(cn, jv, true, t, ipl, fl, ctx);
                }
            }
        }
    }

    /** $SWISSEPH_HOME/ephe, relative to this module: the port takes the ':' of a drive for a separator */
    private static String referenceEphe() {
        final String home = System.getenv("SWISSEPH_HOME");
        assumeTrue(null != home, "SWISSEPH_HOME is not set");
        final Path ephe = Paths.get(home, "ephe").toAbsolutePath(), here = Paths.get("").toAbsolutePath();
        assumeTrue(Files.isRegularFile(ephe.resolve("seas_18.se1")), "no seas_18.se1 in $SWISSEPH_HOME/ephe");
        assumeTrue(ephe.getRoot().equals(here.getRoot()), "$SWISSEPH_HOME is on another drive");
        return here.relativize(ephe).toString();
    }

    @FunctionalInterface
    private interface Engines {
        void run(ISwissEph cn, ISwissEph jv);
    }

    private static void withEphe(String ephe, Engines engines) {
        try (ISwissEph cn = new SwephNative(ephe); ISwissEph jv = new SwissEph(ephe)) {
            engines.run(cn, jv);
        }
    }

    private static double phaseAngle(ISwissEph cn, int ipl, double t) {
        final double[] x = new double[20];
        cn.swe_pheno(t, ipl, SEFLG_SWIEPH, x, null);
        return x[0];
    }

    /** the date between the two on which the native phase angle passes the given one */
    private static double passes(ISwissEph cn, int ipl, double angle, double from, double to) {
        final boolean above = phaseAngle(cn, ipl, from) > angle;
        assertTrue(above != phaseAngle(cn, ipl, to) > angle, "the phase angle of " + ipl + " does not pass " + angle);
        for (int i = 0; i < 50; i++) {
            final double mid = (from + to) / 2;
            if (above == phaseAngle(cn, ipl, mid) > angle) from = mid;
            else to = mid;
        }
        return to;
    }

    /** both sides of a threshold: the phase angles must lie on them, and both engines agree */
    private static void compareAround(ISwissEph cn, ISwissEph jv, int ipl, double angle, double t) {
        int above = 0;
        for (double dt : new double[]{-1e-1, -1e-2, -1e-4, -1e-6, 1e-6, 1e-4, 1e-2, 1e-1}) {
            if (compare(cn, jv, false, t + dt, ipl, SEFLG_SWIEPH, "around " + angle)[0] > angle) above++;
        }
        assertEquals(4, above, "dates with a phase angle above " + angle);
    }

    @Test
    void everyBodyAndFlag_ownEphe_moshierFallback_matchNative() {
        compareAll(getSwephExp(), getSwissEph(), BODIES, DATES, FLAGS, "own ephe");
    }

    @Test
    void everyBodyAndFlag_noEphe_matchNative() {
        withEphe("target/no-ephe", (cn, jv) -> compareAll(cn, jv, BODIES, DATES, FLAGS, "no ephe"));
    }

    @Test
    void everyBodyAndFlag_referenceEphe_matchNative() {
        withEphe(referenceEphe(), (cn, jv) -> compareAll(cn, jv, BODIES, DATES, FLAGS, "reference ephe"));
    }

    @Test
    void everyPlanet_everyTwelveDays_matchNative() {
        final int[] bodies = {SE_SUN, SE_MOON, SE_MERCURY, SE_VENUS, SE_MARS, SE_JUPITER, SE_SATURN,
                SE_URANUS, SE_NEPTUNE, SE_PLUTO};
        for (int ipl : bodies) {
            for (double t = 2444239.5; t < 2463000; t += 11.97) {
                compare(getSwephExp(), getSwissEph(), false, t, ipl, SEFLG_SWIEPH, "1980-2031");
            }
        }
    }

    @Test
    void moon_bothSidesOfTheVreijsThreshold_matchNative() {
        final ISwissEph cn = getSwephExp(), jv = getSwissEph();
        final double newMoon = 2451550.26; // January 2000: the threshold before and after it
        compareAround(cn, jv, SE_MOON, 147.1385465, passes(cn, SE_MOON, 147.1385465, newMoon - 10, newMoon));
        compareAround(cn, jv, SE_MOON, 147.1385465, passes(cn, SE_MOON, 147.1385465, newMoon, newMoon + 10));
    }

    @Test
    void venus_bothSidesOfItsThresholds_matchNative() {
        final ISwissEph cn = getSwephExp(), jv = getSwissEph();
        final double transit2004 = 2453164.8451; // 8 June 2004
        compareAround(cn, jv, SE_VENUS, 163.7, passes(cn, SE_VENUS, 163.7, transit2004 - 40, transit2004));
        compareAround(cn, jv, SE_VENUS, 163.7, passes(cn, SE_VENUS, 163.7, transit2004, transit2004 + 40));
        compareAround(cn, jv, SE_VENUS, 179, passes(cn, SE_VENUS, 179, transit2004 - 10, transit2004));
        compareAround(cn, jv, SE_VENUS, 179, passes(cn, SE_VENUS, 179, transit2004, transit2004 + 10));
    }

    @Test
    void venus_above179degrees_warnsAsNative() {
        final double transit2012 = 2456084.5625; // 6 June 2012
        for (double dt = -0.4; dt <= 0.4; dt += 0.01) {
            compare(getSwephExp(), getSwissEph(), false, transit2012 + dt, SE_VENUS, SEFLG_SWIEPH, "transit of Venus");
            compare(getSwephExp(), getSwissEph(), true, transit2012 + dt, SE_VENUS, SEFLG_MOSEPH, "transit of Venus");
        }
        final StringBuilder serr = new StringBuilder("what another call has left");
        assertEquals(SEFLG_SWIEPH, getSwissEph().swe_pheno(transit2012, SE_VENUS, SEFLG_SWIEPH, new double[20], serr));
        assertTrue(serr.toString().matches("magnitude value for Venus at phase angle i=179\\.\\d is bad; formula is valid only for i < 179\\.0"), "'" + serr + "'");
        assertEquals(SEFLG_SWIEPH, getSwissEph().swe_pheno(transit2012, SE_VENUS, SEFLG_SWIEPH, new double[20], null), "no serr");
    }

    /**
     * From the Earth the phase angle of Mars stays below 48 deg and its second formula is
     * "irrelevant to earth-centered observation"; an observer 0.6 AU above the Earth reaches it.
     */
    @Test
    void mars_bothSidesOf50degrees_farObserver_matchNative() {
        final ISwissEph cn = getSwephExp(), jv = getSwissEph();
        final int fl = SEFLG_SWIEPH | SEFLG_TOPOCTR | SEFLG_TRUEPOS | SEFLG_NOABERR | SEFLG_NOGDEFL;
        cn.swe_set_topo(30.52, 0, 0.6 * 149597870700.0);
        jv.swe_set_topo(30.52, 0, 0.6 * 149597870700.0);
        int above = 0, n = 0;
        for (double t = 2451545; t < 2451545 + 780; t += 1.37) {
            if (compare(cn, jv, false, t, SE_MARS, fl, "far observer")[0] > 50) above++;
            n++;
        }
        assertTrue(above > 10 && above < n - 10, above + " of " + n + " above 50 deg");
    }

    @Test
    void saturn_ringsOpenAndEdgeOn_matchNative() {
        // edge-on 1980, 1995/96, 2009, 2025; open 1988, 2003, 2017
        for (double t = 2444000.5; t < 2462000; t += 3.31) {
            compare(getSwephExp(), getSwissEph(), false, t, SE_SATURN, SEFLG_SWIEPH, "rings");
        }
        for (double t : new double[]{2449860, 2450135, 2455079, 2460758, 2447300, 2452700, 2458030}) {
            for (int fl : FLAGS) {
                compare(getSwephExp(), getSwissEph(), true, t, SE_SATURN, fl, "rings");
            }
        }
    }

    @Test
    void neptune_itsThreePeriods_matchNative() {
        for (double t : new double[]{2444239.5, 2451544.5}) {
            for (double dt : new double[]{-400, -1, -1e-6, 0, 1e-6, 1, 400}) {
                compare(getSwephExp(), getSwissEph(), false, t + dt, SE_NEPTUNE, SEFLG_SWIEPH, "Neptune");
            }
        }
    }

    /**
     * An .se1 file is closed at the end of the dates its name stands for, although it goes on for some days;
     * just after the last one the planet is Moshier's at tjd, and so must it be at tjd - light time.
     */
    @Test
    void bothSidesOfEveryFileBorder_matchNative() {
        final double[] borders = {1940207.5 /* 600 */, 2159357.5 /* 1200 */, 2378496.5 /* 1800 */, 2597641.5 /* 2400 */};
        final double[] dates = new double[borders.length * 6];
        int n = 0;
        for (double b : borders) {
            for (double d : new double[]{-0.2, -1e-4, 0, 1e-4, 0.05, 5}) dates[n++] = b + d;
        }
        withEphe("ephe", (cn, jv) -> compareAll(cn, jv, BODIES, dates, new int[]{SEFLG_SWIEPH, 0}, "file border"));
        withEphe("ephe", (cn, jv) -> assertEquals(SEFLG_MOSEPH,
                jv.swe_pheno(2597641.6, SE_JUPITER, SEFLG_SWIEPH, new double[20], new StringBuilder())));
    }

    @Test
    void withoutSerr_matchNative() {
        for (int ipl : BODIES) {
            for (int fl : FLAGS) {
                final double[] x = new double[20], y = new double[20];
                final int r = getSwephExp().swe_pheno_ut(2415020.4, ipl, fl, x, null);
                assertEquals(r, getSwissEph().swe_pheno_ut(2415020.4, ipl, fl, y, null), "return, ipl " + ipl + " fl " + fl);
                for (int i = 0; i < 20; i++) {
                    assertEquals(x[i], y[i], tolerance(ipl, r), "ipl " + ipl + " fl " + fl + " attr[" + i + "]");
                }
            }
        }
    }

    /** the C reads outside of pla_diam[] and mag_elem[] here: there is nothing to compare with */
    @Test
    void eclNut_doesNotThrow() {
        getSwissEph().swe_pheno(2451545, SE_ECL_NUT, SEFLG_SWIEPH, new double[20], new StringBuilder());
    }

    @Test
    void numberedAsteroids_matchNative() throws IOException {
        final Path dir = Paths.get("target/pheno-ephe");
        final byte[] seas = Files.readAllBytes(Paths.get(referenceEphe(), "seas_18.se1"));
        // what is read of a record: H from column 42, G from 49, the diameter in 58-64
        asteroidFile(dir, seas, 433, "", record(433, "Eros", "11.16", "0.46", "23.0"));
        asteroidFile(dir, seas, 944, "s", record(944, "Hidalgo", "10.77", "0.00", ""));
        asteroidFile(dir, seas, 1566, "s", record(1566, "Icarus", "12.34", "0.77", "1.0"));
        // a negative absolute magnitude, as that of Eris, without and with a diameter
        asteroidFile(dir, seas, 6199, "s", record(6199, "Testris", "-1.12", "0.15", ""));
        asteroidFile(dir, seas, 6200, "", record(6200, "Testrix", "-1.12", "-0.12", "2326."));
        final int[] numbers = {433, 944, 1566, 6199, 6200};
        final int[] bodies = new int[numbers.length];
        for (int i = 0; i < bodies.length; i++) bodies[i] = SE_AST_OFFSET + numbers[i];
        final double[] dates = {2415020.4, 2444239.5, 2451545.0, 2461041.5, 2488069.5,
                2305447.6 /* before the file */, 2670000.5 /* after it */};
        withEphe(dir + ";ephe", (cn, jv) -> {
            compareAll(cn, jv, bodies, dates, FLAGS, "asteroids");
            // each file gives its own magnitude and diameter, whichever was read before
            final double[] h = {11.16, 10.77, 16.9, -1.12, -1.12};
            for (int i = 0; i < bodies.length; i++) {
                final double[] attr = compare(cn, jv, false, 2451545.0, bodies[i], SEFLG_SWIEPH, "asteroids");
                assertTrue(attr[0] > 1 && attr[3] > 0, "ipl " + bodies[i] + " " + Arrays.toString(attr));
                assertEquals(h[i] + 4.5, attr[4], 1.5, "magnitude of ipl " + bodies[i]);
            }
            // the first and the last moment of a file: the last one belongs to its last segment,
            // one segment number too far
            final double[] from = new double[1], to = new double[1];
            cn.swe_get_current_file_data(3, from, to, new int[1]);
            assertTrue(to[0] > from[0] && from[0] > 2300000, Arrays.toString(from) + Arrays.toString(to));
            compareAll(cn, jv, new int[]{SE_AST_OFFSET + 6200}, new double[]{from[0], to[0]}, FLAGS, "file ends");
        });
    }

    /** an element record of the astorb.dat kind */
    private static String record(int number, String name, String h, String g, String diameter) {
        return String.format("%6d %-18s %-15s  %5s %6s   %-7s%-13s", number, name, "J. D. Tester", h, g, diameter, "71 is not read");
    }

    /** the Ceres of seas_18.se1 as the file of a numbered asteroid, with the given element record */
    private static void asteroidFile(Path dir, byte[] seas, int number, String suffix, String record) throws IOException {
        final ByteBuffer in = ByteBuffer.wrap(seas).order(ByteOrder.LITTLE_ENDIAN);
        int pos = 0;
        for (int line = 0; line < 3; line++) { // version, name, copyright
            while (in.get(pos) != 10) pos++;
            pos++;
        }
        final String[] lines = new String(seas, 0, pos, StandardCharsets.ISO_8859_1).split("\\R");
        final int endian = in.getInt(pos), denum = in.getInt(pos + 8);
        final double tfstart = in.getDouble(pos + 12), tfend = in.getDouble(pos + 20);
        final int npl = in.getShort(pos + 28);
        pos += 30;
        int ceres = -1;
        for (int i = 0; i < npl; i++) {
            if (SEI_CERES == in.getShort(pos + 2 * i)) ceres = i;
        }
        pos += 2 * npl + 4; // the list and the CRC
        final int constants = pos;
        pos += 5 * 8;
        int planet = -1, planetLength = 0;
        for (int i = 0; i < npl; i++) { // index position, flags, coefficients, rmax, ten doubles
            int length = 4 + 1 + 1 + 4 + 10 * 8;
            if (0 != (in.get(pos + 4) & SEI_FLG_ELLIPSE)) length += 2 * 8 * in.get(pos + 5);
            if (i == ceres) {
                planet = pos;
                planetLength = length;
            }
            pos += length;
        }
        assertTrue(planet > 0, "Ceres in seas_18.se1");

        final String name = "se" + String.format("%05d", number) + suffix + ".se1";
        final String text = String.join("\r\n", lines[0], name, lines[2], record, "");
        final int header = text.length() + 4 + 4 + 4 + 8 + 8 + 2 + 2 + 30;
        final int planetOut = header + 4 + 5 * 8, delta = planetOut + planetLength - pos;
        final ByteBuffer out = ByteBuffer.allocate(seas.length + delta).order(ByteOrder.LITTLE_ENDIAN);
        out.put(text.getBytes(StandardCharsets.ISO_8859_1)).putInt(endian).putInt(out.capacity()).putInt(denum);
        // the file ends with its last segment, so that its last moment is the one after that segment's
        final double lastMoment = in.getDouble(planet + 18);
        assertTrue(lastMoment >= tfend, lastMoment + " " + tfend);
        out.putDouble(tfstart).putDouble(lastMoment).putShort((short) 1).putShort((short) (SE_AST_OFFSET + number));
        out.put(Arrays.copyOf(("Name " + number).getBytes(StandardCharsets.ISO_8859_1), 30));
        out.putInt(crc32(out.array(), header));
        out.put(seas, constants, 5 * 8).put(seas, planet, planetLength);
        out.put(seas, pos, seas.length - pos);
        // the positions of the index and of every segment
        final int index = in.getInt(planet) + delta;
        out.putInt(planetOut, index);
        final double start = in.getDouble(planet + 10), end = in.getDouble(planet + 18), dseg = in.getDouble(planet + 26);
        final int segments = (int) ((end - start + 0.1) / dseg);
        for (int i = 0; i < segments; i++) {
            final int at = index + 3 * i;
            int seg = (out.get(at) & 0xff) | (out.get(at + 1) & 0xff) << 8 | (out.get(at + 2) & 0xff) << 16;
            seg += delta;
            out.put(at, (byte) seg).put(at + 1, (byte) (seg >> 8)).put(at + 2, (byte) (seg >> 16));
        }
        final Path file = dir.resolve("ast" + number / 1000).resolve(name);
        Files.createDirectories(file.getParent());
        Files.write(file, out.array());
    }

    /** swi_crc32() of swephlib.c */
    private static int crc32(byte[] buf, int length) {
        int crc = 0xffffffff;
        for (int i = 0; i < length; i++) {
            crc ^= (buf[i] & 0xff) << 24;
            for (int bit = 0; bit < 8; bit++) {
                crc = crc < 0 ? (crc << 1) ^ 0x04c11db7 : crc << 1;
            }
        }
        return ~crc;
    }
}
