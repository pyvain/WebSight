import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Collections;

/**
 * <p>A class that represents a vertex of ap ersonal data graph. 
 * A vertex represents a textual personal data, that the user gave to 
 * the application, in order for it to be tracked, or to help
 * track other data.</p>
 * 
 * <p>
 * Each vertex has : <ul>
 *
 * <li> a unique <b>id</b>, which is also its index in the list of the
 * vertices of the graph ; </li>
 *
 * <li> a <b>label</b>, which is a textual data entered by the user, 
 * for example his email address or his nickname ; </li>
 *
 * <li> a set of <b>neighbours</b> in the personal data graph; </li>
 *
 * <li> a set of <b>children</b> in the spanning tree over the connected
 * component of the graph the vertex is in ;</li>
 *
 * <li> a suborder, defined as the number of vertices in the subtree 
 * (of the spanning tree the vertex is in) rooted in the vertex ;</li>
 *
 * <li> the depth of the vertex, defined as :<ul>
 *      <li> the number of edges in the shortest path between the
 *      vertex and the main root if they are in the same connected
 *      component ;</li>
 *      <li> M+1 if the vertex is a secondary root, where M is the
 *      maximal number of edges in the shortest path between the main 
 *      root and any vertex of the same connected component ;</li>
 *      <li> M+1+D if the vertex is not in the same connected component
 *      as the main root, where D is the number of edges between 
 *      the vertex, and the secondary root of the connected component
 *      it is in.</li>
 * </ul> 
 * <li>some data, which are at the moment the URLs of the pages 
 * containing its label.</li> 
 * </ul>
 * </p>
 * <p>
 * @Author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public final class Vertex {

	/**
	 * Identifier of the vertex, which is also its index in the list
	 * of the vertices of the graph.
	 */
	private final int id;

	/**
	 * Label (= textual piece of data) of the vertex.
	 */
	private final String label;

	/**
	 * Data of the vertex.
	 */
	private final DataSet data;

	/**
	 * Map of pairs (neighbour, edge between the vertex and the neighbour). 
	 */
	private final HashMap<Vertex, Edge> neighbours;

	/**
	 * List of the children of the vertex in the spanning tree over the 
	 * connected component of the graph it is in.
	 * 
	 * Note that the ordering of this list is quite important,
	 * as it defines the way the graph is laid out by the
	 * radialLayout(Graph g) constructor.
	 */
	private ArrayList<Vertex> children;

	/** 
	 * Suborder of the vertex, definied as :
	 * - 1 if the vertex is a leaf of the subtree (of the spanning 
	 * tree the vertex is in) rooted in the vertex.
	 * - the sum of the suborders of the children of the vertex in
	 * this subtree else  
 	 */
	private int suborder;

	/**
	 * Depth of the vertex, defined as :<ul>
	 * <li> the number of edges in the shortest path between the
	 * vertex and the main root if they are in the same connected
 	 * component ;</li>
 	 * <li> M+1 if the vertex is a secondary root, where M is the
 	 * maximal number of edges in the shortest path between the main 
 	 * root and any vertex of the same connected component ;</li>
 	 * <li> M+1+D if the vertex is not in the same connected component
 	 * as the main root, where D is the number of edges between 
 	 * the vertex, and the secondary root of the connected component
 	 * it is in.</li>
 	 * </ul> 
	 */
	private int depth;

	/**
	 * Previous state of the vertex's children list.
	 * Equal to null until shuffleChildren() is called, and set back
	 * to null by a call to unshuffleChildren()
	 */
	private ArrayList<Vertex> previousChildren;

	/**
	 * Initializes a new vertex with specified id and label,
	 * empty data, no neighbour, no children, suborder equal to 1
	 * and depth equal to 0.
	 *
	 * @param id
	 * @param label
	 */
	public Vertex(int id, String label) {
		this.id = id;
		this.label = label;
		this.data = new DataSet();
		this.neighbours = new HashMap<Vertex, Edge>();
		this.children = new ArrayList<Vertex>();
		this.previousChildren = null;
		this.suborder = 1;
		this.depth = 0;
	}

	/**
	 * Id getter.
	 * @return the id of the vertex
	 */
	public int getId() {
		return id;
	}

	/**
	 * Label getter.
	 * @return the label of the vertex
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Data getter.
	 * @return The data of the vertex
	 */
	public DataSet getData() {
		return data;
	}

	/**
	 * Neighbourhood getter.
	 * @return A view of the set of neighbours of the vertex
	 */
	public Set<Vertex> getNeighbours() {
		return neighbours.keySet();
	}

	/**
	 * Outgoing edge getter.
	 * @return The edge between the receiving vertex and the specified
	 *         vertex if they are neighbours, null else
	 */
	public Edge getEdgeTo(Vertex v) {
		return neighbours.get(v);
	}

	/**
	 * If the specified vertex is not already a neighbour of the
	 * receiving vertex, adds it its neighbours list.
	 * @param v new neighbour
	 * @param e edge between the receiving and specified vertices
	 */
	public void addNeighbourIfAbsent(Vertex v, Edge e) {
		neighbours.put(v, e);
	}

	/**
	 * Children getter.
	 * @return A read only view of the list of the vertex's children 
	 */
	public List<Vertex> getChildren() {
		return Collections.unmodifiableList(children);
	}

	/**
	 * Clears the list of children of the Vertex
	 */
	public void clearChildren() {
		children = new ArrayList<Vertex>();
	}

	/**
	 * Adds the specfied Vertex to the list of children of the Vertex
	 * @param v new children 
	 */
	public void addChild(Vertex v) {
		children.add(v);
	}

	/**
	 * (Pseudo) Randomly shuffles the children list of the Vertex, and saves 
	 * a copy of its previous state, to allow restoration thanks to the 
	 * unshuffleChildren() method.
	 */
	public void shuffleChildren() {
		previousChildren = new ArrayList<Vertex>(children);
		Collections.shuffle(children);
	}

	/**
	 * When called after a call to shuffleChildren(), restore the
	 * saved previous state of the Vertex's children list
	 */
	public void unshuffleChildren() {
		if (previousChildren != null) {
			children = previousChildren;
		}
	}


	/**
	 * Suborder getter.
	 * @return the suborder of the vertex
	 */
	public int getSuborder() {
		return suborder;
	}

	/**
	 * Depth getter.
	 * @return the depth of the vertex
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * Updates the depth and suborder of all the subtree rooted in
	 * the vertex, and returns the maximum depth of the vertices of
	 * this subtree. To do so :<ul>
	 * <li> changes its depth to the specified one ;</li>
	 * <li> reverberates the change recursively on its children ;</li> 
	 * <li> updates its suborder (according to its children's) ;</li>
	 * <li> compute the maximum depth of the subtree (according to its
	 * children's too).</li>
	 * </ul>
	 * @param depth the new depth of the vertex
	 * @return the maximum depth of the vertices of the subtree rooted
	 *         in the vertex
	 */ 
	public int updateDepthAndSuborder(int depth) {
		this.depth = depth;
		int maxDepth = depth;
		this.suborder = 0;
		if (children.size() == 0) {
			this.suborder++;
		}
		for (Vertex child : children) {
			int childMaxDepth = child.updateDepthAndSuborder(depth + 1); 
			maxDepth = Math.max(maxDepth, childMaxDepth);
			this.suborder += child.getSuborder();
		}
		return maxDepth;
	}

	/**
	 * Returns a String describing the Vertex
	 * @return a String describing the Vertex
	 */
	public String toString() {
		String res = String.format("Vertex %d(%s) :\n", id, label);
		res += String.format("Depth = %d, Suborder = %d\n", depth, suborder);
		res += "Neighbours ids : ";
		for (Vertex neighbour : getNeighbours()) {
			res += String.format("%d ", neighbour.getId());
		}
		res += "\nChildren ids : ";
		for (Vertex child : getChildren()) {
			res += String.format("%d ", child.getId());
		}
		res += "\n";
		return res;
	}
}