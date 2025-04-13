/*
 * Copyright (c) 2009 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.quantization;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.IOException;


/**
 * FixedColorModelQuantizerOp.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2009/06/02 nsano initial version <br>
 */
public class FixedColorModelQuantizeOp implements BufferedImageOp {

    /** */
    private IndexColorModel indexColorModel;

    /** */
    public FixedColorModelQuantizeOp(IndexColorModel indexColorModel) {
        this.indexColorModel = indexColorModel;
    }

    /* */
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        try {
            FixedColorModelQuantizer quantizer = new FixedColorModelQuantizer(src, src.getWidth(), src.getHeight(), indexColorModel);

            if (dest == null) {
                dest = createCompatibleDestImage(src, indexColorModel);
                //dest = createCompatibleDestImage(src, src.getColorModel());
            }

            int width = src.getWidth();
            int height = src.getHeight();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    dest.setRGB(x, y, quantizer.convert(src.getRGB(x, y)));
                }
            }
            return dest;

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /* */
    public Rectangle2D getBounds2D(BufferedImage src) {
        return src.getRaster().getBounds();
    }

    /* */
    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM) {

        if (destCM == null) {
            destCM = src.getColorModel();
        }

        int width = src.getWidth();
        int height = src.getHeight();

        return new BufferedImage(destCM, destCM.createCompatibleWritableRaster(width, height), destCM.isAlphaPremultiplied(), null);
    }

    /* */
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Float();
        }
        dstPt.setLocation(srcPt.getX(), srcPt.getY());
        return dstPt;
    }

    /* */
    public RenderingHints getRenderingHints() {
        return null;
    }
}

