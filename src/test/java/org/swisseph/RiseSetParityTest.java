/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-11
 */
package org.swisseph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import swisseph.DblObj;
import swisseph.SweConst;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static swisseph.SweConst.*;

/**
 * {@code swe_rise_trans()} and {@code swe_rise_trans_true_hor()}, pure Java against native: return
 * value, {@code serr}, the time (left alone where C leaves it alone) and the name of the star.
 * <p>
 * Until 2026-11-24 the Java engine had no {@code rise_set_fast()}, which C takes for the Sun up to
 * 65 degrees of latitude and for the Moon, the planets and the nodes up to 60, did not know
 * {@code SE_BIT_GEOCTR_NO_ECL_LAT} (a Hindu rising was a topocentric one, off by minutes) nor the
 * horizon at the dip ({@code horhgt} -100).
 * <p>
 * The same bit for bit, except for the true node and the osculating apogee, whose orbit has
 * numerical noise, and for some ulps of the Julian day with stars (native itself gives two values
 * for two calls in turn).
 * <p>
 * {@code @Isolated}: the tidal acceleration is JVM-global in the Java engine.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
@Isolated
public class RiseSetParityTest extends RiseSetParityTestBase {
    /** the two methods differ: sunrise by up to 0.5 s at 60 degrees, by up to 20 s at 65 */
    @Test
    void theFastMethod_isNotTheSlowOne() {
        int other = 0, all = 0;
        for (double lat : new double[]{59.9, 64.9}) {
            for (double t = 2451545; t < 2451545 + 365; t += 7) {
                final double[] geo = {18, lat, 0}, fast = new double[1], slow = new double[1];
                getSwissEph().swe_rise_trans(t, SE_SUN, null, SEFLG_SWIEPH, SE_CALC_RISE, geo, 1013.25, 10, fast, null);
                getSwissEph().swe_rise_trans(t, SE_SUN, null, SEFLG_SWIEPH, SE_CALC_RISE | SE_BIT_FORCE_SLOW_METHOD, geo, 1013.25, 10, slow, null);
                all++;
                if (Math.abs(fast[0] - slow[0]) > 0.01 / 86400 && Math.abs(fast[0] - slow[0]) < 0.01) other++;
            }
        }
        assertTrue(other > 20, other + " of " + all);
    }

    /** the bodies after the nodes are slow ones at every latitude, without being asked for */
    @Test
    void theBodiesAfterTheNodes_matchNative() {
        int i = 0;
        for (double lat : new double[]{0, 28.6, -33, 50.45, 59.9, -60}) {
            for (int ipl : new int[]{SE_MEAN_APOG, SE_OSCU_APOG, SE_CHIRON, SE_CERES, SE_AST_OFFSET + 134340}) {
                for (int flag : FAST_FLAGS) {
                    for (int ev : new int[]{SE_CALC_RISE, SE_CALC_SET}) {
                        i++;
                        rise(DATES[i % 2] + i % 400 * 0.913, ipl, null, SEFLG_SWIEPH, ev | flag, place(i, lat), 1013.25, 10, true);
                    }
                }
            }
        }
    }

    /**
     * A body whose semi-diurnal arc is none or the whole circle: up to 60 degrees only one of more than 30
     * degrees of declination, Pluto around -2900 and the Moon seen from 60 degrees.
     */
    @Test
    void circumpolarBodies_ofTheFastMethod_matchNative() {
        int never = 0, always = 0;
        for (int ipl : new int[]{SE_MOON, SE_PLUTO}) {
            for (double t = 626000.3; t < 720000; t += SE_MOON == ipl ? 0.97 : 911.1) {
                if (SE_MOON == ipl && t > 633500) break;
                for (double lat : new double[]{60, -60}) {
                    final double[] geo = {18, lat, 0}, xx = new double[6];
                    getSwephExp().swe_set_topo(geo[0], geo[1], geo[2]);
                    getSwephExp().swe_calc_ut(t, ipl, SEFLG_MOSEPH | SEFLG_EQUATORIAL | SEFLG_TOPOCTR, xx, null);
                    if (Math.abs(xx[1]) < 30) continue;
                    if (xx[1] * lat < 0) never++;
                    else always++;
                    for (int ev : new int[]{SE_CALC_RISE, SE_CALC_SET}) {
                        for (int flag : new int[]{0, SE_BIT_DISC_BOTTOM | SE_BIT_NO_REFRACTION}) {
                            rise(t, ipl, null, SEFLG_MOSEPH, ev | flag, geo, 1013.25, 10, true);
                        }
                    }
                }
            }
        }
        assertTrue(never > 20 && always > 10, never + " below the horizon, " + always + " above it");
    }

    @Test
    void transits_matchNative() {
        int i = 0;
        for (double lat : new double[]{0, 50.45, -62, 68, 85}) {
            for (int ipl = SE_SUN; ipl <= SE_CHIRON; ipl++) {
                for (int ev : new int[]{SE_CALC_MTRANSIT, SE_CALC_ITRANSIT}) {
                    for (int flag : new int[]{0, SE_BIT_HINDU_RISING, SE_BIT_FORCE_SLOW_METHOD, SE_BIT_GEOCTR_NO_ECL_LAT}) {
                        i++;
                        rise(DATES[i % (SE_CHIRON == ipl ? 4 : DATES.length)] + i * 0.913, ipl, null,
                                SE_CHIRON == ipl ? SEFLG_SWIEPH : EPHEMERIDES[i % 2], ev | flag, place(i, lat), 1013.25, 10, 0 != i % 6);
                    }
                }
            }
        }
    }

    /** a star is always searched in the slow way, and its name is written back */
    @Test
    void stars_matchNative() {
        int i = 0, never = 0, found = 0;
        for (double lat : LATITUDES) {
            for (String star : STARS) {
                for (int ev : new int[]{SE_CALC_RISE, SE_CALC_SET, SE_CALC_MTRANSIT, SE_CALC_ITRANSIT}) {
                    for (int flag : new int[]{0, SE_BIT_DISC_CENTER | SE_BIT_NO_REFRACTION, SE_BIT_HINDU_RISING, SE_BIT_GEOCTR_NO_ECL_LAT,
                            SE_BIT_CIVIL_TWILIGHT}) {
                        i++;
                        final double[] r = rise(DATES[i % DATES.length] + i * 0.913, 0 == i % 2 ? SE_SUN : SE_MOON, star, EPHEMERIDES[i % 2],
                                ev | flag, place(i, lat), 1013.25, 10, true);
                        if (CIRCUMPOLAR == r[0]) never++;
                        if (0 == r[0]) found++;
                    }
                }
            }
        }
        assertTrue(found > 1000 && never > 200, found + " events, " + never + " circumpolar");
        // no star, the planet
        assertEquals(OK, rise(2451545.3, SE_VENUS, "", SEFLG_SWIEPH, SE_CALC_RISE, place(1, 50), 1013.25, 10, true)[0]);
        for (int ev : new int[]{SE_CALC_RISE, SE_CALC_MTRANSIT}) {
            assertEquals(ERR, rise(2451545.3, SE_VENUS, "nosuchstar", SEFLG_SWIEPH, ev, place(1, 50), 1013.25, 10, true)[0]);
        }
    }

    /** without an event it is the rising, in the slow way; both of them at once; both transits; unknown bits */
    @Test
    void otherEvents_matchNative() {
        for (double lat : new double[]{50, 62, 68}) {
            for (int ev : new int[]{0, SE_CALC_RISE | SE_CALC_SET, SE_CALC_MTRANSIT | SE_CALC_ITRANSIT, SE_CALC_RISE | SE_CALC_MTRANSIT,
                    SE_CALC_SET | SE_CALC_ITRANSIT, 1 << 20, SE_CALC_RISE | 1 << 20, SE_CALC_RISE | SE_BIT_DISC_CENTER | SE_BIT_DISC_BOTTOM}) {
                for (int ipl : new int[]{SE_SUN, SE_MOON, SE_MARS}) {
                    for (double h = 0; h < 1; h += 0.21) {
                        rise(2455197.3 + h, ipl, null, SEFLG_SWIEPH, ev, place(1, lat), 1013.25, 10, true);
                        horizon(2455197.3 + h, ipl, null, SEFLG_SWIEPH, ev, place(1, lat), 1013.25, 10, 0);
                    }
                }
            }
        }
    }

    /** the ephemeris flag may have others with it; the fast method takes the ephemeris only, the slow one two more */
    @Test
    void otherFlags_matchNative() {
        for (int fl : new int[]{0, SEFLG_SPEED, SEFLG_SIDEREAL, SEFLG_NONUT, SEFLG_TRUEPOS, SEFLG_HELCTR, SEFLG_SWIEPH | SEFLG_NONUT,
                SEFLG_MOSEPH | SEFLG_TRUEPOS, SEFLG_SWIEPH | SEFLG_RADIANS | SEFLG_XYZ}) {
            for (double lat : new double[]{40, 63, 70}) {
                for (int ev : new int[]{SE_CALC_RISE, SE_CALC_SET, SE_CALC_MTRANSIT}) {
                    for (int ipl : new int[]{SE_SUN, SE_MOON, SE_JUPITER}) {
                        rise(2455197.3, ipl, null, fl, ev, place(1, lat), 1013.25, 10, true);
                        rise(2455197.3, ipl, null, fl, ev | SE_BIT_GEOCTR_NO_ECL_LAT, place(1, lat), 1013.25, 10, true);
                    }
                }
            }
        }
    }

    /** the fast method does not look at the height of the place, the slow one refuses it and leaves the time alone */
    @Test
    void heights_matchNative() {
        for (double height : new double[]{-501, -500, -499, 0, 24999, 25000, 25001}) {
            for (int ev : new int[]{SE_CALC_RISE, SE_CALC_SET, SE_CALC_MTRANSIT, SE_CALC_ITRANSIT, SE_CALC_RISE | SE_BIT_HINDU_RISING}) {
                for (double lat : new double[]{50, 63, 70}) {
                    for (int ipl : new int[]{SE_SUN, SE_MOON, SE_VENUS}) {
                        final double[] geo = {12.5, lat, height};
                        for (boolean withSerr : new boolean[]{true, false}) {
                            rise(2455197.3, ipl, null, SEFLG_SWIEPH, ev, geo, 1013.25, 10, withSerr);
                        }
                        final double[] r = horizon(2455197.3, ipl, null, SEFLG_SWIEPH, ev, geo, 1013.25, 10, 0);
                        if (height > 25000 || height < -500) {
                            assertEquals(ERR, r[0]);
                            assertEquals(UNTOUCHED, r[1]);
                        }
                    }
                }
            }
        }
        final DblObj box = new DblObj();
        box.val = UNTOUCHED;
        final double[] geo = {12.5, 70, 30000};
        for (ISwissEph e : new ISwissEph[]{getSwephExp(), getSwissEph()}) {
            assertEquals(ERR, e.swe_rise_trans(2455197.3, SE_SUN, null, SEFLG_SWIEPH, SE_CALC_RISE, geo, 0, 0, box, null));
            assertEquals(UNTOUCHED, box.val);
        }
    }

    /** no position, so no event: the text is the one of swe_calc() */
    @Test
    void refusals_matchNative() {
        int refused = 0;
        for (double lat : new double[]{50, 63, 70}) {
            for (int ev : new int[]{SE_CALC_RISE, SE_CALC_SET, SE_CALC_MTRANSIT}) {
                for (int ipl : new int[]{SE_SUN, SE_MOON, SE_VENUS, SE_PLUTO, SE_MEAN_NODE, SE_TRUE_NODE, SE_CHIRON, SE_AST_OFFSET + 999999}) {
                    for (double t : new double[]{-5e6, 1e7, 2451545.3}) {
                        for (int eph : EPHEMERIDES) {
                            if (ERR == rise(t, ipl, null, eph, ev, place(1, lat), 1013.25, 10, true)[0]) refused++;
                        }
                    }
                }
            }
        }
        assertTrue(refused > 100, refused + " refused");
    }

    /** what is not a number gives what it gives in C, and so does a place which is not one */
    @Test
    void notANumber_matchesNative() {
        final double nan = Double.NaN, inf = Double.POSITIVE_INFINITY;
        for (int ev : new int[]{SE_CALC_RISE, SE_CALC_SET | SE_BIT_HINDU_RISING}) {
            for (int ipl : new int[]{SE_SUN, SE_MOON, SE_VENUS}) {
                for (double x : new double[]{nan, inf, -inf}) {
                    for (double[] geo : new double[][]{{x, 50, 100}, {12.5, x, 100}, {12.5, 50, x}}) {
                        rise(2451545.3, ipl, null, SEFLG_SWIEPH, ev, geo, 1013.25, 10, true);
                    }
                    rise(2451545.3, ipl, null, SEFLG_SWIEPH, ev, place(1, 50), x, 10, true);
                    rise(2451545.3, ipl, null, SEFLG_SWIEPH, ev, place(1, 50), 1013.25, x, true);
                }
            }
        }
        for (int ipl : new int[]{SE_SUN, SE_MOON, SE_VENUS}) {
            for (double lon : new double[]{0, 200, -400, 1000}) {
                for (double lat : new double[]{45, 90, -90, 91, 120, -180, 400}) {
                    rise(2451545.3, ipl, null, SEFLG_SWIEPH, SE_CALC_RISE, new double[]{lon, lat, 0}, 1013.25, 10, true);
                }
            }
        }
    }

    /** the lapse rate is the one which swe_set_lapse_rate() has set; the place is the one of the last call */
    @Test
    void whatOtherCallsSet_matchesNative() {
        final ISwissEph[] engines = {getSwephExp(), getSwissEph()};
        try {
            for (double rate : new double[]{0.0065, 0.01, 0}) {
                for (ISwissEph e : engines) e.swe_set_lapse_rate(rate);
                for (double lat : new double[]{30, 62, 70}) {
                    for (int ipl : new int[]{SE_SUN, SE_MOON}) {
                        rise(2451600.3, ipl, null, SEFLG_SWIEPH, SE_CALC_RISE, place(2, lat), 1013.25, 10, true);
                        horizon(2451600.3, ipl, null, SEFLG_SWIEPH, SE_CALC_RISE, place(2, lat), 1013.25, 10, -100);
                    }
                }
            }
        } finally {
            for (ISwissEph e : engines) e.swe_set_lapse_rate(0.0065);
        }
        for (int ev : new int[]{SE_CALC_RISE, SE_CALC_SET, SE_CALC_MTRANSIT}) {
            for (int flag : new int[]{0, SE_BIT_HINDU_RISING}) {
                final double[][] moon = new double[2][6];
                for (int i = 0; i < 2; i++) engines[i].swe_set_topo(100, -40, 4000);
                rise(2451600.3, SE_MOON, null, SEFLG_SWIEPH, ev | flag, place(1, 50), 1013.25, 10, true);
                for (int i = 0; i < 2; i++) {
                    engines[i].swe_calc_ut(2451600.3, SE_MOON, SEFLG_SWIEPH | SEFLG_TOPOCTR, moon[i], null);
                }
                for (int k = 0; k < 3; k++) assertEquals(moon[0][k], moon[1][k], 1e-9, "topocentric moon after " + ev + " " + flag);
            }
        }
    }

    /**
     * Positions are kept, and another place is the end of them: of the planets and of the bodies of
     * higher numbers, which share one save area. The same place is not; a warning is not repeated.
     */
    @Test
    void anotherPlace_matchesNative() {
        final ISwissEph[] engines = {getSwephExp(), getSwissEph()};
        for (int ipl : new int[]{SE_SUN, SE_MOON, SE_MARS, SE_CUPIDO, SE_WALDEMATH, SE_AST_OFFSET + 134340}) {
            for (double t : new double[]{2451545.25, 626000.25}) {
                // the text of swe_calc() for a fictitious body without a file is another one (2026-11)
                if (ipl >= SE_FICT_OFFSET && ipl <= SE_FICT_MAX && t < 1e6) continue;
                final double[] first = new double[6];
                for (int k = 0; k < 6; k++) {
                    final double[][] xx = new double[2][6];
                    final int[] ret = new int[2];
                    final StringBuilder[] errors = {new StringBuilder(), new StringBuilder()};
                    for (int i = 0; i < 2; i++) {
                        if (k != 3) engines[i].swe_set_topo(0 == k / 2 % 2 ? 10 : 100, 0 == k / 2 % 2 ? 60 : -60, 50);
                        ret[i] = engines[i].swe_calc(t, ipl, SEFLG_SWIEPH | SEFLG_EQUATORIAL | SEFLG_TOPOCTR, xx[i], errors[i]);
                    }
                    final String what = "ipl " + ipl + " jd " + t + " call " + k;
                    assertEquals(ret[0], ret[1], what);
                    assertEquals(errors[0].toString(), errors[1].toString(), what);
                    for (int c = 0; c < 3; c++) assertEquals(xx[0][c], xx[1][c], 1e-10, what + " [" + c + "]");
                    if (0 == k) System.arraycopy(xx[0], 0, first, 0, 6);
                    if (2 == k) assertTrue(first[0] != xx[0][0], what + ": the place is another one");
                }
                for (int ev : new int[]{SE_CALC_RISE, SE_CALC_MTRANSIT}) {
                    for (double lon : new double[]{10, 100, 10}) {
                        rise(t, ipl, null, SEFLG_SWIEPH, ev, new double[]{lon, 50, 0}, 1013.25, 10, true);
                    }
                }
            }
        }
    }

    /** the dip of the horizon, and the apparent altitude below which there is no true one */
    @Test
    void refraction_matchesNative() {
        int below = 0;
        for (double geoalt : new double[]{0, 0.5, 100, 1500, 8000}) {
            for (double press : new double[]{0, 600, 1013.25}) {
                for (double temp : new double[]{-20, 0, 25}) {
                    for (double rate : new double[]{0.0065, 0.01, 0}) {
                        for (int dir : new int[]{SE_TRUE_TO_APP, SE_APP_TO_TRUE}) {
                            final double[] dip = new double[4];
                            getSwephExp().swe_refrac_extended(1, geoalt, press, temp, rate, SE_TRUE_TO_APP, dip);
                            for (double alt : new double[]{-12, -5, -1, -0.3, 0, 1e-6, 0.3, 5, 14, 16, 89, 90, 95, dip[3], Math.nextUp(dip[3]),
                                    Math.nextDown(dip[3]), dip[3] + 0.2, dip[3] - 0.2}) {
                                final double[] a = {7, 7, 7, 7}, b = {7, 7, 7, 7};
                                final String what = alt + " " + geoalt + " " + press + " " + temp + " " + rate + " " + dir;
                                assertEquals(getSwephExp().swe_refrac_extended(alt, geoalt, press, temp, rate, dir, a),
                                        getSwissEph().swe_refrac_extended(alt, geoalt, press, temp, rate, dir, b), 1e-12, what);
                                for (int k = 0; k < 4; k++) assertEquals(a[k], b[k], 1e-12, what + " [" + k + "]");
                                if (alt < a[3]) below++;
                            }
                        }
                    }
                }
            }
        }
        assertTrue(below > 100, below + " below the dip");
    }

    /** four of the methods of the sectors are made of risings and settings */
    @Test
    void gauquelinSectors_matchNative() {
        int i = 0;
        for (int method = 0; method <= 5; method++) {
            for (double lat : new double[]{0, 50, -59, 62, 70}) {
                for (int ipl : new int[]{SE_SUN, SE_MOON, SE_MARS, SE_SATURN}) {
                    i++;
                    final int imeth = method;
                    final double t = 2451545.3 + i * 3.7;
                    compare("gauquelin_sector method " + method + " ipl " + ipl + " jd " + t, null, place(i, lat), true, 1e-9,
                            (e, n, g, r, s) -> e.swe_gauquelin_sector(t, ipl, new StringBuilder(), SEFLG_SWIEPH, imeth, g, 1013.25, 10, r, s));
                }
            }
        }
    }

    /** the kinds of the events and their bits are those of swephexp.h */
    @Test
    void constants_areThoseOfSwephexp() throws Exception {
        final String home = System.getenv("SWISSEPH_HOME");
        assumeTrue(null != home, "SWISSEPH_HOME is not set");
        final Path header = Paths.get(home, "swephexp.h");
        assumeTrue(Files.isRegularFile(header), "no swephexp.h in $SWISSEPH_HOME");
        final Matcher m = Pattern.compile("#define\\s+(SE_(?:CALC_(?:RISE|SET|[MI]TRANSIT)|BIT_\\w+))\\s+(?:\\(1\\s*<<\\s*(\\d+)\\)|(\\d+))")
                .matcher(new String(Files.readAllBytes(header), StandardCharsets.ISO_8859_1));
        int n = 0;
        while (m.find()) {
            final int value = null != m.group(2) ? 1 << Integer.parseInt(m.group(2)) : Integer.parseInt(m.group(3));
            assertEquals(value, SweConst.class.getField(m.group(1)).getInt(null), m.group(1));
            n++;
        }
        assertTrue(n >= 12, n + " constants found");
        assertEquals(SE_BIT_DISC_CENTER | SE_BIT_NO_REFRACTION | SE_BIT_GEOCTR_NO_ECL_LAT, SE_BIT_HINDU_RISING);
    }
}
