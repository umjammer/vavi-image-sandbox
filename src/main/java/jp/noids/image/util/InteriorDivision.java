
package jp.noids.image.util;

import jp.noids.graphics.color.UtColor;


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
            throw new RuntimeException("０デバイド");
        }
        double v3 = v1 / v2;
        double[] rgb = UtColor.f(rgb21, rgb23);
        double v4 = UtColor.distance(rgb) / v2;
        value2 = v3;
        value1 = v4;
    }

    public boolean is_InRange1(double v1, double v2) {
        if (notAvailable)
            throw new RuntimeException("内分のC1、C2が同じであるため、内分値が取り出せませんでした .\n   isAvailable()で内分が使用可能かチェックしてから使ってください");
        return value1 <= v2 && 0.0d - v1 <= value2 && value2 <= 1.0d + v1;
    }

    public boolean is_InRange2(double v) {
        if (notAvailable)
            throw new RuntimeException("内分のC1、C2が同じであるため、内分値が取り出せませんでした .\n   isAvailable()で内分が使用可能かチェックしてから使ってください");
        return 0.0d - v <= value2 && value2 <= 1.0d + v;
    }

    public boolean is_InRange3(double v1, double v2) {
        if (notAvailable)
            throw new RuntimeException("内分のC1、C2が同じであるため、内分値が取り出せませんでした .\n   isAvailable()で内分が使用可能かチェックしてから使ってください");
        return value1 <= v2 && 1.0d + v1 < value2;
    }

    public boolean isAbailable() {
        return !notAvailable;
    }

    public double get_value1() {
        return value1;
    }

    public double get_value2() {
        return value2;
    }
}
