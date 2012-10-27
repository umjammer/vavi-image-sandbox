
package jp.noids.image.scaling.pixDraw;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import jp.noids.image.scaling.Constants;
import jp.noids.image.scaling.Direction;
import jp.noids.image.scaling.ScalingUtil;
import jp.noids.util.UtMath;


/** e */
public class PixDraw_edgeMulti implements Constants, Pixel {

    private static final long serialVersionUID = 1L;

    private static boolean debug = false;
    Pixel pixel1;
    Pixel pixel2;
    boolean f1;
    boolean f2;
    boolean f3;
    protected byte scaleX;
    protected byte scaleY;
    boolean hasWest;
    boolean hasEast;
    boolean hasNorth;
    boolean hasSouth;

    public PixDraw_edgeMulti(Point p, Pixel pixel1, Pixel pixel2, double sx, double sy) {
        this.pixel1 = pixel1;
        this.pixel2 = pixel2;
        scaleX = UtMath.limit8bit(sx);
        scaleY = UtMath.limit8bit(sy);
        if (!(pixel1 instanceof PixDraw_edge3P) || !(pixel2 instanceof PixDraw_edge3P))
            throw new RuntimeException("PixDraw_edge3P以外の重複描画は未実装 !!");
        PixDraw_edge3P pixel11 = (PixDraw_edge3P) pixel1;
        PixDraw_edge3P pixel22 = (PixDraw_edge3P) pixel2;
        Point.Double sp1 = pixel22.startPoint;
        boolean flag = pixel11.a((int) sp1.x, (int) sp1.y, sp1.x, sp1.y);
        Point.Double ep1 = pixel22.endPoint;
        boolean flag1 = pixel11.a((int) ep1.x, (int) ep1.y, ep1.x, ep1.y);
        Point.Double sp2 = pixel11.startPoint;
        boolean flag2 = pixel22.a((int) sp2.x, (int) sp2.y, sp2.x, sp2.y);
        Point.Double ep2 = pixel11.endPoint;
        boolean flag3 = pixel22.a((int) ep2.x, (int) ep2.y, ep2.x, ep2.y);
        if (flag != flag1 || flag2 != flag3) {
            f3 = true;
        } else {
            f1 = flag;
            f2 = flag2;
            f3 = false;
        }
        boolean flag4 = pixel11.a(p.x, p.y, p.x, p.y);
        boolean flag5 = pixel22.a(p.x, p.y, p.x, p.y);
        boolean flag6 = pixel11.a(p.x, p.y, p.x, p.y + 1);
        boolean flag7 = pixel22.a(p.x, p.y, p.x, p.y + 1);
        boolean flag8 = pixel11.a(p.x, p.y, p.x + 1, p.y);
        boolean flag9 = pixel22.a(p.x, p.y, p.x + 1, p.y);
        boolean flag10 = pixel11.a(p.x, p.y, p.x + 1, p.y + 1);
        boolean flag11 = pixel22.a(p.x, p.y, p.x + 1, p.y + 1);
        hasWest = a(flag4, flag5) == -a(flag6, flag7);
        hasEast = a(flag8, flag9) == -a(flag10, flag11);
        hasNorth = a(flag4, flag5) == -a(flag8, flag9);
        hasSouth = a(flag6, flag7) == -a(flag10, flag11);
    }

    public boolean a(Direction direction) {
        if (direction == Direction.ANY)
            return hasWest | hasEast | hasNorth | hasSouth;
        if (direction == Direction.NORTH)
            return hasNorth;
        if (direction == Direction.SOUTH)
            return hasSouth;
        if (direction == Direction.WEST)
            return hasWest;
        if (direction == Direction.EAST)
            return hasEast;
        else
            throw new RuntimeException("未実装");
    }

    public Direction[] getDirections() {
        List<Direction> directions = new ArrayList<Direction>();
        if (hasNorth)
            directions.add(Direction.NORTH);
        if (hasSouth)
            directions.add(Direction.SOUTH);
        if (hasWest)
            directions.add(Direction.WEST);
        if (hasEast)
            directions.add(Direction.EAST);
        return directions.toArray(new Direction[0]);
    }

    /** @return rgb */
    public int a(int x, int y, Direction direction) {
        PixDraw_edge3P pixel11 = (PixDraw_edge3P) pixel1;
        PixDraw_edge3P pixel22 = (PixDraw_edge3P) pixel2;
        int rgb1;
        int rgb2;
        if (direction == Direction.NORTH) {
            boolean flag = pixel11.startPoint.y < y + 0.0001D;
            rgb1 = pixel11.getCornerColor(x, y, flag ^ f1 ? 0 : 1);
            boolean flag4 = pixel22.startPoint.y < y + 0.0001D;
            rgb2 = pixel22.getCornerColor(x, y, flag4 ^ f2 ? 0 : 1);
        } else if (direction == Direction.SOUTH) {
            boolean flag1 = pixel11.startPoint.y > (y + 1) - 0.0001D;
            rgb1 = pixel11.getCornerColor(x, y, flag1 ^ f1 ? 3 : 2);
            boolean flag5 = pixel22.startPoint.y > (y + 1) - 0.0001D;
            rgb2 = pixel22.getCornerColor(x, y, flag5 ^ f2 ? 3 : 2);
        } else if (direction == Direction.WEST) {
            boolean flag2 = pixel11.startPoint.x < x + 0.0001D;
            rgb1 = pixel11.getCornerColor(x, y, flag2 ^ f1 ? 2 : 0);
            boolean flag6 = pixel22.startPoint.x < x + 0.0001D;
            rgb2 = pixel22.getCornerColor(x, y, flag6 ^ f2 ? 2 : 0);
        } else if (direction == Direction.EAST) {
            boolean flag3 = pixel11.startPoint.x > (x + 1) - 0.0001D;
            rgb1 = pixel11.getCornerColor(x, y, flag3 ^ f1 ? 1 : 3);
            boolean flag7 = pixel22.startPoint.x > (x + 1) - 0.0001D;
            rgb2 = pixel22.getCornerColor(x, y, flag7 ^ f2 ? 1 : 3);
        } else {
            throw new RuntimeException("おかしな指定 : " + direction);
        }
        return ScalingUtil.average(rgb1, rgb2);
    }

    public void a(int x, int y, Direction direction, int rgb) {
        PixDraw_edge3P pixel11 = (PixDraw_edge3P) pixel1;
        PixDraw_edge3P pixel22 = (PixDraw_edge3P) pixel2;
        if (direction == Direction.NORTH) {
            boolean flag = pixel11.startPoint.y < y + 0.0001D;
            pixel11.setCornerColor(x, y, flag ^ f1 ? 0 : 1, rgb);
            boolean flag4 = pixel22.startPoint.y < y + 0.0001D;
            pixel22.setCornerColor(x, y, flag4 ^ f2 ? 0 : 1, rgb);
        } else if (direction == Direction.SOUTH) {
            boolean flag1 = pixel11.startPoint.y < y + 0.0001D;
            pixel11.setCornerColor(x, y, flag1 ^ f1 ? 2 : 3, rgb);
            boolean flag5 = pixel22.startPoint.y < y + 0.0001D;
            pixel22.setCornerColor(x, y, flag5 ^ f2 ? 2 : 3, rgb);
        } else if (direction == Direction.WEST) {
            boolean flag2 = pixel11.startPoint.x < x + 0.0001D;
            pixel11.setCornerColor(x, y, flag2 ^ f1 ? 2 : 0, rgb);
            boolean flag6 = pixel22.startPoint.x < x + 0.0001D;
            pixel22.setCornerColor(x, y, flag6 ^ f2 ? 2 : 0, rgb);
        } else if (direction == Direction.EAST) {
            boolean flag3 = pixel11.startPoint.x < x + 0.0001D;
            pixel11.setCornerColor(x, y, flag3 ^ f1 ? 3 : 1, rgb);
            boolean flag7 = pixel22.startPoint.x < x + 0.0001D;
            pixel22.setCornerColor(x, y, flag7 ^ f2 ? 3 : 1, rgb);
        } else {
            throw new RuntimeException("おかしな指定 : " + direction);
        }
    }

    private int a(boolean flag, boolean flag1) {
        if (flag != f1 && flag1 == f2)
            return 1;
        return flag != f1 || flag1 == f2 ? 2 : -1;
    }

    public int getRgb(int x, int y, double x1, double y1, double scaleX, double scaleY) {
        PixDraw_edge3P pixdraw_edge3p = (PixDraw_edge3P) pixel1;
        PixDraw_edge3P pixdraw_edge3p1 = (PixDraw_edge3P) pixel2;
        boolean flag = pixdraw_edge3p.a(x, y, x1 + scaleX / 2D, y1 + scaleY / 2D);
        boolean flag1 = pixdraw_edge3p1.a(x, y, x1 + scaleX / 2D, y1 + scaleY / 2D);
        int rgb;
        if (f3) {
            if (flag != f1 && flag1 == f2)
                rgb = pixdraw_edge3p.getRgb(x, y, x1, y1, scaleX, scaleY);
            else if (flag == f1 && flag1 != f2) {
                rgb = pixdraw_edge3p1.getRgb(x, y, x1, y1, scaleX, scaleY);
            } else {
                int rgb1 = pixdraw_edge3p.getRgb(x, y, x1, y1, scaleX, scaleY);
                int rgb2 = pixdraw_edge3p1.getRgb(x, y, x1, y1, scaleX, scaleY);
                rgb = ScalingUtil.average(rgb1, rgb2);
            }
        } else if (flag != f1 && flag1 == f2)
            rgb = pixdraw_edge3p.getRgb(x, y, x1, y1, scaleX, scaleY);
        else if (flag == f1 && flag1 != f2) {
            rgb = pixdraw_edge3p1.getRgb(x, y, x1, y1, scaleX, scaleY);
        } else {
            int rgb1 = pixdraw_edge3p.getRgb(x, y, x1, y1, scaleX, scaleY);
            int rgb2 = pixdraw_edge3p1.getRgb(x, y, x1, y1, scaleX, scaleY);
            rgb = ScalingUtil.average(rgb1, rgb2);
        }
        return rgb;
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
        PixDraw_edge3P pixel11 = (PixDraw_edge3P) pixel1;
        PixDraw_edge3P pixel22 = (PixDraw_edge3P) pixel2;
        if (f3) {
            boolean flag11 = pixel11.a(x, y, x1, y1);
            boolean flag22 = pixel22.a(x, y, x1, y1);
            if (flag11 != f1 && flag22 == f2)
                pixel11.setCornerColor(x, y, direction, rgb);
            else if (flag11 == f1 && flag22 != f2) {
                pixel22.setCornerColor(x, y, direction, rgb);
            } else {
                pixel11.setCornerColor(x, y, direction, rgb);
                pixel22.setCornerColor(x, y, direction, rgb);
            }
        } else {
            boolean flag11 = pixel11.a(x, y, x1, y1);
            boolean flag22 = pixel22.a(x, y, x1, y1);
            if (flag11 != f1 && flag22 == f2)
                pixel11.setCornerColor(x, y, direction, rgb);
            else if (flag11 == f1 && flag22 != f2) {
                pixel22.setCornerColor(x, y, direction, rgb);
            } else {
                pixel11.setCornerColor(x, y, direction, rgb);
                pixel22.setCornerColor(x, y, direction, rgb);
            }
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
