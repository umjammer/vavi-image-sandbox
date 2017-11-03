
package vavix.awt.image.resample.enlarge.noids.ui.toneCurve;


/** c */
public abstract class ToneBase implements Tone {

    double value1;
    double min;
    double value2;
    double max;

    public ToneBase(double v1, double v2, double min, double max) {
        this.value1 = v1;
        this.value2 = v2;
        this.min = min;
        this.max = max;
    }

    public ToneBase(ToneBase tone) {
        this.value1 = tone.value1;
        this.value2 = tone.value2;
        this.min = tone.min;
        this.max = tone.max;
    }

    public double get_value2() {
        return value2;
    }

    public double getMax() {
        return max;
    }

    public double get_value1() {
        return value1;
    }

    public double getMin() {
        return min;
    }
}
