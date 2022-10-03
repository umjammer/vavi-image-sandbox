/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.logger.Logger;
import org.krysalis.barcode4j.BarcodeClassResolver;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.DefaultBarcodeClassResolver;
import org.krysalis.barcode4j.cli.AdvancedConsoleLogger;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.MimeTypes;

import vavi.awt.ImageComponent;
import vavi.swing.JImageComponent;


/**
 * 1d barcode display.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 041017 nsano initial version <br>
 */
public class BarCode1d {

    /** */
    private static final Logger logger = new AdvancedConsoleLogger(AdvancedConsoleLogger.LEVEL_DEBUG, false, System.out, System.err);

    /** */
    public static void main(String[] args) throws Exception {
        new BarCode1d(args);
    }

    /**
     * @param args message dpi bw(boolean) symbol("code128", "code39", "codabar", "ean-13", "ean-8", "intl2of5", "upc-a", "upc-e", "postnet")
     */
    public BarCode1d(String[] args) throws Exception {

        int dpi = Integer.parseInt(args[1]);
        boolean bw = Boolean.parseBoolean(args[2]);
        String symbol = args[3];

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String format = MimeTypes.expandFormat(MimeTypes.MIME_PNG);
logger.debug("Generating " + format + "...");

        BarcodeClassResolver resolver = new DefaultBarcodeClassResolver();
        BarcodeGenerator gen = BarcodeUtil.createBarcodeGenerator(getConfiguration(symbol), resolver);

logger.debug("Resolution: " + dpi + "dpi");
        BitmapCanvasProvider bitmap;
        if (bw) {
logger.debug("Black/white image (1-bit)");
            bitmap = new BitmapCanvasProvider(baos, format, dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0); // TODO orientation = 0 ???
        } else {
logger.debug("Grayscale image (8-bit) with anti-aliasing");
            bitmap = new BitmapCanvasProvider(baos, format, dpi, BufferedImage.TYPE_BYTE_GRAY, true, 0); // TODO orientation = 0 ???
        }
        gen.generateBarcode(bitmap, args[0]);
        bitmap.finish();

logger.debug(args[0]);
        Image image = ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));
logger.debug(image.toString());
        JFrame frame = new JFrame();
//      frame.setSize(320, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageComponent component = new ImageComponent();
        component.setImage(image);
        int w = image.getWidth(component);
        int h = image.getHeight(component);
logger.debug("size: " + w + "x" + h);
        component.setPreferredSize(new Dimension(w, h));
        frame.getContentPane().add(component);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     *  "code128", "code39", "codabar", "ean-13", "ean-8", "intl2of5", "upc-a", "upc-e", "postnet"
     */
    private Configuration getConfiguration(String symbol) {
        DefaultConfiguration config = new DefaultConfiguration("cfg");
        DefaultConfiguration child = new DefaultConfiguration(symbol);
        config.addChild(child);
        return config;
    }
}

/* */
