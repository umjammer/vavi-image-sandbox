/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import javax.imageio.ImageIO;


/**
 * ImageIO. list reader, writer spi
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (vavi)
 * @version 0.00 020603 vavi initial version <br>
 */
public class ImageIOReaderWriter {

    public static void main(String[] args) throws Exception {
        String[] rs = ImageIO.getReaderFormatNames();
        System.err.println("-- reader --");
        for (String r : rs) {
            System.err.println(r);
        }
        System.err.println("-- writer --");
        String[] ws = ImageIO.getWriterFormatNames();
        for (String w : ws) {
            System.err.println(w);
        }
    }
}

/* */
