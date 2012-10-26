/*
 * SmillaEnlarger  -  resize, especially magnify bitmaps in high quality
 *
 * Copyright (C) 2009 Mischa Lusteck
 * 
 * This program is free software;
 * you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation;
 * either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses/>.
 */

package vavix.awt.image.resample.enlarge.smilla;


/**
 * some static finalants
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/14 nsano initial version <br>
 */
class Constants {
    static final int enExp = 3;

    static final int enLen = (1 << enExp);

// len of dstBlock
    static final int blockExp = 9;

    static final int blockLen = (1 << blockExp);

// srcBlock contains additional margins of borderPixels
    static final int srcBlockMargin = 9;

// sharper peak at 0.0 -> others are less similar
    static final float similPeakThinness = 8.0f;

//
    static final float similPeakFlatness = 1.5f;

// 0.09
    static final float similWeightFakt = 0.09f;

// fakt*peak + ( 1 - fakt )*linear
    static final float similPeakFakt = 0.7f;

    //
// modifies weight of indies
    static final float indieWeightFact = 1.0f;

// higher value -> individuality at smaller distance
    static final float indieSensitivity = 7.0f;

// threshold for summed-up individuality
    static final float indieThreshold = 1.0f;

    //
// sharper peak at 0.0 -> others are less similar
// and thus less weighted -> sharper image
    static final float selectPeakSharpness = 10.0f;

    static final int kerFineExp = 4;

    static final int kerFineLen = 1 << kerFineExp;

    static final int diffTabLen = 1000;

    static final int invTabLen = 10000;

    static float pow_f(float a, float b) {
        return (float) Math.pow(a, b);
    }
}
