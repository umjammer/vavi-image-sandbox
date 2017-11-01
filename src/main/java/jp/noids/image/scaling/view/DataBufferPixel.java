
package jp.noids.image.scaling.view;

import jp.noids.image.scaling.pixDraw.Pixel;


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
     * @throws IllegalArgumentException
     */
    public Pixel getPixel(int x, int y) {
        if (x < 0 || pixels[0].length <= x || y < 0 || pixels.length <= y)
            throw new IllegalArgumentException("不正な範囲です (x,y) = " + x + " , " + y);
        else
            return pixels[y][x];
    }
}
