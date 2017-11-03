
package vavix.awt.image.resample.enlarge.noids.image.scaling.pixDraw;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/** h */
public class PixDraw_notEdgeFlat implements Pixel {

    private static final long serialVersionUID = 1L;

    private static boolean debug = false;

    protected int argb;

    public PixDraw_notEdgeFlat(int argb) {
        this.argb = argb;
    }

    public int getRgb(int x, int y, double x1, double y2, double sx, double sy) {
        return argb;
    }

    public boolean isValid() {
        return true;
    }

    public void setCornerColor(int x, int y, int direction, int rgb) {
        this.argb = rgb;
        System.err.println("あまり推奨されない処理。4点指定できない : \n     vavix.awt.image.resample.enlarge.noids.image.scaling.pixDraw.PixDraw_notEdgeFlat#setCornerColor( )");
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeInt(argb);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        argb = ois.readInt();
    }

    static {
        if (debug)
            System.out.println(" < DEBUG : vavix.awt.image.resample.enlarge.noids.image.scaling.pixDraw.PixDraw_notEdgeFlat > ");
    }
}
