
package vavix.awt.image.resample.enlarge.noids.util;


public abstract class UtMath {

    public static double e = 9.9999999999999995E-08d;
    public static float b = 0.0001f;
    /** [radian] */
    public static double DEGREE = 0.017453292519943295d;
    /** [degree] */
    public static double RADIAN = 57.295779513082323d;

    public static int log10(int value) {
        int log = 1;
        value = Math.abs(value);
        while (true) {
            value /= 10;
            if (value != 0)
                log++;
            else
                return log;
        }
    }

    public static double[] mathod_a(double[] rgb1, double[] rgb2, double[] ret) {
        return method_a(rgb1, rgb2, Double.MAX_VALUE, Double.MAX_VALUE, ret);
    }

    public static double[] method_a(double[] rgb1, double[] rgb2, double d1, double d2, double[] ret) {
        int l = rgb1.length;
        if (ret == null)
            ret = new double[rgb1.length];
        double[] vs = new double[l - 1];
        if (d1 == Double.MAX_VALUE) {
            ret[0] = vs[0] = 0.0d;
        } else {
            ret[0] = -0.5d;
            vs[0] = (3d / (rgb1[1] - rgb1[0])) * ((rgb2[1] - rgb2[0]) / (rgb1[1] - rgb1[0]) - d1);
        }
        for (int i = 2; i <= l - 1; i++) {
            double v1 = (rgb1[-1 + i] - rgb1[(-1 + i) - 1]) / (rgb1[-1 + i + 1] - rgb1[(-1 + i) - 1]);
            double v2 = v1 * ret[(-1 + i) - 1] + 2d;
            ret[-1 + i] = (v1 - 1.0d) / v2;
            vs[-1 + i] = (rgb2[-1 + i + 1] - rgb2[-1 + i]) / (rgb1[-1 + i + 1] - rgb1[-1 + i])
                          - (rgb2[-1 + i] - rgb2[(-1 + i) - 1]) / (rgb1[-1 + i] - rgb1[(-1 + i) - 1]);
            vs[-1 + i] = ((6d * vs[-1 + i]) / (rgb1[-1 + i + 1] - rgb1[(-1 + i) - 1]) - v1 * vs[(-1 + i) - 1]) / v2;
        }

        double v3;
        double v4;
        if (d2 == Double.MAX_VALUE) {
            v3 = v4 = 0.0d;
        } else {
            v3 = 0.5d;
            v4 = (3d / (rgb1[-1 + l] - rgb1[(-1 + l) - 1]))
                 * (d2 - (rgb2[-1 + l] - rgb2[(-1 + l) - 1]) / (rgb1[-1 + l] - rgb1[(-1 + l) - 1]));
        }
        ret[-1 + l] = (v4 - v3 * vs[(-1 + l) - 1])
                      / (d2 - (rgb2[-1 + l] - rgb2[(-1 + l) - 1]) / (rgb1[-1 + l] - rgb1[(-1 + l) - 1]));
        for (int i = l - 1; i >= 1; i--)
            ret[-1 + i] = ret[-1 + i] * ret[-1 + i + 1] + vs[-1 + i];

        return ret;
    }

    public static double method_a(double[] xa, double[] ad1, double[] ad2, double d1) {
        int l = xa.length;
        int s = 1;
        int i;
        for (i = l; i - s > 1;) {
            int j = i + s >> 1;
            if (xa[-1 + j] > d1)
                i = j;
            else
                s = j;
        }

        double x = xa[-1 + i] - xa[-1 + s];
        if (x == 0.0d) {
            throw new RuntimeException("Bad xa value , xa[] must be different ");
        } else {
            double x1 = (xa[-1 + i] - d1) / x;
            double x2 = (d1 - xa[-1 + s]) / x;
            return x1 * ad1[-1 + s] + x2 * ad1[-1 + i]
                   + (((x1 * x1 * x1 - x1) * ad2[-1 + s] + (x2 * x2 * x2 - x2) * ad2[-1 + i]) * (x * x)) / 6D;
        }
    }

    public static double[] method_c(double[] v1, double[] v2, double[] ret) {
        if (ret == null)
            ret = new double[2];
        double sum1 = sum(v1);
        double sum2 = sum(v2);
        int l = v1.length;
        double r1 = (dot(v1, v2) - (sum1 * sum2) / l) / (sumOfTheSquares(v1) - (sum1 * sum1) / l);
        double r2 = (sum2 - r1 * sum1) / l;
        ret[0] = r1;
        ret[1] = r2;
        return ret;
    }

    private static double dot(double[] a, double[] b) {
        double r = 0.0d;
        for (int i = 0; i < a.length; i++)
            r += a[i] * b[i];

        return r;
    }

    private static double sum(double[] values) {
        double sum = 0.0d;
        for (int i = 0; i < values.length; i++)
            sum += values[i];

        return sum;
    }

    private static double sumOfTheSquares(double[] values) {
        double sum = 0.0d;
        for (int i = 0; i < values.length; i++)
            sum += values[i] * values[i];

        return sum;
    }

    public static byte limit8bit(double v) {
        if (v < 0.0d)
            v = 0.0d;
        else if (v > 1.0d)
            v = 1.0d;
        return (byte) (int) (v * 255d);
    }

    public static int pow(int v, int p) {
        if (p == 0)
            return 1;
        if (v == 0)
            return 0;
        int r = v;
        for (int i = 1; i < p; i++)
            r *= v;

        return r;
    }

    public static boolean isInteger(double v) {
        return (int) v == v;
    }

    public static void main(String[] args) {
        System.out.println("" + isInteger(-0d));
    }
}
