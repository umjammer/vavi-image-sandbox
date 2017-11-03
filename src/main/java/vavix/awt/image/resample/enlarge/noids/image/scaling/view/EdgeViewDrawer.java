
package vavix.awt.image.resample.enlarge.noids.image.scaling.view;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import vavix.awt.image.resample.enlarge.noids.image.scaling.pixDraw.Pixel;


public class EdgeViewDrawer implements Scaler {

    DataBufferPixel pixel;

    public EdgeViewDrawer(DataBufferPixel pixel) {
        this.pixel = pixel;
    }

    /**
     * detects thread interruption.
     */
    public void scale(Rectangle.Double rect1, BufferedImage image, Rectangle rect2) throws Exception {
        Rectangle rect1_ = toRectangle(rect1);
        double scaleX = rect1.width / rect2.width;
        double scaleY = rect1.height / rect2.height;
        int x1 = rect2.x;
        int y1 = rect2.y;
        int x2 = rect2.x + rect2.width;
        int y2 = rect2.y + rect2.height;
        int y2_ = rect1_.y + rect1_.height;
        for (int y = rect1_.y; y < y2_; y++) {
            for (int x = rect1_.x; x < rect1_.x + rect1_.width; x++) {
                Pixel p = pixel.getPixel(x, y);
                int x11 = (int) Math.ceil((rect2.x + ((x - rect1.x) * rect2.width) / rect1.width) - 0.5d);
                int x12 = (int) Math.ceil((rect2.x + (((x + 1.0d) - rect1.x) * rect2.width) / rect1.width) - 0.5d);
                int y11 = (int) Math.ceil((rect2.y + ((y - rect1.y) * rect2.height) / rect1.height) - 0.5d);
                int y12 = (int) Math.ceil((rect2.y + (((y + 1.0d) - rect1.y) * rect2.height) / rect1.height) - 0.5d);
                if (x11 < x1)
                    x11 = x1;
                if (x12 > x2)
                    x12 = x2;
                if (y11 < y1)
                    y11 = y1;
                if (y12 > y2)
                    y12 = y2;
                for (int y0 = y11; y0 < y12; y0++) {
                    if (Thread.interrupted())
                        return;
                    for (int x0 = x11; x0 < x12; x0++) {
                        double x4 = rect1.x + ((x0 - rect2.x) * rect1.width) / rect2.width;
                        double y4 = rect1.y + ((y0 - rect2.y) * rect1.height) / rect2.height;
                        int rgb = p.getRgb(x, y, x4, y4, scaleX, scaleY);
                        image.setRGB(x0, y0, rgb);
                    }
                }
            }
        }
    }

    private static Rectangle toRectangle(Rectangle.Double rect) {
        int x = (int) rect.x;
        int y = (int) rect.y;
        int w = (int) (Math.ceil(rect.x + rect.width) - x);
        int h = (int) (Math.ceil(rect.y + rect.height) - y);
        return new Rectangle(x, y, w, h);
    }
}
