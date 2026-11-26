/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-11
 */
package org.swisseph;

import swisseph.SwissEph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static swisseph.SweConst.*;

/**
 * The {@code swe_calc()} sweep shared by the {@code CalcAccuracy*Test} classes: every body, seven
 * kinds of position, a range of dates, both engines in a fresh thread, every difference recorded
 * (see {@link AccuracySurvey}). The classes are split by range only so that they can run in
 * parallel forks.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
final class CalcAccuracy {

    static final int B = SEFLG_SPEED;
    static final String[] FLAG_NAMES = {"plain", "equatorial", "lahiri", "topocentric", "heliocentric", "j2000nonut", "truepos", "xyz"};
    static final int[] FLAGS = {B, B | SEFLG_EQUATORIAL, B | SEFLG_SIDEREAL, B | SEFLG_TOPOCTR, B | SEFLG_HELCTR,
            B | SEFLG_J2000 | SEFLG_NONUT, B | SEFLG_TRUEPOS | SEFLG_NOABERR | SEFLG_NOGDEFL, B | SEFLG_XYZ};

    static final int[] BODIES = {SE_SUN, SE_MOON, SE_MERCURY, SE_VENUS, SE_MARS, SE_JUPITER, SE_SATURN, SE_URANUS,
            SE_NEPTUNE, SE_PLUTO, SE_MEAN_NODE, SE_TRUE_NODE, SE_MEAN_APOG, SE_OSCU_APOG, SE_EARTH, SE_CHIRON, SE_PHOLUS,
            SE_CERES, SE_PALLAS, SE_JUNO, SE_VESTA, SE_INTP_APOG, SE_INTP_PERG};

    static final String[] COMPONENTS = {"lon", "lat", "dist", "vlon", "vlat", "vdist"};

    private CalcAccuracy() {
    }

    /**
     * @param ephe {@code SEFLG_SWIEPH} or {@code SEFLG_MOSEPH}
     * @return the survey, written to {@code target/accuracy/<name>.tsv}
     */
    static AccuracySurvey sweep(String name, String ephePath, int ephe, double[] dates) {
        final AccuracySurvey s = new AccuracySurvey(name);
        AccuracySurvey.inFreshThread(() -> {
            try (ISwissEph cn = new SwephNative(ephePath); ISwissEph jv = new SwissEph(ephePath)) {
                cn.swe_set_topo(30.52, 50.45, 180);
                jv.swe_set_topo(30.52, 50.45, 180);
                cn.swe_set_sid_mode(SE_SIDM_LAHIRI, 0, 0);
                jv.swe_set_sid_mode(SE_SIDM_LAHIRI, 0, 0);
                try {
                    for (int ipl : BODIES) {
                        for (int f = 0; f < FLAGS.length; f++) {
                            final int iflag = ephe | FLAGS[f];
                            final boolean xyz = (iflag & SEFLG_XYZ) != 0;
                            final String key = FLAG_NAMES[f] + "|" + ipl + "|";
                            for (double tjd : dates) {
                                final double[] xc = new double[6], xj = new double[6];
                                final StringBuilder ec = new StringBuilder(), ej = new StringBuilder();
                                final int rc = cn.swe_calc(tjd, ipl, iflag, xc, ec), rj = jv.swe_calc(tjd, ipl, iflag, xj, ej);
                                if (!s.call(key + "ret", rc, rj, ec, ej, () -> "jd " + tjd) || rc < 0) continue;
                                for (int k = 0; k < 6; k++) {
                                    final int kk = k;
                                    final double d;
                                    if (xyz) d = Math.abs(xc[k] - xj[k]) / Math.max(1e-3, Math.abs(xc[2 < k ? k - 3 : k]) + 1e-3);
                                    else if (k == 0) d = AccuracySurvey.angle(xc[0], xj[0]);
                                    else if (k == 2) d = Math.abs(xc[2] - xj[2]) / Math.max(1e-12, Math.abs(xc[2]));
                                    else if (k == 5) d = Math.abs(xc[5] - xj[5]) / Math.max(1, Math.abs(xc[2]));
                                    else d = Math.abs(xc[k] - xj[k]);
                                    s.diff(key + COMPONENTS[k], d, () -> "jd " + tjd + " native " + xc[kk] + " Java " + xj[kk]);
                                }
                            }
                        }
                    }
                } finally {
                    cn.swe_set_sid_mode(SE_SIDM_FAGAN_BRADLEY, 0, 0);
                    jv.swe_set_sid_mode(SE_SIDM_FAGAN_BRADLEY, 0, 0);
                }
            }
            return null;
        });
        s.write();
        return s;
    }

    /**
     * The bounds every sweep must keep. Positions in degrees, speeds in degrees a day, distance
     * relative. The true node and the osculating apogee have their own, the measured noise of their
     * osculating orbits. The interpolated apsides have fixed ones: swemmoon.c swi_intp_apsides()
     * ends with a parabola through three distances 0.18" of anomaly apart, whose second difference
     * is ~1e-16 AU, so one bit of the lunar distance moves the apsis by up to 1e-7 deg (measured
     * 2026-11-26, jd 1746234.1157: the native series is smooth there, the Java one jumps); the
     * topocentric speed is taken from three positions 1e-4 day apart and turns that into 1.5e-3 deg/day.
     */
    static void assertWithin(AccuracySurvey s, double pos, double speed, double dist, double oscPos, double oscSpeed) {
        assertEquals("", s.retDiffList(), "return values");
        assertTrue(s.count() > 10000, "compared " + s.count());
        final StringBuilder bad = new StringBuilder();
        for (String fl : FLAG_NAMES) {
            if ("xyz".equals(fl)) continue;
            for (int ipl : BODIES) {
                final boolean osc = ipl == SE_TRUE_NODE || ipl == SE_OSCU_APOG;
                final boolean intp = ipl == SE_INTP_APOG || ipl == SE_INTP_PERG;
                final boolean topo = "topocentric".equals(fl);
                final double p = intp ? 2e-7 : osc ? oscPos : pos;
                final double v = intp ? (topo ? 3e-3 : 1e-6) : osc ? oscSpeed : speed;
                final String k = fl + "|" + ipl + "|";
                bad.append(s.above(k + "lon", "", p));
                bad.append(s.above(k + "lat", "", p));
                bad.append(s.above(k + "dist", "", intp ? 1e-11 : osc ? Math.max(dist, oscPos * 1e-2) : dist));
                bad.append(s.above(k + "vlon", "", v));
                bad.append(s.above(k + "vlat", "", v));
            }
        }
        assertEquals("", bad.toString());
    }
}
