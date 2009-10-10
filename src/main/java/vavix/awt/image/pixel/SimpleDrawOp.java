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
 * SimpleDrawOp.
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 060616 nsano initial version <br>
 */
public class SimpleDrawOp implements BufferedImageOp {

    /** draw source point x */
    private int dx;
    /** draw source point y */
    private int dy;
    /** destination width */
    private int dw;
    /** destination height */
    private int dh;

    /**
     * @param dx dst ‚É‘Î‚·‚é‘‚«o‚µ x
     * @param dy dst ‚É‘Î‚·‚é‘‚«o‚µ y
     * @param dw dst ‚Ì•
     * @param dh dst ‚Ì‚‚³
     */
    public SimpleDrawOp(int dx, int dy, int dw, int dh) {
        this.dx = dx;
        this.dy = dy;
        this.dw = dw;
        this.dh = dh;
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
        int w = Math.min(sw, dw);
        int h = Math.min(sh, dh);
        int x = Math.max(0, dx);
        int y = Math.max(0, dy);
        int[] pixels = src.getRaster().getPixels(dx < 0 ? Math.abs(dx) : 0, dy < 0 ? Math.abs(dy) : 0, w, h, (int[]) null);
        dst.getRaster().setPixels(x, y, w, h, pixels);
              
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
        return new Rectangle(0, 0, dw, dh);
    }

    /** */
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Double();
        }
        dstPt.setLocation(srcPt.getX() + dx, srcPt.getY() + dy);
        return dstPt;
    }

    /** TODO impl */
    public RenderingHints getRenderingHints() {
        return null;
    }
}

/* */
