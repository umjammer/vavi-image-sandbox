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

package vavix.awt.image.resample.enlarge;


/**
 * EnlargeParameter. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/14 nsano initial version <br>
 */
public class EnlargeParameter {
    public float flat;
    public float sharp;
    public float deNoise;
    public float preSharp;
    public float dither;
    public float fractNoise;
}