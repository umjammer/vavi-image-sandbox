/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.qr;


import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.RGBImageFilter;
import java.awt.image.ReplicateScaleFilter;
import java.io.UnsupportedEncodingException;


/**
 * QrcodeImageSource.
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 040912 nsano initial version <br>
 */
public class QrcodeImageSource implements ImageProducer {

    /** size x size QRcode bitmap */
    private ImageProducer ip;

    /** QRcode square size */
    private int width;

    /** */
    private Color fg = Color.black;
    /** */
    private Color bg = Color.white;

    /** Converts QRcode boolean structure to bitmap byte array. */
    private static byte[] convert(boolean[][] qr) {

        int width = qr.length;
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

    /**
     * Creates QRcode ImageSource using default character encoding.
     *
     * @param string use encoding
     * @param size size of one dot of QR code
     */
    public QrcodeImageSource(String string, int size) {
        this(string, size, System.getProperty("file.encoding"));
    }

    /**
     * Creates QRcode ImageSource specifying character encoding.
     *
     * @param string use encoding
     * @param size size of one dot of QR code
     * @param encoding for string
     */
    public QrcodeImageSource(String string, int size, String encoding) {
        boolean[][] qr;
        try {
            qr = new Qrcode().toQrcode(string.getBytes(encoding));
        } catch (UnsupportedEncodingException e) {
            qr = new Qrcode().toQrcode(string.getBytes());
        }

        // B&W color model
        byte[] r = new byte[] { (byte) bg.getRed(), (byte) fg.getBlue() };
        byte[] g = new byte[] { (byte) bg.getGreen(), (byte) fg.getBlue() };
        byte[] b = new byte[] { (byte) bg.getBlue(), (byte) fg.getBlue() };
        ColorModel cm = new IndexColorModel(1, 2, r, g, b);

        // 1 x 1 QRcode bitmap
        this.width = qr.length;
        this.ip = new MemoryImageSource(width, width, cm, convert(qr), 0, width);

        setSize(size);
    }

    /** Sets QRcode square size. */
    public void setSize(int size) {
        ImageFilter filter = new ReplicateScaleFilter(width * size, width * size);
        this.ip = new FilteredImageSource(ip, filter);
    }

    /** */
    private static class ColorSwapFilter extends RGBImageFilter {
        Color oldColor;
        Color newColor;
        /** */
        public ColorSwapFilter(Color oldColor, Color newColor) {
            canFilterIndexColorModel = true;
            this.oldColor = oldColor;
            this.newColor = newColor;
        }
        /** */
        public int filterRGB(int x, int y, int rgb) {
//System.err.println("[" + StringUtil.toHex8(rgb) + "], [" + oldColor + "],[" + newColor);
            return (oldColor.getRGB() == rgb) ? newColor.getRGB() : rgb;
        }
    }

    /** Sets QRcode foreground color. default is black. TODO now no effect */
    public void setForeground(Color foreground) {
        this.ip = new FilteredImageSource(ip, new ColorSwapFilter(fg, foreground));
        this.fg = foreground;
    }

    /** Sets QRcode foreground color. default is white. TODO now no effect */
    public void setBackground(Color background) {
        this.ip = new FilteredImageSource(ip, new ColorSwapFilter(bg, background));
        this.bg = background;
    }

    /** @see java.awt.image.ImageProducer#addConsumer(java.awt.image.ImageConsumer) */
    public void addConsumer(ImageConsumer ic) {
        ip.addConsumer(ic);
    }

    /** @see java.awt.image.ImageProducer#isConsumer(java.awt.image.ImageConsumer) */
    public boolean isConsumer(ImageConsumer ic) {
    return ip.isConsumer(ic);
    }

    /** @see java.awt.image.ImageProducer#removeConsumer(java.awt.image.ImageConsumer) */
    public void removeConsumer(ImageConsumer ic) {
        ip.removeConsumer(ic);
    }

    /** @see java.awt.image.ImageProducer#startProduction(java.awt.image.ImageConsumer) */
    public void startProduction(ImageConsumer ic) {
        ip.startProduction(ic);
    }

    /** @see java.awt.image.ImageProducer#requestTopDownLeftRightResend(java.awt.image.ImageConsumer) */
    public void requestTopDownLeftRightResend(ImageConsumer ic) {
        ip.requestTopDownLeftRightResend(ic);
    }
}
