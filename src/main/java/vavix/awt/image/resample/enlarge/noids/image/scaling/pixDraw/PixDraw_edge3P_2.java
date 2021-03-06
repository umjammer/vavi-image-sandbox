
package vavix.awt.image.resample.enlarge.noids.image.scaling.pixDraw;

import java.awt.Point;

import vavix.awt.image.resample.enlarge.noids.image.util.UtPoint;


/** g */
public class PixDraw_edge3P_2 extends PixDraw_edge3P {

    private static final long serialVersionUID = 1L;

    private static boolean debug = false;

    static UtPoint util;
    static Object object;
    static boolean working = false;

    public PixDraw_edge3P_2(Point p, Point.Double p1, Point.Double sp, Point.Double ep, int rgb1, int rgb2, double scaleX, double scaleY) {
        super(p, p1, sp, ep, rgb1, rgb2, scaleX, scaleY);
    }

    public int getRgb(int x, int y, double x1, double y1, double scaleX, double scaleY) {
        return getRgb(x, y, x1, y1, scaleX, scaleY, point, startPoint, endPoint, 3);
    }

    public boolean get_flag_a(int x, int y, double x1, double y1) {
        return contains(x1, y1, point, startPoint, endPoint);
    }

    private int getRgb(int x,
                  int y,
                  double x1,
                  double y1,
                  double scaleX,
                  double scaleY,
                  Point.Double p1,
                  Point.Double p2,
                  Point.Double p3,
                  int l) {
        int sq = l * l;
        int r = 0;
        int g = 0;
        int b = 0;
        if (l == 1 || scaleX == 0.0d || scaleY == 0.0d) {
            double x2 = x1 + scaleX / 2d;
            double y2 = y1 + scaleY / 2d;
            boolean flag = contains(x2, y2, p1, p2, p3);
            int rgb = getRgb_a(x, y, x2, y2, flag);
            return rgb;
        }
        for (int xi = 0; xi < l; xi++) {
            for (int yi = 0; yi < l; yi++) {
                double x3 = x1 + (scaleX * yi) / (l - 1);
                double y3 = y1 + (scaleY * xi) / (l - 1);
                boolean flag = contains(x3, y3, p1, p2, p3);
                int rgb = getRgb_a(x, y, x3, y3, flag);
                r += rgb >>> 16 & 0xff;
                g += rgb >>> 8 & 0xff;
                b += rgb & 0xff;
            }
        }

        r /= sq;
        g /= sq;
        b /= sq;
        if (r > 255 || g > 255 || b > 255) {
            throw new RuntimeException("おかしい！！！");
        } else {
            return 0xff000000 | r << 16 | g << 8 | b;
        }
    }

    private boolean contains(double x, double y, Point.Double p1, Point.Double p2, Point.Double p3) {
        if (object == this) {
            working = true;
            boolean flag = util.contains(x, y);
            working = false;
            return flag;
        }
        if (!working) {
            working = true;
            util = new UtPoint(p2, p1, p3);
            object = this;
            boolean flag = util.contains(x, y);
            working = false;
            return flag;
        } else {
            return new UtPoint(p2, p1, p3).contains(x, y);
        }
    }

    static {
        if (debug) {
            System.out.println(" < DEBUG : vavix.awt.image.resample.enlarge.noids.image.scaling.pixDraw.PixDraw_edge3P > ");
        }
    }
}
