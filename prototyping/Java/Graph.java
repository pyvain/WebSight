import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Queue;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.lang.Math;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * <p>A 'personal data graph' represents the online persona of the 
 * user.</p>
 *
 * <p>Each of its vertex represents a textual personal data, that the user
 * gave to the application, in order for it to be tracked, or to help
 * track other data.</p>
 *
 * <p>Each edge of the graph represent a bond between the two pieces of data
 * it connects. Two pieces are bound if at least one online page containing 
 * both of them have been found by the server search engine.</p>
 *
 * <p>To make easier the computation of a radial layout of the graph,
 * a spanning tree structure comes on top of each of its connected component.
 * For that, a certain vertex of each component is designated root of
 * this component, then the spanning tree of the component is obtained
 * by performing a Breadth First Search from the root.</p>
 *
 * <p>One of the root is the main root, and corresponds to a data that is
 * placed at the center of the layoyt. As a personal data graph is meant 
 * to be user centric, the default main root should be the name of the user. 
 * But it is possible for the user to change this root.
 * In fact, all roots are computed based on the main root iteratively :
 * the i-th root is the vertex of smallest id among the vertices not
 * included in the i-1 first spanning trees.</p>
 *
 * <p>By definition, the spanning trees include all the vertices of the 
 * graph, but not necessarily all the edges. The remaining edges are called
 * "extra edges", and are actually edges that create cycles in the graph.</p>
 *
 * <p>A personal data graph can be built from a json encoded graph
 * received from the server.</p>
 *
 * <p>
 * @Author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public final class Graph {

	/**
	 * List of the vertices of the graph.
	 * The position of a vertex in this list is its identifier
	 */
	private final ArrayList<Vertex> vertices;

	/**
	 * List of the edges of the graph.
	 * The position of an edge in this list is its identifier
	 * Two edges sould not have the same end vertices.
	 */
	private final ArrayList<Edge> edges;

	/**
	 * List of the current roots of the graph, with main root first
	 * and others sorted by increasing id
	 */
	private ArrayList<Vertex> roots;

	/**
	 * Set of the current extra edges of the graph.
	 * Note : any edge of extraEdges is also in edges.
	 */
	private HashSet<Edge> extraEdges;

	/**
	 * Private auxiliary initialization method.
	 * Returns a set of URLs built from the specified JSONArrays 
	 * @param a JSONArray containing all the URLs
	 *        ["url1", "url2", ..., "urlM"], 
	 * @param a JSONArray containing URL ids giving a set of URL ids for each vertex
	 *        [url_id_1, ..., _url_id_Z]
	 * @throw JSONException if the input is invalid
	 */
	private Set<String> getURLsFromIds(JSONArray urls, JSONArray urlIds)
	throws JSONException {
		Set<String> urlsSet = new HashSet<String>();
		for (int j = 0; j < urlIds.length(); j++) {
			int id = urlIds.getInt(j);
			urlsSet.add(urls.getString(id));
		}
		return urlsSet;
	}

	/**
	 * Private initialization auxiliary method.
	 * Fills the list of vertices according to the specified JSONArrays 
	 * @param a JSONArray desribing all the urls, of format
	 *        ["url1", "url2", ..., "urlM"], 
	 * @param a JSONArray describing the vertices, of format 
	 *        [
	 *           {"kw":"keyword1", "url_ids":[vertex1_url_id_1, ..., vertex1_url_id_X]},
	 *           ...,
	 *           {"kw":"keywordN", "url_ids":[vertexN_url_id_1, ..., vertexN_url_id_Y]},
	 *        ]
	 * @throw JSONException if the input is invalid
	 */
	private void initVertices(JSONArray urls, JSONArray jsonVertices) 
	throws JSONException {
		for (int id = 0; id < jsonVertices.length(); id++) {
			JSONObject jsonVertex = jsonVertices.getJSONObject(id);
			JSONArray urlIds = jsonVertex.getJSONArray("url_ids");
			Set<String> urlsSet = getURLsFromIds(urls, urlIds); 
			String keyword = jsonVertex.getString("kw");
			Vertex vertex = new Vertex(id, keyword);
			vertex.getData().addURLs(urlsSet);
			vertices.add(vertex);
		}
	}

	/**
	 * Private initialization auxiliary method.
	 * Fills the list of edges according to the specified JSONArrays 
 	 * @param a JSONArray containing all the URLs
	 *        ["url1", "url2", ..., "urlM"], 
	 * @param a JSONArray describing the edges, of format 
	 *        [
	 *	          {
	 *               "src": edge1_vertex_id_1, 
	 *               "dst": edge1_vertex_id_2,
	 *               "url_ids":[edge1_url_id_1, ..., edge1_url_id_Z]
	 *            },
	 *            ...,
     *            {
	 *               "src":edgeP_vertex_id_1, 
	 *               "dst":edgeP_vertex_id_2,
	 *               "url_ids":[edgeP_url_id_1, ..., edgeP_url_id_W]
	 *            }
	 *        ]
	 * @throw JSONException if the input is invalid
	 */
	private void initEdges(JSONArray urls, JSONArray jsonEdges)  
	throws JSONException {
		// Used to remember which pairs of vertices already have an 
		// edge between them. 
		HashMap<Edge, Edge> edgeFinder = new HashMap<Edge, Edge>();
		for (int i = 0; i < jsonEdges.length(); i++) {
			JSONObject jsonEdge = jsonEdges.getJSONObject(i);
			Vertex end1 = vertices.get(jsonEdge.getInt("src"));
			Vertex end2 = vertices.get(jsonEdge.getInt("dst"));
			Edge newEdge = new Edge(edges.size(), end1, end2);
			Edge existingEdge = edgeFinder.get(newEdge);
			// If no edge with same ends is already in the graph,
			// add the new edge to the edge list.
			if (existingEdge == null) {
				edges.add(newEdge);
				edgeFinder.put(newEdge, newEdge);
				existingEdge = newEdge;
			}
			// Adds URLS to the edge
			JSONArray urlIds = jsonEdge.getJSONArray("url_ids");
			Set<String> urlsSet = getURLsFromIds(urls, urlIds);
			existingEdge.getData().addURLs(urlsSet);
			// Update URLS and neighbours of both ends of the edge
			end1.addNeighbourIfAbsent(end2, existingEdge);
			end2.addNeighbourIfAbsent(end1, existingEdge);
			end1.getData().addURLs(urlsSet);
			end2.getData().addURLs(urlsSet);
		}
	}

	/**
	 * Initializes a new graph from a json encoded graph received
	 * from the server.
	 *
	 * @param graph a graph encoded with the following JSON format 
	 *        [ 
	 *            ["vertex1_kwd", ..., "vertexN_kwd"], 
     *            ["url1", "url2", ..., "urlM"],
     *            ["advice1", "advice2", ..., "adviceM"], 
     *            [
	 *                [vertex1_url_id_1, ..., vertex1_url_id_Z],
	 *                ...,
	 *                [vertexN_url_id_1, ..., vertexN_url_id_W]
	 *            ],
	 *            [
	 *                [
	 *                     edge1_vertex_id_1, 
	 *                     edge1_vertex_id_2,
	 *                     [edge1_url_id_1, ..., edge1_url_id_X]
	 *                ],
	 *                ...,
	 *                [
	 *                     edgeP_vertex_id_1, 
	 *                     edgeP_vertex_id_2,
	 *                     [edgeP_url_id_1, ..., edgeP_url_id_X]
	 *                ]
	 *            ]
	 *        ]
	 * @throw IllegalArgumentException if graph is not valid
	 * 
	 */
	public Graph(String graph) throws IllegalArgumentException {
		this.vertices = new ArrayList<Vertex>();
		this.edges = new ArrayList<Edge>();
		this.roots = new ArrayList<Vertex>();
		this.extraEdges = new HashSet<Edge>();
		try {
			JSONObject root = new JSONObject(graph);
			JSONArray jsonURLs = root.getJSONArray("urls");
			JSONArray jsonAdvice = root.getJSONArray("advice");
			JSONArray jsonVertices = root.getJSONArray("vertices");
			JSONArray jsonEdges = root.getJSONArray("edges");
			initVertices(jsonURLs, jsonVertices);
			initEdges(jsonURLs, jsonEdges);
		} catch (JSONException e) {
			throw new IllegalArgumentException("Invalid JSON graph.");
		}
		// Initialize the spanning trees of the graph
		changeRoot(vertices.get(0));
	}

	/**
	 * Vertices getter.
	 * @return a read only list of this graph's vertices
	 */
	public List<Vertex> getVertices() {
		return Collections.unmodifiableList(vertices);
	}

	/**
	 * Edges getter.
	 * @return a read only list of this graph's edges
	 */
	public List<Edge> getEdges() {
		return Collections.unmodifiableList(edges);
	}

	/**
	 * Roots getter.
	 * @return a read only list of this graph's current roots
	 */
	public List<Vertex> getRoots() {
		return Collections.unmodifiableList(roots);
	}

	/**
	 * Sets the Vertex of specified id as the new main root, and updates
	 * the spanning trees of the graph.
	 * @param root The new root of the graph 
	 */
	public void changeRoot(Vertex mainRoot) {
		roots = new ArrayList<Vertex>();
		extraEdges = new HashSet<Edge>(edges);
		// This list contains the new main root first, then other 
		// vertices sorted by increasing indices, allowing to get the
		// non explored vertex of smallest id in O(1) time 
		LinkedList<Vertex> notExplored = new LinkedList<Vertex>(vertices);
		notExplored.remove(mainRoot);
		notExplored.addFirst(mainRoot);
		// This array allows to determine in O(1) time if a vertex has
		// been explored yet
		boolean[] explored = new boolean[vertices.size()];
		Arrays.fill(explored, false);
		// Performs BFS from non explored vertex of smallest id until
		// all vertices have been explored
		while (! notExplored.isEmpty()) {
			// The unexplored Vertex of smallest id becomes a root
			Vertex root = notExplored.remove();
			explored[root.getId()] = true;
			roots.add(root);
			// Performs a BFS from it, updating its children
			Queue<Vertex> queue = new LinkedList<Vertex>();
			queue.add(root);
			Vertex current = null;
			while ((current = queue.poll()) != null) {
				current.clearChildren();
				for (Vertex neighbour : current.getNeighbours()) {
					if (!explored[neighbour.getId()]) {
						explored[neighbour.getId()] = true;
						notExplored.remove(neighbour);
						current.addChild(neighbour);
						extraEdges.remove(current.getEdgeTo(neighbour));
						queue.add(neighbour);
					}
				}
			}
		}
		// Updates depth and suborder of all vertices
		int mainMaxDepth = roots.get(0).updateDepthAndSuborder(0);
		for (int i = 1; i < roots.size(); i++) {
			roots.get(i).updateDepthAndSuborder(mainMaxDepth+1);
		}
	}

	/**
	 * Extra edges getter.
	 * @return a read only set of this graph's current extra edges
	 */
	public Set<Edge> getExtraEdges() {
		return Collections.unmodifiableSet(extraEdges);
	}
}