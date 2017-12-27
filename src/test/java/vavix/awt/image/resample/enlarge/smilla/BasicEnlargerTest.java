/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.resample.enlarge.smilla;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.junit.Ignore;

import vavix.awt.image.resample.enlarge.smilla.BasicArray.Factory;
import vavix.awt.image.resample.enlarge.smilla.BasicEnlarger;
import vavix.awt.image.resample.enlarge.smilla.EnlargeFormat;
import vavix.awt.image.resample.enlarge.smilla.EnlargeParamInt;
import vavix.awt.image.resample.enlarge.smilla.EnlargeParameter;
import vavix.awt.image.resample.enlarge.smilla.PFloat;


/**
 * BasicEnlargerTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/14 nsano initial version <br>
 */
@Ignore
public class BasicEnlargerTest {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        ImageReader ir = null;
        String className = "com.sun.imageio.plugins.jpeg.JPEGImageReader";
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("no such ImageReader: " + className);
        }
        Iterator<ImageReader> irs = ImageIO.getImageReadersByFormatName("JPEG");
        while (irs.hasNext()) {
            ImageReader tmpIr = irs.next();
            if (clazz.isInstance(tmpIr)) {
                ir = tmpIr;
System.err.println("ImageReader: " + ir.getClass());
                break;
            }
        }
        if (ir == null) {
            throw new IllegalStateException("no suitable ImageReader");
        }

        ImageInputStream iis = ImageIO.createImageInputStream(new FileInputStream(args[0]));
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
        BasicEnlarger<?> be = new BasicEnlarger<PFloat>(format, param) {
            @Override
            protected PFloat readSrcPixel(int srcX, int srcY) {
System.err.println(srcX + ", " + srcY + ": " + source.getRGB(srcX, srcY));
                return new PFloat(source.getRGB(srcX, srcY));
            }
            @Override
            protected void writeDstPixel(PFloat p, int dstCX, int dstCY) {
                dest.setRGB(dstCX, dstCY, (int) p.toF());
            }
            @Override
            protected Factory<PFloat> getFactory() {
                return new Factory<PFloat>() {
                    @Override
                    public PFloat newInstance() {
                        return new PFloat();
                    }
                };
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

/* */
