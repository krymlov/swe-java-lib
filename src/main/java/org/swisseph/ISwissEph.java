/*
 * Author    Yura Krymlov
 * Created   2020-05
 */

package org.swisseph;


import org.swisseph.api.ISweJulianDate;
import org.swisseph.app.SweJulianDate;
import org.swisseph.app.SweRuntimeException;
import swisseph.DblObj;
import swisseph.SweConst;
import swisseph.SwephExp;

import java.util.Calendar;

import static java.lang.Double.isNaN;
import static java.lang.Math.round;
import static java.util.Calendar.*;
import static org.swisseph.api.ISweConstants.*;
import static org.swisseph.api.ISweJulianDate.*;
import static swisseph.SweConst.ERR;

/**
 * {@link ISwissEph} is a wrapper interface to the Swiss Ephemeris API defined in {@link SwephExp}.
 * It also adds default factory methods to create and initialize {@link ISweJulianDate}
 * which is a wrapper for different date/time fields needed for datetime presentation.
 * <br><br>
 * This interface has two implementations: Native {@link SwephNative}, which is the default one
 * and pure Java {@link swisseph.SwissEph}, which was created by:
 * Thomas Mack (http://th-mack.de/international/download/index.html)
 * <br><br>
 * {@link ISwissEph} is the object that may hold resources (such as file handles) until it is closed.
 * The close() method of an AutoCloseable object is called automatically when exiting a try-with-resources block.
 *
 * @author Yura
 * @version 1.1, 2021-12
 */
public interface ISwissEph extends AutoCloseable {
    /**
     * @return SweConst.SE_GREG_CAL if julDay relates to Gregorian calendar date (>= October 15, 1582),
     * else returns SweConst.SE_JUL_CAL if julDay relates to Julian calendar (< October 15, 1582)
     */
    static int getCalendarType(final double julDay) {
        return julDay >= JD_GC0 ? SweConst.SE_GREG_CAL : SweConst.SE_JUL_CAL;
    }

    /**
     * @return true if it is the native implementation
     */
    default boolean isNativeAPI() {
        return true;
    }

    /**
     * Closes this resource, relinquishing any underlying resources (close Swiss Ephemeris).
     * This method is invoked automatically on objects managed by the try-with-resources statement.
     */
    default void close() {
        swe_close();
    }

    default ISweJulianDate getJulianDate(final double julDay) {
        return initJulianDate(new SweJulianDate(julDay));
    }

    default ISweJulianDate getJulianDate(final double julDay, final float timeZone) {
        return initJulianDate(new SweJulianDate(julDay, timeZone));
    }

    default ISweJulianDate getJulianDate(final int[] date, final float timeZone) {
        return initJulianDate(new SweJulianDate(date, timeZone));
    }

    default ISweJulianDate getJulianDate(final Calendar calendar) {
        int millis = calendar.getTimeZone().getOffset(calendar.getTimeInMillis());
        return getJulianDate(calendar, (float)(millis / d3600000));
    }

    default ISweJulianDate getJulianDate(final Calendar calendar, final float timeZone) {
        final int[] datetime = new int[]{
                calendar.get(YEAR),
                calendar.get(MONTH) + 1,
                calendar.get(DAY_OF_MONTH),
                calendar.get(HOUR_OF_DAY),
                calendar.get(MINUTE),
                calendar.get(SECOND),
                calendar.get(MILLISECOND)
        };

        return initJulianDate(new SweJulianDate(datetime, timeZone));
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

    String swe_get_ephe_path();

    double swe_get_geo_alt();

    boolean swe_set_topo();

    /***********************************************************
     * exported functions
     ***********************************************************/

    default int swe_heliacal_ut(double tjdstart_ut, double[] geopos, double[] datm, double[] dobs,
                                String objectName, int typeEvent, int iflag, double[] dret, StringBuilder serr) {
        return SwephExp.swe_heliacal_ut(tjdstart_ut, geopos, datm, dobs, objectName, typeEvent, iflag, dret, serr);
    }

    default int swe_heliacal_pheno_ut(double tjd_ut, double[] geopos, double[] datm, double[] dobs, String objectNameIn,
                                      int typeEvent, int helflag, double[] darr, StringBuilder serr) {
        return SwephExp.swe_heliacal_pheno_ut(tjd_ut, geopos, datm, dobs, objectNameIn,
                typeEvent, helflag, darr, serr);
    }

    default int swe_vis_limit_mag(double tjdut, double[] geopos, double[] datm, double[] dobs,
                                  StringBuilder objectName, int helflag, double[] dret, StringBuilder serr) {
        return SwephExp.swe_vis_limit_mag(tjdut, geopos, datm, dobs,
                objectName, helflag, dret, serr);
    }

    /* the following are secret, for Victor Reijs' */
    default int swe_heliacal_angle(double tjdut, double[] dgeo, double[] datm, double[] dobs,
                                   int helflag, double mag, double azi_obj, double azi_sun,
                                   double azi_moon, double alt_moon, double[] dret, StringBuilder serr) {
        return SwephExp.swe_heliacal_angle(tjdut, dgeo, datm, dobs,
                helflag, mag, azi_obj, azi_sun,
                azi_moon, alt_moon, dret, serr);
    }

    default int swe_topo_arcus_visionis(double tjdut, double[] dgeo, double[] datm, double[] dobs,
                                        int helflag, double mag, double azi_obj, double alt_obj, double azi_sun,
                                        double azi_moon, double alt_moon, double[] dret, StringBuilder serr) {
        return SwephExp.swe_topo_arcus_visionis(tjdut, dgeo, datm, dobs,
                helflag, mag, azi_obj, alt_obj, azi_sun,
                azi_moon, alt_moon, dret, serr);
    }

    /* the following is secret, for Dieter, allows to test old models of
     * precession, nutation, etc. Search for SE_MODEL_... in this file */
    default void swe_set_astro_models(StringBuilder samod, int iflag) {
        SwephExp.swe_set_astro_models(samod, iflag);
    }

    default void swe_get_astro_models(StringBuilder samod, StringBuilder sdet, int iflag) {
        SwephExp.swe_get_astro_models(samod, sdet, iflag);
    }

    /****************************
     * exports from sweph.c
     ****************************/

    default String swe_version() {
        return SwephExp.swe_version();
    }

    default String swe_get_library_path() {
        return SwephExp.swe_get_library_path();
    }

    /* planets, moon, nodes etc. */
    default int swe_calc(double tjd, int ipl, int iflag, double[] xx, StringBuilder serr) {
        return SwephExp.swe_calc(tjd, ipl, iflag, xx, serr);
    }

    default int swe_calc_ut(double tjd_ut, int ipl, int iflag, double[] xx, StringBuilder serr) {
        return SwephExp.swe_calc_ut(tjd_ut, ipl, iflag, xx, serr);
    }

    default int swe_calc_pctr(double tjd, int ipl, int iplctr, int iflag, double[] xxret, StringBuilder serr) {
        return SwephExp.swe_calc_pctr(tjd, ipl, iplctr, iflag, xxret, serr);
    }

    /* fixed stars */
    default int swe_fixstar(StringBuilder star, double tjd, int iflag, double[] xx, StringBuilder serr) {
        return SwephExp.swe_fixstar(star, tjd, iflag, xx, serr);
    }

    default int swe_fixstar_ut(StringBuilder star, double tjd_ut, int iflag, double[] xx, StringBuilder serr) {
        return SwephExp.swe_fixstar_ut(star, tjd_ut, iflag,
                xx, serr);
    }

    default int swe_fixstar_mag(StringBuilder star, double[] mag, StringBuilder serr) {
        return SwephExp.swe_fixstar_mag(star, mag, serr);
    }

    default int swe_fixstar2(StringBuilder star, double tjd, int iflag, double[] xx, StringBuilder serr) {
        return SwephExp.swe_fixstar2(star, tjd, iflag, xx, serr);
    }

    default int swe_fixstar2_ut(StringBuilder star, double tjd_ut, int iflag, double[] xx, StringBuilder serr) {
        return SwephExp.swe_fixstar2_ut(star, tjd_ut, iflag, xx, serr);
    }

    default int swe_fixstar2_mag(StringBuilder star, double[] mag, StringBuilder serr) {
        return SwephExp.swe_fixstar2_mag(star, mag, serr);
    }

    /* close Swiss Ephemeris */
    default void swe_close() {
        SwephExp.swe_close();
    }

    /* set directory path of ephemeris files */
    default void swe_set_ephe_path(String path) {
        SwephExp.swe_set_ephe_path(path);
    }

    /* set file name of JPL file */
    default void swe_set_jpl_file(String fname) {
        SwephExp.swe_set_jpl_file(fname);
    }

    /* get planet name */
    default String swe_get_planet_name(int ipl) {
        return SwephExp.swe_get_planet_name(ipl);
    }

    /* set geographic position of observer */
    default void swe_set_topo(double geolon, double geolat, double geoalt) {
        SwephExp.swe_set_topo(geolon, geolat, geoalt);
    }

    /* set sidereal mode */
    default void swe_set_sid_mode(final int sid_mode, final double t0, final double ayan_t0) {
        SwephExp.swe_set_sid_mode(sid_mode, t0, ayan_t0);
    }

    /* get ayanamsa */
    default int swe_get_ayanamsa_ex(double tjd_et, int iflag, double[] daya, StringBuilder serr) {
        return SwephExp.swe_get_ayanamsa_ex(tjd_et, iflag, daya, serr);
    }

    default int swe_get_ayanamsa_ex_ut(double tjd_ut, int iflag, double[] daya, StringBuilder serr) {
        return SwephExp.swe_get_ayanamsa_ex_ut(tjd_ut, iflag, daya, serr);
    }

    default double swe_get_ayanamsa(double tjd_et) {
        return SwephExp.swe_get_ayanamsa(tjd_et);
    }

    default double swe_get_ayanamsa_ut(double tjd_ut) {
        return SwephExp.swe_get_ayanamsa_ut(tjd_ut);
    }

    default String swe_get_ayanamsa_name(int isidmode) {
        return SwephExp.swe_get_ayanamsa_name(isidmode);
    }

    default String swe_get_current_file_data(int ifno, double[] tfstart, double[] tfend, int[] denum) {
        return SwephExp.swe_get_current_file_data(ifno, tfstart, tfend, denum);
    }

    /****************************
     * exports from swedate.c
     ****************************/

    default int swe_date_conversion(
            int y, int m, int d,         /* year, month, day */
            double utime,   /* universal time in hours (decimal) */
            char c,         /* calendar g[regorian]|j[ulian] */
            double[] tjd) {
        return SwephExp.swe_date_conversion(
                y, m, d,         /* year, month, day */
                utime,   /* universal time in hours (decimal) */
                c,         /* calendar g[regorian]|j[ulian] */
                tjd);
    }

    default double swe_julday(int year, int month, int day, double utime, int gregflag) {
        return SwephExp.swe_julday(year, month, day, utime, gregflag);
    }

    default ISweJulianDate swe_revjul(double jd, int gregflag) {
        final double[] utime = new double[1];
        final int[] yearMonDay = new int[3];

        SwephExp.swe_revjul(jd, gregflag, yearMonDay, utime);
        return new SweJulianDate(jd, yearMonDay, utime[0]);
    }

    default ISweJulianDate swe_utc_to_jd(int year, int month, int day, int hour, int min, double dsec, int gregflag, StringBuilder serr) {
        final double[] dret = new double[2];

        int res = SwephExp.swe_utc_to_jd(year, month, day, hour, min, dsec, gregflag, dret, serr);
        if (res == ERR) return null;

        double utime = hour;
        utime += (min / d60);
        utime += (dsec / d3600);

        // dret[0] = Julian day number TT (ET)
        // dret[1] = Julian day number UT1

        int seconds = (int) dsec;
        double millis = (dsec - seconds) * d1000;
        if (millis < d999) millis = round(millis);

        return new SweJulianDate(dret[1], new int[]{year, month, day,
                hour, min, seconds, (int)millis}, utime);
    }

    default ISweJulianDate swe_jdet_to_utc(double tjd_et, int gregflag) {
        final int[] outYearMonthDayHourMin = new int[7];
        final double[] outDsec = new double[1];

        SwephExp.swe_jdet_to_utc(tjd_et, gregflag, outYearMonthDayHourMin, outDsec);

        double utime = outYearMonthDayHourMin[IDXI_HOUR];
        utime += (outYearMonthDayHourMin[IDXI_MINUTE] / d60);
        utime += (outDsec[0] / d3600);

        final int seconds = (int) outDsec[0];
        outYearMonthDayHourMin[IDXI_SECONDS] = seconds;
        outDsec[0] -= seconds; outDsec[0] *= d1000;

        if (outDsec[0] > d999) outYearMonthDayHourMin[IDXI_MILLIS] = (int) outDsec[0];
        else outYearMonthDayHourMin[IDXI_MILLIS] = (int) round(outDsec[0]);

        return new SweJulianDate(tjd_et, outYearMonthDayHourMin, utime);
    }

    default ISweJulianDate swe_jdut1_to_utc(double tjd_ut, int gregflag) {
        final int[] outYearMonthDayHourMin = new int[7];
        final double[] outDsec = new double[1];

        SwephExp.swe_jdut1_to_utc(tjd_ut, gregflag, outYearMonthDayHourMin, outDsec);

        double utime = outYearMonthDayHourMin[IDXI_HOUR];
        utime += (outYearMonthDayHourMin[IDXI_MINUTE] / d60);
        utime += (outDsec[0] / d3600);

        final int seconds = (int) outDsec[0];
        outYearMonthDayHourMin[IDXI_SECONDS] = seconds;
        outDsec[0] -= seconds; outDsec[0] *= d1000;

        if (outDsec[0] > d999) outYearMonthDayHourMin[IDXI_MILLIS] = (int) outDsec[0];
        else outYearMonthDayHourMin[IDXI_MILLIS] = (int) round(outDsec[0]);

        return new SweJulianDate(tjd_ut, outYearMonthDayHourMin, utime);
    }

    /**
     * Transforms local time to UTC or UTC to local time
     * <p>
     * For time zones east of Greenwich, timezone is positive.
     * For time zones west of Greenwich, timezone is negative.
     * <p>
     * For conversion from local time to utc, use +timezone.
     * For conversion from utc to local time, use -timezone.
     */
    default ISweJulianDate swe_utc_time_zone(int iyear, int imonth, int iday,
                                             int ihour, int imin, double dsec,
                                             boolean utcToLocal, double timezone) {
        final int[] outYearMonthDayHourMin = new int[7];
        final double[] outDsec = new double[1];

        // For conversion from local time to utc, use +timezone
        // For conversion from utc to local time, use -timezone

        SwephExp.swe_utc_time_zone(iyear, imonth, iday, ihour, imin, dsec,
                utcToLocal ? -timezone : +timezone, outYearMonthDayHourMin, outDsec);

        double utime = outYearMonthDayHourMin[IDXI_HOUR];
        utime += (outYearMonthDayHourMin[IDXI_MINUTE] / d60);
        utime += (outDsec[0] / d3600);

        final int seconds = (int) outDsec[0];
        outYearMonthDayHourMin[IDXI_SECONDS] = seconds;
        outDsec[0] -= seconds; outDsec[0] *= d1000;

        if (outDsec[0] > d999) outYearMonthDayHourMin[IDXI_MILLIS] = (int) outDsec[0];
        else outYearMonthDayHourMin[IDXI_MILLIS] = (int) round(outDsec[0]);

        return new SweJulianDate(outYearMonthDayHourMin, utime, (float)timezone);
    }

    /****************************
     * exports from swehouse.c
     ****************************/

    default int swe_houses(double tjd_ut, double geolat, double geolon, int hsys, double[] cusps, double[] ascmc) {
        return SwephExp.swe_houses(tjd_ut, geolat, geolon, hsys, cusps, ascmc);
    }

    default int swe_houses_ex(double tjd_ut, int iflag, double geolat, double geolon, int hsys, double[] cusps, double[] ascmc) {
        return SwephExp.swe_houses_ex(tjd_ut, iflag, geolat, geolon, hsys, cusps, ascmc);
    }

    default int swe_houses_ex2(double tjd_ut, int iflag, double geolat, double geolon, int hsys, double[] cusps,
                               double[] ascmc, double[] cusp_speed, double[] ascmc_speed, StringBuilder serr) {
        return SwephExp.swe_houses_ex2(tjd_ut, iflag, geolat, geolon, hsys,
                cusps, ascmc, cusp_speed, ascmc_speed, serr);
    }

    default int swe_houses_armc(double armc, double geolat, double eps, int hsys, double[] cusps, double[] ascmc) {
        return SwephExp.swe_houses_armc(armc, geolat, eps, hsys, cusps, ascmc);
    }

    default int swe_houses_armc_ex2(double armc, double geolat, double eps, int hsys, double[] cusps,
                                    double[] ascmc, double[] cusp_speed, double[] ascmc_speed, StringBuilder serr) {
        return SwephExp.swe_houses_armc_ex2(armc, geolat, eps, hsys, cusps, ascmc, cusp_speed, ascmc_speed, serr);
    }

    default double swe_house_pos(double armc, double geolat, double eps, int hsys, double[] xpin, StringBuilder serr) {
        return SwephExp.swe_house_pos(armc, geolat, eps, hsys, xpin, serr);
    }

    default String swe_house_name(int hsys) {
        return SwephExp.swe_house_name(hsys);
    }

    /****************************
     * exports from swecl.c
     ****************************/

    default int swe_gauquelin_sector(double t_ut, int ipl, StringBuilder starname, int iflag, int imeth,
                                     double[] geopos, double atpress, double attemp, double[] dgsect, StringBuilder serr) {
        return SwephExp.swe_gauquelin_sector(t_ut, ipl, starname, iflag, imeth, geopos, atpress, attemp, dgsect, serr);
    }

    /* computes geographic location and attributes of solar
     * eclipse at a given tjd */
    default int swe_sol_eclipse_where(double tjd, int ifl, double[] geopos, double[] attr, StringBuilder serr) {
        return SwephExp.swe_sol_eclipse_where(tjd, ifl, geopos, attr, serr);
    }

    default int swe_lun_occult_where(double tjd, int ipl, StringBuilder starname, int ifl,
                                     double[] geopos, double[] attr, StringBuilder serr) {
        return SwephExp.swe_lun_occult_where(tjd, ipl, starname, ifl, geopos, attr, serr);
    }

    /* computes attributes of a solar eclipse for given tjd, geolon, geolat */
    default int swe_sol_eclipse_how(double tjd, int ifl, double[] geopos, double[] attr, StringBuilder serr) {
        return SwephExp.swe_sol_eclipse_how(tjd, ifl, geopos, attr, serr);
    }

    /* finds time of next local eclipse */
    default int swe_sol_eclipse_when_loc(double tjd_start, int ifl, double[] geopos, double[] tret,
                                         double[] attr, int backward, StringBuilder serr) {
        return SwephExp.swe_sol_eclipse_when_loc(tjd_start, ifl, geopos, tret, attr, backward, serr);
    }

    default int swe_lun_occult_when_loc(double tjd_start, int ipl, StringBuilder starname, int ifl, double[] geopos,
                                        double[] tret, double[] attr, int backward, StringBuilder serr) {
        return SwephExp.swe_lun_occult_when_loc(tjd_start, ipl, starname, ifl, geopos, tret, attr, backward, serr);
    }

    /* finds time of next eclipse globally */
    default int swe_sol_eclipse_when_glob(double tjd_start, int ifl, int ifltype, double[] tret, int backward,
                                          StringBuilder serr) {
        return SwephExp.swe_sol_eclipse_when_glob(tjd_start, ifl, ifltype, tret, backward, serr);
    }


    /* finds time of next occultation globally */
    default int swe_lun_occult_when_glob(double tjd_start, int ipl, StringBuilder starname, int ifl, int ifltype,
                                         double[] tret, int backward, StringBuilder serr) {
        return SwephExp.swe_lun_occult_when_glob(tjd_start, ipl, starname, ifl, ifltype, tret, backward, serr);
    }

    /* computes attributes of a lunar eclipse for given tjd */
    default int swe_lun_eclipse_how(double tjd_ut, int ifl, double[] geopos, double[] attr, StringBuilder serr) {
        return SwephExp.swe_lun_eclipse_how(tjd_ut, ifl, geopos, attr, serr);
    }

    default int swe_lun_eclipse_when(double tjd_start, int ifl, int ifltype, double[] tret, int backward, StringBuilder serr) {
        return SwephExp.swe_lun_eclipse_when(tjd_start, ifl, ifltype, tret, backward, serr);
    }

    default int swe_lun_eclipse_when_loc(double tjd_start, int ifl, double[] geopos, double[] tret,
                                         double[] attr, int backward, StringBuilder serr) {
        return SwephExp.swe_lun_eclipse_when_loc(tjd_start, ifl, geopos, tret, attr, backward, serr);
    }

    /* planetary phenomena */
    default int swe_pheno(double tjd, int ipl, int iflag, double[] attr, StringBuilder serr) {
        return SwephExp.swe_pheno(tjd, ipl, iflag, attr, serr);
    }

    default int swe_pheno_ut(double tjd_ut, int ipl, int iflag, double[] attr, StringBuilder serr) {
        return SwephExp.swe_pheno_ut(tjd_ut, ipl, iflag, attr, serr);
    }

    default double swe_refrac(double inalt, double atpress, double attemp, int calc_flag) {
        return SwephExp.swe_refrac(inalt, atpress, attemp, calc_flag);
    }

    default double swe_refrac_extended(double inalt, double geoalt, double atpress, double attemp, double lapse_rate, int calc_flag, double[] dret) {
        return SwephExp.swe_refrac_extended(inalt, geoalt, atpress, attemp, lapse_rate, calc_flag, dret);
    }

    default void swe_set_lapse_rate(double lapse_rate) {
        SwephExp.swe_set_lapse_rate(lapse_rate);
    }

    default void swe_azalt(double tjd_ut, int calc_flag, double[] geopos, double atpress,
                           double attemp, double[] xin, double[] xaz) {
        SwephExp.swe_azalt(tjd_ut, calc_flag, geopos, atpress, attemp, xin, xaz);
    }

    default void swe_azalt_rev(double tjd_ut, int calc_flag, double[] geopos, double[] xin, double[] xout) {
        SwephExp.swe_azalt_rev(tjd_ut, calc_flag, geopos, xin, xout);
    }

    default int swe_rise_trans_true_hor(double tjd_ut, int ipl, StringBuilder starname, int epheflag, int rsmi,
                                        double[] geopos, double atpress, double attemp, double horhgt, double[] tret, StringBuilder serr) {
        return SwephExp.swe_rise_trans_true_hor(tjd_ut, ipl, starname, epheflag, rsmi, geopos, atpress, attemp, horhgt, tret, serr);
    }

    default int swe_rise_trans(double tjd_ut, int ipl, StringBuilder starname, int epheflag, int rsmi,
                               double[] geopos, double atpress, double attemp, DblObj tretObj, StringBuilder serr) {
        final double[] tret = new double[1];

        int res = SwephExp.swe_rise_trans(tjd_ut, ipl, starname, epheflag,
                rsmi, geopos, atpress, attemp, tret, serr);

        tretObj.val = tret[0];
        return res;
    }

    default int swe_nod_aps(double tjd_et, int ipl, int iflag, int method, double[] xnasc, double[] xndsc,
                            double[] xperi, double[] xaphe, StringBuilder serr) {
        return SwephExp.swe_nod_aps(tjd_et, ipl, iflag, method, xnasc, xndsc, xperi, xaphe, serr);
    }

    default int swe_nod_aps_ut(double tjd_ut, int ipl, int iflag, int method, double[] xnasc, double[] xndsc,
                               double[] xperi, double[] xaphe, StringBuilder serr) {
        return SwephExp.swe_nod_aps_ut(tjd_ut, ipl, iflag, method, xnasc, xndsc, xperi, xaphe, serr);
    }

    default int swe_get_orbital_elements(double tjd_et, int ipl, int iflag, double[] dret, StringBuilder serr) {
        return SwephExp.swe_get_orbital_elements(tjd_et, ipl, iflag, dret, serr);
    }

    default int swe_orbit_max_min_true_distance(double tjd_et, int ipl, int iflag, double[] dmax,
                                                double[] dmin, double[] dtrue, StringBuilder serr) {
        return SwephExp.swe_orbit_max_min_true_distance(tjd_et, ipl, iflag, dmax, dmin, dtrue, serr);
    }

    /****************************
     * exports from swephlib.c
     ****************************/

    /* delta t */
    default double swe_deltat(double tjd) {
        return SwephExp.swe_deltat(tjd);
    }

    default double swe_deltat_ex(double tjd, int iflag, StringBuilder serr) {
        return SwephExp.swe_deltat_ex(tjd, iflag, serr);
    }

    /* equation of time */
    default int swe_time_equ(double tjd, double[] te, StringBuilder serr) {
        return SwephExp.swe_time_equ(tjd, te, serr);
    }

    default int swe_lmt_to_lat(double tjd_lmt, double geolon, double[] tjd_lat, StringBuilder serr) {
        return SwephExp.swe_lmt_to_lat(tjd_lmt, geolon, tjd_lat, serr);
    }

    default int swe_lat_to_lmt(double tjd_lat, double geolon, double[] tjd_lmt, StringBuilder serr) {
        return SwephExp.swe_lat_to_lmt(tjd_lat, geolon, tjd_lmt, serr);
    }

    /* sidereal time */
    default double swe_sidtime0(double tjd_ut, double eps, double nut) {
        return SwephExp.swe_sidtime0(tjd_ut, eps, nut);
    }

    default double swe_sidtime(final double tjd_ut) {
        return SwephExp.swe_sidtime(tjd_ut);
    }

    default void swe_set_interpolate_nut(/*AS_BOOL*/int do_interpolate) {
        SwephExp.swe_set_interpolate_nut(do_interpolate);
    }

    /* coordinate transformation polar -> polar */
    default void swe_cotrans(double[] xpo, double[] xpn, double eps) {
        SwephExp.swe_cotrans(xpo, xpn, eps);
    }

    default void swe_cotrans_sp(double[] xpo, double[] xpn, double eps) {
        SwephExp.swe_cotrans_sp(xpo, xpn, eps);
    }

    /* tidal acceleration to be used in swe_deltat() */
    default double swe_get_tid_acc() {
        return SwephExp.swe_get_tid_acc();
    }

    default void swe_set_tid_acc(double t_acc) {
        SwephExp.swe_set_tid_acc(t_acc);
    }

    /* set a user defined delta t to be returned by functions
     * swe_deltat() and swe_deltat_ex() */
    default void swe_set_delta_t_userdef(double dt) {
        SwephExp.swe_set_delta_t_userdef(dt);
    }

    default double swe_degnorm(double x) {
        return SwephExp.swe_degnorm(x);
    }

    default double swe_radnorm(double x) {
        return SwephExp.swe_radnorm(x);
    }

    default double swe_rad_midp(double x1, double x0) {
        return SwephExp.swe_rad_midp(x1, x0);
    }

    default double swe_deg_midp(double x1, double x0) {
        return SwephExp.swe_deg_midp(x1, x0);
    }

    default void swe_split_deg(double dDeg, int roundFlag, int[] iDegMinSec, double[] dSecFr, int[] iSign) {
        SwephExp.swe_split_deg(dDeg, roundFlag, iDegMinSec, dSecFr, iSign);
    }
}
