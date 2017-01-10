package fr.pyvain.websight.websight.SweetGraphs;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class ForestRadialLayoutTest {

    private ForestRadialLayout layout1;
    private ForestRadialLayout layout2;
    private Graph graph;
    private Rectangle projSpace;
    private Vertex otherRoot;

    @Before
    public void setUp() throws Exception {
        List<Vertex> vertices = new ArrayList<>();
        List<List<String>> vUrls = Arrays.asList(
                Arrays.asList("url0", "url1"),
                Collections.singletonList("url2"),
                Collections.singletonList("url3"),
                Collections.singletonList("url4"),
                Collections.<String>emptyList(),
                Collections.<String>emptyList(),
                Collections.singletonList("url11"),
                Arrays.asList("url5", "url11", "url2")
                );
        Vertex[] v = new Vertex[8];
        for (int i = 0; i < 8; i++) {
            Vertex vertex = new Vertex(i, "keyword" + i);
            vertex.getData().addURLs(vUrls.get(i));
            vertices.add(vertex);
            v[i] = vertex;
        }
        List<Edge> edges = new ArrayList<>();
        Vertex[] ends1 = new Vertex[]{v[0], v[0], v[0], v[1], v[2], v[2]};
        Vertex[] ends2 = new Vertex[]{v[1], v[2], v[3], v[5], v[6], v[7]};
        List<List<String>> eUrls = Arrays.asList(
                Arrays.asList("url5", "url6"),
                Collections.singletonList("url7"),
                Collections.singletonList("url8"),
                Collections.singletonList("url10"),
                Arrays.asList("url12", "url6"),
                Arrays.asList("url0", "url2", "url3")
                );
        for (int i = 0; i < 6; i++) {
            Edge e = new Edge(ends1[i], ends2[i]);
            e.getData().addURLs(eUrls.get(i));
            edges.add(e);
        }
        graph = new Graph(vertices, edges);
        layout1 = new ForestRadialLayout();
        layout2 = new ForestRadialLayout(graph, v[0]);
        projSpace = new Rectangle(new Point(200, 400), 400, 800);
        otherRoot = v[2];
    }

    @Test
    public void testMany() throws Exception {
        for (int i = 0; i < 20; i++) {
            Graph randomGraph = Graph.randomInputGraph(20, 20);
            new ForestRadialLayout(randomGraph, randomGraph.getVertices().first());
        }
    }

    @Test
    public void testChangeRoot() throws Exception {
        layout1.changeRoot(graph, otherRoot);
        layout2.changeRoot(graph, otherRoot);
    }

    @Test
    public void testGetRootId() throws Exception {
        assertEquals(-1, layout1.getRootId());
        assertEquals(0, layout2.getRootId());
    }

    @Test
    public void testProjectedVertices() throws Exception {
        assertEquals(new HashMap<Integer, Circle>(), layout1.projectedVertices(projSpace));
        Map<Integer, Circle> expected = new HashMap<>();
        expected.put(0, new Circle(new Point(200.000000f, 400.000000f), 18.181818f));
        expected.put(1, new Circle(new Point(230.303024f, 452.486389f), 15.151517f));
        expected.put(2, new Circle(new Point(147.513626f, 369.696960f), 18.181818f));
        expected.put(3, new Circle(new Point(252.486389f, 369.696960f), 12.121213f));
        expected.put(4, new Circle(new Point(18.181824f, 399.999969f), 9.090909f));
        expected.put(5, new Circle(new Point(260.606049f, 504.972778f), 10.606061f));
        expected.put(6, new Circle(new Point(82.918076f, 431.371979f), 13.636364f));
        expected.put(7, new Circle(new Point(168.628006f, 282.918091f), 16.666666f));
        assertEquals(expected, layout2.projectedVertices(projSpace));
    }

    @Test
    public void testProjectedEdges() throws Exception {
        assertEquals(new HashMap<Integer, Map<Integer, Segment>>(),
                layout1.projectedEdges(projSpace));
        Map<Integer, Map<Integer, Segment>> expected = new HashMap<>();
        expected.put(0, new HashMap<Integer, Segment>());
        expected.put(1, new HashMap<Integer, Segment>());
        expected.put(2, new HashMap<Integer, Segment>());
        expected.get(0).put(2, new Segment(new Point(184.254089f, 390.909088f), new Point(163.259537f,378.787872f), 0.606061f));
        expected.get(0).put(3, new Segment(new Point(215.745911f, 390.909088f), new Point(241.989105f,375.757568f), 0.606061f));
        expected.get(0).put(1, new Segment(new Point(209.090912f, 415.745911f), new Point(222.727264f,439.364807f), 1.818182f));
        expected.get(2).put(7, new Segment(new Point(151.812073f, 352.030548f), new Point(164.687744f,299.112274f), 3.030303f));
        expected.get(1).put(5, new Segment(new Point(237.878784f, 465.608002f), new Point(255.303024f,495.787659f), 0.606061f));
        expected.get(2).put(6, new Segment(new Point(134.363297f, 382.252747f), new Point(92.780823f,421.955170f), 1.818182f));
        assertEquals(expected, layout2.projectedEdges(projSpace));
    }

    @Test
    public void testProjectedOrbits() throws Exception {
        assertEquals(new ArrayList<Circle>(), layout1.projectedOrbits(projSpace));
        List<Circle> expected = new ArrayList<>();
        expected.add(new Circle(new Point(200.000000f, 400.000000f), 60.606060f));
        expected.add(new Circle(new Point(200.000000f, 400.000000f), 121.212120f));
        expected.add(new Circle(new Point(200.000000f, 400.000000f), 181.818176f));
        assertEquals(expected, layout2.projectedOrbits(projSpace));
    }
}