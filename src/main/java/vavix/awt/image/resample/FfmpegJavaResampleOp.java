/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.resample;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.IndexColorModel;

//import net.sf.ffmpeg_java.SWScaleLibrary;


/**
 * FfmpegJavaResampleOp.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 090624 nsano initial version <br>
 */
public class FfmpegJavaResampleOp implements BufferedImageOp {

    /** */
    private double sx;
    /** */
    private double sy;

    /** */
    private int hint;

    /**
     * hint is {@link Hint#FAST_BILINEAR}
     * @param sx x scaling
     * @param sy y scaling
     */
    public FfmpegJavaResampleOp(double sx, double sy) {
//        this(sx, sy, SWScaleLibrary.SWS_AREA);
    }

    /**
     * @param sx x scaling
     * @param sy y scaling
     */
    public FfmpegJavaResampleOp(double sx, double sy, int hint) {
        this.sx = sx;
        this.sy = sy;
        this.hint = hint;
    }

    static Ffmpeg swScale = Ffmpeg.getInstance();

    /* */
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {

        if (IndexColorModel.class.isInstance(src.getColorModel())) {
            throw new IllegalArgumentException("Resampling cannot be performed on an indexed image");
        }

        if (dest == null) {
            dest = createCompatibleDestImage(src, src.getColorModel());
        }

//System.err.println("src CM: " + src.getColorModel());
//System.err.println("dest CM: " + dest.getColorModel());
        int srcPixelSize = src.getColorModel().getPixelSize();
        int destPixelSize = dest.getColorModel().getPixelSize();

        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        int resizedWidth = dest.getWidth();
        int resizedHeight = dest.getHeight();

        int srcDataType = src.getRaster().getDataBuffer().getDataType();
        int destDataType = dest.getRaster().getDataBuffer().getDataType();

        Object srcBuffer;
        if (srcDataType == DataBuffer.TYPE_BYTE) {
            srcBuffer = ((DataBufferByte) src.getRaster().getDataBuffer()).getData();
        } else {
            srcBuffer = ((DataBufferInt) src.getRaster().getDataBuffer()).getData();
            srcPixelSize = 32;
        }
        Object destBuffer;
        if (destDataType == DataBuffer.TYPE_BYTE) {
            destBuffer = ((DataBufferByte) dest.getRaster().getDataBuffer()).getData();
        } else {
            destBuffer = ((DataBufferInt) dest.getRaster().getDataBuffer()).getData();
            destPixelSize = 32;
        }

//        Pointer context = swScale.sws_getContext(srcWidth, srcHeight, resizedWidth, resizedHeight, null, null, null);
//        swScale.sws_scale(context, srcBuffer, srcDataType, srcPixelSize, srcWidth, srcHeight, destBuffer, destDataType, destPixelSize, resizedWidth, resizedHeight, hint.value);

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
