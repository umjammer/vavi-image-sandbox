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
 * RandGen.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/14 nsano initial version <br>
 */
class RandGen {
    private long rx, ry;

    public RandGen() {
        rx = 639286192;
        ry = 207155721;
    }

    public RandGen(long xx) {
        rx = xx;
        ry = rx * (rx >> 5) + 1130958141 + (rx >> 16);
    }

    public RandGen(long xx, long yy) {
        rx = xx;
        ry = yy;
    }

    public long randL() {
        rx = (rx ^ ry) + 78511087 + (ry >> 16);
        ry = ry * (rx >> 5) + 1130958141 + (rx >> 16);
        return ry;
    }

    public long randL(long n) {
        rx = (rx ^ ry) + 78511087 + (ry >> 16) + n;
        ry = ry * (rx >> 5) + 1130958141 + (rx >> 16);
        return ry;
    }

    public float randF() {
        return ((randL() >> 5) & 65535) * (1.0f / 65535.0f);
    }
}

/* */
