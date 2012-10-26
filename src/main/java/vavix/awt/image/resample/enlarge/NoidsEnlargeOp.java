/*
 * Copyright (c) 2011 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.resample.enlarge;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;

import jp.noids.image.filter.clean.CleanFilter;
import jp.noids.image.scaling.Action_connectEdges;
import jp.noids.image.scaling.Action_createEdgeData4;
import jp.noids.image.scaling.Action_createView;
import jp.noids.image.scaling.Action_makeCorner;
import jp.noids.image.scaling.Action_smoothing;
import jp.noids.image.scaling.EdgeData;
import jp.noids.image.scaling.view.DataBufferPixel;
import jp.noids.image.scaling.view.Scaler;
import jp.noids.image.scaling.view.ScalerFactory;


/**
 * NoidsEnlargeOp. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2011/09/06 umjammer initial version <br>
 */
public class NoidsEnlargeOp implements BufferedImageOp {

    /** */
    private double sx;
    /** */
    private double sy;

    /** */
    @SuppressWarnings("unused")
    private int hint;

    private int margin = 1;

    int destMargin;

    /**
     * hint is {@link Hint#FAST_BILINEAR}
     * @param sx x scaling
     * @param sy y scaling
     */
    public NoidsEnlargeOp(double sx, double sy) {
        this(sx, sy, 0);
    }

    /**
     * @param sx x scaling
     * @param sy y scaling
     */
    public NoidsEnlargeOp(double sx, double sy, int hint) {
        this.sx = sx;
        this.sy = sy;
        this.hint = hint;
        destMargin = (int) Math.ceil(margin * Math.max(sx, sy));
    }

    /* */
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {

        if (src.getType() != BufferedImage.TYPE_INT_ARGB) {
            throw new IllegalArgumentException("Enlarge is performed on only TYPE_INT_ARGB");
        }

        if (margin != 0) {
            BufferedImage newImage = new BufferedImage(src.getWidth() + margin * 2, src.getHeight() + margin * 2, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = newImage.createGraphics();
            g2.setColor(new Color(0xff, 0xff, 0xff, 0xff));
            g2.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
            g2.drawImage(src, margin, margin, null);
            g2.dispose();
            src = newImage;
        }

        if (dest == null) {
            dest = createCompatibleDestImage(src, src.getColorModel());
        }

        try {
            //
            BufferedImage filteredImage = new CleanFilter().filter(src, null);
            EdgeData edgeData = Action_createEdgeData4.createEdge(filteredImage, margin);
            Action_connectEdges.connectEdges(edgeData);
            edgeData.setupLines();
            Action_makeCorner.makeCorner(edgeData);
            Action_smoothing.smooth(edgeData);
            Action_createView.createView(edgeData);

            //
            int dw = dest.getWidth();
            int dh = dest.getHeight();
            int w = (int) (src.getWidth() * sx + 0.5d);
            int h = (int) (src.getHeight() * sy + 0.5d);
            int tx = destMargin * 2 + w < dw ? (dw - w) / 2 : destMargin;
            int ty = destMargin * 2 + h < dh ? (dh - h) / 2 : destMargin;
            AffineTransform nTx = new AffineTransform();
            nTx.translate(tx, ty);
            nTx.scale(sx, sy);
            nTx.translate(0, 0);
            AffineTransform iTx = nTx.createInverse();

            //
            Point.Double p1 = new Point.Double();
            Point.Double p2 = new Point.Double();
            Rectangle.Double rect1 = new Rectangle.Double(0.0d, 0.0d, src.getWidth(), src.getHeight());
            nTx.transform(new Point.Double(rect1.x, rect1.y), p1);
            nTx.transform(new Point.Double(rect1.width, rect1.height), p2);
            Rectangle.Double rect2 = new Rectangle.Double(0, 0, src.getWidth() * sx, src.getHeight() * sy);
            Rectangle.Double rect3 = new Rectangle.Double(p1.x, p1.y, p2.x - p1.x, p2.y - p1.y);
            Rectangle.Double rect4 = new Rectangle.Double();
            rect4.x = Math.max(rect2.x, rect3.x);
            rect4.y = Math.max(rect2.y, rect3.y);
            double w2 = Math.min(rect2.x + rect2.width, rect3.x + rect3.width);
            double h2 = Math.min(rect2.y + rect2.height, rect3.y + rect3.height);
            rect4.width = w2 - rect4.x;
            rect4.height = h2 - rect4.y;
            Point.Double p3 = (Point.Double) iTx.transform(new Point.Double(rect4.x, rect4.y), new Point.Double());
            Point.Double p4 = (Point.Double) iTx.transform(new Point.Double(rect4.x + rect4.width, rect4.y + rect4.height), new Point.Double());
            Rectangle.Double rect5 = new Rectangle.Double(p3.getX() + margin, p3.getY() + margin, p4.getX() - p3.getX(), p4.getY() - p3.getY());
            Rectangle rect6 = new Rectangle((int) (rect4.x - rect2.x), (int) (rect4.y - rect2.y), (int) rect4.width, (int) rect4.height);

            DataBufferPixel pixel = edgeData.getDataBufferPixel();
            Scaler scaler = ScalerFactory.createScaler(pixel);
            scaler.scale(rect5, dest, rect6);
            
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        if (margin != 0) {
            int dw = dest.getWidth() - destMargin;
            int dh = dest.getHeight() - destMargin;
            BufferedImage newImage = new BufferedImage(dw, dh, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = newImage.createGraphics();
            g2.drawImage(dest, 0, 0, dw, dh, destMargin, destMargin, dw, dh, null);
            g2.dispose();
            dest.flush();
            src.flush();
            dest = newImage;
        }

        return dest;
    }    

    /**
     * @param destCM when null, used src color model 
     */
    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM) {
        Rectangle2D destBounds = getBounds2D(src);
        if (destCM != null) {
            return new BufferedImage(destCM, destCM.createCompatibleWritableRaster((int) destBounds.getWidth(), (int) destBounds.getHeight()), destCM.isAlphaPremultiplied(), null);
        } else {
            return new BufferedImage((int) destBounds.getWidth(), (int) destBounds.getHeight(), src.getType());
        }
    }

    /* */
    public Rectangle2D getBounds2D(BufferedImage src) {
        return new Rectangle(0, 0, (int) (src.getWidth() * sx), (int) (src.getHeight() * sy));
    }

    /* */
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Double();
        }
        dstPt.setLocation(srcPt.getX() * sx, srcPt.getY() * sy);
        return dstPt;
    }

    /* TODO implement */
    public RenderingHints getRenderingHints() {
        return null;
    }
}

/* */
