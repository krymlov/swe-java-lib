/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2019-07
 */

package org.swisseph.utils;


import java.math.BigDecimal;

import static org.swisseph.api.ISweConstants.*;
import static swisseph.SweConst.ODEGREE_CHAR;

/**
 * @author Yura Krymlov
 * @version 1.1, 2019-07
 */
public interface IDegreeUtils {

    /**
     * The method is intended to convert latitude DD 49.758665 to DMS like 49°45'31"N
     */
    static StringBuilder toLAT(final double ddeg) {
        return toDMS(Math.abs(ddeg)).append(ddeg >= 0 ? LAT_NORTH : LAT_SOUTH);
    }

    /**
     * The method is intended to convert longitude DD 27.199346 to DMS like 27°11'58"E
     */
    static StringBuilder toLON(final double ddeg) {
        return toDMS(Math.abs(ddeg)).append(ddeg >= 0 ? LONG_EAST : LONG_WEST);
    }

    /**
     * The method is intended to convert latitude DD 49.758665 to DMS like 49°45'31.00"N
     */
    static StringBuilder toLATms(final double ddeg) {
        return toDMSms(Math.abs(ddeg)).append(ddeg >= 0 ? LAT_NORTH : LAT_SOUTH);
    }

    /**
     * The method is intended to convert longitude DD 27.199346 to DMS like 27°11'58.00"E
     */
    static StringBuilder toLONms(final double ddeg) {
        return toDMSms(Math.abs(ddeg)).append(ddeg >= 0 ? LONG_EAST : LONG_WEST);
    }

    /**
     * The method is intended to convert double like 49.75764 to DMS like 49°45'28"
     */
    static StringBuilder toDMS(final double ddeg) {
        return toDMS(ddeg, false);
    }

    /**
     * The method is intended to convert double like 49.75764 to DMS like 49°45'28"
     *
     * @param ddeg       to convert (it will be rounded to a second)
     * @param timeFormat if true then 49:45:28
     * @return string like 49°45'28" or 49:45:28
     */
    static StringBuilder toDMS(double ddeg, boolean timeFormat) {
        final StringBuilder builder = new StringBuilder(9);

        if (ddeg < 0) {
            ddeg = -ddeg;
            builder.append(CH_DS);
        }

        ddeg += d05d3600;
        final int ideg = (int) ddeg;

        ddeg -= ideg;
        ddeg *= d60;
        final int imin = (int) ddeg;

        ddeg -= imin;
        ddeg *= d60;
        final int isec = (int) ddeg;

        if (ideg < i10) builder.append(CH_ZR);
        builder.append(ideg);

        if (timeFormat) builder.append(CH_CN);
        else builder.append(ODEGREE_CHAR);

        if (imin < i10) builder.append(CH_ZR);
        builder.append(imin);

        if (timeFormat) builder.append(CH_CN);
        else builder.append(CH_SQ);

        if (isec < i10) builder.append(CH_ZR);
        builder.append(isec);

        if (!timeFormat) builder.append(CH_DQ);

        return builder;
    }

    static StringBuilder toDMS(double ddeg, int dms, boolean timeFormat) {
        final StringBuilder builder = new StringBuilder(9);

        if (ddeg < 0) {
            ddeg = -ddeg;
            builder.append(CH_DS);
        }

        ddeg += d05d3600;
        final int ideg = (int) ddeg;

        if (ideg < i10) builder.append(CH_ZR);
        builder.append(ideg);

        if (!timeFormat) builder.append(ODEGREE_CHAR);
        if (dms == 1) return builder;

        if (timeFormat) builder.append(CH_CN);

        ddeg -= ideg;
        ddeg *= d60;
        final int imin = (int) ddeg;

        if (imin < i10) builder.append(CH_ZR);
        builder.append(imin);

        if (!timeFormat) builder.append(CH_SQ);
        if (dms == 2) return builder;

        ddeg -= imin;
        ddeg *= d60;
        final int isec = (int) ddeg;

        if (timeFormat) builder.append(CH_CN);

        if (isec < i10) builder.append(CH_ZR);
        builder.append(isec);

        if (!timeFormat) builder.append(CH_DQ);
        return builder;
    }

    /**
     * The method is intended to convert double like 49.75764 to DMS like 49°45'27.50"
     */
    static StringBuilder toDMSms(final double ddeg) {
        return toDMSms(ddeg, false);
    }

    /**
     * The method is intended to convert double like 49.75764 to DMS like 49°45'27.50"
     *
     * @param ddeg       to convert
     * @param timeFormat if true then 49:45:27.50
     * @return string like 49°45'27.50" or 49:45:27.50
     */
    static StringBuilder toDMSms(double ddeg, boolean timeFormat) {
        final StringBuilder builder = new StringBuilder(12);

        if (ddeg < 0) {
            ddeg = -ddeg;
            builder.append(CH_DS);
        }

        final int ideg = (int) ddeg;

        ddeg -= ideg;
        ddeg *= d60;
        final int imin = (int) ddeg;

        ddeg -= imin;
        ddeg *= d60;
        final int isec = (int) ddeg;

        ddeg -= isec;
        ddeg *= d100;

        final int imls;
        if (ddeg > 99.) imls = (int) ddeg;
        else imls = (int) Math.round(ddeg);

        if (ideg < i10) builder.append(CH_ZR);
        builder.append(ideg);

        if (timeFormat) builder.append(CH_CN);
        else builder.append(ODEGREE_CHAR);

        if (imin < i10) builder.append(CH_ZR);
        builder.append(imin);

        if (timeFormat) builder.append(CH_CN);
        else builder.append(CH_SQ);

        if (isec < i10) builder.append(CH_ZR);
        builder.append(isec).append(CH_DT);

        if (imls < i10) builder.append(CH_ZR);
        builder.append(imls);

        if (!timeFormat) builder.append(CH_DQ);

        return builder;
    }

    /**
     * The method is intended to convert decimal degree like 49.75764 to integer degree like 49452750
     *
     * @param ddeg degree to convert
     * @return integer degree like 49452750
     */
    static int toIDMSms(double ddeg) {
        final StringBuilder builder = new StringBuilder(12);

        if (ddeg < 0) {
            ddeg = -ddeg;
            builder.append(CH_DS);
        }

        final int ideg = (int) ddeg;

        ddeg -= ideg;
        ddeg *= d60;
        final int imin = (int) ddeg;

        ddeg -= imin;
        ddeg *= d60;
        final int isec = (int) ddeg;

        ddeg -= isec;
        ddeg *= d100;

        final int imls;
        if (ddeg > 99.) imls = (int) ddeg;
        else imls = (int) Math.round(ddeg);

        builder.append(ideg);

        if (imin < i10) builder.append(CH_ZR);
        builder.append(imin);

        if (isec < i10) builder.append(CH_ZR);
        builder.append(isec);

        if (imls < i10) builder.append(CH_ZR);
        return Integer.parseInt(builder.append(imls).toString());
    }

    /**
     * The method is intended to convert integer ideg like 49452750 to DMS string like 49°45'27.50"
     */
    static StringBuilder toDMSms(final int ideg) {
        return toDMSms(ideg, false);
    }

    /**
     * The method is intended to convert integer ideg like 49452750 to DMS string like 49°45'27.50"
     *
     * @param ideg       to convert
     * @param timeFormat if true then 49:45:27.50
     * @return string like 49°45'27.50" or 49:45:27.50
     */
    static StringBuilder toDMSms(int ideg, boolean timeFormat) {
        final StringBuilder builder = new StringBuilder(16);

        if (ideg < 0) {
            ideg = -ideg;
            builder.append(CH_DS);
        }

        int deg = ideg / i100;
        final int imls = ideg % i100;
        final int isec = deg % i100;
        final int imin = (deg /= i100) % i100;

        deg /= i100;
        if (deg < i10) builder.append(CH_ZR);
        builder.append(deg);

        if (timeFormat) builder.append(CH_CN);
        else builder.append(ODEGREE_CHAR);

        if (imin < i10) builder.append(CH_ZR);
        builder.append(imin);

        if (timeFormat) builder.append(CH_CN);
        else builder.append(CH_SQ);

        if (isec < i10) builder.append(CH_ZR);
        builder.append(isec).append(CH_DT);

        if (imls < i10) builder.append(CH_ZR);
        builder.append(imls);

        if (!timeFormat) builder.append(CH_DQ);

        return builder;
    }

    /**
     * <pre>
     * The method is intended to convert integer like 49452750 to decimal ideg like 49.75764
     *
     * Decimal Degrees = degrees + (minutes/60.) + (seconds/3600.)
     * 49°45'27.50" -> 49.75764
     * </pre>
     *
     * @param ideg to convert
     * @return decimal ideg like 49.75764
     */
    static double toDDms(int ideg) {
        boolean ng = false;

        if (ideg < 0) {
            ideg = -ideg;
            ng = true;
        }

        if (i0 == ideg) return 0d;

        int arg = ideg / i100;
        final int isec = (arg % i100);
        final int imls = (ideg % i100);
        final int imin = (arg /= i100) % i100;
        double ddeg = (arg / i100); // it is correct

        ddeg += imin / d60;
        ddeg += isec / d3600;
        ddeg += imls / d360000;

        ddeg += d1d3600000;

        return ng ? -ddeg : ddeg;
    }

    static double round(double d, int scale) {
        return round(d, scale, 4);
    }

    static double round(double d, int scale, int roundingMethod) {
        try {
            double rounded = (new BigDecimal(Double.toString(d)))
                    .setScale(scale, roundingMethod).doubleValue();
            return rounded == 0.0D ? 0.0D * d : rounded;
        } catch (NumberFormatException var6) {
            return Double.isInfinite(d) ? d : 0.0D / 0.0;
        }
    }

}
