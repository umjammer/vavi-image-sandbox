
package jp.noids.image.scaling;

import java.awt.Point;

import jp.noids.graphics.color.UtColor;
import jp.noids.image.scaling.edge.Edge;
import jp.noids.image.scaling.edge.EdgeX;
import jp.noids.image.scaling.edge.EdgeY;
import jp.noids.image.scaling.line.Line;
import jp.noids.image.util.UtPoint;
import jp.noids.math.UtAngle;


public class ScalingUtil implements DirectionConstants, Constants {

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
        double[] sxc = UtColor.toFlat(edgeX.getStartColor());
        double[] exc = UtColor.toFlat(edgeX.getEndColor());
        double[] syc = UtColor.toFlat(edgeY.getStartColor());
        double[] eyc = UtColor.toFlat(edgeY.getEndColor());
        return isNearly(sxc, syc, eyc) && isNearly(exc, syc, eyc);
    }

    public static boolean isNearly(EdgeY edgeX, EdgeX edgeY) {
        double[] sxc = UtColor.toFlat(edgeX.getStartColor());
        double[] exc = UtColor.toFlat(edgeX.getEndColor());
        double[] syc = UtColor.toFlat(edgeY.getStartColor());
        double[] eyc = UtColor.toFlat(edgeY.getEndColor());
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
        boolean flag = is_c(x, y, p1, p2, p3);
        boolean flag1 = is_h(x, y, p1, p2, p3);
        if (flag != flag1) {
            is_h(x, y, p1, p2, p3);
            throw new RuntimeException("未実装");
        } else {
            return flag1;
        }
    }

    public static boolean is_h(double x, double y, Point.Double p1, Point.Double p2, Point.Double p3) {
        Point.Double[] points = {
            p2, p1, p3
        };
        UtPoint util = new UtPoint(points);
        return util.is_h(x, y);
    }

    public static boolean is_c(double x, double y, Point.Double p1, Point.Double p2, Point.Double p3) {
        double a1 = getAngle(p1, p2);
        double a2 = getAngle(p1, p3);
        double min = a1 >= a2 ? a2 : a1;
        double max = a1 >= a2 ? a1 : a2;
        boolean f1 = a1 > a2;
        double a = getAngle(p1, x, y);
        boolean f2 = min < a && a < max;
        return f1 ^ f2;
    }

    public static boolean is_d(double x, double y, Point.Double p1, Point.Double p2, Point.Double p3) {
        double x1 = (p2.x - 2d * p1.x) + p3.x;
        double x2 = -2d * (p2.x - p1.x);
        double x3 = p2.x - x;
        boolean flag;
        if (x1 == 0.0d) {
            if (x2 == 0.0d) {
                if (p2.y < p3.y)
                    flag = p2.x < x;
                else
                    flag = x < p2.x;
            } else {
                double x4 = -x3 / x2;
                if (0.0d <= x4 && x4 <= 1.0d) {
                    double d9 = (1.0d - x4) * (1.0d - x4) * p2.y + 2d * (1.0d - x4) * x4 * p1.y + x4 * x4 * p3.y;
                    if (p2.x < p3.x)
                        flag = y < d9;
                    else
                        flag = y > d9;
                } else {
                    int j = 0;
                    double d10 = (p2.x - x) / (p2.x - p1.x);
                    if (d10 < 0.0d) {
                        double d13 = (1.0d - d10) * p2.y + d10 * p1.y;
                        if (d13 < y)
                            j++;
                    }
                    double d14 = (p3.x - x) / (p3.x - p1.x);
                    if (d14 < 0.0D) {
                        double d16 = (1.0d - d14) * p3.y + d14 * p1.y;
                        if (d16 < y)
                            j++;
                    }
                    if (p2.x < p3.x)
                        return j % 2 == 0;
                    return j % 2 != 0;
                }
            }
        } else if (p2.y == p3.y && p2.y == p1.y) {
            if (p2.x < p3.x)
                flag = y < p2.y;
            else
                flag = y > p2.y;
        } else {
            int i = 0;
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
                double d7 = (p3.x - p2.x) / (p3.x - p1.x);
                if (d7 < 0.0D) {
                    double d11 = (1.0D - d7) * p3.y + d7 * p1.y;
                    if (d11 < p2.y)
                        i++;
                }
            }
            if (p1.x < p2.x)
                i++;
            double d8 = -x2 / x1;
            double d12 = (1.0d - d8) * (1.0d - d8) * p2.y + 2d * (1.0d - d8) * d8 * p1.y + d8 * d8 * p3.y;
            if (d8 == 0.0d) {
                if (p3.x < p2.x)
                    i++;
            } else if (0.0d < d8 && d8 <= 1.0D && d12 < p2.y)
                i++;
            boolean flag1 = i % 2 == 0;
            double d15 = x2 * x2 - 4D * x1 * x3;
            if (d15 == 0.0d) {
                int l = 0;
                double d18 = (p2.x - x) / (p2.x - p1.x);
                if (d18 < 0.0D) {
                    double d21 = (1.0d - d18) * p2.y + d18 * p1.y;
                    if (d21 < y)
                        l++;
                }
                double d22 = (p3.x - x) / (p3.x - p1.x);
                if (d22 < 0.0d) {
                    double d26 = (1.0d - d22) * p3.y + d22 * p1.y;
                    if (d26 < y)
                        l++;
                }
                boolean flag2 = l % 2 == 0;
                flag = flag2 == flag1;
            } else if (d15 > 0.0d) {
                double d17 = Math.sqrt(d15);
                double d20 = (-x2 + d17) / (2d * x1);
                double d25 = (-x2 - d17) / (2d * x1);
                double d28 = (1.0d - d20) * (1.0d - d20) * p2.y + 2D * (1.0d - d20) * d20 * p1.y + d20 * d20 * p3.y;
                double d29 = (1.0d - d25) * (1.0d - d25) * p2.y + 2D * (1.0d - d25) * d25 * p1.y + d25 * d25 * p3.y;
                int j1 = 0;
                double d30 = (p2.x - x) / (p2.x - p1.x);
                if (d30 <= 0.0d) {
                    double d31 = (1.0d - d30) * p2.y + d30 * p1.y;
                    if (d31 < y)
                        j1++;
                }
                double d32 = (p3.x - x) / (p3.x - p1.x);
                if (d32 <= 0.0d) {
                    double d33 = (1.0d - d32) * p3.y + d32 * p1.y;
                    if (d33 < y)
                        j1++;
                }
                if (0.0D < d20 && d20 < 1.0D && d28 < y)
                    j1++;
                if (0.0D < d25 && d25 < 1.0D && d29 < y)
                    j1++;
                boolean flag4 = j1 % 2 == 0;
                flag = flag4 == flag1;
            } else {
                int i1 = 0;
                double d19 = (p2.x - x) / (p2.x - p1.x);
                if (d19 < 0.0d) {
                    double d23 = (1.0d - d19) * p2.y + d19 * p1.y;
                    if (d23 < y)
                        i1++;
                }
                double d24 = (p3.x - x) / (p3.x - p1.x);
                if (d24 < 0.0D) {
                    double d27 = (1.0d - d24) * p3.y + d24 * p1.y;
                    if (d27 < y)
                        i1++;
                }
                boolean flag3 = i1 % 2 == 0;
                flag = flag3 == flag1;
            }
        }
        return flag;
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

    public static boolean is_b(Edge edge, int len, double smoothLevel) {
        if (!isValid(edge))
            return false;
        Line line = edge.getLine();
        boolean flag = true;
        boolean flag1 = line.is_b(edge, flag, len);
        boolean flag2 = line.is_b(edge, !flag, len);
        if (flag1 && flag2) {
            double angle1 = line.get_angle_a(edge, !flag);
            double smoothLevel1 = line.getSmoothLevel(edge, flag, len);
            double angle2 = line.get_angle_a(edge, flag);
            double smoothLevel2 = line.getSmoothLevel(edge, !flag, len);
            double angle = UtAngle.absDiff(angle1, angle2 + Math.PI);
            if ((smoothLevel1 > smoothLevel || smoothLevel2 > smoothLevel) && angle > 0.52359877559829882d)
                return true;
        }
        return false;
    }
}
