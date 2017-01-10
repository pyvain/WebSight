package fr.pyvain.websight.websight.PersonalDataGraph;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

/**
 * <p>
 *     @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */

public class TestRadialLayout {

    private static final float EPS = 0.01f;

	@BeforeClass
	public static void beforeTests() {
		System.out.println("Testing class RadialLayout\n");
	}

	@Test
	public void testRadialLayoutTree() {
        // Builds the graph
        List<Vertex> vertices = new ArrayList<>();
        Vertex[] v = new Vertex[8];
        for (int i = 0; i < 8; i++) {
            Vertex vertex = new Vertex(i, "keyword" + i);
            vertices.add(vertex);
            v[i] = vertex;
        }
        List<Edge> edges = new ArrayList<>();
        Vertex[] ends1 = new Vertex[]{v[0], v[0], v[0], v[0], v[1], v[2], v[2]};
        Vertex[] ends2 = new Vertex[]{v[1], v[2], v[3], v[4], v[5], v[6], v[7]};
        for (int i = 0; i < 7; i++) {
            Edge e = new Edge(ends1[i], ends2[i]);
            edges.add(e);
        }
        try {
            new InputGraph(vertices, edges);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        Tree t = new Tree(v[0]);

        // Constructor
        RadialLayout l = new RadialLayout(t, 0, 0f, (float) (2 * Math.PI));
        // getMaxVertexRadius()
        assertEquals(Math.tan(Math.PI/7), l.getMaxVertexRadius(), EPS);
        // getLayoutRadius()
        assertEquals(2 + l.getMaxVertexRadius(), l.getLayoutRadius(), EPS);
        // getPolarCoords()
        int[] radius = new int[]{0, 1, 1, 1, 1, 2, 2, 2};
        double[] angle = new double[]{Math.PI, 2*Math.PI/7, Math.PI, 11*Math.PI/7,
                13*Math.PI/7, 2*Math.PI/7, 11*Math.PI/14, 17*Math.PI/14};
        for (int i = 0; i < 8; i++) {
            assertEquals(radius[i], l.getPolarCoords(i).getRadius());
            assertEquals(angle[i], l.getPolarCoords(i).getAngle(), EPS);
        }
    }

    @Test
    public void testUpdateTree() {
        // Builds the graph
        List<Vertex> vertices = new ArrayList<>();
        Vertex[] v = new Vertex[5];
        for (int i = 0; i < 5; i++) {
            Vertex vertex = new Vertex(i, "keyword" + i);
            vertices.add(vertex);
            v[i] = vertex;
        }
        List<Edge> edges = new ArrayList<>();
        Vertex[] ends1 = new Vertex[]{v[0], v[0], v[0], v[0]};
        Vertex[] ends2 = new Vertex[]{v[1], v[2], v[3], v[4]};
        for (int i = 0; i < 4; i++) {
            Edge e = new Edge(ends1[i], ends2[i]);
            edges.add(e);
        }
        try {
            new InputGraph(vertices, edges);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        Tree t = new Tree(v[0]);

        RadialLayout l = new RadialLayout(t, 0, 0f, (float)(2*Math.PI));
        t.changeRoot(v[1]);
        l.update(t, 0, 0f, (float)(2*Math.PI));

        // getMaxVertexRadius()
        assertEquals(0.5f, l.getMaxVertexRadius(), EPS);
        // getLayoutRadius()
        assertEquals(2 + l.getMaxVertexRadius(), l.getLayoutRadius(), EPS);
        // getPolarCoords()
        int[] radius = new int[]{1, 0, 2, 2, 2};
        double[] angle = new double[]{Math.PI, Math.PI, Math.PI/3, Math.PI, 5*Math.PI/3};
        for (int i = 0; i < 5; i++) {
            assertEquals(radius[i], l.getPolarCoords(i).getRadius());
            assertEquals(angle[i], l.getPolarCoords(i).getAngle(), EPS);
        }
    }

    private InputGraph testingGraphForest() {
        List<Vertex> iVertices = new ArrayList<>();
        Vertex[] v = new Vertex[8];
        for (int i = 0; i < 8; i++) {
            Vertex vertex = new Vertex(i, "keyword" + i);
            iVertices.add(vertex);
            v[i] = vertex;
        }
        List<Edge> iEdges = new ArrayList<>();
        Vertex[] ends1 = new Vertex[]{v[0], v[0], v[0], v[4], v[4]};
        Vertex[] ends2 = new Vertex[]{v[1], v[2], v[3], v[5], v[6]};
        for (int i = 0; i < 5; i++) {
            Edge e = new Edge(ends1[i], ends2[i]);
            iEdges.add(e);
        }
        InputGraph graph = null;
        try {
            graph = new InputGraph(iVertices, iEdges);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return graph;
    }

    @Test
    public void testRadialLayoutForest() {
        InputGraph graph = testingGraphForest();

        SortedSet<Vertex> vertices = graph.getVertices();
        Vertex[] v = new Vertex[8];
        int i = 0;
        for (Vertex vertex : vertices) {
            v[i++] = vertex;
        }
        Forest forest = new Forest(graph, v[0]);
        RadialLayout l = new RadialLayout(forest);

        // getMaxVertexRadius()
        assertEquals(0.5f, l.getMaxVertexRadius(), EPS);
        // getLayoutRadius()
        assertEquals(3.5f, l.getLayoutRadius(), EPS);
        // getPolarCoords()
        int[] radius = new int[]{0, 1, 1, 1, 2, 3, 3, 2};
        double[] angle = new double[]{Math.PI, Math.PI/3, Math.PI, 5*Math.PI/3,
                3*Math.PI/4, 3*Math.PI/8, 9*Math.PI/8, 7*Math.PI/4};
        for (i = 0; i < 8; i++) {
            assertEquals(radius[i], l.getPolarCoords(i).getRadius());
            assertEquals(angle[i], l.getPolarCoords(i).getAngle(), EPS);
        }
    }

    @Test
    public void testUpdateForest() {
        InputGraph graph = testingGraphForest();
        SortedSet<Vertex> vertices = graph.getVertices();
        Vertex[] v = new Vertex[8];
        int i = 0;
        for (Vertex vertex : vertices) {
            v[i++] = vertex;
        }
        Forest forest = new Forest(graph, v[0]);
        RadialLayout l = new RadialLayout(forest);
        forest.setTreeAsMain(v[4]);
        l.update(forest);


        // getMaxVertexRadius()
        assertEquals(0.5f, l.getMaxVertexRadius(), EPS);
        // getLayoutRadius()
        assertEquals(3.5f, l.getLayoutRadius(), EPS);
        // getPolarCoords()
        int[] radius = new int[]{2, 3, 3, 3, 0, 1, 1, 2};
        double[] angle = new double[]{4*Math.PI/5, 4*Math.PI/15, 12*Math.PI/15, 20*Math.PI/15,
                Math.PI, Math.PI/2, 3*Math.PI/2, 9*Math.PI/5};
        for (i = 0; i < 8; i++) {
            assertEquals(radius[i], l.getPolarCoords(i).getRadius());
            assertEquals(angle[i], l.getPolarCoords(i).getAngle(), EPS);
        }
    }

    @Test
    public void testEqualHashCode() {
        InputGraph graph = testingGraphForest();
        SortedSet<Vertex> vertices = graph.getVertices();
        Vertex[] v = new Vertex[8];
        int i = 0;
        for (Vertex vertex : vertices) {
            v[i++] = vertex;
        }
        Forest forest0 = new Forest(graph, v[0]);
        Forest forest0bis = new Forest(graph, v[0]);
        Forest forest1 = new Forest(graph, v[1]);
        RadialLayout l0 = new RadialLayout(forest0);
        RadialLayout l0bis = new RadialLayout(forest0bis);
        RadialLayout l1 = new RadialLayout(forest1);

        assertEquals(l0, l0bis);
        assertEquals(l0.hashCode(), l0bis.hashCode());
        assertEquals(l0bis, l0);
        assertEquals(l0bis.hashCode(), l0.hashCode());
        assertNotEquals(l0, l1);
        assertNotEquals(l1, l0);
    }
}