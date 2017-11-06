/*
 * Copyright (c) 2011 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JFrame;

import vavi.swing.JImageComponent;

import vavix.imageio.IIOUtil;


/**
 * list iio providers.
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2011/02/09 umjammer initial version <br>
 */
public class Test9 {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String file = args[0];

        String formatName = "PNG";
        Iterator<ImageReader> irs = ImageIO.getImageReadersByFormatName(formatName);
        System.err.println("-- " + formatName + " reader --");
        while (irs.hasNext()) {
            System.err.println(irs.next().getClass().getName());
        }
        Iterator<ImageWriter> iws = ImageIO.getImageWritersByFormatName(formatName);
        System.err.println("-- " + formatName + " writer --");
        while (iws.hasNext()) {
            System.err.println(iws.next().getClass().getName());
        }

        String mimeType = "image/png";
        irs = ImageIO.getImageReadersByMIMEType(mimeType);
        System.err.println("-- " + mimeType + " reader --");
        while (irs.hasNext()) {
            System.err.println(irs.next().getClass().getName());
        }
        iws = ImageIO.getImageWritersByMIMEType(mimeType);
        System.err.println("-- " + mimeType + " writer --");
        while (iws.hasNext()) {
            System.err.println(iws.next().getClass().getName());
        }

        String suffix = "png";
        irs = ImageIO.getImageReadersBySuffix(suffix);
        System.err.println("-- " + suffix + " reader --");
        while (irs.hasNext()) {
            System.err.println(irs.next().getClass().getName());
        }
        iws = ImageIO.getImageWritersBySuffix(suffix);
        System.err.println("-- " + suffix + " writer --");
        while (iws.hasNext()) {
            System.err.println(iws.next().getClass().getName());
        }

        setOrder(ImageReaderSpi.class, "com.sixlegs.png.iio.PngImageReaderSpi", "com.sun.imageio.plugins.png.PNGImageReaderSpi");
        showOrder(ImageReaderSpi.class);

        //
        ImageReader ir = null;
        String className = "com.sixlegs.png.iio.PngImageReader";
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
System.err.println("class: " + clazz.getName());
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("no such ImageReader: " + className);
        }
        irs = ImageIO.getImageReadersByFormatName("PNG");
        while (irs.hasNext()) {
            ImageReader tmpIr = irs.next();
System.err.println("readers: " + tmpIr.getClass().getName());
            if (clazz.isInstance(tmpIr)) {
                ir = tmpIr;
System.err.println("ImageReader: " + ir.getClass());
                break;
            }
        }
        if (ir == null) {
            throw new IllegalStateException("no suitable ImageReader");
        }
        ImageInputStream iis = ImageIO.createImageInputStream(new FileInputStream(file));
        ir.setInput(iis);
        BufferedImage image = ir.read(0);
        iis.close();

        //
        IIOUtil.deregister(ImageReaderSpi.class, "vavi.imageio.ico.WindowsIconImageReaderSpi");
        showOrder(ImageReaderSpi.class);
        image = ImageIO.read(new FileInputStream(file));

        //
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JImageComponent component = new JImageComponent();
        component.setImage(image);
        int w = image.getWidth(component);
        int h = image.getHeight(component);
        component.setPreferredSize(new Dimension(w, h));
        frame.getContentPane().add(component);
        frame.pack();
        frame.setVisible(true);
    }

    static <T> void setOrder(Class<T> pt, String p1, String p2) {
System.err.println("--- set order " + pt.getName() + " ---");
        IIORegistry iioRegistry = IIORegistry.getDefaultInstance();
        T sp1 = null;
        T sp2 = null;
        Iterator<T> i = iioRegistry.getServiceProviders(pt, true);
        while (i.hasNext()) {
            T p = i.next();
            if (p1.equals(p.getClass().getName())) {
                sp1 = p;
                System.err.println("1: " + sp1.getClass().getName());
            } else if (p2.equals(p.getClass().getName())) {
                sp2 = p;
                System.err.println("2: " + sp2.getClass().getName());
            }
        }
        if (sp1 == null || sp2 == null) {
            throw new IllegalArgumentException(p1 + " or " + p2 + " not found");
        }
        iioRegistry.setOrdering(pt, sp1, sp2);
    }

    static <T> void showOrder(Class<T> pt) {
        System.err.println("--- after order " + pt.getName() + " ---");
        int c = 1;
        IIORegistry iioRegistry = IIORegistry.getDefaultInstance();
        Iterator<T> i = iioRegistry.getServiceProviders(pt, true);
        while (i.hasNext()) {
            System.err.println(c++ + ": " + i.next().getClass().getName());
        }
    }
}

/* */
