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
import java.awt.image.IndexColorModel;


/**
 * CropTransparentIndexOp.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 060616 nsano initial version <br>
 */
public class CropTransparentIndexOp implements BufferedImageOp {

    private Rectangle rectangle;

    /** */
    public CropTransparentIndexOp(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    /**
     * @param dst when null, created by {@link #createCompatibleDestImage(BufferedImage, ColorModel)}
     */
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        if (!(src.getColorModel() instanceof IndexColorModel)) {
            throw new IllegalArgumentException("not indexed color model image");
        }

        int w = src.getWidth();
        int h = src.getHeight();
        IndexColorModel icm = (IndexColorModel) src.getColorModel();
        int trans = icm.getTransparentPixel();
        if (trans == -1) {
//System.err.println("CTI: no trans");
            return getCopy(src, dst);
        } else {
            int[] srcData = src.getRaster().getPixels(0, 0, w, h, (int[]) null);
left:
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    int i = y * w + x;
                    if (srcData[i] != trans) {
                        break left;
                    }
                    rectangle.x = x;
                }
            }
top:
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int i = y * w + x;
                    if (srcData[i] != trans) {
                        break top;
                    }
                    rectangle.y = y;
                }
            }

            rectangle.width = w - rectangle.x;
width:
            for (int x = w - 1; x >= 0; x--) {
                for (int y = 0; y < h; y++) {
                    int i = y * w + x;
                    if (srcData[i] != trans) {
                        break width;
                    }
                    rectangle.width = x - rectangle.x + 1;
                }
            }

            rectangle.height = h - rectangle.y;
height:
            for (int y = h - 1; y >= 0; y--) {
                for (int x = 0; x < w; x++) {
                    int i = y * w + x;
                    if (srcData[i] != trans) {
                        break height;
                    }
                    rectangle.height = y - rectangle.y + 1;
                }
            }
//System.err.printf("CTI: %d, %d, %d, %d, %d, %d\n", rectangle.x, rectangle.y, rectangle.width, rectangle.height, w, h);

            if (rectangle.x < w && rectangle.y < h && rectangle.width > 0 && rectangle.height > 0) {
                if (dst == null) {
                    dst = createCompatibleDestImage(src, src.getColorModel());
                }

                return new SimpleCropOp(rectangle.x, rectangle.y, rectangle.width, rectangle.height).filter(src, dst);
            } else {
//System.err.printf("CTI: can not crop: %d, %d, %d, %d, %d, %d\n", rectangle.x, rectangle.y, rectangle.width, rectangle.height, w, h);
                return getCopy(src, dst);
            }
        }
    }

    /** */
    private BufferedImage getCopy(BufferedImage src, BufferedImage dst) {
        rectangle.x = 0;
        rectangle.y = 0;
        rectangle.width = src.getWidth();
        rectangle.height = src.getHeight();
        if (dst == null) {
            dst = createCompatibleDestImage(src, src.getColorModel());
        }
        src.copyData(dst.getRaster());
        return dst;
    }

    /**
     * @before rectangle
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

    /**
     * @before rectangle
     */
    public Rectangle2D getBounds2D(BufferedImage src) {
        return new Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    /**
     * @before rectangle
     */
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Double();
        }
        dstPt.setLocation(srcPt.getX() - rectangle.x, srcPt.getY() - rectangle.y);
        return dstPt;
    }

    /** TODO impl */
    public RenderingHints getRenderingHints() {
        return null;
    }
}
