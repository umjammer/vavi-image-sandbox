/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.color;
  
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

import vavi.imageio.ImageConverter;


/**
 * ColorCurveOpTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2012/02/07 umjammer initial version <br>
 * @see "http://poolytooth.blog.fc2.com/blog-entry-2.html"
 */
public class ColorCurveOpTest {

    /**
     *
     * @see "http://www1.axfc.net/uploader/File/so/File_56252.zip" 疑似四色刷りファイル
     */
    @Test
    public void test() throws Exception {
        BufferedImage image = ImageIO.read(ColorCurveOpTest.class.getResourceAsStream("/bw.jpg"));
        ImageConverter ic = ImageConverter.getInstance();
        ic.setColorModelType(BufferedImage.TYPE_INT_RGB);
        BufferedImage src = ic.toBufferedImage(image);
//        ImageIO.write(src, "JPG", new File("tmp/outRGB.jpg"));

        InputStream is = ColorCurveOpTest.class.getResourceAsStream("/c4.cur");
        ColorCurveOp.Curves curves = new ColorCurveOp.GimpCurvesFactory().getCurves(is);
        is.close();

        ColorCurveOp filter = new ColorCurveOp(curves);
        BufferedImage dst = filter.filter(src, null);

        ImageIO.write(dst, "JPG", new File("tmp/out4color.jpg"));
    }
}

/* */
