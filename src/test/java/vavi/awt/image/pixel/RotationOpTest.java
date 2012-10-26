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

import junit.framework.TestCase;

import org.junit.Test;

import vavix.awt.image.pixel.RotationOp;


/**
 * RotationOpTest. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2009/04/14 nsano initial version <br>
 */
public class RotationOpTest extends TestCase {

    BufferedImage image;
    
    public RotationOpTest() throws IOException {
        this.image = ImageIO.read(RotationOpTest.class.getResourceAsStream("klab.gif"));
    }

    @Test
    public void testL90() {
        BufferedImage filteredImage = new RotationOp(RotationOp.ROTATE_LEFT_90).filter(image, null);
JOptionPane.showMessageDialog(null, new ImageIcon(image), "L90", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(filteredImage));
    }

    @Test
    public void testR90() {
        BufferedImage filteredImage = new RotationOp(RotationOp.ROTATE_RIGHT_90).filter(image, null);
JOptionPane.showMessageDialog(null, new ImageIcon(image), "R90", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(filteredImage));
    }

    @Test
    public void test180() {
        BufferedImage filteredImage = new RotationOp(RotationOp.ROTATE_180).filter(image, null);
JOptionPane.showMessageDialog(null, new ImageIcon(image), "180", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(filteredImage));
    }
}

/* */
