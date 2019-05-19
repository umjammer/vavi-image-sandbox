/*
 * Copyright (c) 2011 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderSpi;
import javax.swing.JFrame;
import javax.swing.JPanel;

import vavi.imageio.IIOUtil;

import vavix.util.grep.FileDigger;
import vavix.util.grep.RegexFileDigger;


/**
 * Test2.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2011/02/09 umjammer initial version <br>
 */
public class Test2 {

    static {
        IIOUtil.setOrder(ImageReaderSpi.class, "com.sixlegs.png.iio.PngImageReaderSpi", "com.sun.imageio.plugins.png.PNGImageReaderSpi");
    }

    static BufferedImage image;

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        final JPanel panel = new JPanel() {
            public void paint(Graphics g) {
                g.drawImage(image, 0, 0, this);
            }
        };
        panel.setPreferredSize(new Dimension(640, 480));

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);

        new RegexFileDigger(new FileDigger.FileDredger() {
            public void dredge(File file) throws IOException {
                try {
//System.err.println("--- order  ---");
//Iterator<ImageReaderSpi> i = IIORegistry.getDefaultInstance().getServiceProviders(ImageReaderSpi.class, true);
//while (i.hasNext()) {
//    System.err.println(i.next());
//}
                    image = ImageIO.read(file);
                    panel.repaint();
                } catch (IOException e) {
System.err.println(file);
                    e.printStackTrace();
                }
            }
        }, Pattern.compile(".+\\.(png|PNG)")).dig(new File(args[0]));
    }
}

/* */
