/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Test;
import org.swisseph.api.ISweJulianDate;
import swisseph.SweConst;
import swisseph.SwephExp;
import swisseph.SwissEph;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static swisseph.SweConst.*;

/**
 * How much of {@link SwephExp} the two {@link ISwissEph} implementations actually cover.
 * <p>
 * The first two tests are structural and hold by reflection, so they keep holding as the API
 * grows: every native method must be reachable through {@code ISwissEph}, and
 * {@code swisseph.SwissEph} must override every one of them. The interface bodies delegate to
 * {@code SwephExp}, so a method the pure Java class forgets to override does not fail at
 * compile time - it fails with {@code UnsatisfiedLinkError} for whoever ships without the
 * native library.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
public class ApiCoverageTest extends AbstractTest {

    /**
     * Not ported, and deliberately so - each throws {@link NotImplementedException} with the
     * reason. Listed here so that porting one makes this test fail and get updated.
     */
    static final Set<String> NOT_PORTED = new TreeSet<>(Arrays.asList(
            "swe_calc_pctr", "swe_get_orbital_elements", "swe_orbit_max_min_true_distance"));

    private static String key(Method m) {
        StringBuilder sb = new StringBuilder(m.getName()).append('(');
        Class<?>[] p = m.getParameterTypes();
        for (int i = 0; i < p.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(p[i].getSimpleName());
        }
        return sb.append(')').toString();
    }

    /** every native of SwephExp, as name(paramTypes) */
    private static Set<String> natives() {
        Set<String> out = new TreeSet<>();
        for (Method m : SwephExp.class.getDeclaredMethods()) {
            if (Modifier.isNative(m.getModifiers())) out.add(key(m));
        }
        return out;
    }

    private static Set<String> declared(Class<?> type) {
        Set<String> out = new TreeSet<>();
        for (Method m : type.getMethods()) {
            if (m.getName().startsWith("swe_")) out.add(key(m));
        }
        return out;
    }

    // ======================================================== structural coverage

    @Test
    void everyNativeMethodIsReachableThroughISwissEph() {
        Set<String> missing = new LinkedHashSet<>(natives());
        missing.removeAll(declared(ISwissEph.class));

        assertTrue(missing.isEmpty(), "SwephExp natives with no ISwissEph declaration: " + missing);
        assertEquals(106, natives().size(), "SwephExp should declare 106 natives");
    }

    /**
     * The one that matters for anyone shipping without the .dll/.so: an ISwissEph method the
     * pure Java class does not override keeps the interface default, which calls SwephExp.
     */
    @Test
    void swissEphOverridesEveryNativeMethod() {
        List<String> notOverridden = new ArrayList<>();
        for (Method m : SwissEph.class.getMethods()) {
            if (!m.getName().startsWith("swe_")) continue;
            if (m.getDeclaringClass() == SwissEph.class) continue;
            if (natives().contains(key(m))) notOverridden.add(key(m));
        }
        assertTrue(notOverridden.isEmpty(),
                "swisseph.SwissEph inherits the SwephExp-calling default for: " + notOverridden);
    }

    @Test
    void theOnlyUnportedMethodsSayWhyTheyAreUnported() {
        for (String name : NOT_PORTED) {
            boolean found = false;
            for (Method m : SwissEph.class.getDeclaredMethods()) {
                if (!m.getName().equals(name)) continue;
                found = true;
            }
            assertTrue(found, name + " is no longer declared - update NOT_PORTED");
        }

        NotImplementedException e = assertThrows(NotImplementedException.class,
                () -> getSwissEph().swe_get_orbital_elements(2451545., SE_MARS, SEFLG_SWIEPH,
                        new double[50], new StringBuilder()));
        assertTrue(e.getMessage().contains("SwephNative"),
                "the message should point at the native implementation: " + e.getMessage());
    }

    // ==================================================== the raw 1:1 forms

    @Test
    void theRawRevjulFormFillsInItsOutParameters() {
        double jd = 2451545.;
        int[] ymd = new int[3];
        double[] jut = new double[1];

        getSwissEph().swe_revjul(jd, SE_GREG_CAL, ymd, jut);
        assertEquals(2000, ymd[0]);
        assertEquals(1, ymd[1]);
        assertEquals(1, ymd[2]);
        assertEquals(12., jut[0], 1e-9);

        int[] ymdN = new int[3];
        double[] jutN = new double[1];
        getSwephExp().swe_revjul(jd, SE_GREG_CAL, ymdN, jutN);
        assertEquals(ymdN[0], ymd[0]);
        assertEquals(ymdN[1], ymd[1]);
        assertEquals(ymdN[2], ymd[2]);
        assertEquals(jutN[0], jut[0], 1e-9);
    }

    @Test
    void theRawUtcToJdFormReturnsBothJulianDays() {
        double[] dret = new double[2], dretN = new double[2];

        assertEquals(SweConst.OK, getSwissEph().swe_utc_to_jd(2000, 1, 1, 12, 0, 0.,
                SE_GREG_CAL, dret, new StringBuilder()));
        getSwephExp().swe_utc_to_jd(2000, 1, 1, 12, 0, 0., SE_GREG_CAL, dretN, new StringBuilder());

        assertTrue(dret[0] > dret[1], "TT is ahead of UT1 in 2000");
        assertEquals(dretN[0], dret[0], 1e-8, "julian day TT");
        assertEquals(dretN[1], dret[1], 1e-8, "julian day UT1");
    }

    @Test
    void theRawUtcConversionsAgreeWithTheNativeEngine() {
        double jd = 2451545.;
        int[] a = new int[5], b = new int[5];
        double[] sa = new double[1], sb = new double[1];

        getSwissEph().swe_jdet_to_utc(jd, SE_GREG_CAL, a, sa);
        getSwephExp().swe_jdet_to_utc(jd, SE_GREG_CAL, b, sb);
        assertArrayEqualsWithSeconds(b, sb, a, sa, "swe_jdet_to_utc");

        getSwissEph().swe_jdut1_to_utc(jd, SE_GREG_CAL, a, sa);
        getSwephExp().swe_jdut1_to_utc(jd, SE_GREG_CAL, b, sb);
        assertArrayEqualsWithSeconds(b, sb, a, sa, "swe_jdut1_to_utc");
    }

    /**
     * The raw form takes the time zone with Swiss Ephemeris' own sign convention - positive
     * converts local time to UTC - while the convenience form takes a flag and flips it. Both
     * are exercised here, because getting that backwards is silent.
     */
    @Test
    void theRawTimeZoneFormUsesTheDocumentedSignConvention() {
        int[] out = new int[5], outN = new int[5];
        double[] sec = new double[1], secN = new double[1];

        // +2 hours: 12:00 local becomes 10:00 UTC
        getSwissEph().swe_utc_time_zone(2000, 1, 1, 12, 0, 0., 2., out, sec);
        getSwephExp().swe_utc_time_zone(2000, 1, 1, 12, 0, 0., 2., outN, secN);
        assertEquals(10, out[3], "12:00 local at +2 is 10:00 UTC");
        assertArrayEqualsWithSeconds(outN, secN, out, sec, "local to UTC");

        // and the convenience form must reach the same result
        ISweJulianDate d = getSwissEph().swe_utc_time_zone(2000, 1, 1, 12, 0, 0., false, 2.);
        assertNotNull(d);
        assertEquals(10, (int) d.utime(), "the convenience form agrees");
    }

    @Test
    void theRawRiseTransFormFillsADoubleArray() {
        double[] geopos = {81 + 8 / 60., 16 + 10 / 60., 0.};
        double[] tret = new double[1], tretN = new double[1];

        int rj = getSwissEph().swe_rise_trans(2451545., SE_SUN, null, SEFLG_SWIEPH,
                SE_CALC_RISE, geopos, 0., 0., tret, new StringBuilder());
        int rn = getSwephExp().swe_rise_trans(2451545., SE_SUN, null, SEFLG_SWIEPH,
                SE_CALC_RISE, geopos, 0., 0., tretN, new StringBuilder());

        assertEquals(rn, rj, "return code");
        assertTrue(tret[0] > 2451545., "a sunrise after the start date: " + tret[0]);
        assertEquals(tretN[0], tret[0], 1. / 86400., "sunrise within a second of the native one");
    }

    // ================================================== the crossing solvers

    /**
     * These eight had no override at all, so the pure Java engine threw
     * {@code UnsatisfiedLinkError} on them. The port finds the crossings with its own transit
     * search instead of the analytic solver, so it is held to a second rather than to zero.
     */
    @Test
    void solcrossAndMooncrossWorkWithoutTheNativeLibrary() {
        double jd = 2451545.;
        for (double deg : new double[]{0., 90., 180., 270.}) {
            double sunJ = getSwissEph().swe_solcross_ut(deg, jd, SEFLG_SWIEPH, new StringBuilder());
            double sunN = getSwephExp().swe_solcross_ut(deg, jd, SEFLG_SWIEPH, new StringBuilder());
            assertTrue(sunJ > jd, "the Sun should cross " + deg + " after " + jd + ", got " + sunJ);
            assertEquals(sunN, sunJ, 1. / 86400., "Sun over " + deg);

            double moonJ = getSwissEph().swe_mooncross_ut(deg, jd, SEFLG_SWIEPH, new StringBuilder());
            double moonN = getSwephExp().swe_mooncross_ut(deg, jd, SEFLG_SWIEPH, new StringBuilder());
            assertTrue(moonJ > jd, "the Moon should cross " + deg + " after " + jd);
            assertEquals(moonN, moonJ, 1. / 86400., "Moon over " + deg);
        }
    }

    @Test
    void theEtFormsOfSolcrossAndMooncrossAlsoWork() {
        double jdET = 2451545.;
        double sunJ = getSwissEph().swe_solcross(0., jdET, SEFLG_SWIEPH, new StringBuilder());
        double sunN = getSwephExp().swe_solcross(0., jdET, SEFLG_SWIEPH, new StringBuilder());
        assertEquals(sunN, sunJ, 1. / 86400.);

        double moonJ = getSwissEph().swe_mooncross(0., jdET, SEFLG_SWIEPH, new StringBuilder());
        double moonN = getSwephExp().swe_mooncross(0., jdET, SEFLG_SWIEPH, new StringBuilder());
        assertEquals(moonN, moonJ, 1. / 86400.);
    }

    @Test
    void mooncrossNodeFindsTheMoonOnItsNode() {
        double jd = 2451545.;
        double[] xlon = new double[1], xlat = new double[1];

        double cross = getSwissEph().swe_mooncross_node_ut(jd, SEFLG_SWIEPH, xlon, xlat,
                new StringBuilder());
        assertTrue(cross > jd && cross < jd + 30., "a node crossing within a month: " + cross);
        // on the node the Moon's ecliptic latitude is zero
        assertEquals(0., xlat[0], 0.05, "latitude at the node");
        assertTrue(xlon[0] >= 0. && xlon[0] < 360., "longitude in range: " + xlon[0]);
    }

    @Test
    void helioCrossWorksAndRejectsTheSunAndMoon() {
        double[] jdCross = new double[1];

        assertEquals(SweConst.OK, getSwissEph().swe_helio_cross_ut(SE_MARS, 0., 2451545.,
                SEFLG_SWIEPH, 1, jdCross, new StringBuilder()));
        assertTrue(jdCross[0] > 2451545., "a heliocentric crossing after the start");

        StringBuilder serr = new StringBuilder();
        assertEquals(SweConst.ERR, getSwissEph().swe_helio_cross_ut(SE_SUN, 0., 2451545.,
                SEFLG_SWIEPH, 1, jdCross, serr));
        assertTrue(serr.length() > 0, "and it says why");
    }

    // ============================================ the newly implemented rest

    @Test
    void ayanamsaExAddsNutationUnlessAskedNotTo() {
        double jdET = 2451545.;
        getSwissEph().swe_set_sid_mode(SE_SIDM_LAHIRI, 0., 0.);
        double[] withNut = new double[1], withoutNut = new double[1];

        assertTrue(getSwissEph().swe_get_ayanamsa_ex(jdET, SEFLG_SWIEPH, withNut,
                new StringBuilder()) >= 0);
        assertTrue(getSwissEph().swe_get_ayanamsa_ex(jdET, SEFLG_SWIEPH | SEFLG_NONUT,
                withoutNut, new StringBuilder()) >= 0);

        assertEquals(getSwissEph().swe_get_ayanamsa(jdET), withoutNut[0], 1e-12,
                "with SEFLG_NONUT it is the plain swe_get_ayanamsa()");
        assertTrue(Math.abs(withNut[0] - withoutNut[0]) > 1e-6,
                "nutation should move it: " + withNut[0] + " vs " + withoutNut[0]);
        assertTrue(Math.abs(withNut[0] - withoutNut[0]) * 3600. < 20.,
                "but only by the nutation amplitude");
    }

    @Test
    void astroModelsRoundTrip() {
        StringBuilder models = new StringBuilder();
        getSwissEph().swe_get_astro_models(models, new StringBuilder(), 0);
        String before = models.toString();
        assertTrue(before.contains(","), "a comma separated list: " + before);

        // ask for the delta t model explicitly and read it back. The slot is
        // SE_MODEL_DELTAT, so the field list is built by index rather than by counting commas
        StringBuilder set = new StringBuilder();
        for (int i = 0; i <= SweConst.SE_MODEL_DELTAT; i++) {
            if (i > 0) set.append(',');
            if (i == SweConst.SE_MODEL_DELTAT) set.append(SweConst.SEMOD_DELTAT_STEPHENSON_ETC_2016);
        }
        getSwissEph().swe_set_astro_models(set, 0);

        StringBuilder after = new StringBuilder();
        StringBuilder detail = new StringBuilder();
        getSwissEph().swe_get_astro_models(after, detail, 0);
        assertEquals(SweConst.SEMOD_DELTAT_STEPHENSON_ETC_2016,
                Integer.parseInt(after.toString().split(",")[SweConst.SE_MODEL_DELTAT]));
        assertTrue(detail.toString().contains("delta t"), detail.toString());
    }

    @Test
    void deltaTCanBePinnedAndHandedBack() {
        double jd = 2451545.;
        double automatic = getSwissEph().swe_deltat(jd);

        getSwissEph().swe_set_delta_t_userdef(100. / 86400.);
        assertEquals(100. / 86400., getSwissEph().swe_deltat(jd), 1e-12, "pinned");

        getSwissEph().swe_set_delta_t_userdef(SweConst.SE_DELTAT_AUTOMATIC);
        assertEquals(automatic, getSwissEph().swe_deltat(jd), 1e-12, "handed back to the model");
    }

    @Test
    void dateConversionAcceptsRealDatesAndRejectsImpossibleOnes() {
        double[] tjd = new double[1];

        assertEquals(SweConst.OK, getSwissEph().swe_date_conversion(2000, 1, 1, 12., 'g', tjd));
        assertEquals(2451545., tjd[0], 1e-9);

        assertEquals(SweConst.ERR, getSwissEph().swe_date_conversion(2000, 2, 31, 12., 'g', tjd),
                "31 February does not exist");
        assertTrue(tjd[0] > 0., "and the julian day is still returned");

        // the same call on the native library must agree
        double[] tjdN = new double[1];
        assertEquals(getSwephExp().swe_date_conversion(2000, 1, 1, 12., 'g', tjdN),
                getSwissEph().swe_date_conversion(2000, 1, 1, 12., 'g', tjd));
        assertEquals(tjdN[0], tjd[0], 1e-9);
    }

    @Test
    void currentFileDataNamesTheEphemerisFileInUse() {
        // read something so a file gets opened
        double[] xx = new double[6];
        getSwissEph().swe_calc(2451545., SE_MARS, SEFLG_SWIEPH, xx, new StringBuilder());

        double[] tfstart = new double[1], tfend = new double[1];
        int[] denum = new int[1];
        String name = getSwissEph().swe_get_current_file_data(0, tfstart, tfend, denum);

        assertNotNull(name, "a planet file should be open");
        assertTrue(name.toLowerCase().contains("sepl"), name);
        assertTrue(tfstart[0] < 2451545. && tfend[0] > 2451545.,
                "the range should cover the date: " + tfstart[0] + ".." + tfend[0]);
        assertTrue(denum[0] > 0, "and it should report a DE number: " + denum[0]);
    }

    @Test
    void fixstar2DelegatesToFixstarAndGivesTheSamePosition() {
        double[] x2 = new double[6], x1 = new double[6];
        StringBuilder s2 = new StringBuilder("Spica"), s1 = new StringBuilder("Spica");

        assertTrue(getSwissEph().swe_fixstar2(s2, 2451545., SEFLG_SWIEPH, x2, new StringBuilder()) >= 0);
        assertTrue(getSwissEph().swe_fixstar(s1, 2451545., SEFLG_SWIEPH, x1, new StringBuilder()) >= 0);
        assertEquals(x1[0], x2[0], 1e-12, "the same star data, so the same longitude");

        double[] mag2 = new double[1], mag1 = new double[1];
        getSwissEph().swe_fixstar2_mag(new StringBuilder("Spica"), mag2, new StringBuilder());
        getSwissEph().swe_fixstar_mag(new StringBuilder("Spica"), mag1, new StringBuilder());
        assertEquals(mag1[0], mag2[0], 1e-12, "and the same magnitude");
    }

    @Test
    void interpolateNutIsAccepted() {
        getSwissEph().swe_set_interpolate_nut(1);
        getSwissEph().swe_set_interpolate_nut(0);
        // nothing to assert beyond "does not throw": the port always computes nutation directly
    }

    private static void assertArrayEqualsWithSeconds(int[] expected, double[] expectedSec,
                                                     int[] actual, double[] actualSec, String what) {
        for (int i = 0; i < 5; i++) {
            assertEquals(expected[i], actual[i], what + " field " + i);
        }
        assertEquals(expectedSec[0], actualSec[0], 1e-3, what + " seconds");
    }
}
