/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2019-09
 */

package org.swisseph.app;

import org.swisseph.api.ISweEnum;
import org.swisseph.api.ISweEnumIterator;
import org.swisseph.api.ISweGender;

/**
 * @author Yura Krymlov
 * @version 1.1, 2019-09
 */
public enum SweGender implements ISweGender {
    FEMALE {
        @Override
        public String code() {
            return FEMALE_CD;
        }
    },  // 0
    MALE {
        @Override
        public String code() {
            return MALE_CD;
        }
    };      // 1

    @Override
    public int fid() {
        return ordinal();
    }

    @Override
    public int uid() {
        return ordinal();
    }

    /**
     * @return MALE as the default Gender
     */
    public static ISweGender byDefault() {
        return MALE;
    }

    public static ISweGender byFid(final int fid) {
        return ISweEnum.byFid(fid, values());
    }

    public static ISweGender byCode(final String code) {
        return ISweEnum.byCode(code, values());
    }

    public static ISweEnumIterator<ISweGender> iterator() {
        return new SweEnumIterator<>(values(), FEMALE.uid());
    }
}
