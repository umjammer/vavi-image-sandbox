/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import vavi.imageio.ImageConverter;
import vavi.util.qr.QrcodeImageSource;


/**
 * ImageIO. QRCode
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021117 nsano initial version <br>
 */
public class QRCode_awt {

    /** */
    private static Logger logger = Logger.getLogger(QRCode_imageio.class.getName());

    /**
     * @param args 0: content, 1: size, 2: output file
     */
    public static void main(String[] args) throws Exception {
        Toolkit t = Toolkit.getDefaultToolkit();
        Image image = t.createImage(new QrcodeImageSource(args[0], Integer.parseInt(args[1]), "Windows-31J"));
logger.fine("Image: " + image);
        ImageConverter ic = ImageConverter.getInstance();
        ic.setColorModelType(BufferedImage.TYPE_BYTE_BINARY);
        BufferedImage bi = ic.toBufferedImage(image);
logger.fine("BufferedImage: " + bi);

        boolean result = ImageIO.write(bi, "gif", new File(args[2]));
logger.fine("gif: " + result);
    }
}

/* */
