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
 * MaskAsTransparentIndexOp.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 060616 nsano initial version <br>
 */
public class MaskAsTransparentIndexOp implements BufferedImageOp {

    /** マスク */
    private BufferedImage maskImage;

    /**
     * マスクは今のところ #ffffff が透明で、そうでないところが #000000 固定
     * TODO hints
     */
    public MaskAsTransparentIndexOp(BufferedImage maskImage) {
        if (maskImage.getType() != BufferedImage.TYPE_BYTE_BINARY) { // TODO check colors 2
            throw new IllegalArgumentException("not B&W color model image");
        }
        this.maskImage = maskImage;
    }

    /**
     * @param src should be indexed color model, and same size as {@link #maskImage}
     * @param dst when null, created by {@link #createCompatibleDestImage(BufferedImage, ColorModel)}
     * @throws IllegalArgumentException src is not indexed color model image
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

        int sw = src.getWidth();
        int sh = src.getHeight();
        int[] maskPixels = maskImage.getRaster().getPixels(0, 0, sw, sh, (int[]) null);
        int[] srcPixels = src.getRaster().getPixels(0, 0, sw, sh, (int[]) null);
        int[] dstPixels = new int[maskPixels.length];
        for (int y = 0; y < sh; y++) {
            for (int x = 0; x < sw; x++) {
                int i = y * sw + x;
//System.err.println(x + ", " + y + " : " + maskPixels[i]);
                dstPixels[i] = maskPixels[i] == 1 ? trans : srcPixels[i];
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
