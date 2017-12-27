/*
 * Copyright (c) 2009 by Naohide Sano, All rights reserved.
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
 * RotationOp.
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 060616 nsano initial version <br>
 */
public class RotationOp implements BufferedImageOp {

    public static final int ROTATE_LEFT_90 = -1;
    public static final int ROTATE_RIGHT_90 = 1;
    public static final int ROTATE_180 = 2;

    private int how;

    /** */
    public RotationOp(int how) {
        this.how = how;
    }

    /**
     * @param dst when null, created by {@link #createCompatibleDestImage(BufferedImage, ColorModel)} 
     */
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        if (dst == null) {
            dst = createCompatibleDestImage(src, src.getColorModel());
        }
        int sw = src.getWidth();
        int sh = src.getHeight();
        switch (how) {
        case ROTATE_RIGHT_90:
            for (int y = 0; y < sh; y++) {
                for (int x = 0; x < sw; x++) {
                    dst.setRGB(sh - y - 1, x, src.getRGB(x, y));
                }
            }
            break;
        case ROTATE_LEFT_90:
            for (int y = 0; y < sh; y++) {
                for (int x = 0; x < sw; x++) {
                    dst.setRGB(y, sw - x - 1, src.getRGB(x, y));
                }
            }
            break;
        case ROTATE_180:
            for (int y = 0; y < sh; y++) {
                for (int x = 0; x < sw; x++) {
                    dst.setRGB(sw - x - 1, sh - y - 1, src.getRGB(x, y));
                }
            }
            break;
        default:
            throw new IllegalArgumentException("how: " + how);
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
        switch (how) {
        case ROTATE_LEFT_90:
        case ROTATE_RIGHT_90:
            return new Rectangle(0, 0, src.getHeight(), src.getWidth());
        case ROTATE_180:
            return new Rectangle(0, 0, src.getWidth(), src.getHeight());
        default:
            throw new IllegalArgumentException("how: " + how);
        }
    }

    /** */
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Double();
        }
        dstPt.setLocation(srcPt.getY(), srcPt.getX());
        return dstPt;
    }

    /** TODO impl */
    public RenderingHints getRenderingHints() {
        return null;
    }
}

/* */
