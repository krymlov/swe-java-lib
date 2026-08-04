/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.swisseph.api.ISweObjects;
import org.swisseph.app.SweAyanamsa;
import org.swisseph.app.SweJulianDate;
import org.swisseph.app.SweObjects;
import org.swisseph.app.SweObjectsOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.swisseph.api.ISweObjects.*;
import static org.swisseph.app.SweHouseSystem.PLACIDUS;

/**
 * Drives the real Swiss Ephemeris reference program and compares its output with
 * this library, live, instead of relying on numbers pasted into a test.
 * <p>
 * {@link JhdIuriiKTest} pins the same values as literals so the suite still means
 * something on a machine that has no <code>swetest64.exe</code>; these tests skip
 * themselves when the executable or the ephemeris files are missing.
 * <p>
 * The reference program and its location can be overridden with the system
 * property <code>swetest.exe</code>.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
public class SwetestCrossCheckTest extends AbstractTest {

    static final String SWETEST = System.getProperty("swetest.exe",
            "E:/Github/swisseph/windows/programs/swetest64.exe");

    static final JhdChart JHD = JhdChart.read("IuriiK.jhd");
    static final double DELTA = 1e-7;

    /** planet letters in swetest order, mapped onto ISweObjects indices */
    static final String SWETEST_BODIES = "0123456789m";
    static final int[] SWETEST_TO_OBJECT = {SY, CH, BU, SK, MA, GU, SA, UR, NE, PL, RA};

    private static boolean available() {
        return new File(SWETEST).isFile() && new File("ephe").isDirectory();
    }

    /**
     * @return every "name value" line of swetest keyed by name
     */
    private static Map<String, Double> runSwetest(String... extra) throws IOException, InterruptedException {
        final List<String> cmd = new ArrayList<>();
        cmd.add(SWETEST);
        cmd.add("-b" + JHD.date()[2] + "." + JHD.date()[1] + "." + JHD.date()[0]);
        cmd.add("-ut20:21:00");
        cmd.add("-eswe");
        cmd.add("-true");
        cmd.add("-fPl");
        cmd.add("-edir" + new File("ephe").getAbsolutePath());
        for (String e : extra) cmd.add(e);

        final ProcessBuilder pb = new ProcessBuilder(cmd).redirectErrorStream(true);
        final Process p = pb.start();

        final Map<String, Double> out = new LinkedHashMap<>();
        final List<String> raw = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(
                new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
            for (String l = r.readLine(); null != l; l = r.readLine()) {
                raw.add(l);
                // "Sun               5.3904126" / "house  1         220.9553945"
                final int sp = l.lastIndexOf(' ');
                if (sp <= 0) continue;
                final String name = l.substring(0, sp).trim();
                try {
                    out.put(name, Double.parseDouble(l.substring(sp + 1).trim()));
                } catch (NumberFormatException ignored) {
                    // header / text line
                }
            }
        }
        p.waitFor();

        // a missing ephemeris file makes swetest fall back to Moshier silently enough
        // to invalidate every comparison - fail loudly instead
        for (String l : raw) {
            assertFalse(l.contains("not found") || l.contains("Moshier"),
                    "swetest did not use the Swiss Ephemeris files: " + l);
        }
        assertFalse(out.isEmpty(), "no parsable output from " + String.join(" ", cmd));
        return out;
    }

    @Test
    void swetestIsAvailable() {
        assumeTrue(available(), "swetest64.exe / ephe not found - skipping live cross check");
        assertTrue(new File(SWETEST).canExecute());
    }

    @ParameterizedTest(name = "sid{0}")
    @ValueSource(ints = {0, 1, 3, 5, 27})
    void bodiesMatchSwetestLive(int sid) throws Exception {
        assumeTrue(available(), "swetest64.exe / ephe not found");

        final SweAyanamsa ayanamsa = ayanamsaOf(sid);
        final Map<String, Double> ref = runSwetest("-p" + SWETEST_BODIES, "-sid" + sid);
        final ISweObjects o = objects(ayanamsa);

        final String[] names = {"Sun", "Moon", "Mercury", "Venus", "Mars", "Jupiter",
                "Saturn", "Uranus", "Neptune", "Pluto", "mean Node"};
        for (int i = 0; i < names.length; i++) {
            final Double expected = ref.get(names[i]);
            assertNotNull(expected, names[i] + " missing from swetest output");
            assertEquals(expected, o.longitudes()[SWETEST_TO_OBJECT[i]], DELTA, names[i]);
        }
    }

    @ParameterizedTest(name = "sid{0}")
    @ValueSource(ints = {0, 1, 3, 5, 27})
    void housesMatchSwetestLive(int sid) throws Exception {
        assumeTrue(available(), "swetest64.exe / ephe not found");

        final Map<String, Double> ref = runSwetest("-p0", "-sid" + sid,
                "-house" + JHD.longitude() + "," + JHD.latitude() + ",P");
        final ISweObjects o = objects(ayanamsaOf(sid));

        for (int h = 1; h <= 12; h++) {
            final String key = "house " + (h < 10 ? " " : "") + h;
            final Double expected = ref.get(key);
            assertNotNull(expected, key + " missing from swetest output");
            assertEquals(expected, o.cusps()[h], DELTA, key);
        }
        assertEquals(ref.get("Ascendant"), o.longitudes()[LG], DELTA, "Ascendant");
    }

    /**
     * The pure Java port is an older Swiss Ephemeris (2.01.00), so it is held to a
     * looser bound than the native library - but it still has to agree with the
     * reference program to well under an arc second.
     */
    @Test
    void pureJavaTracksSwetestWithinAnArcSecond() throws Exception {
        assumeTrue(available(), "swetest64.exe / ephe not found");

        final Map<String, Double> ref = runSwetest("-p" + SWETEST_BODIES, "-sid1");
        final ISweObjects j = new SweObjects(getSwissEph(),
                new SweJulianDate(JHD.date(), JHD.timeZone(), JHD.localTime()), JHD.geoLocation(),
                new SweObjectsOptions.Builder().ayanamsa(SweAyanamsa.LAHIRI).houseSystem(PLACIDUS).build())
                .completeBuild();

        final String[] names = {"Sun", "Moon", "Mercury", "Venus", "Mars", "Jupiter",
                "Saturn", "Uranus", "Neptune", "Pluto"};
        for (int i = 0; i < names.length; i++) {
            assertEquals(ref.get(names[i]), j.longitudes()[SWETEST_TO_OBJECT[i]], 1. / 3600., names[i]);
        }
    }

    private ISweObjects objects(SweAyanamsa ayanamsa) {
        return new SweObjects(getSwephExp(),
                new SweJulianDate(JHD.date(), JHD.timeZone(), JHD.localTime()), JHD.geoLocation(),
                new SweObjectsOptions.Builder().ayanamsa(ayanamsa).houseSystem(PLACIDUS).build())
                .completeBuild();
    }

    private static SweAyanamsa ayanamsaOf(int sid) {
        for (SweAyanamsa a : SweAyanamsa.values()) if (a.fid() == sid) return a;
        throw new IllegalArgumentException("no ayanamsa with sid " + sid);
    }
}
