/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.awt.image.jpeg;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;


/**
 * JpegUtil.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 071116 nsano initial version <br>
 */
public class JpegUtil {

    /** */
    private static final float margin = 0.05f;

    /** minimum quality */
    private static final float MINIMUM_QUARITY = 0.01f;

    /* */
    static {
        ImageIO.setUseCache(false);
    }

    /** */
    private JpegUtil() {
    }

    /* */
    public static void writeMaxQualityImage(BufferedImage image, int equalizedSize, OutputStream os) throws IOException {
        //
        int size;

        float quality = 0.95f;
        float previousQuality = -1;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //
        ImageWriter iw = ImageIO.getImageWritersByFormatName("JPEG").next();
        while (true) {

            ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
            iw.setOutput(ios);

            ImageWriteParam iwp = iw.getDefaultWriteParam();
            ((JPEGImageWriteParam) iwp).setOptimizeHuffmanTables(true);
            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            iwp.setCompressionQuality(quality);
            iw.write(null, new IIOImage(image, null, null), iwp);

            ios.flush();
            ios.close();

            size = baos.size();

            if (quality < MINIMUM_QUARITY || new BigDecimal(previousQuality).setScale(3, RoundingMode.HALF_UP).equals(new BigDecimal(quality).setScale(3, RoundingMode.HALF_UP))) {
                break;
            }

            if (size >= equalizedSize * (1 - margin) && size <= equalizedSize) {
                break;
            }

            if (size < equalizedSize * (1 - margin)) {
                if (previousQuality == -1) {
                    break;
                }
                float backupQuality = quality;
                quality += Math.abs(previousQuality - quality) / 2;
                previousQuality = backupQuality;
            } else {
                if (previousQuality == -1) {
                    previousQuality = quality * 2;
                }
                float backupQuality = quality;
                quality -= Math.abs(previousQuality - quality) / 2;
                previousQuality = backupQuality;
            }

            baos.reset();
        }
//System.err.println("quality: " + quality + ", size: " + size);

        baos.writeTo(os);
    }
}
