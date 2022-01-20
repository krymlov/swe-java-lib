/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2019-08
 */

package org.swisseph.api;

import java.io.Serializable;

/**
 * @author Yura Krymlov
 * @version 1.2, 2021-12
 */
public interface ISweSegment extends Serializable {
    double start();
    double close();
}
