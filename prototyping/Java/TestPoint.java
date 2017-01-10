import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestPoint {
	
	@BeforeClass
	public static void beforeTests() {
		System.out.println("\n------------------------------");
		System.out.println("Testing class Point");
		System.out.println("------------------------------");
	}

	@AfterClass
	public static void afterTests() {
		System.out.println("\n------------------------------\n");
	}

	@Test 
	public void testGetters() {
		Point p = new Point(0, 1);
		assertEquals(0, p.getX());
		assertEquals(1, p.getY());
	}

	@Test
	public void testDistance() {
		Point p1 = new Point(0, 1);
		Point p2 = new Point(1, 2);
		float EPS = 0.01f;
		assertEquals(Math.sqrt(2), p1.distance(p2), EPS);
		assertEquals(Math.sqrt(2), p2.distance(p1), EPS);  
	}

	@Test
	public void testCompareTo() {
		Point p1 = new Point(0, 1);
		Point p2 = new Point(1, 2);
		Point p3 = new Point(0, 2);
		Point p4 = new Point(0, 1);
		// smaller x-coordinates
		assertTrue(p1.compareTo(p2) < 0);
		// greater x-coordinates
		assertTrue(p2.compareTo(p1) > 0);
		// Same x-coordinates, smaller y-coordinates
		assertTrue(p1.compareTo(p3) < 0);
		// Same x-coordinates, greater y-coordinates
		assertTrue(p3.compareTo(p1) > 0);
		// Same x-coordinates, same y-coordinates
		assertTrue(p1.compareTo(p4) == 0);
	}

	@Test
	public void testEquals() {
		Point p1 = new Point(0, 1);
		Point p2 = new Point(0, 1);
		Point p3 = new Point(0, 2);
		// equal Point objects
		assertEquals(p1, p2);
		assertEquals(p2, p1);
		// not equal Point objects
		assertNotEquals(p1, p3);
		assertNotEquals(p1, p3);
	}

	@Test
	public void testHashCode() {
		Point p1 = new Point(0, 1);
		Point p2 = new Point(0, 1);
		Point p3 = new Point(0, 2);
		// equal Point objects
		assertEquals(p1.hashCode(), p2.hashCode());
		assertEquals(p2.hashCode(), p1.hashCode());
		// not equal Point objects
		assertNotEquals(p1.hashCode(), p3.hashCode());
		assertNotEquals(p1.hashCode(), p3.hashCode()); 
	}

	@Test
	public void testToString() {
		Point p = new Point(0, 1);
		assertEquals("(0, 1)", p.toString());
	}
}