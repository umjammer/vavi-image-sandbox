
package vavix.awt.image.resample.enlarge.noids.image.scaling.line;

import java.awt.Point;
import java.awt.Rectangle;

import vavix.awt.image.resample.enlarge.noids.image.scaling.ScalingUtil;
import vavix.awt.image.resample.enlarge.noids.image.scaling.edge.Edge;
import vavix.awt.image.resample.enlarge.noids.image.scaling.edge.Edge_g;
import vavix.awt.image.resample.enlarge.noids.math.FMath;
import vavix.awt.image.resample.enlarge.noids.math.UtAngle;
import vavix.awt.image.resample.enlarge.noids.util.UtToString;


/** a */
public class Line {

    Edge edge1;
    Edge edge2;
    boolean connected1;
    boolean connected2;
    Rectangle.Double rect;
    int count = -1;
    int index;
    static int counter = 0;
    double color_value1;
    double color_value2;
    double length;

    public Line(Edge edge1, boolean connected1, Edge edge2, boolean connected2) {
        this.edge1 = edge1;
        this.connected1 = connected1;
        this.edge2 = edge2;
        this.connected2 = connected2;
        index = counter++;
    }

    public Edge get_edge1() {
        return edge1;
    }

    public Edge get_nextEdge() {
        if (isOpen()) {
            throw new IllegalStateException("this line is opened");
        } else {
            Edge edge = edge1.nextEdge(!connected1);
            return edge;
        }
    }

    public boolean isConnected1() {
        return connected1;
    }

    public boolean isOpen() {
        return edge2 != null;
    }

    public Rectangle.Double getBounds() {
        if (rect == null)
            init();
        return rect;
    }

    private void init() {
        double x1 = Double.POSITIVE_INFINITY;
        double y1 = Double.POSITIVE_INFINITY;
        double x2 = Double.NEGATIVE_INFINITY;
        double y2 = Double.NEGATIVE_INFINITY;
        int i = 0;
        double color1 = 0.0d;
        double color2 = 0.0d;
        double l = 0.0d;
        Corner corner = new Corner(this);
        Edge_g edge;
        while ((edge = (Edge_g) corner.get_edge2()) != null) {
            edge.setLine(this);
            i++;
            Point.Double p = edge.get_point1();
            if (x2 < p.x)
                x2 = p.x;
            if (y2 < p.y)
                y2 = p.y;
            if (p.x < x1)
                x1 = p.x;
            if (p.y < y1)
                y1 = p.y;
            color1 += edge.get_color1();
            color2 += edge.get_color2();
            l += edge.length();
        }
        rect = new Rectangle.Double(x1, y1, x2 - x1, y2 - y1);
        count = i;
        color_value1 = color1 / count;
        color_value2 = color2 / count;
        length = l / count;
    }

    public double get_color_value2() {
        return color_value2;
    }

    public int getLength() {
        if (count < 0)
            init();
        return count;
    }

    /**
     * @return false: looped or not connected
     */
    public boolean isConnectedTo(Edge startEdge, boolean asc, int len) {
        int c = 0;
        Edge edge = startEdge;
        boolean asc_ = asc;
        for (int i = 0; i < 10000000; i++) {
            Edge nextEdge = edge.nextEdge(asc_);
            if (!ScalingUtil.isValid(nextEdge) || nextEdge == startEdge)
                return false;
            if (c++ >= len)
                return true;
            asc_ = !nextEdge.isConnected(edge);
            edge = nextEdge;
        }

        throw new IllegalStateException("infinit loop");
    }

    /** @return angle */
    public double get_angle_a(Edge startEdge, boolean asc) {
        Edge edge = startEdge;
        boolean asc_ = !asc;
        final double rad30 = 0.52359877559829882d;
        int c1 = 1;
        Point.Double[] points = new Point.Double[3];
        double[] angles1 = new double[3];
        Point.Double p = edge.get_point1();
        points[0] = p;
        for (int i = 0; i < 3; i++) {
            Edge nextEdge = edge.nextEdge(asc_);
            if (!ScalingUtil.isValid(nextEdge) || nextEdge == startEdge)
                break;
            Point.Double p2 = nextEdge.get_point1();
            if (c1 < 3)
                points[c1] = p2;
            double dy = p.y - p2.y;
            double dx = p.x - p2.x;
            double angle = FMath.getAngle(dy, dx);
            angles1[i] = angle;
            asc_ = !nextEdge.isConnected(edge);
            edge = nextEdge;
            p = p2;
            c1++;
        }

        double sumOfAngle = 0.0d;
        double[] angles = new double[3 - 1];
        int c2 = 0;
        for (int i = 0; i < c1 - 2; i++) {
            double angle1 = UtAngle.diff(angles1[i], angles1[i + 1]);
            if (Math.abs(angle1) > rad30) {
                if (i == 0)
                    return FMath.getAngle(points[0].y - points[1].y, points[0].x - points[1].x);
                break;
            }
            c2++;
            sumOfAngle += angle1;
            angles[i] = angle1;
        }

        sumOfAngle /= c2;
        if (c1 < 2)
            throw new IllegalStateException("cannot define direction when length is 1 (2, when including self)\n  using #isLineLenOver(), check specified direction has enough length");
        double dx;
        double dy;
        if (c1 == 2) {
            dx = points[0].x - points[1].x;
            dy = points[0].y - points[1].y;
        } else {
            double dx1 = points[0].x - points[1].x;
            double dy1 = points[0].y - points[1].y;
            double dx2 = points[1].x - points[2].x;
            double dy2 = points[1].y - points[2].y;
            dx = 0.75d * dx1 + 0.25d * dx2;
            dy = 0.75d * dy1 + 0.25d * dy2;
        }
        double angle = FMath.getAngle(dy, dx);
        return angle + sumOfAngle;
    }

    public boolean isSmoothable(Edge edge, int len, double smoothLevel) {
        Edge edge1 = edge.nextEdge(true);
        Edge edge2 = edge.nextEdge(false);
        if (!ScalingUtil.isValid(edge1) || !ScalingUtil.isValid(edge2))
            return false;
        boolean connected1 = edge1.isConnected(edge);
        boolean connected2 = edge2.isConnected(edge);
        double smoothLevel1 = getSmoothLevel(edge1, connected1, len);
        double smoothLevel2 = getSmoothLevel(edge2, connected2, len);
        return smoothLevel1 > smoothLevel || smoothLevel2 > smoothLevel;
    }

    public double getSmoothLevel(Edge startEdge, boolean asc, int len) {
        if (len < 3)
            throw new IllegalArgumentException("len should be larger equal 3");
        Edge edge = startEdge;
        boolean asc_ = asc;
        int c = 0;
        double[] angles = new double[len];
        for (int i = 0; i < len; i++) {
            Edge nextEdge = edge.nextEdge(asc_);
            if (!ScalingUtil.isValid(nextEdge) || nextEdge == startEdge)
                break;
            Point.Double p1 = edge.get_point1();
            Point.Double p2 = nextEdge.get_point1();
            double y = p1.y - p2.y;
            double x = p1.x - p2.x;
            double a = FMath.getAngle(y, x);
            angles[i] = a;
            asc_ = !nextEdge.isConnected(edge);
            edge = nextEdge;
            c++;
        }

        if (c < 3)
            return 0.0d;
        double diff = UtAngle.diff(angles[0], angles[1]);
        double min = Double.NEGATIVE_INFINITY;
        double angle = Math.abs(diff);
        for (int i = 1; i < c - 1; i++) {
            double diff2 = UtAngle.diff(angles[i], angles[i + 1]);
            double size2 = Math.abs(diff2);
            if (size2 > angle)
                angle = size2;
            double diff3 = Math.abs(diff - diff2);
            if (diff3 > min)
                min = diff3;
        }

        double level = 1.0d;
        final double deg45 = 0.78539816339744828d; // 45 degrees
        if (angle > deg45) {
            double v1 = 0.3d;
            level = v1 - (v1 * (angle - deg45)) / 0.78539816339744828d;
            if (level <= 0.0d)
                return 0.0d;
        }
        final double deg20 = 0.3490658503988659d; // 20 degrees
        if (min > deg20) {
            double v2 = 1.0d - (min - deg20) / deg45;
            if (v2 <= 0.0d)
                return 0.0d;
            if (v2 < level)
                level = v2;
        }
        return level;
    }

    public String toString() {
        return "LINE: [" + index + "] " + (isOpen() ? "Open " : "Close") + " len: " + getLength() + " \t" + UtToString.toString(getBounds(), 2);
    }

    public boolean isLineLenOver() {
        Edge edge = edge1;
        boolean asc = connected1;
label0: do {
            boolean connectedNext;
            Edge nextEdge;
            while (true) {
                nextEdge = edge.nextEdge(asc);
                if (!ScalingUtil.isValid(nextEdge))
                    break label0;
                connectedNext = nextEdge.isConnected(edge);
                if (!nextEdge.get_point2().equals(edge.get_point2()))
                    break;
                boolean flag22 = !nextEdge.isConnected(edge);
                Edge edge2 = nextEdge.nextEdge(flag22);
                if (!ScalingUtil.isValid(edge2))
                    break;
                Point.Double p1 = edge.get_point1();
                Point.Double p2 = nextEdge.get_point1();
                edge.moveBit((p1.x + p2.x) / 2d, (p1.y + p2.y) / 2d);
                setEdge_b(nextEdge);
                if (count <= 4)
                    return false;
            }
            asc = !connectedNext;
            edge = nextEdge;
        } while (edge != edge1);
        return true;
    }

    public void setEdge_b(Edge edge) {
        if (edge.getLine() != this)
            throw new IllegalStateException("#getLine() is self");
        Edge nextEdge = edge.nextEdge(true);
        Edge prevEdge = edge.nextEdge(false);
        boolean valid1 = ScalingUtil.isValid(nextEdge);
        boolean valid2 = ScalingUtil.isValid(prevEdge);
        if (valid1 && valid2) {
            boolean connected1 = nextEdge.isConnected(edge);
            boolean connected2 = prevEdge.isConnected(edge);
            UtLine.connect(nextEdge, connected1, prevEdge, connected2, 1, true);
            if (edge == edge1) {
                if (this.connected1) {
                    edge1 = nextEdge;
                    this.connected1 = !connected1;
                } else {
                    edge1 = prevEdge;
                    this.connected1 = !connected2;
                }
            } else if (edge == edge2)
                if (this.connected2) {
                    edge2 = nextEdge;
                    this.connected2 = !connected1;
                } else {
                    edge2 = prevEdge;
                    this.connected2 = !connected2;
                }
        } else if (valid1) {
            boolean connectedNext = nextEdge.isConnected(edge);
            UtLine.connect(nextEdge, connectedNext, null, true, 1, true);
            if (edge == edge1) {
                if (connected1) {
                    edge1 = nextEdge;
                    connected1 = !connectedNext;
                } else {
                    throw new IllegalStateException("edge1 is not connected");
                }
            } else if (edge == edge2)
                if (connected2) {
                    edge2 = nextEdge;
                    connected2 = !connectedNext;
                } else {
                    throw new IllegalStateException("edge2 is not connected");
                }
        } else if (valid2) {
            boolean connectedPrev = prevEdge.isConnected(edge);
            UtLine.connect(null, true, prevEdge, connectedPrev, 1, true);
            if (edge == edge1) {
                if (connected1)
                    throw new IllegalStateException("edge1 is not connected");
                edge1 = prevEdge;
                connected1 = !connectedPrev;
            } else if (edge == edge2) {
                if (connected2)
                    throw new IllegalStateException("edge2 is not connected");
                edge2 = prevEdge;
                connected2 = !connectedPrev;
            }
        } else {
            throw new IllegalStateException("both edges are not valid");
        }
        count--;
    }
}
