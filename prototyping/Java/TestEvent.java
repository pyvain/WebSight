import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestEvent {

	@BeforeClass
	public static void beforeTests() {
		System.out.println("\n------------------------------");
		System.out.println("Testing class Event");
		System.out.println("------------------------------");
	}

	@AfterClass
	public static void afterTests() {
		System.out.println("\n------------------------------\n");
	}

	@Test 
	public void testGetters() {
		Point p1 = new Point(0, 100);
		Point p2 = new Point(200, 300);
		Segment s = new Segment(p1, p2);
		// Left endpoint event
		Event eLeft = new Event(s, Event.LEFT);
		assertEquals(p1, eLeft.getLocation());
		assertEquals(s, eLeft.getSegment());
		assertEquals(true, eLeft.isLeftEndpoint());
		// Right endpoint event
		Event eRight = new Event(s, Event.RIGHT);
		assertEquals(p2, eRight.getLocation());
		assertEquals(s, eRight.getSegment());
		assertEquals(false, eRight.isLeftEndpoint());
	}

	@Test 
	public void testCompareTo() {
		Segment s1 = new Segment(new Point(0, 100), new Point(200, 300));
		Segment s2 = new Segment(new Point(0, 100), new Point(300, 400));
		Segment s3 = new Segment(new Point(200, 300), new Point(300, 400));
		Event e1Left = new Event(s1, Event.LEFT);
		Event e1Right = new Event(s1, Event.RIGHT);
		Event e2Left = new Event(s2, Event.LEFT);
		Event e2Right = new Event(s2, Event.RIGHT);
		Event e3Left = new Event(s3, Event.LEFT);
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
		Segment s1 = new Segment(new Point(0, 100), new Point(200, 300));
		Segment s2 = new Segment(new Point(0, 100), new Point(200, 300));
		Segment s3 = new Segment(new Point(0, 100), new Point(300, 400));
		// same segment, left endpoint
		assertEquals(new Event(s1, Event.LEFT), new Event(s2, Event.LEFT));
		// same segment, right endpoint
		assertEquals(new Event(s1, Event.RIGHT), new Event(s2, Event.RIGHT));
		// same segment, different endpoints
		assertNotEquals(new Event(s1, Event.LEFT), new Event(s2, Event.RIGHT));
		assertNotEquals(new Event(s1, Event.RIGHT), new Event(s2, Event.LEFT));
		// same location, different segments
		assertNotEquals(new Event(s1, Event.LEFT), new Event(s3, Event.LEFT));
		assertNotEquals(new Event(s3, Event.LEFT), new Event(s1, Event.LEFT));
	}

	@Test 
	public void testHashCode() {
		Segment s1 = new Segment(new Point(0, 100), new Point(200, 300));
		Event e1 = new Event(s1, Event.LEFT);
		Event e2 = new Event(s1, Event.LEFT);
		assertEquals(e1.hashCode(), e2.hashCode());
		e1 = new Event(s1, Event.RIGHT);
		e2 = new Event(s1, Event.RIGHT);
		assertEquals(e1.hashCode(), e2.hashCode());
	}

	@Test
	public void testToString() {
		Segment s1 = new Segment(new Point(0, 100), new Point(200, 300));
		Event e1 = new Event(s1, Event.LEFT);
		Event e2 = new Event(s1, Event.RIGHT);
		assertEquals("(0, 100) : left endpoint event of segment [(0, 100), (200, 300)]",
			e1.toString());
		assertEquals("(200, 300) : right endpoint event of segment [(0, 100), (200, 300)]",
			e2.toString());
	}
}