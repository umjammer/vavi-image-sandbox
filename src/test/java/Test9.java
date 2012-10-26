/*
 * Copyright (c) 2011 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;


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
System.err.println("--- after order " + pt.getName() + " ---");
i = iioRegistry.getServiceProviders(pt, true);
while (i.hasNext()) {
    System.err.println(i.next());
}
    }
}

/* */
