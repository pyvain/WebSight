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
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class SegmentTest {

    private final static double EPS = 1e-10;

    private Point p1;
    private Point p2;
    private float t;
    private Segment s;


    @Before
    public void setUp() throws Exception {
        p1 = new Point(3.1415f, 2.7183f);
        p2 = new Point(1.4142f, 1.6180f);
        t = 1.7321f;
        s = new Segment(p1, p2, t);
    }

    @Test
    public void testGetLeftEnd() throws Exception {
        assertSame(p1, s.getLeftEnd());
    }

    @Test
    public void testSetLeftEnd() throws Exception {
        Point p = new Point(1.2345f, 6.7890f);
        s.setLeftEnd(p);
        assertSame(p, s.getLeftEnd());
    }

    @Test
    public void testGetRightEnd() throws Exception {
        assertSame(p2, s.getRightEnd());
    }

    @Test
    public void testSetRightEnd() throws Exception {
        Point p = new Point(1.2345f, 6.7890f);
        s.setRightEnd(p);
        assertSame(p, s.getRightEnd());
    }

    @Test
    public void testGetThickness() throws Exception {
        assertEquals(t, s.getThickness(), EPS);
    }

    @Test
    public void testSetThickness() throws Exception {
        float t2 = 1.1111f;
        s.setThickness(t2);
        assertEquals(t2, s.getThickness(), EPS);
    }

    @Test
    public void testIntersectionWith() throws Exception {
        // Non vertical segment intersections
        //                  p2
        //      p3  
        //          p5
        //      p6      p4
        //  p1
        Point p1 = new Point(-1000, -1000);
        Point p2 = new Point(1000, 1000);
        Point p3 = new Point(-800, 900);
        Point p4 = new Point(800, -900);
        Point p5 = new Point(0, 0);
        Point p6 = new Point(-800, -800);
        // Inner intersecting non parallel segments
        Segment s1 = new Segment(p1, p2, 0.0f);
        Segment s2 = new Segment(p3, p4, 0.0f);
        assertEquals(p5, s1.intersectionWith(s2));
        assertEquals(p5, s2.intersectionWith(s1));
        // Endpoint intersecting non parallel segments
        s1 = new Segment(p1, p5, 0.0f);
        s2 = new Segment(p5, p4, 0.0f);
        assertEquals(p5, s1.intersectionWith(s2));
        assertEquals(p5, s2.intersectionWith(s1));
        // Half inner, half endpoint intersecting non parallel segments
        s1 = new Segment(p1, p2, 0.0f);
        s2 = new Segment(p5, p4, 0.0f);
        assertEquals(p5, s1.intersectionWith(s2));
        assertEquals(p5, s2.intersectionWith(s1));
        // Non intersecting non parallel segments
        s1 = new Segment(p1, p3, 0.0f);
        s2 = new Segment(p5, p4, 0.0f);
        assertEquals(null, s1.intersectionWith(s2));
        assertEquals(null, s2.intersectionWith(s1));
        // Non intersecting parallel segments
        s1 = new Segment(p1, p3, 0.0f);
        s2 = new Segment(p4, p2, 0.0f);
        assertEquals(null, s1.intersectionWith(s2));
        assertEquals(null, s2.intersectionWith(s1));
        // Single point intersecting parallel segments
        s1 = new Segment(p1, p5, 0.0f);
        s2 = new Segment(p5, p2, 0.0f);
        assertEquals(null, s1.intersectionWith(s2));
        assertEquals(null, s2.intersectionWith(s1));
        // Consequently overlapping parallel segments
        s1 = new Segment(p1, p5, 0.0f);
        s2 = new Segment(p6, p2, 0.0f);
        assertEquals(null, s1.intersectionWith(s2));
        assertEquals(null, s2.intersectionWith(s1));
        
        // Vertical segment intersections
        //
        //          p2  p4
        //          p5
        //      p3  p1
        //          p6

        p1 = new Point(0, -200);
        p2 = new Point(0, +200);
        p3 = new Point(-200, -200);
        p4 = new Point(200, 200);
        p5 = new Point(0, 0);
        p6 = new Point(0, -400);
        // One vertical, one not vertical, inner intersection
        s1 = new Segment(p1, p2, 0.0f);
        s2 = new Segment(p3, p4, 0.0f);
        assertEquals(p5, s1.intersectionWith(s2));
        assertEquals(p5, s2.intersectionWith(s1));
        // One vertical, one not vertical, ends intersection
        s1 = new Segment(p1, p2, 0.0f);
        s2 = new Segment(p3, p2, 0.0f);
        assertEquals(p2, s1.intersectionWith(s2));
        assertEquals(p2, s2.intersectionWith(s1));
        // One vertical, one not vertical,
        // half inner, half end intersection
        s1 = new Segment(p1, p5, 0.0f);
        s2 = new Segment(p3, p4, 0.0f);
        assertEquals(p5, s1.intersectionWith(s2));
        assertEquals(p5, s2.intersectionWith(s1));
        // One vertical, one not vertical, no intersection
        s1 = new Segment(p3, p5, 0.0f);
        s2 = new Segment(p1, p4, 0.0f);
        assertEquals(null, s1.intersectionWith(s2));
        assertEquals(null, s2.intersectionWith(s1));
        // Two verticals, no intersection
        s1 = new Segment(p1, p5, 0.0f);
        s2 = new Segment(p6, p2, 0.0f);
        assertEquals(null, s1.intersectionWith(s2));
        assertEquals(null, s2.intersectionWith(s1));
        // Single point intersecting parallel segments
        s1 = new Segment(p1, p6, 0.0f);
        s2 = new Segment(p1, p5, 0.0f);
        assertEquals(null, s1.intersectionWith(s2));
        assertEquals(null, s2.intersectionWith(s1));
        // Consequently overlapping parallel segments
        s1 = new Segment(p6, p5, 0.0f);
        s2 = new Segment(p1, p2, 0.0f);
        assertEquals(null, s1.intersectionWith(s2));
        assertEquals(null, s2.intersectionWith(s1));
    }

    @Test
    public void testIntersectsWithSegment() throws Exception {
        // Non vertical segment intersections
        //                  p2
        //      p3
        //          p5
        //      p6      p4
        //  p1
        Point p1 = new Point(-1000, -1000);
        Point p2 = new Point(1000, 1000);
        Point p3 = new Point(-800, 900);
        Point p4 = new Point(800, -900);
        Point p5 = new Point(0, 0);
        Point p6 = new Point(-800, -800);
        // Inner intersecting non parallel segments
        Segment s1 = new Segment(p1, p2, 0.0f);
        Segment s2 = new Segment(p3, p4, 0.0f);
        assertTrue(s1.intersectsWith(s2));
        assertTrue(s2.intersectsWith(s1));
        // Endpoint intersecting non parallel segments
        s1 = new Segment(p1, p5, 0.0f);
        s2 = new Segment(p5, p4, 0.0f);
        assertTrue(s1.intersectsWith(s2));
        assertTrue(s2.intersectsWith(s1));
        // Half inner, half endpoint intersecting non parallel segments
        s1 = new Segment(p1, p2, 0.0f);
        s2 = new Segment(p5, p4, 0.0f);
        assertTrue(s1.intersectsWith(s2));
        assertTrue(s2.intersectsWith(s1));
        // Non intersecting non parallel segments
        s1 = new Segment(p1, p3, 0.0f);
        s2 = new Segment(p5, p4, 0.0f);
        assertFalse(s1.intersectsWith(s2));
        assertFalse(s2.intersectsWith(s1));
        // Non intersecting parallel segments
        s1 = new Segment(p1, p3, 0.0f);
        s2 = new Segment(p4, p2, 0.0f);
        assertFalse(s1.intersectsWith(s2));
        assertFalse(s2.intersectsWith(s1));
        // Single point intersecting parallel segments
        s1 = new Segment(p1, p5, 0.0f);
        s2 = new Segment(p5, p2, 0.0f);
        assertFalse(s1.intersectsWith(s2));
        assertFalse(s2.intersectsWith(s1));
        // Consequently overlapping parallel segments
        s1 = new Segment(p1, p5, 0.0f);
        s2 = new Segment(p6, p2, 0.0f);
        assertFalse(s1.intersectsWith(s2));
        assertFalse(s2.intersectsWith(s1));

        // Vertical segment intersections
        //
        //          p2  p4
        //          p5
        //      p3  p1
        //          p6

        p1 = new Point(0, -200);
        p2 = new Point(0, +200);
        p3 = new Point(-200, -200);
        p4 = new Point(200, 200);
        p5 = new Point(0, 0);
        p6 = new Point(0, -400);
        // One vertical, one not vertical, inner intersection
        s1 = new Segment(p1, p2, 0.0f);
        s2 = new Segment(p3, p4, 0.0f);
        assertTrue(s1.intersectsWith(s2));
        assertTrue(s2.intersectsWith(s1));
        // One vertical, one not vertical, ends intersection
        s1 = new Segment(p1, p2, 0.0f);
        s2 = new Segment(p3, p2, 0.0f);
        assertTrue(s1.intersectsWith(s2));
        assertTrue(s2.intersectsWith(s1));
        // One vertical, one not vertical,
        // half inner, half end intersection
        s1 = new Segment(p1, p5, 0.0f);
        s2 = new Segment(p3, p4, 0.0f);
        assertTrue(s1.intersectsWith(s2));
        assertTrue(s2.intersectsWith(s1));
        // One vertical, one not vertical, no intersection
        s1 = new Segment(p3, p5, 0.0f);
        s2 = new Segment(p1, p4, 0.0f);
        assertFalse(s1.intersectsWith(s2));
        assertFalse(s2.intersectsWith(s1));
        // Two verticals, no intersection
        s1 = new Segment(p1, p5, 0.0f);
        s2 = new Segment(p6, p2, 0.0f);
        assertFalse(s1.intersectsWith(s2));
        assertFalse(s2.intersectsWith(s1));
        // Single point intersecting parallel segments
        s1 = new Segment(p1, p6, 0.0f);
        s2 = new Segment(p1, p5, 0.0f);
        assertFalse(s1.intersectsWith(s2));
        assertFalse(s2.intersectsWith(s1));
        // Consequently overlapping parallel segments
        s1 = new Segment(p6, p5, 0.0f);
        s2 = new Segment(p1, p2, 0.0f);
        assertFalse(s1.intersectsWith(s2));
        assertFalse(s2.intersectsWith(s1));
    }

    @Test
    public void testIntersectsWithCircle() {
        // Non vertical segment
        assertTrue(s.intersectsWith(new Circle(p1, 1f)));
        Point m = new Point((p1.getX()+p2.getX())/2, (p1.getY()+p2.getY())/2);
        assertTrue(s.intersectsWith(new Circle(m, 0.5f)));
        assertFalse(s.intersectsWith(new Circle(p1, 10f)));
        // Vertical segment
        Segment s2 = new Segment(p1, new Point(p1.getX(), p1.getY() + 3.14f), 0f);
        assertTrue(s2.intersectsWith(new Circle(p1, 1f)));
        m = new Point(p1.getX(), p1.getY()+1.57f);
        assertTrue(s2.intersectsWith(new Circle(m, 0.5f)));
        assertFalse(s2.intersectsWith(new Circle(p1, 10f)));
    }

    @Test
    public void testEquals() {
        assertEquals(s, s);
        Segment s1 = new Segment(p1, p2, t);
        assertEquals(s, s1);
        assertEquals(s1, s);
        s1.setThickness(t+1);
        assertNotEquals(s, s1);
        assertNotEquals(s1, s);
        s1.setThickness(t);
        s1.setLeftEnd(new Point(p1.getX()+1, p1.getY()));
        assertNotEquals(s, s1);
        assertNotEquals(s1, s);
        s1.setLeftEnd(p1);
        s1.setRightEnd(new Point(p2.getX()+1, p2.getY()));
        assertNotEquals(s, s1);
        assertNotEquals(s1, s);
    }

    @Test
    public void testHashCode(){
        assertEquals(s.hashCode(), s.hashCode());
        Segment s1 = new Segment(p1, p2, t);
        assertEquals(s.hashCode(), s1.hashCode());
    }
}