
package jp.noids.image.scaling.edge;

import jp.noids.image.scaling.line.Line;


/** i */
public abstract class EdgeBase implements Edge {

    boolean flag1 = false;
    public int startColor;
    public int endColor;
    Edge edge1;
    Edge edge2;
    int value1;
    int value2;
    Line line;
    static int X = 248;
    static int Y = 340;

    public void connect(boolean asc, Edge edge, int value) {
        if (asc) {
            edge1 = edge;
            value1 = value;
        } else {
            edge2 = edge;
            value2 = value;
        }
    }

    public void disconnect(Edge edge) {
        if (edge1 == edge) {
            edge1 = null;
            value1 = 0;
        } else if (edge2 == edge) {
            edge2 = null;
            value2 = 0;
        } else {
            throw new RuntimeException("来ないはず");
        }
    }

    public Edge nextEdge(boolean asc) {
        if (asc)
            return edge1;
        else
            return edge2;
    }

    public int next_value(boolean asc) {
        if (asc)
            return value1;
        else
            return value2;
    }

    public boolean isConnected(Edge edge) {
        if (edge == edge1 && edge == edge2)
            throw new RuntimeException("おかしな状態 : 両側に同じ接続（長さ２の閉ループ） : " + this);
        if (edge == edge1)
            return true;
        if (edge == edge2)
            return false;
        else
            throw new RuntimeException("おかしな状態 : ScalingUtil.isEdge()で比較していますか？ : " + this);
    }

    public boolean contains(Edge edge) {
        return edge == edge1 || edge == edge2;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Line getLine() {
        return line;
    }

    public boolean is_flag1() {
        return flag1;
    }

    public void set_flag1(boolean flag) {
        this.flag1 = flag;
    }
}
