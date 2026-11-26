/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-11
 */
package org.swisseph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static swisseph.SweConst.*;

/**
 * {@code swe_calc()} with the files of the reference library from 13000 BCE to 3000 BCE (300 dates).
 * Every difference goes to {@code target/accuracy/CalcAccuracyAncientTest.tsv}; see {@link CalcAccuracy}.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
public class CalcAccuracyAncientTest {

    @Test
    void everyBodyAndKind_matchesNative() {
        assumeTrue(null != AccuracySurvey.REF_EPHE, "SWISSEPH_HOME/ephe");
        final AccuracySurvey s = CalcAccuracy.sweep("CalcAccuracyAncientTest", AccuracySurvey.REF_EPHE, SEFLG_SWIEPH,
                AccuracySurvey.dates(-3026000.5, 625000.5, 300));
        CalcAccuracy.assertWithin(s, 1e-10, 1e-6, 1e-12, 1e-8, 3e-5);
    }
}
