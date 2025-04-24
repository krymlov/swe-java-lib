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

    static StringBuilder convertToDMS(double decimalDegrees) {
        int degrees = (int) decimalDegrees;
        double fractionalDegrees = Math.abs(decimalDegrees - degrees) * 60;
        int minutes = (int) fractionalDegrees;
        double fractionalMinutes = (fractionalDegrees - minutes) * 60;
        int seconds = (int) fractionalMinutes;
        int milliseconds = (int) ((fractionalMinutes - seconds) * 1000);

        StringBuilder sb = new StringBuilder();
        if (degrees < i10) sb.append(CH_ZR);
        sb.append(degrees).append("°");
        if (minutes < i10) sb.append(CH_ZR);
        sb.append(minutes).append("'");
        if (seconds < i10) sb.append(CH_ZR);
        sb.append(seconds).append(".");
        if (milliseconds < i10) sb.append(CH_ZR);
        sb.append(milliseconds);//.append("\"");

        return sb;
    }

    static double convertToDD(String dms) {
        String[] parts = dms.split("[°'\"ms ]+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid DMS: " + dms);
        }

        int degrees = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        int milliseconds = parts.length > 3 ? Integer.parseInt(parts[3]) : 0;
        return degrees + (minutes / 60.0) + (seconds / 3600.0) + (milliseconds / 3_600_000.0);
    }

    @Test
    void testToDD() {
        double d1 = IDegreeUtils.toDDms(49452750);
        double d2 = convertToDD("49°45'27\"500ms");

        String dms1 = IDegreeUtils.toDMSms(d1).toString();
        String dms2 = convertToDMS(d2).toString();
        dms1 = dms1.toString().replace("\"", "");
        //System.out.println(dms1 + " == " + dms2 + " <== " + d1);
        Assertions.assertTrue(dms2.toString().contains(dms1.toString()));
    }

    @Test
    void testToDMS() {
        for (double d = 0.; d < 90; d += 0.01) {
            String dms1 = IDegreeUtils.toDMSms(d).toString();
            String dms2 = convertToDMS(d).toString();
            dms1 = dms1.toString().replace("\"", "");
            //System.out.println(dms1 + " == " + dms2 + " <== " + d);
            Assertions.assertTrue(dms2.toString().contains(dms1.toString()));
        }
    }

    @Test
    void test0DegreeUtils() {
        final int start = 0;
        final int end = 1;

        for (double ddeg = start; ddeg < end; ddeg += d1d3600E03) {
            final int ideg = IDegreeUtils.toIDMSms(ddeg);

            Assertions.assertEquals(IDegreeUtils.toDMSms(ddeg).toString(),
                    IDegreeUtils.toDMSms(ideg).toString());

            Assertions.assertEquals(IDegreeUtils.toDMSms(ddeg).toString(),
                    IDegreeUtils.toDMSms(toDDms(ideg)).toString());
        }
    }


    @RepeatedTest(360)
    void test360DegreeUtils(RepetitionInfo info) {
        final double step = .025 / d3600;
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
