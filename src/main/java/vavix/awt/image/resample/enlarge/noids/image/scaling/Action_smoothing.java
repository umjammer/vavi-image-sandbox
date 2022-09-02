
package vavix.awt.image.resample.enlarge.noids.image.scaling;

import java.awt.Point;

import vavix.awt.image.resample.enlarge.noids.graphics.color.UtColor;
import vavix.awt.image.resample.enlarge.noids.image.scaling.edge.Edge;
import vavix.awt.image.resample.enlarge.noids.image.scaling.edge.EdgeX;
import vavix.awt.image.resample.enlarge.noids.image.scaling.edge.Edge_g;
import vavix.awt.image.resample.enlarge.noids.image.scaling.line.Corner;
import vavix.awt.image.resample.enlarge.noids.image.scaling.line.Line;
import vavix.awt.image.resample.enlarge.noids.image.util.InteriorDivision;


/** g */
public class Action_smoothing {

    private static final boolean debug = true;

    public static void smooth(EdgeData edgeData) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        if (edgeData.getWidth() * edgeData.getHeight() < 40000) {
            smoothFine(edgeData);
        } else {
            if (debug) {
                System.out.println("skipping smoothing fine because image size is too large");
            }
        }

        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        smoothSimple(edgeData);

        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        smoothColor(edgeData);
    }

    public static void smoothFine(EdgeData edgeData) {
        int sw = edgeData.getWidth();
        int sh = edgeData.getHeight();
        boolean[][] matrix = {
            new boolean[sw * sh], new boolean[sw * sh]
        };
        for (int y = 0; y < sh; y++) {
            for (int x = 0; x < sw; x++) {
                Edge_g edge = (Edge_g) edgeData.getEdgeAt(x, y);
                if (edge != null) {
label0:             for (int i = 0; i < 2; i++) {
                        boolean asc = i == 0;
                        if (matrix[i][x + y * sw])
                            continue;
                        Edge_g edge1 = edge;
                        boolean asc1 = asc;
                        while (true) {
                            Edge_g edge2 = (Edge_g) edge1.nextEdge(asc1);
                            if (!ScalingUtil.isValid((edge2)))
                                continue label0;
                            if (edge2 == edge)
                                break label0;
                            int rgb1 = edge1.get_color_a(asc1, true);
                            int rgb2 = edge1.get_color_a(asc1, false);
                            boolean asc2 = edge2.isConnected(edge1);
                            int rgb3 = edge2.get_color_a(!asc2, true);
                            int rgb4 = edge2.get_color_a(!asc2, false);
                            double[] rgb1_ = UtColor.flatten(rgb1);
                            double[] rgb2_ = UtColor.flatten(rgb2);
                            double[] rgb3_ = UtColor.flatten(rgb3);
                            double[] rgb4_ = UtColor.flatten(rgb4);
                            InteriorDivision id1 = new InteriorDivision(rgb3_, rgb1_, rgb2_);
                            InteriorDivision id2 = new InteriorDivision(rgb4_, rgb1_, rgb2_);
                            if (id1.get_value1() < 0.0025d && id2.get_value1() < 0.0025d) {
                                final double X = 0.001d;
                                boolean inRange1 = id1.is_InRange2(X);
                                boolean inRange2 = id2.is_InRange2(X);
                                if (inRange1 && inRange2) {
                                    Point p1 = edge2.get_point_b(!asc2, true);
                                    Point p2 = edge2.get_point_b(!asc2, false);
                                    Point.Double p3 = edge2.get_point1();
                                    double rate1;
                                    double rate2;
                                    if (edge2.isHorizontal()) {
                                        if (edgeData.contains(p1.x - 1, p1.y))
                                            rate1 = 0.95d;
                                        else if (edgeData.contains(p1.x - 2, p1.y))
                                            rate1 = 0.7d;
                                        else
                                            rate1 = 0.0001d;
                                        if (edgeData.contains(p2.x + 1, p2.y))
                                            rate2 = 0.95d;
                                        else if (edgeData.contains(p2.x + 2, p2.y))
                                            rate2 = 0.7d;
                                        else
                                            rate2 = 0.0001d;
                                    } else {
                                        if (edgeData.contains(p1.x, p1.y - 1))
                                            rate1 = 0.95d;
                                        else if (edgeData.contains(p1.x, p1.y - 2))
                                            rate1 = 0.7d;
                                        else
                                            rate1 = 0.0001d;
                                        if (edgeData.contains(p2.x, p2.y + 1))
                                            rate2 = 0.95d;
                                        else if (edgeData.contains(p2.x, p2.y + 2))
                                            rate2 = 0.7d;
                                        else
                                            rate2 = 0.0001d;
                                    }
                                    int argb31 = ScalingUtil.blend(rgb3, rgb1, 1.0d - rate1);
                                    int argb42 = ScalingUtil.blend(rgb4, rgb2, 1.0d - rate2);
                                    rgb1 = argb31;
                                    double[] rgb11_ = UtColor.flatten(rgb1);
                                    InteriorDivision id11 = new InteriorDivision(rgb3_, rgb11_, rgb2_);
                                    rgb2 = argb42;
                                    rgb2_ = UtColor.flatten(rgb2);
                                    InteriorDivision id22 = new InteriorDivision(rgb4_, rgb11_, rgb2_);
                                    double id111 = id11.get_value2();
                                    double id222 = id22.get_value2();
                                    boolean flag5 = 0.0d - X < id111 && id111 < 1.0d + X;
                                    boolean flag6 = 0.0d - X < id222 && id222 < 1.0d + X;
                                    if (flag5 && flag6) {
                                        edge2.set_color_a(!asc2, true, argb31);
                                        edge2.set_color_a(!asc2, false, argb42);
                                        if (edge2.isHorizontal()) {
                                            double x1 = (p3.x - (p1.x + 0.5d)) / (p2.x - p1.x);
                                            double x2 = x1 * id111 + (1.0d - x1) * id222;
                                            double x3 = x2 * (p1.x + 0.5d) + (1.0d - x2) * (p2.x + 0.5d);
                                            edge2.moveBit(x3, p3.y);
                                        } else {
                                            double y1 = (p3.y - (p1.y + 0.5d)) / (p2.y - p1.y);
                                            double y2 = y1 * id111 + (1.0d - y1) * id222;
                                            double y3 = y2 * (p1.y + 0.5d) + (1.0d - y2) * (p2.y + 0.5d);
                                            edge2.moveBit(p3.x, y3);
                                        }
                                    }
                                }
                            }
                            Point.Double p = edge2.get_point1();
                            int ix = (int) p.x;
                            int iy = (int) p.y;
                            try {
                                matrix[asc2 ? 0 : 1][ix + iy * sw] = true;
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                                System.err.println("ix + iy*sw : "+ (ix + iy * sw));
                                System.err.println("ix : " + ix);
                                System.err.println("iy : " + iy);
                                System.err.println("sw : " + sw);
                            }
                            asc1 = !asc2;
                            edge1 = edge2;
                        }
                    }
                }
            }
        }
    }

    public static void smoothSimple(EdgeData edgeData) {
        int w = edgeData.getWidth();
        int h = edgeData.getHeight();
//        boolean[] flags = new boolean[w * h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Edge edge = edgeData.getEdgeAt(x, y);
                if (edge != null) {
                    Edge edge2 = edge.nextEdge(true);
                    Edge edge3 = edge.nextEdge(false);
                    if (ScalingUtil.isValid(edge2) && ScalingUtil.isValid(edge3)) {
                        Point.Double p1 = edge.get_point1();
                        Point.Double p2 = edge2.get_point1();
                        Point.Double p3 = edge3.get_point1();
                        final double v = 6d;
                        double x1 = (p1.x * v + p2.x + p3.x) / (v + 2d);
                        double y1 = (p1.y * v + p2.y + p3.y) / (v + 2d);
                        if (edge instanceof EdgeX)
                            edge.moveBit(x1, y1);
                        else
                            edge.moveBit(x1, y1);
                    }
                }
            }
        }
    }

    public static void smoothColor(EdgeData edgeData) {
        Line[] lines = edgeData.getLines();
        for (Line line : lines) {
            Corner util = new Corner(line);
            Edge_g edge1 = (Edge_g) util.get_edge1();
            boolean connected = line.isConnected1();
            Edge_g edge11 = (Edge_g) edge1.nextEdge(!connected);
            int color01 = edge1.get_color_a(connected, true);
            int color02 = edge1.get_color_a(connected, false);
            int color11;
            int color31;
            if (ScalingUtil.isValid((edge11))) {
                color11 = edge11.get_color_a(edge11.isConnected(edge1), true);
                color31 = edge11.get_color_a(edge11.isConnected(edge1), false);
            } else {
                color11 = 0xff_00ff00;
                color31 = 0xff_00ff00;
            }
            Edge_g edge2;
            while ((edge2 = (Edge_g) util.get_edge2()) != null) {
                boolean flag1 = util.get_flag();
                Edge_g edge3 = (Edge_g) edge2.nextEdge(flag1);
                Edge_g edge4 = (Edge_g) edge2.nextEdge(!flag1);
                if (!ScalingUtil.isValid((edge3)))
                    break;
                if (!ScalingUtil.isValid((edge4))) {
                    color11 = edge2.getStartColor();
                    color31 = edge2.getEndColor();
                } else {
                    int color21 = edge2.get_color_a(flag1, true);
                    int color41 = edge2.get_color_a(flag1, false);
                    int color12;
                    int color32;
                    if (edge3 == edge1) {
                        color12 = color01;
                        color32 = color02;
                    } else {
                        color12 = edge3.get_color_a(!edge3.isConnected(edge2), true);
                        color32 = edge3.get_color_a(!edge3.isConnected(edge2), false);
                    }
                    double rate = 0.5d;
                    int color22 = ScalingUtil.blend(color11, color12, 0.5d);
                    int color5 = ScalingUtil.blend(color21, color22, rate);
                    int color42 = ScalingUtil.blend(color31, color32, 0.5d);
                    int color6 = ScalingUtil.blend(color41, color42, rate);
                    color11 = color21;
                    color31 = color41;
                    edge2.set_color_a(flag1, true, color5);
                    edge2.set_color_a(flag1, false, color6);
                    @SuppressWarnings("unused")
                    Edge_g edge5 = edge2;
                }
            }
        }
    }

    static {
        if (debug)
            System.out.println(" < DEBUG : vavix.awt.image.resample.enlarge.noids.image.scaling.Action_smoothing > ");
    }
}
