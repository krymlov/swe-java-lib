/*
   This is a port of the Swiss Ephemeris Free Edition, Version 2.00.00
   of Astrodienst AG, Switzerland from the original C Code to Java. For
   copyright see the original copyright notices below and additional
   copyright notes in the file named LICENSE, or - if this file is not
   available - the copyright notes at http://www.astro.ch/swisseph/ and
   following. 

   For any questions or comments regarding this port to Java, you should
   ONLY contact me and not Astrodienst, as the Astrodienst AG is not involved
   in this port in any way.

   Thomas Mack, mack@ifis.cs.tu-bs.de, 23rd of April 2001

*/
/* Copyright (C) 1997 - 2008 Astrodienst AG, Switzerland.  All rights reserved.

  License conditions
  ------------------

  This file is part of Swiss Ephemeris.

  Swiss Ephemeris is distributed with NO WARRANTY OF ANY KIND.  No author
  or distributor accepts any responsibility for the consequences of using it,
  or for whether it serves any particular purpose or works at all, unless he
  or she says so in writing.

  Swiss Ephemeris is made available by its authors under a dual licensing
  system. The software developer, who uses any part of Swiss Ephemeris
  in his or her software, must choose between one of the two license models,
  which are
  a) GNU public license version 2 or later
  b) Swiss Ephemeris Professional License

  The choice must be made before the software developer distributes software
  containing parts of Swiss Ephemeris to others, and before any public
  service using the developed software is activated.

  If the developer choses the GNU GPL software license, he or she must fulfill
  the conditions of that license, which includes the obligation to place his
  or her whole software project under the GNU GPL or a compatible license.
  See http://www.gnu.org/licenses/old-licenses/gpl-2.0.html

  If the developer choses the Swiss Ephemeris Professional license,
  he must follow the instructions as found in http://www.astro.com/swisseph/
  and purchase the Swiss Ephemeris Professional Edition from Astrodienst
  and sign the corresponding license contract.

  The License grants you the right to use, copy, modify and redistribute
  Swiss Ephemeris, but only under certain conditions described in the License.
  Among other things, the License requires that the copyright notices and
  this notice be preserved on all copies.

  Authors of the Swiss Ephemeris: Dieter Koch and Alois Treindl

  The authors of Swiss Ephemeris have no control or influence over any of
  the derived works, i.e. over software or services created by other
  programmers which use Swiss Ephemeris functions.

  The names of the authors or of the copyright holder (Astrodienst) must not
  be used for promoting any software, product or service which uses or contains
  the Swiss Ephemeris. This copyright notice is the ONLY place where the
  names of the authors can legally appear, except in cases where they have
  given special permission in writing.

  The trademarks 'Swiss Ephemeris' and 'Swiss Ephemeris inside' may be used
  for promoting such software, products or services.
*/
package swisseph;

import static java.lang.Math.round;
import static org.swisseph.api.ISweConstants.*;

/**
 * This class keeps date and time values in just one place.<p>
 * Use it for UTC time conversions.
 *
 * @author Thomas Mack / mack@ifis.cs.tu-bs.de
 * @version 1.0.0a
 */

public final class SDate {
    final int[] ymdhms = new int[7];
    final double hour, second;

    public SDate(int year, int month, int day, double hour) {
        this.hour = hour;

        this.ymdhms[0] = year;
        this.ymdhms[1] = month;
        this.ymdhms[2] = day;
        this.ymdhms[3] = (int) hour;

        double dtmp = hour;
        dtmp -= (int) hour;
        dtmp *= d60;

        this.ymdhms[4] = (int) dtmp;
        this.second = (dtmp - this.ymdhms[4]) * d60;
        this.ymdhms[5] = (int) this.second;

        final double millis = (second - (int) second) * d1000;
        if (millis > d999) this.ymdhms[6] = (int) millis;
        else this.ymdhms[6] = (int) round(millis);
    }

    public SDate(int year, int month, int day, int hour, int minute, double dsec) {
        this.ymdhms[0] = year;
        this.ymdhms[1] = month;
        this.ymdhms[2] = day;

        this.ymdhms[3] = hour;
        this.ymdhms[4] = minute;

        this.second = dsec;
        this.ymdhms[5] = (int) dsec;

        final double millis = (dsec - (int) dsec) * d1000;
        if (millis > d999) this.ymdhms[6] = (int) millis;
        else this.ymdhms[6] = (int) round(millis);

        double time = ymdhms[3];
        time += (ymdhms[4] / d60);
        time += (dsec / d3600);

        this.hour = time;
    }

    public double getSecond() {
        return second;
    }

    public double getHour() {
        return hour;
    }

    public int[] getDate() {
        return ymdhms;
    }
}
