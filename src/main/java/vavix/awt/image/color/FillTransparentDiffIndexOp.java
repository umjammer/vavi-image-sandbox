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
 * DiffFillTransparentIndexOp.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 060616 nsano initial version <br>
 */
public class FillTransparentDiffIndexOp implements BufferedImageOp {

    private BufferedImage targetImage;

    /**
     * @param targetImage should be indexed color model
     * @throws IllegalArgumentException targetImage is not indexed color model image
     */
    public FillTransparentDiffIndexOp(BufferedImage targetImage) {
        if (!IndexColorModel.class.isInstance(targetImage.getColorModel())) {
            throw new IllegalArgumentException("not indexed color model image");
        }
        this.targetImage = targetImage;
    }

    /**
     * @param dst currently, should not be set
     * @throws IllegalArgumentException when src is not same size as targetImage
     */
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        dst = new FillTransparentIndexOp(src).filter(src, null);

        int sw = src.getWidth();
        int sh = src.getHeight();
//System.err.println(targetImage.getWidth() + ", " + targetImage.getHeight() + ", " + sw + ", " + sh);
        if (targetImage.getWidth() != sw || targetImage.getHeight() != sh) {
            throw new IllegalArgumentException("src is not same size as targetImage");
        }

        int[] targetRgbs = targetImage.getRGB(0, 0, sw, sh, (int[]) null, 0, sw);
        int[] srcRgbs = src.getRGB(0, 0, sw, sh, (int[]) null, 0, sw);
        int[] srcData = src.getRaster().getPixels(0, 0, sw, sh, (int[]) null);
        int[] dstData = dst.getRaster().getPixels(0, 0, sw, sh, (int[]) null);
        for (int y = 0; y < sh; y++) {
            for (int x = 0; x < sw; x++) {
                int i = y * sw + x;
                dstData[i] = srcRgbs[i] == targetRgbs[i] ? dstData[i] : srcData[i];
            }
        }
        dst.getRaster().setPixels(0, 0, sw, sh, dstData);

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
