
package vavix.awt.image.resample.enlarge.noids.graphics.color;

import java.awt.Color;

import vavix.awt.image.resample.enlarge.noids.util.UtMath;
import vavix.awt.image.resample.enlarge.noids.util.UtString;

import static vavix.awt.image.resample.enlarge.noids.util.UtString.fillZeros;


/** */
public abstract class UtColor {

    private static final double[] table1 = {
        0.0, 10, 20, 40, 70, 180
    };

    private static final double[] table2 = {
        0.0, 0.05, 0.4, 0.8, 1.0, 1.0
    };

    @SuppressWarnings("unused")
    private static final double[] table3 = createTable_c(table1, table2);

    private static final double[] table4 = {
        0.0, 10, 30, 60, 100
    };

    private static final double[] table5 = {
        0.0, 0.05, 0.5, 0.8, 0.9
    };

    @SuppressWarnings("unused")
    private static final double[] table6 = createTable_c(table4, table5);

    private static final double[] table7 = {
        0.0, 10, 20, 40, 60, 100
    };

    private static final double[] table8 = {
        0.0, 0.05, 0.5, 0.8, 0.9, 1.0
    };

    @SuppressWarnings("unused")
    private static final double[] table9 = createTable_c(table7, table8);

    private static final double[] table10 = {
        0.0, 10, 50, 80, 90, 500
    };

    private static final double[] table11 = {
        0.0, 0.05, 0.5, 0.8, 1.0, 1.0
    };

    @SuppressWarnings("unused")
    private static final double[] table12 = createTable_c(table10, table11);

    private static double[] createTable_c(double[] rgb1, double[] rgb2) {
        return UtMath.method_a(rgb1, rgb2, null);
    }

    public static double distance(int rgb1, int rgb2) {
        double[] c1 = flatten(rgb1);
        double[] c2 = flatten(rgb2);
        return Math.sqrt((c1[0] - c2[0]) * (c1[0] - c2[0]) +
                         (c1[1] - c2[1]) * (c1[1] - c2[1]) +
                         (c1[2] - c2[2]) * (c1[2] - c2[2]));
    }

    /** @return rgb */
    public static double[] flatten(int rgb) {
        double r = (rgb >>> 16 & 0xff) / 255D;
        double g = (rgb >>> 8 & 0xff) / 255D;
        double b = (rgb & 0xff) / 255D;
        return new double[] {
            r, g, b
        };
    }

    public static Color toColor(int rgb) {
        return new Color(rgb);
    }

    public static int[] getDefaultColorTable() {
        int[] colors = new int[216];
        int i = 0;
        for (int r = 0; r <= 255; r += 51) {
            for (int g = 0; g <= 255; g += 51) {
                for (int b = 0; b <= 255; b += 51) {
                    int c = r << 16 | g << 8 | b;
                    colors[i++] = c;
                }
            }
        }
        return colors;
    }

    public static String toString(double[] rgb) {
        return fillZeros(rgb[0], 2) + " , " + fillZeros(rgb[1], 2) + " , " + fillZeros(rgb[2], 2);
    }

    public static double[] diff(double[] rgb1, double[] rgb2) {
        return new double[] {
            rgb2[0] - rgb1[0], rgb2[1] - rgb1[1], rgb2[2] - rgb1[2]
        };
    }

    public static double distance(double[] rgb) {
        return rgb[0] * rgb[0] + rgb[1] * rgb[1] + rgb[2] * rgb[2];
    }

    public static double method3(double[] rgb1, double[] rgb2) {
        return rgb1[0] * rgb2[0] + rgb1[1] * rgb2[1] + rgb1[2] * rgb2[2];
    }

    public static double[] method4(double[] rgb1, double[] rgb2) {
        return new double[] {
            rgb1[1] * rgb2[2] - rgb2[1] * rgb1[2], rgb1[2] * rgb2[0] - rgb2[2] * rgb1[0], rgb1[0] * rgb2[1] - rgb2[0] * rgb1[1]
        };
    }

    public static double maxDiff(double r, double g, double b) {
        return Math.max(Math.abs(r - g), Math.max(Math.abs(g - b), Math.abs(b - r)));
    }
}
