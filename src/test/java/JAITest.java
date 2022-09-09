/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import com.sun.media.jai.codec.ImageEncoderImpl;
import com.sun.media.jai.codec.PNGEncodeParam;


/**
 * JAI test.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (vavi)
 * @version 0.00 030905 nsano initial version <br>
 */
public class JAITest {

    /**
     * @param args 0: in image, 1: out png
     */
    public static void main(String[] args) throws Exception {
        BufferedImage image = ImageIO.read(new File(args[0]));
        PNGEncodeParam encodeParam = PNGEncodeParam.getDefaultEncodeParam(image);
        ImageEncoderImpl encoder = new com.sun.media.jai.codecimpl.PNGImageEncoder(Files.newOutputStream(Paths.get(args[1])), encodeParam);
        encoder.encode(image);
    }
}

/* */
