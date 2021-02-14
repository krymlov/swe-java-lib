/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2019-11
 */

package org.swisseph.api;

import java.io.Serializable;

/**
 * @author Yura Krymlov
 * @version 1.1, 2019-11
 */
public interface ISweEnum extends Serializable {
    String NIL_CD = "NIL";
    int NIL_FID = 0;

    /**
     * The fid - is a functional identifier
     *
     * @return int id as a functional identifier
     */
    int fid();

    /**
     * The uid - is an unique identifier
     *
     * @return int uid as a unique identifier
     */
    int uid();

    /**
     * The code - is the unique short string identifier
     *
     * @return string code as a unique identifier
     */
    String code();

    /**
     * The name of this enum (typically as declared in its enum declaration).
     *
     * @return string name as a unique identifier
     */
    String name();

    /**
     * @return the enum value with the given FID
     * @throws IllegalArgumentException if not found
     */
    static <T extends ISweEnum> T byFid(final int fid, final T[] values) {
        for (T val : values) if (val.fid() == fid) return val;
        throw new IllegalArgumentException("No Enum found with FID: " + fid);
    }

    /**
     * @return the enum value with the given UID
     * @throws IllegalArgumentException if not found
     */
    static <T extends ISweEnum> T byUid(final int uid, final T[] values) {
        for (T val : values) if (val.uid() == uid) return val;
        throw new IllegalArgumentException("No Enum found with UID: " + uid);
    }

    /**
     * @return the enum value with the given index
     */
    static <T extends ISweEnum> T byIndex(int index, final T[] values) {
        if (0 > index) {
            throw new IllegalArgumentException("No Enum found with IDX: " + index);
        }

        if (0 == index) return values[0];

        final int mod = values.length-1;
        index %= mod;

        if (0 == index) return values[mod];
        else return values[index];
    }

    /**
     * @return the enum value with the given code
     * @throws IllegalArgumentException if not found
     */
    static <T extends ISweEnum> T byCode(final String code, final T[] values) {
        for (T val : values) if (val.code().equals(code)) return val;
        throw new IllegalArgumentException("No Enum found with CODE: " + code);
    }

    /**
     * @return the enum value with the given name
     * @throws IllegalArgumentException if not found
     */
    static <T extends ISweEnum> T byName(final String name, final T[] values) {
        for (T val : values) if (val.name().equals(name)) return val;
        throw new IllegalArgumentException("No Enum found with NAME: " + name);
    }
}
