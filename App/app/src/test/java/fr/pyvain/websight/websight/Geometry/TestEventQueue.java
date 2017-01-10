package fr.pyvain.websight.websight.Geometry;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * <p>
 *     @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */


public class TestEventQueue {

	@BeforeClass
	public static void beforeTests() {
		System.out.println("Testing class EventQueue\n");
	}

	@Test
	public void testValidEventQueue() {
		Segment s1 = new Segment(new CPoint(0, 100), new CPoint(200, 100));
		Segment s2 = new Segment(new CPoint(-100, 200), new CPoint(300, -100));
		Segment s3 = new Segment(new CPoint(400, 400), new CPoint(200, -800));
		ArrayList<Segment> segments = new ArrayList<>();
		segments.add(s1);
		segments.add(s2);
		segments.add(s3);
		EventQueue eq = new EventQueue(segments);
		Assert.assertEquals(new Event(s2, true), eq.nextEvent());
		assertEquals(new Event(s1, true), eq.nextEvent());
		assertEquals(new Event(s3, true), eq.nextEvent());
		assertEquals(new Event(s1, false), eq.nextEvent());
		assertEquals(new Event(s2, false), eq.nextEvent());
		assertEquals(new Event(s3, false), eq.nextEvent());
		assertEquals(null, eq.nextEvent());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testInvalidEventQueue() {
		Segment s1 = new Segment(new CPoint(0, 100), new CPoint(200, 100));
		Segment s2 = new Segment(new CPoint(-100, 200), new CPoint(0, 100));
		ArrayList<Segment> segments = new ArrayList<>();
		segments.add(s1);
		segments.add(s2);
		new EventQueue(segments);
	}
}