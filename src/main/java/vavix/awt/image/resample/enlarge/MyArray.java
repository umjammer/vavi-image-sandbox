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
 * 2-dim. float-array and direction-array
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @author <a href="mailto:sano-n@klab.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/14 nsano initial version <br>
 */
class MyArray extends BasicArray<PFloat> {

    public MyArray() {
    }

    public MyArray(int sx, int sy) {
        super(sx, sy);
    }

    public MyArray(MyArray aSrc) {
        super(aSrc);
    }

    public float getF(int x, int y) {
        return get(x, y).toF();
    }

    public void Set(int x, int y, PFloat p) {
        set(x, y, p);
    }

    public void Add(int x, int y, PFloat p) {
        add(x, y, p);
    }

    public void Sub(int x, int y, PFloat p) {
        sub(x, y, p);
    }

    public void Set(int x, int y, float p) {
        set(x, y, new PFloat(p));
    }

    public void Add(int x, int y, float p) {
        add(x, y, new PFloat(p));
    }

    public void Sub(int x, int y, float p) {
        sub(x, y, new PFloat(p));
    }

    public MyArray smoothDouble() {
        return smoothDouble();
    }

    public MyArray shrinkHalf() {
        return shrinkHalf();
    }

    public MyArray shrink(int sizeXNew, int sizeYNew) {
        return shrink(sizeXNew, sizeYNew);
    }

    public MyArray splitLowFreq(int lenExp) {
        return splitLowFreq(lenExp);
    }

    public MyArray smooth() {
        return smooth();
    }

    public DirArray Gradient() {
        int x, y;
        int sizeX = sizeX(), sizeY = sizeY();

        float p00, p10, p20;
        float p01, p11, p21;
        float p02, p12, p22;
        DirArray gradArray = new DirArray(sizeX, sizeY);
        float dx, dy;

        for (y = 1; y < sizeY - 1; y++) {
            for (x = 1; x < sizeX - 1; x++) {
                p00 = get(x - 1, y - 1).toF();
                p10 = get(x, y - 1).toF();
                p20 = get(x + 1, y - 1).toF();
                p01 = get(x - 1, y).toF();
                p11 = get(x, y).toF();
                p21 = get(x + 1, y).toF();
                p02 = get(x - 1, y + 1).toF();
                p12 = get(x, y + 1).toF();
                p22 = get(x + 1, y + 1).toF();
                dx = p20 - p10 + 2.0f * (p21 - p11) + p22 - p12;
                dy = p02 - p01 + 2.0f * (p12 - p11) + p22 - p21;
                dx *= 0.25;
                dy *= 0.25;
                gradArray.set(x, y, new Point2(dx, dy));
            }
        }

        return gradArray;
    }

    public void FillWithHill() {
        int x, y;
        int sizeX = sizeX(), sizeY = sizeY();
        float scaleF = 2.0f / (sizeX);

        for (y = 0; y < sizeY; y++) {
            float yf = (y - (sizeY >> 1)) * scaleF;
            for (x = 0; x < sizeX; x++) {
                float xf = (x - (sizeX >> 1)) * scaleF;
                float w = 2.0f * (xf * xf + yf * yf);
                if (w > 1.0)
                    w = 2.0f - w;
                if (w < 0.0)
                    w = 0.0f;
                set(x, y, new PFloat(w));
            }
        }
    }

    public void FillWithDots() {
        int x, y;
        int sizeX = sizeX(), sizeY = sizeY();
//        float scaleF = 2.0f/(sizeX);

        for (y = 0; y < sizeY; y++) {
            for (x = 0; x < sizeX; x++) {
                if ((x & 3) != 0 || (y & 3) != 0)
                    set(x, y, new PFloat(0.0f));
                else
                    set(x, y, new PFloat(1.0f));
            }
        }
    }

    public void OperateDir(DirArray dirArr, RandGen rGen) {
        int x, y, a;
        int sizeX = sizeX(), sizeY = sizeY();
        final float kTab[] = {
            1.0f, 4.0f, 6.0f, 4.0f, 1.0f
        };
        float[] ker = new float[25];
        float w, dw;
        Point2 dir;
        MyArray newArr = new MyArray(sizeX, sizeY);

        for (y = 2; y < sizeY - 2; y++) {
            for (x = 2; x < sizeX - 2; x++) {
                int kx, ky;
                float totalWeight = 0.0f, normF, val;
                a = 0;
                dir = dirArr.get(x, y);
                float phi = (float) (dir.angle() * 0.5 * (2.0 * Math.PI / 360.0));
                float cc = (float) Math.cos(phi), ss = (float) Math.sin(phi);
                for (ky = 0; ky < 5; ky++) {
                    for (kx = 0; kx < 5; kx++) {
                        w = kTab[kx] * kTab[ky] * (1.0f / 256.0f);
                        dw = (kx - 2) * cc + (ky - 2) * ss;
                        dw = 1.0f * dw * dw;
                        dw = 1.0f - dw;
                        if (dw < 0.0)
                            dw = 0.0f;
                        w *= dw;
                        ker[a++] = w;
                        totalWeight += w;
                    }
                }
                if (totalWeight > 0.0)
                    normF = 1.0f / totalWeight;
                else
                    normF = 0.0f;
                for (a = 0; a < 5 * 5; a++)
                    ker[a] *= normF;

                val = 0.0f;
                for (ky = 0; ky < 5; ky++) {
                    for (kx = 0; kx < 5; kx++) {
                        val += ker[ky * 5 + kx] * getF(x + kx - 2, y + ky - 2);
                    }
                }
                float centerV = getF(x, y);
                val = centerV - 0.5f * (val - centerV);
                val += 0.0001 * (rGen.randF() - 0.5);
                if (val < 0.0)
                    val = 0.0f;
                else if (val > 1.0)
                    val = 1.0f;

                newArr.set(x, y, new PFloat(val));
            }
        }

        for (y = 2; y < sizeY - 2; y++) {
            for (x = 2; x < sizeX - 2; x++) {
                set(x, y, newArr.get(x, y));
            }
        }
    }
}
