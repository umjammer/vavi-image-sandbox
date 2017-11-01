
package jp.noids.image.scaling;

import java.awt.Point;
import java.awt.image.BufferedImage;

import jp.noids.graphics.color.HSL;
import jp.noids.image.scaling.edge.Edge;
import jp.noids.image.scaling.edge.EdgeX;
import jp.noids.image.scaling.edge.EdgeY;
import jp.noids.image.scaling.edge.Edge_g;
import jp.noids.image.scaling.line.UtLine;
import jp.noids.util.UtString;


/** m */
public abstract class Action_connectEdges implements DirectionConstants, Constants {

    public static void connectEdges(EdgeData edgeData) throws InterruptedException {
        BufferedImage image = edgeData.getImage();
        int w = image.getWidth();
        int h = image.getHeight();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Edge_g edge = (Edge_g) edgeData.getEdgeAt(x, y);
                if (edge != null) {
                    /* Point.Double p = */ edge.get_point1();
                    /* g g2 = */ a(edgeData, edge, x, y, true);
                    /* g g3 = */ a(edgeData, edge, x, y, false);
                }
            }

            if (Thread.interrupted())
                throw new InterruptedException();
        }
    }

    public static Edge_g a(EdgeData edgeData, Edge_g edge, int x, int y, boolean asc) {
        Edge_g edge1 = (Edge_g) edge.nextEdge(asc);
        if (edge1 != null)
            if (!ScalingUtil.isValid(edge1))
                return null;
            else
                return edge1;
        int direction;
        if (edge instanceof EdgeX)
            direction = asc ? 1 : 3;
        else if (edge instanceof EdgeY)
            direction = asc ? 2 : 0;
        else
            throw new RuntimeException("不正な値");
        int l = 0;
        Object obj = null;
        boolean flag1 = false;
label0: for (int i = 0; i < table_a.length; i++) {
            Class_a a1 = table_a[i];
            Point p = getPoint_a(x, y, direction, a1);
            if (p.x < 0 || edgeData.getImage().getWidth() <= p.x || p.y < 0 || edgeData.getImage().getHeight() <= p.y)
                continue;
            Edge[] edges = edgeData.getEdgesAt(p.x, p.y);
            for (int j = 0; j < edges.length; j++) {
                Edge_g edge2 = (Edge_g) edges[j];
                if (edge2 == edge || edge.contains((edge2)))
                    continue;
                int r = a(edgeData, edge, edge2, direction, x, y, a1);
                if (r <= 0)
                    continue;
                boolean flag2 = a(direction, a1, edge2);
                if (obj == null) {
                    obj = (edge2);
                    l = r;
                    flag1 = flag2;
                } else if (l < r) {
                    obj = (edge2);
                    l = r;
                    flag1 = flag2;
                }
                if (r >= 36)
                    break label0;
            }
        }

        if (l < 8)
            return null;
        if (UtLine.connect((Edge) edge, asc, (Edge) obj, flag1, l)) {
            return ((Edge_g) obj);
        } else {
            edge.connect(asc, Edge.dummyEdge, 0);
            return null;
        }
    }

    public static int a(EdgeData edgeData, Edge_g edge1, Edge_g edge2, int direction, int x, int y, Class_a a1) {
        boolean flag = edge1.isHorizontal() == edge2.isHorizontal();
        if (flag != a1.get_flag1())
            return 0;
        boolean flag1 = a(direction, edge1, edge2);
        if (flag1 != a1.get_flag2())
            return 0;
        int xc1 = edge1.getXColor(direction);
        int yc1 = edge1.getYColor(direction);
        int xc2 = edge2.getXColor(direction);
        int yc2 = edge2.getYColor(direction);
        double c1;
        double c2;
        if (flag1) {
            c1 = HSL.get_value_c(xc1, yc2);
            c2 = HSL.get_value_c(yc1, xc2);
        } else {
            c1 = HSL.get_value_c(xc1, xc2);
            c2 = HSL.get_value_c(yc1, yc2);
        }
        int i2 = a1.get_value() + b(c1) + b(c2);
        int j2 = connect(edgeData, edge1, edge2, direction, x, y, flag1);
        i2 -= j2;
        if (i2 < 0)
            i2 = 0;
        return i2;
    }

    private static boolean a(int direction, Class_a a1, Edge_g edge) {
        boolean flag1 = a1.get_flag3();
        boolean flag;
        switch (direction) {
        case 2:
            flag = flag1;
            break;
        case 0:
            flag = !flag1;
            break;
        case 1:
            if (edge.isHorizontal())
                flag = flag1;
            else
                flag = !flag1;
            break;
        case 3:
            if (edge.isHorizontal())
                flag = !flag1;
            else
                flag = flag1;
            break;
        default:
            throw new RuntimeException("未実装");
        }
        return flag;
    }

    public static int connect(EdgeData edgeData, Edge_g edge1, Edge_g edge2, int direction, int x, int y, boolean flag) {
        boolean debug = false;
        Point sp1 = edge1.getStartPoint(direction);
        Point ep1 = edge1.getEndPoint(direction);
        Point sp2 = edge2.getStartPoint(direction);
        Point ep2 = edge2.getEndPoint(direction);
        int maxX = Math.max(Math.max(sp1.x, ep1.x), Math.max(sp2.x, ep2.x));
        int minX = Math.min(Math.min(sp1.x, ep1.x), Math.min(sp2.x, ep2.x));
        int maxY = Math.max(Math.max(sp1.y, ep1.y), Math.max(sp2.y, ep2.y));
        int minY = Math.min(Math.min(sp1.y, ep1.y), Math.min(sp2.y, ep2.y));
        int dw = (maxX - minX) + 1 + 2;
        int dh = (maxY - minY) + 1 + 2;
        int[][] weights = new int[dh][dw];
        int ox = minX - 1;
        int oy = minY - 1;
//        boolean flag2 = false;
        int xc1;
        int yc1;
        int ex;
        int ey;
        if (edge1.isHorizontal()) {
            xc1 = edge1.getStartPoint(direction).x + 1;
            ex = edge1.getEndPoint(direction).x - 1;
            yc1 = ey = edge1.getStartPoint(direction).y;
        } else {
            xc1 = ex = edge1.getEndPoint(direction).x;
            yc1 = edge1.getStartPoint(direction).y + 1;
            ey = edge1.getEndPoint(direction).y - 1;
        }
        for (int y1 = yc1; y1 <= ey; y1++) {
            for (int x1 = xc1; x1 <= ex; x1++) {
                weights[y1 - oy][x1 - ox] = -2;
            }
        }

        if (edge1.isHorizontal()) {
            if (direction == 1)
                weights[y - oy - 1][x - ox] = -2;
            else
                weights[(y - oy) + 1][x - ox] = -2;
        } else if (direction == 2)
            weights[y - oy][x - ox - 1] = -2;
        else
            weights[y - oy][(x - ox) + 1] = -2;
        weights[edge1.getStartPoint(direction).y - oy][edge1.getStartPoint(direction).x - ox] = 1000;
        weights[edge1.getEndPoint(direction).y - oy][edge1.getEndPoint(direction).x - ox] = 2000;
        xc1 = edge1.getXColor(direction);
        yc1 = edge1.getYColor(direction);
        for (int y3 = 0; y3 < dh; y3++) {
            for (int x3 = 0; x3 < dw; x3++) {
                if (weights[y3][x3] == 0) {
                    int x4 = x3 + ox;
                    int y4 = y3 + oy;
                    if (x4 < 0 || y4 < 0 || x4 >= edgeData.width || y4 >= edgeData.height) {
                        weights[y3][x3] = -10;
                    } else {
                        int rgb = edgeData.getImage().getRGB(x4, y4);
                        double d6 = HSL.get_value_c(xc1, rgb);
                        double d7 = HSL.get_value_c(yc1, rgb);
                        double d8 = d6 >= d7 ? d7 : d6;
                        if (d8 > 0.5D)
                            weights[y3][x3] = -6;
                        else if (d6 < d7)
                            weights[y3][x3] = -7;
                        else
                            weights[y3][x3] = -8;
                    }
                }
            }
        }

        int[][] ai1 = {
            { -1,  0, 1 },
            {  1,  0, 1 },
            {  0, -1, 1 },
            {  0,  1, 1 },
            { -1, -1, 3 },
            { -1,  1, 3 },
            {  1, -1, 3 },
            { -1, -1, 3 }
        };
        boolean flag3;
        do {
            flag3 = false;
            for (int y5 = 0; y5 < dh; y5++) {
                for (int x5 = 0; x5 < dw; x5++) {
                    if (weights[y5][x5] == -7) {
                        int j7 = 0x7fffffff;
                        for (int i8 = 0; i8 < ai1.length; i8++) {
                            int l8 = x5 + ai1[i8][0];
                            int j9 = y5 + ai1[i8][1];
                            int l9 = ai1[i8][2];
                            if (l8 >= 0 && l8 < dw && j9 >= 0 && j9 < dh) {
                                int j10 = weights[j9][l8] - 1000;
                                if (j10 >= 0 && j10 < 1000 && j10 + l9 < j7)
                                    j7 = j10 + l9;
                            }
                        }

                        if (j7 != 0x7fffffff) {
                            weights[y5][x5] = 1000 + j7;
                            flag3 = true;
                        }
                    }
                }
            }

        } while (flag3);
        do {
            flag3 = false;
            for (int y5 = 0; y5 < dh; y5++) {
                for (int x5 = 0; x5 < dw; x5++) {
                    if (weights[y5][x5] == -8) {
                        int k7 = 0x7fffffff;
                        for (int j8 = 0; j8 < ai1.length; j8++) {
                            int i9 = x5 + ai1[j8][0];
                            int k9 = y5 + ai1[j8][1];
                            int i10 = ai1[j8][2];
                            if (i9 >= 0 && i9 < dw && k9 >= 0 && k9 < dh) {
                                int k10 = weights[k9][i9] - 2000;
                                if (k10 >= 0 && k10 < 1000 && k10 + i10 < k7)
                                    k7 = k10 + i10;
                            }
                        }

                        if (k7 != 0x7fffffff) {
                            weights[y5][x5] = 2000 + k7;
                            flag3 = true;
                        }
                    }
                }
            }

        } while (flag3);
        int w1;
        int w2;
        if (!flag) {
            w1 = weights[edge2.getStartPoint(direction).y - oy][edge2.getStartPoint(direction).x - ox];
            w2 = weights[edge2.getEndPoint(direction).y - oy][edge2.getEndPoint(direction).x - ox];
        } else {
            w2 = weights[edge2.getStartPoint(direction).y - oy][edge2.getStartPoint(direction).x - ox];
            w1 = weights[edge2.getEndPoint(direction).y - oy][edge2.getEndPoint(direction).x - ox];
        }

        int w3;
        if (1000 <= w1 && w1 < 2000)
            w3 = w1 - 1000;
        else if (w1 == -2)
            w3 = 0;
        else
            w3 = 10000;

        int w4;
        if (2000 <= w2 && w2 < 3000)
            w4 = w2 - 2000;
        else if (w2 == -2)
            w4 = 0;
        else
            w4 = 10000;

        if (debug) {
            System.out.println("==============================");
            debug(direction, x, y, dw, dh, weights, edge1, edge2, ox, oy);
            if (w3 + w4 >= 10000)
                System.out.println("\n\327not connect!");
            else
                System.out.println("\n◎connected! : " + (w3 + w4));
            System.out.println("e2 start : " + edge2.getStartPoint(direction).x + "," + edge2.getStartPoint(direction).y);
            System.out.println("e2 end   : " + edge2.getEndPoint(direction).x + "," + edge2.getEndPoint(direction).y);
        }

        return w3 + w4;
    }

    private static void debug(int d, int x, int y, int w, int h, int[][] weights, Edge_g e1, Edge_g e2, int sx, int sy) {
        System.out.println("(" + x + "," + y + ")" + arrows[d] + " (" + ((int) e2.get_point1().x) + "," + ((int) e2.get_point1().y) + ")");
        for (int y1 = 0; y1 < h; y1++) {
            System.out.println();
            for (int x1 = 0; x1 < w; x1++) {
                int weight = weights[y1][x1];
                String s;
                switch (weight) {
                case -1:
                    s = "■";
                    break;
                case 0: // '\0'
                    s = "・";
                    break;
                case -2:
                    s = "OK";
                    break;
                case -4:
                    s = "ｓ";
                    break;
                case -5:
                    s = "ｅ";
                    break;
                case -6:
                    s = "\327";
                    break;
                case -7:
                    s = "○";
                    break;
                case -8:
                    s = "●";
                    break;
                case -10:
                    s = "−";
                    break;
                case -9:
                case -3:
                default:
                    if (1000 <= weight && weight < 2000)
                        s = "S" + (weight - 1000);
                    else if (2000 <= weight && weight < 3000)
                        s = "E" + (weight - 2000);
                    else
                        s = "？";
                    break;
                }
                if (x1 + sx == e2.getStartPoint(d).x && y1 + sy == e2.getStartPoint(d).y)
                    s = s + "s";
                if (x1 + sx == e2.getEndPoint(d).x && y1 + sy == e2.getEndPoint(d).y)
                    s = s + "e";
                System.out.print(UtString.fillSpaces(s, 6));
            }
        }

        System.out.println("");
    }

    private static boolean a(int direction, Edge_g edge1, Edge_g edge2) {
        int xc1 = edge1.getXColor(direction);
        int yc1 = edge1.getYColor(direction);
        int xc2 = edge2.getXColor(direction);
        int yc2 = edge2.getYColor(direction);
        double d1 = HSL.get_value_c(xc1, xc2);
        double d2 = HSL.get_value_c(yc1, yc2);
        double d3 = HSL.get_value_c(xc1, yc2);
        double d4 = HSL.get_value_c(yc1, xc2);
        return d3 + d4 < d1 + d2;
    }

    private static Point getPoint_a(int x, int y, int direction, Class_a a1) {
        int x1;
        int y1;
        switch (direction) {
        case 2:
            x1 = x + a1.getX();
            y1 = y + a1.getY();
            break;
        case 0:
            x1 = x - a1.getX();
            y1 = y - a1.getY();
            break;
        case 3:
            x1 = x + a1.getY();
            y1 = y - a1.getX();
            break;
        case 1:
            x1 = x - a1.getY();
            y1 = y + a1.getX();
            break;
        default:
            throw new RuntimeException("未実装");
        }
        return new Point(x1, y1);
    }

    private static int b(double d1) {
        int i = (int) (d1 * (table_i.length - 1));
        return table_i[i];
    }
}
