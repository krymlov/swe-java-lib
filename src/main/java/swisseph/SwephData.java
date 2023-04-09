/*
   This is a port of the Swiss Ephemeris Free Edition, Version 2.00.00
   of Astrodienst AG, Switzerland from the original C Code to Java. For
   copyright see the original copyright notices below and additional
   copyright notes in the file named LICENSE, or - if this file is not
   available - the copyright notes at http://www.astro.ch/swisseph/ and
   following.

   For any questions or comments regarding this port to Java, you should
   ONLY contact me and not Astrodienst, as the Astrodienst AG is not involved
   in this port in any way.

   Thomas Mack, mack@ifis.cs.tu-bs.de, 23rd of April 2001

*/
/* Copyright (C) 1997 - 2008 Astrodienst AG, Switzerland.  All rights reserved.

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
  a) GNU public license version 2 or later
  b) Swiss Ephemeris Professional License

  The choice must be made before the software developer distributes software
  containing parts of Swiss Ephemeris to others, and before any public
  service using the developed software is activated.

  If the developer choses the GNU GPL software license, he or she must fulfill
  the conditions of that license, which includes the obligation to place his
  or her whole software project under the GNU GPL or a compatible license.
  See http://www.gnu.org/licenses/old-licenses/gpl-2.0.html

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

/*
* This class contains many constants for internal use only.
* It does not need to be instantiated.
*/
interface SwephData {

  static final String SE_VERSION = "2.01.00";
  static final String SE_JAVA_VERSION = "2.01.00_02";

  // Aus sweph.h:
  static final int[] PNOINT2JPL = new int[] {SwephJPL.J_EARTH, SwephJPL.J_MOON,
                    SwephJPL.J_MERCURY, SwephJPL.J_VENUS, SwephJPL.J_MARS,
                    SwephJPL.J_JUPITER, SwephJPL.J_SATURN, SwephJPL.J_URANUS,
                    SwephJPL.J_NEPTUNE, SwephJPL.J_PLUTO, SwephJPL.J_SUN};
  static final int[] pnoint2jpl = PNOINT2JPL;

  /* planetary radii in meters */
  static final int NDIAM = (SweConst.SE_VESTA + 1);
  static final double pla_diam[] = {1392000000.0, /* Sun */
          3476300.0, /* Moon */
          2439000.0 * 2, /* Mercury */
          6052000.0 * 2, /* Venus */
          3397200.0 * 2, /* Mars */
          71398000.0 * 2, /* Jupiter */
          60000000.0 * 2, /* Saturn */
          25400000.0 * 2, /* Uranus */
          24300000.0 * 2, /* Neptune */
          2500000.0 * 2, /* Pluto */
          0, 0, 0, 0,    /* nodes and apogees */
          6378140.0 * 2, /* Earth */
          0.0, /* Chiron */
          0.0, /* Pholus */
          913000.0, /* Ceres */
          523000.0, /* Pallas */
          244000.0, /* Juno */
          501000.0, /* Vesta */
  };

  static final double J2000 = 2451545.0;        // 2000 January 1.5
  static final double B1950 = 2433282.42345905; // 1950 January 0.923
  static final double J1900 = 2415020.0;        // 1900 January 0.5
  static final double B1850 = 2396758.2035810;  /* 1850 January 16:53 */

  static final int MPC_CERES = 1;
  static final int MPC_PALLAS = 2;
  static final int MPC_JUNO = 3;
  static final int MPC_VESTA = 4;
  static final int MPC_CHIRON = 2060;
  static final int MPC_PHOLUS = 5145;

  static final String SE_NAME_SUN = "Sun";
  static final String SE_NAME_MOON = "Moon";
  static final String SE_NAME_MERCURY = "Mercury";
  static final String SE_NAME_VENUS = "Venus";
  static final String SE_NAME_MARS = "Mars";
  static final String SE_NAME_JUPITER = "Jupiter";
  static final String SE_NAME_SATURN = "Saturn";
  static final String SE_NAME_URANUS = "Uranus";
  static final String SE_NAME_NEPTUNE = "Neptune";
  static final String SE_NAME_PLUTO = "Pluto";
  static final String SE_NAME_MEAN_NODE = "mean Node";
  static final String SE_NAME_TRUE_NODE = "true Node";
  static final String SE_NAME_MEAN_APOG = "mean Apogee";
  static final String SE_NAME_OSCU_APOG = "osc. Apogee";
  static final String SE_NAME_INTP_APOG = "intp. Apogee";
  static final String SE_NAME_INTP_PERG = "intp. Perigee";
  static final String SE_NAME_EARTH = "Earth";
  static final String SE_NAME_CERES = "Ceres";
  static final String SE_NAME_PALLAS = "Pallas";
  static final String SE_NAME_JUNO = "Juno";
  static final String SE_NAME_VESTA = "Vesta";
  static final String SE_NAME_CHIRON = "Chiron";
  static final String SE_NAME_PHOLUS = "Pholus";


  static final String SE_NAME_CUPIDO = "Cupido";
  static final String SE_NAME_HADES = "Hades";
  static final String SE_NAME_ZEUS = "Zeus";
  static final String SE_NAME_KRONOS = "Kronos";
  static final String SE_NAME_APOLLON = "Apollon";
  static final String SE_NAME_ADMETOS = "Admetos";
  static final String SE_NAME_VULKANUS = "Vulkanus";
  static final String SE_NAME_POSEIDON = "Poseidon";
  static final String SE_NAME_ISIS = "Isis";
  static final String SE_NAME_NIBIRU = "Nibiru";
  static final String SE_NAME_HARRINGTON = "Harrington";
  static final String SE_NAME_NEPTUNE_LEVERRIER = "Leverrier";
  static final String SE_NAME_NEPTUNE_ADAMS = "Adams";
  static final String SE_NAME_PLUTO_LOWELL = "Lowell";
  static final String SE_NAME_PLUTO_PICKERING = "Pickering";
  static final String SE_NAME_VULCAN = "Vulcan";
  static final String SE_NAME_WHITE_MOON = "White Moon";

  /* Ayanamsas 
   * For each ayanamsa, there are two values:
   * t0       epoch of ayanamsa, TDT (ET)
   * ayan_t0  ayanamsa value at epoch
   */
  static final AyaInit[] ayanamsa = new AyaInit[] {
    new AyaInit(2433282.42346, 24.042044444),  /* 0: Fagan/Bradley (Default) */
    new AyaInit(2435553.5, 23.250182778 - 0.004658035),   /* 1: Lahiri (derived from: * Indian Astronomical Ephemeris 1989, p. 556; * the subtracted value is nutation) */
    new AyaInit(J1900, 360 - 333.58695),   /* 2: De Luce (Robert Hand) */
    new AyaInit(J1900, 360 - 338.98556),   /* 3: Raman (Robert Hand) */
    new AyaInit(J1900, 360 - 341.33904),   /* 4: Ushashashi (Robert Hand) */
    new AyaInit(J1900, 360 - 337.636111),  /* 5: Krishnamurti (Robert Hand) */
    new AyaInit(J1900, 360 - 333.0369024), /* 6: Djwhal Khool; (Graham Dawson) *    Aquarius entered on 1 July 2117 */
    new AyaInit(J1900, 360 - 338.917778),  /* 7: Yukteshwar; (David Cochrane) */
    new AyaInit(J1900, 360 - 338.634444),  /* 8: JN Bhasin; (David Cochrane) */
    new AyaInit(1684532.5, -5.66667),      /* 9: Babylonian, Kugler 1 */
    new AyaInit(1684532.5, -4.26667),      /*10: Babylonian, Kugler 2 */
    new AyaInit(1684532.5, -3.41667),      /*11: Babylonian, Kugler 3 */
    new AyaInit(1684532.5, -4.46667),      /*12: Babylonian, Huber */
    new AyaInit(1673941, -5.079167),       /*13: Babylonian, Mercier; *    eta Piscium culminates with zero point */
    new AyaInit(1684532.5, -4.44138598),   /*14: t0 is defined by Aldebaran at 15 Taurus */
    new AyaInit(1674484.0, -9.33333),        /*15: Hipparchos */
    new AyaInit(1927135.8747793, 0),       /*16: Sassanian */ /*{1746443.513, 0},                     *17: Galactic Center at 0 Sagittarius */
    new AyaInit(1746447.518, 0),           /*17: Galactic Center at 0 Sagittarius */
    new AyaInit(J2000, 0),                 /*18: J2000 */
    new AyaInit(J1900, 0),                 /*19: J1900 */
    new AyaInit(B1950, 0),                 /*20: B1950 */
    new AyaInit(1903396.8128654, 0),       /*21: Suryasiddhanta, assuming ingress of mean Sun into Aries at point of mean equinox of date on 21.3.499, noon, Ujjain (75.7684565 E) = 7:30:31.57 UT */
    new AyaInit(1903396.8128654,-0.21463395),/*22: Suryasiddhanta, assuming ingress of mean Sun into Aries at true position of mean Sun at same epoch */
    new AyaInit(1903396.7895321, 0),       /*23: Aryabhata, same date, but UT 6:56:55.57 analogous 21 */
    new AyaInit(1903396.7895321,-0.23763238),/*24: Aryabhata, analogous 22 */
    new AyaInit(1903396.8128654,-0.79167046),/*25: SS, Revati/zePsc at polar long. 359°50'*/
    new AyaInit(1903396.8128654, 2.11070444),/*26: SS, Citra/Spica at polar long. 180° */
    new AyaInit(0, 0),	                /*27: True Citra (Spica exactly at 0 Libra) */
    new AyaInit(0, 0),	                /*28: True Revati (zeta Psc exactly at 0 Aries) */
    new AyaInit(0, 0),			/*29: True Pushya (delta Cnc exactly a 16 Cancer */
    new AyaInit(0, 0),           // 30: Gil Brand
/*************************/
/* 31: Galactic Equator IAU 1958, i.e. galactic/ecliptic intersection point
      based on galactic coordinate system */
          new AyaInit(0, 0),	             // 31: GE IAU 1958
/*************************/
/* 32: Galactic Equator True, i.e. galactic/ecliptic intersection point based
     on the galactic pole as given in: Liu/Zhu/Zhang, „Reconsidering the
     galactic coordinate system“, A & A No. AA2010, Oct. 2010 */
          new AyaInit(0, 0),	             // 32: GE true
/*************************/
/* 33: Galactic Equator Mula, i.e. galactic/ecliptic intersection point in the
      middle of lunar mansion Mula */
        new AyaInit(0, 0),	             // 33: GE Mula
/*************************/
/* 34: Skydram/Galactic Alignment (R. Mardyks); autumn equinox aligned with
      Galactic Equator/Pole */
        new AyaInit(2451079.734892000, 30),   // 34: Skydram/Mardyks
/*************************/
/* 35: Chandra Hari */
        new AyaInit(0, 0),	             // 35: Chandra Hari
/*************************/
/* 36: Dhruva Galactic Centre Middle of Mula (Ernst Wilhelm) */
        new AyaInit(0, 0),	             // 36: Ernst Wilhelm
/*************************/
/* 37: Kali 3623 = 522 CE, Ujjain (75.7684565), based on Kali midnight and
      year length of Suryasiddhanta */
        new AyaInit(1911797.740782065, 0),     // 36: 0 ayanamsha in year 522
/*************************/
/* 38: Babylonian (Britton 2010)
      John P. Britton, "Studies in Babylonian lunar theory: part III. The
      introduction of the uniform zodiac", in Arch. Hist. Exact. Sci.
      (2010)64:617-663, p. 630. */
        new AyaInit(1721057.5, -3.2),         // 38: Babylonian (Britton 2010)
/*************************/
/* 39: Sunil Sheoran ("Vedic")
      S. Sheoran, "The Science of Time and Timeline of World History", 2017. */
        new AyaInit(0, 0),                    // 39: Sunil Sheoran ("Vedic")
/*************************/
/* 40: Galactic Center at 0 Capricon (Cochrane) */
        new AyaInit(0, 0),                    // 40: Cochrane
/*************************/
/* 41: "Galactic Equatorial" (N.A. Fiorenza) */
        new AyaInit(2451544.5, 25.0),          // 41: N.A. Fiorenza */
/*************************/
/* 42: Vettius Valens (Moon; derived from Holden 1995 p. 12 for epoch of Valens
      1 Jan. 150 CE julian) */
        new AyaInit(1775845.5, -2.9422),      // 42: Vettius Valens
/*************************/
/* 43: Lahiri (1940), book "Panchanga darpan":
      22°26'45".50 + 50".25748T + 0".00011115T^2 */
        new AyaInit(J1900, 22.44597222), // 43: Lahiri (1940)
/*************************/
/* 44: Lahiri (VP285), mean sun at 360° in 285CE; epoch for mean sun at 0 acc.
      to Simon 1994, corrected for Vondrak precession
      (Preface to Lahiri's "Indian Ephemeris" 1980) */
        new AyaInit(1825235.2458513028, 0.0), // 44: Lahiri VP285 (1980)
/*************************/
/* 45: Krishnamurti from mean equinox 291, based on Newcomb precession,
      according to D. Senthilathiban, "Study of KP Ayanamsa with Modern
      Precession Theories" (2019), but using precession Vondrak 2011 and
      correction base on Newcomb precession. */
//{1827424.752255678, 0.0, FALSE, SEMOD_PREC_NEWCOMB), // 45: Krishnamurti VP291
        new AyaInit(1827424.752255678, 0.0), // 45: Krishnamurti VP291
/*************************/
/* 46: Lahiri original: Calendar Reform Committee 1956,
      before the correction by 0.658" in IAE 1985.
      The subtracted value is nutation according to Woolard 1953.
      However, nutation Woolard was used by IENA/IAE only from 1960 on,
      so this value is not correct. In order to reproduce mean ayanamshas
      of IENA >=1960, we could choose 23.25 - 0.00464207 + 0.07 / 3600.0
      as initial value in 1956. However this will not help to reproduce
      true ayanamshas. A deviation of around 0.1" remains,
      for unknown reasons. The difference between Lahiri (1) and
      Lahiri ICRC (45) amounts to 1.1". */
        new AyaInit(2435553.5, 23.25 - 0.00464207), // 46: SE_SIDM_LAHIRI_ICRC
/*************************/
};

/*
 * earlier content
 */

  static final double TWOPI = 2.0 * SMath.PI;

//  static final int ENDMARK = -99;

  static final int SEI_EPSILON = -2;
  static final int SEI_NUTATION = -1;
  static final int SEI_EMB = 0;
  static final int SEI_EARTH = 0;
  static final int SEI_SUN = 0;
  static final int SEI_MOON = 1;
  static final int SEI_MERCURY = 2;
  static final int SEI_VENUS = 3;
  static final int SEI_MARS = 4;
  static final int SEI_JUPITER = 5;
  static final int SEI_SATURN = 6;
  static final int SEI_URANUS = 7;
  static final int SEI_NEPTUNE = 8;
  static final int SEI_PLUTO = 9;
  static final int SEI_SUNBARY = 10;     // barycentric sun
  static final int SEI_ANYBODY = 11;     // any asteroid
  static final int SEI_CHIRON = 12;
  static final int SEI_PHOLUS = 13;
  static final int SEI_CERES = 14;
  static final int SEI_PALLAS = 15;
  static final int SEI_JUNO = 16;
  static final int SEI_VESTA = 17;

  static final int SEI_NPLANETS = 18;

  static final int SEI_MEAN_NODE = 0;
  static final int SEI_TRUE_NODE = 1;
  static final int SEI_MEAN_APOG = 2;
  static final int SEI_OSCU_APOG = 3;
  static final int SEI_INTP_APOG = 4;
  static final int SEI_INTP_PERG = 5;

  static final int SEI_NNODE_ETC = 6;

  static final int SEI_FLG_HELIO = 1;
  static final int SEI_FLG_ROTATE = 2;
  static final int SEI_FLG_ELLIPSE = 4;
  static final int SEI_FLG_EMBHEL = 8; // TRUE, if heliocentric earth is given
                                     // instead of barycentric sun
                                     // i.e. bary sun is computed from
                                     // barycentric and heliocentric earth

  static final int SEI_FILE_PLANET = 0;
  static final int SEI_FILE_MOON = 1;
  static final int SEI_FILE_MAIN_AST = 2;
  static final int SEI_FILE_ANY_AST = 3;
  static final int SEI_FILE_FIXSTAR = 4;

  // Aus swephexph.h:
  static final int SEI_FILE_TEST_ENDIAN = 0x616263;   // abc
  static final int SEI_FILE_BIGENDIAN = 0;
  static final int SEI_FILE_NOREORD = 0;
  static final int SEI_FILE_LITENDIAN = 1;
  static final int SEI_FILE_REORD = 2;

  static final int SEI_FILE_NMAXPLAN = 50;
  static final int SEI_FILE_EFPOSBEGIN = 500;

  static final String SE_FILE_SUFFIX = "se1";

  static final int SEI_NEPHFILES = 7;
  static final int SEI_CURR_FPOS = -1;
  static final int SEI_NMODELS = 20;

  static final double SEI_ECL_GEOALT_MAX =  25000.0;
  static final double SEI_ECL_GEOALT_MIN =  (-500.0);

/* Chiron's orbit becomes chaotic
 * before 720 AD and after 4606 AD, because of close encounters
 * with Saturn. Accepting a maximum error of 5 degrees,
 * the ephemeris is good between the following dates:
 */
  /*static final double CHIRON_START = 1958470.5;      * 1.1.650 old limit until v. 2.00 */
  static final double CHIRON_START = 1967601.5;  	/* 1.1.675 */
  static final double CHIRON_END = 3419437.5;        // 1.1.4650

/* Pholus's orbit is unstable as well, because he sometimes
 * approaches Saturn.
 * Accepting a maximum error of 5 degrees,
 * the ephemeris is good after the following date:
 */
  /* static final double PHOLUS_START = 314845.5;       * 1.1.-3850  old limit until v. 2.00 */
  static final double PHOLUS_START = 640648.5;	/* 1.1.-2958 jul */
  static final double PHOLUS_END =   4390617.5;  	/* 1.1.7309 */

  static final double MOSHPLEPH_START =  625000.5;
  static final double MOSHPLEPH_END =   2818000.5;
  static final double MOSHLUEPH_START =  625000.5;
  static final double MOSHLUEPH_END =   2818000.5;
  /* static final double MOSHNDEPH_START = -254900.5; // 14 Feb -5410 00:00 ET jul.cal.*/
  /* static final double MOSHNDEPH_END =   3697000.5; // 11 Dec 5409 00:00 ET, greg. cal. */
  static final double MOSHNDEPH_START = -3100015.5; // 15 Aug -13200 00:00 ET jul.cal.*/
  static final double MOSHNDEPH_END =   8000016.5; // 15 Mar 17191 00:00 ET, greg. cal */
/*
*/
static final double JPL_DE431_START = -3027215.5;
static final double JPL_DE431_END   =  7930192.5;

  static final int MAXORD = 40;

  static final double NCTIES = 6.0;    // number of centuries per eph. file

  static final int NOT_AVAILABLE = -2;
  static final int BEYOND_EPH_LIMITS = -3;

  static final int J_TO_J2000 = 1;
  static final int J2000_TO_J = -1;


  // we always use Astronomical Almanac constants, if available
  static final double MOON_MEAN_DIST = 384400000.0;           // in m, AA 1996, F2
  static final double MOON_MEAN_INCL = 5.1453964;             // AA 1996, D2
  static final double MOON_MEAN_ECC = 0.054900489;            // AA 1996, F2
  // static final double SUN_EARTH_MRAT = 328900.561400;         Su/(Ea+Mo) AA 2006 K7
  static final double SUN_EARTH_MRAT = 332946.050895;         // Su / (Ea only) AA 2006 K7
  static final double EARTH_MOON_MRAT = (1 / 0.0123000383);   // AA 2006, K7
  static final double AUNIT = 1.49597870691e+11;              // au in meters, AA 2006 K6
  static final double CLIGHT = 2.99792458e+8;                 // m/s, AA 1996 K6
  static final double HELGRAVCONST = 1.32712440017987e+20;    // G * M(sun), m^3/sec^2, AA 2006 K6
  static final double GEOGCONST = 3.98600448e+14; // G * M(earth) m^3/sec^2, AA 1996 K6
  static final double KGAUSS = 0.01720209895; // Gaussian gravitational constant K6
  static final double KGAUSS_GEO = 0.0000298122353216;        // Earth only
  // static final double KGAUSS_GEO = 0.0000299502129737        // Earth + Moon
  static final double SUN_RADIUS = 959.63 / 3600 * SwissData.DEGTORAD;  // Meeus germ. p 391
  static final double EARTH_RADIUS = 6378136.6;               // AA 2006 K6
  /*static final double EARTH_OBLATENESS = (1.0/ 298.257223563); * AA 1998 K13 */
  static final double EARTH_OBLATENESS = (1.0/ 298.25642);    // AA 2006 K6
  static final double EARTH_ROT_SPEED = 7.2921151467e-5 * 86400; // in rad/day, expl. suppl., p 162

  static final double LIGHTTIME_AUNIT = (499.0047838061/3600/24); // 8.3167 minutes (days), AA 2006 K6

  /* node of ecliptic measured on ecliptic 2000 */
  static final double SSY_PLANE_NODE_E2000 = 107.582569 * SwissData.DEGTORAD;
  /* node of ecliptic measured on solar system rotation plane */
  static final double SSY_PLANE_NODE = 107.58883388 * SwissData.DEGTORAD;
  /* inclination of ecliptic against solar system rotation plane */
  static final double SSY_PLANE_INCL = 1.578701 * SwissData.DEGTORAD;

  static final double KM_S_TO_AU_CTY = 21.095;           // km/s to AU/century
  static final double MOON_SPEED_INTV = 0.00005;         // 4.32 seconds (in days)
  static final double PLAN_SPEED_INTV = 0.0001;          // 8.64 seconds (in days)
  static final double MEAN_NODE_SPEED_INTV = 0.001;
  static final double NODE_CALC_INTV = 0.0001;
  static final double NODE_CALC_INTV_MOSH = 0.1;
  static final double NUT_SPEED_INTV = 0.0001;
  static final double DEFL_SPEED_INTV = 0.0000005;

  static final double SE_LAPSE_RATE = 0.0065;  /* deg K / m, for refraction */


/*
 * stuff exported from swemplan.c and swemmoon.c
 * and constants used inside these functions.
************************************************************/

  static final double STR = 4.8481368110953599359e-6;   // radians per arc second

  /* dpsi and deps loaded for 100 years after 1962 */
  static final int SWE_DATA_DPSI_DEPS = 36525;


  // Aus sweph.c:
  static final int IS_PLANET = 0;
  static final int IS_MOON = 1;
  static final int IS_ANY_BODY = 2;
  static final int IS_MAIN_ASTEROID = 3;

  static final boolean DO_SAVE = true;
  static final boolean NO_SAVE = false;

//  java.io.RandomAccessFile fixfp = null;     // fixed stars


//  static final int pnoext2int[] = {SEI_SUN, SEI_MOON, SEI_MERCURY, SEI_VENUS,
//    SEI_MARS, SEI_JUPITER, SEI_SATURN, SEI_URANUS, SEI_NEPTUNE, SEI_PLUTO,
//    0, 0, 0, 0, SEI_EARTH, SEI_CHIRON, SEI_PHOLUS, SEI_CERES, SEI_PALLAS,
//    SEI_JUNO, SEI_VESTA, };


///////////////////////////////////////////////////////////////
// SURYA: /////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double SURYA_MAX_LON_SPEED = 1.025;
  static final double SURYA_MIN_LON_SPEED = 0.946;
  static final double SURYA_MAX_LON_ACCEL = 0.000735;
  static final double SURYA_MIN_LON_ACCEL = -0.000720;
  static final double SURYA_MAX_TOPO_LON_SPEED = 1.04;
  static final double SURYA_MIN_TOPO_LON_SPEED = 0.93;
  static final double SURYA_MAX_TOPO_LON_ACCEL = 0.06;
  static final double SURYA_MIN_TOPO_LON_ACCEL = -0.06;
  static final double SURYA_MAX_HELIO_LON_SPEED = 0;
  static final double SURYA_MIN_HELIO_LON_SPEED = 0;
  static final double SURYA_MAX_HELIO_LON_ACCEL = 0;
  static final double SURYA_MIN_HELIO_LON_ACCEL = 0;

  static final double SURYA_MAX_LAT_SPEED = 0.0000620;
  static final double SURYA_MIN_LAT_SPEED = -0.0000618;
  static final double SURYA_MAX_LAT_ACCEL = 0.0000203;
  static final double SURYA_MIN_LAT_ACCEL = -0.0000204;
  static final double SURYA_MAX_TOPO_LAT_SPEED = 0.0066;
  static final double SURYA_MIN_TOPO_LAT_SPEED = -0.0065;
  static final double SURYA_MAX_TOPO_LAT_ACCEL = 0.025;
  static final double SURYA_MIN_TOPO_LAT_ACCEL = -0.025;
  static final double SURYA_MAX_HELIO_LAT_SPEED = 0;
  static final double SURYA_MIN_HELIO_LAT_SPEED = 0;
  static final double SURYA_MAX_HELIO_LAT_ACCEL = 0;
  static final double SURYA_MIN_HELIO_LAT_ACCEL = 0;

  static final double SURYA_MAX_DIST_SPEED = 0.000328;
  static final double SURYA_MIN_DIST_SPEED = -0.000327;
  static final double SURYA_MAX_DIST_ACCEL = 0.00000734;
  static final double SURYA_MIN_DIST_ACCEL = -0.00000694;
  static final double SURYA_MAX_TOPO_DIST_SPEED = 0.00059;
  static final double SURYA_MIN_TOPO_DIST_SPEED = -0.00058;
  static final double SURYA_MAX_TOPO_DIST_ACCEL = 0.00104;
  static final double SURYA_MIN_TOPO_DIST_ACCEL = -0.0013;
  static final double SURYA_MAX_HELIO_DIST_SPEED = 0;
  static final double SURYA_MIN_HELIO_DIST_SPEED = 0;
  static final double SURYA_MAX_HELIO_DIST_ACCEL = 0;
  static final double SURYA_MIN_HELIO_DIST_ACCEL = 0;

  // java Swetest -head -bj-3027215.5 -s... -n... -ejplde431.eph -edir./ephe -fPJadss -p0
  static final double SURYA_MAX_RECT_SPEED = 1.1192891;
  static final double SURYA_MIN_RECT_SPEED = 0.8716103;
  static final double SURYA_MAX_RECT_ACCEL = 1./0.;
  static final double SURYA_MIN_RECT_ACCEL = 1./0.;
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p0 -s0.7064 -fPJadss -n15511619 -topo0,0,50000 > sun-topo-0-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p0 -s0.7064 -fPJadss -n15511619 -topo11,0,0 > sun-topo-11-0-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p0 -s0.7064 -fPJadss -n15511619 -topo11,0,-6300000 > sun-topo-11-0--6300000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p0 -s0.7064 -fPJadss -n15511619 -topo11,89,0 > sun-topo-11-89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p0 -s0.7064 -fPJadss -n15511619 -topo11,-89,0 > sun-topo-11--89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p0 -s0.7064 -fPJadss -n15511619 -topo11,89,50000 > sun-topo-11-89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p0 -s0.7064 -fPJadss -n15511619 -topo11,0,50000 > sun-topo-11-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p0 -s0.7064 -fPJadss -n15511619 -topo11,-89,50000 > sun-topo-11--89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p0 -s0.7064 -fPJadss -n15511619 -topo179,0,50000 > sun-topo-179-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p0 -s0.7064 -fPJadss -n15511619 -topo-179,0,50000 > sun-topo--179-0-50000 &
  // 0.8509551 .. 1.1416163 at -topo0,0,50000		<-- MIN
  // 0.8511042 .. 1.1414601 at -topo11,0,0
  // 0.8509861 .. 1.1415944 at -topo11,0,50000
  // 0.8659616 .. 1.1246064 at -topo11,0,-6300000
  // 0.8658840 .. 1.1246951 at -topo11,89,0
  // 0.8658840 .. 1.1246951 at -topo11,-89,0
  // 0.8658820 .. 1.1246974 at -topo11,89,50000
  // 0.8658820 .. 1.1246974 at -topo11,-89,50000
  // 0.8509695 .. 1.1416192 at -topo179,0,50000
  // 0.8510149 .. 1.1416461 at -topo-179,0,50000	<-- MAX
  static final double SURYA_MAX_TOPO_RECT_SPEED = 1.1416461;
  static final double SURYA_MIN_TOPO_RECT_SPEED = 0.8509551;
  static final double SURYA_MAX_TOPO_RECT_ACCEL = 1./0.;
  static final double SURYA_MIN_TOPO_RECT_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double SURYA_MAX_HELIO_RECT_SPEED = 1./0.;
  static final double SURYA_MIN_HELIO_RECT_SPEED = 1./0.;
  static final double SURYA_MAX_HELIO_RECT_ACCEL = 1./0.;
  static final double SURYA_MIN_HELIO_RECT_ACCEL = 1./0.;

  static final double SURYA_MAX_DECL_SPEED = 0.4125450;
  static final double SURYA_MIN_DECL_SPEED = -0.3970215;
  static final double SURYA_MAX_DECL_ACCEL = 1./0.;
  static final double SURYA_MIN_DECL_ACCEL = 1./0.;
  // -0.4189250 .. 0.4127358 at -topo0,0,50000
  // -0.4189254 .. 0.4127297 at -topo11,0,0
  // -0.4189265 .. 0.4127323 at -topo11,0,50000	<-- MIN
  // -0.4188348 .. 0.4125894 at -topo11,0,-6300000
  // -0.4188344 .. 0.4125897 at -topo11,89,0
  // -0.4188346 .. 0.4125903 at -topo11,-89,0
  // -0.4188344 .. 0.4125897 at -topo11,89,50000
  // -0.4188346 .. 0.4125903 at -topo11,-89,50000
  // -0.4189235 .. 0.4128070 at -topo179,0,50000	<-- MAX
  // -0.4189249 .. 0.4128064 at -topo-179,0,50000
  static final double SURYA_MAX_TOPO_DECL_SPEED = 0.4128070;
  static final double SURYA_MIN_TOPO_DECL_SPEED = -0.4189265;
  static final double SURYA_MAX_TOPO_DECL_ACCEL = 1./0.;
  static final double SURYA_MIN_TOPO_DECL_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double SURYA_MAX_HELIO_DECL_SPEED = 1./0.;
  static final double SURYA_MIN_HELIO_DECL_SPEED = 1./0.;
  static final double SURYA_MAX_HELIO_DECL_ACCEL = 1./0.;
  static final double SURYA_MIN_HELIO_DECL_ACCEL = 1./0.;


///////////////////////////////////////////////////////////////
// CHANDRA: ///////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double CHANDRA_MAX_LON_SPEED = 15.41;
  static final double CHANDRA_MIN_LON_SPEED = 11.75;
  static final double CHANDRA_MAX_LON_ACCEL = 0.522;
  static final double CHANDRA_MIN_LON_ACCEL = -0.520;
  static final double CHANDRA_MAX_TOPO_LON_SPEED = 22.0;
  static final double CHANDRA_MIN_TOPO_LON_SPEED = 6.0;
  static final double CHANDRA_MAX_TOPO_LON_ACCEL = 23.5;
  static final double CHANDRA_MIN_TOPO_LON_ACCEL = -23.2;
  static final double CHANDRA_MAX_HELIO_LON_SPEED = 1.0584;
  static final double CHANDRA_MIN_HELIO_LON_SPEED = 0.9155;
  static final double CHANDRA_MAX_HELIO_LON_ACCEL = 0.007875;
  static final double CHANDRA_MIN_HELIO_LON_ACCEL = -0.007888;

  static final double CHANDRA_MAX_LAT_SPEED = 1.44;
  static final double CHANDRA_MIN_LAT_SPEED = -1.44;
  static final double CHANDRA_MAX_LAT_ACCEL = 0.366;
  static final double CHANDRA_MIN_LAT_ACCEL = -0.366;
  static final double CHANDRA_MAX_TOPO_LAT_SPEED = 4.2;
  static final double CHANDRA_MIN_TOPO_LAT_SPEED = -4.7;
  static final double CHANDRA_MAX_TOPO_LAT_ACCEL = 12.5;
  static final double CHANDRA_MIN_TOPO_LAT_ACCEL = -12.5;
  static final double CHANDRA_MAX_HELIO_LAT_SPEED = 0.003415;
  static final double CHANDRA_MIN_HELIO_LAT_SPEED = -0.0034187;
  static final double CHANDRA_MAX_HELIO_LAT_ACCEL = 0.0008119;
  static final double CHANDRA_MIN_HELIO_LAT_ACCEL = -0.0008069;

  static final double CHANDRA_MAX_DIST_SPEED = 0.000044;
  static final double CHANDRA_MIN_DIST_SPEED = -0.0000434;
  static final double CHANDRA_MAX_DIST_ACCEL = 0.0000140;
  static final double CHANDRA_MIN_DIST_ACCEL = -0.00000898;
  static final double CHANDRA_MAX_TOPO_DIST_SPEED = 0.00030;
  static final double CHANDRA_MIN_TOPO_DIST_SPEED = -0.00031;
  static final double CHANDRA_MAX_TOPO_DIST_ACCEL = 0.00099;
  static final double CHANDRA_MIN_TOPO_DIST_ACCEL = -0.00098;
  static final double CHANDRA_MAX_HELIO_DIST_SPEED = 0.0008899;
  static final double CHANDRA_MIN_HELIO_DIST_SPEED = -0.000889;
  static final double CHANDRA_MAX_HELIO_DIST_ACCEL = 0.0001394;
  static final double CHANDRA_MIN_HELIO_DIST_ACCEL = -0.00013959;

  // time ./swetest -head -bj-3027215.5 -ejplde431.eph -edir./ephe -p1 -s0.7064 -fPJadss -n15511619 > moon_jpl
  static final double CHANDRA_MAX_RECT_SPEED = 17.5652713;
  static final double CHANDRA_MIN_RECT_SPEED = 10.2878798;
  static final double CHANDRA_MAX_RECT_ACCEL = 1./0.;
  static final double CHANDRA_MIN_RECT_ACCEL = 1./0.;
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p1 -s0.7064 -fPJadss -n15511619 -topo0,0,50000 > moon-topo-0-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p1 -s0.7064 -fPJadss -n15511619 -topo11,0,0 > moon-topo-11-0-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p1 -s0.7064 -fPJadss -n15511619 -topo11,0,50000 > moon-topo-11-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p1 -s0.7064 -fPJadss -n15511619 -topo11,0,-6300000 > moon-topo-11-0--6300000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p1 -s0.7064 -fPJadss -n15511619 -topo11,89,0 > moon-topo-11-89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p1 -s0.7064 -fPJadss -n15511619 -topo11,-89,0 > moon-topo-11--89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p1 -s0.7064 -fPJadss -n15511619 -topo11,89,50000 > moon-topo-11-89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p1 -s0.7064 -fPJadss -n15511619 -topo11,-89,50000 > moon-topo-11--89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p1 -s0.7064 -fPJadss -n15511619 -topo179,0,50000 > moon-topo-179-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p1 -s0.7064 -fPJadss -n15511619 -topo-179,0,50000 > moon-topo--179-0-50000 &
  // 4.6573901 .. 24.4157988 at -topo0,0,50000
  // 4.7015831 .. 24.3761631 at -topo11,0,0
  // 4.6568738 .. 24.4289479 at -topo11,0,50000
  // 10.2291999 .. 17.6134294 at -topo11,0,-6300000
  // 10.2014613 .. 17.6484812 at -topo11,89,0
  // 10.2014710 .. 17.6484522 at -topo11,-89,0
  // 10.2007399 .. 17.6493925 at -topo11,89,50000
  // 10.2007497 .. 17.6493633 at -topo11,-89,50000
  // 4.6586466 .. 24.4699016 at -topo179,0,50000
  // 4.6542540 .. 24.4751212 at -topo-179,0,50000	<-- MIN	<-- MAX
  static final double CHANDRA_MAX_TOPO_RECT_SPEED = 24.4751212;
  static final double CHANDRA_MIN_TOPO_RECT_SPEED = 4.6542540;
  static final double CHANDRA_MAX_TOPO_RECT_ACCEL = 1./0.;
  static final double CHANDRA_MIN_TOPO_RECT_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double CHANDRA_MAX_HELIO_RECT_SPEED = 1./0.;
  static final double CHANDRA_MIN_HELIO_RECT_SPEED = 1./0.;
  static final double CHANDRA_MAX_HELIO_RECT_ACCEL = 1./0.;
  static final double CHANDRA_MIN_HELIO_RECT_ACCEL = 1./0.;

  static final double CHANDRA_MAX_DECL_SPEED = 7.5378600;
  static final double CHANDRA_MIN_DECL_SPEED = -7.5244591;
  static final double CHANDRA_MAX_DECL_ACCEL = 1./0.;
  static final double CHANDRA_MIN_DECL_ACCEL = 1./0.;
  // -8.2651440 .. 8.2303963 at -topo0,0,50000		<-- MIN
  // -8.2470912 .. 8.2500578 at -topo11,0,0
  // -8.2572387 .. 8.2607011 at -topo11,0,50000
  // -7.5232524 .. 7.5360846 at -topo11,0,-6300000
  // -7.5201675 .. 7.5316452 at -topo11,89,0
  // -7.5218851 .. 7.5359995 at -topo11,-89,0
  // -7.5201472 .. 7.5316180 at -topo11,89,50000
  // -7.5218349 .. 7.5359673 at -topo11,-89,50000
  // -8.2478077 .. 8.2804317 at -topo179,0,50000	<-- MAX
  // -8.2523501 .. 8.2786299 at -topo-179,0,50000
  static final double CHANDRA_MAX_TOPO_DECL_SPEED = 8.2804317;
  static final double CHANDRA_MIN_TOPO_DECL_SPEED = -8.2651440;
  static final double CHANDRA_MAX_TOPO_DECL_ACCEL = 1./0.;
  static final double CHANDRA_MIN_TOPO_DECL_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double CHANDRA_MAX_HELIO_DECL_SPEED = 1./0.;
  static final double CHANDRA_MIN_HELIO_DECL_SPEED = 1./0.;
  static final double CHANDRA_MAX_HELIO_DECL_ACCEL = 1./0.;
  static final double CHANDRA_MIN_HELIO_DECL_ACCEL = 1./0.;



///////////////////////////////////////////////////////////////
// BUDHA: /////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double BUDHA_MAX_LON_SPEED = 2.23;
  static final double BUDHA_MIN_LON_SPEED = -1.40;
  static final double BUDHA_MAX_LON_ACCEL = 0.20;
  static final double BUDHA_MIN_LON_ACCEL = -0.199;
  static final double BUDHA_MAX_TOPO_LON_SPEED = 2.3;
  static final double BUDHA_MIN_TOPO_LON_SPEED = -1.49;
  static final double BUDHA_MAX_TOPO_LON_ACCEL = 0.281;
  static final double BUDHA_MIN_TOPO_LON_ACCEL = -0.282;
  static final double BUDHA_MAX_HELIO_LON_SPEED = 6.358;
  static final double BUDHA_MIN_HELIO_LON_SPEED = 2.743;
  static final double BUDHA_MAX_HELIO_LON_ACCEL = 0.1547;
  static final double BUDHA_MIN_HELIO_LON_ACCEL = -0.1654;

  static final double BUDHA_MAX_LAT_SPEED = 0.35;
  static final double BUDHA_MIN_LAT_SPEED = -0.31;
  static final double BUDHA_MAX_LAT_ACCEL = 0.036;
  static final double BUDHA_MIN_LAT_ACCEL = -0.044;
  static final double BUDHA_MAX_TOPO_LAT_SPEED = 0.37;
  static final double BUDHA_MIN_TOPO_LAT_SPEED = -0.34;
  static final double BUDHA_MAX_TOPO_LAT_ACCEL = 0.078;
  static final double BUDHA_MIN_TOPO_LAT_ACCEL = -0.10;
  static final double BUDHA_MAX_HELIO_LAT_SPEED = 0.7557;
  static final double BUDHA_MIN_HELIO_LAT_SPEED = -0.4114;
  static final double BUDHA_MAX_HELIO_LAT_ACCEL = 0.05938;
  static final double BUDHA_MIN_HELIO_LAT_ACCEL = -0.07986;

  static final double BUDHA_MAX_DIST_SPEED = 0.0286;
  static final double BUDHA_MIN_DIST_SPEED = -0.0285;
  static final double BUDHA_MAX_DIST_ACCEL = 0.00325;
  static final double BUDHA_MIN_DIST_ACCEL = -0.00150;
  static final double BUDHA_MAX_TOPO_DIST_SPEED = 0.031;
  static final double BUDHA_MIN_TOPO_DIST_SPEED = -0.031;
  static final double BUDHA_MAX_TOPO_DIST_ACCEL = 0.0045;
  static final double BUDHA_MIN_TOPO_DIST_ACCEL = -0.0026;
  static final double BUDHA_MAX_HELIO_DIST_SPEED = 0.005831;
  static final double BUDHA_MIN_HELIO_DIST_SPEED = -0.005831;
  static final double BUDHA_MAX_HELIO_DIST_ACCEL = 0.00064693;
  static final double BUDHA_MIN_HELIO_DIST_ACCEL = -0.0002801;

  // time ./swetest -head -bj-3027215.5 -ejplde431.eph -edir./ephe -p2 -s0.7064 -fPJadss -n15511619 > mercury_jpl
  static final double BUDHA_MAX_RECT_SPEED = 2.4204481;
  static final double BUDHA_MIN_RECT_SPEED = -1.5488386;
  static final double BUDHA_MAX_RECT_ACCEL = 1./0.;
  static final double BUDHA_MIN_RECT_ACCEL = 1./0.;
  // -1.5742275 .. 2.4331242 at -topo0,0,50000		MIN(2)
  // -1.5742011 .. 2.4333455 at -topo11,0,0		MIN(3)	MAX(3)
  // -1.5743958 .. 2.4334422 at -topo11,0,50000	MIN(1)	MAX(2)	<--
  // -1.5501068 .. 2.4216002 at -topo11,0,-6300000
  // -1.5499773 .. 2.4216610 at -topo11,89,0
  // -1.5499775 .. 2.4216610 at -topo11,-89,0
  // -1.5499740 .. 2.4216626 at -topo11,89,50000
  // -1.5499741 .. 2.4216626 at -topo11,-89,50000
  // -1.5738373 .. 2.4333983 at -topo179,0,50000		MAX(4)
  // -1.5741371 .. 2.4334617 at -topo-179,0,50000	MIN(4)	MAX(1)
  static final double BUDHA_MAX_TOPO_RECT_SPEED = 2.4334617;
  static final double BUDHA_MIN_TOPO_RECT_SPEED = -1.5743958;
  static final double BUDHA_MAX_TOPO_RECT_ACCEL = 1./0.;
  static final double BUDHA_MIN_TOPO_RECT_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double BUDHA_MAX_HELIO_RECT_SPEED = 1./0.;
  static final double BUDHA_MIN_HELIO_RECT_SPEED = 1./0.;
  static final double BUDHA_MAX_HELIO_RECT_ACCEL = 1./0.;
  static final double BUDHA_MIN_HELIO_RECT_ACCEL = 1./0.;

  static final double BUDHA_MAX_DECL_SPEED = 1.0505545;
  static final double BUDHA_MIN_DECL_SPEED = -0.8200447;
  static final double BUDHA_MAX_DECL_ACCEL = 1./0.;
  static final double BUDHA_MIN_DECL_ACCEL = 1./0.;
// time ./swetest -head -bj-3027215.5 -ejplde431.eph -edir./ephe -p2 -s0.7064 -fPJadss -n15511619 -topo0,0,50000 > mercury-topo-0-0-50000
// sort -n -k+6 mercury-topo-0-0-50000 | tail -3; sort -n -k+6 mercury-topo-0-0-50000 | head -6

  // -0.8211451 .. 1.0518265 at -topo0,0,50000		MIN(3)
  // -0.8211233 .. 1.0518169 at -topo11,0,0		MIN(2)	MAX(3)
  // -0.8211220 .. 1.0518169 at -topo11,0,50000		MIN(1)	MAX(3)	<--
  // -0.8212869 .. 1.0518181 at -topo11,0,-6300000
  // -0.8212894 .. 1.0518181 at -topo11,89,0
  // -0.8212826 .. 1.0518182 at -topo11,-89,0		MIN(4)
  // -0.8212894 .. 1.0518181 at -topo11,89,50000
  // -0.8212826 .. 1.0518182 at -topo11,-89,50000	MIN(4)
  // -0.8214798 .. 1.0518089 at -topo179,0,50000			MAX(1)
  // -0.8214764 .. 1.0518107 at -topo-179,0,50000		MAX(2)
  static final double BUDHA_MAX_TOPO_DECL_SPEED = 1.0518265;
  static final double BUDHA_MIN_TOPO_DECL_SPEED = -0.8214798;
  static final double BUDHA_MAX_TOPO_DECL_ACCEL = 1./0.;
  static final double BUDHA_MIN_TOPO_DECL_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double BUDHA_MAX_HELIO_DECL_SPEED = 1./0.;
  static final double BUDHA_MIN_HELIO_DECL_SPEED = 1./0.;
  static final double BUDHA_MAX_HELIO_DECL_ACCEL = 1./0.;
  static final double BUDHA_MIN_HELIO_DECL_ACCEL = 1./0.;



///////////////////////////////////////////////////////////////
// SHUKRA: ////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double SHUKRA_MAX_LON_SPEED = 1.266;
  static final double SHUKRA_MIN_LON_SPEED = -1.22;
  static final double SHUKRA_MAX_LON_ACCEL = 0.0427;
  static final double SHUKRA_MIN_LON_ACCEL = -0.0432;
  static final double SHUKRA_MAX_TOPO_LON_SPEED = 1.28;
  static final double SHUKRA_MIN_TOPO_LON_SPEED = -0.70;
  static final double SHUKRA_MAX_TOPO_LON_ACCEL = 0.246;
  static final double SHUKRA_MIN_TOPO_LON_ACCEL = -0.245;
  static final double SHUKRA_MAX_HELIO_LON_SPEED = 1.635;
  static final double SHUKRA_MIN_HELIO_LON_SPEED = 1.565;
  static final double SHUKRA_MAX_HELIO_LON_ACCEL = 0.0009600;
  static final double SHUKRA_MIN_HELIO_LON_ACCEL = -0.001066;

  static final double SHUKRA_MAX_LAT_SPEED = 0.264;
  static final double SHUKRA_MIN_LAT_SPEED = -0.251;
  static final double SHUKRA_MAX_LAT_ACCEL = 0.0167;
  static final double SHUKRA_MIN_LAT_ACCEL = -0.0170;
  static final double SHUKRA_MAX_TOPO_LAT_SPEED = 0.29;
  static final double SHUKRA_MIN_TOPO_LAT_SPEED = -0.27;
  static final double SHUKRA_MAX_TOPO_LAT_ACCEL = 0.13;
  static final double SHUKRA_MIN_TOPO_LAT_ACCEL = -0.113;
  static final double SHUKRA_MAX_HELIO_LAT_SPEED = 0.096175;
  static final double SHUKRA_MIN_HELIO_LAT_SPEED = -0.09549;
  static final double SHUKRA_MAX_HELIO_LAT_ACCEL = 0.002635;
  static final double SHUKRA_MIN_HELIO_LAT_ACCEL = -0.00275;

  static final double SHUKRA_MAX_DIST_SPEED = 0.00806;
  static final double SHUKRA_MIN_DIST_SPEED = -0.0083;
  static final double SHUKRA_MAX_DIST_ACCEL = 0.000316;
  static final double SHUKRA_MIN_DIST_ACCEL = -0.0000625;
  static final double SHUKRA_MAX_TOPO_DIST_SPEED = 0.0084;
  static final double SHUKRA_MIN_TOPO_DIST_SPEED = -0.0086;
  static final double SHUKRA_MAX_TOPO_DIST_ACCEL = 0.0015;
  static final double SHUKRA_MIN_TOPO_DIST_ACCEL = -0.00108;
  static final double SHUKRA_MAX_HELIO_DIST_SPEED = 0.0002173;
  static final double SHUKRA_MIN_HELIO_DIST_SPEED = -0.0002172;
  static final double SHUKRA_MAX_HELIO_DIST_ACCEL = 0.000006264;
  static final double SHUKRA_MIN_HELIO_DIST_ACCEL = -0.000005947;

  // time ./swetest -head -bj-3027215.5 -ejplde431.eph -edir./ephe -p3 -s0.7064 -fPJadss -n15511619 > venus_jpl
  static final double SHUKRA_MAX_RECT_SPEED = 1.4037654;
  static final double SHUKRA_MIN_RECT_SPEED = -0.7297440;
  static final double SHUKRA_MAX_RECT_ACCEL = 1./0.;
  static final double SHUKRA_MIN_RECT_ACCEL = 1./0.;
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p3 -s0.7064 -fPJadss -n15511619 -topo0,0,50000 > venus-topo-0-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p3 -s0.7064 -fPJadss -n15511619 -topo11,0,0 > venus-topo-11-0-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p3 -s0.7064 -fPJadss -n15511619 -topo11,0,50000 > venus-topo-11-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p3 -s0.7064 -fPJadss -n15511619 -topo11,0,-6300000 > venus-topo-11-0--6300000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p3 -s0.7064 -fPJadss -n15511619 -topo11,89,0 > venus-topo-11-89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p3 -s0.7064 -fPJadss -n15511619 -topo11,-89,0 > venus-topo-11--89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p3 -s0.7064 -fPJadss -n15511619 -topo11,89,50000 > venus-topo-11-89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p3 -s0.7064 -fPJadss -n15511619 -topo11,-89,50000 > venus-topo-11--89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p3 -s0.7064 -fPJadss -n15511619 -topo179,0,50000 > venus-topo-179-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p3 -s0.7064 -fPJadss -n15511619 -topo-179,0,50000 > venus-topo--179-0-50000 &
  // -0.7922448 .. 1.4149243 at -topo0,0,50000
  // -0.7912426 .. 1.4148820 at -topo11,0,0
  // -0.7917265 .. 1.4149614 at -topo11,0,50000	<-- MAX
  // -0.7304503 .. 1.4050099 at -topo11,0,-6300000
  // -0.7307662 .. 1.4050596 at -topo11,89,0
  // -0.7307661 .. 1.4050596 at -topo11,-89,0
  // -0.7307744 .. 1.4050609 at -topo11,89,50000
  // -0.7307743 .. 1.4050609 at -topo11,-89,50000
  // -0.7923691 .. 1.4147885 at -topo179,0,50000
  // -0.7927519 .. 1.4148072 at -topo-179,0,50000	<-- MIN
  static final double SHUKRA_MAX_TOPO_RECT_SPEED = 1.4149614;
  static final double SHUKRA_MIN_TOPO_RECT_SPEED = -0.7927519;
  static final double SHUKRA_MAX_TOPO_RECT_ACCEL = 1./0.;
  static final double SHUKRA_MIN_TOPO_RECT_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double SHUKRA_MAX_HELIO_RECT_SPEED = 1./0.;
  static final double SHUKRA_MIN_HELIO_RECT_SPEED = 1./0.;
  static final double SHUKRA_MAX_HELIO_RECT_ACCEL = 1./0.;
  static final double SHUKRA_MIN_HELIO_RECT_ACCEL = 1./0.;

  static final double SHUKRA_MAX_DECL_SPEED = 0.5540340;
  static final double SHUKRA_MIN_DECL_SPEED = -0.5498661;
  static final double SHUKRA_MAX_DECL_ACCEL = 1./0.;
  static final double SHUKRA_MIN_DECL_ACCEL = 1./0.;
  // -0.5513874 .. 0.5551877 at -topo0,0,50000		<-- MAX
  // -0.5513890 .. 0.5551763 at -topo11,0,0
  // -0.5513894 .. 0.5551766 at -topo11,0,50000	<-- MIN
  // -0.5513546 .. 0.5551627 at -topo11,0,-6300000
  // -0.5513562 .. 0.5551689 at -topo11,89,0
  // -0.5513531 .. 0.5551568 at -topo11,-89,0
  // -0.5513562 .. 0.5551689 at -topo11,89,50000
  // -0.5513531 .. 0.5551567 at -topo11,-89,50000
  // -0.5513435 .. 0.5551473 at -topo179,0,50000
  // -0.5513430 .. 0.5551476 at -topo-179,0,50000
  static final double SHUKRA_MAX_TOPO_DECL_SPEED = 0.5551877;
  static final double SHUKRA_MIN_TOPO_DECL_SPEED = -0.5513894;
  static final double SHUKRA_MAX_TOPO_DECL_ACCEL = 1./0.;
  static final double SHUKRA_MIN_TOPO_DECL_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double SHUKRA_MAX_HELIO_DECL_SPEED = 1./0.;
  static final double SHUKRA_MIN_HELIO_DECL_SPEED = 1./0.;
  static final double SHUKRA_MAX_HELIO_DECL_ACCEL = 1./0.;
  static final double SHUKRA_MIN_HELIO_DECL_ACCEL = 1./0.;



///////////////////////////////////////////////////////////////
// MANGALA: ///////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double MANGALA_MAX_LON_SPEED = 0.794;
  static final double MANGALA_MIN_LON_SPEED = -0.720;
  static final double MANGALA_MAX_LON_ACCEL = 0.0146;
  static final double MANGALA_MIN_LON_ACCEL = -0.0152;
  static final double MANGALA_MAX_TOPO_LON_SPEED = 0.81;
  static final double MANGALA_MIN_TOPO_LON_SPEED = -0.425;
  static final double MANGALA_MAX_TOPO_LON_ACCEL = 0.16;
  static final double MANGALA_MIN_TOPO_LON_ACCEL = -0.159;
  static final double MANGALA_MAX_HELIO_LON_SPEED = 0.6390;
  static final double MANGALA_MIN_HELIO_LON_SPEED = 0.4337;
  static final double MANGALA_MAX_HELIO_LON_ACCEL = 0.0010154;
  static final double MANGALA_MIN_HELIO_LON_ACCEL = -0.0010040;

  static final double MANGALA_MAX_LAT_SPEED = 0.084;
  static final double MANGALA_MIN_LAT_SPEED = -0.0839;
  static final double MANGALA_MAX_LAT_ACCEL = 0.0035;
  static final double MANGALA_MIN_LAT_ACCEL = -0.00209;
  static final double MANGALA_MAX_TOPO_LAT_SPEED = 0.095;
  static final double MANGALA_MIN_TOPO_LAT_SPEED = -0.099;
  static final double MANGALA_MAX_TOPO_LAT_ACCEL = 0.0805;
  static final double MANGALA_MIN_TOPO_LAT_ACCEL = -0.074;
  static final double MANGALA_MAX_HELIO_LAT_SPEED = 0.01994;
  static final double MANGALA_MIN_HELIO_LAT_SPEED = -0.02097;
  static final double MANGALA_MAX_HELIO_LAT_ACCEL = 0.00023610;
  static final double MANGALA_MIN_HELIO_LAT_ACCEL = -0.0001698;

  static final double MANGALA_MAX_DIST_SPEED = 0.0101;
  static final double MANGALA_MIN_DIST_SPEED = -0.01028;
  static final double MANGALA_MAX_DIST_ACCEL = 0.000234;
  static final double MANGALA_MIN_DIST_ACCEL = -0.0000695;
  static final double MANGALA_MAX_TOPO_DIST_SPEED = 0.0103;
  static final double MANGALA_MIN_TOPO_DIST_SPEED = -0.0105;
  static final double MANGALA_MAX_TOPO_DIST_ACCEL = 0.00123;
  static final double MANGALA_MIN_TOPO_DIST_ACCEL = -0.0011;
  static final double MANGALA_MAX_HELIO_DIST_SPEED = 0.0013516;
  static final double MANGALA_MIN_HELIO_DIST_SPEED = -0.0013516;
  static final double MANGALA_MAX_HELIO_DIST_ACCEL = 0.000015148;
  static final double MANGALA_MIN_HELIO_DIST_ACCEL = -0.000010287;

  // time ./swetest -head -bj-3027215.5 -ejplde431.eph -edir./ephe -p4 -s0.7064 -fPJadss -n15511619 > mars_jpl
  static final double MANGALA_MAX_RECT_SPEED = 0.8706997;
  static final double MANGALA_MIN_RECT_SPEED = -0.4557325;
  static final double MANGALA_MAX_RECT_ACCEL = 1./0.;
  static final double MANGALA_MIN_RECT_ACCEL = 1./0.;
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p4 -s0.7064 -fPJadss -n15511619 -topo0,0,50000 > mars-topo-0-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p4 -s0.7064 -fPJadss -n15511619 -topo11,0,0 > mars-topo-11-0-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p4 -s0.7064 -fPJadss -n15511619 -topo11,0,50000 > mars-topo-11-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p4 -s0.7064 -fPJadss -n15511619 -topo11,0,-6300000 > mars-topo-11-0--6300000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p4 -s0.7064 -fPJadss -n15511619 -topo11,89,0 > mars-topo-11-89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p4 -s0.7064 -fPJadss -n15511619 -topo11,-89,0 > mars-topo-11--89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p4 -s0.7064 -fPJadss -n15511619 -topo11,89,50000 > mars-topo-11-89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p4 -s0.7064 -fPJadss -n15511619 -topo11,-89,50000 > mars-topo-11--89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p4 -s0.7064 -fPJadss -n15511619 -topo179,0,50000 > mars-topo-179-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p4 -s0.7064 -fPJadss -n15511619 -topo-179,0,50000 > mars-topo--179-0-50000 &
  // -0.4842397 .. 0.8803327 at -topo0,0,50000		<-- MAX
  // -0.4835498 .. 0.8801316 at -topo11,0,0
  // -0.4837636 .. 0.8801872 at -topo11,0,50000
  // -0.4570783 .. 0.8733273 at -topo11,0,-6300000
  // -0.4571120 .. 0.8733617 at -topo11,89,0
  // -0.4571120 .. 0.8733617 at -topo11,-89,0
  // -0.4571133 .. 0.8733626 at -topo11,89,50000
  // -0.4571132 .. 0.8733626 at -topo11,-89,50000
  // -0.4843882 .. 0.8800615 at -topo179,0,50000
  // -0.4843898 .. 0.8800073 at -topo-179,0,50000	<-- MIN
  static final double MANGALA_MAX_TOPO_RECT_SPEED = 0.8803327;
  static final double MANGALA_MIN_TOPO_RECT_SPEED = -0.4843898;
  static final double MANGALA_MAX_TOPO_RECT_ACCEL = 1./0.;
  static final double MANGALA_MIN_TOPO_RECT_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double MANGALA_MAX_HELIO_RECT_SPEED = 1./0.;
  static final double MANGALA_MIN_HELIO_RECT_SPEED = 1./0.;
  static final double MANGALA_MAX_HELIO_RECT_ACCEL = 1./0.;
  static final double MANGALA_MIN_HELIO_RECT_ACCEL = 1./0.;

  static final double MANGALA_MAX_DECL_SPEED = 0.3208319;
  static final double MANGALA_MIN_DECL_SPEED = -0.3337168;
  static final double MANGALA_MAX_DECL_ACCEL = 1./0.;
  static final double MANGALA_MIN_DECL_ACCEL = 1./0.;
  // -0.3359842 .. 0.3225565 at -topo0,0,50000		<-- MIN
  // -0.3359780 .. 0.3225492 at -topo11,0,0
  // -0.3359780 .. 0.3225501 at -topo11,0,50000
  // -0.3359714 .. 0.3224426 at -topo11,0,-6300000
  // -0.3359715 .. 0.3224438 at -topo11,89,0
  // -0.3359714 .. 0.3224412 at -topo11,-89,0
  // -0.3359715 .. 0.3224438 at -topo11,89,50000
  // -0.3359714 .. 0.3224412 at -topo11,-89,50000
  // -0.3359696 .. 0.3226173 at -topo179,0,50000	<-- MAX
  // -0.3359694 .. 0.3226168 at -topo-179,0,50000
  static final double MANGALA_MAX_TOPO_DECL_SPEED = 0.3226173;
  static final double MANGALA_MIN_TOPO_DECL_SPEED = -0.3359842;
  static final double MANGALA_MAX_TOPO_DECL_ACCEL = 1./0.;
  static final double MANGALA_MIN_TOPO_DECL_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double MANGALA_MAX_HELIO_DECL_SPEED = 1./0.;
  static final double MANGALA_MIN_HELIO_DECL_SPEED = 1./0.;
  static final double MANGALA_MAX_HELIO_DECL_ACCEL = 1./0.;
  static final double MANGALA_MIN_HELIO_DECL_ACCEL = 1./0.;



///////////////////////////////////////////////////////////////
// GURU: //////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double GURU_MAX_LON_SPEED = 0.244;
  static final double GURU_MIN_LON_SPEED = -0.1369;
  static final double GURU_MAX_LON_ACCEL = 0.00354;
  static final double GURU_MIN_LON_ACCEL = -0.00341;
  static final double GURU_MAX_TOPO_LON_SPEED = 0.247;
  static final double GURU_MIN_TOPO_LON_SPEED = -0.1405;
  static final double GURU_MAX_TOPO_LON_ACCEL = 0.0167;
  static final double GURU_MIN_TOPO_LON_ACCEL = -0.0167;
  static final double GURU_MAX_HELIO_LON_SPEED = 0.09287;
  static final double GURU_MIN_HELIO_LON_SPEED = 0.074689;
  static final double GURU_MAX_HELIO_LON_ACCEL = 0.000036229;
  static final double GURU_MIN_HELIO_LON_ACCEL = -0.000036650;

  static final double GURU_MAX_LAT_SPEED = 0.0063;
  static final double GURU_MIN_LAT_SPEED = -0.0062;
  static final double GURU_MAX_LAT_ACCEL = 0.000164;
  static final double GURU_MIN_LAT_ACCEL = -0.000144;
  static final double GURU_MAX_TOPO_LAT_SPEED = 0.0074;
  static final double GURU_MIN_TOPO_LAT_SPEED = -0.0074;
  static final double GURU_MAX_TOPO_LAT_ACCEL = 0.0064;
  static final double GURU_MIN_TOPO_LAT_ACCEL = -0.00601;
  static final double GURU_MAX_HELIO_LAT_SPEED = 0.0024277;
  static final double GURU_MIN_HELIO_LAT_SPEED = -0.002620;
  static final double GURU_MAX_HELIO_LAT_ACCEL = 0.000013982;
  static final double GURU_MIN_HELIO_LAT_ACCEL = -0.000013189;

  static final double GURU_MAX_DIST_SPEED = 0.0163;
  static final double GURU_MIN_DIST_SPEED = -0.0164;
  static final double GURU_MAX_DIST_ACCEL = 0.000325; // ???
  static final double GURU_MIN_DIST_ACCEL = -0.000225;
  static final double GURU_MAX_TOPO_DIST_SPEED = 0.0165;
  static final double GURU_MIN_TOPO_DIST_SPEED = -0.0166;
  static final double GURU_MAX_TOPO_DIST_ACCEL = 0.00133; // ???
  static final double GURU_MIN_TOPO_DIST_ACCEL = -0.00122;
  static final double GURU_MAX_HELIO_DIST_SPEED = 0.00040998;
  static final double GURU_MIN_HELIO_DIST_SPEED = -0.00040970;
  static final double GURU_MAX_HELIO_DIST_ACCEL = 0.00000077866;
  static final double GURU_MIN_HELIO_DIST_ACCEL = -0.0000006786;

  // time ./swetest -head -bj-3027215.5 -ejplde431.eph -edir./ephe -p5 -s0.7064 -fPJadss -n15511619 > jupiter_jpl
  static final double GURU_MAX_RECT_SPEED = 0.2659874;
  static final double GURU_MIN_RECT_SPEED = -0.1530986;
  static final double GURU_MAX_RECT_ACCEL = 1./0.;
  static final double GURU_MIN_RECT_ACCEL = 1./0.;
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p5 -s0.7064 -fPJadss -n15511619 -topo0,0,50000 > jupiter-topo-0-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p5 -s0.7064 -fPJadss -n15511619 -topo11,0,0 > jupiter-topo-11-0-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p5 -s0.7064 -fPJadss -n15511619 -topo11,0,50000 > jupiter-topo-11-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p5 -s0.7064 -fPJadss -n15511619 -topo11,0,-6300000 > jupiter-topo-11-0--6300000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p5 -s0.7064 -fPJadss -n15511619 -topo11,89,0 > jupiter-topo-11-89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p5 -s0.7064 -fPJadss -n15511619 -topo11,-89,0 > jupiter-topo-11--89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p5 -s0.7064 -fPJadss -n15511619 -topo11,89,50000 > jupiter-topo-11-89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p5 -s0.7064 -fPJadss -n15511619 -topo11,-89,50000 > jupiter-topo-11--89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p5 -s0.7064 -fPJadss -n15511619 -topo179,0,50000 > jupiter-topo-179-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p5 -s0.7064 -fPJadss -n15511619 -topo-179,0,50000 > jupiter-topo--179-0-50000 &
  // -0.1728654 .. 0.2749562 at -topo0,0,50000
  // -0.1729813 .. 0.2750843 at -topo11,0,0
  // -0.1730138 .. 0.2751051 at -topo11,0,50000	<-- MAX
  // -0.1697955 .. 0.2724612 at -topo11,0,-6300000
  // -0.1697963 .. 0.2724751 at -topo11,89,0
  // -0.1697962 .. 0.2724751 at -topo11,-89,0
  // -0.1697963 .. 0.2724755 at -topo11,89,50000
  // -0.1697962 .. 0.2724755 at -topo11,-89,50000
  // -0.1730639 .. 0.2745095 at -topo179,0,50000
  // -0.1731234 .. 0.2744408 at -topo-179,0,50000	<-- MIN
  static final double GURU_MAX_TOPO_RECT_SPEED = 0.2751051;
  static final double GURU_MIN_TOPO_RECT_SPEED = -0.1731234;
  static final double GURU_MAX_TOPO_RECT_ACCEL = 1./0.;
  static final double GURU_MIN_TOPO_RECT_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double GURU_MAX_HELIO_RECT_SPEED = 1./0.;
  static final double GURU_MIN_HELIO_RECT_SPEED = 1./0.;
  static final double GURU_MAX_HELIO_RECT_ACCEL = 1./0.;
  static final double GURU_MIN_HELIO_RECT_ACCEL = 1./0.;

  static final double GURU_MAX_DECL_SPEED = 0.0967245;
  static final double GURU_MIN_DECL_SPEED = -0.0990172;
  static final double GURU_MAX_DECL_ACCEL = 1./0.;
  static final double GURU_MIN_DECL_ACCEL = 1./0.;
  // -0.1051247 .. 0.1025999 at -topo0,0,50000
  // -0.1051229 .. 0.1025969 at -topo11,0,0
  // -0.1051228 .. 0.1025974 at -topo11,0,50000
  // -0.1051358 .. 0.1025432 at -topo11,0,-6300000
  // -0.1051357 .. 0.1025436 at -topo11,89,0
  // -0.1051357 .. 0.1025434 at -topo11,-89,0
  // -0.1051357 .. 0.1025436 at -topo11,89,50000
  // -0.1051357 .. 0.1025434 at -topo11,-89,50000
  // -0.1051470 .. 0.1026345 at -topo179,0,50000	<-- MAX
  // -0.1051474 .. 0.1026331 at -topo-179,0,50000	<-- MIN
  static final double GURU_MAX_TOPO_DECL_SPEED = 0.1026345;
  static final double GURU_MIN_TOPO_DECL_SPEED = -0.1051474;
  static final double GURU_MAX_TOPO_DECL_ACCEL = 1./0.;
  static final double GURU_MIN_TOPO_DECL_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double GURU_MAX_HELIO_DECL_SPEED = 1./0.;
  static final double GURU_MIN_HELIO_DECL_SPEED = 1./0.;
  static final double GURU_MAX_HELIO_DECL_ACCEL = 1./0.;
  static final double GURU_MIN_HELIO_DECL_ACCEL = 1./0.;



///////////////////////////////////////////////////////////////
// SHANI: /////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double SHANI_MAX_LON_SPEED = 0.13404;
  static final double SHANI_MIN_LON_SPEED = -0.084;
  static final double SHANI_MAX_LON_ACCEL = 0.00199;
  static final double SHANI_MIN_LON_ACCEL = -0.00195;
  static final double SHANI_MAX_TOPO_LON_SPEED = 0.134;
  static final double SHANI_MIN_TOPO_LON_SPEED = -0.0855;
  static final double SHANI_MAX_TOPO_LON_ACCEL = 0.0086;
  static final double SHANI_MIN_TOPO_LON_ACCEL = -0.00864;
  static final double SHANI_MAX_HELIO_LON_SPEED = 0.03929;
  static final double SHANI_MIN_HELIO_LON_SPEED = 0.028729;
  static final double SHANI_MAX_HELIO_LON_ACCEL = 0.000026497;
  static final double SHANI_MIN_HELIO_LON_ACCEL = -0.000026590;

  static final double SHANI_MAX_LAT_SPEED = 0.0055;
  static final double SHANI_MIN_LAT_SPEED = -0.0054;
  static final double SHANI_MAX_LAT_ACCEL = 0.000123;
  static final double SHANI_MIN_LAT_ACCEL = -0.000104;
  static final double SHANI_MAX_TOPO_LAT_SPEED = 0.006;
  static final double SHANI_MIN_TOPO_LAT_SPEED = -0.0059;
  static final double SHANI_MAX_TOPO_LAT_ACCEL = 0.0032;
  static final double SHANI_MIN_TOPO_LAT_ACCEL = -0.00301;
  static final double SHANI_MAX_HELIO_LAT_SPEED = 0.0016789;
  static final double SHANI_MIN_HELIO_LAT_SPEED = -0.001653;
  static final double SHANI_MAX_HELIO_LAT_ACCEL = 0.00001127;
  static final double SHANI_MIN_HELIO_LAT_ACCEL = -0.000011128;

  static final double SHANI_MAX_DIST_SPEED = 0.0168;
  static final double SHANI_MIN_DIST_SPEED = -0.0169;
  static final double SHANI_MAX_DIST_ACCEL = 0.000322;
  static final double SHANI_MIN_DIST_ACCEL = -0.00027;
  static final double SHANI_MAX_TOPO_DIST_SPEED = 0.017;
  static final double SHANI_MIN_TOPO_DIST_SPEED = -0.01702;
  static final double SHANI_MAX_TOPO_DIST_ACCEL = 0.00133;
  static final double SHANI_MIN_TOPO_DIST_ACCEL = -0.00127;
  static final double SHANI_MAX_HELIO_DIST_SPEED = 0.00043914;
  static final double SHANI_MIN_HELIO_DIST_SPEED = -0.00044091;
  static final double SHANI_MAX_HELIO_DIST_ACCEL = 0.00000043248;
  static final double SHANI_MIN_HELIO_DIST_ACCEL = -0.00000041039;

  // time ./swetest -head -bj-3027215.5 -ejplde431.eph -edir./ephe -p6 -s0.7064 -fPJadss -n15511619 > saturn_jpl
  static final double SHANI_MAX_RECT_SPEED = 0.1500197;
  static final double SHANI_MIN_RECT_SPEED = -0.0947058;
  static final double SHANI_MAX_RECT_ACCEL = 1./0.;
  static final double SHANI_MIN_RECT_ACCEL = 1./0.;
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p6 -s0.7064 -fPJadss -n15511619 -topo0,0,50000 > saturn-topo-0-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p6 -s0.7064 -fPJadss -n15511619 -topo11,0,0 > saturn-topo-11-0-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p6 -s0.7064 -fPJadss -n15511619 -topo11,0,50000 > saturn-topo-11-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p6 -s0.7064 -fPJadss -n15511619 -topo11,0,-6300000 > saturn-topo-11-0--6300000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p6 -s0.7064 -fPJadss -n15511619 -topo11,89,0 > saturn-topo-11-89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p6 -s0.7064 -fPJadss -n15511619 -topo11,-89,0 > saturn-topo-11--89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p6 -s0.7064 -fPJadss -n15511619 -topo11,89,50000 > saturn-topo-11-89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p6 -s0.7064 -fPJadss -n15511619 -topo11,-89,50000 > saturn-topo-11--89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p6 -s0.7064 -fPJadss -n15511619 -topo179,0,50000 > saturn-topo-179-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p6 -s0.7064 -fPJadss -n15511619 -topo-179,0,50000 > saturn-topo--179-0-50000 &
  // -0.1288822 .. 0.1632502 at -topo0,0,50000
  // -0.1288439 .. 0.1631619 at -topo11,0,0
  // -0.1288610 .. 0.1631750 at -topo11,0,50000
  // -0.1274977 .. 0.1617061 at -topo11,0,-6300000
  // -0.1274919 .. 0.1617037 at -topo11,89,0
  // -0.1274918 .. 0.1617036 at -topo11,-89,0
  // -0.1274918 .. 0.1617036 at -topo11,89,50000
  // -0.1274916 .. 0.1617036 at -topo11,-89,50000
  // -0.1293203 .. 0.1633044 at -topo179,0,50000	<-- MIN
  // -0.1292769 .. 0.1633152 at -topo-179,0,50000	<-- MAX
  static final double SHANI_MAX_TOPO_RECT_SPEED = 0.1633152;
  static final double SHANI_MIN_TOPO_RECT_SPEED = -0.1293203;
  static final double SHANI_MAX_TOPO_RECT_ACCEL = 1./0.;
  static final double SHANI_MIN_TOPO_RECT_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double SHANI_MAX_HELIO_RECT_SPEED = 1./0.;
  static final double SHANI_MIN_HELIO_RECT_SPEED = 1./0.;
  static final double SHANI_MAX_HELIO_RECT_ACCEL = 1./0.;
  static final double SHANI_MIN_HELIO_RECT_ACCEL = 1./0.;

  static final double SHANI_MAX_DECL_SPEED = 0.0549226;
  static final double SHANI_MIN_DECL_SPEED = -0.0566490;
  static final double SHANI_MAX_DECL_ACCEL = 1./0.;
  static final double SHANI_MIN_DECL_ACCEL = 1./0.;
  // -0.0663555 .. 0.0648684 at -topo0,0,50000
  // -0.0663566 .. 0.0648692 at -topo11,0,0
  // -0.0663564 .. 0.0648693 at -topo11,0,50000	<-- MAX
  // -0.0663788 .. 0.0648594 at -topo11,0,-6300000
  // -0.0663787 .. 0.0648595 at -topo11,89,0
  // -0.0663787 .. 0.0648594 at -topo11,-89,0
  // -0.0663787 .. 0.0648595 at -topo11,89,50000
  // -0.0663787 .. 0.0648594 at -topo11,-89,50000
  // -0.0664027 .. 0.0648502 at -topo179,0,50000	<-- MIN
  // -0.0664026 .. 0.0648501 at -topo-179,0,50000
  static final double SHANI_MAX_TOPO_DECL_SPEED = 0.0648693;
  static final double SHANI_MIN_TOPO_DECL_SPEED = 0.0648502;
  static final double SHANI_MAX_TOPO_DECL_ACCEL = 1./0.;
  static final double SHANI_MIN_TOPO_DECL_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double SHANI_MAX_HELIO_DECL_SPEED = 1./0.;
  static final double SHANI_MIN_HELIO_DECL_SPEED = 1./0.;
  static final double SHANI_MAX_HELIO_DECL_ACCEL = 1./0.;
  static final double SHANI_MIN_HELIO_DECL_ACCEL = 1./0.;



///////////////////////////////////////////////////////////////
// URANUS: ////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double URANUS_MAX_LON_SPEED = 0.067;
  static final double URANUS_MIN_LON_SPEED = -0.044;
  static final double URANUS_MAX_LON_ACCEL = 0.000929;
  static final double URANUS_MIN_LON_ACCEL = -0.000975;
  static final double URANUS_MAX_TOPO_LON_SPEED = 0.067;
  static final double URANUS_MIN_TOPO_LON_SPEED = -0.045;
  static final double URANUS_MAX_TOPO_LON_ACCEL = 0.00408;
  static final double URANUS_MIN_TOPO_LON_ACCEL = -0.00414;
  static final double URANUS_MAX_HELIO_LON_SPEED = 0.013090;
  static final double URANUS_MIN_HELIO_LON_SPEED = 0.010609;
  static final double URANUS_MAX_HELIO_LON_ACCEL = 0.000023908;
  static final double URANUS_MIN_HELIO_LON_ACCEL = -0.000024088;

  static final double URANUS_MAX_LAT_SPEED = 0.00082;
  static final double URANUS_MIN_LAT_SPEED = -0.00079;
  static final double URANUS_MAX_LAT_ACCEL = 0.000073;
  static final double URANUS_MIN_LAT_ACCEL = -0.000078;
  static final double URANUS_MAX_TOPO_LAT_SPEED = 0.0011;
  static final double URANUS_MIN_TOPO_LAT_SPEED = -0.0011;
  static final double URANUS_MAX_TOPO_LAT_ACCEL = 0.0018;
  static final double URANUS_MIN_TOPO_LAT_ACCEL = -0.0014;
  static final double URANUS_MAX_HELIO_LAT_SPEED = 0.00022338;
  static final double URANUS_MIN_HELIO_LAT_SPEED = -0.00020480;
  static final double URANUS_MAX_HELIO_LAT_ACCEL = 0.0000105550;
  static final double URANUS_MIN_HELIO_LAT_ACCEL = -0.000010525;

  static final double URANUS_MAX_DIST_SPEED = 0.0174;
  static final double URANUS_MIN_DIST_SPEED = -0.0174;
  static final double URANUS_MAX_DIST_ACCEL = 0.00032;
  static final double URANUS_MIN_DIST_ACCEL = -0.00029;
  static final double URANUS_MAX_TOPO_DIST_SPEED = 0.0177;
  static final double URANUS_MIN_TOPO_DIST_SPEED = -0.0176;
  static final double URANUS_MAX_TOPO_DIST_ACCEL = 0.0014;
  static final double URANUS_MIN_TOPO_DIST_ACCEL = -0.0013;
  static final double URANUS_MAX_HELIO_DIST_SPEED = 0.00020103;
  static final double URANUS_MIN_HELIO_DIST_SPEED = -0.00020132;
  static final double URANUS_MAX_HELIO_DIST_ACCEL = 0.00000028679;
  static final double URANUS_MIN_HELIO_DIST_ACCEL = -0.000000229;

  // time ./swetest -head -bj-3027215.5 -ejplde431.eph -edir./ephe -p7 -s0.7064 -fPJadss -n15511619 > uranus_jpl
  static final double URANUS_MAX_RECT_SPEED = 0.0702972;
  static final double URANUS_MIN_RECT_SPEED = -0.0482331;
  static final double URANUS_MAX_RECT_ACCEL = 1./0.;
  static final double URANUS_MIN_RECT_ACCEL = 1./0.;
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p7 -s0.7064 -fPJadss -n15511619 -topo0,0,50000 > uranus-topo-0-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p7 -s0.7064 -fPJadss -n15511619 -topo11,0,0 > uranus-topo-11-0-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p7 -s0.7064 -fPJadss -n15511619 -topo11,0,50000 > uranus-topo-11-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p7 -s0.7064 -fPJadss -n15511619 -topo11,0,-6300000 > uranus-topo-11-0--6300000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p7 -s0.7064 -fPJadss -n15511619 -topo11,89,0 > uranus-topo-11-89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p7 -s0.7064 -fPJadss -n15511619 -topo11,-89,0 > uranus-topo-11--89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p7 -s0.7064 -fPJadss -n15511619 -topo11,89,50000 > uranus-topo-11-89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p7 -s0.7064 -fPJadss -n15511619 -topo11,-89,50000 > uranus-topo-11--89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p7 -s0.7064 -fPJadss -n15511619 -topo179,0,50000 > uranus-topo-179-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p7 -s0.7064 -fPJadss -n15511619 -topo-179,0,50000 > uranus-topo--179-0-50000 &
  // -0.1243592 .. 0.1050555 at -topo0,0,50000		<-- MIN	<-- MAX
  // -0.1242312 .. 0.1049450 at -topo11,0,0
  // -0.1242357 .. 0.1049492 at -topo11,0,50000
  // -0.1236645 .. 0.1044812 at -topo11,0,-6300000
  // -0.1236676 .. 0.1044800 at -topo11,89,0
  // -0.1236674 .. 0.1044799 at -topo11,-89,0
  // -0.1236676 .. 0.1044799 at -topo11,89,50000
  // -0.1236675 .. 0.1044799 at -topo11,-89,50000
  // -0.1240597 .. 0.1050007 at -topo179,0,50000
  // -0.1240382 .. 0.1050127 at -topo-179,0,50000
  static final double URANUS_MAX_TOPO_RECT_SPEED = 0.1050555;
  static final double URANUS_MIN_TOPO_RECT_SPEED = -0.1243592;
  static final double URANUS_MAX_TOPO_RECT_ACCEL = 1./0.;
  static final double URANUS_MIN_TOPO_RECT_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double URANUS_MAX_HELIO_RECT_SPEED = 1./0.;
  static final double URANUS_MIN_HELIO_RECT_SPEED = 1./0.;
  static final double URANUS_MAX_HELIO_RECT_ACCEL = 1./0.;
  static final double URANUS_MIN_HELIO_RECT_ACCEL = 1./0.;

  static final double URANUS_MAX_DECL_SPEED = 0.0263998;
  static final double URANUS_MIN_DECL_SPEED = -0.0254980;
  static final double URANUS_MAX_DECL_ACCEL = 1./0.;
  static final double URANUS_MIN_DECL_ACCEL = 1./0.;
  // -0.0459399 .. 0.0459600 at -topo0,0,50000
  // -0.0459265 .. 0.0459610 at -topo11,0,0		<-- MAX
  // -0.0459269 .. 0.0459612 at -topo11,0,50000
  // -0.0459495 .. 0.0459395 at -topo11,0,-6300000
  // -0.0459492 .. 0.0459396 at -topo11,89,0
  // -0.0459491 .. 0.0459396 at -topo11,-89,0
  // -0.0459491 .. 0.0459396 at -topo11,89,50000
  // -0.0459491 .. 0.0459396 at -topo11,-89,50000
  // -0.0460239 .. 0.0459206 at -topo179,0,50000	<-- MIN
  // -0.0460238 .. 0.0459206 at -topo-179,0,50000
  static final double URANUS_MAX_TOPO_DECL_SPEED = 0.0459610;
  static final double URANUS_MIN_TOPO_DECL_SPEED = -0.0460239;
  static final double URANUS_MAX_TOPO_DECL_ACCEL = 1./0.;
  static final double URANUS_MIN_TOPO_DECL_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double URANUS_MAX_HELIO_DECL_SPEED = 1./0.;
  static final double URANUS_MIN_HELIO_DECL_SPEED = 1./0.;
  static final double URANUS_MAX_HELIO_DECL_ACCEL = 1./0.;
  static final double URANUS_MIN_HELIO_DECL_ACCEL = 1./0.;



///////////////////////////////////////////////////////////////
// NEPTUNE: ///////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double NEPTUNE_MAX_LON_SPEED = 0.040;
  static final double NEPTUNE_MIN_LON_SPEED = -0.0286;
  static final double NEPTUNE_MAX_LON_ACCEL = 0.000612;
  static final double NEPTUNE_MIN_LON_ACCEL = -0.000606;
  static final double NEPTUNE_MAX_TOPO_LON_SPEED = 0.043; // ???
  static final double NEPTUNE_MIN_TOPO_LON_SPEED = -0.0295;
  static final double NEPTUNE_MAX_TOPO_LON_ACCEL = 0.00253;
  static final double NEPTUNE_MIN_TOPO_LON_ACCEL = -0.00252;
  static final double NEPTUNE_MAX_HELIO_LON_SPEED = 0.006223;
  static final double NEPTUNE_MIN_HELIO_LON_SPEED = 0.00584;
  static final double NEPTUNE_MAX_HELIO_LON_ACCEL = 0.000023804;
  static final double NEPTUNE_MIN_HELIO_LON_ACCEL = -0.000023845;

  static final double NEPTUNE_MAX_LAT_SPEED = 0.0013;
  static final double NEPTUNE_MIN_LAT_SPEED = -0.0013;
  static final double NEPTUNE_MAX_LAT_ACCEL = 0.000069;
  static final double NEPTUNE_MIN_LAT_ACCEL = -0.000065;
  static final double NEPTUNE_MAX_TOPO_LAT_SPEED = 0.00156;
  static final double NEPTUNE_MIN_TOPO_LAT_SPEED = -0.00143;
  static final double NEPTUNE_MAX_TOPO_LAT_ACCEL = 0.00087;
  static final double NEPTUNE_MIN_TOPO_LAT_ACCEL = -0.000854;
  static final double NEPTUNE_MAX_HELIO_LAT_SPEED = 0.0002670;
  static final double NEPTUNE_MIN_HELIO_LAT_SPEED = -0.0002728;
  static final double NEPTUNE_MAX_HELIO_LAT_ACCEL = 0.0000106092;
  static final double NEPTUNE_MIN_HELIO_LAT_ACCEL = -0.000010590;

  static final double NEPTUNE_MAX_DIST_SPEED = 0.0175;
  static final double NEPTUNE_MIN_DIST_SPEED = -0.0175;
  static final double NEPTUNE_MAX_DIST_ACCEL = 0.000316;
  static final double NEPTUNE_MIN_DIST_ACCEL = -0.0003;
  static final double NEPTUNE_MAX_TOPO_DIST_SPEED = 0.0177;
  static final double NEPTUNE_MIN_TOPO_DIST_SPEED = -0.0177;
  static final double NEPTUNE_MAX_TOPO_DIST_ACCEL = 0.00135;
  static final double NEPTUNE_MIN_TOPO_DIST_ACCEL = -0.00129;
  static final double NEPTUNE_MAX_HELIO_DIST_SPEED = 0.000038700;
  static final double NEPTUNE_MIN_HELIO_DIST_SPEED = -0.000038379;
  static final double NEPTUNE_MAX_HELIO_DIST_ACCEL = 0.00000022853;
  static final double NEPTUNE_MIN_HELIO_DIST_ACCEL = -0.00000023012;

  // time ./swetest -head -bj-3027215.5 -ejplde431.eph -edir./ephe -p8 -s0.7064 -fPJadss -n15511619 > neptun_jpl
  static final double NEPTUNE_MAX_RECT_SPEED = 0.0425179;
  static final double NEPTUNE_MIN_RECT_SPEED = -0.0320257;
  static final double NEPTUNE_MAX_RECT_ACCEL = 1./0.;
  static final double NEPTUNE_MIN_RECT_ACCEL = 1./0.;
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p8 -s0.7064 -fPJadss -n15511619 -topo0,0,50000 > neptun-topo-0-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p8 -s0.7064 -fPJadss -n15511619 -topo11,0,0 > neptun-topo-11-0-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p8 -s0.7064 -fPJadss -n15511619 -topo11,0,50000 > neptun-topo-11-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p8 -s0.7064 -fPJadss -n15511619 -topo11,0,-6300000 > neptun-topo-11-0--6300000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p8 -s0.7064 -fPJadss -n15511619 -topo11,89,0 > neptun-topo-11-89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p8 -s0.7064 -fPJadss -n15511619 -topo11,-89,0 > neptun-topo-11--89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p8 -s0.7064 -fPJadss -n15511619 -topo11,89,50000 > neptun-topo-11-89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p8 -s0.7064 -fPJadss -n15511619 -topo11,-89,50000 > neptun-topo-11--89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p8 -s0.7064 -fPJadss -n15511619 -topo179,0,50000 > neptun-topo-179-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p8 -s0.7064 -fPJadss -n15511619 -topo-179,0,50000 > neptun-topo--179-0-50000 &
  // -0.1568381 .. 0.1104760 at -topo0,0,50000		<-- MIN
  // -0.1568259 .. 0.1103960 at -topo11,0,0
  // -0.1568303 .. 0.1103985 at -topo11,0,50000
  // -0.1562692 .. 0.1103679 at -topo11,0,-6300000
  // -0.1562722 .. 0.1103677 at -topo11,89,0
  // -0.1562721 .. 0.1103678 at -topo11,-89,0
  // -0.1562723 .. 0.1103677 at -topo11,89,50000
  // -0.1562722 .. 0.1103678 at -topo11,-89,50000
  // -0.1558474 .. 0.1105298 at -topo179,0,50000	<-- MAX
  // -0.1558562 .. 0.1105202 at -topo-179,0,50000
  static final double NEPTUNE_MAX_TOPO_RECT_SPEED = 0.1105298;
  static final double NEPTUNE_MIN_TOPO_RECT_SPEED = 0.1104760;
  static final double NEPTUNE_MAX_TOPO_RECT_ACCEL = 1./0.;
  static final double NEPTUNE_MIN_TOPO_RECT_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double NEPTUNE_MAX_HELIO_RECT_SPEED = 1./0.;
  static final double NEPTUNE_MIN_HELIO_RECT_SPEED = 1./0.;
  static final double NEPTUNE_MAX_HELIO_RECT_ACCEL = 1./0.;
  static final double NEPTUNE_MIN_HELIO_RECT_ACCEL = 1./0.;

  static final double NEPTUNE_MAX_DECL_SPEED = 0.0156134;
  static final double NEPTUNE_MIN_DECL_SPEED = -0.0157893;
  static final double NEPTUNE_MAX_DECL_ACCEL = 1./0.;
  static final double NEPTUNE_MIN_DECL_ACCEL = 1./0.;
  // -0.0515829 .. 0.0510428 at -topo0,0,50000
  // -0.0516209 .. 0.0510078 at -topo11,0,0
  // -0.0516207 .. 0.0510078 at -topo11,0,50000
  // -0.0516483 .. 0.0510044 at -topo11,0,-6300000
  // -0.0516482 .. 0.0510044 at -topo11,89,0
  // -0.0516481 .. 0.0510044 at -topo11,-89,0
  // -0.0516482 .. 0.0510044 at -topo11,89,50000
  // -0.0516481 .. 0.0510044 at -topo11,-89,50000
  // -0.0517976 .. 0.0511087 at -topo179,0,50000
  // -0.0517984 .. 0.0511090 at -topo-179,0,50000	<-- MIN	<-- MAX
  static final double NEPTUNE_MAX_TOPO_DECL_SPEED = 0.0511090;
  static final double NEPTUNE_MIN_TOPO_DECL_SPEED = -0.0517984;
  static final double NEPTUNE_MAX_TOPO_DECL_ACCEL = 1./0.;
  static final double NEPTUNE_MIN_TOPO_DECL_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double NEPTUNE_MAX_HELIO_DECL_SPEED = 1./0.;
  static final double NEPTUNE_MIN_HELIO_DECL_SPEED = 1./0.;
  static final double NEPTUNE_MAX_HELIO_DECL_ACCEL = 1./0.;
  static final double NEPTUNE_MIN_HELIO_DECL_ACCEL = 1./0.;



///////////////////////////////////////////////////////////////
// PLUTO: /////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double PLUTO_MAX_LON_SPEED = 0.041;
  static final double PLUTO_MIN_LON_SPEED = -0.0284;
  static final double PLUTO_MAX_LON_ACCEL = 0.000601;
  static final double PLUTO_MIN_LON_ACCEL = -0.00065; // ????
  static final double PLUTO_MAX_TOPO_LON_SPEED = 0.0413;
  static final double PLUTO_MIN_TOPO_LON_SPEED = -0.0288;
  static final double PLUTO_MAX_TOPO_LON_ACCEL = 0.00253;
  static final double PLUTO_MIN_TOPO_LON_ACCEL = -0.0026; // ????
  static final double PLUTO_MAX_HELIO_LON_SPEED = 0.0072091;
  static final double PLUTO_MIN_HELIO_LON_SPEED = 0.0025030;
  static final double PLUTO_MAX_HELIO_LON_ACCEL = 0.000024137;
  static final double PLUTO_MIN_HELIO_LON_ACCEL = -0.00002406;

  static final double PLUTO_MAX_LAT_SPEED = 0.0101;
  static final double PLUTO_MIN_LAT_SPEED = -0.00998;
  static final double PLUTO_MAX_LAT_ACCEL = 0.000159;
  static final double PLUTO_MIN_LAT_ACCEL = -0.000188;
  static final double PLUTO_MAX_TOPO_LAT_SPEED = 0.0102;
  static final double PLUTO_MIN_TOPO_LAT_SPEED = -0.011;
  static final double PLUTO_MAX_TOPO_LAT_ACCEL = 0.0013;
  static final double PLUTO_MIN_TOPO_LAT_ACCEL = -0.0014;
  static final double PLUTO_MAX_HELIO_LAT_SPEED = 0.0012607;
  static final double PLUTO_MIN_HELIO_LAT_SPEED = -0.00170212;
  static final double PLUTO_MAX_HELIO_LAT_ACCEL = 0.000010674;
  static final double PLUTO_MIN_HELIO_LAT_ACCEL = -0.000010758;

  static final double PLUTO_MAX_DIST_SPEED = 0.01805;
  static final double PLUTO_MIN_DIST_SPEED = -0.01805;
  static final double PLUTO_MAX_DIST_ACCEL = 0.000315;
  static final double PLUTO_MIN_DIST_ACCEL = -0.000296;
  static final double PLUTO_MAX_TOPO_DIST_SPEED = 0.0183;
  static final double PLUTO_MIN_TOPO_DIST_SPEED = -0.0183;
  static final double PLUTO_MAX_TOPO_DIST_ACCEL = 0.00135;
  static final double PLUTO_MIN_TOPO_DIST_ACCEL = -0.0013;
  static final double PLUTO_MAX_HELIO_DIST_SPEED = 0.00071348;
  static final double PLUTO_MIN_HELIO_DIST_SPEED = -0.00071142;
  static final double PLUTO_MAX_HELIO_DIST_ACCEL = 0.0000002773;
  static final double PLUTO_MIN_HELIO_DIST_ACCEL = -0.00000022049;

  // time ./swetest -head -bj-3027215.5 -ejplde431.eph -edir./ephe -p9 -s0.7064 -fPJadss -n15511619 > pluto_jpl
  static final double PLUTO_MAX_RECT_SPEED = 0.0518363;
  static final double PLUTO_MIN_RECT_SPEED = -0.0359654;
  static final double PLUTO_MAX_RECT_ACCEL = 1./0.;
  static final double PLUTO_MIN_RECT_ACCEL = 1./0.;
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p9 -s0.7064 -fPJadss -n15511619 -topo0,0,50000 > pluto-topo-0-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p9 -s0.7064 -fPJadss -n15511619 -topo11,0,0 > pluto-topo-11-0-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p9 -s0.7064 -fPJadss -n15511619 -topo11,0,50000 > pluto-topo-11-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p9 -s0.7064 -fPJadss -n15511619 -topo11,0,-6300000 > pluto-topo-11-0--6300000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p9 -s0.7064 -fPJadss -n15511619 -topo11,89,0 > pluto-topo-11-89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p9 -s0.7064 -fPJadss -n15511619 -topo11,-89,0 > pluto-topo-11--89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p9 -s0.7064 -fPJadss -n15511619 -topo11,89,50000 > pluto-topo-11-89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p9 -s0.7064 -fPJadss -n15511619 -topo11,-89,50000 > pluto-topo-11--89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p9 -s0.7064 -fPJadss -n15511619 -topo179,0,50000 > pluto-topo-179-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -p9 -s0.7064 -fPJadss -n15511619 -topo-179,0,50000 > pluto-topo--179-0-50000 &
  // -0.2561626 .. 0.1647922 at -topo0,0,50000
  // -0.2561895 .. 0.1647510 at -topo11,0,0
  // -0.2561863 .. 0.1647536 at -topo11,0,50000
  // -0.2565834 .. 0.1650370 at -topo11,0,-6300000
  // -0.2565814 .. 0.1650349 at -topo11,89,0
  // -0.2565811 .. 0.1650347 at -topo11,-89,0
  // -0.2565813 .. 0.1650348 at -topo11,89,50000
  // -0.2565811 .. 0.1650347 at -topo11,-89,50000
  // -0.2570153 .. 0.1654531 at -topo179,0,50000	<-- MIN
  // -0.2570124 .. 0.1654553 at -topo-179,0,50000	<-- MAX
  static final double PLUTO_MAX_TOPO_RECT_SPEED = 0.1654553;
  static final double PLUTO_MIN_TOPO_RECT_SPEED = -0.2570153;
  static final double PLUTO_MAX_TOPO_RECT_ACCEL = 1./0.;
  static final double PLUTO_MIN_TOPO_RECT_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double PLUTO_MAX_HELIO_RECT_SPEED = 1./0.;
  static final double PLUTO_MIN_HELIO_RECT_SPEED = 1./0.;
  static final double PLUTO_MAX_HELIO_RECT_ACCEL = 1./0.;
  static final double PLUTO_MIN_HELIO_RECT_ACCEL = 1./0.;

  static final double PLUTO_MAX_DECL_SPEED = 0.0189821;
  static final double PLUTO_MIN_DECL_SPEED = -0.0192680;
  static final double PLUTO_MAX_DECL_ACCEL = 1./0.;
  static final double PLUTO_MIN_DECL_ACCEL = 1./0.;
  // -0.0832814 .. 0.0850578 at -topo0,0,50000
  // -0.0833168 .. 0.0850874 at -topo11,0,0
  // -0.0833168 .. 0.0850884 at -topo11,0,50000		<-- MAX
  // -0.0833218 .. 0.0849652 at -topo11,0,-6300000
  // -0.0833218 .. 0.0849658 at -topo11,89,0
  // -0.0833218 .. 0.0849658 at -topo11,-89,0
  // -0.0833218 .. 0.0849658 at -topo11,89,50000
  // -0.0833218 .. 0.0849658 at -topo11,-89,50000
  // -0.0833655 .. 0.0848725 at -topo179,0,50000	<-- MIN
  // -0.0833592 .. 0.0848665 at -topo-179,0,50000
  static final double PLUTO_MAX_TOPO_DECL_SPEED = 0.0850884;
  static final double PLUTO_MIN_TOPO_DECL_SPEED = -0.0833655;
  static final double PLUTO_MAX_TOPO_DECL_ACCEL = 1./0.;
  static final double PLUTO_MIN_TOPO_DECL_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double PLUTO_MAX_HELIO_DECL_SPEED = 1./0.;
  static final double PLUTO_MIN_HELIO_DECL_SPEED = 1./0.;
  static final double PLUTO_MAX_HELIO_DECL_ACCEL = 1./0.;
  static final double PLUTO_MIN_HELIO_DECL_ACCEL = 1./0.;



///////////////////////////////////////////////////////////////
// MNODE: /////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double MNODE_MAX_LON_SPEED = -0.0528;
  static final double MNODE_MIN_LON_SPEED = -0.0531;
  static final double MNODE_MAX_LON_ACCEL = 0.0000249;
  static final double MNODE_MIN_LON_ACCEL = -0.0000244;
  /**
  * There is no meaning in calculating topocentric positions of mean elements,
  * so it more an academic kind of exercise...
  */
  static final double MNODE_MAX_TOPO_LON_SPEED = -0.0528;
  static final double MNODE_MIN_TOPO_LON_SPEED = -0.0531;
  static final double MNODE_MAX_TOPO_LON_ACCEL = 0.0000247;
  static final double MNODE_MIN_TOPO_LON_ACCEL = -0.0000252;
  // No heliocentric positions for the mean node...
  static final double MNODE_MAX_HELIO_LON_SPEED = 1./0.;
  static final double MNODE_MIN_HELIO_LON_SPEED = 1./0.;
  static final double MNODE_MAX_HELIO_LON_ACCEL = 1./0.;
  static final double MNODE_MIN_HELIO_LON_ACCEL = 1./0.;

  static final double MNODE_MAX_LAT_SPEED = 0.;
  static final double MNODE_MIN_LAT_SPEED = 0.;
  static final double MNODE_MAX_LAT_ACCEL = 0.;
  static final double MNODE_MIN_LAT_ACCEL = 0.;
  static final double MNODE_MAX_TOPO_LAT_SPEED = 0.;
  static final double MNODE_MIN_TOPO_LAT_SPEED = 0.;
  static final double MNODE_MAX_TOPO_LAT_ACCEL = 0.;
  static final double MNODE_MIN_TOPO_LAT_ACCEL = 0.;
  // No heliocentric positions for the mean node...
  static final double MNODE_MAX_HELIO_LAT_SPEED = 1./0.;
  static final double MNODE_MIN_HELIO_LAT_SPEED = 1./0.;
  static final double MNODE_MAX_HELIO_LAT_ACCEL = 1./0.;
  static final double MNODE_MIN_HELIO_LAT_ACCEL = 1./0.;

  static final double MNODE_MAX_DIST_SPEED = 0.;
  static final double MNODE_MIN_DIST_SPEED = 0.;
  static final double MNODE_MAX_DIST_ACCEL = 0.;
  static final double MNODE_MIN_DIST_ACCEL = 0.;
  static final double MNODE_MAX_TOPO_DIST_SPEED = 0.;
  static final double MNODE_MIN_TOPO_DIST_SPEED = 0.;
  static final double MNODE_MAX_TOPO_DIST_ACCEL = 0.;
  static final double MNODE_MIN_TOPO_DIST_ACCEL = 0.;
  // No heliocentric positions for the mean node...
  static final double MNODE_MAX_HELIO_DIST_SPEED = 1./0.;
  static final double MNODE_MIN_HELIO_DIST_SPEED = 1./0.;
  static final double MNODE_MAX_HELIO_DIST_ACCEL = 1./0.;
  static final double MNODE_MIN_HELIO_DIST_ACCEL = 1./0.;

  // time ./swetest -head -bj-3027215.5 -ejplde431.eph -edir./ephe -pm -s0.7064 -fPJadss -n15511619 > meannode_jpl
  static final double MNODE_MAX_RECT_SPEED = -0.0482372;
  static final double MNODE_MIN_RECT_SPEED = -0.0581265;
  static final double MNODE_MAX_RECT_ACCEL = 1./0.;
  static final double MNODE_MIN_RECT_ACCEL = 1./0.;
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pm -s0.7064 -fPJadss -n15511619 -topo0,0,50000 > meannode-topo-0-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pm -s0.7064 -fPJadss -n15511619 -topo11,0,0 > meannode-topo-11-0-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pm -s0.7064 -fPJadss -n15511619 -topo11,0,50000 > meannode-topo-11-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pm -s0.7064 -fPJadss -n15511619 -topo11,0,-6300000 > meannode-topo-11-0--6300000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pm -s0.7064 -fPJadss -n15511619 -topo11,89,0 > meannode-topo-11-89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pm -s0.7064 -fPJadss -n15511619 -topo11,-89,0 > meannode-topo-11--89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pm -s0.7064 -fPJadss -n15511619 -topo11,89,50000 > meannode-topo-11-89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pm -s0.7064 -fPJadss -n15511619 -topo11,-89,50000 > meannode-topo-11--89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pm -s0.7064 -fPJadss -n15511619 -topo179,0,50000 > meannode-topo-179-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pm -s0.7064 -fPJadss -n15511619 -topo-179,0,50000 > meannode-topo--179-0-50000 &
  // -0.0581265 .. -0.0482335 at -topo0,0,50000
  // -0.0581265 .. -0.0482335 at -topo11,0,0
  // -0.0581265 .. -0.0482335 at -topo11,0,50000
  // -0.0581265 .. -0.0482335 at -topo11,0,-6300000
  // -0.0581265 .. -0.0482335 at -topo11,89,0
  // -0.0581265 .. -0.0482335 at -topo11,-89,0
  // -0.0581265 .. -0.0482335 at -topo11,89,50000
  // -0.0581265 .. -0.0482335 at -topo11,-89,50000
  // -0.0581265 .. -0.0482335 at -topo179,0,50000
  // -0.0581265 .. -0.0482335 at -topo-179,0,50000
  static final double MNODE_MAX_TOPO_RECT_SPEED = MNODE_MAX_RECT_SPEED;
  static final double MNODE_MIN_TOPO_RECT_SPEED = MNODE_MIN_RECT_SPEED;
  static final double MNODE_MAX_TOPO_RECT_ACCEL = 1./0.;
  static final double MNODE_MIN_TOPO_RECT_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double MNODE_MAX_HELIO_RECT_SPEED = 1./0.;
  static final double MNODE_MIN_HELIO_RECT_SPEED = 1./0.;
  static final double MNODE_MAX_HELIO_RECT_ACCEL = 1./0.;
  static final double MNODE_MIN_HELIO_RECT_ACCEL = 1./0.;

  static final double MNODE_MAX_DECL_SPEED = 0.0217529;
  static final double MNODE_MIN_DECL_SPEED = -0.0217577;
  static final double MNODE_MAX_DECL_ACCEL = 1./0.;
  static final double MNODE_MIN_DECL_ACCEL = 1./0.;
  // -0.0217577 .. 0.0217529 at -topo0,0,50000
  // -0.0217577 .. 0.0217529 at -topo11,0,0
  // -0.0217577 .. 0.0217529 at -topo11,0,50000
  // ... -topo11,0,-6300000
  // ... -topo11,89,0
  // ... -topo11,-89,0
  // ... -topo11,89,50000
  // ... -topo11,-89,50000
  // ... -topo179,0,50000
  // ... -topo-179,0,50000
  static final double MNODE_MAX_TOPO_DECL_SPEED = MNODE_MAX_DECL_SPEED;
  static final double MNODE_MIN_TOPO_DECL_SPEED = MNODE_MIN_DECL_SPEED;
  static final double MNODE_MAX_TOPO_DECL_ACCEL = 1./0.;
  static final double MNODE_MIN_TOPO_DECL_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double MNODE_MAX_HELIO_DECL_SPEED = 1./0.;
  static final double MNODE_MIN_HELIO_DECL_SPEED = 1./0.;
  static final double MNODE_MAX_HELIO_DECL_ACCEL = 1./0.;
  static final double MNODE_MIN_HELIO_DECL_ACCEL = 1./0.;



///////////////////////////////////////////////////////////////
// TNODE: /////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double TNODE_MAX_LON_SPEED = 0.0328;
  static final double TNODE_MIN_LON_SPEED = -0.261;
  static final double TNODE_MAX_LON_ACCEL = 0.054;
  static final double TNODE_MIN_LON_ACCEL = -0.054;
  static final double TNODE_MAX_TOPO_LON_SPEED = 0.0328;
  static final double TNODE_MIN_TOPO_LON_SPEED = -0.261;
  static final double TNODE_MAX_TOPO_LON_ACCEL = 0.054;
  static final double TNODE_MIN_TOPO_LON_ACCEL = -0.054;
  // No heliocentric positions for the true node...
  static final double TNODE_MAX_HELIO_LON_SPEED = 1./0.;
  static final double TNODE_MIN_HELIO_LON_SPEED = 1./0.;
  static final double TNODE_MAX_HELIO_LON_ACCEL = 1./0.;
  static final double TNODE_MIN_HELIO_LON_ACCEL = 1./0.;

  static final double TNODE_MAX_LAT_SPEED = 0.;
  static final double TNODE_MIN_LAT_SPEED = 0.;
  static final double TNODE_MAX_LAT_ACCEL = 0.;
  static final double TNODE_MIN_LAT_ACCEL = 0.;
  static final double TNODE_MAX_TOPO_LAT_SPEED = 0.;
  static final double TNODE_MIN_TOPO_LAT_SPEED = 0.;
  static final double TNODE_MAX_TOPO_LAT_ACCEL = 0.;
  static final double TNODE_MIN_TOPO_LAT_ACCEL = 0.;
  // No heliocentric positions for the true node...
  static final double TNODE_MAX_HELIO_LAT_SPEED = 1./0.;
  static final double TNODE_MIN_HELIO_LAT_SPEED = 1./0.;
  static final double TNODE_MAX_HELIO_LAT_ACCEL = 1./0.;
  static final double TNODE_MIN_HELIO_LAT_ACCEL = 1./0.;



  static final double TNODE_MAX_DIST_SPEED = 0.0000228;
  static final double TNODE_MIN_DIST_SPEED = -0.0000216;
  static final double TNODE_MAX_DIST_ACCEL = 0.00000835;
  static final double TNODE_MIN_DIST_ACCEL = -0.0000086;
  static final double TNODE_MAX_TOPO_DIST_SPEED = 0.000022; // ????
  static final double TNODE_MIN_TOPO_DIST_SPEED = -0.000022; // ????
  static final double TNODE_MAX_TOPO_DIST_ACCEL = 0.00000835;
  static final double TNODE_MIN_TOPO_DIST_ACCEL = -0.00000856;
  // No heliocentric positions for the true node...
  static final double TNODE_MAX_HELIO_DIST_SPEED = 1./0.;
  static final double TNODE_MIN_HELIO_DIST_SPEED = 1./0.;
  static final double TNODE_MAX_HELIO_DIST_ACCEL = 1./0.;
  static final double TNODE_MIN_HELIO_DIST_ACCEL = 1./0.;

  // time ./swetest -head -bj-3027215.5 -ejplde431.eph -edir./ephe -pt -s0.7064 -fPJadss -n15511619 > truenode_jpl
  static final double TNODE_MAX_RECT_SPEED = 0.0359328;
  static final double TNODE_MIN_RECT_SPEED = -0.2850885;
  static final double TNODE_MAX_RECT_ACCEL = 1./0.;
  static final double TNODE_MIN_RECT_ACCEL = 1./0.;
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pt -s0.7064 -fPJadss -n15511619 -topo0,0,50000 > truenode-topo-0-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pt -s0.7064 -fPJadss -n15511619 -topo11,0,0 > truenode-topo-11-0-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pt -s0.7064 -fPJadss -n15511619 -topo11,0,50000 > truenode-topo-11-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pt -s0.7064 -fPJadss -n15511619 -topo11,0,-6300000 > truenode-topo-11-0--6300000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pt -s0.7064 -fPJadss -n15511619 -topo11,89,0 > truenode-topo-11-89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pt -s0.7064 -fPJadss -n15511619 -topo11,-89,0 > truenode-topo-11--89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pt -s0.7064 -fPJadss -n15511619 -topo11,89,50000 > truenode-topo-11-89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pt -s0.7064 -fPJadss -n15511619 -topo11,-89,50000 > truenode-topo-11--89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pt -s0.7064 -fPJadss -n15511619 -topo179,0,50000 > truenode-topo-179-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pt -s0.7064 -fPJadss -n15511619 -topo-179,0,50000 > truenode-topo--179-0-50000 &
  // -0.2850885 .. 0.0359328 at -topo0,0,50000
  // -0.2850885 .. 0.0359328 at -topo11,0,0
  // ... -topo11,0,50000
  // ... -topo11,0,-6300000
  // ... -topo11,89,0
  // ... -topo11,-89,0
  // ... -topo11,89,50000
  // ... -topo11,-89,50000
  // ... -topo179,0,50000
  // ... -topo-179,0,50000
  static final double TNODE_MAX_TOPO_RECT_SPEED = TNODE_MAX_RECT_SPEED;
  static final double TNODE_MIN_TOPO_RECT_SPEED = TNODE_MIN_RECT_SPEED;
  static final double TNODE_MAX_TOPO_RECT_ACCEL = 1./0.;
  static final double TNODE_MIN_TOPO_RECT_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double TNODE_MAX_HELIO_RECT_SPEED = 1./0.;
  static final double TNODE_MIN_HELIO_RECT_SPEED = 1./0.;
  static final double TNODE_MAX_HELIO_RECT_ACCEL = 1./0.;
  static final double TNODE_MIN_HELIO_RECT_ACCEL = 1./0.;

  static final double TNODE_MAX_DECL_SPEED = 0.1062415;
  static final double TNODE_MIN_DECL_SPEED = -0.1071840;
  static final double TNODE_MAX_DECL_ACCEL = 1./0.;
  static final double TNODE_MIN_DECL_ACCEL = 1./0.;
  // ... -topo0,0,50000
  // ... -topo11,0,0
  // ... -topo11,0,50000
  // ... -topo11,0,-6300000
  // ... -topo11,89,0
  // ... -topo11,-89,0
  // ... -topo11,89,50000
  // ... -topo11,-89,50000
  // ... -topo179,0,50000
  // ... -topo-179,0,50000
  static final double TNODE_MAX_TOPO_DECL_SPEED = TNODE_MAX_DECL_SPEED;
  static final double TNODE_MIN_TOPO_DECL_SPEED = TNODE_MIN_DECL_SPEED;
  static final double TNODE_MAX_TOPO_DECL_ACCEL = 1./0.;
  static final double TNODE_MIN_TOPO_DECL_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double TNODE_MAX_HELIO_DECL_SPEED = 1./0.;
  static final double TNODE_MIN_HELIO_DECL_SPEED = 1./0.;
  static final double TNODE_MAX_HELIO_DECL_ACCEL = 1./0.;
  static final double TNODE_MIN_HELIO_DECL_ACCEL = 1./0.;



///////////////////////////////////////////////////////////////
// MEAN APOGEE (Lilith): //////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double MAPOGEE_MAX_LON_SPEED = 0.114;
  static final double MAPOGEE_MIN_LON_SPEED = 0.113;
  static final double MAPOGEE_MAX_LON_ACCEL = 0.000031;
  static final double MAPOGEE_MIN_LON_ACCEL = -0.000031;
  /**
  * There is no meaning in calculating topocentric positions of mean elements,
  * so it's more an academic kind of exercise...
  */
  static final double MAPOGEE_MAX_TOPO_LON_SPEED = 0.12;
  static final double MAPOGEE_MIN_TOPO_LON_SPEED = 0.12;
  static final double MAPOGEE_MAX_TOPO_LON_ACCEL = 0.000031;
  static final double MAPOGEE_MIN_TOPO_LON_ACCEL = 0.000031;
  // No heliocentric positions for the mean apogee...
  static final double MAPOGEE_MAX_HELIO_LON_SPEED = 1./0;
  static final double MAPOGEE_MIN_HELIO_LON_SPEED = 1./0;
  static final double MAPOGEE_MAX_HELIO_LON_ACCEL = 1./0;
  static final double MAPOGEE_MIN_HELIO_LON_ACCEL = 1./0;

  static final double MAPOGEE_MAX_LAT_SPEED = 0.0155;
  static final double MAPOGEE_MIN_LAT_SPEED = -0.016;
  static final double MAPOGEE_MAX_LAT_ACCEL = 0.000052;
  static final double MAPOGEE_MIN_LAT_ACCEL = -0.0000525;
  static final double MAPOGEE_MAX_TOPO_LAT_SPEED = 0.0156;
  static final double MAPOGEE_MIN_TOPO_LAT_SPEED = -0.0156;
  static final double MAPOGEE_MAX_TOPO_LAT_ACCEL = 0.000052;
  static final double MAPOGEE_MIN_TOPO_LAT_ACCEL = -0.000052;
  // No heliocentric positions for the mean apogee...
  static final double MAPOGEE_MAX_HELIO_LAT_SPEED = 1./0.;
  static final double MAPOGEE_MIN_HELIO_LAT_SPEED = 1./0.;
  static final double MAPOGEE_MAX_HELIO_LAT_ACCEL = 1./0.;
  static final double MAPOGEE_MIN_HELIO_LAT_ACCEL = 1./0.;

  static final double MAPOGEE_MAX_DIST_SPEED = 0.0;
  static final double MAPOGEE_MIN_DIST_SPEED = 0.0;
  static final double MAPOGEE_MAX_DIST_ACCEL = 0.0;
  static final double MAPOGEE_MIN_DIST_ACCEL = 0.0;
  static final double MAPOGEE_MAX_TOPO_DIST_SPEED = 0.0;
  static final double MAPOGEE_MIN_TOPO_DIST_SPEED = 0.0;
  static final double MAPOGEE_MAX_TOPO_DIST_ACCEL = 0.0;
  static final double MAPOGEE_MIN_TOPO_DIST_ACCEL = 0.0;
  // No heliocentric positions for the mean apogee...
  static final double MAPOGEE_MAX_HELIO_DIST_SPEED = 1./0.;
  static final double MAPOGEE_MIN_HELIO_DIST_SPEED = 1./0.;
  static final double MAPOGEE_MAX_HELIO_DIST_ACCEL = 1./0.;
  static final double MAPOGEE_MIN_HELIO_DIST_ACCEL = 1./0.;

  // time ./swetest -head -bj-3027215.5 -ejplde431.eph -edir./ephe -pA -s0.7064 -fPJadss -n15511619 > meanapogee_jpl
  static final double MAPOGEE_MAX_RECT_SPEED = 0.1282058;
  static final double MAPOGEE_MIN_RECT_SPEED = 0.0949346;
  static final double MAPOGEE_MAX_RECT_ACCEL = 1./0.;
  static final double MAPOGEE_MIN_RECT_ACCEL = 1./0.;
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pA -s0.7064 -fPJadss -n15511619 -topo0,0,50000 > meanapogee-topo-0-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pA -s0.7064 -fPJadss -n15511619 -topo11,0,0 > meanapogee-topo-11-0-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pA -s0.7064 -fPJadss -n15511619 -topo11,0,50000 > meanapogee-topo-11-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pA -s0.7064 -fPJadss -n15511619 -topo11,0,-6300000 > meanapogee-topo-11-0--6300000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pA -s0.7064 -fPJadss -n15511619 -topo11,89,0 > meanapogee-topo-11-89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pA -s0.7064 -fPJadss -n15511619 -topo11,-89,0 > meanapogee-topo-11--89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pA -s0.7064 -fPJadss -n15511619 -topo11,89,50000 > meanapogee-topo-11-89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pA -s0.7064 -fPJadss -n15511619 -topo11,-89,50000 > meanapogee-topo-11--89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pA -s0.7064 -fPJadss -n15511619 -topo179,0,50000 > meanapogee-topo-179-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pA -s0.7064 -fPJadss -n15511619 -topo-179,0,50000 > meanapogee-topo--179-0-50000 &
  // ... -topo0,0,50000
  // ... -topo11,0,0
  // ... -topo11,0,50000
  // ... -topo11,0,-6300000
  // ... -topo11,89,0
  // ... -topo11,-89,0
  // ... -topo11,89,50000
  // ... -topo11,-89,50000
  // ... -topo179,0,50000
  // ... -topo-179,0,50000
  static final double MAPOGEE_MAX_TOPO_RECT_SPEED = MAPOGEE_MAX_RECT_SPEED;
  static final double MAPOGEE_MIN_TOPO_RECT_SPEED = MAPOGEE_MIN_RECT_SPEED;
  static final double MAPOGEE_MAX_TOPO_RECT_ACCEL = 1./0.;
  static final double MAPOGEE_MIN_TOPO_RECT_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double MAPOGEE_MAX_HELIO_RECT_SPEED = 1./0.;
  static final double MAPOGEE_MIN_HELIO_RECT_SPEED = 1./0.;
  static final double MAPOGEE_MAX_HELIO_RECT_ACCEL = 1./0.;
  static final double MAPOGEE_MIN_HELIO_RECT_ACCEL = 1./0.;

  static final double MAPOGEE_MAX_DECL_SPEED = 0.0589295;
  static final double MAPOGEE_MIN_DECL_SPEED = -0.0589357;
  static final double MAPOGEE_MAX_DECL_ACCEL = 1./0.;
  static final double MAPOGEE_MIN_DECL_ACCEL = 1./0.;
  // ... -topo0,0,50000
  // ... -topo11,0,0
  // ... -topo11,0,50000
  // ... -topo11,0,-6300000
  // ... -topo11,89,0
  // ... -topo11,-89,0
  // ... -topo11,89,50000
  // ... -topo11,-89,50000
  // ... -topo179,0,50000
  // ... -topo-179,0,50000
  static final double MAPOGEE_MAX_TOPO_DECL_SPEED = MAPOGEE_MAX_DECL_SPEED;
  static final double MAPOGEE_MIN_TOPO_DECL_SPEED = MAPOGEE_MIN_DECL_SPEED;
  static final double MAPOGEE_MAX_TOPO_DECL_ACCEL = 1./0.;
  static final double MAPOGEE_MIN_TOPO_DECL_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double MAPOGEE_MAX_HELIO_DECL_SPEED = 1./0.;
  static final double MAPOGEE_MIN_HELIO_DECL_SPEED = 1./0.;
  static final double MAPOGEE_MAX_HELIO_DECL_ACCEL = 1./0.;
  static final double MAPOGEE_MIN_HELIO_DECL_ACCEL = 1./0.;



///////////////////////////////////////////////////////////////
// OSCULATING APOGEE: /////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double OAPOGEE_MAX_LON_SPEED = 6.5; // ???
  static final double OAPOGEE_MIN_LON_SPEED = -3.9; // ???
  static final double OAPOGEE_MAX_LON_ACCEL = 2.12; // ???
  static final double OAPOGEE_MIN_LON_ACCEL = -2.2; // ???
  static final double OAPOGEE_MAX_TOPO_LON_SPEED = 6.48;
  static final double OAPOGEE_MIN_TOPO_LON_SPEED = -7.12;
  static final double OAPOGEE_MAX_TOPO_LON_ACCEL = 5; // ???
  static final double OAPOGEE_MIN_TOPO_LON_ACCEL = -6; // ???
  // No heliocentric positions for the osculating apogee...
  static final double OAPOGEE_MAX_HELIO_LON_SPEED = 1./0.;
  static final double OAPOGEE_MIN_HELIO_LON_SPEED = 1./0.;
  static final double OAPOGEE_MAX_HELIO_LON_ACCEL = 1./0.;
  static final double OAPOGEE_MIN_HELIO_LON_ACCEL = 1./0.;

  static final double OAPOGEE_MAX_LAT_SPEED = 0.595;
  static final double OAPOGEE_MIN_LAT_SPEED = -0.592;
  static final double OAPOGEE_MAX_LAT_ACCEL = 0.198;
  static final double OAPOGEE_MIN_LAT_ACCEL = -0.184;
  static final double OAPOGEE_MAX_TOPO_LAT_SPEED = 0.595;
  static final double OAPOGEE_MIN_TOPO_LAT_SPEED = -0.593;
  static final double OAPOGEE_MAX_TOPO_LAT_ACCEL = 0.22; // ???
  static final double OAPOGEE_MIN_TOPO_LAT_ACCEL = -0.2; // ???
  // No heliocentric positions for the osculating apogee...
  static final double OAPOGEE_MAX_HELIO_LAT_SPEED = 1./0.;
  static final double OAPOGEE_MIN_HELIO_LAT_SPEED = 1./0.;
  static final double OAPOGEE_MAX_HELIO_LAT_ACCEL = 1./0.;
  static final double OAPOGEE_MIN_HELIO_LAT_ACCEL = 1./0.;

  static final double OAPOGEE_MAX_DIST_SPEED = 0.0000336;
  static final double OAPOGEE_MIN_DIST_SPEED = -0.0000388;
  static final double OAPOGEE_MAX_DIST_ACCEL = 0.000320;
  static final double OAPOGEE_MIN_DIST_ACCEL = -0.000332;
  static final double OAPOGEE_MAX_TOPO_DIST_SPEED = 0.0000348;
  static final double OAPOGEE_MIN_TOPO_DIST_SPEED = -0.0000389;
  static final double OAPOGEE_MAX_TOPO_DIST_ACCEL = 0.00033; // ???
  static final double OAPOGEE_MIN_TOPO_DIST_ACCEL = -0.00033; // ???
  // No heliocentric positions for the osculating apogee...
  static final double OAPOGEE_MAX_HELIO_DIST_SPEED = 1./0.;
  static final double OAPOGEE_MIN_HELIO_DIST_SPEED = 1./0.;
  static final double OAPOGEE_MAX_HELIO_DIST_ACCEL = 1./0.;
  static final double OAPOGEE_MIN_HELIO_DIST_ACCEL = 1./0.;

  // time ./swetest -head -bj-3027215.5 -ejplde431.eph -edir./ephe -pB -s0.7064 -fPJadss -n15511619 > oscapogee_jpl
  static final double OAPOGEE_MAX_RECT_SPEED = 7.3217998;
  static final double OAPOGEE_MIN_RECT_SPEED = -4.1553280;
  static final double OAPOGEE_MAX_RECT_ACCEL = 1./0.;
  static final double OAPOGEE_MIN_RECT_ACCEL = 1./0.;
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pB -s0.7064 -fPJadss -n15511619 -topo0,0,50000 > oscapogee-topo-0-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pB -s0.7064 -fPJadss -n15511619 -topo11,0,0 > oscapogee-topo-11-0-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pB -s0.7064 -fPJadss -n15511619 -topo11,0,50000 > oscapogee-topo-11-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pB -s0.7064 -fPJadss -n15511619 -topo11,0,-6300000 > oscapogee-topo-11-0--6300000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pB -s0.7064 -fPJadss -n15511619 -topo11,89,0 > oscapogee-topo-11-89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pB -s0.7064 -fPJadss -n15511619 -topo11,-89,0 > oscapogee-topo-11--89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pB -s0.7064 -fPJadss -n15511619 -topo11,89,50000 > oscapogee-topo-11-89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pB -s0.7064 -fPJadss -n15511619 -topo11,-89,50000 > oscapogee-topo-11--89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pB -s0.7064 -fPJadss -n15511619 -topo179,0,50000 > oscapogee-topo-179-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pB -s0.7064 -fPJadss -n15511619 -topo-179,0,50000 > oscapogee-topo--179-0-50000 &
  // ... -topo0,0,50000
  // ... -topo11,0,0
  // ... -topo11,0,50000
  // ... -topo11,0,-6300000
  // ... -topo11,89,0
  // ... -topo11,-89,0
  // ... -topo11,89,50000
  // ... -topo11,-89,50000
  // ... -topo179,0,50000
  // ... -topo-179,0,50000
  static final double OAPOGEE_MAX_TOPO_RECT_SPEED = OAPOGEE_MAX_RECT_SPEED;
  static final double OAPOGEE_MIN_TOPO_RECT_SPEED = OAPOGEE_MIN_RECT_SPEED;
  static final double OAPOGEE_MAX_TOPO_RECT_ACCEL = 1./0.;
  static final double OAPOGEE_MIN_TOPO_RECT_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double OAPOGEE_MAX_HELIO_RECT_SPEED = 1./0.;
  static final double OAPOGEE_MIN_HELIO_RECT_SPEED = 1./0.;
  static final double OAPOGEE_MAX_HELIO_RECT_ACCEL = 1./0.;
  static final double OAPOGEE_MIN_HELIO_RECT_ACCEL = 1./0.;

  static final double OAPOGEE_MAX_DECL_SPEED = 3.1477670;
  static final double OAPOGEE_MIN_DECL_SPEED = -3.1524771;
  static final double OAPOGEE_MAX_DECL_ACCEL = 1./0.;
  static final double OAPOGEE_MIN_DECL_ACCEL = 1./0.;
  // ... -topo0,0,50000
  // ... -topo11,0,0
  // ... -topo11,0,50000
  // ... -topo11,0,-6300000
  // ... -topo11,89,0
  // ... -topo11,-89,0
  // ... -topo11,89,50000
  // ... -topo11,-89,50000
  // ... -topo179,0,50000
  // ... -topo-179,0,50000
  static final double OAPOGEE_MAX_TOPO_DECL_SPEED = OAPOGEE_MAX_DECL_SPEED;
  static final double OAPOGEE_MIN_TOPO_DECL_SPEED = OAPOGEE_MIN_DECL_SPEED;
  static final double OAPOGEE_MAX_TOPO_DECL_ACCEL = 1./0.;
  static final double OAPOGEE_MIN_TOPO_DECL_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double OAPOGEE_MAX_HELIO_DECL_SPEED = 1./0.;
  static final double OAPOGEE_MIN_HELIO_DECL_SPEED = 1./0.;
  static final double OAPOGEE_MAX_HELIO_DECL_ACCEL = 1./0.;
  static final double OAPOGEE_MIN_HELIO_DECL_ACCEL = 1./0.;



///////////////////////////////////////////////////////////////
// CHIRON: ////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double CHIRON_MAX_LON_SPEED = 0.1481;
  static final double CHIRON_MIN_LON_SPEED = -0.08136;
  static final double CHIRON_MAX_LON_ACCEL = 0.001985;
  static final double CHIRON_MIN_LON_ACCEL = -0.002076;
  static final double CHIRON_MAX_TOPO_LON_SPEED = 0.1490;
  static final double CHIRON_MIN_TOPO_LON_SPEED = -0.0826;
  static final double CHIRON_MAX_TOPO_LON_ACCEL = 0.00892;
  static final double CHIRON_MIN_TOPO_LON_ACCEL = -0.0091;
  static final double CHIRON_MAX_HELIO_LON_SPEED = 0.048572;
  static final double CHIRON_MIN_HELIO_LON_SPEED = 0.008467;
  static final double CHIRON_MAX_HELIO_LON_ACCEL = 0.000036235;
  static final double CHIRON_MIN_HELIO_LON_ACCEL = -0.000035949;

  static final double CHIRON_MAX_LAT_SPEED = 0.01538;
  static final double CHIRON_MIN_LAT_SPEED = -0.01344;
  static final double CHIRON_MAX_LAT_ACCEL = 0.000313;
  static final double CHIRON_MIN_LAT_ACCEL = -0.0002607;
  static final double CHIRON_MAX_TOPO_LAT_SPEED = 0.01574;
  static final double CHIRON_MIN_TOPO_LAT_SPEED =  -0.01368;
  static final double CHIRON_MAX_TOPO_LAT_ACCEL = 0.0033643;
  static final double CHIRON_MIN_TOPO_LAT_ACCEL = -0.003132;
  static final double CHIRON_MAX_HELIO_LAT_SPEED = 0.0066239;
  static final double CHIRON_MIN_HELIO_LAT_SPEED = -0.0018657;
  static final double CHIRON_MAX_HELIO_LAT_ACCEL = 0.000011620;
  static final double CHIRON_MIN_HELIO_LAT_ACCEL = -0.000017098;

  static final double CHIRON_MAX_DIST_SPEED = 0.01867;
  static final double CHIRON_MIN_DIST_SPEED = -0.018683;
  static final double CHIRON_MAX_DIST_ACCEL = 0.0003195;
  static final double CHIRON_MIN_DIST_ACCEL = -0.0002838;
  static final double CHIRON_MAX_TOPO_DIST_SPEED = 0.01883;
  static final double CHIRON_MIN_TOPO_DIST_SPEED = -0.01884;
  static final double CHIRON_MAX_TOPO_DIST_ACCEL = 0.001324;
  static final double CHIRON_MIN_TOPO_DIST_ACCEL = -0.001288;
  static final double CHIRON_MAX_HELIO_DIST_SPEED = 0.00208240;
  static final double CHIRON_MIN_HELIO_DIST_SPEED = -0.0020787;
  static final double CHIRON_MAX_HELIO_DIST_ACCEL = 0.0000023777;
  static final double CHIRON_MIN_HELIO_DIST_ACCEL = -0.0000012240;

  // time ./swetest -head -bj-3027215.5 -ejplde431.eph -edir./ephe -pD -s0.7064 -fPJadss -n15511619 > chiron_jpl
  static final double CHIRON_MAX_RECT_SPEED = 0.1481655;
  static final double CHIRON_MIN_RECT_SPEED = -0.0786760;
  static final double CHIRON_MAX_RECT_ACCEL = 1./0.;
  static final double CHIRON_MIN_RECT_ACCEL = 1./0.;
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pD -s0.7064 -fPJadss -n15511619 -topo0,0,50000 > chiron-topo-0-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pD -s0.7064 -fPJadss -n15511619 -topo11,0,0 > chiron-topo-11-0-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pD -s0.7064 -fPJadss -n15511619 -topo11,0,50000 > chiron-topo-11-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pD -s0.7064 -fPJadss -n15511619 -topo11,0,-6300000 > chiron-topo-11-0--6300000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pD -s0.7064 -fPJadss -n15511619 -topo11,89,0 > chiron-topo-11-89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pD -s0.7064 -fPJadss -n15511619 -topo11,-89,0 > chiron-topo-11--89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pD -s0.7064 -fPJadss -n15511619 -topo11,89,50000 > chiron-topo-11-89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pD -s0.7064 -fPJadss -n15511619 -topo11,-89,50000 > chiron-topo-11--89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pD -s0.7064 -fPJadss -n15511619 -topo179,0,50000 > chiron-topo-179-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pD -s0.7064 -fPJadss -n15511619 -topo-179,0,50000 > chiron-topo--179-0-50000 &
  // -0.1149048 .. 0.1658259 at -topo0,0,50000		<-- MIN	<-- MAX
  // -0.1148605 .. 0.1657069 at -topo11,0,0
  // -0.1148696 .. 0.1657190 at -topo11,0,50000
  // -0.1137132 .. 0.1648709 at -topo11,0,-6300000
  // -0.1137193 .. 0.1648724 at -topo11,89,0
  // -0.1137194 .. 0.1648724 at -topo11,-89,0
  // -0.1137194 .. 0.1648724 at -topo11,89,50000
  // -0.1137195 .. 0.1648724 at -topo11,-89,50000
  // -0.1137290 .. 0.1653790 at -topo179,0,50000
  // -0.1137718 .. 0.1653926 at -topo-179,0,50000
  static final double CHIRON_MAX_TOPO_RECT_SPEED = 0.1658259;
  static final double CHIRON_MIN_TOPO_RECT_SPEED = -0.1149048;
  static final double CHIRON_MAX_TOPO_RECT_ACCEL = 1./0.;
  static final double CHIRON_MIN_TOPO_RECT_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double CHIRON_MAX_HELIO_RECT_SPEED = 1./0.;
  static final double CHIRON_MIN_HELIO_RECT_SPEED = 1./0.;
  static final double CHIRON_MAX_HELIO_RECT_ACCEL = 1./0.;
  static final double CHIRON_MIN_HELIO_RECT_ACCEL = 1./0.;

  static final double CHIRON_MAX_DECL_SPEED = 0.0383058;
  static final double CHIRON_MIN_DECL_SPEED = -0.0553686;
  static final double CHIRON_MAX_DECL_ACCEL = 1./0.;
  static final double CHIRON_MIN_DECL_ACCEL = 1./0.;
  // -0.0644868 .. 0.0446940 at -topo0,0,50000
  // -0.0644831 .. 0.0447047 at -topo11,0,0
  // -0.0644829 .. 0.0447050 at -topo11,0,50000
  // -0.0645042 .. 0.0447169 at -topo11,0,-6300000
  // -0.0645042 .. 0.0447167 at -topo11,89,0
  // -0.0645041 .. 0.0447168 at -topo11,-89,0
  // -0.0645042 .. 0.0447167 at -topo11,89,50000
  // -0.0645041 .. 0.0447168 at -topo11,-89,50000
  // -0.0645219 .. 0.0447458 at -topo179,0,50000
  // -0.0645226 .. 0.0447469 at -topo-179,0,50000	<-- MIN	<-- MAX
  static final double CHIRON_MAX_TOPO_DECL_SPEED = 0.0447469;
  static final double CHIRON_MIN_TOPO_DECL_SPEED = -0.0645226;
  static final double CHIRON_MAX_TOPO_DECL_ACCEL = 1./0.;
  static final double CHIRON_MIN_TOPO_DECL_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double CHIRON_MAX_HELIO_DECL_SPEED = 1./0.;
  static final double CHIRON_MIN_HELIO_DECL_SPEED = 1./0.;
  static final double CHIRON_MAX_HELIO_DECL_ACCEL = 1./0.;
  static final double CHIRON_MIN_HELIO_DECL_ACCEL = 1./0.;



///////////////////////////////////////////////////////////////
// PHOLUS: ////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double PHOLUS_MAX_LON_SPEED = 0.144;
  static final double PHOLUS_MIN_LON_SPEED = -0.083;
  static final double PHOLUS_MAX_LON_ACCEL = 0.00202;
  static final double PHOLUS_MIN_LON_ACCEL = -0.00203;
  static final double PHOLUS_MAX_TOPO_LON_SPEED = 0.146;
  static final double PHOLUS_MIN_TOPO_LON_SPEED = -0.084;
  static final double PHOLUS_MAX_TOPO_LON_ACCEL = 0.009147;
  static final double PHOLUS_MIN_TOPO_LON_ACCEL = -0.0092;
  static final double PHOLUS_MAX_HELIO_LON_SPEED = 0.0456612;
  static final double PHOLUS_MIN_HELIO_LON_SPEED = 0.0031847;
  static final double PHOLUS_MAX_HELIO_LON_ACCEL = 0.000036611;
  static final double PHOLUS_MIN_HELIO_LON_ACCEL = -0.000035427;

  static final double PHOLUS_MAX_LAT_SPEED = 0.0475;
  static final double PHOLUS_MIN_LAT_SPEED = -0.0359;
  static final double PHOLUS_MAX_LAT_ACCEL = 0.0008841;
  static final double PHOLUS_MIN_LAT_ACCEL = -0.000759;
  static final double PHOLUS_MAX_TOPO_LAT_SPEED = 0.0482;
  static final double PHOLUS_MIN_TOPO_LAT_SPEED = -0.03636;
  static final double PHOLUS_MAX_TOPO_LAT_ACCEL = 0.00490;
  static final double PHOLUS_MIN_TOPO_LAT_ACCEL = -0.00485;
  static final double PHOLUS_MAX_HELIO_LAT_SPEED = 0.020809;
  static final double PHOLUS_MIN_HELIO_LAT_SPEED = -0.0036998;
  static final double PHOLUS_MAX_HELIO_LAT_ACCEL = 0.000022898;
  static final double PHOLUS_MIN_HELIO_LAT_ACCEL = -0.000020868;

  static final double PHOLUS_MAX_DIST_SPEED = 0.01806;
  static final double PHOLUS_MIN_DIST_SPEED = -0.01822;
  static final double PHOLUS_MAX_DIST_ACCEL = 0.0003200;
  static final double PHOLUS_MIN_DIST_ACCEL = -0.000297;
  static final double PHOLUS_MAX_TOPO_DIST_SPEED = 0.0183;
  static final double PHOLUS_MIN_TOPO_DIST_SPEED = -0.0185;
  static final double PHOLUS_MAX_TOPO_DIST_ACCEL = 0.00133;
  static final double PHOLUS_MIN_TOPO_DIST_ACCEL = -0.00131;
  static final double PHOLUS_MAX_HELIO_DIST_SPEED = 0.0026983;
  static final double PHOLUS_MIN_HELIO_DIST_SPEED = -0.0026987;
  static final double PHOLUS_MAX_HELIO_DIST_ACCEL = 0.0000030692;
  static final double PHOLUS_MIN_HELIO_DIST_ACCEL = -0.0000013359;

  // time ./swetest -head -bj-3027215.5 -ejplde431.eph -edir./ephe -pE -s0.7064 -fPJadss -n15511619 > pholus_jpl
  static final double PHOLUS_MAX_RECT_SPEED = 0.1583175;
  static final double PHOLUS_MIN_RECT_SPEED = -0.0960042;
  static final double PHOLUS_MAX_RECT_ACCEL = 1./0.;
  static final double PHOLUS_MIN_RECT_ACCEL = 1./0.;
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pE -s0.7064 -fPJadss -n15511619 -topo0,0,50000 > pholus-topo-0-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pE -s0.7064 -fPJadss -n15511619 -topo11,0,0 > pholus-topo-11-0-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pE -s0.7064 -fPJadss -n15511619 -topo11,0,50000 > pholus-topo-11-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pE -s0.7064 -fPJadss -n15511619 -topo11,0,-6300000 > pholus-topo-11-0--6300000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pE -s0.7064 -fPJadss -n15511619 -topo11,89,0 > pholus-topo-11-89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pE -s0.7064 -fPJadss -n15511619 -topo11,-89,0 > pholus-topo-11--89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pE -s0.7064 -fPJadss -n15511619 -topo11,89,50000 > pholus-topo-11-89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pE -s0.7064 -fPJadss -n15511619 -topo11,-89,50000 > pholus-topo-11--89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pE -s0.7064 -fPJadss -n15511619 -topo179,0,50000 > pholus-topo-179-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pE -s0.7064 -fPJadss -n15511619 -topo-179,0,50000 > pholus-topo--179-0-50000 &
  // -0.1558616 .. 0.1726363 at -topo0,0,50000
  // -0.1557640 .. 0.1727827 at -topo11,0,0
  // -0.1557616 .. 0.1727958 at -topo11,0,50000		<-- MAX
  // -0.1560708 .. 0.1714469 at -topo11,0,-6300000
  // -0.1560692 .. 0.1714450 at -topo11,89,0
  // -0.1560691 .. 0.1714451 at -topo11,-89,0
  // -0.1560692 .. 0.1714450 at -topo11,89,50000
  // -0.1560690 .. 0.1714451 at -topo11,-89,50000
  // -0.1562780 .. 0.1726044 at -topo179,0,50000
  // -0.1562970 .. 0.1726383 at -topo-179,0,50000	<-- MIN
  static final double PHOLUS_MAX_TOPO_RECT_SPEED = 0.1727958;
  static final double PHOLUS_MIN_TOPO_RECT_SPEED = -0.1562970;
  static final double PHOLUS_MAX_TOPO_RECT_ACCEL = 1./0.;
  static final double PHOLUS_MIN_TOPO_RECT_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double PHOLUS_MAX_HELIO_RECT_SPEED = 1./0.;
  static final double PHOLUS_MIN_HELIO_RECT_SPEED = 1./0.;
  static final double PHOLUS_MAX_HELIO_RECT_ACCEL = 1./0.;
  static final double PHOLUS_MIN_HELIO_RECT_ACCEL = 1./0.;

  static final double PHOLUS_MAX_DECL_SPEED = 0.0747238;
  static final double PHOLUS_MIN_DECL_SPEED = -0.0494900;
  static final double PHOLUS_MAX_DECL_ACCEL = 1./0.;
  static final double PHOLUS_MIN_DECL_ACCEL = 1./0.;
  // -0.0582157 .. 0.0833630 at -topo0,0,50000		<-- MAX
  // -0.0582233 .. 0.0833588 at -topo11,0,0
  // -0.0582261 .. 0.0833615 at -topo11,0,50000		<-- MIN
  // -0.0578962 .. 0.0830224 at -topo11,0,-6300000
  // -0.0578955 .. 0.0830243 at -topo11,89,0
  // -0.0578958 .. 0.0830241 at -topo11,-89,0
  // -0.0578955 .. 0.0830243 at -topo11,89,50000
  // -0.0578957 .. 0.0830241 at -topo11,-89,50000
  // -0.0580738 .. 0.0828280 at -topo179,0,50000
  // -0.0580625 .. 0.0828404 at -topo-179,0,50000
  static final double PHOLUS_MAX_TOPO_DECL_SPEED = 0.0833630;
  static final double PHOLUS_MIN_TOPO_DECL_SPEED = -0.0582261;
  static final double PHOLUS_MAX_TOPO_DECL_ACCEL = 1./0.;
  static final double PHOLUS_MIN_TOPO_DECL_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double PHOLUS_MAX_HELIO_DECL_SPEED = 1./0.;
  static final double PHOLUS_MIN_HELIO_DECL_SPEED = 1./0.;
  static final double PHOLUS_MAX_HELIO_DECL_ACCEL = 1./0.;
  static final double PHOLUS_MIN_HELIO_DECL_ACCEL = 1./0.;



///////////////////////////////////////////////////////////////
// CERES: /////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double CERES_MAX_LON_SPEED = 0.47778;
  static final double CERES_MIN_LON_SPEED = -0.2397;
  static final double CERES_MAX_LON_ACCEL = 0.00784;
  static final double CERES_MIN_LON_ACCEL = -0.007809;
  static final double CERES_MAX_TOPO_LON_SPEED = 0.482;
  static final double CERES_MIN_TOPO_LON_SPEED = -0.2462;
  static final double CERES_MAX_TOPO_LON_ACCEL = 0.04415;
  static final double CERES_MIN_TOPO_LON_ACCEL = -0.0434;
  static final double CERES_MAX_HELIO_LON_SPEED = 0.27746;
  static final double CERES_MIN_HELIO_LON_SPEED = 0.1684273;
  static final double CERES_MAX_HELIO_LON_ACCEL = 0.00024858;
  static final double CERES_MIN_HELIO_LON_ACCEL = -0.00026465;

  static final double CERES_MAX_LAT_SPEED = 0.111909;
  static final double CERES_MIN_LAT_SPEED = -0.10943;
  static final double CERES_MAX_LAT_ACCEL = 0.003567;
  static final double CERES_MIN_LAT_ACCEL = -0.003284;
  static final double CERES_MAX_TOPO_LAT_SPEED = 0.1141;
  static final double CERES_MIN_TOPO_LAT_SPEED = -0.11225;
  static final double CERES_MAX_TOPO_LAT_ACCEL = 0.0254;
  static final double CERES_MIN_TOPO_LAT_ACCEL = -0.0241;
  static final double CERES_MAX_HELIO_LAT_SPEED = 0.049023;
  static final double CERES_MIN_HELIO_LAT_SPEED = -0.047271;
  static final double CERES_MAX_HELIO_LAT_ACCEL = 0.000239747;
  static final double CERES_MIN_HELIO_LAT_ACCEL = -0.00020807;

  static final double CERES_MAX_DIST_SPEED = 0.014525;
  static final double CERES_MIN_DIST_SPEED = -0.014456;
  static final double CERES_MAX_DIST_ACCEL = 0.0003006;
  static final double CERES_MIN_DIST_ACCEL = -0.0001636;
  static final double CERES_MAX_TOPO_DIST_SPEED = 0.01468;
  static final double CERES_MIN_TOPO_DIST_SPEED = -0.01462;
  static final double CERES_MAX_TOPO_DIST_ACCEL = 0.001298;
  static final double CERES_MIN_TOPO_DIST_ACCEL = -0.001166;
  static final double CERES_MAX_HELIO_DIST_SPEED = 0.0012446382;
  static final double CERES_MIN_HELIO_DIST_SPEED = -0.001241531;
  static final double CERES_MAX_HELIO_DIST_ACCEL = 0.0000061;
  static final double CERES_MIN_HELIO_DIST_ACCEL = -0.00000387;

  // time ./swetest -head -bj-3027215.5 -ejplde431.eph -edir./ephe -pF -s0.7064 -fPJadss -n15511619 > ceres_jpl
  static final double CERES_MAX_RECT_SPEED = 0.5224900;
  static final double CERES_MIN_RECT_SPEED = -0.2653369;
  static final double CERES_MAX_RECT_ACCEL = 1./0.;
  static final double CERES_MIN_RECT_ACCEL = 1./0.;
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pF -s0.7064 -fPJadss -n15511619 -topo0,0,50000 > ceres-topo-0-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pF -s0.7064 -fPJadss -n15511619 -topo11,0,0 > ceres-topo-11-0-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pF -s0.7064 -fPJadss -n15511619 -topo11,0,50000 > ceres-topo-11-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pF -s0.7064 -fPJadss -n15511619 -topo11,0,-6300000 > ceres-topo-11-0--6300000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pF -s0.7064 -fPJadss -n15511619 -topo11,89,0 > ceres-topo-11-89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pF -s0.7064 -fPJadss -n15511619 -topo11,-89,0 > ceres-topo-11--89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pF -s0.7064 -fPJadss -n15511619 -topo11,89,50000 > ceres-topo-11-89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pF -s0.7064 -fPJadss -n15511619 -topo11,-89,50000 > ceres-topo-11--89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pF -s0.7064 -fPJadss -n15511619 -topo179,0,50000 > ceres-topo-179-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pF -s0.7064 -fPJadss -n15511619 -topo-179,0,50000 > ceres-topo--179-0-50000 &
  // -0.2804198 .. 0.5306490 at -topo0,0,50000		<-- MAX
  // -0.2808381 .. 0.5304787 at -topo11,0,0
  // -0.2809222 .. 0.5305165 at -topo11,0,50000		<-- MIN
  // -0.2705653 .. 0.5259866 at -topo11,0,-6300000
  // -0.2706153 .. 0.5259741 at -topo11,89,0
  // -0.2706155 .. 0.5259740 at -topo11,-89,0
  // -0.2706166 .. 0.5259737 at -topo11,89,50000
  // -0.2706168 .. 0.5259737 at -topo11,-89,50000
  // -0.2808423 .. 0.5305055 at -topo179,0,50000
  // -0.2807956 .. 0.5304711 at -topo-179,0,50000
  static final double CERES_MAX_TOPO_RECT_SPEED = 0.5306490;
  static final double CERES_MIN_TOPO_RECT_SPEED = -0.2809222;
  static final double CERES_MAX_TOPO_RECT_ACCEL = 1./0.;
  static final double CERES_MIN_TOPO_RECT_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double CERES_MAX_HELIO_RECT_SPEED = 1./0.;
  static final double CERES_MIN_HELIO_RECT_SPEED = 1./0.;
  static final double CERES_MAX_HELIO_RECT_ACCEL = 1./0.;
  static final double CERES_MIN_HELIO_RECT_ACCEL = 1./0.;

  static final double CERES_MAX_DECL_SPEED = 0.2113396;
  static final double CERES_MIN_DECL_SPEED = -0.1986084;
  static final double CERES_MAX_DECL_ACCEL = 1./0.;
  static final double CERES_MIN_DECL_ACCEL = 1./0.;
  // -0.2014059 .. 0.2137539 at -topo0,0,50000		<-- MIN
  // -0.2013611 .. 0.2137622 at -topo11,0,0
  // -0.2013636 .. 0.2137663 at -topo11,0,50000
  // -0.2010531 .. 0.2135844 at -topo11,0,-6300000
  // -0.2010537 .. 0.2135871 at -topo11,89,0
  // -0.2010558 .. 0.2135828 at -topo11,-89,0
  // -0.2010537 .. 0.2135871 at -topo11,89,50000
  // -0.2010559 .. 0.2135828 at -topo11,-89,50000
  // -0.2012663 .. 0.2139429 at -topo179,0,50000
  // -0.2012691 .. 0.2139455 at -topo-179,0,50000	<-- MAX
  static final double CERES_MAX_TOPO_DECL_SPEED = 0.2139455;
  static final double CERES_MIN_TOPO_DECL_SPEED = -0.2014059;
  static final double CERES_MAX_TOPO_DECL_ACCEL = 1./0.;
  static final double CERES_MIN_TOPO_DECL_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double CERES_MAX_HELIO_DECL_SPEED = 1./0.;
  static final double CERES_MIN_HELIO_DECL_SPEED = 1./0.;
  static final double CERES_MAX_HELIO_DECL_ACCEL = 1./0.;
  static final double CERES_MIN_HELIO_DECL_ACCEL = 1./0.;



///////////////////////////////////////////////////////////////
// PALLAS: ////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double PALLAS_MAX_LON_SPEED = 0.7687;
  static final double PALLAS_MIN_LON_SPEED = -0.3482;
  static final double PALLAS_MAX_LON_ACCEL = 0.01561;
  static final double PALLAS_MIN_LON_ACCEL = -0.01499;
  static final double PALLAS_MAX_TOPO_LON_SPEED = 0.7730;
  static final double PALLAS_MIN_TOPO_LON_SPEED = -0.3570;
  static final double PALLAS_MAX_TOPO_LON_ACCEL = 0.12775;
  static final double PALLAS_MIN_TOPO_LON_ACCEL = -0.1280;
  static final double PALLAS_MAX_HELIO_LON_SPEED = 0.626831;
  static final double PALLAS_MIN_HELIO_LON_SPEED = 0.11145519;
  static final double PALLAS_MAX_HELIO_LON_ACCEL = 0.0026825;
  static final double PALLAS_MIN_HELIO_LON_ACCEL = -0.0026481;

  static final double PALLAS_MAX_LAT_SPEED = 0.54900;
  static final double PALLAS_MIN_LAT_SPEED = -0.5261;
  static final double PALLAS_MAX_LAT_ACCEL = 0.013324;
  static final double PALLAS_MIN_LAT_ACCEL = -0.008125;
  static final double PALLAS_MAX_TOPO_LAT_SPEED = 0.5518;
  static final double PALLAS_MIN_TOPO_LAT_SPEED = -0.5288;
  static final double PALLAS_MAX_TOPO_LAT_ACCEL = 0.0506;
  static final double PALLAS_MIN_TOPO_LAT_ACCEL = -0.02938;
  static final double PALLAS_MAX_HELIO_LAT_SPEED = 0.2002976;
  static final double PALLAS_MIN_HELIO_LAT_SPEED = -0.18542288;
  static final double PALLAS_MAX_HELIO_LAT_ACCEL = 0.002821069;
  static final double PALLAS_MIN_HELIO_LAT_ACCEL = -0.00077783;

  static final double PALLAS_MAX_DIST_SPEED = 0.01780;
  static final double PALLAS_MIN_DIST_SPEED = -0.01778;
  static final double PALLAS_MAX_DIST_ACCEL = 0.0003338;
  static final double PALLAS_MIN_DIST_ACCEL = -0.000185;
  static final double PALLAS_MAX_TOPO_DIST_SPEED = 0.0180;
  static final double PALLAS_MIN_TOPO_DIST_SPEED = -0.01799;
  static final double PALLAS_MAX_TOPO_DIST_ACCEL = 0.001342;
  static final double PALLAS_MIN_TOPO_DIST_ACCEL = -0.001189;
  static final double PALLAS_MAX_HELIO_DIST_SPEED = 0.004602944;
  static final double PALLAS_MIN_HELIO_DIST_SPEED = -0.004618;
  static final double PALLAS_MAX_HELIO_DIST_ACCEL = 0.000044661;
  static final double PALLAS_MIN_HELIO_DIST_ACCEL = -0.0000083;

  // time ./swetest -head -bj-3027215.5 -ejplde431.eph -edir./ephe -pG -s0.7064 -fPJadss -n15511619 > pallas_jpl
  static final double PALLAS_MAX_RECT_SPEED = 0.7352639;
  static final double PALLAS_MIN_RECT_SPEED = -0.2297855;
  static final double PALLAS_MAX_RECT_ACCEL = 1./0.;
  static final double PALLAS_MIN_RECT_ACCEL = 1./0.;
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pG -s0.7064 -fPJadss -n15511619 -topo0,0,50000 > pallas-topo-0-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pG -s0.7064 -fPJadss -n15511619 -topo11,0,0 > pallas-topo-11-0-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pG -s0.7064 -fPJadss -n15511619 -topo11,0,50000 > pallas-topo-11-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pG -s0.7064 -fPJadss -n15511619 -topo11,0,-6300000 > pallas-topo-11-0--6300000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pG -s0.7064 -fPJadss -n15511619 -topo11,89,0 > pallas-topo-11-89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pG -s0.7064 -fPJadss -n15511619 -topo11,-89,0 > pallas-topo-11--89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pG -s0.7064 -fPJadss -n15511619 -topo11,89,50000 > pallas-topo-11-89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pG -s0.7064 -fPJadss -n15511619 -topo11,-89,50000 > pallas-topo-11--89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pG -s0.7064 -fPJadss -n15511619 -topo179,0,50000 > pallas-topo-179-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pG -s0.7064 -fPJadss -n15511619 -topo-179,0,50000 > pallas-topo--179-0-50000 &
  // -0.2430649 .. 0.7424363 at -topo0,0,50000
  // -0.2429989 .. 0.7427120 at -topo11,0,0
  // -0.2430749 .. 0.7427575 at -topo11,0,50000		<-- MIN	<-- MAX
  // -0.2340119 .. 0.7373165 at -topo11,0,-6300000
  // -0.2340322 .. 0.7373405 at -topo11,89,0
  // -0.2340321 .. 0.7373405 at -topo11,-89,0
  // -0.2340328 .. 0.7373411 at -topo11,89,50000
  // -0.2340327 .. 0.7373411 at -topo11,-89,50000
  // -0.2429059 .. 0.7425019 at -topo179,0,50000
  // -0.2429079 .. 0.7425897 at -topo-179,0,50000
  static final double PALLAS_MAX_TOPO_RECT_SPEED = 0.7427575;
  static final double PALLAS_MIN_TOPO_RECT_SPEED = -0.2430749;
  static final double PALLAS_MAX_TOPO_RECT_ACCEL = 1./0.;
  static final double PALLAS_MIN_TOPO_RECT_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double PALLAS_MAX_HELIO_RECT_SPEED = 1./0.;
  static final double PALLAS_MIN_HELIO_RECT_SPEED = 1./0.;
  static final double PALLAS_MAX_HELIO_RECT_ACCEL = 1./0.;
  static final double PALLAS_MIN_HELIO_RECT_ACCEL = 1./0.;

  static final double PALLAS_MAX_DECL_SPEED = 0.5399119;
  static final double PALLAS_MIN_DECL_SPEED = -0.5165119;
  static final double PALLAS_MAX_DECL_ACCEL = 1./0.;
  static final double PALLAS_MIN_DECL_ACCEL = 1./0.;
  // -0.5206622 .. 0.5415006 at -topo0,0,50000		<-- MIN
  // -0.5204892 .. 0.5415245 at -topo11,0,0
  // -0.5205174 .. 0.5415482 at -topo11,0,50000
  // -0.5171256 .. 0.5387894 at -topo11,0,-6300000
  // -0.5171234 .. 0.5387973 at -topo11,89,0
  // -0.5171074 .. 0.5387840 at -topo11,-89,0
  // -0.5171232 .. 0.5387977 at -topo11,89,50000
  // -0.5171071 .. 0.5387843 at -topo11,-89,50000
  // -0.5200186 .. 0.5419217 at -topo179,0,50000	<-- MAX
  // -0.5199453 .. 0.5418829 at -topo-179,0,50000
  static final double PALLAS_MAX_TOPO_DECL_SPEED = 0.5419217;
  static final double PALLAS_MIN_TOPO_DECL_SPEED = -0.5206622;
  static final double PALLAS_MAX_TOPO_DECL_ACCEL = 1./0.;
  static final double PALLAS_MIN_TOPO_DECL_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double PALLAS_MAX_HELIO_DECL_SPEED = 1./0.;
  static final double PALLAS_MIN_HELIO_DECL_SPEED = 1./0.;
  static final double PALLAS_MAX_HELIO_DECL_ACCEL = 1./0.;
  static final double PALLAS_MIN_HELIO_DECL_ACCEL = 1./0.;



///////////////////////////////////////////////////////////////
// JUNO: //////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double JUNO_MAX_LON_SPEED = 0.61473;
  static final double JUNO_MIN_LON_SPEED = -0.46666;
  static final double JUNO_MAX_LON_ACCEL = 0.00968;
  static final double JUNO_MIN_LON_ACCEL = -0.009779;
  static final double JUNO_MAX_TOPO_LON_SPEED = 0.6178;
  static final double JUNO_MIN_TOPO_LON_SPEED = -0.2702;
  static final double JUNO_MAX_TOPO_LON_ACCEL = 0.0682;
  static final double JUNO_MIN_TOPO_LON_ACCEL = -0.06812;
  static final double JUNO_MAX_HELIO_LON_SPEED = 0.42012063;
  static final double JUNO_MIN_HELIO_LON_SPEED = 0.1363890;
  static final double JUNO_MAX_HELIO_LON_ACCEL = 0.00087833;
  static final double JUNO_MIN_HELIO_LON_ACCEL = -0.00086738;

  static final double JUNO_MAX_LAT_SPEED = 0.2118;
  static final double JUNO_MIN_LAT_SPEED = -0.2069;
  static final double JUNO_MAX_LAT_ACCEL = 0.006619;
  static final double JUNO_MIN_LAT_ACCEL = -0.006030;
  static final double JUNO_MAX_TOPO_LAT_SPEED = 0.213;
  static final double JUNO_MIN_TOPO_LAT_SPEED = -0.2089;
  static final double JUNO_MAX_TOPO_LAT_ACCEL = 0.02594;
  static final double JUNO_MIN_TOPO_LAT_ACCEL = -0.02464;
  static final double JUNO_MAX_HELIO_LAT_SPEED = 0.09100508;
  static final double JUNO_MIN_HELIO_LAT_SPEED = -0.09178865;
  static final double JUNO_MAX_HELIO_LAT_ACCEL = 0.000645883;
  static final double JUNO_MIN_HELIO_LAT_ACCEL = -0.000592596;

  static final double JUNO_MAX_DIST_SPEED = 0.01631;
  static final double JUNO_MIN_DIST_SPEED = -0.01649;
  static final double JUNO_MAX_DIST_ACCEL = 0.0003149;
  static final double JUNO_MIN_DIST_ACCEL = -0.00018013;
  static final double JUNO_MAX_TOPO_DIST_SPEED = 0.016457;
  static final double JUNO_MIN_TOPO_DIST_SPEED = -0.01666;
  static final double JUNO_MAX_TOPO_DIST_ACCEL = 0.001315;
  static final double JUNO_MIN_TOPO_DIST_ACCEL = -0.001185;
  static final double JUNO_MAX_HELIO_DIST_SPEED = 0.0029944744;
  static final double JUNO_MIN_HELIO_DIST_SPEED = -0.002995286;
  static final double JUNO_MAX_HELIO_DIST_ACCEL = 0.00002158148;
  static final double JUNO_MIN_HELIO_DIST_ACCEL = -0.0000125;

  // time ./swetest -head -bj-3027215.5 -ejplde431.eph -edir./ephe -pH -s0.7064 -fPJadss -n15511619 > juno_jpl
  static final double JUNO_MAX_RECT_SPEED = 0.6371096;
  static final double JUNO_MIN_RECT_SPEED = -0.2427109;
  static final double JUNO_MAX_RECT_ACCEL = 1./0.;
  static final double JUNO_MIN_RECT_ACCEL = 1./0.;
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pH -s0.7064 -fPJadss -n15511619 -topo0,0,50000 > juno-topo-0-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pH -s0.7064 -fPJadss -n15511619 -topo11,0,0 > juno-topo-11-0-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pH -s0.7064 -fPJadss -n15511619 -topo11,0,50000 > juno-topo-11-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pH -s0.7064 -fPJadss -n15511619 -topo11,0,-6300000 > juno-topo-11-0--6300000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pH -s0.7064 -fPJadss -n15511619 -topo11,89,0 > juno-topo-11-89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pH -s0.7064 -fPJadss -n15511619 -topo11,-89,0 > juno-topo-11--89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pH -s0.7064 -fPJadss -n15511619 -topo11,89,50000 > juno-topo-11-89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pH -s0.7064 -fPJadss -n15511619 -topo11,-89,50000 > juno-topo-11--89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pH -s0.7064 -fPJadss -n15511619 -topo179,0,50000 > juno-topo-179-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pH -s0.7064 -fPJadss -n15511619 -topo-179,0,50000 > juno-topo--179-0-50000 &
  // -0.2559846 .. 0.6447384 at -topo0,0,50000		<-- MAX
  // -0.2565276 .. 0.6443679 at -topo11,0,0
  // -0.2566038 .. 0.6444018 at -topo11,0,50000
  // -0.2469324 .. 0.6400916 at -topo11,0,-6300000
  // -0.2469835 .. 0.6401144 at -topo11,89,0
  // -0.2469835 .. 0.6401144 at -topo11,-89,0
  // -0.2469848 .. 0.6401150 at -topo11,89,50000	<-- MIN
  // -0.2469848 .. 0.6401150 at -topo11,-89,50000	<-- MIN
  // -0.2549038 .. 0.6445999 at -topo179,0,50000
  // -0.2548705 .. 0.6446594 at -topo-179,0,50000
  static final double JUNO_MAX_TOPO_RECT_SPEED = 0.6447384;
  static final double JUNO_MIN_TOPO_RECT_SPEED = -0.2469848;
  static final double JUNO_MAX_TOPO_RECT_ACCEL = 1./0.;
  static final double JUNO_MIN_TOPO_RECT_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double JUNO_MAX_HELIO_RECT_SPEED = 1./0.;
  static final double JUNO_MIN_HELIO_RECT_SPEED = 1./0.;
  static final double JUNO_MAX_HELIO_RECT_ACCEL = 1./0.;
  static final double JUNO_MIN_HELIO_RECT_ACCEL = 1./0.;

  static final double JUNO_MAX_DECL_SPEED = 0.2485279;
  static final double JUNO_MIN_DECL_SPEED = -0.2582130;
  static final double JUNO_MAX_DECL_ACCEL = 1./0.;
  static final double JUNO_MIN_DECL_ACCEL = 1./0.;
  // -0.2587948 .. 0.2502102 at -topo0,0,50000		<-- MIN
  // -0.2587834 .. 0.2499672 at -topo11,0,0
  // -0.2587879 .. 0.2499775 at -topo11,0,50000
  // -0.2582143 .. 0.2486781 at -topo11,0,-6300000
  // -0.2582171 .. 0.2486882 at -topo11,89,0
  // -0.2582177 .. 0.2486818 at -topo11,-89,0
  // -0.2582172 .. 0.2486884 at -topo11,89,50000
  // -0.2582177 .. 0.2486819 at -topo11,-89,50000
  // -0.2586975 .. 0.2502499 at -topo179,0,50000	<-- MAX
  // -0.2586861 .. 0.2502441 at -topo-179,0,50000
  static final double JUNO_MAX_TOPO_DECL_SPEED = 0.2502499;
  static final double JUNO_MIN_TOPO_DECL_SPEED = -0.2587948;
  static final double JUNO_MAX_TOPO_DECL_ACCEL = 1./0.;
  static final double JUNO_MIN_TOPO_DECL_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double JUNO_MAX_HELIO_DECL_SPEED = 1./0.;
  static final double JUNO_MIN_HELIO_DECL_SPEED = 1./0.;
  static final double JUNO_MAX_HELIO_DECL_ACCEL = 1./0.;
  static final double JUNO_MIN_HELIO_DECL_ACCEL = 1./0.;



///////////////////////////////////////////////////////////////
// VESTA: /////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double VESTA_MAX_LON_SPEED = 0.5540;
  static final double VESTA_MIN_LON_SPEED = -0.40;
  static final double VESTA_MAX_LON_ACCEL = 0.00876;
  static final double VESTA_MIN_LON_ACCEL = -0.00877;
  static final double VESTA_MAX_TOPO_LON_SPEED = 0.5568;
  static final double VESTA_MIN_TOPO_LON_SPEED = -0.2770;
  static final double VESTA_MAX_TOPO_LON_ACCEL = 0.05581;
  static final double VESTA_MIN_TOPO_LON_ACCEL = -0.05541;
  static final double VESTA_MAX_HELIO_LON_SPEED = 0.3379980;
  static final double VESTA_MIN_HELIO_LON_SPEED = 0.22067514;
  static final double VESTA_MAX_HELIO_LON_ACCEL = 0.00029479;
  static final double VESTA_MIN_HELIO_LON_ACCEL = -0.00033568;

  static final double VESTA_MAX_LAT_SPEED = 0.09498;
  static final double VESTA_MIN_LAT_SPEED = -0.09798;
  static final double VESTA_MAX_LAT_ACCEL = 0.003088;
  static final double VESTA_MIN_LAT_ACCEL = -0.003171;
  static final double VESTA_MAX_TOPO_LAT_SPEED = 0.09843;
  static final double VESTA_MIN_TOPO_LAT_SPEED = -0.1011;
  static final double VESTA_MAX_TOPO_LAT_ACCEL = 0.02671;
  static final double VESTA_MIN_TOPO_LAT_ACCEL = -0.02858;
  static final double VESTA_MAX_HELIO_LAT_SPEED = 0.0387172;
  static final double VESTA_MIN_HELIO_LAT_SPEED = -0.04107737;
  static final double VESTA_MAX_HELIO_LAT_ACCEL = 0.00022967;
  static final double VESTA_MIN_HELIO_LAT_ACCEL = -0.00022729;

  static final double VESTA_MAX_DIST_SPEED = 0.01421;
  static final double VESTA_MIN_DIST_SPEED = -0.01422;
  static final double VESTA_MAX_DIST_ACCEL = 0.000299;
  static final double VESTA_MIN_DIST_ACCEL = -0.0001321;
  static final double VESTA_MAX_TOPO_DIST_SPEED = 0.01438;
  static final double VESTA_MIN_TOPO_DIST_SPEED = -0.01420;
  static final double VESTA_MAX_TOPO_DIST_ACCEL = 0.001300;
  static final double VESTA_MIN_TOPO_DIST_ACCEL = -0.001138;
  static final double VESTA_MAX_HELIO_DIST_SPEED = 0.001193567;
  static final double VESTA_MIN_HELIO_DIST_SPEED = -0.001193051;
  static final double VESTA_MAX_HELIO_DIST_ACCEL = 0.0000070;
  static final double VESTA_MIN_HELIO_DIST_ACCEL = -0.0000048;

  // time ./swetest -head -bj-3027215.5 -ejplde431.eph -edir./ephe -pI -s0.7064 -fPJadss -n15511619 > vesta_jpl
  static final double VESTA_MAX_RECT_SPEED = 0.5889533;
  static final double VESTA_MIN_RECT_SPEED = -0.2930882;
  static final double VESTA_MAX_RECT_ACCEL = 1./0.;
  static final double VESTA_MIN_RECT_ACCEL = 1./0.;
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pI -s0.7064 -fPJadss -n15511619 -topo0,0,50000 > vesta-topo-0-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pI -s0.7064 -fPJadss -n15511619 -topo11,0,0 > vesta-topo-11-0-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pI -s0.7064 -fPJadss -n15511619 -topo11,0,50000 > vesta-topo-11-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pI -s0.7064 -fPJadss -n15511619 -topo11,0,-6300000 > vesta-topo-11-0--6300000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pI -s0.7064 -fPJadss -n15511619 -topo11,89,0 > vesta-topo-11-89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pI -s0.7064 -fPJadss -n15511619 -topo11,-89,0 > vesta-topo-11--89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pI -s0.7064 -fPJadss -n15511619 -topo11,89,50000 > vesta-topo-11-89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pI -s0.7064 -fPJadss -n15511619 -topo11,-89,50000 > vesta-topo-11--89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pI -s0.7064 -fPJadss -n15511619 -topo179,0,50000 > vesta-topo-179-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pI -s0.7064 -fPJadss -n15511619 -topo-179,0,50000 > vesta-topo--179-0-50000 &
  // -0.3086817 .. 0.5962301 at -topo0,0,50000		<-- MIN
  // -0.3079639 .. 0.5965240 at -topo11,0,0
  // -0.3080461 .. 0.5965646 at -topo11,0,50000
  // -0.2976037 .. 0.5919475 at -topo11,0,-6300000
  // -0.2976590 .. 0.5919340 at -topo11,89,0
  // -0.2976588 .. 0.5919340 at -topo11,-89,0
  // -0.2976604 .. 0.5919336 at -topo11,89,50000
  // -0.2976603 .. 0.5919336 at -topo11,-89,50000
  // -0.3083094 .. 0.5966574 at -topo179,0,50000
  // -0.3082262 .. 0.5966647 at -topo-179,0,50000	<-- MAX
  static final double VESTA_MAX_TOPO_RECT_SPEED = 0.5966647;
  static final double VESTA_MIN_TOPO_RECT_SPEED = -0.3086817;
  static final double VESTA_MAX_TOPO_RECT_ACCEL = 1./0.;
  static final double VESTA_MIN_TOPO_RECT_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double VESTA_MAX_HELIO_RECT_SPEED = 1./0.;
  static final double VESTA_MIN_HELIO_RECT_SPEED = 1./0.;
  static final double VESTA_MAX_HELIO_RECT_ACCEL = 1./0.;
  static final double VESTA_MIN_HELIO_RECT_ACCEL = 1./0.;

  static final double VESTA_MAX_DECL_SPEED = 0.2222065;
  static final double VESTA_MIN_DECL_SPEED = -0.2242716;
  static final double VESTA_MAX_DECL_ACCEL = 1./0.;
  static final double VESTA_MIN_DECL_ACCEL = 1./0.;
  // -0.2268079 .. 0.2245570 at -topo0,0,50000
  // -0.2267914 .. 0.2245578 at -topo11,0,0
  // -0.2267929 .. 0.2245575 at -topo11,0,50000
  // -0.2267069 .. 0.2245894 at -topo11,0,-6300000
  // -0.2267042 .. 0.2245903 at -topo11,89,0
  // -0.2267074 .. 0.2245882 at -topo11,-89,0
  // -0.2267042 .. 0.2245903 at -topo11,89,50000
  // -0.2267074 .. 0.2245882 at -topo11,-89,50000
  // -0.2269444 .. 0.2246227 at -topo179,0,50000	<-- MIN	<-- MAX
  // -0.2269413 .. 0.2246227 at -topo-179,0,50000		<-- MAX
  static final double VESTA_MAX_TOPO_DECL_SPEED = 0.2246227;
  static final double VESTA_MIN_TOPO_DECL_SPEED = -0.2269444;
  static final double VESTA_MAX_TOPO_DECL_ACCEL = 1./0.;
  static final double VESTA_MIN_TOPO_DECL_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double VESTA_MAX_HELIO_DECL_SPEED = 1./0.;
  static final double VESTA_MIN_HELIO_DECL_SPEED = 1./0.;
  static final double VESTA_MAX_HELIO_DECL_ACCEL = 1./0.;
  static final double VESTA_MIN_HELIO_DECL_ACCEL = 1./0.;



///////////////////////////////////////////////////////////////
// INTERPOLATED LUNAR APOGEE: /////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double INTPAPOG_MAX_LON_SPEED = 0.240442;
  static final double INTPAPOG_MIN_LON_SPEED = -0.155670;
  static final double INTPAPOG_MAX_LON_ACCEL = 0.0103163;
  static final double INTPAPOG_MIN_LON_ACCEL = -0.0103726;
  static final double INTPAPOG_MAX_TOPO_LON_SPEED = 0.240440;
  static final double INTPAPOG_MIN_TOPO_LON_SPEED = -0.1556659;
  static final double INTPAPOG_MAX_TOPO_LON_ACCEL = 0.0103158;
  static final double INTPAPOG_MIN_TOPO_LON_ACCEL = -0.0103707;
  // No heliocentric positions for the interpolated lunar apogee...
  static final double INTPAPOG_MAX_HELIO_LON_SPEED = 1./0.;
  static final double INTPAPOG_MIN_HELIO_LON_SPEED = 1./0.;
  static final double INTPAPOG_MAX_HELIO_LON_ACCEL = 1./0.;
  static final double INTPAPOG_MIN_HELIO_LON_ACCEL = 1./0.;

  static final double INTPAPOG_MAX_LAT_SPEED = 0.031604;
  static final double INTPAPOG_MIN_LAT_SPEED = -0.031613;
  static final double INTPAPOG_MAX_LAT_ACCEL = 0.00109429;
  static final double INTPAPOG_MIN_LAT_ACCEL = -0.0010990;
  static final double INTPAPOG_MAX_TOPO_LAT_SPEED = 0.03159950;
  static final double INTPAPOG_MIN_TOPO_LAT_SPEED = -0.031610;
  static final double INTPAPOG_MAX_TOPO_LAT_ACCEL = 0.00109428;
  static final double INTPAPOG_MIN_TOPO_LAT_ACCEL = -0.00109888;
  // No heliocentric positions for the interpolated lunar apogee...
  static final double INTPAPOG_MAX_HELIO_LAT_SPEED = 1./0.;
  static final double INTPAPOG_MIN_HELIO_LAT_SPEED = 1./0.;
  static final double INTPAPOG_MAX_HELIO_LAT_ACCEL = 1./0.;
  static final double INTPAPOG_MIN_HELIO_LAT_ACCEL = 1./0.;

  static final double INTPAPOG_MAX_DIST_SPEED = 0.00000026577;
  static final double INTPAPOG_MIN_DIST_SPEED = -0.00000026575;
  static final double INTPAPOG_MAX_DIST_ACCEL = 0.0000000080677;
  static final double INTPAPOG_MIN_DIST_ACCEL = -0.000000006945;
  static final double INTPAPOG_MAX_TOPO_DIST_SPEED = 0.00000026584;
  static final double INTPAPOG_MIN_TOPO_DIST_SPEED = -0.00000026578;
  static final double INTPAPOG_MAX_TOPO_DIST_ACCEL = 0.0000000080677;
  static final double INTPAPOG_MIN_TOPO_DIST_ACCEL = -0.0000000069411;
  // No heliocentric positions for the interpolated lunar apogee...
  static final double INTPAPOG_MAX_HELIO_DIST_SPEED = 1./0.;
  static final double INTPAPOG_MIN_HELIO_DIST_SPEED = 1./0.;
  static final double INTPAPOG_MAX_HELIO_DIST_ACCEL = 1./0.;
  static final double INTPAPOG_MIN_HELIO_DIST_ACCEL = 1./0.;

  // time ./swetest -head -bj-3027215.5 -ejplde431.eph -edir./ephe -pc -s0.7064 -fPJadss -n15511619 > intpapogee_jpl
  static final double INTPAPOG_MAX_RECT_SPEED = 0.2722842;
  static final double INTPAPOG_MIN_RECT_SPEED = -0.1802144;
  static final double INTPAPOG_MAX_RECT_ACCEL = 1./0.;
  static final double INTPAPOG_MIN_RECT_ACCEL = 1./0.;
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pc -s0.7064 -fPJadss -n15511619 -topo0,0,50000 > intpapogee-topo-0-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pc -s0.7064 -fPJadss -n15511619 -topo11,0,0 > intpapogee-topo-11-0-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pc -s0.7064 -fPJadss -n15511619 -topo11,0,50000 > intpapogee-topo-11-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pc -s0.7064 -fPJadss -n15511619 -topo11,0,-6300000 > intpapogee-topo-11-0--6300000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pc -s0.7064 -fPJadss -n15511619 -topo11,89,0 > intpapogee-topo-11-89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pc -s0.7064 -fPJadss -n15511619 -topo11,-89,0 > intpapogee-topo-11--89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pc -s0.7064 -fPJadss -n15511619 -topo11,89,50000 > intpapogee-topo-11-89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pc -s0.7064 -fPJadss -n15511619 -topo11,-89,50000 > intpapogee-topo-11--89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pc -s0.7064 -fPJadss -n15511619 -topo179,0,50000 > intpapogee-topo-179-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pc -s0.7064 -fPJadss -n15511619 -topo-179,0,50000 > intpapogee-topo--179-0-50000 &
  // ... -topo0,0,50000
  // ... -topo11,0,0
  // ... -topo11,0,50000
  // ... -topo11,0,-6300000
  // ... -topo11,89,0
  // ... -topo11,-89,0
  // ... -topo11,89,50000
  // ... -topo11,-89,50000
  // ... -topo179,0,50000
  // ... -topo-179,0,50000
  static final double INTPAPOG_MAX_TOPO_RECT_SPEED = INTPAPOG_MAX_RECT_SPEED;
  static final double INTPAPOG_MIN_TOPO_RECT_SPEED = INTPAPOG_MIN_RECT_SPEED;
  static final double INTPAPOG_MAX_TOPO_RECT_ACCEL = 1./0.;
  static final double INTPAPOG_MIN_TOPO_RECT_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double INTPAPOG_MAX_HELIO_RECT_SPEED = 1./0.;
  static final double INTPAPOG_MIN_HELIO_RECT_SPEED = 1./0.;
  static final double INTPAPOG_MAX_HELIO_RECT_ACCEL = 1./0.;
  static final double INTPAPOG_MIN_HELIO_RECT_ACCEL = 1./0.;

  static final double INTPAPOG_MAX_DECL_SPEED = 0.1278990;
  static final double INTPAPOG_MIN_DECL_SPEED = -0.1276126;
  static final double INTPAPOG_MAX_DECL_ACCEL = 1./0.;
  static final double INTPAPOG_MIN_DECL_ACCEL = 1./0.;
  // ... -topo0,0,50000
  // ... -topo11,0,0
  // ... -topo11,0,50000
  // ... -topo11,0,-6300000
  // ... -topo11,89,0
  // ... -topo11,-89,0
  // ... -topo11,89,50000
  // ... -topo11,-89,50000
  // ... -topo179,0,50000
  // ... -topo-179,0,50000
  static final double INTPAPOG_MAX_TOPO_DECL_SPEED = INTPAPOG_MAX_DECL_SPEED;
  static final double INTPAPOG_MIN_TOPO_DECL_SPEED = INTPAPOG_MIN_DECL_SPEED;
  static final double INTPAPOG_MAX_TOPO_DECL_ACCEL = 1./0.;
  static final double INTPAPOG_MIN_TOPO_DECL_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double INTPAPOG_MAX_HELIO_DECL_SPEED = 1./0.;
  static final double INTPAPOG_MIN_HELIO_DECL_SPEED = 1./0.;
  static final double INTPAPOG_MAX_HELIO_DECL_ACCEL = 1./0.;
  static final double INTPAPOG_MIN_HELIO_DECL_ACCEL = 1./0.;



///////////////////////////////////////////////////////////////
// INTERPOLATED LUNAR PERIGEE: ////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double INTPPERG_MAX_LON_SPEED = 0.583372;
  static final double INTPPERG_MIN_LON_SPEED = -2.33189;
  static final double INTPPERG_MAX_LON_ACCEL = 0.185425;
  static final double INTPPERG_MIN_LON_ACCEL = -0.185357;
  static final double INTPPERG_MAX_TOPO_LON_SPEED = 0.583371;
  static final double INTPPERG_MIN_TOPO_LON_SPEED = -2.33188;
  static final double INTPPERG_MAX_TOPO_LON_ACCEL = 0.185418;
  static final double INTPPERG_MIN_TOPO_LON_ACCEL = -0.185353;
  // No heliocentric positions for the interpolated lunar apogee...
  static final double INTPPERG_MAX_HELIO_LON_SPEED = 1./0.;
  static final double INTPPERG_MIN_HELIO_LON_SPEED = 1./0.;
  static final double INTPPERG_MAX_HELIO_LON_ACCEL = 1./0.;
  static final double INTPPERG_MIN_HELIO_LON_ACCEL = 1./0.;

  static final double INTPPERG_MAX_LAT_SPEED = 0.193078;
  static final double INTPPERG_MIN_LAT_SPEED = -0.193022;
  static final double INTPPERG_MAX_LAT_ACCEL = 0.016642;
  static final double INTPPERG_MIN_LAT_ACCEL = -0.016678;
  static final double INTPPERG_MAX_TOPO_LAT_SPEED = 0.193073;
  static final double INTPPERG_MIN_TOPO_LAT_SPEED = -0.193018;
  static final double INTPPERG_MAX_TOPO_LAT_ACCEL = 0.016639;
  static final double INTPPERG_MIN_TOPO_LAT_ACCEL = -0.016674;
  // No heliocentric positions for the interpolated lunar apogee...
  static final double INTPPERG_MAX_HELIO_LAT_SPEED = 1./0.;
  static final double INTPPERG_MIN_HELIO_LAT_SPEED = 1./0.;
  static final double INTPPERG_MAX_HELIO_LAT_ACCEL = 1./0.;
  static final double INTPPERG_MIN_HELIO_LAT_ACCEL = 1./0.;

  static final double INTPPERG_MAX_DIST_SPEED = 0.00000143617;
  static final double INTPPERG_MIN_DIST_SPEED = -0.00000143606;
  static final double INTPPERG_MAX_DIST_ACCEL = 0.0000000294169;
  static final double INTPPERG_MIN_DIST_ACCEL = -0.00000015879;
  static final double INTPPERG_MAX_TOPO_DIST_SPEED = 0.00000143615;
  static final double INTPPERG_MIN_TOPO_DIST_SPEED = -0.00000143607;
  static final double INTPPERG_MAX_TOPO_DIST_ACCEL = 0.000000029418;
  static final double INTPPERG_MIN_TOPO_DIST_ACCEL = -0.00000015880;
  // No heliocentric positions for the interpolated lunar apogee...
  static final double INTPPERG_MAX_HELIO_DIST_SPEED = 1./0.;
  static final double INTPPERG_MIN_HELIO_DIST_SPEED = 1./0.;
  static final double INTPPERG_MAX_HELIO_DIST_ACCEL = 1./0.;
  static final double INTPPERG_MIN_HELIO_DIST_ACCEL = 1./0.;

  // time ./swetest -head -bj-3027215.5 -ejplde431.eph -edir./ephe -pg -s0.7064 -fPJadss -n15511619 > intpperigee_jpl
// -2.6484532 -> 0.6663555
  static final double INTPPERG_MAX_RECT_SPEED = 0.6663555;
  static final double INTPPERG_MIN_RECT_SPEED = -2.6484532;
  static final double INTPPERG_MAX_RECT_ACCEL = 1./0.;
  static final double INTPPERG_MIN_RECT_ACCEL = 1./0.;
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pg -s0.7064 -fPJadss -n15511619 -topo0,0,50000 > intpperigee-topo-0-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pg -s0.7064 -fPJadss -n15511619 -topo11,0,0 > intpperigee-topo-11-0-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pg -s0.7064 -fPJadss -n15511619 -topo11,0,50000 > intpperigee-topo-11-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pg -s0.7064 -fPJadss -n15511619 -topo11,0,-6300000 > intpperigee-topo-11-0--6300000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pg -s0.7064 -fPJadss -n15511619 -topo11,0,50000 > intpperigee-topo-11-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pg -s0.7064 -fPJadss -n15511619 -topo11,89,0 > intpperigee-topo-11-89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pg -s0.7064 -fPJadss -n15511619 -topo11,-89,0 > intpperigee-topo-11--89-0 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pg -s0.7064 -fPJadss -n15511619 -topo11,89,50000 > intpperigee-topo-11-89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pg -s0.7064 -fPJadss -n15511619 -topo11,-89,50000 > intpperigee-topo-11--89-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pg -s0.7064 -fPJadss -n15511619 -topo179,0,50000 > intpperigee-topo-179-0-50000 &
  // time ./swetest -bj-3027215.5 -ejplde431.eph -edir./ephe -pg -s0.7064 -fPJadss -n15511619 -topo-179,0,50000 > intpperigee-topo--179-0-50000 &
  // ... -topo0,0,50000
  // ... -topo11,0,0
  // ... -topo11,0,-6300000
  // ... -topo11,89,0
  // ... -topo11,-89,0
  // ... -topo11,89,50000
  // ... -topo11,0,50000
  // ... -topo11,-89,50000
  // ... -topo179,0,50000
  // ... -topo-179,0,50000
  static final double INTPPERG_MAX_TOPO_RECT_SPEED = INTPPERG_MAX_RECT_SPEED;
  static final double INTPPERG_MIN_TOPO_RECT_SPEED = INTPPERG_MIN_RECT_SPEED;
  static final double INTPPERG_MAX_TOPO_RECT_ACCEL = 1./0.;
  static final double INTPPERG_MIN_TOPO_RECT_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double INTPPERG_MAX_HELIO_RECT_SPEED = 1./0.;
  static final double INTPPERG_MIN_HELIO_RECT_SPEED = 1./0.;
  static final double INTPPERG_MAX_HELIO_RECT_ACCEL = 1./0.;
  static final double INTPPERG_MIN_HELIO_RECT_ACCEL = 1./0.;

  static final double INTPPERG_MAX_DECL_SPEED = 1.1370722;
  static final double INTPPERG_MIN_DECL_SPEED = -1.1366842;
  static final double INTPPERG_MAX_DECL_ACCEL = 1./0.;
  static final double INTPPERG_MIN_DECL_ACCEL = 1./0.;
  // ... -topo0,0,50000
  // ... -topo11,0,0
  // ... -topo11,0,-6300000
  // ... -topo11,89,0
  // ... -topo11,-89,0
  // ... -topo11,89,50000
  // ... -topo11,0,50000
  // ... -topo11,-89,50000
  // ... -topo179,0,50000
  // ... -topo-179,0,50000
  static final double INTPPERG_MAX_TOPO_DECL_SPEED = INTPPERG_MAX_DECL_SPEED;
  static final double INTPPERG_MIN_TOPO_DECL_SPEED = INTPPERG_MIN_DECL_SPEED;
  static final double INTPPERG_MAX_TOPO_DECL_ACCEL = 1./0.;
  static final double INTPPERG_MIN_TOPO_DECL_ACCEL = 1./0.;
  // SEFLG_EQUATORIAL refers to the earth equator ALWAYS, so we skip heliocentric calculations
  static final double INTPPERG_MAX_HELIO_DECL_SPEED = 1./0.;
  static final double INTPPERG_MIN_HELIO_DECL_SPEED = 1./0.;
  static final double INTPPERG_MAX_HELIO_DECL_ACCEL = 1./0.;
  static final double INTPPERG_MIN_HELIO_DECL_ACCEL = 1./0.;



///////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////



  // Speeds and accelerations in longitudinal direction:
  // Maximum geocentric speeds in longitudinal direction:
  static final double[] maxLonSpeed = new double[]
      {SURYA_MAX_LON_SPEED,   CHANDRA_MAX_LON_SPEED, BUDHA_MAX_LON_SPEED,
       SHUKRA_MAX_LON_SPEED,  MANGALA_MAX_LON_SPEED, GURU_MAX_LON_SPEED,
       SHANI_MAX_LON_SPEED,   URANUS_MAX_LON_SPEED,  NEPTUNE_MAX_LON_SPEED,
       PLUTO_MAX_LON_SPEED,   MNODE_MAX_LON_SPEED,   TNODE_MAX_LON_SPEED,
       MAPOGEE_MAX_LON_SPEED, OAPOGEE_MAX_LON_SPEED, 1./0,
       CHIRON_MAX_LON_SPEED,  PHOLUS_MAX_LON_SPEED,  CERES_MAX_LON_SPEED,
       PALLAS_MAX_LON_SPEED,  JUNO_MAX_LON_SPEED,    VESTA_MAX_LON_SPEED,
       INTPAPOG_MAX_LON_SPEED,INTPPERG_MAX_LON_SPEED};
  // Minimum geocentric speeds in longitudinal direction:
  static final double[] minLonSpeed = new double[]
      {SURYA_MIN_LON_SPEED,   CHANDRA_MIN_LON_SPEED, BUDHA_MIN_LON_SPEED,
       SHUKRA_MIN_LON_SPEED,  MANGALA_MIN_LON_SPEED, GURU_MIN_LON_SPEED,
       SHANI_MIN_LON_SPEED,   URANUS_MIN_LON_SPEED,  NEPTUNE_MIN_LON_SPEED,
       PLUTO_MIN_LON_SPEED,   MNODE_MIN_LON_SPEED,   TNODE_MIN_LON_SPEED,
       MAPOGEE_MIN_LON_SPEED, OAPOGEE_MIN_LON_SPEED, 1./0,
       CHIRON_MIN_LON_SPEED,  PHOLUS_MIN_LON_SPEED,  CERES_MIN_LON_SPEED,
       PALLAS_MIN_LON_SPEED,  JUNO_MIN_LON_SPEED,    VESTA_MIN_LON_SPEED,
       INTPAPOG_MIN_LON_SPEED,INTPPERG_MIN_LON_SPEED};

  // Maximum topocentric speeds in longitudinal direction, up to 50000m
  // altitude:
  static final double[] maxTopoLonSpeed = new double[]
      {SURYA_MAX_TOPO_LON_SPEED,   CHANDRA_MAX_TOPO_LON_SPEED,
       BUDHA_MAX_TOPO_LON_SPEED,   SHUKRA_MAX_TOPO_LON_SPEED,
       MANGALA_MAX_TOPO_LON_SPEED, GURU_MAX_TOPO_LON_SPEED,
       SHANI_MAX_TOPO_LON_SPEED,   URANUS_MAX_TOPO_LON_SPEED,
       NEPTUNE_MAX_TOPO_LON_SPEED, PLUTO_MAX_TOPO_LON_SPEED,
       MNODE_MAX_TOPO_LON_SPEED,   TNODE_MAX_TOPO_LON_SPEED,
       MAPOGEE_MAX_TOPO_LON_SPEED, OAPOGEE_MAX_TOPO_LON_SPEED,
       1./0,                       CHIRON_MAX_TOPO_LON_SPEED,
       PHOLUS_MAX_TOPO_LON_SPEED,  CERES_MAX_TOPO_LON_SPEED,
       PALLAS_MAX_TOPO_LON_SPEED,  JUNO_MAX_TOPO_LON_SPEED,
       VESTA_MAX_TOPO_LON_SPEED,   INTPAPOG_MAX_TOPO_LON_SPEED,
       INTPPERG_MAX_TOPO_LON_SPEED};
  // Minimum topocentric speeds in longitudinal direction, up to 50000m
  // altitude:
  static final double[] minTopoLonSpeed = new double[]
      {SURYA_MIN_TOPO_LON_SPEED,   CHANDRA_MIN_TOPO_LON_SPEED,
       BUDHA_MIN_TOPO_LON_SPEED,   SHUKRA_MIN_TOPO_LON_SPEED,
       MANGALA_MIN_TOPO_LON_SPEED, GURU_MIN_TOPO_LON_SPEED,
       SHANI_MIN_TOPO_LON_SPEED,   URANUS_MIN_TOPO_LON_SPEED,
       NEPTUNE_MIN_TOPO_LON_SPEED, PLUTO_MIN_TOPO_LON_SPEED,
       MNODE_MIN_TOPO_LON_SPEED,   TNODE_MIN_TOPO_LON_SPEED,
       MAPOGEE_MIN_TOPO_LON_SPEED, OAPOGEE_MIN_TOPO_LON_SPEED,
       1./0,                       CHIRON_MIN_TOPO_LON_SPEED,
       PHOLUS_MIN_TOPO_LON_SPEED,  CERES_MIN_TOPO_LON_SPEED,
       PALLAS_MIN_TOPO_LON_SPEED,  JUNO_MIN_TOPO_LON_SPEED,
       VESTA_MIN_TOPO_LON_SPEED,   INTPAPOG_MIN_TOPO_LON_SPEED,
       INTPPERG_MIN_TOPO_LON_SPEED};

  // Maximum heliocentric speeds in longitudinal direction:
  static final double[] maxHelioLonSpeed = new double[]
      {SURYA_MAX_HELIO_LON_SPEED,   CHANDRA_MAX_HELIO_LON_SPEED,
       BUDHA_MAX_HELIO_LON_SPEED,   SHUKRA_MAX_HELIO_LON_SPEED,
       MANGALA_MAX_HELIO_LON_SPEED, GURU_MAX_HELIO_LON_SPEED,
       SHANI_MAX_HELIO_LON_SPEED,   URANUS_MAX_HELIO_LON_SPEED,
       NEPTUNE_MAX_HELIO_LON_SPEED, PLUTO_MAX_HELIO_LON_SPEED,
       MNODE_MAX_HELIO_LON_SPEED,   TNODE_MAX_HELIO_LON_SPEED,
       MAPOGEE_MAX_HELIO_LON_SPEED, OAPOGEE_MAX_HELIO_LON_SPEED,
       1./0,                        CHIRON_MAX_HELIO_LON_SPEED,
       PHOLUS_MAX_HELIO_LON_SPEED,  CERES_MAX_HELIO_LON_SPEED,
       PALLAS_MAX_HELIO_LON_SPEED,  JUNO_MAX_HELIO_LON_SPEED,
       VESTA_MAX_HELIO_LON_SPEED,   INTPAPOG_MAX_HELIO_LON_SPEED,
       INTPPERG_MAX_HELIO_LON_SPEED};
  // Minimum heliocentric speeds in longitudinal direction:
  static final double[] minHelioLonSpeed = new double[]
      {SURYA_MIN_HELIO_LON_SPEED,   CHANDRA_MIN_HELIO_LON_SPEED,
       BUDHA_MIN_HELIO_LON_SPEED,   SHUKRA_MIN_HELIO_LON_SPEED,
       MANGALA_MIN_HELIO_LON_SPEED, GURU_MIN_HELIO_LON_SPEED,
       SHANI_MIN_HELIO_LON_SPEED,   URANUS_MIN_HELIO_LON_SPEED,
       NEPTUNE_MIN_HELIO_LON_SPEED, PLUTO_MIN_HELIO_LON_SPEED,
       MNODE_MIN_HELIO_LON_SPEED,   TNODE_MIN_HELIO_LON_SPEED,
       MAPOGEE_MIN_HELIO_LON_SPEED, OAPOGEE_MIN_HELIO_LON_SPEED,
       1./0,                        CHIRON_MIN_HELIO_LON_SPEED,
       PHOLUS_MIN_HELIO_LON_SPEED,  CERES_MIN_HELIO_LON_SPEED,
       PALLAS_MIN_HELIO_LON_SPEED,  JUNO_MIN_HELIO_LON_SPEED,
       VESTA_MIN_HELIO_LON_SPEED,   INTPAPOG_MIN_HELIO_LON_SPEED,
       INTPPERG_MIN_HELIO_LON_SPEED};

  // Maximum geocentric accelerations in longitudinal direction:
  static final double[] maxLonAccel = new double[]
      {SURYA_MAX_LON_ACCEL,   CHANDRA_MAX_LON_ACCEL, BUDHA_MAX_LON_ACCEL,
       SHUKRA_MAX_LON_ACCEL,  MANGALA_MAX_LON_ACCEL, GURU_MAX_LON_ACCEL,
       SHANI_MAX_LON_ACCEL,   URANUS_MAX_LON_ACCEL,  NEPTUNE_MAX_LON_ACCEL,
       PLUTO_MAX_LON_ACCEL,   MNODE_MAX_LON_ACCEL,   TNODE_MAX_LON_ACCEL,
       MAPOGEE_MAX_LON_ACCEL, OAPOGEE_MAX_LON_ACCEL, 1./0,
       CHIRON_MAX_LON_ACCEL,  PHOLUS_MAX_LON_ACCEL,  CERES_MAX_LON_ACCEL,
       PALLAS_MAX_LON_ACCEL,  JUNO_MAX_LON_ACCEL,    VESTA_MAX_LON_ACCEL,
       INTPAPOG_MAX_LON_ACCEL,INTPPERG_MAX_LON_ACCEL};
  // Minimum geocentric accelerations in longitudinal direction:
  static final double[] minLonAccel = new double[]
      {SURYA_MIN_LON_ACCEL,   CHANDRA_MIN_LON_ACCEL, BUDHA_MIN_LON_ACCEL,
       SHUKRA_MIN_LON_ACCEL,  MANGALA_MIN_LON_ACCEL, GURU_MIN_LON_ACCEL,
       SHANI_MIN_LON_ACCEL,   URANUS_MIN_LON_ACCEL,  NEPTUNE_MIN_LON_ACCEL,
       PLUTO_MIN_LON_ACCEL,   MNODE_MIN_LON_ACCEL,   TNODE_MIN_LON_ACCEL,
       MAPOGEE_MIN_LON_ACCEL, OAPOGEE_MIN_LON_ACCEL, 1./0,
       CHIRON_MIN_LON_ACCEL,  PHOLUS_MIN_LON_ACCEL,  CERES_MIN_LON_ACCEL,
       PALLAS_MIN_LON_ACCEL,  JUNO_MIN_LON_ACCEL,    VESTA_MIN_LON_ACCEL,
       INTPAPOG_MIN_LON_ACCEL,INTPPERG_MIN_LON_ACCEL};

  // Maximum topocentric accelerations in longitudinal direction, up to
  // 50000m altitude:
  static final double[] maxTopoLonAccel = new double[]
      {SURYA_MAX_TOPO_LON_ACCEL,   CHANDRA_MAX_TOPO_LON_ACCEL,
       BUDHA_MAX_TOPO_LON_ACCEL,   SHUKRA_MAX_TOPO_LON_ACCEL,
       MANGALA_MAX_TOPO_LON_ACCEL, GURU_MAX_TOPO_LON_ACCEL,
       SHANI_MAX_TOPO_LON_ACCEL,   URANUS_MAX_TOPO_LON_ACCEL,
       NEPTUNE_MAX_TOPO_LON_ACCEL, PLUTO_MAX_TOPO_LON_ACCEL,
       MNODE_MAX_TOPO_LON_ACCEL,   TNODE_MAX_TOPO_LON_ACCEL,
       MAPOGEE_MAX_TOPO_LON_ACCEL, OAPOGEE_MAX_TOPO_LON_ACCEL,
       1./0,                       CHIRON_MAX_TOPO_LON_ACCEL,
       PHOLUS_MAX_TOPO_LON_ACCEL,  CERES_MAX_TOPO_LON_ACCEL,
       PALLAS_MAX_TOPO_LON_ACCEL,  JUNO_MAX_TOPO_LON_ACCEL,
       VESTA_MAX_TOPO_LON_ACCEL,   INTPAPOG_MAX_TOPO_LON_ACCEL,
       INTPPERG_MAX_TOPO_LON_ACCEL};
  // Minimum topocentric accelerations in longitudinal direction, up to
  // 50000m altitude:
  static final double[] minTopoLonAccel = new double[]
      {SURYA_MIN_TOPO_LON_ACCEL,   CHANDRA_MIN_TOPO_LON_ACCEL,
       BUDHA_MIN_TOPO_LON_ACCEL,   SHUKRA_MIN_TOPO_LON_ACCEL,
       MANGALA_MIN_TOPO_LON_ACCEL, GURU_MIN_TOPO_LON_ACCEL,
       SHANI_MIN_TOPO_LON_ACCEL,   URANUS_MIN_TOPO_LON_ACCEL,
       NEPTUNE_MIN_TOPO_LON_ACCEL, PLUTO_MIN_TOPO_LON_ACCEL,
       MNODE_MIN_TOPO_LON_ACCEL,   TNODE_MIN_TOPO_LON_ACCEL,
       MAPOGEE_MIN_TOPO_LON_ACCEL, OAPOGEE_MIN_TOPO_LON_ACCEL,
       1./0,                       CHIRON_MIN_TOPO_LON_ACCEL,
       PHOLUS_MIN_TOPO_LON_ACCEL,  CERES_MIN_TOPO_LON_ACCEL,
       PALLAS_MIN_TOPO_LON_ACCEL,  JUNO_MIN_TOPO_LON_ACCEL,
       VESTA_MIN_TOPO_LON_ACCEL,   INTPAPOG_MIN_TOPO_LON_ACCEL,
       INTPPERG_MIN_TOPO_LON_ACCEL};

  // Maximum heliocentric accelerations in longitudinal direction:
  static final double[] maxHelioLonAccel = new double[]
      {SURYA_MAX_HELIO_LON_ACCEL,   CHANDRA_MAX_HELIO_LON_ACCEL,
       BUDHA_MAX_HELIO_LON_ACCEL,   SHUKRA_MAX_HELIO_LON_ACCEL,
       MANGALA_MAX_HELIO_LON_ACCEL, GURU_MAX_HELIO_LON_ACCEL,
       SHANI_MAX_HELIO_LON_ACCEL,   URANUS_MAX_HELIO_LON_ACCEL,
       NEPTUNE_MAX_HELIO_LON_ACCEL, PLUTO_MAX_HELIO_LON_ACCEL,
       MNODE_MAX_HELIO_LON_ACCEL,   TNODE_MAX_HELIO_LON_ACCEL,
       MAPOGEE_MAX_HELIO_LON_ACCEL, OAPOGEE_MAX_HELIO_LON_ACCEL,
       1./0,                        CHIRON_MAX_HELIO_LON_ACCEL,
       PHOLUS_MAX_HELIO_LON_ACCEL,  CERES_MAX_HELIO_LON_ACCEL,
       PALLAS_MAX_HELIO_LON_ACCEL,  JUNO_MAX_HELIO_LON_ACCEL,
       VESTA_MAX_HELIO_LON_ACCEL,   INTPAPOG_MAX_HELIO_LON_ACCEL,
       INTPPERG_MAX_HELIO_LON_ACCEL};
  // Minimum heliocentric accelerations in longitudinal direction:
  static final double[] minHelioLonAccel = new double[]
      {SURYA_MIN_HELIO_LON_ACCEL,   CHANDRA_MIN_HELIO_LON_ACCEL,
       BUDHA_MIN_HELIO_LON_ACCEL,   SHUKRA_MIN_HELIO_LON_ACCEL,
       MANGALA_MIN_HELIO_LON_ACCEL, GURU_MIN_HELIO_LON_ACCEL,
       SHANI_MIN_HELIO_LON_ACCEL,   URANUS_MIN_HELIO_LON_ACCEL,
       NEPTUNE_MIN_HELIO_LON_ACCEL, PLUTO_MIN_HELIO_LON_ACCEL,
       MNODE_MIN_HELIO_LON_ACCEL,   TNODE_MIN_HELIO_LON_ACCEL,
       MAPOGEE_MIN_HELIO_LON_ACCEL, OAPOGEE_MIN_HELIO_LON_ACCEL,
       1./0,                        CHIRON_MIN_HELIO_LON_ACCEL,
       PHOLUS_MIN_HELIO_LON_ACCEL,  CERES_MIN_HELIO_LON_ACCEL,
       PALLAS_MIN_HELIO_LON_ACCEL,  JUNO_MIN_HELIO_LON_ACCEL,
       VESTA_MIN_HELIO_LON_ACCEL,   INTPAPOG_MIN_HELIO_LON_ACCEL,
       INTPPERG_MIN_HELIO_LON_ACCEL};




  // Speeds and accelerations in latitudinal direction:
  // Maximum geocentric speeds in latitudinal direction:
  static final double[] maxLatSpeed = new double[]
      {SURYA_MAX_LAT_SPEED,   CHANDRA_MAX_LAT_SPEED, BUDHA_MAX_LAT_SPEED,
       SHUKRA_MAX_LAT_SPEED,  MANGALA_MAX_LAT_SPEED, GURU_MAX_LAT_SPEED,
       SHANI_MAX_LAT_SPEED,   URANUS_MAX_LAT_SPEED,  NEPTUNE_MAX_LAT_SPEED,
       PLUTO_MAX_LAT_SPEED,   MNODE_MAX_LAT_SPEED,   TNODE_MAX_LAT_SPEED,
       MAPOGEE_MAX_LAT_SPEED, OAPOGEE_MAX_LAT_SPEED, 1./0,
       CHIRON_MAX_LAT_SPEED,  PHOLUS_MAX_LAT_SPEED,  CERES_MAX_LAT_SPEED,
       PALLAS_MAX_LAT_SPEED,  JUNO_MAX_LAT_SPEED,    VESTA_MAX_LAT_SPEED,
       INTPAPOG_MAX_LAT_SPEED,INTPPERG_MAX_LAT_SPEED};
  // Minimum geocentric speeds in latitudinal direction:
  static final double[] minLatSpeed = new double[]
      {SURYA_MIN_LAT_SPEED,   CHANDRA_MIN_LAT_SPEED, BUDHA_MIN_LAT_SPEED,
       SHUKRA_MIN_LAT_SPEED,  MANGALA_MIN_LAT_SPEED, GURU_MIN_LAT_SPEED,
       SHANI_MIN_LAT_SPEED,   URANUS_MIN_LAT_SPEED,  NEPTUNE_MIN_LAT_SPEED,
       PLUTO_MIN_LAT_SPEED,   MNODE_MIN_LAT_SPEED,   TNODE_MIN_LAT_SPEED,
       MAPOGEE_MIN_LAT_SPEED, OAPOGEE_MIN_LAT_SPEED, 1./0,
       CHIRON_MIN_LAT_SPEED,  PHOLUS_MIN_LAT_SPEED,  CERES_MIN_LAT_SPEED,
       PALLAS_MIN_LAT_SPEED,  JUNO_MIN_LAT_SPEED,    VESTA_MIN_LAT_SPEED,
       INTPAPOG_MIN_LAT_SPEED,INTPPERG_MIN_LAT_SPEED};

  // Maximum topocentric speeds in latitudinal direction, up to 50000m
  // altitude:
  static final double[] maxTopoLatSpeed = new double[]
      {SURYA_MAX_TOPO_LAT_SPEED,   CHANDRA_MAX_TOPO_LAT_SPEED,
       BUDHA_MAX_TOPO_LAT_SPEED,   SHUKRA_MAX_TOPO_LAT_SPEED,
       MANGALA_MAX_TOPO_LAT_SPEED, GURU_MAX_TOPO_LAT_SPEED,
       SHANI_MAX_TOPO_LAT_SPEED,   URANUS_MAX_TOPO_LAT_SPEED,
       NEPTUNE_MAX_TOPO_LAT_SPEED, PLUTO_MAX_TOPO_LAT_SPEED,
       MNODE_MAX_TOPO_LAT_SPEED,   TNODE_MAX_TOPO_LAT_SPEED,
       MAPOGEE_MAX_TOPO_LAT_SPEED, OAPOGEE_MAX_TOPO_LAT_SPEED,
       1./0,                       CHIRON_MAX_TOPO_LAT_SPEED,
       PHOLUS_MAX_TOPO_LAT_SPEED,  CERES_MAX_TOPO_LAT_SPEED,
       PALLAS_MAX_TOPO_LAT_SPEED,  JUNO_MAX_TOPO_LAT_SPEED,
       VESTA_MAX_TOPO_LAT_SPEED,   INTPAPOG_MAX_TOPO_LAT_SPEED,
       INTPPERG_MAX_TOPO_LAT_SPEED};
  // Minimum topocentric speeds in latitudinal direction, up to 50000m
  // altitude:
  static final double[] minTopoLatSpeed = new double[]
      {SURYA_MIN_TOPO_LAT_SPEED,   CHANDRA_MIN_TOPO_LAT_SPEED,
       BUDHA_MIN_TOPO_LAT_SPEED,   SHUKRA_MIN_TOPO_LAT_SPEED,
       MANGALA_MIN_TOPO_LAT_SPEED, GURU_MIN_TOPO_LAT_SPEED,
       SHANI_MIN_TOPO_LAT_SPEED,   URANUS_MIN_TOPO_LAT_SPEED,
       NEPTUNE_MIN_TOPO_LAT_SPEED, PLUTO_MIN_TOPO_LAT_SPEED,
       MNODE_MIN_TOPO_LAT_SPEED,   TNODE_MIN_TOPO_LAT_SPEED,
       MAPOGEE_MIN_TOPO_LAT_SPEED, OAPOGEE_MIN_TOPO_LAT_SPEED,
       1./0,                       CHIRON_MIN_TOPO_LAT_SPEED,
       PHOLUS_MIN_TOPO_LAT_SPEED,  CERES_MIN_TOPO_LAT_SPEED,
       PALLAS_MIN_TOPO_LAT_SPEED,  JUNO_MIN_TOPO_LAT_SPEED,
       VESTA_MIN_TOPO_LAT_SPEED,   INTPAPOG_MIN_TOPO_LAT_SPEED,
       INTPPERG_MIN_TOPO_LAT_SPEED};

  // Maximum heliocentric speeds in latitudinal direction:
  static final double[] maxHelioLatSpeed = new double[]
      {SURYA_MAX_HELIO_LAT_SPEED,   CHANDRA_MAX_HELIO_LAT_SPEED,
       BUDHA_MAX_HELIO_LAT_SPEED,   SHUKRA_MAX_HELIO_LAT_SPEED,
       MANGALA_MAX_HELIO_LAT_SPEED, GURU_MAX_HELIO_LAT_SPEED,
       SHANI_MAX_HELIO_LAT_SPEED,   URANUS_MAX_HELIO_LAT_SPEED,
       NEPTUNE_MAX_HELIO_LAT_SPEED, PLUTO_MAX_HELIO_LAT_SPEED,
       MNODE_MAX_HELIO_LAT_SPEED,   TNODE_MAX_HELIO_LAT_SPEED,
       MAPOGEE_MAX_HELIO_LAT_SPEED, OAPOGEE_MAX_HELIO_LAT_SPEED,
       1./0,                        CHIRON_MAX_HELIO_LAT_SPEED,
       PHOLUS_MAX_HELIO_LAT_SPEED,  CERES_MAX_HELIO_LAT_SPEED,
       PALLAS_MAX_HELIO_LAT_SPEED,  JUNO_MAX_HELIO_LAT_SPEED,
       VESTA_MAX_HELIO_LAT_SPEED,   INTPAPOG_MAX_HELIO_LAT_SPEED,
       INTPPERG_MAX_HELIO_LAT_SPEED};
  // Minimum heliocentric speeds in latitudinal direction:
  static final double[] minHelioLatSpeed = new double[]
      {SURYA_MIN_HELIO_LAT_SPEED,   CHANDRA_MIN_HELIO_LAT_SPEED,
       BUDHA_MIN_HELIO_LAT_SPEED,   SHUKRA_MIN_HELIO_LAT_SPEED,
       MANGALA_MIN_HELIO_LAT_SPEED, GURU_MIN_HELIO_LAT_SPEED,
       SHANI_MIN_HELIO_LAT_SPEED,   URANUS_MIN_HELIO_LAT_SPEED,
       NEPTUNE_MIN_HELIO_LAT_SPEED, PLUTO_MIN_HELIO_LAT_SPEED,
       MNODE_MIN_HELIO_LAT_SPEED,   TNODE_MIN_HELIO_LAT_SPEED,
       MAPOGEE_MIN_HELIO_LAT_SPEED, OAPOGEE_MIN_HELIO_LAT_SPEED,
       1./0,                        CHIRON_MIN_HELIO_LAT_SPEED,
       PHOLUS_MIN_HELIO_LAT_SPEED,  CERES_MIN_HELIO_LAT_SPEED,
       PALLAS_MIN_HELIO_LAT_SPEED,  JUNO_MIN_HELIO_LAT_SPEED,
       VESTA_MIN_HELIO_LAT_SPEED,   INTPAPOG_MAX_HELIO_LAT_SPEED,
       INTPPERG_MIN_HELIO_LAT_SPEED};

  // Maximum geocentric accelerations in latitudinal direction:
  static final double[] maxLatAccel = new double[]
      {SURYA_MAX_LAT_ACCEL,   CHANDRA_MAX_LAT_ACCEL, BUDHA_MAX_LAT_ACCEL,
       SHUKRA_MAX_LAT_ACCEL,  MANGALA_MAX_LAT_ACCEL, GURU_MAX_LAT_ACCEL,
       SHANI_MAX_LAT_ACCEL,   URANUS_MAX_LAT_ACCEL,  NEPTUNE_MAX_LAT_ACCEL,
       PLUTO_MAX_LAT_ACCEL,   MNODE_MAX_LAT_ACCEL,   TNODE_MAX_LAT_ACCEL,
       MAPOGEE_MAX_LAT_ACCEL, OAPOGEE_MAX_LAT_ACCEL, 1./0,
       CHIRON_MAX_LAT_ACCEL,  PHOLUS_MAX_LAT_ACCEL,  CERES_MAX_LAT_ACCEL,
       PALLAS_MAX_LAT_ACCEL,  JUNO_MAX_LAT_ACCEL,    VESTA_MAX_LAT_ACCEL,
       INTPAPOG_MAX_LAT_ACCEL,INTPPERG_MAX_LAT_ACCEL};
  // Minimum geocentric accelerations in latitudinal direction:
  static final double[] minLatAccel = new double[]
      {SURYA_MIN_LAT_ACCEL,   CHANDRA_MIN_LAT_ACCEL, BUDHA_MIN_LAT_ACCEL,
       SHUKRA_MIN_LAT_ACCEL,  MANGALA_MIN_LAT_ACCEL, GURU_MIN_LAT_ACCEL,
       SHANI_MIN_LAT_ACCEL,   URANUS_MIN_LAT_ACCEL,  NEPTUNE_MIN_LAT_ACCEL,
       PLUTO_MIN_LAT_ACCEL,   MNODE_MIN_LAT_ACCEL,   TNODE_MIN_LAT_ACCEL,
       MAPOGEE_MIN_LAT_ACCEL, OAPOGEE_MIN_LAT_ACCEL, 1./0,
       CHIRON_MIN_LAT_ACCEL,  PHOLUS_MIN_LAT_ACCEL,  CERES_MIN_LAT_ACCEL,
       PALLAS_MIN_LAT_ACCEL,  JUNO_MIN_LAT_ACCEL,    VESTA_MIN_LAT_ACCEL,
       INTPAPOG_MIN_LAT_ACCEL,INTPPERG_MIN_LAT_ACCEL};

  // Maximum topocentric accelerations in latitudinal direction, up to
  // 50000m altitude:
  static final double[] maxTopoLatAccel = new double[]
      {SURYA_MAX_TOPO_LAT_ACCEL,   CHANDRA_MAX_TOPO_LAT_ACCEL,
       BUDHA_MAX_TOPO_LAT_ACCEL,   SHUKRA_MAX_TOPO_LAT_ACCEL,
       MANGALA_MAX_TOPO_LAT_ACCEL, GURU_MAX_TOPO_LAT_ACCEL,
       SHANI_MAX_TOPO_LAT_ACCEL,   URANUS_MAX_TOPO_LAT_ACCEL,
       NEPTUNE_MAX_TOPO_LAT_ACCEL, PLUTO_MAX_TOPO_LAT_ACCEL,
       MNODE_MAX_TOPO_LAT_ACCEL,   TNODE_MAX_TOPO_LAT_ACCEL,
       MAPOGEE_MAX_TOPO_LAT_ACCEL, OAPOGEE_MAX_TOPO_LAT_ACCEL,
       1./0,                       CHIRON_MAX_TOPO_LAT_ACCEL,
       PHOLUS_MAX_TOPO_LAT_ACCEL,  CERES_MAX_TOPO_LAT_ACCEL,
       PALLAS_MAX_TOPO_LAT_ACCEL,  JUNO_MAX_TOPO_LAT_ACCEL,
       VESTA_MAX_TOPO_LAT_ACCEL,   INTPAPOG_MAX_TOPO_LAT_ACCEL,
       INTPPERG_MAX_TOPO_LAT_ACCEL};
  // Minimum topocentric accelerations in latitudinal direction, up to
  // 50000m altitude:
  static final double[] minTopoLatAccel = new double[]
      {SURYA_MIN_TOPO_LAT_ACCEL,   CHANDRA_MIN_TOPO_LAT_ACCEL,
       BUDHA_MIN_TOPO_LAT_ACCEL,   SHUKRA_MIN_TOPO_LAT_ACCEL,
       MANGALA_MIN_TOPO_LAT_ACCEL, GURU_MIN_TOPO_LAT_ACCEL,
       SHANI_MIN_TOPO_LAT_ACCEL,   URANUS_MIN_TOPO_LAT_ACCEL,
       NEPTUNE_MIN_TOPO_LAT_ACCEL, PLUTO_MIN_TOPO_LAT_ACCEL,
       MNODE_MIN_TOPO_LAT_ACCEL,   TNODE_MIN_TOPO_LAT_ACCEL,
       MAPOGEE_MIN_TOPO_LAT_ACCEL, OAPOGEE_MIN_TOPO_LAT_ACCEL,
       1./0,                       CHIRON_MIN_TOPO_LAT_ACCEL,
       PHOLUS_MIN_TOPO_LAT_ACCEL,  CERES_MIN_TOPO_LAT_ACCEL,
       PALLAS_MIN_TOPO_LAT_ACCEL,  JUNO_MIN_TOPO_LAT_ACCEL,
       VESTA_MIN_TOPO_LAT_ACCEL,   INTPAPOG_MIN_TOPO_LAT_ACCEL,
       INTPPERG_MIN_TOPO_LAT_ACCEL};

  // Maximum heliocentric accelerations in latitudinal direction:
  static final double[] maxHelioLatAccel = new double[]
      {SURYA_MAX_HELIO_LAT_ACCEL,   CHANDRA_MAX_HELIO_LAT_ACCEL,
       BUDHA_MAX_HELIO_LAT_ACCEL,   SHUKRA_MAX_HELIO_LAT_ACCEL,
       MANGALA_MAX_HELIO_LAT_ACCEL, GURU_MAX_HELIO_LAT_ACCEL,
       SHANI_MAX_HELIO_LAT_ACCEL,   URANUS_MAX_HELIO_LAT_ACCEL,
       NEPTUNE_MAX_HELIO_LAT_ACCEL, PLUTO_MAX_HELIO_LAT_ACCEL,
       MNODE_MAX_HELIO_LAT_ACCEL,   TNODE_MAX_HELIO_LAT_ACCEL,
       MAPOGEE_MAX_HELIO_LAT_ACCEL, OAPOGEE_MAX_HELIO_LAT_ACCEL,
       1./0,                        CHIRON_MAX_HELIO_LAT_ACCEL,
       PHOLUS_MAX_HELIO_LAT_ACCEL,  CERES_MAX_HELIO_LAT_ACCEL,
       PALLAS_MAX_HELIO_LAT_ACCEL,  JUNO_MAX_HELIO_LAT_ACCEL,
       VESTA_MAX_HELIO_LAT_ACCEL,   INTPAPOG_MAX_HELIO_LAT_ACCEL,
       INTPPERG_MAX_HELIO_LAT_ACCEL};
  // Minimum heliocentric accelerations in latitudinal direction:
  static final double[] minHelioLatAccel = new double[]
      {SURYA_MIN_HELIO_LAT_ACCEL,   CHANDRA_MIN_HELIO_LAT_ACCEL,
       BUDHA_MIN_HELIO_LAT_ACCEL,   SHUKRA_MIN_HELIO_LAT_ACCEL,
       MANGALA_MIN_HELIO_LAT_ACCEL, GURU_MIN_HELIO_LAT_ACCEL,
       SHANI_MIN_HELIO_LAT_ACCEL,   URANUS_MIN_HELIO_LAT_ACCEL,
       NEPTUNE_MIN_HELIO_LAT_ACCEL, PLUTO_MIN_HELIO_LAT_ACCEL,
       MNODE_MIN_HELIO_LAT_ACCEL,   TNODE_MIN_HELIO_LAT_ACCEL,
       MAPOGEE_MIN_HELIO_LAT_ACCEL, OAPOGEE_MIN_HELIO_LAT_ACCEL,
       1./0,                        CHIRON_MIN_HELIO_LAT_ACCEL,
       PHOLUS_MIN_HELIO_LAT_ACCEL,  CERES_MIN_HELIO_LAT_ACCEL,
       PALLAS_MIN_HELIO_LAT_ACCEL,  JUNO_MIN_HELIO_LAT_ACCEL,
       VESTA_MIN_HELIO_LAT_ACCEL,   INTPAPOG_MIN_HELIO_LAT_ACCEL,
       INTPPERG_MIN_HELIO_LAT_ACCEL};




  // Speeds and accelerations in the distance to the earth:
  // Maximum geocentric speeds in the distance to the earth:
  static final double[] maxDistSpeed = new double[]
      {SURYA_MAX_DIST_SPEED,   CHANDRA_MAX_DIST_SPEED, BUDHA_MAX_DIST_SPEED,
       SHUKRA_MAX_DIST_SPEED,  MANGALA_MAX_DIST_SPEED, GURU_MAX_DIST_SPEED,
       SHANI_MAX_DIST_SPEED,   URANUS_MAX_DIST_SPEED,  NEPTUNE_MAX_DIST_SPEED,
       PLUTO_MAX_DIST_SPEED,   MNODE_MAX_DIST_SPEED,   TNODE_MAX_DIST_SPEED,
       MAPOGEE_MAX_DIST_SPEED, OAPOGEE_MAX_DIST_SPEED, 1./0.,
       CHIRON_MAX_DIST_SPEED,  PHOLUS_MAX_DIST_SPEED,  CERES_MAX_DIST_SPEED,
       PALLAS_MAX_DIST_SPEED,  JUNO_MAX_DIST_SPEED,    VESTA_MAX_DIST_SPEED,
       INTPAPOG_MAX_DIST_SPEED,INTPPERG_MAX_DIST_SPEED};
  // Minimum geocentric speeds in the direction of the distance to the earth:
  static final double[] minDistSpeed = new double[]
      {SURYA_MIN_DIST_SPEED,   CHANDRA_MIN_DIST_SPEED, BUDHA_MIN_DIST_SPEED,
       SHUKRA_MIN_DIST_SPEED,  MANGALA_MIN_DIST_SPEED, GURU_MIN_DIST_SPEED,
       SHANI_MIN_DIST_SPEED,   URANUS_MIN_DIST_SPEED,  NEPTUNE_MIN_DIST_SPEED,
       PLUTO_MIN_DIST_SPEED,   MNODE_MIN_DIST_SPEED,   TNODE_MIN_DIST_SPEED,
       MAPOGEE_MIN_DIST_SPEED, OAPOGEE_MIN_DIST_SPEED, 1./0.,
       CHIRON_MIN_DIST_SPEED,  PHOLUS_MIN_DIST_SPEED,  CERES_MIN_DIST_SPEED,
       PALLAS_MIN_DIST_SPEED,  JUNO_MIN_DIST_SPEED,    VESTA_MIN_DIST_SPEED,
       INTPAPOG_MIN_DIST_SPEED,INTPPERG_MIN_DIST_SPEED};

  // Maximum topocentric speeds in the distance to the earth:
  static final double[] maxTopoDistSpeed = new double[]
      {SURYA_MAX_TOPO_DIST_SPEED,   CHANDRA_MAX_TOPO_DIST_SPEED,
       BUDHA_MAX_TOPO_DIST_SPEED,   SHUKRA_MAX_TOPO_DIST_SPEED,
       MANGALA_MAX_TOPO_DIST_SPEED, GURU_MAX_TOPO_DIST_SPEED,
       SHANI_MAX_TOPO_DIST_SPEED,   URANUS_MAX_TOPO_DIST_SPEED,
       NEPTUNE_MAX_TOPO_DIST_SPEED, PLUTO_MAX_TOPO_DIST_SPEED,
       MNODE_MAX_TOPO_DIST_SPEED,   TNODE_MAX_TOPO_DIST_SPEED,
       MAPOGEE_MAX_TOPO_DIST_SPEED, OAPOGEE_MAX_TOPO_DIST_SPEED,
       1./0.,                       CHIRON_MAX_TOPO_DIST_SPEED,
       PHOLUS_MAX_TOPO_DIST_SPEED,  CERES_MAX_TOPO_DIST_SPEED,
       PALLAS_MAX_TOPO_DIST_SPEED,  JUNO_MAX_TOPO_DIST_SPEED,
       VESTA_MAX_TOPO_DIST_SPEED,   INTPAPOG_MAX_TOPO_DIST_SPEED,
       INTPPERG_MAX_TOPO_DIST_SPEED};
  // Minimum topocentric speeds in the distance to the earth:
  static final double[] minTopoDistSpeed = new double[]
      {SURYA_MIN_TOPO_DIST_SPEED,   CHANDRA_MIN_TOPO_DIST_SPEED,
       BUDHA_MIN_TOPO_DIST_SPEED,   SHUKRA_MIN_TOPO_DIST_SPEED,
       MANGALA_MIN_TOPO_DIST_SPEED, GURU_MIN_TOPO_DIST_SPEED,
       SHANI_MIN_TOPO_DIST_SPEED,   URANUS_MIN_TOPO_DIST_SPEED,
       NEPTUNE_MIN_TOPO_DIST_SPEED, PLUTO_MIN_TOPO_DIST_SPEED,
       MNODE_MIN_TOPO_DIST_SPEED,   TNODE_MIN_TOPO_DIST_SPEED,
       MAPOGEE_MIN_TOPO_DIST_SPEED, OAPOGEE_MIN_TOPO_DIST_SPEED,
       1./0.,                       CHIRON_MIN_TOPO_DIST_SPEED,
       PHOLUS_MIN_TOPO_DIST_SPEED,  CERES_MIN_TOPO_DIST_SPEED,
       PALLAS_MIN_TOPO_DIST_SPEED,  JUNO_MIN_TOPO_DIST_SPEED,
       VESTA_MIN_TOPO_DIST_SPEED,   INTPAPOG_MIN_TOPO_DIST_SPEED,
       INTPPERG_MIN_TOPO_DIST_SPEED};

  // Maximum speeds in the distance to the sun:
  static final double[] maxHelioDistSpeed = new double[]
      {SURYA_MAX_HELIO_DIST_SPEED,   CHANDRA_MAX_HELIO_DIST_SPEED,
       BUDHA_MAX_HELIO_DIST_SPEED,   SHUKRA_MAX_HELIO_DIST_SPEED,
       MANGALA_MAX_HELIO_DIST_SPEED, GURU_MAX_HELIO_DIST_SPEED,
       SHANI_MAX_HELIO_DIST_SPEED,   URANUS_MAX_HELIO_DIST_SPEED,
       NEPTUNE_MAX_HELIO_DIST_SPEED, PLUTO_MAX_HELIO_DIST_SPEED,
       MNODE_MAX_HELIO_DIST_SPEED,   TNODE_MAX_HELIO_DIST_SPEED,
       MAPOGEE_MAX_HELIO_DIST_SPEED, OAPOGEE_MAX_HELIO_DIST_SPEED,
       1./0.,                        CHIRON_MAX_HELIO_DIST_SPEED,
       PHOLUS_MAX_HELIO_DIST_SPEED,  CERES_MAX_HELIO_DIST_SPEED,
       PALLAS_MAX_HELIO_DIST_SPEED,  JUNO_MAX_HELIO_DIST_SPEED,
       VESTA_MAX_HELIO_DIST_SPEED,   INTPAPOG_MAX_HELIO_DIST_SPEED,
       INTPPERG_MAX_HELIO_DIST_SPEED};
  // Minimum speeds in the distance to the sun:
  static final double[] minHelioDistSpeed = new double[]
      {SURYA_MIN_HELIO_DIST_SPEED,   CHANDRA_MIN_HELIO_DIST_SPEED,
       BUDHA_MIN_HELIO_DIST_SPEED,   SHUKRA_MIN_HELIO_DIST_SPEED,
       MANGALA_MIN_HELIO_DIST_SPEED, GURU_MIN_HELIO_DIST_SPEED,
       SHANI_MIN_HELIO_DIST_SPEED,   URANUS_MIN_HELIO_DIST_SPEED,
       NEPTUNE_MIN_HELIO_DIST_SPEED, PLUTO_MIN_HELIO_DIST_SPEED,
       MNODE_MIN_HELIO_DIST_SPEED,   TNODE_MIN_HELIO_DIST_SPEED,
       MAPOGEE_MIN_HELIO_DIST_SPEED, OAPOGEE_MIN_HELIO_DIST_SPEED,
       1./0.,                        CHIRON_MIN_HELIO_DIST_SPEED,
       PHOLUS_MIN_HELIO_DIST_SPEED,  CERES_MIN_HELIO_DIST_SPEED,
       PALLAS_MIN_HELIO_DIST_SPEED,  JUNO_MIN_HELIO_DIST_SPEED,
       VESTA_MIN_HELIO_DIST_SPEED,   INTPAPOG_MIN_HELIO_DIST_SPEED,
       INTPPERG_MIN_HELIO_DIST_SPEED};

  // Maximum geocentric accelerations in the distance to the earth:
  static final double[] maxDistAccel = new double[]
      {SURYA_MAX_DIST_ACCEL,   CHANDRA_MAX_DIST_ACCEL, BUDHA_MAX_DIST_ACCEL,
       SHUKRA_MAX_DIST_ACCEL,  MANGALA_MAX_DIST_ACCEL, GURU_MAX_DIST_ACCEL,
       SHANI_MAX_DIST_ACCEL,   URANUS_MAX_DIST_ACCEL,  NEPTUNE_MAX_DIST_ACCEL,
       PLUTO_MAX_DIST_ACCEL,   MNODE_MAX_DIST_ACCEL,   TNODE_MAX_DIST_ACCEL,
       MAPOGEE_MAX_DIST_ACCEL, OAPOGEE_MAX_DIST_ACCEL, 1./0.,
       CHIRON_MAX_DIST_ACCEL,  PHOLUS_MAX_DIST_ACCEL,  CERES_MAX_DIST_ACCEL,
       PALLAS_MAX_DIST_ACCEL,  JUNO_MAX_DIST_ACCEL,    VESTA_MAX_DIST_ACCEL,
       INTPAPOG_MAX_DIST_ACCEL,INTPPERG_MAX_DIST_ACCEL};
  // Minimum geocentric accelerations in the distance to the earth:
  static final double[] minDistAccel = new double[]
      {SURYA_MIN_DIST_ACCEL,   CHANDRA_MIN_DIST_ACCEL, BUDHA_MIN_DIST_ACCEL,
       SHUKRA_MIN_DIST_ACCEL,  MANGALA_MIN_DIST_ACCEL, GURU_MIN_DIST_ACCEL,
       SHANI_MIN_DIST_ACCEL,   URANUS_MIN_DIST_ACCEL,  NEPTUNE_MIN_DIST_ACCEL,
       PLUTO_MIN_DIST_ACCEL,   MNODE_MIN_DIST_ACCEL,   TNODE_MIN_DIST_ACCEL,
       MAPOGEE_MIN_DIST_ACCEL, OAPOGEE_MIN_DIST_ACCEL, 1./0.,
       CHIRON_MIN_DIST_ACCEL,  PHOLUS_MIN_DIST_ACCEL,  CERES_MIN_DIST_ACCEL,
       PALLAS_MIN_DIST_ACCEL,  JUNO_MIN_DIST_ACCEL,    VESTA_MIN_DIST_ACCEL,
       INTPAPOG_MIN_DIST_ACCEL,INTPPERG_MIN_DIST_ACCEL};

  // Maximum topocentric accelerations in the distance to the earth:
  static final double[] maxTopoDistAccel = new double[]
      {SURYA_MAX_TOPO_DIST_ACCEL,   CHANDRA_MAX_TOPO_DIST_ACCEL,
       BUDHA_MAX_TOPO_DIST_ACCEL,   SHUKRA_MAX_TOPO_DIST_ACCEL,
       MANGALA_MAX_TOPO_DIST_ACCEL, GURU_MAX_TOPO_DIST_ACCEL,
       SHANI_MAX_TOPO_DIST_ACCEL,   URANUS_MAX_TOPO_DIST_ACCEL,
       NEPTUNE_MAX_TOPO_DIST_ACCEL, PLUTO_MAX_TOPO_DIST_ACCEL,
       MNODE_MAX_TOPO_DIST_ACCEL,   TNODE_MAX_TOPO_DIST_ACCEL,
       MAPOGEE_MAX_TOPO_DIST_ACCEL, OAPOGEE_MAX_TOPO_DIST_ACCEL,
       1./0.,                       CHIRON_MAX_TOPO_DIST_ACCEL,
       PHOLUS_MAX_TOPO_DIST_ACCEL,  CERES_MAX_TOPO_DIST_ACCEL,
       PALLAS_MAX_TOPO_DIST_ACCEL,  JUNO_MAX_TOPO_DIST_ACCEL,
       VESTA_MAX_TOPO_DIST_ACCEL,   INTPAPOG_MAX_TOPO_DIST_ACCEL,
       INTPPERG_MAX_TOPO_DIST_ACCEL};
  // Minimum topocentric accelerations in the distance to the earth:
  static final double[] minTopoDistAccel = new double[]
      {SURYA_MIN_TOPO_DIST_ACCEL,   CHANDRA_MIN_TOPO_DIST_ACCEL,
       BUDHA_MIN_TOPO_DIST_ACCEL,   SHUKRA_MIN_TOPO_DIST_ACCEL,
       MANGALA_MIN_TOPO_DIST_ACCEL, GURU_MIN_TOPO_DIST_ACCEL,
       SHANI_MIN_TOPO_DIST_ACCEL,   URANUS_MIN_TOPO_DIST_ACCEL,
       NEPTUNE_MIN_TOPO_DIST_ACCEL, PLUTO_MIN_TOPO_DIST_ACCEL,
       MNODE_MIN_TOPO_DIST_ACCEL,   TNODE_MIN_TOPO_DIST_ACCEL,
       MAPOGEE_MIN_TOPO_DIST_ACCEL, OAPOGEE_MIN_TOPO_DIST_ACCEL,
       1./0.,                       CHIRON_MIN_TOPO_DIST_ACCEL,
       PHOLUS_MIN_TOPO_DIST_ACCEL,  CERES_MIN_TOPO_DIST_ACCEL,
       PALLAS_MIN_TOPO_DIST_ACCEL,  JUNO_MIN_TOPO_DIST_ACCEL,
       VESTA_MIN_TOPO_DIST_ACCEL,   INTPAPOG_MIN_TOPO_DIST_ACCEL,
       INTPPERG_MIN_TOPO_DIST_ACCEL};

  // Maximum accelerations in the distance to the sun:
  static final double[] maxHelioDistAccel = new double[]
      {SURYA_MAX_HELIO_DIST_ACCEL,   CHANDRA_MAX_HELIO_DIST_ACCEL,
       BUDHA_MAX_HELIO_DIST_ACCEL,   SHUKRA_MAX_HELIO_DIST_ACCEL,
       MANGALA_MAX_HELIO_DIST_ACCEL, GURU_MAX_HELIO_DIST_ACCEL,
       SHANI_MAX_HELIO_DIST_ACCEL,   URANUS_MAX_HELIO_DIST_ACCEL,
       NEPTUNE_MAX_HELIO_DIST_ACCEL, PLUTO_MAX_HELIO_DIST_ACCEL,
       MNODE_MAX_HELIO_DIST_ACCEL,   TNODE_MAX_HELIO_DIST_ACCEL,
       MAPOGEE_MAX_HELIO_DIST_ACCEL, OAPOGEE_MAX_HELIO_DIST_ACCEL,
       1./0.,                        CHIRON_MAX_HELIO_DIST_ACCEL,
       PHOLUS_MAX_HELIO_DIST_ACCEL,  CERES_MAX_HELIO_DIST_ACCEL,
       PALLAS_MAX_HELIO_DIST_ACCEL,  JUNO_MAX_HELIO_DIST_ACCEL,
       VESTA_MAX_HELIO_DIST_ACCEL,   INTPAPOG_MAX_HELIO_DIST_ACCEL,
       INTPPERG_MAX_HELIO_DIST_ACCEL};
  // Minimum accelerations in the distance to the sun:
  static final double[] minHelioDistAccel = new double[]
      {SURYA_MIN_HELIO_DIST_ACCEL,   CHANDRA_MIN_HELIO_DIST_ACCEL,
       BUDHA_MIN_HELIO_DIST_ACCEL,   SHUKRA_MIN_HELIO_DIST_ACCEL,
       MANGALA_MIN_HELIO_DIST_ACCEL, GURU_MIN_HELIO_DIST_ACCEL,
       SHANI_MIN_HELIO_DIST_ACCEL,   URANUS_MIN_HELIO_DIST_ACCEL,
       NEPTUNE_MIN_HELIO_DIST_ACCEL, PLUTO_MIN_HELIO_DIST_ACCEL,
       MNODE_MIN_HELIO_DIST_ACCEL,   TNODE_MIN_HELIO_DIST_ACCEL,
       MAPOGEE_MIN_HELIO_DIST_ACCEL, OAPOGEE_MIN_HELIO_DIST_ACCEL,
       1./0.,                        CHIRON_MIN_HELIO_DIST_ACCEL,
       PHOLUS_MIN_HELIO_DIST_ACCEL,  CERES_MIN_HELIO_DIST_ACCEL,
       PALLAS_MIN_HELIO_DIST_ACCEL,  JUNO_MIN_HELIO_DIST_ACCEL,
       VESTA_MIN_HELIO_DIST_ACCEL,   INTPAPOG_MIN_HELIO_DIST_ACCEL,
       INTPPERG_MIN_HELIO_DIST_ACCEL};



  // Speeds and accelerations in the rectascension direction
  // Maximum geocentric speeds in the rectascension direction
  static final double[] maxRectSpeed = new double[]
      {SURYA_MAX_RECT_SPEED,   CHANDRA_MAX_RECT_SPEED, BUDHA_MAX_RECT_SPEED,
       SHUKRA_MAX_RECT_SPEED,  MANGALA_MAX_RECT_SPEED, GURU_MAX_RECT_SPEED,
       SHANI_MAX_RECT_SPEED,   URANUS_MAX_RECT_SPEED,  NEPTUNE_MAX_RECT_SPEED,
       PLUTO_MAX_RECT_SPEED,   MNODE_MAX_RECT_SPEED,   TNODE_MAX_RECT_SPEED,
       MAPOGEE_MAX_RECT_SPEED, OAPOGEE_MAX_RECT_SPEED, 1./0.,
       CHIRON_MAX_RECT_SPEED,  PHOLUS_MAX_RECT_SPEED,  CERES_MAX_RECT_SPEED,
       PALLAS_MAX_RECT_SPEED,  JUNO_MAX_RECT_SPEED,    VESTA_MAX_RECT_SPEED,
       INTPAPOG_MAX_RECT_SPEED,INTPPERG_MAX_RECT_SPEED};
  // Minimum geocentric speeds in the rectascension direction
  static final double[] minRectSpeed = new double[]
      {SURYA_MIN_RECT_SPEED,   CHANDRA_MIN_RECT_SPEED, BUDHA_MIN_RECT_SPEED,
       SHUKRA_MIN_RECT_SPEED,  MANGALA_MIN_RECT_SPEED, GURU_MIN_RECT_SPEED,
       SHANI_MIN_RECT_SPEED,   URANUS_MIN_RECT_SPEED,  NEPTUNE_MIN_RECT_SPEED,
       PLUTO_MIN_RECT_SPEED,   MNODE_MIN_RECT_SPEED,   TNODE_MIN_RECT_SPEED,
       MAPOGEE_MIN_RECT_SPEED, OAPOGEE_MIN_RECT_SPEED, 1./0.,
       CHIRON_MIN_RECT_SPEED,  PHOLUS_MIN_RECT_SPEED,  CERES_MIN_RECT_SPEED,
       PALLAS_MIN_RECT_SPEED,  JUNO_MIN_RECT_SPEED,    VESTA_MIN_RECT_SPEED,
       INTPAPOG_MIN_RECT_SPEED,INTPPERG_MIN_RECT_SPEED};

  // Maximum topocentric speeds in the rectascension direction
  static final double[] maxTopoRectSpeed = new double[]
      {SURYA_MAX_TOPO_RECT_SPEED,   CHANDRA_MAX_TOPO_RECT_SPEED,
       BUDHA_MAX_TOPO_RECT_SPEED,   SHUKRA_MAX_TOPO_RECT_SPEED,
       MANGALA_MAX_TOPO_RECT_SPEED, GURU_MAX_TOPO_RECT_SPEED,
       SHANI_MAX_TOPO_RECT_SPEED,   URANUS_MAX_TOPO_RECT_SPEED,
       NEPTUNE_MAX_TOPO_RECT_SPEED, PLUTO_MAX_TOPO_RECT_SPEED,
       MNODE_MAX_TOPO_RECT_SPEED,   TNODE_MAX_TOPO_RECT_SPEED,
       MAPOGEE_MAX_TOPO_RECT_SPEED, OAPOGEE_MAX_TOPO_RECT_SPEED,
       1./0.,                       CHIRON_MAX_TOPO_RECT_SPEED,
       PHOLUS_MAX_TOPO_RECT_SPEED,  CERES_MAX_TOPO_RECT_SPEED,
       PALLAS_MAX_TOPO_RECT_SPEED,  JUNO_MAX_TOPO_RECT_SPEED,
       VESTA_MAX_TOPO_RECT_SPEED,   INTPAPOG_MAX_TOPO_RECT_SPEED,
       INTPPERG_MAX_TOPO_RECT_SPEED};
  // Minimum topocentric speeds in the rectascension direction
  static final double[] minTopoRectSpeed = new double[]
      {SURYA_MIN_TOPO_RECT_SPEED,   CHANDRA_MIN_TOPO_RECT_SPEED,
       BUDHA_MIN_TOPO_RECT_SPEED,   SHUKRA_MIN_TOPO_RECT_SPEED,
       MANGALA_MIN_TOPO_RECT_SPEED, GURU_MIN_TOPO_RECT_SPEED,
       SHANI_MIN_TOPO_RECT_SPEED,   URANUS_MIN_TOPO_RECT_SPEED,
       NEPTUNE_MIN_TOPO_RECT_SPEED, PLUTO_MIN_TOPO_RECT_SPEED,
       MNODE_MIN_TOPO_RECT_SPEED,   TNODE_MIN_TOPO_RECT_SPEED,
       MAPOGEE_MIN_TOPO_RECT_SPEED, OAPOGEE_MIN_TOPO_RECT_SPEED,
       1./0.,                       CHIRON_MIN_TOPO_RECT_SPEED,
       PHOLUS_MIN_TOPO_RECT_SPEED,  CERES_MIN_TOPO_RECT_SPEED,
       PALLAS_MIN_TOPO_RECT_SPEED,  JUNO_MIN_TOPO_RECT_SPEED,
       VESTA_MIN_TOPO_RECT_SPEED,   INTPAPOG_MIN_TOPO_RECT_SPEED,
       INTPPERG_MIN_TOPO_RECT_SPEED};

  // Maximum speeds in the rectascension direction
  static final double[] maxHelioRectSpeed = new double[]
      {SURYA_MAX_HELIO_RECT_SPEED,   CHANDRA_MAX_HELIO_RECT_SPEED,
       BUDHA_MAX_HELIO_RECT_SPEED,   SHUKRA_MAX_HELIO_RECT_SPEED,
       MANGALA_MAX_HELIO_RECT_SPEED, GURU_MAX_HELIO_RECT_SPEED,
       SHANI_MAX_HELIO_RECT_SPEED,   URANUS_MAX_HELIO_RECT_SPEED,
       NEPTUNE_MAX_HELIO_RECT_SPEED, PLUTO_MAX_HELIO_RECT_SPEED,
       MNODE_MAX_HELIO_RECT_SPEED,   TNODE_MAX_HELIO_RECT_SPEED,
       MAPOGEE_MAX_HELIO_RECT_SPEED, OAPOGEE_MAX_HELIO_RECT_SPEED,
       1./0.,                        CHIRON_MAX_HELIO_RECT_SPEED,
       PHOLUS_MAX_HELIO_RECT_SPEED,  CERES_MAX_HELIO_RECT_SPEED,
       PALLAS_MAX_HELIO_RECT_SPEED,  JUNO_MAX_HELIO_RECT_SPEED,
       VESTA_MAX_HELIO_RECT_SPEED,   INTPAPOG_MAX_HELIO_RECT_SPEED,
       INTPPERG_MAX_HELIO_RECT_SPEED};
  // Minimum speeds in the rectascension direction
  static final double[] minHelioRectSpeed = new double[]
      {SURYA_MIN_HELIO_RECT_SPEED,   CHANDRA_MIN_HELIO_RECT_SPEED,
       BUDHA_MIN_HELIO_RECT_SPEED,   SHUKRA_MIN_HELIO_RECT_SPEED,
       MANGALA_MIN_HELIO_RECT_SPEED, GURU_MIN_HELIO_RECT_SPEED,
       SHANI_MIN_HELIO_RECT_SPEED,   URANUS_MIN_HELIO_RECT_SPEED,
       NEPTUNE_MIN_HELIO_RECT_SPEED, PLUTO_MIN_HELIO_RECT_SPEED,
       MNODE_MIN_HELIO_RECT_SPEED,   TNODE_MIN_HELIO_RECT_SPEED,
       MAPOGEE_MIN_HELIO_RECT_SPEED, OAPOGEE_MIN_HELIO_RECT_SPEED,
       1./0.,                        CHIRON_MIN_HELIO_RECT_SPEED,
       PHOLUS_MIN_HELIO_RECT_SPEED,  CERES_MIN_HELIO_RECT_SPEED,
       PALLAS_MIN_HELIO_RECT_SPEED,  JUNO_MIN_HELIO_RECT_SPEED,
       VESTA_MIN_HELIO_RECT_SPEED,   INTPAPOG_MIN_HELIO_RECT_SPEED,
       INTPPERG_MIN_HELIO_RECT_SPEED};

  // Maximum geocentric accelerations in the rectascension direction
  static final double[] maxRectAccel = new double[]
      {SURYA_MAX_RECT_ACCEL,   CHANDRA_MAX_RECT_ACCEL, BUDHA_MAX_RECT_ACCEL,
       SHUKRA_MAX_RECT_ACCEL,  MANGALA_MAX_RECT_ACCEL, GURU_MAX_RECT_ACCEL,
       SHANI_MAX_RECT_ACCEL,   URANUS_MAX_RECT_ACCEL,  NEPTUNE_MAX_RECT_ACCEL,
       PLUTO_MAX_RECT_ACCEL,   MNODE_MAX_RECT_ACCEL,   TNODE_MAX_RECT_ACCEL,
       MAPOGEE_MAX_RECT_ACCEL, OAPOGEE_MAX_RECT_ACCEL, 1./0.,
       CHIRON_MAX_RECT_ACCEL,  PHOLUS_MAX_RECT_ACCEL,  CERES_MAX_RECT_ACCEL,
       PALLAS_MAX_RECT_ACCEL,  JUNO_MAX_RECT_ACCEL,    VESTA_MAX_RECT_ACCEL,
       INTPAPOG_MAX_RECT_ACCEL,INTPPERG_MAX_RECT_ACCEL};
  // Minimum geocentric accelerations in the rectascension direction
  static final double[] minRectAccel = new double[]
      {SURYA_MIN_RECT_ACCEL,   CHANDRA_MIN_RECT_ACCEL, BUDHA_MIN_RECT_ACCEL,
       SHUKRA_MIN_RECT_ACCEL,  MANGALA_MIN_RECT_ACCEL, GURU_MIN_RECT_ACCEL,
       SHANI_MIN_RECT_ACCEL,   URANUS_MIN_RECT_ACCEL,  NEPTUNE_MIN_RECT_ACCEL,
       PLUTO_MIN_RECT_ACCEL,   MNODE_MIN_RECT_ACCEL,   TNODE_MIN_RECT_ACCEL,
       MAPOGEE_MIN_RECT_ACCEL, OAPOGEE_MIN_RECT_ACCEL, 1./0.,
       CHIRON_MIN_RECT_ACCEL,  PHOLUS_MIN_RECT_ACCEL,  CERES_MIN_RECT_ACCEL,
       PALLAS_MIN_RECT_ACCEL,  JUNO_MIN_RECT_ACCEL,    VESTA_MIN_RECT_ACCEL,
       INTPAPOG_MIN_RECT_ACCEL,INTPPERG_MIN_RECT_ACCEL};

  // Maximum topocentric accelerations in the rectascension direction
  static final double[] maxTopoRectAccel = new double[]
      {SURYA_MAX_TOPO_RECT_ACCEL,   CHANDRA_MAX_TOPO_RECT_ACCEL,
       BUDHA_MAX_TOPO_RECT_ACCEL,   SHUKRA_MAX_TOPO_RECT_ACCEL,
       MANGALA_MAX_TOPO_RECT_ACCEL, GURU_MAX_TOPO_RECT_ACCEL,
       SHANI_MAX_TOPO_RECT_ACCEL,   URANUS_MAX_TOPO_RECT_ACCEL,
       NEPTUNE_MAX_TOPO_RECT_ACCEL, PLUTO_MAX_TOPO_RECT_ACCEL,
       MNODE_MAX_TOPO_RECT_ACCEL,   TNODE_MAX_TOPO_RECT_ACCEL,
       MAPOGEE_MAX_TOPO_RECT_ACCEL, OAPOGEE_MAX_TOPO_RECT_ACCEL,
       1./0.,                       CHIRON_MAX_TOPO_RECT_ACCEL,
       PHOLUS_MAX_TOPO_RECT_ACCEL,  CERES_MAX_TOPO_RECT_ACCEL,
       PALLAS_MAX_TOPO_RECT_ACCEL,  JUNO_MAX_TOPO_RECT_ACCEL,
       VESTA_MAX_TOPO_RECT_ACCEL,   INTPAPOG_MAX_TOPO_RECT_ACCEL,
       INTPPERG_MAX_TOPO_RECT_ACCEL};
  // Minimum topocentric accelerations in the rectascension direction
  static final double[] minTopoRectAccel = new double[]
      {SURYA_MIN_TOPO_RECT_ACCEL,   CHANDRA_MIN_TOPO_RECT_ACCEL,
       BUDHA_MIN_TOPO_RECT_ACCEL,   SHUKRA_MIN_TOPO_RECT_ACCEL,
       MANGALA_MIN_TOPO_RECT_ACCEL, GURU_MIN_TOPO_RECT_ACCEL,
       SHANI_MIN_TOPO_RECT_ACCEL,   URANUS_MIN_TOPO_RECT_ACCEL,
       NEPTUNE_MIN_TOPO_RECT_ACCEL, PLUTO_MIN_TOPO_RECT_ACCEL,
       MNODE_MIN_TOPO_RECT_ACCEL,   TNODE_MIN_TOPO_RECT_ACCEL,
       MAPOGEE_MIN_TOPO_RECT_ACCEL, OAPOGEE_MIN_TOPO_RECT_ACCEL,
       1./0.,                       CHIRON_MIN_TOPO_RECT_ACCEL,
       PHOLUS_MIN_TOPO_RECT_ACCEL,  CERES_MIN_TOPO_RECT_ACCEL,
       PALLAS_MIN_TOPO_RECT_ACCEL,  JUNO_MIN_TOPO_RECT_ACCEL,
       VESTA_MIN_TOPO_RECT_ACCEL,   INTPAPOG_MIN_TOPO_RECT_ACCEL,
       INTPPERG_MIN_TOPO_RECT_ACCEL};

  // Maximum heliocentric accelerations in the rectascension direction
  static final double[] maxHelioRectAccel = new double[]
      {SURYA_MAX_HELIO_RECT_ACCEL,   CHANDRA_MAX_HELIO_RECT_ACCEL,
       BUDHA_MAX_HELIO_RECT_ACCEL,   SHUKRA_MAX_HELIO_RECT_ACCEL,
       MANGALA_MAX_HELIO_RECT_ACCEL, GURU_MAX_HELIO_RECT_ACCEL,
       SHANI_MAX_HELIO_RECT_ACCEL,   URANUS_MAX_HELIO_RECT_ACCEL,
       NEPTUNE_MAX_HELIO_RECT_ACCEL, PLUTO_MAX_HELIO_RECT_ACCEL,
       MNODE_MAX_HELIO_RECT_ACCEL,   TNODE_MAX_HELIO_RECT_ACCEL,
       MAPOGEE_MAX_HELIO_RECT_ACCEL, OAPOGEE_MAX_HELIO_RECT_ACCEL,
       1./0.,                        CHIRON_MAX_HELIO_RECT_ACCEL,
       PHOLUS_MAX_HELIO_RECT_ACCEL,  CERES_MAX_HELIO_RECT_ACCEL,
       PALLAS_MAX_HELIO_RECT_ACCEL,  JUNO_MAX_HELIO_RECT_ACCEL,
       VESTA_MAX_HELIO_RECT_ACCEL,   INTPAPOG_MAX_HELIO_RECT_ACCEL,
       INTPPERG_MAX_HELIO_RECT_ACCEL};
  // Minimum heliocentric accelerations in the rectascension direction
  static final double[] minHelioRectAccel = new double[]
      {SURYA_MIN_HELIO_RECT_ACCEL,   CHANDRA_MIN_HELIO_RECT_ACCEL,
       BUDHA_MIN_HELIO_RECT_ACCEL,   SHUKRA_MIN_HELIO_RECT_ACCEL,
       MANGALA_MIN_HELIO_RECT_ACCEL, GURU_MIN_HELIO_RECT_ACCEL,
       SHANI_MIN_HELIO_RECT_ACCEL,   URANUS_MIN_HELIO_RECT_ACCEL,
       NEPTUNE_MIN_HELIO_RECT_ACCEL, PLUTO_MIN_HELIO_RECT_ACCEL,
       MNODE_MIN_HELIO_RECT_ACCEL,   TNODE_MIN_HELIO_RECT_ACCEL,
       MAPOGEE_MIN_HELIO_RECT_ACCEL, OAPOGEE_MIN_HELIO_RECT_ACCEL,
       1./0.,                        CHIRON_MIN_HELIO_RECT_ACCEL,
       PHOLUS_MIN_HELIO_RECT_ACCEL,  CERES_MIN_HELIO_RECT_ACCEL,
       PALLAS_MIN_HELIO_RECT_ACCEL,  JUNO_MIN_HELIO_RECT_ACCEL,
       VESTA_MIN_HELIO_RECT_ACCEL,   INTPAPOG_MIN_HELIO_RECT_ACCEL,
       INTPPERG_MIN_HELIO_RECT_ACCEL};



  // Speeds and accelerations in the declination direction
  // Maximum geocentric speeds in the declination direction
  static final double[] maxDeclSpeed = new double[]
      {SURYA_MAX_DECL_SPEED,   CHANDRA_MAX_DECL_SPEED, BUDHA_MAX_DECL_SPEED,
       SHUKRA_MAX_DECL_SPEED,  MANGALA_MAX_DECL_SPEED, GURU_MAX_DECL_SPEED,
       SHANI_MAX_DECL_SPEED,   URANUS_MAX_DECL_SPEED,  NEPTUNE_MAX_DECL_SPEED,
       PLUTO_MAX_DECL_SPEED,   MNODE_MAX_DECL_SPEED,   TNODE_MAX_DECL_SPEED,
       MAPOGEE_MAX_DECL_SPEED, OAPOGEE_MAX_DECL_SPEED, 1./0.,
       CHIRON_MAX_DECL_SPEED,  PHOLUS_MAX_DECL_SPEED,  CERES_MAX_DECL_SPEED,
       PALLAS_MAX_DECL_SPEED,  JUNO_MAX_DECL_SPEED,    VESTA_MAX_DECL_SPEED,
       INTPAPOG_MAX_DECL_SPEED,INTPPERG_MAX_DECL_SPEED};
  // Minimum geocentric speeds in the declination direction
  static final double[] minDeclSpeed = new double[]
      {SURYA_MIN_DECL_SPEED,   CHANDRA_MIN_DECL_SPEED, BUDHA_MIN_DECL_SPEED,
       SHUKRA_MIN_DECL_SPEED,  MANGALA_MIN_DECL_SPEED, GURU_MIN_DECL_SPEED,
       SHANI_MIN_DECL_SPEED,   URANUS_MIN_DECL_SPEED,  NEPTUNE_MIN_DECL_SPEED,
       PLUTO_MIN_DECL_SPEED,   MNODE_MIN_DECL_SPEED,   TNODE_MIN_DECL_SPEED,
       MAPOGEE_MIN_DECL_SPEED, OAPOGEE_MIN_DECL_SPEED, 1./0.,
       CHIRON_MIN_DECL_SPEED,  PHOLUS_MIN_DECL_SPEED,  CERES_MIN_DECL_SPEED,
       PALLAS_MIN_DECL_SPEED,  JUNO_MIN_DECL_SPEED,    VESTA_MIN_DECL_SPEED,
       INTPAPOG_MIN_DECL_SPEED,INTPPERG_MIN_DECL_SPEED};

  // Maximum topocentric speeds in the declination direction
  static final double[] maxTopoDeclSpeed = new double[]
      {SURYA_MAX_TOPO_DECL_SPEED,   CHANDRA_MAX_TOPO_DECL_SPEED,
       BUDHA_MAX_TOPO_DECL_SPEED,   SHUKRA_MAX_TOPO_DECL_SPEED,
       MANGALA_MAX_TOPO_DECL_SPEED, GURU_MAX_TOPO_DECL_SPEED,
       SHANI_MAX_TOPO_DECL_SPEED,   URANUS_MAX_TOPO_DECL_SPEED,
       NEPTUNE_MAX_TOPO_DECL_SPEED, PLUTO_MAX_TOPO_DECL_SPEED,
       MNODE_MAX_TOPO_DECL_SPEED,   TNODE_MAX_TOPO_DECL_SPEED,
       MAPOGEE_MAX_TOPO_DECL_SPEED, OAPOGEE_MAX_TOPO_DECL_SPEED,
       1./0.,                       CHIRON_MAX_TOPO_DECL_SPEED,
       PHOLUS_MAX_TOPO_DECL_SPEED,  CERES_MAX_TOPO_DECL_SPEED,
       PALLAS_MAX_TOPO_DECL_SPEED,  JUNO_MAX_TOPO_DECL_SPEED,
       VESTA_MAX_TOPO_DECL_SPEED,   INTPAPOG_MAX_TOPO_DECL_SPEED,
       INTPPERG_MAX_TOPO_DECL_SPEED};
  // Minimum topocentric speeds in the declination direction
  static final double[] minTopoDeclSpeed = new double[]
      {SURYA_MIN_TOPO_DECL_SPEED,   CHANDRA_MIN_TOPO_DECL_SPEED,
       BUDHA_MIN_TOPO_DECL_SPEED,   SHUKRA_MIN_TOPO_DECL_SPEED,
       MANGALA_MIN_TOPO_DECL_SPEED, GURU_MIN_TOPO_DECL_SPEED,
       SHANI_MIN_TOPO_DECL_SPEED,   URANUS_MIN_TOPO_DECL_SPEED,
       NEPTUNE_MIN_TOPO_DECL_SPEED, PLUTO_MIN_TOPO_DECL_SPEED,
       MNODE_MIN_TOPO_DECL_SPEED,   TNODE_MIN_TOPO_DECL_SPEED,
       MAPOGEE_MIN_TOPO_DECL_SPEED, OAPOGEE_MIN_TOPO_DECL_SPEED,
       1./0.,                       CHIRON_MIN_TOPO_DECL_SPEED,
       PHOLUS_MIN_TOPO_DECL_SPEED,  CERES_MIN_TOPO_DECL_SPEED,
       PALLAS_MIN_TOPO_DECL_SPEED,  JUNO_MIN_TOPO_DECL_SPEED,
       VESTA_MIN_TOPO_DECL_SPEED,   INTPAPOG_MIN_TOPO_DECL_SPEED,
       INTPPERG_MIN_TOPO_DECL_SPEED};

  // Maximum speeds in the declination direction
  static final double[] maxHelioDeclSpeed = new double[]
      {SURYA_MAX_HELIO_DECL_SPEED,   CHANDRA_MAX_HELIO_DECL_SPEED,
       BUDHA_MAX_HELIO_DECL_SPEED,   SHUKRA_MAX_HELIO_DECL_SPEED,
       MANGALA_MAX_HELIO_DECL_SPEED, GURU_MAX_HELIO_DECL_SPEED,
       SHANI_MAX_HELIO_DECL_SPEED,   URANUS_MAX_HELIO_DECL_SPEED,
       NEPTUNE_MAX_HELIO_DECL_SPEED, PLUTO_MAX_HELIO_DECL_SPEED,
       MNODE_MAX_HELIO_DECL_SPEED,   TNODE_MAX_HELIO_DECL_SPEED,
       MAPOGEE_MAX_HELIO_DECL_SPEED, OAPOGEE_MAX_HELIO_DECL_SPEED,
       1./0.,                        CHIRON_MAX_HELIO_DECL_SPEED,
       PHOLUS_MAX_HELIO_DECL_SPEED,  CERES_MAX_HELIO_DECL_SPEED,
       PALLAS_MAX_HELIO_DECL_SPEED,  JUNO_MAX_HELIO_DECL_SPEED,
       VESTA_MAX_HELIO_DECL_SPEED,   INTPAPOG_MAX_HELIO_DECL_SPEED,
       INTPPERG_MAX_HELIO_DECL_SPEED};
  // Minimum speeds in the declination direction
  static final double[] minHelioDeclSpeed = new double[]
      {SURYA_MIN_HELIO_DECL_SPEED,   CHANDRA_MIN_HELIO_DECL_SPEED,
       BUDHA_MIN_HELIO_DECL_SPEED,   SHUKRA_MIN_HELIO_DECL_SPEED,
       MANGALA_MIN_HELIO_DECL_SPEED, GURU_MIN_HELIO_DECL_SPEED,
       SHANI_MIN_HELIO_DECL_SPEED,   URANUS_MIN_HELIO_DECL_SPEED,
       NEPTUNE_MIN_HELIO_DECL_SPEED, PLUTO_MIN_HELIO_DECL_SPEED,
       MNODE_MIN_HELIO_DECL_SPEED,   TNODE_MIN_HELIO_DECL_SPEED,
       MAPOGEE_MIN_HELIO_DECL_SPEED, OAPOGEE_MIN_HELIO_DECL_SPEED,
       1./0.,                        CHIRON_MIN_HELIO_DECL_SPEED,
       PHOLUS_MIN_HELIO_DECL_SPEED,  CERES_MIN_HELIO_DECL_SPEED,
       PALLAS_MIN_HELIO_DECL_SPEED,  JUNO_MIN_HELIO_DECL_SPEED,
       VESTA_MIN_HELIO_DECL_SPEED,   INTPAPOG_MIN_HELIO_DECL_SPEED,
       INTPPERG_MIN_HELIO_DECL_SPEED};

  // Maximum geocentric accelerations in the declination direction
  static final double[] maxDeclAccel = new double[]
      {SURYA_MAX_DECL_ACCEL,   CHANDRA_MAX_DECL_ACCEL, BUDHA_MAX_DECL_ACCEL,
       SHUKRA_MAX_DECL_ACCEL,  MANGALA_MAX_DECL_ACCEL, GURU_MAX_DECL_ACCEL,
       SHANI_MAX_DECL_ACCEL,   URANUS_MAX_DECL_ACCEL,  NEPTUNE_MAX_DECL_ACCEL,
       PLUTO_MAX_DECL_ACCEL,   MNODE_MAX_DECL_ACCEL,   TNODE_MAX_DECL_ACCEL,
       MAPOGEE_MAX_DECL_ACCEL, OAPOGEE_MAX_DECL_ACCEL, 1./0.,
       CHIRON_MAX_DECL_ACCEL,  PHOLUS_MAX_DECL_ACCEL,  CERES_MAX_DECL_ACCEL,
       PALLAS_MAX_DECL_ACCEL,  JUNO_MAX_DECL_ACCEL,    VESTA_MAX_DECL_ACCEL,
       INTPAPOG_MAX_DECL_ACCEL,INTPPERG_MAX_DECL_ACCEL};
  // Minimum geocentric accelerations in the declination direction
  static final double[] minDeclAccel = new double[]
      {SURYA_MIN_DECL_ACCEL,   CHANDRA_MIN_DECL_ACCEL, BUDHA_MIN_DECL_ACCEL,
       SHUKRA_MIN_DECL_ACCEL,  MANGALA_MIN_DECL_ACCEL, GURU_MIN_DECL_ACCEL,
       SHANI_MIN_DECL_ACCEL,   URANUS_MIN_DECL_ACCEL,  NEPTUNE_MIN_DECL_ACCEL,
       PLUTO_MIN_DECL_ACCEL,   MNODE_MIN_DECL_ACCEL,   TNODE_MIN_DECL_ACCEL,
       MAPOGEE_MIN_DECL_ACCEL, OAPOGEE_MIN_DECL_ACCEL, 1./0.,
       CHIRON_MIN_DECL_ACCEL,  PHOLUS_MIN_DECL_ACCEL,  CERES_MIN_DECL_ACCEL,
       PALLAS_MIN_DECL_ACCEL,  JUNO_MIN_DECL_ACCEL,    VESTA_MIN_DECL_ACCEL,
       INTPAPOG_MIN_DECL_ACCEL,INTPPERG_MIN_DECL_ACCEL};

  // Maximum topocentric accelerations in the declination direction
  static final double[] maxTopoDeclAccel = new double[]
      {SURYA_MAX_TOPO_DECL_ACCEL,   CHANDRA_MAX_TOPO_DECL_ACCEL,
       BUDHA_MAX_TOPO_DECL_ACCEL,   SHUKRA_MAX_TOPO_DECL_ACCEL,
       MANGALA_MAX_TOPO_DECL_ACCEL, GURU_MAX_TOPO_DECL_ACCEL,
       SHANI_MAX_TOPO_DECL_ACCEL,   URANUS_MAX_TOPO_DECL_ACCEL,
       NEPTUNE_MAX_TOPO_DECL_ACCEL, PLUTO_MAX_TOPO_DECL_ACCEL,
       MNODE_MAX_TOPO_DECL_ACCEL,   TNODE_MAX_TOPO_DECL_ACCEL,
       MAPOGEE_MAX_TOPO_DECL_ACCEL, OAPOGEE_MAX_TOPO_DECL_ACCEL,
       1./0.,                       CHIRON_MAX_TOPO_DECL_ACCEL,
       PHOLUS_MAX_TOPO_DECL_ACCEL,  CERES_MAX_TOPO_DECL_ACCEL,
       PALLAS_MAX_TOPO_DECL_ACCEL,  JUNO_MAX_TOPO_DECL_ACCEL,
       VESTA_MAX_TOPO_DECL_ACCEL,   INTPAPOG_MAX_TOPO_DECL_ACCEL,
       INTPPERG_MAX_TOPO_DECL_ACCEL};
  // Minimum topocentric accelerations in the declination direction
  static final double[] minTopoDeclAccel = new double[]
      {SURYA_MIN_TOPO_DECL_ACCEL,   CHANDRA_MIN_TOPO_DECL_ACCEL,
       BUDHA_MIN_TOPO_DECL_ACCEL,   SHUKRA_MIN_TOPO_DECL_ACCEL,
       MANGALA_MIN_TOPO_DECL_ACCEL, GURU_MIN_TOPO_DECL_ACCEL,
       SHANI_MIN_TOPO_DECL_ACCEL,   URANUS_MIN_TOPO_DECL_ACCEL,
       NEPTUNE_MIN_TOPO_DECL_ACCEL, PLUTO_MIN_TOPO_DECL_ACCEL,
       MNODE_MIN_TOPO_DECL_ACCEL,   TNODE_MIN_TOPO_DECL_ACCEL,
       MAPOGEE_MIN_TOPO_DECL_ACCEL, OAPOGEE_MIN_TOPO_DECL_ACCEL,
       1./0.,                       CHIRON_MIN_TOPO_DECL_ACCEL,
       PHOLUS_MIN_TOPO_DECL_ACCEL,  CERES_MIN_TOPO_DECL_ACCEL,
       PALLAS_MIN_TOPO_DECL_ACCEL,  JUNO_MIN_TOPO_DECL_ACCEL,
       VESTA_MIN_TOPO_DECL_ACCEL,   INTPAPOG_MIN_TOPO_DECL_ACCEL,
       INTPPERG_MIN_TOPO_DECL_ACCEL};

  // Maximum heliocentric accelerations in the declination direction
  static final double[] maxHelioDeclAccel = new double[]
      {SURYA_MAX_HELIO_DECL_ACCEL,   CHANDRA_MAX_HELIO_DECL_ACCEL,
       BUDHA_MAX_HELIO_DECL_ACCEL,   SHUKRA_MAX_HELIO_DECL_ACCEL,
       MANGALA_MAX_HELIO_DECL_ACCEL, GURU_MAX_HELIO_DECL_ACCEL,
       SHANI_MAX_HELIO_DECL_ACCEL,   URANUS_MAX_HELIO_DECL_ACCEL,
       NEPTUNE_MAX_HELIO_DECL_ACCEL, PLUTO_MAX_HELIO_DECL_ACCEL,
       MNODE_MAX_HELIO_DECL_ACCEL,   TNODE_MAX_HELIO_DECL_ACCEL,
       MAPOGEE_MAX_HELIO_DECL_ACCEL, OAPOGEE_MAX_HELIO_DECL_ACCEL,
       1./0.,                        CHIRON_MAX_HELIO_DECL_ACCEL,
       PHOLUS_MAX_HELIO_DECL_ACCEL,  CERES_MAX_HELIO_DECL_ACCEL,
       PALLAS_MAX_HELIO_DECL_ACCEL,  JUNO_MAX_HELIO_DECL_ACCEL,
       VESTA_MAX_HELIO_DECL_ACCEL,   INTPAPOG_MAX_HELIO_DECL_ACCEL,
       INTPPERG_MAX_HELIO_DECL_ACCEL};
  // Minimum heliocentric accelerations in the declination direction
  static final double[] minHelioDeclAccel = new double[]
      {SURYA_MIN_HELIO_DECL_ACCEL,   CHANDRA_MIN_HELIO_DECL_ACCEL,
       BUDHA_MIN_HELIO_DECL_ACCEL,   SHUKRA_MIN_HELIO_DECL_ACCEL,
       MANGALA_MIN_HELIO_DECL_ACCEL, GURU_MIN_HELIO_DECL_ACCEL,
       SHANI_MIN_HELIO_DECL_ACCEL,   URANUS_MIN_HELIO_DECL_ACCEL,
       NEPTUNE_MIN_HELIO_DECL_ACCEL, PLUTO_MIN_HELIO_DECL_ACCEL,
       MNODE_MIN_HELIO_DECL_ACCEL,   TNODE_MIN_HELIO_DECL_ACCEL,
       MAPOGEE_MIN_HELIO_DECL_ACCEL, OAPOGEE_MIN_HELIO_DECL_ACCEL,
       1./0.,                        CHIRON_MIN_HELIO_DECL_ACCEL,
       PHOLUS_MIN_HELIO_DECL_ACCEL,  CERES_MIN_HELIO_DECL_ACCEL,
       PALLAS_MIN_HELIO_DECL_ACCEL,  JUNO_MIN_HELIO_DECL_ACCEL,
       VESTA_MIN_HELIO_DECL_ACCEL,   INTPAPOG_MIN_HELIO_DECL_ACCEL,
       INTPPERG_MIN_HELIO_DECL_ACCEL};

//////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////

}
