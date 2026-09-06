/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-09
 */

package org.swisseph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.swisseph.api.ISweConstants.NAKSHATRA_LENGTH;
import static org.swisseph.api.ISweConstants.NAKSHATRA_PADA_LENGTH;
import static org.swisseph.api.ISweConstants.RASI_LENGTH;
import static org.swisseph.utils.IModuloUtils.SEGMENT_TOLERANCE;
import static org.swisseph.utils.IModuloUtils.segment;
import static org.swisseph.utils.IModuloUtils.snapToSegment;

/**
 * A transit search exists to find the moment a graha is <b>on</b> a segment boundary. Recomputing
 * the longitude for the moment it returns gives the boundary back only to about 3e-9 of a degree,
 * and - measured over 600 ingresses across ten grahas and the rasi, naksatra and pada lengths -
 * <b>47% of them land on the low side</b>. A bare {@code (int) (longitude / length)} turns that
 * coin flip into a whole sign: a chart built on the reported ingress instant showed the graha at
 * 29&deg;59'59.99" of the sign it was leaving about half the time.
 * <p>
 * These pin the snap that fixes it. Substituting the old bare floor fails
 * {@link #aLongitudeAHairBelowABoundaryBelongsToTheSegmentItStarts()} and
 * {@link #theSnapCoversWhatTheTransitSearchActuallyLeaves()}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-09
 */
class SegmentBoundaryTest {

    /** the worst residual measured over 600 real ingresses */
    static final double WORST_MEASURED_RESIDUAL = 3.25e-9;

    @Test
    @DisplayName("a longitude a hair below a boundary belongs to the segment that starts there")
    void aLongitudeAHairBelowABoundaryBelongsToTheSegmentItStarts() {
        // exactly on: unambiguous
        assertEquals(2, segment(RASI_LENGTH, 60.));
        assertEquals(3, segment(RASI_LENGTH, 90.));

        // a hair below - what the transit search hands back about half the time
        assertEquals(2, segment(RASI_LENGTH, 60. - 1e-9),
                "60 minus a nanodegree is the start of rasi 3, not the end of rasi 2");
        assertEquals(3, segment(RASI_LENGTH, 90. - 1e-9));
        assertEquals(6, segment(RASI_LENGTH, 180. - 1e-9));
        assertEquals(9, segment(RASI_LENGTH, 270. - 1e-9));

        // and a hair above, which was never in doubt
        assertEquals(2, segment(RASI_LENGTH, 60. + 1e-9));
    }

    @Test
    @DisplayName("the snap covers what the transit search actually leaves")
    void theSnapCoversWhatTheTransitSearchActuallyLeaves() {
        assertTrue(SEGMENT_TOLERANCE > WORST_MEASURED_RESIDUAL,
                "the tolerance has to absorb the worst residual measured over 600 ingresses");
        assertTrue(SEGMENT_TOLERANCE * 3600. < 1e-3,
                "and stay below Swiss Ephemeris' own accuracy of about 1e-3 arcsec, so the snap "
                        + "can never move a graha further than the ephemeris is unsure anyway");

        for (double length : new double[]{RASI_LENGTH, NAKSHATRA_LENGTH, NAKSHATRA_PADA_LENGTH}) {
            final int count = (int) Math.round(360. / length);
            for (int i = 1; i < count; i++) {
                final double boundary = i * length;
                assertEquals(i, segment(length, boundary - WORST_MEASURED_RESIDUAL),
                        "length " + length + ", boundary " + boundary);
            }
        }
    }

    @Test
    @DisplayName("a value clear of a boundary is left alone")
    void aValueClearOfABoundaryIsLeftAlone() {
        // half a milliarcsecond is already far outside the snap
        assertEquals(1, segment(RASI_LENGTH, 60. - 1e-6));
        assertEquals(0, segment(RASI_LENGTH, 15.));
        assertEquals(11, segment(RASI_LENGTH, 345.));
        assertEquals(15.0, snapToSegment(RASI_LENGTH, 15.));
    }

    @Test
    @DisplayName("the wrap at 360 does not produce an index one past the last segment")
    void theWrapAt360DoesNotProduceAnIndexOnePastTheLast() {
        // modulo() only snaps within 1e-13, so 360 minus a nanodegree survives as itself and
        // would otherwise be answered as segment 12 of 12
        assertEquals(0, segment(RASI_LENGTH, 360. - 1e-9));
        assertEquals(0, segment(NAKSHATRA_LENGTH, 360. - 1e-9));
        assertEquals(0, segment(NAKSHATRA_PADA_LENGTH, 360. - 1e-9));
        assertEquals(0., snapToSegment(RASI_LENGTH, 360. - 1e-9));

        assertEquals(11, segment(RASI_LENGTH, 359.));
        assertEquals(26, segment(NAKSHATRA_LENGTH, 359.));
    }

    @Test
    @DisplayName("NaN stays NaN - an undetermined longitude is not segment 0")
    void nanStaysNan() {
        assertEquals(-1, segment(RASI_LENGTH, Double.NaN));
        assertTrue(Double.isNaN(snapToSegment(RASI_LENGTH, Double.NaN)));
    }

    @Test
    @DisplayName("a negative longitude reduces before it snaps")
    void aNegativeLongitudeReducesBeforeItSnaps() {
        assertEquals(11, segment(RASI_LENGTH, -15.));
        assertEquals(0, segment(RASI_LENGTH, -1e-9), "just below zero is the start of Mesha");
        assertEquals(2, segment(RASI_LENGTH, -300. - 1e-9));
    }
}
