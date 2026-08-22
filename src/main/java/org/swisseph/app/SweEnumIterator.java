/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2019-11
 */

package org.swisseph.app;

import org.swisseph.api.ISweEnum;
import org.swisseph.api.ISweEnumIterator;

import java.util.NoSuchElementException;

/**
 * Walks a family's values from one index to another.
 *
 * <h2>The reserved NIL member is never handed out</h2>
 * Almost every family here declares a {@link ISweEnum#isNil() NIL} at ordinal 0 so that an absent
 * or uncalculated quantity has something to be. It is reserved, not a value, so it is skipped -
 * including when a caller asks to start on it, which {@code iteratorFrom(NIL)} does.
 * <p>
 * Doing it here rather than in each registry's {@code iterator()} / {@code iteratorFrom()} /
 * {@code iteratorTo()} factory is deliberate: those factories start from an ordinal the caller
 * supplies, so every one of them would need the same guard, and the ones taking an arbitrary
 * member could not be fixed by picking a better starting constant at all. {@link ISweEnum#all()}
 * still returns NIL - that is the way to reach it on purpose.
 *
 * @author Yura Krymlov
 * @version 1.2, 2019-11
 */
public class SweEnumIterator<E extends ISweEnum> implements ISweEnumIterator<E> {
    protected final int length;
    protected final E[] values;
    protected int index;

    public SweEnumIterator(final E[] values, final int fromIndex) {
        this.values = values;
        this.index = fromIndex;
        this.length = values.length;
    }

    public SweEnumIterator(final E[] values, final int fromIndex, final int endIndex) {
        this.length = endIndex + 1;

        if (this.length > values.length) {
            throw new NoSuchElementException();
        }

        this.index = fromIndex;
        this.values = values;
    }

    /**
     * The next index this iterator would hand out, having stepped over any reserved member.
     * <p>
     * A negative starting index is treated as "before the beginning" rather than as an error, so
     * that {@link #hasNext()} can answer it without throwing.
     */
    protected int nextIndex() {
        int next = Math.max(index, 0);
        while (next < length && values[next].isNil()) next++;
        return next;
    }

    @Override
    public boolean hasNext() {
        return length > nextIndex();
    }

    @Override
    public E next() {
        index = nextIndex();

        if (index >= length) {
            throw new NoSuchElementException();
        }

        return values[index++];
    }

}
