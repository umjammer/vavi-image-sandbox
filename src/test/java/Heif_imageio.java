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
import vavi.util.Debug;


/**
 * Heif_imageio. (heif imageio)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2017/11/16 umjammer initial version <br>
 */
public class Heif_imageio {

    /**
     * @param args 0...: heif files
     */
    public static void main(String[] args) throws IOException {
        Random random = new Random(System.currentTimeMillis());
        String filename = args[random.nextInt(args.length)];

Debug.println(filename);
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
