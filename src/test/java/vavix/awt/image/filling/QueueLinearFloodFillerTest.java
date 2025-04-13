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

import org.junit.jupiter.api.Disabled;

import vavi.imageio.IIOUtil;


/**
 * QueueLinearFloodFillerTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2011/10/29 umjammer initial version <br>
 */
@Disabled
public class QueueLinearFloodFillerTest {

    static {
//        IIOUtil.setOrder(ImageReaderSpi.class, "com.sixlegs.png.iio.PngImageReaderSpi", "com.sun.imageio.plugins.png.PNGImageReaderSpi");
        IIOUtil.deregister(ImageReaderSpi.class, "com.sun.imageio.plugins.png.PNGImageReaderSpi");
    }

    public static void main(String[] args) throws Exception {

        BufferedImage image = ImageIO.read(new File(args[0]));

        QueueLinearFloodFiller filler = new QueueLinearFloodFiller(image);
        filler.setFillColor(Color.red);
        filler.setTolerance(25);
        filler.floodFill(400, 400);

        BufferedImage newImage = filler.getImage();

        JPanel panel = new JPanel() {
            public void paint(Graphics g) {
                g.drawImage(newImage, 0, 0, this);
            }
        };
        panel.setPreferredSize(new Dimension(newImage.getWidth(), newImage.getHeight()));

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
