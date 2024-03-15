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
 * Java のバカがイメージ切り抜きのまともなのを持っていないから...
 *
 * <ul>
 * <li> {@link BufferedImage#getSubimage(int, int, int, int)} は見かけだけ</li>
 * <li> {@link java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int, int, int, int, int, java.awt.image.ImageObserver)}
 *      はリサイズじゃないときにもリサイズのオペレーションが入るし</li>
 * <li> ImageCropFilter は Toolkit で Image にして draw すると、やっぱり Graphics#draw がカラー変換かけやがる</li>
 * </ul>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 060616 nsano initial version <br>
 */
public class SimpleCropOp implements BufferedImageOp {

    /** */
    private int sx;
    /** */
    private int sy;
    /** */
    private int sw;
    /** */
    private int sh;


    /**
     * TODO hints
     * @param sx src に対する切り取る x
     * @param sy src に対する切り取る y
     * @param sw 切り取る幅
     * @param sh 切り取る高さ
     */
    public SimpleCropOp(int sx, int sy, int sw, int sh) {
        this.sx = sx;
        this.sy = sy;
        this.sw = sw;
        this.sh = sh;
//System.err.printf("1: %d, %d, %d, %d\n", sx, sy, sw, sh);
    }

    /**
     * @param dst when null, created by {@link #createCompatibleDestImage(BufferedImage, ColorModel)}
     */
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
//System.err.printf("2: %d, %d\n", src.getWidth(), src.getHeight());
        if (dst == null) {
            dst = createCompatibleDestImage(src, src.getColorModel());
        }
        int w = Math.min(sw, src.getWidth());
        int h = Math.min(sh, src.getHeight());
        int x = Math.max(sx, 0);
        int y = Math.max(sy, 0);
//System.err.printf("3: %d, %d, %d, %d\n", x, y, w, h);
        int[] pixels = src.getRaster().getPixels(x, y, w, h, (int[]) null);
        dst.getRaster().setPixels(sx < 0 ? Math.abs(sx) : 0, sy < 0 ? Math.abs(sy) : 0, w, h, pixels);

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
        return new Rectangle(sx, sy, sw, sh);
    }

    /** */
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Double();
        }
        dstPt.setLocation(srcPt.getX() + sx, srcPt.getY() + sy);
        return dstPt;
    }

    /** TODO impl */
    public RenderingHints getRenderingHints() {
        return null;
    }
}
