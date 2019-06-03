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


/**
 * CreateMaskIndexOp.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 060616 nsano initial version <br>
 */
public class CreateMaskIndexOp implements BufferedImageOp {

    /**
     * マスクは今のところ {@link #argb} が #ffffff で、そうでないところが #000000 固定
     * @param dst when null, created by {@link #createCompatibleDestImage(BufferedImage, ColorModel)}
     */
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        if (!IndexColorModel.class.isInstance(src.getColorModel())) {
            throw new IllegalArgumentException("not indexed color model image");
        }

        if (dst == null) {
            dst = createCompatibleDestImage(src, src.getColorModel());
        }

        IndexColorModel icm = (IndexColorModel) src.getColorModel();
        int trans = icm.getTransparentPixel();
        if (trans == -1) {
            throw new IllegalArgumentException("src has not transparent pixels");
        }

        int sw = src.getWidth();
        int sh = src.getHeight();
        int[] srcPixels = src.getRaster().getPixels(0, 0, sw, sh, (int[]) null);
        int[] dstPixels = new int[srcPixels.length];
        for (int y = 0; y < sh; y++) {
            for (int x = 0; x < sw; x++) {
                int i = y * sw + x;
//System.err.printf("%d, %d : %02x, %02x\n", x, y, srcPixels[i], trans);
                dstPixels[i] = srcPixels[i] == trans ? 1 : 0;
            }
        }
        dst.getRaster().setPixels(0, 0, sw, sh, dstPixels);

        return dst;
    }

    /**
     * @return always return B&W color model
     */
    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM) {
        Rectangle destBounds = (Rectangle) getBounds2D(src);
        return new BufferedImage(destBounds.width, destBounds.height, BufferedImage.TYPE_BYTE_BINARY);
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
