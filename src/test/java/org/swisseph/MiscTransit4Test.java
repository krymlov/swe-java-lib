/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2021-02
 */
package org.swisseph;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

/**
 * Elevations 271 to 360 of {@link MiscTransitTestBase}.
 *
 * @author Yura Krymlov
 * @version 1.1, 2026-11
 */
public class MiscTransit4Test extends MiscTransitTestBase {

    @RepeatedTest(90)
    void testShaniSadeSati(RepetitionInfo info) {
        shaniSadeSati(270 + info.getCurrentRepetition());
    }
}
