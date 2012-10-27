
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
    private static double[] table3 = UtMath.a(table1, table2, (double[]) null);

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
        double d4 = r < g ? g <= b ? b : g : r <= b ? b : r;
        double d3 = r > g ? g >= b ? b : g : r >= b ? b : r;
        ret[2] = (d4 + d3) - 1.0d;
        double d5 = d4 - d3;
        if (d5 == 0.0d) {
            ret[1] = 0.0d;
            ret[0] = 0.0d;
            return ret;
        }
        if (ret[2] <= 0.0d)
            ret[1] = d5 / (d4 + d3);
        else
            ret[1] = d5 / (2d - (d4 + d3));
        double h;
        if (r == d4)
            h = (g - b) / d5;
        else if (g == d4)
            h = 2d + (b - r) / d5;
        else
            h = 4d + (r - g) / d5;
        h *= 60d;
        if (h < 0.0d)
            h += 360d;
        ret[0] = h;
        ret[1] = ret[1] * (1.0D - (ret[2] >= 0.0D ? ret[2] : -ret[2]));
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
        double s_;
        double l_;
        if (l <= 0.0d) {
            s_ = ((l + 1.0d) / 2d) * (1.0d - s);
            l_ = (l + 1.0d) - s_;
        } else {
            l_ = ((l + 1.0d) / 2d) * (1.0d - s) + s;
            s_ = (l + 1.0d) - l_;
        }
        ret[0] = get_b(h + 120d, s_, l_);
        ret[1] = get_b(h, s_, l_);
        ret[2] = get_b(h - 120D, s_, l_);
        return ret;
    }

    static double get_b(double h, double s, double l) {
        h = limit(h, 360d);
        if (h < 0.0d)
            h += 360d;
        if (h < 60d)
            return s + ((l - s) * h) / 60d;
        if (h >= 60d && h < 180d)
            return l;
        if (h >= 180d && h < 240d)
            return s + ((l - s) * (240d - h)) / 60d;
        else
            return s;
    }

    static double limit(double value, double limit) {
        return value % limit;
    }
}
