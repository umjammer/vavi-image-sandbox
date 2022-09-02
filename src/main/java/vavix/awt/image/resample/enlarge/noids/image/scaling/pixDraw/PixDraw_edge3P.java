
package vavix.awt.image.resample.enlarge.noids.image.scaling.pixDraw;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;

import vavi.util.Debug;
import vavix.awt.image.resample.enlarge.noids.image.scaling.ScalingUtil;
import vavix.awt.image.resample.enlarge.noids.util.UtMath;
import vavix.awt.image.resample.enlarge.noids.util.UtToString;


public class PixDraw_edge3P implements PixDraw {

    private static final long serialVersionUID = 1L;

    private static final boolean debug = false;

    protected Point.Double startPoint = new Point.Double();
    protected Point.Double endPoint = new Point.Double();
    protected Point.Double point = new Point.Double();
    protected int rgb1;
    protected int rgb2;
    protected int rgb3;
    protected int rgb4;
    protected int rgb;
    protected boolean contains00;
    protected boolean contains10;
    protected boolean contains01;
    protected boolean contains11;
    protected byte count1;
    protected byte scaleX;
    protected byte scaleY;

    public PixDraw_edge3P(Point p, Point.Double p1, Point.Double sp, Point.Double ep, int rgb1_, int rgb2_, double sx, double sy) {
        if (sp.equals(ep))
            System.err.println("start, end points are same. " + sp + " : vavix.awt.image.resample.enlarge.noids.image.scaling.point.ViewPoint_edge3P#ViewPoint_edge3P()");
        if (sp.equals(point) || ep.equals(p1)) {
            p1.x = (sp.x + ep.x) * 0.5d;
            p1.y = (sp.y + ep.y) * 0.5d;
        }
        startPoint.x = (float) sp.x;
        startPoint.y = (float) sp.y;
        endPoint.x = (float) ep.x;
        endPoint.y = (float) ep.y;
        point.x = (float) p1.x;
        point.y = (float) p1.y;
        final float MIN = 0.0001f;
        if (point.x == p.x)
            point.x += MIN;
        else if (point.x == (p.x + 1))
            point.x -= MIN;
        if (point.y == p.y)
            point.y += MIN;
        else if (point.y == (p.y + 1))
            point.y -= MIN;
        double dx = endPoint.x - startPoint.x;
        if (dx < 0.0d)
            dx = -dx;
        double dy = endPoint.y - startPoint.y;
        if (dy < 0.0d)
            dy = -dy;
        boolean isPortrait = dx < dy;
        boolean flagX0 = startPoint.x == p.x;
        boolean flagX1 = startPoint.x == (p.x + 1);
        boolean flagY0 = startPoint.y == p.y;
        boolean flagY1 = startPoint.y == (p.y + 1);
        if (flagX0 && flagY0) {
            if (isPortrait)
                startPoint.x += MIN;
            else
                startPoint.y += MIN;
        } else if (flagX0 && flagY1) {
            if (isPortrait)
                startPoint.x += MIN;
            else
                startPoint.y -= MIN;
        } else if (flagX1 && flagY0) {
            if (isPortrait)
                startPoint.x -= MIN;
            else
                startPoint.y += MIN;
        } else if (flagX1 && flagY1)
            if (isPortrait)
                startPoint.x -= MIN;
            else
                startPoint.y -= MIN;
        flagX0 = endPoint.x == p.x;
        flagX1 = endPoint.x == (p.x + 1);
        flagY0 = endPoint.y == p.y;
        flagY1 = endPoint.y == (p.y + 1);
        if (flagX0 && flagY0) {
            if (isPortrait)
                endPoint.x += MIN;
            else
                endPoint.y += MIN;
        } else if (flagX0 && flagY1) {
            if (isPortrait)
                endPoint.x += MIN;
            else
                endPoint.y -= MIN;
        } else if (flagX1 && flagY0) {
            if (isPortrait)
                endPoint.x -= MIN;
            else
                endPoint.y += MIN;
        } else if (flagX1 && flagY1)
            if (isPortrait)
                endPoint.x -= MIN;
            else
                endPoint.y -= MIN;
        contains00 = get_flag_a(p.x, p.y, p.x, p.y);
        contains10 = get_flag_a(p.x, p.y, p.x + 1, p.y);
        contains01 = get_flag_a(p.x, p.y, p.x, p.y + 1);
        contains11 = get_flag_a(p.x, p.y, p.x + 1, p.y + 1);
        rgb1 = contains00 ? rgb1_ : rgb2_;
        rgb2 = contains10 ? rgb1_ : rgb2_;
        rgb3 = contains01 ? rgb1_ : rgb2_;
        rgb4 = contains11 ? rgb1_ : rgb2_;
        count1 = 0;
        if (contains00)
            count1++;
        if (contains10)
            count1++;
        if (contains01)
            count1++;
        if (contains11)
            count1++;
        if (count1 == 0 || count1 == 4)
            rgb = contains00 ? rgb2_ : rgb1_;
        this.scaleX = UtMath.limit8bit(sx);
        this.scaleY = UtMath.limit8bit(sy);
    }

    public int getRgb(int x, int y, double x1, double y1, double scaleX, double scaleY) {
        return getRgb_a(x, y, x1, y1, scaleX, scaleY, point, startPoint, endPoint, 3);
    }

    public boolean get_flag_a(int x1, int y1, double x2, double y2) {
        return ScalingUtil.contains_d(x2, y2, point, startPoint, endPoint);
    }

    public boolean isValid() {
        return true;
    }

    /** @return argb */
    private int getRgb_a(int x, int y, double x1, double y1, double scaleX, double scaleY, Point.Double p1, Point.Double p2, Point.Double p3, int l) {
        int sq = l * l;
        int r = 0;
        int g = 0;
        int b = 0;
        if (l == 1 || scaleX == 0.0d || scaleY == 0.0d) {
            double x2 = x1 + scaleX / 2d;
            double y2 = y1 + scaleY / 2d;
            boolean flag = ScalingUtil.contains_d(x2, y2, p1, p2, p3);
            int argb = getRgb_a(x, y, x2, y2, flag);
            return argb;
        }
        for (int xi = 0; xi < l; xi++) {
            for (int yi = 0; yi < l; yi++) {
                double x2 = x1 + (scaleX * yi) / (l - 1);
                double y2 = y1 + (scaleY * xi) / (l - 1);
                boolean flag1 = ScalingUtil.contains_d(x2, y2, p1, p2, p3);
                int rgb = getRgb_a(x, y, x2, y2, flag1);
                r += rgb >>> 16 & 0xff;
                g += rgb >>> 8 & 0xff;
                b += rgb & 0xff;
            }
        }

        r /= sq;
        g /= sq;
        b /= sq;
        if (r > 255 || g > 255 || b > 255)
            throw new IllegalStateException("wrong rgb");
        else
            return 0xff000000 | r << 16 | g << 8 | b;
    }

    /** @return argb */
    protected int getRgb_a(int x, int y, double x1, double y1, boolean contains) {
        int c = contains ? (int) count1 : 4 - count1;
        if (c == 0)
            return rgb;
        if (c == 4) {
            double x2 = x1 - x;
            double y2 = y1 - y;
            int r1 = rgb1 >>> 16 & 0xff;
            int g1 = rgb1 >>> 8 & 0xff;
            int b1 = rgb1 & 0xff;
            int r2 = rgb2 >>> 16 & 0xff;
            int g2 = rgb2 >>> 8 & 0xff;
            int b2 = rgb2 & 0xff;
            int r3 = rgb3 >>> 16 & 0xff;
            int g3 = rgb3 >>> 8 & 0xff;
            int b3 = rgb3 & 0xff;
            int r4 = rgb4 >>> 16 & 0xff;
            int g4 = rgb4 >>> 8 & 0xff;
            int b4 = rgb4 & 0xff;
            int r = (int) ((1.0d - x2) * (1.0d - y2) * (r1 & 0xff) + x2 * (1.0d - y2) * (r2 & 0xff)
                            + (1.0d - x2) * y2 * (r3 & 0xff) + x2 * y2 * (r4 & 0xff));
            int g = (int) ((1.0d - x2) * (1.0d - y2) * (g1 & 0xff) + x2 * (1.0d - y2) * (g2 & 0xff)
                            + (1.0d - x2) * y2 * (g3 & 0xff) + x2 * y2 * (g4 & 0xff));
            int b = (int) ((1.0d - x2) * (1.0d - y2) * (b1 & 0xff) + x2 * (1.0d - y2) * (b2 & 0xff)
                            + (1.0d - x2) * y2 * (b3 & 0xff) + x2 * y2 * (b4 & 0xff));
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
            return 0xff000000 | r << 16 | g << 8 | b;
        }
        if (c == 1) {
            if (contains00 == contains)
                return rgb1;
            if (contains10 == contains)
                return rgb2;
            if (contains01 == contains)
                return rgb3;
            if (contains11 == contains)
                return rgb4;
            else
                throw new IllegalStateException("state3");
        }
        if (c == 2) {
            if (contains00 == contains && contains10 == contains) {
                double rate1 = (x + 1) - x1;
                return ScalingUtil.blend(rgb1, rgb2, rate1);
            }
            if (contains01 == contains && contains11 == contains) {
                double rate2 = (x + 1) - x1;
                return ScalingUtil.blend(rgb3, rgb4, rate2);
            }
            if (contains00 == contains && contains01 == contains) {
                double rate3 = (y + 1) - y1;
                return ScalingUtil.blend(rgb1, rgb3, rate3);
            }
            if (contains10 == contains && contains11 == contains) {
                double rate4 = (y + 1) - y1;
                return ScalingUtil.blend(rgb2, rgb4, rate4);
            } else {
                throw new IllegalStateException("separated points?");
            }
        }
        if (c == 3) {
            double v1 = x1 - x;
            double rate5 = y1 - y;
            if (v1 < 0.0d)
                v1 = 0.0d;
            else if (v1 > 1.0d)
                v1 = 1.0d;
            if (rate5 < 0.0d)
                rate5 = 0.0d;
            else if (rate5 > 1.0d)
                rate5 = 1.0d;
            if (contains00 != contains) {
                if (rate5 > -v1 + 1.0d)
                    if (v1 == 1.0d) {
                        return ScalingUtil.blend(rgb4, rgb2, rate5);
                    } else {
                        double v2 = (rate5 - 1.0d) / (v1 - 1.0d);
                        double rate6 = v2 / (v2 + 1.0d);
                        double rate7 = (v1 - rate6) / (1.0d - rate6);
                        int argb = ScalingUtil.blend(rgb3, rgb2, 1.0d - rate6);
                        return ScalingUtil.blend(rgb4, argb, rate7);
                    }
                if (v1 == 0.0d) {
                    return rgb3;
                } else {
                    double d12 = rate5 / v1;
                    double rate8 = d12 / (d12 + 1.0d);
                    int argb = ScalingUtil.blend(rgb3, rgb2, rate8);
                    return argb;
                }
            }
            if (contains10 != contains) {
                if (rate5 > v1)
                    if (v1 == 0.0d) {
                        return ScalingUtil.blend(rgb1, rgb3, 1.0d - rate5);
                    } else {
                        double d13 = (rate5 - 1.0d) / (v1 - 0.0d);
                        double rate9 = 1.0d / (1.0d - d13);
                        double rate17 = (rate9 - v1) / rate9;
                        int argb = ScalingUtil.blend(rgb1, rgb4, 1.0d - rate9);
                        return ScalingUtil.blend(rgb3, argb, rate17);
                    }
                if (v1 == 0.0D) {
                    return rgb1;
                } else {
                    double d14 = (rate5 - 0.0d) / (v1 - 1.0d);
                    double rate16 = 1.0D / (1.0d - d14);
                    int argb = ScalingUtil.blend(rgb1, rgb4, rate16);
                    return argb;
                }
            }
            if (contains01 != contains) {
                if (rate5 < v1)
                    if (v1 == 1.0d) {
                        return ScalingUtil.blend(rgb2, rgb4, 1.0d - rate5);
                    } else {
                        double d15 = (rate5 - 0.0d) / (v1 - 1.0d);
                        double rate10 = d15 / (d15 - 1.0d);
                        double rate15 = (v1 - rate10) / (1.0d - rate10);
                        int argb = ScalingUtil.blend(rgb1, rgb4, 1.0d - rate10);
                        return ScalingUtil.blend(rgb2, argb, rate15);
                    }
                if (v1 == 0.0d) {
                    return rgb1;
                } else {
                    double d16 = (rate5 - 1.0d) / (v1 - 0.0d);
                    double rate11 = d16 / (d16 - 1.0d);
                    int argb = ScalingUtil.blend(rgb1, rgb4, rate11);
                    return argb;
                }
            }
            if (contains11 != contains) {
                if (rate5 < -v1 + 1.0d)
                    if (v1 == 0.0d) {
                        return ScalingUtil.blend(rgb1, rgb3, 1.0d - rate5);
                    } else {
                        double d17 = rate5 / v1;
                        double rate12 = 1.0D / (d17 + 1.0d);
                        double rate14 = (rate12 - v1) / rate12;
                        int argb = ScalingUtil.blend(rgb3, rgb2, 1.0d - rate12);
                        return ScalingUtil.blend(rgb1, argb, rate14);
                    }
                if (v1 == 1.0d) {
                    return rgb2;
                } else {
                    double d18 = (rate5 - 1.0d) / (v1 - 1.0d);
                    double rate13 = 1.0d / (d18 + 1.0d);
                    int argb = ScalingUtil.blend(rgb3, rgb2, rate13);
                    return argb;
                }
            } else {
                throw new IllegalStateException("state1");
            }
        } else {
            throw new IllegalStateException("state2");
        }
    }

    public void setCornerColor(int x, int y, int direction, int rgb) {
        switch (direction) {
        case 0:
            rgb1 = rgb;
            break;
        case 1:
            rgb2 = rgb;
            break;
        case 2:
            rgb3 = rgb;
            break;
        case 3:
            rgb4 = rgb;
            break;
        default:
            throw new IllegalStateException("impossible");
        }
    }

    public int getCornerColor(int x, int y, int direction) {
        switch (direction) {
        case 0:
            return rgb1;
        case 1:
            return rgb2;
        case 2:
            return rgb3;
        case 3:
            return rgb4;
        }
        throw new IllegalStateException("impossible");
    }

    public String toString() {
        return UtToString.toString(startPoint) + " > "
                + UtToString.toString(point)
                + " > "
                + UtToString.toString(endPoint);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeByte(scaleX);
        oos.writeByte(scaleY);
        throw new UnsupportedEncodingException("not implemented yet");
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        scaleX = ois.readByte();
        scaleY = ois.readByte();
        throw new UnsupportedEncodingException("not implemented yet");
    }

    static {
        Debug.println("vavix.awt.image.resample.enlarge.noids.image.scaling.pixDraw.PixDraw_edge3P");
    }
}
