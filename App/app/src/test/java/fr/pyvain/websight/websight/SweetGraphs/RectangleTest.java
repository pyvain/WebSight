package fr.pyvain.websight.websight.SweetGraphs;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * <p>
 *
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 *         </p>
 */
public class RectangleTest {

    private final static float EPS = (float)1e-10;
    private Point center;
    private float width;
    private float height;
    private Rectangle r;

    @Before
    public void setUp() throws Exception {
        center = new Point(3.1415f, 2.7183f);
        width = 1.4142f;
        height = 1.6180f;
        r = new Rectangle(center, width, height);
    }

    @Test
    public void testGetCenter() throws Exception {
        assertSame(center, r.getCenter());
    }

    @Test
    public void testSetCenter() throws Exception {
        Point center2 = new Point(1.2345f, 6.7890f);
        r.setCenter(center2);
        assertSame(center2, r.getCenter());
    }

    @Test
    public void testGetWidth() throws Exception {
        assertEquals(width, r.getWidth(), EPS);
    }

    @Test
    public void testSetWidth() throws Exception {
        float width2 = 1.2345f;
        r.setWidth(width2);
        assertEquals(width2, r.getWidth(), EPS);
    }

    @Test
    public void testGetHeight() throws Exception {
        assertEquals(height, r.getHeight(), EPS);
    }

    @Test
    public void testSetHeight() throws Exception {
        float height2 = 1.2345f;
        r.setHeight(height2);
        assertEquals(height2, r.getHeight(), EPS);
    }

    @Test
    public void testContainsCircle() throws Exception {
        float x = center.getX();
        float y = center.getY();
        // Completely in
        Circle c = new Circle(center, Math.min(height, width)/4);
        assertTrue(r.contains(c));
        // Overlapping on one side
        c = new Circle(new Point(x - width/2 - 0.9f, y), 1f);
        assertTrue(r.contains(c));
        c = new Circle(new Point(x + width/2 + 0.9f, y), 1f);
        assertTrue(r.contains(c));
        c = new Circle(new Point(x, y - height/2 - 0.9f), 1f);
        assertTrue(r.contains(c));
        c = new Circle(new Point(x, y + height/2 + 0.9f), 1f);
        assertTrue(r.contains(c));
        // Overlapping on one corner



    }

    @Test
    public void testContainsSegment() throws Exception {
        float x = center.getX();
        float y = center.getY();
        // Both ends in
        Segment s = new Segment(center, new Point(x + width/4, y), 0f);
        assertTrue(r.contains(s));
        s = new Segment(center, new Point(x, y + height/4), 0f);
        assertTrue(r.contains(s));
        // Left end in
        s = new Segment(center, new Point(x + width, y), 0f);
        assertTrue(r.contains(s));
        s = new Segment(center, new Point(x, y + height), 0f);
        assertTrue(r.contains(s));
        // Right end in
        s = new Segment(new Point(x - width, y), center, 0f);
        assertTrue(r.contains(s));
        s = new Segment(new Point(x, y - height), center, 0f);
        assertTrue(r.contains(s));
        // No end in
        s = new Segment(new Point(x - width, y), new Point(x + width, y), 0f);
        assertFalse(r.contains(s));
        s = new Segment(new Point(x, y - height), new Point(x, y + height), 0f);
        assertFalse(r.contains(s));
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals(r, r);
        Rectangle r1 = new Rectangle(center, width, height);
        assertEquals(r, r1);
        assertEquals(r1, r);
        r1.setWidth(2*width);
        assertNotEquals(r, r1);
        assertNotEquals(r1, r);
        r1.setWidth(width);
        r1.setHeight(2*height);
        assertNotEquals(r, r1);
        assertNotEquals(r1, r);
        r1.setHeight(height);
        r1.setCenter(new Point(center.getX()+1, center.getY()));
        assertNotEquals(r, r1);
        assertNotEquals(r1, r);
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(r.hashCode(), r.hashCode());
        Rectangle r1 = new Rectangle(center, width, height);
        assertEquals(r.hashCode(), r1.hashCode());
    }
}