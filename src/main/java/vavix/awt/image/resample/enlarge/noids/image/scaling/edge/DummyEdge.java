
package vavix.awt.image.resample.enlarge.noids.image.scaling.edge;

import java.awt.Point;


/** b */
public class DummyEdge extends AbstractEdge implements Edge {

    private static final String Message = "This is a dummy edge";

    public double get_color1() {
        throw new UnsupportedOperationException(Message);
    }

    public double get_color2() {
        throw new UnsupportedOperationException(Message);
    }

    public int getEndColor() {
        throw new UnsupportedOperationException(Message);
    }

    public int getStartColor() {
        throw new UnsupportedOperationException(Message);
    }

    public Point.Double get_point1() {
        throw new UnsupportedOperationException(Message);
    }

    public Point get_point2() {
        throw new UnsupportedOperationException(Message);
    }

    public void moveBit(double d1, double d2) {
        throw new UnsupportedOperationException(Message);
    }

    public boolean isHorizontal() {
        throw new UnsupportedOperationException(Message);
    }

    public int getXColor(int direction) {
        throw new UnsupportedOperationException(Message);
    }

    public int getYColor(int direction) {
        throw new UnsupportedOperationException(Message);
    }

    public Point getStartPoint(int direction) {
        throw new UnsupportedOperationException(Message);
    }

    public Point getEndPoint(int direction) {
        throw new UnsupportedOperationException(Message);
    }

    public int length() {
        throw new UnsupportedOperationException(Message);
    }

    public int get_color_a(boolean asc, boolean flag1) {
        throw new UnsupportedOperationException(Message);
    }

    public void set_color_a(boolean asc, boolean flag1, int j) {
        throw new UnsupportedOperationException(Message);
    }

    public Point get_point_b(boolean asc, boolean flag1) {
        throw new UnsupportedOperationException(Message);
    }
}
