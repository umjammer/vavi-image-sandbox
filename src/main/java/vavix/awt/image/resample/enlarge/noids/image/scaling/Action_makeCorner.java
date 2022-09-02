
package vavix.awt.image.resample.enlarge.noids.image.scaling;

import java.awt.Point;

import vavix.awt.image.resample.enlarge.noids.image.scaling.edge.Edge;
import vavix.awt.image.resample.enlarge.noids.image.scaling.line.Corner;
import vavix.awt.image.resample.enlarge.noids.image.scaling.line.Line;
import vavix.awt.image.resample.enlarge.noids.image.scaling.line.UtLine;
import vavix.awt.image.resample.enlarge.noids.math.FMath;


/** e */
public class Action_makeCorner {

    public static void makeCorner(EdgeData edgeData) {
        Line[] lines = edgeData.getLines();
        for (Line value : lines)
            if (value != null) {
                Line line = value;
                if (line.getLength() >= 3) {
                    Corner corner = new Corner(line);
                    Edge edge;
                    boolean smoothable;
                    if (line.isOpen()) {
                        edge = null;
                        smoothable = false;
                    } else {
                        edge = line.get_nextEdge();
                        smoothable = ScalingUtil.isSmoothable(edge, 4, 0.4d);
                    }
                    boolean[] connected1 = new boolean[1];
                    boolean[] connected2 = new boolean[1];
                    Edge edge1;
                    while ((edge1 = corner.get_edge2()) != null) {
                        boolean asc1 = corner.get_flag();
                        if (!ScalingUtil.isValid(edge1) || !line.isConnectedTo(edge1, asc1, 2))
                            break;
                        if (smoothable) {
                            smoothable = false;
                            edge = edge1;
                        } else {
                            Edge edge6 = UtLine.getEdge_a(edge1, asc1, 1, connected1);
                            boolean smoothable1 = ScalingUtil.isSmoothable(edge1, 4, 0.4d);
                            if (!smoothable1) {
                                smoothable = false;
                                edge = edge1;
                            } else {
                                boolean smoothable6 = ScalingUtil.isSmoothable(edge6, 4, 0.4d);
                                Edge edge2 = null;
                                Edge edge11 = null;
                                Edge edge66 = null;
                                boolean asc4 = true;
                                boolean connected11 = true;
                                boolean flag6 = false;
                                if (smoothable6) {
                                    Edge edge5 = UtLine.getEdge_a(edge1, asc1, 2, connected2);
                                    boolean smoothable5 = ScalingUtil.isSmoothable(edge5, 4, 0.4d);
                                    if (!smoothable5) {
                                        edge2 = null;
                                        edge11 = edge1;
                                        edge66 = edge6;
                                        asc4 = asc1;
                                        connected11 = connected1[0];
                                        flag6 = true;
                                    }
                                } else {
                                    edge2 = edge1;
                                    edge11 = edge;
                                    edge66 = edge6;
                                    asc4 = asc1;
                                    connected11 = connected1[0];
                                    flag6 = true;
                                }
                                if (flag6) {
                                    double a11 = line.get_angle_a(edge11, asc4);
                                    Point.Double p11 = edge11.get_point1();
                                    double a66 = line.get_angle_a(edge66, connected11);
                                    Point.Double p66 = edge66.get_point1();
                                    Point.Double pa = getPoint_a(a11, p11, a66, p66);
                                    double d11 = p11.distance(pa);
                                    double d66 = p66.distance(pa);
                                    if (pa != null &&
                                            pa.x >= 0.0d &&
                                            pa.y >= 0.0d &&
                                            pa.x < edgeData.getWidth() &&
                                            pa.y < edgeData.getHeight() &&
                                            d11 < 2d &&
                                            d66 < 2d)
                                        if (edge2 == null) {
                                            if (d11 < d66) {
                                                edge1.moveBit(pa.x, pa.y);
                                                edge1.set_flag1(true);
                                            } else {
                                                edge6.moveBit(pa.x, pa.y);
                                                edge6.set_flag1(true);
                                            }
                                        } else {
                                            edge2.moveBit(pa.x, pa.y);
                                            edge2.set_flag1(true);
                                        }
                                }
                                smoothable = smoothable1;
                                edge = edge1;
                            }
                        }
                    }
                }
            }
    }

    private static Point.Double getPoint_a(double angle1, Point.Double p1, double angle2, Point.Double p2) {
        double x1 = FMath.cos(angle1);
        double y1 = FMath.sin(angle1);
        double x2 = FMath.cos(angle2);
        double y2 = FMath.sin(angle2);
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
