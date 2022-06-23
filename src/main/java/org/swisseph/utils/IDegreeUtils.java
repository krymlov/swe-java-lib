/*
* Copyright (C) By the Author
* Author    Yura Krymlov
* Created   2019-07
*/

package org.swisseph.utils;


import org.swisseph.api.ISweConstants;

import java.math.BigDecimal;

import static org.swisseph.api.ISweConstants.*;
import static swisseph.SweConst.ODEGREE_CHAR;

/**
 * @author Yura Krymlov
 * @version 1.1, 2019-07
 */
public interface IDegreeUtils {

    /**
     * The method is intended to convert latitude DD 49.758665 to DMS like 49°45'31"N
     */
    static StringBuilder toLAT(final double degree) {
        return toDMS(Math.abs(degree)).append(degree >= 0 ? LAT_NORTH : LAT_SOUTH);
    }
    
    /**
     * The method is intended to convert longitude DD 27.199346 to DMS like 27°11'58"E
     */
    static StringBuilder toLON(final double degree) {
        return toDMS(Math.abs(degree)).append(degree >= 0 ? LONG_EAST : LONG_WEST);
    }
    
    /**
     * The method is intended to convert latitude DD 49.758665 to DMS like 49°45'31.00"N
     */
    static StringBuilder toLATms(final double degree) {
        return toDMSms(Math.abs(degree)).append(degree >= 0 ? LAT_NORTH : LAT_SOUTH);
    }
    
    /**
     * The method is intended to convert longitude DD 27.199346 to DMS like 27°11'58.00"E
     */
    static StringBuilder toLONms(final double degree) {
        return toDMSms(Math.abs(degree)).append(degree >= 0 ? LONG_EAST : LONG_WEST);
    }

    /**
     * The method is intended to convert double like 49.75764 to DMS like 49°45'28"
     */
    static StringBuilder toDMS(final double degree) {
        return toDMS(degree, false);
    }
    
    /**
     * The method is intended to convert double like 49.75764 to DMS like 49°45'28"
     *  
     * @param arg to convert (it will be rounded to a second)
     * @param timeFormat if true then 49:45:28
     * @return string like 49°45'28" or 49:45:28
     */
    static StringBuilder toDMS(double arg, boolean timeFormat) {
        final StringBuilder builder = new StringBuilder(9);
        
        if ( arg < 0 ) {
            arg = -arg;
            builder.append(CH_DS);
        }

        arg += ISweConstants.d05d3600;
        final int kdeg = (int)arg;
        
        arg -= kdeg; arg *= d60;
        final int kmin = (int)arg;
        
        arg -= kmin; arg *= d60;
        final int ksec = (int)arg;
        
        if ( kdeg < i10 ) builder.append(CH_ZR);
        builder.append(kdeg);
        
        if ( timeFormat ) builder.append(CH_CN);
        else builder.append(ODEGREE_CHAR);
        
        if ( kmin < i10 ) builder.append(CH_ZR);
        builder.append(kmin);
        
        if ( timeFormat ) builder.append(CH_CN);
        else builder.append(CH_SQ);

        if ( ksec < i10 ) builder.append(CH_ZR);
        builder.append(ksec);

        if ( !timeFormat ) builder.append(CH_DQ);
        
        return builder;
    }

    static StringBuilder toDMS(double arg, int dms, boolean timeFormat) {
        final StringBuilder builder = new StringBuilder(9);

        if ( arg < 0 ) {
            arg = -arg;
            builder.append(CH_DS);
        }

        arg += ISweConstants.d05d3600;
        final int kdeg = (int)arg;

        if ( kdeg < i10 ) builder.append(CH_ZR);
        builder.append(kdeg);

        if ( !timeFormat ) builder.append(ODEGREE_CHAR);
        if ( dms == 1 ) return builder;

        if ( timeFormat ) builder.append(CH_CN);

        arg -= kdeg; arg *= d60;
        final int kmin = (int)arg;

        if ( kmin < i10 ) builder.append(CH_ZR);
        builder.append(kmin);

        if ( !timeFormat ) builder.append(CH_SQ);
        if ( dms == 2 ) return builder;

        arg -= kmin; arg *= d60;
        final int ksec = (int)arg;

        if ( timeFormat ) builder.append(CH_CN);

        if ( ksec < i10 ) builder.append(CH_ZR);
        builder.append(ksec);

        if ( !timeFormat ) builder.append(CH_DQ);
        return builder;
    }
    
    /**
     * The method is intended to convert double like 49.75764 to DMS like 49°45'27.50"
     */
    static StringBuilder toDMSms(final double degree) {
        return toDMSms(degree, false);
    }
    
    /**
     * The method is intended to convert double like 49.75764 to DMS like 49°45'27.50"
     *  
     * @param arg to convert
     * @param timeFormat if true then 49:45:27.50
     * @return string like 49°45'27.50" or 49:45:27.50
     */
    static StringBuilder toDMSms(double arg, boolean timeFormat) {
        final StringBuilder builder = new StringBuilder(12);
        
        if ( arg < 0 ) {
            arg = -arg;
            builder.append(CH_DS);
        }
                
        final int kdeg = (int)arg;
        
        arg -= kdeg; arg *= d60;
        final int kmin = (int)arg;
        
        arg -= kmin; arg *= d60;
        final int ksec = (int)arg;
        
        arg -= ksec; arg *= d100;
        final int kmls = (int)arg;
        
        if ( kdeg < i10 ) builder.append(CH_ZR);
        builder.append(kdeg);
        
        if ( timeFormat ) builder.append(CH_CN);
        else builder.append(ODEGREE_CHAR);
        
        if ( kmin < i10 ) builder.append(CH_ZR);
        builder.append(kmin);
        
        if ( timeFormat ) builder.append(CH_CN);
        else builder.append(CH_SQ);

        if ( ksec < i10 ) builder.append(CH_ZR);
        builder.append(ksec).append(CH_DT);
        
        if ( kmls < i10 ) builder.append(CH_ZR);
        builder.append(kmls);

        if ( !timeFormat ) builder.append(CH_DQ);

        return builder;
    }

    /**
     * The method is intended to convert decimal degree like 49.75764 to integer degree like 49452750 
     *  
     * @param arg degree to convert
     * @return integer degree like 49452750
     */
    static int toIDMSms(double arg) {
        final StringBuilder builder = new StringBuilder(12);
        
        if ( arg < 0 ) {
            arg = -arg;
            builder.append(CH_DS);
        }
                
        final int kdeg = (int)arg;
        
        arg -= kdeg; arg *= d60;
        final int kmin = (int)arg;
        
        arg -= kmin; arg *= d60;
        final int ksec = (int)arg;
        
        arg -= ksec; arg *= d100;
        final int kmls = (int)arg;

        builder.append(kdeg);

        if ( kmin < i10 ) builder.append(CH_ZR);
        builder.append(kmin);

        if ( ksec < i10 ) builder.append(CH_ZR);
        builder.append(ksec);
        
        if ( kmls < i10 ) builder.append(CH_ZR);
        return Integer.parseInt(builder.append(kmls).toString());
    }
    
    /**
     * The method is intended to convert integer degree like 49452750 to DMS string like 49°45'27.50"
     */
    static StringBuilder toDMSms(final int degree) {
        return toDMSms(degree, false);
    }
    
    /**
     * The method is intended to convert integer degree like 49452750 to DMS string like 49°45'27.50" 
     *  
     * @param degree to convert
     * @param timeFormat if true then 49:45:27.50
     * @return string like 49°45'27.50" or 49:45:27.50
     */
    static StringBuilder toDMSms(int degree, boolean timeFormat) {
        final StringBuilder builder = new StringBuilder(16);
        
        if ( degree < 0 ) {
            degree = -degree;
            builder.append(CH_DS);
        }

        int deg = degree / i100;
        final int mls = degree % i100;
        final int sec = deg % i100;
        final int min = (deg /= i100) % i100;
        
        deg /= i100;
        if ( deg < i10 ) builder.append(CH_ZR);
        builder.append(deg);
        
        if ( timeFormat ) builder.append(CH_CN);
        else builder.append(ODEGREE_CHAR);

        if ( min < i10 ) builder.append(CH_ZR);
        builder.append(min);
        
        if ( timeFormat ) builder.append(CH_CN);
        else builder.append(CH_SQ);

        if ( sec < i10 ) builder.append(CH_ZR);
        builder.append(sec).append(CH_DT);
        
        if ( mls < i10 ) builder.append(CH_ZR);
        builder.append(mls);

        if ( !timeFormat ) builder.append(CH_DQ);

        return builder;
    }

    /**
     * <pre>
     * The method is intended to convert integer like 49452750 to decimal degree like 49.75764 
     *  
     * Decimal Degrees = degrees + (minutes/60.) + (seconds/3600.)
     * 49°45'27.50" -> 49.75764
     * </pre>
     * 
     * @param degree to convert
     * @return decimal degree like 49.75764
     */
    static double toDDms(int degree) {
        boolean ng = false;

        if ( degree < 0 ) {
            degree = -degree;
            ng = true;
        }
        
        if ( i0 == degree ) return 0d;
        
        int arg = degree / i100;
        final int sec = (arg % i100);
        final int mls = (degree % i100);
        final int min = (arg /= i100) % i100;
        double dd = (arg / i100); // it is correct

        dd += min/d60;
        dd += sec/d3600;
        dd += mls/d360000;
        dd += d005d3600;
        
        // 360000 == (3600 * 1000) / 10 
        // (it is because millis in NN format 
        // means NNN = NN * 10 should be
        
        return ng ? -dd : dd;
    }

    static double round(double d, int scale) {
        return round(d, scale, 4);
    }

    static double round(double d, int scale, int roundingMethod) {
        try {
            double rounded = (new BigDecimal(Double.toString(d)))
                    .setScale(scale, roundingMethod).doubleValue();
            return rounded == 0.0D ? 0.0D * d : rounded;
        } catch (NumberFormatException var6) {
            return Double.isInfinite(d) ? d : 0.0D / 0.0;
        }
    }

}
