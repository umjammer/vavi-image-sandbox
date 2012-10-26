/*
 * Copyright (c) 2009 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.resample;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.util.Arrays;


/**
 * 全然キレくないやんけ！
 * 
 * @see "http://www7a.biglobe.ne.jp/~fairytale/article/program/graphics.html#lanczos"
 */
public class HexeReinLanczosResampleOp implements BufferedImageOp {

    /** N値 */
    private int n;
    /** */
    private double scale;

    /** */
    public HexeReinLanczosResampleOp(double scale) {
        this(2, scale);
    }

    /** */
    public HexeReinLanczosResampleOp(int n, double scale) {
        this.n = n;
        this.scale = scale;
    }

    /* */
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        if (dst == null) {
            dst = createCompatibleDestImage(src, src.getColorModel());
        }

        int nx = n - 1;

        for (int y = 0; y < dst.getHeight(); y++) {
            for (int x = 0; x < dst.getWidth(); x++) {
                double x0 = x / scale;
                double y0 = y / scale;

                int xBase = (int) x0;
                int yBase = (int) y0;

                int color = 0;

                // ランツォシュの処理範囲
                if (xBase >= nx && xBase < src.getWidth() - n && yBase >= nx && yBase < src.getHeight() - n) {
                    double color_element[] = new double[3];
                    Arrays.fill(color_element, 0.0);

                    double w_total = 0.0;

                    // 周辺(a*2)^2画素を取得して処理
                    for (int i = -nx; i <= n; i++) {
                        for (int j = -nx; j <= n; j++) {
                            int xCurrent = xBase + i;
                            int yCurrent = yBase + j;

                            // 距離決定
                            double distX = Math.abs(xCurrent - x0);
                            double distY = Math.abs(yCurrent - y0);

                            // 重み付け
                            double weight = 0.0;

                            if (distX == 0.0) {
                                weight = 1.0;
                            } else if (distX < n) {
                                double dPIx = Math.PI * distX;
                                weight = (Math.sin(dPIx) * Math.sin(dPIx / n)) / (dPIx * (dPIx / n));
                            } else {
                                continue;
                            }

                            if (distY == 0.0) {
                                ;
                            } else if (distY < n) {
                                double dPIy = Math.PI * distY;
                                weight *= (Math.sin(dPIy) * Math.sin(dPIy / n)) / (dPIy * (dPIy / n));
                            } else {
                                continue;
                            }

                            // 画素取得
                            int color_process = src.getRGB(xCurrent, yCurrent);

                            for (int k = 0; k < 3; k++) {
                                color_element[k] += ((color_process >> k * 8) & 0xff) * weight;
                            }

                            w_total += weight;
                        }
                    }

                    for (int i = 0; i < 3; i++) {
                        if (w_total != 0) {
                            color_element[i] /= w_total;
                        }
                        color_element[i] = (color_element[i] > 255) ? 255 : (color_element[i] < 0) ? 0 : color_element[i];
                        color += (int) color_element[i] << i * 8;
                    }
                }

                dst.setRGB(x, y, color);
            }
        }

        return dst;
    }

    /* */
    public Rectangle2D getBounds2D(BufferedImage src) {
        return src.getRaster().getBounds();
    }

    /* */
    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM) {

        if (destCM == null) {
            destCM = src.getColorModel();
        }

        int width = (int) (src.getWidth() * scale);
        int height = (int) (src.getHeight() * scale);

        return new BufferedImage(destCM, destCM.createCompatibleWritableRaster(width, height), destCM.isAlphaPremultiplied(), null);
    }

    /* */
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Float();
        }
        dstPt.setLocation(srcPt.getX() * scale, srcPt.getY() * scale);
        return dstPt;
    }

    /* */
    public RenderingHints getRenderingHints() {
        return null;
    }
}

/* */

