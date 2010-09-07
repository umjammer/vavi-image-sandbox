/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.util;


/**
 * Hsv. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/07 nsano initial version <br>
 */
public class Hsv {
    float h;
    float s;
    float v;

    /** */
    public Rgb toRgb() {
        float r, g, b;
        if (s == 0) {
            r = g = b = v * 2.55f;
            Rgb rgb = new Rgb(Math.round(r), Math.round(g), Math.round(b));
//System.err.println(hsv + " -> " + rgb);
            return rgb;
        }
        s = s / 100f;
        v = v / 100f;
        h /= 60f;
        int i = (int) Math.floor(h);
        float f = h - i;
        float p = v * (1 - s);
        float q = v * (1 - s * f);
        float t = v * (1 - s * (1 - f));
        switch (i) {
        case 0:
            r = v;
            g = t;
            b = p;
            break;
        case 1:
            r = q;
            g = v;
            b = p;
            break;
        case 2:
            r = p;
            g = v;
            b = t;
            break;
        case 3:
            r = p;
            g = q;
            b = v;
            break;
        case 4:
            r = t;
            g = p;
            b = v;
            break;
        default:
            r = v;
            g = p;
            b = q;
        }

        Rgb rgb = new Rgb(Math.round(r * 255),
                          Math.round(g * 255),
                          Math.round(b * 255));
        
//System.err.println(hsv + " -> " + rgb);
        return rgb;
    }
    
    public String toString() {
        return String.format("H:%.2f V:%.2f S:%.2f", h, v, s);
    }
}
/* */
