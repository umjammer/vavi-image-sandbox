package vavi.util.barcode;


import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.logger.Logger;
import org.krysalis.barcode4j.BarcodeClassResolver;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.DefaultBarcodeClassResolver;
import org.krysalis.barcode4j.cli.AdvancedConsoleLogger;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.MimeTypes;

import vavi.swing.JImageComponent;


/**
 * 1D Barcode Image.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 041017 nsano initial version <br>
 */
public class Barcode {

    /** */
    private static Logger logger = new AdvancedConsoleLogger(AdvancedConsoleLogger.LEVEL_DEBUG, false, System.out, System.err);

    /** */
    private String value;
    /** */
    private int dpi;
    /** */
    private boolean bw;
    /** */
    private String symbol;

    /**
     * @param value only numeric allowed
     * @param symbol "code128", "code39", "codabar", "ean-13", "ean-8", "intl2of5", "upc-a", "upc-e", "postnet"
     * @param dpi dot per inch
     * @param bw true use B&amp;W
     */
    public Barcode(String value, String symbol, int dpi, boolean bw) {
        this.value = value;
        this.dpi = dpi;
        this.bw = bw;
        this.symbol = symbol;
    }

    /**
     * @throws IOException when BarcodeException occurs
     */
    public BufferedImage getBufferedImage() throws IOException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String format = MimeTypes.expandFormat(MimeTypes.MIME_PNG);
logger.debug("Generating " + format + "...");

            BarcodeClassResolver resolver = new DefaultBarcodeClassResolver();
            BarcodeGenerator gen = BarcodeUtil.createBarcodeGenerator(getConfiguration(symbol), resolver);

logger.debug("Resolution: " + dpi + "dpi");
            BitmapCanvasProvider bitmap;
            if (bw) {
logger.debug("Black/white image (1-bit)");
                bitmap = new BitmapCanvasProvider(baos, format, dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            } else {
logger.debug("Grayscale image (8-bit) with anti-aliasing");
                bitmap = new BitmapCanvasProvider(baos, format, dpi, BufferedImage.TYPE_BYTE_GRAY, true, 0);
            }
            gen.generateBarcode(bitmap, value);
            bitmap.finish();

logger.debug(value);
            return ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));
        } catch (BarcodeException e) {
            throw (IOException) new IOException().initCause(e);
        } catch (ConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @param symbol "code128", "code39", "codabar", "ean-13", "ean-8", "intl2of5", "upc-a", "upc-e", "postnet"
     */
    private Configuration getConfiguration(String symbol) {
        DefaultConfiguration config = new DefaultConfiguration("cfg");
        DefaultConfiguration child = new DefaultConfiguration(symbol);
        config.addChild(child);
        return config;
    }

    //----

    /** */
    public static void main(String[] args) throws Exception {
        String value = args[0];
        String symbol = args[1];
        int dpi = Integer.parseInt(args[2]);
        boolean bw = Boolean.parseBoolean(args[3]);

        Barcode barcode = new Barcode(value, symbol, dpi, bw);

        BufferedImage image = barcode.getBufferedImage();
        int h = image.getHeight();
        int w = image.getWidth();

        BufferedImage newImage = new BufferedImage(h, w, image.getType());
        Graphics2D g2d = newImage.createGraphics();
        g2d.rotate(Math.toRadians(90));
        g2d.drawImage(image, 0, -h, null);

logger.debug(image.toString());

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JImageComponent component = new JImageComponent();
        component.setImage(newImage);
        component.setPreferredSize(new Dimension(newImage.getWidth(), newImage.getHeight()));
        frame.getContentPane().add(component);
        frame.pack();
        frame.setVisible(true);
    }
}

/* */
