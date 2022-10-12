/*
 * Copyright (c) 2009 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.quantization;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.IndexColorModel;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.io.OutputStream;


/**
 * FixedColorModelQuantizer.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2009/06/02 nsano initial version <br>
 */
public class FixedColorModelQuantizer {

    /** no. of learning cycles */
    public static final int nCycles = 100;

    /** number of colours used */
    public int netSize = 256;

    /** number of reserved colours used */
    public static final int specials = 3;

    /** reserved background colour */
    public static final int bgColour = specials - 1;

    public int cutNetSize;

    public int maxNetPos;

    /** for 256 cols, radius starts at 32 */
    public int initRad;

    public static final int radiusBiasShift = 6;

    public static final int radiusBias = 1 << radiusBiasShift;

    public int initBiasRadius;

    /** factor of 1/30 each cycle */
    public static final int radiusDec = 30;

    /** alpha starts at 1 */
    public static final int alphaBiasShift = 10;

    /** biased by 10 bits */
    public static final int initAlpha = 1 << alphaBiasShift;
    public static final double gamma = 1024.0;
    public static final double beta = 1.0 / 1024.0;
    public static final double betaGamma = beta * gamma;

    /** the network itself */
    private double[][] network;

    /** the network itself */
    protected int[][] colorMap;

    /** for network lookup - really 256 */
    private int[] netIndex;

    /** bias and freq arrays for learning */
    private double[] bias;

    private double[] freq;

    // four primes near 500 - assume no image has a length so large
    // that it is divisible by all four primes

    public static final int prime1 = 499;
    public static final int prime2 = 491;
    public static final int prime3 = 487;
    public static final int prime4 = 503;

    public static final int maxPrime = prime4;

    protected int[] pixels = null;

    private IndexColorModel indexColorModel;

    /** */
    public FixedColorModelQuantizer(Image image, int w, int h, IndexColorModel indexColorModel) throws IOException {
        init0();
        setPixels(image, w, h);
        setUpArrays();
        this.indexColorModel = indexColorModel;
        this.netSize = indexColorModel.getMapSize();
        init();
    }

    /** */
    public int getColorCount() {
        return netSize;
    }

    /** */
    public Color getColor(int i) {
        if (i < 0 || i >= netSize) {
            return null;
        }
        int bb = colorMap[i][0];
        int gg = colorMap[i][1];
        int rr = colorMap[i][2];
        return new Color(rr, gg, bb);
    }

    /** */
    public int writeColourMap(boolean rgb, OutputStream out) throws IOException {
        for (int i = 0; i < netSize; i++) {
            int bb = colorMap[i][0];
            int gg = colorMap[i][1];
            int rr = colorMap[i][2];
            out.write(rgb ? rr : bb);
            out.write(gg);
            out.write(rgb ? bb : rr);
        }
        return netSize;
    }

    /** */
    protected void setUpArrays() {
        network[0][0] = 0.0; // black
        network[0][1] = 0.0;
        network[0][2] = 0.0;

        network[1][0] = 1.0; // white
        network[1][1] = 1.0;
        network[1][2] = 1.0;

        // RESERVED bgColour // background

        for (int i = 0; i < specials; i++) {
            freq[i] = 1.0 / netSize;
            bias[i] = 0.0;
        }

        for (int i = specials; i < netSize; i++) {
            double[] p = network[i];
            p[0] = (256.0 * (i - specials)) / cutNetSize;
            p[1] = (256.0 * (i - specials)) / cutNetSize;
            p[2] = (256.0 * (i - specials)) / cutNetSize;

            freq[i] = 1.0 / netSize;
            bias[i] = 0.0;
        }
    }

    /** */
    private void setPixels(Image image, int w, int h) throws IOException {
        if (w * h < maxPrime) {
            throw new IllegalArgumentException("Image is too small");
        }
        pixels = new int[w * h];
        PixelGrabber pg = new PixelGrabber(image, 0, 0, w, h, pixels, 0, w);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }
        if ((pg.getStatus() & java.awt.image.ImageObserver.ABORT) != 0) {
            throw new IOException("Image pixel grab aborted or errored");
        }
    }

    /** */
    private void init() {
        byte[] as = new byte[netSize];
        byte[] rs = new byte[netSize];
        byte[] gs = new byte[netSize];
        byte[] bs = new byte[netSize];
        indexColorModel.getAlphas(as);
        indexColorModel.getReds(rs);
        indexColorModel.getGreens(gs);
        indexColorModel.getBlues(bs);
        for (int i = 0; i < netSize; i++) {
            colorMap[i][0] = bs[i] & 0xff;
            colorMap[i][1] = gs[i] & 0xff;
            colorMap[i][2] = rs[i] & 0xff;
            colorMap[i][3] = as[i] & 0xff;
        }
        buildIndex();
    }

    /** */
    private void init0() {
        this.cutNetSize = netSize - specials;
        this.maxNetPos = netSize - 1;
        this.initRad = netSize / 8;
        this.initBiasRadius = initRad * radiusBias;
        this.network = new double[netSize][3];
        this.colorMap = new int[netSize][4];
        this.netIndex = new int[netSize];
        this.bias = new double[netSize];
        this.freq = new double[netSize];
    }

    /** Insertion sort of network and building of netindex[0..255] */
    private void buildIndex() {
        int previousCol = 0;
        int startPos = 0;

        for (int i = 0; i < netSize; i++) {
            int[] p = colorMap[i];
            int[] q = null;
            int smallPos = i;
            int smallVal = p[1]; // index on g
            // find smallest in i..netsize-1
            for (int j = i + 1; j < netSize; j++) {
                q = colorMap[j];
                if (q[1] < smallVal) { // index on g
                    smallPos = j;
                    smallVal = q[1]; // index on g
                }
            }
            q = colorMap[smallPos];
            // swap p (i) and q (smallpos) entries
            if (i != smallPos) {
                int j = q[0];
                q[0] = p[0];
                p[0] = j;
                j = q[1];
                q[1] = p[1];
                p[1] = j;
                j = q[2];
                q[2] = p[2];
                p[2] = j;
                j = q[3];
                q[3] = p[3];
                p[3] = j;
            }
            // smallval entry is now in position i
            if (smallVal != previousCol) {
                netIndex[previousCol] = (startPos + i) >> 1;
                for (int j = previousCol + 1; j < smallVal; j++) {
                    netIndex[j] = i;
                }
                previousCol = smallVal;
                startPos = i;
            }
        }
        netIndex[previousCol] = (startPos + maxNetPos) >> 1;
        for (int j = previousCol + 1; j < 256; j++) {
            netIndex[j] = maxNetPos; // really 256
        }
    }

    /** */
    public int convert(int pixel) {
        int alfa = (pixel >> 24) & 0xff;
        int r = (pixel >> 16) & 0xff;
        int g = (pixel >> 8) & 0xff;
        int b = (pixel) & 0xff;
        int i = searchIndex(b, g, r);
        int bb = colorMap[i][0];
        int gg = colorMap[i][1];
        int rr = colorMap[i][2];
        return (alfa << 24) | (rr << 16) | (gg << 8) | (bb);
    }

    /** */
    public int lookup(int pixel) {
        int r = (pixel >> 16) & 0xff;
        int g = (pixel >> 8) & 0xff;
        int b = (pixel) & 0xff;
        return searchIndex(b, g, r);
    }

    /** */
    public int lookup(Color c) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        return searchIndex(b, g, r);
    }

    /** */
    public int lookup(boolean rgb, int x, int g, int y) {
        return rgb ? searchIndex(y, g, x) : searchIndex(x, g, y);
    }

    /** Search for BGR values 0..255 and return colour index */
    protected int searchIndex(int b, int g, int r) {
        int bestD = 1000; // biggest possible dist is 256*3
        int best = -1;
        int i = netIndex[g]; // index on g
        int j = i - 1; // start at netindex[g] and work outwards

        while ((i < netSize) || (j >= 0)) {
            if (i < netSize) {
                int[] p = colorMap[i];
                int dist = p[1] - g; // inx key
                if (dist >= bestD) {
                    i = netSize; // stop iter
                } else {
                    if (dist < 0) {
                        dist = -dist;
                    }
                    int a = p[0] - b;
                    if (a < 0) {
                        a = -a;
                    }
                    dist += a;
                    if (dist < bestD) {
                        a = p[2] - r;
                        if (a < 0) {
                            a = -a;
                        }
                        dist += a;
                        if (dist < bestD) {
                            bestD = dist;
                            best = i;
                        }
                    }
                    i++;
                }
            }
            if (j >= 0) {
                int[] p = colorMap[j];
                int dist = g - p[1]; // inx key - reverse dif
                if (dist >= bestD) {
                    j = -1; // stop iter
                } else {
                    if (dist < 0) {
                        dist = -dist;
                    }
                    int a = p[0] - b;
                    if (a < 0) {
                        a = -a;
                    }
                    dist += a;
                    if (dist < bestD) {
                        a = p[2] - r;
                        if (a < 0) {
                            a = -a;
                        }
                        dist += a;
                        if (dist < bestD) {
                            bestD = dist;
                            best = j;
                        }
                    }
                    j--;
                }
            }
        }

        return best;
    }
}

/* */
