package fr.pyvain.websight.websight.SweetGraphs;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class GraphInstrumentedTest {

    Graph graph;

    @Before
    public void setUp() throws Exception {
        // Prepares the json
        String sGraph = ("{" +
                "\"urls\":[" +
                "\"url0\", \"url1\", \"url2\", \"url3\", \"url4\", " +
                "\"url5\", \"url6\", \"url7\", \"url8\", \"url9\", " +
                "\"url10\", \"url11\"]," +
                "\"vertices\":[" +
                "{\"kw\":\"label0\", \"url_ids\":[0, 1]}," +
                "{\"kw\":\"label1\", \"url_ids\":[2]}," +
                "{\"kw\":\"label2\", \"url_ids\":[3]}," +
                "{\"kw\":\"label3\", \"url_ids\":[4]}," +
                "{\"kw\":\"label4\", \"url_ids\":[]}," +
                "{\"kw\":\"label5\", \"url_ids\":[]}," +
                "{\"kw\":\"label6\", \"url_ids\":[11]}]," +
                "\"edges\":[" +
                "{\"src\":0, \"dst\":1, \"url_ids\":[5,6]}," +
                "{\"src\":1, \"dst\":2, \"url_ids\":[7]}," +
                "{\"src\":1, \"dst\":3, \"url_ids\":[8]}," +
                "{\"src\":3, \"dst\":0, \"url_ids\":[9]}," +
                "{\"src\":4, \"dst\":5, \"url_ids\":[10]}]}");

        // Builds the graph
        graph = null;
        try {
            graph = new Graph(sGraph);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testVertices() {
        SortedSet<Vertex> vertices = graph.getVertices();
        assertEquals(7, vertices.size());
        Vertex[] v = new Vertex[7];
        int i = 0;
        for (Vertex vertex : vertices) {
            v[i++] = vertex;
        }
        // check vertices
        List<List<String>> urls = Arrays.asList(
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
            assertEquals("label"+i, v[i].getLabel());
            assertEquals(urls.get(i).size(), v[i].getData().getURLs().size());
            assertTrue(v[i].getData().getURLs().containsAll(urls.get(i)));
            assertEquals(neighbours.get(i).size(), v[i].getNeighbours().size());
            assertTrue(v[i].getNeighbours().containsAll(neighbours.get(i)));
        }
    }

    @Test
    public void testEdges() {
        SortedSet<Vertex> vertices = graph.getVertices();
        assertEquals(7, vertices.size());
        Vertex[] v = new Vertex[7];
        int i = 0;
        for (Vertex vertex : vertices) {
            v[i++] = vertex;
        }
        Set<Edge> edges = graph.getEdges();
        assertEquals(5, edges.size());
        List<List<String>> urls = Arrays.asList(
                Arrays.asList("url5", "url6"),
                Collections.singletonList("url7"),
                Collections.singletonList("url8"),
                Collections.singletonList("url9"),
                Collections.singletonList("url10"));
        List<Vertex> end1 = Arrays.asList(v[0], v[1], v[1], v[3], v[4]);
        List<Vertex> end2 = Arrays.asList(v[1], v[2], v[3], v[0], v[5]);
        Edge[] e = new Edge[5];
        for (i = 0; i < 5; i++) {
            e[i] = new Edge(end1.get(i), end2.get(i));
            e[i].getData().addURLs(urls.get(i));
        }
        assertEquals(e[0], v[0].getEdgeTo(v[1]));
        assertEquals(e[3], v[0].getEdgeTo(v[3]));
        assertEquals(e[0], v[1].getEdgeTo(v[0]));
        assertEquals(e[1], v[1].getEdgeTo(v[2]));
        assertEquals(e[2], v[1].getEdgeTo(v[3]));
        assertEquals(e[1], v[2].getEdgeTo(v[1]));
        assertEquals(e[3], v[3].getEdgeTo(v[0]));
        assertEquals(e[2], v[3].getEdgeTo(v[1]));
        assertEquals(e[4], v[4].getEdgeTo(v[5]));
        assertEquals(e[4], v[5].getEdgeTo(v[4]));
        assertTrue(graph.getEdges().containsAll(Arrays.asList(e)));
    }
}