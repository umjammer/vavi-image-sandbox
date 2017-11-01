
package jp.noids.ui.toneCurve;

import java.awt.Point;
import java.awt.geom.Point2D;


/** h */
public interface Tone {

    double getTone(double color);

    int size();

    double get_value2();

    double getMax();

    double get_value1();

    double getMin();

    int indexOf(Point2D.Double point);

    void remove(int index);

    Point.Double get(int index);

    int add(Point2D.Double point);

    void update();

    double getX(int index);

    double getY(int index);
}
