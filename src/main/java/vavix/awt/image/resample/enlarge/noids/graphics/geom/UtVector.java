
package vavix.awt.image.resample.enlarge.noids.graphics.geom;

import java.awt.Point;


public class UtVector {

    /** @return angle [radian] */
    public static double getAngle(double[] hsl1, double[] hsl2) {
        double ml = hsl1[0] * hsl2[0] + hsl1[1] * hsl2[1] + hsl1[2] * hsl2[2];
        double d1 = Math.sqrt(hsl1[0] * hsl1[0] + hsl1[1] * hsl1[1] + hsl1[2] * hsl1[2]);
        double d2 = Math.sqrt(hsl2[0] * hsl2[0] + hsl2[1] * hsl2[1] + hsl2[2] * hsl2[2]);
        if (d1 == 0.0d || d2 == 0.0d)
            return 0.0d;
        else
            return Math.acos(ml / (d1 * d2));
    }

    /** @return hsl */
    public static double[] get_diff(double[] hsl1, double[] hsl2, double[] ret) {
        if (ret == null)
            ret = new double[3];
        ret[0] = hsl2[0] - hsl1[0];
        ret[1] = hsl2[1] - hsl1[1];
        ret[2] = hsl2[2] - hsl1[2];
        return ret;
    }

    public static double[] a(Point.Double p1, Point.Double p2, Vector2d v1, Vector2d v2) {
        return a(p1.x, p1.y, p2, v1, v2);
    }

    public static double[] a(double x, double y, Point.Double p2, Vector2d v1, Vector2d v2) {
        double d2 = v2.x * v1.y - v2.y * v1.x;
        if (d2 == 0.0d)
            return null;
        double d3 = ((x - p2.x) * v1.y - (y - p2.y) * v1.x) / d2;
        double d4;
        if (v1.x != 0.0d)
            d4 = (x - p2.x - d3 * v2.x) / v1.x;
        else
            d4 = (y - p2.y - d3 * v2.y) / v1.y;
        return new double[] {
            d4, d3
        };
    }

    public static void main(String[] args) {
        Point.Double p1 = new Point.Double(10d, 10d);
        Point.Double p2 = new Point.Double(20d, 20d);
        Vector2d v1 = new Vector2d(10f, 10f);
        Vector2d v2 = new Vector2d(0.0f, 10f);
        double[] r = a(p1, p2, v1, v2);
        System.out.println("r[0] : " + r[0]);
        System.out.println("r[1] : " + r[1]);
    }
}
