import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.lang.Math;

/**
 * <p>A radial layout is a style of drawing that places the vertices 
 * of a graph on concentric circles. It is often used to display trees
 * with emphasis on a certain vertex.</p>
 *
 * <p>In this implementation, a radial layout is simply defined by the
 * Graph it is a drawing of, the polar coordinates for all its vertices, 
 * and the maximum size the vertices can have without touching.
 *
 * The root is at the center of the layout, and each vertex of depth r 
 * is placed on the circle of radius r.
 *
 * The space on circles is shared between the vertices via a recursive
 * algorithm, based on the following definitions.
 *
 * Each vertex is given an angular sector, whose width is :
 * - 2pi for the main root
 * - for a secondary root, a part of the main root's angular sector 
 * proportional to its suborder compared to the suborders of other
 * secondary roots, which is at most 2pi/3 to avoid some edges to
 * be drawn on top of each other
 * ie min(2pi/3, 2pi * 2ndary root suborder / sum of secondary roots suborders) 
 * - for any non-root vertex, a part of its father's angular sector
 * proportional to its suborder (compared to the suborders of the other
 * children of the father), which is at most 2pi/3 to avoid some edges
 * to be drawn on top of each other
 * ie min(2pi/3, father sector * vertex suborder / father suborder)
 * 
 * The beginning angle of each angular sector is :
 * - 0° for the root
 * - 0° + the sum of the i first secondary roots' angular sector width
 * for the i+1th secondary root
 * - father angular sector start + the sum of the i first children's
 * angular sector width for the i+1th children</p>
 *
 * <p>The previous definitions show that it the orders of the chosing
 * of the roots, and of the ordering of the children of each vertex
 * of a graph which defines the way it will be radially laid out.</p>
 *
 * <p>To make the drawing of such a layout on android devices,
 * this implementation allows to project the layout in a 
 * [-SPACEWIDTH,SPACEWIDTH]x[-SPACEWIDTH,SPACEWIDTH] space, and computes 
 * the cartesian coordinates of both vertices and edges in it.</p>
 *
 * <p>
 * @Author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public final class RadialLayout {

	/**
	 * The width (in radians) of the main root sector.
	 */
	private final static float MAINWIDTH = (float)(2*Math.PI);

	/**
	 * The starting angle (in radians) of the main root sector.
	 */
	private final static float MAINSTART = 0f;

	/**
	 * Half the length of the side of the squared space the layout is 
	 * projected in
	 */
	private final static float SPACEWIDTH = 1000f;

	/**
	 * Associated Graph.
	 */
	private final Graph graph;

	/**
	 * Density of the vertices in the layout (in ]0,1[).
	 */
	private final float density;


	/**
	 * Maximum radius the vertices can have wihtout touching.
	 */
	private float maxRadius;

	/**
	 * Maximum depth of the vertices in the layout.
	 */
	private float maxDepth;

	/**
	 * List of the angular positions, of the vertices of the graph,
	 * in radians. The i-th value of the list is the angular position 
	 * of the i-th vertex of the graph.
	 */
	private final ArrayList<Float> thetas;

	/**
	 * List of the distances between the center of the layout and each
	 * vertex of the graph. The i-th value of the list is the distance
	 * between the center of the layout and the i-th vertex of the graph.
	 */
	private final ArrayList<Float> depths;

	/**
	 * List of the central points of the vertices of the graph,
	 * when the layout is projected in a 
	 * [-SPACEWIDTH,SPACEWIDTH]*[-SPACEWIDTH,SPACEWIDTH] space
	 */
	private ArrayList<Point> points;

	/**
	 * List of the segments materializing the vertices of the graph,
	 * when the layout is projected in a 
	 * [-SPACEWIDTH,SPACEWIDTH]*[-SPACEWIDTH,SPACEWIDTH] space
	 */
	private ArrayList<Segment> segments;


	/**
	 * Private auxiliary initialization method.
	 *
	 * Computes the specified vertex's angular position thanks to the 
	 * specified sector start and sector width (in radians). Then sets 
	 * in the radial layout its depth and angular position.
	 * Finally, computes the sectorStart and sectorWidth of the children
	 * of the vertex and calls itself recursively on them.
	 *
	 * Also updates the maxRadius and maxDepth if needed.
	 *
	 * @param v            the Vertex whose angular position must be set
	 * @param sectorStart  beginning of the angular sector of the Vertex
	 *                     in [0, 2pi]
	 * @param sectorWidth  width of the angular sector of the Vertex
	 *                     in [0, 2pi]
	 */
	private void updatePositions(Vertex v, float sectorStart, float sectorWidth) {
		int id = v.getId();
		float depth = v.getDepth();
		float theta = sectorStart + sectorWidth/2;
		thetas.set(id, theta);
		depths.set(id, depth);
		maxDepth = Math.max(maxDepth, depth);
		if (sectorWidth < Math.PI/2) {
			maxRadius = (float) Math.max(maxRadius, depth*Math.sin(sectorWidth/2));
		}
		float childStart = sectorStart;
		float childWidth = 0f;
		for (Vertex child : v.getChildren()) {
			childWidth = sectorWidth * child.getSuborder() / v.getSuborder();
			childWidth = (float) Math.min(childWidth, 2*Math.PI/3);
			updatePositions(child, childStart, childWidth);
			childStart += childWidth;
		}
	}	

	/**
	 * Initializes a new radial layout from the specified Graph and
	 * density. A highest density means a smallest space between vertices.
	 *
	 * @param graph    The graph that must be drawn
	 * @param density  The density of vertice in the layout, which must be
	 *                 in ]0,1[
	 * @throw IllegalArgumentException if the density is outside ]0,1[
	 */
	public RadialLayout(Graph graph, float density) 
	throws IllegalArgumentException {
		if (density <= 0 || density >= 1) {
			throw new IllegalArgumentException("density must be in ]0,1[");
		}
		this.density = density;
		this.graph = graph;
		maxRadius = 0.5f;
		points = null;
		segments = null;
		List<Vertex> vertices = graph.getVertices(); 
		int nbVertices = vertices.size();
		thetas = new ArrayList<Float>(Collections.nCopies(nbVertices, 0f));
		depths = new ArrayList<Float>(Collections.nCopies(nbVertices, 0f));
		// Computes the sum of secondary roots suborders
		List<Vertex> roots = graph.getRoots();
		int totalSuborder = 0;
		Iterator<Vertex> iter = roots.iterator();
		iter.next();
		for (; iter.hasNext();) {
			totalSuborder += iter.next().getSuborder();
		}
		// Updates main connected component from the main root
		iter = roots.iterator();
		updatePositions(iter.next(), MAINSTART, MAINWIDTH);
		// Updates the other connected components from the secondary roots
		float start = MAINSTART;
		float width = 0f;
		Vertex secRoot = null;
		for (; iter.hasNext();) {
			secRoot = iter.next();
			width = MAINWIDTH * secRoot.getSuborder() / totalSuborder;
			width = (float) Math.min(width, 2*Math.PI/3);
			updatePositions(secRoot, start, width);
			start += width;
		}
	}

	/**
	 * Graph getter.
	 * @return the graph associated with the layout
	 */
	public Graph getGraph() {
		return graph;
	}

	/**
	 * Vertex radius getter.
	 * @return the radius of the vertices in the layout
	 */
	public float getVertexRadius() {
		return maxRadius * density;
	}

	/**
	 * Angular positions getter.
	 * @return a read only view of the angular positions of the vertices
	 *         of the radial layout.
	 */
	public List<Float> getAngularPositions() {
		return Collections.unmodifiableList(thetas);
	}

	/**
	 * Depths getter.
	 * @return a read only view of the distances between the center and the 
	 *         vertices of the radial layout.
	 */
	public List<Float> getDepths() {
		return Collections.unmodifiableList(depths);
	}

	/**
	 * Returns a view of the list of the central points of the vertices
	 * of the graph, when the layout is projected in a
	 * [-SPACEWIDTH,SPACEWIDTH]*[-SPACEWIDTH,SPACEWIDTH] space,
	 * after computing it if it has not been done yet.
	 *
	 * @return a read only view of the list of the central points of the 
	 *         layout's vertices in the projection space
	 */
	public List<Point> getCentralPoints() {
		// points is null if and only if the list of the central points
		// has not been computed yet
		if (points == null) {
			points = new ArrayList<Point>(thetas.size());
			// Computes the resizing ratio so that the most extern vertices
			// are at the rim of the projection space
			float ratio = SPACEWIDTH / (maxDepth+maxRadius);   
			// Computes the coordinates of each vertex
			for (int i = 0; i < thetas.size(); i++) {
				int x = Math.round(ratio * depths.get(i) * (float) Math.cos(thetas.get(i)));
				int y = Math.round(ratio * depths.get(i) * (float) Math.sin(thetas.get(i)));
				points.add(new Point(x, y));
			}
		}
		return Collections.unmodifiableList(points);
	}

	/**
	 * Returns a view of the list of the segments materializing the 
	 * vertices of the graph, when the layout is projected in a 
	 * [-SPACEWIDTH,SPACEWIDTH]*[-SPACEWIDTH,SPACEWIDTH] space,
	 * after computing it if it has not been done yet.
	 * Note : vertical segments are tilted a bit to make easier
	 * the computation of segment intersections.
	 *
	 * @return a read only view of the list of the segments materializing 
	 *         the layout's vertices in the projection space
	 */
	public List<Segment> getSegments() {
		// points is null if and only if the list of the central points
		// has not been computed yet
		if (segments == null) {
			List<Point> points = getCentralPoints();
			List<Edge> edges = graph.getEdges();
			segments = new ArrayList<Segment>(edges.size());
			float radius = maxRadius*density*SPACEWIDTH / (maxDepth+maxRadius);
			// Computes the coordinates of each segment, which goes from 
			// the rim of the corresponding edge's first end, to the rim
			// of its second end 
			for (Edge e : edges) {
				Point end1 = points.get(e.getEnd1().getId());
				Point end2 = points.get(e.getEnd2().getId());
				// the edge intersect with its ends at alpha and 
				// (1-alpha) of its length
				float alpha = 1 - radius/end1.distance(end2);
				int x1 = Math.round(alpha*end1.getX() + (1-alpha)*end2.getX());
				int y1 = Math.round(alpha*end1.getY() + (1-alpha)*end2.getY());
				int x2 = Math.round((1-alpha)*end1.getX() + alpha*end2.getX());
				int y2 = Math.round((1-alpha)*end1.getY() + alpha*end2.getY());
				if (x1 == x2) {
					x2 ++;
				}
				Point p1 = new Point(x1, y1);
				Point p2 = new Point(x2, y2);
				segments.add(new Segment(p1, p2));
			}
		}
		return Collections.unmodifiableList(segments);
	}

	/**
	 * Projected vertex radius getter.
	 * @return the radius of the vertices in the projection space
	 */
	public int getProjectedVertexRadius() {
		return Math.round(maxRadius*density*SPACEWIDTH / (maxDepth+maxRadius));
	}

	/**
	 * Computes the number of edges intersections in the layout then
	 * returns it
	 *
	 * @return the number of edges intersections in the layout
	 */
	public int nbEdgeCrossings() {
		List<Segment> segments = getSegments();
		// The only case where segments is unfortunately invalid, is if
		// two segments are on top of each other.
		// In this case we return a very big number of intersections
		try {
			return InterComputer.intersections(segments).size();
		} catch (IllegalArgumentException e) {
			return Integer.MAX_VALUE;
		}
	}

	/**
	 * Computes the number of intersections between edges and vertices
	 * in the layout then returns it.
	 *
	 * @return the number of intersections between edges and vertices 
	 *         in the layout
	 */
	public int nbVertexCrossings() {
		List<Segment> segments = getSegments();
		List<Segment> extraSegments = new ArrayList<Segment>();
		for (Edge e : graph.getExtraEdges()) {
			extraSegments.add(segments.get(e.getId()));
		}
		List<Point> centers = getCentralPoints();
		int radius = getProjectedVertexRadius();
		return InterComputer.nbIntersections(extraSegments, centers, radius-1);
	}
}