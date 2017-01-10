package fr.pyvain.websight.websight.PersonalDataGraph;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * <p>
 *     @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */

public class TestForest {

    @BeforeClass
    public static void beforeTests() {
        System.out.println("Testing class Forest\n");
    }

    private InputGraph testingGraph() {
        List<Vertex> vertices = new ArrayList<>();
        Vertex[] v = new Vertex[7];
        for (int i = 0; i < 7; i++) {
            Vertex vertex = new Vertex(i, "keyword" + i);
            vertices.add(vertex);
            v[i] = vertex;
        }

        List<Edge> edges = new ArrayList<>();
        Vertex[] ends1 = new Vertex[]{v[0], v[0], v[3], v[4], v[4], v[5]};
        Vertex[] ends2 = new Vertex[]{v[1], v[2], v[4], v[5], v[6], v[6]};
        for (int i = 0; i < 6; i++) {
            Edge e = new Edge(ends1[i], ends2[i]);
            edges.add(e);
        }

        // Builds the graph
        InputGraph graph = null;
        try {
            graph = new InputGraph(vertices, edges);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return graph;
    }

    @Test
    public void testForest() {
        InputGraph graph = testingGraph();
        SortedSet<Vertex> vertices = graph.getVertices();
        Vertex[] v = new Vertex[7];
        int i = 0;
        for (Vertex vertex : vertices) {
            v[i++] = vertex;
        }
        /// constructor
        Forest forest = new Forest(graph, v[3]);
        // getVertices()
        SortedSet<Vertex> forestVertices = forest.getVertices();
        assertTrue(forestVertices.containsAll(Arrays.asList(
                v[0], v[1], v[2], v[3], v[4], v[5], v[6])));
        // getEdges()
        Set<Edge> forestEdges = forest.getEdges();
        assertTrue(forestEdges.containsAll(Arrays.asList(
                new Edge(v[0], v[1]),
                new Edge(v[0], v[2]),
                new Edge(v[3], v[4]),
                new Edge(v[4], v[5]),
                new Edge(v[4], v[6]),
                new Edge(v[5], v[6]))));
        List<Tree> trees = forest.getTrees();
        // getTrees()
        assertEquals(trees, Arrays.asList(new Tree(v[3]), new Tree(v[0])));
        // getMainTree()
        assertSame(forest.getMainTree(), trees.get(0));
        // getTreeContaining()
        assertSame(forest.getMainTree(), forest.getTreeContaining(v[3]));
        // size()
        assertEquals(7, forest.size());
    }

    @Test
    public void testSetAsRoot() {
        InputGraph graph = testingGraph();
        SortedSet<Vertex> vertices = graph.getVertices();
        Vertex[] v = new Vertex[7];
        int i = 0;
        for (Vertex vertex : vertices) {
            v[i++] = vertex;
        }
        Forest forest = new Forest(graph, v[3]);
        forest.setAsRoot(v[6]);
        // getVertices()
        SortedSet<Vertex> forestVertices = forest.getVertices();
        assertTrue(forestVertices.containsAll(Arrays.asList(
                v[0], v[1], v[2], v[3], v[4], v[5], v[6])));
        // getEdges()
        Set<Edge> forestEdges = forest.getEdges();
        assertTrue(forestEdges.containsAll(Arrays.asList(
                new Edge(v[0], v[1]),
                new Edge(v[0], v[2]),
                new Edge(v[3], v[4]),
                new Edge(v[4], v[5]),
                new Edge(v[4], v[6]),
                new Edge(v[5], v[6]))));
        List<Tree> trees = forest.getTrees();
        // getTrees()
        assertEquals(trees, Arrays.asList(new Tree(v[6]), new Tree(v[0])));
        // getMainTree()
        assertSame(forest.getMainTree(), trees.get(0));
        // getTreeContaining()
        assertSame(forest.getMainTree(), forest.getTreeContaining(v[3]));
        // size()
        assertEquals(7, forest.size());
}

    @Test
    public void testSetTreeAsMain() {
        InputGraph graph = testingGraph();
        SortedSet<Vertex> vertices = graph.getVertices();
        Vertex[] v = new Vertex[7];
        int i = 0;
        for (Vertex vertex : vertices) {
            v[i++] = vertex;
        }
        Forest forest = new Forest(graph, v[3]);
        forest.setTreeAsMain(v[0]);
        // getVertices()
        SortedSet<Vertex> forestVertices = forest.getVertices();
        assertTrue(forestVertices.containsAll(Arrays.asList(
                v[0], v[1], v[2], v[3], v[4], v[5], v[6])));
        // getEdges()
        Set<Edge> forestEdges = forest.getEdges();
        assertTrue(forestEdges.containsAll(Arrays.asList(
                new Edge(v[0], v[1]),
                new Edge(v[0], v[2]),
                new Edge(v[3], v[4]),
                new Edge(v[4], v[5]),
                new Edge(v[4], v[6]),
                new Edge(v[5], v[6]))));
        List<Tree> trees = forest.getTrees();
        // getTrees()
        assertEquals(trees, Arrays.asList(new Tree(v[0]), new Tree(v[3])));
        // getMainTree()
        assertSame(forest.getMainTree(), trees.get(0));
        // getTreeContaining()
        assertSame(forest.getMainTree(), forest.getTreeContaining(v[0]));
        // size()
        assertEquals(7, forest.size());
    }

    @Test
    public void testEqual(){
        InputGraph graph = testingGraph();
        SortedSet<Vertex> vertices = graph.getVertices();
        Vertex[] v = new Vertex[7];
        int i = 0;
        for (Vertex vertex : vertices) {
            v[i++] = vertex;
        }
        Forest forest1 = new Forest(graph, v[0]);
        Forest forest1bis = new Forest(graph, v[0]);
        Forest forest2 = new Forest(graph, v[1]);
        assertEquals(forest1, forest1bis);
        assertEquals(forest1.hashCode(), forest1bis.hashCode());
        assertEquals(forest1bis, forest1);
        assertEquals(forest1bis.hashCode(), forest1.hashCode());
        assertNotEquals(forest1, forest2);
        assertNotEquals(forest2, forest1);
    }

    @Test
    public void testCopy(){
        InputGraph graph = testingGraph();
        SortedSet<Vertex> vertices = graph.getVertices();
        Vertex[] v = new Vertex[7];
        int i = 0;
        for (Vertex vertex : vertices) {
            v[i++] = vertex;
        }

        Forest forest1 = new Forest(graph, v[0]);
        Forest forest2 = new Forest(graph, v[1]);
        forest2.copy(forest1);
        assertEquals(forest1, forest2);
    }
}
