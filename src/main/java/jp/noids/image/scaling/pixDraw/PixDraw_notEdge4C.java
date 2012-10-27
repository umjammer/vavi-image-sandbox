
package jp.noids.image.scaling.pixDraw;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/** f */
public class PixDraw_notEdge4C implements Pixel {

    private static final long serialVersionUID = 1L;

    private static boolean debug = false;
    protected byte r0;
    protected byte g0;
    protected byte b0;
    protected byte r1;
    protected byte g1;
    protected byte b1;
    protected byte r2;
    protected byte g2;
    protected byte b2;
    protected byte r3;
    protected byte g3;
    protected byte b3;

    public PixDraw_notEdge4C(int rgb1, int rgb2, int rgb3, int rgb4) {
        r0 = (byte) (rgb1 >>> 16 & 0xff);
        g0 = (byte) (rgb1 >>> 8 & 0xff);
        b0 = (byte) (rgb1 & 0xff);
        r1 = (byte) (rgb2 >>> 16 & 0xff);
        g1 = (byte) (rgb2 >>> 8 & 0xff);
        b1 = (byte) (rgb2 & 0xff);
        r2 = (byte) (rgb3 >>> 16 & 0xff);
        g2 = (byte) (rgb3 >>> 8 & 0xff);
        b2 = (byte) (rgb3 & 0xff);
        r3 = (byte) (rgb4 >>> 16 & 0xff);
        g3 = (byte) (rgb4 >>> 8 & 0xff);
        b3 = (byte) (rgb4 & 0xff);
    }

    public int getRgb(int x, int y, double x1, double y1, double sx, double sy) {
        double dx = x1 - x;
        double dy = y1 - y;
        int r = (int) ((1.0D - dx) * (1.0D - dy) * (r0 & 0xff) + dx * (1.0D - dy) * (r1 & 0xff) + (1.0D - dx)
                       * dy * (r2 & 0xff) + dx * dy * (r3 & 0xff));
        int g = (int) ((1.0D - dx) * (1.0D - dy) * (g0 & 0xff) + dx * (1.0D - dy) * (g1 & 0xff)
                        + (1.0D - dx) * dy * (g2 & 0xff) + dx * dy * (g3 & 0xff));
        int b = (int) ((1.0D - dx) * (1.0D - dy) * (b0 & 0xff) + dx * (1.0D - dy) * (b1 & 0xff)
                        + (1.0D - dx) * dy * (b2 & 0xff) + dx * dy * (b3 & 0xff));
        if (r < 0)
            r = 0;
        else if (r > 255)
            r = 255;
        if (g < 0)
            g = 0;
        else if (g > 255)
            g = 255;
        if (b < 0)
            b = 0;
        else if (b > 255)
            b = 255;
        return 0xff000000 | r << 16 | g << 8 | b;
    }

    public boolean isValid() {
        return true;
    }

    public void setCornerColor(int r, int g, int direction, int rgb) {
        switch (direction) {
        case 0:
            r0 = (byte) (rgb >>> 16 & 0xff);
            g0 = (byte) (rgb >>> 8 & 0xff);
            b0 = (byte) (rgb & 0xff);
            break;
        case 1:
            r1 = (byte) (rgb >>> 16 & 0xff);
            g1 = (byte) (rgb >>> 8 & 0xff);
            b1 = (byte) (rgb & 0xff);
            break;
        case 2:
            r2 = (byte) (rgb >>> 16 & 0xff);
            g2 = (byte) (rgb >>> 8 & 0xff);
            b2 = (byte) (rgb & 0xff);
            break;
        case 3:
            r3 = (byte) (rgb >>> 16 & 0xff);
            g3 = (byte) (rgb >>> 8 & 0xff);
            b3 = (byte) (rgb & 0xff);
            break;
        default:
            throw new RuntimeException("おかしな状態");
        }
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeByte(r0);
        oos.writeByte(g0);
        oos.writeByte(b0);
        oos.writeByte(r1);
        oos.writeByte(g1);
        oos.writeByte(b1);
        oos.writeByte(r2);
        oos.writeByte(g2);
        oos.writeByte(b2);
        oos.writeByte(r3);
        oos.writeByte(g3);
        oos.writeByte(b3);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        r0 = ois.readByte();
        g0 = ois.readByte();
        b0 = ois.readByte();
        r1 = ois.readByte();
        g1 = ois.readByte();
        b1 = ois.readByte();
        r2 = ois.readByte();
        g2 = ois.readByte();
        b2 = ois.readByte();
        r3 = ois.readByte();
        g3 = ois.readByte();
        b3 = ois.readByte();
    }

    static {
        if (debug)
            System.out.println(" < DEBUG : jp.noids.image.scaling.pixDraw.PixDraw_notEdge4C > ");
    }
}
