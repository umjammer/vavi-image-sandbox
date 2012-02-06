/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.color;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.junit.Test;

import vavi.imageio.ImageConverter;


/**
 * ColorCurveOpTest. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2012/02/07 umjammer initial version <br>
 */
public class ColorCurveOpTest {

    @Test
    public void test() throws Exception {
        BufferedImage image = ImageIO.read(new File("tmp/inputBW2.jpg"));
        ImageConverter ic = ImageConverter.getInstance();
        ic.setColorModelType(BufferedImage.TYPE_INT_RGB);
        BufferedImage src = ic.toBufferedImage(image);
//        ImageIO.write(src, "JPG", new File("tmp/outRGB.jpg"));
        ColorCurveOp filter = new ColorCurveOp("tmp/疑似四色刷り/疑似四色刷り旧形式アルファ補正付き.cur", false);
        BufferedImage dst = filter.filter(src, null);
        ImageIO.write(dst, "JPG", new File("tmp/out4color2.jpg"));
    }
}

/* */
