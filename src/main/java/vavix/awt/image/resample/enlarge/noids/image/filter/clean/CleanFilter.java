
package vavix.awt.image.resample.enlarge.noids.image.filter.clean;

import java.awt.Point;
import java.awt.image.BufferedImage;

import vavix.awt.image.resample.enlarge.noids.graphics.UtBufferedImage;
import vavix.awt.image.resample.enlarge.noids.graphics.color.HSL;
import vavix.awt.image.resample.enlarge.noids.image.util.UtImage;
import vavix.awt.image.resample.enlarge.noids.ui.toneCurve.ToneLine;


public class CleanFilter {

    static Point.Double[] points = {
        new Point.Double(0.0d, 1.0d),
        new Point.Double(0.2d, 0.2d),
        new Point.Double(1.0d, 0.0d)
    };

    static ToneLine toneLine = new ToneLine(0.0d, 1.0d, 0.0d, 1.0d, points);

    /**
     *
     * @return null when some errors occur.
     */
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        try {
            if (src.getType() != BufferedImage.TYPE_INT_ARGB)
                throw new RuntimeException("INT_ARGB以外の型については未実装です");
            if (dst == null)
                dst = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
            else if (dst.getType() != BufferedImage.TYPE_INT_ARGB)
                throw new RuntimeException("フィルタの出力用画像のタイプが適切ではありません");
            int w = src.getWidth();
            int h = src.getHeight();
            UtBufferedImage.copy(src, dst);
            UtImage util1 = new UtImage(src);
            UtImage util2 = new UtImage(dst);
            double[] colors = new double[9];
            double min = 0.2d;
            for (int y = 1; y < h - 1; y++) {
                for (int x = 1; x < w - 1; x++) {
                    int argb = util1.getARGB(x, y);
                    double t = 0.0d;
                    int c = 0;
                    for (int dy = 0; dy < 3; dy++) {
                        for (int dx = 0; dx < 3; dx++) {
                            int x1 = (x + dx) - 1;
                            int y1 = (y + dy) - 1;
                            double v = dx != 1 || dy != 1 ? HSL.get_value_c(argb, util1.getARGB(x1, y1)) : 0.0d;
                            if (v > min)
                                c++;
                            colors[dx + dy * 3] = v;
                        }
                    }

                    double a = 0.0d;
                    double r = 0.0d;
                    double g = 0.0d;
                    double b = 0.0d;
                    if (c < 3) {
                        for (int dy = 0; dy < 3; dy++) {
                            for (int dx = 0; dx < 3; dx++) {
                                int x1 = (x + dx) - 1;
                                int y1 = (y + dy) - 1;
                                double tone = toneLine.getTone(colors[dx + dy * 3]);
                                a += util1.getA(x1, y1) * tone;
                                r += util1.getR(x1, y1) * tone;
                                g += util1.getG(x1, y1) * tone;
                                b += util1.getB(x1, y1) * tone;
                                t += tone;
                            }
                        }

                        a /= t;
                        r /= t;
                        g /= t;
                        b /= t;
                        util2.setARGB(x, y, a, r, g, b);
                    } else {
                        util2.setARGB(x, y, argb);
                    }
                }
            }

            return dst;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }
}
