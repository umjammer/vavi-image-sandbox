/*
 * Copyright (c) 2011 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.filling;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderSpi;
import javax.swing.JFrame;
import javax.swing.JPanel;

import vavix.imageio.IIOUtil;


/**
 * QueueLinearFloodFillerTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2011/10/29 umjammer initial version <br>
 */
public class QueueLinearFloodFillerTest {

    static {
//        IIOUtil.setOrder(ImageReaderSpi.class, "com.sixlegs.png.iio.PngImageReaderSpi", "com.sun.imageio.plugins.png.PNGImageReaderSpi");
        IIOUtil.deregister(ImageReaderSpi.class, "com.sun.imageio.plugins.png.PNGImageReaderSpi");
    }

    public static void main(String[] args) throws Exception {

        final BufferedImage image = ImageIO.read(new File(args[0]));

        QueueLinearFloodFiller filler = new QueueLinearFloodFiller(image);
        filler.setFillColour(Color.red);
        filler.setTolerance(new int[] { 0xf0, 0xf0, 0xf0 });
        filler.floodFill(400, 400);

        JPanel panel = new JPanel() {
            public void paint(Graphics g) {
                g.drawImage(image, 0, 0, this);
            }
        };
        panel.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}

/* */
