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
public class t5 {

    public static void main(String[] args) throws Exception {
        String[] rs = ImageIO.getReaderFormatNames();
        System.err.println("-- reader --");
        for (int i = 0; i < rs.length; i++) {
            System.err.println(rs[i]);
        }
        System.err.println("-- writer --");
        String[] ws = ImageIO.getWriterFormatNames();
        for (int i = 0; i < ws.length; i++) {
            System.err.println(ws[i]);
        }
    }
}

/* */
