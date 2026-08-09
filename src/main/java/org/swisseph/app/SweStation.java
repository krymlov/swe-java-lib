/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */

package org.swisseph.app;

import org.swisseph.api.ISweJulianDate;
import org.swisseph.api.ISweStation;

import static org.swisseph.utils.IDegreeUtils.toDMSms;

/**
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
public class SweStation implements ISweStation {
    private static final long serialVersionUID = 4126853925694147531L;

    protected final ISweJulianDate julianDate;
    protected final double longitude;
    protected final int planet;
    protected final boolean retrograde;

    public SweStation(int planet, ISweJulianDate julianDate, double longitude, boolean retrograde) {
        this.julianDate = julianDate;
        this.longitude = longitude;
        this.planet = planet;
        this.retrograde = retrograde;
    }

    @Override
    public int planet() {
        return planet;
    }

    @Override
    public ISweJulianDate julianDate() {
        return julianDate;
    }

    @Override
    public double longitude() {
        return longitude;
    }

    @Override
    public boolean retrograde() {
        return retrograde;
    }

    @Override
    public String toString() {
        return "planet " + planet + " turns " + (retrograde ? "retrograde" : "direct")
                + " at " + toDMSms(longitude, true) + " on JD " + julianDate.julianDay();
    }
}
