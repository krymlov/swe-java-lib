/*
* Copyright (C) By the Author
* Author    Yura Krymlov
* Created   2019-11
*/

package org.swisseph.api;

import java.io.Serializable;

/**
 * @author  Yura Krymlov
 * @version 1.1, 2019-11
 */
public interface ISweEnumEntity<E extends ISweEnum> extends Serializable {
    /**
     * @return this entity's enum
     */
    E entityEnum();

    /**
     * @return this entity's julian day
     */
    double julianDay();

    /**
     * @return this entity's longitude
     */
    double longitude();

}
