package fr.pyvain.websight.websight.SweetGraphs;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class GraphTest {

    private Graph graph;
    private List<Edge> edges;

    @Before
    public void setUp() throws Exception {
        List<Vertex> vertices = new ArrayList<>();
        Vertex[] v = new Vertex[7];
        List<List<String>> vUrls = Arrays.asList(
                Arrays.asList("url0", "url1"),
                Collections.singletonList("url2"),
                Collections.singletonList("url3"),
                Collections.singletonList("url4"),
                Collections.<String>emptyList(),
                Collections.<String>emptyList(),
                Collections.singletonList("url11"));
        for (int i = 0; i < 7; i++) {
            Vertex vertex = new Vertex(i, "label" + i);
            vertex.getData().addURLs(vUrls.get(i));
            vertices.add(vertex);
            v[i] = vertex;
        }

        edges = new ArrayList<>();
        Vertex[] ends1 = new Vertex[]{v[0], v[1], v[1], v[0], v[4]};
        Vertex[] ends2 = new Vertex[]{v[1], v[2], v[3], v[3], v[5]};
        List<List<String>> eUrls = Arrays.asList(
                Arrays.asList("url5", "url6"),
                Collections.singletonList("url7"),
                Collections.singletonList("url8"),
                Collections.singletonList("url9"),
                Collections.singletonList("url10"));
        for (int i = 0; i < 5; i++) {
            Edge e = new Edge(ends1[i], ends2[i]);
            e.getData().addURLs(eUrls.get(i));
            edges.add(e);
        }

        // Builds the graph
        graph =  new Graph(vertices, edges);
    }

    @Test
    public void testGetVertices() {
        SortedSet<Vertex> vertices = graph.getVertices();
        assertEquals(7, vertices.size());
        Vertex[] v = new Vertex[7];
        int i = 0;
        for (Vertex vertex : vertices) {
            v[i++] = vertex;
        }
        // check vertices
        List<List<String>> vUrls = Arrays.asList(
                Arrays.asList("url0", "url1", "url5", "url6", "url9"),
                Arrays.asList("url2", "url5", "url6", "url7", "url8"),
                Arrays.asList("url3", "url7"),
                Arrays.asList("url4", "url8", "url9"),
                Collections.singletonList("url10"),
                Collections.singletonList("url10"),
                Collections.singletonList("url11"));

        List<List<Vertex>> neighbours = Arrays.asList(
                Arrays.asList(v[1], v[3]),
                Arrays.asList(v[0], v[2], v[3]),
                Collections.singletonList(v[1]),
                Arrays.asList(v[0], v[1]),
                Collections.singletonList(v[5]),
                Collections.singletonList(v[4]),
                Collections.<Vertex>emptyList());

        for (i = 0; i < 7; i++) {
            assertEquals(i, v[i].getId());
            assertEquals("label" + i, v[i].getLabel());
            assertEquals(vUrls.get(i).size(), v[i].getData().getURLs().size());
            assertTrue(v[i].getData().getURLs().containsAll(vUrls.get(i)));
            assertEquals(neighbours.get(i).size(), v[i].getNeighbours().size());
            assertTrue(v[i].getNeighbours().containsAll(neighbours.get(i)));
        }
    }

    @Test
    public void testGetEdges() {
        Set<Edge> gEdges = graph.getEdges();
        assertEquals(5, gEdges.size());
        assertTrue(gEdges.containsAll(edges));
        Vertex[] v = new Vertex[7];
        int i = 0;
        for (Vertex vertex : graph.getVertices()) {
            v[i++] = vertex;
        }
        // checks edges
        assertEquals(edges.get(0), v[0].getEdgeTo(v[1]));
        assertEquals(edges.get(3), v[0].getEdgeTo(v[3]));
        assertEquals(edges.get(0), v[1].getEdgeTo(v[0]));
        assertEquals(edges.get(1), v[1].getEdgeTo(v[2]));
        assertEquals(edges.get(2), v[1].getEdgeTo(v[3]));
        assertEquals(edges.get(1), v[2].getEdgeTo(v[1]));
        assertEquals(edges.get(3), v[3].getEdgeTo(v[0]));
        assertEquals(edges.get(2), v[3].getEdgeTo(v[1]));
        assertEquals(edges.get(4), v[4].getEdgeTo(v[5]));
        assertEquals(edges.get(4), v[5].getEdgeTo(v[4]));
    }


    @Test
    public void testGetMinVertexSize() throws Exception {
        assertEquals(1, graph.getMinVertexSize());
    }

    @Test
    public void testGetMaxVertexSize() throws Exception {
        assertEquals(5, graph.getMaxVertexSize());
    }

    @Test
    public void testGetMinEdgeSize() throws Exception {
        assertEquals(1, graph.getMinEdgeSize());
    }

    @Test
    public void testGetMaxEdgeSize() throws Exception {
        assertEquals(2, graph.getMaxEdgeSize());
    }
}