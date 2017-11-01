
package jp.noids.image.scaling.edge;

import java.awt.Point;

import jp.noids.image.scaling.Constants;
import jp.noids.image.scaling.DirectionConstants;


/** g */
public interface Edge_g extends Edge, DirectionConstants, Constants {

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
