/*
* Copyright (C) By the Author
* Author    Yura Krymlov
* Created   2020-01
*/

package org.swisseph.app;

import org.swisseph.api.ISweObjectsSequence;

import java.util.Arrays;

import static org.swisseph.api.ISweObjects.LG;

/**
 * http://www.th-mack.de/download/jyotish-0.21a-bin.zip
 * 
 * @author  Thomas Mack (December 14, 2002)
 * @version 0.21a (adjusted by Yura in 2019-10)
 */
public class SweObjectsSequence implements ISweObjectsSequence {
    private static final long serialVersionUID = 1057568435339714459L;

    protected final double[] degrees;
    protected final int[] objects;
    protected boolean sorted;
    
    public SweObjectsSequence(double[] longitudes) {
        degrees = new double[longitudes.length];
        objects = new int[longitudes.length];

        for (int k = LG; k < longitudes.length; k++) {
            this.degrees[k] = longitudes[k];
            this.objects[k] = k;
        }
    }

    @Override
    public void sort() {
        if ( sorted ) return;
        
        int var1 = this.objects.length;
        boolean var2;
        
        do {
            --var1;
            if ( var1 < 0 ) {
                return;
            }

            var2 = false;

            for (int var3 = 0; var3 < var1; ++var3) {
                if ( this.degrees[var3] > this.degrees[var3 + 1] ) {
                    double var4 = this.degrees[var3];
                    int var6 = this.objects[var3];
                    this.degrees[var3] = this.degrees[var3 + 1];
                    this.objects[var3] = this.objects[var3 + 1];
                    this.degrees[var3 + 1] = var4;
                    this.objects[var3 + 1] = var6;
                    var2 = true;
                }
            }
        } while ( var2 );

        sorted = true;
    }

    @Override
    public int[] objects() {
        return objects;
    }

    @Override
    public double[] degrees() {
        return degrees;
    }

    @Override
    public boolean sorted() {
        return sorted;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(objects);
        result = prime * result + Arrays.hashCode(degrees);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        
        final SweObjectsSequence other = (SweObjectsSequence)obj;
        if ( !Arrays.equals(objects, other.objects) ) return false;
        return Arrays.equals(degrees, other.degrees);
    }
}
