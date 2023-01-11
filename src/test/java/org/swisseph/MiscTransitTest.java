/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2021-02
 */
package org.swisseph;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.swisseph.utils.IModuloUtils;
import swisseph.Transits;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Yura Krymlov
 * @version 1.0, 2021-02
 */
@Execution(ExecutionMode.CONCURRENT)
public class MiscTransitTest extends AbstractTest {

    @RepeatedTest(360)
    void testShaniSadeSati(RepetitionInfo info) {
        final int elev = info.getCurrentRepetition();
        int lat = IModuloUtils.modulo(90, elev);
        int lng = IModuloUtils.modulo(180, elev);

        if ((elev / 90) % 2 != 0) lat = -lat;
        if (elev > 180) lng = -lng;

        final StringBuilder builder = new StringBuilder(255);

        if (elev % 2 == 0) {
            builder.append("-p6 -lon0/60/120/180/240/300/360");
        } else builder.append("-p6 -lon30/90/150/210/270/330/0");

        builder.append(" -topo").append(lat).append(';')
                .append(lng).append(';').append(elev);
        builder.append(" -b1/1/").append(1900 + elev);
        builder.append(" -b1/1/").append(2000 + elev);
        builder.append(" -Dloc24en -fj10v -ut -eswe");
        builder.append(" -sid1 -true -edirephe");

        //System.out.println(builder);

        Transits gochara1 = new Transits(getSwephExp());
        Transits gochara2 = new Transits(getSwissEph());

        String[] argv = builder.toString().split(" ");
        gochara1.startCalculations(argv, true);
        gochara2.startCalculations(argv, true);

        ArrayList<String> output1 = gochara1.getSwissOut().getOutput();
        ArrayList<String> output2 = gochara2.getSwissOut().getOutput();

        assertEquals(output2.size(), output1.size());

        for (int i = 0; i < output1.size(); i++) {
            if (i < 8) continue;

            String value1 = output1.get(i);
            String value2 = output2.get(i);

            String[] vals1 = value1.split(" ");
            String[] vals2 = value2.split(" ");

            assertEquals(Double.parseDouble(vals1[0]),
                    Double.parseDouble(vals2[0]), 0.025);
        }
    }
}
