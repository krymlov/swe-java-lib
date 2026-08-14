/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@code swe_get_tid_acc}, {@code swe_set_tid_acc}, {@code swe_set_delta_t_userdef}.
 * <p>
 * All three are process-global state, so each test restores the automatic default it found
 * on entry - {@code SE_DELTAT_AUTOMATIC} is {@code -1e-10}, and swephexp.h documents
 * {@code SE_TIDAL_AUTOMATIC} the same way for the tidal acceleration.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
class SwephTidalTest extends AIswTest {

    static final double SE_TIDAL_AUTOMATIC = -1e-10;
    static final double SE_DELTAT_AUTOMATIC = -1e-10;

    @AfterEach
    void backToAutomatic() {
        getSwephExp().swe_set_tid_acc(SE_TIDAL_AUTOMATIC);
        getSwephExp().swe_set_delta_t_userdef(SE_DELTAT_AUTOMATIC);
    }

    @Test
    void swe_get_tid_acc_reportsAPlausibleValue() {
        double acc = getSwephExp().swe_get_tid_acc();
        // the historically used values cluster around -25 to -26 arcsec/century^2
        assertTrue(acc < -10. && acc > -40., "tidal acceleration: " + acc);
    }

    @Test
    void swe_set_tid_acc_isReadBackBySweGetTidAcc() {
        getSwephExp().swe_set_tid_acc(-25.8);
        assertEquals(-25.8, getSwephExp().swe_get_tid_acc(), 1e-9);
    }

    @Test
    void swe_set_tid_acc_changesDeltaTAtOldEpochs() {
        // the tidal term only matters far from the present; near 2000 the effect vanishes
        double oldJd = getSwephExp().swe_julday(1000, 1, 1, 0., SE_GREG_CAL);

        getSwephExp().swe_set_tid_acc(-26.0);
        double dt1 = getSwephExp().swe_deltat(oldJd);

        getSwephExp().swe_set_tid_acc(-23.9);
        double dt2 = getSwephExp().swe_deltat(oldJd);

        assertNotEquals(dt1, dt2, "a different tidal acceleration must move delta t at year 1000");
    }

    @Test
    void swe_set_delta_t_userdef_pinsTheValueSweDeltatReturns() {
        double pinned = 100. / 86400.;   // 100 seconds, expressed in days
        getSwephExp().swe_set_delta_t_userdef(pinned);

        assertEquals(pinned, getSwephExp().swe_deltat(J2000), 1e-9);
        // it must apply regardless of which date is asked
        assertEquals(pinned, getSwephExp().swe_deltat(getSwephExp().swe_julday(1000, 1, 1, 0., SE_GREG_CAL)), 1e-9);
    }

    @Test
    void swe_set_delta_t_userdef_automaticHandsControlBackToTheModel() {
        double automatic = getSwephExp().swe_deltat(J2000);

        getSwephExp().swe_set_delta_t_userdef(100. / 86400.);
        assertNotEquals(automatic, getSwephExp().swe_deltat(J2000), 1e-9);

        getSwephExp().swe_set_delta_t_userdef(SE_DELTAT_AUTOMATIC);
        assertEquals(automatic, getSwephExp().swe_deltat(J2000), 1e-9);
    }
}
