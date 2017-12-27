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
 * DirArray. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/14 nsano initial version <br>
 */
class DirArray extends BasicArray<Point2> {

    Factory<Point2> factory_ = new Factory<Point2>() {
        @Override
        public Point2 newInstance() {
            return new Point2();
        }
    };

    public DirArray() {
        setFactory(factory_);
    }

    public DirArray(int sx, int sy) {
        super(sx, sy);
        setFactory(factory_);
    }

    public DirArray smoothDouble() {
        return smoothDouble();
    }

    public DirArray shrinkHalf() {
        return shrinkHalf();
    }

    public DirArray shrink(int sizeXNew, int sizeYNew) {
        return shrink(sizeXNew, sizeYNew);
    }

    public DirArray splitLowFreq(int lenExp) {
        return splitLowFreq(lenExp);
    }

    public DirArray smooth() {
        return smooth();
    }

    public DirArray func0() {
        int x, y;
        int sizeX = sizeX(), sizeY = sizeY();

        DirArray newArr = new DirArray(sizeX, sizeY);
        for (y = 1; y < sizeY - 1; y++) {
            for (x = 1; x < sizeX - 1; x++) {
                Point2 d;
                Point2 d00, d10, d20;
                Point2 d01, d11, d21;
                Point2 d02, d12, d22;
                Point2 n00, n10, n20;
                Point2 n01, n11, n21;
                Point2 n02, n12, n22;
                float f00, f10, f20;
                @SuppressWarnings("unused")
                float f01, f11, f21;
                float f02, f12, f22;
                d00 = get(x - 1, y - 1);
                d10 = get(x, y - 1);
                d20 = get(x + 1, y - 1);
                d01 = get(x - 1, y);
                d11 = get(x, y);
                d21 = get(x + 1, y);
                d02 = get(x - 1, y + 1);
                d12 = get(x, y + 1);
                d22 = get(x + 1, y + 1);
                n00 = d00.normalized();
                n10 = d10.normalized();
                n20 = d20.normalized();
                n01 = d01.normalized();
                n11 = d11.normalized();
                n21 = d21.normalized();
                n02 = d02.normalized();
                n12 = d12.normalized();
                n22 = d22.normalized();
                f00 = nFunc0(n00, n11);
                f10 = nFunc0(n10, n11);
                f20 = nFunc0(n20, n11);
                f01 = nFunc0(n01, n11);
                f11 = nFunc0(n11, n11);
                f21 = nFunc0(n21, n11);
                f02 = nFunc0(n02, n11);
                f12 = nFunc0(n12, n11);
                f22 = nFunc0(n22, n11);
                d = d00.operatorMultiply(f00)
                        .operatorPlus(d10.operatorMultiply(2.0f * f10))
                        .operatorPlus(d20.operatorMultiply(f20));
                d = d.operatorPlusEqual(d01.operatorMultiply(f01)
                        .operatorPlus(d21.operatorMultiply(f21))
                        .operatorMultiply(2.0f));
                d = d.operatorPlusEqual(d02.operatorMultiply(f02)
                        .operatorPlus(d12.operatorMultiply(2.0f * f12))
                        .operatorPlus(d22.operatorMultiply(f22)));
                newArr.set(x, y, d11.operatorPlus(d.operatorMultiply(0.02f)));
            }
        }
        return newArr;
    }

    public DirArray func() {
        int x, y;
        int sizeX = sizeX(), sizeY = sizeY();

        DirArray newArr = new DirArray(sizeX, sizeY);
        for (y = 1; y < sizeY - 1; y++) {
            for (x = 1; x < sizeX - 1; x++) {
                Point2 d;
                Point2 d00, d10, d20;
                Point2 d01, d11, d21;
                Point2 d02, d12, d22;
                Point2 n00, n10, n20;
                Point2 n01, n11, n21;
                Point2 n02, n12, n22;

                d00 = get(x - 1, y - 1);
                d10 = get(x, y - 1);
                d20 = get(x + 1, y - 1);
                d01 = get(x - 1, y);
                d11 = get(x, y);
                d21 = get(x + 1, y);
                d02 = get(x - 1, y + 1);
                d12 = get(x, y + 1);
                d22 = get(x + 1, y + 1);
                n00 = d00.normalized();
                n10 = d10.normalized();
                n20 = d20.normalized();
                n01 = d01.normalized();
                n11 = d11.normalized();
                n21 = d21.normalized();
                n02 = d02.normalized();
                n12 = d12.normalized();
                n22 = d22.normalized();

                d = d00.operatorMultiply(nFunc(n00, n11))
                        .operatorPlus(d10.operatorMultiply(2.0f * nFunc(n10, n11)))
                        .operatorPlus(d20.operatorMultiply(nFunc(n20, n11)));
                d = d.operatorPlusEqual((d01.operatorMultiply(nFunc(n01, n11)).operatorPlus(d21.operatorMultiply(nFunc(n21, n11)))).operatorMultiply(2.0f));
                d = d.operatorPlusEqual(d02.operatorMultiply(nFunc(n02, n11))
                        .operatorPlus(d12.operatorMultiply(2.0f * nFunc(n12, n11)))
                        .operatorPlus(d22.operatorMultiply(nFunc(n22, n11))));
                d = d.operatorMultiplyEqual(0.05f);
                newArr.set(x, y, d.operatorPlus(d11));
            }
        }
        return newArr;
    }

    public void normalize() {
        int x, y;
        int sizeX = sizeX(), sizeY = sizeY();

        for (y = 0; y < sizeY; y++) {
            for (x = 0; x < sizeX; x++) {
                Point2 d;
                d = get(x, y);
                d.normalize();
                set(x, y, d);
            }
        }
    }

    public void gradToDir() {
        int x, y;
        int sizeX = sizeX(), sizeY = sizeY();

        for (y = 0; y < sizeY; y++) {
            for (x = 0; x < sizeX; x++) {
                float hx;
                Point2 d;
                d = get(x, y);
                hx = d.x;
                d.x = d.y;
                d.y = -hx;
                set(x, y, d.doubleDir());
            }
        }
    }

    public void smoothenWithDir() {
        int x, y, ax, ay, a;
        int sizeX = sizeX(), sizeY = sizeY();

        DirArray dir = new DirArray();

        for (y = 1; y < sizeY - 1; y++) {
            for (x = 1; x < sizeX - 1; x++) {
                float[] w = new float[9];
                float ww, wTotal;

                a = 0;
                wTotal = 0.0f;
                Point2 dM;// = dir.Get(x,y);
                for (ay = 0; ay < 3; ay++) {
                    for (ax = 0; ax < 3; ax++) {
                        dM = new Point2((ax - 1), (ay - 1));
                        dM = dM.doubleDir();
                        ww = 1.0f + dM.operatorMultiply(20.0f).operatorMultiply(dir.get(x - 1 + ax, y - 1 + ay));
                        if (ww < 0.000001)
                            ww = 0.000001f;
                        if (ax == 1)
                            ww *= 2.0;
                        if (ay == 1)
                            ww *= 2.0;
                        w[a] = ww;
                        wTotal += ww;
                        a++;
                    }
                }
                ww = 1.0f / wTotal;
                for (a = 0; a < 9; a++)
                    w[a] *= ww;

                a = 0;
                Point2 pSmooth = new Point2(0.0f);
                for (ay = 0; ay < 3; ay++) {
                    for (ax = 0; ax < 3; ax++) {
                        pSmooth = pSmooth.operatorPlusEqual(dir.get(x - 1 + ax, y - 1 + ay).operatorMultiply(w[a]));
                        a++;
                    }
                }
                set(x, y, pSmooth);
            }
        }
    }

    private float nFunc(Point2 n1, Point2 n2) {
        float w;
        w = 1.0f - (n1.operatorMinus(n2)).norm() * 10.0f;
        if (w < 0.0)
            w = 0.0f;
        //w=w*w*(3.0 - 2.0*w);
        w *= w;
        w *= w;
        return w;
    }

    private float nFunc0(Point2 n1, Point2 n2) {
        float w = n1.operatorMultiply(n2);
        w = 0.5f + 0.5f * w;
        w = 6.0f * w - 5.0f;
        if (w < 0.0)
            w = 0.0f;
        w = w * w;
        w = w * w;
        return w;
    }
}
/* */
