/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-09
 */

package org.swisseph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.swisseph.api.ISweObjects;
import org.swisseph.app.SweAyanamsa;
import org.swisseph.app.SweGeoLocation;
import org.swisseph.app.SweJulianDate;
import org.swisseph.app.SweObjects;
import org.swisseph.app.SweObjectsOptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static swisseph.SweConst.SEFLG_SWIEPH;

/**
 * The sidereal mode is <b>thread-local sticky state</b> inside Swiss Ephemeris - {@code swed.sidd}
 * - and {@code initSwissEph()} is the only thing that sets it.
 * <p>
 * It used to be set only for a sidereal ayanamsa, so a tropical chart left whatever the previous
 * chart on the same thread had put there. Nothing in a tropical chart reads it, so no number was
 * wrong; but the state was not what the caller had asked for, and it reads back through
 * {@code swe_get_ayanamsa_ex()}, which does not care that the caller has since gone tropical.
 * This is the same family as the thread affinity already documented for a built {@code Kundali}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-09
 */
class SidModeStickinessTest extends AbstractTest {

    static final SweGeoLocation KYIV = new SweGeoLocation(30.5234, 50.4501, 0.);
    static final double JD_2000 = 2451545.0;

    private ISweObjects chart(org.swisseph.api.ISweObjectsOptions options) {
        return new SweObjects(getSwissEph(), new SweJulianDate(JD_2000), KYIV, options)
                .completeBuild();
    }

    /** what swisseph would answer right now, straight from its own thread-local state */
    private double ayanamsaFromLibraryState() {
        final double[] daya = new double[1];
        getSwissEph().swe_get_ayanamsa_ex(JD_2000, SEFLG_SWIEPH, daya, new StringBuilder());
        return daya[0];
    }

    @Test
    @DisplayName("a tropical chart resets the sidereal mode instead of inheriting the last one")
    void aTropicalChartResetsTheSiderealMode() {
        // put an unmistakable mode on this thread
        chart(new SweObjectsOptions.Builder().ayanamsa(SweAyanamsa.getTrueSpica()).build());
        final double afterCitra = ayanamsaFromLibraryState();

        // the library default the reset goes to
        chart(new SweObjectsOptions.Builder().ayanamsa(SweAyanamsa.FAGAN_BRADLEY).build());
        final double faganBradley = ayanamsaFromLibraryState();

        assertNotEquals(afterCitra, faganBradley,
                "True Citra and Fagan/Bradley have to differ, or this test proves nothing");

        // back to True Citra, then tropical: the mode must not still be True Citra afterwards
        chart(new SweObjectsOptions.Builder().ayanamsa(SweAyanamsa.getTrueSpica()).build());
        chart(SweObjectsOptions.TROPICAL_ZODIAC);

        assertEquals(faganBradley, ayanamsaFromLibraryState(),
                "a tropical chart has to leave the thread in the library default, not in "
                        + "whatever the previous sidereal chart set");
    }

    @Test
    @DisplayName("a sidereal chart still sets its own mode, whichever came before")
    void aSiderealChartStillSetsItsOwnMode() {
        chart(SweObjectsOptions.TROPICAL_ZODIAC);
        chart(new SweObjectsOptions.Builder().ayanamsa(SweAyanamsa.LAHIRI).build());
        final double afterTropical = ayanamsaFromLibraryState();

        chart(new SweObjectsOptions.Builder().ayanamsa(SweAyanamsa.getTrueSpica()).build());
        chart(new SweObjectsOptions.Builder().ayanamsa(SweAyanamsa.LAHIRI).build());
        final double afterCitra = ayanamsaFromLibraryState();

        assertEquals(afterTropical, afterCitra,
                "Lahiri must answer the same whatever the thread was set to before it");
    }
}
