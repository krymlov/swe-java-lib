/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2019-07
 */

package org.swisseph.utils;

import org.apache.commons.lang3.time.FastDateFormat;
import org.swisseph.api.ISweConstants;
import org.swisseph.api.ISweJulianDate;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import static org.swisseph.api.ISweConstants.*;

/**
 * @author Yura Krymlov
 * @version 1.1, 2019-07
 */
public interface IDateUtils {
    String F4Y_2M_2D = "%4d-%02d-%02d";
    String F2H_2M_2S = "%02d:%02d:%02d";
    String F2H_2M = "%02d:%02d";

    String F2H_2M_2H_2M = F2H_2M + " - " + F2H_2M;
    String F4Y_2M_2D_2H_2M = F4Y_2M_2D + STR_WS + F2H_2M;
    String F4Y_2M_2D_2H_2M_2S = F4Y_2M_2D + STR_WS + F2H_2M_2S;
    String F4Y_2M_2D_2H_2M_2S_MS = F4Y_2M_2D_2H_2M_2S + ".%03d";

    TimeZone FDTE_UTC_TMZ = TimeZone.getTimeZone(UTC);
    FastDateFormat FDTE_FORMATER = FastDateFormat
            .getInstance(ISweConstants.FDTE_PATTERN, FDTE_UTC_TMZ);

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
     */
    static Date convert(final long datetime) throws ParseException {
        return FDTE_FORMATER.parse(Long.toString(datetime));
    }

    /**
     * The method is intended to convert datetime as {@link Date} object
     * to the long value in format 'yyyyMMddHHmmss'
     */
    static long convert(final Date datetime) {
        return Long.parseLong(FDTE_FORMATER.format(datetime));
    }

    static long convert(final ISweJulianDate julianDate) {
        return convert(new int[] {julianDate.year(), julianDate.month(), julianDate.day(),
                julianDate.hours(), julianDate.minutes(), (int)julianDate.seconds()});
    }

    static long convert(final int[] datetime) {
        if (null == datetime || datetime.length < 6) {
            throw new IllegalArgumentException("date[] length < 6");
        }

        final StringBuilder builder = new StringBuilder(14);
        formatYMD(builder, false, datetime[0], datetime[1], datetime[2]);
        return Long.parseLong(formatHMS(builder, false, datetime[3], datetime[4], datetime[5]).toString());
    }

    static StringBuilder format(final ISweJulianDate julianDate) {
        return format(julianDate, F4Y_2M_2D_2H_2M_2S_MS);
    }

    static StringBuilder format6(final ISweJulianDate julianDate) {
        return format(julianDate, F4Y_2M_2D_2H_2M_2S);
    }

    static StringBuilder format5(final ISweJulianDate julianDate) {
        return format(julianDate, F4Y_2M_2D_2H_2M);
    }

    static StringBuilder format(final ISweJulianDate julianDate, final String format) {
        final int[] datetime = julianDate.date();

        if (null == datetime || datetime.length < 3) {
            throw new IllegalArgumentException("Date/time part is not valid for formatting");
        }

        switch (format) {
            case F4Y_2M_2D_2H_2M_2S_MS: {
                final StringBuilder builder = new StringBuilder(22);
                formatYMD(builder, true, datetime[0], datetime[1], datetime[2]).append(STR_WS);
                formatHMS(builder, true,
                        datetime.length > 3 ? datetime[3] : julianDate.hours(),
                        datetime.length > 4 ? datetime[4] : julianDate.minutes());
                builder.append(CH_CN);

                double seconds = julianDate.seconds();
                if (seconds < d10) builder.append('0');
                builder.append(((int) (julianDate.seconds() * i100)) / d100);
                return builder;
            }

            case F4Y_2M_2D_2H_2M_2S: {
                final StringBuilder builder = new StringBuilder(24);
                formatYMD(builder, true, datetime[0], datetime[1], datetime[2]).append(STR_WS);
                return formatHMS(builder, true,
                        datetime.length > 3 ? datetime[3] : julianDate.hours(),
                        datetime.length > 4 ? datetime[4] : julianDate.minutes(),
                        datetime.length > 5 ? datetime[5] : (int)julianDate.seconds());
            }

            case F4Y_2M_2D_2H_2M: {
                final StringBuilder builder = new StringBuilder(20);
                formatYMD(builder, true, datetime[0], datetime[1], datetime[2]).append(STR_WS);
                return formatHMS(builder, true,
                        datetime.length > 3 ? datetime[3] : julianDate.hours(),
                        datetime.length > 4 ? datetime[4] : julianDate.minutes());
            }

            case F4Y_2M_2D: {
                final StringBuilder builder = new StringBuilder(12);
                return formatYMD(builder, true, datetime[0], datetime[1], datetime[2]);
            }

            case F2H_2M_2S: {
                final StringBuilder builder = new StringBuilder(12);
                return formatHMS(builder, true,
                        datetime.length > 3 ? datetime[3] : julianDate.hours(),
                        datetime.length > 4 ? datetime[4] : julianDate.minutes(),
                        datetime.length > 5 ? datetime[5] : (int)julianDate.seconds());
            }

            case F2H_2M: {
                final StringBuilder builder = new StringBuilder(8);
                return formatHMS(builder, true,
                        datetime.length > 3 ? datetime[3] : julianDate.hours(),
                        datetime.length > 4 ? datetime[4] : julianDate.minutes());
            }
        }

        final StringBuilder builder = new StringBuilder(20);
        formatYMD(builder, true, datetime[0], datetime[1], datetime[2]).append(STR_WS);
        return formatHMS(builder, true, datetime[3], datetime[4]);
    }

    static StringBuilder formatYMD(StringBuilder builder, boolean separate, int... ymd) {
        builder.append(ymd[0]);

        for (int i = 1; i < ymd.length; i++) {
            if (separate) builder.append(CH_DS);

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
