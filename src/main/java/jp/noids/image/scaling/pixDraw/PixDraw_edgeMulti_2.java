
package jp.noids.image.scaling.pixDraw;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jp.noids.image.scaling.Constants;
import jp.noids.image.scaling.ScalingUtil;
import jp.noids.util.UtMath;


/** c */
public class PixDraw_edgeMulti_2 implements Constants, jp.noids.image.scaling.pixDraw.Pixel {

    private static final long serialVersionUID = 1L;

    private static boolean debug = false;
    PixDraw_edge3P pixel1;
    PixDraw_edgeEnd pixel2;
    protected byte scaleX;
    protected byte scaleY;
    boolean flag;
    static boolean needToShow = false;

    @SuppressWarnings("cast")
    public PixDraw_edgeMulti_2(Point p, PixDraw_edge3P pixel1, PixDraw_edgeEnd pixel2, double sx, double sy) {
        this.pixel1 = pixel1;
        this.pixel2 = pixel2;
        scaleX = UtMath.limit8bit(sx);
        scaleY = UtMath.limit8bit(sy);
        if (!(pixel1 instanceof PixDraw_edge3P) || !(pixel2 instanceof PixDraw_edgeEnd))
            throw new RuntimeException("PixDraw_edge3P+PixDraw_edgeEnd以外の重複描画は未実装 !!");
        PixDraw_edge3P pixel11 = pixel1;
        PixDraw_edgeEnd pixel22 = pixel2;
        Point.Double ep = pixel22.endPoint;
        boolean flag = pixel11.a((int) ep.x, (int) ep.y, ep.x, ep.y);
        Point.Double sp = pixel22.startPoint;
        boolean flag1 = pixel11.a((int) sp.x, (int) sp.y, sp.x, sp.y);
        if (flag != flag1) {
            if (needToShow) {
                System.err.println("クロスしている重複境界線の描画は未対応です\n  jp.noids.image.scaling.pixDraw.PixDraw_edgeMulti#PixDraw_edgeMulti( ) ");
                needToShow = false;
            }
        } else {
            this.flag = flag;
        }
    }

    public int getRgb(int x, int y, double x1, double y1, double scaleX, double scaleY) {
        boolean flag = pixel1.a(x, y, x1 + scaleX / 2d, y1 + scaleY / 2d);
        int argb;
        if (flag != this.flag) {
            argb = pixel1.getRgb(x, y, x1, y1, scaleX, scaleY);
        } else {
            int argb1 = pixel1.getRgb(x, y, x1, y1, scaleX, scaleY);
            int argb2 = pixel2.getRgb(x, y, x1, y1, scaleX, scaleY);
            argb = ScalingUtil.average(argb1, argb2);
        }
        return argb;
    }

    public void setCornerColor(int x, int y, int direction, int rgb) {
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
        boolean flag = pixel1.a(x, y, x1, y1);
        if (flag != this.flag) {
            pixel1.setCornerColor(x, y, direction, rgb);
        } else {
            pixel1.setCornerColor(x, y, direction, rgb);
            pixel2.setCornerColor(x, y, direction, rgb);
        }
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
