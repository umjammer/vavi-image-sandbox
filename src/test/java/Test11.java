/*
 * Copyright (c) 2011 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLEncoder;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 * Test11. (Amazon Image Hacks)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2011/02/25 umjammer initial version <br>
 */
public class Test11 {

    /**
     * TODO 2022-02-25 not work
     * @param args
     */
    public static void main(String[] args) throws Exception {

        String ext = ".jpg";
        String base = "https://ec2.images-amazon.com/images/I/3141BgH-BkL._SL500_AA400";

        BufferedImage image = ImageIO.read(new URL(base + ext));

        String text = "_ZA%s,%d,%d,%d,%d,%s,%d,%d,%d,%d";

        StringBuilder sb = new StringBuilder(base);

//        int w = image.getWidth();
//        int h = image.getHeight();
        for (int y = 20; y < 100; y += 20) {
            for (int x = 80; x < 140; x += 20) {
                int c = image.getRGB(x, y);
                int r = (c & 0x00ff0000) >> 16;
                int g = (c & 0x0000ff00) >> 8;
                int b = c & 0x000000ff;
                sb.append(String.format(text, URLEncoder.encode("#", "UTF-8"), x, y, x + 19, y + 19, "times", 20, r, g, b));
            }
        }
        sb.append(ext);
System.err.println(sb.length());

        final BufferedImage image2 = ImageIO.read(new URL(sb.toString()));

        final JPanel panel = new JPanel() {
            public void paint(Graphics g) {
                g.drawImage(image2, 0, 0, this);
            }
        };
        panel.setPreferredSize(new Dimension(640, 480));

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}

/* */
