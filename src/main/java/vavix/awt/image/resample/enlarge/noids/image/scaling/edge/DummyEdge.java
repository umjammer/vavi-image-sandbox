
package vavix.awt.image.resample.enlarge.noids.image.scaling.edge;

import java.awt.Point;


/** b */
public class DummyEdge extends AbstractEdge implements Edge {

    private static final String Message = "この境界はダミーです";

    public double get_color1() {
        throw new RuntimeException(Message);
    }

    public double get_color2() {
        throw new RuntimeException(Message);
    }

    public int getEndColor() {
        throw new RuntimeException(Message);
    }

    public int getStartColor() {
        throw new RuntimeException(Message);
    }

    public Point.Double get_point1() {
        throw new RuntimeException(Message);
    }

    public Point get_point2() {
        throw new RuntimeException(Message);
    }

    public void moveBit(double d1, double d2) {
        throw new RuntimeException(Message);
    }

    public boolean isHorizontal() {
        throw new RuntimeException(Message);
    }

    public int getXColor(int direction) {
        throw new RuntimeException(Message);
    }

    public int getYColor(int direction) {
        throw new RuntimeException(Message);
    }

    public Point getStartPoint(int direction) {
        throw new RuntimeException(Message);
    }

    public Point getEndPoint(int direction) {
        throw new RuntimeException(Message);
    }

    public int length() {
        throw new RuntimeException(Message);
    }

    public int get_color_a(boolean flag, boolean flag1) {
        throw new RuntimeException(Message);
    }

    public void set_color_a(boolean flag, boolean flag1, int j) {
        throw new RuntimeException(Message);
    }

    public Point get_point_b(boolean flag, boolean flag1) {
        throw new RuntimeException(Message);
    }
}
