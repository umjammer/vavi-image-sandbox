/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import com.sun.media.jai.codec.ImageEncoderImpl;
import com.sun.media.jai.codec.PNGEncodeParam;


/**
 * JAI test.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (vavi)
 * @version 0.00 030905 nsano initial version <br>
 */
public class t29 {

    /**
     * The program entry.
     */
    public static void main(String[] args) throws Exception {
        BufferedImage image = ImageIO.read(new File(args[0]));
        PNGEncodeParam encodeParam = PNGEncodeParam.getDefaultEncodeParam(image);
        ImageEncoderImpl encoder = new com.sun.media.jai.codecimpl.PNGImageEncoder(new FileOutputStream(args[1]), encodeParam);
        encoder.encode(image);
    }
}

/* */
