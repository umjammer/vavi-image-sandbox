
package vavix.awt.image.resample.enlarge.noids.image.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;


/** c */
public class UtImage {

    BufferedImage image;
    int[] data;
    int width;
    int height;
    boolean checkBounds = true;

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
     * @throws
     */
    public int getA(int x, int y) throws Exception {
        if (checkBounds)
            checkBounds(x, y);
        return data[x + y * width] >> 24 & 0xff;
    }

    /**
     * @return red value
     * @throws
     */
    public int getR(int x, int y) throws Exception {
        if (checkBounds)
            checkBounds(x, y);
        return data[x + y * width] >> 16 & 0xff;
    }

    /**
     * @return green value
     * @throws
     */
    public int getG(int x, int y) throws Exception {
        if (checkBounds)
            checkBounds(x, y);
        return data[x + y * width] >> 8 & 0xff;
    }

    /**
     * @return blue value
     * @throws
     */
    public int getB(int x, int y) throws Exception {
        if (checkBounds)
            checkBounds(x, y);
        return data[x + y * width] & 0xff;
    }

    public int getARGB(int x, int y) throws Exception {
        if (checkBounds)
            checkBounds(x, y);
        return data[x + y * width];
    }

    public void setARGB(int x, int y, double a, double r, double g, double b) throws Exception {
        if (checkBounds)
            checkBounds(x, y);
        int argb = toARGB(a, r, g, b);
        data[x + y * width] = argb;
    }

    public void setARGB(int x, int y, int argb) throws Exception {
        if (checkBounds)
            checkBounds(x, y);
        data[x + y * width] = argb;
    }

    public void fillARGB(int x, int y, int width, int height, int argb) throws Exception {
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

    private void checkBounds(int x, int y) throws Exception {
        if (x < 0 || width <= x || y < 0 || height <= y) {
            System.out.println("画像外のサイズが指定されています : ");
            System.out.println("    画像サイズ ( " + width + " , " + height + " )");
            System.out.println("    指定画素   ( " + x + " , " + y + " )");
            throw new Exception();
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
