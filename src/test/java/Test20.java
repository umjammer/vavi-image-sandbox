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

import vavi.imageio.ImageConverter;
import vavix.awt.image.resample.enlarge.NoidsEnlargeOp;


/**
 * mugen viewer.
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2011/08/31 umjammer initial version <br>
 */
public class Test20 {

    /**
     * @param args
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

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new JScrollPane(panel));
        frame.pack();
        frame.setVisible(true);
    }
}

/* */
