
package jp.noids.image.scaling.edge;

import java.awt.Point;

import jp.noids.image.scaling.Constants;
import jp.noids.image.scaling.DirectionConstants;
import jp.noids.image.scaling.line.Line;


/** a */
public interface Edge extends DirectionConstants, Constants {

    final Edge dummyEdge = new DummyEdge();

    Point.Double get_point1();

    Point get_point2();

    void moveBit(double x, double y);

    int get_color_a(boolean flag, boolean flag1);

    void set_color_a(boolean flag, boolean flag1, int argb);

    Point get_point_b(boolean flag, boolean flag1);

    void connect(boolean flag, Edge edge, int i);

    void disconnect(Edge edge);

    Edge nextEdge(boolean asc);

    int next_value(boolean flag);

    boolean isConnected(Edge edge);

    boolean contains(Edge edge);

    void setLine(Line line);

    Line getLine();

    boolean is_flag1();

    void set_flag1(boolean flag);
}
