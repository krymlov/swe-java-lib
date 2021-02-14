/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2020-12
 */

package org.swisseph.api;

/**
 * @author Yura Krymlov
 * @version 1.0, 2020-12
 */
public interface ISweEnumSequence<E extends ISweEnumSequence<E>> extends ISweEnum {

    /**
     * @return corresponding object or null if nothing found
     */
    default E findByName(final String name) {
        if ( null == name ) return null;
        for (E val : all()) if (name.equals(val.name())) return val;
        return null;
    }

    /**
     * @return corresponding object or null if nothing found
     */
    default E findByCode(final String code) {
        if ( null == code ) return null;
        for (E val : all()) if (code.equals(val.code())) return val;
        return null;
    }

    /**
     * @return corresponding object or null if nothing found
     */
    default E findByUid(final int uid) {
        for (E val : all()) if (uid == val.uid()) return val;
        return null;
    }

    /**
     * @return corresponding object or null if nothing found
     */
    default E findByFid(final int fid) {
        for (E val : all()) if (fid == val.fid()) return val;
        return null;
    }

    /**
     * Returns all values of this sequence.
     */
    E[] all();

    /**
     * Returns a first value of this sequence.
     */
    default E first() {
        return all()[0];
    }

    /**
     * Returns a last value of this sequence.
     */
    default E last() {
        final E[] all = all();
        return all[all.length-1];
    }

    /**
     * Returns the ordinal of this sequence (its position in its enum declaration).
     */
    int ordinal();

    /**
     * Returns next value of this sequence starting from this one.
     */
    default E following() {
        return follow(1);
    }

    /**
     * Returns previous value of this sequence starting from this one.
     */
    default E previous() {
        return follow(-1);
    }

    /**
     * Returns value of this sequence - absolute position from this one
     */
    default E follow(int ordinal) {
        final int first = first().ordinal();
        final int last = last().ordinal();

        ordinal += ordinal();

        if (ordinal < first) ordinal = last;
        else if (ordinal > last) {
            ordinal %= last;
            if (0 == first && ordinal > 0) {
                ordinal -= 1;
            }
        }

        return all()[ordinal];
    }

}
