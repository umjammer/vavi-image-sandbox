
package vavix.awt.image.resample.enlarge.noids.image.scaling.line;

import vavix.awt.image.resample.enlarge.noids.image.scaling.ScalingUtil;
import vavix.awt.image.resample.enlarge.noids.image.scaling.edge.Edge;


public class Class_b {

    Line line;
    Edge edge1;
    Edge edge2;
    boolean notValid;
    Edge edge3;
    boolean flag;

    public Class_b(Line line) {
        this.line = line;
        edge1 = edge2 = line.get_edge1();
        notValid = line.is_connected1();
    }

    public Edge get_edge2() {
        if (edge2 == null || edge2 == Edge.dummyEdge)
            return null;
        edge3 = edge2;
        flag = notValid;
        edge2 = edge2.nextEdge(notValid);
        if (edge2 == edge1 || !ScalingUtil.isValid(edge2))
            edge2 = null;
        else
            notValid = !edge2.isConnected(edge3);
        return edge3;
    }

    public boolean get_flag() {
        return flag;
    }

    public Edge get_edge1() {
        return edge1;
    }
}
