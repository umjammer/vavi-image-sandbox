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

    public Hsv() {
    }

    public Hsv(float h, float s, float v) {
        this.h = h;
        this.s = s;
        this.v = v;
    }

    /** */
    public Rgb toRgb() {
        float r, g, b;
        if (s == 0) {
            r = g = b = v * 2.55f;
            Rgb rgb = new Rgb(Math.round(r), Math.round(g), Math.round(b));
//System.err.println(hsv + " -> " + rgb);
            return rgb;
        }
        float s_ = s / 100f;
        float v_ = v / 100f;
        float h_ = h / 60f;
        int i = (int) Math.floor(h_);
        float f = h_ - i;
        float p = v_ * (1 - s_);
        float q = v_ * (1 - s_ * f);
        float t = v_ * (1 - s_ * (1 - f));
        switch (i) {
        case 0:
            r = v_;
            g = t;
            b = p;
            break;
        case 1:
            r = q;
            g = v_;
            b = p;
            break;
        case 2:
            r = p;
            g = v_;
            b = t;
            break;
        case 3:
            r = p;
            g = q;
            b = v_;
            break;
        case 4:
            r = t;
            g = p;
            b = v_;
            break;
        default:
            r = v_;
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
        return String.format("H:%.2f S:%.2f V:%.2f", h, s, v);
    }
}
/* */
