/*
 * http://www.knorrpage.de/colormatch.html
 */

package vavix.awt.image.util;

import java.awt.Color;


/**
 * ColorMatcher.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 041007 nsano initial version <br>
 * @see "http://www.knorrpage.de/colormatch.html"
 */
public class ColorMatcher {

    private Rgb rgb = new Rgb(127, 127, 127);

    private Color[] results = new Color[6];

    /**
     *
     * @param color base color you want to match
     * @return matching colors size is 6, 0: given color, 1...5: matching colors
     */
    public Color[] getMatchedColors(Color color) {
        rgb.r = color.getRed();
        rgb.g = color.getGreen();
        rgb.b = color.getBlue();
        results[0] = color;
        update(rgb.toHsv());
        return results;
    }

    /** */
    private static final int rc(int x, int m) {
        if (x > m) {
            return m;
        }
        if (x < 0) {
            return 0;
        } else {
            return x;
        }
    }

    /** */
    private void update(Hsv hsv) {
        Hsv y = new Hsv();
        Hsv yx = new Hsv();

        y.s = hsv.s;
        y.h = hsv.h;
        if (hsv.v > 70) {
            y.v = hsv.v - 30;
        } else {
            y.v = hsv.v + 30;
        }
        results[1] = y.toRgb().toColor();

        if ((hsv.h >= 0) && (hsv.h < 30)) {
            yx.h = y.h = hsv.h + 20;
            yx.s = y.s = hsv.s;
            y.v = hsv.v;
            if (hsv.v > 70) {
                yx.v = hsv.v - 30;
            } else {
                yx.v = hsv.v + 30;
            }
        }
        if ((hsv.h >= 30) && (hsv.h < 60)) {
            yx.h = y.h = hsv.h + 150;
            y.s = rc(Math.round(hsv.s - 30), 100);
            y.v = rc(Math.round(hsv.v - 20), 100);
            yx.s = rc(Math.round(hsv.s - 70), 100);
            yx.v = rc(Math.round(hsv.v + 20), 100);
        }
        if ((hsv.h >= 60) && (hsv.h < 180)) {
            yx.h = y.h = hsv.h - 40;
            y.s = yx.s = hsv.s;
            y.v = hsv.v;
            if (hsv.v > 70) {
                yx.v = hsv.v - 30;
            } else {
                yx.v = hsv.v + 30;
            }
        }
        if ((hsv.h >= 180) && (hsv.h < 220)) {
            yx.h = hsv.h - 170;
            y.h = hsv.h - 160;
            yx.s = y.s = hsv.s;
            y.v = hsv.v;
            if (hsv.v > 70) {
                yx.v = hsv.v - 30;
            } else {
                yx.v = hsv.v + 30;
            }
        }
        if ((hsv.h >= 220) && (hsv.h < 300)) {
            yx.h = y.h = hsv.h;
            yx.s = y.s = rc(Math.round(hsv.s - 60), 100);
            y.v = hsv.v;
            if (hsv.v > 70) {
                yx.v = hsv.v - 30;
            } else {
                yx.v = hsv.v + 30;
            }
        }
        if (hsv.h >= 300) {
            if (hsv.s > 50) {
                y.s = yx.s = hsv.s - 40;
            } else {
                y.s = yx.s = hsv.s + 40;
            }
            yx.h = y.h = (hsv.h + 20) % 360;
            y.v = hsv.v;
            if (hsv.v > 70) {
                yx.v = hsv.v - 30;
            } else {
                yx.v = hsv.v + 30;
            }
        }

        results[2] = y.toRgb().toColor();

        results[3] = yx.toRgb().toColor();

        y.h = 0;
        y.s = 0;
        y.v = 100 - hsv.v;
        results[4] = y.toRgb().toColor();

        y.h = 0;
        y.s = 0;
        y.v = hsv.v;
        results[5] = y.toRgb().toColor();
    }
}

/* */
