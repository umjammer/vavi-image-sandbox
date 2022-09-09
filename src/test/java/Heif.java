/*
 * Copyright (c) 2017 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.rococoa.cocoa.appkit.NSImage;
import org.rococoa.cocoa.foundation.NSData;

import vavi.swing.JImageComponent;


/**
 * Heif. (heif) using jna, rococoa
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2017/11/16 umjammer initial version <br>
 */
public class Heif {

    static {
        // !!! JNA は -Djava.library.path を見なくて、以下 !!!
        com.sun.jna.NativeLibrary.addSearchPath("rococoa", System.getProperty("java.library.path"));
    }

    /**
     * @param args 0: int
     */
    public static void main(String[] args) throws IOException {
        Random random = new Random(System.currentTimeMillis());
        String filename = args[random.nextInt(args.length)];

        // file not found で null が返る...orz
        NSImage nsImage = NSImage.imageWithContentsOfFile(filename);
        if (nsImage == null) {
            throw new FileNotFoundException(filename);
        }
System.err.println(nsImage);
        //com.sun.jna.Pointer imageRep = image.TIFFRepresentation();
        NSData data = nsImage.TIFFRepresentation();
System.err.println(data.length());
        ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
        BufferedImage image = ImageIO.read(bais);

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