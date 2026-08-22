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
     * Whether this is the reserved "not a value" member.
     * <p>
     * Practically every family in this workspace declares one at ordinal 0 - {@code ERasi.NIL},
     * {@code EBhava.NIL}, {@code ETithi.NIL} and so on - so that an uncalculated or absent
     * quantity has something to be rather than being {@code null}. The convention was
     * expressed only by the two constants above and re-tested by hand at each call site
     * ({@code IKundaliSegment} and {@code ITithi} both carried a literal copy of
     * {@code 0 == fid() && NIL_CD.equals(code())}); this gives it one name and one definition.
     *
     * @return true for the NIL member of a family, false for every real value
     */
    default boolean isNil() {
        return NIL_FID == fid() && NIL_CD.equals(code());
    }

    /**
     * The label meant for a human reader, as opposed to {@link #code()} - a short technical
     * key such as {@code "B6"} - and {@link #name()}, the constant's own identifier.
     * <p>
     * Defaults to the code, so nothing changes for a family that does not override it.
     * <p>
     * <b>Why this exists.</b> Concrete families in {@code swe-jyotisa-lib} currently carry
     * their display name in a <i>second enum constant</i> - {@code BhavaAri{B6, ARI}},
     * {@code GrahaGuru{G3, GU, GURU, Ju, Jupiter}} - and presentation code reaches it with
     * {@link ISweEnumSequence#following()}, i.e. a sequence-navigation operation used as a
     * presentation accessor. That makes {@code following()} mean different things depending
     * on how many aliases a family happens to declare, and it is the root of the
     * {@code GrahaGuru.GU != GrahaGuru.GURU} identity trap. This method is where such a
     * label belongs, and it is also the natural hook for a {@code ResourceBundle} later.
     *
     * @return the display label; never null
     */
    default String label() {
        return code();
    }

    /**
     * The family's reserved {@link #isNil() NIL} member, or {@code null} if it declares none.
     * <p>
     * This is what every lookup below answers with when it finds nothing valid. Three families
     * deliberately have no reserved member - {@code EGraha}, {@code ELagna} and the alias leaves
     * such as {@code GrahaGuru}, which are several names for one real value - and for those a
     * failed lookup still throws, because there is nothing truthful to return.
     */
    static <T extends ISweEnum> T nil(final T[] values) {
        if (null != values) for (T val : values) if (val.isNil()) return val;
        return null;
    }

    /**
     * @return the family's NIL when there is one, otherwise throws
     */
    static <T extends ISweEnum> T nilOrFail(final T[] values, final String what, final Object key) {
        final T nil = nil(values);
        if (null != nil) return nil;
        throw new IllegalArgumentException("No Enum found with " + what + ": " + key
                + ", and this family declares no NIL member to answer with");
    }

    /**
     * @return the enum value with the given FID, or the family's {@link #nil(ISweEnum[]) NIL}
     * @throws IllegalArgumentException only if the family declares no NIL member
     */
    static <T extends ISweEnum> T byFid(final int fid, final T[] values) {
        if (null != values) for (T val : values) if (val.fid() == fid) return val;
        return nilOrFail(values, "FID", fid);
    }

    /**
     * @return the enum value with the given UID, or the family's {@link #nil(ISweEnum[]) NIL}
     * @throws IllegalArgumentException only if the family declares no NIL member
     */
    static <T extends ISweEnum> T byUid(final int uid, final T[] values) {
        if (null != values) for (T val : values) if (val.uid() == uid) return val;
        return nilOrFail(values, "UID", uid);
    }

    /**
     * Cyclic lookup by position: the index wraps, so a caller may keep counting past the end
     * of the family and stay inside it. {@code ENaksatraPada.navamsa()} relies on exactly
     * that, mapping 108 padas onto 12 rasis with {@code ERasi.byIndex(ordinal())}.
     * <p>
     * A family that reserves index 0 for its {@link #isNil() NIL} member cycles over
     * {@code 1..length-1} and keeps index 0 addressing NIL; a family without one cycles over
     * the whole array.
     * <p>
     * <b>This used to assume the NIL layout unconditionally</b> - it reduced modulo
     * {@code values.length - 1} whatever it was given. For a family with no NIL member that
     * is off by one past the first lap: a 7-element family answered index 7 with
     * {@code values[1]} where the cycle says {@code values[0]}, silently returning the
     * neighbour rather than failing. That is the same failure mode
     * {@link ISweEnumSequence#follow(int)} was corrected for. Behaviour for NIL families -
     * which is every caller in this workspace today - is unchanged, index for index.
     *
     * @param index  position, wrapped into range; must not be negative
     * @param values the family's values, NIL first if it has one
     * @return the enum value at that cyclic position
     * @throws IllegalArgumentException if the index is negative or the array is empty
     */
    static <T extends ISweEnum> T byIndex(final int index, final T[] values) {
        if (null == values || 0 == values.length) {
            throw new IllegalArgumentException("No values to look an index up in");
        }

        // a negative position names nothing, so it is answered like any other failed lookup
        if (0 > index) return nilOrFail(values, "IDX", index);

        if (!values[0].isNil()) {
            // no reserved member - the whole array is the cycle
            return values[index % values.length];
        }

        if (0 == index) return values[0];

        // index 0 stays NIL; 1..length-1 form the cycle
        final int count = values.length - 1;
        return values[1 + ((index - 1) % count)];
    }

    /**
     * @return the enum value with the given code, or the family's {@link #nil(ISweEnum[]) NIL}
     * @throws IllegalArgumentException only if the family declares no NIL member
     */
    static <T extends ISweEnum> T byCode(final String code, final T[] values) {
        if (null != values && null != code) for (T val : values) {
            if (code.equals(val.code())) return val;
        }
        return nilOrFail(values, "CODE", code);
    }

    /**
     * @return the enum value with the given name, or the family's {@link #nil(ISweEnum[]) NIL}
     * @throws IllegalArgumentException only if the family declares no NIL member
     */
    static <T extends ISweEnum> T byName(final String name, final T[] values) {
        if (null != values && null != name) for (T val : values) {
            if (name.equals(val.name())) return val;
        }
        return nilOrFail(values, "NAME", name);
    }
}
