/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2020-05
 */

package org.swisseph.app;


import org.swisseph.api.ISweJulianDate;
import swisseph.SweDate;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

import static java.lang.Double.NaN;
import static java.lang.Double.compare;
import static java.util.Calendar.*;
import static org.swisseph.api.ISweConstants.*;

/**
 * @author Yura Krymlov
 * @version 1.2, 2023-03
 */
public class SweJulianDate implements ISweJulianDate {
    private static final long serialVersionUID = 4570855450497987964L;

    /**
     * time zone, julian day, delta-T, universal time (UT), ephemeris time (ET), local time (LT)
     */
    protected final double[] values = new double[]{d0, NaN, NaN, NaN, NaN, NaN};

    /**
     * local date: yyyy, mm, dd
     */
    protected final int[] date;


    public SweJulianDate(final double julDay) {
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

    public SweJulianDate(double julDay, int[] date, double utime) {
        this(date, utime, UT_TMZ, utime);
        this.values[IDXD_JULDAY] = julDay;
    }

    public SweJulianDate(int[] date, double utime, float timeZone, double localTime) {
        this.values[IDXD_TIMEZONE] = timeZone;
        this.values[IDXD_LTIME] = localTime;
        this.values[IDXD_UTIME] = utime;
        this.date = date;
    }

    public SweJulianDate(int[] date, float timeZone, double localTime) {
        this.values[IDXD_TIMEZONE] = timeZone;
        this.values[IDXD_LTIME] = localTime;
        this.date = date;
    }

    public SweJulianDate(final Calendar calendar) {
        this.date = new int[]{calendar.get(YEAR), calendar.get(MONTH) + 1, calendar.get(DAY_OF_MONTH), calendar.get(HOUR_OF_DAY), calendar.get(MINUTE)};
        this.values[IDXD_TIMEZONE] = (float) (calendar.getTimeZone().getOffset(calendar.getTimeInMillis()) / d3600E03);
        this.values[IDXD_LTIME] =
                calendar.get(Calendar.HOUR_OF_DAY) +
                calendar.get(Calendar.MINUTE)/d60 +
                calendar.get(Calendar.SECOND)/d3600 +
                calendar.get(Calendar.MILLISECOND)/d3600E03;
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

    /*
    @Override
    public double dseconds() {
        if (date.length > IDXI_NANOS) {
            double dsec = date[IDXI_SECONDS];
            dsec += (date[IDXI_MILLIS] / d1000);
            dsec += (date[IDXI_NANOS] / d1000E9);
            return dsec;
        } else if (date.length > IDXI_MILLIS) {
            double dsec = date[IDXI_SECONDS];
            dsec += (date[IDXI_MILLIS] / d1000);
            return dsec;
        } else if (!isNaN(values[IDXD_UTIME])) {
            return useconds();
        } else if (date.length > IDXI_SECONDS) {
            return date[IDXI_SECONDS];
        } else return d0;
    }*/

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        if (another == null || getClass() != another.getClass()) return false;
        return compare(((SweJulianDate) another).julianDay(), julianDay()) == 0;
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
