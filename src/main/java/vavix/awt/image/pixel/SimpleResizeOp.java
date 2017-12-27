/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.pixel;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;


/**
 * SimpleResizeOp (moving pixels only).
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 060616 nsano initial version <br>
 */
public class SimpleResizeOp implements BufferedImageOp {

    /** */
    private float sx;
    /** */
    private float sy;

    /**
     * TODO hints
     */
    public SimpleResizeOp(float sx, float sy) {
        this.sx = sx;
        this.sy = sy;
    }

    /**
     * @param dst when null, created by {@link #createCompatibleDestImage(BufferedImage, ColorModel)}
     */
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        if (dst == null) {
            dst = createCompatibleDestImage(src, src.getColorModel());
        }

        for (int y = 0; y < dst.getHeight(); y++) {
            for (int x = 0; x < dst.getWidth(); x++) {
                dst.setRGB(x, y, src.getRGB((int) (x / sx), (int) (y / sy)));
            }
        }

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
        return new Rectangle(0, 0, (int) (src.getWidth() * sx), (int) (src.getHeight() * sy));
    }

    /** */
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Double();
        }
        dstPt.setLocation(srcPt.getX() * sx, srcPt.getY() * sy);
        return dstPt;
    }

    /** TODO impl */
    public RenderingHints getRenderingHints() {
        return null;
    }
}

/* */
