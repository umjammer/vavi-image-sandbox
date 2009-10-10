
package vavix.awt.image.resample;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;


/**
 * AreaAverage ‚æ‚è‚¿‚å‚Á‚Æƒ}ƒV
 * 
 * @see "http://mxsoftware20071114144427.javaeye.com/blog/251089"
 */
public class ZhoumxLanczosResample2Op implements BufferedImageOp {

    private int width;
    private int height;
    private int scaledWidth;
    private int scaledHeight;
    double support = 3.0;
    double[] contrib;
    double[] normContrib;
    double[] tmpContrib;
    int startContrib, stopContrib;
    int nDots;
    int nHalfDots;

    public ZhoumxLanczosResample2Op(int w, int h) {
        scaledWidth = w;
        scaledHeight = h;
    }

    /**
     * Start: Use Lanczos filter to replace the original algorithm for image
     * scaling. Lanczos improves quality of the scaled image modify by :blade
     */
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        width = src.getWidth();
        height = src.getHeight();

        getContrib();
        BufferedImage pbOut = getHorizontalFiltering(src, scaledWidth);
        BufferedImage pbFinalOut = getVerticalFiltering(pbOut, scaledHeight);
        return pbFinalOut;
    }

    /** */
    private static final double lanczos(int i, int inWidth, int outWidth, double Support) {
        double x = (double) i * (double) outWidth / inWidth;
        return Math.sin(x * Math.PI) / (x * Math.PI) * Math.sin(x * Math.PI / Support) / (x * Math.PI / Support);
    }

    /** 
     * Assumption: same horizontal and vertical scaling factor
     */ 
    private void getContrib() {
        nHalfDots = (int) (width * support / scaledWidth);
        nDots = nHalfDots * 2 + 1;
        try {
            contrib = new double[nDots];
            normContrib = new double[nDots];
            tmpContrib = new double[nDots];
        } catch (Exception e) {
            throw new IllegalStateException("init contrib, normContrib, tmpContrib: " + e);
        }

        int center = nHalfDots;
        contrib[center] = 1.0;

        double weight = 0.0;
        int i = 0;
        for (i = 1; i <= center; i++) {
            contrib[center + i] = lanczos(i, width, scaledWidth, support);
            weight += contrib[center + i];
        }

        for (i = center - 1; i >= 0; i--) {
            contrib[i] = contrib[center * 2 - i];
        }

        weight = weight * 2 + 1.0;

        for (i = 0; i <= center; i++) {
            normContrib[i] = contrib[i] / weight;
        }

        for (i = center + 1; i < nDots; i++) {
            normContrib[i] = normContrib[center * 2 - i];
        }
    }

    /** */ 
    private void getTempContrib(int start, int stop) {
        double weight = 0;

        int i = 0;
        for (i = start; i <= stop; i++) {
            weight += contrib[i];
        }

        for (i = start; i <= stop; i++) {
            tmpContrib[i] = contrib[i] / weight;
        }
    }

    /** */ 
    private static final int getRedValue(int rgbValue) {
        int temp = rgbValue & 0x00ff0000;
        return temp >> 16;
    }

    /** */ 
    private static final int getGreenValue(int rgbValue) {
        int temp = rgbValue & 0x0000ff00;
        return temp >> 8;
    }

    /** */ 
    private static final int getBlueValue(int rgbValue) {
        return rgbValue & 0x000000ff;
    }

    /** */ 
    private static final int getRGB(int redValue, int greenValue, int blueValue) {
        return (redValue << 16) + (greenValue << 8) + blueValue;
    }

    /** */ 
    private int filterHorizontal(BufferedImage bufImg, int startX, int stopX, int start, int stop, int y, double[] pContrib) {
        double valueRed = 0.0;
        double valueGreen = 0.0;
        double valueBlue = 0.0;
        int valueRGB = 0;
        int i, j;

        for (i = startX, j = start; i <= stopX; i++, j++) {
            valueRGB = bufImg.getRGB(i, y);

            valueRed += getRedValue(valueRGB) * pContrib[j];
            valueGreen += getGreenValue(valueRGB) * pContrib[j];
            valueBlue += getBlueValue(valueRGB) * pContrib[j];
        }

        valueRGB = getRGB(clip((int) valueRed), clip((int) valueGreen), clip((int) valueBlue));
        return valueRGB;
    }

    /** */ 
    private BufferedImage getHorizontalFiltering(BufferedImage bufImage, int iOutW) {
        int dwInW = bufImage.getWidth();
        int dwInH = bufImage.getHeight();
        int value = 0;
        BufferedImage pbOut = new BufferedImage(iOutW, dwInH, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < iOutW; x++) {

            int startX;
            int start;
            int X = (int) (((double) x) * ((double) dwInW) / iOutW + 0.5);
            int y = 0;

            startX = X - nHalfDots;
            if (startX < 0) {
                startX = 0;
                start = nHalfDots - X;
            } else {
                start = 0;
            }

            int stop;
            int stopX = X + nHalfDots;
            if (stopX > (dwInW - 1)) {
                stopX = dwInW - 1;
                stop = nHalfDots + (dwInW - 1 - X);
            } else {
                stop = nHalfDots * 2;
            }

            if (start > 0 || stop < nDots - 1) {
                getTempContrib(start, stop);
                for (y = 0; y < dwInH; y++) {
                    value = filterHorizontal(bufImage, startX, stopX, start, stop, y, tmpContrib);
                    pbOut.setRGB(x, y, value);
                }
            } else {
                for (y = 0; y < dwInH; y++) {
                    value = filterHorizontal(bufImage, startX, stopX, start, stop, y, normContrib);
                    pbOut.setRGB(x, y, value);
                }
            }
        }

        return pbOut;
    }

    /** */ 
    private int filterVertical(BufferedImage pbInImage, int startY, int stopY, int start, int stop, int x, double[] pContrib) {
        double valueRed = 0.0;
        double valueGreen = 0.0;
        double valueBlue = 0.0;
        int valueRGB = 0;
        int i, j;

        for (i = startY, j = start; i <= stopY; i++, j++) {
            valueRGB = pbInImage.getRGB(x, i);

            valueRed += getRedValue(valueRGB) * pContrib[j];
            valueGreen += getGreenValue(valueRGB) * pContrib[j];
            valueBlue += getBlueValue(valueRGB) * pContrib[j];
        }

        valueRGB = getRGB(clip((int) valueRed), clip((int) valueGreen), clip((int) valueBlue));
        return valueRGB;
    }

    /** */ 
    private BufferedImage getVerticalFiltering(BufferedImage pbImage, int iOutH) {
        int iW = pbImage.getWidth();
        int iH = pbImage.getHeight();
        int value = 0;
        BufferedImage pbOut = new BufferedImage(iW, iOutH, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < iOutH; y++) {

            int startY;
            int start;
            int Y = (int) (((double) y) * ((double) iH) / iOutH + 0.5);

            startY = Y - nHalfDots;
            if (startY < 0) {
                startY = 0;
                start = nHalfDots - Y;
            } else {
                start = 0;
            }

            int stop;
            int stopY = Y + nHalfDots;
            if (stopY > (iH - 1)) {
                stopY = iH - 1;
                stop = nHalfDots + (iH - 1 - Y);
            } else {
                stop = nHalfDots * 2;
            }

            if (start > 0 || stop < nDots - 1) {
                getTempContrib(start, stop);
                for (int x = 0; x < iW; x++) {
                    value = filterVertical(pbImage, startY, stopY, start, stop, x, tmpContrib);
                    pbOut.setRGB(x, y, value);
                }
            } else {
                for (int x = 0; x < iW; x++) {
                    value = filterVertical(pbImage, startY, stopY, start, stop, x, normContrib);
                    pbOut.setRGB(x, y, value);
                }
            }

        }

        return pbOut;
    }

    /** */ 
    private static final int clip(int x) {
        if (x < 0) {
            return 0;
        }
        if (x > 255) {
            return 255;
        }
        return x;
    }

    /* */
    public Rectangle2D getBounds2D(BufferedImage src) {
        return src.getRaster().getBounds();
    }

    /* */
    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM) {

        if (destCM == null) {
            destCM = src.getColorModel();
        }

        int width = scaledWidth;
        int height = scaledHeight;

        return new BufferedImage(destCM, destCM.createCompatibleWritableRaster(width, height), destCM.isAlphaPremultiplied(), null);
    }

    /* */
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Float();
        }
        dstPt.setLocation(srcPt.getX() * ((double) scaledWidth / width), srcPt.getY() * ((double) scaledHeight / height));
        return dstPt;
    }

    /* */
    public RenderingHints getRenderingHints() {
        return null;
    }
}
