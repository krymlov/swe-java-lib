/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-11
 */
package org.swisseph;

import org.junit.jupiter.api.Test;
import swisseph.SwissEph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.swisseph.api.ISweConstants.EPHE_PATH;
import static swisseph.SweConst.*;

/**
 * The functions that were in no two-engine test (WHERE_JAVA_PORT.md F2) and those covered only by a
 * few values (F3), swept: horizon coordinates and refraction, houses from the ARMC and house
 * positions of every system, calendar and time functions, coordinate transformations, sidereal
 * time, delta T, the degree splitter, names. Every difference goes to
 * {@code target/accuracy/UtilityParityTest.tsv}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
public class UtilityParityTest {

    static final String HSYS = "ABCDEFGHIiJKLMNOPQRSTUVWXY";
    static final double[] LATITUDES = {-89.9, -66.6, -60, -45, -23.4, -1, 0, 1, 23.4, 45, 50.45, 60, 66.6, 89.9};

    @Test
    void everyFunction_matchesNative() {
        final AccuracySurvey s = new AccuracySurvey("UtilityParityTest");
        AccuracySurvey.inFreshThread(() -> {
            try (ISwissEph cn = new SwephNative(EPHE_PATH); ISwissEph jv = new SwissEph(EPHE_PATH)) {
                horizon(s, cn, jv);
                houses(s, cn, jv);
                calendar(s, cn, jv);
                time(s, cn, jv);
                coordinates(s, cn, jv);
                degrees(s, cn, jv);
            }
            return null;
        });
        s.write();
        assertEquals("", s.retDiffList(), "return values");
        assertEquals("", s.serrDiffList(), "texts and names");
        // measured 2026-11-26: the cusp speeds of Alcabitius and APC near the poles are
        // differences of large numbers (3.5e-8); Krusinski house positions at the equator are
        // the accepted difference B3 of WHERE_JAVA_PORT.md (5.1e-5)
        final StringBuilder bad = new StringBuilder();
        s.forEachRow((k, r) -> {
            final double bound = k.endsWith("|cusp_speed") ? 1e-7 : "house_pos|U".equals(k) ? 1e-4 : 1e-9;
            if (!(r.max <= bound)) bad.append(k).append(" max ").append(r.max).append(" at ").append(r.where).append('\n');
        });
        assertEquals("", bad.toString(), "differences");
    }

    private static void horizon(AccuracySurvey s, ISwissEph cn, ISwissEph jv) {
        final double[] dates = AccuracySurvey.dates(2086307.5, 2561117.5, 25);
        for (double tjd : dates) {
            for (double lat : LATITUDES) {
                final double[] geo = {30.52 + lat, lat, 180};
                for (int flag : new int[]{SE_ECL2HOR, SE_EQU2HOR}) {
                    for (double lon = 0; lon < 360; lon += 37.3) {
                        final double[] xin = {lon, lat / 3, 1}, ac = new double[3], aj = new double[3];
                        cn.swe_azalt(tjd, flag, geo, 1013.25, 15, xin, ac);
                        jv.swe_azalt(tjd, flag, geo, 1013.25, 15, xin, aj);
                        final String w = "jd " + tjd + " geo " + lat + " flag " + flag + " x " + lon;
                        s.diff("azalt|az", AccuracySurvey.angle(ac[0], aj[0]), () -> w);
                        s.diff("azalt|alt", Math.abs(ac[1] - aj[1]), () -> w);
                        s.diff("azalt|appalt", Math.abs(ac[2] - aj[2]), () -> w);
                        final double[] hin = {lon, lat / 2}, rc = new double[3], rj = new double[3];
                        cn.swe_azalt_rev(tjd, flag, geo, hin, rc);
                        jv.swe_azalt_rev(tjd, flag, geo, hin, rj);
                        // at a pole of the target system the longitude is undefined
                        if (Math.abs(rc[1]) < 89.999) s.diff("azalt_rev|x0", AccuracySurvey.angle(rc[0], rj[0]), () -> w);
                        s.diff("azalt_rev|x1", Math.abs(rc[1] - rj[1]), () -> w);
                    }
                }
            }
        }
        for (double alt = -5; alt <= 90; alt += 0.37) {
            for (int dir : new int[]{SE_TRUE_TO_APP, SE_APP_TO_TRUE}) {
                for (double press : new double[]{0, 700, 1013.25}) {
                    final double a = alt;
                    final String w = "alt " + a + " dir " + dir + " press " + press;
                    s.diff("refrac", Math.abs(cn.swe_refrac(a, press, 10, dir) - jv.swe_refrac(a, press, 10, dir)), () -> w);
                    for (double h : new double[]{0, 2500}) {
                        final double[] dc = new double[20], dj = new double[20];
                        final double rc = cn.swe_refrac_extended(a, h, press, 10, 0.0065, dir, dc);
                        final double rj = jv.swe_refrac_extended(a, h, press, 10, 0.0065, dir, dj);
                        s.diff("refrac_extended", Math.abs(rc - rj), () -> w + " h " + h);
                        for (int k = 0; k < 4; k++) {
                            final int kk = k;
                            s.diff("refrac_extended|dret" + k, Math.abs(dc[k] - dj[k]), () -> w + " h " + h + " dret " + kk);
                        }
                    }
                }
            }
        }
    }

    private static void houses(AccuracySurvey s, ISwissEph cn, ISwissEph jv) {
        for (int h = 0; h < HSYS.length(); h++) {
            final char hsys = HSYS.charAt(h);
            final String hn = String.valueOf(hsys);
            s.call("house_name|" + hn, 0, 0, cn.swe_house_name(hsys), jv.swe_house_name(hsys), () -> hn);
            for (double lat : LATITUDES) {
                for (double armc = 3.3; armc < 360; armc += 29.9) {
                    final double[] cc = new double[37], cj = new double[37], ac = new double[10], aj = new double[10];
                    final double[] sc = new double[37], sj = new double[37], bc = new double[10], bj = new double[10];
                    final StringBuilder ec = new StringBuilder(), ej = new StringBuilder();
                    final double a = armc;
                    final String w = "hsys " + hn + " lat " + lat + " armc " + a;
                    final int rc = cn.swe_houses_armc_ex2(armc, lat, 23.44, hsys, cc, ac, sc, bc, ec);
                    final int rj = jv.swe_houses_armc_ex2(armc, lat, 23.44, hsys, cj, aj, sj, bj, ej);
                    if (!s.call("houses_armc|" + hn + "|ret", rc, rj, ec, ej, () -> w)) continue;
                    final int n = hsys == 'G' ? 36 : 12;
                    for (int i = 1; i <= n; i++) {
                        final int ii = i;
                        s.diff("houses_armc|" + hn + "|cusp", AccuracySurvey.angle(cc[i], cj[i]), () -> w + " cusp " + ii);
                        s.diff("houses_armc|" + hn + "|cusp_speed", Math.abs(sc[i] - sj[i]), () -> w + " cusp " + ii);
                    }
                    for (int i = 0; i < 8; i++) {
                        final int ii = i;
                        s.diff("houses_armc|ascmc", AccuracySurvey.angle(ac[i], aj[i]), () -> w + " ascmc " + ii);
                    }
                    for (double lon = 1.7; lon < 360; lon += 41.3) {
                        final double[] xp = {lon, 2.5};
                        final StringBuilder pc = new StringBuilder(), pj = new StringBuilder();
                        final double hc = cn.swe_house_pos(armc, lat, 23.44, hsys, xp.clone(), pc);
                        final double hj = jv.swe_house_pos(armc, lat, 23.44, hsys, xp.clone(), pj);
                        final double l = lon;
                        s.call("house_pos|" + hn + "|serr", 0, 0, pc, pj, () -> w);
                        double d = Math.abs(hc - hj);
                        final double span = hsys == 'G' ? 36 : 12;
                        if (d > span / 2) d = span - d;
                        s.diff("house_pos|" + hn, d, () -> w + " lon " + l + " native " + hc + " Java " + hj);
                    }
                }
            }
        }
    }

    private static void calendar(AccuracySurvey s, ISwissEph cn, ISwissEph jv) {
        for (int y = -5000; y <= 5000; y += 37) {
            for (int m = 1; m <= 12; m += 5) {
                for (int d = 1; d <= 31; d += 10) {
                    for (int greg : new int[]{SE_GREG_CAL, SE_JUL_CAL}) {
                        final int yy = y, mm = m, dd = d;
                        final String w = y + "-" + m + "-" + d + " greg " + greg;
                        final double jc = cn.swe_julday(y, m, d, 7.25, greg), jj = jv.swe_julday(y, m, d, 7.25, greg);
                        s.diff("julday", Math.abs(jc - jj), () -> w);
                        final int[] ymc = new int[3], ymj = new int[3];
                        final double[] uc = new double[1], uj = new double[1];
                        cn.swe_revjul(jc, greg, ymc, uc);
                        jv.swe_revjul(jc, greg, ymj, uj);
                        s.call("revjul", ymc[0] * 10000 + ymc[1] * 100 + ymc[2], ymj[0] * 10000 + ymj[1] * 100 + ymj[2], "", "", () -> w);
                        s.diff("revjul|hour", Math.abs(uc[0] - uj[0]), () -> w);
                        final double[] tc = new double[1], tj = new double[1];
                        final char cal = greg == SE_GREG_CAL ? 'g' : 'j';
                        s.call("date_conversion", cn.swe_date_conversion(yy, mm, dd, 7.25, cal, tc),
                                jv.swe_date_conversion(yy, mm, dd, 7.25, cal, tj), "", "", () -> w);
                        s.diff("date_conversion", Math.abs(tc[0] - tj[0]), () -> w);
                        s.call("day_of_week", cn.swe_day_of_week(jc), jv.swe_day_of_week(jc), "", "", () -> w);
                        if (y < 1 || y > 3000) continue;
                        for (double tz : new double[]{-11.5, 0, 2, 5.5, 13.75}) {
                            final int[] oc = new int[5], oj = new int[5];
                            final double[] sc = new double[1], sj = new double[1];
                            cn.swe_utc_time_zone(y, m, d, 7, 13, 21.25, tz, oc, sc);
                            jv.swe_utc_time_zone(y, m, d, 7, 13, 21.25, tz, oj, sj);
                            final String wz = w + " tz " + tz;
                            s.call("utc_time_zone", java.util.Arrays.hashCode(oc), java.util.Arrays.hashCode(oj), "", "", () -> wz);
                            s.diff("utc_time_zone|sec", Math.abs(sc[0] - sj[0]), () -> wz);
                        }
                    }
                }
            }
        }
    }

    private static void time(AccuracySurvey s, ISwissEph cn, ISwissEph jv) {
        for (double tjd : AccuracySurvey.dates(625000.5, 2818000.5, 401)) {
            final String w = "jd " + tjd;
            for (int eph : new int[]{SEFLG_SWIEPH, SEFLG_MOSEPH}) {
                final StringBuilder ec = new StringBuilder(), ej = new StringBuilder();
                s.diff("deltat_ex|" + eph, Math.abs(cn.swe_deltat_ex(tjd, eph, ec) - jv.swe_deltat_ex(tjd, eph, ej)), () -> w);
                s.call("deltat_ex|" + eph + "|serr", 0, 0, ec, ej, () -> w);
            }
            s.diff("deltat", Math.abs(cn.swe_deltat(tjd) - jv.swe_deltat(tjd)), () -> w);
            s.diff("sidtime", Math.abs(cn.swe_sidtime(tjd) - jv.swe_sidtime(tjd)), () -> w);
            s.diff("sidtime0", Math.abs(cn.swe_sidtime0(tjd, 23.43, -0.004) - jv.swe_sidtime0(tjd, 23.43, -0.004)), () -> w);
            final double[] tc = new double[1], tj = new double[1];
            final StringBuilder ec = new StringBuilder(), ej = new StringBuilder();
            s.call("time_equ", cn.swe_time_equ(tjd, tc, ec), jv.swe_time_equ(tjd, tj, ej), ec, ej, () -> w);
            s.diff("time_equ", Math.abs(tc[0] - tj[0]), () -> w);
            for (double lon : new double[]{-150.5, 0, 30.52, 179.9}) {
                final double[] lc = new double[1], lj = new double[1];
                final StringBuilder fc = new StringBuilder(), fj = new StringBuilder();
                final String wl = w + " lon " + lon;
                s.call("lmt_to_lat", cn.swe_lmt_to_lat(tjd, lon, lc, fc), jv.swe_lmt_to_lat(tjd, lon, lj, fj), fc, fj, () -> wl);
                s.diff("lmt_to_lat", Math.abs(lc[0] - lj[0]), () -> wl);
                final StringBuilder gc = new StringBuilder(), gj = new StringBuilder();
                s.call("lat_to_lmt", cn.swe_lat_to_lmt(tjd, lon, lc, gc), jv.swe_lat_to_lmt(tjd, lon, lj, gj), gc, gj, () -> wl);
                s.diff("lat_to_lmt", Math.abs(lc[0] - lj[0]), () -> wl);
            }
        }
    }

    private static void coordinates(AccuracySurvey s, ISwissEph cn, ISwissEph jv) {
        for (double lon = 0; lon < 360; lon += 13.7) {
            for (double lat = -89; lat <= 89; lat += 11.9) {
                for (double eps : new double[]{-23.44, 23.44, 90}) {
                    final double[] xc = new double[6], xj = new double[6];
                    final double[] xin = {lon, lat, 1.5, 0.98, -0.1, 0.002};
                    cn.swe_cotrans(xin, xc, eps);
                    jv.swe_cotrans(xin, xj, eps);
                    final String w = "x " + lon + " " + lat + " eps " + eps;
                    s.diff("cotrans|lon", AccuracySurvey.angle(xc[0], xj[0]), () -> w);
                    s.diff("cotrans|lat", Math.abs(xc[1] - xj[1]), () -> w);
                    cn.swe_cotrans_sp(xin, xc, eps);
                    jv.swe_cotrans_sp(xin, xj, eps);
                    for (int k = 0; k < 6; k++) {
                        final int kk = k;
                        s.diff("cotrans_sp", k == 0 ? AccuracySurvey.angle(xc[0], xj[0]) : Math.abs(xc[k] - xj[k]), () -> w + " [" + kk + "]");
                    }
                }
            }
        }
    }

    private static void degrees(AccuracySurvey s, ISwissEph cn, ISwissEph jv) {
        final int[] flags = {0, SE_SPLIT_DEG_ROUND_SEC, SE_SPLIT_DEG_ROUND_MIN, SE_SPLIT_DEG_ROUND_DEG, SE_SPLIT_DEG_ZODIACAL,
                SE_SPLIT_DEG_ZODIACAL | SE_SPLIT_DEG_ROUND_SEC, SE_SPLIT_DEG_NAKSHATRA, SE_SPLIT_DEG_NAKSHATRA | SE_SPLIT_DEG_ROUND_MIN,
                SE_SPLIT_DEG_KEEP_SIGN | SE_SPLIT_DEG_ZODIACAL | SE_SPLIT_DEG_ROUND_SEC, SE_SPLIT_DEG_KEEP_DEG | SE_SPLIT_DEG_ROUND_DEG};
        for (double x = -400; x <= 400; x += 0.987654321) {
            for (int fl : flags) {
                final int[] dc = new int[3], dj = new int[3], gc = new int[1], gj = new int[1];
                final double[] fc = new double[1], fj = new double[1];
                cn.swe_split_deg(x, fl, dc, fc, gc);
                jv.swe_split_deg(x, fl, dj, fj, gj);
                final double xx = x;
                final String w = "x " + x + " flag " + fl;
                s.call("split_deg", java.util.Arrays.hashCode(dc) * 31 + gc[0], java.util.Arrays.hashCode(dj) * 31 + gj[0], "", "",
                        () -> w + " native " + java.util.Arrays.toString(dc) + " Java " + java.util.Arrays.toString(dj));
                s.diff("split_deg|secfr", Math.abs(fc[0] - fj[0]), () -> w);
                s.diff("degnorm", Math.abs(cn.swe_degnorm(xx) - jv.swe_degnorm(xx)), () -> w);
            }
        }
        for (int ipl = -1; ipl <= 60; ipl++) {
            final int p = ipl;
            s.call("get_planet_name", 0, 0, cn.swe_get_planet_name(ipl), jv.swe_get_planet_name(ipl), () -> "ipl " + p);
        }
    }
}
