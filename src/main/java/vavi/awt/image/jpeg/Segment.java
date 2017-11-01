/*
 * Copyright (c) 2015 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.awt.image.jpeg;



/**
 * Segment.
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2015/12/17 umjammer initial version <br>
 */
public class Segment {

    protected String type;
    protected int size;
    protected byte[] data;

    protected Segment(String type) {
        this.type = type;
        this.size = 0;
        this.data = null;
System.err.printf("%-5s: format I\n", type);
    }

    Segment(String type, int size, byte[] data) {
        this.type = type;
        this.size = size;
        this.data = data;
System.err.printf("%-5s: format II : %5d\n", type, size + 2);
    }

}
/* */
