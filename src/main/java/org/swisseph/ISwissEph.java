/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2020-05
 */

package org.swisseph;


import org.swisseph.api.ISweJulianDate;
import org.swisseph.app.SweJulianDate;
import org.swisseph.app.SweRuntimeException;
import swisseph.DblObj;

import java.io.Serializable;
import java.util.Calendar;

import static java.lang.Double.isNaN;
import static java.util.Calendar.*;
import static org.swisseph.api.ISweConstants.*;
import static org.swisseph.api.ISweJulianDate.*;
import static swisseph.SweConst.SE_GREG_CAL;
import static swisseph.SweConst.SE_JUL_CAL;

/**
 * @author Yura
 * @version 1.0, 2020-05
 */
public interface ISwissEph extends Serializable {

    /**
     * @return true if it is the native implementation
     */
    boolean isNativeAPI();

    static int getCalendarType(final double julDay) {
        return julDay >= JD_GC0 ? SE_GREG_CAL : SE_JUL_CAL;
    }

    default ISweJulianDate getJulianDate(final double julDay) {
        return initJulianDate(new SweJulianDate(julDay));
    }

    default ISweJulianDate getJulianDate(final double julDay, final double timeZone) {
        return initJulianDate(new SweJulianDate(julDay, timeZone));
    }

    default ISweJulianDate getJulianDate(final int[] date, final boolean gregorianCalendar, final double timeZone) {
        return initJulianDate(new SweJulianDate(date, gregorianCalendar, timeZone));
    }

    default ISweJulianDate getJulianDate(final Calendar calendar) {
        int millis = calendar.getTimeZone().getOffset(calendar.getTimeInMillis());
        return getJulianDate(calendar, millis / d3600000);
    }

    default ISweJulianDate getJulianDate(final Calendar calendar, final double timeZone) {
        final int[] datetime = new int[]{
                calendar.get(YEAR),
                calendar.get(MONTH) + 1,
                calendar.get(DAY_OF_MONTH),
                calendar.get(HOUR_OF_DAY),
                calendar.get(MINUTE),
                calendar.get(SECOND),
                calendar.get(MILLISECOND)
        };

        return initJulianDate(new SweJulianDate(datetime, true, timeZone));
    }

    /**
     * Calculates julian day number and utc time using the given local time and time zone as the part of {@link ISweJulianDate} parameter
     * or if julian day number is already set then calculate local time for the given julian day and time zone
     */
    default ISweJulianDate initJulianDate(ISweJulianDate julianDate) {
        if (null == julianDate) {
            throw new SweRuntimeException("Julian date is mandatory parameter");
        }

        final double julDay = julianDate.julianDay();

        if (d0 > julDay || isNaN(julDay)) {
            julianDate = initJulianDay(julianDate);
        }

        return initDateTime(julianDate);
    }

    /**
     * Calculates julian day number and utc time using the given local
     * time and time zone as the part of {@link ISweJulianDate} parameter
     */
    default ISweJulianDate initJulianDay(final ISweJulianDate julianDate) {
        final int[] date = julianDate.date();

        if (null == date || date.length < 3) {
            throw new SweRuntimeException("Valid local date/time is expected");
        }

        final int year = julianDate.year();
        final int month = julianDate.month();
        final int dayOfMonth = julianDate.day();
        final int hourOfDay = julianDate.hours();
        final int minutes = julianDate.minutes();
        final double seconds = julianDate.seconds();
        final double timeZone = julianDate.timeZone();
        final int calendarType = julianDate.sweCalendarType();

        ISweJulianDate utcjdt = julianDate;
        double utime = julianDate.utime();

        if (timeZone != d0) { // For conversion from local time to UTC
            utcjdt = swe_utc_time_zone(year, month, dayOfMonth,
                    hourOfDay, minutes, seconds, false, timeZone);
            utime = utcjdt.utime();
        } else if (isNaN(utime)) {
            utime = hourOfDay;
            utime += (minutes / d60);
            utime += (seconds / d3600);
        }

        final double julday = swe_julday(utcjdt.year(),
                utcjdt.month(), utcjdt.day(), utime, calendarType);

        julianDate.values()[IDXD_JULDAY] = julday;
        julianDate.values()[IDXD_UTIME] = utime;

        return julianDate;
    }

    default ISweJulianDate initDateTime(ISweJulianDate julianDate) {
        final int[] date = julianDate.date();

        if (null != date && date.length > IDXI_SECONDS) {
            return julianDate;
        }

        final double julDay = julianDate.julianDay();

        if (d0 > julDay || isNaN(julDay)) {
            throw new SweRuntimeException("Valid julian day number is expected");
        }

        final ISweJulianDate utcjdt = swe_revjul(julDay, julianDate.sweCalendarType());

        // For conversion from UTC to local time even if tmz == 0...
        final ISweJulianDate tmzjdt = swe_utc_time_zone(utcjdt.year(), utcjdt.month(), utcjdt.day(),
                utcjdt.uhours(), utcjdt.uminutes(), utcjdt.useconds(), true, julianDate.timeZone());

        return new SweJulianDate(julianDate, tmzjdt.date(), utcjdt.utime());
    }

    void swe_close();

    String swe_get_ephe_path();

    void swe_set_ephe_path(String path);

    //String swe_get_library_path();
    String swe_version();

    boolean swe_set_topo();

    double swe_get_geo_alt();

    void swe_set_topo(double geolon, double geolat, double geoalt);

    void swe_set_sid_mode(int sid_mode, double t0, double ayan_t0);

    double swe_sidtime(double tjd_ut);

    double swe_get_ayanamsa_ut(double tjd_ut);

    String swe_get_ayanamsa_name(int isidmode);

    double swe_get_ayanamsa(double tjd_et);

    String swe_get_planet_name(int ipl);

    int swe_houses_ex(double tjd_ut, int iflag, double geolat, double geolon,
                      int hsys, double[] cusps, double[] ascmc);

    int swe_calc(double tjd, int ipl, int iflag, double xx[], StringBuilder serr);

    int swe_calc_ut(double tjd_ut, int ipl, int iflag, double xx[], StringBuilder serr);

    int swe_sol_eclipse_when_loc(double tjd_start, int ifl, double[] geopos,
                                 double[] tret, double[] attr, int backward, StringBuilder serr);

    int swe_sol_eclipse_when_glob(double tjd_start, int ifl, int ifltype,
                                  double[] tret, int backward, StringBuilder serr);

    int swe_lun_eclipse_when_loc(double tjd_start, int ifl, double geopos[], double tret[], double attr[], int backward, StringBuilder serr);

    int swe_lun_eclipse_when(double tjd_start, int ifl, int ifltype, double[] tret, int backward, StringBuilder serr);

    int swe_rise_trans(double tjd_ut, int ipl, StringBuilder starname, int epheflag, int rsmi,
                       double[] geopos, double atpress, double attemp, DblObj tret, StringBuilder serr);

    double swe_deltat(double tjd);

    double swe_deltat_ex(double tjd, int iflag, StringBuilder serr);

    void swe_set_jpl_file(String fname);

    String swe_house_name(int hsys);

    void swe_set_tid_acc(double t_acc);

    double swe_get_tid_acc();

    double swe_julday(int year, int month, int day, double hour, int gregFlag);

    ISweJulianDate swe_revjul(double jd, int gregFlag);

    ISweJulianDate swe_utc_to_jd(int year, int month, int day, int hour,
                                 int min, double sec, int gregflag, StringBuilder serr);

    ISweJulianDate swe_jdet_to_utc(double tjd_et, int gregflag);

    ISweJulianDate swe_jdut1_to_utc(double tjd_ut, int gregflag);

    /**
     * Transforms local time to UTC or UTC to local time
     * <p>
     * For time zones east of Greenwich, timezone is positive.
     * For time zones west of Greenwich, timezone is negative.
     * <p>
     * For conversion from local time to utc, use +timezone.
     * For conversion from utc to local time, use -timezone.
     */
    ISweJulianDate swe_utc_time_zone(int iyear, int imonth, int iday, int ihour,
                                     int imin, double dsec, boolean utcToLocal, double timezone);

    void swe_split_deg(double ddeg, int roundflag, int[] iDegMinSec, double[] dsecfr, int[] isgn);

    int swe_heliacal_ut(double tjdstart_ut, double[] geopos, double[] datm,
                        double[] dobs, String objectName, int typeEvent,
                        int iflag, double[] dret, StringBuilder serr);

    int swe_lun_occult_where(double tjd, int ipl, StringBuilder starname, int ifl,
                             double[] geopos, double[] attr, StringBuilder serr);
}
