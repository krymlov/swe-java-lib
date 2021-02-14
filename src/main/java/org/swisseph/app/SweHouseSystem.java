/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2019-10
 */

package org.swisseph.app;

import org.swisseph.api.ISweEnum;
import org.swisseph.api.ISweEnumIterator;
import org.swisseph.api.ISweHouseSystem;

import static swisseph.SweConst.*;

/**
 * House System
 *
 * @author Yura Krymlov
 * @version 1.1, 2019-10
 */
public enum SweHouseSystem implements ISweHouseSystem {
    NIL {
        @Override public int fid() { return NIL_FID; }
        @Override public String code() { return NIL_CD; }
    }, // 0  Reserved
    ALCABITIUS {
        @Override public int fid() { return SE_HSYS_ALCABITIUS; }
        @Override public String code() { return HSB_CD; }
    },
    CAMPANUS {
        @Override public int fid() { return SE_HSYS_CAMPANUS; }
        @Override public String code() { return HSC_CD; }
    },
    EQUAL {
        @Override public int fid() { return SE_HSYS_EQUAL; }
        @Override public String code() { return HSE_CD; }
    },
    HORIZONTAL {
        @Override public int fid() { return SE_HSYS_HORIZONTAL; }
        @Override public String code() { return HSH_CD; }
    },
    KOCH {
        @Override public int fid() { return SE_HSYS_KOCH; }
        @Override public String code() { return HSK_CD; }
    },
    MORINUS {
        @Override public int fid() { return SE_HSYS_MORINUS; }
        @Override public String code() { return HSM_CD; }
    },
    PORPHYRIUS {
        @Override public int fid() { return SE_HSYS_PORPHYRIUS; }
        @Override public String code() { return HSO_CD; }
    },
    PLACIDUS {
        @Override public int fid() { return SE_HSYS_PLACIDUS; }
        @Override public String code() { return HSP_CD; }
    },          // This is the most commonly used house system in Western astrology
    REGIOMONTANUS {
        @Override public int fid() { return SE_HSYS_REGIOMONTANUS; }
        @Override public String code() { return HSR_CD; }
    },
    POLICH_PAGE {
        @Override public int fid() { return SE_HSYS_POLICH_PAGE; }
        @Override public String code() { return HST_CD; }
    },
    KRUSINSKI {
        @Override public int fid() { return SE_HSYS_KRUSINSKI; }
        @Override public String code() { return HSU_CD; }
    },
    VEHLOW {
        @Override public int fid() { return SE_HSYS_VEHLOW; }
        @Override public String code() { return HSV_CD; }
    },
    WHOLE_SIGN {
        @Override public int fid() { return SE_HSYS_WHOLE_SIGN; }
        @Override public String code() { return HSW_CD; }
    },      // Default - It is thought to be the oldest system of house division
    MERIDIAN {
        @Override public int fid() { return SE_HSYS_MERIDIAN; }
        @Override public String code() { return HSX_CD; }
    };

    @Override
    public int uid() {
        return ordinal();
    }

    @Override
    public ISweHouseSystem first() {
        return ALCABITIUS;
    }

    @Override
    public ISweHouseSystem last() {
        return MERIDIAN;
    }

    @Override
    public ISweHouseSystem[] all() {
        return values();
    }

    /**
     * WholeSign - It is thought to be the oldest system of house division
     *
     * @return default house system - WholeSign
     */
    public static ISweHouseSystem byDefault() {
        return WHOLE_SIGN;
    }

    public static ISweHouseSystem byFid(final int fid) {
        return ISweEnum.byFid(fid, values());
    }

    public static ISweHouseSystem byCode(final String code) {
        return ISweEnum.byCode(code, values());
    }

    public static ISweEnumIterator<ISweHouseSystem> iterator() {
        return new SweEnumIterator<>(values(), ALCABITIUS.uid());
    }

    public static ISweEnumIterator<ISweHouseSystem> iteratorFrom(final ISweHouseSystem houseSystem) {
        return new SweEnumIterator<>(values(), houseSystem.uid());
    }

    public static ISweEnumIterator<ISweHouseSystem> iteratorTo(final ISweHouseSystem houseSystem) {
        return new SweEnumIterator<>(values(), ALCABITIUS.uid(), houseSystem.uid());
    }
}
