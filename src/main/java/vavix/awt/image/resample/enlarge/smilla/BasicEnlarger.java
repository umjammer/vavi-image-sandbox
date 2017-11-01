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


// BasicEnlarger contains the algorithm, applied on srcBlock and dstBlock
// a derived real enlarger has to implement
//      void ReadCurrentBlock(int dstXEdge,int dstYEdge);
//      for reading a block from the source
// and
//      void WriteDstBlock();
//      for writing the enlarged block to the dest.
// Thus BasicEnlarger is independent from format, data type of source, destination
// It needs only the size and scaleFactor,
// and clipping, parameters
// There is no global enlarge-method, this has to be written in the derived enlarger-classes,
// using EnlargeBlock

//the different formats used:
//
// - the complete source image
//      sizeX, sizeY, format.srcWidth, format.srcHeight
//
// - the complete source enlarged by scaleX,scaleY
//      sizeXDst, sizeYDst, format.DstWidth(), format.DstHeight()
//
// - the clipping rectangle in the format structure
//      the clipping rect in the format structure uses the < DstWidth(), DstHeight() >
//      coordinate system
//      but it allows values outside [ 0, DstWidth()-1 ] x [ 0, DstHeight()-1 ]
// - the format.cliprect is changed in the BasicEnlarger structure to a new clipping rectangle
//      within the allowed borders of the full dstRect
//      additionally an offset is calculated to place the cliprect within the output
// - the output image
//      outputWidth, outputHeight
//      offsetX, offsetY are the coordinates of the changed cliprect within the output
//      might lead to black borders outside the cliprect

public abstract class BasicEnlarger<T extends Primitive<T>> {

    // need values<0,>size in smallToBigTabs
    private static final long smallToBigMargin = Constants.blockLen + 40;

    // srcBlock contains additional margins of borderPixels
    private BasicArray<T> srcBlock, dstBlock;

    private float scaleFaktX, invScaleFaktX;

    private float scaleFaktY, invScaleFaktY;

    private int sizeX, sizeY;

    private int sizeXDst, sizeYDst;

    // size of resulting image
    private int outputWidth, outputHeight;

    // pos of cliprect within image ( generate black margins )
    private int offsetX, offsetY;

    // clip-rect
    private int clipX0, clipX1, clipY0, clipY1;

    // if scaleF < 1.0: don't allocate blocks&tabs, only call ShrinkClip()
    private boolean onlyShrinking;

    //
    // Parameters
    //
    private float sharpExp;

    private float centerWeightF;

    private float centerWExp;

    private float derivF;

    private float selectPeakExp;

    private float derivDiffF;

    private float linePosF;

    private float lineNegF;

    private float ditherF;

    @SuppressWarnings("unused")
    private float dirDiffF;

    private float preSharpenF;

    private float deNoiseF;

    private float fractNoiseF;

    //
    // Helper-Objects
    //

    // Enlarging is done blockwise
    private int sizeSrcBlockX, sizeSrcBlockY;

    private int sizeDstBlock;

    private int dstBlockEdgeX, dstBlockEdgeY; // smallPos of upper left edge of DstBlock

    private int srcBlockEdgeX, srcBlockEdgeY; // bigPos of upper left edge of   SrcBlock

    private int dstMinBX, dstMinBY;

    private int dstMaxBX, dstMaxBY; // clipped part of the current block

    private RandGen randGen;

    private FractTab fractTab; // used for deforming kernels

    private float[] selectDiffTab;

    private float[] centerWeightTab; // weight multiplied with factor increasing near center of bigPixel

    private float[] invTab; // table for x . 1/x ( for inner loop )

    private int[] smallToBigTabX; // tables for mapping small/dst-pixels to big/src-pixels

    private int[] smallToBigTabY; //

    // Before Enlarging, calculate the importance of each BigPixel in Block
    //
    // similWeights: big weight, if similar to neighbours
    // border pixels are less important
    //
    // indieWeights: important are also pixels, which are totally
    // independent from most neighbours, e.g single specs or thin lines in front of
    // background of different color
    private MyArray baseWeights;

    private MyArray workMask; // decides, if anything is to be done within bigPixel (smoothed . multiply)

    private MyArray workMaskDst; // workMaskDst: smooth-enlarged

    @SuppressWarnings("unused")
    private MyArray baseParams;

    private BasicArray<T> dX, dY, d2X, d2Y, dXY, d2L; // get modified Gradient, 2nd Deriv, Laplace

    private MyArray baseIntensity;

    // for each smallPixelPos calc. kernels for smooth-enlarging
    // and for selecting of neigh. BigPixels
    private float[][] enlargeKernelX;

    private float[][] enlargeKernelY;

    private float[][] selectKernelX; // 5-Kernel , multiply kx*ky

    private float[][] selectKernelY;

    // Helper-Matrices containing values and weights of the current 5x5 BigPixels
    @SuppressWarnings("unchecked")
    private T[] bigPixelColor = (T[]) new Primitive[5 * 5];

    private float[] bigPixelWeight = new float[5 * 5];

    private float[] bigPixelIntensity = new float[5 * 5];

    // increased weight near pixel-center dep. of d2L
    private float[] bigPixelCenterW = new float[5 * 5];

    @SuppressWarnings("unchecked")
    private T[] bigPixelDX = (T[]) new Primitive[5 * 5];

    @SuppressWarnings("unchecked")
    private T[] bigPixelDY = (T[]) new Primitive[5 * 5];

    @SuppressWarnings("unchecked")
    private T[] bigPixelD2X = (T[]) new Primitive[5 * 5];

    @SuppressWarnings("unchecked")
    private T[] bigPixelD2Y = (T[]) new Primitive[5 * 5];

    @SuppressWarnings("unchecked")
    private T[] bigPixelDXY = (T[]) new Primitive[5 * 5];

    @SuppressWarnings("unchecked")
    private T[] bigPixelD2L = (T[]) new Primitive[5 * 5];

    // for FractNoise: random kernel center pos & center val for fract deform kernels
    private int[] bigPixelFractCX = new int[5 * 5];

    private int[] bigPixelFractCY = new int[5 * 5];

    private float[] bigPixelFractCVal = new float[5 * 5];

    public BasicEnlarger(final EnlargeFormat format, final EnlargeParameter param) {
        // int srcSizeX, int srcSizeY, float scaleF) {
        sizeX = format.srcWidth;
        sizeY = format.srcHeight;

        // for pixel-accuracy use slightly different scaleF in x,y
        sizeXDst = format.dstWidth();
        sizeYDst = format.dstHeight();
        scaleFaktX = (float) sizeXDst / (float) sizeX;
        scaleFaktY = (float) sizeYDst / (float) sizeY;
        // scaleFaktX = format.scaleX; // (float) sizeXDst / (float) sizeX;
        // scaleFaktY = format.scaleY; // (float) sizeYDst / (float) sizeY;
        invScaleFaktX = 1.0f / scaleFaktX;
        invScaleFaktY = 1.0f / scaleFaktY;

        // the 'clipping rectangle' might reach outside the dst-boundaries
        // this leads to black margins in the output
        // for calculation, a new clipping rect inside the boundaries is calculated
        // additionally, outputWidth, outputHeight give the dimensions of the result,
        // offsetX, offsetY give the topleft edge of the new cliprect within the output

        outputWidth = format.clipW();
        outputHeight = format.clipH();
        calculateClipAndOffset(format);
        onlyShrinking = (scaleFaktX < 1.0 && scaleFaktY < 1.0);

        randGen = new RandGen(635017, 934021);
        smallToBigTabX = new int[(int) (sizeXDst + 2 * smallToBigMargin)];
        smallToBigTabY = new int[(int) (sizeYDst + 2 * smallToBigMargin)];
        for (int a = 0; a < sizeXDst + 2 * smallToBigMargin; a++) {
            smallToBigTabX[a] = (int) ((a - smallToBigMargin) * invScaleFaktX);
        }
        for (int a = 0; a < sizeYDst + 2 * smallToBigMargin; a++) {
            smallToBigTabY[a] = (int) ((a - smallToBigMargin) * invScaleFaktY);
        }

        if (onlyShrinking())
            return;

        sizeDstBlock = Constants.blockLen;
        sizeSrcBlockX = (int) (invScaleFaktX * (sizeDstBlock) + 0.5) + 2 * Constants.srcBlockMargin;
        sizeSrcBlockY = (int) (invScaleFaktY * (sizeDstBlock) + 0.5) + 2 * Constants.srcBlockMargin;
        srcBlock = new BasicArray<>(sizeSrcBlockX, sizeSrcBlockY);
        dstBlock = new BasicArray<>(sizeDstBlock, sizeDstBlock);

        baseWeights = new MyArray(sizeSrcBlockX, sizeSrcBlockY);
        workMask = new MyArray(sizeSrcBlockX, sizeSrcBlockY);
        baseParams = new MyArray(sizeSrcBlockX, sizeSrcBlockY);
        baseIntensity = new MyArray(sizeSrcBlockX, sizeSrcBlockY);
        workMaskDst = new MyArray(sizeDstBlock, sizeDstBlock);

        dX = new BasicArray<>(sizeSrcBlockX, sizeSrcBlockY);
        dY = new BasicArray<>(sizeSrcBlockX, sizeSrcBlockY);
        d2X = new BasicArray<>(sizeSrcBlockX, sizeSrcBlockY);
        d2Y = new BasicArray<>(sizeSrcBlockX, sizeSrcBlockY);
        dXY = new BasicArray<>(sizeSrcBlockX, sizeSrcBlockY);
        d2L = new BasicArray<>(sizeSrcBlockX, sizeSrcBlockY);

        selectDiffTab = new float[Constants.diffTabLen];
        centerWeightTab = new float[Constants.diffTabLen];
        invTab = new float[Constants.invTabLen];
        invTab[0] = 1000000.0f;
        for (int a = 1; a < Constants.invTabLen; a++)
            invTab[a] = (float) (Constants.invTabLen - 1) / (float) (a);

        // use different kernels in x,y-dir, because diff. scaleF in x,y
        // for sake of pixel-accuracy allow slight difference in aspect ratio
        // and thus change scaleF into slightly diff. scaleFX,scaleFY

        enlargeKernelX = new float[sizeXDst][];
        enlargeKernelY = new float[sizeYDst][];
        selectKernelX = new float[sizeXDst][];
        selectKernelY = new float[sizeYDst][];
        for (int a = 0; a < sizeXDst; a++) {
            enlargeKernelX[a] = new float[5];
            selectKernelX[a] = new float[5];
        }
        for (int a = 0; a < sizeYDst; a++) {
            enlargeKernelY[a] = new float[5];
            selectKernelY[a] = new float[5];
        }

        derivF = 0.0f;
        sharpExp = 0.01f;
        centerWeightF = 3.0f;
        centerWExp = 3.5f;
        selectPeakExp = 3.0f;
        derivDiffF = 0.7f;

        lineNegF = 1.0f;
        linePosF = 0.15f;
        ditherF = 0.1f;
        dirDiffF = 1.0f;
        deNoiseF = 0.0f;
        preSharpenF = 0.0f;
        fractNoiseF = 0.0f;

        setParameter(param);
        createKernels();
        createDiffTabs();

        fractTab = null; // fractTab has to be imported with SetFractTab
    }

    // test this in own enlarger before calling any enlarger-methods!
    // (blocks etc. are only created if not shrinking)
    public boolean onlyShrinking() {
        return onlyShrinking;
    }

    public void enlarge() {
        int dstX, dstY;

        if (onlyShrinking()) {
            shrinkClip();
            return;
        }

        for (dstY = clipY0(); dstY < clipY1(); dstY += Constants.blockLen) {
            for (dstX = clipX0(); dstX < clipX1(); dstX += Constants.blockLen) {
                blockBegin(dstX, dstY);
                readSrcBlock();
                srcBlockReduceNoise();
                srcBlockSharpen();
                enlargeBlock();
                writeDstBlock();
            }
        }
    }

    // example enlarge
    public void setParameter(final EnlargeParameter p) {
        setParameter(p.sharp, p.flat);
        setDeNoise(p.deNoise);
        setPreSharpen(p.preSharp);
        setDither(p.dither);
        setFractNoise(p.fractNoise);
    }

    static final int listLen = 7;

    static final float[] sE_list0 = {
        16.0f, 6.0f, 4.0f, 2.0f, 1.0f, 0.5f, 0.1f
    };

    static final float[] cWF_list0 = {
        8.0f, 7.5f, 7.0f, 5.0f, 3.0f, 2.0f, 1.0f
    };

    static final float[] cWE_list0 = {
        6.0f, 5.5f, 5.0f, 4.5f, 4.0f, 3.0f, 2.0f
    };

    static final float[] sPE_list0 = {
        4.0f, 4.0f, 3.5f, 2.5f, 1.5f, 1.2f, 1.0f
    };

    static final float[] sE_list1 = {
        12.0f, 5.0f, 2.5f, 1.0f, 0.5f, 0.1f, 0.01f
    };

    static final float[] cWF_list1 = {
        8.0f, 7.0f, 5.0f, 3.0f, 2.0f, 1.0f, 1.0f
    };

    static final float[] cWE_list1 = {
        6.0f, 5.0f, 4.5f, 4.0f, 3.0f, 2.0f, 2.0f
    };

    static final float[] sPE_list1 = {
        4.0f, 3.5f, 2.5f, 1.5f, 1.2f, 1.0f, 1.0f
    };

    public void setParameter(float sharpness, float flatness) {

        if (onlyShrinking())
            return;

        if (sharpness < 0.0)
            sharpness = 0.0f;
        else if (sharpness > 1.0)
            sharpness = 1.0f;

        if (flatness < 0.0)
            flatness = 0.0f;
        else if (flatness > 1.0)
            flatness = 1.0f;

        float t = (1.0f - sharpness) * (listLen - 1);
//System.err.println(t);
        int idx = (int) t;
        t -= idx;

        float hh;

        derivF = 1.0f - flatness;
        hh = sE_list0[idx] * (1.0f - t) + sE_list0[idx + 1] * t;
        sharpExp = sE_list1[idx] * (1.0f - t) + sE_list1[idx + 1] * t;
        sharpExp = sharpExp * flatness + hh * (1.0f - flatness);

        hh = cWF_list0[idx] * (1.0f - t) + cWF_list0[idx + 1] * t;
        centerWeightF = cWF_list1[idx] * (1.0f - t) + cWF_list1[idx + 1] * t;
        centerWeightF = centerWeightF * flatness + hh * (1.0f - flatness);

        hh = cWE_list0[idx] * (1.0f - t) + cWE_list0[idx + 1] * t;
        centerWExp = cWE_list1[idx] * (1.0f - t) + cWE_list1[idx + 1] * t;
        centerWExp = centerWExp * flatness + hh * (1.0f - flatness);

        hh = sPE_list0[idx] * (1.0f - t) + sPE_list0[idx + 1] * t;
        selectPeakExp = sPE_list1[idx] * (1.0f - t) + sPE_list1[idx + 1] * t;
        selectPeakExp = selectPeakExp * flatness + hh * (1.0f - flatness);

        createDiffTabs();
    }

    public void setDeNoise(float dF) {
        deNoiseF = dF;
    }

    public void setPreSharpen(float pF) {
        preSharpenF = pF;
    }

    public void setDither(float pD) {
        ditherF = pD;
    }

    public void setFractNoise(float fN) {
        fractNoiseF = fN;
    }

    public void setFractTab(FractTab fT) {
        fractTab = fT;
    }

    public int sizeDstX() {
        return sizeXDst;
    }

    public int sizeDstY() {
        return sizeYDst;
    }

    public int sizeSrcX() {
        return sizeX;
    }

    public int sizeSrcY() {
        return sizeY;
    }

    public float dither() {
        return ditherF;
    }

    public float fractNoise() {
        return fractNoiseF;
    }

    protected abstract BasicArray.Factory<T> getFactory();

    // the methods for Enlarge, protected for use in class with calc-thread-design
    // the read & write methods,
    // normally only ReadSrcPixel & WriteDstPixel have to be implemented in real enlarger
    // these are used by the predefined Block & Line Read/Write methods
    protected abstract T readSrcPixel(int srcX, int srcY);

    protected abstract void writeDstPixel(T p, int dstCX, int dstCY);

    protected void readSrcBlock() {
        // copy data, pos outside src is ok, filled with margin-data
        //srcBlock.CopyFromArray(src, SrcBlockEdgeX(), SrcBlockEdgeY() );
        int x, y, sx, sy;
        if (onlyShrinking())
            return;

        T[] dst;
        int srcSizeX = sizeSrcX();
        int srcSizeY = sizeSrcY();

        int blockSizeX = sizeSrcBlockX();
        int blockSizeY = sizeSrcBlockY();
        int srcEdgeX = srcBlockEdgeX();
        int srcEdgeY = srcBlockEdgeY();
        dst = currentSrcBlock().buffer();
        int dstP = 0;

        y = 0;
        sy = srcEdgeY;
        // while dst outside: copy pixels of src-line 0
        while (sy <= 0 && y < blockSizeY) {
            x = 0;
            sx = srcEdgeX;
            // while dst outside: write src-edge-pixel
            while (sx < 0 && x < blockSizeX) {
                dst[dstP] = readSrcPixel(0, 0);
                dstP++;
                x++;
                sx++;
            }
            // copy pixels of src-line 0
            while (sx < srcSizeX - 1 && x < blockSizeX) {
                dst[dstP] = readSrcPixel(sx, 0);
                dstP++;
                x++;
                sx++;
            }
            // while dst outside: write src-edge-pixel
            while (x < blockSizeX) {
                dst[dstP] = readSrcPixel(srcSizeX - 1, 0);
                dstP++;
                x++;
                sx++;
            }
            y++;
            sy++;
        }

        while (sy < srcSizeY - 1 && y < blockSizeY) {
            x = 0;
            sx = srcEdgeX;
            // while dst outside: write src-border-pixel
            while (sx < 0 && x < blockSizeX) {
                dst[dstP] = readSrcPixel(0, sy);
                dstP++;
                x++;
                sx++;
            }
            // copy pixels normally
            while (sx < srcSizeX - 1 && x < blockSizeX) {
                dst[dstP] = readSrcPixel(sx, sy);
                dstP++;
                x++;
                sx++;
            }
            // while dst outside: write src-border-pixel
            while (x < blockSizeX) {
                dst[dstP] = readSrcPixel(srcSizeX - 1, sy);
                dstP++;
                x++;
                sx++;
            }
            y++;
            sy++;
        }

        // for outside-parts: copy pixels of last src-line
        while (y < blockSizeY) {
            x = 0;
            sx = srcEdgeX;
            // while dst outside: write src-edge-pixel
            while (sx < 0 && x < blockSizeX) {
                dst[dstP] = readSrcPixel(0, srcSizeY - 1);
                dstP++;
                x++;
                sx++;
            }
            // copy pixels of last src-line
            while (sx < srcSizeX - 1 && x < blockSizeX) {
                dst[dstP] = readSrcPixel(sx, srcSizeY - 1);
                dstP++;
                x++;
                sx++;
            }
            // while dst outside: write src-edge-pixel
            while (x < blockSizeX) {
                dst[dstP] = readSrcPixel(srcSizeX - 1, srcSizeY - 1);
                dstP++;
                x++;
                sx++;
            }
            y++;
            sy++;
        }
    }

    protected void writeDstBlock() {
        // offsetX, offsetY: new addition to allow black margins in output
        int dstBX, dstBY;
        if (onlyShrinking())
            return;

        for (dstBY = dstMinBY(); dstBY < dstMaxBY(); dstBY++) {
            for (dstBX = dstMinBX(); dstBX < dstMaxBX(); dstBX++) {
                int dstCX = dstBX + dstBlockEdgeX() - clipX0() + offsetX;
                int dstCY = dstBY + dstBlockEdgeY() - clipY0() + offsetY;
                writeDstPixel(dstBlock.get(dstBX, dstBY), dstCX, dstCY);
            }
        }
    }

    // read & write line: for case of shrinking
    protected void readSrcLine(int srcY, T[] srcLine) {
        for (int srcX = 0; srcX < sizeSrcX(); srcX++) {
            srcLine[srcX] = readSrcPixel(srcX, srcY);
        }
    }

    protected void writeDstLine(int dstY, T[] dstLine) {
        // offsetX, offsetY: new addition to allow black margins in output
        for (int dstX = clipX0(); dstX < clipX1(); dstX++) {
            writeDstPixel(dstLine[dstX], dstX - clipX0() + offsetX, dstY - clipY0() + offsetY);
        }
    }

    // calculate positions, clipping
    protected void blockBegin(int dstXEdge, int dstYEdge) {
        // smallPos of upper left edge of DstBlock
        dstBlockEdgeX = dstXEdge;
        dstBlockEdgeY = dstYEdge;
        // bigPos of upper left edge of   SrcBlock: add margin
        srcBlockEdgeX = bigSrcPosX(dstXEdge) - Constants.srcBlockMargin;
        srcBlockEdgeY = bigSrcPosY(dstYEdge) - Constants.srcBlockMargin;

        // calculate clipping
        dstMinBX = 0;
        dstMaxBX = sizeDstBlock;
        dstMinBY = 0;
        dstMaxBY = sizeDstBlock;
        if (dstBlockEdgeX < clipX0)
            dstMinBX = clipX0 - dstBlockEdgeX;
        if (dstBlockEdgeY < clipY0)
            dstMinBY = clipY0 - dstBlockEdgeY;
        if (dstBlockEdgeX + sizeDstBlock >= clipX1)
            dstMaxBX = clipX1 - dstBlockEdgeX;
        if (dstBlockEdgeY + sizeDstBlock >= clipY1)
            dstMaxBY = clipY1 - dstBlockEdgeY;
    }

    // calc indie & simil Weights for BigPixels
    protected void calcBaseWeights() {
        int x, y;

        readDerivatives();
        for (y = 1; y < sizeSrcBlockY - 1; y++) {
            for (x = 1; x < sizeSrcBlockX - 1; x++) {
                float dd, intensityFakt;
                @SuppressWarnings("unused")
                float gradNorm, laplaceNorm;
                float dWork; // workMask-Fakt

                intensityFakt = baseIntensity.getF(x, y);
                gradNorm = (srcBlock.dX(x, y).norm1() + srcBlock.dY(x, y).norm1());
                laplaceNorm = d2L.get(x, y).norm1();

                dd = 1.0f - 12.0f * gradNorm * intensityFakt;

                if (dd < 0.0)
                    dd = 0.0f;
                else if (dd > 1.0)
                    dd = 1.0f;
                dd += 0.0001;

                dWork = (20.0f * gradNorm) * (intensityFakt + 0.9f);
                dWork *= dWork;
                dWork -= 0.7;
                if (dWork < 0.0)
                    dWork = 0.0f;
                else if (dWork > 1.0)
                    dWork = 1.0f;

                baseWeights.set(x, y, dd);
                workMask.set(x, y, dWork);
            }
        }

        MyArray bW = new MyArray(baseWeights);

        for (y = 1; y < sizeSrcBlockY - 1; y++) {
            for (x = 1; x < sizeSrcBlockX - 1; x++) {
                float dd, cc, intensityFakt;
                float gradNorm, laplaceNorm;

                intensityFakt = baseIntensity.getF(x, y);
                gradNorm = (srcBlock.dX(x, y).norm1() + srcBlock.dY(x, y).norm1());
                laplaceNorm = d2L.get(x, y).norm1();
                float v = bW.getF(x, y);

                dd = modVal2(v - bW.getF(x - 1, y - 1), v - bW.getF(x + 1, y + 1));
                dd += modVal2(v - bW.getF(x - 1, y + 1), v - bW.getF(x + 1, y - 1));
                dd *= 0.5;
                dd += modVal2(v - bW.getF(x, y + 1), v - bW.getF(x, y - 1));
                dd += modVal2(v - bW.getF(x - 1, y), v - bW.getF(x + 1, y));
                dd *= (1.0 / 3.0);

                dd = v - lineNegF * dd; //0.8
                if (dd < 0.01) {
                    dd = dd * 100.0f;
                    dd = 1.0f / (2.0f - dd);
                    dd *= 0.01;
                }
                dd = (float) Math.pow(dd, sharpExp); // Sharpness!

                cc = 1.0f - 12.0f * gradNorm * intensityFakt;
                if (cc < 0.0)
                    cc = 0.0f;
                else if (cc > 1.0)
                    dd = 1.0f;
                cc = 10.0f * laplaceNorm * intensityFakt * (0.4f + cc);
                if (cc > 1.0)
                    cc = 1.0f;
                cc = (float) Math.pow(cc, 2.0);
                dd += linePosF * cc;
                if (dd < 0.0)
                    dd = 0.0f;

                baseWeights.set(x, y, dd);
            }
        }
        workMask.smoothen();
    }

    @SuppressWarnings("unchecked")
    protected void blockEnlargeSmooth() {
        int a, srcBY, srcBYNew, dstBX, dstBY;
        T[][] line = (T[][]) new Primitive[5][], ll = (T[][]) new Primitive[5][];
        T[] hl;
        if (onlyShrinking())
            return;

        for (a = 0; a < 5; a++)
            line[a] = ll[a] = (T[]) new Primitive[sizeDstBlock];
        srcBY = currentSrcBlockY(0);
        for (a = 0; a < 5; a++)
            blockReadLineSmooth(srcBY + a - 2, line[a]);
        for (dstBY = 0; dstBY < sizeDstBlock; dstBY++) {
            int dstY;
            float[] kTabY;
            dstY = dstBY + dstBlockEdgeY;
            if (dstY >= 0 && dstY < sizeYDst)
                kTabY = enlargeKernelY[dstY];
            else
                kTabY = enlargeKernelY[0];
            srcBYNew = currentSrcBlockY(dstBY);

            // bigPos changed? . scroll
            if (srcBYNew > srcBY) {
                srcBY = srcBYNew;
                hl = line[0];
                line[0] = line[1];
                line[1] = line[2];
                line[2] = line[3];
                line[3] = line[4];
                line[4] = hl;
                blockReadLineSmooth(srcBY + 2, line[4]);
            }
            for (dstBX = 0; dstBX < sizeDstBlock; dstBX++) {
                T p = line[0][dstBX].operatorMultiply(kTabY[0]);
                p = p.operatorPlusEqual(line[1][dstBX].operatorMultiply(kTabY[1]));
                p = p.operatorPlusEqual(line[2][dstBX].operatorMultiply(kTabY[2]));
                p = p.operatorPlusEqual(line[3][dstBX].operatorMultiply(kTabY[3]));
                p = p.operatorPlusEqual(line[4][dstBX].operatorMultiply(kTabY[4]));
                dstBlock.set(dstBX, dstBY, p);
            }
        }
    }

    protected void addRandom() {
        if (onlyShrinking())
            return;

        for (int dstBY = dstMinBY; dstBY < dstMaxBY; dstBY++) {
            for (int dstBX = dstMinBX; dstBX < dstMaxBX; dstBX++) {
                float w = (2.0f * randGen.randF() - 1.0f);
                w *= randGen.randF();
                w *= 0.5 * ditherF;
                w = 1.0f + w;

                dstBlock.mul(dstBX, dstBY, w);
            }
        }
    }

    protected void enlargeBlock() {
        if (onlyShrinking())
            return;

        calcBaseWeights();
        blockEnlargeSmooth();
        maskBlockEnlargeSmooth();
        enlargeBlockPart(dstMinBY, dstMaxBY);
        addRandom();
        dstBlock.clamp01();
    }

    // used for splitting up EnlargeBlock ( . calc thread )
    protected void enlargeBlockPart(int dstStartBY, int dstEndBY) {
        int dstBX, dstBY, srcBX, srcBY, srcBXNew;
        float[] kerX, kerY;
        @SuppressWarnings("unchecked")
        T[] modColor = (T[]) new Primitive[25];
        if (onlyShrinking())
            return;

// for each pixel in dstBlock:
// get neighbouring BigPixels and their simil & indie weights (ReadBigPixelNeighs)
// get weighting kernels for sx and sy pos                    ( kerX, kerY )
// get the prev.calc. smooth-enlarged color at (dstBX,dstBY)  ( smallColor )
// weight all neigh. bigPixels with
//        their indies & simils
//        the kernel kerX*kerY
//        the weight resulting from difference of
//          bigPixelColor and smallColor                       ( SelectWeight )
// sum up all weighted colors and return
//    lincomb with smoothEnlarged smallColor

// for each bigPixel calculate quadric from derivatives
// for inc. of dstBX calc only increment of the Quadrics
        @SuppressWarnings("unchecked")
        T[] quadric = (T[]) new Primitive[5 * 5], quadDelta = (T[]) new Primitive[5 * 5], quadD2 = (T[]) new Primitive[5 * 5];
        int srcXm2, srcYm2;
        float fx, fy;
        float deltaX = 1.0f * invScaleFaktX;
        int a, ax, ay;
        boolean lastPixelWasCalculated; // used for quadric-refreshing

        if (dstStartBY < dstMinBY)
            dstStartBY = dstMinBY;
        if (dstEndBY > dstMaxBY)
            dstEndBY = dstMaxBY;
        for (dstBY = dstStartBY; dstBY < dstEndBY; dstBY++) {
            kerY = selectKernelY[dstBY + dstBlockEdgeY];

            srcBX = currentSrcBlockX(0);
            srcBY = currentSrcBlockY(dstBY);
            srcYm2 = srcBY - 2 + srcBlockEdgeY;
            fy = (dstBY + dstBlockEdgeY) * invScaleFaktY - (srcYm2) - 0.25f;

            readBigPixelNeighs(srcBX, srcBY);

            lastPixelWasCalculated = false; // quadric: a fresh start for a new row
            for (dstBX = dstMinBX; dstBX < dstMaxBX; dstBX++) {
                kerX = selectKernelX[dstBX + dstBlockEdgeX];
                srcBXNew = currentSrcBlockX(dstBX);
                srcXm2 = srcBXNew - 2 + srcBlockEdgeX;
                fx = (dstBX + dstBlockEdgeX) * invScaleFaktX - (srcXm2) - 0.25f;

                if (srcBXNew > srcBX) { // do we step forward in the src-system?
                    // in this case: refresh the src-neighbour-mat
                    // the matrix of 5x5 quadric-datas has to be shifted,
                    // the last column is calculated
                    srcBX = srcBXNew;
                    readBigPixelNeighs(srcBX, srcBY);

                    // if the last pixel was not calculated, then all
                    // quadrics are newly initialized further down, else:
                    // shift the quadric-data by one to the left
                    if (derivF > 0.0 && lastPixelWasCalculated) {
                        a = 0;
                        for (ay = 0; ay < 5; ay++) {
                            for (ax = 0; ax < 4; ax++) {
                                quadric[a] = quadric[a + 1];
                                quadDelta[a] = quadDelta[a + 1];
                                quadD2[a] = quadD2[a + 1];

                                // increment the quadric-data
                                quadric[a] = quadric[a].operatorPlusEqual(quadDelta[a]);
                                quadDelta[a] = quadDelta[a].operatorPlusEqual(quadD2[a]);

                                a++;
                            }
                            // the last quadric in every row is new
                            float px = fx - (ax);
                            float py = fy - (ay);
                            quadricCalc(px, py, a, deltaX, quadric[a], quadDelta[a], quadD2[a]);
                            a++;
                        }
                    }
                } else if (derivF > 0.0 && lastPixelWasCalculated) {
                    // increment the quadric-data
                    a = 0;
                    for (ay = 0; ay < 5; ay++) {
                        for (ax = 0; ax < 5; ax++) {
                            quadric[a] = quadric[a].operatorPlusEqual(quadDelta[a]);
                            quadDelta[a] = quadDelta[a].operatorPlusEqual(quadD2[a]);
                            a++;
                        }
                    }
                }

                // Modify One Small Pixel at (dstBX,dstBY) with smallColor
                T smallColor = dstBlock.get(dstBX, dstBY);

                // for diffCalc fract-modify smallColor
                T smallColorFract = smallColor;

                if (fractTab != null && fractNoiseF > 0.0) {
                    float wf = myFractTab().getT(dstBX + dstBlockEdgeX(), dstBY + dstBlockEdgeY());
                    smallColorFract = smallColorFract.operatorPlusEqual(smallColorFract.operatorMultiply(fractNoiseF * 0.01f
                                                                                                         * wf));
                    smallColorFract.clip();
                }

                float[] wMat = new float[5 * 5];
                float totalWeight, w, normF;
                float wMask = 1.0f;
                T color = srcBlock.newContent(), diff;

                wMask = workMaskDst.getF(dstBX, dstBY) - 0.01f;
                if (wMask > 0.0) {
                    wMask *= 1.5;
                    if (wMask > 1.0)
                        wMask = 1.0f;
                    else {
                        wMask = wMask * wMask * (3.0f - 2.0f * wMask);
                    }
                    totalWeight = 0.0f;

                    // if last pixel was not in the mask:
                    // initialize the 5x5 quadrics
                    if (derivF > 0.0 && !lastPixelWasCalculated) {
                        a = 0;
                        for (ay = 0; ay < 5; ay++) {
                            for (ax = 0; ax < 5; ax++) {
                                float px = fx - (ax);
                                float py = fy - (ay);
                                quadricCalc(px, py, a, deltaX, quadric[a], quadDelta[a], quadD2[a]);
                                a++;
                            }
                        }
                    }
                    lastPixelWasCalculated = true;

                    a = 0;
                    // weight the 5x5 source pixels
                    for (ay = 0; ay < 5; ay++) {
                        for (ax = 0; ax < 5; ax++) {
                            if (derivF > 0.0) {
                                modColor[a] = quadric[a].operatorPlus(bigPixelColor[a]);
                                w = derivDiffF * quadric[a].norm1();
                            } else {
                                modColor[a] = bigPixelColor[a];
                                w = 0.0f;
                            }

                            //diff = modColor[a] - smallColor;
                            diff = modColor[a].operatorMinus(smallColorFract);
                            w = 10.0f * (w + diff.norm1()) * bigPixelIntensity[a];
                            w = bigPixelWeight[a] * kerX[ax] * kerY[ay] * selectWeight(w);

                            // give bigPixel add. Weigt near center
                            float px = fx - (ax);
                            float py = fy - (ay);
                            float ww = bigPixelCenterW[a];
                            ww = 1.0f + (centerWeight(px * px + py * py) - 1.0f) * ww;
                            w *= ww;

                            // deform the weight via fractTab,
                            // for each srcPixel we have deform-kernel
                            // ( FractCX,FractCY,FractCVal : center pos & center val in fractTab )
                            // kernel was selected by randomizing the coord of the srcPixel
                            if (fractTab != null && fractNoiseF > 0.0) {
                                int dx = (int) (px * scaleFaktX) + bigPixelFractCX[a];
                                int dy = (int) (py * scaleFaktY) + bigPixelFractCY[a];
                                ww = 1.0f + 1.5f * fractNoiseF * (fractTab.getT(dx, dy) - bigPixelFractCVal[a]);

                                if (ww < 0.01)
                                    ww = 0.01f + (0.01f - ww);
                                if (ww > 3.0)
                                    ww = 3.0f - (ww - 3.0f);
                                if (ww < 0.01)
                                    ww = 0.01f + (0.01f - ww);
                                w *= ww;
                            }
                            // wavy special effect
                            // float sdx = bigPixelDX[a].x;
                            // float sdy = bigPixelDY[a].x;
                            // float sd = Math.sqrt(sdx * sdx + sdy * sdy);
                            // if (sd > 0) {
                            // float sdd = 1.0 / sd;
                            // sdx *=sdd;
                            // sdy *=sdd;
                            // sd *= 10.0;
                            // if (sd > 0.5)
                            //     sd = 0.5;
                            // }
                            // ww = 1.0 + sd * Math.cos(10.0 * (sdx * px + sdy * py));
                            // modColor[a] *= ww;

                            w += 0.000000001;
                            wMat[a] = w;
                            totalWeight += w;
                            a++;
                        }
                    }
                    normF = 1.0f / totalWeight;
                    T colorR = srcBlock.newContent();
                    color.setZero();
                    colorR.setZero();

                    for (a = 0; a < 25; a++)
                        wMat[a] *= normF;

                    for (a = 0; a < 25; a++) {
                        color.addMul(wMat[a], modColor[a]); //color += modColor[a]*wMat[a];
                    }

                    diff = color.operatorMinus(smallColor);
                    smallColor = smallColor.operatorPlusEqual(diff.operatorMultiply(wMask));

                }
                dstBlock.set(dstBX, dstBY, smallColor);
            }
        }
    }

    // Smooth-Enlarging Mask-Field
    protected void maskBlockEnlargeSmooth() {
        int a, srcBY, srcBYNew, dstBX, dstBY;
        float[][] line = new float[5][], ll = new float[5][];
        float[] hl;

        for (a = 0; a < 5; a++)
            line[a] = ll[a] = new float[sizeDstBlock];
        srcBY = currentSrcBlockY(0);
        for (a = 0; a < 5; a++)
            maskBlockReadLineSmooth(srcBY + a - 2, line[a]);
        for (dstBY = 0; dstBY < sizeDstBlock; dstBY++) {
            int dstY;
            float[] kTabY;
            dstY = dstBY + dstBlockEdgeY;
            if (dstY >= 0 && dstY < sizeYDst)
                kTabY = enlargeKernelY[dstY];
            else {
                kTabY = enlargeKernelY[0];
            }
            srcBYNew = currentSrcBlockY(dstBY);

            // bigPos changed? . scroll
            if (srcBYNew > srcBY) {
                srcBY = srcBYNew;
                hl = line[0];
                line[0] = line[1];
                line[1] = line[2];
                line[2] = line[3];
                line[3] = line[4];
                line[4] = hl;
                maskBlockReadLineSmooth(srcBY + 2, line[4]);
            }
            for (dstBX = 0; dstBX < sizeDstBlock; dstBX++) {
                float p;
                p = line[0][dstBX] * kTabY[0];
                p += line[1][dstBX] * kTabY[1];
                p += line[2][dstBX] * kTabY[2];
                p += line[3][dstBX] * kTabY[3];
                p += line[4][dstBX] * kTabY[4];
                workMaskDst.set(dstBX, dstBY, p);
            }
        }
    }

    // Shrinking ( scaleF < 1.0 )
    @SuppressWarnings("unchecked")
    protected void shrinkClip() {

        T[] srcLine, addLine, dstLine;
        int srcY, dstX, dstY;
        int srcY0, srcY1;
        float floorY, ff;

        srcLine = (T[]) new Primitive[sizeX + 2];
        addLine = (T[]) new Primitive[sizeXDst + 2];
        dstLine = (T[]) new Primitive[sizeXDst + 2];
        for (dstX = 0; dstX < sizeXDst; dstX++)
            dstLine[dstX].setZero();

        if (clipY0 > 0) {
            srcY0 = (int) ((clipY0 - 1) / scaleFaktY + 0.5);
            floorY = (srcY0) * scaleFaktY;
            dstY = (int) (floorY);
            floorY -= (dstY);
        } else {
            srcY0 = 0;
            dstY = 0;
            floorY = 0.0f;
        }
        srcY1 = (int) ((clipY1 + 1) / scaleFaktY + 0.5) + 1;
        if (srcY1 > sizeY)
            srcY1 = sizeY;

        for (srcY = srcY0; srcY < srcY1; srcY++) {
            readSrcLine(srcY, srcLine); // read srcLine,  shrink it in x-direction, resulting in addLine
            shrinkLineClip(srcLine, addLine);
            ff = floorY + scaleFaktY - 1.0f;
            if (ff > 0) { // stepping into new dstLine reached, share addLine between old and new dstLine
                for (dstX = clipX0; dstX < clipX1; dstX++) {
                    dstLine[dstX] = dstLine[dstX].operatorPlusEqual(addLine[dstX].operatorMultiply(scaleFaktY - ff));
                }
                if (dstY >= clipY0 && dstY < clipY1) {
                    writeDstLine(dstY, dstLine);
                }
                floorY -= 1.0;
                dstY++;
                for (dstX = clipX0; dstX < clipX1; dstX++) { // clear dstLine, fill with rest of addLine
                    dstLine[dstX] = addLine[dstX].operatorMultiply(ff);
                }
            } else
                // addLine fully added to current dstLine ( with appropriate weight )
                for (dstX = clipX0; dstX < clipX1; dstX++)
                    dstLine[dstX] = dstLine[dstX].operatorPlus(addLine[dstX].operatorMultiply(scaleFaktY));
            floorY += scaleFaktY;
        }
        if (dstY >= clipY0 && dstY < clipY1) {
            writeDstLine(dstY, dstLine);
        }
    }

    protected float scaleFaktX() {
        return scaleFaktX;
    }

    protected float scaleFaktY() {
        return scaleFaktY;
    }

    // needed for implementing  Read&Write
    protected int clipX0() {
        return clipX0;
    }

    protected int clipY0() {
        return clipY0;
    }

    protected int clipX1() {
        return clipX1;
    }

    protected int clipY1() {
        return clipY1;
    }

    protected int outputWidth() {
        return outputWidth;
    }

    protected int outputHeight() {
        return outputHeight;
    }

    protected int dstMinBX() {
        return dstMinBX;
    }

    protected int dstMaxBX() {
        return dstMaxBX;
    }

    protected int dstMinBY() {
        return dstMinBY;
    }

    protected int dstMaxBY() {
        return dstMaxBY;
    }

    protected int srcBlockEdgeX() {
        return srcBlockEdgeX;
    }

    protected int srcBlockEdgeY() {
        return srcBlockEdgeY;
    }

    protected int dstBlockEdgeX() {
        return dstBlockEdgeX;
    }

    protected int dstBlockEdgeY() {
        return dstBlockEdgeY;
    }

    protected int sizeSrcBlockX() {
        return sizeSrcBlockX;
    }

    protected int sizeSrcBlockY() {
        return sizeSrcBlockY;
    }

    protected int sizeDstBlock() {
        return sizeDstBlock;
    }

    protected void srcBlockReduceNoise() {
        srcBlock.setFactory(getFactory()); // TODO vavi
        srcBlock.reduceNoise(deNoiseF);
    }

    protected void srcBlockSharpen() {
        srcBlock.sharpen(preSharpenF);
    }

    protected BasicArray<T> currentSrcBlock() {
        return srcBlock;
    }

    protected BasicArray<T> currentDstBlock() {
        return dstBlock;
    }

    protected float randF() {
        return randGen.randF();
    }

    protected FractTab myFractTab() {
        return fractTab;
    }

    // format.clip allows exceeding bounds ( for black margins )
    // this is converted to new cliprect within bounds and additional offset
    private void calculateClipAndOffset(final EnlargeFormat format) {
        clipX0 = format.clipX0;
        clipY0 = format.clipY0;
        clipX1 = format.clipX1;
        clipY1 = format.clipY1;
        offsetX = offsetY = 0;
        if (clipX0 < 0) {
            offsetX = -clipX0;
            clipX0 = 0;
        }
        if (clipY0 < 0) {
            offsetY = -clipY0;
            clipY0 = 0;
        }
        if (clipX1 > sizeXDst) {
            clipX1 = sizeXDst;
        }
        if (clipY1 > sizeYDst) {
            clipY1 = sizeYDst;
        }
    }

    // Shrinking ( scaleF < 1.0 )
    private void shrinkLineClip(T[] srcLine, T[] dstLine) {
        int srcX, dstX;
        int srcX0, srcX1;
        float floorX, ff;

        floorX = 0.0f; // left border of current smallPixel

        for (dstX = clipX0; dstX < clipX1; dstX++)
            dstLine[dstX].setZero();

        if (clipX0 > 0) {
            srcX0 = (int) ((clipX0 - 1) / scaleFaktX + 0.5);
            floorX = (srcX0) * scaleFaktX;
            dstX = (int) (floorX);
            floorX -= (dstX);
        } else {
            srcX0 = 0;
            dstX = 0;
            floorX = 0.0f;
        }
        srcX1 = (int) ((clipX1 + 1) / scaleFaktX + 0.5);
        if (srcX1 > sizeX)
            srcX1 = sizeX;

        for (srcX = srcX0; srcX < srcX1; srcX++) {
            ff = floorX + scaleFaktX - 1.0f;
            if (ff > 0) { // stepping into new dstPixel reached: share srcPixel between old and new dstPixel
                dstLine[dstX] = dstLine[dstX].operatorPlusEqual(srcLine[srcX].operatorMultiply(scaleFaktX - ff));
                dstX++;
                floorX -= 1.0;
                dstLine[dstX] = srcLine[srcX].operatorMultiply(ff);
            } else
                // fully add srcPixel to dstPixel ( weighted )
                dstLine[dstX] = dstLine[dstX].operatorPlusEqual(srcLine[srcX].operatorMultiply(scaleFaktX));
            floorX += scaleFaktX;
        }
    }

    static final float enlargeKernelRad = 1.8f;

    static final float selectKernelRad = 1.9f;

    // general kernelList-Creation
    // Create the Smooth-Enlarger- and Select-Kernels
    private void createKernels() {
        int kernelLen;
        float[] fineKernelIntegralTab;

        // kernels for enlarge in x-dir
        kernelLen = 2 * (int) ((1 << Constants.kerFineExp) * enlargeKernelRad * scaleFaktX) + 1;
        fineKernelIntegralTab = createSmoothEnlargeKernelIntegralTab(kernelLen);
        createKernelsFromIntegralTab(enlargeKernelX, sizeXDst, fineKernelIntegralTab, kernelLen, scaleFaktX, smallToBigTabX);

        // kernels for enlarge in y-dir
        kernelLen = 2 * (int) ((1 << Constants.kerFineExp) * enlargeKernelRad * scaleFaktY) + 1;
        fineKernelIntegralTab = createSmoothEnlargeKernelIntegralTab(kernelLen);
        createKernelsFromIntegralTab(enlargeKernelY, sizeYDst, fineKernelIntegralTab, kernelLen, scaleFaktY, smallToBigTabY);

        // kernels for selection in x-dir
        kernelLen = 2 * (int) ((1 << Constants.kerFineExp) * selectKernelRad * scaleFaktX) + 1;
        fineKernelIntegralTab = createSelectKernelIntegralTab(kernelLen);
        createKernelsFromIntegralTab(selectKernelX, sizeXDst, fineKernelIntegralTab, kernelLen, scaleFaktX, smallToBigTabX);

        // kernels for selection in y-dir
        kernelLen = 2 * (int) ((1 << Constants.kerFineExp) * selectKernelRad * scaleFaktY) + 1;
        fineKernelIntegralTab = createSelectKernelIntegralTab(kernelLen);
        createKernelsFromIntegralTab(selectKernelY, sizeYDst, fineKernelIntegralTab, kernelLen, scaleFaktY, smallToBigTabY);
    }

    @SuppressWarnings("unused")
    private float[] createSmoothEnlargeKernelTab(int len) {
        int n;
        float x, y;
        float sum, normF;

        float[] tab = new float[len];
        sum = 0.0f;
        for (n = 0; n < len; n++) {
            x = 2.0f * (n) / (len - 1) - 1;
            y = 2.0f - 1.0f / (1 + x + 0.00000001f) - 1.0f / (1 - x + 0.00000001f);
            y = (float) Math.exp(y);
            tab[n] = y;//*y*y;
            sum += tab[n];
        }

        normF = 1.0f / sum;
        for (n = 0; n < len; n++) {
            tab[n] *= normF;
        }
        return tab;
    }

    @SuppressWarnings("unused")
    private float[] createSelectKernelTab(int len) {
        int n;
        float x, y;
        float sum, normF;

        float[] tab = new float[len];
        sum = 0.0f;
        for (n = 0; n < len; n++) {

            x = 2.0f * (n) / (len - 1) - 1.0f;
            y = 1.0f - x * x * x * x;
            tab[n] = y;
            sum += tab[n];

        }

        normF = 1.0f / sum;
        for (n = 0; n < len; n++) {
            tab[n] *= normF;
        }
        return tab;
    }

    private float[] createSmoothEnlargeKernelIntegralTab(int len) {
        int n;
        float x, y;
        float sum, normF;

        float[] tab = new float[len];
        sum = 0.0f;
        for (n = 0; n < len; n++) {
            x = 2.0f * (n) / (len - 1) - 1;
            y = 2.0f - 1.0f / (1 + x + 0.00000001f) - 1.0f / (1 - x + 0.00000001f);
            y = (float) Math.exp(y);
            sum += y;
            tab[n] = sum;
        }

        normF = 1.0f / sum;
        for (n = 0; n < len; n++) {
            tab[n] *= normF;
        }
        return tab;
    }

    private float[] createSelectKernelIntegralTab(int len) {
        int n;
        float x, y;
        float sum, normF;

        float[] tab = new float[len];
        sum = 0.0f;
        for (n = 0; n < len; n++) {

            x = 2.0f * (n) / (len - 1) - 1.0f;
            y = 1.0f - x * x * x * x;
            sum += y;
            tab[n] = sum;

        }

        normF = 1.0f / sum;
        for (n = 0; n < len; n++) {
            tab[n] *= normF;
        }
        return tab;
    }

    @SuppressWarnings("unused")
    private void createKernelsFromTab(float[][] kerList, int kerListLen, float[] kerTab, int kerTabLen, float scaleF) {
        float invScaleF = 1.0f / scaleF;
        float invFineScaleF = (1.0f / scaleF) * (1.0f / (1 << Constants.kerFineExp));
        int smallPos, bigPos, k, a, tabPos;
        // smallPos:    pos of small pixel in dst
        // bigPos:      pos of corresp. bigPixel in src
        // finePosKer:  fine Pos of fine-kernel-entry
        // bigPosKer:   pos of corresp. bigPixel in src
        for (smallPos = 0; smallPos < kerListLen; smallPos++) {
            float[] ker = kerList[smallPos];
            for (a = 0; a < 5; a++) {
                ker[a] = 0.0f;
            }
            bigPos = (int) ((smallPos) * invScaleF);
            for (k = 0; k < kerTabLen; k++) {
                int finePosKer, bigPosKer;
                // get bigPixelPos of fine-kernel pixel
                // get tabIndex by comp. to bigPos of center of kernel
                finePosKer = ((smallPos << Constants.kerFineExp) + k - (kerTabLen >> 1));
                finePosKer += (1 << Constants.kerFineExp) >> 1; // add half smallPixel
                bigPosKer = (int) ((finePosKer) * invFineScaleF);
                tabPos = 2 + bigPosKer - bigPos;
                ker[tabPos] += kerTab[k];
            }
        }
    }

    @SuppressWarnings("unused")
    private void createKernelsFromIntegralTab(float[][] kerList,
                                              int kerListLen,
                                              float[] kerITab,
                                              int kerTabLen,
                                              float scaleF,
                                              int[] smallToBigTab) {
        float invScaleF = 1.0f / scaleF;
        float invFineScaleF = (1.0f / scaleF) * (1.0f / (1 << Constants.kerFineExp));
        int smallPos, kernelStartPosFine, bigPos, k, a, tabPos;
        // smallPos:    pos of small pixel in dst
        // bigPos:      pos of corresp. bigPixel in src
        // finePosKer:  fine Pos of fine-kernel-entry
        // bigPosKer:   pos of corresp. bigPixel in src

        for (smallPos = 0; smallPos < kerListLen; smallPos++) {
            float[] ker = kerList[smallPos];
            for (a = 0; a < 5; a++) {
                ker[a] = 0.0f;
            }
            bigPos = smallToBigTab[(int) (smallPos + smallToBigMargin)];
            kernelStartPosFine = (smallPos << Constants.kerFineExp) + ((1 << Constants.kerFineExp) >> 1) - (kerTabLen >> 1);
            for (a = 0; a < 5; a++) {
                int bigPosKer, finePosKerLeft, finePosKerRight;
                bigPosKer = bigPos - 2 + a;
                finePosKerLeft = (int) ((bigPosKer << Constants.kerFineExp) * scaleF) - kernelStartPosFine;
                finePosKerRight = (int) (((bigPosKer + 1) << Constants.kerFineExp) * scaleF) - kernelStartPosFine;
                if (finePosKerLeft < 0)
                    finePosKerLeft = 0;
                else if (finePosKerLeft >= kerTabLen)
                    finePosKerLeft = kerTabLen - 1;
                if (finePosKerRight < 0)
                    finePosKerRight = 0;
                else if (finePosKerRight >= kerTabLen)
                    finePosKerRight = kerTabLen - 1;
                ker[a] = kerITab[finePosKerRight] - kerITab[finePosKerLeft];
            }
        }
    }

    private void createDiffTabs() {
        int a;

        if (onlyShrinking())
            return;

        for (a = 0; a < Constants.diffTabLen; a++) {
            float w, w0 = (float) (a) / (float) (Constants.diffTabLen);
            //
            // 1. SelectWeights
            //
            w = 1.0f - w0;
            if (w < 0.0)
                w = 0.0f;
            w = w * w * (3.0f - 2.0f * w);
            w = (float) Math.pow(w, selectPeakExp);
            selectDiffTab[a] = w;

            //
            // 2. CenterWeights
            //
            w = w0;
            w = (float) Math.pow(w, centerWExp);
            w = 1.0f + centerWeightF * w;

            centerWeightTab[a] = w;
        }
    }

    private void blockReadLineSmooth(int srcBY, T[] line) {
        int srcBX, dstBX, dstX;
        for (dstBX = 0; dstBX < sizeDstBlock; dstBX++) {
            float[] kTabX;
            T p;

            dstX = dstBX + dstBlockEdgeX;
            if (dstX >= 0 && dstX < sizeXDst)
                kTabX = enlargeKernelX[dstX];
            else
                kTabX = enlargeKernelX[0];

            srcBX = currentSrcBlockX(dstBX);
            p = srcBlock.get(srcBX - 2, srcBY).operatorMultiply(kTabX[0]);
            p = p.operatorPlusEqual(srcBlock.get(srcBX - 1, srcBY).operatorMultiply(kTabX[1]));
            p = p.operatorPlusEqual(srcBlock.get(srcBX, srcBY).operatorMultiply(kTabX[2]));
            p = p.operatorPlusEqual(srcBlock.get(srcBX + 1, srcBY).operatorMultiply(kTabX[3]));
            p = p.operatorPlusEqual(srcBlock.get(srcBX + 2, srcBY).operatorMultiply(kTabX[4]));
            line[dstBX] = p;
        }
    }

    private void maskBlockReadLineSmooth(int srcBY, float[] line) {
        int srcBX, dstBX, dstX;
        for (dstBX = 0; dstBX < sizeDstBlock; dstBX++) {
            float[] kTabX;
            float p;

            dstX = dstBX + dstBlockEdgeX;
            if (dstX >= 0 && dstX < sizeXDst)
                kTabX = enlargeKernelX[dstX];
            else
                kTabX = enlargeKernelX[0];

            srcBX = currentSrcBlockX(dstBX);
            p = workMask.getF(srcBX - 2, srcBY) * kTabX[0];
            p += workMask.getF(srcBX - 1, srcBY) * kTabX[1];
            p += workMask.getF(srcBX, srcBY) * kTabX[2];
            p += workMask.getF(srcBX + 1, srcBY) * kTabX[3];
            p += workMask.getF(srcBX + 2, srcBY) * kTabX[4];
            line[dstBX] = p;
        }
    }

    // bigPos of smallPixel / smallPixel in current block ( margin: need values<0,etc )
    private int bigSrcPosX(int smallPos) {
        return smallToBigTabX[(int) (smallPos + smallToBigMargin)];
    }

    private int bigSrcPosY(int smallPos) {
        return smallToBigTabY[(int) (smallPos + smallToBigMargin)];
    }

    private int srcX(int dstBX) {
        return bigSrcPosX(dstBX + dstBlockEdgeX);
    }

    private int srcY(int dstBY) {
        return bigSrcPosY(dstBY + dstBlockEdgeY);
    }

    // bigPos in current srcBlock of  smallPos in current block
    private int currentSrcBlockX(int dstBX) {
        return srcX(dstBX) - srcBlockEdgeX;
    }

    private int currentSrcBlockY(int dstBY) {
        return srcY(dstBY) - srcBlockEdgeY;
    }

    private void readDerivatives() {
        int x, y;

        for (y = 1; y < sizeSrcBlockY - 1; y++) {
            for (x = 1; x < sizeSrcBlockX - 1; x++) {
                T s00 = srcBlock.get(x - 1, y - 1);
                T s10 = srcBlock.get(x, y - 1);
                T s20 = srcBlock.get(x + 1, y - 1);
                T s01 = srcBlock.get(x - 1, y);
                T s11 = srcBlock.get(x, y);
                T s21 = srcBlock.get(x + 1, y);
                T s02 = srcBlock.get(x - 1, y + 1);
                T s12 = srcBlock.get(x, y + 1);
                T s22 = srcBlock.get(x + 1, y + 1);

                T dx, dy, d2x, d2y, dxy, d2;

                dx = s21.operatorMinus(s01).operatorMultiply(0.5f); //0.25.. + 0.125*( s22 - s02 + s20 - s00 );
                dy = s12.operatorMinus(s10).operatorMultiply(0.5f); //0.25.. + 0.125*( s22 - s20 + s02 - s00 );

                d2x = s21.operatorPlus(s01).operatorMinus(s11.operatorMultiply(2.0f));

                d2y = s12.operatorPlus(s10).operatorMinus(s11.operatorMultiply(2.0f));

                dxy = s22.operatorMinus(s20).operatorMinus(s02).operatorPlus(s00).operatorMultiply(0.25f);

                dX.set(x, y, dx);
                dY.set(x, y, dy);
                d2X.set(x, y, d2x);
                d2Y.set(x, y, d2y);
                dXY.set(x, y, dxy);

                d2 = s00.operatorPlus(s02).operatorPlus(s20).operatorPlus(s22);
                d2 = d2.operatorPlusEqual(s10.operatorPlus(s12).operatorPlus(s01).operatorPlus(s21).operatorMultiply(2.0f));
                d2 = s11.operatorMinus(d2.operatorMultiply(1.0f / 12.0f));
                d2L.set(x, y, d2);
            }
        }

        for (y = 3; y < sizeSrcBlockY - 3; y++) {
            for (x = 3; x < sizeSrcBlockX - 3; x++) {
                float sum = 0.0f;
                T c = srcBlock.get(x, y);
                for (int ay = 0; ay < 7; ay++) {
                    for (int ax = 0; ax < 7; ax++) {
                        sum += srcBlock.get(x - 3 + ax, y - 3 + ay).operatorMinus(c).norm1();
                    }
                }
                sum = 1.0f / (sum * 0.5f + 0.05f);
                baseIntensity.set(x, y, sum);
            }
        }

        MyArray bI = new MyArray(baseIntensity);

        for (y = 1; y < sizeSrcBlockY - 1; y++) {
            for (x = 1; x < sizeSrcBlockX - 1; x++) {
                float iMin = 1000.0f;
                @SuppressWarnings("unused")
                T c = srcBlock.get(x, y);
                for (int ay = 0; ay < 3; ay++) {
                    for (int ax = 0; ax < 3; ax++) {
                        float v = bI.getF(x - 1 + ax, y - 1 + ay);
                        if (iMin > v)
                            iMin = v;
                    }
                }
                baseIntensity.set(x, y, iMin);
            }
        }

        MyArray intensityS = baseIntensity.smooth();
        baseIntensity = intensityS;
        intensityS = baseIntensity.smooth();
        baseIntensity = intensityS;
    }

    // calc indie & simil Weights for BigPixels
    @SuppressWarnings("unused")
    private void calcBaseWeights0() {
    }

    // calc indie & simil Weights for BigPixels
    @SuppressWarnings("unused")
    private void calcBaseWeights1() {
    }

    // for a BigPixel (srcBX,srcBY) read surrounding 5x5
    private void readBigPixelNeighs(int srcBX, int srcBY) {
        int x, y, xx, yy, pos;
        for (y = 0; y < 5; y++) {
            for (x = 0; x < 5; x++) {
                pos = matPos(x, y);
                xx = srcBX - 2 + x;
                yy = srcBY - 2 + y;
                bigPixelColor[pos] = srcBlock.get(xx, yy);
                bigPixelWeight[pos] = baseWeights.getF(xx, yy);
                bigPixelDX[pos] = dX.get(xx, yy);
                bigPixelDY[pos] = dY.get(xx, yy);
                bigPixelD2X[pos] = d2X.get(xx, yy);
                bigPixelD2Y[pos] = d2Y.get(xx, yy);
                bigPixelDXY[pos] = dXY.get(xx, yy);
                bigPixelD2L[pos] = d2L.get(xx, yy);
                bigPixelIntensity[pos] = baseIntensity.getF(xx, yy);

                float ff = bigPixelD2L[pos].norm1() * bigPixelIntensity[pos] * 30.0f;
                if (ff > 1.0)
                    ff = 1.0f;
                ff *= ff;
                ff *= ff;
                ff *= ff;
                bigPixelCenterW[pos] = ff; // increased weight near pixel-center

                // for FractNoise: select random center of deform kernel within fractTab
                if (fractTab != null && fractNoiseF != 0.0) {
                    bigPixelFractCX[pos] = xx;
                    bigPixelFractCY[pos] = yy;
                    fractTab.getKernelCenter(bigPixelFractCX, bigPixelFractCY, bigPixelFractCVal, pos);
                }
            }
        }
    }

    // Selection-WeightFact: used when selecting BigPixel for smallPixel
    private float selectWeight(float pointDiff) {
        if (pointDiff >= 1.0)
            return selectDiffTab[Constants.diffTabLen - 1];
        return selectDiffTab[(int) (pointDiff * (Constants.diffTabLen - 1))];
    }

    // give bigPixel add. Weigt near center
    private float centerWeight(float dd) {
        dd = 1.0f - dd * (1.0f / 1.5f);
        if (dd < 0.0)
            return centerWeightTab[0];
        return centerWeightTab[(int) (dd * (Constants.diffTabLen - 1))];
    }

    private int matPos(int x, int y) {
        return x + 5 * y;
    }

    @SuppressWarnings("unused")
    private float inverse(float x) {
        if (x < 0.001) {
            x *= 1000.0f * (Constants.invTabLen - 1);
            return 1000.0f * invTab[(int) (x + 0.5)];
        } else if (x >= 1.0) {
            x *= 0.01;
            if (x >= 1.0) {
                x *= 0.01;
                if (x > 1.0)
                    return 0.0001f / x; // we give up
                return 0.0001f * invTab[(int) (x * (Constants.invTabLen - 1) + 0.5)];
            }
            return 0.01f * invTab[(int) (x * (Constants.invTabLen - 1) + 0.5)];
        }
        return invTab[(int) (x * (Constants.invTabLen - 1) + 0.5)];
    }

    @SuppressWarnings("unused")
    private T linModColor(float fx, float fy, int a) {
        T modC;
        final float faktD = 1.0f, faktD2 = 0.7f; //!!! 0.7;
        modC = bigPixelColor[a].operatorPlus(bigPixelDX[a].operatorMultiply(fx)
                .operatorPlus(bigPixelDY[a].operatorMultiply(fy))
                .operatorMultiply(faktD));
        T modC2;
        modC2 = bigPixelD2X[a].operatorMultiply(fx * fx)
                .operatorPlus(bigPixelD2Y[a].operatorMultiply(fy * fy))
                .operatorMultiply(0.5f)
                .operatorPlus(bigPixelDXY[a].operatorMultiply(fx * fy));

        modC2 = modC2.operatorMultiplyEqual(faktD2);
        //float ff = (4.0 - fx*fx - fy*fy)*(1.0/4.0);
        //if(ff<0.0) ff=0.0;
        //ff = ff*ff + faktD2;
        //modC2 *= ff;

        modC = modC.operatorPlusEqual(modC2);
        modC.clip();
        return modC;
    }

    @SuppressWarnings("unused")
    private T linModColorB(float fx, float fy, int a) {
        T modC;
        final float faktD = 1.0f, faktD2 = 0.7f; //!!! 1.0 /  0.7;

        modC = bigPixelDX[a].operatorMultiply(fx).operatorPlus(bigPixelDY[a].operatorMultiply(fy));
        modC = modC.operatorPlusEqual(bigPixelD2X[a].operatorMultiply(0.5f * faktD2 * fx * fx)
                .operatorPlus(bigPixelD2Y[a].operatorMultiply(0.5f * faktD2 * fy * fy)));
        modC = modC.operatorPlusEqual(bigPixelDXY[a].operatorMultiply(faktD2 * fx * fy));
        return modC;
    }

    private void quadricCalc(float fx, float fy, int a, float deltaX, T quad, T quadD, T quadD2) {
        quad = bigPixelDX[a].operatorMultiply(fx).operatorPlus(bigPixelDY[a].operatorMultiply(fy));
        quad = quad.operatorPlusEqual(bigPixelD2X[a].operatorMultiply(0.35f * fx * fx)
                .operatorPlus(bigPixelD2Y[a].operatorMultiply(0.35f * fy * fy)));
        quad = quad.operatorPlusEqual(bigPixelDXY[a].operatorMultiply(0.7f * fx * fy));
        quad = quad.operatorMultiplyEqual(derivF);

        quadD = bigPixelDX[a].operatorMultiply(deltaX);
        quadD = quadD.operatorPlusEqual(bigPixelD2X[a].operatorMultiply(0.35f * deltaX * (2.0f * fx + deltaX)));
        quadD = quadD.operatorPlusEqual(bigPixelDXY[a].operatorMultiply(0.7f * deltaX * fy));
        quadD = quadD.operatorMultiplyEqual(derivF);

        quadD2 = bigPixelD2X[a].operatorMultiply(0.7f * derivF * deltaX * deltaX);
    }

    @SuppressWarnings("unused")
    private float modVal(float f) {
        if (f < 0.0)
            return 0.0f;
        return (float) (6.0 * Math.pow(f, 3.0));
    }

    private float modVal2(float f, float f2) {
        if (f > f2)
            f = f2;
        if (f < 0.0)
            return 0.0f;
        return (float) (6.0 * Math.pow(f, 2.0));
    }
}
