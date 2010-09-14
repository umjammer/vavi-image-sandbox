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
 * Point2. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/14 nsano initial version <br>
 */
public class Point2 implements Primitive<Point2> {
    float x, y;

    public Point2() {
        x = y = 0.0f;
    }

    public Point2(float f) {
        x = y = f;
    }

    public Point2(float x0, float y0) {
        x = x0;
        y = y0;
    }

    public Point2 operatorEqual(Point2 p) {
        x = p.x;
        y = p.y;
        return this;
    }

    public Point2 operatorPlusEqual(Point2 p) {
        x += p.x;
        y += p.y;
        return this;
    }

    public Point2 operatorMinusEqual(Point2 p) {
        x -= p.x;
        y -= p.y;
        return this;
    }

    public Point2 operatorMultiplyEqual(float a) {
        x *= a;
        y *= a;
        return this;
    }

    public Point2 operatorPlus(Point2 p) {
        return p.operatorPlusEqual(this);
    }

    public Point2 operatorMinus(Point2 p) {
        p.x = x - p.x;
        p.y = y - p.y;
        return p;
    }

    public Point2 operatorMinus() {
        return new Point2(-x, -y);
    }

    public float operatorMultiply(Point2 p) {
        return x * p.x + y * p.y;
    }

    public Point2 operatorMultiply(float a) {
        return new Point2(x * a, y * a);
    }

    public boolean isZero() {
        return x == 0.0 && y == 0.0;
    }

    public Point2 normalized() {
        float d = (float) Math.sqrt(x * x + y * y);
        d = 1.0f / d;
        return new Point2(x * d, y * d);
    }

    public void normalize() {
        float d = (float) Math.sqrt(x * x + y * y);
        if (d > 0.0) {
            d = 1.0f / d;
            x *= d;
            y *= d;
        }
    }

    public float norm() {
        return (1.0f / 2.0f) * (Math.abs(x) + Math.abs(y));
    }

    public float norm1() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public void clip() {
    }

    public void addMul(float a, Point2 p) {
        x += a * p.x;
        y += a * p.y;
    }

    public void setZero() {
        x = y = 0.0f;
    }

    public Point2 doubleDir() { // angle of vector doubled, length unmodified
        Point2 q = new Point2();
        float d = (float) Math.sqrt(x * x + y * y);
        if (d == 0.0)
            return new Point2(0.0f, 0.0f);
        d = 1.0f / d;
        q.x = (x * x - y * y) * d;
        q.y = 2 * x * y * d;
        return q;
    }

    public Point2 doubleDirNorm() { // angle of vector doubled, length normalized
        Point2 q = new Point2();
        float d = (x * x + y * y);
        if (d == 0.0f)
            return new Point2(0.0f, 0.0f);
        d = 1.0f / d;
        q.x = (x * x - y * y) * d;
        q.y = 2 * x * y * d;
        return q;
    }

    public float angle() {
        float d, cc, ss, phi;
        d = (float) Math.sqrt(x * x + y * y);
        if (d == 0.0)
            return 0.0f;
        d = 1.0f / d;
        cc = x * d;
        ss = y * d;
        if (cc > -0.8f && cc < 0.8f) {
            phi = (float) (Math.acos(cc) * 360.0f * (0.5f / Math.PI));
            if (ss < 0.0f)
                phi = 360.0f - phi;
        } else {
            phi = (float) (Math.asin(ss) * 360.0 * (0.5 / Math.PI));
            if (cc < 0.0f)
                phi = 180.0f - phi;
            if (phi < 0.0f)
                phi += 360.0f;
        }
        return phi;
    }

    public Point2 operatorMultiply(float a, Point2 p) {
        p = p.operatorMultiplyEqual(a);
        return p;
    }
}

/* */
