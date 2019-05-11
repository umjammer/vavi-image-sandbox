
/*
 * Copyright (c) 2018 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 * Test31.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2018/02/03 umjammer initial version <br>
 * @see "https://stackoverflow.com/questions/12598818/finding-a-picture-in-a-picture-with-java"
 */
public class Test31 {

    /**
     * Finds the a region in one image that best matches another, smaller,
     * image.
     */
    public static int[] findSubimage(BufferedImage im1, BufferedImage im2) {
        int w1 = im1.getWidth();
        int h1 = im1.getHeight();
        int w2 = im2.getWidth();
        int h2 = im2.getHeight();
        assert (w2 <= w1 && h2 <= h1);
        // will keep track of best position found
        int bestX = -1;
        int bestY = -1;
        double lowestDiff = Double.POSITIVE_INFINITY;
        // brute-force search through whole image (slow...)
        for (int x = 0; x < w1 - w2; x++) {
            for (int y = 0; y < h1 - h2; y++) {
                double comp = compareImages(im1.getSubimage(x, y, w2, h2), im2);
                if (comp < lowestDiff) {
                    bestX = x;
                    bestY = y;
                    lowestDiff = comp;
                }
            }
            System.err.print(".");
        }
        System.err.println();
        // output similarity measure from 0 to 1, with 0 being identical
        System.err.println(lowestDiff);
        // return best location
        return new int[] {
            bestX, bestY
        };
    }

    /**
     * Determines how different two identically sized regions are.
     */
    public static double compareImages(BufferedImage im1, BufferedImage im2) {
        assert (im1.getHeight() == im2.getHeight() && im1.getWidth() == im2.getWidth());
        double variation = 0.0;
        for (int x = 0; x < im1.getWidth(); x++) {
            for (int y = 0; y < im1.getHeight(); y++) {
                variation += compareARGB(im1.getRGB(x, y), im2.getRGB(x, y)) / Math.sqrt(3);
            }
        }
        return variation / (im1.getWidth() * im1.getHeight());
    }

    /**
     */
    private static double compareARGB(int rgb1, int rgb2) {
        int a1 = rgb1 & 0xff000000 >> 24;
        int r1 = rgb1 & 0xff0000 >> 16;
        int g1 = rgb1 & 0xff00 >> 8;
        int b1 = rgb1 & 0xff;
        int a2 = rgb2 & 0xff000000 >> 24;
        int r2 = rgb2 & 0xff0000 >> 16;
        int g2 = rgb2 & 0xff00 >> 8;
        int b2 = rgb2 & 0xff;
        return a1 * a2 * Math.sqrt((r1 - r2) ^ 2 + (g1 - g2) ^ 2 + (b1 - b2) ^ 2);
    }

    public static void main(String[] args) throws Exception {
        BufferedImage wm1 = ImageIO.read(new File("tmp/mgm1.png"));
        BufferedImage wm2 = ImageIO.read(new File("tmp/mgm2.png"));
        Files.list(Paths.get("/Users/nsano/Downloads/JDownloader/(一般コミック) [涼川りん] あそびあそばせ 第05巻"))
        .filter(p -> p.toString().endsWith("0002.jpg"))
        .forEach(p -> {
            try {
                System.err.println(p);
                BufferedImage image = ImageIO.read(p.toFile());
                int[] r = findSubimage(image, wm1);
                if (r[0] != -1) {
                    System.err.println(r[0] + ", " + r[1]);
                    JPanel panel = new JPanel() {
                        public void paint(Graphics g) {
                            g.drawImage(image, 0, 0, this);
                            g.setColor(Color.red);
                            g.drawRect(r[0], r[1], r[0] + wm1.getWidth(), r[1] + wm1.getHeight());
                        }
                    };
                    panel.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
                    JFrame frame = new JFrame();
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.getContentPane().add(panel);
                    frame.pack();
                    frame.setVisible(true);
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });
    }
}

/* */
