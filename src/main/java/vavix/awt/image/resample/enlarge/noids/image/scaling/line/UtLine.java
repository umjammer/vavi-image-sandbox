
package vavix.awt.image.resample.enlarge.noids.image.scaling.line;

import vavix.awt.image.resample.enlarge.noids.image.scaling.DirectionConstants;
import vavix.awt.image.resample.enlarge.noids.image.scaling.ScalingUtil;
import vavix.awt.image.resample.enlarge.noids.image.scaling.edge.Edge;


/** c */
public class UtLine implements DirectionConstants {

    static final boolean debug = false;

    public static boolean connect(Edge edge1, boolean connected1, Edge edge2, boolean connected2, int next_value) {
        return connect(edge1, connected1, edge2, connected2, next_value, false);
    }

    public static boolean connect(Edge edge1, boolean connected1, Edge edge2, boolean connected2, int next_value, boolean flag2) {
        if (edge1 != null) {
            Edge edge = edge1.nextEdge(connected1);
            if (edge != null) {
                if (!flag2 && edge1.next_value(connected1) >= next_value)
                    return false;
                if (ScalingUtil.isValid(edge)) {
                    edge.disconnect(edge1);
                    if (debug)
                        debug(edge);
                }
            }
        }
        if (edge2 != null) {
            Edge edge = edge2.nextEdge(connected2);
            if (edge != null) {
                if (!flag2 && edge2.next_value(connected2) >= next_value)
                    return false;
                if (ScalingUtil.isValid(edge)) {
                    edge.disconnect(edge2);
                    if (debug)
                        debug(edge);
                }
            }
        }
        if (edge1 != null)
            edge1.connect(connected1, edge2, next_value);
        if (edge2 != null)
            edge2.connect(connected2, edge1, next_value);
        if (debug) {
            debug(edge1);
            debug(edge2);
        }
        return true;
    }

    static void debug(Edge edge) {
        Edge nextEdge = edge.nextEdge(true);
        Edge prevEdge = edge.nextEdge(false);
        if (ScalingUtil.isValid(nextEdge))
            try {
                nextEdge.isConnected(edge);
            } catch (RuntimeException e) {
                System.err.println("next connection is wrong");
                System.err.println("\t" + edge);
                System.err.println("\t" + nextEdge);
                e.printStackTrace();
            }
        if (ScalingUtil.isValid(prevEdge))
            try {
                prevEdge.isConnected(edge);
            } catch (RuntimeException e) {
                System.err.println("prev connection is wrong");
                System.err.println("\t" + edge);
                System.err.println("\t" + prevEdge);
                e.printStackTrace();
            }
    }

    /**
     * @param connected OUT
     */
    public static Edge getEdge_a(Edge startEdge, boolean asc, int times, boolean[] connected) {
        Edge edge = startEdge;
        if (times < 0) {
            asc = !asc;
            times = -times;
        }
        boolean notConnected = asc;
        for (int i = 0; i < times; i++) {
            Edge nextEdge = edge.nextEdge(notConnected);
            if (!ScalingUtil.isValid(nextEdge))
                return null;
            notConnected = !nextEdge.isConnected(edge);
            edge = nextEdge;
        }

        if (connected != null && connected.length > 0)
            connected[0] = !notConnected;
        return edge;
    }
}
