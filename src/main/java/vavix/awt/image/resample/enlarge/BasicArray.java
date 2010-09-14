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

import java.lang.reflect.ParameterizedType;


/**
 * basic template for 2-dim. arrays
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/14 nsano initial version <br>
 */
class BasicArray<T extends Primitive<T>> {

    private int sizeX, sizeY;

    private T[] buf;

    // TODO èÍèäÇ¢Ç‹Ç¢Çø
    public T newContent() {
        try {
            return (T) ((Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]).newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public BasicArray() {
        sizeX = 0;
        sizeY = 0;
        buf = null;
    }

    public BasicArray(int sx, int sy) {
        sizeX = sx;
        sizeY = sy;
        buf = (T[]) new Primitive[sizeX * sizeY];
    }

    public BasicArray(BasicArray<T> aSrc) {
        sizeX = aSrc.sizeX;
        sizeY = aSrc.sizeY;
        buf = (T[]) new Primitive[sizeX * sizeY];

        System.arraycopy(aSrc.buf, 0, buf, 0, sizeX * sizeY);
    }

    public BasicArray<T> operatorEqual(BasicArray<T> aSrc) {
        changeSize(aSrc.sizeX, aSrc.sizeY);
        System.arraycopy(aSrc.buf, 0, buf, 0, sizeX * sizeY);
        return this;
    }

    public T get(int x, int y) {
        return buf[x + y * sizeX];
    }

    public void set(int x, int y, T p) {
        buf[x + y * sizeX] = p;
    }

    public void add(int x, int y, T p) {
        buf[x + y * sizeX] = buf[x + y * sizeX].operatorPlusEqual(p);
    }

    public void sub(int x, int y, T p) {
        buf[x + y * sizeX] = buf[x + y * sizeX].operatorMinusEqual(p);
    }

    public void mul(int x, int y, float f) {
        buf[x + y * sizeX] = buf[x + y * sizeX].operatorMultiplyEqual(f);
    }

    public T dX(int x, int y) {
        return get(x + 1, y).operatorMinus(get(x - 1, y)).operatorMultiply(0.5f);
    }

    public T dY(int x, int y) {
        return get(x, y + 1).operatorMinus(get(x, y - 1)).operatorMultiply(0.5f);
    }

    public T d2X(int x, int y) {
        return get(x + 1, y).operatorPlus(get(x - 1, y)).operatorMinus(get(x, y).operatorMultiply(2.0f));
    }

    public T d2Y(int x, int y) {
        return get(x, y + 1).operatorPlus(get(x, y - 1)).operatorMinus(get(x, y).operatorMultiply(2.0f));
    }

    public T dXY(int x, int y) {
        return get(x + 1, y + 1).operatorMinus(get(x - 1, y + 1))
                .operatorMinus(get(x + 1, y - 1))
                .operatorPlus(get(x - 1, y - 1));
    }

    public T laplace(int x, int y) {
        T l = get(x, y - 1).operatorPlus(get(x - 1, y)).operatorPlus(get(x + 1, y)).operatorPlus(get(x, y + 1));
        l = l.operatorMultiplyEqual(2.0f);
        l = l.operatorPlusEqual(get(x - 1, y - 1).operatorPlus(get(x + 1, y - 1))
                .operatorPlus(get(x - 1, y + 1))
                .operatorPlus(get(x + 1, y + 1)));
        l = l.operatorMultiplyEqual(1.0f / 12.0f);
        return l.operatorMinus(get(x, y));
    }

    public int sizeX() {
        return sizeX;
    }

    public int sizeY() {
        return sizeY;
    }

    public T[] buffer() {
        return buf;
    }

    public void changeSize(int sxNew, int syNew) {
        sizeX = sxNew;
        sizeY = syNew;
        buf = (T[]) new Primitive[sizeX * sizeY];
    }

    public void copyFromArray(BasicArray<T> srcArr, int srcX, int srcY) {
        int x, y, sx, sy;
        T[] dst;
        int src;
        y = 0;
        sy = srcY;
        dst = buf;
        int dstP = 0;

        // while dst outside: copy pixels of src-line 0
        while (sy <= 0 && y < sizeY) {
            x = 0;
            sx = srcX;
            src = 0;
            if (sx > 0)
                src += sx;
            // while dst outside: write src-edge-pixel
            while (sx < 0 && x < sizeX) {
                dst[dstP++] = srcArr.buf[src];
                x++;
                sx++;
            }

            while (sx < srcArr.sizeX - 1 && x < sizeX) {
                dst[dstP++] = srcArr.buf[src++];
                x++;
                sx++;
            }

            // while dst outside: write src-edge-pixel
            while (x < sizeX) {
                dst[dstP++] = srcArr.buf[src];
                x++;
                sx++;
            }
            y++;
            sy++;
        }

        while (sy < srcArr.sizeY - 1 && y < sizeY) {
            x = 0;
            sx = srcX;
            src = sy * srcArr.sizeX;
            if (sx > 0)
                src += sx;
            // while dst outside: write src-edge-pixel
            while (sx < 0 && x < sizeX) {
                dst[dstP++] = srcArr.buf[src];
                x++;
                sx++;
            }

            while (sx < srcArr.sizeX - 1 && x < sizeX) {
                dst[dstP++] = srcArr.buf[src++];
                x++;
                sx++;
            }

            // while dst outside: write src-edge-pixel
            while (x < sizeX) {
                dst[dstP++] = srcArr.buf[src];
                x++;
                sx++;
            }
            y++;
            sy++;
        }

        // for outside-parts: copy pixels of last src-line
        while (y < sizeY) {
            x = 0;
            sx = srcX;
            src = (srcArr.sizeY - 1) * srcArr.sizeX;
            if (sx > 0)
                src += sx;
            // while dst outside: write src-edge-pixel
            while (sx < 0 && x < sizeX) {
                dst[dstP++] = srcArr.buf[src];
                x++;
                sx++;
            }

            while (sx < srcArr.sizeX - 1 && x < sizeX) {
                dst[dstP++] = srcArr.buf[src++];
                x++;
                sx++;
            }

            // while dst outside: write src-edge-pixel
            while (x < sizeX) {
                dst[dstP++] = srcArr.buf[src];
                x++;
                sx++;
            }
            y++;
            sy++;
        }
    }

    public BasicArray<T> clip(int leftX, int topY, int sizeXNew, int sizeYNew) {
        BasicArray<T> clipArr = new BasicArray<T>(sizeXNew, sizeYNew);
        clipArr.copyFromArray(this, leftX, topY);
        return clipArr;
    }

    public BasicArray<T> smoothDouble() {
        BasicArray<T> newArray = new BasicArray<T>(sizeX * 2, sizeY * 2);

        T[] line0 = (T[]) new Primitive[2 * sizeX];
        T[] line1 = (T[]) new Primitive[2 * sizeX];
        T[] line2 = (T[]) new Primitive[2 * sizeX];

        readLineSmoothDouble(0, line1);
        readLineSmoothDouble(0, line2);
        for (int y = 0; y < sizeY; y++) {
            // scroll down: swap lines and refresh line2
            T[] hh = line0;
            line0 = line1;
            line1 = line2;
            line2 = hh;
            if (y < sizeY - 1)
                readLineSmoothDouble(y + 1, line2);
            else
                readLineSmoothDouble(y, line2);
            for (int x = 0; x < 2 * sizeX; x++) {
                T p0 = line0[x];
                T p1 = line1[x];
                T p2 = line2[x];
                T p = smoothFunc(p0, p1, p2);
                newArray.set(x, 2 * y, p);
                p = smoothFunc(p2, p1, p0);
                newArray.set(x, 2 * y + 1, p);
            }
        }
        return newArray;
    }

    public BasicArray<T> smoothDoubleTorus() {
        BasicArray<T> newArray = new BasicArray<T>(sizeX * 2, sizeY * 2);

        T[] line0 = (T[]) new Primitive[2 * sizeX];
        T[] line1 = (T[]) new Primitive[2 * sizeX];
        T[] line2 = (T[]) new Primitive[2 * sizeX];

        readLineSmoothDoubleTorus(sizeY - 1, line1);
        readLineSmoothDoubleTorus(0, line2);
        for (int y = 0; y < sizeY; y++) {
            // scroll down: swap lines and refresh line2
            T[] hh = line0;
            line0 = line1;
            line1 = line2;
            line2 = hh;
            if (y < sizeY - 1)
                readLineSmoothDoubleTorus(y + 1, line2);
            else
                readLineSmoothDoubleTorus(0, line2);
            for (int x = 0; x < 2 * sizeX; x++) {
                T p0 = line0[x];
                T p1 = line1[x];
                T p2 = line2[x];
                T p = smoothFunc(p0, p1, p2);
                newArray.set(x, 2 * y, p);
                p = smoothFunc(p2, p1, p0);
                newArray.set(x, 2 * y + 1, p);
            }
        }
        return newArray;
    }

    public BasicArray<T> shrinkHalf() {
        int x, y;
        BasicArray<T> halfArr = new BasicArray<T>((sizeX + 1) >> 1, (sizeY + 1) >> 1);

        for (y = 0; y < halfArr.sizeY(); y++) {
            for (x = 0; x < halfArr.sizeX(); x++) {
                halfArr.set(x, y, halfArr.newContent());
            }
        }

        for (y = 0; y < sizeY; y++) {
            for (x = 0; x < sizeX; x++) {
                halfArr.add(x >> 1, y >> 1, get(x, y));
            }
        }

        for (y = 0; y < halfArr.sizeY(); y++) {
            for (x = 0; x < halfArr.sizeX(); x++) {
                halfArr.mul(x, y, 0.25f);
            }
        }

        return halfArr;
    }

    public BasicArray<T> shrink(int sizeXNew, int sizeYNew) {
        if (sizeXNew <= 0)
            sizeXNew = 1;
        if (sizeYNew <= 0)
            sizeYNew = 1;

        float scaleX = (float) (sizeXNew) / (float) (sizeX);
        float scaleY = (float) (sizeYNew) / (float) (sizeY);
        int y, xBig;
        float ff;

        BasicArray<T> dst = new BasicArray<T>(sizeXNew, sizeYNew);
        T[] line = (T[]) new Primitive[sizeXNew];

        int yBig = 0;
        float floorY = 0.0f;
        for (y = 0; y < sizeY - 1; y++) {
            shrinkLine(y, scaleX, sizeXNew, line);
            ff = floorY + scaleY - 1.0f;
            if (ff > 0) { // stepping into new bigPixel reached
                for (xBig = 0; xBig < sizeXNew; xBig++) {
                    dst.add(xBig, yBig, line[xBig].operatorMultiply(scaleY - ff));
                    dst.add(xBig, yBig + 1, line[xBig].operatorMultiply(ff));
                }
                floorY -= 1.0;
                ++yBig;
            } else
                for (xBig = 0; xBig < sizeXNew; xBig++)
                    dst.add(xBig, yBig, line[xBig].operatorMultiply(scaleY));
            floorY += scaleY;
        }
        shrinkLine(sizeY - 1, scaleX, sizeXNew, line);
        for (xBig = 0; xBig < sizeXNew; xBig++)
            dst.add(xBig, sizeYNew - 1, line[xBig].operatorMultiply(scaleY));

        return dst;
    }

    public BasicArray<T> blockyPixEnlarge(int sizeXNew, int sizeYNew) {
        if (sizeXNew <= 0)
            sizeXNew = 1;
        if (sizeYNew <= 0)
            sizeYNew = 1;

        float scaleX = (sizeX) / (sizeXNew);
        float scaleY = (sizeY) / (sizeYNew);
System.err.println(" < " + scaleX + " " + scaleY + " > ");
        int x, y, xSrc;
        float floorX, ffx, ffy;

        BasicArray<T> dst = new BasicArray<T>(sizeXNew, sizeYNew);

        int ySrc = 0;
        float floorY = 0.0f;
        for (y = 0; y < sizeYNew - 1; y++) {
            ffy = floorY + scaleY - 1.0f;
            floorX = 0.0f;
            xSrc = 0;
            for (x = 0; x < sizeXNew - 1; x++) {
                ffx = floorX + scaleX - 1.0f;

                T val, val1;

                if (ffx > 0.0) {
                    val = get(xSrc, ySrc).operatorMultiply(1.0f - ffx).operatorPlus(get(xSrc + 1, ySrc).operatorMultiply(ffx));
                    if (ffy > 0.0) {
                        val1 = get(xSrc, ySrc + 1).operatorMultiply(1.0f - ffx)
                                .operatorPlus(get(xSrc + 1, ySrc + 1).operatorMultiply(ffx));
                        val = val.operatorPlusEqual(val1.operatorMinus(val).operatorMultiply(ffy));
                    }
                } else {
                    val = get(xSrc, ySrc);
                    if (ffy > 0.0)
                        val = val.operatorPlusEqual(get(xSrc, ySrc + 1).operatorMinus(val).operatorMultiply(ffy));
                }

                dst.set(x, y, val);
                floorX += scaleX;
                if (floorX >= 1.0) {
                    floorX -= 1.0;
                    xSrc++;
                }
            }
            floorY += scaleY;
            if (floorY >= 1.0) {
                floorY -= 1.0;
                ySrc++;
            }
        }

        return dst;
    }

    public BasicArray<T> splitLowFreq(int lenExp) {
        BasicArray<T> loArr = new BasicArray<T>((sizeX + (1 << lenExp)) >> lenExp, (sizeY + (1 << lenExp)) >> lenExp);

        for (int y = 0; y < loArr.sizeY(); y++) {
            for (int x = 0; x < loArr.sizeX(); x++) {
                loArr.set(x, y, loArr.newContent());
            }
        }

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                loArr.add(x >> lenExp, y >> lenExp, get(x, y));
            }
        }
        float fFakt = 1.0f / (1 << (2 * lenExp));
        for (int y = 0; y < loArr.sizeY(); y++) {
            for (int x = 0; x < loArr.sizeX(); x++) {
                loArr.set(x, y, loArr.get(x, y).operatorMultiply(fFakt));
            }
        }
        BasicArray<T> a3;
        BasicArray<T> a2 = loArr.smoothDouble();
        for (int a = 0; a < lenExp - 1; a++) {
            a3 = a2.smoothDouble();
            a2 = a3;
        }

        a3 = new BasicArray<T>(sizeX, sizeY);

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                T w = a2.get(x, y);
                a3.set(x, y, w);
                w = get(x, y).operatorMinus(w);
                set(x, y, w);
            }
        }

        return a3;
    }

    public BasicArray<T> smooth() {
        BasicArray<T> a4 = new BasicArray<T>(sizeX, sizeY);

        for (int y = 1; y < sizeY - 1; y++) {
            for (int x = 1; x < sizeX - 1; x++) {
                T pSmooth = get(x - 1, y - 1).operatorPlus(get(x, y - 1).operatorMultiply(2.0f)).operatorPlus(get(x + 1, y - 1));
                pSmooth = pSmooth.operatorPlusEqual(get(x - 1, y).operatorPlus(get(x, y).operatorMultiply(2.0f))
                        .operatorPlus(get(x + 1, y))).operatorMultiply(2.0f);
                pSmooth = pSmooth.operatorPlusEqual(get(x - 1, y + 1).operatorPlus(get(x, y + 1).operatorMultiply(2.0f))
                        .operatorPlus(get(x + 1, y + 1)));
                pSmooth = pSmooth.operatorMultiplyEqual(0.0625f);
                a4.set(x, y, pSmooth);
            }
        }
        return a4;
    }

    public void addArray(BasicArray<T> arr) {
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                add(x, y, arr.get(x, y));
            }
        }
    }

    public void subArray(BasicArray<T> arr) {
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                add(x, y, arr.get(x, y).operatorMinus());
            }
        }
    }

    public void mulArray(float f) {
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                mul(x, y, f);
            }
        }
    }

    public void sharpen(float f) {
        BasicArray<T> src = new BasicArray<T>(this);

        if (f == 0.0)
            return;

        for (int y = 1; y < sizeY - 1; y++) {
            for (int x = 1; x < sizeX - 1; x++) {
                T l = src.get(x, y - 1).operatorPlus(src.get(x - 1, y));
                l = l.operatorPlusEqual(src.get(x + 1, y).operatorPlus(src.get(x, y + 1)));
                l = l.operatorMultiplyEqual(2.0f);
                l = l.operatorPlusEqual(src.get(x - 1, y - 1).operatorPlus(src.get(x + 1, y - 1)));
                l = l.operatorPlusEqual(src.get(x - 1, y + 1).operatorPlus(src.get(x + 1, y + 1)));
                l = l.operatorMultiplyEqual(1.0f / 12.0f);
                l = l.operatorMinusEqual(src.get(x, y));
                l = src.get(x, y).operatorMinus(l.operatorMultiply(f));
                set(x, y, l);
            }
        }
    }

    public void smoothen() {
        BasicArray<T> src = new BasicArray<T>(this);

        for (int y = 1; y < sizeY - 1; y++) {
            for (int x = 1; x < sizeX - 1; x++) {
                T pSmooth = src.get(x - 1, y - 1)
                        .operatorPlus(src.get(x, y - 1).operatorMultiply(2.0f))
                        .operatorPlus(src.get(x + 1, y - 1));
                pSmooth = pSmooth.operatorPlusEqual(src.get(x - 1, y)
                        .operatorPlus(src.get(x, y).operatorMultiply(2.0f))
                        .operatorPlus(src.get(x + 1, y))).operatorMultiply(2.0f);
                pSmooth = pSmooth.operatorPlusEqual(src.get(x - 1, y + 1)
                        .operatorPlus(src.get(x, y + 1).operatorMultiply(2.0f))
                        .operatorPlus(src.get(x + 1, y + 1)));
                pSmooth = pSmooth.operatorMultiplyEqual(0.0625f);
                set(x, y, pSmooth);
            }
        }
    }

    public void clamp01() {
        int sizeX = sizeX(), sizeY = sizeY();
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                T p = get(x, y);
                p.clip();
                set(x, y, p);
            }
        }
    }

    public void reduceNoise(float reduceF) {
        int sizeX = sizeX(), sizeY = sizeY();

        if (reduceF <= 0.0)
            return;
        reduceF = 1.0f / reduceF;

        BasicArray<T> hiF = new BasicArray<T>(this);
        BasicArray<T> loF = hiF.splitLowFreq(1);

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                T p = hiF.get(x, y);
                float dd = p.norm1() * 5.0f * reduceF;
                if (dd < 1.0) {
                    float w = dd;
                    w *= (2.0 - w);
                    w *= (2.0 - w);
                    w *= (2.0 - w);

                    // create smooth ending
                    if (w < 0.2) {
                        w = w * 5.0f;
                        w = 0.1f * w * w + 0.1f;
                    }
                    w = 1.1f * (w - 1.0f) + 1.0f;

                    p = p.operatorMultiplyEqual(1.0f - w);
                    sub(x, y, p);
                }
            }
        }
    }

    public void hiSharpen(float f) {
        BasicArray<T> src = new BasicArray<T>(this);
        int sizeX = sizeX(), sizeY = sizeY();

        for (int y = 1; y < sizeY - 1; y++) {
            for (int x = 1; x < sizeX - 1; x++) {
                float dd = 0.0f;
                T c = src.get(x, y);
                T l = src.get(x, y - 1).operatorMinus(c);
                dd += l.norm1();
                l = src.get(x - 1, y).operatorMinus(c);
                dd += l.norm1();
                l = src.get(x + 1, y).operatorMinus(c);
                dd += l.norm1();
                l = src.get(x, y + 1).operatorMinus(c);
                dd += l.norm1();
                dd *= 2.0;
                l = src.get(x - 1, y - 1).operatorMinus(c);
                dd += l.norm1();
                l = src.get(x + 1, y - 1).operatorMinus(c);
                dd += l.norm1();
                l = src.get(x - 1, y + 1).operatorMinus(c);
                dd += l.norm1();
                l = src.get(x + 1, y + 1).operatorMinus(c);
                dd += l.norm1();
                dd *= (1.0 / 12.0);

                dd = 10.0f * f * dd;
                if (dd > 1.0)
                    dd = 1.0f;
                dd = (float) Math.pow(dd, 2.0);
                //dd = dd*dd*(3.0 - 2.0*dd);

                float ff = c.norm1();
                ff = 1.0f / (10.0f * ff + 0.001f) - 1.0f / 3.0001f;
                mul(x, y, 1.0f + 0.2f * dd * ff);
            }
        }
    }

    private T smoothFunc(T p0, T p1, T p2) {
        return p0.operatorMultiply(5.0f).operatorPlus(p1.operatorMultiply(10.0f)).operatorPlus(p2).operatorMultiply(0.0625f);
    }

    private void readLineSmoothDouble(int y, T[] line) {
        int src = y * sizeX;
        T p0 = buf[src];
        T p1 = buf[src];
        T p2 = buf[src + 1];
        int lineP = 0;
        line[lineP++] = smoothFunc(p0, p1, p2);
        line[lineP++] = smoothFunc(p2, p1, p0);
        for (int x = 1; x < sizeX - 1; x++) {
            p0 = buf[src];
            p1 = buf[src + 1];
            p2 = buf[src + 2];
            line[lineP++] = smoothFunc(p0, p1, p2);
            line[lineP++] = smoothFunc(p2, p1, p0);
            src++;
        }
        p0 = buf[src];
        p1 = buf[src + 1];
        p2 = buf[src + 1];
        line[lineP++] = smoothFunc(p0, p1, p2);
        line[lineP++] = smoothFunc(p2, p1, p0);
    }

    private void readLineSmoothDoubleTorus(int y, T[] line) {
        int pFirst = y * sizeX; // buf
        int pLast = pFirst + sizeX - 1;

        int src = pFirst;
        T p0 = buf[pLast];
        T p1 = buf[src];
        T p2 = buf[src + 1];
        int lineP = 0;
        line[lineP++] = smoothFunc(p0, p1, p2);
        line[lineP++] = smoothFunc(p2, p1, p0);
        for (int x = 1; x < sizeX - 1; x++) {
            p0 = buf[src];
            p1 = buf[src + 1];
            p2 = buf[src + 2];
            line[lineP++] = smoothFunc(p0, p1, p2);
            line[lineP++] = smoothFunc(p2, p1, p0);
            src++;
        }
        p0 = buf[src];
        p1 = buf[src + 1];
        p2 = buf[pFirst];
        line[lineP++] = smoothFunc(p0, p1, p2);
        line[lineP++] = smoothFunc(p2, p1, p0);
    }

    private void shrinkLine(int y, float scaleF, int sizeXNew, T[] line) {
        int xBig;
        float floorX = 0.0f; // left border of current smallPixel

        for (xBig = 0; xBig < sizeXNew; xBig++)
            line[xBig] = newContent();

        xBig = 0;
        floorX = 0.0f;
        for (int x = 0; x < sizeX - 1; x++) {
            float ff = floorX + scaleF - 1.0f;
            if (ff > 0) { // stepping into new bigPixel reached
                line[xBig] = line[xBig].operatorPlusEqual(get(x, y).operatorMultiply(scaleF - ff));
                ++xBig;
                floorX -= 1.0;
                line[xBig] = line[xBig].operatorPlusEqual(get(x, y).operatorMultiply(ff));
            } else
                line[xBig] = line[xBig].operatorPlusEqual(get(x, y).operatorMultiply(scaleF));
            floorX += scaleF;
        }
        line[sizeXNew - 1] = line[sizeXNew - 1].operatorPlusEqual(get(sizeX - 1, y).operatorMultiply(scaleF));
    }
}
