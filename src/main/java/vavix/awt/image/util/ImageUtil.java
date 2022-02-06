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

    /**
     * Gets suitable scale from screen size.
     *
     * @param threshold staring to scale.
     */
    public static double fit(BufferedImage image, double threshold) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (image.getHeight() > screenSize.getHeight() * threshold) {
            return screenSize.getHeight() * threshold / image.getHeight();
        } else {
            // TODO horizontal check
            return 1;
        }
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
