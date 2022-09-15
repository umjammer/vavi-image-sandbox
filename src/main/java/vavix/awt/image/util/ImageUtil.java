/*
 * Copyright (c) 2018 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;


/**
 * ImageUtil.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2018/02/25 umjammer initial version <br>
 */
public final class ImageUtil {

    private ImageUtil() {
    }

    /** Gets suitable scale from screen width */
    public static double fitX(BufferedImage image, double xThreshold) {
        return fit(image, xThreshold, 1);
    }

    /** Gets suitable scale from screen height */
    public static double fitY(BufferedImage image, double yThreshold) {
        return fit(image, 1, yThreshold);
    }

    /**
     * Gets suitable scale from screen size.
     *
     * @param xThreshold staring to scale.
     * @param yThreshold staring to scale.
     */
    public static double fit(BufferedImage image, double xThreshold, double yThreshold) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return fit(image,
                new Dimension(
                        (int) (screenSize.getWidth() * xThreshold),
                        (int) (screenSize.getHeight() * yThreshold)));
    }

    /** Gets suitable scale from size */
    public static double fit(BufferedImage image, Dimension size) {
        int w = size.width;
        int h = size.height;
        int iw = image.getWidth();
        int ih = image.getHeight();
        double sw = 1;
        double sh = 1;
        double s;
        if (iw > w || ih > h) {
            if (iw > w) {
                sw = w / (double) iw;
            }
            if (ih * sw > h) {
                sh = h / (double) ih;
            }
            s = Math.min(sw, sh);
        } else {
            if (w > iw) {
                sw = (double) iw / w;
            }
            if (ih * sw < h) {
                sh = (double) ih / h;
            }
            s = 1 / Math.max(sw, sh);
        }
        return s;
    }

    /**
     * Scales a image.
     */
    public static BufferedImage scale(BufferedImage image, double scale) {
//System.err.println(scale);
        if (scale == 1) {
            return clone(image);
        }
        ResampleOp filter = new ResampleOp((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
        filter.setFilter(ResampleFilters.getLanczos3Filter());
        return filter.filter(image, null);
    }

    /**
     * Clones a image.
     */
    public static BufferedImage clone(BufferedImage image) {
        ColorModel cm = image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}

/* */
