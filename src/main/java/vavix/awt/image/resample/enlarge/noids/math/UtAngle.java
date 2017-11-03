
package vavix.awt.image.resample.enlarge.noids.math;


public abstract class UtAngle {

    public static double absDiff(double a1, double a2) {
        double diff = diff(a1, a2);
        return diff >= 0.0d ? diff : -diff;
    }

    public static double diff(double a1, double a2) {
        double diff;
        diff = a1 - a2;
        while (diff < -Math.PI)
            diff += Math.PI * 2;
        while (diff > Math.PI)
            diff -= Math.PI * 2;
        return diff;
    }
}
