/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import vavi.imageio.ImageConverter;
import vavi.util.qr.QrcodeImageSource;


/**
 * ImageIO.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 021117 nsano initial version <br>
 */
public class t146_4 {

    /** */
    private static Log log = LogFactory.getLog(t146_4.class);

    /**
     * @param args 0: content, 1: size, 2: output file
     */
    public static void main(String[] args) throws Exception {
        Toolkit t = Toolkit.getDefaultToolkit();
        Image image = t.createImage(new QrcodeImageSource(args[0], Integer.parseInt(args[1]), "Windows-31J"));
log.debug("Image: " + image);
        ImageConverter ic = ImageConverter.getInstance();
        ic.setColorModelType(BufferedImage.TYPE_BYTE_BINARY);
        BufferedImage bi = ic.toBufferedImage(image);
log.debug("BufferedImage: " + bi);

        boolean result = ImageIO.write(bi, "gif", new File(args[2]));
log.debug("gif: " + result);
    }
}

/* */
