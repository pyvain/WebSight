package fr.pyvain.websight.websight.PersonalDataGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import fr.pyvain.websight.websight.Geometry.CPoint;
import fr.pyvain.websight.websight.Geometry.InterComputer;
import fr.pyvain.websight.websight.Geometry.Segment;

/**
 * <p>This class represents a drawing of a graph. It is defined by : <ul>
 *     <li>The centers of the graph's vertices.</li>
 *     <li>The radius of the graph's vertices</li>
 *     <li>The segments representing the graph's edges</li>
 * </ul></p>
 *
 * <p>To make easier the actual drawing phase, it also contains
 * the distance in pixel equivalent to 1 unit of distance in the base layout,
 * and the radius of the vertices.</p>
 *
 * <p>Finally, it allows to compute which Vertex / Edge is at a
 * given position</p>
 *
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public final class Drawing {

    // The followings constants give the dimension of the "draft" canvas,
    // i.e. the canvas to use to check the number of intersections in a layout

    /**
     * Left/top end x/y-coordinate of the "draft canvas".
     */
    public static final int CMIN = -1000;

    /**
     * Right/bottom end x/y-coordinate of the "draft canvas".
     */
    public static final int CMAX = 1000;

    /**
     * X/Y-coordinate of (0, 0) in the draft canvas
     */
    public static final int C0 = 0;


    /**
     * Centers of the vertices.
     */
    private final Map<Vertex, CPoint> centers;

    /**
     * Segments materializing the edges.
     */
    private final Map<Edge, Segment> segments;

    /**
     * Radius of the vertices.
     */
    private float radius;

    /**
     * Distance equivalent to 1 unit of distance in the base layout.
     */
    private float unit;

    /**
     * Initializes a new empty drawing.
     */
    public Drawing() {
        centers = new HashMap<>();
        segments = new HashMap<>();
        radius = 0f;
        unit = 0f;
    }

    /**
     * Initializes a new drawing of the specified graph, using the specified
     * radial layout, on a canvas of specified dimensions, placing the center of
     * the layout at specified position, and using specified
     * Vertex radius.
     *
     * @param g            Graph that must be drawn
     * @param l            RadialLayout to be used
     * @param vRadius      Vertex radius to be used, must be in
     *                     ]0,layout's max vertex radius[
     * @param xMin         left end x-coordinate of the canvas
     * @param yMin         top end y-coordinate of the canvas
     * @param xMax         right end x-coordinate of the canvas
     * @param yMax         bottom end y-coordinate of the canvas
     * @param x0           x-coordinate of (0, 0) in the drawing
     * @param y0           y-coordinate of (0, 0) in the drawing
     * @throws IllegalArgumentException if the canvas dimension are
     * invalid, the root is outside the canvas, or the vertex radius
     * is out of bounds.
     */
    public Drawing(Graph g, RadialLayout l, float vRadius,
                   int xMin, int yMin, int xMax, int yMax, int x0, int y0)
            throws IllegalArgumentException
    {
        this();
        update(g, l, vRadius, xMin, yMin, xMax, yMax, x0, y0);
    }


    /**
     * Replaces the current drawing by a drawing of the specified graph,
     * using the specified radial layout, on a canvas of specified dimensions,
     * placing the center of the layout at specified position, and using specified
     * Vertex radius.
     *
     * @param g            Graph that must be drawn
     * @param l            RadialLayout to be used
     * @param vRadius      Vertex radius to be used, must be in
     *                     ]0,layout's max vertex radius[
     * @param xMin         left end x-coordinate of the canvas
     * @param yMin         top end y-coordinate of the canvas
     * @param xMax         right end x-coordinate of the canvas
     * @param yMax         bottom end y-coordinate of the canvas
     * @param x0           x-coordinate of (0, 0) in the drawing
     * @param y0           y-coordinate of (0, 0) in the drawing*
     * @throws IllegalArgumentException if the canvas dimension are
     * invalid, the root is outside the canvas, or the vertex radius
     * is out of bounds.
     */
    public void update(Graph g, RadialLayout l, float vRadius,
                       int xMin, int yMin, int xMax, int yMax, int x0, int y0)
            throws IllegalArgumentException
    {
        // Checks arguments
        if (xMax < xMin || yMax < yMin) {
            throw new IllegalArgumentException("Invalid canvas dimensions");
        } else if (x0 < xMin || xMax <= x0 || y0 < yMin || yMax <= y0) {
            throw new IllegalArgumentException("Root must be in the canvas");
        } else if (vRadius <= 0 || vRadius >= l.getMaxVertexRadius()) {
            throw new IllegalArgumentException("Invalid vertex Radius");
        }

        // Clears the drawing
        centers.clear();
        segments.clear();

        // Computes new drawing
        // Position of (0, 0) in the graph
        CPoint p0 = new CPoint(x0, y0);
        // Ratio between distances in the drawing and distances in the layout
        unit = Math.min(xMax-xMin, yMax-yMin) / (2*l.getLayoutRadius());
        // Radius of vertices in the drawing
        radius = unit * vRadius;
        // (x, y) in the layout -> (x0 + unit*x, y0 + unit*y) in the drawing
        int i = 0;
        for (Vertex v : g.getVertices()) {
            centers.put(v, new CPoint(l.getPolarCoords(i++), p0, unit));
        }
        // Each segment goes from rim to rim
        for (Edge e : g.getEdges()) {
            CPoint end1 = centers.get(e.getEnd1());
            CPoint end2 = centers.get(e.getEnd2());
            // The edge intersects with 1st (resp. 2nd) end vertex at
            // alpha (resp. 1-alpha) of its length
            float alpha = radius / (end1.distance(end2));
            CPoint p1 = new CPoint(end1, end2, alpha);
            CPoint p2 = new CPoint(end1, end2, 1-alpha);
            segments.put(e, new Segment(p1, p2));
        }
    }

    /**
     * Centers getter.
     * @return a read only view of the centers of the vertices
     */
    public Map<Vertex, CPoint> getCenters() {
        return Collections.unmodifiableMap(centers);
    }

    /**
     * Segments getter.
     * @return a read only view of the  segments materializing
     *         the base layout's vertices
     */
    public Map<Edge, Segment> getSegments() {
        return Collections.unmodifiableMap(segments);
    }

    /**
     * Unit getter.
     * @return the distance equivalent to 1 unit of distance in the base layout.
     */
    public float getUnit() {
        return unit;
    }

    /**
     * Vertex radius getter.
     * @return the radius of the vertices
     */
    public float getVertexRadius() {
        return radius;
    }

    /**
     * If there is a Vertex drawn within specified distance of the
     * specified point, returns it. Else returns null.
     * @param p CPoint to analyse
     * @param d radius of the area to analyse, must be >= 0
     * @return The Vertex drawn in the specified area if it exists,
     * null else
     */
    public Vertex vertexAt(CPoint p, int d) {
        for (Vertex v : centers.keySet()) {
            if (p.distance(centers.get(v)) <= radius + d) {
                return v;
            }
        }
        return null;
    }

    /**
     * If there is an Edge drawn within specified distance of the
     * specified point, returns it. Else returns null.
     * @param p CPoint to analyse
     * @param d radius of the area to analyse, must be > 0
     * @return The Edge drawn in the specified area if it exists,
     * null else
     */
    public Edge edgeAt(CPoint p, int d) {
        for (Edge e : segments.keySet()) {
            if (segments.get(e).contains(p, d)) {
                return e;
            }
        }
        return null;
    }


    /**
     * Returns the number of intersection between the edges of the
     * drawing.
     *
     * Undefined behaviour if one of the segments is outside
     * [-1000, 1000] x [-1000, 1000] (caused by long overflow)
     *
     * @return the number of intersection between the edges of the
     * layout
     */
    public int nbEdgeIntersections() {
        return InterComputer.edgeIntersections(segments.values()).size();
    }

    /**
     * Returns the number of times the given set of edges
     * cross Vertices in the drawing.
     *
     * Undefined behaviour if one of the segments is outside
     * [-1000, 1000] x [-1000, 1000] (caused by long overflow)
     *
     * @param edges The set of edges to compute the intersection
     *              with the vertices of the drawing
     * @return the number of times vertices cross edges in the
     * layout
     */
    public int nbVertexCrossings(Collection<Edge> edges) {
        Collection<Segment> segments = new ArrayList<>();
        for (Edge e : edges) {
            segments.add(this.segments.get(e));
        }
        return InterComputer.nbVertexCrossings(segments,
                centers.values(), radius+1) - 2*segments.size();
    }
}