import java.util.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestIntersectionSet {

	@BeforeClass
	public static void beforeTests() {
		System.out.println("\n------------------------------");
		System.out.println("Testing class IntersectionSet");
		System.out.println("------------------------------");
	}

	@AfterClass
	public static void afterTests() {
		System.out.println("\n------------------------------\n");
	}

	@Test 
	public void testIntersectionSet() {
		IntersectionSet intersections = new IntersectionSet();
		// Empty collection
		assertEquals(intersections.at(0).size(), 0);
	}

	@Test
	public void testAdd() {
		IntersectionSet intersections = new IntersectionSet();
		// first X
		Segment.setComparingX(-100);
		Segment s1 = new Segment(new Point(-100, 100), new Point(100, -100));
		Segment s2 = new Segment(new Point(-100, -100), new Point(100, 100));
		Intersection i1 = new Intersection(new Point(0, 0), s1, s2);
		intersections.add(i1);
		ArrayList<Intersection> interAt0;
		interAt0 = new ArrayList<Intersection>(intersections.at(0));
		assertEquals(1, interAt0.size());
		assertEquals(new Point(0, 0), interAt0.get(0).getLocation());
		assertEquals(2, interAt0.get(0).getSegments().size());
		assertTrue(interAt0.get(0).getSegments().contains(s1));
		assertTrue(interAt0.get(0).getSegments().contains(s2));
		// already present location
		Segment.setComparingX(-50);
		Segment s3 = new Segment(new Point(-50, -200), new Point(50,200));
		Intersection i2 = new Intersection(new Point(0, 0), s1, s3);
		intersections.add(i2);
		interAt0 = new ArrayList<Intersection>(intersections.at(0));
		assertEquals(1, interAt0.size());
		assertEquals(new Point(0, 0), interAt0.get(0).getLocation());
		assertEquals(3, interAt0.get(0).getSegments().size());
		assertTrue(interAt0.get(0).getSegments().contains(s1));
		assertTrue(interAt0.get(0).getSegments().contains(s2));
		assertTrue(interAt0.get(0).getSegments().contains(s3));
		// New location
		Segment s4 = new Segment(new Point(-200, 0), new Point(0, -200));
		Intersection i3 = new Intersection(new Point(-100, -100), s3, s4);
		intersections.add(i3);
		ArrayList<Intersection> interAtX;
		interAtX = new ArrayList<Intersection>(intersections.at(-100));
		assertEquals(1, interAtX.size());
		assertEquals(new Point(-100, -100), interAtX.get(0).getLocation());
		assertEquals(2, interAtX.get(0).getSegments().size());
		assertTrue(interAtX.get(0).getSegments().contains(s3));
		assertTrue(interAtX.get(0).getSegments().contains(s4));
	}

	@Test
	public void testSize() {
		IntersectionSet intersections = new IntersectionSet();
		assertEquals(0, intersections.size());
		// After first insertion
		Segment s1 = new Segment(new Point(-100, 100), new Point(100, -100));
		Segment s2 = new Segment(new Point(-100, -100), new Point(100, 100));
		Intersection i1 = new Intersection(new Point(0, 0), s1, s2);
		intersections.add(i1);
		assertEquals(1, intersections.size());
		// After insertion in already present location
		Segment s3 = new Segment(new Point(-200, -200), new Point(200,200));
		Intersection i2 = new Intersection(new Point(0, 0), s1, s3);
		intersections.add(i2);
		assertEquals(1, intersections.size());
		// After insertion in new location
		Segment s4 = new Segment(new Point(-200, 0), new Point(0, -200));
		Intersection i3 = new Intersection(new Point(-100, -100), s3, s4);
		intersections.add(i3);
		assertEquals(2, intersections.size());
	}

	@Test
	public void testToArray() {
		IntersectionSet intersections = new IntersectionSet();
		assertEquals(0, intersections.toArray().length);
		// After first insertion
		Segment s1 = new Segment(new Point(-100, 100), new Point(100, -100));
		Segment s2 = new Segment(new Point(-100, -100), new Point(100, 100));
		Intersection i1 = new Intersection(new Point(0, 0), s1, s2);
		intersections.add(i1);
		assertEquals(1, intersections.toArray().length);
		assertEquals(i1, intersections.toArray()[0]);
		// After insertion in already present location
		Segment s3 = new Segment(new Point(-200, -200), new Point(200,200));
		Intersection i2 = new Intersection(new Point(0, 0), s1, s3);
		intersections.add(i2);
		assertEquals(1, intersections.toArray().length);
		Intersection i12 = new Intersection(new Point(0, 0), s1, s2);
		i12.addSegmentIfAbsent(s3);
		assertEquals(i12, intersections.toArray()[0]);
		// After insertion in new location
		Segment s4 = new Segment(new Point(-200, 0), new Point(0, -200));
		Intersection i3 = new Intersection(new Point(-100, -100), s3, s4);
		intersections.add(i3);
		assertEquals(2, intersections.toArray().length);
		assertTrue((intersections.toArray()[0].equals(i3) && 
					intersections.toArray()[1].equals(i12)) ||
				   (intersections.toArray()[0].equals(i12) && 
			 		intersections.toArray()[1].equals(i3)));
	}
}