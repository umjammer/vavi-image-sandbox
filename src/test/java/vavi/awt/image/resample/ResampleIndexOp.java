/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.awt.image.resample;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

import vavix.awt.image.color.CreateMaskIndexOp;
import vavix.awt.image.color.MaskAsTransparentIndexOp;


/**
 * ResampleIndexOp.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 060616 nsano initial version <br>
 */
public class ResampleIndexOp implements BufferedImageOp {

    /** */
    private float sx;
    /** */
    private float sy;

    /**
     * TODO hints
     */
    public ResampleIndexOp(float sx, float sy) {
        this.sx = sx;
        this.sy = sy;
    }

    /**
     * @param dst when null, created by {@link #createCompatibleDestImage(BufferedImage, ColorModel)}
     * @throws IllegalArgumentException src is not indexed color model image
     * @throws IllegalArgumentException TODO dst is not null
     */
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        if (!(src.getColorModel() instanceof IndexColorModel)) {
            throw new IllegalArgumentException("not indexed color model image");
        }

        if (dst != null) { // TODO implement
            throw new IllegalArgumentException("not implemented yet");
        }

        BufferedImage maskImage = new CreateMaskIndexOp().filter(src, null);
        BufferedImage tempImage = new ResampleMaskOp(sx, sy).filter(maskImage, null);
        maskImage.flush();
        maskImage = tempImage;

        BufferedImage resampledImage = new AwtResampleOp(sx, sy).filter(src, null);
        dst = new MaskAsTransparentIndexOp(maskImage).filter(resampledImage, null);
        resampledImage.flush();

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
