
package jp.noids.image.scaling.edge;

import java.awt.Point;

import jp.noids.graphics.color.HSL;
import jp.noids.util.UtMath;
import jp.noids.util.UtToString;


/** f */
public class EdgeY extends AbstractEdge implements Edge {

    public int startY;
    public int endY;
    public int x;
    public float y;
    public static int count = 0;
    float x1 = -999999F;
    float y1 = -999999F;

    public EdgeY(int x, int startY, int endY, int color1, int color2, double[] ad) {
        this.x = x;
        this.startY = startY;
        this.endY = endY;
        this.startColor = color1;
        this.endColor = color2;
        init(ad);
        count++;
    }

    private void init(double[] ad) {
        int l = endY - startY - 1;
        if (l == 0)
            y = endY;
        else if (l == 1) {
            double d1 = 1.0d - ad[0] / ad[1];
            y = (float) ((startY + 1) + d1);
        } else if (2 <= l) {
            double[] ad1 = new double[l];
            double[] ad2 = new double[l];
            for (int i = 0; i < l; i++) {
                ad1[i] = (startY + i) + 1.5d;
                ad2[i] = ad[i];
            }

            double[] ad3 = UtMath.c(ad1, ad2, (double[]) null);
            double d2 = ad[l] / 2d;
            y = (float) ((d2 - ad3[1]) / ad3[0]);
            if (y < (startY + 1))
                y = (float) ((startY + 1) + 0.0001d);
            if (y > endY)
                y = (float) (endY - 0.0001d);
        }
    }

    public boolean contains(int x, int y) {
        return this.x == x && startY <= y && y <= endY;
    }

    public Point getStartPoint() {
        return new Point(x, startY);
    }

    public Point getEndPoint() {
        return new Point(x, endY);
    }

    public double get_color1() {
        return HSL.get_value_c(startColor, endColor) / ((endY - startY) + 1);
    }

    public double get_color2() {
        return HSL.get_value_c(startColor, endColor);
    }

    public int length() {
        return (endY - startY) + 1;
    }

    public double getY() {
        return y;
    }

    public int getEndColor() {
        return endColor;
    }

    public int getEndY() {
        return endY;
    }

    public int getStartColor() {
        return startColor;
    }

    public int getStartY() {
        return startY;
    }

    public Point.Double get_point1() {
        if (x1 < -1000f)
            return new Point.Double(x + 0.5d, y);
        else
            return new Point.Double(x1, y1);
    }

    public Point get_point2() {
        if (x1 < -1000f)
            return new Point((int) (x + 0.5d), (int) y);
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
        return false;
    }

    public int getXColor(int direction) {
        switch (direction) {
        case 2:
            return startColor;
        case 0:
            return endColor;
        case 1:
            return startColor;
        case 3:
            return endColor;
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
            return endColor;
        case 3:
            return startColor;
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
            return getStartPoint();
        case 3:
            return getEndPoint();
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
            return getEndPoint();
        case 3:
            return getStartPoint();
        }
        throw new RuntimeException("未実装");
    }

    public int get_color_a(boolean flag, boolean flag1) {
        if (flag)
            return flag1 ? startColor : endColor;
        else
            return flag1 ? endColor : startColor;
    }

    public Point get_point_b(boolean flag, boolean flag1) {
        if (flag)
            return flag1 ? getStartPoint() : getEndPoint();
        else
            return flag1 ? getEndPoint() : getStartPoint();
    }

    public void set_color_a(boolean flag, boolean flag1, int color) {
        if (flag) {
            if (flag1)
                startColor = color;
            else
                endColor = color;
        } else if (flag1)
            endColor = color;
        else
            startColor = color;
    }

    public String toString() {
        return "EdgeY " + UtToString.toString(get_point1())
                + " ["
                + Integer.toHexString(hashCode())
                + "]";
    }
}
