/*
 * Copyright (c) 2017 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import vavi.swing.JImageComponent;


/**
 * Test22. (heif imageio)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2017/11/16 umjammer initial version <br>
 */
public class Test22 {

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {
        Random random = new Random(System.currentTimeMillis());
        String filename = args[random.nextInt(args.length)];

        BufferedImage image = ImageIO.read(new File(filename));

        //
        JFrame frame = new JFrame();
        frame.setTitle(filename);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JImageComponent component = new JImageComponent();
        component.setImage(image);
        int w = image.getWidth(component);
        int h = image.getHeight(component);
        component.setPreferredSize(new Dimension(w, h));
        frame.getContentPane().add(component);
        frame.pack();
        frame.setVisible(true);
    }
}

/* */