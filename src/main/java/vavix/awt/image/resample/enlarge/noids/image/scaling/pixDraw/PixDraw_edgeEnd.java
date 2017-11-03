
package vavix.awt.image.resample.enlarge.noids.image.scaling.pixDraw;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import vavix.awt.image.resample.enlarge.noids.image.scaling.ScalingUtil;
import vavix.awt.image.resample.enlarge.noids.util.UtMath;


/** a */
public class PixDraw_edgeEnd implements vavix.awt.image.resample.enlarge.noids.image.scaling.pixDraw.Pixel {

    private static final long serialVersionUID = 1L;

    private static boolean debug = false;
    protected Point.Double endPoint = new Point.Double();
    protected Point.Double startPoint = new Point.Double();
    protected int direction;
    protected byte scaleX;
    protected byte scaleY;

    protected byte r0;
    protected byte g0;
    protected byte b0;
    protected byte r1;
    protected byte g1;
    protected byte b1;
    protected byte r2;
    protected byte g2;
    protected byte b2;
    protected byte r3;
    protected byte g3;
    protected byte b3;

    public PixDraw_edgeEnd(Point p, Point.Double sp, Point.Double ep, int i, int rgb2, int rgb3, double sx, double sy) {
        double d = 0.0001d;
        if (sp.x == p.x)
            sp.x += d;
        else if (sp.x == (p.x + 1))
            sp.x -= d;
        if (sp.y == p.y)
            sp.y += d;
        else if (sp.y == (p.y + 1))
            sp.y -= d;
        endPoint.x = (float) ep.x;
        endPoint.y = (float) ep.y;
        startPoint.x = (float) sp.x;
        startPoint.y = (float) sp.y;
        scaleX = UtMath.limit8bit(sx);
        scaleY = UtMath.limit8bit(sy);
        boolean isE = ep.x == p.x;
        boolean isW = ep.x == (p.x + 1);
        boolean isS = ep.y == p.y;
        boolean isN = ep.y == (p.y + 1);
        if (isE && isS)
            direction = sp.y - p.y <= sp.x - p.x ? 2 : 1;
        else if (isE && isN)
            direction = (p.y + 1) - sp.y <= sp.x - p.x ? 2 : 3;
        else if (isW && isS)
            direction = sp.y - p.y <= (p.x + 1) - sp.x ? 0 : 1;
        else if (isW && isN)
            direction = (p.y + 1) - sp.y <= (p.x + 1) - sp.x ? 0 : 3;
        else if (isE)
            direction = 2;
        else if (isW)
            direction = 0;
        else if (isS)
            direction = 1;
        else if (isN)
            direction = 3;
        else
            throw new RuntimeException("おかしな状態!!");
        switch (direction) {
        case 3:
            setCornerColor(p.x, p.y, 0, rgb2);
            setCornerColor(p.x, p.y, 2, rgb2);
            setCornerColor(p.x, p.y, 1, rgb3);
            setCornerColor(p.x, p.y, 3, rgb3);
            break;
        case 1:
            setCornerColor(p.x, p.y, 1, rgb2);
            setCornerColor(p.x, p.y, 3, rgb2);
            setCornerColor(p.x, p.y, 0, rgb3);
            setCornerColor(p.x, p.y, 2, rgb3);
            break;
        case 0:
            setCornerColor(p.x, p.y, 2, rgb2);
            setCornerColor(p.x, p.y, 3, rgb2);
            setCornerColor(p.x, p.y, 0, rgb3);
            setCornerColor(p.x, p.y, 1, rgb3);
            break;
        case 2:
            setCornerColor(p.x, p.y, 2, rgb3);
            setCornerColor(p.x, p.y, 3, rgb3);
            setCornerColor(p.x, p.y, 0, rgb2);
            setCornerColor(p.x, p.y, 1, rgb2);
            break;
        }
    }

    public int getRgb(int x, int y, double x1, double y1, double scaleX, double scaleY) {
        return getRgb_b(x, y, x1 + scaleX * 0.5d, y1 + scaleY * 0.5d);
    }

    private int toArgb(byte r, byte g, byte b) {
        return 0xff000000 | (r & 0xff) << 16 | (g & 0xff) << 8 | b & 0xff;
    }

    public int getRgb_b(int x, int y, double x1, double y1) {
        int argb2 = c(x, y, x1, y1);
        double rateX;
        double rateY;
        int argb;
        switch (direction) {
        case 2:
            rateX = (startPoint.x - x1) / (startPoint.x - x);
            if (rateX < 0.0d)
                rateX = 0.0d;
            else if (rateX > 1.0d)
                rateX = 1.0d;
            double v2 = rateX * endPoint.y + (1.0d - rateX) * startPoint.y;
            if (y1 < v2) {
                rateY = 1.0d - (v2 - y1) / (endPoint.y - y);
                argb = toArgb(r0, g0, b0);
            } else {
                rateY = 1.0d - (y1 - v2) / ((y + 1) - endPoint.y);
                argb = toArgb(r2, g2, b2);
            }
            break;
        case 0:
            rateX = (x1 - startPoint.x) / ((x + 1) - startPoint.x);
            if (rateX < 0.0d)
                rateX = 0.0d;
            else if (rateX > 1.0d)
                rateX = 1.0d;
            double v0 = rateX * endPoint.y + (1.0d - rateX) * startPoint.y;
            if (y1 < v0) {
                rateY = 1.0d - (v0 - y1) / (endPoint.y - y);
                argb = toArgb(r1, g1, b1);
            } else {
                rateY = 1.0d - (y1 - v0) / ((y + 1) - endPoint.y);
                argb = toArgb(r3, g3, b3);
            }
            break;
        case 3:
            rateX = (y1 - startPoint.y) / ((y + 1) - startPoint.y);
            if (rateX < 0.0d)
                rateX = 0.0d;
            else if (rateX > 1.0d)
                rateX = 1.0d;
            double v3 = rateX * endPoint.x + (1.0d - rateX) * startPoint.x;
            if (x1 < v3) {
                rateY = 1.0d - (v3 - x1) / (endPoint.x - x);
                argb = toArgb(r2, g2, b2);
            } else {
                rateY = 1.0d - (x1 - v3) / ((x + 1) - endPoint.x);
                argb = toArgb(r3, g3, b3);
            }
            break;
        case 1:
            rateX = (startPoint.y - y1) / (startPoint.y - y);
            if (rateX < 0.0d)
                rateX = 0.0d;
            else if (rateX > 1.0d)
                rateX = 1.0d;
            double v1 = rateX * endPoint.x + (1.0d - rateX) * startPoint.x;
            if (x1 < v1) {
                rateY = 1.0d - (v1 - x1) / (endPoint.x - x);
                argb = toArgb(r0, g0, b0);
            } else {
                rateY = 1.0d - (x1 - v1) / ((x + 1) - endPoint.x);
                argb = toArgb(r1, g1, b1);
            }
            break;

        default:
            throw new RuntimeException("未実装 : " + direction);
        }
        if (rateY < 0.0d)
            rateY = 0.0d;
        else if (rateY > 1.0d)
            rateY = 1.0d;
        double rate = rateX + rateX * rateY;
        if (rate > 1.0d)
            rate = 1.0d;
        else if (rate < 0.0d)
            rate = 0.0d;
        return ScalingUtil.blend(argb, argb2, rate);
    }

    public boolean isValid() {
        return false;
    }

    public void setCornerColor(int r, int g, int direction, int rgb) {
        switch (direction) {
        case 0:
            r0 = (byte) (rgb >>> 16 & 0xff);
            g0 = (byte) (rgb >>> 8 & 0xff);
            b0 = (byte) (rgb & 0xff);
            break;
        case 1:
            r1 = (byte) (rgb >>> 16 & 0xff);
            g1 = (byte) (rgb >>> 8 & 0xff);
            b1 = (byte) (rgb & 0xff);
            break;
        case 2:
            r2 = (byte) (rgb >>> 16 & 0xff);
            g2 = (byte) (rgb >>> 8 & 0xff);
            b2 = (byte) (rgb & 0xff);
            break;
        case 3:
            r3 = (byte) (rgb >>> 16 & 0xff);
            g3 = (byte) (rgb >>> 8 & 0xff);
            b3 = (byte) (rgb & 0xff);
            break;
        default:
            throw new RuntimeException("おかしな状態");
        }
    }

    /** @return argb */
    public int c(int x1, int y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        int r = (int) ((1.0d - dx) * (1.0d - dy) * (r0 & 0xff) +
                dx * (1.0d - dy) * (r1 & 0xff) +
                (1.0d - dx) * dy * (r2 & 0xff) +
                dx * dy * (r3 & 0xff));
        int g = (int) ((1.0d - dx) * (1.0d - dy) * (g0 & 0xff) +
                dx * (1.0d - dy) * (g1 & 0xff) +
                (1.0d - dx) * dy * (g2 & 0xff) +
                dx * dy * (g3 & 0xff));
        int b = (int) ((1.0d - dx) * (1.0d - dy) * (b0 & 0xff) +
                dx * (1.0d - dy) * (b1 & 0xff) +
                (1.0d - dx) * dy * (b2 & 0xff) +
                dx * dy * (b3 & 0xff));
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

    private void writeObject(ObjectOutputStream oos) throws IOException {
        throw new RuntimeException("未修正");
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        throw new RuntimeException("未修正");
    }

    static {
        if (debug)
            System.out.println(" < DEBUG : vavix.awt.image.resample.enlarge.noids.image.scaling.pixDraw.PixDraw_edgeEnd > ");
    }
}
