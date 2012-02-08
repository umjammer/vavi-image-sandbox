/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.util;

import java.awt.Color;


/**
 * Rgb. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/07 nsano initial version <br>
 */
public class Rgb {
    int r;
    int g;
    int b;

    /** */
    public Rgb(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    /** */
    public Rgb(Color c) {
        this(c.getRed(), c.getGreen(), c.getBlue());
    }

    /** */
    public Rgb() {
        this.r = 0;
        this.g = 0;
        this.b = 0;
    }

    /** */
    public Hsv toHsv() {
        Hsv hsv = new Hsv();

        int m = r;
        if (g < m) {
            m = g;
        }
        if (b < m) {
            m = b;
        }
        int v = r;
        if (g > v) {
            v = g;
        }
        if (b > v) {
            v = b;
        }
        float value = 100f * v / 255f;
        float delta = v - m;
        
        if (v == 0.0) {
            hsv.s = 0;
        } else {
            hsv.s = 100 * delta / v;
        }
        
        if (hsv.s == 0) {
            hsv.h = 0;
        } else {
            if (r == v) {
                hsv.h = 60.0f * (g - b) / delta;
            } else if (g == v) {
                hsv.h = 120f + 60f * (b - r) / delta;
            } else if (b == v) {
                hsv.h = 240f + 60f * (r - g) / delta;
            }
            
            if (hsv.h < 0.0) {
                hsv.h = hsv.h + 360f;
            }
        }
        
        hsv.v = Math.round(value);

        return hsv;
    }
    
    public Color toColor() {
        return new Color(r, g, b);
    }

    public String toString() {
        return String.format("R:%02X G: %02X B: %02X", r, g, b);
    }
}
/* */
