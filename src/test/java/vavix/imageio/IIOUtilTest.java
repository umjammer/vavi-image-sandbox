/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.imageio;

import javax.imageio.spi.ImageReaderSpi;

import org.junit.Test;


/**
 * IIOUtilTest. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2012/02/08 umjammer initial version <br>
 */
public class IIOUtilTest {

    @Test
    public void test() {
        IIOUtil.setOrder(ImageReaderSpi.class, "com.sixlegs.png.iio.PngImageReaderSpi", "com.sun.imageio.plugins.png.PNGImageReaderSpi");
    }
}

/* */
