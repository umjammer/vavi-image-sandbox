
package jp.noids.image.scaling.pixDraw;

import java.io.Serializable;

import jp.noids.image.scaling.DirectionConstants;


/** d */
public interface Pixel extends Serializable, DirectionConstants {

    int getRgb(int x, int y, double x1, double y1, double scaleX, double scaleY);

    void setCornerColor(int x, int y, int direction, int rgb);

    boolean isValid();
}
