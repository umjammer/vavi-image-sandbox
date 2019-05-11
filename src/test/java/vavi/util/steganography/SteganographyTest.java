/*
 * Copyright (c) 2017 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.steganography;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

//import vavix.awt.image.resample.ZhoumxLanczosResample2Op;


/**
 * SteganographyTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 Nov 12, 2017 umjammer initial version <br>
 */
public class SteganographyTest {

    @Test
    public void test() throws IOException {
        final String string = "Copyright © 2017 by Naohide Sano, All rights reserved.";
        BufferedImage image1 = ImageIO.read(new File("tmp/P.jpg"));
        Steganography steganography = new Steganography(image1);
        BufferedImage steganImage = steganography.encode(string);
        File image2file = new File("tmp/stegan.png");
        ImageIO.write(steganImage, "PNG", image2file);
        BufferedImage image2 = ImageIO.read(image2file);
        Steganography steganography2 = new Steganography(image2);
        assertEquals(string, steganography2.decode());
//        BufferedImage image3 = new ZhoumxLanczosResample2Op(image2.getWidth() / 2, image2.getHeight() / 2).filter(image2, null);
//        Steganography steganography3 = new Steganography(image3);
//        assertNotEquals(string, steganography3.decode());
    }
}

/* */
