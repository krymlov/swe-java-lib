/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2021-02
 */
package org.swisseph;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.swisseph.api.ISweGeoLocation;
import org.swisseph.app.SweGeoLocation;
import swisseph.SwissEph;

import static org.swisseph.api.ISweConstants.EPHE_PATH;

/**
 * @author Yura Krymlov
 * @version 1.0, 2021-02
 */
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public abstract class AbstractTest {
    protected static final ThreadLocal<ISwissEph> SWISS_EPHS = new ThreadLocal<>();
    protected static final ThreadLocal<ISwissEph> SWEPH_EXPS = new ThreadLocal<>();

    protected static final ISweGeoLocation GEO_CHENNAI = new SweGeoLocation(
            80 + (16 / 60.), 13 + (5 / 60.), 6.7);

    protected static final ISweGeoLocation GEO_LUCKNOW = new SweGeoLocation(
            81.83, 25.95, 123);

    protected static final ISweGeoLocation GEO_GREENWICH = new SweGeoLocation(
            0., 51.50, 40);


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
        try (ISwissEph swissEph = SWISS_EPHS.get()) {
            if (null == swissEph) return;
            SWISS_EPHS.remove();
        }
    }

    public static void closeSwephExp() {
        try (ISwissEph swissEph = SWEPH_EXPS.get()) {
            if (null == swissEph) return;
            SWEPH_EXPS.remove();
        }
    }

    @AfterEach
    protected void callAfterEach() {
        closeSwissEph();
        closeSwephExp();
    }
}
