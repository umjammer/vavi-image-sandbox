/*
 * Copyright (c) 2017 by Naohide Sano, All rights reserved.
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
 * HeifImageReaderSpiTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2017/11/17 umjammer initial version <br>
 */
public class HeifImageReaderSpiTest {

    @Test
    public void test0() throws Exception {
        assertTrue(Arrays.asList(ImageIO.getReaderFormatNames()).contains("heif"));
        assertTrue(Arrays.asList(ImageIO.getReaderFormatNames()).contains("heic"));
        assertFalse(Arrays.asList(ImageIO.getWriterFormatNames()).contains("heif"));
        assertInstanceOf(RococoaImageReader.class, IIOUtil.getImageReader("HEIF", ImageReader.class.getName()));
        assertInstanceOf(RococoaImageReader.class, IIOUtil.getImageReader("HEIC", ImageReader.class.getName()));
    }

    @Test
    public void test11() throws Exception {
        BufferedImage image = ImageIO.read(new File("src/test/resources/sample1.heic"));
Debug.println(image);
        assertNotNull(image);
        assertEquals(1440, image.getWidth());
        assertEquals(960, image.getHeight());
    }

    @Test
    public void test() throws Exception {
        BufferedImage image = ImageIO.read(HeifImageReaderSpiTest.class.getResourceAsStream("/sample1.heic"));
Debug.println(image);
        assertNotNull(image);
        assertEquals(1440, image.getWidth());
        assertEquals(960, image.getHeight());
    }

    @Test
    public void test2() throws Exception {
        ImageReader ir = IIOUtil.getImageReader("HEIF", ImageReader.class.getName());
        ir.setInput(new FileCacheImageInputStream(HeifImageReaderSpiTest.class.getResourceAsStream("/sample1.heic"), null));
        BufferedImage image = ir.read(0);
Debug.println(image);
        assertNotNull(image);
        assertEquals(1440, image.getWidth());
        assertEquals(960, image.getHeight());
    }


    @Test
    public void test3() throws Exception {
        NSImage nsImage = NSImage.imageWithContentsOfFile("src/test/resources/sample1.heic");
        NSData data = nsImage.TIFFRepresentation();
        ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());

        BufferedImage image = ImageIO.read(bais);
Debug.println(image);
        assertNotNull(image);
        assertEquals(1440, image.getWidth());
        assertEquals(960, image.getHeight());
    }
}
