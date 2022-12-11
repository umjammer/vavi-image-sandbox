/*

 */

package vavix.awt.image.color;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;


/**
 */
public class UnSetTransparentIndexOp implements BufferedImageOp {

    /**
     * TODO hints
     */
    public UnSetTransparentIndexOp() {

    }

    /**
     * @param src should be indexed color model
     * @param dst currently, should not be set
     * @throws IllegalArgumentException src is not indexed color model image
     * @throws IllegalArgumentException src has not transparent pixels specified at constructor
     * @throws IllegalArgumentException TODO dst is not null
     */
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        if (!(src.getColorModel() instanceof IndexColorModel)) {
            throw new IllegalArgumentException("not indexed color model image");
        }

        if (dst != null) { // TODO implement
            throw new IllegalArgumentException("not implemented yet");
        }

        IndexColorModel srcICM = (IndexColorModel) src.getColorModel();
        WritableRaster raster = src.getRaster();
        int size = srcICM.getMapSize();
        byte[] reds = new byte[size];
        byte[] greens = new byte[size];
        byte[] blues = new byte[size];
        srcICM.getReds(reds);
        srcICM.getGreens(greens);
        srcICM.getBlues(blues);
        IndexColorModel newIcm = new IndexColorModel(srcICM.getPixelSize(), size, reds, greens, blues, -1);
        return new BufferedImage(newIcm, raster, src.isAlphaPremultiplied(), null);
    }

    /**
     * @param destCM when null, used src color model 
     */
    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM) {
        Rectangle destBounds = (Rectangle) getBounds2D(src);
        if (destCM != null) {
            return new BufferedImage(destCM, destCM.createCompatibleWritableRaster(destBounds.width, destBounds.height), destCM.isAlphaPremultiplied(), null);
        } else {
            return new BufferedImage(destBounds.width, destBounds.height, src.getType());
        }
    }

    /** */
    public Rectangle2D getBounds2D(BufferedImage src) {
        return new Rectangle(0, 0, src.getWidth(), src.getHeight());
    }

    /** */
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Double();
        }
        dstPt.setLocation(srcPt.getX(), srcPt.getY());
        return dstPt;
    }

    /** TODO impl */
    public RenderingHints getRenderingHints() {
        return null;
    }
}
