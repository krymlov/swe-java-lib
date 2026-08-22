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
     * This family's reserved {@link ISweEnum#isNil() NIL} member, or {@code null} if it declares
     * none - which is the case for {@code EGraha}, {@code ELagna} and for every alias leaf such
     * as {@code GrahaGuru}, whose constants are all names for one real value.
     *
     * @return the NIL member of this family, or null
     */
    default E nil() {
        return ISweEnum.nil(all());
    }

    /**
     * @return the member with that name, or this family's {@link #nil() NIL} if there is none
     */
    default E findByName(final String name) {
        if (null != name) for (E val : all()) if (name.equals(val.name())) return val;
        return nil();
    }

    /**
     * @return the member with that code, or this family's {@link #nil() NIL} if there is none
     */
    default E findByCode(final String code) {
        if (null != code) for (E val : all()) if (code.equals(val.code())) return val;
        return nil();
    }

    /**
     * @return the member with that uid, or this family's {@link #nil() NIL} if there is none
     */
    default E findByUid(final int uid) {
        for (E val : all()) if (uid == val.uid()) return val;
        return nil();
    }

    /**
     * @return the member with that fid, or this family's {@link #nil() NIL} if there is none
     */
    default E findByFid(final int fid) {
        for (E val : all()) if (fid == val.fid()) return val;
        return nil();
    }

    /**
     * Returns all values of this sequence.
     */
    E[] all();

    /**
     * The first value of this sequence, <b>skipping the reserved {@link ISweEnum#isNil() NIL}
     * member</b>.
     * <p>
     * NIL is declared at ordinal 0 by almost every family here so that an absent or
     * uncalculated quantity has something to be. It is reserved, not a value: it must never be
     * what an ordinary walk starts on, and {@link #follow(int)} - bounded by {@code first()} and
     * {@code last()} - therefore never reaches it either. {@link #all()} still returns it, which
     * is how a caller that wants it can get at it.
     * <p>
     * The one family this cannot hold for is a Null Object itself ({@code NilRasi} and its
     * siblings), whose only member <i>is</i> NIL - there the array's first element is returned,
     * because there is nothing else to return.
     */
    default E first() {
        final E[] all = all();
        for (E value : all) if (!value.isNil()) return value;
        return all[0];
    }

    /**
     * The last value of this sequence, skipping the reserved NIL member - see {@link #first()}.
     * <p>
     * A trailing sentinel is rarer than a leading one but does occur: {@code SweAyanamsa} keeps
     * {@code AY_NONE} at the end and excludes it by overriding this method, since it is a real
     * member (tropical, "no ayanamsa") rather than a NIL.
     */
    default E last() {
        final E[] all = all();
        for (int i = all.length - 1; i >= 0; i--) if (!all[i].isNil()) return all[i];
        return all[all.length - 1];
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
    default E follow(final int steps) {
        final int first = first().ordinal();
        final int count = last().ordinal() - first + 1;

        final int offset = ((ordinal() + steps - first) % count + count) % count;
        return all()[first + offset];
    }

}
