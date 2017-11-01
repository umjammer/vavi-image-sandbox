
package jp.noids.graphics.color;

import java.awt.Color;

import jp.noids.math.FMath;
import jp.noids.util.UtMath;
import jp.noids.util.UtString;


public abstract class HSL {

    private static double[] table1 = {
        0.0, 0.1, 0.2, 0.4, 0.5, 1.0
    };

    private static double[] table2 = {
        1.0, 0.9, 0.5, 0.2, 0.0, 0.0
    };

    @SuppressWarnings("unused")
    private static double[] table3 = UtMath.mathod_a(table1, table2, (double[]) null);

    public static void main(String[] args) {
        int[] colors = UtColor.getDefaultColorTable();
        for (int i = 0; i < colors.length; i++) {
            int c = colors[i];
            double[] hsl = toHsl(c, (double[]) null);
            System.out.print("0x" + UtString.toHex(c) + " : ");
            UtColor.debug(hsl);
            int rgb = toRgb(hsl[0], hsl[1], hsl[2]);
            System.out.println("    RGB : " + UtString.toHexString_(rgb));
        }
    }

    public static double get_value_c(int rgb1, int rgb2) {
        double[] hsl1 = toHsl(rgb1, (double[]) null);
        double[] hsl2 = toHsl(rgb2, (double[]) null);
        return get_value_a(hsl1, hsl2);
    }

    public static double get_value_a(double[] hsl1, double[] hsl2) {
        double a1 = (Math.PI * hsl1[0]) / 180d;
        double x1 = hsl1[1] * FMath.cos(a1);
        double y1 = hsl1[1] * FMath.sin(a1);
        double a2 = (Math.PI * hsl2[0]) / 180d;
        double x2 = hsl2[1] * FMath.cos(a2);
        double y2 = hsl2[1] * FMath.sin(a2);
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (hsl1[2] - hsl2[2]) * (hsl1[2] - hsl2[2])) / 2d;
    }

    /** @return hsl */
    public static double[] getHsl_b(double[] hsl, double[] ret) {
        if (ret == null)
            ret = new double[3];
        double a = (Math.PI * hsl[0]) / 180d;
        ret[0] = hsl[1] * FMath.cos(a);
        ret[1] = hsl[1] * FMath.sin(a);
        ret[2] = hsl[2];
        return ret;
    }

    public static double[] toHsl(int rgb, double[] ret) {
        return toHsl((rgb >>> 16 & 0xff) / 255d, (rgb >>> 8 & 0xff) / 255d, (rgb & 0xff) / 255d, ret);
    }

    public static double[] toHsl(Color color, double[] ret) {
        int rgb = color.getRGB();
        return toHsl((rgb >>> 16 & 0xff) / 255d, (rgb >>> 8 & 0xff) / 255d, (rgb & 0xff) / 255d, ret);
    }

    public static double[] toHsl(double r, double g, double b, double[] ret) {
        if (ret == null)
            ret = new double[3];
        double max = r < g ? g <= b ? b : g : r <= b ? b : r;
        double min = r > g ? g >= b ? b : g : r >= b ? b : r;
        ret[2] = (max + min) - 1.0d;
        double delta = max - min;
        if (delta == 0.0d) {
            ret[1] = 0.0d;
            ret[0] = 0.0d;
            return ret;
        }
        if (ret[2] <= 0.0d)
            ret[1] = delta / (max + min);
        else
            ret[1] = delta / (2d - (max + min));
        double h;
        if (r == max)
            h = (g - b) / delta;
        else if (g == max)
            h = 2d + (b - r) / delta;
        else
            h = 4d + (r - g) / delta;
        h *= 60d;
        if (h < 0.0d)
            h += 360d;
        ret[0] = h;
        ret[1] = ret[1] * (1.0d - (ret[2] >= 0.0d ? ret[2] : -ret[2]));
        return ret;
    }

    public static int toRgb(double h, double s, double l) {
        double[] ret = toRgb(h, s, l, (double[]) null);
        int r = (int) (0.5d + ret[0] * 255d);
        int g = (int) (0.5d + ret[1] * 255d);
        int b = (int) (0.5d + ret[2] * 255d);
        if (r < 0)
            r = 0;
        else if (r > 255)
            r = 255;
        if (g < 0)
            g = 0;
        else if (g > 255)
            g = 255;
        if (b < 0)
            b = 0;
        else if (b > 255)
            b = 255;
        return r << 16 | g << 8 | b;
    }

    public static double[] toRgb(double h, double s, double l, double[] ret) {
        if (ret == null)
            ret = new double[3];
        double il = 1.0d - (l >= 0.0d ? l : -l);
        s = il != 0.0d ? s / il : s;
        if (l < -1d)
            l = -1d;
        if (l > 1.0d)
            l = 1.0d;
        if (s < 0.0d)
            s = 0.0d;
        if (s > 1.0d)
            s = 1.0d;
        h = limit(h, 360d);
        if (h < 0.0d)
            h += 360d;
        double m1;
        double m2;
        if (l <= 0.0d) {
            m1 = ((l + 1.0d) / 2d) * (1.0d - s);
            m2 = (l + 1.0d) - m1;
        } else {
            m2 = ((l + 1.0d) / 2d) * (1.0d - s) + s;
            m1 = (l + 1.0d) - m2;
        }
        ret[0] = getRgbValue(h + 120d, m1, m2);
        ret[1] = getRgbValue(h, m1, m2);
        ret[2] = getRgbValue(h - 120d, m1, m2);
        return ret;
    }

    static double getRgbValue(double h, double n1, double n2) {
        h = limit(h, 360d);
        if (h < 0.0d)
            h += 360d;
        if (h < 60d)
            return n1 + ((n2 - n1) * h) / 60d;
        if (h >= 60d && h < 180d)
            return n2;
        if (h >= 180d && h < 240d)
            return n1 + ((n2 - n1) * (240d - h)) / 60d;
        else
            return n1;
    }

    static double limit(double value, double limit) {
        return value % limit;
    }
}
