
package jp.noids.util;


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

    public static double[] a(double[] rgb1, double[] rgb2, double[] ret) {
        return a(rgb1, rgb2, Double.MAX_VALUE, Double.MAX_VALUE, ret);
    }

    public static double[] a(double[] rgb1, double[] rgb2, double d1, double d2, double[] ret) {
        int l = rgb1.length;
        if (ret == null)
            ret = new double[rgb1.length];
        double[] ad3 = new double[l - 1];
        if (d1 == Double.MAX_VALUE) {
            ret[0] = ad3[0] = 0.0d;
        } else {
            ret[0] = -0.5d;
            ad3[0] = (3d / (rgb1[1] - rgb1[0])) * ((rgb2[1] - rgb2[0]) / (rgb1[1] - rgb1[0]) - d1);
        }
        for (int i = 2; i <= l - 1; i++) {
            double d5 = (rgb1[-1 + i] - rgb1[(-1 + i) - 1]) / (rgb1[-1 + i + 1] - rgb1[(-1 + i) - 1]);
            double d3 = d5 * ret[(-1 + i) - 1] + 2d;
            ret[-1 + i] = (d5 - 1.0D) / d3;
            ad3[-1 + i] = (rgb2[-1 + i + 1] - rgb2[-1 + i]) / (rgb1[-1 + i + 1] - rgb1[-1 + i])
                          - (rgb2[-1 + i] - rgb2[(-1 + i) - 1]) / (rgb1[-1 + i] - rgb1[(-1 + i) - 1]);
            ad3[-1 + i] = ((6d * ad3[-1 + i]) / (rgb1[-1 + i + 1] - rgb1[(-1 + i) - 1]) - d5 * ad3[(-1 + i) - 1]) / d3;
        }

        double d4;
        double d6;
        if (d2 == Double.MAX_VALUE) {
            d4 = d6 = 0.0d;
        } else {
            d4 = 0.5d;
            d6 = (3d / (rgb1[-1 + l] - rgb1[(-1 + l) - 1]))
                 * (d2 - (rgb2[-1 + l] - rgb2[(-1 + l) - 1]) / (rgb1[-1 + l] - rgb1[(-1 + l) - 1]));
        }
        ret[-1 + l] = (d6 - d4 * ad3[(-1 + l) - 1])
                      / (d2 - (rgb2[-1 + l] - rgb2[(-1 + l) - 1]) / (rgb1[-1 + l] - rgb1[(-1 + l) - 1]));
        for (int i1 = l - 1; i1 >= 1; i1--)
            ret[-1 + i1] = ret[-1 + i1] * ret[-1 + i1 + 1] + ad3[-1 + i1];

        return ret;
    }

    public static double a(double[] xa, double[] ad1, double[] ad2, double d1) {
        int k = xa.length;
        int l = 1;
        int i1;
        for (i1 = k; i1 - l > 1;) {
            int j1 = i1 + l >> 1;
            if (xa[-1 + j1] > d1)
                i1 = j1;
            else
                l = j1;
        }

        double d2 = xa[-1 + i1] - xa[-1 + l];
        if (d2 == 0.0d) {
            throw new RuntimeException("Bad xa value , xa[] must be different ");
        } else {
            double d4 = (xa[-1 + i1] - d1) / d2;
            double d3 = (d1 - xa[-1 + l]) / d2;
            return d4 * ad1[-1 + l] + d3 * ad1[-1 + i1]
                   + (((d4 * d4 * d4 - d4) * ad2[-1 + l] + (d3 * d3 * d3 - d3) * ad2[-1 + i1]) * (d2 * d2)) / 6D;
        }
    }

    public static double[] c(double[] v1, double[] v2, double[] ret) {
        if (ret == null)
            ret = new double[2];
        double sum1 = sum(v1);
        double sum2 = sum(v2);
        int l = v1.length;
        double d3 = (a(v1, v2) - (sum1 * sum2) / l) / (sumOfTheSquares(v1) - (sum1 * sum1) / l);
        double d4 = (sum2 - d3 * sum1) / l;
        ret[0] = d3;
        ret[1] = d4;
        return ret;
    }

    private static double a(double[] ad, double[] ad1) {
        double d1 = 0.0d;
        for (int i = 0; i < ad.length; i++)
            d1 += ad[i] * ad1[i];

        return d1;
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
