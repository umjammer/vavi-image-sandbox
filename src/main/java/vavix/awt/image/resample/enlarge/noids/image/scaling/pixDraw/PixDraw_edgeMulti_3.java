
package vavix.awt.image.resample.enlarge.noids.image.scaling.pixDraw;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;

import vavix.awt.image.resample.enlarge.noids.image.scaling.Constants;
import vavix.awt.image.resample.enlarge.noids.util.UtMath;


/** b */
public class PixDraw_edgeMulti_3 implements Constants, Pixel {

    private static final long serialVersionUID = 1L;

    private static final boolean debug = false;
    private Pixel pixel1;
    private Pixel pixel2;
    private boolean flag;
    protected byte scaleX;
    protected byte scaleY;

    public PixDraw_edgeMulti_3(Point p, Pixel px1, Pixel px2, double sx, double sy) {
        pixel1 = px1;
        pixel2 = px2;
        scaleX = UtMath.limit8bit(sx);
        scaleY = UtMath.limit8bit(sy);
        if (!(px1 instanceof PixDraw_edge3P) || !(px2 instanceof PixDraw_edge3P)) {
            throw new IllegalArgumentException("only PixDraw_edge3P is supported for px1, px2");
        }
        PixDraw_edge3P pix1 = (PixDraw_edge3P) px1;
        PixDraw_edge3P pix2 = (PixDraw_edge3P) px2;
        Point.Double sp = pix2.startPoint;
        boolean flag = pix1.get_flag_a((int) sp.x, (int) sp.y, sp.x, sp.y);
        this.flag = flag;
    }

    public int getRgb(int x, int y, double x1, double y1, double scaleX, double scaleY) {
        int l = 3;
        int sq = l * l;
        int r = 0;
        int g = 0;
        int b = 0;
        if (l == 1 || scaleX == 0.0d || scaleY == 0.0d) {
            double x2 = x1 + scaleX / 2d;
            double y2 = y1 + scaleY / 2d;
            int rgb = getRgb_b(x, y, x2, y2, 0.0d, 0.0d);
            return rgb;
        }
        for (int dy = 0; dy < l; dy++) {
            for (int dx = 0; dx < l; dx++) {
                double x2 = x1 + (scaleX * dx) / (l - 1);
                double x3 = y1 + (scaleY * dy) / (l - 1);
                int rgb = getRgb_b(x, y, x2, x3, 0.0d, 0.0d);
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

    private int getRgb_b(int x, int y, double x1, double y1, double scaleX, double scaleY) {
        PixDraw_edge3P pixdraw_edge3p = (PixDraw_edge3P) pixel1;
        PixDraw_edge3P pixdraw_edge3p1 = (PixDraw_edge3P) pixel2;
        boolean flag = pixdraw_edge3p.get_flag_a(x, y, x1 + scaleX / 2d, y1 + scaleY / 2d);
        int argb;
        if (flag != this.flag)
            argb = pixdraw_edge3p.getRgb(x, y, x1, y1, scaleX, scaleY);
        else
            argb = pixdraw_edge3p1.getRgb(x, y, x1, y1, scaleX, scaleY);
        return argb;
    }

    public void setCornerColor(int x, int y, int direction, int argb) {
        double x1;
        double y1;
        switch (direction) {
        case 0:
            x1 = x;
            y1 = y;
            break;
        case 1:
            x1 = x + 1;
            y1 = y;
            break;
        case 2:
            x1 = x;
            y1 = y + 1;
            break;
        case 3:
            x1 = x + 1;
            y1 = y + 1;
            break;
        default:
            throw new IllegalStateException("impossible");
        }
        PixDraw_edge3P pix1 = (PixDraw_edge3P) pixel1;
        PixDraw_edge3P pix2 = (PixDraw_edge3P) pixel2;
        boolean flag = pix1.get_flag_a(x, y, x1, y1);
        @SuppressWarnings("unused")
        boolean flag1 = pix2.get_flag_a(x, y, x1, y1);
        if (flag != this.flag)
            pix1.setCornerColor(x, y, direction, argb);
        else
            pix2.setCornerColor(x, y, direction, argb);
    }

    public boolean isValid() {
        return true;
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
        if (debug)
            System.out.println(" < DEBUG : vavix.awt.image.resample.enlarge.noids.image.scaling.pixDraw.PixDraw_edgeMulti > ");
    }
}
