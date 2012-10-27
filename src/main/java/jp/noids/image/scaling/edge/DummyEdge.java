
package jp.noids.image.scaling.edge;

import java.awt.Point;


/** b */
public class DummyEdge extends jp.noids.image.scaling.edge.AbstractEdge implements Edge {

    public DummyEdge() {
    }

    public double get_color1() {
        throw new RuntimeException("この境界はダミーです");
    }

    public double get_color2() {
        throw new RuntimeException("この境界はダミーです");
    }

    public int getEndColor() {
        throw new RuntimeException("この境界はダミーです");
    }

    public int getStartColor() {
        throw new RuntimeException("この境界はダミーです");
    }

    public Point.Double get_point1() {
        throw new RuntimeException("この境界はダミーです");
    }

    public Point get_point2() {
        throw new RuntimeException("この境界はダミーです");
    }

    public void moveBit(double d1, double d2) {
        throw new RuntimeException("この境界はダミーです");
    }

    public boolean isHorizontal() {
        throw new RuntimeException("この境界はダミーです");
    }

    public int getXColor(int direction) {
        throw new RuntimeException("この境界はダミーです");
    }

    public int getYColor(int direction) {
        throw new RuntimeException("この境界はダミーです");
    }

    public Point getStartPoint(int direction) {
        throw new RuntimeException("この境界はダミーです");
    }

    public Point getEndPoint(int direction) {
        throw new RuntimeException("この境界はダミーです");
    }

    public int length() {
        throw new RuntimeException("この境界はダミーです");
    }

    public int get_color_a(boolean flag, boolean flag1) {
        throw new RuntimeException("この境界はダミーです");
    }

    public void set_color_a(boolean flag, boolean flag1, int j) {
        throw new RuntimeException("この境界はダミーです");
    }

    public Point get_point_b(boolean flag, boolean flag1) {
        throw new RuntimeException("この境界はダミーです");
    }
}
