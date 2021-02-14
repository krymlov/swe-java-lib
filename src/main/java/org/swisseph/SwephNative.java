/*
* Copyright (C) By the Author
* Author    Yura Krymlov
* Created   2020-05
*/

package org.swisseph;


import org.swisseph.api.ISweJulianDate;
import org.swisseph.app.SweJulianDate;
import swisseph.DblObj;
import swisseph.SwephExp;

import static org.swisseph.api.ISweConstants.d3600;
import static org.swisseph.api.ISweConstants.d60;
import static org.swisseph.api.ISweJulianDate.*;
import static swisseph.SweConst.ERR;
import static swisseph.SweConst.SE_GREG_CAL;

/**
 * @author Yura Krymlov
 * @version 1.0, 2020-05
 */
public class SwephNative implements ISwissEph {
    private static final long serialVersionUID = -6149105182275656104L;

    public static final String SWISSEPH_LIBRARY_NAME = "swe2v10";
    protected static final SwephExp swephExp = new SwephExp(SWISSEPH_LIBRARY_NAME);

    protected String ephe_path;
    protected boolean topo_set;
    protected double geo_alt;
    
    public SwephNative(String ephePath) {
        swe_set_ephe_path(ephePath);
    }

    @Override
    public boolean isNativeAPI() {
        return true;
    }

    // ----------------------------------------------------------------------

    @Override
    public void swe_close() {
        SwephExp.swe_close();
    }
    
    @Override
    public boolean swe_set_topo() {
        return topo_set;
    }
    
    @Override
    public double swe_get_geo_alt() {
        return geo_alt;
    }

    @Override
    public String swe_get_ephe_path() {
        return ephe_path;
    }

    @Override
    public void swe_set_ephe_path(final String ephe_path) {
        SwephExp.swe_set_ephe_path(ephe_path);
        this.ephe_path = ephe_path;
    }

    //@Override
    public String swe_get_library_path() {
        return SwephExp.swe_get_library_path();
    }

    @Override
    public String swe_version() {
        return SwephExp.swe_version();
    }

    @Override
    public void swe_set_topo(final double geolon, final double geolat, final double geoalt) {
        SwephExp.swe_set_topo(geolon, geolat, geoalt);
        this.topo_set = true;
        this.geo_alt = geoalt;
    }

    @Override
    public void swe_set_sid_mode(final int sid_mode, final double t0, final double ayan_t0) {
        SwephExp.swe_set_sid_mode(sid_mode, t0, ayan_t0);
    }

    @Override
    public double swe_sidtime(final double tjd_ut) {
        return SwephExp.swe_sidtime(tjd_ut);
    }

    /**
     * This calculates the sidereal time from a Julian day number, the
     * obliquity of the eclipse and the nutation (in degrees). You might
     * want to use swe_sidtime(double), if you have just the Julian day
     * number available.
     * <p>
     * 
     * @param tjd_ut The Julian day number
     * @param eps Obliquity of the ecliptic
     * @param nut Nutation in degrees
     * @return Sidereal time in degrees.
     * @see #swe_sidtime(double)
     */
    public double swe_sidtime0(double tjd_ut, double eps, double nut) {
        return SwephExp.swe_sidtime0(tjd_ut, eps, nut);
    }

    @Override
    public String swe_get_planet_name(int ipl) {
        return SwephExp.swe_get_planet_name(ipl);
    }

    @Override
    public double swe_get_ayanamsa_ut(double tjd_ut) {
        return SwephExp.swe_get_ayanamsa_ut(tjd_ut);
    }

    @Override
    public String swe_get_ayanamsa_name(int isidmode) {
        return SwephExp.swe_get_ayanamsa_name(isidmode);
    }
    
    @Override
    public double swe_get_ayanamsa(double tjd_et) {
        return SwephExp.swe_get_ayanamsa(tjd_et);
    }

    @Override
    public int swe_houses_ex(double tjd_ut, int iflag, double geolat, double geolon, int hsys, double[] cusps,
        double[] ascmc) {
        return SwephExp.swe_houses_ex(tjd_ut, iflag, geolat, geolon, hsys, cusps, ascmc);
    }

    @Override
    public int swe_calc(double tjd, int ipl, int iflag, double[] xx, StringBuilder serr) {
        return SwephExp.swe_calc(tjd, ipl, iflag, xx, serr);
    }

    @Override
    public int swe_calc_ut(double tjd_ut, int ipl, int iflag, double[] xx, StringBuilder serr) {
        return SwephExp.swe_calc_ut(tjd_ut, ipl, iflag, xx, serr);
    }

    /* finds time of next local eclipse */
    @Override
    public int swe_sol_eclipse_when_loc(double tjd_start, int ifl, double[] geopos, double[] tret,
        double[] attr, int backward, StringBuilder serr) {
        return SwephExp.swe_sol_eclipse_when_loc(tjd_start, ifl, geopos, tret, attr, backward, serr);
    }

    @Override
    public int swe_lun_eclipse_when_loc(double tjd_start, int ifl, double[] geopos, double[] tret,
        double[] attr, int backward, StringBuilder serr) {
        return SwephExp.swe_lun_eclipse_when_loc(tjd_start, ifl, geopos, tret, attr, backward, serr);
    }

    /* finds time of next eclipse globally */
    @Override
    public int swe_sol_eclipse_when_glob(double tjd_start, int ifl, int ifltype, double[] tret, int backward,
        StringBuilder serr) {
        return SwephExp.swe_sol_eclipse_when_glob(tjd_start, ifl, ifltype, tret, backward, serr);
    }

    @Override
    public int swe_lun_eclipse_when(double tjd_start, int ifl, int ifltype, double[] tret, int backward,
        StringBuilder serr) {
        return SwephExp.swe_lun_eclipse_when(tjd_start, ifl, ifltype, tret, backward, serr);
    }

    @Override
    public int swe_rise_trans(double tjd_ut, int ipl, StringBuilder starname, int epheflag, int rsmi,
        double[] geopos, double atpress, double attemp, DblObj tretObj, StringBuilder serr) {
        final double[] tret = new double[1];

        int res = SwephExp.swe_rise_trans(tjd_ut, ipl, starname, epheflag,
                rsmi, geopos, atpress, attemp, tret, serr);

        tretObj.val = tret[0];
        return res;
    }

    /**
     * @param utime universal time in hours (decimal)
     * @param gflag calendar g[regorian]|j[ulian]
     * @return status or -1 if error
     */
    public int swe_date_conversion(int year, int month, int day, double utime, char gflag, double[] tjd) {
        return SwephExp.swe_date_conversion(year, month, day, utime, gflag, tjd);
    }

    @Override
    public double swe_julday(int year, int month, int day, double utime, int gregflag) {
        return SwephExp.swe_julday(year, month, day, utime, gregflag);
    }

    @Override
    public ISweJulianDate swe_revjul(double jd, int gregflag) {
        final double[] utime = new double[1];
        final int[] yearMonDay = new int[3];
        
        SwephExp.swe_revjul(jd, gregflag, yearMonDay, utime);
        return new SweJulianDate(jd, yearMonDay, utime[0], SE_GREG_CAL == gregflag);
    }

    @Override
    public double swe_deltat(double tjd) {
        return SwephExp.swe_deltat(tjd);
    }

    @Override
    public double swe_deltat_ex(double tjd, int iflag, StringBuilder serr) {
        return SwephExp.swe_deltat_ex(tjd, iflag, serr);
    }
    
    /* tidal acceleration to be used in swe_deltat() */
    @Override
    public double swe_get_tid_acc() {
        return SwephExp.swe_get_tid_acc();
    }

    @Override
    public void swe_set_tid_acc(double t_acc) {
        SwephExp.swe_set_tid_acc(t_acc);
    }

    /* set a user defined delta t to be returned by functions swe_deltat() and swe_deltat_ex() */
    public void swe_set_delta_t_userdef(double dt) {
        SwephExp.swe_set_delta_t_userdef(dt);
    }
    
    /**
     *  set file name of JPL file 
     */
    @Override
    public void swe_set_jpl_file(String fname) {
        SwephExp.swe_set_jpl_file(fname);
    }
    
    @Override
    public String swe_house_name(int hsys) {
        return SwephExp.swe_house_name(hsys);
    }
    
    @Override
    public ISweJulianDate swe_utc_to_jd(int year, int month, int day, int hour, int min, double sec, int gregflag, StringBuilder serr) {
        final double[] dret = new double[2];
        
        int res = SwephExp.swe_utc_to_jd(year, month, day, hour, min, sec, gregflag, dret, serr);
        if ( res == ERR ) return null;
        
        double time = hour;
        time += (min / d60);
        time += (sec / d3600);
        
        // dret[0] = Julian day number TT (ET)
        // dret[1] = Julian day number UT1
        
        return new SweJulianDate(dret[1], new int[] {year, month, day,
            hour, min, (int)sec}, time, SE_GREG_CAL == gregflag);
    }
    
    @Override
    public ISweJulianDate swe_jdet_to_utc(double tjd_et, int gregflag) {
        final int[] outYearMonthDayHourMin = new int[6];
        final double[] outDsec = new double[1];
        
        SwephExp.swe_jdet_to_utc(tjd_et, gregflag, outYearMonthDayHourMin, outDsec);
        outYearMonthDayHourMin[IDXI_SECONDS] = (int)outDsec[0];

        double time = outYearMonthDayHourMin[IDXI_HOUR];
        time += (outYearMonthDayHourMin[IDXI_MINUTE]/d60);
        time += (outDsec[0]/d3600);
        
        return new SweJulianDate(tjd_et, outYearMonthDayHourMin, time, SE_GREG_CAL == gregflag);
    }
    
    @Override
    public ISweJulianDate swe_jdut1_to_utc(double tjd_ut, int gregflag) {
        final int[] outYearMonthDayHourMin = new int[6];
        final double[] outDsec = new double[1];
        
        SwephExp.swe_jdut1_to_utc(tjd_ut, gregflag, outYearMonthDayHourMin, outDsec);
        outYearMonthDayHourMin[IDXI_SECONDS] = (int)outDsec[0];
        
        double time = outYearMonthDayHourMin[IDXI_HOUR];
        time += (outYearMonthDayHourMin[IDXI_MINUTE]/d60);
        time += (outDsec[0]/d3600);
        
        return new SweJulianDate(tjd_ut, outYearMonthDayHourMin, time, SE_GREG_CAL == gregflag);
    }
    
    /**
    * transform local time to UTC or UTC to local time
    * 
    * input 
    *   iyear ... dsec     date and time
    *   d_timezone     timezone offset
    * output
    *   iyear_out ... dsec_out
    * 
    * For time zones east of Greenwich, d_timezone is positive
    * For time zones west of Greenwich, d_timezone is negative
    * 
    * For conversion from local time to utc, use +timezone
    * For conversion from utc to local time, use -timezone
    */
    @Override
    public ISweJulianDate swe_utc_time_zone(int iyear, int imonth, int iday,
    int ihour, int imin, double dsec, boolean utcToLocal, double timezone) {
        final int[] outYearMonthDayHourMin = new int[6];
        final double[] outDsec = new double[1];
        
        // For conversion from local time to utc, use +timezone
        // For conversion from utc to local time, use -timezone
        
        SwephExp.swe_utc_time_zone(iyear, imonth, iday, ihour, imin, dsec,
            utcToLocal ? -timezone : +timezone, outYearMonthDayHourMin, outDsec);
        
        outYearMonthDayHourMin[IDXI_SECONDS] = (int)outDsec[0];
        double utime = outYearMonthDayHourMin[IDXI_HOUR];
        utime += (outYearMonthDayHourMin[IDXI_MINUTE]/d60);
        utime += (outDsec[0]/d3600);
        
        SweJulianDate sweJulDate = new SweJulianDate(outYearMonthDayHourMin, utime, true);
        sweJulDate.timeZone(timezone);
        return sweJulDate;
    }

    @Override
    public void swe_split_deg(double dDeg, int roundFlag, int[] iDegMinSec, double[] dSecFr, int[] iSign) {
        SwephExp.swe_split_deg(dDeg, roundFlag, iDegMinSec, dSecFr, iSign);
    }

    @Override
    public int swe_heliacal_ut(double tjdstart_ut, double[] geopos, double[] datm, double[] dobs,
                               String objectName, int typeEvent, int iflag, double[] dret, StringBuilder serr) {
        return SwephExp.swe_heliacal_ut(tjdstart_ut, geopos, datm, dobs, objectName, typeEvent, iflag, dret, serr);
    }

    @Override
    public int swe_lun_occult_where(double tjd, int ipl, StringBuilder starname, int ifl,
                                    double[] geopos, double[] attr, StringBuilder serr) {
        return SwephExp.swe_lun_occult_where(tjd, ipl, starname, ifl, geopos, attr, serr);
    }
}
