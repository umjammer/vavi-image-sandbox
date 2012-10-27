
package jp.noids.image.scaling;

import java.awt.Point;

import jp.noids.image.scaling.edge.Edge;
import jp.noids.image.scaling.line.Line;
import jp.noids.image.scaling.line.UtLine;
import jp.noids.image.scaling.line.Class_b;
import jp.noids.math.FMath;


/** e */
public class Action_makeCorner implements Constants {

    public static void makeCorner(EdgeData edgeData) {
        Line[] lines = edgeData.getLines();
        for (int i = 0; i < lines.length; i++)
            if (lines[i] != null) {
                Line line = lines[i];
                if (line.getLength() >= (byte) 3) {
                    Class_b b1 = new Class_b(line);
                    Edge edge;
                    boolean flag;
                    if (line.isOpen()) {
                        edge = null;
                        flag = false;
                    } else {
                        edge = line.get_nextEdge();
                        flag = ScalingUtil.is_b(edge, 4, 0.4d);
                    }
                    boolean[] aflag = new boolean[1];
                    boolean[] aflag1 = new boolean[1];
                    Edge edge1;
                    while ((edge1 = b1.get_edge2()) != null) {
                        boolean flag1 = b1.get_flag();
                        if (!ScalingUtil.isValid(edge1) || !line.is_b(edge1, flag1, 2))
                            break;
                        if (flag) {
                            flag = false;
                            edge = edge1;
                        } else {
                            Edge edge6 = UtLine.a(edge1, flag1, 1, aflag);
                            boolean flag2 = ScalingUtil.is_b(edge1, 4, 0.4d);
                            if (!flag2) {
                                flag = false;
                                edge = edge1;
                            } else {
                                boolean flag3 = ScalingUtil.is_b(edge6, 4, 0.4d);
                                Edge edge2 = null;
                                Edge edge3 = null;
                                Edge edge4 = null;
                                boolean flag4 = true;
                                boolean flag5 = true;
                                boolean flag6 = false;
                                if (flag3) {
                                    Edge edge5 = UtLine.a(edge1, flag1, 2, aflag1);
                                    boolean flag7 = ScalingUtil.is_b(edge5, 4, 0.4d);
                                    if (!flag7) {
                                        edge2 = null;
                                        edge3 = edge1;
                                        edge4 = edge6;
                                        flag4 = flag1;
                                        flag5 = aflag[0];
                                        flag6 = true;
                                    }
                                } else {
                                    edge2 = edge1;
                                    edge3 = edge;
                                    edge4 = edge6;
                                    flag4 = flag1;
                                    flag5 = aflag[0];
                                    flag6 = true;
                                }
                                if (flag6) {
                                    double d2 = line.get_angle_a(edge3, flag4);
                                    Point.Double p1 = edge3.get_point1();
                                    double d3 = line.get_angle_a(edge4, flag5);
                                    Point.Double p2 = edge4.get_point1();
                                    Point.Double p3 = a(d2, p1, d3, p2);
                                    double d4 = p1.distance(p3);
                                    double d5 = p2.distance(p3);
                                    if (p3 != null &&
                                        p3.x >= 0.0D &&
                                        p3.y >= 0.0D &&
                                        p3.x < edgeData.getWidth() &&
                                        p3.y < edgeData.getHeight() &&
                                        d4 < 2d &&
                                        d5 < 2d)
                                        if (edge2 == null) {
                                            if (d4 < d5) {
                                                edge1.moveBit(p3.x, p3.y);
                                                edge1.set_flag1(true);
                                            } else {
                                                edge6.moveBit(p3.x, p3.y);
                                                edge6.set_flag1(true);
                                            }
                                        } else {
                                            edge2.moveBit(p3.x, p3.y);
                                            edge2.set_flag1(true);
                                        }
                                }
                                flag = flag2;
                                edge = edge1;
                            }
                        }
                    }
                }
            }
    }

    private static Point.Double a(double d1, Point.Double p1, double d2, Point.Double p2) {
        double x1 = FMath.cos(d1);
        double y1 = FMath.sin(d1);
        double x2 = FMath.cos(d2);
        double y2 = FMath.sin(d2);
        double d = x2 * y1 - y2 * x1;
        if (d == 0.0d) {
            return null;
        } else {
            double d0 = (p1.x * y1 - p1.y * x1 - (p2.x * y1 - p2.y * x1)) / d;
            Point.Double p = new Point.Double();
            p.x = p2.x + d0 * x2;
            p.y = p2.y + d0 * y2;
            return p;
        }
    }
}
