/*
 * Copyright (c) 2017 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.imageio.rococoa;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * RococoaImageReaderSpiTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2017/11/17 umjammer initial version <br>
 */
public class RococoaImageReaderSpiTest {

    @Test
    public void test() throws Exception {
        BufferedImage image = ImageIO.read(new File("tmp/summer_1440x960.heic"));
        assertNotNull(image);
    }

}

/* */
