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
import static org.swisseph.utils.IModuloUtils.segmentDegree;
import static org.swisseph.utils.IModuloUtils.snapToSegment;
import static org.swisseph.utils.IDegreeUtils.toDMSms;
import static org.swisseph.utils.IDegreeUtils.toDMSmsWithin;

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
    @DisplayName("the degree within the segment snaps with the index, not against it")
    void theDegreeWithinTheSegmentSnapsWithTheIndex() {
        // The index and the degree are the two halves of one answer. The index snapped from the
        // start; the degree did not, so at an ingress the chart said "Mesha 29 59'59.99"" - the
        // sign it had entered, at a degree in the sign it had left. Measured on real ingresses:
        // 40 of 84.
        for (double boundary : new double[]{0., 30., 60., 180., 270., 330., 360.}) {
            final double justBelow = boundary - 1e-9;
            assertEquals(0., segmentDegree(RASI_LENGTH, justBelow), 0.,
                    "a hair below " + boundary + " is the start of the next rasi, degree 0");
            assertEquals(segment(RASI_LENGTH, justBelow) * RASI_LENGTH
                            + segmentDegree(RASI_LENGTH, justBelow),
                    snapToSegment(RASI_LENGTH, justBelow), 0.,
                    "index * length + degree has to rebuild the snapped longitude");
        }

        // away from a boundary nothing moves, and the two halves still rebuild the input
        final double ordinary = 137.4237;
        assertEquals(17.4237, segmentDegree(RASI_LENGTH, ordinary), 1e-12);
        assertEquals(ordinary, segment(RASI_LENGTH, ordinary) * RASI_LENGTH
                + segmentDegree(RASI_LENGTH, ordinary), 1e-12);

        // and it holds for every segment length the jyotisa layer divides by
        assertEquals(0., segmentDegree(NAKSHATRA_LENGTH, NAKSHATRA_LENGTH - 1e-9), 0.);
        assertEquals(0., segmentDegree(NAKSHATRA_PADA_LENGTH, NAKSHATRA_PADA_LENGTH - 1e-9), 0.);

        // NaN stays NaN: an indeterminable longitude must not become a real degree
        assertTrue(Double.isNaN(segmentDegree(RASI_LENGTH, Double.NaN)));
    }

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

    @Test
    @DisplayName("a rendered position stays inside the segment it is labelled with, both ends")
    void aRenderedPositionStaysInsideItsSegment() {
        // The author's report, straight out of Kundali.toString():
        //
        //   (RA) = 300°00'00.00" -> Rasi= MAK (0.0  %) | 30°00'00.00"
        //
        // 300 is the start of Kumbha, not a position in Makara, and 30 is a degree in no sign at
        // all. Rahu is retrograde, so it enters Makara at the TOP of it - measured 299.999999859,
        // which is a hair more than SEGMENT_TOLERANCE below the boundary, so the snap correctly
        // leaves it in Makara and only the rendering carried it across.
        final double rahu = 299.999999859055;

        assertEquals("299°59'59.99\"", toDMSmsWithin(rahu, NAKSHATRA_PADA_LENGTH).toString(),
                "the longitude has to stay in the sign the row names");
        assertEquals("29°59'59.99\"",
                toDMSmsWithin(segmentDegree(RASI_LENGTH, rahu), NAKSHATRA_PADA_LENGTH).toString(),
                "and so does the degree within it - 30 is not a position in a sign");

        // the other end of the same segment: a value the snap DOES move belongs to the sign
        // above, and must render as its boundary rather than being truncated back down
        final double snapped = 300. - SEGMENT_TOLERANCE / 2.;
        assertEquals("300°00'00.00\"", toDMSmsWithin(snapped, NAKSHATRA_PADA_LENGTH).toString(),
                "inside the tolerance the value IS the boundary, and truncating it would put it "
                        + "back in the sign the snap just took it out of");
        assertEquals(0., segmentDegree(RASI_LENGTH, snapped), 0.);
    }

    @Test
    @DisplayName("the pada length covers the rasi and the naksatra boundaries too")
    void thePadaLengthCoversEveryBoundaryARowNames() {
        // A report row names a rasi, a naksatra AND a pada at once. 30 is 9 padas and 13°20' is
        // 4, so every boundary of either is a multiple of 3°20' - the finest length satisfies all
        // three, and the coarser ones do not: the rasi length leaves a naksatra boundary alone
        // and the naksatra length leaves a rasi boundary alone.
        final double belowNaksatra = NAKSHATRA_LENGTH - 3e-7;
        final double belowRasi = RASI_LENGTH - 3e-7;

        assertEquals("13°19'59.99\"", toDMSmsWithin(belowNaksatra, NAKSHATRA_PADA_LENGTH).toString());
        assertEquals("29°59'59.99\"", toDMSmsWithin(belowRasi, NAKSHATRA_PADA_LENGTH).toString());

        assertEquals("13°20'00.00\"", toDMSmsWithin(belowNaksatra, RASI_LENGTH).toString(),
                "the rasi length cannot see a naksatra boundary");
        assertEquals("30°00'00.00\"", toDMSmsWithin(belowRasi, NAKSHATRA_LENGTH).toString(),
                "nor the naksatra length a rasi boundary");
    }

    @Test
    @DisplayName("every other value renders exactly as it did before")
    void anOrdinaryValueRendersUnchanged() {
        // The clamp fires only within half a rendered unit of a boundary, so nothing else may
        // move - which is why no golden file changed when this was introduced.
        int moved = 0;
        for (int i = 0; i < 500000; i++) {
            final double v = i * (360. / 500000.);
            if (!toDMSms(v).toString().equals(toDMSmsWithin(v, NAKSHATRA_PADA_LENGTH).toString())) {
                moved++;
            }
        }
        assertEquals(0, moved, "sampled degrees whose rendering changed");
    }
}
