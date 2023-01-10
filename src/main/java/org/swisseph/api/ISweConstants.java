/*
* Copyright (C) By the Author
* Author    Yura Krymlov
* Created   2019-07
*/

package org.swisseph.api;

/**
 * @author  Yura Krymlov
 * @version 1.1, 2019-07
 */
public interface ISweConstants {
        
    int i0 = 0;
    int i1 = 1;
    int i2 = 2;
    int i3 = 3;
    int i6 = 6;
    int i5 = 5;
    int i7 = 7;
    int i8 = 8;
    int i9 = 9;
    int i10 = 10;
    int i11 = 11;
    int i12 = 12;
    int i30 = 30;
    int i60 = 60;
    int i100 = 100;
    int i360 = 360;
    int i1000 = 1000;

    double d05 = 0.5;
    double d0 = 0.;
    double d1 = 1.;
    double d2 = 2.;
    double d3 = 3.;
    double d4  = 4.;
    double d5  = 5.;
    double d6  = 6.;
    double d7  = 7.;
    double d8  = 8.;
    double d9  = 9.;
    double d10 = 10.;
    double d11 = 11.;
    double d12 = 12.;
    double d15 = 15.;
    double d16 = 16.;
    double d18 = 18.;
    double d20 = 20.;
    double d24 = 24.;
    double d27 = 27.;
    double d30 = 30.;
    double d33 = 33.;
    double d34 = 34.;
    double d40 = 40.;
    double d45 = 45.;
    double d60 = 60.;
    double d81 = 81.;
    double d90 = 90.;
    double d95 = 95.;
    double d100 = 100.;
    double d108 = 108.;
    double d118 = 118.;
    double d120 = 120.;
    double d133 = 133.;
    double d140 = 140.;
    double d150 = 150.;
    double d165 = 165.;
    double d170 = 170.;
    double d177 = 177.;
    double d180 = 180.;
    double d190 = 190.;
    double d195 = 195.;
    double d200 = 200.;
    double d210 = 210.;
    double d213 = 213.;
    double d240 = 240.;
    double d250 = 250.;
    double d270 = 270.;
    double d275 = 275.;
    double d298 = 298.;
    double d300 = 300.;
    double d320 = 320.;
    double d330 = 330.;
    double d342 = 342.;
    double d345 = 345.;
    double d357 = 357.;
    double d360 = 360.;
    double d3600 = 3600.;
    double d86400 = 86400.;
    double d360000 = 360000.;
    double d3600000 = 3600000.;
    
    double dd20 = d1/d3;
    double dd40 = d2/d3;
    
    // round to a minute
    double d05d60 = d05/d60;
    
    // round to a second
    double d05d3600 = d05/d3600;
    
    // round to a millisecond
    double d005d3600 = .005/d3600;

    // MILLIARCSEC
    double d1d3600000 = d1/d3600000;

    double DELTA_D005 = 0.005;
    double DELTA_D00001 = 0.00001;
    double DELTA_D000001 = 0.000001;
    double DELTA_D0000001 = 0.0000001;

    double DSTEP_ROUND = DELTA_D00001 /d3600;
    
    double ARCTIC_CIRCLE_LATITUDE = 66.5;

    double CHAKRA_LENGTH = d360;

    double RASI_LENGTH = d30;
    double RASI_HALF_LENGTH = RASI_LENGTH/d2;
    double D100_RASI_LENGTH = d100/RASI_LENGTH;

    double BHAVA_LENGTH = RASI_LENGTH;
    double D100_BHAVA_LENGTH = d100/BHAVA_LENGTH;

    double VAARA_LENGTH = d24;
    double D100_VAARA_LENGTH = d100/VAARA_LENGTH;
    
    double KARANA_LENGTH = d6;
    double D100_KARANA_LENGTH = d100/KARANA_LENGTH;

    double TITHI_LENGTH = d12;
    double D100_TITHI_LENGTH = d100/TITHI_LENGTH;;

    double NITYA_YOGA_LENGTH = d360/d27;
    double D100_NITYA_YOGA_LENGTH = d100/NITYA_YOGA_LENGTH;
    
    double NAKSHATRA_LENGTH  = d360/d27;
    double NAKSHATRA_PADA_LENGTH = d360/d108;    
    double NAKSHATRA_HALF_LENGTH = NAKSHATRA_LENGTH/d2;
    double NAKSHATRA_PADA_HALF_LENGTH = NAKSHATRA_PADA_LENGTH/d2;

    double D100_NAKSHATRA_LENGTH  = d100/NAKSHATRA_LENGTH;
    double D100_NAKSHATRA_PADA_LENGTH = d100/NAKSHATRA_PADA_LENGTH;

    char CH_ZR = '0';
    char CH_DQ = '\"';
    char CH_SQ = '\'';
    char CH_VS = '|';
    char CH_DT = '.'; // Dot
    char CH_DS = '-'; // Divis
    char CH_CN = ':'; // Colon
    
    char LONG_WEST = 'W';
    char LONG_EAST = 'E';
    char LAT_SOUTH = 'S';
    char LAT_NORTH = 'N';

    String STR_EY = "";
    String STR_WS = " ";
    
    String UTC = "UTC";
    String UTF8 = "UTF-8";
    
    String EPHE_PATH = "ephe";

    String FDTE_PATTERN = "yyyyMMddHHmmss";
}
