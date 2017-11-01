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
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * PalettizeOp.
 * 
 * ex.
 * <pre>
 *  src = new PalettizeOp(256).filter(src, null);
 * </pre>
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 060616 nsano initial version <br>
 */
public class PalettizeOp implements BufferedImageOp {

    /** */
    private int maxEntries;

    /**
     * TODO hints
     */
    public PalettizeOp(int maxEntries) {
        this.maxEntries = maxEntries;
    }

    /**
     * Convert an image to a palletized one. Will not create a palette above a
     * fixed number of entries
     * 
     * @param src should be indexed color model
     * @param dst should not be set
     * @throws IllegalStateException when palette becomes too large
     */
    public BufferedImage filter(BufferedImage img, BufferedImage dst) {

        // Just because an image has a palette doesnt mean it has a good one
        // so we re-index even if its an IndexColorModel
        int addedCount = 0;
        Map<Integer, Integer> added = new LinkedHashMap<>();
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if (!added.containsKey(img.getRGB(x, y))) {
                    added.put(img.getRGB(x, y), addedCount++);
                }
                if (added.size() > maxEntries) {
                    throw new IllegalStateException("palette becomes too large");
                }
            }
        }
        int[] cmap = new int[added.size()];
        for (int c : added.keySet()) {
            cmap[added.get(c)] = c;
        }

        int bitCount = 1;
        while (added.size() >> bitCount != 0) {
            bitCount *= 2;
        }

        IndexColorModel icm = new IndexColorModel(bitCount, added.size(), cmap, 0, DataBuffer.TYPE_BYTE, null);

        // Check if generated palette matched original
        if (img.getColorModel() instanceof IndexColorModel) {
            IndexColorModel originalModel = (IndexColorModel) img.getColorModel();
            if (originalModel.getPixelSize() == icm.getPixelSize() && originalModel.getMapSize() == icm.getMapSize()) {
                // Old model already had efficient palette
                return null;
            }
        }

        // Be careful to assign correctly assign byte packing method based on
        // pixel size
        if (dst != null) { // TODO implement
            throw new IllegalArgumentException("not implemented yet");
        } else {
            dst = new BufferedImage(img.getWidth(), img.getHeight(), icm.getPixelSize() < 8 ? BufferedImage.TYPE_BYTE_BINARY : BufferedImage.TYPE_BYTE_INDEXED, icm);
        }

        WritableRaster wr = dst.getRaster();
        for (int y = 0; y < dst.getHeight(); y++) {
            for (int x = 0; x < dst.getWidth(); x++) {
                wr.setSample(x, y, 0, added.get(img.getRGB(x, y)));
            }
        }
        return dst;
    }

    /**
     * @param destCM when null, used src color model
     */
    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM) {
        Rectangle destBounds = (Rectangle) getBounds2D(src);
        return new BufferedImage((int) destBounds.getWidth(), (int) destBounds.getHeight(), destCM.getPixelSize() < 8 ? BufferedImage.TYPE_BYTE_BINARY : BufferedImage.TYPE_BYTE_INDEXED, IndexColorModel.class.cast(destCM));
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

/* */
