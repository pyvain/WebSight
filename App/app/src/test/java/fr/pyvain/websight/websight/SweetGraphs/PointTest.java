package fr.pyvain.websight.websight.SweetGraphs;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class PointTest {

    private final static double EPS = 1e-3;

    private float x;
    private float y;
    private Point p;
    private Point p1;
    private Point p2;
    private Point p3;
    private Point p4;
    private Point p5;

    @Before
    public void setUp() throws Exception {
        x = 3.1415f;
        y = 2.7183f;
        p = new Point(x, y);
        float delta = (float)(1e-3);
        p1 = new Point(x-delta, y); // smaller x
        p2 = new Point(x+delta, y); // greater x
        p3 = new Point(x, y-delta); // equal x, smaller y
        p4 = new Point(x, y+delta); // equal x, greater y
        p5 = new Point(x, y); // equal x, equal y
    }


    @Test
    public void testGetX() throws Exception {
        assertEquals(x, p.getX(), EPS);
    }

    @Test
    public void testGetY() throws Exception {
        assertEquals(y, p.getY(), EPS);
    }

    @Test
    public void testSetX() throws Exception {
        float x2 = 1.6180f;
        p.setX(x2);
        assertEquals(x2, p.getX(), EPS);
    }

    @Test
    public void testSetY() throws Exception {
        float y2 = 1.6180f;
        p.setY(y2);
        assertEquals(y2, p.getY(), EPS);
    }

    @Test
    public void testEquals() throws Exception {
        assertNotEquals(p, p1);
        assertNotEquals(p1, p);
        assertNotEquals(p, p2);
        assertNotEquals(p2, p);
        assertNotEquals(p, p3);
        assertNotEquals(p3, p);
        assertNotEquals(p, p4);
        assertNotEquals(p4, p);
        assertEquals(p, p5);
        assertEquals(p5, p);
        assertEquals(p, p);
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(p.hashCode(), p5.hashCode());
        assertEquals(p.hashCode(), p.hashCode());
    }

    @Test
    public void testCompareTo() throws Exception {
        // smaller x
        assertTrue(p1.compareTo(p) < 0);
        assertTrue(p.compareTo(p1) > 0);
        // greater x
        assertTrue(p2.compareTo(p) > 0);
        assertTrue(p.compareTo(p2) < 0);
        // equal x, smaller y
        assertTrue(p3.compareTo(p) < 0);
        assertTrue(p.compareTo(p3) > 0);
        // equal x, greater y
        assertTrue(p4.compareTo(p) > 0);
        assertTrue(p.compareTo(p4) < 0);
        // equal x, equal y
        assertEquals(0, p.compareTo(p5));
    }

    @Test
    public void testDistanceBetween() throws Exception {
        assertEquals(0, Point.distanceBetween(p, p5), EPS);
        assertEquals(0, Point.distanceBetween(p5, p), EPS);
        Point p6 = new Point(x+0.1234f, y+0.5678f);
        assertEquals(0.581f, Point.distanceBetween(p, p6), EPS);
        assertEquals(0.581f, Point.distanceBetween(p6, p), EPS);
    }

    @Test
    public void testRotateAround() throws Exception {
        Point c = new Point(0, 0);
        p.rotateAround(c, 2*(float)Math.PI);
        assertEquals(p5, p);
        p.rotateAround(c, (float)Math.PI);
        assertEquals(new Point(-x, -y), p);
    }
}