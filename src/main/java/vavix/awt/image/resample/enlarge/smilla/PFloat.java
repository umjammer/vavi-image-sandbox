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
 * simple vector classes
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/14 nsano initial version <br>
 */
class PFloat implements Primitive<PFloat> {

    private float x;

    public PFloat() {
        x = 0.0f;
    }

    public PFloat(float f) {
        x = f;
    }

    public PFloat operatorEqual(PFloat p) {
        x = p.x;
        return this;
    }

    public PFloat operatorPlusEqual(PFloat p) {
        x += p.x;
        return this;
    }

    public PFloat operatorMinusEqual(PFloat p) {
        x -= p.x;
        return this;
    }

    public PFloat operatorMultiplyEqual(float a) {
        x *= a;
        return this;
    }

    public PFloat operatorPlus(PFloat p) {
        return p.operatorPlusEqual(this);
    }

    public PFloat operatorMinus(PFloat p) {
        p.x = x - p.x;
        return p;
    }

    public PFloat operatorMinus() {
        return new PFloat(-x);
    }

    public float operatorMultiply(PFloat p) {
        return x * p.x;
    }

    public PFloat operatorMultiply(float a) {
        return new PFloat(x * a);
    }

    public boolean isZero() {
        return x == 0.0;
    }

    public PFloat normalized() {
        return new PFloat(1.0f);
    }

    public void normalize() {
        x = 1.0f;
    }

    public float norm() {
        return Math.abs(x);
    }

    public float norm1() {
        return Math.abs(x);
    }

    public void clip() {
        if (x < 0.0)
            x = 0.0f;
        else if (x > 1.0)
            x = 1.0f;
    }

    public void addMul(float a, PFloat p) {
        x += a * p.x;
    }

    public void setZero() {
        x = 0.0f;
    }

    public float toF() {
        return x;
    }

    public PFloat operatorMultiply(float a, PFloat p) {
        p.operatorMultiplyEqual(a);
        return p;
    }
}

/* */