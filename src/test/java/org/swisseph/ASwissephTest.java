/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2021-02
 */
package org.swisseph;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.swisseph.api.ISweGeoLocation;
import org.swisseph.app.SweGeoLocation;
import swisseph.SwissEph;

/**
 * @author Yura Krymlov
 * @version 1.0, 2021-02
 */
@Execution(ExecutionMode.CONCURRENT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public abstract class ASwissephTest {
    protected static final String EPHE_PATH = "ephe";

    protected static final ThreadLocal<ISwissEph> SWISS_EPHS = new ThreadLocal<>();
    protected static final ThreadLocal<ISwissEph> SWEPH_EXPS = new ThreadLocal<>();

    protected static final ISweGeoLocation GEO_CHENNAI = new SweGeoLocation(
            80 + (16 / 60.), 13 + (5 / 60.), 6.7);


    protected static ISwissEph newSwissEph() {
        return new SwissEph(EPHE_PATH);
    }

    protected static ISwissEph newSwephExp() {
        return new SwephNative(EPHE_PATH);
    }

    public static ISwissEph getSwissEph() {
        ISwissEph swissEph = SWISS_EPHS.get();

        if (null == swissEph) {
            swissEph = newSwissEph();
            SWISS_EPHS.set(swissEph);
        }

        return swissEph;
    }

    public static ISwissEph getSwephExp() {
        ISwissEph swissEph = SWEPH_EXPS.get();

        if (null == swissEph) {
            swissEph = newSwephExp();
            SWEPH_EXPS.set(swissEph);
        }

        return swissEph;
    }

    public static void closeSwissEph() {
        ISwissEph swissEph = SWISS_EPHS.get();
        if (null == swissEph) return;
        swissEph.swe_close();
        SWISS_EPHS.remove();
    }

    public static void closeSwephExp() {
        ISwissEph swissEph = SWEPH_EXPS.get();
        if (null == swissEph) return;
        swissEph.swe_close();
        SWEPH_EXPS.remove();
    }

    @AfterEach
    protected void callAfterEach() {
        closeSwissEph();
        closeSwephExp();
    }
}
