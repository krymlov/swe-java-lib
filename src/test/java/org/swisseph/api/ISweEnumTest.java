/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph.api;

import org.junit.jupiter.api.Test;
import org.swisseph.app.SweAyanamsa;
import org.swisseph.app.SweHouseSystem;

import static org.junit.jupiter.api.Assertions.*;
import static org.swisseph.api.ISweEnum.NIL_CD;
import static org.swisseph.api.ISweEnum.NIL_FID;

/**
 * {@link ISweEnum#isNil()}, {@link ISweEnum#label()} and the corrected
 * {@link ISweEnum#byIndex(int, ISweEnum[])}.
 * <p>
 * The {@code byIndex} tests matter most: the method is a cyclic lookup that used to reduce
 * modulo {@code values.length - 1} unconditionally, which is only right for a family that
 * reserves index 0 for its NIL member. Every caller in this workspace does have one, so the
 * defect was invisible - it shows up only for a NIL-less family, and then as the neighbouring
 * element rather than as an error. Both layouts are pinned here, and the NIL case is checked
 * index for index against the old formula so the fix is provably behaviour-preserving where
 * it matters.
 */
class ISweEnumTest {

    /** a minimal family, so the layout is under the test's control rather than incidental */
    private static class Member implements ISweEnum {
        private final int fid;
        private final String code;

        Member(int fid, String code) {
            this.fid = fid;
            this.code = code;
        }

        @Override public int fid() { return fid; }
        @Override public int uid() { return fid; }
        @Override public String code() { return code; }
        @Override public String name() { return code; }
        @Override public String toString() { return code; }
    }

    private static Member[] withNil(final int realMembers) {
        final Member[] values = new Member[realMembers + 1];
        values[0] = new Member(NIL_FID, NIL_CD);
        for (int i = 1; i <= realMembers; i++) values[i] = new Member(i, "M" + i);
        return values;
    }

    private static Member[] withoutNil(final int realMembers) {
        final Member[] values = new Member[realMembers];
        for (int i = 0; i < realMembers; i++) values[i] = new Member(i + 1, "M" + (i + 1));
        return values;
    }

    /** the implementation as it stood before the fix, to prove equivalence on NIL families */
    private static <T extends ISweEnum> T legacyByIndex(int index, final T[] values) {
        if (0 == index) return values[0];
        final int mod = values.length - 1;
        index %= mod;
        if (0 == index) return values[mod];
        else return values[index];
    }

    // ============================================================== isNil

    @Test
    void isNilRecognisesTheReservedMemberAndNothingElse() {
        final Member[] values = withNil(12);

        assertTrue(values[0].isNil(), "fid 0 + code NIL is the reserved member");
        for (int i = 1; i < values.length; i++) {
            assertFalse(values[i].isNil(), values[i].code() + " is a real value");
        }
    }

    @Test
    void isNilNeedsBothTheFidAndTheCode() {
        assertFalse(new Member(NIL_FID, "M0").isNil(), "fid alone is not enough");
        assertFalse(new Member(3, NIL_CD).isNil(), "code alone is not enough");
    }

    @Test
    void theRealFamiliesAgreeWithIsNil() {
        // SweHouseSystem declares a NIL member; SweAyanamsa's first value is a real ayanamsa
        assertTrue(ISweEnum.byCode(NIL_CD, SweHouseSystem.values()).isNil(),
                "SweHouseSystem.NIL must report itself as NIL");
        for (SweAyanamsa a : SweAyanamsa.values()) {
            assertFalse(a.isNil(), a.name() + " is a real ayanamsa");
        }
    }

    // ============================================================== label

    @Test
    void labelDefaultsToTheCode() {
        assertEquals("M7", new Member(7, "M7").label());
        for (SweAyanamsa a : SweAyanamsa.values()) {
            assertEquals(a.code(), a.label(), a.name() + " has no label of its own yet");
        }
    }

    @Test
    void labelCanBeOverriddenWithoutTouchingTheCode() {
        final ISweEnum labelled = new Member(5, "M5") {
            @Override public String label() { return "Simha"; }
        };
        assertEquals("Simha", labelled.label());
        assertEquals("M5", labelled.code(), "the technical key must be unaffected");
    }

    // ============================================================== byIndex, NIL layout

    @Test
    void withANilMemberIndexZeroIsNilAndTheRestCycle() {
        final Member[] values = withNil(12);

        assertTrue(ISweEnum.byIndex(0, values).isNil());
        assertEquals("M1", ISweEnum.byIndex(1, values).code());
        assertEquals("M12", ISweEnum.byIndex(12, values).code());
        assertEquals("M1", ISweEnum.byIndex(13, values).code(), "wraps back to the first");
        assertEquals("M12", ISweEnum.byIndex(24, values).code());
        assertEquals("M1", ISweEnum.byIndex(25, values).code());
    }

    @Test
    void forNilFamiliesTheFixIsBehaviourPreservingIndexForIndex() {
        // 12 rasis and 27 naksatras are the two layouts this workspace actually uses;
        // ENaksatraPada.navamsa() drives ERasi.byIndex(ordinal()) over 0..108
        for (int members : new int[]{2, 5, 12, 27}) {
            final Member[] values = withNil(members);
            for (int index = 0; index <= 3 * members + 5; index++) {
                assertEquals(legacyByIndex(index, values).code(),
                        ISweEnum.byIndex(index, values).code(),
                        "index " + index + " of a " + members + "-member NIL family");
            }
        }
    }

    // ============================================================== byIndex, no NIL member

    @Test
    void withoutANilMemberTheWholeArrayCycles() {
        final Member[] values = withoutNil(7);

        for (int i = 0; i < 7; i++) {
            assertEquals("M" + (i + 1), ISweEnum.byIndex(i, values).code(), "index " + i);
        }
        // the case the old formula got wrong: one full lap
        assertEquals("M1", ISweEnum.byIndex(7, values).code(),
                "a NIL-less family must wrap to its own first element");
        assertEquals("M2", ISweEnum.byIndex(8, values).code());
        assertEquals("M1", ISweEnum.byIndex(14, values).code());

        assertNotEquals(legacyByIndex(7, values).code(), ISweEnum.byIndex(7, values).code(),
                "this is precisely where the old implementation returned the neighbour");
    }

    // ============================================================== guards

    /**
     * A negative position names nothing, so since 2026-08-22 it is answered like any other failed
     * lookup - with the family's reserved NIL rather than with an exception. That is the whole
     * point of declaring one: a caller that cannot name a member gets an object it can test with
     * {@link ISweEnum#isNil()} instead of having to catch something.
     */
    @Test
    void aNegativeIndexAnswersNilRatherThanThrowing() {
        final Member[] values = withNil(12);

        assertTrue(ISweEnum.byIndex(-1, values).isNil(), "byIndex(-1)");
        assertSame(ISweEnum.nil(values), ISweEnum.byIndex(Integer.MIN_VALUE, values));
    }

    /**
     * A family that declares no reserved member still throws - it has nothing truthful to answer
     * with, and returning some real member would be a wrong answer rather than a missing one.
     */
    @Test
    void aNegativeIndexStillThrowsWhenTheFamilyHasNoNil() {
        final Member[] values = withoutNil(7);

        assertNull(ISweEnum.nil(values), "this fixture deliberately has no NIL member");
        assertThrows(IllegalArgumentException.class, () -> ISweEnum.byIndex(-1, values));
    }

    @Test
    void anEmptyOrMissingArrayIsRejectedRatherThanIndexed() {
        assertThrows(IllegalArgumentException.class,
                () -> ISweEnum.byIndex(0, new Member[0]));
        assertThrows(IllegalArgumentException.class,
                () -> ISweEnum.byIndex(0, (Member[]) null));
    }
}
