
package jp.noids.math;



public abstract class FMath {

    private static final double INVERSE_PI = 0.31830988618379069d;

    static final double[] cosins;
    static final double[] atans;

    public static void main(String[] args) {
        long l = System.currentTimeMillis();
        System.out.println("aa " + getAngle(1.0d, 1.0d));
        System.out.println("aa " + getAngle(-1d, 1.0d));
        System.out.println("aa " + getAngle(1.0d, -1d));
        System.out.println("aa " + getAngle(-1d, -1d));
        double q = 0.0d;
        int m = 5000000; // 0x4c4b40
        double d1 = -0.33414340999999997d;
        double d2 = -0.91080432d;
        double d = 0.0d;
        for (int i = 0; i < m; i++) {
            d1 += i;
            d2 += i;
            q += getAngle(d1, d2);
        }

        System.out.println("sin : "+ (System.currentTimeMillis() - l) * 0.001d + " sec");
        System.out.println("q " + q);
        System.out.println("total d " + d);
    }

    public static double getAngle(double x, double y) {
        if (y == 0.0d)
            return x >= 0.0d ? Math.PI / 2 : -Math.PI / 2;
        double rate = x / y;
        double angle = getAngle(rate);
        if (y > 0.0d)
            return angle;
        if (x > 0.0d)
            return Math.PI + angle;
        else
            return -Math.PI + angle;
    }

    public static double getAngle(double rate) {
        boolean flag = false;
        double rate1 = rate;
        if (rate1 < 0.0d) {
            rate1 = -rate1;
            flag = true;
        }
        boolean flag1 = false;
        if (rate1 > 1.0d) {
            rate1 = 1.0d / rate1;
            flag1 = true;
        }
        double rate2 = rate1 * 999d;
        int rate2_ = (int) rate2;
        double angle;
        if (rate2 != rate2_ && rate2_ < 999) {
            double decimal = rate2 - rate2_;
            angle = (atans[rate2_ + 1] - atans[rate2_]) * decimal + atans[rate2_];
        } else {
            if (rate2_ >= 1000)
                rate2_ = 1000;
            angle = atans[rate2_];
        }
        if (flag1)
            angle = Math.PI / 2 - angle;
        if (flag)
            angle = -angle;
        return angle;
    }

    public static double sin(double angle) {
        double angle_ = angle - Math.PI / 2;
        boolean flag = true;
        if (angle_ < 0.0d)
            angle_ = -angle_;
        if (angle_ > Math.PI * 2) {
            long a1 = (long) (180d * angle_ * INVERSE_PI);
            a1 /= 360l;
            angle_ -= a1 * Math.PI * 2;
            if (angle_ < 0.0d)
                angle_ = 0.0d;
        }
        angle_ /= Math.PI;
        angle_ -= (int) (angle_ / 2D) * 2;
        if (angle_ > 1.0d)
            angle_ = 2d - angle_;
        if (angle_ > 0.5d) {
            angle_ = 1.0d - angle_;
            flag = false;
        }
        double a2 = angle_ * 2d * 1000d;
        int a2_ = (int) a2;
        if (a2_ != a2 && a2_ < 999) {
            double decimal = a2 - a2_;
            double cos = (cosins[a2_ + 1] - cosins[a2_]) * decimal + cosins[a2_];
            return flag ? cos : -cos;
        }
        if (a2_ >= 1000)
            a2_ = 999;
        return flag ? cosins[a2_] : -cosins[a2_];
    }

    public static double cos(double angle) {
        double angle_ = angle;
        boolean flag = true;
        if (angle_ < 0.0d)
            angle_ = -angle_;
        if (angle_ > Math.PI * 2) {
            long a1 = (long) (180d * angle_ * INVERSE_PI);
            a1 /= 360l;
            angle_ -= a1 * Math.PI * 2;
            if (angle_ < 0.0d)
                angle_ = 0.0d;
        }
        angle_ /= Math.PI;
        angle_ -= (int) (angle_ / 2d) * 2;
        if (angle_ > 1.0d)
            angle_ = 2d - angle_;
        if (angle_ > 0.5d) {
            angle_ = 1.0d - angle_;
            flag = false;
        }
        double a2 = angle_ * 2d * 1000d;
        int a2_ = (int) a2;
        if (a2_ != a2 && a2_ < 999) {
            double decimal = a2 - a2_;
            double cos = (cosins[a2_ + 1] - cosins[a2_]) * decimal + cosins[a2_];
            return flag ? cos : -cos;
        }
        if (a2_ >= 1000)
            a2_ = 999;
        return flag ? cosins[a2_] : -cosins[a2_];
    }

    static {
        cosins = new double[1000];
        double d = 0.0015707963267948967d;
        for (int i = 0; i < 1000; i++)
            cosins[i] = Math.cos(i * d);

        atans = new double[1000];
        double d1 = 0.001001001001001001d;
        for (int i = 0; i < 1000; i++)
            atans[i] = Math.atan(i * d1);
    }
}
