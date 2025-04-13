/*
 * Copyright (c) 2009 by KLab Inc., All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.awt.image.pixel;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import vavix.awt.image.pixel.RotationOp;


/**
 * RotationOpTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2009/04/14 nsano initial version <br>
 */
public class RotationOpTest {

    BufferedImage image;

    public RotationOpTest() throws IOException {
        this.image = ImageIO.read(RotationOpTest.class.getResourceAsStream("/namacha02.jpg"));
    }

    @Test
    public void testL90() {
        BufferedImage filteredImage = new RotationOp(RotationOp.ROTATE_LEFT_90).filter(image, null);
if (System.getProperty("vavi.test", "").equals("ide"))
 JOptionPane.showMessageDialog(null, new ImageIcon(image), "L90", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(filteredImage));
    }

    @Test
    public void testR90() {
        BufferedImage filteredImage = new RotationOp(RotationOp.ROTATE_RIGHT_90).filter(image, null);
if (System.getProperty("vavi.test", "").equals("ide"))
 JOptionPane.showMessageDialog(null, new ImageIcon(image), "R90", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(filteredImage));
    }

    @Test
    public void test180() {
        BufferedImage filteredImage = new RotationOp(RotationOp.ROTATE_180).filter(image, null);
if (System.getProperty("vavi.test", "").equals("ide"))
 JOptionPane.showMessageDialog(null, new ImageIcon(image), "180", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(filteredImage));
    }

    @AfterEach
    public void tearDown() throws Exception {
        new Robot().keyPress(KeyEvent.VK_SPACE);
    }
}
