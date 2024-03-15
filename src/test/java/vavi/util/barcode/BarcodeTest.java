/*
 * Copyright (c) 2017 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.barcode;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import vavi.swing.JImageComponent;

import static org.junit.jupiter.api.Assertions.fail;


/**
 * BarcodeTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2017/11/18 umjammer initial version <br>
 */
@Disabled
public class BarcodeTest {

    @Test
    public void test() {
        fail("Not yet implemented");
    }

    //----

    /** */
    public static void main(String[] args) throws Exception {
        String value = args[0];
        String symbol = args[1];
        int dpi = Integer.parseInt(args[2]);
        boolean bw = Boolean.parseBoolean(args[3]);

        Barcode barcode = new Barcode(value, symbol, dpi, bw);

        BufferedImage image = barcode.getBufferedImage();
        int h = image.getHeight();
        int w = image.getWidth();

        BufferedImage newImage = new BufferedImage(h, w, image.getType());
        Graphics2D g2d = newImage.createGraphics();
        g2d.rotate(Math.toRadians(90));
        g2d.drawImage(image, 0, -h, null);

System.err.println(image);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JImageComponent component = new JImageComponent();
        component.setImage(newImage);
        component.setPreferredSize(new Dimension(newImage.getWidth(), newImage.getHeight()));
        frame.getContentPane().add(component);
        frame.pack();
        frame.setVisible(true);
    }
}
