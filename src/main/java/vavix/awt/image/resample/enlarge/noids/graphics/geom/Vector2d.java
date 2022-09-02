
package vavix.awt.image.resample.enlarge.noids.graphics.geom;

import java.awt.Point;


/** a */
public class Vector2d extends Point.Double {

    public Vector2d(float x, float y) {
        super(x, y);
    }

    public Vector2d(double x, double y) {
        super(x, y);
    }

    /** b⃗ - a⃗ */
    public Vector2d(Point.Double a, Point.Double b) {
        super(b.x - a.x, b.y - a.y);
    }

    public Vector2d rotate90() {
        double tmp = x;
        x = -y;
        y = tmp;
        return this;
    }

    /** @return new instance */
    public Vector2d rotate90AndNew() {
        return new Vector2d(-y, x);
    }

    public Vector2d unit() {
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

    public Vector2d multiply(double scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }

    /** @return new instance */
    public Vector2d addAndNew(Point.Double point) {
        return new Vector2d(x + point.x, y + point.y);
    }

    public String toString() {
        return String.format("[% 5.5f ,% 5.5f]", x, y);
    }
}
