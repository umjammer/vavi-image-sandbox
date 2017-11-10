/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.ReplicateScaleFilter;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;

import vavi.imageio.ImageConverter;
import vavi.swing.JImageComponent;
import vavi.util.qr.Qrcode;


/**
 * ImageIO.
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 021117 nsano initial version <br>
 */
@Ignore
public class t146_3 {

    /** */
    private static Log log = LogFactory.getLog(t146_3.class);

    /** */
    private static byte[] convert(boolean[][] qr) {

        int width = qr[0].length;
        int height = width;

        byte[] result = new byte[width * height];
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (qr[i][j]) {
                    result[j * width + i] |= 1;
                }
            }
        }

        return result;
    }

    /** */
    private static byte[] r = new byte[] { (byte) 0xff, 0x00 };
    private static byte[] g = new byte[] { (byte) 0xff, 0x00 };
    private static byte[] b = new byte[] { (byte) 0xff, 0x00 };

    /** */
    private static ColorModel cm = new IndexColorModel(1, 2, r, g, b);

    /**
     * 
     * @param args 0: content, 1: size
     */
    public static void main(String[] args) throws Exception {
        String[] rs = ImageIO.getReaderMIMETypes();
        System.err.println("-- reader --");
        for (int i = 0; i < rs.length; i++) {
            System.err.println(rs[i]);
        }
        System.err.println("-- writer --");
        String[] ws = ImageIO.getWriterMIMETypes();
        for (int i = 0; i < ws.length; i++) {
            System.err.println(ws[i]);
        }

        boolean[][] qr = new Qrcode().toQrcode(args[0].getBytes("Windows-31J"));
        int width = qr.length;

        int times = Integer.parseInt(args[1]);
        ImageFilter filter = new ReplicateScaleFilter(width * times, width * times);

        ImageProducer ip = new MemoryImageSource(width, width, cm, convert(qr), 0, width);
        Toolkit t = Toolkit.getDefaultToolkit();
        Image image = t.createImage(new FilteredImageSource(ip, filter));

        ImageConverter ic = ImageConverter.getInstance();
        ic.setColorModelType(BufferedImage.TYPE_BYTE_BINARY);
        BufferedImage bi = ic.toBufferedImage(image);
        boolean result = ImageIO.write(bi, "gif", new File("qr.gif"));
log.debug("gif: " + result);
        result = ImageIO.write(bi, "bmp", new File("qr.bmp"));
log.debug("bmp: " + result);
        result = ImageIO.write(bi, "png", new File("qr.png"));
log.debug("png: " + result);
        result = ImageIO.write(bi, "jpeg", new File("qr.jpeg"));
log.debug("jpeg: " + result);
        result = ImageIO.write(bi, "jpeg 2000", new File("qr.jp2k"));
log.debug("jpeg 2000: " + result);

log.debug(image);
        JImageComponent component = new JImageComponent();
        component.setImage(bi);
        component.setPreferredSize(new Dimension(width * times, width * times));

        JFrame frame = new JFrame();
        frame.setTitle(args[0] +  " x " + times);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(component);
        frame.pack();
        frame.setVisible(true);
//        Insets insets = frame.getInsets();
//log.debug(insets);
//        frame.setSize(width + insets.left + insets.right, width + insets.top + insets.bottom);
    }
}

/* */
