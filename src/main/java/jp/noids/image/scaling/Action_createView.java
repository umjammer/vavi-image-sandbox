
package jp.noids.image.scaling;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import jp.noids.image.scaling.edge.Edge;
import jp.noids.image.scaling.edge.Edge_g;
import jp.noids.image.scaling.edge.Edge_h;
import jp.noids.image.scaling.line.Line;
import jp.noids.image.scaling.line.Class_b;
import jp.noids.image.scaling.pixDraw.PixDraw_edge3P;
import jp.noids.image.scaling.pixDraw.PixDraw_edge3P_2;
import jp.noids.image.scaling.pixDraw.PixDraw_edgeEnd;
import jp.noids.image.scaling.pixDraw.PixDraw_edgeMulti;
import jp.noids.image.scaling.pixDraw.PixDraw_edgeMulti_3;
import jp.noids.image.scaling.pixDraw.PixDraw_edgeMulti_2;
import jp.noids.image.scaling.pixDraw.PixDraw_notEdge4C;
import jp.noids.image.scaling.pixDraw.PixDraw_notEdgeFlat;
import jp.noids.image.scaling.pixDraw.Pixel;
import jp.noids.image.scaling.view.DataBufferPixel;
import jp.noids.image.util.UtImage;


/** f */
public abstract class Action_createView implements DirectionConstants, Constants {

    /** j */
    static class PixelData {
        int x;
        int y;
        PixDraw_edgeMulti pixel;
        public PixelData(int x, int y, PixDraw_edgeMulti pixel) {
            this.x = x;
            this.y = y;
            this.pixel = pixel;
        }
    }

    static int C1 = 1;
    static int C2 = 2;
    static int C4 = 4;
    static int C8 = 8;

    public static void createView(EdgeData edgeData) throws Exception {
        Pixel[][] pixels = new Pixel[edgeData.getHeight()][edgeData.getWidth()];
        UtImage util = new UtImage(edgeData.getImage());
        List<PixelData> pixelData = new ArrayList<PixelData>();
        Line[] lines = edgeData.getLines();
        for (int i = 0; i < lines.length; i++)
            if (lines[i] != null && lines[i].getLength() > 4 && lines[i].isLineLenOver() && lines[i].get_color_value2() >= 0.15d) {
                Class_b b1 = new Class_b(lines[i]);
                Edge edge;
                while ((edge = b1.get_edge2()) != null) {
                    boolean flag = b1.get_flag();
                    Point p = edge.get_point2();
                    int x = p.x;
                    int y = p.y;
                    Point.Double p0 = edge.get_point1();
                    Edge edge1 = edge.nextEdge(!flag);
                    Edge edge2 = edge.nextEdge(flag);
                    int rgb1 = edge.get_color_a(flag, true);
                    int rgb2 = edge.get_color_a(flag, false);
                    if (edge instanceof Edge_h) {
                        Point.Double p1 = a(p, edge1.get_point1(), edge.get_point1());
                        Point.Double p2 = a(p, edge2.get_point1(), edge.get_point1());
                        boolean flag1 = edge.is_flag1();
                        PixDraw_edge3P pixdraw_edge3p = a(flag1, p, p0, p1, p2, rgb1, rgb2, 1.0D, 1.0D);
                        Edge_h edge3 = (Edge_h) edge;
                        Edge edge4 = edge3.get_Edge();
                        Point.Double p4 = edge4.get_point1();
                        Point.Double p3 = edge3.get_Point();
                        Point.Double p5 = a(p, p4, p3);
                        Point.Double p6 = new Point.Double(p3.x + (p3.x - p4.x) * 100000000d, p3.y + (p3.y - p4.y) * 100000000d);
                        Point.Double p7 = a(p, p6, p3);
                        PixDraw_edge3P pixdraw_edge3p2 = a(false, p, p0, p5, p7, rgb1, rgb2, 1.0d, 1.0d);
                        PixDraw_edgeMulti_3 pixDraw_edgeMulti_2 = new PixDraw_edgeMulti_3(p,
                                                                                          pixdraw_edge3p,
                                                                                          pixdraw_edge3p2,
                                                                                          1.0d,
                                                                                          1.0d);
                        if (pixels[y][x] == null) {
                            pixels[y][x] = pixDraw_edgeMulti_2;
                        } else {
                            throw new RuntimeException("未実装");
                        }
                    } else if (ScalingUtil.isValid(edge1) && ScalingUtil.isValid(edge2)) {
                        Point.Double p1 = a(p, edge1.get_point1(), edge.get_point1());
                        Point.Double p2 = a(p, edge2.get_point1(), edge.get_point1());
                        boolean flag2 = edge.is_flag1();
                        PixDraw_edge3P pixel = a(flag2, p, p0, p1, p2, rgb1, rgb2, 1.0d, 1.0d);
                        if (pixels[y][x] == null) {
                            pixels[y][x] = pixel;
                        } else if (pixels[y][x] instanceof PixDraw_edge3P) {
                            PixDraw_edgeMulti pixel1 = new PixDraw_edgeMulti(p, pixels[y][x], pixel, 0.5d, 0.5d);
                            if (pixel1.isDirectionOf(Direction.ANY)) {
                                pixelData.add(new PixelData(x, y, pixel1));
                            }
                            pixels[y][x] = pixel1;
                        } else if (pixels[y][x] instanceof PixDraw_edgeEnd)
                            pixels[y][x] = new PixDraw_edgeMulti_2(p, pixel, (PixDraw_edgeEnd) pixels[y][x], 0.5d, 0.5d);
                    } else if (ScalingUtil.isValid(edge1) || ScalingUtil.isValid(edge2)) {
                        Edge edge7;
                        int rgb11;
                        int rgb22;
                        if (ScalingUtil.isValid(edge1)) {
                            edge7 = edge1;
                            rgb11 = rgb1;
                            rgb22 = rgb2;
                        } else {
                            edge7 = edge2;
                            rgb11 = rgb2;
                            rgb22 = rgb1;
                        }
                        Point.Double p1 = a(p, edge7.get_point1(), edge.get_point1());
                        @SuppressWarnings("unused")
                        int rgb3 = ScalingUtil.blend(rgb1, rgb2, 0.5d);
                        byte byte0;
                        if (edge instanceof Edge_g) {
                            if (((Edge_g) edge).isHorizontal()) {
                                byte0 = ((byte) (edge.isConnected(edge7) ? 3 : 1));
                            } else {
                                byte0 = ((byte) (edge.isConnected(edge7) ? 0 : 2));
                            }
                        } else {
                            throw new RuntimeException("未実装 : 準備しにゃ");
                        }
                        PixDraw_edgeEnd a7 = new PixDraw_edgeEnd(p, p0, p1, (byte0), rgb11, rgb22, 1.0d, 1.0d);
                        if (pixels[y][x] == null)
                            pixels[y][x] = (a7);
                        else if (pixels[y][x] instanceof PixDraw_edge3P)
                            pixels[y][x] = new PixDraw_edgeMulti_2(p, (PixDraw_edge3P) pixels[y][x], a7, 0.5D, 0.5D);
                    }
                    if (ScalingUtil.isValid(edge2)) {
                        Point p1 = edge.get_point2();
                        Point p2 = edge2.get_point2();
                        int x1 = p1.x >= p2.x ? p2.x : p1.x;
                        int y1 = p1.y >= p2.y ? p2.y : p1.y;
                        int x2 = p1.x <= p2.x ? p2.x : p1.x;
                        int y2 = p1.y <= p2.y ? p2.y : p1.y;
                        for (int y3 = y1; y3 <= y2; y3++) {
                            for (int x3 = x1; x3 <= x2; x3++)
                                if ((x3 != p1.x || y3 != p1.y) && (x3 != p2.x || y3 != p2.y)) {
                                    Point p3 = new Point(x3, y3);
                                    Point.Double p4 = edge.get_point1();
                                    Point.Double p5 = edge2.get_point1();
                                    Point.Double[] points = b(p3, p4, p5);
                                    if (points != null && !points[0].equals((points[1]))) {
                                        Point.Double p6 = new Point.Double((points[0].x + points[1].x) / 2d, (points[0].y + points[1].y) / 2D);
                                        PixDraw_edge3P pixdraw_edge3p3 = a(false, p3, p6, points[0], points[1], rgb1, rgb2, 0.5d, 0.5d);
                                        if (pixels[y3][x3] != null) {
                                            if (pixels[y3][x3] instanceof PixDraw_edge3P) {
                                                PixDraw_edgeMulti pixel = new PixDraw_edgeMulti(p3,
                                                                                             pixels[y3][x3],
                                                                                             pixdraw_edge3p3,
                                                                                             0.5d,
                                                                                             0.5d);
                                                if (pixel.isDirectionOf(Direction.ANY))
                                                    pixelData.add(new PixelData(x3, y3, pixel));
                                                pixels[y3][x3] = (pixel);
                                            } else if (pixels[y3][x3] instanceof PixDraw_edgeEnd)
                                                pixels[y3][x3] = (new PixDraw_edgeMulti_2(p3,
                                                                                          pixdraw_edge3p3,
                                                                                          (PixDraw_edgeEnd) pixels[y3][x3],
                                                                                          0.5d,
                                                                                          0.5d));
                                        } else {
                                            pixels[y3][x3] = (pixdraw_edge3p3);
                                        }
                                    }
                                }
                        }
                    }
                }
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
            }

        int[][] colors = new int[edgeData.getHeight()][edgeData.getWidth()];
        for (int y = 1; y < pixels.length; y++) {
            for (int x = 1; x < pixels[y].length; x++)
                colors[y][x] = a(pixels, util, x, y);
        }

        if (Thread.interrupted())
            throw new InterruptedException();

        for (PixelData pix : pixelData) {
            PixDraw_edgeMulti pixel = pix.pixel;
            Direction[] dirs = pixel.getDirections();
            for (int i = 0; i < dirs.length; i++) {
                try {
                    if (dirs[i] == Direction.SOUTH && (pixels[pix.y + 1][pix.x] instanceof PixDraw_edgeMulti)) {
                        PixDraw_edgeMulti e2 = (PixDraw_edgeMulti) pixels[pix.y + 1][pix.x];
                        int c1 = pixel.a(pix.x, pix.y, Direction.SOUTH);
                        int c2 = e2.a(pix.x, pix.y + 1, Direction.NORTH);
                        int c = ScalingUtil.average(c1, c2);
                        pixel.a(pix.x, pix.y, Direction.SOUTH, c);
                        e2.a(pix.x, pix.y + 1, Direction.NORTH, c);
                    } else if (dirs[i] == Direction.EAST && (pixels[pix.y][pix.x + 1] instanceof PixDraw_edgeMulti)) {
                        PixDraw_edgeMulti e3 = (PixDraw_edgeMulti) pixels[pix.y][pix.x + 1];
                        int c1 = pixel.a(pix.x, pix.y, Direction.EAST);
                        int c2 = e3.a(pix.x + 1, pix.y, Direction.WEST);
                        int c = ScalingUtil.average(c1, c2);
                        pixel.a(pix.x, pix.y, Direction.EAST, c);
                        e3.a(pix.x + 1, pix.y, Direction.WEST, c);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                }
            }
        }

        for (int y = 0; y < pixels.length; y++) {
            for (int x = 0; x < pixels[y].length; x++) {
                if (!isValid(pixels[y][x])) {
                    if (y == 0 || x == 0 || y == pixels.length - 1 || x == pixels[y].length - 1) {
                        pixels[y][x] = new PixDraw_notEdgeFlat(util.getARGB(x, y));
                    } else {
                        int c1 = colors[y][x];
                        int c2 = colors[y][x + 1];
                        int c3 = colors[y + 1][x];
                        int c4 = colors[y + 1][x + 1];
                        if (pixels[y][x] instanceof PixDraw_edgeEnd) {
                            pixels[y][x].setCornerColor(x, y, 0, c1);
                            pixels[y][x].setCornerColor(x, y, 1, c2);
                            pixels[y][x].setCornerColor(x, y, 2, c3);
                            pixels[y][x].setCornerColor(x, y, 3, c4);
                        } else {
                            pixels[y][x] = (new PixDraw_notEdge4C(c1, c2, c3, c4));
                        }
                    }
                }
            }
        }

        DataBufferPixel pixel = new DataBufferPixel(pixels);
        edgeData.setDataBufferPixel(pixel);
    }

    private static int a(Pixel[][] pixels, UtImage util, int x, int y) {
        int[] colors = new int[4];
        int i = 0;
        boolean f1 = isValid(pixels[y - 1][x - 1]);
        boolean f2 = isValid(pixels[y - 1][x]);
        boolean f3 = isValid(pixels[y][x - 1]);
        boolean f4 = isValid(pixels[y][x]);
        int dx = 0;
        int dy = 0;
        if (f1) {
            dx++;
            dy++;
        }
        if (f2) {
            dx--;
            dy++;
        }
        if (f3) {
            dx++;
            dy--;
        }
        if (f4) {
            dx--;
            dy--;
        }
        double x1 = x;
        double y1 = y;
        if (dx > 0)
            x1 += 0.0001d;
        else if (dx < 0)
            x1 -= 0.0001d;
        if (dy > 0)
            y1 += 0.0001d;
        else if (dy < 0)
            y1 -= 0.0001d;
        if (f1)
            colors[i++] = pixels[y - 1][x - 1].getRgb(x - 1, y - 1, x1, y1, 0.0D, 0.0D);
        if (f2)
            colors[i++] = pixels[y - 1][x].getRgb(x, y - 1, x1, y1, 0.0D, 0.0D);
        if (f3)
            colors[i++] = pixels[y][x - 1].getRgb(x - 1, y, x1, y1, 0.0D, 0.0D);
        if (f4)
            colors[i++] = pixels[y][x].getRgb(x, y, x1, y1, 0.0D, 0.0D);
        if (i > 0) {
            int ac = ScalingUtil.average(colors, i);
            if (f1)
                pixels[y - 1][x - 1].setCornerColor(x - 1, y - 1, 3, ac);
            if (f2)
                pixels[y - 1][x].setCornerColor(x, y - 1, 2, ac);
            if (f3)
                pixels[y][x - 1].setCornerColor(x - 1, y, 1, ac);
            if (f4)
                pixels[y][x].setCornerColor(x, y, 0, ac);
            return ac;
        }
        try {
            int c1 = util.getARGB(x - 1, y - 1);
            int c2 = util.getARGB(x, y - 1);
            int c3 = util.getARGB(x - 1, y);
            int c4 = util.getARGB(x, y);
            return ScalingUtil.average(c1, c2, c3, c4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0xffff0000;
    }

    private static boolean isValid(Pixel pixel) {
        return pixel != null && pixel.isValid();
    }

    static Point.Double a(Point p, Point.Double p1, Point.Double p2) {
        double x1 = p1.x;
        double y1 = p1.y;
        double x2 = p2.x;
        double y2 = p2.y;
        double x;
        double y;
        if (x1 == x2) {
            x = x1;
            y = y1 >= y2 ? p.y + 1 : p.y;
        } else if (x1 < x2) {
            double d8 = (y2 - y1) / (x2 - x1);
            double d10 = d8 * (p.x - x2) + y2;
            if (p.y <= d10 && d10 <= (p.y + 1)) {
                x = p.x;
                y = d10;
            } else if (y1 < y2) {
                x = (1.0d / d8) * (p.y - y2) + x2;
                y = p.y;
            } else {
                x = (1.0d / d8) * ((p.y + 1.0d) - y2) + x2;
                y = p.y + 1.0d;
            }
        } else {
            double d9 = (y2 - y1) / (x2 - x1);
            double d11 = d9 * ((p.x + 1.0d) - x2) + y2;
            if (p.y <= d11 && d11 <= (p.y + 1)) {
                x = p.x + 1.0d;
                y = d11;
            } else if (y1 < y2) {
                x = (1.0D / d9) * (p.y - y2) + x2;
                y = p.y;
            } else {
                x = (1.0D / d9) * ((p.y + 1.0D) - y2) + x2;
                y = p.y + 1.0d;
            }
        }
        return new Point.Double(x, y);
    }

    static boolean a(Point p, Point.Double p1) {
        return p.x <= p1.x && p1.x < p.x + 1.0d && p.y <= p1.y && p1.y < p.y + 1.0d;
    }

    private static PixDraw_edge3P a(boolean flag,
                                    Point p,
                                    Point.Double p1,
                                    Point.Double sp,
                                    Point.Double ep,
                                    int c1,
                                    int c2,
                                    double sx,
                                    double sy) {
        Object obj;
        if (flag)
            obj = new PixDraw_edge3P_2(p, p1, sp, ep, c1, c2, sx, sy);
        else
            obj = new PixDraw_edge3P(p, p1, sp, ep, c1, c2, sx, sy);
        return (PixDraw_edge3P) obj;
    }

    static Point.Double[] b(Point p, Point.Double p1, Point.Double p2) {
        if (a(p, p1) || a(p, p2))
            return null;
        double x1 = p1.x;
        double y1 = p1.y;
        double x2 = p2.x;
        double y2 = p2.y;
        if (x1 == x2)
            if (p.x <= x1 && x1 < (p.x + 1)) {
                if (y1 < y2)
                    if (y1 <= p.y && (p.y + 1) <= y2)
                        return new Point.Double[] {
                            new Point.Double(x1, p.y), new Point.Double(x2, p.y + 1)
                        };
                    else
                        return null;
                if (y2 <= p.y && (p.y + 1) <= y1)
                    return new Point.Double[] {
                        new Point.Double(x2, p.y + 1), new Point.Double(x1, p.y)
                    };
                else
                    return null;
            } else {
                return null;
            }
        if (y1 == y2)
            if (p.y <= y1 && y1 < (p.y + 1)) {
                if (x1 < x2)
                    if (x1 <= p.x && (p.x + 1) <= x2)
                        return new Point.Double[] {
                            new Point.Double(p.x, y1), new Point.Double(p.x + 1, y2)
                        };
                    else
                        return null;
                if (x2 <= p.x && (p.x + 1) <= x1)
                    return new Point.Double[] {
                        new Point.Double(p.x + 1, y1), new Point.Double(p.x, y2)
                    };
                else
                    return null;
            } else {
                return null;
            }
        double r = (y2 - y1) / (x2 - x1);
        double u = 1.0D / r;
        double y3 = r * (p.x - x2) + y2;
        double y4 = r * ((p.x + 1) - x2) + y2;
        double x3 = u * (p.y - y2) + x2;
        double x4 = u * ((p.y + 1) - y2) + x2;
        Point.Double points[] = new Point.Double[2];
        int i = 0;
        if (p.y <= y3 && y3 <= (p.y + 1))
            points[i++] = new Point.Double(p.x, y3);
        if (p.y <= y4 && y4 <= (p.y + 1))
            points[i++] = new Point.Double(p.x + 1, y4);
        if (p.x <= x3 && x3 <= (p.x + 1))
            points[i++] = new Point.Double(x3, p.y);
        if (p.x <= x4 && x4 <= (p.x + 1))
            points[i++] = new Point.Double(x4, p.y + 1);
        if (i == 0)
            return null;
        if (i == 2) {
            if ((x1 < x2) ^ (points[0].x < points[1].x)) {
                Point.Double p3 = points[0];
                points[0] = points[1];
                points[1] = p3;
            }
            return points;
        } else {
            throw new RuntimeException("おかしな状態");
        }
    }
}
