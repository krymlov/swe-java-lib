/*
* Copyright (C) By the Author
* Author    Yura Krymlov
* Created   2020-05
*/

package org.swisseph;


import swisseph.SwephExp;

/**
 * @author Yura Krymlov
 * @version 1.1, 2022-05
 */
public class SwephNative implements ISwissEph {

    public static final String SWISSEPH_LIBRARY_NAME = "swe-2.10.03";
    protected static final SwephExp swephExp = new SwephExp(SWISSEPH_LIBRARY_NAME);

    protected String ephe_path;
    protected boolean topo_set;
    protected double geo_alt;
    
    public SwephNative(String ephePath) {
        swe_set_ephe_path(ephePath);
    }

    // ----------------------------------------------------------------------
    
    @Override
    public boolean swe_set_topo() {
        return topo_set;
    }
    
    @Override
    public double swe_get_geo_alt() {
        return geo_alt;
    }

    @Override
    public String swe_get_ephe_path() {
        return ephe_path;
    }

    @Override
    public void swe_set_ephe_path(final String ephe_path) {
        ISwissEph.super.swe_set_ephe_path(ephe_path);
        this.ephe_path = ephe_path;
    }

    @Override
    public void swe_set_topo(final double geolon, final double geolat, final double geoalt) {
        ISwissEph.super.swe_set_topo(geolon, geolat, geoalt);
        this.topo_set = true;
        this.geo_alt = geoalt;
    }

}
