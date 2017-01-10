package fr.pyvain.websight.websight.PersonalDataGraph;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * <p>
 *     @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */

public class TestTree {

    @BeforeClass
    public static void beforeTests() {
        System.out.println("Testing class Tree\n");
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
    public void testTree() {
        InputGraph graph = testingGraph();
        SortedSet<Vertex> vertices = graph.getVertices();
        Vertex[] v = new Vertex[7];
        int i = 0;
        for (Vertex vertex : vertices) {
            v[i++] = vertex;
        }

        Tree tree = new Tree(v[3]);
        // getVertices()
        SortedSet<Vertex> treeVertices = tree.getVertices();
        assertEquals(4, treeVertices.size());
        assertTrue(treeVertices.containsAll(Arrays.asList(v[3], v[4], v[5], v[6])));
        // getEdges()
        Set<Edge> treeEdges = tree.getEdges();
        assertEquals(4, treeEdges.size());
        assertTrue(treeEdges.containsAll(Arrays.asList(
                new Edge(v[3], v[4]),
                new Edge(v[4], v[5]),
                new Edge(v[4], v[6]),
                new Edge(v[5], v[6]))));
        // getExtraEdges()
        Set<Edge> treeExtraEdges = tree.getExtraEdges();
        assertEquals(1, treeExtraEdges.size());
        assertTrue(treeExtraEdges.contains(new Edge(v[5], v[6])));
        // getChildren()
        List<List<Vertex>> children = Arrays.asList(
                null, null, null,
                Collections.singletonList(v[4]),
                Arrays.asList(v[5], v[6]),
                Collections.<Vertex>emptyList(),
                Collections.<Vertex>emptyList());
        Map<Vertex, List<Vertex>> mChildren = tree.getChildren();
        for (i = 0; i < 7; i++) {
            assertEquals(children.get(i), tree.getChildren(v[i]));
            assertEquals(children.get(i), mChildren.get(v[i]));
        }
        // getRoot()
        assertEquals(v[3], tree.getRoot());
        // getLastShuffled()
        assertNull(tree.getLastShuffled());
        // getPreShuffleOrder()
        assertNull(tree.getPreShuffleOrder());
        // getNbDescendants()
        Map<Vertex, Integer> mNbDescendants = tree.getNbDescendants();
        int[] nbDescendants = new int[]{-1, -1, -1, 3, 2, 0, 0};
        for (i = 0; i < 7; i++) {
            assertEquals(nbDescendants[i], tree.getNbDescendants(v[i]));
            if (nbDescendants[i] != -1) {
                assertEquals(nbDescendants[i], mNbDescendants.get(v[i]).intValue());
            } else {
                assertNull(mNbDescendants.get(v[i]));
            }
        }
        // checks height
        assertEquals(2, tree.getHeight());
        // size()
        assertEquals(4, tree.size());
    }

    @Test
    public void testTreeByCopy() {
        InputGraph graph = testingGraph();
        SortedSet<Vertex> vertices = graph.getVertices();
        Vertex[] v = new Vertex[7];
        int i = 0;
        for (Vertex vertex : vertices) {
            v[i++] = vertex;
        }
        Tree tree1 = new Tree(v[3]);
        Tree tree2 = new Tree(tree1);
        assertEquals(tree1, tree2);
    }

    @Test
    public void testShuffleChildren() {
        InputGraph graph = testingGraph();
        SortedSet<Vertex> vertices = graph.getVertices();
        Vertex[] v = new Vertex[7];
        int i = 0;
        for (Vertex vertex : vertices) {
            v[i++] = vertex;
        }
        Tree tree = new Tree(v[0]);

        // shuffleChildren()
        List<Vertex> before = new ArrayList<>(tree.getChildren(v[0]));
        tree.shuffleChildren(v[0]);
        List<Vertex> after = new ArrayList<>(tree.getChildren(v[0]));
        for (Vertex vertex : before) {
            assertTrue(after.contains(vertex));
        }
        // unshuffleChildren()
        tree.unshuffleChildren();
        List<Vertex> back = new ArrayList<>(tree.getChildren(v[0]));
        assertEquals(before, back);
    }

    @Test
    public void testStates() {
        InputGraph graph = testingGraph();
        SortedSet<Vertex> vertices = graph.getVertices();
        Vertex[] v = new Vertex[7];
        int i = 0;
        for (Vertex vertex : vertices) {
            v[i++] = vertex;
        }
        Tree tree = new Tree(v[0]);
        List<Vertex> before = new ArrayList<>(tree.getChildren(v[0]));
        tree.neighbourState();
        List<Vertex> after = new ArrayList<>(tree.getChildren(v[0]));
        for (Vertex vertex : before) {
            assertTrue(after.contains(vertex));
        }
        tree.previousState();
        List<Vertex> back = new ArrayList<>(tree.getChildren(v[0]));
        assertEquals(before, back);
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
        Tree tree1 = new Tree(v[0]);
        Tree tree1bis = new Tree(v[0]);
        Tree tree2 = new Tree(v[1]);

        assertEquals(tree1, tree1bis);
        assertEquals(tree1.hashCode(), tree1bis.hashCode());
        assertEquals(tree1bis, tree1);
        assertEquals(tree1bis.hashCode(), tree1.hashCode());
        assertNotEquals(tree1, tree2);
        assertNotEquals(tree2, tree1);
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

        Tree tree1 = new Tree(v[0]);
        Tree tree2 = new Tree(v[1]);
        tree2.copy(tree1);
        assertEquals(tree1, tree2);
    }
}