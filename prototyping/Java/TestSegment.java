import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestSegment {

	@BeforeClass
	public static void beforeTests() {
		System.out.println("\n------------------------------");
		System.out.println("Testing class Segment");
		System.out.println("------------------------------");
	}

	@AfterClass
	public static void afterTests() {
		System.out.println("\n------------------------------\n");
	}

	@Test 
	public void testValidSegment() {
		Point p1 = new Point(0, 100);
		Point p2 = new Point(200, 300);
		// Initializing with well ordered endpoints
		Segment s1 = new Segment(p1, p2);
		assertEquals(p1, s1.getP1());
		assertEquals(p2, s1.getP2());
		// Initializing with badly ordered endpoints
		Segment s2 = new Segment(p2, p1);
		assertEquals(p1, s1.getP1());
		assertEquals(p2, s1.getP2());		
	}

	@Test(expected=IllegalArgumentException.class)
	public void testInvalidSegment() {
		Point p1 = new Point(0, 100);
		Point p2 = new Point(0, 200);
		// Initializing with equal x-coordinates
		Segment s1 = new Segment(p1, p2);
	}

	@Test
	public void testIntersectionWith() {
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
		Segment s1 = new Segment(p1, p2);
		Segment s2 = new Segment(p3, p4);
		assertEquals(p5, s1.intersectionWith(s2));
		assertEquals(p5, s2.intersectionWith(s1));
		// Endpoint intersecting non parallel segments
		s1 = new Segment(p1, p5);
		s2 = new Segment(p5, p4);
		assertEquals(p5, s1.intersectionWith(s2));
		assertEquals(p5, s2.intersectionWith(s1));
		// Half inner, half endpoint intersecting non parallel segments
		s1 = new Segment(p1, p2);
		s2 = new Segment(p5, p4);
		assertEquals(p5, s1.intersectionWith(s2));
		assertEquals(p5, s2.intersectionWith(s1));
		// Non intersecting non parallel segments
		s1 = new Segment(p1, p3);
		s2 = new Segment(p5, p4);
		assertEquals(null, s1.intersectionWith(s2));
		assertEquals(null, s2.intersectionWith(s1));
		// Non intersecting parallel segments
		s1 = new Segment(p1, p3);
		s2 = new Segment(p4, p2);
		assertEquals(null, s1.intersectionWith(s2));
		assertEquals(null, s2.intersectionWith(s1));
		// Single point intersecting parallel segments
		s1 = new Segment(p1, p5);
		s2 = new Segment(p5, p2);
		assertEquals(null, s1.intersectionWith(s2));
		assertEquals(null, s2.intersectionWith(s1));
		// Consequently overlapping parallel segments
		s1 = new Segment(p1, p5);
		s2 = new Segment(p6, p2);
		assertEquals(null, s1.intersectionWith(s2));
		assertEquals(null, s2.intersectionWith(s1));
	}

	@Test
	public void testNbIntersectionsWithCircle() {
		Segment s1 = new Segment(new Point(-200, 0), new Point(200, 800));
		assertEquals(2, s1.nbIntersectionsWithCircle(new Point(0, 400), 100));		
		assertEquals(1, s1.nbIntersectionsWithCircle(new Point(-200, 0), 50));		
		assertEquals(0, s1.nbIntersectionsWithCircle(new Point(-400, 0), 100));
		Segment s2 = new Segment(new Point(-200, 0), new Point(200, 0));
		assertEquals(1, s2.nbIntersectionsWithCircle(new Point(0, 200), 200));		
	}
	
	@Test
	public void testCompareTo() {
		//          p2
		//    p3
		//       p4
		//  p1
		Point p1 = new Point(-1000, -1000);
		Point p2 = new Point(1000, 1000);
		Point p3 = new Point(-800, 900);
		Point p4 = new Point(800, -900);
		Segment s1 = new Segment(p1, p2);
		Segment s2 = new Segment(p3, p4);
		// different y-coordinates at comparingX
		Segment.setComparingX(-400);
		assertTrue(s1.compareTo(s2) < 0);
		assertTrue(s2.compareTo(s1) > 0);
		// same y-coordinates at comparingX, different gradients
		Segment.setComparingX(0);
		assertTrue(s1.compareTo(s2) > 0);
		assertTrue(s2.compareTo(s1) < 0);
		// Same y-coordinates at comparingX, same gradients
		assertTrue(s1.compareTo(s1) == 0);
	}

	@Test
	public void testEquals() {
		Point p1 = new Point(0, 100);
		Point p2 = new Point(200, 300);
		Point p3 = new Point(0, 100);
		Point p4 = new Point(200, 300);
		Point p5 = new Point(0, 0);
		Point p6 = new Point(400, 500);
		// equal Segment objects
		Segment s1 = new Segment(p1, p2);
		Segment s2 = new Segment(p3, p4);
		assertEquals(s1, s2);
		assertEquals(s2, s1);
		// different p1
		Segment s3 = new Segment(p5, p2);
		assertNotEquals(s1, s3);
		assertNotEquals(s3, s1);
		// same p1 different p2
		Segment s4 = new Segment(p1, p6);
		assertNotEquals(s1, s4);
		assertNotEquals(s4, s1);
		// different p1 and p2
		Segment s5 = new Segment(p5, p6);
		assertNotEquals(s1, s5);
		assertNotEquals(s5, s1);
	}

	@Test
	public void testHashCode() {
		Point p1 = new Point(0, 100);
		Point p2 = new Point(200, 300);
		Point p3 = new Point(0, 100);
		Point p4 = new Point(200, 300);
		Point p5 = new Point(400, 500);
		// equal Segment objects
		Segment s1 = new Segment(p1, p2);
		Segment s2 = new Segment(p3, p4);
		assertEquals(s1.hashCode(), s2.hashCode());
		// not equal Segment objects
		Segment s3 = new Segment(p1, p5);
		assertNotEquals(s1.hashCode(), s3.hashCode());
	}

	@Test
	public void testToString() {
		Point p1 = new Point(0, 100);
		Point p2 = new Point(200, 300);
		Segment s1 = new Segment(p1, p2);
		assertEquals("[(0, 100), (200, 300)]", s1.toString());
	}
}