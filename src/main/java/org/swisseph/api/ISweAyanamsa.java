/*
* Copyright (C) By the Author
* Author    Yura Krymlov
* Created   2019-11
*/

package org.swisseph.api;

/**
 * @author  Yura Krymlov
 * @version 1.0, 2019-11
 */
public interface ISweAyanamsa extends ISweEnumSequence<ISweAyanamsa> {

    String AY00_CD = "AY00"; // 0 Fagan/Bradley
    String AY01_CD = "AY01"; // 1 Lahiri
    String AY02_CD = "AY02"; // 2 De Luce
    String AY03_CD = "AY03"; // 3 Raman
    String AY04_CD = "AY04"; // 4 Ushashashi
    String AY05_CD = "AY05"; // 5 Krishnamurti
    String AY06_CD = "AY06"; // 6 Djwhal Khool
    String AY07_CD = "AY07"; // 7 Sri Yukteshwar
    String AY08_CD = "AY08"; // 8 JN Bhasin
    String AY09_CD = "AY09"; // 9  Babylonian, Kugler 1
    String AY10_CD = "AY10"; // 10 Babylonian, Kugler 2
    String AY11_CD = "AY11"; // 11 Babylonian, Kugler 3
    String AY12_CD = "AY12"; // 12 Babylonian, Huber
    String AY13_CD = "AY13"; // 13 Babylonian, Mercier
    String AY14_CD = "AY14"; // 14 Aldebaran at 15 degrees Taurus
    String AY15_CD = "AY15"; // 15 Hipparchos
    String AY16_CD = "AY16"; // 16 Sassanian
    String AY17_CD = "AY17"; // 17 0 degrees Sagittarius
    String AY18_CD = "AY18"; // 18 J2000 (Julian day 2451545.0, 2000 January 1.5)
    String AY19_CD = "AY19"; // 19 J1900 (Julian day 2415020.0, 1900 January 0.5)
    String AY20_CD = "AY20"; // 20 B1950 (Julian day 2433282.42345905, 1950 January 0.923)
    String AY21_CD = "AY21"; // 21 Suryasiddhanta
    String AY22_CD = "AY22"; // 22 SURYASIDDHANTA_MSUN
    String AY23_CD = "AY23"; // 23 ARYABHATA
    String AY24_CD = "AY24"; // 24 ARYABHATA_MSUN
    String AY25_CD = "AY25"; // 25 SS, Revati/zePsc at polar long. 359' 50'
    String AY26_CD = "AY26"; // 26 SS, Citra/Spica at polar long. 180'
    String AY27_CD = "AY27"; // 27 True Spica (Spica exactly at 0 Libra)
    String AY28_CD = "AY28"; // 28 True Revati (zeta Psc exactly at 0 Aries)
    String AY29_CD = "AY29"; // 29 True Pushya (delta Cnc exactly a 16 Cancer)
    String AY30_CD = "AY30"; // 30 Galactic Center (Gil Brand)
    String AY31_CD = "AY31"; // 31 Galactic Equator (IAU1958)
    String AY32_CD = "AY32"; // 32 Galactic Equator
    String AY33_CD = "AY33"; // 33 Galactic Equator mid-Mula
    String AY34_CD = "AY34"; // 34 Skydram (Mardyks)
    String AY35_CD = "AY35"; // 35 True Mula (Chandra Hari)
    String AY36_CD = "AY36"; // 36 Dhruva/Gal.Center/Mula (Wilhelm)
    String AY37_CD = "AY37"; // 37 Aryabhata 522
    String AY38_CD = "AY38"; // 38 Babylonian/Britton
    String AY39_CD = "AY39"; // 39 Vedic Sheoran
    String AY40_CD = "AY40"; // 40 Cochrane (Gal.Center = 0 Cap)
    String AY41_CD = "AY41"; // 41 Galactic Equator (Fiorenza)
    String AY42_CD = "AY42"; // 42 Vettius Valens
    String AY43_CD = "AY43"; // 43 Lahiri 1940
    String AY44_CD = "AY44"; // 44 Lahiri VP285
    String AY45_CD = "AY45"; // 45 Krishnamurti-Senthilathiban
    String AY46_CD = "AY46"; // 46 Lahiri ICRC
    String AYUR_CD = "AYUR"; // 255 defined by the user in the additional two par
    String AYN0_CD = "AYN0"; // NONE

    default boolean sidereal() {
        return true;
    }
}
