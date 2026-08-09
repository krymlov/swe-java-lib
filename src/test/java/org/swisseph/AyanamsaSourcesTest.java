/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.junit.jupiter.api.Test;
import org.swisseph.api.ISweAyanamsa;
import org.swisseph.api.ISweJulianDate;
import org.swisseph.app.SweAyanamsa;
import org.swisseph.app.SweJulianDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static swisseph.SweConst.SEFLG_NONUT;
import static swisseph.SweConst.SEFLG_SWIEPH;
import static swisseph.SweConst.SEFLG_TRUEPOS;

/**
 * Is the ayanamsa the same number everywhere, for the same input?
 * <p>
 * Four sources are compared on one fixture - 4 April, 17:50:40, time zone 5:30 East,
 * Machilipatnam - over seventeen epochs from year 0 to 2100:
 *
 * <ol>
 * <li><b>swetest64.exe</b>, the Swiss Ephemeris reference program;</li>
 * <li><b>the native library</b> through JNI ({@code org.swisseph.SwephNative});</li>
 * <li><b>the pure Java port</b> ({@code swisseph.SwissEph});</li>
 * <li><b>Jagannatha Hora 8 uk</b>, from the dumps in
 * {@code jyotisa-uajhora/etc/v8.0/diff/2017en}.</li>
 * </ol>
 *
 * The short answer is <b>no</b>, and the three differences have three different causes:
 * <ul>
 * <li>swetest and the native library are the same code, so they agree to the last printed
 *     digit;</li>
 * <li>the pure Java port is Swiss Ephemeris 2.01 and differs by roughly the nutation
 *     amplitude, 17", for the arithmetic ayanamsas and by 37" for True Chitrapaksha, which
 *     also carries the older star data;</li>
 * <li>Jagannatha Hora differs by a <b>constant</b> -20.1" at every epoch, measured in the
 *     mean frame. A constant offset over 2100 years is a different definition of the
 *     reference point, not a modelling difference.</li>
 * </ul>
 *
 * There is also a fourth trap that has nothing to do with the engine: the same ayanamsa has
 * three legitimate values at one instant depending on the frame asked for - apparent, true
 * position, and no nutation. For a star-based ayanamsa those differ by tens of arc seconds.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
public class AyanamsaSourcesTest extends AbstractTest {

    static final int[] YEARS = {0, 100, 500, 1000, 1500, 1700, 1800, 1900, 1970, 1990,
            2000, 2010, 2030, 2050, 2070, 2090, 2100};

    static final double GEO_LON = 81 + 8 / 60., GEO_LAT = 16 + 10 / 60.;
    static final float TIME_ZONE = 5.5f;
    static final double LOCAL_TIME = 17 + 50 / 60. + 40 / 3600.;

    /**
     * What Jagannatha Hora 8 uk prints as "Ayanamsa:" for each epoch of this fixture, in
     * degrees. Taken from the dumps, converted from its d-m-s form, and expressed as a signed
     * angle so the two pre-500 values are negative rather than near 360.
     */
    static final double[] JHORA = {
            -(3 + 56 / 60. + 49.24 / 3600.),    //    0   (356-03-10.76)
            -(2 + 33 / 60. + 47.16 / 3600.),    //  100   (357-26-12.84)
            2 + 58 / 60. + 42.40 / 3600.,       //  500
            9 + 55 / 60. + 7.54 / 3600.,        // 1000
            16 + 52 / 60. + 27.10 / 3600.,      // 1500
            19 + 39 / 60. + 38.39 / 3600.,      // 1700
            21 + 3 / 60. + 17.28 / 3600.,       // 1800
            22 + 26 / 60. + 58.38 / 3600.,      // 1900
            23 + 25 / 60. + 34.50 / 3600.,      // 1970
            23 + 42 / 60. + 19.32 / 3600.,      // 1990
            23 + 50 / 60. + 41.83 / 3600.,      // 2000
            23 + 59 / 60. + 4.23 / 3600.,       // 2010
            24 + 15 / 60. + 49.23 / 3600.,      // 2030
            24 + 32 / 60. + 34.32 / 3600.,      // 2050
            24 + 49 / 60. + 19.49 / 3600.,      // 2070
            25 + 6 / 60. + 4.76 / 3600.,        // 2090
            25 + 14 / 60. + 27.35 / 3600.,      // 2100
    };

    /** the nine ayanamsas etc/difference.txt reports on */
    static final ISweAyanamsa[] AYANAMSAS = {SweAyanamsa.FAGAN_BRADLEY, SweAyanamsa.LAHIRI,
            SweAyanamsa.BV_RAMAN, SweAyanamsa.KRISHNAMURTI, SweAyanamsa.SHRI_YUKTESHWAR,
            SweAyanamsa.SASSANIAN, SweAyanamsa.SURYA_SIDDHANTA, SweAyanamsa.ARYABHATA,
            SweAyanamsa.TRUE_CITRA};

    private double jdET(ISwissEph swe, int year) {
        final ISweJulianDate d = swe.getJulianDate(
                new int[]{year, 4, 4, 17, 50}, TIME_ZONE, LOCAL_TIME);
        return d.julianDay() + swe.swe_deltat(d.julianDay());
    }

    /** the ayanamsa in whatever frame the flags ask for, as a signed angle */
    private double ayanamsa(ISwissEph swe, ISweAyanamsa aya, int year, int flags) {
        swe.swe_set_sid_mode(aya.fid(), 0., 0.);
        final double[] daya = new double[1];
        swe.swe_get_ayanamsa_ex(jdET(swe, year), flags, daya, new StringBuilder());
        return daya[0] > 180. ? daya[0] - 360. : daya[0];
    }

    // ================================================ 1. swetest vs the native library

    /**
     * The reference program and the JNI wrapper run the same code, so they must agree to the
     * last digit swetest prints - seven decimals of a degree, i.e. 0.00036".
     */
    @Test
    void swetestAndTheNativeLibraryAreTheSameNumber() {
        assumeTrue(Swetest.available(), "swetest64.exe or ephe/ is missing");
        final List<String> bad = new ArrayList<>();

        for (ISweAyanamsa aya : AYANAMSAS) {
            for (int year : YEARS) {
                final Map<String, Double> ref = Swetest.values(new int[]{year, 4, 4},
                        "12:20:40", "-p0", "-true", "-sid" + aya.fid(), "-fPl");
                final Double swetest = ref.get("ayanamsa");
                if (null == swetest) continue;

                final double jni = ayanamsa(getSwephExp(), aya,
                        year, SEFLG_SWIEPH | SEFLG_TRUEPOS);
                final double d = Math.abs(swetest - (jni < 0 ? jni + 360. : jni)) * 3600.;
                if (d > 0.001) {
                    bad.add(String.format(Locale.ROOT, "%s %d: swetest %.7f, JNI %.7f (%.4f\")",
                            aya.name(), year, swetest, jni, d));
                }
            }
        }
        assertTrue(bad.isEmpty(), bad.size() + " disagreements: "
                + bad.subList(0, Math.min(4, bad.size())));
    }

    // ============================================ 2. the native library vs the pure Java port

    /**
     * The port is Swiss Ephemeris 2.01, so it differs. What matters is that the difference
     * stays inside the nutation amplitude for the arithmetic ayanamsas, and inside twice that
     * for True Chitrapaksha, which also depends on the position computed for Spica.
     * <p>
     * Asked through {@code swe_get_ayanamsa_ex()} the two engines are closer than
     * etc/difference.txt reports - Fagan/Bradley agrees to 0.4" - because that document
     * measures what a built chart reports, which also carries the port's position and
     * sidereal-time differences. Both numbers are real; they answer different questions.
     */
    @Test
    void thePureJavaPortStaysWithinTheKnownBoundOfTheNativeLibrary() {
        for (ISweAyanamsa aya : AYANAMSAS) {
            double worst = 0;
            int worstYear = 0;
            for (int year : YEARS) {
                final double n = ayanamsa(getSwephExp(), aya, year, SEFLG_SWIEPH | SEFLG_TRUEPOS);
                final double j = ayanamsa(getSwissEph(), aya, year, SEFLG_SWIEPH | SEFLG_TRUEPOS);
                final double d = Math.abs(n - j) * 3600.;
                if (d > worst) { worst = d; worstYear = year; }
            }
            final double bound = aya == SweAyanamsa.TRUE_CITRA ? 40. : 20.;
            assertTrue(worst < bound, aya.name() + " worst " + worst + "\" at " + worstYear
                    + ", bound " + bound + "\"");
        }
    }

    // ================================================ 3. the three frames of one ayanamsa

    /**
     * The same ayanamsa at the same instant has three legitimate values, and mixing them up is
     * the most common way to get a number that does not match anybody:
     * <ul>
     * <li><b>apparent</b> - the default;</li>
     * <li><b>true position</b> - {@code SEFLG_TRUEPOS}, what this library reports because that
     *     is what the rest of the chart is built with;</li>
     * <li><b>no nutation</b> - {@code SEFLG_NONUT}, the mean frame.</li>
     * </ul>
     * For an arithmetic ayanamsa the first two coincide; for a star-based one they do not.
     */
    @Test
    void oneAyanamsaHasThreeValuesDependingOnTheFrameAskedFor() {
        final int year = 2000;

        // arithmetic: true position changes nothing, nutation does
        final double lahiriApparent = ayanamsa(getSwephExp(), SweAyanamsa.LAHIRI, year, SEFLG_SWIEPH);
        final double lahiriTrue = ayanamsa(getSwephExp(), SweAyanamsa.LAHIRI, year,
                SEFLG_SWIEPH | SEFLG_TRUEPOS);
        final double lahiriMean = ayanamsa(getSwephExp(), SweAyanamsa.LAHIRI, year,
                SEFLG_SWIEPH | SEFLG_NONUT);

        assertEquals(lahiriApparent, lahiriTrue, 1e-12,
                "Lahiri is arithmetic, so SEFLG_TRUEPOS makes no difference");
        assertTrue(Math.abs(lahiriApparent - lahiriMean) * 3600. > 5.,
                "but nutation does: " + (lahiriApparent - lahiriMean) * 3600. + "\"");

        // star based: all three differ
        final double citraApparent = ayanamsa(getSwephExp(), SweAyanamsa.TRUE_CITRA, year, SEFLG_SWIEPH);
        final double citraTrue = ayanamsa(getSwephExp(), SweAyanamsa.TRUE_CITRA, year,
                SEFLG_SWIEPH | SEFLG_TRUEPOS);
        final double citraMean = ayanamsa(getSwephExp(), SweAyanamsa.TRUE_CITRA, year,
                SEFLG_SWIEPH | SEFLG_NONUT);

        assertTrue(Math.abs(citraApparent - citraTrue) * 3600. > 5.,
                "True Chitrapaksha is derived from Spica's computed position, so SEFLG_TRUEPOS "
                        + "moves it: " + (citraApparent - citraTrue) * 3600. + "\"");
        assertTrue(Math.abs(citraApparent - citraMean) * 3600. > 1.,
                "and so does nutation");
    }

    // ==================================================== 4. Jagannatha Hora 8 uk

    /**
     * Jagannatha Hora is configured with {@code UserSelectedAyanamsa=17} and prints values that
     * match none of ours exactly. Measured in the <b>mean</b> frame against True Chitrapaksha
     * it is a flat offset of about -20.1" at every one of the seventeen epochs - the spread
     * over 2100 years is under an arc second.
     * <p>
     * A constant offset is not a modelling difference: it is a different Chitra reference
     * point. Anything comparing charts with Jagannatha Hora has to allow for it.
     */
    @Test
    void jagannathaHoraIsAConstantOffsetFromTrueChitrapaksha() {
        final List<Double> offsets = new ArrayList<>();

        for (int i = 0; i < YEARS.length; i++) {
            final double mean = ayanamsa(getSwephExp(), SweAyanamsa.TRUE_CITRA, YEARS[i],
                    SEFLG_SWIEPH | SEFLG_NONUT);
            offsets.add((JHORA[i] - mean) * 3600.);
        }

        double min = Double.MAX_VALUE, max = -Double.MAX_VALUE, sum = 0;
        for (double o : offsets) {
            min = Math.min(min, o);
            max = Math.max(max, o);
            sum += o;
        }
        final double mean = sum / offsets.size();

        assertEquals(-20.1, mean, 0.5, "the offset should be about -20.1 arc seconds");
        assertTrue(max - min < 2.5, "and it should be constant to within a couple of arc "
                + "seconds over 2100 years, spread was " + (max - min) + "\": " + offsets);
    }

    /**
     * The same measurement against the frames Jagannatha Hora is <i>not</i> using, so the
     * choice of the mean frame above is justified rather than fitted: against apparent or
     * true position the difference scatters instead of holding steady.
     */
    @Test
    void jagannathaHoraDoesNotMatchTheApparentOrTruePositionFrames() {
        double spreadApparent = spreadAgainst(SEFLG_SWIEPH);
        double spreadTrue = spreadAgainst(SEFLG_SWIEPH | SEFLG_TRUEPOS);
        double spreadMean = spreadAgainst(SEFLG_SWIEPH | SEFLG_NONUT);

        assertTrue(spreadMean < 2.5, "mean frame spread " + spreadMean + "\"");
        assertTrue(spreadApparent > 20., "apparent frame spread " + spreadApparent + "\"");
        assertTrue(spreadTrue > 20., "true position frame spread " + spreadTrue + "\"");
    }

    private double spreadAgainst(int flags) {
        double min = Double.MAX_VALUE, max = -Double.MAX_VALUE;
        for (int i = 0; i < YEARS.length; i++) {
            final double d = (JHORA[i]
                    - ayanamsa(getSwephExp(), SweAyanamsa.TRUE_CITRA, YEARS[i], flags)) * 3600.;
            min = Math.min(min, d);
            max = Math.max(max, d);
        }
        return max - min;
    }

    /**
     * And the practical consequence, stated as a number: how far apart a sidereal longitude
     * computed here and one printed by Jagannatha Hora can be. 20" is 0.006 degrees - well
     * inside a nakshatra pada, but not nothing if a planet sits on a boundary.
     */
    @Test
    void theJagannathaHoraOffsetIsSmallerThanAPada() {
        final double pada = 360. / 27. / 4.;                 // 3 degrees 20 minutes
        for (int i = 0; i < YEARS.length; i++) {
            final double mean = ayanamsa(getSwephExp(), SweAyanamsa.TRUE_CITRA, YEARS[i],
                    SEFLG_SWIEPH | SEFLG_NONUT);
            assertTrue(Math.abs(JHORA[i] - mean) < pada / 100.,
                    "year " + YEARS[i] + " differs by " + (JHORA[i] - mean) * 3600. + "\"");
        }
    }

    // ============================================ the chart-level view

    /**
     * What {@code ISweObjects.ayanamsa()} reports is the true-position value, because that is
     * what the chart around it is built with. This pins that, so the reported number and the
     * planets never come from different frames.
     */
    @Test
    void theChartReportsTheFrameItWasBuiltWith() {
        for (int year : new int[]{1976, 2000}) {
            final org.swisseph.api.ISweObjects chart = new org.swisseph.app.SweObjects(
                    getSwephExp(),
                    new SweJulianDate(new int[]{year, 4, 4, 17, 50}, TIME_ZONE, LOCAL_TIME),
                    new org.swisseph.app.SweGeoLocation(GEO_LON, GEO_LAT, 0.),
                    new org.swisseph.app.SweObjectsOptions.Builder()
                            .ayanamsa(SweAyanamsa.TRUE_CITRA)
                            .houseSystem(org.swisseph.app.SweHouseSystem.PLACIDUS).build())
                    .completeBuild();

            final double truePos = ayanamsa(getSwephExp(), SweAyanamsa.TRUE_CITRA, year,
                    SEFLG_SWIEPH | SEFLG_TRUEPOS);
            assertEquals(truePos < 0 ? truePos + 360. : truePos, chart.ayanamsa(), 1e-9,
                    "year " + year);
        }
    }
}
