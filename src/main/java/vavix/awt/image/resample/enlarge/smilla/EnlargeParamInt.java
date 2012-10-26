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
 * data structure containing the sharp,flat,... parameters
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/14 nsano initial version <br>
 */
class EnlargeParamInt {

    /**
     * @param sharp 0 ~ 100 ?
     * @param flat
     * @param dither
     * @param deNoise
     * @param preSharp
     * @param fractNoise
     */
    public EnlargeParamInt(int sharp, int flat, int dither, int deNoise, int preSharp, int fractNoise) {
        this.sharp = sharp;
        this.flat = flat;
        this.dither = dither;
        this.deNoise = deNoise;
        this.preSharp = preSharp;
        this.fractNoise = fractNoise;
    }

    int sharp;
    int flat;
    int dither;
    int deNoise;
    int preSharp;
    int fractNoise;

    public EnlargeParameter floatParam() {
        EnlargeParameter p = new EnlargeParameter();
        p.sharp = (sharp + 1) * 0.01f;
        p.flat = (flat) * 0.01f;
        p.dither = (dither) * 0.01f;
        p.deNoise = (deNoise) * 0.02f;
        p.preSharp = (preSharp) * 0.01f;
        float f = (fractNoise) * 0.01f;
        p.fractNoise = 0.5f * f * (3.0f - f);
        return p;
    }
}
