/**
 * Copyright (C) 1997 - 2008 Astrodienst AG, Switzerland.
 * All rights reserved.
 */
package swisseph;

/**
 * This class represents the standard application interface (API) to the Swiss Ephemeris.
 * <p>
 * The function calls are documented in the Programmer's documentation, which is online in HTML format
 * <p>
 * exported functions: <a href="https://www.astro.com/ftp/swisseph/src/swephexp.h">...</a>
 *
 * @author Yura Krymlov
 * @version 2.10
 */
public class SwephExp {

    public SwephExp(String libraryName) {
        System.loadLibrary(libraryName);
    }

    /***********************************************************
     * exported functions
     ***********************************************************/

    public static native int swe_heliacal_ut(double tjdstart_ut, double[] geopos, double[] datm,
                                             double[] dobs, String objectNameIn, int typeEvent,
                                             int iflag, double[] dret, StringBuilder serr);

    public static native int swe_heliacal_pheno_ut(double tjd_ut, double[] geopos, double[] datm, double[] dobs,
                                                   String objectNameIn, int typeEvent, int helflag, double[] darr, StringBuilder serr);

    public static native int swe_vis_limit_mag(double tjdut, double[] geopos, double[] datm, double[] dobs,
                                               StringBuilder objectName, int helflag, double[] dret, StringBuilder serr);

    /* the following are secret, for Victor Reijs' */
    public static native int swe_heliacal_angle(double tjdut, double[] dgeo, double[] datm, double[] dobs,
                                                int helflag, double mag, double azi_obj, double azi_sun, double azi_moon, double alt_moon, double[] dret, StringBuilder serr);

    public static native int swe_topo_arcus_visionis(double tjdut, double[] dgeo, double[] datm, double[] dobs,
                                                     int helflag, double mag, double azi_obj, double alt_obj, double azi_sun, double azi_moon, double alt_moon, double[] dret, StringBuilder serr);

    /* the following is secret, for Dieter, allows to test old models of
     * precession, nutation, etc. Search for SE_MODEL_... in this file */
    public static native void swe_set_astro_models(StringBuilder samod, int iflag);

    public static native void swe_get_astro_models(StringBuilder samod, StringBuilder sdet, int iflag);

    /****************************
     * exports from sweph.c
     ****************************/

    public static native String swe_version();

    public static native String swe_get_library_path();

    /* planets, moon, nodes etc. */
    public static native int swe_calc(
            double tjd, int ipl, int iflag,
            double[] xx,
            StringBuilder serr);

    public static native int swe_calc_ut(double tjd_ut, int ipl, int iflag,
                                         double[] xx, StringBuilder serr);

    public static native int swe_calc_pctr(double tjd, int ipl, int iplctr, int iflag, double[] xxret, StringBuilder serr);

    /* fixed stars */
    public static native int swe_fixstar(
            StringBuilder star, double tjd, int iflag,
            double[] xx,
            StringBuilder serr);

    public static native int swe_fixstar_ut(StringBuilder star, double tjd_ut, int iflag,
                                            double[] xx, StringBuilder serr);

    public static native int swe_fixstar_mag(StringBuilder star, double[] mag, StringBuilder serr);

    public static native int swe_fixstar2(
            StringBuilder star, double tjd, int iflag,
            double[] xx,
            StringBuilder serr);

    public static native int swe_fixstar2_ut(StringBuilder star, double tjd_ut, int iflag,
                                             double[] xx, StringBuilder serr);

    public static native int swe_fixstar2_mag(StringBuilder star, double[] mag, StringBuilder serr);

    /* close Swiss Ephemeris */
    public static native void swe_close();

    /* set directory path of ephemeris files */
    public static native void swe_set_ephe_path(String path);

    /* set file name of JPL file */
    public static native void swe_set_jpl_file(String fname);

    /* get planet name */
    public static native String swe_get_planet_name(int ipl);

    /* set geographic position of observer */
    public static native void swe_set_topo(double geolon, double geolat, double geoalt);

    /* set sidereal mode */
    public static native void swe_set_sid_mode(int sid_mode, double t0, double ayan_t0);

    /* get ayanamsa */
    public static native int swe_get_ayanamsa_ex(double tjd_et, int iflag, double[] daya, StringBuilder serr);

    public static native int swe_get_ayanamsa_ex_ut(double tjd_ut, int iflag, double[] daya, StringBuilder serr);

    public static native double swe_get_ayanamsa(double tjd_et);

    public static native double swe_get_ayanamsa_ut(double tjd_ut);

    public static native String swe_get_ayanamsa_name(int isidmode);

    public static native String swe_get_current_file_data(int ifno, double[] tfstart, double[] tfend, int[] denum);

    /****************************
     * exports from swedate.c
     ****************************/

    public static native int swe_date_conversion(
            int y, int m, int d,         /* year, month, day */
            double utime,   /* universal time in hours (decimal) */
            char c,         /* calendar g[regorian]|j[ulian] */
            double[] tjd);

    public static native double swe_julday(
            int year, int month, int day, double hour,
            int gregflag);

    public static native void swe_revjul(
            double jd,
            int gregflag,
            int[] jYearMonDay, double[] jut);

    public static native int swe_utc_to_jd(
            int iyear, int imonth, int iday,
            int ihour, int imin, double dsec,
            int gregflag, double[] dret, StringBuilder serr);

    public static native void swe_jdet_to_utc(
            double tjd_et, int gregflag,
            int[] iYearMonthDayHourMin, double[] dsec);

    public static native void swe_jdut1_to_utc(
            double tjd_ut, int gregflag,
            int[] iYearMonthDayHourMin, double[] dsec);

    public static native void swe_utc_time_zone(
            int iyear, int imonth, int iday,
            int ihour, int imin, double dsec,
            double d_timezone, int[] ioutYearMonthDayHourMin, double[] dsec_out);

    /****************************
     * exports from swehouse.c
     ****************************/

    public static native int swe_houses(
            double tjd_ut, double geolat, double geolon, int hsys,
            double[] cusps, double[] ascmc);

    public static native int swe_houses_ex(
            double tjd_ut, int iflag, double geolat, double geolon, int hsys,
            double[] cusps, double[] ascmc);

    public static native int swe_houses_ex2(
            double tjd_ut, int iflag, double geolat, double geolon, int hsys,
            double[] cusps, double[] ascmc, double[] cusp_speed, double[] ascmc_speed, StringBuilder serr);

    public static native int swe_houses_armc(
            double armc, double geolat, double eps, int hsys,
            double[] cusps, double[] ascmc);

    public static native int swe_houses_armc_ex2(
            double armc, double geolat, double eps, int hsys,
            double[] cusps, double[] ascmc, double[] cusp_speed, double[] ascmc_speed, StringBuilder serr);

    public static native double swe_house_pos(
            double armc, double geolat, double eps, int hsys, double[] xpin, StringBuilder serr);

    public static native String swe_house_name(int hsys);


    /****************************
     * exports from swecl.c
     ****************************/

    public static native int swe_gauquelin_sector(double t_ut, int ipl, StringBuilder starname, int iflag, int imeth, double[] geopos, double atpress, double attemp, double[] dgsect, StringBuilder serr);

    /* computes geographic location and attributes of solar
     * eclipse at a given tjd */
    public static native int swe_sol_eclipse_where(double tjd, int ifl, double[] geopos, double[] attr, StringBuilder serr);

    public static native int swe_lun_occult_where(double tjd, int ipl, StringBuilder starname, int ifl, double[] geopos, double[] attr, StringBuilder serr);

    /* computes attributes of a solar eclipse for given tjd, geolon, geolat */
    public static native int swe_sol_eclipse_how(double tjd, int ifl, double[] geopos, double[] attr, StringBuilder serr);

    /* finds time of next local eclipse */
    public static native int swe_sol_eclipse_when_loc(double tjd_start, int ifl, double[] geopos, double[] tret, double[] attr, int backward, StringBuilder serr);

    public static native int swe_lun_occult_when_loc(double tjd_start, int ipl, StringBuilder starname, int ifl,
                                                     double[] geopos, double[] tret, double[] attr, int backward, StringBuilder serr);

    /* finds time of next eclipse globally */
    public static native int swe_sol_eclipse_when_glob(double tjd_start, int ifl, int ifltype,
                                                       double[] tret, int backward, StringBuilder serr);

    /* finds time of next occultation globally */
    public static native int swe_lun_occult_when_glob(double tjd_start, int ipl, StringBuilder starname, int ifl, int ifltype,
                                                      double[] tret, int backward, StringBuilder serr);

    /* computes attributes of a lunar eclipse for given tjd */
    public static native int swe_lun_eclipse_how(
            double tjd_ut,
            int ifl,
            double[] geopos,
            double[] attr,
            StringBuilder serr);

    public static native int swe_lun_eclipse_when(double tjd_start, int ifl, int ifltype,
                                                  double[] tret, int backward, StringBuilder serr);

    public static native int swe_lun_eclipse_when_loc(double tjd_start, int ifl,
                                                      double[] geopos, double[] tret, double[] attr, int backward, StringBuilder serr);

    /* planetary phenomena */
    public static native int swe_pheno(double tjd, int ipl, int iflag, double[] attr, StringBuilder serr);

    public static native int swe_pheno_ut(double tjd_ut, int ipl, int iflag, double[] attr, StringBuilder serr);

    public static native double swe_refrac(double inalt, double atpress, double attemp, int calc_flag);

    public static native double swe_refrac_extended(double inalt, double geoalt, double atpress, double attemp, double lapse_rate, int calc_flag, double[] dret);

    public static native void swe_set_lapse_rate(double lapse_rate);

    public static native void swe_azalt(
            double tjd_ut,
            int calc_flag,
            double[] geopos,
            double atpress,
            double attemp,
            double[] xin,
            double[] xaz);

    public static native void swe_azalt_rev(
            double tjd_ut,
            int calc_flag,
            double[] geopos,
            double[] xin,
            double[] xout);

    public static native int swe_rise_trans_true_hor(
            double tjd_ut, int ipl, StringBuilder starname,
            int epheflag, int rsmi,
            double[] geopos,
            double atpress, double attemp,
            double horhgt,
            double[] tret,
            StringBuilder serr);

    public static native int swe_rise_trans(
            double tjd_ut, int ipl, StringBuilder starname,
            int epheflag, int rsmi,
            double[] geopos,
            double atpress, double attemp,
            double[] tret,
            StringBuilder serr);

    public static native int swe_nod_aps(double tjd_et, int ipl, int iflag,
                                         int method,
                                         double[] xnasc, double[] xndsc,
                                         double[] xperi, double[] xaphe,
                                         StringBuilder serr);

    public static native int swe_nod_aps_ut(double tjd_ut, int ipl, int iflag,
                                            int method,
                                            double[] xnasc, double[] xndsc,
                                            double[] xperi, double[] xaphe,
                                            StringBuilder serr);

    public static native int swe_get_orbital_elements(
            double tjd_et, int ipl, int iflag, double[] dret, StringBuilder serr);

    public static native int swe_orbit_max_min_true_distance(double tjd_et, int ipl, int iflag, double[] dmax, double[] dmin, double[] dtrue, StringBuilder serr);

    /****************************
     * exports from swephlib.c
     ****************************/

    /* delta t */
    public static native double swe_deltat(double tjd);

    public static native double swe_deltat_ex(double tjd, int iflag, StringBuilder serr);

    /* equation of time */
    public static native int swe_time_equ(double tjd, double[] te, StringBuilder serr);

    public static native int swe_lmt_to_lat(double tjd_lmt, double geolon, double[] tjd_lat, StringBuilder serr);

    public static native int swe_lat_to_lmt(double tjd_lat, double geolon, double[] tjd_lmt, StringBuilder serr);

    /* sidereal time */
    public static native double swe_sidtime0(double tjd_ut, double eps, double nut);

    public static native double swe_sidtime(double tjd_ut);

    public static native void swe_set_interpolate_nut(/*AS_BOOL*/int do_interpolate);

    /* coordinate transformation polar -> polar */
    public static native void swe_cotrans(double[] xpo, double[] xpn, double eps);

    public static native void swe_cotrans_sp(double[] xpo, double[] xpn, double eps);

    /* tidal acceleration to be used in swe_deltat() */
    public static native double swe_get_tid_acc();

    public static native void swe_set_tid_acc(double t_acc);

    /* set a user defined delta t to be returned by functions
     * swe_deltat() and swe_deltat_ex() */
    public static native void swe_set_delta_t_userdef(double dt);

    public static native double swe_degnorm(double x);

    public static native double swe_radnorm(double x);

    public static native double swe_rad_midp(double x1, double x0);

    public static native double swe_deg_midp(double x1, double x0);

    public static native void swe_split_deg(double ddeg, int roundflag, int[] iDegMinSec, double[] dsecfr, int[] isgn);
}
