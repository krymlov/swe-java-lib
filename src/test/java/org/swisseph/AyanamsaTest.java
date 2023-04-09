/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2023-01
 */

package org.swisseph;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.swisseph.api.ISweAyanamsa;
import org.swisseph.api.ISweEnumIterator;

import static org.swisseph.app.SweAyanamsa.LAHIRI_ICRC;
import static org.swisseph.app.SweAyanamsa.iteratorTo;

/**
 * @author Yura Krymlov
 * @version 1.0, 2023-04
 */
public class AyanamsaTest extends AbstractTest {

    @Test
    void testAyanamsaNames() {
        ISweEnumIterator<ISweAyanamsa> iterator = iteratorTo(LAHIRI_ICRC);

        while (iterator.hasNext()) {
            ISweAyanamsa sweAyanamsa = iterator.next();
            String nameExp = getSwephExp().swe_get_ayanamsa_name(sweAyanamsa.fid());
            String nameEph = getSwissEph().swe_get_ayanamsa_name(sweAyanamsa.fid());
            Assertions.assertEquals(nameExp, nameEph);
        }
    }

}
