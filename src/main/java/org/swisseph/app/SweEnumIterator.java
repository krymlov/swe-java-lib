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
 * @author Yura Krymlov
 * @version 1.1, 2019-11
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

    @Override
    public boolean hasNext() {
        return length > index;
    }

    @Override
    public E next() {
        if (index >= length || index < 0) {
            throw new NoSuchElementException();
        }

        E value = values[index];
        ++index;

        return value;
    }

}
