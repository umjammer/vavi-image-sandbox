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
 * Point.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/14 nsano initial version <br>
 */
public class Point implements Primitive<Point> {
    private float x, y, z;

    public Point() {
        x = y = z = 0.0f;
    }

    public Point(float f) {
        x = y = z = f;
    }

    public Point(float x0, float y0, float z0) {
        x = x0;
        y = y0;
        z = z0;
    }

    public Point operatorEqual(Point p) {
        x = p.x;
        y = p.y;
        z = p.z;
        return this;
    }

    public Point operatorPlusEqual(Point p) {
        x += p.x;
        y += p.y;
        z += p.z;
        return this;
    }

    public Point operatorMinusEqual(Point p) {
        x -= p.x;
        y -= p.y;
        z -= p.z;
        return this;
    }

    public Point operatorMultiplyEqual(float a) {
        x *= a;
        y *= a;
        z *= a;
        return this;
    }

    public Point operatorPlus(Point p) {
        return p.operatorPlusEqual(this);
    }

    public Point operatorMinus(Point p) {
        p.x = x - p.x;
        p.y = y - p.y;
        p.z = z - p.z;
        return p;
    }

    public Point operatorMinus() {
        return new Point(-x, -y, -z);
    }

    public float operatorMultiply(Point p) {
        return x * p.x + y * p.y + z * p.z;
    }

    public Point operatorMultiply(float a) {
        return new Point(x * a, y * a, z * a);
    }

    public boolean isZero() {
        return x == 0.0 && y == 0.0 && z == 0.0;
    }

    public Point xProd(Point p) {
        return new Point(y * p.z - z * p.y, z * p.x - x * p.z, x * p.y - y * p.x);
    }

    public Point normalized() {
        float d = (float) Math.sqrt(x * x + y * y + z * z);
        d = 1.0f / d;
        return new Point(x * d, y * d, z * d);
    }

    public void normalize() {
        float d = (float) Math.sqrt(x * x + y * y + z * z);
        d = 1.0f / d;
        x *= d;
        y *= d;
        z *= d;
    }

    public float norm() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public float norm1() {
        return (float) (1.0 / 3.0) * (Math.abs(x) + Math.abs(y) + Math.abs(z));
    }

    public void clip() {
        if (x < 0.0)
            x = 0.0f;
        else if (x > 1.0)
            x = 1.0f;
        if (y < 0.0)
            y = 0.0f;
        else if (y > 1.0)
            y = 1.0f;
        if (z < 0.0)
            z = 0.0f;
        else if (z > 1.0)
            z = 1.0f;
    }

    public void fromColor(long col) {
        x = ((col >> 16) & 255) * (1.0f / 255.0f);
        y = ((col >> 8) & 255) * (1.0f / 255.0f);
        z = ((col) & 255) * (1.0f / 255.0f);
    }

    public void addMul(float a, Point p) {
        x += a * p.x;
        y += a * p.y;
        z += a * p.z;
    }

    public void setZero() {
        x = y = z = 0.0f;
    }

    public long colorCast() {
        long ll, vv;
        vv = (long) (x * 255);
        vv = (vv < 0 ? 0 : (vv > 255 ? 255 : vv));
        ll = vv << 16;
        vv = (long) (y * 255);
        vv = (vv < 0 ? 0 : (vv > 255 ? 255 : vv));
        ll += vv << 8;
        vv = (long) (z * 255);
        vv = (vv < 0 ? 0 : (vv > 255 ? 255 : vv));
        ll += vv;
        return ll;
    }

    public Point operatorMultiply(float a, Point p) {
        p = p.operatorMultiplyEqual(a);
        return p;
    }
}

/* */
