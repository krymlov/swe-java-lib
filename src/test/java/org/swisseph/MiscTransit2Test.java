/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2021-02
 */
package org.swisseph;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

/**
 * Elevations 91 to 180 of {@link MiscTransitTestBase}.
 *
 * @author Yura Krymlov
 * @version 1.1, 2026-11
 */
public class MiscTransit2Test extends MiscTransitTestBase {

    @RepeatedTest(90)
    void testShaniSadeSati(RepetitionInfo info) {
        shaniSadeSati(90 + info.getCurrentRepetition());
    }
}
