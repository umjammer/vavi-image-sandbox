/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.resample.enlarge.smilla;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.junit.jupiter.api.Disabled;

import vavi.imageio.IIOUtil;


/**
 * BasicEnlargerTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/14 nsano initial version <br>
 */
@Disabled
public class BasicEnlargerTest {

    /**
     * @param args 0: jpeg
     */
    public static void main(String[] args) throws Exception {
        ImageReader ir = IIOUtil.getImageReader("JPEG", "com.sun.imageio.plugins.jpeg.JPEGImageReader");
        ImageInputStream iis = ImageIO.createImageInputStream(Files.newInputStream(Paths.get(args[0])));
        ir.setInput(iis);
        final BufferedImage source = ir.read(0);
        iis.close();

        //

        EnlargeParameter param = new EnlargeParamInt(80, 80, 80, 80, 80, 80).floatParam();
        EnlargeFormat format = new EnlargeFormat();
        format.srcWidth = source.getWidth();
        format.srcHeight = source.getHeight();
        format.scaleX = 10;
        format.scaleY = 10;
        format.clipX0 = 0;
        format.clipY0 = 0;
        format.clipX1 = Math.round(source.getWidth() * format.scaleX);
        format.clipY1 = Math.round(source.getHeight() * format.scaleY);
        final BufferedImage dest = new BufferedImage(format.clipX1,
                                                     format.clipY1,
                                                     source.getType());
        BasicEnlarger<?> be = new BasicEnlarger<PFloat>(PFloat.class, format, param) {
            @Override
            protected PFloat readSrcPixel(int srcX, int srcY) {
//System.err.println(srcX + ", " + srcY + ": " + source.getRGB(srcX, srcY));
                return new PFloat(source.getRGB(srcX, srcY));
            }
            @Override
            protected void writeDstPixel(PFloat p, int dstCX, int dstCY) {
                dest.setRGB(dstCX, dstCY, (int) p.toF());
            }
        };
        be.enlarge();

        //

        final JPanel panel = new JPanel() {
            public void paint(Graphics g) {
                g.drawImage(dest, 0, 0, this);
            }
        };
        panel.setPreferredSize(new Dimension(640, 480));

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(panel);
        frame.getContentPane().add(scrollPane);
        frame.pack();
        frame.setVisible(true);
    }
}
