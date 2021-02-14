/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2020-01
 */

package org.swisseph.api;

import java.io.Serializable;

/**
 * @author Yura Krymlov
 * @version 1.0, 2020-01
 */
public interface ISweObjectsSequence extends Serializable {
    double[] degrees();
    int[] objects();
    boolean sorted();
    void sort();
}
