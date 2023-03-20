/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2020-05
 */

package org.swisseph.api;


import swisseph.SweConst;
import java.io.Serializable;

import static java.lang.Double.isNaN;
import static org.swisseph.api.ISweConstants.*;

/**
 * This is the wrapper class for date, time zone, julianDay, deltaT,
 * universal time (decimal hours), ephemeris time (et) and local time
 * <br><br>
 * The class instance should be created or initialized by corresponding {@link org.swisseph.ISwissEph} methods.
 * <br><br>
 * Proper initialization by corresponding {@link org.swisseph.ISwissEph} methods for an instance of
 * {@link ISweJulianDate} with incomplete or mix of Local and UTC datetime is not guaranteed!!!
 * <br><br>
 * see creation/initialization of {@link swisseph.SweDate} as a reference!
 *
 * @author Yura Krymlov
 * @version 1.2, 2023-03
 */
public interface ISweJulianDate extends Serializable {
    boolean SE_JUL_CAL = false;
    boolean SE_GREG_CAL = true;

    // -3699-01-02 12:00:00.0h (jul)
    // DeltaT: 1.121190209400905
    double JULDAY_MIN = 370000.;

    // 2954-01-14 12:00:00.0h (greg)
    // DeltaT: 0.04591118586419796
    double JULDAY_MAX = 2800000.;

    /**
     * The Julian day number for the start of the Gregorian
     * calendar system (October 15, 1582)
     */
    double JD_GC0 = 2299160.5;

    float UT_TMZ = 0f;

    // ---------------------------------------------------------------------------

    int IDXI_YEAR = 0, IDXI_MONTH = 1, IDXI_DAY = 2;
    int IDXD_TIMEZONE = 0, IDXD_JULDAY = 1, IDXD_DELTAT = 2, IDXD_UTIME = 3, IDXD_ETIME = 4, IDXD_LTIME = 5;

    static boolean sweGregorianCalendar(final double julDay) {
        return julDay >= JD_GC0 ? SE_GREG_CAL : SE_JUL_CAL;
    }

    static boolean gregorianCalendar(int year, int month, int day) {
        if (year > 1582) return true;
        if (year < 1582) return false;

        // date <= October 15, 1582
        if (month < 10 || (month == 10 && day < 15)) {
            return false;
        }

        return true;
    }

    default int sweCalendarType() {
        return gregorianCalendar() ? SweConst.SE_GREG_CAL : SweConst.SE_JUL_CAL;
    }

    default boolean gregorianCalendar() {
        final int[] date = date();
        if (null != date && date.length > 2) {
            return gregorianCalendar(date[0], date[1], date[2]);
        }
        return sweGregorianCalendar(julianDay());
    }

    /**
     * @return julian day number or NaN if not calculated yet
     */
    double julianDay();

    /**
     * @return delta T or NaN if not calculated yet/valid
     */
    double deltaT();

    /**
     * @return the date as julian day in ET (Ephemeris Time) or NaN if not calculated yet
     */
    double epheTime();

    /**
     * @return local time (decimal hours in time zone) or NaN if not calculated yet
     */
    double localTime();
    void localTime(double ltime);

    float timeZone();

    /**
     * @return time zone, julian day, delta T, universal time (decimal hours in UT), ephemeris time (ET), local time (LT)<br>
     * <b>Please pay attention, implementation can return a copy of the array</b>
     */
    double[] values();

    /**
     * @return local date as year, month, day (according to time zone)<br>
     * <b>Please pay attention, implementation can return a copy of the array</b>
     */
    int[] date();

    /**
     * @return local year
     */
    int year();

    /**
     * @return local month
     */
    int month();

    /**
     * @return local day
     */
    int day();

    /**
     * @return local hours
     */
    default int hours() {
        final double ltime = localTime();
        if (isNaN(ltime)) return i0;
        if (d0 == ltime) return i0;
        return (int) ltime;
    }

    /**
     * @return local minutes
     */
    default int minutes() {
        double ltime = localTime();
        if (isNaN(ltime)) return i0;
        if (d0 == ltime) return i0;
        ltime -= (int) ltime;
        ltime *= d60;
        return (int) ltime;
    }

    /**
     * @return local seconds
     */
    default double seconds() {
        double ltime = localTime();
        if (isNaN(ltime)) return d0;
        if (d0 == ltime) return d0;
        ltime -= (int) ltime;
        ltime *= d60;
        ltime -= (int) ltime;
        ltime *= d60;
        return ltime;
    }

    /**
     * @return universal time (decimal hours in UT)
     */
    double utime();

    /**
     * @return hours in UT
     */
    default int uhours() {
        final double utime = utime();
        if (isNaN(utime)) return i0;
        if (d0 == utime) return i0;
        return (int) utime;
    }

    /**
     * @return minutes in UT
     */
    default int uminutes() {
        double utime = utime();
        if (isNaN(utime)) return i0;
        if (d0 == utime) return i0;
        utime -= (int) utime;
        utime *= d60;
        return (int) utime;
    }

    /**
     * @return seconds in UT
     */
    default double useconds() {
        double utime = utime();
        if (isNaN(utime)) return d0;
        if (d0 == utime) return d0;
        utime -= (int) utime;
        utime *= d60;
        utime -= (int) utime;
        utime *= d60;
        return utime;
    }
}