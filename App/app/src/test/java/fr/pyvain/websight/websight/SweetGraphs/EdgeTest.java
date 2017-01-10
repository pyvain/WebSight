package fr.pyvain.websight.websight.SweetGraphs;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

/**
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class EdgeTest {

    private Vertex v0;
    private Vertex v1;
    private Edge e1;
    private Edge e2;

    @Before
    public void setUp() throws Exception {
        v0 = new Vertex(0, "label0");
        v1 = new Vertex(1, "label1");
        e1 = new Edge(v0, v1);
        e2 = new Edge(v1, v0);
    }

    @Test
    public void testGetEnd1() throws Exception {
        assertSame(v0, e1.getEnd1());
        assertSame(v0, e2.getEnd1());
    }

    @Test
    public void testGetEnd2() throws Exception {
        assertSame(v1, e1.getEnd2());
        assertSame(v1, e2.getEnd2());
    }

    @Test
    public void testGetData() throws Exception {
        assertEquals(new DataSet(), e1.getData());
        assertEquals(new DataSet(), e2.getData());
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals(e1, e1);
        assertEquals(e1, e2);
        assertEquals(e2, e1);
        Vertex v0bis = new Vertex(0, "otherlabel0");
        Vertex v1bis = new Vertex(1, "otherlabel1");
        Edge e1bis = new Edge(v0bis, v1bis);
        assertEquals(e1, e1bis);
        assertEquals(e1bis, e1);
        Vertex v2 = new Vertex(2, "kwd2");
        Edge e3 = new Edge(v0, v2);
        assertNotEquals(e1, e3);
        assertNotEquals(e3, e1);
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(e1.hashCode(), e1.hashCode());
        assertEquals(e1.hashCode(), e2.hashCode());
        Vertex v0bis = new Vertex(0, "otherlabel0");
        Vertex v1bis = new Vertex(1, "otherlabel1");
        Edge e1bis = new Edge(v0bis, v1bis);
        assertEquals(e1bis.hashCode(), e1.hashCode());
    }
}