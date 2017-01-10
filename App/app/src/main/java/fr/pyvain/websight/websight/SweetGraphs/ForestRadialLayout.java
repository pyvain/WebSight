package fr.pyvain.websight.websight.SweetGraphs;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class ForestRadialLayout implements RadialLayout, Parcelable {

    private final static float MIN_RADIUS = 0.15f;
    private final static float MAX_RADIUS = 0.35f;
    private final static float MIN_THICKNESS = 0.01f;
    private final static float MAX_THICKNESS = 0.07f;

    private final Rectangle frame;
    private int rootId;
    private final Map<Integer, Circle> vertices;
    private final Map<Integer, Map<Integer, Segment>> edges;
    private final List<Circle> orbits;

    public ForestRadialLayout(Graph graph, Vertex root) {
        this();
        changeRoot(graph, root);
    }

    public ForestRadialLayout() {
        frame = new Rectangle(new Point(0f, 0f), 0f, 0f);
        rootId = -1;
        vertices = new HashMap<>();
        edges = new HashMap<>();
        orbits = new ArrayList<>();
    }

//    public ForestRadialLayout(Graph graph, ForestRadialLayout from, ForestRadialLayout to, float step) {
//        this();
//        rootId = from.getRootId();
//        for (Vertex v : graph.getVertices()) {
//            int vId = v.getId();
//            Circle cFrom = from.vertices.get(vId);
//            Circle cTo = to.vertices.get(vId);
//            float x = (1-step)*cFrom.getCenter().getX() + step*cTo.getCenter().getX();
//            float y = (1-step)*cFrom.getCenter().getY() + step*cTo.getCenter().getY();
//            float r = (1-step)*cFrom.getRadius() + step*cTo.getRadius();
//            Circle inter = new Circle(new Point(x, y), r);
//            vertices.put(vId, inter);
//        }
//        for (Edge e : graph.getEdges()) {
//            int id1 = e.getEnd1().getId();
//            int id2 = e.getEnd2().getId();
//            Segment sFrom = from.edges.get(id1).get(id2);
//            Segment sTo = to.edges.get(id1).get(id2);
//            float x1 = (1-step)*sFrom.getLeftEnd().getX() + step*sTo.getLeftEnd().getX();
//            float y1 = (1-step)*sFrom.getLeftEnd().getY() + step*sTo.getLeftEnd().getY();
//            float x2 = (1-step)*sFrom.getRightEnd().getX() + step*sTo.getRightEnd().getX();
//            float y2 = (1-step)*sFrom.getRightEnd().getY() + step*sTo.getRightEnd().getY();
//            float thickness = (1-step)*sFrom.getThickness() + step*sTo.getThickness();
//            Segment inter = new Segment(new Point(x1, y1), new Point(x2, y2), thickness);
//            if (!edges.containsKey(id1)) {
//                edges.put(id1, new HashMap<Integer, Segment>());
//            }
//            edges.get(id1).put(id2, inter);
//        }
//        orbits.addAll(to.orbits);
//        Rectangle fFrom = from.frame;
//        Rectangle fTo = to.frame;
//        frame.getCenter().setX((1-step)*fFrom.getCenter().getX() + step*fTo.getCenter().getX());
//        frame.getCenter().setY((1-step)*fFrom.getCenter().getY() + step*fTo.getCenter().getY());
//        frame.setWidth((1-step)*fFrom.getWidth() + step*fTo.getWidth());
//        frame.setHeight((1-step)*fFrom.getHeight() + step*fTo.getHeight());
//    }

    private Point polarInterpolation(Point p1, Point p2, Point c, float step) {
        float r1 = Point.distanceBetween(p1, c);
        float r2 = Point.distanceBetween(p2, c);
        float r = (1-step)*r1 + step*r2;
        float angle1 = (float)Math.atan2(p1.getY()-c.getY(), p1.getX()-c.getX());
        float angle2 = (float)Math.atan2(p2.getY()-c.getY(), p2.getX()-c.getX());
        float angle;
        if (p2.equals(c)) {
            angle = angle1;
        } else if (p1.equals(c)) {
            angle = angle2;
        } else {
            float diff = angle2-angle1;
            if (diff < - Math.PI) {
                angle1 -= 2*Math.PI;
            } else if (diff > Math.PI) {
                angle1 += 2*Math.PI;
            }
            angle = (1 - step) * angle1 + step * angle2;
        }
        float x = c.getX() + r*(float)Math.cos(angle);
        float y = c.getY() + r*(float)Math.sin(angle);
        return new Point(x, y);
    }

    public ForestRadialLayout(Graph graph, ForestRadialLayout from, ForestRadialLayout to, float step) {
        this();
        rootId = from.getRootId();
        Point c = from.vertices.get(rootId).getCenter();
        for (Vertex v : graph.getVertices()) {
            int vId = v.getId();
            Circle cFrom = from.vertices.get(vId);
            Circle cTo = to.vertices.get(vId);
            Point center = polarInterpolation(cFrom.getCenter(), cTo.getCenter(), c, step);
            float r = (1-step)*cFrom.getRadius() + step*cTo.getRadius();
            Circle inter = new Circle(center, r);
            vertices.put(vId, inter);
        }
        for (Edge e : graph.getEdges()) {
            updateE(e, graph);
        }
        orbits.addAll(to.orbits);
        Rectangle fFrom = from.frame;
        Rectangle fTo = to.frame;
        frame.getCenter().setX((1-step)*fFrom.getCenter().getX() + step*fTo.getCenter().getX());
        frame.getCenter().setY((1-step)*fFrom.getCenter().getY() + step*fTo.getCenter().getY());
        frame.setWidth((1-step)*fFrom.getWidth() + step*fTo.getWidth());
        frame.setHeight((1-step)*fFrom.getHeight() + step*fTo.getHeight());
    }


    public void rotate(float angle) {
        Point root = vertices.get(rootId).getCenter();
        for (Map.Entry<Integer, Circle> e : vertices.entrySet()) {
            e.getValue().getCenter().rotateAround(root, angle);
        }
        for (Map.Entry<Integer, Map<Integer, Segment>> e1 : edges.entrySet()) {
            for (Map.Entry<Integer, Segment> e2 : e1.getValue().entrySet()) {
                Segment s = e2.getValue();
                s.getLeftEnd().rotateAround(root, angle);
                s.getRightEnd().rotateAround(root, angle);
            }
        }
    }

    public void changeRoot(Graph graph, Vertex root) {
        this.rootId = root.getId();
        vertices.clear();
        edges.clear();
        orbits.clear();
        Map<Vertex, Integer> nbDescendants = new HashMap<>();
        Map<Vertex, List<Vertex>> children = new HashMap<>();

        // Performs a first BFS to compute a spanning tree rooted in the main root
        updateC(children, root);
        // Performs a DFS to compute the nb of descendant of each vertex in the main
        // connected component
        updateD(nbDescendants, root, children);
        // Performs a DFS to compute the height of the tree
        int mainHeight = computeHeight(root, children);

        // Finds the root (ie the vertex of smallest id) in every other connected component
        // And performs a BFS to compute the corresponding spanning tree, updating children
        // and nbChildren
        // Also computes the sum of the numbers of descendants of the roots
        // And the max heights of the spanning trees, needed to compute the positions.
        List<Vertex> roots = new ArrayList<>();
        int maxHeight = -1;
        int totalNbDescendants = 0;
        for (Vertex v : graph.getVertices()) {
            if (! children.containsKey(v)) {
                roots.add(v);
                updateC(children, v);
                updateD(nbDescendants, v, children);
                maxHeight = Math.max(maxHeight, computeHeight(v, children));
                totalNbDescendants += 1 + nbDescendants.get(v);
            }
        }



        // Computes the drawing of all the vertices of the graph
        float secStart = 0f;
        float totalWidth = 2*(float)Math.PI;
        updateV(root, 0, secStart, totalWidth, children, nbDescendants, graph);
        for (Vertex v : roots) {
            float secWidth = totalWidth * (1+nbDescendants.get(v)) / totalNbDescendants;
            updateV(v, mainHeight+1, secStart, secWidth, children, nbDescendants, graph);
            secStart += secWidth;
        }

        // Computes the drawing of all the edges of the graph
        // Each segment goes from rim to rim
        for (Edge e : graph.getEdges()) {
            updateE(e, graph);
        }

        // Computes the drawing of all the orbits
        updateO(mainHeight+1+maxHeight);

        // Computes the new frame
        float layoutRadius = mainHeight+1+maxHeight+MAX_RADIUS;
        frame.setCenter(new Point(0, 0));
        frame.setWidth(2*layoutRadius);
        frame.setHeight(2*layoutRadius);
    }

    private void updateC(Map<Vertex, List<Vertex>> children, Vertex root) {
        Queue<Vertex> queue = new LinkedList<>();
        queue.add(root);
        children.put(root, new ArrayList<Vertex>());
        Vertex current;
        while ((current = queue.poll()) != null) {
            List<Vertex> currentChildren = children.get(current);
            for (Vertex neighbour : current.getNeighbours()) {
                if (!children.containsKey(neighbour)) {
                    currentChildren.add(neighbour);
                    queue.add(neighbour);
                    children.put(neighbour, new ArrayList<Vertex>());
                }
            }
        }
    }

    private void updateD(Map<Vertex, Integer> nbDescendants, Vertex v,
                        Map<Vertex, List<Vertex>> children) {
        int result = 0;
        for (Vertex c : children.get(v)) {
            updateD(nbDescendants, c, children);
            result += 1 + nbDescendants.get(c);
        }
        nbDescendants.put(v, result);
    }

    private int computeHeight(Vertex v, Map<Vertex, List<Vertex>> children) {
        int maxHeight = 0;
        for (Vertex c : children.get(v)) {
            int h = computeHeight(c, children);
            maxHeight = Math.max(maxHeight, 1+h);
        }
        return maxHeight;
    }

    private void updateV(Vertex v, int depth, float secStart, float secWidth,
                       Map<Vertex, List<Vertex>> children, Map<Vertex, Integer> nbDescendants,
                        Graph graph)
    {
        float angle = secStart + secWidth/2;
        float x = depth * (float)Math.cos(angle);
        float y = depth * (float)Math.sin(angle);
        float sizeScale = graph.getMaxVertexSize() - graph.getMinVertexSize();
        float vSize = v.getData().getURLs().size() - graph.getMinVertexSize();
        float ratio = (sizeScale == 0) ? 1f : vSize/sizeScale;
        float radius = (1-ratio)*MIN_RADIUS + ratio*MAX_RADIUS;
        vertices.put(v.getId(), new Circle(new Point(x, y), radius));
        // Calls itself recursively on children
        int total = nbDescendants.get(v);
        float cStart = secStart;
        for (Vertex c : children.get(v)) {
            float cWidth = secWidth * (1 + nbDescendants.get(c)) / total;
            updateV(c, depth+1, cStart, cWidth, children, nbDescendants, graph);
            cStart += cWidth;
        }
    }

    private void updateE(Edge e, Graph graph) {
        int id1 = e.getEnd1().getId();
        int id2 = e.getEnd2().getId();
        Circle c1 = vertices.get(id1);
        Circle c2 = vertices.get(id2);
        Point p1 = c1.getCenter();
        Point p2 = c2.getCenter();
        float d = Point.distanceBetween(p1, p2);
        // Computes the end of the segment that touches c1
        float alpha = c1.getRadius() / d;
        float x1 = alpha * p2.getX() + (1 - alpha) * p1.getX();
        float y1 = alpha * p2.getY() + (1 - alpha) * p1.getY();
        // Computes the end of the segment that touches c2
        float beta = c2.getRadius() / d;
        float x2 = beta * p1.getX() + (1 - beta) * p2.getX();
        float y2 = beta * p1.getY() + (1 - beta) * p2.getY();
        // Computes the thickness of the segment
        float sizeScale = graph.getMaxEdgeSize() - graph.getMinEdgeSize();
        float eSize = e.getData().getURLs().size() - graph.getMinEdgeSize();
        float ratio = (sizeScale == 0) ? 1f : eSize/sizeScale;
        float thickness = (1-ratio)*MIN_THICKNESS + ratio*MAX_THICKNESS;
        Map <Integer, Segment> map;
        if ((map = edges.get(id1)) == null) {
            map = new HashMap<>();
            edges.put(id1, map);
        }
        map.put(id2, new Segment(
                new Point(x1, y1),
                new Point(x2, y2),
                thickness
        ));
    }

    private void updateO(int maxDepth) {
        Point origin = new Point(0, 0);
        for (int i = 1; i <= maxDepth; i++) {
            orbits.add(new Circle(origin, i));
        }
    }

    public int getRootId() {
        return rootId;
    }

    public List<Circle> projectedOrbits(Rectangle canvasSize) {
        List<Circle> result = new ArrayList<>(orbits.size());
        float ratio = Math.min(
                canvasSize.getWidth()/frame.getWidth(),
                canvasSize.getHeight()/frame.getHeight()
        );
        for (Circle o : orbits) {
            float dx = o.getCenter().getX()-frame.getCenter().getX();
            float dy = o.getCenter().getY()-frame.getCenter().getY();
            result.add(new Circle(
                    new Point(
                            canvasSize.getCenter().getX() + ratio*dx,
                            canvasSize.getCenter().getY() + ratio*dy
                    ),
                    ratio * o.getRadius()
            ));
        }
        return result;
    }

    public Map<Integer, Circle> projectedVertices(Rectangle canvasSize) {
        Map<Integer, Circle> result = new HashMap<>(vertices.size());
        float ratio = Math.min(
                canvasSize.getWidth()/frame.getWidth(),
                canvasSize.getHeight()/frame.getHeight()
        );
        for (Map.Entry<Integer, Circle> e : vertices.entrySet()) {
            float dx = e.getValue().getCenter().getX()-frame.getCenter().getX();
            float dy = e.getValue().getCenter().getY()-frame.getCenter().getY();
            Circle c = new Circle(
                    new Point(
                            canvasSize.getCenter().getX() + ratio*dx,
                            canvasSize.getCenter().getY() + ratio*dy
                    ),
                    ratio * e.getValue().getRadius()
            );
            result.put(e.getKey(), c);
        }
        return result;
    }

    public Map<Integer, Map<Integer, Segment>> projectedEdges(Rectangle canvasSize) {
        Map<Integer, Map<Integer, Segment>> result = new HashMap<>(edges.size());
        float ratio = Math.min(
                canvasSize.getWidth()/frame.getWidth(),
                canvasSize.getHeight()/frame.getHeight()
        );
        for (Map.Entry<Integer, Map<Integer, Segment>> e1 : edges.entrySet()) {
            Map<Integer, Segment> map = new HashMap<>(e1.getValue().size());
            for (Map.Entry<Integer, Segment> e2 : e1.getValue().entrySet()) {
                float dx1 = e2.getValue().getLeftEnd().getX() - frame.getCenter().getX();
                float dy1 = e2.getValue().getLeftEnd().getY() - frame.getCenter().getY();
                float dx2 = e2.getValue().getRightEnd().getX() - frame.getCenter().getX();
                float dy2 = e2.getValue().getRightEnd().getY() - frame.getCenter().getY();
                Segment s = new Segment(
                        new Point(
                                canvasSize.getCenter().getX() + ratio * dx1,
                                canvasSize.getCenter().getY() + ratio * dy1
                        ),
                        new Point(
                                canvasSize.getCenter().getX() + ratio * dx2,
                                canvasSize.getCenter().getY() + ratio * dy2
                        ),
                        ratio * e2.getValue().getThickness()
                );
                map.put(e2.getKey(), s);
            }
            result.put(e1.getKey(), map);
        }
        return result;
    }

    public float projectedMinRadius(Rectangle canvasSize) {
        float ratio = Math.min(
                canvasSize.getWidth()/frame.getWidth(),
                canvasSize.getHeight()/frame.getHeight()
        );
        return ratio*MIN_RADIUS;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(frame, flags);
        out.writeInt(rootId);
        out.writeInt(vertices.size());
        for (Map.Entry<Integer, Circle> e : vertices.entrySet()) {
            out.writeInt(e.getKey());
            out.writeParcelable(e.getValue(), flags);
        }
        out.writeInt(edges.size());
        for (Map.Entry<Integer, Map<Integer, Segment>> e1 : edges.entrySet()) {
            out.writeInt(e1.getKey());
            out.writeInt(e1.getValue().size());
            for (Map.Entry<Integer, Segment> e2 : e1.getValue().entrySet()) {
                out.writeInt(e2.getKey());
                out.writeParcelable(e2.getValue(), flags);
            }
        }
        out.writeInt(orbits.size());
        for (Circle o : orbits) {
            out.writeParcelable(o, flags);
        }
    }

    public static final Parcelable.Creator<ForestRadialLayout> CREATOR
            = new Parcelable.Creator<ForestRadialLayout>() {
        public ForestRadialLayout createFromParcel(Parcel in) {
            return new ForestRadialLayout(in);
        }

        public ForestRadialLayout[] newArray(int size) {
            return new ForestRadialLayout[size];
        }
    };

    private ForestRadialLayout(Parcel in) {
        frame = in.readParcelable(Rectangle.class.getClassLoader());
        rootId = in.readInt();
        // Rebuilds the map of the vertices
        int nbVertices = in.readInt();
        vertices = new HashMap<>(nbVertices);
        for (int i = 0; i < nbVertices; i++) {
            vertices.put(in.readInt(), (Circle)in.readParcelable(Circle.class.getClassLoader()));
        }
        // Rebuilds the 2D map of the edges
        int nbEdges1 = in.readInt();
        edges = new HashMap<>(nbEdges1);
        for (int i = 0; i < nbEdges1; i++) {
            int key = in.readInt();
            int nbEdges2 = in.readInt();
            Map<Integer, Segment> map = new HashMap<>(nbEdges2);
            for (int j = 0; j < nbEdges2; j++) {
                map.put(in.readInt(), (Segment)in.readParcelable(Segment.class.getClassLoader()));
            }
            edges.put(key, map);
        }
        // Rebuilds the list of the orbits
        int nbOrbits = in.readInt();
        orbits = new ArrayList<>(nbOrbits);
        for (int i = 0; i < nbOrbits; i++) {
            orbits.add((Circle)in.readParcelable(Circle.class.getClassLoader()));
        }
    }
}
