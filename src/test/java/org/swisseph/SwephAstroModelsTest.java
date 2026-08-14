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
 * <b>{@link swisseph.SweConst}'s {@code SE_MODEL_*} constants do not match this ordering</b> -
 * they index the pure Java port's own, differently-ordered internal list ({@code DELTAT} is
 * slot 7 there, {@code SIDT} is slot 3), not swephexp.h's ({@code DELTAT} is slot 0,
 * {@code SIDT} is slot 7). That reordering is fine for {@code swisseph.SwissEph}, which reads
 * and writes its own array with its own constants consistently. It is wrong for this raw
 * delegating call, which reaches {@link swisseph.SwephExp#swe_set_astro_models} directly and
 * must use swephexp.h's slot numbers - so this file spells the index out (0) rather than
 * naming it, to avoid the trap of reaching for {@code SweConst.SE_MODEL_DELTAT} here and
 * silently setting the sidereal-time model instead.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
class SwephAstroModelsTest extends AIswTest {

    /** index 0 of the model list, per swephexp.h's {@code SE_MODEL_DELTAT} - NOT
     * {@link swisseph.SweConst#SE_MODEL_DELTAT}, see the class javadoc */
    static final String SEMOD_DELTAT_STEPHENSON_MORRISON_1984 = "1";

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

    /**
     * Pins the discrepancy the class javadoc warns about. If {@code swisseph.SweConst} is
     * ever realigned with swephexp.h, this starts failing - which is the point: it means
     * {@code SEMOD_DELTAT_STEPHENSON_MORRISON_1984}'s hardcoded "index 0" above needs
     * revisiting too.
     */
    @Test
    void sweConstsModelIndicesDoNotMatchSwephexpHForThisRawCall() {
        assertEquals(0, swisseph.SweConst.SE_MODEL_PREC_LONGTERM,
                "swephexp.h's SE_MODEL_DELTAT is slot 0, not SweConst's SE_MODEL_PREC_LONGTERM");
        assertEquals(7, swisseph.SweConst.SE_MODEL_DELTAT,
                "SweConst's own (differently-ordered) SE_MODEL_DELTAT is slot 7, not slot 0");
    }
}
