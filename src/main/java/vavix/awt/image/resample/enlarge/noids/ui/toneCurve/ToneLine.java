
package vavix.awt.image.resample.enlarge.noids.ui.toneCurve;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;


public class ToneLine extends ToneBase {

    private double[] xs;
    private double[] ys;
    final List<Point.Double> points;

    public ToneLine() {
        super(0.0d, 1.0d, 0.0d, 1.0d);
        points = new ArrayList<>();
        points.add(new Point.Double(0.0d, 0.0d));
        points.add(new Point.Double(1.0d, 1.0d));
        update();
    }

    public ToneLine(double x1, double y1, double x2, double y2, Point.Double[] points) {
        super(x1, y1, x2, y2);
        this.points = new ArrayList<>();
        init(points);
    }

    public ToneLine(Point.Double[] points) {
        super(0.0d, 1.0d, 0.0d, 1.0d);
        this.points = new ArrayList<>();
        init(points);
    }

    public ToneLine(ToneLine toneLine) {
        super(toneLine);
        points = new ArrayList<>();
        points.clear();
        for (Point.Double point : toneLine.points)
            points.add(point);

        update();
    }

    public void init(Point.Double[] points) {
        for (int i = 0; i < points.length; i++)
            this.points.add(points[i]);

        update();
    }

    public Object clone() {
        return new ToneLine(this);
    }

    public double getTone(double color) {
        if (color < xs[0])
            return ys[0];
        if (color > xs[xs.length - 1])
            return ys[xs.length - 1];
        double tone = 0.0d;
        for (int i = 1; i < xs.length; i++) {
            if (xs[i - 1] == xs[i])
                throw new RuntimeException("未実装");
            if (xs[i - 1] > color || color > xs[i])
                continue;
            tone = ((ys[i - 1] - ys[i]) / (xs[i - 1] - xs[i])) * (color - xs[i]) + ys[i];
            break;
        }

        if (tone < min)
            return min;
        if (tone > max)
            return max;
        else
            return tone;
    }

    public void update() {
        xs = new double[points.size()];
        ys = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            Point.Double p = points.get(i);
            xs[i] = p.x;
            ys[i] = p.y;
        }
    }

    public static void main(String[] args) {
        ToneLine toneline = new ToneLine(new Point.Double[] {
            new Point.Double(0.0d, 0.0d),
            new Point.Double(0.5d, 0.7d),
            new Point.Double(1.0d, 1.0d)
        });
        for (double c = 0.0d; c < 1.0d; c += 0.1d)
            System.out.println("cv" + toneline.getTone(c));
    }

    public int size() {
        return points.size();
    }

    public double getX(int index) {
        return xs[index];
    }

    public double getY(int index) {
        return ys[index];
    }

    public int indexOf(Point2D.Double point) {
        int index = 0;
        double max = Double.MAX_VALUE; // 1.7976931348623157e+308d
        for (int i = 0; i < points.size(); i++) {
            Point.Double p = points.get(i);
            double d = Math.abs(point.x - p.x);
            if (d < max) {
                max = d;
                index = i;
            }
        }

        return index;
    }

    public void remove(int index) {
        if (index > 0 && index < points.size() - 1) {
            points.remove(index);
            update();
        }
    }

    /** @return null when not found */
    public Point.Double get(int index) {
        if (index >= 0 && index < points.size())
            return points.get(index);
        else
            return null;
    }

    /** @return -1 when not added */
    public int add(Point2D.Double point) {
        if (point.y < min || max < point.y)
            return -1;
        for (int i = 1; i < points.size(); i++) {
            Point.Double p1 = points.get(i - 1);
            Point.Double p2 = points.get(i);
            if (p1.x < point.x && point.x < p2.x) {
                points.add(i, new Point.Double(point.x, point.y));
                update();
                return i;
            }
        }

        return -1;
    }
}
