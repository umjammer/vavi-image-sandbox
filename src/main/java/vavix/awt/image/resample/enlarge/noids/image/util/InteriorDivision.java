
package vavix.awt.image.resample.enlarge.noids.image.util;

import vavix.awt.image.resample.enlarge.noids.graphics.color.UtColor;


/** a */
public class InteriorDivision {

    double value1;
    double value2;
    boolean notAvailable;

    public InteriorDivision(int rgb1, int rgb2, int rgb3) {
        this(UtColor.flatten(rgb1), UtColor.flatten(rgb2), UtColor.flatten(rgb3));
    }

    public InteriorDivision(double[] rgb1, double[] rgb2, double[] rgb3) {
        notAvailable = false;
        if (rgb2[0] == rgb3[0] && rgb2[1] == rgb3[1] && rgb2[2] == rgb3[2]) {
            notAvailable = true;
            return;
        }
        double[] rgb21 = UtColor.diff(rgb2, rgb1);
        double[] rgb23 = UtColor.diff(rgb2, rgb3);
        double v1 = UtColor.method3(rgb21, rgb23);
        double v2 = UtColor.distance(rgb23);
        if (v2 == 0.0d) {
            throw new ArithmeticException("division by 0");
        }
        double v3 = v1 / v2;
        double[] rgb = UtColor.method4(rgb21, rgb23);
        double v4 = UtColor.distance(rgb) / v2;
        value2 = v3;
        value1 = v4;
    }

    public boolean is_InRange1(double v1, double v2) {
        if (notAvailable)
            throw new IllegalStateException("couldn't resolve interior division value because of comparing same colors.\n use #isAvailable()");
        return value1 <= v2 && 0.0d - v1 <= value2 && value2 <= 1.0d + v1;
    }

    public boolean is_InRange2(double v) {
        if (notAvailable)
            throw new IllegalStateException("couldn't resolve interior division value because of comparing same colors.\n use #isAvailable()");
        return 0.0d - v <= value2 && value2 <= 1.0d + v;
    }

    public boolean is_InRange3(double v1, double v2) {
        if (notAvailable)
            throw new IllegalStateException("couldn't resolve interior division value because of comparing same colors.\n use #isAvailable()");
        return value1 <= v2 && 1.0d + v1 < value2;
    }

    public boolean isAvailable() {
        return !notAvailable;
    }

    public double get_value1() {
        return value1;
    }

    public double get_value2() {
        return value2;
    }
}
