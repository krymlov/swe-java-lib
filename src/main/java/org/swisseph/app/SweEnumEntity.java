/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2020-12
 */
package org.swisseph.app;

import org.swisseph.api.ISweEnum;
import org.swisseph.api.ISweEnumEntity;

import static org.swisseph.api.ISweConstants.CH_VS;
import static org.swisseph.api.ISweConstants.STR_EY;
import static org.swisseph.utils.IDegreeUtils.toDMSms;

/**
 * @author Yura Krymlov
 * @version 1.0, 2020-12
 */
public class SweEnumEntity<E extends ISweEnum> implements ISweEnumEntity<E> {
    private static final long serialVersionUID = -4015176768486717512L;

    protected final double julianDay;
    protected final double longitude;
    protected final E entityEnum;

    protected SweEnumEntity(final double longitude, final E entityEnum, final double julianDay) {
        this.entityEnum = entityEnum;
        this.julianDay = julianDay;
        this.longitude = longitude;
    }

    @Override
    public E entityEnum() {
        return entityEnum;
    }

    @Override
    public double longitude() {
        return longitude;
    }

    @Override
    public double julianDay() {
        return julianDay;
    }

    @Override
    public String toString() {
        return toBuilder(STR_EY).toString();
    }

    protected String printCode() {
        return entityEnum.code();
    }

    protected String printLongitude() {
        return toDMSms(longitude).toString();
    }

    protected String printJulianDay() {
        return new swisseph.SweDate(julianDay).toStringShort();
    }

    protected StringBuilder toBuilder(CharSequence firstElement) {
        final StringBuilder builder = new StringBuilder(64);

        if (firstElement.length() > 0) {
            builder.append(firstElement);
            builder.append(CH_VS);
        }

        return builder.append(printCode()).append(CH_VS)
                .append(printLongitude()).append(CH_VS)
                .append(printJulianDay());
    }
}
