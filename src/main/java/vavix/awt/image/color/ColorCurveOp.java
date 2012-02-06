/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *  
 * Original
 *  http://www.gimp.org/
 *  http://avisynth.org.ru/docs/english/externalfilters/gicocu.htm
 */

package vavix.awt.image.color;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;


/**
 * ColorCurveOp. 
 *
 * @author E-Male 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2012/02/07 umjammer initial version <br>
 */
public class ColorCurveOp implements BufferedImageOp {

    private static final int ROUND(float x) {
        return (int) (x + 0.5f);
    }

    private static final int CLAMP(float x, float l, float u) {
        return (int) ((x < l) ? l : (x > u) ? u : x);
    }

    private static final int CLAMP0255(float a) {
        return CLAMP(a, 0, 255);
    }

    private class CRMatrix {
        public CRMatrix() {
            this.data = new float[4][4];
        }

        public CRMatrix(float[][] data) {
            this.data = data;
        }

        float[][] data;
    }

    private class Curves {
        int[][][] points = new int[5][17][2];

        int[][] curve = new int[5][256];
    }

    private void curves_CR_compose(CRMatrix a, CRMatrix b, CRMatrix ab) {
        int i, j;

        for (i = 0; i < 4; i++) {
            for (j = 0; j < 4; j++) {
                ab.data[i][j] = (a.data[i][0] * b.data[0][j] +
                                 a.data[i][1] * b.data[1][j] +
                                 a.data[i][2] * b.data[2][j] +
                                 a.data[i][3] * b.data[3][j]);
            }
        }
    }

    void hsv_curve_apply(Curves curves, int[] r, int[] g, int[] b) {
        int cmin, x, y, z, cdelta, chi, rd, gd, bd, ch;
        // RGB to HSV (x=H y=S z=V)
        cmin = Math.min(r[0], g[0]);
        cmin = Math.min(b[0], cmin);
        z = Math.max(r[0], g[0]);
        z = Math.max(b[0], z);
        cdelta = z - cmin;
        if (cdelta != 0) {
            y = (cdelta << 8) / z;
            if (y > 255)
                y = 255;
            if (r[0] == z) {
                x = ((g[0] - b[0]) << 8) / cdelta;
            } else if (g[0] == z) {
                x = 512 + (((b[0] - r[0]) << 8) / cdelta);
            } else {
                x = 1024 + (((r[0] - g[0]) << 8) / cdelta);
            }
            if (x < 0) {
                x = x + 1536;
            }
            x = x / 6;
        } else {
            y = 0;
            x = 0;
        }

        // Applying the curves
        x = curves.curve[1][x];
        y = curves.curve[2][y];
        z = curves.curve[3][z];

        // HSV to RGB
        if (y == 0) {
            r[0] = z;
            g[0] = z;
            b[0] = z;
        } else {
            chi = (x * 6) >> 8;
            ch = (x * 6 - (chi << 8));
            rd = (z * (256 - y)) >> 8;
            gd = (z * (256 - ((y * ch) >> 8))) >> 8;
            bd = (z * (256 - (y * (256 - ch) >> 8))) >> 8;
            if (chi == 0) {
                r[0] = z;
                g[0] = bd;
                b[0] = rd;
            } else if (chi == 1) {
                r[0] = gd;
                g[0] = z;
                b[0] = rd;
            } else if (chi == 2) {
                r[0] = rd;
                g[0] = z;
                b[0] = bd;
            } else if (chi == 3) {
                r[0] = rd;
                g[0] = gd;
                b[0] = z;
            } else if (chi == 4) {
                r[0] = bd;
                g[0] = rd;
                b[0] = z;
            } else {
                r[0] = z;
                g[0] = rd;
                b[0] = gd;
            }
        }
    }

    void curves_channel_reset(Curves curves, int channel) {
        int j;

        for (j = 0; j < 256; j++)
            curves.curve[channel][j] = j;

        for (j = 0; j < 17; j++) {
            curves.points[channel][j][0] = -1;
            curves.points[channel][j][1] = -1;
        }

        curves.points[channel][0][0] = 0;
        curves.points[channel][0][1] = 0;
        curves.points[channel][16][0] = 255;
        curves.points[channel][16][1] = 255;
    }

    void curves_init(Curves curves) {
        int channel;

        for (channel = 0; channel <= 4; channel++) {
            curves_channel_reset(curves, channel);
        }
    }

    private void curves_plot_curve(Curves curves, int channel, int p1, int p2, int p3, int p4) {
        CRMatrix geometry = new CRMatrix();
        CRMatrix tmp1 = new CRMatrix(), tmp2 = new CRMatrix();
        CRMatrix deltas = new CRMatrix();
        float x, dx, dx2, dx3;
        float y, dy, dy2, dy3;
        float d, d2, d3;
        int lastx, lasty;
        int newx, newy;
        int i;

        /* construct the geometry matrix from the segment */
        for (i = 0; i < 4; i++) {
            geometry.data[i][2] = 0;
            geometry.data[i][3] = 0;
        }

        for (i = 0; i < 2; i++) {
            geometry.data[0][i] = curves.points[channel][p1][i];
            geometry.data[1][i] = curves.points[channel][p2][i];
            geometry.data[2][i] = curves.points[channel][p3][i];
            geometry.data[3][i] = curves.points[channel][p4][i];
        }

        // subdivide the curve 1000 times
        // n can be adjusted to give a finer or coarser curve
        d = 1.0f / 1000;
        d2 = d * d;
        d3 = d * d * d;

        // construct a temporary matrix for determining the forward differencing
        // deltas
        tmp2.data[0][0] = 0;
        tmp2.data[0][1] = 0;
        tmp2.data[0][2] = 0;
        tmp2.data[0][3] = 1;
        tmp2.data[1][0] = d3;
        tmp2.data[1][1] = d2;
        tmp2.data[1][2] = d;
        tmp2.data[1][3] = 0;
        tmp2.data[2][0] = 6 * d3;
        tmp2.data[2][1] = 2 * d2;
        tmp2.data[2][2] = 0;
        tmp2.data[2][3] = 0;
        tmp2.data[3][0] = 6 * d3;
        tmp2.data[3][1] = 0;
        tmp2.data[3][2] = 0;
        tmp2.data[3][3] = 0;

        CRMatrix CR_basis = new CRMatrix(new float[][] {
            { -0.5f, 1.5f, -1.5f, 0.5f },
            { 1.0f, -2.5f, 2.0f, -0.5f },
            { -0.5f, 0.0f, 0.5f, 0.0f },
            { 0.0f, 1.0f, 0.0f, 0.0f }
        });

        // compose the basis and geometry matrices
        curves_CR_compose(CR_basis, geometry, tmp1);

        // compose the above results to get the deltas matrix
        curves_CR_compose(tmp2, tmp1, deltas);

        // extract the x deltas
        x = deltas.data[0][0];
        dx = deltas.data[1][0];
        dx2 = deltas.data[2][0];
        dx3 = deltas.data[3][0];

        // extract the y deltas
        y = deltas.data[0][1];
        dy = deltas.data[1][1];
        dy2 = deltas.data[2][1];
        dy3 = deltas.data[3][1];

        lastx = CLAMP(x, 0, 255);
        lasty = CLAMP(y, 0, 255);

        curves.curve[channel][lastx] = lasty;

        /* loop over the curve */
        for (i = 0; i < 1000; i++) {
            /* increment the x values */
            x += dx;
            dx += dx2;
            dx2 += dx3;

            /* increment the y values */
            y += dy;
            dy += dy2;
            dy2 += dy3;

            newx = CLAMP0255(ROUND(x));
            newy = CLAMP0255(ROUND(y));

            /* if this point is different than the last one...then draw it */
            if ((lastx != newx) || (lasty != newy))
                curves.curve[channel][newx] = newy;

            lastx = newx;
            lasty = newy;
        }
    }

    void curves_calculate_curve(Curves curves, int channel) {
        int i;
        int[] points = new int[17];
        int num_pts;
        int p1, p2, p3, p4;

        //g_return_if_fail (curves != NULL);

        //switch (curves.curve_type[channel])
        {
            //case GIMP_CURVE_FREE:
            //break;

            //case GIMP_CURVE_SMOOTH:
            num_pts = 0;
            for (i = 0; i < 17; i++)
                if (curves.points[channel][i][0] != -1)
                    points[num_pts++] = i;

            if (num_pts != 0) {
                for (i = 0; i < curves.points[channel][points[0]][0]; i++)
                    curves.curve[channel][i] = curves.points[channel][points[0]][1];
                for (i = curves.points[channel][points[num_pts - 1]][0]; i < 256; i++)
                    curves.curve[channel][i] = curves.points[channel][points[num_pts - 1]][1];
            }

            for (i = 0; i < num_pts - 1; i++) {
                p1 = (i == 0) ? points[i] : points[(i - 1)];
                p2 = points[i];
                p3 = points[(i + 1)];
                p4 = (i == (num_pts - 2)) ? points[(num_pts - 1)] : points[(i + 2)];

                curves_plot_curve(curves, channel, p1, p2, p3, p4);
            }

            for (i = 0; i < num_pts; i++) {
                int x, y;

                x = curves.points[channel][points[i]][0];
                y = curves.points[channel][points[i]][1];
                curves.curve[channel][x] = y;
            }

            //break;
        }
    }

    void load_curve_file(Curves curves, InputStream file) throws IOException {

        int i, j;
        int[][] index = new int[5][17];
        int[][] value = new int[5][17];

        Scanner scanner = new Scanner(file);

        String header = scanner.nextLine();

System.err.println(header);
        if (!"# GIMP Curves File".equals(header))
            throw new IOException("not gimp curves file");

        for (i = 0; i < 5; i++) {
            for (j = 0; j < 17; j++) {
                index[i][j] = scanner.nextInt();
                value[i][j] = scanner.nextInt();
System.err.printf("index: %d, value: %d\n", index[i][j], value[i][j]);
            }
        }

        for (i = 0; i < 5; i++) {
            for (j = 0; j < 17; j++) {
                curves.points[i][j][0] = index[i][j];
                curves.points[i][j][1] = value[i][j];
            }
        }

        // make LUTs
        for (i = 0; i < 5; i++)
            curves_calculate_curve(curves, i);
    }

    Curves curves = new Curves();

    // GiCoCu is RGB24 & RGB32 only!
    public ColorCurveOp(String curve, boolean _photoshop) throws IOException {
        FileInputStream file = new FileInputStream(new File(curve));

        if (!_photoshop)
            load_curve_file(curves, file);
        else
            for (int a = 0; a < 5; a++)
                for (int b = 0; b < 256; b++)
                    curves.curve[a][b] = file.read();

        file.close();
    }

    /**
     * 
     * @param src 
     * @param dst 
     */
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        if (dst == null) {
            dst = createCompatibleDestImage(src, src.getColorModel());
        }

        int row_size = dst.getWidth();
        int height = dst.getHeight();

        int r, g, b;

//        if (!hsv)
            if (src.getType() == BufferedImage.TYPE_INT_RGB) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < row_size; x++) {
                        int rgb = src.getRGB(x, y);
                        r = curves.curve[0][curves.curve[1][(rgb & 0x00ff0000) >> 16]];
                        g = curves.curve[0][curves.curve[2][(rgb & 0x0000ff00) >> 8]];
                        b = curves.curve[0][curves.curve[3][rgb & 0x000000ff]];
                        dst.setRGB(x, y, r << 16 | g << 8 | b);
                    }
                }
            } else if (src.getType() == BufferedImage.TYPE_INT_ARGB
                    || src.getType() == BufferedImage.TYPE_BYTE_GRAY) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < row_size; x++) {
                        int rgb = src.getRGB(x, y);
                        int a = curves.curve[4][(rgb & 0xff000000) >>> 24];
                        r = curves.curve[0][curves.curve[1][(rgb & 0x00ff0000) >> 16]];
                        g = curves.curve[0][curves.curve[2][(rgb & 0x0000ff00) >> 8]];
                        b = curves.curve[0][curves.curve[3][(rgb & 0x000000ff)]];
                        dst.setRGB(x, y, a << 24 | r << 16 | g << 8 | b);
                    }
                }
            } else
                throw new IllegalArgumentException("unsupported image type: " + src.getType());
//                for (int y = 0; y < height; y++) {
//                    for (int x = 0; x < row_size; x += 4) {
//                        dstp[x] = curves.curve[0][curves.curve[3][srcp[x]]];
//                        dstp[x + 1] = curves.curve[0][curves.curve[2][srcp[x + 1]]];
//                        dstp[x + 2] = curves.curve[0][curves.curve[1][srcp[x + 2]]];
//                        dstp[x + 3] = srcp[x + 3];
//                    }
//                    srcp += src_pitch;
//                    dstp += dst_pitch;
//                }
//        else if (vi.IsRGB24())
//            for (int y = 0; y < height; y++) {
//                for (int x = 0; x < row_size; x += 3) {
//                    b = srcp[x];
//                    g = srcp[x + 1];
//                    r = srcp[x + 2];
//                    hsv_curve_apply(curves, r, g, b);
//                    dstp[x] = b;
//                    dstp[x + 1] = g;
//                    dstp[x + 2] = r;
//                }
//                srcp += src_pitch;
//                dstp += dst_pitch;
//            }
//        else
//            for (int y = 0; y < height; y++) {
//                for (int x = 0; x < row_size; x += 4) {
//                    b = srcp[x];
//                    g = srcp[x + 1];
//                    r = srcp[x + 2];
//                    hsv_curve_apply(curves, r, g, b);
//                    dstp[x] = b;
//                    dstp[x + 1] = g;
//                    dstp[x + 2] = r;
//                    dstp[x + 3] = srcp[x + 3];
//                }
//                srcp += src_pitch;
//                dstp += dst_pitch;
//            }

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