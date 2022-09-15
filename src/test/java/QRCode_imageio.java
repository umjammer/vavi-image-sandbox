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
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

import vavi.imageio.ImageConverter;
import vavi.swing.JImageComponent;
import vavi.util.qr.Qrcode;


/**
 * ImageIO.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021117 nsano initial version <br>
 */
public class QRCode_imageio {

    /** */
    private static Logger logger = Logger.getLogger(QRCode_imageio.class.getName());

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
    private static final byte[] r = new byte[] { (byte) 0xff, 0x00 };
    private static final byte[] g = new byte[] { (byte) 0xff, 0x00 };
    private static final byte[] b = new byte[] { (byte) 0xff, 0x00 };

    /** */
    private static final ColorModel cm = new IndexColorModel(1, 2, r, g, b);

    /**
     * 
     * @param args 0: content, 1: size
     */
    public static void main(String[] args) throws Exception {
        String[] rs = ImageIO.getReaderMIMETypes();
        System.err.println("-- reader --");
        for (String s : rs) {
            System.err.println(s);
        }
        System.err.println("-- writer --");
        String[] ws = ImageIO.getWriterMIMETypes();
        for (String w : ws) {
            System.err.println(w);
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
logger.fine("gif: " + result);
        result = ImageIO.write(bi, "bmp", new File("qr.bmp"));
logger.fine("bmp: " + result);
        result = ImageIO.write(bi, "png", new File("qr.png"));
logger.fine("png: " + result);
        result = ImageIO.write(bi, "jpeg", new File("qr.jpeg"));
logger.fine("jpeg: " + result);
        result = ImageIO.write(bi, "jpeg 2000", new File("qr.jp2k"));
logger.fine("jpeg 2000: " + result);

logger.fine(image.toString());
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
//logger.fine(insets);
//        frame.setSize(width + insets.left + insets.right, width + insets.top + insets.bottom);
    }
}

/* */
