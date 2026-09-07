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
     * The same as {@link #toDMSms(double)}, but kept <b>inside the segment the value belongs
     * to</b> - a sign, a naksatra, a pada.
     * <p>
     * A retrograde ingress enters at the <i>top</i> of its segment: Rahu reaches Vrishabha at
     * 59.99999986, and that hair is far under the hundredth of an arcsecond rendered here, so
     * plain rounding carries it to {@code 60°00'00.00"} - the start of the sign <i>above</i>.
     * A whole backward listing then reads R11 at 330, R12 at 360, R1 at 30, R2 at 60, every row
     * naming the boundary of its neighbour and none of them a position in the sign it is
     * labelled with.
     * <p>
     * Truncating to the last whole hundredth keeps it where it belongs - {@code 59°59'59.99"} -
     * and costs 0.01", four orders below what Swiss Ephemeris itself claims. It happens
     * <b>only</b> when rounding would carry across a boundary, so every other value renders
     * exactly as before.
     *
     * <b>Both ends of the segment</b>: the value is first snapped up when it is only a rounding
     * artefact short of the next boundary - which is where the transit search was actually
     * aiming, and what {@link IModuloUtils#snapToSegment(double, double)} does for the index -
     * and only then kept from rounding onto that boundary from below. Doing one without the
     * other renders the two halves of the same answer against different boundaries: the author's
     * {@code (RA) = 300°00'00.00" -> Rasi= MAK ... | 30°00'00.00"} had a longitude naming
     * the sign above and a degree naming no position in any sign at all.
     *
     * @param length the segment width - 30 for a rasi, 13°20' for a naksatra; 0 for a family
     *               that is a point rather than a range, which renders unchanged. Where a row
     *               names several at once - a rasi, a naksatra and a pada - pass the
     *               <b>pada</b>: every rasi and naksatra boundary is a multiple of 3°20',
     *               so the finest length satisfies all three
     */
    static StringBuilder toDMSmsWithin(final double ddeg, final double length) {
        return toDMSms(keepWithin(IModuloUtils.snapToSegment(length, ddeg), length, d1 / d360000));
    }

    /**
     * {@link #toDMS(double, boolean, boolean)} rounded to the whole second, and kept inside its
     * own segment the way {@link #toDMSmsWithin(double, double)} is - so the top of a sign reads
     * {@code 29°59'59"} rather than {@code 30°00'00"}.
     */
    static StringBuilder toDMSWithin(final double ddeg, final double length) {
        return toDMS(keepWithin(IModuloUtils.snapToSegment(length, ddeg), length, d1 / d3600),
                false, true);
    }

    /**
     * The value, stepped back to the last whole {@code unit} when rounding to that unit would
     * carry it onto the next segment boundary. Everything else is returned untouched.
     */
    static double keepWithin(final double ddeg, final double length, final double unit) {
        if (length > d0) {
            final double rem = ddeg % length;
            if (rem > d0 && length - rem < unit / d2) {
                return Math.floor(ddeg / unit) * unit;
            }
        }
        return ddeg;
    }

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
        return toDMS(ddeg, timeFormat, true);
    }

    static StringBuilder toDMS(double ddeg, boolean timeFormat, boolean round) {
        final StringBuilder builder = new StringBuilder(9);

        if (ddeg < 0) {
            ddeg = -ddeg;
            builder.append(CH_DS);
        }

        if (round) ddeg += d05d3600;
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
        return toDMS(ddeg, dms, timeFormat, true);
    }

    static StringBuilder toDMS(double ddeg, int dms, boolean timeFormat, boolean round) {
        final StringBuilder builder = new StringBuilder(9);

        if (ddeg < 0) {
            ddeg = -ddeg;
            builder.append(CH_DS);
        }

        if (round) ddeg += d05d3600;
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

        ddeg += D05_CSEC;
        final int ideg = (int) ddeg;

        ddeg -= ideg;
        ddeg *= d60;
        final int imin = (int) ddeg;

        ddeg -= imin;
        ddeg *= d60;
        final int isec = (int) ddeg;

        ddeg -= isec;
        ddeg *= d100;

        final int imls = ddeg < d99 ? (int) ddeg : i99;

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

        ddeg += D05_CSEC;
        final int ideg = (int) ddeg;

        ddeg -= ideg;
        ddeg *= d60;
        final int imin = (int) ddeg;

        ddeg -= imin;
        ddeg *= d60;
        final int isec = (int) ddeg;

        ddeg -= isec;
        ddeg *= d100;

        final int imls = ddeg < d99 ? (int) ddeg : i99;

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
     * <p>
     * The result is the exact decimal value of the DMS triple. It used to be biased
     * by +1 milli-arcsecond to compensate for {@link #toIDMSms(double)} truncating
     * instead of rounding; that truncation is fixed, so the bias is gone.
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
