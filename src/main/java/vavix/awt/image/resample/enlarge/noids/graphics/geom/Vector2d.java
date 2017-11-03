
package vavix.awt.image.resample.enlarge.noids.graphics.geom;

import java.awt.Point;


/** a */
public class Vector2d extends Point.Double {

    public Vector2d() {
    }

    public Vector2d(float x, float y) {
        super(x, y);
    }

    public Vector2d(double x, double y) {
        super(x, y);
    }

    public Vector2d(Point.Double source, Point.Double target) {
        super(target.x - source.x, target.y - source.y);
    }

    public Vector2d rotate() {
        double tmp = x;
        x = -y;
        y = tmp;
        return this;
    }

    public Vector2d newRotatedInstance() {
        return new Vector2d(-y, x);
    }

    public Vector2d ar() {
        double v = Math.sqrt(x * x + y * y);
        if (v == 0.0d) {
            x = 0.0d;
            y = 0.0d;
        } else {
            x /= v;
            y /= v;
        }
        return this;
    }

    public Vector2d times(double n) {
        x *= n;
        y *= n;
        return this;
    }

    public Vector2d newMovedInstance(Point.Double point) {
        return new Vector2d(x + point.x, y + point.y);
    }

    public String toString() {
        return String.format("[% 5.5f ,% 5.5f]", x, y);
    }
}
