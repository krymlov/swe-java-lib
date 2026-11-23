/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@code swephlib.c}: {@code swe_set_astro_models}, {@code swe_get_astro_models} - the "for
 * Dieter" switches that let the caller force an older model of delta t, precession, nutation,
 * etc. {@code samod} is a comma separated list of {@code SE_MODEL_*}-indexed integers, or a
 * bare Swiss Ephemeris version string ("SE2.06") that expands to the model set that version
 * used.
 * <p>
 * Until 2026-11-23 {@link swisseph.SweConst}'s {@code SE_MODEL_*} and {@code SEMOD_*} constants
 * had the numbering of an older release ({@code SE_MODEL_DELTAT} was slot 7, not 0), so this
 * file spelled the index out. They are swephexp.h's now, pinned by
 * {@link #sweConstsModelIndicesMatchSwephexpH()}; {@code AstroModelsParityTest} compares the
 * pure Java engine with these native calls model by model.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
class SwephAstroModelsTest extends AIswTest {

    /** slot 0 of the model list ({@code SE_MODEL_DELTAT}): the delta t model of SE 1.00 */
    static final String SEMOD_DELTAT_STEPHENSON_MORRISON_1984 =
            String.valueOf(swisseph.SweConst.SEMOD_DELTAT_STEPHENSON_MORRISON_1984);

    @AfterEach
    void resetToDefaultModels() {
        // an empty samod resets swed.astro_models[] to the current SE_VERSION's defaults -
        // swephlib.c's swe_set_astro_models() takes the "*samod == '\0'" branch
        getSwephExp().swe_set_astro_models(new StringBuilder(), 0);
    }

    @Test
    void swe_set_astro_models_changesTheDeltaTModelForOldDates() {
        double jdOld = 2086303.5; // year 1000, well before any model's tabulated range

        getSwephExp().swe_set_astro_models(new StringBuilder(), 0);
        double deltaTDefault = getSwephExp().swe_deltat(jdOld);

        getSwephExp().swe_set_astro_models(new StringBuilder(SEMOD_DELTAT_STEPHENSON_MORRISON_1984), 0);
        double deltaTOldModel = getSwephExp().swe_deltat(jdOld);

        // the pre-2016 and 2016 delta t models disagree by tens of seconds at year 1000 -
        // see CLAUDE.md's "Delta t: the pure Java engine was up to 122 seconds out"
        assertTrue(Math.abs(deltaTDefault - deltaTOldModel) * 86400. > 1.,
                "switching the delta t model should change the result at an old date: default="
                        + deltaTDefault + " old=" + deltaTOldModel);
    }

    @Test
    void swe_get_astro_models_reportsTheEphemerisAndTidalAccelerationInSdet() {
        StringBuilder samod = new StringBuilder();
        StringBuilder sdet = new StringBuilder();

        getSwephExp().swe_get_astro_models(samod, sdet, SEFLG_SWIEPH);

        String details = sdet.toString();
        assertTrue(details.toLowerCase().contains("tidal acc"), details);
        assertTrue(details.toLowerCase().contains("jpl eph"), details);
    }

    /**
     * {@code swephlib.c}'s {@code swe_get_astro_models()} builds the comma-separated model
     * list into a local buffer and then never copies it back into {@code samod} - the
     * {@code strcpy(samod, samod0)} that would do so is commented out. So unlike {@code sdet},
     * {@code samod} is input-only in practice: it is read (to optionally re-apply a model set
     * via {@code swe_set_astro_models}) but never written. Pinned here so a future native
     * upgrade that fixes this is noticed rather than silently changing behaviour.
     */
    @Test
    void swe_get_astro_models_neverWritesBackTheModelListItself() {
        StringBuilder samod = new StringBuilder();
        StringBuilder sdet = new StringBuilder();

        getSwephExp().swe_get_astro_models(samod, sdet, 0);

        assertEquals(0, samod.length(), "samod is not filled in by the native side: " + samod);
    }

    @Test
    void swe_set_astro_models_acceptsASwissEphemerisVersionString() {
        getSwephExp().swe_set_astro_models(new StringBuilder("SE2.06"), 0);

        StringBuilder sdet = new StringBuilder();
        getSwephExp().swe_get_astro_models(new StringBuilder(), sdet, 0);
        assertTrue(sdet.length() > 0, "the call should not throw and should leave usable state");
    }

    /** swephexp.h 2.10.03: the slots and the model numbers */
    @Test
    void sweConstsModelIndicesMatchSwephexpH() {
        assertEquals(0, swisseph.SweConst.SE_MODEL_DELTAT);
        assertEquals(1, swisseph.SweConst.SE_MODEL_PREC_LONGTERM);
        assertEquals(2, swisseph.SweConst.SE_MODEL_PREC_SHORTTERM);
        assertEquals(3, swisseph.SweConst.SE_MODEL_NUT);
        assertEquals(4, swisseph.SweConst.SE_MODEL_BIAS);
        assertEquals(5, swisseph.SweConst.SE_MODEL_JPLHOR_MODE);
        assertEquals(6, swisseph.SweConst.SE_MODEL_JPLHORA_MODE);
        assertEquals(7, swisseph.SweConst.SE_MODEL_SIDT);
        assertEquals(8, swisseph.SweConst.NSE_MODELS);
        assertEquals(1, swisseph.SweConst.SEMOD_DELTAT_STEPHENSON_MORRISON_1984);
        assertEquals(5, swisseph.SweConst.SEMOD_DELTAT_STEPHENSON_ETC_2016);
        assertEquals(9, swisseph.SweConst.SEMOD_PREC_VONDRAK_2011);
        assertEquals(11, swisseph.SweConst.SEMOD_PREC_NEWCOMB);
        assertEquals(4, swisseph.SweConst.SEMOD_NUT_IAU_2000B);
        assertEquals(5, swisseph.SweConst.SEMOD_NUT_WOOLARD);
        assertEquals(3, swisseph.SweConst.SEMOD_BIAS_IAU2006);
        assertEquals(4, swisseph.SweConst.SEMOD_SIDT_LONGTERM);
        assertEquals(3, swisseph.SweConst.SEMOD_JPLHORA_3);
    }
}
