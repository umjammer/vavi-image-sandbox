
package vavix.awt.image.resample.enlarge.noids.math;


public abstract class UtAngle {

    public static double absDiff(double a, double b) {
        double diff = diff(a, b);
        return diff >= 0.0d ? diff : -diff;
    }

    public static double diff(double a, double b) {
        double diff;
        diff = a - b;
        while (diff < -Math.PI)
            diff += Math.PI * 2;
        while (diff > Math.PI)
            diff -= Math.PI * 2;
        return diff;
    }
}
