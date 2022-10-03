/*
 * Copyright (c) 2018 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.qr;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.border.LineBorder;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import vavi.awt.ImageComponent;
import vavi.swing.JImageComponent;

import static org.junit.jupiter.api.Assertions.*;


/**
 * QrcodeImageSourceTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2018/02/25 umjammer initial version <br>
 */
public class QrcodeImageSourceTest {

    @Test
    @Disabled
    public void test() {
        fail("Not yet implemented");
    }

    //----

    /**
     * Tests this class.
     * 
     * usage: java QrcodeImageSource string size
     */
    public static void main(String[] args) throws Exception {
        int times = Integer.parseInt(args[1]);
        final Toolkit t = Toolkit.getDefaultToolkit();
        final QrcodeImageSource ip = new QrcodeImageSource(args[0], times, "Windows-31J");
        ip.setForeground(Color.pink);
        Image image = t.createImage(ip);

        final ImageComponent component = new ImageComponent();
        component.setImage(image);
        int width = image.getWidth(null);
        component.setBorder(new LineBorder(Color.white, times / 2));
//System.err.println("size: " + width);
        component.setPreferredSize(new Dimension(width + times, width + times));

        JFrame frame = new JFrame();
        frame.setTitle(args[0] +  " x " + times);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.addComponentListener(new ComponentAdapter() {
//            /** */
//            public void componentResized(ComponentEvent e) {
//                Component c = e.getComponent();
//                System.err.println("c: " + c);
//                ip.setSize(Math.min(c.getWidth(), c.getHeight()));
//                final Image image = t.createImage(ip);
//                SwingUtilities.invokeLater(new Runnable() {
//                    /** */
//                    public void run() {
//                        component.setImage(image);
//                        int width = image.getWidth(null);
//                        System.err.println("size: " + width);
//                        component.setPreferredSize(new Dimension(width, width));
//                    }
//                });
//            }
//        });
        frame.getContentPane().add(component);
        frame.pack();
        frame.setVisible(true);
    }
}

/* */
