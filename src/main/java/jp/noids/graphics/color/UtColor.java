
package jp.noids.graphics.color;

import java.awt.Color;

import jp.noids.util.UtMath;
import jp.noids.util.UtString;


/** */
public abstract class UtColor {

    private static double[] table1 = {
        0.0, 10, 20, 40, 70, 180
    };

    private static double[] table2 = {
        0.0, 0.05, 0.4, 0.8, 1.0, 1.0
    };

    @SuppressWarnings("unused")
    private static double[] table3 = c(table1, table2);

    private static double[] table4 = {
        0.0, 10, 30, 60, 100
    };

    private static double[] table5 = {
        0.0, 0.05, 0.5, 0.8, 0.9
    };

    @SuppressWarnings("unused")
    private static double[] table6 = c(table4, table5);

    private static double[] table7 = {
        0.0, 10, 20, 40, 60, 100
    };

    private static double[] table8 = {
        0.0, 0.05, 0.5, 0.8, 0.9, 1.0
    };

    @SuppressWarnings("unused")
    private static double[] table9 = c(table7, table8);

    private static double[] table10 = {
        0.0, 10, 50, 80, 90, 500
    };

    private static double[] table11 = {
        0.0, 0.05, 0.5, 0.8, 1.0, 1.0
    };

    @SuppressWarnings("unused")
    private static double[] table12 = c(table10, table11);

    private static double[] c(double[] rgb1, double[] rgb2) {
        return UtMath.mathod_a(rgb1, rgb2, (double[]) null);
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

    public static void debug(double[] rgb) {
        System.out.println("  " + UtString.fillZeros(rgb[0], 2) + " , " + UtString.fillZeros(rgb[1], 2) + " , " + UtString.fillZeros(rgb[2], 2));
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

    public static double[] f(double[] rgb1, double[] rgb2) {
        return new double[] {
            rgb1[1] * rgb2[2] - rgb2[1] * rgb1[2], rgb1[2] * rgb2[0] - rgb2[2] * rgb1[0], rgb1[0] * rgb2[1] - rgb2[0] * rgb1[1]
        };
    }

    public static double maxDiff(double r, double g, double b) {
        return Math.max(Math.abs(r - g), Math.max(Math.abs(g - b), Math.abs(b - r)));
    }
}
