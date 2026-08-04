/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Drives the Swiss Ephemeris reference program <code>swetest64.exe</code> and parses its
 * output, so the library can be diffed against it live rather than against numbers pasted
 * into a test.
 * <p>
 * Override the executable with <code>-Dswetest.exe=...</code>. The ephemeris directory is
 * always the one this project ships, so both sides read the same files.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
public final class Swetest {

    public static final String EXE = System.getProperty("swetest.exe",
            "E:/Github/swisseph/windows/programs/swetest64.exe");

    public static final File EPHE = new File("ephe").getAbsoluteFile();

    /** planet letters in swetest order and where they land in {@link org.swisseph.api.ISweObjects} */
    public static final String BODIES = "0123456789";
    public static final String[] BODY_NAMES = {"Sun", "Moon", "Mercury", "Venus", "Mars",
            "Jupiter", "Saturn", "Uranus", "Neptune", "Pluto"};

    private Swetest() {
    }

    public static boolean available() {
        return new File(EXE).isFile() && EPHE.isDirectory();
    }

    /**
     * @param date  day, month, year - swetest picks the Julian calendar before 1582-10-15,
     *              exactly as {@link org.swisseph.api.ISweJulianDate#gregorianCalendar} does
     * @param extra any further switches, e.g. -p..., -sid..., -house...
     */
    public static List<String> lines(int[] date, String utcTime, String... extra) {
        final List<String> cmd = new ArrayList<>();
        cmd.add(EXE);
        cmd.add("-b" + date[2] + "." + date[1] + "." + date[0]);
        cmd.add("-ut" + utcTime);
        cmd.add("-eswe");
        cmd.add("-edir" + EPHE.getPath());
        cmd.addAll(Arrays.asList(extra));

        try {
            final Process process = new ProcessBuilder(cmd).redirectErrorStream(true).start();
            final List<String> out = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                for (String line = reader.readLine(); null != line; line = reader.readLine()) {
                    out.add(line);
                }
            }
            process.waitFor();
            return out;
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("cannot run " + String.join(" ", cmd), e);
        }
    }

    /**
     * Every "&lt;name&gt; &lt;number&gt;" line keyed by name. The format string must select a
     * single numeric column (e.g. <code>-fPl</code> or <code>-fPj</code>), otherwise the
     * name would swallow all but the last column.
     * <p>
     * A missing ephemeris file only makes swetest print a warning and silently continue
     * with Moshier, which would invalidate every comparison, so that is turned into a
     * failure here.
     */
    public static Map<String, Double> values(int[] date, String utcTime, String... extra) {
        final List<String> raw = lines(date, utcTime, extra);
        final Map<String, Double> out = new LinkedHashMap<>();

        for (String line : raw) {
            assertFalse(line.contains("not found") || line.contains("Moshier"),
                    "swetest did not use the Swiss Ephemeris files: " + line + "\n  " + raw);

            final int sp = line.lastIndexOf(' ');
            if (sp <= 0) continue;
            try {
                out.put(line.substring(0, sp).trim(), Double.parseDouble(line.substring(sp + 1).trim()));
            } catch (NumberFormatException notANumericLine) {
                // header or text line
            }
        }

        assertTrue(out.containsKey("UT:") || !out.isEmpty(), "no parsable output: " + raw);
        return out;
    }

    /**
     * @return the 12 house cusps, or null when swetest could not build them (Placidus and
     * Koch are undefined beyond the polar circle)
     */
    public static double[] cusps(Map<String, Double> values) {
        final double[] cusps = new double[13];
        for (int h = 1; h <= 12; h++) {
            final Double cusp = values.get("house " + (h < 10 ? " " : "") + h);
            if (null == cusp) return null;
            cusps[h] = cusp;
        }
        return cusps;
    }

    public static String house(double geolon, double geolat, char hsys) {
        return "-house" + geolon + "," + geolat + "," + hsys;
    }
}
