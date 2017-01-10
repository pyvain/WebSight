package fr.pyvain.websight.websight.Geometry;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * <p>
 *     @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */


public class TestEvent {

	@BeforeClass
	public static void beforeTests() {
		System.out.println("Testing class Event");
	}

	@Test
	public void testGetters() {
		CPoint p1 = new CPoint(0, 100);
		CPoint p2 = new CPoint(200, 300);
		Segment s = new Segment(p1, p2);
		// Left endpoint event
		Event eLeft = new Event(s, true);
		assertEquals(p1, eLeft.getLocation());
		assertEquals(s, eLeft.getSegment());
		assertEquals(true, eLeft.isLeftEndpoint());
		// Right endpoint event
		Event eRight = new Event(s, false);
		assertEquals(p2, eRight.getLocation());
		assertEquals(s, eRight.getSegment());
		assertEquals(false, eRight.isLeftEndpoint());
	}

	@Test 
	public void testCompareTo() {
		Segment s1 = new Segment(new CPoint(0, 100), new CPoint(200, 300));
		Segment s2 = new Segment(new CPoint(0, 100), new CPoint(300, 400));
		Segment s3 = new Segment(new CPoint(200, 300), new CPoint(300, 400));
		Event e1Left = new Event(s1, true);
		Event e1Right = new Event(s1, false);
		Event e2Left = new Event(s2, true);
		Event e2Right = new Event(s2, false);
		Event e3Left = new Event(s3, true);
		// Same location 
		assertTrue(e1Left.compareTo(e2Left) == 0);
		assertTrue(e2Left.compareTo(e1Left) == 0);
		assertTrue(e1Right.compareTo(e3Left) == 0);
		assertTrue(e3Left.compareTo(e1Right) == 0);
		// Different locations
		assertTrue(e1Left.compareTo(e1Right) < 0);
		assertTrue(e1Right.compareTo(e1Left) > 0);
		assertTrue(e1Left.compareTo(e2Right) < 0);
		assertTrue(e2Right.compareTo(e1Left) > 0);
		assertTrue(e1Left.compareTo(e3Left) < 0);
		assertTrue(e3Left.compareTo(e1Left) > 0);
	}

	@Test
	public void testEquals() {
		Segment s1 = new Segment(new CPoint(0, 100), new CPoint(200, 300));
		Segment s2 = new Segment(new CPoint(0, 100), new CPoint(200, 300));
		Segment s3 = new Segment(new CPoint(0, 100), new CPoint(300, 400));
		// same segment, left endpoint
		assertEquals(new Event(s1, true), new Event(s2, true));
		// same segment, right endpoint
		assertEquals(new Event(s1, false), new Event(s2, false));
		// same segment, different endpoints
		assertNotEquals(new Event(s1, true), new Event(s2, false));
		assertNotEquals(new Event(s1, false), new Event(s2, true));
		// same location, different segments
		assertNotEquals(new Event(s1, true), new Event(s3, true));
		assertNotEquals(new Event(s3, true), new Event(s1, true));
	}

	@Test 
	public void testHashCode() {
		Segment s1 = new Segment(new CPoint(0, 100), new CPoint(200, 300));
		Event e1 = new Event(s1, true);
		Event e2 = new Event(s1, true);
		assertEquals(e1.hashCode(), e2.hashCode());
		e1 = new Event(s1, false);
		e2 = new Event(s1, false);
		assertEquals(e1.hashCode(), e2.hashCode());
	}

	@Test
	public void testToString() {
		Segment s1 = new Segment(new CPoint(0, 100), new CPoint(200, 300));
		Event e1 = new Event(s1, true);
		Event e2 = new Event(s1, false);
		assertEquals("(0, 100) : left endpoint event of segment [(0, 100), (200, 300)]",
			e1.toString());
		assertEquals("(200, 300) : right endpoint event of segment [(0, 100), (200, 300)]",
			e2.toString());
	}
}