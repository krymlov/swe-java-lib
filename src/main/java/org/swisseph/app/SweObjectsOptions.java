/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2019-12
 */

package org.swisseph.app;

import org.swisseph.api.ISweAyanamsa;
import org.swisseph.api.ISweHouseSystem;
import org.swisseph.api.ISweObjectsOptions;

import static org.swisseph.api.ISweConstants.CH_VS;
import static org.swisseph.api.ISweConstants.d0;
import static org.swisseph.app.SweAyanamsa.getLahiri;
import static org.swisseph.app.SweAyanamsa.getTrueSpica;

/**
 * @author Yura Krymlov
 * @version 1.1, 2019-12
 */
public class SweObjectsOptions implements ISweObjectsOptions {
    private static final long serialVersionUID = 9075839757472544858L;

    public static final ISweObjectsOptions LAHIRI_TRADITIONAL = new SweObjectsOptions(getLahiri());
    public static final ISweObjectsOptions LAHIRI_CITRAPAKSA = new SweObjectsOptions(getTrueSpica());

    protected final ISweHouseSystem houseSystem;
    protected final ISweAyanamsa ayanamsa;

    /**
     * true or mean node to use for RA/KE calculation
     */
    protected final boolean trueNode;

    /**
     * Special preset of flags for planets calculation
     */
    protected final int calcFlags;

    // Reference date (Julian day), if sid_mode is SE_SIDM_USER
    // Initial ayanamsha at t0, if sid_mode is SE_SIDM_USER
    protected final double initialJulianDay, initialAyanamsa;

    public SweObjectsOptions() {
        this(SweAyanamsa.byDefault());
    }

    /**
     * @param trueNode true or mean node to use for RA/KE calculation
     */
    public SweObjectsOptions(boolean trueNode) {
        this(SweAyanamsa.byDefault(), trueNode);
    }

    public SweObjectsOptions(ISweAyanamsa ayanamsa) {
        this(ayanamsa, false);
    }

    /**
     * @param trueNode true or mean node to use for RA/KE calculation
     */
    public SweObjectsOptions(ISweAyanamsa ayanamsa, boolean trueNode) {
        this(ayanamsa, SweHouseSystem.byDefault(), trueNode);
    }

    /**
     * @param trueNode true or mean node to use for RA/KE calculation
     */
    public SweObjectsOptions(ISweHouseSystem houseSystem, boolean trueNode) {
        this(SweAyanamsa.byDefault(), houseSystem, trueNode);
    }

    public SweObjectsOptions(ISweAyanamsa ayanamsa, ISweHouseSystem houseSystem, boolean trueNode) {
        this(ayanamsa, houseSystem, trueNode, DEFAULT_SS_TRUEPOS_NONUT_SPEED_FLAGS);
    }

    public SweObjectsOptions(ISweAyanamsa ayanamsa, ISweHouseSystem houseSystem, boolean trueNode, int calcFlags) {
        this(ayanamsa, houseSystem, trueNode, calcFlags, d0, d0);
    }

    public SweObjectsOptions(ISweObjectsOptions opt, ISweAyanamsa ayanamsa) {
        this(ayanamsa, opt.houseSystem(), opt.trueNode(), opt.calcFlags(),
                opt.initialJulianDay(), opt.initialAyanamsa());
    }

    public SweObjectsOptions(ISweObjectsOptions opt, ISweHouseSystem houseSystem) {
        this(opt.ayanamsa(), houseSystem, opt.trueNode(), opt.calcFlags(),
                opt.initialJulianDay(), opt.initialAyanamsa());
    }

    /**
     * This sets a user ayanamsha mode for the sidereal planet calculations.
     *
     * @param initialJulianDay   - initial julian day (reference date)
     * @param initialAyanamsa - initial ayanamsa (at reference date)
     *                        This is (tropical position - sidereal position) at ayanamsaJulday
     */
    public SweObjectsOptions(ISweAyanamsa ayanamsa, ISweHouseSystem houseSystem, boolean trueNode,
                             int calcFlags, double initialJulianDay, double initialAyanamsa) {
        this.initialJulianDay = initialJulianDay;
        this.initialAyanamsa = initialAyanamsa;
        this.houseSystem = houseSystem;
        this.calcFlags = calcFlags;
        this.trueNode = trueNode;
        this.ayanamsa = ayanamsa;
    }

    @Override
    public ISweHouseSystem houseSystem() {
        return houseSystem;
    }

    @Override
    public ISweAyanamsa ayanamsa() {
        return ayanamsa;
    }

    @Override
    public boolean trueNode() {
        return trueNode;
    }

    @Override
    public double initialJulianDay() {
        return initialJulianDay;
    }

    @Override
    public double initialAyanamsa() {
        return initialAyanamsa;
    }

    @Override
    public int calcFlags() {
        return calcFlags;
    }

    @Override
    public ISweObjectsOptions clone() throws CloneNotSupportedException {
        return (ISweObjectsOptions) super.clone();
    }

    @Override
    public String toString() {
        return new StringBuilder(64)
                .append(ayanamsa()).append(CH_VS)
                .append(houseSystem()).append(CH_VS)
                .append(calcFlags()).append(CH_VS)
                .append(trueNode()).append(CH_VS)
                .append(riseSetFlags())
                .toString();
    }
}
