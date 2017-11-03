
package vavix.awt.image.resample.enlarge.noids.image.scaling.edge;

import java.awt.Point;

import vavix.awt.image.resample.enlarge.noids.graphics.color.HSL;
import vavix.awt.image.resample.enlarge.noids.util.UtMath;
import vavix.awt.image.resample.enlarge.noids.util.UtToString;


/** e */
public class EdgeX extends AbstractEdge implements Edge {

    public int startX;
    public int endX;
    public int y;
    public float x;
    public static int count = 0;
    float x1 = -999999f;
    float y1 = -999999f;

    public EdgeX() {
    }

    public EdgeX(int sx, int ex, int y, int color1, int color2, double[] ad) {
        if (color1 == color2)
            System.out.println("faefaaaaaaaaaaaaaaaaaaaaaaa");
        this.startX = sx;
        this.endX = ex;
        this.y = y;
        this.startColor = color1;
        this.endColor = color2;
        init(ad);
        count++;
    }

    private void init(double[] ad) {
        int l = endX - startX - 1;
        if (l == 0)
            x = endX;
        else if (l == 1) {
            double s = 1.0d - ad[0] / ad[1];
            x = (float) ((startX + 1) + s);
        } else if (2 <= l) {
            double[] ad1 = new double[l];
            double[] ad2 = new double[l];
            for (int i = 0; i < l; i++) {
                ad1[i] = (startX + i) + 1.5d;
                ad2[i] = ad[i];
            }

            double[] ad3 = UtMath.method_c(ad1, ad2, (double[]) null);
            double d2 = ad[l] / 2d;
            x = (float) ((d2 - ad3[1]) / ad3[0]);
            if (x < (startX + 1))
                x = (float) (startX + 1.0d + 0.0001d);
            if (x > endX)
                x = (float) (endX - 0.0001d);
        }
    }

    public boolean contains(int x, int y) {
        return this.y == y && startX <= x && x <= endX;
    }

    public Point getStartPoint() {
        return new Point(startX, y);
    }

    public Point getEndPoint() {
        return new Point(endX, y);
    }

    public double get_color1() {
        return HSL.get_value_c(startColor, endColor) / ((endX - startX) + 1);
    }

    public double get_color2() {
        return HSL.get_value_c(startColor, endColor);
    }

    public int length() {
        return (endX - startX) + 1;
    }

    public double getX() {
        return x;
    }

    public int getEndColor() {
        return endColor;
    }

    public int getEndX() {
        return endX;
    }

    public int getStartColor() {
        return startColor;
    }

    public int getStartX() {
        return startX;
    }

    public Point.Double get_point1() {
        if (x1 < -1000F)
            return new Point.Double(x, y + 0.5d);
        else
            return new Point.Double(x1, y1);
    }

    public Point get_point2() {
        if (x1 < -1000F)
            return new Point((int) x, (int) (y + 0.5d));
        else
            return new Point((int) x1, (int) y1);
    }

    public void moveBit(double x, double y) {
        if (x < 0.0d || y < 0.0d)
            throw new RuntimeException("未実装");
        x1 = (float) x;
        y1 = (float) y;
        if (x1 - (int) x1 == 0.0f)
            x1 += 0.0001d;
        if (y1 - (int) y1 == 0.0f)
            y1 += 0.0001d;
    }

    public boolean isHorizontal() {
        return true;
    }

    public int get_color_a(boolean flag, boolean flag1) {
        if (flag)
            return flag1 ? endColor : startColor;
        else
            return flag1 ? startColor : endColor;
    }

    public void set_color_a(boolean flag, boolean flag1, int color) {
        if (flag) {
            if (flag1)
                endColor = color;
            else
                startColor = color;
        } else {
            if (flag1)
                startColor = color;
            else
                endColor = color;
        }
    }

    public Point get_point_b(boolean flag, boolean flag1) {
        if (flag)
            return flag1 ? getEndPoint() : getStartPoint();
        else
            return flag1 ? getStartPoint() : getEndPoint();
    }

    public int getXColor(int direction) {
        switch (direction) {
        case 2:
            return startColor;
        case 0:
            return endColor;
        case 1:
            return endColor;
        case 3:
            return startColor;
        }
        throw new RuntimeException("未実装");
    }

    public int getYColor(int direction) {
        switch (direction) {
        case 2:
            return endColor;
        case 0:
            return startColor;
        case 1:
            return startColor;
        case 3:
            return endColor;
        }
        throw new RuntimeException("未実装");
    }

    public Point getStartPoint(int direction) {
        switch (direction) {
        case 2:
            return getStartPoint();
        case 0:
            return getEndPoint();
        case 1:
            return getEndPoint();
        case 3:
            return getStartPoint();
        }
        throw new RuntimeException("未実装");
    }

    public Point getEndPoint(int direction) {
        switch (direction) {
        case 2:
            return getEndPoint();
        case 0:
            return getStartPoint();
        case 1:
            return getStartPoint();
        case 3:
            return getEndPoint();
        }
        throw new RuntimeException("未実装");
    }

    public String toString() {
        return "EdgeX " + UtToString.toString(get_point1())
                + " ["
                + Integer.toHexString(hashCode())
                + "]";
    }
}
