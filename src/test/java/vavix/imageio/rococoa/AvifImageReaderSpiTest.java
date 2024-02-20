/*
 * Copyright (c) 2023 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.imageio.rococoa;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileCacheImageInputStream;

import org.junit.jupiter.api.Test;
import org.rococoa.cocoa.appkit.NSImage;
import org.rococoa.cocoa.foundation.NSData;
import vavi.imageio.IIOUtil;
import vavi.util.Debug;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * AvifImageReaderSpiTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2023/10/09 umjammer initial version <br>
 */
public class AvifImageReaderSpiTest {

    @Test
    public void test0() throws Exception {
        assertTrue(Arrays.asList(ImageIO.getReaderFormatNames()).contains("avif"));
        assertFalse(Arrays.asList(ImageIO.getWriterFormatNames()).contains("avif"));
        assertInstanceOf(RococoaImageReader.class, IIOUtil.getImageReader("AVIF", ImageReader.class.getName()));
    }

    @Test
    public void test11() throws Exception {
        BufferedImage image = ImageIO.read(new File("src/test/resources/kimono.avif"));
Debug.println(image);
        assertNotNull(image);
        assertEquals(722, image.getWidth());
        assertEquals(1024, image.getHeight());
    }

    @Test
    public void test() throws Exception {
        BufferedImage image = ImageIO.read(AvifImageReaderSpiTest.class.getResourceAsStream("/kimono.avif"));
Debug.println(image);
        assertNotNull(image);
        assertEquals(722, image.getWidth());
        assertEquals(1024, image.getHeight());
    }

    @Test
    public void test2() throws Exception {
        ImageReader ir = IIOUtil.getImageReader("AVIF", ImageReader.class.getName());
        ir.setInput(new FileCacheImageInputStream(AvifImageReaderSpiTest.class.getResourceAsStream("/kimono.avif"), null));
        BufferedImage image = ir.read(0);
Debug.println(image);
        assertNotNull(image);
        assertEquals(722, image.getWidth());
        assertEquals(1024, image.getHeight());
    }


    @Test
    public void test3() throws Exception {
        NSImage nsImage = NSImage.imageWithContentsOfFile("src/test/resources/kimono.avif");
        NSData data = nsImage.TIFFRepresentation();
        ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());

        BufferedImage image = ImageIO.read(bais);
Debug.println(image);
        assertNotNull(image);
        assertEquals(722, image.getWidth());
        assertEquals(1024, image.getHeight());
    }
}

/* */
