/*
 * http://www.codeproject.com/KB/GDI-plus/queuelinearfloodfill.aspx
 *
 * Original algorithm by J. Dunlap
 */

package vavix.awt.image.filling;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;


/**
 * @author Owen Kaluza
 */
public class QueueLinearFloodFiller {

    // color tolerance R,G,B
    private int[] tolerance = new int[] {
        0, 0, 0
    };
    // cached image properties
    private int width = 0;
    private int height = 0;
    // internal, initialized per fill
    private boolean[] pixelsChecked;
    private int fillColor = 0;
    private int[] startColor;
    private int[] pixels;

    //Queue of floodfill ranges
    protected Queue<FloodFillRange> ranges;
    private int imageType;

    public QueueLinearFloodFiller(BufferedImage image) {
        this.width = image.getWidth();
        this.height = image.getHeight();

        ranges = new LinkedList<>();
        pixelsChecked = new boolean[width * height];

        pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        imageType = image.getType();
    }

    public int getFillColor() {
        return fillColor;
    }

    public void setTargetColor(Color value) {
        if (startColor == null) {
            startColor = new int[3];
        }
        startColor[0] = value.getRed();
        startColor[1] = value.getGreen();
        startColor[2] = value.getBlue();
    }

    public void setFillColor(Color value) {
        fillColor = value.getRGB();
    }

    public void setFillColor(int value) {
        fillColor = value;
    }

    public int[] getTolerance() {
        return tolerance;
    }

    public void setTolerance(int[] value) {
        tolerance = value;
    }

    public void setTolerance(int value) {
        tolerance = new int[] {
            value, value, value
        };
    }

    public BufferedImage getImage() {
        BufferedImage image = new BufferedImage(width, height, imageType == 0 ? BufferedImage.TYPE_INT_RGB : imageType);
        image.setRGB(0, 0, width, height, pixels, 0, width);
        return image;
    }

    /**
     * Fills the specified point on the bitmap with the currently selected fill color.
     *
     * @param x The starting coords for the fill
     * @param y The starting coords for the fill
     */
    public void floodFill(int x, int y) {

        // Get starting color.
        if (startColor == null) {
            int startPixel = pixels[(width * y) + x];
            setTargetColor(new Color(startPixel));
        }

        // Do first call to floodfill.
        linearFill(x, y);

        // Call floodfill routine while floodfill ranges still exist on the queue
        while (ranges.size() > 0) {
            // Get Next Range Off the Queue
            FloodFillRange range = ranges.remove();

            // Check Above and Below Each Pixel in the Floodfill Range
            int downPixelIndex = (width * (range.y + 1)) + range.startX;
            int upPixelIndex   = (width * (range.y - 1)) + range.startX;
            int upY   = range.y - 1; // so we can pass the y coord by ref
            int downY = range.y + 1;
            for (int i = range.startX; i <= range.endX; i++) {
                // Start Fill Upwards
                // if we're not above the top of the bitmap and the pixel above this one is within the color tolerance
                if (range.y > 0 && !pixelsChecked[upPixelIndex] && checkPixel(upPixelIndex))
                    linearFill(i, upY);

                // Start Fill Downwards
                // if we're not below the bottom of the bitmap and the pixel below this one is within the color tolerance
                if (range.y < (height - 1) && !pixelsChecked[downPixelIndex] && checkPixel(downPixelIndex))
                    linearFill(i, downY);

                downPixelIndex++;
                upPixelIndex++;
            }
        }
    }

    /**
     * Finds the furthermost left and right boundaries of the fill area
     * on a given y coordinate, starting from a given x coordinate, filling as it goes.
     * Adds the resulting horizontal range to the queue of floodfill ranges,
     * to be processed in the main loop.
     *
     * @param x The starting coords
     * @param y The starting coords
     */
    protected void linearFill(int x, int y) {
        // Find Left Edge of Color Area
        int lFillLoc = x; // the location to check/fill on the left
        int pixelIndex = (width * y) + x;
        while (true) {
            // fill with the color
            pixels[pixelIndex] = fillColor;
            // indicate that this pixel has already been checked and filled
            pixelsChecked[pixelIndex] = true;
            // de-increment
            lFillLoc--; // de-increment counter
            pixelIndex--; // de-increment pixel index
            // exit loop if we're at edge of bitmap or color area
            if (lFillLoc < 0 || pixelsChecked[pixelIndex] || !checkPixel(pixelIndex)) {
                break;
            }
        }
        lFillLoc++;

        // Find Right Edge of Color Area
        int rFillLoc = x; // the location to check/fill on the left
        pixelIndex = (width * y) + x;
        while (true) {
            // fill with the color
            pixels[pixelIndex] = fillColor;
            // indicate that this pixel has already been checked and filled
            pixelsChecked[pixelIndex] = true;
            // increment
            rFillLoc++; // increment counter
            pixelIndex++; // increment pixel index
            // exit loop if we're at edge of bitmap or color area
            if (rFillLoc >= width || pixelsChecked[pixelIndex] || !checkPixel(pixelIndex)) {
                break;
            }
        }
        rFillLoc--;

        // add range to queue
        FloodFillRange r = new FloodFillRange(lFillLoc, rFillLoc, y);
        ranges.offer(r);
    }

    /** Sees if a pixel is within the color tolerance range. */
    protected boolean checkPixel(int pixelIndex) {
        int red   = (pixels[pixelIndex] >>> 16) & 0xff;
        int green = (pixels[pixelIndex] >>> 8) & 0xff;
        int blue  =  pixels[pixelIndex] & 0xff;

        return (red   >= (startColor[0] - tolerance[0]) && red   <= (startColor[0] + tolerance[0]) &&
                green >= (startColor[1] - tolerance[1]) && green <= (startColor[1] + tolerance[1]) &&
                blue  >= (startColor[2] - tolerance[2]) && blue  <= (startColor[2] + tolerance[2]));
    }

    /** Represents a linear range to be filled and branched from. */
    protected static class FloodFillRange {
        public int startX;
        public int endX;
        public int y;
        public FloodFillRange(int startX, int endX, int y) {
            this.startX = startX;
            this.endX = endX;
            this.y = y;
        }
    }
}
