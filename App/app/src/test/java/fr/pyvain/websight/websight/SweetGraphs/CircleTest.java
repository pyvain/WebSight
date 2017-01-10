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
public class CircleTest {

    private final static double EPS = 1e-10;

    private Point p;
    private float r;
    private Circle c;


    @Before
    public void setUp() throws Exception {
        p = new Point(3.1415f, 2.7183f);
        r = 1.7321f;
        c = new Circle(p, r);
    }

    @Test
    public void testGetCenter() throws Exception {
        assertSame(p, c.getCenter());
    }

    @Test
    public void testSetCenter() throws Exception {
        Point p2 = new Point(1.4142f, 1.6180f);
        c.setCenter(p2);
        assertSame(p2, c.getCenter());
    }

    @Test
    public void testGetRadius() throws Exception {
        assertEquals(r, c.getRadius(), EPS);
    }

    @Test
    public void testSetRadius() throws Exception {
        float r2 = 1.1111f;
        c.setRadius(r2);
        assertEquals(r2, c.getRadius(), EPS);
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals(c, c);
        Circle c1 = new Circle(p, r);
        assertEquals(c, c1);
        assertEquals(c1, c);
        c1.setRadius(2*r);
        assertNotEquals(c, c1);
        assertNotEquals(c1, c);
        c1.setRadius(r);
        c1.setCenter(new Point(1f, 1f));
        assertNotEquals(c, c1);
        assertNotEquals(c1, c);
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(c.hashCode(), c.hashCode());
        Circle c1 = new Circle(p, r);
        assertEquals(c.hashCode(), c1.hashCode());
    }
}