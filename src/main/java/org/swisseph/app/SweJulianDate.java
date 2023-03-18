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

import static java.lang.Double.*;
import static java.util.Calendar.*;
import static org.swisseph.api.ISweConstants.*;

/**
 * @author Yura Krymlov
 * @version 1.2, 2023-03
 */
public class SweJulianDate implements ISweJulianDate {
    private static final long serialVersionUID = -1805221056986936494L;

    /**
     * time zone, julian day, delta T, universal time (decimal hours), ephemeris time (et)
     */
    protected final double[] values = new double[]{d0, NaN, NaN, NaN, NaN};

    /**
     * zoned date/time!<br>
     * date: yyyy, mm, dd + time: hh, mm, ss, millis
     */
    protected final int[] date;


    protected SweJulianDate(int[] datetime) {
        this.date = datetime;
    }

    public SweJulianDate(double julDay, int year, int month, int day, double utime) {
        this.date = new int[]{year, month, day};
        this.values[IDXD_JULDAY] = julDay;
        this.values[IDXD_UTIME] = utime;
    }

    public SweJulianDate(double julDay, int[] date, double utime) {
        this.values[IDXD_JULDAY] = julDay;
        this.values[IDXD_UTIME] = utime;
        this.date = date;
    }

    public SweJulianDate(int[] date, double utime, float timeZone) {
        this.values[IDXD_TIMEZONE] = timeZone;
        this.values[IDXD_UTIME] = utime;
        this.date = date;
    }

    public SweJulianDate(double julDay, float timeZone) {
        this.values[IDXD_TIMEZONE] = timeZone;
        this.values[IDXD_JULDAY] = julDay;
        this.date = null;
    }

    public SweJulianDate(int[] datetime, float timeZone) {
        this.values[IDXD_TIMEZONE] = timeZone;
        this.date = datetime;
    }

    public SweJulianDate(ISweJulianDate julDate, int[] date, double utime) {
        this.values[IDXD_TIMEZONE] = julDate.timeZone();
        this.values[IDXD_JULDAY] = julDate.julianDay();
        this.values[IDXD_DELTAT] = julDate.deltaT();
        this.values[IDXD_UTIME] = utime;
        this.date = date;
    }

    public SweJulianDate(final SweDate sweDate) {
        this.values[IDXD_JULDAY] = sweDate.getJulDay();
        this.values[IDXD_DELTAT] = sweDate.getDeltaT();
        this.values[IDXD_UTIME] = sweDate.getHour();
        this.date = new int[]{sweDate.getYear(),
                sweDate.getMonth(), sweDate.getDay()};
    }

    public SweJulianDate(final double julDay) {
        this.values[IDXD_JULDAY] = julDay;
        this.date = null;
    }

    public SweJulianDate(final Calendar calendar) {
        final int year = calendar.get(YEAR);
        final int month = calendar.get(MONTH) + 1;
        final int day = calendar.get(DAY_OF_MONTH);

        this.date = new int[]{year, month, day, calendar.get(HOUR_OF_DAY),
                calendar.get(MINUTE), calendar.get(SECOND), calendar.get(MILLISECOND)
        };

        this.values[IDXD_TIMEZONE] = calendar.getTimeZone()
                .getOffset(calendar.getTimeInMillis()) / d3600000;
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
    public double ephemerisTime() {
        return values[IDXD_ETIME];
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
    public int hours() {
        return date.length > IDXI_HOUR ? date[IDXI_HOUR] : i0;
    }

    @Override
    public int minutes() {
        return date.length > IDXI_MINUTE ? date[IDXI_MINUTE] : i0;
    }

    @Override
    public int seconds() {
        return date.length > IDXI_SECONDS ? date[IDXI_SECONDS] : i0;
    }

    public int millis() {
        return date.length > IDXI_MILLIS ? date[IDXI_MILLIS] : i0;
    }

    @Override
    public double dseconds() {
        if (date.length > IDXI_MILLIS) {
            return date[IDXI_SECONDS] + (date[IDXI_MILLIS] / d1000);
        } else if (!isNaN(values[IDXD_UTIME])) {
            return useconds();
        } else if (date.length > IDXI_SECONDS) {
            return date[IDXI_SECONDS];
        } else return d0;
    }

    @Override
    public double utime() {
        return values[IDXD_UTIME];
    }

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
