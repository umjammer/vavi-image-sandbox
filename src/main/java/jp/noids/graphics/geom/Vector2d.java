
package jp.noids.graphics.geom;

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

    public Vector2d(Point.Double point1, Point.Double point2) {
        super(point2.x - point1.x, point2.y - point1.y);
    }

    public Vector2d swap() {
        double tmp = x;
        x = -y;
        y = tmp;
        return this;
    }

    public Vector2d aq() {
        return new Vector2d(-y, x);
    }

    public Vector2d ar() {
        double distance = Math.sqrt(x * x + y * y);
        if (distance == 0.0d) {
            x = 0.0d;
            y = 0.0d;
        } else {
            x /= distance;
            y /= distance;
        }
        return this;
    }

    public Vector2d times(double n) {
        x *= n;
        y *= n;
        return this;
    }

    public Vector2d move(Point.Double point) {
        return new Vector2d(x + point.x, y + point.y);
    }

    public String toString() {
        return String.format("[% 5.5f ,% 5.5f]", x, y);
    }
}
