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
 * Point4.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/14 nsano initial version <br>
 */
public class Point4 implements Primitive<Point4> {
    public float x, y, z, w;

    public Point4() {
        x = y = z = w = 0.0f;
    }

    public Point4(float f) {
        w = x = y = z = f;
    }

    public Point4(float x0, float y0, float z0, float w0) {
        x = x0;
        y = y0;
        z = z0;
        w = w0;
    }

    public Point4 operatorEqual(Point4 p) {
        x = p.x;
        y = p.y;
        z = p.z;
        w = p.w;
        return this;
    }

    public Point4 operatorPlusEqual(Point4 p) {
        x += p.x;
        y += p.y;
        z += p.z;
        w += p.w;
        return this;
    }

    public Point4 operatorMinusEqual(Point4 p) {
        x -= p.x;
        y -= p.y;
        z -= p.z;
        w -= p.w;
        return this;
    }

    public Point4 operatorMultiplyEqual(float a) {
        x *= a;
        y *= a;
        z *= a;
        w *= a;
        return this;
    }

    public Point4 operatorPlus(Point4 p) {
        return p.operatorPlusEqual(this);
    }

    public Point4 operatorMinus(Point4 p) {
        p.x = x - p.x;
        p.y = y - p.y;
        p.z = z - p.z;
        p.w = w - p.w;
        return p;
    }

    public Point4 operatorMinus() {
        return new Point4(-x, -y, -z, -w);
    }

    public float operatorMultiply(Point4 p) {
        return x * p.x + y * p.y + z * p.z + w * p.w;
    }

    public Point4 operatorMultiply(float a) {
        return new Point4(x * a, y * a, z * a, w * a);
    }

    public boolean isZero() {
        return x == 0.0 && y == 0.0 && z == 0.0 && w == 0.0;
    }

    public Point4 normalized() {
        float d = (float) Math.sqrt(x * x + y * y + z * z + w * w);
        d = 1.0f / d;
        return new Point4(x * d, y * d, z * d, w * d);
    }

    public void normalize() {
        float d = (float) Math.sqrt(x * x + y * y + z * z + w * w);
        d = 1.0f / d;
        x *= d;
        y *= d;
        z *= d;
        w *= d;
    }

    public float norm() {
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public float norm1() {
        return (float) (1.0 / 4.0) * (Math.abs(x) + Math.abs(y) + Math.abs(z) + Math.abs(w));
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
        if (w < 0.0)
            w = 0.0f;
        else if (w > 1.0)
            w = 1.0f;
    }

    public void fromColor(long col) { // ARGB
        x = (float) (((col >> 16) & 255) * (1.0 / 255.0));
        y = (float) (((col >> 8) & 255) * (1.0 / 255.0));
        z = (float) (((col) & 255) * (1.0 / 255.0));
        w = (float) (((col >> 24) & 255) * (1.0 / 255.0));
    }

    public void addMul(float a, Point4 p) {
        x += a * p.x;
        y += a * p.y;
        z += a * p.z;
        w += a * p.w;
    }

    public void setZero() {
        x = y = z = w = 0.0f;
    }

    public long colorCast() {
        long ll, vv; // ARGB
        vv = (long) (x * 255);
        vv = (vv < 0 ? 0 : (vv > 255 ? 255 : vv));
        ll = vv << 16;
        vv = (long) (y * 255);
        vv = (vv < 0 ? 0 : (vv > 255 ? 255 : vv));
        ll += vv << 8;
        vv = (long) (z * 255);
        vv = (vv < 0 ? 0 : (vv > 255 ? 255 : vv));
        ll += vv;
        vv = (long) (w * 255);
        vv = (vv < 0 ? 0 : (vv > 255 ? 255 : vv));
        ll += vv << 24;
        return ll;
    }

    public Point4 operatorMultiply(float a, Point4 p) {
        p = p.operatorMultiplyEqual(a);
        return p;
    }
}

/* */
