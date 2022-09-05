/************************************************************
 SWISSEPH: exported definitions and constants

 This file represents the standard application interface (API)
 to the Swiss Ephemeris.

 A C programmer needs only to include this file, and link his code
 with the SwissEph library.

 The function calls are documented in the Programmer's documentation,
 which is online in HTML format.

 Structure of this file:
 Public API definitions
 Internal developer's definitions
 Public API functions.

 Authors: Dieter Koch and Alois Treindl, Astrodienst Zurich
 ************************************************************/
/* Copyright (C) 1997 - 2021 Astrodienst AG, Switzerland.  All rights reserved.

  License conditions
  ------------------

  This file is part of Swiss Ephemeris.

  Swiss Ephemeris is distributed with NO WARRANTY OF ANY KIND.  No author
  or distributor accepts any responsibility for the consequences of using it,
  or for whether it serves any particular purpose or works at all, unless he
  or she says so in writing.  

  Swiss Ephemeris is made available by its authors under a dual licensing
  system. The software developer, who uses any part of Swiss Ephemeris
  in his or her software, must choose between one of the two license models,
  which are
  a) GNU Affero General Public License (AGPL)
  b) Swiss Ephemeris Professional License

  The choice must be made before the software developer distributes software
  containing parts of Swiss Ephemeris to others, and before any public
  service using the developed software is activated.

  If the developer choses the AGPL software license, he or she must fulfill
  the conditions of that license, which includes the obligation to place his
  or her whole software project under the AGPL or a compatible license.
  See https://www.gnu.org/licenses/agpl-3.0.html

  If the developer choses the Swiss Ephemeris Professional license,
  he must follow the instructions as found in http://www.astro.com/swisseph/ 
  and purchase the Swiss Ephemeris Professional Edition from Astrodienst
  and sign the corresponding license contract.

  The License grants you the right to use, copy, modify and redistribute
  Swiss Ephemeris, but only under certain conditions described in the License.
  Among other things, the License requires that the copyright notices and
  this notice be preserved on all copies.

  Authors of the Swiss Ephemeris: Dieter Koch and Alois Treindl

  The authors of Swiss Ephemeris have no control or influence over any of
  the derived works, i.e. over software or services created by other
  programmers which use Swiss Ephemeris functions.

  The names of the authors or of the copyright holder (Astrodienst) must not
  be used for promoting any software, product or service which uses or contains
  the Swiss Ephemeris. This copyright notice is the ONLY place where the
  names of the authors can legally appear, except in cases where they have
  given special permission in writing.

  The trademarks 'Swiss Ephemeris' and 'Swiss Ephemeris inside' may be used
  for promoting such software, products or services.
*/
package swisseph;

/**
 * This class contains all the constants that might be interesting to the
 * user of the swisseph package.<P>
 * All constants are static, so there is no need to instantiate the class.
 * <P><I><B>You will find the complete documentation for the original
 * SwissEphemeris package at <A HREF="http://www.astro.ch/swisseph/sweph_g.htm">
 * http://www.astro.ch/swisseph/sweph_g.htm</A>. By far most of the information
 * there is directly valid for this port to Java as well.</B></I>
 *
 * @version 1.0.0a
 */
public interface SweConst {

    // Identical in most ISO-8859 sets
    char ODEGREE_CHAR = '°'; // Unicode degree character 176
    String ODEGREE_STRING = "°"; /* degree as string, utf8 encoding */

////////////////////////////////////////////////////////////////////////////////
//// sweodef.h: ////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
    /**
     * Constant for successful return values. It is equal to 0.
     */
    int OK = 0;
    /**
     * Constant for unsuccessful return values. It is equal to -1.
     */
    int ERR = -1;
    double SE_AUNIT_TO_KM = (149597870.700);
    double SE_AUNIT_TO_LIGHTYEAR = (1.0 / 63241.07708427);
    double SE_AUNIT_TO_PARSEC = (1.0 / 206264.8062471);

    int SE_JUL_CAL = 0;
    int SE_GREG_CAL = 1;

//////////////////////////////////////////////////////////////////////////////
// swephexp.h: ///////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
    /*
     * planet numbers for the ipl parameter in swe_calc()
     */
    /**
     * Planet number for the ipl parameter in swe_calc*(). It does not calculate
     * a real planet, but the ecliptic and nutation of that date. Its value is -1.
     */
    int SE_ECL_NUT = -1;

    /**
     * planet number of the sun. It is equal to 0.
     */
    int SE_SUN = 0;
    /**
     * planet number of the moon. It is equal to 1.
     */
    int SE_MOON = 1;
    /**
     * planet number of mercury. It is equal to 2.
     */
    int SE_MERCURY = 2;
    /**
     * planet number of venus. It is equal to 3.
     */
    int SE_VENUS = 3;
    /**
     * planet number of mars. It is equal to 4.
     */
    int SE_MARS = 4;
    /**
     * planet number of jupiter. It is equal to 5.
     */
    int SE_JUPITER = 5;
    /**
     * planet number of saturn. It is equal to 6.
     */
    int SE_SATURN = 6;
    /**
     * planet number of uranus. It is equal to 7.
     */
    int SE_URANUS = 7;
    /**
     * planet number of neptune. It is equal to 8.
     */
    int SE_NEPTUNE = 8;
    /**
     * planet number of pluto. It is equal to 9.
     */
    int SE_PLUTO = 9;
    /**
     * (planet) number of the mean moon node. It is equal to 10.
     */
    int SE_MEAN_NODE = 10;
    /**
     * (planet) number of the true moon node. It is equal to 11.
     */
    int SE_TRUE_NODE = 11;
    /**
     * (planet) number of the mean apogee (== Lilith). It is equal to 12.
     */
    int SE_MEAN_APOG = 12;
    /**
     * (planet) number of the osculating apogee. It is equal to 13.
     */
    int SE_OSCU_APOG = 13;
    /**
     * planet number of the earth. It is equal to 14.
     */
    int SE_EARTH = 14;
    /**
     * planet number of chiron. It is equal to 15.
     */
    int SE_CHIRON = 15;
    /**
     * planet number of pholus. It is equal to 16.
     */
    int SE_PHOLUS = 16;
    /**
     * planet number of ceres. It is equal to 17.
     */
    int SE_CERES = 17;
    /**
     * planet number of pallas. It is equal to 18.
     */
    int SE_PALLAS = 18;
    /**
     * planet number of juno. It is equal to 19.
     */
    int SE_JUNO = 19;
    /**
     * planet number of vesta. It is equal to 20.
     */
    int SE_VESTA = 20;
    /**
     * planet number of the interpolated lunar agopee. It is equal to 21.
     */
    int SE_INTP_APOG = 21;
    /**
     * planet number of the interpolated lunar perigee. It is equal to 22.
     */
    int SE_INTP_PERG = 22;

    /**
     * Amount of defined planets. It is equal to 23.
     */
    static final int SE_NPLANETS = 23;

    int SE_PLMOON_OFFSET = 9000;

    /**
     * The offset, where asteroid numbers start. It is equal to 10000.
     */
    int SE_AST_OFFSET = 10000;
    int SE_VARUNA = (SE_AST_OFFSET + 20000);

    static final int SE_FICT_OFFSET = 40;
    int SE_FICT_OFFSET_1 = 39;
    static final int SE_FICT_MAX = 999;
    static final int SE_NFICT_ELEM = 15;

    static final int SE_COMET_OFFSET = 1000;

    static final int SE_NALL_NAT_POINTS = (SE_NPLANETS + SE_NFICT_ELEM);

    /* Hamburger or Uranian "planets" */
    /**
     * The number of the Hamburger or Uranian "planet" cupido. It is equal to 40.
     */
    int SE_CUPIDO = 40;
    /**
     * The number of the Hamburger or Uranian "planet" hades. It is equal to 41.
     */
    int SE_HADES = 41;
    /**
     * The number of the Hamburger or Uranian "planet" zeus. It is equal to 42.
     */
    int SE_ZEUS = 42;
    /**
     * The number of the Hamburger or Uranian "planet" kronos. It is equal to 43.
     */
    int SE_KRONOS = 43;
    /**
     * The number of the Hamburger or Uranian "planet" appollon. It is equal to 44.
     */
    int SE_APOLLON = 44;
    /**
     * The number of the Hamburger or Uranian "planet" admetos. It is equal to 45.
     */
    int SE_ADMETOS = 45;
    /**
     * The number of the Hamburger or Uranian "planet" vulkanus. It is equal to 46.
     */
    int SE_VULKANUS = 46;
    /**
     * The number of the Hamburger or Uranian "planet" poseidon. It is equal to 47.
     */
    int SE_POSEIDON = 47;
    /* other fictitious bodies */
    /**
     * The "planet" number of the fictitious body isis. It is equal to 48.
     */
    int SE_ISIS = 48;
    /**
     * The "planet" number of the fictitious body nibiru. It is equal to 49.
     */
    int SE_NIBIRU = 49;
    /**
     * The "planet" number of the fictitious body harrington. It is equal to 50.
     */
    int SE_HARRINGTON = 50;
    /**
     * The "planet" number of the fictitious body neptune-leverrier. It is equal to 51.
     */
    int SE_NEPTUNE_LEVERRIER = 51;
    /**
     * The "planet" number of the fictitious body neptune-adams. It is equal to 52.
     */
    int SE_NEPTUNE_ADAMS = 52;
    /**
     * The "planet" number of the fictitious body pluto-lowell. It is equal to 53.
     */
    int SE_PLUTO_LOWELL = 53;
    /**
     * The "planet" number of the fictitious body pluto-pickering. It is equal to 54.
     */
    int SE_PLUTO_PICKERING = 54;
    /**
     * The "planet" number of the fictitious body vulcan. It is equal to 55.
     */
    int SE_VULCAN = 55;
    /**
     * The "planet" number of the fictitious body &quot;white moon&quot;
     * (== Selena). It is equal to 56.
     */
    int SE_WHITE_MOON = 56;
    /**
     * The "planet" number of the fictitious body &quot;Proserpina&quot;.
     * It is equal to 57.
     */
    int SE_PROSERPINA = 57;
    /**
     * The "planet" number of the fictitious body &quot;Waldemath&quot;.
     * It is equal to 58.
     */
    int SE_WALDEMATH = 58;

    int SE_FIXSTAR = -10;

    /**
     * This is a constant to access the ascendant value in parameter ascmc[]
     * of SwissEph.swe_houses_armc() / SwissEph.swe_houses() or to specify
     * the ascendant object in transit calculations. Its value is 0.
     */
    int SE_ASC = 0;
    /**
     * This is a constant to access the medium coeli value in parameter ascmc[]
     * of SwissEph.swe_houses_armc() / SwissEph.swe_houses() or to specify
     * the medium coeli object in transit calculations. Its value is 1.
     */
    int SE_MC = 1;
    /**
     * This is a constant to access the value of the sidereal time in parameter
     * ascmc[] of SwissEph.swe_houses_armc() / SwissEph.swe_houses() or to specify
     * the sidereal time as an object in transit calculations. Its value
     * is 2.
     */
    int SE_ARMC = 2;
    /**
     * This is a constant to access the vertex value in parameter ascmc[]
     * of SwissEph.swe_houses_armc() / SwissEph.swe_houses() or to specify
     * the vertex object in transit calculations. Its value is 3.
     */
    int SE_VERTEX = 3;
    /**
     * This is a constant to access the value of the &quot;equatorial ascendant&quot;
     * in parameter ascmc[] of SwissEph.swe_houses_armc() / SwissEph.swe_houses() or
     * to specify the equatorial ascendant object in transit calculations.
     * Its value is 4.
     */
    int SE_EQUASC = 4;
    /**
     * This is a constant to access the value of the &quot;co-ascendant&quot; of
     * W. Koch in parameter ascmc[] of SwissEph.swe_houses_armc() /
     * SwissEph.swe_houses() or to specify the corresponding object in transit
     * calculations. Its value is 5.
     */
    int SE_COASC1 = 5;
    /**
     * This is a constant to access the value of the &quot;co-ascendant&quot; of
     * M. Munkasey in parameter ascmc[] of SwissEph.swe_houses_armc() /
     * SwissEph.swe_houses() or to specify the corresponding object in transit
     * calculations. Its value is 6.
     */
    int SE_COASC2 = 6;
    /**
     * This is a constant to access the polar ascendant value of M. Munkasey in
     * parameter ascmc[] of SwissEph.swe_houses_armc() / SwissEph.swe_houses()
     * or to specify the corresponding object in transit calculations.
     * Its value is 7.
     */
    int SE_POLASC = 7;
    /**
     * This is a constant to know the count of valid parameters in parameter
     * ascmc[] of SwissEph.swe_houses_armc() / SwissEph.swe_houses(). Its value
     * is 8 now, meaning, ascmc[0] to ascmc[7] contain meaningful data, even though
     * ascmc[] has to be of size 10.
     */
    int SE_NASCMC = 8;

    /*
     * only used for experimenting with various JPL ephemeris files
     * which are available at Astrodienst's internal network
     */
    int SE_DE_NUMBER = 431;
    String SE_FNAME_DE200 = "de200.eph";
    String SE_FNAME_DE403 = "de403.eph";
    String SE_FNAME_DE404 = "de404.eph";
    String SE_FNAME_DE405 = "de405.eph";
    String SE_FNAME_DE406 = "de406.eph";
    String SE_FNAME_DE431 = "de431.eph";
    String SE_FNAME_DFT = SE_FNAME_DE431;
    String SE_FNAME_DFT2 = SE_FNAME_DE406;

    /*
     * flag bits for parameter iflag in function swe_calc()
     * The flag bits are defined in such a way that iflag = 0 delivers what one
     * usually wants:
     *    - the default ephemeris (SWISS EPHEMERIS) is used,
     *    - apparent geocentric positions referring to the true equinox of date
     *      are returned.
     * If not only coordinates, but also speed values are required, use
     * flag = SEFLG_SPEED.
     *
     * The 'L' behind the number indicates that 32-bit integers (Long) are used.
     */

    /**
     * Calculate the heliocentric position of the planet. This is a constant to be used
     * as a flag to swe_calc() / swe_fixstar().
     */
    int SEFLG_HELCTR = 8;     // return heliocentric position
    /**
     * Calculate the true position of the planet versus the apparent position. This is a
     * constant to be used as a flag to swe_calc() / swe_fixstar().
     */
    int SEFLG_TRUEPOS = 16;   // return true positions, not apparent
    /**
     * Calculate the position of the planet without considering the precession, i.e.,
     * calculate J2000 equinox. This is a constant to be used as a flag to swe_calc() /
     * swe_fixstar().
     */
    int SEFLG_J2000 = 32;     // no precession, i.e. give J2000 equinox
    /**
     * Calculate the position of the planet without considering the nutation, i.e.,
     * calculate the mean equinox of the day. This is a constant to be used as a flag to
     * swe_calc() / swe_fixstar().
     */
    int SEFLG_NONUT = 64;     // no nutation, i.e. mean equinox of date
    /**
     * Calculate the speed of the planet. This calculation should not be used,
     * as it is slower and less precise than SEFLG_SPEED. This is a constant to be used
     * as a flag to swe_calc() / swe_fixstar().
     *
     * @see #SEFLG_SPEED
     */
    int SEFLG_SPEED3 = 128;   // speed from 3 positions (do not use
    // it, SEFLG_SPEED is faster and more
    // precise.)
    /**
     * Calculate the speed of the planet. This is a constant to be used
     * as a flag to swe_calc() / swe_fixstar().
     */
    int SEFLG_SPEED = 256;    // high precision speed
    /**
     * Calculate the position of the planet without considering gravitational deflection.
     * This is a constant to be used as a flag to swe_calc() / swe_fixstar().
     */
    int SEFLG_NOGDEFL = 512;  // turn off gravitational deflection
    /**
     * Calculate the position of the planet without considering the 'annual' aberration
     * of light. This is a constant to be used as a flag to swe_calc() / swe_fixstar().
     */
    int SEFLG_NOABERR = 1024; // turn off 'annual' aberration of light

    int SEFLG_ASTROMETRIC = (SEFLG_NOABERR | SEFLG_NOGDEFL); /* astrometric position,
     * i.e. with light-time, but without aberration and
     * light deflection */
    /**
     * Calculate the position of the planet in the earth equator coordinate system. This
     * is a constant to be used as a flag to swe_calc() / swe_fixstar().
     */
    int SEFLG_EQUATORIAL = 2 * 1024; // equatorial positions are wanted
    /**
     * Return cartesian coordinates instead of polar coordinates. This is a constant to be
     * used as a flag to swe_calc() / swe_fixstar().
     */
    int SEFLG_XYZ = 4 * 1024;     // cartesian, not polar, coordinates
    /**
     * Return coordinates in radians instead of degrees. This is a constant to be used as
     * a flag to swe_calc() / swe_fixstar().
     */
    int SEFLG_RADIANS = 8 * 1024; // coordinates in radians, not degrees
    /**
     * Calculate barycentric positions. This is a constant to be used as a flag to
     * swe_calc() / swe_fixstar().
     */
    int SEFLG_BARYCTR = 16 * 1024; // barycentric positions
    /**
     * Calculate topocentric positions. This is a constant to be used as a flag to
     * swe_calc() / swe_fixstar().
     */
    int SEFLG_TOPOCTR = 32 * 1024; // topocentric positions
    int SEFLG_ORBEL_AA = SEFLG_TOPOCTR; /* used for Astronomical Almanac mode in
     * calculation of Kepler elipses */
    int SEFLG_TROPICAL = (0);          /* tropical position (default) */
    /**
     * Calculate sidereal positions. This is a constant to be used as a flag to
     * swe_calc() / swe_fixstar().
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SEFLG_SIDEREAL = 64 * 1024; // sidereal positions
    /**
     * Calculate in the "International Celestial Reference System." This is a
     * constant to be used as a flag to swe_calc() / swe_fixstar().
     */
    int SEFLG_ICRS = 128 * 1024; // ICRS (DE406 reference frame)
    int SEFLG_DPSIDEPS_1980 = (256 * 1024); /* reproduce JPL Horizons
     * 1962 - today to 0.002 arcsec. */
    int SEFLG_JPLHOR = SEFLG_DPSIDEPS_1980;
    int SEFLG_JPLHOR_APPROX = (512 * 1024);   /* approximate JPL Horizons 1962 - today */
    int SEFLG_CENTER_BODY = (1024 * 1024);  /* calculate position of center of body (COB)
                                                of planet, not barycenter of its system */
    int SEFLG_TEST_PLMOON = (2 * 1024 * 1024 | SEFLG_J2000 | SEFLG_ICRS | SEFLG_HELCTR | SEFLG_TRUEPOS);  /* test raw data in files sepm9* */
    /**
     * calculate transits over a longitude. This is a constant to be used as a
     * flag to the TransitCalculator constructors.
     *
     * @see swisseph.TCPlanet#TCPlanet(SwissEph, int, int, double)
     * @see swisseph.TCPlanetPlanet#TCPlanetPlanet(SwissEph, int, int, int, double)
     */
    int SEFLG_TRANSIT_LONGITUDE = 128 * 1024;
    /**
     * calculate transits over a latitude. This is a constant to be used as a
     * flag to the TransitCalculator constructors.
     *
     * @see swisseph.TCPlanet#TCPlanet(SwissEph, int, int, double)
     * @see swisseph.TCPlanetPlanet#TCPlanetPlanet(SwissEph, int, int, int, double)
     */
    int SEFLG_TRANSIT_LATITUDE = 256 * 1024;
    /**
     * calculate transits over a distance in AU. This is a constant to be used
     * as a flag to the TransitCalculator constructors.
     *
     * @see swisseph.TCPlanet#TCPlanet(SwissEph, int, int, double)
     * @see swisseph.TCPlanetPlanet#TCPlanetPlanet(SwissEph, int, int, int, double)
     */
    int SEFLG_TRANSIT_DISTANCE = 512 * 1024;
    /**
     * calculate transits over a (longitudinal, latitudinal or distance)
     * speed value. This is a constant to be used as a flag the
     * TransitCalculator constructors.
     *
     * @see swisseph.TCPlanet#TCPlanet(SwissEph, int, int, double)
     * @see swisseph.TCPlanetPlanet#TCPlanetPlanet(SwissEph, int, int, int, double)
     */
    int SEFLG_TRANSIT_SPEED = 1024 * 1024;
    /**
     * calculate yoga transits, this means consider the SUM of two planets
     * positions or speeds instead of the difference. This is a constant to be
     * used as a flag to the TransitCalculator constructors.
     *
     * @see swisseph.TCPlanetPlanet#TCPlanetPlanet(SwissEph, int, int, int, double)
     */
    int SEFLG_YOGA_TRANSIT = 2048 * 1024;
    /**
     * calculate partile transits. This calculates the times when two planets
     * have an identically integer degree value in their respective sign ==
     * 30 degree section: (int)(lon1 % 30) == (int)(lon2 % 30). This is a
     * flag to the TransitCalculator constructors.
     *
     * @see swisseph.TCPlanetPlanet#TCPlanetPlanet(SwissEph, int, int, int, double)
     */
    int SEFLG_PARTILE_TRANSIT_START = 4096 * 1024;
    /**
     * calculate partile transits. This calculates the time when two planets
     * aren't partile anymore. So they don't have an identically integer
     * degree value in their respective sign == 30 degree section:
     * (int)(lon1 % 30) != (int)(lon2 % 30). This is a flag to the
     * TransitCalculator constructors.
     *
     * @see swisseph.TCPlanetPlanet#TCPlanetPlanet(SwissEph, int, int, int, double)
     */
    int SEFLG_PARTILE_TRANSIT_END = 8192 * 1024;
    /**
     * calculate partile transits. This calculates the time when two planets
     * gain or loose a partile aspect, depending on if they have a partile
     * aspect on the starting date or not. This is a flag to the
     * TransitCalculator constructors.
     *
     * @see swisseph.TCPlanetPlanet#TCPlanetPlanet(SwissEph, int, int, int, double)
     * @see #SEFLG_PARTILE_TRANSIT_START
     * @see #SEFLG_PARTILE_TRANSIT_END
     */
    int SEFLG_PARTILE_TRANSIT = SEFLG_PARTILE_TRANSIT_START + SEFLG_PARTILE_TRANSIT_END;

    int SE_HOUSE1 = -1;
    int SE_HOUSE2 = -2;
    int SE_HOUSE3 = -3;
    int SE_HOUSE4 = -4;
    int SE_HOUSE5 = -5;
    int SE_HOUSE6 = -6;
    int SE_HOUSE7 = -7;
    int SE_HOUSE8 = -8;
    int SE_HOUSE9 = -9;
    int SE_HOUSE10 = -10;
    int SE_HOUSE11 = -11;
    int SE_HOUSE12 = -12;
    int SE_HSYS_PLACIDUS = 'P';
    int SE_HSYS_KOCH = 'K';
    int SE_HSYS_PORPHYRIUS = 'O';
    int SE_HSYS_REGIOMONTANUS = 'R';
    int SE_HSYS_CAMPANUS = 'C';
    int SE_HSYS_EQUAL = 'E';
    int SE_HSYS_VEHLOW = 'V';
    int SE_HSYS_MERIDIAN = 'X';
    int SE_HSYS_HORIZONTAL = 'H';
    int SE_HSYS_POLICH_PAGE = 'T';
    int SE_HSYS_ALCABITIUS = 'B';
    int SE_HSYS_GAUQUELIN_SECTORS = 'G';
    int SE_HSYS_MORINUS = 'M';
    int SE_HSYS_KRUSINSKI = 'U';
    int SE_HSYS_WHOLE_SIGN = 'W';

    /**
     * Use the calculation routines that use the ephemeris data files of the
     * Solar System Dynamics Group of the Jet Propulsion Laboratory of NASA
     * <A HREF="http://ssd.jpl.nasa.gov/">http://ssd.jpl.nasa.gov/</A>. This
     * requires you to have the ephemeris data files from JPL (DE406: about
     * 9.5&nbsp;MB per 300&nbsp;years). They can be obtained from
     * <A HREF="ftp://navigator.jpl.nasa.gov/pub/ephem/export">
     * ftp://navigator.jpl.nasa.gov/pub/ephem/export</A> (as of December 21, 2000).
     *
     * @see SwissEph#swe_set_ephe_path(java.lang.String)
     */
    int SEFLG_JPLEPH = 1;
    /**
     * Use the calculation routines of Swiss Ephemeris from Astrodienst&nbsp;AG
     * Z&uuml;rich <A HREF="http://www.astro.com">http://www.astro.com</A>. This
     * requires you to have the data files for SwissEphemeris available and in
     * the search path for the ephemeris files.<BR>
     * Basically, these routines are
     * identical to the JPL routines, but the data files are <I>much</I> more
     * compressed (about one 10th of the original JPL files), and the exactness
     * is retained to 1/1000 of an arc second.
     *
     * @see SwissEph#swe_set_ephe_path(java.lang.String)
     */
    int SEFLG_SWIEPH = 2;
    /**
     * Use the Moshier semi-analytical calculation routines.
     * These routines are the slowest (about 10 times slower than
     * JPL or SWISS EPHEMERIS) and most
     * inaccurate, but they do not require any additional data files.<BR>
     * The accuracy is about 1 arc seconds for the planets and a few arc seconds
     * for the moon. Unfortunately, the deviation for true lunar nodes may be
     * up to one arc minute.
     */
    int SEFLG_MOSEPH = 4;
    /**
     * Defines the default method of calculation as SEFLG_SWIEPH.
     *
     * @see swisseph.SweConst#SEFLG_JPLEPH
     * @see swisseph.SweConst#SEFLG_SWIEPH
     * @see swisseph.SweConst#SEFLG_MOSEPH
     */
    int SEFLG_DEFAULTEPH = SEFLG_SWIEPH;
    int SEFLG_EPHMASK = SEFLG_JPLEPH | SEFLG_SWIEPH | SEFLG_MOSEPH;

    int SE_SIDBITS = 256;
    /* for projection onto ecliptic of t0 */
    /**
     * This specifies that sidereal calculations are done via a
     * projection onto the ecliptic at date t0.
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDBIT_ECL_T0 = 256;
    /* for projection onto solar system plane */
    /**
     * This specifies that sidereal calculations are done via a
     * projection onto the solar system plane.
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDBIT_SSY_PLANE = 512;
    /* with user-defined ayanamsha, t0 is UT */
    int SE_SIDBIT_USER_UT = 1024;
    /* ayanamsha measured on ecliptic of date;
     * see commentaries in sweph.c:swi_get_ayanamsa_ex(). */
    int SE_SIDBIT_ECL_DATE = 2048;
    /* test feature: don't apply constant offset to ayanamsha
     * see commentary above sweph.c:get_aya_correction() */
    int SE_SIDBIT_NO_PREC_OFFSET = 4096;
    /* test feature: calculate ayanamsha using its original precession model */
    int SE_SIDBIT_PREC_ORIG = 8192;
    /* sidereal modes (ayanamsas) */
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha).
     * Fagan/Bradley is used as default, if nothing other is specified
     * via swe_set_sid_mode.
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_FAGAN_BRADLEY = 0;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * defined by Lahiri.
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_LAHIRI = 1;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * defined by De Luce.
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_DELUCE = 2;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * defined by Raman.
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_RAMAN = 3;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * defined by Ushashashi.
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_USHASHASHI = 4;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * defined by Krishnamurti.
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_KRISHNAMURTI = 5;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * defined by Djwhal Khool.
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_DJWHAL_KHUL = 6;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * defined by Sri Yukteshwar.
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_YUKTESHWAR = 7;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * defined by JN Bhasin.
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_JN_BHASIN = 8;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * defined by Babylonian, Kugler 1.
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_BABYL_KUGLER1 = 9;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * defined by Babylonian, Kugler 2.
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_BABYL_KUGLER2 = 10;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * defined by Babylonian, Kugler 3.
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_BABYL_KUGLER3 = 11;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * defined by Babylonian, Huber.
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_BABYL_HUBER = 12;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * defined by Babylonian, Mercier.
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_BABYL_ETPSC = 13;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * defining t0 by Aldebaran at 15 degrees Taurus.
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_ALDEBARAN_15TAU = 14;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) by
     * defined by Hipparchos.
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_HIPPARCHOS = 15;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * defined by Sassanian.
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_SASSANIAN = 16;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * defined by the galactic center being at 0 degrees Sagittarius.
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_GALCENT_0SAG = 17;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * J2000 (Julian day 2451545.0, 2000 January 1.5)
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_J2000 = 18;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * J1900 (Julian day 2415020.0, 1900 January 0.5).
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_J1900 = 19;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * B1950 (Julian day 2433282.42345905, 1950 January 0.923).
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_B1950 = 20;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * SURYASIDDHANTA (Suryasiddhanta, assuming ingress of mean Sun into
     * Aries at point of mean equinox of date on 21.3.499, noon, Ujjain
     * (75.7684565 E) = 7:30:31.57 UT).
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_SURYASIDDHANTA = 21;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * SURYASIDDHANTA_MSUN (Suryasiddhanta, assuming ingress of mean Sun
     * into Aries at true position of mean Sun at same epoch).
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_SURYASIDDHANTA_MSUN = 22;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * ARYABHATA (Aryabhata, same date as SE_SIDM_SURYASIDDHANTA_MSUN, but UT 6:56:55.57).
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_ARYABHATA = 23;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * ARYABHATA_MSUN (Aryabhata, analogous SE_SIDM_SURYASIDDHANTA_MSUN).
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_ARYABHATA_MSUN = 24;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * SE_SIDM_SS_REVATI (SS, Revati/zePsc at polar long. 359°50').
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_SS_REVATI = 25;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * SE_SIDM_SS_CITRA (SS, Citra/Spica at polar long. 180°).
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_SS_CITRA = 26;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * SE_SIDM_TRUE_CITRA (True Spica (Spica exactly at 0 Libra)
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_TRUE_CITRA = 27;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * SE_SIDM_TRUE_REVATI (True Revati (zeta Psc exactly at 0 Aries))
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_TRUE_REVATI = 28;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * SE_SIDM_TRUE_PUSHYA (True Pushya (delta Cnc exactly a 16 Cancer))
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_TRUE_PUSHYA = 29;
    int SE_SIDM_GALCENT_RGILBRAND = 30;
    int SE_SIDM_GALEQU_IAU1958 = 31;
    int SE_SIDM_GALEQU_TRUE = 32;
    int SE_SIDM_GALEQU_MULA = 33;
    int SE_SIDM_GALALIGN_MARDYKS = 34;
    int SE_SIDM_TRUE_MULA = 35;
    int SE_SIDM_GALCENT_MULA_WILHELM = 36;
    int SE_SIDM_ARYABHATA_522 = 37;
    int SE_SIDM_BABYL_BRITTON = 38;
    int SE_SIDM_TRUE_SHEORAN = 39;
    int SE_SIDM_GALCENT_COCHRANE = 40;
    int SE_SIDM_GALEQU_FIORENZA = 41;
    int SE_SIDM_VALENS_MOON = 42;
    int SE_SIDM_LAHIRI_1940 = 43;
    int SE_SIDM_LAHIRI_VP285 = 44;
    int SE_SIDM_KRISHNAMURTI_VP291 = 45;
    int SE_SIDM_LAHIRI_ICRC = 46;
    /**
     * A constant to be used for specifying the sidereal mode (ayanamsha) as
     * defined by the user in the additional two parameters of swe_set_sid_mode().
     *
     * @see SwissEph#swe_set_sid_mode(int, double, double)
     */
    int SE_SIDM_USER = 255;
    int SE_NSIDM_PREDEF = 47;
    /* used for swe_nod_aps(): */


    static final int SE_MAX_STNAME = 256;   // maximum size of fixstar name;
    // the parameter star in swe_fixstar
    // must allow twice this space for
    // the returned star name.

    /* defines for eclipse computations */




    /* used for swe_nod_aps(): */
    /**
     * Used for swe_nod_aps(): mean nodes/apsides
     */
    int SE_NODBIT_MEAN = 1; // mean nodes/apsides
    /**
     * Used for swe_nod_aps(): osculating nodes/apsides
     */
    int SE_NODBIT_OSCU = 2; // osculating nodes/apsides
    /**
     * Used for swe_nod_aps(): osculating nodes/apsides, but motion about solar
     * system barycenter is considered
     */
    int SE_NODBIT_OSCU_BAR = 4; // same, but motion about solar
    // system barycenter is considered
    /**
     * Used for swe_nod_aps(): focal point of orbit instead of aphelion
     */
    int SE_NODBIT_FOPOINT = 256; // focal point of orbit
    // instead of aphelion


    /* defines for function swe_split_deg() (in swephlib.c) */
    int SE_SPLIT_DEG_ROUND_SEC = 1;
    int SE_SPLIT_DEG_ROUND_MIN = 2;
    int SE_SPLIT_DEG_ROUND_DEG = 4;
    int SE_SPLIT_DEG_ZODIACAL = 8;
    int SE_SPLIT_DEG_NAKSHATRA = 1024;
    /**
     * SwissLib.swe_split_deg(): Don't round to next sign, e.g. 29.9999999 will
     * be rounded to 29d59'59" (or 29d59' or 29d)
     */
    int SE_SPLIT_DEG_KEEP_SIGN = 16;
    /* don't round to next sign,
     * e.g. 29.9999999 will be rounded
     * to 29d59'59" (or 29d59' or 29d) */
    /**
     * SwissLib.swe_split_deg(): Don't round to next degree, e.g. 13.9999999 will
     * be rounded to 13d59'59" (or 13d59' or 13d)
     */
    int SE_SPLIT_DEG_KEEP_DEG = 32;
    /* don't round to next degree
     * e.g. 13.9999999 will be rounded
     * to 13d59'59" (or 13d59' or 13d) */

    /* for heliacal functions */
    int SE_HELIACAL_RISING = 1;
    int SE_HELIACAL_SETTING = 2;
    int SE_MORNING_FIRST = SE_HELIACAL_RISING;
    int SE_EVENING_LAST = SE_HELIACAL_SETTING;
    int SE_EVENING_FIRST = 3;
    int SE_MORNING_LAST = 4;
    int SE_ACRONYCHAL_RISING = 5;  /* still not implemented */
    int SE_ACRONYCHAL_SETTING = 6;  /* still not implemented */
    int SE_COSMICAL_SETTING = SE_ACRONYCHAL_SETTING;

    int SE_HELFLAG_LONG_SEARCH = 128;
    int SE_HELFLAG_HIGH_PRECISION = 256;
    int SE_HELFLAG_OPTICAL_PARAMS = 512;
    int SE_HELFLAG_NO_DETAILS = 1024;
    int SE_HELFLAG_SEARCH_1_PERIOD = (1 << 11); /*  2048 */
    int SE_HELFLAG_VISLIM_DARK = (1 << 12); /*  4096 */
    int SE_HELFLAG_VISLIM_NOMOON = (1 << 13); /*  8192 */
    int SE_HELFLAG_VISLIM_PHOTOPIC = (1 << 14); /* 16384 */
    int SE_HELFLAG_AV = (1 << 15); /* 32768 */
    int SE_HELFLAG_AVKIND_VR = (1 << 15); /* 32768 */
    int SE_HELFLAG_AVKIND_PTO = (1 << 16);
    int SE_HELFLAG_AVKIND_MIN7 = (1 << 17);
    int SE_HELFLAG_AVKIND_MIN9 = (1 << 18);
    int SE_HELFLAG_AVKIND = SE_HELFLAG_AVKIND_VR | SE_HELFLAG_AVKIND_PTO | SE_HELFLAG_AVKIND_MIN7 | SE_HELFLAG_AVKIND_MIN9;
    double TJD_INVALID = 99999999.0;
//  int SIMULATE_VICTORVB           = 1;	// is a #define for SweHel

    int SE_HELIACAL_LONG_SEARCH = 128;
    int SE_HELIACAL_HIGH_PRECISION = 256;
    int SE_HELIACAL_OPTICAL_PARAMS = 512;
    int SE_HELIACAL_NO_DETAILS = 1024;
    int SE_HELIACAL_SEARCH_1_PERIOD = (1 << 11); /*  2048 */
    int SE_HELIACAL_VISLIM_DARK = (1 << 12); /*  4096 */
    int SE_HELIACAL_VISLIM_NOMOON = (1 << 13); /*  8192 */
    int SE_HELIACAL_VISLIM_PHOTOPIC = (1 << 14); /* 16384 */
    int SE_HELIACAL_AVKIND_VR = (1 << 15); /* 32768 */
    int SE_HELIACAL_AVKIND_PTO = (1 << 16);
    int SE_HELIACAL_AVKIND_MIN7 = (1 << 17);
    int SE_HELIACAL_AVKIND_MIN9 = (1 << 18);
    int SE_HELIACAL_AVKIND = (SE_HELFLAG_AVKIND_VR | SE_HELFLAG_AVKIND_PTO | SE_HELFLAG_AVKIND_MIN7 | SE_HELFLAG_AVKIND_MIN9);

    int SE_PHOTOPIC_FLAG = 0;
    int SE_SCOTOPIC_FLAG = 1;
    int SE_MIXEDOPIC_FLAG = 2;


    /* for setGlobalTidalAcc() and ephemeris-dependent delta t:
     * intrinsic tidal acceleration in the mean motion of the moon,
     * not given in the parameters list of the ephemeris files but computed
     * by Chapront/Chapront-Touzé/Francou A&A 387 (2002), p. 705.
     */
    double SE_TIDAL_DE200 = (-23.8946);
    double SE_TIDAL_DE403 = (-25.580);  /* was (-25.8) until V. 1.76.2 */
    double SE_TIDAL_DE404 = (-25.580);  /* was (-25.8) until V. 1.76.2 */
    double SE_TIDAL_DE405 = (-25.826);  /* was (-25.7376) until V. 1.76.2 */
    double SE_TIDAL_DE406 = (-25.826);  /* was (-25.7376) until V. 1.76.2 */
    double SE_TIDAL_DE421 = (-25.85);   /* JPL Interoffice Memorandum 14-mar-2008 on DE421 Lunar Orbit */
    double SE_TIDAL_DE422 = (-25.85);   /* JPL Interoffice Memorandum 14-mar-2008 on DE421 (sic!) Lunar Orbit */
    double SE_TIDAL_DE430 = (-25.82);   /* JPL Interoffice Memorandum 9-jul-2013 on DE430 Lunar Orbit */
    double SE_TIDAL_DE431 = (-25.80);   /* IPN Progress Report 42-196 • February 15, 2014, p. 15 */
    double SE_TIDAL_DE441 = (-25.936);   /* unpublished value, from email by Jon Giorgini to DK on 11 Apr 2021 */
    double SE_TIDAL_26 = (-26.0);
    double SE_TIDAL_STEPHENSON_2016 = (-25.85);
    double SE_TIDAL_DEFAULT = SE_TIDAL_DE431;
    double SE_TIDAL_AUTOMATIC = 999999;
    double SE_TIDAL_MOSEPH = SE_TIDAL_DE404;
    double SE_TIDAL_SWIEPH = SE_TIDAL_DEFAULT;
    double SE_TIDAL_JPLEPH = SE_TIDAL_DEFAULT;

    int SE_MODEL_PREC_LONGTERM = 0;
    int SE_MODEL_PREC_SHORTTERM = 1;
    int SE_MODEL_NUT = 2;
    int SE_MODEL_SIDT = 3;
    int SE_MODEL_BIAS = 4;
    int SE_MODEL_JPLHOR_MODE = 5;
    int SE_MODEL_JPLHORA_MODE = 6;
    int SE_MODEL_DELTAT = 7;

    /* precession models */
    int SEMOD_PREC_IAU_1976 = 1;
    int SEMOD_PREC_IAU_2000 = 2;
    int SEMOD_PREC_IAU_2006 = 3;
    int SEMOD_PREC_BRETAGNON_2003 = 4;
    int SEMOD_PREC_LASKAR_1986 = 5;
    int SEMOD_PREC_SIMON_1994 = 6;
    int SEMOD_PREC_WILLIAMS_1994 = 7;
    int SEMOD_PREC_VONDRAK_2011 = 8;
    int SEMOD_PREC_DEFAULT = SEMOD_PREC_VONDRAK_2011;
    /* former implementations of the used
     * IAU 1976, 2000 and 2006 for a limited time range
     * in combination with a different model for
     * long term precession.
    #define SEMOD_PREC_DEFAULT_SHORT SEMOD_PREC_IAU_2000
     */
    int SEMOD_PREC_DEFAULT_SHORT = SEMOD_PREC_VONDRAK_2011;

    /* nutation models */
    int SEMOD_NNUT = 5;
    int SEMOD_NUT_IAU_1980 = 1;
    int SEMOD_NUT_IAU_CORR_1987 = 2; /* Herring's (1987) corrections to IAU 1980
     * nutation series. AA (1996) neglects them.*/
    int SEMOD_NUT_IAU_2000A = 3; /* very time consuming ! */
    int SEMOD_NUT_IAU_2000B = 4; /* fast, but precision of milli-arcsec */
    int SEMOD_NUT_WOOLARD = 5;
    int SEMOD_NUT_DEFAULT = SEMOD_NUT_IAU_2000B;  /* fast, but precision of milli-arcsec */

    /* methods for sidereal time */
    int SEMOD_NSIDT = 4;
    int SEMOD_SIDT_LONGTERM = 1;
    int SEMOD_SIDT_IERS_CONV_2010 = 2;
    int SEMOD_SIDT_PREC_MODEL = 3;
    int SEMOD_SIDT_DEFAULT = SEMOD_SIDT_LONGTERM;

    /* frame bias methods */
    int SEMOD_NBIAS = 3;
    int SEMOD_BIAS_IAU2000 = 1;  /* use frame bias matrix IAU 2000 */
    int SEMOD_BIAS_IAU2006 = 2;  /* use frame bias matrix IAU 2000 */
    int SEMOD_BIAS_DEFAULT = SEMOD_BIAS_IAU2006;

    /* methods of JPL Horizons (iflag & SEFLG_JPLHOR),
     * using daily dpsi, deps;  see explanations below */
    int SEMOD_JPLHOR_EXTENDED_1800 = 1;  /* daily dpsi and deps from file are
     * limited to 1962 - today. JPL uses the
     * first and last value for all  dates
     * beyond this time range. */
    int SEMOD_JPLHOR_NOT_EXTENDED = 2;  /* outside the available time range
     * 1962 - today default to SEFLG_JPLHOR_APROX */
    int SEMOD_JPLHOR_DEFAULT = SEMOD_JPLHOR_EXTENDED_1800;
    /* SEMOD_JPLHOR_EXTENDED_1800, if combined with SEFLG_JPLHOR provides good
     * agreement with JPL Horizons for 1800 - today. However, Horizons uses
     * correct dpsi and deps only after 20-jan-1962. For all dates before that
     * it uses dpsi and deps of 20-jan-1962, which provides a continuous
     * ephemeris, but does not make sense otherwise.
     * Before 1800, even this option does not provide agreement with Horizons,
     * because Horizons uses a different precession model (Owen 1986)
     * before 1800, which is not included in the Swiss Ephemeris.
     * SEMOD_JPLHOR_NOT_EXTENDED causes the program to default to SEFLG_JPLHOR_APPROX,
     * if the date is outside the time range 1962 - today, where values
     * for dpsi and deps are given.
     * Note that this will result in a non-continuous ephemeris near
     * 20-jan-1962 and current years.
     */

    /* methods of approximation of JPL Horizons (iflag & SEFLG_JPLHORA),
     * without dpsi, deps; see explanations below */
    int SEMOD_JPLHORA_1 = 1;
    int SEMOD_JPLHORA_2 = 2;
    int SEMOD_JPLHORA_DEFAULT = SEMOD_JPLHORA_1;

    /* With SEMOD_JPLHORA_1, planetary positions are always calculated
     * using a recent precession/nutation model. Frame bias matrix is applied
     * with some correction to RA and another correction is added to epsilon.
     * This provides a very good approximation of JPL Horizons positions.
     * With SEMOD_JPLHORA_2, frame bias as r$ecommended by IERS Conventions 2003
     * and 2010 is *not* applied. Instead, dpsi_bias and deps_bias are added to
     * nutation. This procedure is found in some older astronomical software.
     * Equatorial apparent positions will be close to JPL Horizons
     * (within a few mas) beetween 1962 and current years. Ecl. longitude
     * will be good, latitude bad.
     */

    int SEMOD_DELTAT_ESPENAK_MEEUS_2006 = 1;
    int SEMOD_DELTAT_STEPHENSON_MORRISON_2004 = 2;
    int SEMOD_DELTAT_DEFAULT = SEMOD_DELTAT_ESPENAK_MEEUS_2006;



    /*
     * ephemeris path
     * this defines where ephemeris files are expected if the function
     * swe_set_ephe_path() is not called by the application.
     * Normally, every application should make this call to define its
     * own place for the ephemeris files.
     */
    /**
     * Ephemeris path.<P>
     * This defines where ephemeris files are expected if the function
     * swe_set_ephe_path() is not called by the application.<P>
     * It is defined as being <CODE>".:./ephe:/users/ephe2/:/users/ephe/"</CODE>.
     *
     * @see SwissEph#swe_set_ephe_path(java.lang.String)
     */
// ifndef SE_EPHE_PATH -- This is not currently possible to be done with this environment
    String SE_EPHE_PATH = ".:./ephe:/users/ephe2/:/users/ephe/";
                        /* At Astrodienst, we maintain two ephemeris areas for
                           the thousands of asteroid files:
                           the short files in /users/ephe/ast*,
                           the long file in /users/ephe2/ast*. */
// endif /* SE_EPHE_PATH */

    static final String SE_STARFILE_OLD = "fixstars.cat";
    static final String SE_STARFILE = "sefstars.txt";
    static final String SE_ASTNAMFILE = "seasnam.txt";
    /**
     * The name of the file containing the orbital elements of ficticious planets.
     */
    static final String SE_FICTFILE = "seorbel.txt";


    /* defines for eclipse computations */

    int SE_ECL_CENTRAL = 1;
    int SE_ECL_NONCENTRAL = 2;
    int SE_ECL_TOTAL = 4;
    /**
     * Annular eclipse. This is an eclipse, where the moon is seen smaller
     * than the sun, so you get a "ring" of the sun around the moon.
     */
    int SE_ECL_ANNULAR = 8;
    int SE_ECL_PARTIAL = 16;
    int SE_ECL_ANNULAR_TOTAL = 32;
    int SE_ECL_HYBRID = 32;  // = annular-total
    int SE_ECL_PENUMBRAL = 64;
    int SE_ECL_ALLTYPES_SOLAR = SE_ECL_CENTRAL | SE_ECL_NONCENTRAL | SE_ECL_TOTAL | SE_ECL_ANNULAR | SE_ECL_PARTIAL | SE_ECL_ANNULAR_TOTAL;
    int SE_ECL_ALLTYPES_LUNAR = SE_ECL_TOTAL | SE_ECL_PARTIAL | SE_ECL_PENUMBRAL;
    int SE_ECL_VISIBLE = 128;
    int SE_ECL_MAX_VISIBLE = 256;
    /**
     * Begin of partial eclipse.
     * This is the time, when the moon touches the sun the first time.
     */
    int SE_ECL_1ST_VISIBLE = 512;    /* begin of partial eclipse */
    /**
     * Begin of partial eclipse.
     * This is the time, when the moon touches the sun the first time.
     */
    int SE_ECL_PARTBEG_VISIBLE = 512;    /* begin of partial eclipse */
    /**
     * Begin of total eclipse.
     * This is the time, when the sun completely disappears.
     */
    int SE_ECL_2ND_VISIBLE = 1024;    /* begin of total eclipse */
    /**
     * Begin of total eclipse.
     * This is the time, when the sun completely disappears.
     */
    int SE_ECL_TOTBEG_VISIBLE = 1024;    /* begin of total eclipse */
    /**
     * End of total eclipse.
     * This is the time, when the sun starts to reappear.
     */
    int SE_ECL_3RD_VISIBLE = 2048;    /* end of total eclipse */
    /**
     * End of total eclipse.
     * This is the time, when the sun starts to reappear.
     */
    int SE_ECL_TOTEND_VISIBLE = 2048;    /* end of total eclipse */
    /**
     * End of partial eclipse.
     * This is the time, when the moon and the sun finally separate.
     */
    int SE_ECL_4TH_VISIBLE = 4096;    /* end of partial eclipse */
    /**
     * End of partial eclipse.
     * This is the time, when the moon and the sun finally separate.
     */
    int SE_ECL_PARTEND_VISIBLE = 4096;    /* end of partial eclipse */
    /**
     * Begin of penumbral eclipse.
     */
    int SE_ECL_PENUMBBEG_VISIBLE = 8192;    /* begin of penumbral eclipse */
    /**
     * End of penumbral eclipse.
     */
    int SE_ECL_PENUMBEND_VISIBLE = 16384;   /* end of penumbral eclipse */
    /**
     * Occultation begins during the day
     */
    int SE_ECL_OCC_BEG_DAYLIGHT = 8192;    /* occultation begins during the day */
    /**
     * Occultation ends during the day
     */
    int SE_ECL_OCC_END_DAYLIGHT = 16384;   /* occultation ends during the day */
    /**
     * Just check if the next conjunction of the moon with
     * a planet is an occultation; don't search further.
     */
    int SE_ECL_ONE_TRY = 32 * 1024;

    /* for swe_rise_transit() */
    /**
     * Calculate the time of sunrise. This is a constant to be used as a flag to
     * swe_rise_trans().
     */
    int SE_CALC_RISE = 1;
    /**
     * Calculate the time of sunset. This is a constant to be used as a flag to
     * swe_rise_trans().
     */
    int SE_CALC_SET = 2;
    /**
     * Calculate the time of the upper meridian transit (southern for northern geo.
     * latitudes). This is a constant to be used as a flag to swe_rise_trans().
     */
    int SE_CALC_MTRANSIT = 4;
    /**
     * Calculate the time of the lower meridian transit (northern, below the
     * horizon). This is a constant to be used as a flag to swe_rise_trans().
     */
    int SE_CALC_ITRANSIT = 8;
    /**
     * Add this to SE_CALC_RISE/SET, if rise or set of disc center is requested.
     * This is a constant to be used as a flag to swe_rise_trans().
     *
     * @see swisseph.SwissEph#swe_rise_trans(double, int, java.lang.StringBuilder, int, int, double[], double, double, swisseph.DblObj, java.lang.StringBuilder)
     */
    int SE_BIT_DISC_CENTER = 256;
    /* to be or'ed to SE_CALC_RISE/SET
     * if rise or set of disc center is
     * required */
    /**
     * Add this to SE_CALC_RISE/SET, if rise or set of disc bottom is requested.
     * This is a constant to be used as a flag to swe_rise_trans().
     *
     * @see swisseph.SwissEph#swe_rise_trans(double, int, java.lang.StringBuilder, int, int, double[], double, double, swisseph.DblObj, java.lang.StringBuilder)
     */
    int SE_BIT_DISC_BOTTOM = 8192;
    /* to be or'ed to SE_CALC_RISE/SET,
     * if rise or set of lower limb of
     * disc is requried */
    int SE_BIT_GEOCTR_NO_ECL_LAT = 128; /* use geocentric rather than topocentric
                                        position of object and
					ignore its ecliptic latitude */
    /**
     * Add this to SE_CALC_RISE/SET, if refraction should not to be considered.
     * This is a constant to be used as a flag to swe_rise_trans().
     *
     * @see swisseph.SwissEph#swe_rise_trans(double, int, java.lang.StringBuilder, int, int, double[], double, double, swisseph.DblObj, java.lang.StringBuilder)
     */
    int SE_BIT_NO_REFRACTION = 512;
    /* to be or'ed to SE_CALC_RISE/SET,
     * if refraction is not to be considered */
    int SE_BIT_CIVIL_TWILIGHT = 1024; /* to be or'ed to SE_CALC_RISE/SET */
    int SE_BIT_NAUTIC_TWILIGHT = 2048; /* to be or'ed to SE_CALC_RISE/SET */
    int SE_BIT_ASTRO_TWILIGHT = 4096; /* to be or'ed to SE_CALC_RISE/SET */
    int SE_BIT_FIXED_DISC_SIZE = (16 * 1024); /* or'ed to SE_CALC_RISE/SET:
     * neglect the effect of distance on
     * disc size */
    int SE_BIT_FORCE_SLOW_METHOD = 32768; /* This is only an Astrodienst in-house
     * test flag. It forces the usage
     * of the old, slow calculation of
     * risings and settings. */
    int SE_BIT_HINDU_RISING = (SE_BIT_DISC_CENTER | SE_BIT_NO_REFRACTION | SE_BIT_GEOCTR_NO_ECL_LAT);

    /* for swe_azalt() and swe_azalt_rev() */
    /**
     * This is a constant to be used as a flag to swe_azalt(). It determines the
     * meaning of the contents of parameter xin[]. xin[0]=&nbsp;ecl. long.,
     * xin[1]=&nbsp;ecl. lat.. xin[2]=distance is not required.
     *
     * @see swisseph.SwissEph#swe_azalt(double, int, double[], double, double, double[], double[])
     */
    int SE_ECL2HOR = 0;
    /**
     * This is a constant to be used as a flag to swe_azalt(). It determines the
     * meaning of the contents of parameter xin[]. xin[0]=&nbsp;rectascension,
     * xin[1]=&nbsp;declination. xin[2]=distance is not required.
     *
     * @see swisseph.SwissEph#swe_azalt(double, int, double[], double, double, double[], double[])
     */
    int SE_EQU2HOR = 1;
    /**
     * This is a constant to be used as a flag to swe_azalt_rev(). It determines
     * the meaning of the contents of output parameter xout[]. xout[0]=&nbsp;ecl.
     * long., xout[1]=&nbsp;ecl. lat..
     *
     * @see swisseph.SwissEph#swe_azalt_rev(double, int, double[], double[], double[])
     */
    int SE_HOR2ECL = 0;
    /**
     * This is a constant to be used as a flag to swe_azalt_rev(). It determines
     * the meaning of the contents of output parameter xout[].
     * xout[0]=&nbsp;equatorial long., xout[1]=&nbsp;equatorial lat..
     *
     * @see swisseph.SwissEph#swe_azalt_rev(double, int, double[], double[], double[])
     */
    int SE_HOR2EQU = 1;

    /* for swe_refrac() */
    /**
     * Calculate the apparent altitude from the true altitude.
     * This is a constant to be used as a flag to swe_refrac().
     *
     * @see swisseph.SwissEph#swe_refrac(double, double, double, int)
     * @see #SE_APP_TO_TRUE
     */
    int SE_TRUE_TO_APP = 0;
    /**
     * Calculate the true altitude from the apparent altitude.
     * This is a constant to be used as a flag to swe_refrac().
     *
     * @see #SE_TRUE_TO_APP
     * @see swisseph.SwissEph#swe_refrac(double, double, double, int)
     */
    int SE_APP_TO_TRUE = 1;

//  static final int pnoext2int[] = {SwephData.SEI_SUN, SwephData.SEI_MOON,
//    SwephData.SEI_MERCURY, SwephData.SEI_VENUS, SwephData.SEI_MARS,
//    SwephData.SEI_JUPITER, SwephData.SEI_SATURN, SwephData.SEI_URANUS,
//    SwephData.SEI_NEPTUNE, SwephData.SEI_PLUTO, 0, 0, 0, 0, SwephData.SEI_EARTH,
//    SwephData.SEI_CHIRON, SwephData.SEI_PHOLUS, SwephData.SEI_CERES,
//    SwephData.SEI_PALLAS, SwephData.SEI_JUNO, SwephData.SEI_VESTA, };
//

    // we always use Astronomical Almanac constants, if available
    double AUNIT = 1.4959787066e+11;        // au in meters,
    // AA 1996 K6
//////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////


}
