
package vavix.awt.image.resample.enlarge.noids.image.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;


/** c */
public class UtImage {

    private BufferedImage image;
    private int[] data;
    private int width;
    private int height;
    private boolean checkBounds;

    public UtImage(BufferedImage image) {
        this(image, false);
    }

    public UtImage(BufferedImage image, boolean checkBounds) {
        this.image = image;
        this.data = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.checkBounds = checkBounds;
    }

    public void dispose() {
        image = null;
        data = null;
    }

    /**
     * @return alpha value
     */
    public int getA(int x, int y) {
        if (checkBounds)
            checkBounds(x, y);
        return data[x + y * width] >> 24 & 0xff;
    }

    /**
     * @return red value
     */
    public int getR(int x, int y) {
        if (checkBounds)
            checkBounds(x, y);
        return data[x + y * width] >> 16 & 0xff;
    }

    /**
     * @return green value
     */
    public int getG(int x, int y) {
        if (checkBounds)
            checkBounds(x, y);
        return data[x + y * width] >> 8 & 0xff;
    }

    /**
     * @return blue value
     */
    public int getB(int x, int y) {
        if (checkBounds)
            checkBounds(x, y);
        return data[x + y * width] & 0xff;
    }

    public int getARGB(int x, int y) {
        if (checkBounds)
            checkBounds(x, y);
        return data[x + y * width];
    }

    public void setARGB(int x, int y, double a, double r, double g, double b) {
        if (checkBounds)
            checkBounds(x, y);
        int argb = toARGB(a, r, g, b);
        data[x + y * width] = argb;
    }

    public void setARGB(int x, int y, int argb) {
        if (checkBounds)
            checkBounds(x, y);
        data[x + y * width] = argb;
    }

    public void fillARGB(int x, int y, int width, int height, int argb) {
        if (checkBounds) {
            checkBounds(x, y);
            checkBounds(x + width, y + height);
        }
        for (int y1 = 0; y1 < height; y1++) {
            int y0 = y + y1;
            for (int x1 = 0; x1 < width; x1++) {
                int x0 = x + x1;
                data[x0 + y0 * this.width] = argb;
            }
        }
    }

    private void checkBounds(int x, int y) throws IllegalArgumentException {
        if (x < 0 || width <= x || y < 0 || height <= y) {
            System.err.println("size is out ob bounds: ");
            System.err.println("    image size     ( " + width + " , " + height + " )");
            System.err.println("    specified size ( " + x + " , " + y + " )");
            throw new IllegalArgumentException(x + " , " + y);
        }
    }

    public static int toARGB(double a, double r, double g, double b) {
        int r0 = (int) (r + 0.5d);
        if (r0 < 0)
            r0 = 0;
        if (r0 > 255)
            r0 = 255;
        int g0 = (int) (g + 0.5d);
        if (g0 < 0)
            g0 = 0;
        if (g0 > 255)
            g0 = 255;
        int b0 = (int) (b + 0.5d);
        if (b0 < 0)
            b0 = 0;
        if (b0 > 255)
            b0 = 255;
        int a0 = (int) (a + 0.5d);
        if (a0 < 0)
            a0 = 0;
        if (a0 > 255)
            a0 = 255;
        return a0 << 24 | r0 << 16 | g0 << 8 | b0;
    }

    public int getHeight() {
        return image.getHeight();
    }

    public int getWidth() {
        return image.getWidth();
    }
}
