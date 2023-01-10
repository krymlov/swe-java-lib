/*
* Copyright (C) By the Author
* Author    Yura Krymlov
* Created   2020-05
*/

package org.swisseph.api;


import swisseph.SweConst;

import java.io.Serializable;

/**
 * This is the wrapper class for date,time,tmz,julianDay,deltaT,universal time (decimal hours)
 * The class instance should be created or initialized by corresponding {@link org.swisseph.ISwissEph} methods
 *
 * @author Yura Krymlov
 * @version 1.1, 2021-12
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

    // ---------------------------------------------------------------------------
    
    int IDXD_TIMEZONE = 0, IDXD_JULDAY = 1, IDXD_DELTAT = 2, IDXD_UTIME = 3, IDXD_ETIME = 4;
    int IDXI_YEAR = 0, IDXI_MONTH = 1, IDXI_DAY = 2, IDXI_HOUR = 3, IDXI_MINUTE = 4, IDXI_SECONDS = 5;
    
    static boolean sweGregorianCalendar(final double julDay) {
        return julDay >= JD_GC0 ? SE_GREG_CAL : SE_JUL_CAL;
    }

    static boolean gregorianCalendar(int year, int month, int day) {
        if ( year > 1582 ) return true;
        if ( year < 1582 ) return false;

        // date <= October 15, 1582
        if ( month < 10 || (month == 10 && day < 15) ) {
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
     * Queries the delta T value for the date of this object.
     * @return delta T or NaN if not calculated yet/valid
     */
    double deltaT();

    /**
     * @return The date as julian day in ET (Ephemeris Time) or NaN if not calculated yet
     */
    double ephemerisTime();
    
    float timeZone();
    
    /**
     * @return time zone, julian day, delta T, universal time (decimal hours), ephemeris time (ET)<br>
     * <b>Please pay attention, implementation can return a copy of the array</b>
     */
    double[] values();
    
    /**
     * @return date as year, month, day and optionally hour, minutes, seconds in local time<br>
     * <b>Please pay attention, implementation can return a copy of the array</b>
     */
    int[] date();
    
    int year();
    int month();
    int day();
    
    int hours();
    int minutes();
    double seconds();
    
    double utime();
    int uhours();
    int uminutes();
    double useconds();
}