
package vavix.awt.image.resample.enlarge.noids.image.scaling.edge;

import java.awt.Point;

import vavix.awt.image.resample.enlarge.noids.image.scaling.Constants;
import vavix.awt.image.resample.enlarge.noids.image.scaling.DirectionConstants;


/** g */
public interface Edge_g extends Edge {

    int getStartColor();

    int getEndColor();

    Point getStartPoint(int direction);

    Point getEndPoint(int direction);

    int getXColor(int direction);

    int getYColor(int direction);

    boolean isHorizontal();

    double get_color1();

    double get_color2();

    int length();
}
