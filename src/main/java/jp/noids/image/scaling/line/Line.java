
package jp.noids.image.scaling.line;

import java.awt.Point;
import java.awt.Rectangle;

import jp.noids.image.scaling.Constants;
import jp.noids.image.scaling.DirectionConstants;
import jp.noids.image.scaling.ScalingUtil;
import jp.noids.image.scaling.edge.Edge;
import jp.noids.image.scaling.edge.Edge_g;
import jp.noids.math.FMath;
import jp.noids.math.UtAngle;
import jp.noids.util.UtToString;


/** a */
public class Line implements DirectionConstants, Constants {

    Edge edge1;
    Edge edge2;
    boolean _connected1;
    boolean _connected2;
    Rectangle.Double rect;
    int count = -1;
    int index;
    static int counter = 0;
    double color_value1;
    double color_value2;
    double length;

    public Line(Edge edge1, boolean connected1, Edge edge2, boolean connected2) {
        this.edge1 = edge1;
        this._connected1 = connected1;
        this.edge2 = edge2;
        this._connected2 = connected2;
        index = counter++;
    }

    public Edge get_edge1() {
        return edge1;
    }

    public Edge get_nextEdge() {
        if (isOpen()) {
            throw new RuntimeException("Openな線では呼び出せません");
        } else {
            Edge edge = edge1.nextEdge(!_connected1);
            return edge;
        }
    }

    public boolean is_connected1() {
        return _connected1;
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
        double x1 = 1.0d / 0.0d;
        double y1 = 1.0d / 0.0d;
        double x2 = -1.0d / 0.0d;
        double y2 = -1.0d / 0.0d;
        int i = 0;
        double color1 = 0.0d;
        double color2 = 0.0d;
        double l = 0.0d;
        Class_b b1 = new Class_b(this);
        Edge_g edge;
        while ((edge = (Edge_g) b1.get_edge2()) != null) {
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

    public boolean is_b(Edge edge, boolean flag, int v) {
        int c = 0;
        Edge edge1 = edge;
        boolean notConnected = flag;
        for (int i = 0; i < 10000000; i++) {
            Edge edge2 = edge1.nextEdge(notConnected);
            if (!ScalingUtil.isValid(edge2) || edge2 == edge)
                return false;
            if (c++ >= v)
                return true;
            notConnected = !edge2.isConnected(edge1);
            edge1 = edge2;
        }

        throw new RuntimeException("無限ループ");
    }

    /** @return angle */
    public double get_angle_a(Edge edge, boolean flag) {
        Edge edge1 = edge;
        boolean flag1 = !flag;
        double d1 = 0.52359877559829882d;
        int c1 = 1;
        Point.Double[] points = new Point.Double[3];
        double[] angles1 = new double[3];
        Point.Double p = edge1.get_point1();
        points[0] = p;
        for (int i = 0; i < (byte) 3; i++) {
            Edge edge2 = edge1.nextEdge(flag1);
            if (!ScalingUtil.isValid(edge2) || edge2 == edge)
                break;
            Point.Double p2 = edge2.get_point1();
            if (c1 < 3)
                points[c1] = p2;
            double dy = p.y - p2.y;
            double dx = p.x - p2.x;
            double angle = FMath.getAngle(dy, dx);
            angles1[i] = angle;
            flag1 = !edge2.isConnected(edge1);
            edge1 = edge2;
            p = p2;
            c1++;
        }

        double sumOfAngle = 0.0d;
        double[] angles = new double[3 - 1];
        int c2 = 0;
        for (int i = 0; i < c1 - 2; i++) {
            double angle1 = UtAngle.diff(angles1[i], angles1[i + 1]);
            if (Math.abs(angle1) > d1) {
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
            throw new RuntimeException("長さが１(自身を入れると2)では、方向を決定できません\n  isLineLenOver()を使って、指定の向きに十分な長さがあるか事前に確認してください");
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

    public boolean is_a(Edge edge, int len, double smoothLevel) {
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

    public double getSmoothLevel(Edge edge, boolean flag, int len) {
        if (len < 3)
            throw new RuntimeException("smoothレベルを見るには3以上の長さが必要です");
        Edge edge1 = edge;
        boolean flag1 = flag;
        int c = 0;
        double[] angles = new double[len];
        for (int i = 0; i < len; i++) {
            Edge edge2 = edge1.nextEdge(flag1);
            if (!ScalingUtil.isValid(edge2) || edge2 == edge)
                break;
            Point.Double p1 = edge1.get_point1();
            Point.Double p2 = edge2.get_point1();
            double y = p1.y - p2.y;
            double x = p1.x - p2.x;
            double a = FMath.getAngle(y, x);
            angles[i] = a;
            flag1 = !edge2.isConnected(edge1);
            edge1 = edge2;
            c++;
        }

        if (c < 3)
            return 0.0d;
        double diff = UtAngle.diff(angles[0], angles[1]);
        double min = -1.0D / 0.0d;
        double size = Math.abs(diff);
        for (int i = 1; i < c - 1; i++) {
            double diff2 = UtAngle.diff(angles[i], angles[i + 1]);
            double size2 = Math.abs(diff2);
            if (size2 > size)
                size = size2;
            double diff3 = Math.abs(diff - diff2);
            if (diff3 > min)
                min = diff3;
        }

        double d6 = 1.0d;
        double d9 = 0.78539816339744828d;
        if (size > d9) {
            double d11 = 0.3d;
            d6 = d11 - (d11 * (size - d9)) / 0.78539816339744828d;
            if (d6 <= 0.0D)
                return 0.0D;
        }
        double d12 = 0.3490658503988659d;
        double d14 = 0.78539816339744828d;
        if (min > d12) {
            double d15 = 1.0D - (min - d12) / d14;
            if (d15 <= 0.0D)
                return 0.0D;
            if (d15 < d6)
                d6 = d15;
        }
        return d6;
    }

    public void debug() {
        System.out.println("[" + index + "] " + (isOpen() ? "Open " : "Close") + " 長さ " + getLength() + " \t" + UtToString.toString(getBounds(), 2));
    }

    public boolean isLineLenOver() {
        Edge edge = edge1;
        boolean flag = _connected1;
label0: do {
            boolean flag11;
            Edge edge1;
            while (true) {
                edge1 = edge.nextEdge(flag);
                if (!ScalingUtil.isValid(edge1))
                    break label0;
                flag11 = edge1.isConnected(edge);
                if (!edge1.get_point2().equals(edge.get_point2()))
                    break;
                boolean flag22 = !edge1.isConnected(edge);
                Edge edge2 = edge1.nextEdge(flag22);
                if (!ScalingUtil.isValid(edge2))
                    break;
                Point.Double p1 = edge.get_point1();
                Point.Double p2 = edge1.get_point1();
                edge.moveBit((p1.x + p2.x) / 2d, (p1.y + p2.y) / 2d);
                setEdge_b(edge1);
                if (count <= 4)
                    return false;
            }
            flag = !flag11;
            edge = edge1;
        } while (edge != edge1);
        return true;
    }

    public void setEdge_b(Edge edge) {
        if (edge.getLine() != this)
            throw new RuntimeException("おかしな状態");
        Edge nextEdge1 = edge.nextEdge(true);
        Edge nextEdge2 = edge.nextEdge(false);
        boolean valid1 = ScalingUtil.isValid(nextEdge1);
        boolean valid2 = ScalingUtil.isValid(nextEdge2);
        if (valid1 && valid2) {
            boolean connected1 = nextEdge1.isConnected(edge);
            boolean connected2 = nextEdge2.isConnected(edge);
            UtLine.a(nextEdge1, connected1, nextEdge2, connected2, 1, true);
            if (edge == edge1) {
                if (_connected1) {
                    edge1 = nextEdge1;
                    _connected1 = !connected1;
                } else {
                    edge1 = nextEdge2;
                    _connected1 = !connected2;
                }
            } else if (edge == edge2)
                if (_connected2) {
                    edge2 = nextEdge1;
                    _connected2 = !connected1;
                } else {
                    edge2 = nextEdge2;
                    _connected2 = !connected2;
                }
        } else if (valid1) {
            boolean flag3 = nextEdge1.isConnected(edge);
            UtLine.a(nextEdge1, flag3, (Edge) null, true, 1, true);
            if (edge == edge1) {
                if (_connected1) {
                    edge1 = nextEdge1;
                    _connected1 = !flag3;
                } else {
                    throw new RuntimeException("未実装");
                }
            } else if (edge == edge2)
                if (_connected2) {
                    edge2 = nextEdge1;
                    _connected2 = !flag3;
                } else {
                    throw new RuntimeException("未実装");
                }
        } else if (valid2) {
            boolean flag4 = nextEdge2.isConnected(edge);
            UtLine.a((Edge) null, true, nextEdge2, flag4, 1, true);
            if (edge == edge1) {
                if (_connected1)
                    throw new RuntimeException("未実装");
                edge1 = nextEdge2;
                _connected1 = !flag4;
            } else if (edge == edge2) {
                if (_connected2)
                    throw new RuntimeException("未実装");
                edge2 = nextEdge2;
                _connected2 = !flag4;
            }
        } else {
            throw new RuntimeException("おかしいな状態");
        }
        count--;
    }
}
