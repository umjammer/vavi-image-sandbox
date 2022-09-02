
package vavix.awt.image.resample.enlarge.noids.image.scaling.view;

import vavix.awt.image.resample.enlarge.noids.image.scaling.pixDraw.Pixel;


/** a */
public class DataBufferPixel {

    Pixel[][] pixels;

    public DataBufferPixel(Pixel[][] pixels) {
        this.pixels = pixels;
    }

    public void dispose() {
        pixels = null;
    }

    /**
     * @throws IllegalArgumentException x, y is not valid
     */
    public Pixel getPixel(int x, int y) {
        if (x < 0 || pixels[0].length <= x || y < 0 || pixels.length <= y)
            throw new IllegalArgumentException("wrong range (x,y) = " + x + " , " + y);
        else
            return pixels[y][x];
    }
}
