
package jp.noids.image.scaling.line;

import jp.noids.image.scaling.DirectionConstants;
import jp.noids.image.scaling.ScalingUtil;
import jp.noids.image.scaling.edge.Edge;


/** c */
public class UtLine implements DirectionConstants {

    static boolean debug = false;

    public static boolean connect(Edge edge1, boolean flag, Edge edge2, boolean flag1, int i) {
        return connect(edge1, flag, edge2, flag1, i, false);
    }

    public static boolean connect(Edge edge1, boolean flag, Edge edge2, boolean flag1, int i, boolean flag2) {
        if (edge1 != null) {
            Edge edge = edge1.nextEdge(flag);
            if (edge != null) {
                if (!flag2 && edge1.next_value(flag) >= i)
                    return false;
                if (ScalingUtil.isValid(edge)) {
                    edge.disconnect(edge1);
                    if (debug)
                        debug(edge);
                }
            }
        }
        if (edge2 != null) {
            Edge edge = edge2.nextEdge(flag1);
            if (edge != null) {
                if (!flag2 && edge2.next_value(flag1) >= i)
                    return false;
                if (ScalingUtil.isValid(edge)) {
                    edge.disconnect(edge2);
                    if (debug)
                        debug(edge);
                }
            }
        }
        if (edge1 != null)
            edge1.connect(flag, edge2, i);
        if (edge2 != null)
            edge2.connect(flag1, edge1, i);
        if (debug) {
            debug(edge1);
            debug(edge2);
        }
        return true;
    }

    static void debug(Edge edge) {
        Edge edge1 = edge.nextEdge(true);
        Edge edge2 = edge.nextEdge(false);
        if (ScalingUtil.isValid(edge1))
            try {
                edge1.isConnected(edge);
            } catch (RuntimeException e) {
                System.err.println("次の接続が不正な状態です ");
                System.err.println("\t" + edge);
                System.err.println("\t" + edge1);
                e.printStackTrace(System.err);
            }
        if (ScalingUtil.isValid(edge2))
            try {
                edge2.isConnected(edge);
            } catch (RuntimeException e) {
                System.err.println("次の接続が不正な状態です ");
                System.err.println("\t" + edge);
                System.err.println("\t" + edge2);
                e.printStackTrace(System.err);
            }
    }

    public static Edge getEdge_a(Edge edge, boolean flag, int v, boolean[] aflag) {
        Edge edge1 = edge;
        if (v < 0) {
            flag = !flag;
            v = -v;
        }
        boolean flag1 = flag;
        for (int i = 0; i < v; i++) {
            Edge edge2 = edge1.nextEdge(flag1);
            if (!ScalingUtil.isValid(edge2))
                return null;
            flag1 = !edge2.isConnected(edge1);
            edge1 = edge2;
        }

        if (aflag != null && aflag.length > 0)
            aflag[0] = !flag1;
        return edge1;
    }
}
