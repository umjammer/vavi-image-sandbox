
package jp.noids.image.scaling.view;


/** c */
public class ScalerFactory {

    public static Scaler createScaler(DataBufferPixel pixel) {
        return new EdgeViewDrawer(pixel);
    }
}
