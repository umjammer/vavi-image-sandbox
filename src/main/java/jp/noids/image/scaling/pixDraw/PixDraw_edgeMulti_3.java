
package jp.noids.image.scaling.pixDraw;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jp.noids.image.scaling.Constants;
import jp.noids.util.UtMath;


/** b */
public class PixDraw_edgeMulti_3 implements Constants, Pixel {

    private static final long serialVersionUID = 1L;

    private static boolean debug = false;
    Pixel pixel1;
    Pixel pixel2;
    boolean flag;
    protected byte scaleX;
    protected byte scaleY;

    public PixDraw_edgeMulti_3(Point p, Pixel px1, Pixel px2, double sx, double sy) {
        pixel1 = px1;
        pixel2 = px2;
        scaleX = UtMath.limit8bit(sx);
        scaleY = UtMath.limit8bit(sy);
        if (!(px1 instanceof PixDraw_edge3P) || !(px2 instanceof PixDraw_edge3P)) {
            throw new RuntimeException("PixDraw_edge3P以外の重複描画は未実装 !!");
        }
        PixDraw_edge3P pix1 = (PixDraw_edge3P) px1;
        PixDraw_edge3P pix2 = (PixDraw_edge3P) px2;
        Point.Double sp = pix2.startPoint;
        boolean flag = pix1.a((int) sp.x, (int) sp.y, sp.x, sp.y);
        this.flag = flag;
    }

    public int getRgb(int x, int y, double x1, double y1, double scaleX, double scaleY) {
        byte byte0 = 3;
        int sq = byte0 * byte0;
        int r = 0;
        int g = 0;
        int b = 0;
        if (byte0 == 1 || scaleX == 0.0d || scaleY == 0.0d) {
            double x2 = x1 + scaleX / 2d;
            double y2 = y1 + scaleY / 2d;
            int rgb = b(x, y, x2, y2, 0.0d, 0.0d);
            return rgb;
        }
        for (int dy = 0; dy < byte0; dy++) {
            for (int dx = 0; dx < byte0; dx++) {
                double x2 = x1 + (scaleX * dx) / (byte0 - 1);
                double x3 = y1 + (scaleY * dy) / (byte0 - 1);
                int rgb = b(x, y, x2, x3, 0.0D, 0.0D);
                r += rgb >>> 16 & 0xff;
                g += rgb >>> 8 & 0xff;
                b += rgb & 0xff;
            }
        }

        r /= sq;
        g /= sq;
        b /= sq;
        if (r > 255 || g > 255 || b > 255)
            throw new RuntimeException("おかしい！！！");
        else
            return 0xff000000 | r << 16 | g << 8 | b;
    }

    private int b(int x, int y, double x1, double y1, double scaleX, double scaleY) {
        PixDraw_edge3P pixdraw_edge3p = (PixDraw_edge3P) pixel1;
        PixDraw_edge3P pixdraw_edge3p1 = (PixDraw_edge3P) pixel2;
        boolean flag = pixdraw_edge3p.a(x, y, x1 + scaleX / 2D, y1 + scaleY / 2D);
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
            throw new RuntimeException("おかしな状態");
        }
        PixDraw_edge3P pix1 = (PixDraw_edge3P) pixel1;
        PixDraw_edge3P pix2 = (PixDraw_edge3P) pixel2;
        boolean flag = pix1.a(x, y, x1, y1);
        @SuppressWarnings("unused")
        boolean flag1 = pix2.a(x, y, x1, y1);
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
        throw new RuntimeException("未修正");
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        scaleX = ois.readByte();
        scaleY = ois.readByte();
        throw new RuntimeException("未修正");
    }

    static {
        if (debug)
            System.out.println(" < DEBUG : jp.noids.image.scaling.pixDraw.PixDraw_edgeMulti > ");
    }
}
