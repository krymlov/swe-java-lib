package swisseph;

import org.swisseph.ISwissEph;

import java.util.Random;

/**
* Interface for different calculation- and comparison-classes used for
* transit calculations.
*/
public abstract class TransitCalculator implements java.io.Serializable {
    private static final long serialVersionUID = -7113406204240396993L;
    protected static final Random RANDOM = new Random();
    
    protected final ISwissEph sw;
  
  protected TransitCalculator(ISwissEph sw) {
      if ( null == sw ) {
          throw new IllegalArgumentException("ISwissEph cannot be NULL");
      }
      
      this.sw = sw;
  }

  // This method changes the offset value for the transit
  /**
  * @return Returns true, if one position value is identical to another
  * position value. E.g., 360 degree is identical to 0 degree in
  * circular angles.
  * @see #rolloverVal
  */
  public abstract boolean getRollover();
  /**
  * @return Returns the value, which is identical to zero on the other
  * end of a linear scale.
  * @see #rolloverVal
  */
  public double getRolloverVal() {
    return rolloverVal;
  }
  /**
   * @return Returns the lowest possible offset value.
   */
  public double getMinOffset() {
    return 0.;
  }
  /**
   * @return Returns the highest possible offset value.
   */
  public double getMaxOffset() {
    return 360.;
  }
  /**
  * This sets the degree or other value for the position or speed of
  * the planet to transit. It will be used on the next call to getTransit().
  * @param value The desired offset value.
  * @see #getOffset()
  */
  public abstract void setOffset(double value);
  /**
  * This returns the degree or other value of the position or speed of
  * the planet to transit.
  * @return The currently set offset value.
  * @see #setOffset(double)
  */
  public abstract double getOffset();
  /**
  * This returns all the &quot;object identifiers&quot; used in this
  * TransitCalculator. It may be the planet number or planet numbers,
  * when calculating planets.
  * @return An array of identifiers identifying the calculated objects.
  */
  public Object[] getObjectIdentifiers() {
    return null;
  }

  /**
  * Set the factor, when to stop the calculation.
  * The getTransit*() methods will iterate calculations until the maximum
  * precision in the planetary calculation routines has been passed. With
  * this method, you specify a factor to the calculation precision used.
  * E.g., 100 means, stop the calculation, when the difference between the
  * calculated value and requested value has crossed a value of 100 times
  * less than the precision available in the calculation routines. 0.01 on
  * the other hand would stop the calculation, BEFORE the maximum available
  * precision had been reached. The default for the precision factor is 1.<p>
  * @param pfac The factor for the precision as explained above.
  * <b>Note:</b> A value greater than one will NOT really increase precision,
  * it will just <i>appear</i> to do so. It can be handy to show more equal
  * values when approaching the transit point from different sides (forward
  * or backward) or from different starting points.
  * @see #getPrecisionFactor()
  */
  public abstract void setPrecisionFactor(double pfac);
  /**
  * Returns the factor used to control stopping of the calculation
  * iterations.
  * @return The precision factor as set by the setPrecisionFactor()
  * method.
  * @see #setPrecisionFactor(double)
  */
  public abstract double getPrecisionFactor();




  //////////////////////////////////////////////////////////////////////////////


  // Rollover from 360 degrees to 0 degrees for planetary longitudinal positions
  // or similar, or continuous and unlimited values:
  protected boolean rollover = false; // We need a rollover of 360 degrees being
                                      // equal to 0 degrees for longitudinal
                                      // position transits only.
  protected double rolloverVal = 360.; // if rollover, we roll over from 360 to 0
                                       // as default. Other values than 0.0 for the
                                       // minimum values are not supported for now.

  // These methods have to return the maxima of the first derivative of the
  // function, mathematically spoken...
  protected abstract double getMaxSpeed();
  protected abstract double getMinSpeed();

  // This method returns the precision in x-direction in an x-y-coordinate
  // system for the transit calculation routine.
  protected abstract double getDegreePrecision(double jdET);

  // This method returns the precision in y-direction in an x-y-coordinate
  // system from the x-direction precision.
  protected abstract double getTimePrecision(double degPrec);

  // This is the main routine, mathematically speaking: returning f(x):
  protected abstract double calc(double jdET);


  // This routine allows for changing jdET before starting calculations.
  double preprocessDate(double jdET, boolean back) {
    return jdET;
  }
  // These routines check the result if it meets the stop condition
  protected boolean checkIdenticalResult(double offset, double val) {
    return val == offset;
  }
  protected boolean checkResult(double offset, double lastVal, double val, boolean above, boolean pxway) {
      return (// transits from higher deg. to lower deg.:
               ( above && val<=offset && !pxway) ||
               // transits from lower deg. to higher deg.:
               (!above && val>=offset &&  pxway)) ||
              (rollover && (
               // transits from above the transit degree via rollover over
               // 0 degrees to a higher degree:
               (offset<lastVal && val>.9*rolloverVal && lastVal<20. && !pxway) ||
               // transits from below the transit degree via rollover over
               // 360 degrees to a lower degree:
               (offset>lastVal && val<20. && lastVal>.9*rolloverVal &&  pxway) ||
               // transits from below the transit degree via rollover over
               // 0 degrees to a higher degree:
               (offset>val && val>.9*rolloverVal && lastVal<20. && !pxway) ||
               // transits from above the transit degree via rollover over
               // 360 degrees to a lower degree:
               (offset<val && val<20. && lastVal>.9*rolloverVal &&  pxway))
              );
  }

  // Find next reasonable point to probe.
  protected double getNextJD(double jdET, double val, double offset, double min, double max, boolean back) {
    double jdPlus  = 0;
    double jdMinus = 0;
    if (rollover) {
      // In most cases here we cannot find out for sure if the distance
      // is decreasing or increasing. We take the smaller one of these:
      jdPlus  = SMath.min(val-offset,rolloverVal-val+offset)/SMath.abs(max);
      jdMinus = SMath.min(val-offset,rolloverVal-val+offset)/SMath.abs(min);
      if (back) {
        jdET -= SMath.min(jdPlus,jdMinus);
      } else {
        jdET += SMath.min(jdPlus,jdMinus);
      }
    } else { // Latitude, distance and speed calculations...
      //jdPlus = (back?(val-offset):(offset-val))/max;
      //jdMinus = (back?(val-offset):(offset-val))/min;
      jdPlus = (offset-val)/max;
      jdMinus = (offset-val)/min;
      if (back) {
        if (jdPlus >= 0 && jdMinus >= 0) {
          throw new SwissephException(jdET, SwissephException.OUT_OF_TIME_RANGE,
              -1, "No transit in ephemeris time range."); // I mean: No transits possible...
        } else if (jdPlus >= 0) {
          jdET += jdMinus;
        } else { // if (jdMinus >= 0)
          jdET += jdPlus;
        }
      } else {
        if (jdPlus <= 0 && jdMinus <= 0) {
          throw new SwissephException(jdET, SwissephException.OUT_OF_TIME_RANGE,
              -1, "No transit in ephemeris time range."); // I mean: No transits possible...
        } else if (jdPlus <= 0) {
          jdET += jdMinus;
        } else { // if (jdMinus <= 0)
          jdET += jdPlus;
        }
      }
    }
    return jdET;
  }
  
  /**
   * Searches for the next or previous transit of a planet over a specified
   * longitude, latitude, distance or speed value with geocentric or topocentric
   * positions in a tropical or sidereal zodiac. Dates are interpreted as ET
   * (=UT&nbsp;+&nbsp;deltaT).<p>
   * See swisseph.TCPlanet or swisseph.TCPlanetPlanet below for examples on
   * how to use this method.<p>
   *
   * @param tc The TransitCalculator that should be used here.
   * @param jdET The date (and time) in ET, from where to start searching.
   * @param backwards If backward search should be performed.
   * @return return A double containing the julian day number for the next /
   * previous transit as ET.
   * @see swisseph.TCPlanet
   * @see swisseph.TCPlanetPlanet
   */
  protected static double getTransitET(TransitCalculator tc, double jdET, boolean backwards)
   throws IllegalArgumentException, SwissephException {
     return getTransitET(tc,
                         jdET,
                         backwards,
                         (backwards?-Double.MAX_VALUE:Double.MAX_VALUE));
   }
   /**
   * Searches for the next or previous transit of a planet over a specified
   * longitude, latitude, distance or speed value with geocentric or topocentric
   * positions in a tropical or sidereal zodiac. Dates are interpreted as ET
   * (=UT&nbsp;+&nbsp;deltaT).<p>
   * See swisseph.TCPlanet or swisseph.TCPlanetPlanet below for examples on
   * how to use this method.<p>
   *
   * @param tc The TransitCalculator that should be used here.
   * @param jdET The date (and time) in ET, from where to start searching.
   * @param backwards If backward search should be performed.
   * @param jdLimit This is the date, when the search for transits should be
   * stopped, even if no transit point had been found up to then.
   * @return return A double containing the julian day number for the next /
   * previous transit as ET.
   * @see swisseph.TCPlanet
   * @see swisseph.TCPlanetPlanet
   */
   protected static double getTransitET(TransitCalculator tc, double jdET, boolean backwards, double jdLimit)
   throws IllegalArgumentException, SwissephException {
     boolean calcUT = (tc instanceof TCHouses);
     return Extensions.getTransit(tc, jdET - (calcUT ? SweDate.getDeltaT(jdET) : 0), 
         backwards, jdLimit) + (calcUT ? SweDate.getDeltaT(jdET) : 0);
   }
   /**
   * Searches for the next or previous transit of a planet over a specified
   * longitude, latitude, distance or speed value with geocentric or topocentric
   * positions in a tropical or sidereal zodiac. Dates are interpreted as UT
   * (=ET&nbsp;-&nbsp;deltaT).<p>
   * See swisseph.TCPlanet or swisseph.TCPlanetPlanet below for examples on
   * how to use this method.<p>
   *
   * @param tc The TransitCalculator that should be used here.
   * @param jdUT The date (and time) in UT, from where to start searching.
   * @param backwards If backward search should be performed.
   * @return return A double containing the julian day number for the next /
   * previous transit as UT.
   * @see swisseph.TCPlanet
   * @see swisseph.TCPlanetPlanet
   */
   public static double getTransitUT(
           TransitCalculator tc,
           double jdUT,
           boolean backwards)
          throws IllegalArgumentException, SwissephException {
     boolean calcUT = (tc instanceof TCHouses);
     double jdET = Extensions.getTransit(
                           tc,
                           jdUT + (calcUT ? 0 : SweDate.getDeltaT(jdUT)),
                           backwards,
                           (backwards?-Double.MAX_VALUE:Double.MAX_VALUE));
     return jdET - (calcUT ? 0 : SweDate.getDeltaT(jdET));
   }
   /**
   * Searches for the next or previous transit of a planet over a specified
   * longitude, latitude, distance or speed value with geocentric or topocentric
   * positions in a tropical or sidereal zodiac. Dates are interpreted as UT
   * (=ET&nbsp;-&nbsp;deltaT).<p>
   * See swisseph.TCPlanet or swisseph.TCPlanetPlanet below for examples on
   * how to use this method.<p>
   *
   * @param tc The TransitCalculator that should be used here.
   * @param jdUT The date (and time) in UT, from where to start searching.
   * @param backwards If backward search should be performed.
   * @param jdLimit This is the date, when the search for transits should be
   * stopped, even if no transit point had been found up to then. It is
   * interpreted as UT time as well.
   * @return return A double containing the julian day number for the next /
   * previous transit as UT.
   * @see swisseph.TCPlanet
   * @see swisseph.TCPlanetPlanet
   */
   protected static double getTransitUT(
           TransitCalculator tc,
           double jdUT,
           boolean backwards,
           double jdLimit)
          throws IllegalArgumentException, SwissephException {
     double jdET = Extensions.getTransit(
                           tc,
                           jdUT + SweDate.getDeltaT(jdUT),
                           backwards,
                           jdLimit + SweDate.getDeltaT(jdLimit));
     return jdET - SweDate.getDeltaT(jdET);
   }
}
