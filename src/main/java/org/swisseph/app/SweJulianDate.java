/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2020-05
 */

package org.swisseph.app;


import org.apache.commons.lang3.SerializationUtils;
import org.swisseph.api.ISweJulianDate;
import swisseph.SweDate;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

import static java.lang.Double.*;
import static java.util.Calendar.*;
import static org.swisseph.api.ISweConstants.*;

/**
 * @author Yura Krymlov
 * @version 1.2, 2023-03
 */
public class SweJulianDate implements ISweJulianDate {
    private static final long serialVersionUID = 1257948680125346489L;

    /**
     * time zone, julian day, delta-T, universal time (UT), ephemeris time (ET), local time (LT)
     */
    protected final double[] values = new double[]{UT_TMZ, NaN, NaN, NaN, NaN, d0};

    /**
     * local date: yyyy, mm, dd
     */
    protected final int[] date;

    public SweJulianDate(double julDay) {
        this(julDay, UT_TMZ);
    }

    public SweJulianDate(double julDay, float timeZone) {
        this.values[IDXD_TIMEZONE] = timeZone;
        this.values[IDXD_JULDAY] = julDay;
        this.date = null;
    }

    public SweJulianDate(final SweDate sweDate) {
        this(sweDate.getJulDay(), sweDate.getYear(), sweDate.getMonth(), sweDate.getDay(), sweDate.getHour());
    }

    public SweJulianDate(double julDay, int year, int month, int day, double utime) {
        this(julDay, new int[]{year, month, day}, utime);
    }

    public SweJulianDate(double julDay, final int[] date, double utime) {
        this(date, utime, UT_TMZ, utime);
        this.values[IDXD_JULDAY] = julDay;
    }

    public SweJulianDate(final int[] date, double utime, float timeZone, double localTime) {
        if (isNaN(localTime)) throw new SweRuntimeException("Valid local time is expected");
        this.values[IDXD_TIMEZONE] = timeZone;
        this.values[IDXD_LTIME] = localTime;
        this.values[IDXD_UTIME] = utime;
        this.date = date;
        makeValidTime();
    }

    public SweJulianDate(final int[] date, float timeZone, double localTime) {
        this.values[IDXD_TIMEZONE] = timeZone;
        this.values[IDXD_LTIME] = localTime;
        this.date = date;
        makeValidTime();
    }

    public SweJulianDate(final Calendar calendar) {
        this(new int[]{calendar.get(YEAR), calendar.get(MONTH) + 1, calendar.get(DAY_OF_MONTH),
                        calendar.get(HOUR_OF_DAY), calendar.get(MINUTE)},
                (float) (calendar.getTimeZone().getOffset(calendar.getTimeInMillis()) / d3600E03),
                localTime(calendar));
    }

    public static double localTime(final Calendar calendar) {
        return calendar.get(HOUR_OF_DAY) +
                calendar.get(MINUTE) / d60 +
                calendar.get(SECOND) / d3600 +
                calendar.get(MILLISECOND) / d3600E03;
    }

    public static double localTime(final int[] datetime) {
        double localTime = d0;
        if (datetime.length > 3) localTime = datetime[3];
        if (datetime.length > 4) localTime += datetime[4] / d60;
        if (datetime.length > 5) localTime += datetime[5] / d3600;
        if (datetime.length > 6) localTime += datetime[6] / d3600E03;
        return localTime;
    }

    @Override
    public double[] values() {
        return values;
    }

    @Override
    public float timeZone() {
        return (float) values[IDXD_TIMEZONE];
    }

    @Override
    public double julianDay() {
        return values[IDXD_JULDAY];
    }

    @Override
    public double deltaT() {
        return values[IDXD_DELTAT];
    }

    @Override
    public double utime() {
        return values[IDXD_UTIME];
    }

    @Override
    public double epheTime() {
        return values[IDXD_ETIME];
    }

    @Override
    public double localTime() {
        return values[IDXD_LTIME];
    }

    @Override
    public void localTime(final double ltime) {
        values[IDXD_LTIME] = ltime;
    }

    @Override
    public int year() {
        return date[IDXI_YEAR];
    }

    @Override
    public int month() {
        return date[IDXI_MONTH];
    }

    @Override
    public int day() {
        return date[IDXI_DAY];
    }

    @Override
    public int[] date() {
        return date;
    }

    @Override
    public void makeValidTime() {
        final int[] date = date();
        if (null != date && d0 != values[IDXD_LTIME] && date.length > 4 && date[4] > minutes()) {
            if (!isNaN(values[IDXD_UTIME])) values[IDXD_UTIME] += d1d3600E11;
            values[IDXD_LTIME] += d1d3600E11;
        }
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        if (!(another instanceof ISweJulianDate)) return false;
        return compare(((ISweJulianDate)another).julianDay(), julianDay()) == 0;
    }

    @Override
    public SweJulianDate clone() {
        return SerializationUtils.clone(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(julianDay());
    }

    @Override
    public String toString() {
        return "date=" + Arrays.toString(date)
                + ", vals=" + Arrays.toString(values);
    }

}
