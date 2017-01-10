package fr.pyvain.websight.websight.SweetGraphs;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class VertexTest {

    private int id;
    private String label;
    private Vertex v;


    @Before
    public void setUp() throws Exception {
        id = 42;
        label = "randomLabel";
        v = new Vertex(id, label);
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals(id, v.getId());
    }

    @Test
    public void testGetLabel() throws Exception {
        assertEquals(label, v.getLabel());
    }

    @Test
    public void testGetData() throws Exception {
        assertEquals(new DataSet(), v.getData());
    }

    @Test
    public void testGetNeighbours() throws Exception {
        assertEquals(new HashSet<Vertex>(), v.getNeighbours());
    }

    @Test
    public void testGetEdgeTo() throws Exception {
        assertEquals(null, v.getEdgeTo(v));
    }

    @Test
    public void testAddNeighbour() throws Exception {
        Vertex v1 = new Vertex(1, "kwd1");
        Vertex v2 = new Vertex(2, "kwd2");
        // First absent add
        Edge e01 = new Edge(v, v1);
        v.addNeighbour(v1, e01);
        Set<Vertex> neighbours = v.getNeighbours();
        assertEquals(1, neighbours.size());
        assertTrue(neighbours.contains(v1));
        assertSame(e01, v.getEdgeTo(v1));
        // Add already present
        v.addNeighbour(v1, e01);
        neighbours = v.getNeighbours();
        assertEquals(1, neighbours.size());
        assertTrue(neighbours.contains(v1));
        assertSame(e01, v.getEdgeTo(v1));
        // Second absent add
        Edge e02 = new Edge(v, v2);
        v.addNeighbour(v2, e02);
        neighbours = v.getNeighbours();
        assertEquals(2, neighbours.size());
        assertTrue(neighbours.contains(v1));
        assertSame(e01, v.getEdgeTo(v1));
        assertTrue(neighbours.contains(v2));
        assertSame(e02, v.getEdgeTo(v2));
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals(v, v);
        Vertex v1 = new Vertex(id, "mylabeliscool");
        assertEquals(v1, v);
        assertEquals(v, v1);
        Vertex v2 = new Vertex(314, "mylabelisgreat");
        assertNotEquals(v, v2);
        assertNotEquals(v2, v);
        v.addNeighbour(v2, new Edge(v, v2));
        assertEquals(v1, v);
        assertEquals(v, v1);
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(v.hashCode(), v.hashCode());
        Vertex v1 = new Vertex(id, "mylabeliscool");
        assertEquals(v1.hashCode(), v.hashCode());
        Vertex v2 = new Vertex(314, "mylabelisgreat");
        v.addNeighbour(v2, new Edge(v, v2));
        assertEquals(v1.hashCode(), v.hashCode());
    }

    @Test
    public void testCompareTo() throws Exception {
        Vertex v1 = new Vertex(id, "mylabeliscool");
        assertEquals(0, v.compareTo(v1));
        assertEquals(0, v1.compareTo(v));
        Vertex v2 = new Vertex(id+1, "abcedfgh");
        assertTrue(v.compareTo(v2) < 0);
        assertTrue(v2.compareTo(v) > 0);
        Vertex v3 = new Vertex(id-1, "abcedfgh");
        assertTrue(v.compareTo(v3) > 0);
        assertTrue(v3.compareTo(v) < 0);
        v.addNeighbour(v1, new Edge(v, v1));
        assertEquals(0, v.compareTo(v1));
        assertEquals(0, v1.compareTo(v));
        assertTrue(v.compareTo(v2) < 0);
        assertTrue(v2.compareTo(v) > 0);
        assertTrue(v.compareTo(v3) > 0);
        assertTrue(v3.compareTo(v) < 0);
    }
}