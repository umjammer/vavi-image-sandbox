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


/**
 * ReplaceColorOp.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 060616 nsano initial version <br>
 */
public class ReplaceColorOp implements BufferedImageOp {

    /** */
    private int srcColor;

    /** */
    private int dstColor;

    /**
     * TODO hints
     */
    public ReplaceColorOp(int srcColor, int dstColor) {
        this.srcColor = srcColor & 0x00ffffff;
        this.dstColor = dstColor & 0x00ffffff;
    }

    /**
     * @param src should be indexed color model
     * @param dst when null, created by {@link #createCompatibleDestImage(BufferedImage, ColorModel)}
     */
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        if (dst == null) {
            dst = createCompatibleDestImage(src, src.getColorModel());
        }

        int sw = src.getWidth();
        int sh = src.getHeight();
        int[] srcRgbs = src.getRGB(0, 0, sw, sh, null, 0, sw);
        int[] dstRgbs = new int[srcRgbs.length];
        for (int y = 0; y < sh; y++) {
            for (int x = 0; x < sw; x++) {
                int i = y * sw + x;
//System.err.printf("%08x\n", srcRgbs[i]);
                dstRgbs[i] = (srcRgbs[i] & 0x00ffffff) == srcColor ? (dstColor | (srcRgbs[i] & 0xff000000)) : srcRgbs[i];
            }
        }
        dst.setRGB(0, 0, sw, sh, dstRgbs, 0, sw);

        return dst;
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

/* */
