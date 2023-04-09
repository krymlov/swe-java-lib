/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2019-07
 */

package org.swisseph.app;


import org.swisseph.api.ISweAyanamsa;
import org.swisseph.api.ISweEnum;
import org.swisseph.api.ISweEnumIterator;

import static swisseph.SweConst.*;

/**
 * @author Yura Krymlov
 * @version 1.1, 2019-07
 */
public enum SweAyanamsa implements ISweAyanamsa {
    FAGAN_BRADLEY {
        @Override public int fid() { return SE_SIDM_FAGAN_BRADLEY; }
        @Override public String code() { return AY00_CD; }
    }, // 0 Fagan/Bradley
    LAHIRI {
        @Override public int fid() { return SE_SIDM_LAHIRI; }
        @Override public String code() { return AY01_CD; }
    }, // 1 Lahiri
    DELUCE {
        @Override public int fid() { return SE_SIDM_DELUCE; }
        @Override public String code() { return AY02_CD; }
    }, // 2 De Luce
    BV_RAMAN {
        @Override public int fid() { return SE_SIDM_RAMAN; }
        @Override public String code() { return AY03_CD; }
    }, // 3 Raman
    USHA_SHASHI {
        @Override public int fid() { return SE_SIDM_USHASHASHI; }
        @Override public String code() { return AY04_CD; }
    }, // 4 Ushashashi
    KRISHNAMURTI {
        @Override public int fid() { return SE_SIDM_KRISHNAMURTI; }
        @Override public String code() { return AY05_CD; }
    }, // 5 Krishnamurti
    DJWHAL_KHOOL {
        @Override public int fid() { return SE_SIDM_DJWHAL_KHUL; }
        @Override public String code() { return AY06_CD; }
    }, // 6 Djwhal Khool
    SHRI_YUKTESHWAR {
        @Override public int fid() { return SE_SIDM_YUKTESHWAR; }
        @Override public String code() { return AY07_CD; }
    }, // 7 Sri Yukteshwar
    JN_BHASIN {
        @Override public int fid() { return SE_SIDM_JN_BHASIN; }
        @Override public String code() { return AY08_CD; }
    }, // 8 JN Bhasin
    BABYL_KUGLER1 {
        @Override public int fid() { return SE_SIDM_BABYL_KUGLER1; }
        @Override public String code() { return AY09_CD; }
    }, // 9  Babylonian, Kugler 1
    BABYL_KUGLER2 {
        @Override public int fid() { return SE_SIDM_BABYL_KUGLER2; }
        @Override public String code() { return AY10_CD; }
    }, // 10 Babylonian, Kugler 2
    BABYL_KUGLER3 {
        @Override public int fid() { return SE_SIDM_BABYL_KUGLER3; }
        @Override public String code() { return AY11_CD; }
    }, // 11 Babylonian, Kugler 3
    BABYL_HUBER {
        @Override public int fid() { return SE_SIDM_BABYL_HUBER; }
        @Override public String code() { return AY12_CD; }
    }, // 12 Babylonian, Huber
    BABYL_MERCIER {
        @Override public int fid() { return SE_SIDM_BABYL_ETPSC; }
        @Override public String code() { return AY13_CD; }
    }, // 13 Babylonian, Mercier
    ALDEBARAN_15TAU  {
        @Override public int fid() { return SE_SIDM_ALDEBARAN_15TAU; }
        @Override public String code() { return AY14_CD; }
    }, // 14 Aldebaran at 15 degrees Taurus
    HIPPARCHOS {
        @Override public int fid() { return SE_SIDM_HIPPARCHOS; }
        @Override public String code() { return AY15_CD; }
    }, // 15 Hipparchos
    SASSANIAN {
        @Override public int fid() { return SE_SIDM_SASSANIAN; }
        @Override public String code() { return AY16_CD; }
    }, // 16 Sassanian
    GALCENT_0SAG {
        @Override public int fid() { return SE_SIDM_GALCENT_0SAG; }
        @Override public String code() { return AY17_CD; }
    }, // 17 0 degrees Sagittarius
    J2000 {
        @Override public int fid() { return SE_SIDM_J2000; }
        @Override public String code() { return AY18_CD; }
    }, // 18 J2000 (Julian day 2451545.0, 2000 January 1.5)
    J1900 {
        @Override public int fid() { return SE_SIDM_J1900; }
        @Override public String code() { return AY19_CD; }
    }, // 19 J1900 (Julian day 2415020.0, 1900 January 0.5)
    B1950 {
        @Override public int fid() { return SE_SIDM_B1950; }
        @Override public String code() { return AY20_CD; }
    }, // 20 B1950 (Julian day 2433282.42345905, 1950 January 0.923)
    SURYA_SIDDHANTA {
        @Override public int fid() { return SE_SIDM_SURYASIDDHANTA; }
        @Override public String code() { return AY21_CD; }
    }, // 21 Suryasiddhanta
    SURYA_SIDDHANTA_MSUN {
        @Override public int fid() { return SE_SIDM_SURYASIDDHANTA_MSUN; }
        @Override public String code() { return AY22_CD; }
    }, // 22 SURYASIDDHANTA_MSUN
    ARYABHATA {
        @Override public int fid() { return SE_SIDM_ARYABHATA; }
        @Override public String code() { return AY23_CD; }
    }, // 23 ARYABHATA
    ARYABHATA_MSUN {
        @Override public int fid() { return SE_SIDM_ARYABHATA_MSUN; }
        @Override public String code() { return AY24_CD; }
    }, // 24 ARYABHATA_MSUN
    SS_REVATI {
        @Override public int fid() { return SE_SIDM_SS_REVATI; }
        @Override public String code() { return AY25_CD; }
    }, // 25 SS, Revati/zePsc at polar long. 359' 50'
    SS_CITRA {
        @Override public int fid() { return SE_SIDM_SS_CITRA; }
        @Override public String code() { return AY26_CD; }
    }, // 26 SS, Citra/Spica at polar long. 180'
    TRUE_CITRA {
        @Override public int fid() { return SE_SIDM_TRUE_CITRA; }
        @Override public String code() { return AY27_CD; }
    }, // 27 True Spica (Spica exactly at 0 Libra)
    TRUE_REVATI {
        @Override public int fid() { return SE_SIDM_TRUE_REVATI; }
        @Override public String code() { return AY28_CD; }
    }, // 28 True Revati (zeta Psc exactly at 0 Aries)
    TRUE_PUSHYA {
        @Override public int fid() { return SE_SIDM_TRUE_PUSHYA; }
        @Override public String code() { return AY29_CD; }
    }, // 29 True Pushya (delta Cnc exactly a 16 Cancer)
    GALCENT_RGILBRAND {
        @Override public int fid() { return SE_SIDM_GALCENT_RGILBRAND; }
        @Override public String code() { return AY30_CD; }
    },
    GALEQU_IAU1958 {
        @Override public int fid() { return SE_SIDM_GALEQU_IAU1958; }
        @Override public String code() { return AY31_CD; }
    },
    GALEQU_TRUE {
        @Override public int fid() { return SE_SIDM_GALEQU_TRUE; }
        @Override public String code() { return AY32_CD; }
    },
    GALEQU_MULA {
        @Override public int fid() { return SE_SIDM_GALEQU_MULA; }
        @Override public String code() { return AY33_CD; }
    },
    GALALIGN_MARDYKS {
        @Override public int fid() { return SE_SIDM_GALALIGN_MARDYKS; }
        @Override public String code() { return AY34_CD; }
    },
    TRUE_MULA {
        @Override public int fid() { return SE_SIDM_TRUE_MULA; }
        @Override public String code() { return AY35_CD; }
    },
    GALCENT_MULA_WILHELM {
        @Override public int fid() { return SE_SIDM_GALCENT_MULA_WILHELM; }
        @Override public String code() { return AY36_CD; }
    },
    ARYABHATA_522 {
        @Override public int fid() { return SE_SIDM_ARYABHATA_522; }
        @Override public String code() { return AY37_CD; }
    },
    BABYL_BRITTON {
        @Override public int fid() { return SE_SIDM_BABYL_BRITTON; }
        @Override public String code() { return AY38_CD; }
    },
    TRUE_SHEORAN {
        @Override public int fid() { return SE_SIDM_TRUE_SHEORAN; }
        @Override public String code() { return AY39_CD; }
    },
    GALCENT_COCHRANE {
        @Override public int fid() { return SE_SIDM_GALCENT_COCHRANE; }
        @Override public String code() { return AY40_CD; }
    },
    GALEQU_FIORENZA {
        @Override public int fid() { return SE_SIDM_GALEQU_FIORENZA; }
        @Override public String code() { return AY41_CD; }
    },
    VALENS_MOON {
        @Override public int fid() { return SE_SIDM_VALENS_MOON; }
        @Override public String code() { return AY42_CD; }
    },
    LAHIRI_1940 {
        @Override public int fid() { return SE_SIDM_LAHIRI_1940; }
        @Override public String code() { return AY43_CD; }
    },
    LAHIRI_VP285 {
        @Override public int fid() { return SE_SIDM_LAHIRI_VP285; }
        @Override public String code() { return AY44_CD; }
    },
    KRISHNAMURTI_VP291 {
        @Override public int fid() { return SE_SIDM_KRISHNAMURTI_VP291; }
        @Override public String code() { return AY45_CD; }
    },
    LAHIRI_ICRC {
        @Override public int fid() { return SE_SIDM_LAHIRI_ICRC; }
        @Override public String code() { return AY46_CD; }
    },
    AY_USER {
        @Override public int fid() { return SE_SIDM_USER; }
        @Override public String code() { return AYUR_CD; }
    }; // 255 defined by the user

    @Override
    public int uid() {
        return ordinal();
    }

    @Override
    public ISweAyanamsa first() {
        return FAGAN_BRADLEY;
    }

    @Override
    public ISweAyanamsa last() {
        return AY_USER;
    }

    @Override
    public ISweAyanamsa[] all() {
        return values();
    }

    public static ISweAyanamsa byFid(final int fid) {
        return ISweEnum.byFid(fid, values());
    }

    public static ISweAyanamsa byCode(final String code) {
        return ISweEnum.byCode(code, values());
    }

    public static ISweEnumIterator<ISweAyanamsa> iterator() {
        return new SweEnumIterator<>(values(), FAGAN_BRADLEY.uid());
    }

    public static ISweEnumIterator<ISweAyanamsa> iteratorFrom(final SweAyanamsa ayanamsa) {
        return new SweEnumIterator<>(values(), ayanamsa.uid());
    }

    public static ISweEnumIterator<ISweAyanamsa> iteratorTo(final SweAyanamsa ayanamsa) {
        return new SweEnumIterator<>(values(), FAGAN_BRADLEY.uid(), ayanamsa.uid());
    }

    /**
     * Lahiri’s Ephemeris, established through the Calendar Reform Committee of 1955, also sought to follow
     * this position of Spica, however, adopted a formula which only worked for a temporary period of time and
     * today is no longer in line with Chitra Paksha. To avoid making new formulas, as we have ready
     * highly-accurate ephemeris-data from the Jet propulsion Laboratory of NASA, software makers have through
     * the Swiss Ephemeris been able to mark and adjust the Ayanamśa to the exact longitude of Spica.
     * <br><br>
     * Popular astrology software such as Jagannātha Horā and Shri Jyoti Star have an option to chose Chitra
     * Paksha Ayanamsha based on the exact position of the fixed star-Spica.
     *
     * @return // True Spica (Spica exactly at 0 Libra)
     */
    public static ISweAyanamsa getTrueSpica() {
        return TRUE_CITRA;
    }

    /**
     * @return Traditional LAHIRI
     */
    public static ISweAyanamsa getLahiri() {
        return LAHIRI;
    }

    /**
     * Traditional LAHIRI is the default ayanamsa.
     * <br><br>Please pay attention, it is NOT Citra Paksa Lahiri!<br>
     *
     * @return Traditional LAHIRI as the default Ayanamsa
     */
    public static ISweAyanamsa byDefault() {
        return LAHIRI;
    }
}
