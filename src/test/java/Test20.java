/*
 * Copyright (c) 2011 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import vavi.imageio.ImageConverter;

import vavix.awt.image.resample.enlarge.NoidsEnlargeOp;

import static vavix.util.DelayedWorker.later;


/**
 * mugen viewer.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2011/08/31 umjammer initial version <br>
 */
public class Test20 {

    static long time;

    static {
//        IIOUtil.setOrder(ImageReaderSpi.class, "com.sixlegs.png.iio.PngImageReaderSpi", "com.sun.imageio.plugins.png.PNGImageReaderSpi");
        time = Boolean.parseBoolean(System.getProperty("vavi.test", "false")) ? 10 * 1000 : 1000 * 1000;
    }

    /**
     * TODO png
     */
    @Test
    public void test01() throws Exception {
        final double scale = Math.PI * 2;

        BufferedImage originalImage = ImageIO.read(getClass().getResourceAsStream("/namacha02.jpg"));
        ImageConverter converter = ImageConverter.getInstance();
        converter.setColorModelType(BufferedImage.TYPE_INT_ARGB);
        BufferedImage image = converter.toBufferedImage(originalImage);

        NoidsEnlargeOp filter = new NoidsEnlargeOp(scale, scale);
        BufferedImage actualImage = filter.filter(image, null);

        final BufferedImage expectedImage = ImageIO.read(getClass().getResourceAsStream("enlarged.png"));

        for (int y = 0; y < expectedImage.getHeight(); y++) {
            for (int x = 0; x < expectedImage.getWidth(); x++) {
                Assertions.assertEquals(expectedImage.getRGB(x, y), actualImage.getRGB(x, y));
            }
        }
    }

    @Test
    @EnabledIfSystemProperty(named = "vavi.test", matches = ".*")
    public void test0() throws Exception {
        main(new String[] {"src/test/resources/namacha02.jpg"});
        while (!later(time).come()) Thread.yield();
    }

    /**
     * @param args 0: input
     */
    public static void main(String[] args) throws Exception {
        String file = args[0];

        final double scale = Math.PI * 2;

        BufferedImage originalImage = ImageIO.read(new File(file));
        ImageConverter converter = ImageConverter.getInstance();
        converter.setColorModelType(BufferedImage.TYPE_INT_ARGB);
        BufferedImage image = converter.toBufferedImage(originalImage);

        NoidsEnlargeOp filter = new NoidsEnlargeOp(scale, scale);
        final BufferedImage filterdImage = filter.filter(image, null);

        JPanel panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                g.drawImage(filterdImage, 0, 0, null);
            }
        };
        panel.setPreferredSize(new Dimension(filterdImage.getWidth(), filterdImage.getHeight()));
ImageIO.write(filterdImage, "PNG", new File("tmp/enlarged.png"));
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new JScrollPane(panel));
        frame.pack();
        frame.setVisible(true);
    }
}

/* */
