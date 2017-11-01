/*
 * Copyright (c) 2011 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.pack.binpack;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderSpi;

import org.junit.Test;

import vavix.imageio.IIOUtil;
import vavix.util.grep.FileDigger;
import vavix.util.grep.RegexFileDigger;


/**
 * BinPackerTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2011/10/28 umjammer initial version <br>
 */
public class BinPackerTest {

    static {
        IIOUtil.setOrder(ImageReaderSpi.class, "com.sixlegs.png.iio.PngImageReaderSpi", "com.sun.imageio.plugins.png.PNGImageReaderSpi");
    }

    @Test
    public void test001() throws IOException {

        final List<BufferedImage> images = new ArrayList<>();
        final List<Dimension> rects = new ArrayList<>();

        new RegexFileDigger(new FileDigger.FileDredger() {
            public void dredge(File file) throws IOException {
                try {
//                    if (images.size() < 30) {
                        BufferedImage image = ImageIO.read(file);
                        rects.add(new Dimension(image.getWidth() + 1, image.getHeight() + 1));
//System.err.println("IN: " + images.size() + ": " + image.getWidth() + ", " + image.getHeight());
                        images.add(image);
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, Pattern.compile(".+\\.(png|PNG)")).dig(new File("tmp/textures"));

        BinPacker packer = new BinPacker();
        List<List<BinPacker.Rect>> packs = packer.pack(rects, 1024, true);

        BufferedImage image = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

int l = 0;
        int c = 0;
        for (List<BinPacker.Rect> pack : packs) {

            g2.setBackground(new Color(0, true));
            g2.clearRect(0, 0, 1024, 1024);

            for (BinPacker.Rect rect : pack) {

//System.err.printf("%d: %d, %d, %d, %d, %b\n", pack.getId(), pack.getX(), pack.getY(), pack.getWidth(), pack.getHeight(), pack.isRotated());
                AffineTransform tx = new AffineTransform();
                tx.setToRotation(rect.rotated ? Math.toRadians(90) : 0, rect.x, rect.y);
                g2.setTransform(tx);
                g2.drawImage(images.get(rect.id), rect.x, rect.y - (rect.rotated ? rect.width : 0), null);
                tx.setToRotation(0, rect.x, rect.y);
                g2.setTransform(tx);
                g2.setColor(Color.red);
                g2.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
            }

System.err.printf("out %d\n", c);
            ImageIO.write(image, "PNG", new File("tmp", "out_" + c++ + ".png"));
l += pack.size();
        }

System.err.println("packed: " + l + "/" + rects.size());
    }
}

/* */
