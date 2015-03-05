
package jp.noids.image.util;

import java.awt.Point;


/** b */
public class UtPoint {

    int stat1;
    int stat2;

    Point.Double[] points;

    public UtPoint(Point.Double p1, Point.Double p2, Point.Double p3) {
        this(new Point.Double[] {
            p1, p2, p3
        });
    }

    public UtPoint(Point.Double[] ps) {
        stat1 = -1;
        this.points = new Point.Double[ps.length];
        for (int i = 0; i < ps.length; i++)
            points[i] = new Point.Double(ps[i].x, ps[i].y);

        int l = points.length;
        if (l < 2)
            throw new RuntimeException("おかしな状態");
        if (l == 2)
            throw new RuntimeException("未実装");
        double y0 = points[0].y;
        double y1 = points[1].y;
        double y2 = points[l - 1].y;
        double y3 = points[l - 2].y;
        boolean flag = y0 < y1 || y2 < y3;
        boolean flag1 = y0 > y1 || y2 > y3;
        double y5 = flag ? (1.0d / 0.0d) : (-1.0d / 0.0d);
        double y4 = flag1 ? (-1.0d / 0.0d) : (1.0d / 0.0d);
        for (int i = 0; i < points.length; i++) {
            if (points[i].y < y4)
                y4 = points[i].y;
            if (points[i].y > y5)
                y5 = points[i].y;
        }

        int q = -1;
        double x10 = 0.0;
        double y10 = 0.0;
        boolean flag2 = true;
        for (int i = 0; i < points.length - 1; i++) {
            if (points[i].y == points[i + 1].y)
                continue;
            x10 = (points[i].x + points[i + 1].x) / 2;
            y10 = (points[i].y + points[i + 1].y) / 2;
            flag2 = points[i].y < points[i + 1].y;
            q = i;
            break;
        }

        if (q == -1)
            if (points[0].x < points[1].x) {
                stat1 = 0;
                return;
            } else {
                stat1 = 1;
                return;
            }
        int s = -1;
        stat2 = flag2 ? 0 : 1;
        for (int i = 0; i < points.length - 1; i++)
            if (i != q) {
                double x8 = points[i].x;
                double y8 = points[i].y;
                double x9 = points[i + 1].x;
                double y9 = points[i + 1].y;
                if (y8 != y9) {
                    int s_;
                    if (y8 < y9)
                        s_ = 1;
                    else
                        s_ = 0;
                    if (y10 == y8) {
                        if (s_ != s && x10 < x8)
                            stat2++;
                    } else if (y10 == y9) {
                        if (x10 < x9)
                            stat2++;
                    } else if (s_ == 1) {
                        if ((i == 0 || y8 < y10) && (i == l - 2 || y10 < y9)) {
                            double d12 = ((x8 - x9) / (y8 - y9)) * (y10 - y8) + x8;
                            if (x10 < d12)
                                stat2++;
                        }
                    } else if (s_ == 0) {
                        if ((i == l - 2 || y9 < y10) && (i == 0 || y10 < y8)) {
                            double d13 = ((x8 - x9) / (y8 - y9)) * (y10 - y8) + x8;
                            if (x10 < d13)
                                stat2++;
                        }
                    } else {
                        throw new RuntimeException("未実装");
                    }
                    s = s_;
                }
            }

        stat1 = 2;
    }

    public boolean contains(double x, double y) {
        if (stat1 == -1)
            return true;
        if (stat1 == 0)
            return y < points[0].y;
        if (stat1 == 1)
            return y > points[0].y;
        int l = points.length;
        int c = 0;
        int s = -1;
        for (int i = 0; i < points.length - 1; i++) {
            double x1 = points[i].x;
            double y1 = points[i].y;
            double x2 = points[i + 1].x;
            double y2 = points[i + 1].y;
            if (y1 != y2) {
                int s_;
                if (y1 < y2)
                    s_ = 1;
                else
                    s_ = 0;
                if (y == y1) {
                    if (s_ != s && x < x1)
                        c++;
                } else if (y == y2) {
                    if (x < x2)
                        c++;
                } else if (s_ == 1) {
                    if ((i == 0 || y1 < y) && (i == l - 2 || y < y2)) {
                        double d6 = ((x1 - x2) / (y1 - y2)) * (y - y1) + x1;
                        if (x < d6)
                            c++;
                    }
                } else if (s_ == 0) {
                    if ((i == l - 2 || y2 < y) && (i == 0 || y < y1)) {
                        double d7 = ((x1 - x2) / (y1 - y2)) * (y - y1) + x1;
                        if (x < d7)
                            c++;
                    }
                } else {
                    throw new RuntimeException("未実装");
                }
                s = s_;
            }
        }

        return stat2 % 2 == c % 2;
    }
}
