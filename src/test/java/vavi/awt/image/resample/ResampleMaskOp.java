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


/**
 * ResampleMaskOp.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 060616 nsano initial version <br>
 */
public class ResampleMaskOp implements BufferedImageOp {

    /** */
    private float sx;
    /** */
    private float sy;

    /**
     * TODO hints
     */
    public ResampleMaskOp(float sx, float sy) {
        this.sx = sx;
        this.sy = sy;
    }

    /**
     * @param dst when null, created by {@link #createCompatibleDestImage(BufferedImage, ColorModel)}
     */
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        if (src.getType() != BufferedImage.TYPE_BYTE_BINARY) { // TODO check colors 2
            throw new IllegalArgumentException("not B&W color model image");
        }

        if (dst == null) {
            dst = createCompatibleDestImage(src, src.getColorModel());
        }

        Rectangle destBounds = (Rectangle) getBounds2D(src);
        BufferedImage tempImage = new BufferedImage(destBounds.width, destBounds.height, BufferedImage.TYPE_INT_ARGB);
        BufferedImage filteredImage = new AwtResampleOp(sx, sy).filter(src, tempImage);
        tempImage.flush();
//JOptionPane.showMessageDialog(null, new ImageIcon(filteredImage), "mask", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(src));
//System.err.println("colorModel: " + filteredImage.getColorModel());

        int dw = dst.getWidth();
        int dh = dst.getHeight();
        int[] pixels = filteredImage.getRGB(0, 0, dw, dh, null, 0, dw);
        int[] dstPixels = new int[pixels.length];
        for (int y = 0; y < dh; y++) {
            for (int x = 0; x < dw; x++) {
                int i = y * dw + x;
//System.err.printf("%08x, %08x \n", pixels[i], filteredImage.getRGB(x, y));
                dstPixels[i] = pixels[i] == 0xffffffff ? 1 : 0;
            }
        }
        dst.getRaster().setPixels(0, 0, dw, dh, dstPixels);
//JOptionPane.showMessageDialog(null, new ImageIcon(dst), "mask", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(filteredImage));
        filteredImage.flush();

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
