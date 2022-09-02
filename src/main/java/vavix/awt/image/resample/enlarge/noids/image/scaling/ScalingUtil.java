
package vavix.awt.image.resample.enlarge.noids.image.scaling;

import java.awt.Point;

import vavix.awt.image.resample.enlarge.noids.graphics.color.UtColor;
import vavix.awt.image.resample.enlarge.noids.image.scaling.edge.Edge;
import vavix.awt.image.resample.enlarge.noids.image.scaling.edge.EdgeX;
import vavix.awt.image.resample.enlarge.noids.image.scaling.edge.EdgeY;
import vavix.awt.image.resample.enlarge.noids.image.scaling.line.Line;
import vavix.awt.image.resample.enlarge.noids.image.util.UtPoint;
import vavix.awt.image.resample.enlarge.noids.math.UtAngle;


public class ScalingUtil implements DirectionConstants, Constants {

    private static final double deg30 = 0.52359877559829882d;

    /** @return argb */
    public static int blend(int argb1, int argb2, double rate) {
        int a1 = argb1 >>> 24 & 0xff;
        int r1 = argb1 >>> 16 & 0xff;
        int g1 = argb1 >>> 8 & 0xff;
        int b1 = argb1 & 0xff;
        int a2 = argb2 >>> 24 & 0xff;
        int r2 = argb2 >>> 16 & 0xff;
        int g2 = argb2 >>> 8 & 0xff;
        int b2 = argb2 & 0xff;
        int a = (int) (a1 * rate + a2 * (1.0d - rate));
        int r = (int) (r1 * rate + r2 * (1.0d - rate));
        int g = (int) (g1 * rate + g2 * (1.0d - rate));
        int b = (int) (b1 * rate + b2 * (1.0d - rate));
        if (a < 0)
            a = 0;
        else if (a > 255)
            a = 255;
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
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static int average(int argb1, int argb2, int argb3, int argb4) {
        int a1 = argb1 >>> 24 & 0xff;
        int r1 = argb1 >>> 16 & 0xff;
        int g1 = argb1 >>> 8 & 0xff;
        int b1 = argb1 & 0xff;
        int a2 = argb2 >>> 24 & 0xff;
        int r2 = argb2 >>> 16 & 0xff;
        int g2 = argb2 >>> 8 & 0xff;
        int b2 = argb2 & 0xff;
        int a3 = argb3 >>> 24 & 0xff;
        int r3 = argb3 >>> 16 & 0xff;
        int g3 = argb3 >>> 8 & 0xff;
        int b3 = argb3 & 0xff;
        int a4 = argb4 >>> 24 & 0xff;
        int r4 = argb4 >>> 16 & 0xff;
        int g4 = argb4 >>> 8 & 0xff;
        int b4 = argb4 & 0xff;
        int a = (a1 + a2 + a3 + a4) / 4;
        int r = (r1 + r2 + r3 + r4) / 4;
        int g = (g1 + g2 + g3 + g4) / 4;
        int b = (b1 + b2 + b3 + b4) / 4;
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static int blend(int argb1, int argb2, int argb3, double rate1, double rate2, double rate3) {
        double rate = rate1 + rate2 + rate3;
        int unit = (int) (1000d / rate);
        int rate1_ = (int) (unit * rate1);
        int rate2_ = (int) (unit * rate2);
        int rate3_ = (int) (unit * rate3);
        unit = rate1_ + rate2_ + rate3_;
        int a1 = argb1 >>> 24 & 0xff;
        int r1 = argb1 >>> 16 & 0xff;
        int g1 = argb1 >>> 8 & 0xff;
        int b1 = argb1 & 0xff;
        int a2 = argb2 >>> 24 & 0xff;
        int r2 = argb2 >>> 16 & 0xff;
        int g2 = argb2 >>> 8 & 0xff;
        int b2 = argb2 & 0xff;
        int a3 = argb3 >>> 24 & 0xff;
        int r3 = argb3 >>> 16 & 0xff;
        int g3 = argb3 >>> 8 & 0xff;
        int b3 = argb3 & 0xff;
        int a = (rate1_ * a1 + rate2_ * a2 + rate3_ * a3) / unit;
        int r = (rate1_ * r1 + rate2_ * r2 + rate3_ * r3) / unit;
        int g = (rate1_ * g1 + rate2_ * g2 + rate3_ * g3) / unit;
        int b = (rate1_ * b1 + rate2_ * b2 + rate3_ * b3) / unit;
        return a << 24 | r << 16 | g << 8 | b;
    }

    /** @return argb */
    public static int average(int argb1, int argb2) {
        int a1 = argb1 >>> 24 & 0xff;
        int r1 = argb1 >>> 16 & 0xff;
        int g1 = argb1 >>> 8 & 0xff;
        int b1 = argb1 & 0xff;
        int a2 = argb2 >>> 24 & 0xff;
        int r2 = argb2 >>> 16 & 0xff;
        int g2 = argb2 >>> 8 & 0xff;
        int b2 = argb2 & 0xff;
        int a = (a1 + a2) / 2;
        int r = (r1 + r2) / 2;
        int g = (g1 + g2) / 2;
        int b = (b1 + b2) / 2;
        return a << 24 | r << 16 | g << 8 | b;
    }

    /** @return argb */
    public static int average(int[] colors, int len) {
        if (len == 1)
            return colors[0];
        int a1 = 0;
        int r1 = 0;
        int g1 = 0;
        int b1 = 0;
        for (int i = 0; i < len; i++) {
            a1 += colors[i] >>> 24 & 0xff;
            r1 += colors[i] >>> 16 & 0xff;
            g1 += colors[i] >>> 8 & 0xff;
            b1 += colors[i] & 0xff;
        }

        int a = a1 / len;
        int r = r1 / len;
        int g = g1 / len;
        int b = b1 / len;
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static boolean isNearly(EdgeX edgeX, EdgeY edgeY) {
        double[] sxc = UtColor.flatten(edgeX.getStartColor());
        double[] exc = UtColor.flatten(edgeX.getEndColor());
        double[] syc = UtColor.flatten(edgeY.getStartColor());
        double[] eyc = UtColor.flatten(edgeY.getEndColor());
        return isNearly(sxc, syc, eyc) && isNearly(exc, syc, eyc);
    }

    public static boolean isNearly(EdgeY edgeX, EdgeX edgeY) {
        double[] sxc = UtColor.flatten(edgeX.getStartColor());
        double[] exc = UtColor.flatten(edgeX.getEndColor());
        double[] syc = UtColor.flatten(edgeY.getStartColor());
        double[] eyc = UtColor.flatten(edgeY.getEndColor());
        return isNearly(sxc, syc, eyc) && isNearly(exc, syc, eyc);
    }

    public static boolean isNearly(double[] rgb1, double[] rgb2, double[] rgb3) {
        double r = (rgb1[0] - rgb2[0]) / (rgb3[0] - rgb2[0]);
        if (r < -0.05d || 1.05d < r)
            return false;
        double g = (rgb1[1] - rgb2[1]) / (rgb3[1] - rgb2[1]);
        if (g < -0.05d || 1.05d < g)
            return false;
        double b = (rgb1[2] - rgb2[2]) / (rgb3[2] - rgb2[2]);
        if (b < -0.05d || 1.05d < b)
            return false;
        return UtColor.maxDiff(r, g, b) <= 0.1d;
    }

    public static boolean is_a(double x, double y, Point.Double p1, Point.Double p2, Point.Double p3) {
        boolean flag = is_angle_c(x, y, p1, p2, p3);
        boolean contains = contains(x, y, p1, p2, p3);
        if (flag != contains) {
            contains(x, y, p1, p2, p3);
            throw new UnsupportedOperationException("not implemented yet");
        } else {
            return contains;
        }
    }

    public static boolean contains(double x, double y, Point.Double p1, Point.Double p2, Point.Double p3) {
        Point.Double[] points = {
            p2, p1, p3
        };
        UtPoint util = new UtPoint(points);
        return util.contains(x, y);
    }

    public static boolean is_angle_c(double x, double y, Point.Double p1, Point.Double p2, Point.Double p3) {
        double a1 = getAngle(p1, p2);
        double a2 = getAngle(p1, p3);
        double min = Math.min(a1, a2);
        double max = Math.max(a1, a2);
        boolean f1 = a1 > a2;
        double a = getAngle(p1, x, y);
        boolean f2 = min < a && a < max;
        return f1 ^ f2;
    }

    public static boolean contains_d(double x, double y, Point.Double p1, Point.Double p2, Point.Double p3) {
        double x1 = (p2.x - 2d * p1.x) + p3.x;
        double x2 = -2d * (p2.x - p1.x);
        double x3 = p2.x - x;
        boolean inside;
        if (x1 == 0.0d) {
            if (x2 == 0.0d) {
                if (p2.y < p3.y)
                    inside = p2.x < x;
                else
                    inside = x < p2.x;
            } else {
                double x4 = -x3 / x2;
                if (0.0d <= x4 && x4 <= 1.0d) {
                    double d9 = (1.0d - x4) * (1.0d - x4) * p2.y + 2d * (1.0d - x4) * x4 * p1.y + x4 * x4 * p3.y;
                    if (p2.x < p3.x)
                        inside = y < d9;
                    else
                        inside = y > d9;
                } else {
                    int r = 0;
                    double x21 = (p2.x - x) / (p2.x - p1.x);
                    if (x21 < 0.0d) {
                        double y21 = (1.0d - x21) * p2.y + x21 * p1.y;
                        if (y21 < y)
                            r++;
                    }
                    double x31 = (p3.x - x) / (p3.x - p1.x);
                    if (x31 < 0.0d) {
                        double y31 = (1.0d - x31) * p3.y + x31 * p1.y;
                        if (y31 < y)
                            r++;
                    }
                    if (p2.x < p3.x)
                        return r % 2 == 0;
                    return r % 2 != 0;
                }
            }
        } else if (p2.y == p3.y && p2.y == p1.y) {
            if (p2.x < p3.x)
                inside = y < p2.y;
            else
                inside = y > p2.y;
        } else {
            int r = 0;
            if (p3.x == p1.x) {
                if (p2.x < p3.x) {
                    if (p3.x < x)
                        return p2.y < p3.y;
                } else if (x < p3.x)
                    return p2.y >= p3.y;
            } else if (p2.x == p1.x) {
                if (p2.x < p3.x) {
                    if (x < p2.x)
                        return p2.y >= p3.y;
                } else if (p2.x < x)
                    return p2.y < p3.y;
            } else {
                double x32 = (p3.x - p2.x) / (p3.x - p1.x);
                if (x32 < 0.0d) {
                    double y32 = (1.0d - x32) * p3.y + x32 * p1.y;
                    if (y32 < p2.y)
                        r++;
                }
            }
            if (p1.x < p2.x)
                r++;
            double x21 = -x2 / x1;
            double d12 = (1.0d - x21) * (1.0d - x21) * p2.y + 2d * (1.0d - x21) * x21 * p1.y + x21 * x21 * p3.y;
            if (x21 == 0.0d) {
                if (p3.x < p2.x)
                    r++;
            } else if (0.0d < x21 && x21 <= 1.0d && d12 < p2.y)
                r++;
            boolean result1 = r % 2 == 0;
            double x13 = x2 * x2 - 4d * x1 * x3;
            if (x13 == 0.0d) {
                int r_ = 0;
                double x21_ = (p2.x - x) / (p2.x - p1.x);
                if (x21_ < 0.0D) {
                    double y21 = (1.0d - x21_) * p2.y + x21_ * p1.y;
                    if (y21 < y)
                        r_++;
                }
                double x31 = (p3.x - x) / (p3.x - p1.x);
                if (x31 < 0.0d) {
                    double y31 = (1.0d - x31) * p3.y + x31 * p1.y;
                    if (y31 < y)
                        r_++;
                }
                boolean result2 = r_ % 2 == 0;
                inside = result2 == result1;
            } else if (x13 > 0.0d) {
                double x17 = Math.sqrt(x13);
                double x20 = (-x2 + x17) / (2d * x1);
                double x25 = (-x2 - x17) / (2d * x1);
                double y28 = (1.0d - x20) * (1.0d - x20) * p2.y + 2D * (1.0d - x20) * x20 * p1.y + x20 * x20 * p3.y;
                double y29 = (1.0d - x25) * (1.0d - x25) * p2.y + 2D * (1.0d - x25) * x25 * p1.y + x25 * x25 * p3.y;
                int r_ = 0;
                double x21_ = (p2.x - x) / (p2.x - p1.x);
                if (x21_ <= 0.0d) {
                    double y21 = (1.0d - x21_) * p2.y + x21_ * p1.y;
                    if (y21 < y)
                        r_++;
                }
                double x31 = (p3.x - x) / (p3.x - p1.x);
                if (x31 <= 0.0d) {
                    double y31 = (1.0d - x31) * p3.y + x31 * p1.y;
                    if (y31 < y)
                        r_++;
                }
                if (0.0d < x20 && x20 < 1.0d && y28 < y)
                    r_++;
                if (0.0d < x25 && x25 < 1.0d && y29 < y)
                    r_++;
                boolean result4 = r_ % 2 == 0;
                inside = result4 == result1;
            } else {
                int r_ = 0;
                double x21_ = (p2.x - x) / (p2.x - p1.x);
                if (x21_ < 0.0d) {
                    double y21 = (1.0d - x21_) * p2.y + x21_ * p1.y;
                    if (y21 < y)
                        r_++;
                }
                double x31 = (p3.x - x) / (p3.x - p1.x);
                if (x31 < 0.0d) {
                    double y31 = (1.0d - x31) * p3.y + x31 * p1.y;
                    if (y31 < y)
                        r_++;
                }
                boolean result3 = r_ % 2 == 0;
                inside = result3 == result1;
            }
        }
        return inside;
    }

    static double getAngle(Point.Double p, double x, double y) {
        double angle = Math.atan2(y - p.y, x - p.x);
        if (angle < 0.0d)
            angle += Math.PI * 2;
        return angle;
    }

    static double getAngle(Point.Double p1, Point.Double p2) {
        double angle = Math.atan2(p2.y - p1.y, p2.x - p1.x);
        if (angle < 0.0d)
            angle += Math.PI * 2;
        return angle;
    }

    public static boolean isValid(Edge edge) {
        return edge != null && edge != Edge.dummyEdge;
    }

    public static boolean isSmoothable(Edge edge, int len, double smoothLevel) {
        if (!isValid(edge))
            return false;
        Line line = edge.getLine();
        boolean asc = true;
        boolean connectedNext = line.isConnectedTo(edge, asc, len);
        boolean connectedPrev = line.isConnectedTo(edge, !asc, len);
        if (connectedNext && connectedPrev) {
            double angleNext = line.get_angle_a(edge, !asc);
            double smoothLevelNext = line.getSmoothLevel(edge, asc, len);
            double anglePrev = line.get_angle_a(edge, asc);
            double smoothLevelPrev = line.getSmoothLevel(edge, !asc, len);
            double angle = UtAngle.absDiff(angleNext, anglePrev + Math.PI);
            if ((smoothLevelNext > smoothLevel || smoothLevelPrev > smoothLevel) && angle > deg30)
                return true;
        }
        return false;
    }
}
