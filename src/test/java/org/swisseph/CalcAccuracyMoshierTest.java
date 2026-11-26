/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-11
 */
package org.swisseph;

import org.junit.jupiter.api.Test;

import static swisseph.SweConst.*;

/**
 * {@code swe_calc()} from the Moshier theories over their whole range, 3000 BCE to 3000 CE (400 dates).
 * Every difference goes to {@code target/accuracy/CalcAccuracyMoshierTest.tsv}; see {@link CalcAccuracy}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
public class CalcAccuracyMoshierTest {

    @Test
    void everyBodyAndKind_matchesNative() {
        final AccuracySurvey s = CalcAccuracy.sweep("CalcAccuracyMoshierTest", org.swisseph.api.ISweConstants.EPHE_PATH, SEFLG_MOSEPH, AccuracySurvey.dates(625000.5, 2818000.5, 400));
        CalcAccuracy.assertWithin(s, 1e-9, 3e-5, 1e-11, 1e-6, 3e-5);
    }
}
