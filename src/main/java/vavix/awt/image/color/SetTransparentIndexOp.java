/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.color;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;


/**
 * これもそう、これくらい持っとけよ。
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 060616 nsano initial version <br>
 */
public class SetTransparentIndexOp implements BufferedImageOp {

    /** 透明色 */
    private int r, g, b;

    /**
     * TODO hints
     */
    public SetTransparentIndexOp(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    /**
     * @param src should be indexed color model
     * @param dst currently, should not be set
     * @throws IllegalArgumentException src is not indexed color model image
     * @throws IllegalArgumentException src has not transparent pixels specified at constructor
     * @throws IllegalArgumentException TODO dst is not null
     */
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        if (!(src.getColorModel() instanceof IndexColorModel)) {
            throw new IllegalArgumentException("not indexed color model image");
        }

        if (dst != null) { // TODO implement
            throw new IllegalArgumentException("not implemented yet");
        }

        IndexColorModel srcICM = (IndexColorModel) src.getColorModel();
        WritableRaster raster = src.getRaster();
        int size = srcICM.getMapSize();
        byte[] reds = new byte[size];
        byte[] greens = new byte[size];
        byte[] blues = new byte[size];
        srcICM.getReds(reds);
        srcICM.getGreens(greens);
        srcICM.getBlues(blues);
        int pixel = -1;
        for (int i = 0; i < size; i++) {
            if ((reds[i] & 0xff) == r && (greens[i] & 0xff) == g && (blues[i] & 0xff) == b) {
        //        System.err.printf("PIXEL[%3d]: R %02x G %02x B %02x\n", i, reds[i], greens[i], blues[i]);
                pixel = i;
            }
        }
        if (pixel == -1) {
            throw new IllegalArgumentException("src has not transparent pixels");
        }
        IndexColorModel newIcm = new IndexColorModel(srcICM.getPixelSize(), size, reds, greens, blues, pixel);
        return new BufferedImage(newIcm, raster, src.isAlphaPremultiplied(), null);
    }

    /**
     * @param destCM when null, used src color model
     */
    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM) {
        Rectangle destBounds = (Rectangle) getBounds2D(src);
        if (destCM != null) {
            return new BufferedImage(destCM, destCM.createCompatibleWritableRaster(destBounds.width, destBounds.height), destCM.isAlphaPremultiplied(), null);
        } else {
            return new BufferedImage(destBounds.width, destBounds.height, src.getType());
        }
    }

    /** */
    public Rectangle2D getBounds2D(BufferedImage src) {
        return new Rectangle(0, 0, src.getWidth(), src.getHeight());
    }

    /** */
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Double();
        }
        dstPt.setLocation(srcPt.getX(), srcPt.getY());
        return dstPt;
    }

    /** TODO impl */
    public RenderingHints getRenderingHints() {
        return null;
    }
}
