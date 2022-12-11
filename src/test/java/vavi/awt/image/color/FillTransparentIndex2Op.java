/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.awt.image.color;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * FillTransparentIndex2Op. (use raster operation)
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 060616 nsano initial version <br>
 */
public class FillTransparentIndex2Op implements BufferedImageOp {

    private static final int leastColor = 10;

    private BufferedImage targetImage;

    /**
     * @param targetImage should be indexed color model
     * @throws IllegalArgumentException src is not indexed color model image
     */
    public FillTransparentIndex2Op(BufferedImage targetImage) {
        if (!(targetImage.getColorModel() instanceof IndexColorModel)) {
            throw new IllegalArgumentException("not indexed color model image");
        }
        this.targetImage = targetImage;
    }

    /**
     * <ol>
     * <li>透明色が設定されていればそれを使用する</li>
     * <li>使用されていないパレットがあればそのパレットを透明色にする</li>
     * <li>パレットのピクセル数が #leastColor 以下の場合透明色になってもらう</li>
     * <li>パレットのピクセル数がいちばん多い色で塗りつぶす</li>
     * </ol>
     * 
     * @param src just use for size
     * @param dst currently, should not be set
     */
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        if (dst != null) { // TODO implement
            throw new IllegalArgumentException("not implemented yet");
        }

        int w = src.getWidth();
        int h = src.getHeight();
        IndexColorModel icm = (IndexColorModel) targetImage.getColorModel();
        int trans = icm.getTransparentPixel();
        if (trans != -1) {
            // 1. 透明色が設定されていればそれを使用する
            dst = createCompatibleDestImage(src, icm);
        } else {
            int mapSize = icm.getMapSize();
            byte[] rs = new byte[mapSize];
            byte[] gs = new byte[mapSize];
            byte[] bs = new byte[mapSize];
            icm.getReds(rs);
            icm.getGreens(gs);
            icm.getBlues(bs);

            // index, count
            Map<Integer, Integer> colorMap = new HashMap<>();
            int tw = targetImage.getWidth();
            int th = targetImage.getHeight();
            int[] data = targetImage.getData().getPixels(0, 0, tw, th, (int[]) null);
            for (int y = 0; y < th; y++) {
                for (int x = 0; x < tw; x++) {
                    int index = data[y * tw + x];
                    if (!colorMap.containsKey(index)) {
                        colorMap.put(index, 1);
                    } else {
                        colorMap.put(index, colorMap.get(index) + 1);
                    }
                }
            }

            //
            int unusedColor = -1;
            if (colorMap.size() < icm.getMapSize()) {
                for (int i = 0; i < icm.getMapSize(); i++) {
                    if (!colorMap.containsKey(i)) {
                        unusedColor = i;
                        break;
                    }
                }
            }

            if (unusedColor != -1) {
                // 2. 使用されていないパレットがあれば
                //    そのパレットを透明色にする
                trans = unusedColor;
                IndexColorModel newIcm = new IndexColorModel(icm.getPixelSize(), mapSize, rs, gs, bs, trans);
                dst = createCompatibleDestImage(src, newIcm);
            } else {
                List<Entry<Integer, Integer>> list = new ArrayList<>(colorMap.entrySet());
                list.sort((o1, o2) -> o2.getValue() - o1.getValue());

                if (list.get(list.size() - 1).getValue() < leastColor) {
                    // 3. パレットのピクセル数が #leastColor 以下の場合
                    //    透明色になってもらう
                    trans = list.get(list.size() - 1).getKey();
                    IndexColorModel newIcm = new IndexColorModel(icm.getPixelSize(), mapSize, rs, gs, bs, trans);
                    dst = createCompatibleDestImage(src, newIcm);
                } else {
                    // 4. パレットのピクセル数がいちばん多い色で塗りつぶす
                    trans = list.get(0).getKey();
                    dst = createCompatibleDestImage(src, icm);
                }
            }
        }

        int[] pixels = new int[w * h];
        Arrays.fill(pixels, trans); // TODO why trans is index???
        dst.setRGB(0, 0, w, h, pixels, 0, w);

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

/* */
