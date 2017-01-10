package fr.pyvain.websight.websight.PersonalDataGraph;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import fr.pyvain.websight.websight.Geometry.CPoint;
import fr.pyvain.websight.websight.Geometry.Segment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * <p>
 *     @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */

public class TestDrawing {

    @BeforeClass
    public static void beforeTests() {
        System.out.println("Testing class Drawing\n");
    }

    private static final float EPS = 0.001f;

    private InputGraph testingGraph() {
        // Builds the graph
        List<Vertex> iVertices = new ArrayList<>();
        Vertex[] v = new Vertex[3];
        for (int i = 0; i < 3; i++) {
            Vertex vertex = new Vertex(i, "keyword" + i);
            iVertices.add(vertex);
            v[i] = vertex;
        }
        List<Edge> iEdges = new ArrayList<>();
        Vertex[] ends1 = new Vertex[]{v[0], v[1]};
        Vertex[] ends2 = new Vertex[]{v[1], v[2]};
        for (int i = 0; i < 2; i++) {
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
    public void testDrawing() {
        InputGraph graph = testingGraph();
        Vertex[] v = new Vertex[3];
        SortedSet<Vertex> vertices = graph.getVertices();
        int i = 0;
        for (Vertex vertex : vertices) {
            v[i++] = vertex;
        }
        Tree t = new Tree(v[0]);
        RadialLayout l = new RadialLayout(t, 0, 0f, (float) (2 * Math.PI));
        Drawing d = new Drawing(t, l, l.getMaxVertexRadius()/2,
                0, 0, 1000, 1000, 500, 500);
        // checks vertex radius
        assertEquals(50, d.getVertexRadius(), EPS);
        // checks unit
        assertEquals(200, d.getUnit(), EPS);
        // checks centers
        Map<Vertex, CPoint> centers = d.getCenters();
        assertEquals(3, centers.size());
        assertEquals(new CPoint(500, 500), centers.get(v[0]));
        assertEquals(new CPoint(300, 500), centers.get(v[1]));
        assertEquals(new CPoint(100, 500), centers.get(v[2]));
        // checks segments
        Map<Edge, Segment> segments = d.getSegments();
        assertEquals(2, segments.size());
        assertEquals(new Segment(new CPoint(350, 500), new CPoint(450, 500)),
                segments.get(v[0].getEdgeTo(v[1])));
        assertEquals(new Segment(new CPoint(150, 500), new CPoint(250, 500)),
                segments.get(v[1].getEdgeTo(v[2])));
    }

    @Test
    public void testVertexAt() {
        InputGraph graph = testingGraph();
        Vertex[] v = new Vertex[3];
        SortedSet<Vertex> vertices = graph.getVertices();
        int i = 0;
        for (Vertex vertex : vertices) {
            v[i++] = vertex;
        }
        Tree t = new Tree(v[0]);
        RadialLayout l = new RadialLayout(t, 0, 0f, (float) (2 * Math.PI));
        Drawing d = new Drawing(t, l, l.getMaxVertexRadius()/2,
                0, 0, 1000, 1000, 500, 500);
        // in with perfect precision
        assertEquals(v[0], d.vertexAt(new CPoint(500, 500), 0));
        assertEquals(v[1], d.vertexAt(new CPoint(300, 500), 0));
        assertEquals(v[2], d.vertexAt(new CPoint(100, 500), 0));
        // edge with perfect precision
        assertEquals(v[0], d.vertexAt(new CPoint(550, 500), 0));
        assertEquals(v[0], d.vertexAt(new CPoint(535, 535), 0));
        assertEquals(v[1], d.vertexAt(new CPoint(300, 550), 0));
        assertEquals(v[1], d.vertexAt(new CPoint(265, 465), 0));
        assertEquals(v[2], d.vertexAt(new CPoint(100, 450), 0));
        assertEquals(v[2], d.vertexAt(new CPoint(65, 535), 0));
        // out with perfect precision
        assertEquals(null, d.vertexAt(new CPoint(551, 500), 0));
        assertEquals(null, d.vertexAt(new CPoint(536, 536), 0));
        assertEquals(null, d.vertexAt(new CPoint(300, 551), 0));
        assertEquals(null, d.vertexAt(new CPoint(264, 464), 0));
        assertEquals(null, d.vertexAt(new CPoint(100, 449), 0));
        assertEquals(null, d.vertexAt(new CPoint(64, 536), 0));
        // in with given precision
        assertEquals(v[0], d.vertexAt(new CPoint(551, 500), 1));
        assertEquals(v[0], d.vertexAt(new CPoint(536, 536), 1));
        assertEquals(v[1], d.vertexAt(new CPoint(300, 551), 1));
        assertEquals(v[1], d.vertexAt(new CPoint(264, 464), 1));
        assertEquals(v[2], d.vertexAt(new CPoint(100, 449), 1));
        assertEquals(v[2], d.vertexAt(new CPoint(64, 536), 1));
        // empty zone
        assertEquals(null, d.vertexAt(new CPoint(0, 100), 10));
        assertEquals(null, d.vertexAt(new CPoint(1000, 1000), 100));
        assertEquals(null, d.vertexAt(new CPoint(700, 500), 100));
    }

    @Test
    public void testEdgeAt() {
        InputGraph graph = testingGraph();
        SortedSet<Vertex> vertices = graph.getVertices();
        Vertex[] v = new Vertex[3];
        int i = 0;
        for (Vertex vertex : vertices) {
            v[i++] = vertex;
        }
        Edge[] e = new Edge[2];
        e[0] = v[0].getEdgeTo(v[1]);
        e[1] = v[1].getEdgeTo(v[2]);

        Tree t = new Tree(v[0]);
        RadialLayout l = new RadialLayout(t, 0, 0f, (float) (2 * Math.PI));
        Drawing d = new Drawing(t, l, l.getMaxVertexRadius()/2,
                0, 0, 1000, 1000, 500, 500);
        // in with perfect precision
        assertEquals(e[0], d.edgeAt(new CPoint(400, 500), 0));
        assertEquals(e[1], d.edgeAt(new CPoint(200, 500), 0));
        // rim with perfect precision
        assertEquals(e[0], d.edgeAt(new CPoint(350, 500), 0));
        assertEquals(e[1], d.edgeAt(new CPoint(250, 500), 0));
        // out with perfect precision
        assertEquals(null, d.edgeAt(new CPoint(400, 501), 0));
        assertEquals(null, d.edgeAt(new CPoint(349, 500), 0));
        assertEquals(null, d.edgeAt(new CPoint(200, 501), 0));
        assertEquals(null, d.edgeAt(new CPoint(251, 500), 0));
        // in with given precision
        assertEquals(e[0], d.edgeAt(new CPoint(400, 501), 1));
        assertEquals(e[0], d.edgeAt(new CPoint(349, 500), 1));
        assertEquals(e[1], d.edgeAt(new CPoint(200, 501), 1));
        assertEquals(e[1], d.edgeAt(new CPoint(251, 500), 1));
        // empty zone
        assertEquals(null, d.edgeAt(new CPoint(0, 100), 10));
        assertEquals(null, d.edgeAt(new CPoint(1000, 1000), 100));
        assertEquals(null, d.edgeAt(new CPoint(700, 500), 100));
    }
}