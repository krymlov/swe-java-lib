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
 * @version 1.2, 2021-12
 */
public class SweSegment implements ISweSegment {
    private static final long serialVersionUID = 5886425968833629547L;
    public static final ISweSegment ZERO_SEGMENT = new SweSegment(d0, d0);

    protected final double start;
    protected final double close;

    public SweSegment(final double start, final double close) {
        this.start = start;
        this.close = close;
    }

    @Override
    public double start() {
        return start;
    }

    @Override
    public double close() {
        return close;
    }

    @Override
    public String toString() {
        return toDMSms(start, true) + " -> " + toDMSms(close, true);
    }

}
