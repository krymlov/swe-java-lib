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
import static org.swisseph.app.SweAyanamsa.getLahiri;
import static org.swisseph.app.SweAyanamsa.getTrueSpica;
import static swisseph.SweConst.SE_SIDM_USER;

/**
 * @author Yura Krymlov
 * @version 1.1, 2019-12
 */
public class SweObjectsOptions implements ISweObjectsOptions {
    private static final long serialVersionUID = 461264153326654053L;

    public static final ISweObjectsOptions LAHIRI_AYANAMSA = new Builder().ayanamsa(getLahiri()).build();
    public static final ISweObjectsOptions TRUECITRA_AYANAMSA = new Builder().ayanamsa(getTrueSpica()).build();

    protected final ISweHouseSystem houseSystem;
    protected final ISweAyanamsa ayanamsa;

    protected final boolean trueNode;

    protected final double initialJulianDay;
    protected final double initialAyanamsa;

    protected final int mainFlags, houseFlags, calcFlags;
    protected final int riseSetFlags, transitFlags;

    /**
     * @param initialJulianDay - initial julian day (reference date)
     * @param initialAyanamsa  - initial ayanamsa (at reference date)
     */
    protected SweObjectsOptions(ISweAyanamsa ayanamsa, ISweHouseSystem houseSystem, boolean trueNode,
                                int mainFlags, int houseFlags, int calcFlags,
                                double initialJulianDay, double initialAyanamsa,
                                int riseSetFlags, int transitFlags) {
        this.initialJulianDay = initialJulianDay;
        this.initialAyanamsa = initialAyanamsa;
        this.riseSetFlags = riseSetFlags;
        this.transitFlags = transitFlags;
        this.houseSystem = houseSystem;
        this.houseFlags = houseFlags;
        this.calcFlags = calcFlags;
        this.mainFlags = mainFlags;
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

    /**
     * true or mean node to use for RA/KE calculation
     */
    @Override
    public boolean trueNode() {
        return trueNode;
    }

    /**
     * Reference date (Julian day), if sid_mode is SE_SIDM_USER
     */
    @Override
    public double initialJulianDay() {
        return initialJulianDay;
    }

    /**
     * Initial ayanamsha at t0, if sid_mode is SE_SIDM_USER
     */
    @Override
    public double initialAyanamsa() {
        return initialAyanamsa;
    }

    /**
     * Special preset of flags like for ayanamsa calculation
     */
    @Override
    public int mainFlags() {
        return mainFlags;
    }

    /**
     * Special preset of flags like for ascendant calculation (swe_houses* api)
     */
    public int houseFlags() {
        return houseFlags;
    }

    /**
     * Special preset of flags like for planets calculation (swe_calc* api)
     */
    @Override
    public int calcFlags() {
        return calcFlags;
    }

    /**
     * This is a flag to swe_rise_trans()
     */
    @Override
    public int riseSetFlags() {
        return riseSetFlags;
    }

    @Override
    public int transitFlags() {
        return transitFlags;
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
                .append(mainFlags()).append(CH_VS)
                .append(houseFlags()).append(CH_VS)
                .append(calcFlags()).append(CH_VS)
                .append(trueNode()).append(CH_VS)
                .append(riseSetFlags()).append(CH_VS)
                .append(transitFlags())
                .toString();
    }

    public static final class Builder {
        private ISweHouseSystem houseSystem = SweHouseSystem.byDefault();
        private ISweAyanamsa ayanamsa = SweAyanamsa.byDefault();

        private int mainFlags = DEFAULT_SS_MAIN_FLAGS;
        private int houseFlags = DEFAULT_SS_HOUSE_FLAGS;
        private int calcFlags = DEFAULT_SS_CALC_FLAGS;
        private int riseSetFlags = DEFAULT_SS_RISE_SET_FLAGS;
        private int transitFlags = DEFAULT_SS_TRANSIT_FLAGS;

        private double initialJulianDay, initialAyanamsa;

        private boolean trueNode;

        public Builder options(ISweObjectsOptions options) {
            this.initialJulianDay = options.initialJulianDay();
            this.initialAyanamsa = options.initialAyanamsa();
            this.transitFlags = options.transitFlags();
            this.riseSetFlags = options.riseSetFlags();
            this.houseSystem = options.houseSystem();
            this.houseFlags = options.houseFlags();
            this.calcFlags = options.calcFlags();
            this.mainFlags = options.mainFlags();
            this.trueNode = options.trueNode();
            this.ayanamsa = options.ayanamsa();
            return this;
        }

        /**
         * Special preset of flags to swe_rise_trans
         */
        public Builder riseSetFlags(int flags) {
            this.riseSetFlags = flags;
            return this;
        }

        /**
         * true or mean node to use for RA/KE calculation
         */
        public Builder trueNode(boolean trueNode) {
            this.trueNode = trueNode;
            return this;
        }

        /**
         * Special preset of flags like for ayanamsa calculation
         */
        public Builder mainFlags(int flags) {
            this.mainFlags = flags;
            return this;
        }

        /**
         * Special preset of flags like for ascendant calculation
         */
        public Builder houseFlags(int flags) {
            this.houseFlags = flags;
            return this;
        }

        /**
         * Special preset of flags for planets calculation
         */
        public Builder calcFlags(int flags) {
            this.calcFlags = flags;
            return this;
        }

        /**
         * Special preset of flags for transit calculation
         */
        public Builder transitFlags(int flags) {
            this.transitFlags = flags;
            return this;
        }

        public Builder ayanamsa(ISweAyanamsa ayanamsa) {
            this.ayanamsa = ayanamsa;
            return this;
        }

        public Builder houseSystem(ISweHouseSystem houseSystem) {
            this.houseSystem = houseSystem;
            return this;
        }

        /**
         * Reference date (Julian day) if sid_mode is SE_SIDM_USER
         */
        public Builder initialJulianDay(double initJulianDay) {
            if (ayanamsa.fid() == SE_SIDM_USER) {
                this.initialJulianDay = initJulianDay;
            }
            return this;
        }

        /**
         * Initial ayanamsha if sid_mode is SE_SIDM_USER
         */
        public Builder initialAyanamsa(double initAyanamsa) {
            if (ayanamsa.fid() == SE_SIDM_USER) {
                this.initialAyanamsa = initAyanamsa;
            }
            return this;
        }

        public ISweObjectsOptions build() {
            return new SweObjectsOptions(ayanamsa, houseSystem, trueNode,
                    mainFlags, houseFlags, calcFlags,
                    initialJulianDay, initialAyanamsa,
                    riseSetFlags, transitFlags);
        }
    }
}
