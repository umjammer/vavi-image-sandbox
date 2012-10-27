
package jp.noids.image.scaling.edge;

import java.awt.Point;

import jp.noids.image.scaling.line.Line;


/** c */
public abstract class Edge_c implements Edge {

    Edge edge;

    boolean flag1 = false;

    public boolean is_flag1() {
        return flag1;
    }

    public void set_flag1(boolean flag) {
        this.flag1 = flag;
    }

    public int get_color_a(boolean flag, boolean flag1) {
        return edge.get_color_a(flag, flag1);
    }

    public void set_color_a(boolean flag, boolean flag1, int i) {
        edge.set_color_a(flag, flag1, i);
    }

    public Edge nextEdge(boolean flag) {
        return edge.nextEdge(flag);
    }

    public int next_value(boolean flag) {
        return edge.next_value(flag);
    }

    public Line getLine() {
        return edge.getLine();
    }

    public Point.Double get_point1() {
        return edge.get_point1();
    }

    public Point get_point2() {
        return edge.get_point2();
    }

    public boolean isConnected(Edge edge) {
        return this.edge.isConnected(edge);
    }

    public boolean isClosed(Edge edge) {
        return this.edge.isClosed(edge);
    }

    public void b(Edge edge) {
        this.edge.b(edge);
    }

    public void a(boolean flag, Edge edge, int i) {
        this.edge.a(flag, edge, i);
    }

    public void setLine(Line line) {
        this.edge.setLine(line);
    }

    public void moveBit(double x, double y) {
        edge.moveBit(x, y);
    }

    public Point get_point_b(boolean flag, boolean flag1) {
        return edge.get_point_b(flag, flag1);
    }
}
