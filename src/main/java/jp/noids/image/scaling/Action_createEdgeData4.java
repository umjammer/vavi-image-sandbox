
package jp.noids.image.scaling;

import java.awt.image.BufferedImage;
import java.util.List;

import jp.noids.graphics.color.HSL;
import jp.noids.graphics.geom.UtVector;
import jp.noids.image.scaling.edge.EdgeX;
import jp.noids.image.scaling.edge.EdgeY;
import jp.noids.image.util.UtImage;
import jp.noids.image.util.InteriorDivision;


public abstract class Action_createEdgeData4 implements DirectionConstants, Constants {

    static double value1 = 0.05d;
    static double value2 = 0.1d;
    static boolean flag1 = false;
    static double value3 = 0.01d;

    /**
     * 
     * @param image type should be {@link BufferedImage#TYPE_INT_ARGB}
     * @throws IllegalArgumentException
     * @throws InterruptedException
     */
    public static EdgeData createEdge(BufferedImage image, int margin) throws InterruptedException {
        try {
            if (image.getType() != BufferedImage.TYPE_INT_ARGB)
                throw new IllegalArgumentException("INT_ARGB以外の型については未実装です");
            int w = image.getWidth();
            int h = image.getHeight();
            EdgeData edgeData = new EdgeData(image, margin);
            UtImage util = new UtImage(image);
            int c = 0;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w - 1;) {
                    EdgeX edgeX = createEdgeX(util, x, y);
                    if (edgeX == null) {
                        x++;
                    } else {
                        int ex = edgeX.getEndX();
                        int l = (ex - x) + 1;
                        if (l <= 5)
                            edgeData.addEdge(edgeX);
                        x = ex;
                    }
                    if (c++ > h * w * 10)
                        throw new IllegalStateException("無限ループ");
                }
            }

            if (Thread.interrupted())
                throw new InterruptedException();

            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h - 1;) {
                    EdgeY edgeY = createEdgeY(util, x, y);
                    if (edgeY == null) {
                        y++;
                    } else {
                        int ey = edgeY.getEndY();
                        int l = (ey - y) + 1;
                        if (l <= 5)
                            edgeData.addEdge(edgeY);
                        y = ey;
                    }
                    if (c++ > h * w * 10)
                        throw new IllegalStateException("無限ループ");
                }
            }

            if (Thread.interrupted())
                throw new InterruptedException();

            for (int x = 0; x < edgeData.edgeXs.length; x++) {
                List<EdgeX> edgeXs = edgeData.edgeXs[x];
                if (edgeXs != null) {
                    for (int xi = edgeXs.size() - 1; xi >= 0; xi--) {
                        EdgeX edgeX = edgeXs.get(xi);
                        double x1 = edgeX.getX();
                        int x2 = (int) x1;
                        EdgeY[] edgeYs = edgeData.getEdgeYs(x2, x);
                        for (int y = 0; y < edgeYs.length; y++) {
                            if (x != (int) edgeYs[y].getY())
                                continue;
                            if (edgeYs[y].get_color1() < edgeX.get_color1())
                                edgeData.remove(x2, edgeYs[y]);
                            else
                                edgeXs.remove(xi);
                            break;
                        }
                    }
                }
            }

            if (Thread.interrupted())
                throw new InterruptedException();

            for (int x = 0; x < edgeData.edgeXs.length; x++) {
                List<EdgeX> edgeXs = edgeData.edgeXs[x];
                if (edgeXs != null) {
                    for (int xi = edgeXs.size() - 1; xi >= 0; xi--) {
                        EdgeX edgeX = edgeXs.get(xi);
                        if ((edgeX.endX - edgeX.startX) + 1 <= 2 && edgeX.get_color2() < 0.4d && contains(edgeData, x, edgeX))
                            edgeXs.remove(xi);
                    }
                }
            }

            for (int y = 0; y < edgeData.edgeYs.length; y++) {
                List<EdgeY> edegYs = edgeData.edgeYs[y];
                if (edegYs != null) {
                    for (int yi = edegYs.size() - 1; yi >= 0; yi--) {
                        EdgeY edgeY = edegYs.get(yi);
                        if ((edgeY.endY - edgeY.startY) + 1 <= 2 && edgeY.get_color2() < 0.4d && contains(edgeData, y, edgeY))
                            edegYs.remove(yi);
                    }
                }
            }

            return edgeData;
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    private static EdgeX createEdgeX(UtImage util, int x, int y) throws Exception {
        int w = util.getWidth();
        double d1 = 0.0D;
        int argb1 = util.getARGB(x, y);
        double[] hsl2 = HSL.toHsl(argb1, (double[]) null);
        double[] hsl1 = new double[3];
        double[] hsl0 = HSL.getHsl_b(hsl2, (double[]) null);
        double[] hsl7 = null;
        double[] hsl8 = new double[3];
        double[] ad9 = new double[21];
        int c = 0;
        int argb2 = util.getARGB(x + 1, y);
        HSL.toHsl(argb2, hsl1);
        double d2 = HSL.get_value_a(hsl2, hsl1);
        boolean flag = false;
        int x2 = x + 1;
        if (d2 > 0.09d) {
            flag = true;
            x2 = x + 2;
            ad9[c++] = d2;
            d1 = d2;
            double[] hsl4 = HSL.getHsl_b(hsl1, (double[]) null);
            hsl7 = UtVector.get_diff(hsl0, hsl4, (double[]) null);
        } else if (d2 > value1 && x + 2 < w) {
            int argb3 = util.getARGB(x + 2, y);
            HSL.toHsl(argb3, hsl1);
            double d4 = HSL.get_value_a(hsl2, hsl1);
            if (d4 > 0.09d) {
                InteriorDivision a1 = new InteriorDivision(argb2, argb1, argb3);
                if (a1.is_InRange1(0.0d, value2)) {
                    flag = true;
                    x2 = x + 3;
                    ad9[c++] = d2;
                    ad9[c++] = d4;
                    d1 = d4;
                    double[] hsl5 = HSL.getHsl_b(hsl1, (double[]) null);
                    hsl7 = UtVector.get_diff(hsl0, hsl5, (double[]) null);
                }
            }
        }
        if (!flag)
            return null;
        for (; x2 < w && x2 < x + 20; x2++) {
            HSL.toHsl(util.getARGB(x2, y), hsl1);
            double d3 = HSL.get_value_a(hsl2, hsl1);
            if (d3 - d1 <= 0.09d)
                break;
            double[] hsl9 = HSL.getHsl_b(hsl1, (double[]) null);
            UtVector.get_diff(hsl0, hsl9, hsl8);
            double a = UtVector.getAngle(hsl7, hsl8);
            if (a > 0.69813170079773179d) { // 40 degree [radian]
                break;
            }
            ad9[c++] = d3;
            d1 = d3;
        }

        int k2 = x2 - x - 1;
        argb2 = util.getARGB(x + k2, y);
        if (k2 == 0 || 20 <= k2) {
            return null;
        }
        if (x + k2 < w - 2) {
            int l2 = util.getARGB(x + k2 + 1, y);
            HSL.toHsl(l2, hsl1);
            double d6 = HSL.get_value_a(hsl2, hsl1);
            if (d6 - d1 > value1) {
                InteriorDivision a2 = new InteriorDivision(l2, argb1, argb2);
                if (a2.is_InRange3(0.0D, value2)) {
                    argb2 = l2;
                    k2++;
                    ad9[c++] = d6;
                }
            }
        }
        return new EdgeX(x, x + k2, y, argb1, argb2, ad9);
    }

    private static EdgeY createEdgeY(UtImage util, int x, int y) throws Exception {
        int h = util.getHeight();
        double d1 = 0.0D;
        int argb1 = util.getARGB(x, y);
        double[] hsl1 = HSL.toHsl(argb1, (double[]) null);
        double[] hsl0 = new double[3];
        double[] hsl2 = HSL.getHsl_b(hsl1, (double[]) null);
        double[] hsl7 = null;
        double[] hsl8 = new double[3];
        double[] result = new double[21];
        int i = 0;
        int argb2 = util.getARGB(x, y + 1);
        HSL.toHsl(argb2, hsl0);
        double d2 = HSL.get_value_a(hsl1, hsl0);
        boolean flag = false;
        int y1 = y + 1;
        if (d2 > 0.09d) {
            flag = true;
            y1 = y + 2;
            result[i++] = d2;
            d1 = d2;
            double[] hsl4 = HSL.getHsl_b(hsl0, (double[]) null);
            hsl7 = UtVector.get_diff(hsl2, hsl4, (double[]) null);
        } else if (d2 > value1 && y + 2 < h) {
            int argb3 = util.getARGB(x, y + 2);
            HSL.toHsl(argb3, hsl0);
            double hsl5 = HSL.get_value_a(hsl1, hsl0);
            if (hsl5 > 0.09d) {
                InteriorDivision a1 = new InteriorDivision(argb2, argb1, argb3);
                if (a1.is_InRange1(0.0D, value2)) {
                    flag = true;
                    y1 = y + 3;
                    result[i++] = d2;
                    result[i++] = hsl5;
                    d1 = hsl5;
                    double[] hsl6 = HSL.getHsl_b(hsl0, (double[]) null);
                    hsl7 = UtVector.get_diff(hsl2, hsl6, (double[]) null);
                }
            }
        }
        if (!flag) {
            return null;
        }
        for (; y1 < h && y1 < y + 20; y1++) {
            HSL.toHsl(util.getARGB(x, y1), hsl0);
            double d8 = HSL.get_value_a(hsl1, hsl0);
            if (d8 - d1 <= 0.09d) {
                break;
            }
            double[] hsl9 = HSL.getHsl_b(hsl0, (double[]) null);
            UtVector.get_diff(hsl2, hsl9, hsl8);
            double a = UtVector.getAngle(hsl7, hsl8);
            if (a > 0.69813170079773179d) { // 40 degree [radian]
                break;
            }
            result[i++] = d8;
            d1 = d8;
        }

        int y2 = y1 - y - 1;
        argb2 = util.getARGB(x, y + y2);
        if (y2 == 0 || 20 <= y2)
            return null;
        if (y + y2 < h - 2) {
            int argb3 = util.getARGB(x, y + y2 + 1);
            HSL.toHsl(argb3, hsl0);
            double d6 = HSL.get_value_a(hsl1, hsl0);
            if (d6 - d1 > value1) {
                InteriorDivision a2 = new InteriorDivision(argb3, argb1, argb2);
                if (a2.is_InRange3(0.0d, value2)) {
                    argb2 = argb3;
                    y2++;
                    result[i++] = d6;
                }
            }
        }
        return new EdgeY(x, y, y + y2, argb1, argb2, result);
    }

    private static boolean contains(EdgeData edgeData, int y, EdgeX edgeX) {
        for (int x = edgeX.startX; x <= edgeX.endX; x++) {
            EdgeY[] edgeXs = edgeData.getEdgeYs(x, y);
            for (int i = 0; i < edgeXs.length; i++) {
                EdgeY edgeY = edgeXs[i];
                if (edgeX.get_color2() < edgeY.get_color2() / 1.5d && ScalingUtil.isNearly(edgeX, edgeY)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean contains(EdgeData edgeData, int x, EdgeY edgeY) {
        int y = edgeY.startY;
        for (; x <= edgeY.endY; x++) {
            EdgeX[] edgeXs = edgeData.getEdgeXs(x, y);
            for (int i = 0; i < edgeXs.length; i++) {
                EdgeX edgeX = edgeXs[i];
                if (edgeY.get_color2() < edgeX.get_color2() / 1.5d && ScalingUtil.isNearly(edgeY, edgeX)) {
                    return true;
                }
            }
        }

        return false;
    }
}
