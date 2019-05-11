/*
 * Copyright (c) 2009 by KLab Inc., All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.awt.image.pixel;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.junit.jupiter.api.Test;

import vavix.awt.image.pixel.SimpleResizeOp;


/**
 * SimpleResizeOpTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2009/05/08 nsano initial version <br>
 */
public class SimpleResizeOpTest {

    BufferedImage image;

    public SimpleResizeOpTest() throws IOException {
        this.image = ImageIO.read(SimpleResizeOpTest.class.getResourceAsStream("/sample.gif"));
    }

    @Test
    public void testHalf() {
        BufferedImage filteredImage = new SimpleResizeOp(.5f, .5f).filter(image, null);
JOptionPane.showMessageDialog(null, new ImageIcon(image), "Half", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(filteredImage));
    }

    @Test
    public void testDouble() {
        BufferedImage filteredImage = new SimpleResizeOp(2f, 2f).filter(image, null);
JOptionPane.showMessageDialog(null, new ImageIcon(image), "Double", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(filteredImage));
    }
}

/* */
