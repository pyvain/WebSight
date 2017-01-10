import java.util.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestEventQueue {

	@BeforeClass
	public static void beforeTests() {
		System.out.println("\n------------------------------");
		System.out.println("Testing class EventQueue");
		System.out.println("------------------------------");
	}

	@AfterClass
	public static void afterTests() {
		System.out.println("\n------------------------------\n");
	}

	@Test 
	public void testValidEventQueue() {
		Segment s1 = new Segment(new Point(0, 100), new Point(200, 100));
		Segment s2 = new Segment(new Point(-100, 200), new Point(300, -100));
		Segment s3 = new Segment(new Point(400, 400), new Point(200, -800));
		ArrayList<Segment> segments = new ArrayList<Segment>();
		segments.add(s1);
		segments.add(s2);
		segments.add(s3);
		EventQueue eq = new EventQueue(segments);
		assertEquals(new Event(s2, Event.LEFT), eq.nextEvent());
		assertEquals(new Event(s1, Event.LEFT), eq.nextEvent());
		assertEquals(new Event(s3, Event.LEFT), eq.nextEvent());
		assertEquals(new Event(s1, Event.RIGHT), eq.nextEvent());
		assertEquals(new Event(s2, Event.RIGHT), eq.nextEvent());
		assertEquals(new Event(s3, Event.RIGHT), eq.nextEvent());
		assertEquals(null, eq.nextEvent());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testInvalidEventQueue() {
		Segment s1 = new Segment(new Point(0, 100), new Point(200, 100));
		Segment s2 = new Segment(new Point(-100, 200), new Point(0, 100));
		Segment s3 = new Segment(new Point(400, 400), new Point(200, -800));
		ArrayList<Segment> segments = new ArrayList<Segment>();
		segments.add(s1);
		segments.add(s2);
		segments.add(s3);
		EventQueue eq = new EventQueue(segments);
	}
}