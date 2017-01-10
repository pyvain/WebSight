package fr.pyvain.websight.websight.Geometry;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.SortedSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * <p>
 *     @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */


public class TestIntersection {

	@BeforeClass
	public static void beforeTests() {
		System.out.println("Testing class Intersection\n");
	}

	@Test
	public void testGetters() {
		Segment s1 = new Segment(new CPoint(-100, 100), new CPoint(100, -100));
		Segment s2 = new Segment(new CPoint(-100, -100), new CPoint(100, 100));
		CPoint location = s1.intersectionWith(s2);
		Intersection inter = new Intersection(location, s1, s2);
		assertEquals(location, inter.getLocation());
		assertTrue(inter.getSegments().contains(s1));
		assertTrue(inter.getSegments().contains(s2));
	}

	@Test
	public void testAddSegmentIfAbsent() {
		//   p3    p6
		//         p2
		//     p1    
		//     p5    p4
		CPoint p1 = new CPoint(-100, -200);
		CPoint p2 = new CPoint(100, 200);
		CPoint p3 = new CPoint(-300, 400);
		CPoint p4 = new CPoint(300, -400);
		CPoint p5 = new CPoint(-100, -300);
		CPoint p6 = new CPoint(100, 300);
		Segment s1 = new Segment(p1, p2);
		Segment s2 = new Segment(p3, p4);
		Segment s3 = new Segment(p5, p6);
		CPoint location = s1.intersectionWith(s2);
		Segment.setComparingX(-100);
		Intersection i1 = new Intersection(location, s1, s2);
		// Adds new segment
		i1.addSegmentIfAbsent(s3);
		SortedSet<Segment> segments = i1.getSegments();
		assertEquals(3, segments.size());
		assertTrue(segments.contains(s1));
		assertTrue(segments.contains(s2));
		assertTrue(segments.contains(s3));
		// Adds already present segment
		i1.addSegmentIfAbsent(s3);
		segments = i1.getSegments();
		assertEquals(3, segments.size());
		assertTrue(segments.contains(s1));
		assertTrue(segments.contains(s2));
		assertTrue(segments.contains(s3));
	}

	@Test
	public void testEquals() {
		Segment s1 = new Segment(new CPoint(0, 0), new CPoint(200, 200));
		Segment s2 = new Segment(new CPoint(0, 200), new CPoint(200, 0));
		Segment s3 = new Segment(new CPoint(0, 300), new CPoint(300, 0));
		Segment.setComparingX(0);
		Intersection i1 = new Intersection(new CPoint(100, 100), s1, s2);
		Intersection i2 = new Intersection(new CPoint(100, 100), s3, s2);
		assertEquals(i1, i1);
		assertEquals(i1.hashCode(), i1.hashCode());
		assertNotEquals(i2, i1);
		assertNotEquals(i1, i2);
	}

	@Test
	public void testToString() {
		CPoint p1 = new CPoint(-100, -200);
		CPoint p2 = new CPoint(100, 200);
		CPoint p3 = new CPoint(-300, 400);
		CPoint p4 = new CPoint(300, -400);
		Segment s1 = new Segment(p1, p2);
		Segment s2 = new Segment(p3, p4);
		CPoint location = s1.intersectionWith(s2);
		Intersection e1 = new Intersection(location, s1, s2);
		assertEquals("(0, 0), intersection of segments :\n" +
			"[(-300, 400), (300, -400)]\n" +
			"[(-100, -200), (100, 200)]\n",
			e1.toString());
	}
}