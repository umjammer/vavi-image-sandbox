/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * FillTransparentIndexOp.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 060616 nsano initial version <br>
 */
public class FillTransparentIndexOp implements BufferedImageOp {

    protected static final int leastColor = 10;

    protected BufferedImage targetImage;


    /**
     * @param targetImage should be indexed color model
     * @throws IllegalArgumentException targetImage is not indexed color model image
     */
    public FillTransparentIndexOp(BufferedImage targetImage) {
        if (!IndexColorModel.class.isInstance(targetImage.getColorModel())) {
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
        IndexColorModel icm = IndexColorModel.class.cast(targetImage.getColorModel());
        int trans = icm.getTransparentPixel();
        if (trans != -1) {
            // 1. 透明色が設定されていればそれを使用する
            dst = createCompatibleDestImage(src, icm);
//System.err.printf("trans defined: %d\n",  trans);
        } else {
            int mapSize = icm.getMapSize();
            byte[] rs = new byte[mapSize];
            byte[] gs = new byte[mapSize];
            byte[] bs = new byte[mapSize];
            icm.getReds(rs);
            icm.getGreens(gs);
            icm.getBlues(bs);

            // index, count
            Map<Integer, Integer> colorMap = new HashMap<Integer, Integer>();
            int tw = targetImage.getWidth();
            int th = targetImage.getHeight();
            int[] data = targetImage.getData().getPixels(0, 0, tw, th, (int[]) null);
            for (int y = 0; y < th; y++) {
                for (int x = 0; x < tw; x++) {
                    int index = data[y * tw + x];
                    if (!colorMap.containsKey(index)) {
//System.err.printf("used index: %d\n", index);
                        colorMap.put(index, 1);
                    } else {
                        colorMap.put(index, colorMap.get(index) + 1);
                    }
                }
            }
//System.err.println("used colors: " + colorMap.size());

            //
            int unusedColor = -1;
            if (colorMap.size() < mapSize) {
//System.err.println("unused colors: " + (mapSize - colorMap.size()));
                for (int i = 0; i < mapSize; i++) {
                    if (!colorMap.containsKey(i)) {
                        unusedColor = i;
//System.err.printf("unused color: %d\n", i);
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
		//System.err.printf("trans unused color: %d %08x, %d/%d\n", trans, getRgb(trans, rs, bs, gs), icm.getMapSize() - colorMap.size(), colorMap.size());


	    }
	    // 
	    else if (mapSize + 1 < (int) Math.pow(2, icm.getPixelSize())) {
                trans = mapSize;
                byte[] rs2 = new byte[mapSize + 1];
                byte[] gs2 = new byte[mapSize + 1];
                byte[] bs2 = new byte[mapSize + 1];
                System.arraycopy(rs, 0, rs2, 0, mapSize);
                System.arraycopy(gs, 0, gs2, 0, mapSize);
                System.arraycopy(bs, 0, bs2, 0, mapSize);
                rs2[trans] = (byte) 0xfe;
                gs2[trans] = (byte) 0xfe;
                bs2[trans] = (byte) 0xfe;
                IndexColorModel newIcm = new IndexColorModel(icm.getPixelSize(), mapSize + 1, rs2, gs2, bs2, trans);
                dst = createCompatibleDestImage(src, newIcm);
		//System.err.printf("trans add unused color: %d %08x, 1/%d\n", trans, getRgb(trans, rs2, bs2, gs2), (int) Math.pow(2, icm.getPixelSize()) - colorMap.size());
            } else {
                List<Entry<Integer, Integer>> list = new ArrayList<Entry<Integer, Integer>>(colorMap.entrySet());
                Collections.sort(list, new Comparator<Entry<Integer, Integer>>() {
                    @Override
                    public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
                        return o2.getValue() - o1.getValue();
                    }
                });
		//System.err.printf("most used color: %d: %08x count %d\n", list.get(0).getKey(), getRgb(list.get(0).getKey(), rs, gs, bs), list.get(0).getValue());
		//System.err.printf("last used color: %d: %08x count %d\n", list.get(list.size() - 1).getKey(), getRgb(list.get(list.size() - 1).getKey(), rs, gs, bs), list.get(list.size() - 1).getValue());

                if (list.get(list.size() - 1).getValue() < leastColor) {
                    // 3. パレットのピクセル数が #leastColor 以下の場合
                    //    透明色になってもらう
                    trans = list.get(list.size() - 1).getKey();
                    IndexColorModel newIcm = new IndexColorModel(icm.getPixelSize(), mapSize, rs, gs, bs, trans);
                    dst = createCompatibleDestImage(src, newIcm);
		    //System.err.printf("trans last used color: %d %08x\n",  trans, getRgb(trans, rs, bs, gs));
                } else {
                    // 4. パレットのピクセル数がいちばん多い色で塗りつぶす
                    trans = list.get(0).getKey();
                    dst = createCompatibleDestImage(src, icm);
		    //System.err.printf("fill most used color %d, %08x\n", trans, getRgb(trans, rs, bs, gs));
                }
            }
        }

        int[] pixels = dst.getRaster().getPixels(0, 0, w, h, (int[]) null);
        Arrays.fill(pixels, trans);
        dst.getRaster().setPixels(0, 0, w, h, pixels);

        return dst;
    }

    /** */
    protected int getRgb(int i, byte[] rs, byte[] gs, byte[] bs) {
        return ((rs[i] & 0xff) << 16) |
               ((gs[i] & 0xff) << 8) |
                (bs[i] & 0xff);
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
