package fr.pyvain.websight.websight.PersonalDataGraph;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * <p>This class represents a general undirected graph. Such a graph
 * can be built from a json encoded graph received from the server.</p>
 *
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public final class InputGraph implements Graph {

	/**
	 * Vertices of the graph.
	 * The position of a vertex in this list is its identifier
	 */
	private final SortedSet<Vertex> vertices;

	/**
	 * Edges of the graph.
	 * Two edges should not have the same end vertices.
	 */
	private final Set<Edge> edges;

    /**
     * Initializes a new empty input graph.
     */
    private InputGraph() {
        this.vertices = new TreeSet<>();
        this.edges = new HashSet<>();
    }

    /**
     * Initializes a new input graph containing specified vertices
     * and edges. Merges vertices which have the same ends. Adds
     * neighbours to the vertices according to the edges.
     *
     * All the ends of the specified edges must be among specified
     * vertices. The specified vertices must not already have
     * neighbours. The specified vertices must have consecutive,
     * increasing ids starting from 0.
     *
     * @param vertices Vertices of the graph
     * @param edges    Edges of the graph
     * @throws IllegalArgumentException if one of the 3 requirements above
     * is not met.
     */
    public InputGraph(List<Vertex> vertices, List<Edge> edges)
    throws IllegalArgumentException {
        this();
        init(vertices, edges);
    }

	/**
	 * Initializes a new graph from a json encoded graph received
	 * from the server.
	 *
     * @see InputGraph#parseVertices
     * @see InputGraph#parseEdges
	 * @param graph  A graph encoded with the following JSON format
     *               {
     *                "urls": ["url1", ..., "urlM"],
     *                "vertices": **JSON spec of the vertices, see parseVertices**,
     *                "edges": **JSON spec of the edges, see parseEdges**,
     *               }
     *
	 * @throws IllegalArgumentException if graph is not valid
	 */
	public InputGraph(String graph) throws IllegalArgumentException {
        this();
        System.out.println("GRAPH\n" + graph);
        try {
			JSONObject root = new JSONObject(graph);
			JSONArray urls = root.getJSONArray("urls");
			JSONArray vertices = root.getJSONArray("vertices");
			JSONArray edges = root.getJSONArray("edges");
            List<Vertex> vList = parseVertices(vertices, urls);
            List<Edge> eList = parseEdges(edges, urls, vList);
            init(vList, eList);
		} catch (JSONException e) {
			throw new IllegalArgumentException(
                    "Invalid JSON graph : " + e.getMessage());
		}
	}

    /**
     * Private auxiliary initialization method.
     * Fills edges and vertices, merging vertices which have the same ends and
     * adding neighbours to the vertices according to the edges.
     *
     * All the ends of the specified edges must be among specified
     * vertices. The specified vertices must not already have
     * neighbours. The specified vertices must have consecutive,
     * increasing ids starting from 0.
     *
     * @param vertices Vertices of the graph
     * @param edges    Edges of the graph
     * @throws IllegalArgumentException if one of the 3 requirements above
     * is not met.
     */
     private void init(List<Vertex> vertices, List<Edge> edges)
             throws IllegalArgumentException {
         for (Vertex v : vertices) {
             if ((v.getId() != this.vertices.size()) || (v.getNeighbours().size() != 0)) {
                 throw new IllegalArgumentException("Invalid ");
             } else {
                 this.vertices.add(v);
             }
         }
         HashMap<Edge, Edge> edgeFinder = new HashMap<>();
         for (Edge newEdge : edges) {
             Set<String> urls = newEdge.getData().getURLs();
             Edge edge = edgeFinder.get(newEdge);
             // Adds the new edge to the edge list.
             if (edge == null) {
                 this.edges.add(newEdge);
                 edgeFinder.put(newEdge, newEdge);
                 edge = newEdge;
                 // Or merge with existing edge
             } else {
                 edge.getData().addURLs(urls);
             }
             // Adds the edge's URLS to both ends, and sets them as neighbours
             edge.getEnd1().addNeighbourIfAbsent(edge.getEnd2(), edge);
             edge.getEnd2().addNeighbourIfAbsent(edge.getEnd1(), edge);
             edge.getEnd1().getData().addURLs(urls);
             edge.getEnd2().getData().addURLs(urls);
         }
     }

    /**
     * Private auxiliary initialization method.
     * Returns a set of URLs built from the specified JSONArrays
     *
     * @param urls    A JSONArray containing all the URLs
     *                ["url1", "url2", ..., "urlM"],
     * @param urlIds  A JSONArray containing URL ids giving a set of URL
     *                ids for each vertex [url_id_1, ..., _url_id_Z]
     * @throws JSONException if the input is invalid
     */
    private Set<String> getURLsFromIds(JSONArray urlIds, JSONArray urls)
            throws JSONException {
        Set<String> urlsSet = new HashSet<>();
        for (int j = 0; j < urlIds.length(); j++) {
            urlsSet.add(urls.getString(urlIds.getInt(j)));
        }
        return urlsSet;
    }

    /**
     * Private initialization auxiliary method.
     * Parses the specified JSONArrays, builds a Vertex list accordingly
     * and returns it.
     *
     * @param urls     JSONArray describing all the urls
     *                      ["url1", "url2", ..., "urlM"],
     * @param vertices JSON specification of vertices
     *                 [
     *                  {"kw":"keyword1",
     *                   "url_ids":[v0_url_id_0, ..., v0_url_id_X] },
     *                  ...,
     *                  {"kw":"keywordN",
     *                   "url_ids":[vN_url_id_0, ..., vN_url_id_Y]}
     *                 ]
     * @throws JSONException if the input is invalid
     * @return List of vertices as specified by the input JSONs
     */
    private List<Vertex> parseVertices(JSONArray vertices, JSONArray urls)
            throws JSONException {
        List<Vertex> result = new ArrayList<>(vertices.length());
        for (int id = 0; id < vertices.length(); id++) {
            JSONObject vertex = vertices.getJSONObject(id);
            Vertex v = new Vertex(id, vertex.getString("kw"));
            JSONArray urlIds = vertex.getJSONArray("url_ids");
            Set<String> urlsSet = getURLsFromIds(urlIds, urls);
            v.getData().addURLs(urlsSet);
            result.add(v);
        }
        return result;
    }

    /**
     * Private initialization auxiliary method.
     * Parses the specified JSONArrays, builds an Edge list accordingly
     * and returns it.
     * The vertices of the graph must be initialized before calling
     * this method.
     *
     * @param urls   A JSONArray containing all the URLs
     *               ["url1", "url2", ..., "urlM"],
     * @param edges  A JSONArray describing the edges, of format
     *               [
     *                 {
     *                   "src": e0_vertex_id_0,
     *                   "dst": e0_vertex_id_1,
     *                   "url_ids":[e0_url_id_0, ..., e0_url_id_Z]
     *                 },
     *                 ...,
     *                 {
     *                   "src": eP_vertex_id_0,
     *                   "dst": eP_vertex_id_1,
     *                   "url_ids":[eP_url_id_0, ..., eP_url_id_W]
     *                 }
     *               ]
     * @throws JSONException if the input is invalid
     */
    private List<Edge> parseEdges(JSONArray edges, JSONArray urls,
                                  List<Vertex> vertices) throws JSONException
    {
        List<Edge> result = new ArrayList<>(edges.length());
        for (int i = 0; i < edges.length(); i++) {
            JSONObject edge = edges.getJSONObject(i);
            Vertex end1 = vertices.get(edge.getInt("src"));
            Vertex end2 = vertices.get(edge.getInt("dst"));
            Edge e = new Edge(end1, end2);
            JSONArray urlIds = edge.getJSONArray("url_ids");
            Set<String> urlsSet = getURLsFromIds(urlIds, urls);
            e.getData().addURLs(urlsSet);
            result.add(e);
        }
        return result;
    }

	/**
	 * Vertices getter.
	 * @return a read only list of this graph's vertices
	 */
	public SortedSet<Vertex> getVertices() {
		return Collections.unmodifiableSortedSet(vertices);
	}

	/**
	 * Edges getter.
	 * @return a read only list of this graph's edges
	 */
	public Set<Edge> getEdges() {
		return Collections.unmodifiableSet(edges);
	}

    /**
     * Random data graph generator.
     * Builds a data graph which has the specified number of vertices
     * and edges, containing no urls.
     *
     * @param nbVertices number of vertices wanted
     * @param nbEdges    number of edges wanted
     * @return a random data graph which has the specified number of
     * vertices and edges
     * @throws IllegalArgumentException if the number of edges is to
     * high for the number of vertices (i.e. E > V*(V-1)/2)
     */
	public static InputGraph randomInputGraph(int nbVertices, int nbEdges)
			throws IllegalArgumentException {
		if (nbEdges > (nbVertices-1)*nbVertices/2) {
			throw new IllegalArgumentException("too many edges");
		}

		List<Vertex> vertices = new ArrayList<>();
		for (int i = 0; i < nbVertices; i++) {
			vertices.add(new Vertex(i, ("kw"+i)));
		}
		List<Edge> edges = new ArrayList<>();
        // Picks nbEdges different couples of vertices randomly,
		// by generating all couples of vertices, shuffling them and picking
        // the nbEdges first
		LinkedList<Integer> couples = new LinkedList<>();
		for (int i = 0; i < nbVertices; i++) {
			for (int j = i+1; j < nbVertices; j++) {
				couples.add(i+j*nbVertices);
			}
		}
		Collections.shuffle(couples);
		for (int i = 0; i < nbEdges; i++) {
			int couple = couples.poll();
            edges.add(new Edge(vertices.get(couple%nbVertices),
                    vertices.get(couple/nbVertices)));
		}
		return new InputGraph(vertices, edges);
	}
}