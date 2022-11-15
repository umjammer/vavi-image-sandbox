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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


/**
 * an array for fractal deformation of the enlarge-kernels
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/14 nsano initial version <br>
 */
class FractTab {

    private static final int FRACTTABEXP = 9;
    @SuppressWarnings("unused")
    private static final int FRACTTABLEN = (1 << FRACTTABEXP);
    private static final int FRACTTABMASK = (1 << FRACTTABEXP) - 1;
    private static final int RandTabExp = 12;
    private static final int RandTabLen = (1 << RandTabExp);
    private static final int RandTabMask = (1 << RandTabExp) - 1;

    private MyArray fTab;
    private RandGen randG;
    private int[] randTab;
    private float scaleF, invScaleF;

    public FractTab(float sF) {
        scaleF = sF;
        invScaleF = 1.0f / sF;

        randTab = new int[RandTabLen];
        createRandTab();
        createTab();
    }

    // get random kernel center ( for rand seeds in centerX, centerY )
    public void getKernelCenter(int[] centerX, int[] centerY, float[] centerV, int centerVP) {
        randPerm(centerX, centerY);
        centerX[centerVP] &= FRACTTABMASK;
        centerY[centerVP] &= FRACTTABMASK;
        centerV[centerVP] = fTab.get(centerX[centerVP], centerY[centerVP]).toF();
    }

    public void createTab() {
        final long startExp = 2;
        MyArray a0;

        int aLen = (1 << startExp);
        float rFakt;
        float fRatio = scaleF / (1 << (FRACTTABEXP - startExp));

        a0 = new MyArray(aLen, aLen);
        for (int s = 0; s < FRACTTABEXP - startExp; s++) {
            MyArray hh;
            hh = a0.smoothDoubleTorus();
            a0 = hh;
            fRatio *= 2.0f;
            rFakt = 2.0f * fRatio;
            if (rFakt > 1.0)
                rFakt = 1.0f;
            else
                rFakt = rFakt * rFakt * (3.0f - 2.0f * rFakt); // S-Func()
            rFakt *= 1.5 * Math.pow(2, FRACTTABEXP - s - startExp - 8); // ldexp
            addRand(a0, rFakt);
        }

        fTab = a0;
        fTab.mulArray(1000.0f * invScaleF);
    }

    public float getT(int x, int y) {
        x &= FRACTTABMASK;
        y &= FRACTTABMASK;
        return fTab.get(x, y).toF();
    }

    private void addRand(BasicArray<PFloat> a0, float rFakt) {
        for (int y = 0; y < a0.sizeY(); y++) {
            for (int x = 0; x < a0.sizeX(); x++) {
                a0.add(x, y, new PFloat((randG.randF() - 0.5f) * rFakt));
            }
        }
    }

    @SuppressWarnings("unused")
    private void saveTab() throws IOException {
        int x, y;
        int sizeX = fTab.sizeX(), sizeY = fTab.sizeY();
        BufferedImage image = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_4BYTE_ABGR);

        for (y = 0; y < sizeY; y++) {
            for (x = 0; x < sizeX; x++) {
                float vv, rr, gg, bb;
                vv = 0.1f * fTab.get(x, y).toF();
                if (vv < 0)
                    vv = -vv;
                rr = vv * 8.0f;
                if (rr > 1.0)
                    rr = 1.0f;
                rr = 255.0f * rr * (2.0f - rr);
                gg = vv * 2.0f;
                if (gg > 1.0)
                    gg = 1.0f;
                gg = 255.0f * gg * gg * (3.0f - 2.0f * gg);
                bb = vv;
                if (bb > 1.0)
                    bb = 1.0f;
                bb *= 255.0 * bb;

                int[] c = new int[] { (int) (rr), (int) (gg), (int) (bb), 255 };
                image.getRaster().setPixel(x, y, c);
            }
        }

        ImageIO.write(image, "PNG", new File("/Users/mischa/Desktop/tsttab.png"));
    }

    private void createRandTab() {
        for (int a = 0; a < RandTabLen; a++) {
            randTab[a] = a;
        }
        for (int c = 0; c < 4 * RandTabLen; c++) {
            int rr = 3615232;
            int s1, s2, hh;
            rr += (randG.randL() >> 5);
            s1 = rr & RandTabMask;
            rr += (randG.randL() >> 5);
            s2 = rr & RandTabMask;

            hh = randTab[s1];
            randTab[s1] = randTab[s2];
            randTab[s2] = hh;
        }
    }

    private int rand(int r) {
        return randTab[r & RandTabMask];
    }

    private void randPerm(int[] r1, int[] r2) { // (r1,r2) -> (rand1(r1,r1),rand2(r1,r2))
        r1[0] = (r1[0] >> 8) + rand(r1[0]);
        r2[0] = (r2[0] >> 8) + rand(r2[0]);
        r1[0] = rand(r1[0] ^ r2[0]);
        r2[0] = rand(r1[0] + r2[0]);
    }
}
