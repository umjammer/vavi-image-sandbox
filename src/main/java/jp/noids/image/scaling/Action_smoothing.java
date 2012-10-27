
package jp.noids.image.scaling;

import java.awt.Point;

import jp.noids.graphics.color.UtColor;
import jp.noids.image.scaling.edge.Edge;
import jp.noids.image.scaling.edge.EdgeX;
import jp.noids.image.scaling.edge.EdgeY;
import jp.noids.image.scaling.edge.Edge_g;
import jp.noids.image.scaling.line.Line;
import jp.noids.image.scaling.line.Class_b;
import jp.noids.image.util.Class_a;


/** g */
public class Action_smoothing implements DirectionConstants, Constants {

    private static boolean debug = true;

    public static void smooth(EdgeData edgeData) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        if (edgeData.getWidth() * edgeData.getHeight() < 40000) {
            smoothFine(edgeData);
        } else {
            if (debug) {
                System.out.println("smoothing fine は画素数が大きいためスキップ");
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
        boolean[][] aflag = {
            new boolean[sw * sh], new boolean[sw * sh]
        };
        for (int y = 0; y < sh; y++) {
            for (int x = 0; x < sw; x++) {
                Edge_g edge = (Edge_g) edgeData.getEdgeAt(x, y);
                if (edge != null) {
label0:             for (int i = 0; i < 2; i++) {
                        boolean flag = i == 0;
                        if (aflag[i][x + y * sw])
                            continue;
                        Edge_g edge1 = edge;
                        boolean flag1 = flag;
                        while (true) {
                            Edge_g edge2 = (Edge_g) edge1.nextEdge(flag1);
                            if (!ScalingUtil.isValid((edge2)))
                                continue label0;
                            if (edge2 == edge)
                                break label0;
                            int rgb1 = edge1.get_color_a(flag1, true);
                            int rgb2 = edge1.get_color_a(flag1, false);
                            boolean flag2 = edge2.isConnected((edge1));
                            int rgb3 = edge2.get_color_a(!flag2, true);
                            int rgb4 = edge2.get_color_a(!flag2, false);
                            double[] rgb1_ = UtColor.toFlat(rgb1);
                            double[] rgb2_ = UtColor.toFlat(rgb2);
                            double[] rgb3_ = UtColor.toFlat(rgb3);
                            double[] rgb4_ = UtColor.toFlat(rgb4);
                            Class_a a1 = new Class_a(rgb3_, rgb1_, rgb2_);
                            Class_a a3 = new Class_a(rgb4_, rgb1_, rgb2_);
                            if (a1.get_value1() < 0.0025d && a3.get_value1() < 0.0025d) {
                                double d2 = 0.001d;
                                boolean flag3 = a1.is_InRange2(d2);
                                boolean flag4 = a3.is_InRange2(d2);
                                if (flag3 && flag4) {
                                    Point p1 = edge2.get_point_b(!flag2, true);
                                    Point p2 = edge2.get_point_b(!flag2, false);
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
                                    int argb1 = ScalingUtil.blend(rgb3, rgb1, 1.0d - rate1);
                                    int argb2 = ScalingUtil.blend(rgb4, rgb2, 1.0d - rate2);
                                    rgb1 = argb1;
                                    double[] rgb5_ = UtColor.toFlat(rgb1);
                                    Class_a a2 = new Class_a(rgb3_, rgb5_, rgb2_);
                                    rgb2 = argb2;
                                    rgb2_ = UtColor.toFlat(rgb2);
                                    Class_a a4 = new Class_a(rgb4_, rgb5_, rgb2_);
                                    double d7 = a2.get_value2();
                                    double d8 = a4.get_value2();
                                    boolean flag5 = 0.0d - d2 < d7 && d7 < 1.0d + d2;
                                    boolean flag6 = 0.0d - d2 < d8 && d8 < 1.0d + d2;
                                    if (flag5 && flag6) {
                                        edge2.set_color_a(!flag2, true, argb1);
                                        edge2.set_color_a(!flag2, false, argb2);
                                        if (edge2.isHorizontal()) {
                                            double x1 = (p3.x - (p1.x + 0.5d)) / (p2.x - p1.x);
                                            double x2 = x1 * d7 + (1.0d - x1) * d8;
                                            double x3 = x2 * (p1.x + 0.5d) + (1.0d - x2) * (p2.x + 0.5d);
                                            edge2.moveBit(x3, p3.y);
                                        } else {
                                            double y1 = (p3.y - (p1.y + 0.5d)) / (p2.y - p1.y);
                                            double y2 = y1 * d7 + (1.0d - y1) * d8;
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
                                aflag[flag2 ? 0 : 1][ix + iy * sw] = true;
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                                System.out.println("ix + iy*sw : "+ (ix + iy * sw));
                                System.out.println("ix : " + ix);
                                System.out.println("iy : " + iy);
                                System.out.println("sw : " + sw);
                            }
                            flag1 = !flag2;
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
                        double v = 6d;
                        double x1 = (p1.x * v + p2.x + p3.x) / (v + 2d);
                        double y1 = (p1.y * v + p2.y + p3.y) / (v + 2d);
                        if (edge instanceof EdgeX)
                            ((EdgeX) edge).moveBit(x1, y1);
                        else
                            ((EdgeY) edge).moveBit(x1, y1);
                    }
                }
            }
        }
    }

    public static void smoothColor(EdgeData edgeData) {
        Line[] lines = edgeData.getLines();
        for (int i = 0; i < lines.length; i++) {
            Class_b util = new Class_b(lines[i]);
            Edge_g edge1 = (Edge_g) util.get_edge1();
            boolean connected = lines[i].is_connected1();
            Edge_g edge11 = (Edge_g) edge1.nextEdge(!connected);
            int color01 = edge1.get_color_a(connected, true);
            int color02 = edge1.get_color_a(connected, false);
            int color11;
            int color31;
            if (ScalingUtil.isValid((edge11))) {
                color11 = edge11.get_color_a(edge11.isConnected(edge1), true);
                color31 = edge11.get_color_a(edge11.isConnected(edge1), false);
            } else {
                color11 = 0xff00ff00;
                color31 = 0xff00ff00;
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
            System.out.println(" < DEBUG : jp.noids.image.scaling.Action_smoothing > ");
    }
}
