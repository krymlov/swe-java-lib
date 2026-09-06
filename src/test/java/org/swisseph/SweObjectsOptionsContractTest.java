/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-09
 */

package org.swisseph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.swisseph.api.ISweObjectsOptions;
import org.swisseph.app.SweAyanamsa;
import org.swisseph.app.SweHouseSystem;
import org.swisseph.app.SweObjectsOptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static swisseph.SweConst.SEFLG_JPLEPH;
import static swisseph.SweConst.SEFLG_MOSEPH;
import static swisseph.SweConst.SEFLG_SIDEREAL;
import static swisseph.SweConst.SEFLG_SPEED;
import static swisseph.SweConst.SEFLG_SWIEPH;
import static swisseph.SweConst.SEFLG_TRUEPOS;

/**
 * The options contract: every setting has to survive the builder whatever order it is called in,
 * and mean the same thing at the far end of the chain.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-09
 */
class SweObjectsOptionsContractTest {

    static final double T0 = 2451545.0;
    static final double AYAN_T0 = 23.5;

    @Test
    @DisplayName("SE_SIDM_USER t0/ayan_t0 survive whatever order the builder is called in")
    void userAyanamsaReferenceValuesAreOrderIndependent() {
        // The guard used to sit inside the setters, so the value was dropped unless the
        // ayanamsa had already been set - the same order dependency this builder was fixed
        // for once before, with ayanamsa() and SEFLG_SIDEREAL.
        final ISweObjectsOptions after = new SweObjectsOptions.Builder()
                .ayanamsa(SweAyanamsa.AY_USER)
                .initialJulianDay(T0).initialAyanamsa(AYAN_T0).build();

        final ISweObjectsOptions before = new SweObjectsOptions.Builder()
                .initialJulianDay(T0).initialAyanamsa(AYAN_T0)
                .ayanamsa(SweAyanamsa.AY_USER).build();

        assertEquals(T0, after.initialJulianDay());
        assertEquals(AYAN_T0, after.initialAyanamsa());
        assertEquals(T0, before.initialJulianDay(),
                "setting the reference date before the ayanamsa must not lose it");
        assertEquals(AYAN_T0, before.initialAyanamsa(),
                "setting the reference ayanamsa before the ayanamsa must not lose it");
    }

    @Test
    @DisplayName("a non-user ayanamsa carries no reference date - swe_set_sid_mode wants 0 there")
    void aNonUserAyanamsaCarriesNoReferenceDate() {
        final ISweObjectsOptions options = new SweObjectsOptions.Builder()
                .ayanamsa(SweAyanamsa.LAHIRI)
                .initialJulianDay(T0).initialAyanamsa(AYAN_T0).build();

        assertEquals(0., options.initialJulianDay());
        assertEquals(0., options.initialAyanamsa());
    }

    @Test
    @DisplayName("the house system survives being set before the ayanamsa")
    void theHouseSystemSurvivesBeingSetBeforeTheAyanamsa() {
        assertEquals(SweHouseSystem.PLACIDUS, new SweObjectsOptions.Builder()
                .houseSystem(SweHouseSystem.PLACIDUS)
                .ayanamsa(SweAyanamsa.getNone()).build().houseSystem());
        assertEquals(SweHouseSystem.PLACIDUS, new SweObjectsOptions.Builder()
                .ayanamsa(SweAyanamsa.getNone())
                .houseSystem(SweHouseSystem.PLACIDUS).build().houseSystem());
    }

    @Test
    @DisplayName("SEFLG_SIDEREAL follows the ayanamsa in all four flag sets")
    void siderealFollowsTheAyanamsaInEveryFlagSet() {
        final ISweObjectsOptions tropical = new SweObjectsOptions.Builder()
                .ayanamsa(SweAyanamsa.getNone()).build();
        for (int flags : new int[]{tropical.mainFlags(), tropical.houseFlags(),
                tropical.calcFlags(), tropical.transitFlags()}) {
            assertEquals(0, flags & SEFLG_SIDEREAL, "tropical must not carry SEFLG_SIDEREAL");
        }

        final ISweObjectsOptions sidereal = new SweObjectsOptions.Builder()
                .ayanamsa(SweAyanamsa.LAHIRI).build();
        for (int flags : new int[]{sidereal.mainFlags(), sidereal.houseFlags(),
                sidereal.calcFlags(), sidereal.transitFlags()}) {
            assertNotEquals(0, flags & SEFLG_SIDEREAL, "a sidereal ayanamsa needs the flag");
        }
    }

    @Test
    @DisplayName("epheFlags() is the ephemeris choice alone, and follows mainFlags")
    void epheFlagsIsTheEphemerisChoiceAlone() {
        // swe_rise_trans() and the eclipse functions take the ephemeris flag on its own. They
        // used to be handed a literal SEFLG_SWIEPH, so a chart asking for Moshier got its
        // sunrise from a different ephemeris than its planets - and Gulika, Maandi, the
        // Kalavela upagrahas and the time lagnas all hang off that sunrise.
        final ISweObjectsOptions moshier = new SweObjectsOptions.Builder()
                .mainFlags(SEFLG_SIDEREAL | SEFLG_MOSEPH)
                .ayanamsa(SweAyanamsa.LAHIRI).build();
        assertEquals(SEFLG_MOSEPH, moshier.epheFlags());

        final ISweObjectsOptions swiss = new SweObjectsOptions.Builder()
                .mainFlags(SEFLG_SIDEREAL | SEFLG_SWIEPH | SEFLG_TRUEPOS | SEFLG_SPEED)
                .ayanamsa(SweAyanamsa.LAHIRI).build();
        assertEquals(SEFLG_SWIEPH, swiss.epheFlags(),
                "everything that is not an ephemeris bit has to be masked out");

        final ISweObjectsOptions jpl = new SweObjectsOptions.Builder()
                .mainFlags(SEFLG_SIDEREAL | SEFLG_JPLEPH)
                .ayanamsa(SweAyanamsa.LAHIRI).build();
        assertEquals(SEFLG_JPLEPH, jpl.epheFlags());
    }

    @Test
    @DisplayName("options(other) copies every field, so a variant needs one setter, not ten")
    void optionsCopiesEveryField() {
        final ISweObjectsOptions source = new SweObjectsOptions.Builder()
                .ayanamsa(SweAyanamsa.LAHIRI).houseSystem(SweHouseSystem.PLACIDUS)
                .trueNode(true).riseSetFlags(0x1234)
                .mainFlags(SEFLG_SIDEREAL | SEFLG_MOSEPH).build();

        final ISweObjectsOptions copy = new SweObjectsOptions.Builder().options(source).build();

        assertEquals(source.ayanamsa(), copy.ayanamsa());
        assertEquals(source.houseSystem(), copy.houseSystem());
        assertEquals(source.trueNode(), copy.trueNode());
        assertEquals(source.mainFlags(), copy.mainFlags());
        assertEquals(source.houseFlags(), copy.houseFlags());
        assertEquals(source.calcFlags(), copy.calcFlags());
        assertEquals(source.riseSetFlags(), copy.riseSetFlags());
        assertEquals(source.transitFlags(), copy.transitFlags());
        assertEquals(source.epheFlags(), copy.epheFlags());
        assertEquals(source.initialJulianDay(), copy.initialJulianDay());
        assertEquals(source.initialAyanamsa(), copy.initialAyanamsa());
    }
}
