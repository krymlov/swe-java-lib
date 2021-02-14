/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2019-08
 */

package org.swisseph.app;

import org.swisseph.api.ISweSegment;

import static org.swisseph.api.ISweConstants.d0;
import static org.swisseph.utils.IDegreeUtils.toDMSms;

/**
 * @author Yura Krymlov
 * @version 1.1, 2019-08
 */
public class SweSegment implements ISweSegment {
    public static final ISweSegment ZERO_SEGMENT = new SweSegment(d0, d0);

    protected final double begin;
    protected final double end;

    public SweSegment(final double begin, final double end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    public double begin() {
        return begin;
    }

    @Override
    public double end() {
        return end;
    }

    @Override
    public String toString() {
        return toDMSms(begin, true) + " -> " + toDMSms(end, true);
    }

}
