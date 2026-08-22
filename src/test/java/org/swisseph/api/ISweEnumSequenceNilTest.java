/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph.api;

import org.junit.jupiter.api.Test;
import org.swisseph.app.SweEnumIterator;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The <b>default</b> implementations of {@link ISweEnumSequence#first()}, {@link
 * ISweEnumSequence#last()} and the NIL skipping in {@link SweEnumIterator}, tested against a
 * family declared here rather than against a real one.
 *
 * <h2>Why a made-up family, and why this file exists at all</h2>
 * Every real family in the workspace <b>overrides</b> {@code first()} and {@code last()} to name
 * its own bounds ({@code ERasi.first()} is {@code MESHA}), so the defaults are never reached from
 * production code and a reflective sweep over the real sequences cannot tell a correct default
 * from a broken one - verified by reverting the default to {@code all()[0]}, which left every
 * such check green.
 * <p>
 * The defaults still matter: they are the contract a family added later inherits without doing
 * anything, and "NIL is reserved and invisible" has to hold for it too. So they are exercised
 * directly, on a family that deliberately does not override them.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
class ISweEnumSequenceNilTest {

    /**
     * A family that declares NIL at index 0 and overrides nothing - the layout every registry
     * has, minus the {@code first()}/{@code last()} overrides that hide the default.
     */
    private static class Member implements ISweEnumSequence<Member> {
        private final int fid;
        private final String code;
        private final int ordinal;
        private final Member[] family;

        Member(int fid, String code, int ordinal, Member[] family) {
            this.fid = fid;
            this.code = code;
            this.ordinal = ordinal;
            this.family = family;
        }

        @Override public int fid() { return fid; }
        @Override public int uid() { return fid; }
        @Override public String code() { return code; }
        @Override public String name() { return code; }
        @Override public int ordinal() { return ordinal; }
        @Override public Member[] all() { return family; }
        @Override public String toString() { return code; }
    }

    /** NIL first, then {@code members} real values - and no overrides at all */
    private static Member[] withNil(final int members) {
        final Member[] family = new Member[members + 1];

        family[0] = new Member(ISweEnum.NIL_FID, ISweEnum.NIL_CD, 0, family);
        for (int i = 1; i <= members; i++) family[i] = new Member(i, "M" + i, i, family);

        return family;
    }

    /** a family with no reserved member, for the cases that must behave differently */
    private static Member[] withoutNil(final int members) {
        final Member[] family = new Member[members];
        for (int i = 0; i < members; i++) family[i] = new Member(i + 1, "M" + (i + 1), i, family);
        return family;
    }

    /** the degenerate case: a family that is nothing but its reserved member, like NilRasi */
    private static Member[] onlyNil() {
        final Member[] family = new Member[1];
        family[0] = new Member(ISweEnum.NIL_FID, ISweEnum.NIL_CD, 0, family);
        return family;
    }

    // ============================================================ first() and last()

    @Test
    void firstSkipsTheReservedMemberWithoutAnOverride() {
        final Member[] family = withNil(12);

        assertTrue(family[0].isNil(), "the fixture must start with NIL");
        assertSame(family[1], family[0].first(), "first() must step over the reserved member");
        assertFalse(family[0].first().isNil());
    }

    @Test
    void lastSkipsATrailingReservedMemberToo() {
        final Member[] family = new Member[3];
        family[0] = new Member(1, "M1", 0, family);
        family[1] = new Member(2, "M2", 1, family);
        family[2] = new Member(ISweEnum.NIL_FID, ISweEnum.NIL_CD, 2, family);

        assertSame(family[1], family[0].last(), "a trailing NIL must not be the last value");
    }

    @Test
    void aFamilyWithoutAReservedMemberIsUnaffected() {
        final Member[] family = withoutNil(7);

        assertSame(family[0], family[0].first());
        assertSame(family[6], family[0].last());
    }

    /**
     * The Null Objects themselves - {@code NilRasi} and its four siblings - are only NIL, so
     * there is nothing else for {@code first()} to answer. Returning the member rather than
     * throwing keeps them usable as ordinary values; they are excluded from the reference base
     * instead.
     */
    @Test
    void aFamilyThatIsOnlyNilAnswersItself() {
        final Member[] family = onlyNil();

        assertSame(family[0], family[0].first());
        assertSame(family[0], family[0].last());
    }

    // ============================================================ navigation

    @Test
    void navigationNeverReachesTheReservedMember() {
        final Member[] family = withNil(12);
        final Member first = family[0].first();

        Member cursor = first;
        for (int i = 0; i < 26; i++) {
            assertFalse(cursor.isNil(), "following() reached NIL at step " + i);
            cursor = cursor.following();
        }

        for (int k = -26; k <= 26; k++) {
            assertFalse(first.follow(k).isNil(), "follow(" + k + ") reached NIL");
        }
    }

    // ============================================================ the lookups

    @Test
    void aFailedFindAnswersTheReservedMember() {
        final Member[] family = withNil(12);
        final Member any = family[3];

        assertSame(family[0], any.nil());
        assertSame(family[0], any.findByName("nothing"));
        assertSame(family[0], any.findByCode("nothing"));
        assertSame(family[0], any.findByUid(Integer.MIN_VALUE));
        assertSame(family[0], any.findByFid(Integer.MIN_VALUE));
    }

    /**
     * A family with no reserved member has nothing to answer with, so {@code findBy*} still
     * returns {@code null} there. That is the case for the alias leaves - {@code GrahaGuru} and
     * friends, several names for one value - and callers that scan them must stay null-safe.
     */
    @Test
    void aFamilyWithoutAReservedMemberStillAnswersNullFromFind() {
        final Member[] family = withoutNil(7);

        org.junit.jupiter.api.Assertions.assertNull(family[0].nil());
        org.junit.jupiter.api.Assertions.assertNull(family[0].findByName("nothing"));
    }

    // ============================================================ the iterators

    @Test
    void anIteratorStartedOnTheReservedMemberStepsOverIt() {
        final Member[] family = withNil(12);
        final SweEnumIterator<Member> iterator = new SweEnumIterator<>(family, 0);

        assertTrue(iterator.hasNext());
        assertSame(family[1], iterator.next(), "iteratorFrom(NIL) must not yield NIL");

        int seen = 1;
        while (iterator.hasNext()) {
            assertFalse(iterator.next().isNil());
            seen++;
        }

        assertEquals(12, seen, "every real member, and only those");
    }

    @Test
    void aBoundedIteratorSkipsItAsWell() {
        final Member[] family = withNil(12);
        final SweEnumIterator<Member> iterator = new SweEnumIterator<>(family, 0, 3);

        assertSame(family[1], iterator.next());
        assertSame(family[2], iterator.next());
        assertSame(family[3], iterator.next());
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    /**
     * A family that is only NIL yields nothing at all rather than yielding NIL - {@code hasNext()}
     * has to answer that without walking off the end.
     */
    @Test
    void anIteratorOverOnlyNilIsEmpty() {
        final SweEnumIterator<Member> iterator = new SweEnumIterator<>(onlyNil(), 0);

        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }
}
