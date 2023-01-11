/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2021-02
 */
package org.swisseph;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.swisseph.utils.IDegreeUtils;

import static org.swisseph.api.ISweConstants.*;
import static org.swisseph.utils.IDegreeUtils.toDDms;

/**
 * @author Yura Krymlov
 * @version 1.0, 2023-01
 */
@Execution(ExecutionMode.CONCURRENT)
public class DegreeUtilsTest extends AbstractTest {

    @Test
    void test0DegreeUtils() {
        final int start = 0;
        final int end = 1;

        for (double ddeg = start; ddeg < end; ddeg += d1d3600000) {
            final int ideg = IDegreeUtils.toIDMSms(ddeg);

            Assertions.assertEquals(IDegreeUtils.toDMSms(ddeg).toString(),
                    IDegreeUtils.toDMSms(ideg).toString());

            Assertions.assertEquals(IDegreeUtils.toDMSms(ddeg).toString(),
                    IDegreeUtils.toDMSms(toDDms(ideg)).toString());
        }
    }


    @RepeatedTest(360)
    void test360DegreeUtils(RepetitionInfo info) {
        final double step = .025/d3600;
        final int start = info.getCurrentRepetition();
        final int end = info.getCurrentRepetition() + 1;

        for (double ddeg = start; ddeg < end; ddeg += step) {
            final int ideg = IDegreeUtils.toIDMSms(ddeg);

            Assertions.assertEquals(IDegreeUtils.toDMSms(ddeg).toString(),
                    IDegreeUtils.toDMSms(ideg).toString());

            Assertions.assertEquals(IDegreeUtils.toDMSms(ddeg).toString(),
                    IDegreeUtils.toDMSms(toDDms(ideg)).toString());
        }
    }
}
