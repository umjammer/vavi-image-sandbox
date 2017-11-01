/*
 * Copyright (c) 2015 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.awt.image.jpeg.segment;

import vavi.awt.image.jpeg.Segment;
import vavi.util.StringUtil;


/**
 * DHT. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2015/12/17 umjammer initial version <br>
 */
public class COM extends Segment {

    public COM() {
        super("COM");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        sb.append(", ");
        sb.append(size);
        sb.append('\n');

        sb.append(StringUtil.getDump(data, 1, data.length - 1));

        return sb.toString();
    }
}

/* */
