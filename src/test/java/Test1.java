/*
 * Copyright (c) 2011 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.jupiter.api.Disabled;


/**
 * Rolling Artwork
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2011/02/09 umjammer initial version <br>
 */
@Disabled
public class Test1 {

    static {
//        IIOUtil.setOrder(ImageReaderSpi.class, "com.sixlegs.png.iio.PngImageReaderSpi", "com.sun.imageio.plugins.png.PNGImageReaderSpi");
    }

    /**
     * @param args 0: input image file name, 1: output file base name
     */
    public static void main(String[] args) throws Exception {
        BufferedImage image = ImageIO.read(new File(args[0]));

        final int N = 18;

        int w = image.getWidth();
        int h = image.getHeight();
        int r = Math.min(h, w);

        final BufferedImage[] resultImages = new BufferedImage[N];

        for (int i = 0; i < N; i++) {
            resultImages[i] = new BufferedImage(w, h, image.getType() != 0 ? image.getType() : BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = resultImages[i].createGraphics();

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

//            g2d.setColor(new Color(0xff, 0xff, 0xff, 0xff));
//            g2d.fillRect(0, 0, w, h);

            g2d.setClip(new Ellipse2D.Double(0, 0, r, r));

            g2d.translate(w / 2, h / 2);
            g2d.rotate(2 * Math.PI / N * i);

            g2d.drawImage(image, w / -2, h / -2, null);

//            ImageIO.write(resultImages[i], "PNG", new FileOutputStream(String.format("%s%02d.%s", args[1], i, "jpg")));
        }

        JPanel panel = new JPanel() {
            int c = 0;
            {
                new Thread(new Runnable() {
                    long l;
                    @Override
                    public void run() {
                        l = System.currentTimeMillis();
                        while (true) {
                            long d = 25 - (System.currentTimeMillis() - l);
//System.err.println(d);
                            try {
                                Thread.sleep(Math.max(d, 0));
                            } catch (InterruptedException e) {
                            }
                            repaint();
                            c++;
                            if (c == N) {
                                c = 0;
                            }
                            l = System.currentTimeMillis();
                        }
                    }
                }).start();
            }
            public void paint(Graphics g) {
//System.err.println(c);
//                g.setClip(new Rectangle(0, is[c].getHeight() / 3 * 2, is[c].getWidth(), is[c].getHeight()));
                g.drawImage(resultImages[c], 0, 0, this);
            }
        };
        panel.setPreferredSize(new Dimension(w, h));

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}

/* */
