/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.swisseph.api.ISweGeoLocation;
import org.swisseph.api.ISweJulianDate;
import org.swisseph.app.SweGeoLocation;
import org.swisseph.app.SweJulianDate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Reader for a Jagannatha Hora <code>.jhd</code> birth data file.
 * <p>
 * The format is one value per line:
 * <pre>
 *  1  month
 *  2  day
 *  3  year
 *  4  local time, decimal hours
 *  5  time zone, hours, <b>sign reversed</b> (-3 means UTC+3)
 *  6  longitude, <b>sign reversed</b> (-27.13 means 27 deg 13 min East)
 *  7  latitude (49.45 means 49 deg 45 min North)
 *  8  altitude, meters
 *  9  .. 12  DST / display settings, not used here
 * 13  city
 * 14  country
 * 15+ further settings, not used here
 * </pre>
 * Longitude and latitude are stored as <b>degrees.minutes</b>, not decimal degrees:
 * 49.45 is 49&deg;45', i.e. 49.75. Reading them as decimal degrees would put
 * {@code IuriiK.jhd} some 34 km away from Starokostyantyniv; reading them as
 * degrees.minutes puts it within a kilometre, and the resulting julian day and
 * ascendant match <code>swetest</code> to every digit it prints.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
public final class JhdChart {

    private final int month, day, year;
    private final double localTime;
    private final float timeZone;
    private final double longitude, latitude, altitude;
    private final String city, country;

    private JhdChart(List<String> lines) {
        this.month = (int) Double.parseDouble(lines.get(0));
        this.day = (int) Double.parseDouble(lines.get(1));
        this.year = (int) Double.parseDouble(lines.get(2));
        this.localTime = Double.parseDouble(lines.get(3));
        this.timeZone = (float) -Double.parseDouble(lines.get(4));
        this.longitude = degreesMinutes(-Double.parseDouble(lines.get(5)));
        this.latitude = degreesMinutes(Double.parseDouble(lines.get(6)));
        this.altitude = Double.parseDouble(lines.get(7));
        this.city = lines.size() > 12 ? lines.get(12) : "";
        this.country = lines.size() > 13 ? lines.get(13) : "";
    }

    /**
     * @param value degrees with the minutes in the fractional part, e.g. 49.45
     * @return decimal degrees, e.g. 49.75
     */
    static double degreesMinutes(double value) {
        final double sign = Math.signum(value);
        final double abs = Math.abs(value);
        final double deg = Math.floor(abs);
        final double min = (abs - deg) * 100.;
        return sign * (deg + min / 60.);
    }

    public static JhdChart read(String resource) {
        try (InputStream in = JhdChart.class.getClassLoader().getResourceAsStream(resource)) {
            if (null == in) throw new IllegalArgumentException("resource not found: " + resource);
            final List<String> lines = new ArrayList<>();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                for (String l = r.readLine(); null != l; l = r.readLine()) lines.add(l.trim());
            }
            return new JhdChart(lines);
        } catch (IOException e) {
            throw new IllegalStateException("cannot read " + resource, e);
        }
    }

    /** local date as {year, month, day, hours, minutes} */
    public int[] date() {
        final double[] hms = ISweJulianDate.splitTime(localTime);
        return new int[]{year, month, day, (int) hms[0], (int) hms[1]};
    }

    public ISweJulianDate julianDate() {
        return new SweJulianDate(date(), timeZone, localTime);
    }

    public ISweGeoLocation geoLocation() {
        return new SweGeoLocation(longitude, latitude, altitude);
    }

    public double localTime() { return localTime; }
    public float timeZone() { return timeZone; }
    public double longitude() { return longitude; }
    public double latitude() { return latitude; }
    public double altitude() { return altitude; }
    public String city() { return city; }
    public String country() { return country; }

    @Override
    public String toString() {
        return city + ", " + country + " " + year + "-" + month + "-" + day
                + " " + localTime + "h tz=" + timeZone + " " + longitude + "E " + latitude + "N";
    }
}
