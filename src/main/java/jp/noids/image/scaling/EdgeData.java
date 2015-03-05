
package jp.noids.image.scaling;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import jp.noids.image.scaling.edge.Edge;
import jp.noids.image.scaling.edge.EdgeX;
import jp.noids.image.scaling.edge.EdgeY;
import jp.noids.image.scaling.line.Line;
import jp.noids.image.scaling.view.DataBufferPixel;
import jp.noids.image.util.UtImage;


/** d */
public class EdgeData implements DirectionConstants, Constants {

    List<EdgeX>[] edgeXs = null;
    List<EdgeY>[] edgeYs = null;
    BufferedImage image;
    int margin = 0;
    UtImage util;
    int width;
    int height;
    List<Line> lines;
    DataBufferPixel pixel;

    @SuppressWarnings("unchecked")
    EdgeData(BufferedImage image, int margin) {
        int w = image.getWidth();
        int h = image.getHeight();
        this.edgeXs = new ArrayList[h];
        this.edgeYs = new ArrayList[w];
        this.image = image;
        this.util = new UtImage(image);
        this.width = w;
        this.height = h;
        this.margin = margin;
    }

    public void dispose() {
        edgeXs = null;
        edgeYs = null;
        if (image != null)
            image.flush();
        if (util != null)
            util.dispose();
        lines.clear();
        if (pixel != null)
            pixel.dispose();
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getMergin() {
        return margin;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void addEdge(EdgeX edgeX) {
        if (edgeXs[edgeX.y] == null)
            edgeXs[edgeX.y] = new ArrayList<EdgeX>();
        edgeXs[edgeX.y].add(edgeX);
    }

    public void addEdge(EdgeY edgeY) {
        if (edgeYs[edgeY.x] == null)
            edgeYs[edgeY.x] = new ArrayList<EdgeY>();
        edgeYs[edgeY.x].add(edgeY);
    }

    public EdgeY[] getEdgeYs(int x, int y) {
        List<EdgeY> ys = edgeYs[x];
        if (ys == null)
            return new EdgeY[0];
        EdgeY edgeY1 = null;
        EdgeY edgeY2 = null;
        for (EdgeY edgeY : ys) {
            if (edgeY.contains(x, y))
                if (edgeY1 == null)
                    edgeY1 = edgeY;
                else
                    edgeY2 = edgeY;
        }

        if (edgeY1 == null)
            return new EdgeY[0];
        if (edgeY2 == null)
            return new EdgeY[] {
                edgeY1
            };
        else
            return new EdgeY[] {
                edgeY1, edgeY2
            };
    }

    public EdgeX[] getEdgeXs(int x, int y) {
        List<EdgeX> xs = edgeXs[y];
        if (xs == null)
            return new EdgeX[0];
        EdgeX edgeX1 = null;
        EdgeX edgeX2 = null;
        for (EdgeX edgeX : xs) {
            if (edgeX.contains(x, y))
                if (edgeX1 == null)
                    edgeX1 = edgeX;
                else
                    edgeX2 = edgeX;
        }

        if (edgeX1 == null)
            return new EdgeX[0];
        if (edgeX2 == null)
            return new EdgeX[] {
                edgeX1
            };
        else
            return new EdgeX[] {
                edgeX1, edgeX2
            };
    }

    public List<EdgeX> getEgdeXs(int y) {
        return edgeXs[y];
    }

    public List<EdgeY> getEdgeYs(int x) {
        return edgeYs[x];
    }

    public void remove(int x, EdgeY edgeY) {
        List<EdgeY> ys = edgeYs[x];
        if (ys == null)
            throw new RuntimeException("未実装");
        boolean flag = ys.remove(edgeY);
        if (!flag)
            throw new RuntimeException("削除に失敗 : " + edgeY);
    }

    public Edge getEdgeAt(int x, int y) {
        Object[] edges = getEdgeYs(x, y);
        for (int i = 0; i < edges.length; i++) {
            Point.Double p = ((EdgeY) edges[i]).get_point1();
            if (x == (int) p.x && y == (int) p.y)
                return (Edge) edges[i];
        }

        edges = getEdgeXs(x, y);
        for (int i = 0; i < edges.length; i++) {
            Point.Double p = ((EdgeX) edges[i]).get_point1();
            if (x == (int) p.x && y == (int) p.y)
                return (Edge) edges[i];
        }

        return null;
    }

    public Edge[] getEdgesAt(int x, int y) {
        List<Edge> edges = null;
        EdgeX[] edgeXs = getEdgeXs(x, y);
        for (int i = 0; i < edgeXs.length; i++)
            if ((int) edgeXs[i].getX() == x) {
                if (edges == null)
                    edges = new ArrayList<Edge>();
                edges.add(edgeXs[i]);
            }

        EdgeY[] edgeYs = getEdgeYs(x, y);
        for (int i = 0; i < edgeYs.length; i++)
            if ((int) edgeYs[i].getY() == y) {
                if (edges == null)
                    edges = new ArrayList<Edge>();
                edges.add(edgeYs[i]);
            }

        if (edges != null)
            return edges.toArray(new Edge[0]);
        else
            return new Edge[0];
    }

    public boolean contains(int x, int y) {
        if (x < 0 || width <= x || y < 0 || height <= y) {
            return false;
        } else {
            EdgeX[] edgeXs = getEdgeXs(x, y);
            EdgeY[] edgeYs = getEdgeYs(x, y);
            return edgeXs.length != 0 || edgeYs.length != 0;
        }
    }

    public void setupLines() throws InterruptedException {
        int w = getWidth();
        int h = getHeight();
        boolean[] connected = new boolean[w * h];
        List<Line> lines = new ArrayList<Line>();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Edge edge = getEdgeAt(x, y);
                if (edge != null && !connected[x + y * w]) {
                    Line line = getLine(this, edge, connected);
                    if (line.getLength() >= 4) {
                        lines.add(line);
                    }
                }
            }

            if (Thread.interrupted())
                throw new InterruptedException();
        }

        this.lines = lines;
    }

    public static Line getLine(EdgeData edgeData, Edge edge, boolean[] connected) {
        int w = edgeData.getWidth();
        Edge edge1 = null;
        Edge edge2 = null;
        boolean connected1 = true;
        boolean connected2 = true;
label0: for (int i = 0; i < 2; i++) {
            boolean isStart = i == 0;
            Edge edge3 = edge;
            boolean notConnected3 = isStart;
            while (true) {
                Edge edge4 = edge3.nextEdge(notConnected3);
                if (!ScalingUtil.isValid(edge4)) {
                    if (i == 0) {
                        edge1 = edge3;
                        connected1 = !notConnected3;
                    } else if (i == 1) {
                        edge2 = edge3;
                        connected2 = !notConnected3;
                    } else {
                        throw new RuntimeException("未実装");
                    }
                    break;
                }
                if (edge4 == edge) {
                    edge1 = edge;
                    connected1 = isStart;
                    break label0;
                }
                boolean connected4 = edge4.isConnected(edge3);
                Point.Double p = edge4.get_point1();
                int x = (int) p.x;
                int y = (int) p.y;
                if (connected != null)
                    connected[x + y * w] = true;
                notConnected3 = !connected4;
                edge3 = edge4;
            }
        }

        Line line = new Line(edge1, connected1, edge2, connected2);
        return line;
    }

    public Line[] getLines() {
        return lines.toArray(new Line[0]);
    }

    public DataBufferPixel getDataBufferPixel() {
        return pixel;
    }

    public void setDataBufferPixel(DataBufferPixel pixel) {
        this.pixel = pixel;
    }
}
