/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.pixel;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;


/**
 * AwtCropOp.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 060616 nsano initial version <br>
 */
public class AwtCropOp implements BufferedImageOp {

    /** */
    private int sx;
    /** */
    private int sy;
    /** */
    private int sw;
    /** */
    private int sh;


    /**
     * TODO hints
     */
    public AwtCropOp(int sx, int sy, int sw, int sh) {
        this.sx = sx;
        this.sy = sy;
        this.sw = sw;
        this.sh = sh;
    }

    /**
     * @param dst when null, created by {@link #createCompatibleDestImage(BufferedImage, ColorModel)} 
     */
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        Image tmpImage = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(src.getSource(), new CropImageFilter(sx, sy, sw, sh)));
//JOptionPane.showMessageDialog(null, "tmpImage", "tmpImage", JOptionPane.PLAIN_MESSAGE, new ImageIcon(tmpImage));
        if (dst == null) {
            dst = createCompatibleDestImage(src, null);
        }
        Graphics g = dst.createGraphics();
        g.drawImage(tmpImage, 0, 0, null);
//JOptionPane.showMessageDialog(null, "tmpImage", "tmpImage", JOptionPane.PLAIN_MESSAGE, new ImageIcon(dst));
        
        return dst;
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
        return new Rectangle(sx, sy, sw, sh);
    }

    /** */
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Double();
        }
        dstPt.setLocation(srcPt.getX() - sx, srcPt.getY() - sy);
        return dstPt;
    }

    /** TODO impl */
    public RenderingHints getRenderingHints() {
        return null;
    }
}

/* */
