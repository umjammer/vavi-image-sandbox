
package jp.noids.image.scaling.view;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;


/** b */
public interface Scaler {

    void scale(Rectangle.Double dstRect, BufferedImage image, Rectangle srcRect) throws Exception;
}
