/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2019-07
 */

package org.swisseph.utils;

import org.apache.commons.lang3.time.FastDateFormat;
import org.swisseph.api.ISweJulianDate;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import static org.swisseph.api.ISweConstants.*;
import static org.swisseph.api.ISweJulianDate.splitTime;

/**
 * @author Yura Krymlov
 * @version 1.1, 2019-07
 */
public interface IDateUtils {
    String F2H_2M = "%02d:%02d";
    String F4Y_2M_2D = "%4d–%02d–%02d";
    String F2H_2M_2S = F2H_2M + ":%02d";
    String F2H_2M_2S_MS = F2H_2M_2S + ".%02d";

    String F2H_2M_2H_2M = F2H_2M + " – " + F2H_2M;
    String F4Y_2M_2D_2H_2M = F4Y_2M_2D + STR_WS + F2H_2M;
    String F4Y_2M_2D_2H_2M_2S = F4Y_2M_2D + STR_WS + F2H_2M_2S;
    String F4Y_2M_2D_2H_2M_2S_MS = F4Y_2M_2D + STR_WS + F2H_2M_2S_MS;

    FastDateFormat FDTE_FORMATER = FastDateFormat.getInstance(FDTE_PATTERN, TimeZone.getTimeZone(UTC));

    /**
     * The method is intended to convert datetime as String object
     * in format 'yyyyMMddHHmmss' to the {@link Date} object
     */
    static Date convert(final String datetime) throws ParseException {
        return FDTE_FORMATER.parse(datetime);
    }

    /**
     * The method is intended to convert datetime as long value
     * in format 'yyyyMMddHHmmss' to the {@link Date} object
     * <p>
     * The value is zero padded back to 14 digits first: Long.toString() drops the
     * leading zeros of a year below 1000, so 500-01-01 12:00:00 (5000101120000)
     * would otherwise be read as the year 5000.
     */
    static Date convert(final long datetime) throws ParseException {
        return FDTE_FORMATER.parse(String.format("%014d", datetime));
    }

    /**
     * The method is intended to convert datetime as {@link Date} object
     * to the long value in format 'yyyyMMddHHmmss'
     */
    static long convert(final Date datetime) {
        return Long.parseLong(FDTE_FORMATER.format(datetime));
    }

    static long convert(final ISweJulianDate julianDate) {
        return convert(new int[]{julianDate.year(), julianDate.month(), julianDate.day(),
                julianDate.hours(), julianDate.minutes(), (int) julianDate.dseconds()});
    }

    static long convert(final int[] datetime) {
        if (null == datetime || datetime.length < 6) {
            throw new IllegalArgumentException("date[] length < 6");
        }

        final StringBuilder builder = new StringBuilder(14);
        formatYMD(builder, false, datetime[0], datetime[1], datetime[2]);
        return Long.parseLong(formatHMS(builder, false, datetime[3], datetime[4], datetime[5]).toString());
    }

    static StringBuilder format5(final ISweJulianDate julianDate) {
        return format(julianDate, F4Y_2M_2D_2H_2M);
    }

    static StringBuilder format6(final ISweJulianDate julianDate) {
        return format(julianDate, F4Y_2M_2D_2H_2M_2S);
    }

    static StringBuilder format7(final ISweJulianDate julianDate) {
        return format(julianDate, F4Y_2M_2D_2H_2M_2S_MS);
    }

    static StringBuilder format(final ISweJulianDate julianDate, final String format) {
        if (null == julianDate.date() || julianDate.date().length < 3) {
            throw new IllegalArgumentException("date[] length < 3");
        }

        switch (format) {
            case F4Y_2M_2D_2H_2M_2S_MS: {
                // seconds are rendered as ss.cc - a fixed two-digit fraction, so snap the
                // whole time to a centisecond first and let the carry run through the
                // seconds and minutes rather than printing an impossible "60.00"
                final double[] hms = splitTime(julianDate.localTime(), D05_CSEC_UNIT);
                final int isec = (int) hms[2];
                final int csec = (int) Math.round((hms[2] - isec) * i100);

                final StringBuilder builder = new StringBuilder(25);
                formatYMD(builder, true, julianDate.year(), julianDate.month(), julianDate.day()).append(STR_WS);
                formatHMS(builder, true, (int) hms[0], (int) hms[1], isec);
                builder.append(CH_DT);

                if (csec < i10) builder.append(CH_ZR);
                return builder.append(csec);
            }

            case F4Y_2M_2D_2H_2M_2S: {
                final StringBuilder builder = new StringBuilder(19);
                formatYMD(builder, true, julianDate.year(), julianDate.month(), julianDate.day()).append(STR_WS);
                return formatHMS(builder, true, julianDate.hours(), julianDate.minutes(), (int) julianDate.dseconds());
            }

            case F4Y_2M_2D_2H_2M: {
                final StringBuilder builder = new StringBuilder(16);
                formatYMD(builder, true, julianDate.year(), julianDate.month(), julianDate.day()).append(STR_WS);
                return formatHMS(builder, true, julianDate.hours(), julianDate.minutes());
            }

            case F4Y_2M_2D: {
                final StringBuilder builder = new StringBuilder(10);
                return formatYMD(builder, true, julianDate.year(), julianDate.month(), julianDate.day());
            }

            case F2H_2M_2S: {
                final StringBuilder builder = new StringBuilder(8);
                return formatHMS(builder, true, julianDate.hours(), julianDate.minutes(), (int) julianDate.dseconds());
            }

            case F2H_2M: {
                final StringBuilder builder = new StringBuilder(5);
                return formatHMS(builder, true, julianDate.hours(), julianDate.minutes());
            }
        }

        final StringBuilder builder = new StringBuilder(16);
        formatYMD(builder, true, julianDate.year(), julianDate.month(), julianDate.day()).append(STR_WS);
        return formatHMS(builder, true, julianDate.hours(), julianDate.minutes());
    }

    static StringBuilder formatYMD(StringBuilder builder, boolean separate, int... ymd) {
        builder.append(ymd[0]);

        for (int i = 1; i < ymd.length; i++) {
            if (separate) builder.append(EN_DASH);
            if (ymd[i] < i10) builder.append(CH_ZR);
            builder.append(ymd[i]);
        }

        return builder;
    }

    static StringBuilder formatHMS(StringBuilder builder, boolean separate, int... hms) {
        if (hms[0] < i10) builder.append(CH_ZR);
        builder.append(hms[0]);

        for (int i = 1; i < hms.length; i++) {
            if (separate) builder.append(CH_CN);
            if (hms[i] < i10) builder.append(CH_ZR);
            builder.append(hms[i]);
        }

        return builder;
    }

}
