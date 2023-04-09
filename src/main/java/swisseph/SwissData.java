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

final class SwissData {
  /**
  * The character to be used as the degree character. Only textmode
  * applications (and probably awt applications in Java 1.1 and below?)
  * will require to differentiate between different characters, awt&nbsp;/
  * swing components of Java 1.2 and above will use the unicode encoding
  * always!
  */

  public static final String[] ayanamsa_name = {
          "Fagan/Bradley",                    /*  0 SE_SIDM_FAGAN_BRADLEY */
          "Lahiri",                           /*  1 SE_SIDM_LAHIRI */
          "De Luce",                          /*  2 SE_SIDM_DELUCE */
          "Raman",                            /*  3 SE_SIDM_RAMAN */
          "Usha/Shashi",                      /*  4 SE_SIDM_USHASHASHI */
          "Krishnamurti",                     /*  5 SE_SIDM_KRISHNAMURTI */
          "Djwhal Khul",                      /*  6 SE_SIDM_DJWHAL_KHUL */
          "Yukteshwar",                       /*  7 SE_SIDM_YUKTESHWAR */
          "J.N. Bhasin",                      /*  8 SE_SIDM_JN_BHASIN */
          "Babylonian/Kugler 1",              /*  9 SE_SIDM_BABYL_KUGLER1 */
          "Babylonian/Kugler 2",              /* 10 SE_SIDM_BABYL_KUGLER2 */
          "Babylonian/Kugler 3",              /* 11 SE_SIDM_BABYL_KUGLER3 */
          "Babylonian/Huber",                 /* 12 SE_SIDM_BABYL_HUBER */
          "Babylonian/Eta Piscium",           /* 13 SE_SIDM_BABYL_ETPSC */
          "Babylonian/Aldebaran = 15 Tau",    /* 14 SE_SIDM_ALDEBARAN_15TAU */
          "Hipparchos",                       /* 15 SE_SIDM_HIPPARCHOS */
          "Sassanian",                        /* 16 SE_SIDM_SASSANIAN */
          "Galact. Center = 0 Sag",           /* 17 SE_SIDM_GALCENT_0SAG */
          "J2000",                            /* 18 SE_SIDM_J2000 */
          "J1900",                            /* 19 SE_SIDM_J1900 */
          "B1950",                            /* 20 SE_SIDM_B1950 */
          "Suryasiddhanta",                   /* 21 SE_SIDM_SURYASIDDHANTA */
          "Suryasiddhanta, mean Sun",         /* 22 SE_SIDM_SURYASIDDHANTA_MSUN */
          "Aryabhata",                        /* 23 SE_SIDM_ARYABHATA */
          "Aryabhata, mean Sun",              /* 24 SE_SIDM_ARYABHATA_MSUN */
          "SS Revati",                        /* 25 SE_SIDM_SS_REVATI */
          "SS Citra",                         /* 26 SE_SIDM_SS_CITRA */
          "True Citra",                       /* 27 SE_SIDM_TRUE_CITRA */
          "True Revati",                      /* 28 SE_SIDM_TRUE_REVATI */
          "True Pushya (PVRN Rao)",           /* 29 SE_SIDM_TRUE_PUSHYA */
          "Galactic Center (Gil Brand)",      /* 30 SE_SIDM_GALCENT_RGILBRAND */
          "Galactic Equator (IAU1958)",       /* 31 SE_SIDM_GALEQU_IAU1958 */
          "Galactic Equator",                 /* 32 SE_SIDM_GALEQU_TRUE */
          "Galactic Equator mid-Mula",        /* 33 SE_SIDM_GALEQU_MULA */
          "Skydram (Mardyks)",                /* 34 SE_SIDM_GALALIGN_MARDYKS */
          "True Mula (Chandra Hari)",         /* 35 SE_SIDM_TRUE_MULA */
          "Dhruva/Gal.Center/Mula (Wilhelm)", /* 36 SE_SIDM_GALCENT_MULA_WILHELM */
          "Aryabhata 522",                    /* 37 SE_SIDM_ARYABHATA_522 */
          "Babylonian/Britton",               /* 38 SE_SIDM_BABYL_BRITTON */
          "\"Vedic\"/Sheoran",                /* 39 SE_SIDM_TRUE_SHEORAN */
          "Cochrane (Gal.Center = 0 Cap)",    /* 40 SE_SIDM_GALCENT_COCHRANE */
          "Galactic Equator (Fiorenza)",      /* 41 SE_SIDM_GALEQU_FIORENZA */
          "Vettius Valens",                   /* 42 SE_SIDM_VALENS_MOON */
          "Lahiri 1940",                      /* 43 SE_SIDM_LAHIRI_1940 */
          "Lahiri VP285",                     /* 44 SE_SIDM_LAHIRI_VP285 */
          "Krishnamurti-Senthilathiban",      /* 45 SE_SIDM_KRISHNAMURTI_VP291 */
          "Lahiri ICRC",                      /* 46 SE_SIDM_LAHIRI_ICRC */
          /*"Manjula/Laghumanasa",*/
  };

//////////////////////////////////////////////////////////////////////////////
// sweodef.h: ////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
  public static final int AS_MAXCH=256; // used for string declarations,
                                        // allowing 255 char+\0

  static final double DEGTORAD=0.0174532925199433;
  static final double RADTODEG=57.2957795130823;

  static final int DEG=360000;  // degree expressed in centiseconds
  static final int DEG7_30=2700000;	// 7.5 degrees
  static final int DEG15=15 * DEG;
  static final int DEG24=24 * DEG;
  static final int DEG30=30 * DEG;
  static final int DEG60=60 * DEG;
  static final int DEG90=90 * DEG;
  static final int DEG120=120 * DEG;
  static final int DEG150=150 * DEG;
  static final int DEG180=180 * DEG;
  static final int DEG270=270 * DEG;
  static final int DEG360=360 * DEG;

  static final double CSTORAD=4.84813681109536E-08; // centisec to rad:
                                                    // pi / 180 /3600/100
  static final double RADTOCS=2.06264806247096E+07; // rad to centisec
                                                    // 180*3600*100/pi

  static final double CS2DEG=1.0/360000.0;	     // centisec to degree

  static final String BFILE_R_ACCESS="r";  // open binary file for reading
  static final String BFILE_RW_ACCESS="r+";// open binary file for writing and reading
  static final String BFILE_W_CREATE="w";  // create/open binary file for write
  static final String BFILE_A_ACCESS="a+"; // create/open binary file for append
  static final String FILE_R_ACCESS="r";   // open text file for reading
  static final String FILE_RW_ACCESS="r+"; // open text file for writing and reading
  static final String FILE_W_CREATE="w";   // create/open text file for write
  static final String FILE_A_ACCESS="a+";  // create/open text file for append
  static final int O_BINARY=0;	           // for open(), not defined in Unix
  static final int OPEN_MODE=0666;         // default file creation mode
  // file.separator may be null with JavaME
  public static final String DIR_GLUE = (System.getProperty("file.separator") == null ? "/" : System.getProperty("file.separator"));              // glue string for directory/file
  public static final String PATH_SEPARATOR=";:"; // semicolon or colon may be used


//////////////////////////////////////////////////////////////////////////////
// swephexp.h: ///////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////

//  static final int SE_MAX_STNAME=20;    // maximum size of fixstar name;
//                                        // the parameter star in swe_fixstar
//					// must allow twice this space for
//				        // the returned star name.
//

  static final int pnoext2int[] = {SwephData.SEI_SUN, SwephData.SEI_MOON,
    SwephData.SEI_MERCURY, SwephData.SEI_VENUS, SwephData.SEI_MARS,
    SwephData.SEI_JUPITER, SwephData.SEI_SATURN, SwephData.SEI_URANUS,
    SwephData.SEI_NEPTUNE, SwephData.SEI_PLUTO, 0, 0, 0, 0, SwephData.SEI_EARTH,
    SwephData.SEI_CHIRON, SwephData.SEI_PHOLUS, SwephData.SEI_CERES,
    SwephData.SEI_PALLAS, SwephData.SEI_JUNO, SwephData.SEI_VESTA, };

  //////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
  boolean ephe_path_is_set=false;	/* ephe_path_is_set = FALSE */
  boolean jpl_file_is_open=false;	/* jpl_file_is_open = FALSE */
  FilePtr fixfp=null;			/* fixfp, fixed stars file pointer */
  java.util.Hashtable fixstarsHash = null;
  String ephepath = SweConst.SE_EPHE_PATH;	/* ephepath, ephemeris path */
  String jplfnam = SweConst.SE_FNAME_DFT;	/* jplfnam, JPL file name, default */
  int jpldenum = 0;			/* jpldenum */
  double eop_tjd_beg;
  double eop_tjd_beg_horizons;
  double eop_tjd_end;
  double eop_tjd_end_add;
  int eop_dpsi_loaded;
  boolean geopos_is_set=false;		/* geopos_is_set, for topocentric */
  boolean ayana_is_set=false;		/* ayana_is_set, ayanamsa is set */
  boolean is_old_starfile=false;	/* is_old_starfile, fixstars.cat is used (default is sefstars.txt) */

  final GenConst gcdat;
  final FileData[] fidat = new FileData[SwephData.SEI_NEPHFILES];
  final PlanData[] pldat = new PlanData[SwephData.SEI_NPLANETS];
  final PlanData[] nddat = new PlanData[SwephData.SEI_NNODE_ETC];
  final SavePositions[] savedat = new SavePositions[SweConst.SE_NPLANETS+1];
  int[] astro_models = new int[SwephData.SEI_NMODELS];
  final Epsilon oec, oec2000;
  final Nut nut, nut2000, nutv;
  final TopoData topd;
  final SidData sidd;
  String astelem;
  double ast_G, ast_H, ast_diam;
  int i_saved_planet_name;
  String saved_planet_name;
  //double dpsi[36525];  /* works for 100 years after 1962 */
  //double deps[36525];
  double[] dpsi;
  double[] deps;

  int timeout;

  /**
  * Constructs a new SwissData object.
  */
  public SwissData() {
    int i;
    for(i=0;i<SwephData.SEI_NEPHFILES;i++){ fidat[i] = new FileData(); }
    gcdat = new GenConst();
    for(i=0;i<SwephData.SEI_NPLANETS;i++){ pldat[i] = new PlanData(); }
    for(i=0;i<SwephData.SEI_NNODE_ETC;i++){ nddat[i] = new PlanData(); }
    for(i=0;i<SweConst.SE_NPLANETS+1;i++){ savedat[i] = new SavePositions(); }
    oec = new Epsilon();
    oec2000 = new Epsilon();
    nut = new Nut();
    nut2000 = new Nut();
    nutv = new Nut();
    topd = new TopoData();
    sidd = new SidData();
  }

}
