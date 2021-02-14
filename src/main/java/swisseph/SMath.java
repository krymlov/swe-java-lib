/* java.lang.SMath -- common mathematical functions
   Copyright (C) Sourav.*/


/*
 * Some of the algorithms in this class are in the public domain, as part
 * of fdlibm (freely-distributable math library), available at
 * http://www.netlib.org/fdlibm/, and carry the following copyright:
 * ====================================================
 * Copyright (C) 1993 by Sun Microsystems, Inc. All rights reserved.
 *
 * Developed at SunSoft, a Sun Microsystems, Inc. business.
 * Permission to use, copy, modify, and distribute this
 * software is freely granted, provided that this notice
 * is preserved.
 * ====================================================
 */
package swisseph;

/**
 * Helper class mapping all mathematical functions and constants to
 * the java.lang.StrictMath class.
 */
public class SMath
		implements java.io.Serializable
		{
  public static final double E = StrictMath.E;
  public static final double PI = StrictMath.PI;
  public static int abs(int i) { return StrictMath.abs(i); }
  public static long abs(long l) { return StrictMath.abs(l); }
  public static float abs(float f) { return StrictMath.abs(f); }
  public static double abs(double d) { return StrictMath.abs(d); }
  public static int min(int a, int b) {return StrictMath.min(a, b); }
  public static long min(long a, long b) {return StrictMath.min(a, b); }
  public static float min(float a, float b) {return StrictMath.min(a, b); }
  public static double min(double a, double b) {return StrictMath.min(a, b); }
  public static int max(int a, int b) { return StrictMath.max(a, b); }
  public static long max(long a, long b) { return StrictMath.max(a, b); }
  public static float max(float a, float b) { return StrictMath.max(a, b); }
  public static double max(double a, double b) { return StrictMath.max(a, b); }
  public static double sin(double a) { return StrictMath.sin(a); }
  public static double cos(double a) { return StrictMath.cos(a); }
  public static double tan(double a) { return StrictMath.tan(a); }
  public static double asin(double x) { return StrictMath.asin(x); }
  public static double acos(double x) { return StrictMath.acos(x); }
  public static double atan(double x) { return StrictMath.atan(x); }
  public static double atan2(double y, double x) { return StrictMath.atan2(y, x); }
  public static double exp(double x) { return StrictMath.exp(x); }
  public static double log(double x) { return StrictMath.log(x); }
  public static double sqrt(double x) { return StrictMath.sqrt(x); }
  public static double pow(double x, double y) { return StrictMath.pow(x, y); }
  public static double IEEEremainder(double x, double y) { return StrictMath.IEEEremainder(x, y); }
  public static double ceil(double a) { return StrictMath.ceil(a); }
  public static double floor(double a) { return StrictMath.floor(a); }
  public static double rint(double a) { return StrictMath.rint(a); }
  public static int round(float f) { return StrictMath.round(f); }
  public static long round(double d) { return StrictMath.round(d); }
  public static synchronized double random() { return StrictMath.random(); }
}
