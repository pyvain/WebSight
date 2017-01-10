import java.util.SortedSet;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestIntersection {

	@BeforeClass
	public static void beforeTests() {
		System.out.println("\n------------------------------");
		System.out.println("Testing class Intersection");
		System.out.println("------------------------------");
	}

	@AfterClass
	public static void afterTests() {
		System.out.println("\n------------------------------\n");
	}

	@Test 
	public void testGetters() {
		Segment s1 = new Segment(new Point(-100, 100), new Point(100, -100));
		Segment s2 = new Segment(new Point(-100, -100), new Point(100, 100));
		Point location = s1.intersectionWith(s2);
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
		Point p1 = new Point(-100, -200);
		Point p2 = new Point(100, 200);
		Point p3 = new Point(-300, 400);
		Point p4 = new Point(300, -400);
		Point p5 = new Point(-100, -300);
		Point p6 = new Point(100, 300);
		Segment s1 = new Segment(p1, p2);
		Segment s2 = new Segment(p3, p4);
		Segment s3 = new Segment(p5, p6);
		Point location = s1.intersectionWith(s2);
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
		Segment s1 = new Segment(new Point(0, 0), new Point(200, 200));
		Segment s2 = new Segment(new Point(0, 200), new Point(200, 0));
		Segment s3 = new Segment(new Point(0, 300), new Point(300, 0));
		Segment.setComparingX(0);
		Intersection i1 = new Intersection(new Point(100, 100), s1, s2);
		Intersection i2 = new Intersection(new Point(100, 100), s3, s2);
		assertEquals(i1, i1);
		assertNotEquals(i2, i1);
		assertNotEquals(i1, i2);
	}

	@Test
	public void testToString() {
		Point p1 = new Point(-100, -200);
		Point p2 = new Point(100, 200);
		Point p3 = new Point(-300, 400);
		Point p4 = new Point(300, -400);
		Segment s1 = new Segment(p1, p2);
		Segment s2 = new Segment(p3, p4);
		Point location = s1.intersectionWith(s2);
		Intersection e1 = new Intersection(location, s1, s2);
		assertEquals("(0, 0), intersection of segments :\n" +
			"[(-300, 400), (300, -400)]\n" +
			"[(-100, -200), (100, 200)]\n",
			e1.toString());
	}
}