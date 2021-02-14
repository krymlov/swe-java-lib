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

import static java.lang.Double.NaN;
import static java.lang.Double.doubleToLongBits;
import static java.util.Calendar.*;
import static org.swisseph.api.ISweConstants.*;

/**
 * @author Yura Krymlov
 * @version 1.0, 2020-05
 */
public class SweJulianDate implements ISweJulianDate {
    private static final long serialVersionUID = -3541143847227431442L;

    protected final boolean gregorianCalendar;

    /**
     * time zone, julian day, delta T, universal time (decimal hours) 
     */
    protected final double[] values = new double[] { d0, NaN, NaN, NaN };
    
    /**
     * yyyy, mm, dd; optionally hh, mm, ss in local time
     */
    protected final int[] date;

    
    protected SweJulianDate(int[] date, boolean gregorianCalendar) {
        this.gregorianCalendar = gregorianCalendar;
        this.date = date;
    }

    public SweJulianDate(double julDay, int year, int month, int day, double utime, boolean gregorianCalendar) {
        this.gregorianCalendar = gregorianCalendar;
        this.date = new int[] { year, month, day };
        this.values[IDXD_JULDAY] = julDay;
        this.values[IDXD_UTIME] = utime;
    }

    public SweJulianDate(double julDay, int[] date, double utime, boolean gregorianCalendar) {
        this.gregorianCalendar = gregorianCalendar;
        this.values[IDXD_JULDAY] = julDay;
        this.values[IDXD_UTIME] = utime;
        this.date = date;
    }

    public SweJulianDate(int[] date, double utime, boolean gregorianCalendar) {
        this.gregorianCalendar = gregorianCalendar;
        this.values[IDXD_UTIME] = utime;
        this.date = date;
    }
    
    public SweJulianDate(double julDay, boolean gregorianCalendar, double timeZone) {
        this.gregorianCalendar = gregorianCalendar;
        this.values[IDXD_TIMEZONE] = timeZone;
        this.values[IDXD_JULDAY] = julDay;
        this.date = null;
    }
    
    public SweJulianDate(double julDay, double timeZone) {
        this.gregorianCalendar = ISweJulianDate.sweGregorianCalendar(julDay);
        this.values[IDXD_TIMEZONE] = timeZone;
        this.values[IDXD_JULDAY] = julDay;
        this.date = null;
    }
    
    public SweJulianDate(ISweJulianDate julDate, int[] date, double utime) {
        this.gregorianCalendar = julDate.gregorianCalendar();
        this.values[IDXD_TIMEZONE] = julDate.timeZone();
        this.values[IDXD_JULDAY] = julDate.julianDay();
        this.values[IDXD_DELTAT] = julDate.deltaT();
        this.values[IDXD_UTIME] = utime;
        this.date = date;
    }
    
    public SweJulianDate(int[] date, boolean gregorianCalendar, double timeZone) {
        this.gregorianCalendar = gregorianCalendar;
        this.values[IDXD_TIMEZONE] = timeZone;
        this.date = date;
    }
    
    public SweJulianDate(final SweDate sweDate) {
        this.gregorianCalendar = sweDate.getCalendarType();
        this.values[IDXD_JULDAY] = sweDate.getJulDay();
        this.values[IDXD_DELTAT] = sweDate.getDeltaT();
        this.values[IDXD_UTIME] = sweDate.getHour();
        this.date = new int[] { sweDate.getYear(), 
            sweDate.getMonth(), sweDate.getDay() };
    }
    
    public SweJulianDate(final double julDay) {
        this.gregorianCalendar = ISweJulianDate.sweGregorianCalendar(julDay);
        this.values[IDXD_JULDAY] = julDay;
        this.date = null;
    }

    public SweJulianDate(final Calendar calendar) {
        final int year = calendar.get(YEAR);
        final int month = calendar.get(MONTH) + 1;
        final int day = calendar.get(DAY_OF_MONTH);

        this.gregorianCalendar = gregorianCalendar(year, month, day);
        this.date = new int[] { year, month, day, calendar.get(HOUR_OF_DAY),
            calendar.get(MINUTE), calendar.get(SECOND), calendar.get(MILLISECOND)
        };

        this.values[IDXD_TIMEZONE] = calendar.getTimeZone()
            .getOffset(calendar.getTimeInMillis()) / d3600000;
    }

    @Override
    public boolean gregorianCalendar() {
        return gregorianCalendar;
    }
    
    @Override
    public double[] values() {
        return values;
    }

    @Override
    public double timeZone() {
        return values[IDXD_TIMEZONE];
    }
    
    @Override
    public ISweJulianDate timeZone(final double timeZone) {
        values[IDXD_TIMEZONE] = timeZone;
        return this;
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
        if ( date.length > IDXI_HOUR ) {
            return date[IDXI_HOUR];
        } else return i0;
    }

    @Override
    public int minutes() {
        if ( date.length > IDXI_MINUTE ) {
            return date[IDXI_MINUTE];
        } else return i0;
    }

    @Override
    public double seconds() {
        final double utime = values[IDXD_UTIME];
        
        if ( ! Double.isNaN(utime) ) return useconds();
        else if ( date.length > IDXI_SECONDS ) {
            return date[IDXI_SECONDS];
        } else return d0;
    }
    
    @Override
    public int uhours() {
        final double utime = values[IDXD_UTIME];
        if ( Double.isNaN(utime) ) return i0;
        return (int)utime;
    }
    
    @Override
    public int uminutes() {
        double utime = values[IDXD_UTIME];
        if ( Double.isNaN(utime) ) return i0;
        final int hour = (int)utime;
        utime -= hour; utime *= d60;
        return (int)utime;
    }
    
    @Override
    public double useconds() {
        double utime = values[IDXD_UTIME];
        if ( Double.isNaN(utime) ) return d0;
        final int hour = (int)utime;
        utime -= hour; utime *= d60;
        final int min = (int)utime;
        utime -= min; utime *= d60;
        return utime;
    }

    @Override
    public double utime() {
        return values[IDXD_UTIME];
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(values[IDXD_JULDAY]);
        result = prime * result + (int)(temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;

        final double julDay = this.values[IDXD_JULDAY];
        if ( Double.isNaN(julDay) ) return false;
        if ( !(obj instanceof ISweJulianDate) ) return false;

        final ISweJulianDate other = (ISweJulianDate)obj;
        return doubleToLongBits(julDay) == doubleToLongBits(other.julianDay());
    }

    @Override
    public String toString() {
        return "date=" + Arrays.toString(date) 
            + ", vals=" + Arrays.toString(values);
    }

}
