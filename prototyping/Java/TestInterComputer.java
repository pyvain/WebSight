import java.util.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestInterComputer {

	@BeforeClass
	public static void beforeTests() {
		System.out.println("\n------------------------------");
		System.out.println("Testing class InterComputer");
		System.out.println("------------------------------");
	}

	@AfterClass
	public static void afterTests() {
		System.out.println("\n------------------------------\n");
	}

	@Test 
	public void testIntersections1() {
		// Empty set of segments
		ArrayList<Segment> segments = new ArrayList<Segment>();
		Intersection[] inters = InterComputer.intersections(segments).toArray();
		assertEquals(0, inters.length);
		// Simple intersection
		Segment s1 = new Segment(new Point(-100, 100), new Point(100, -100));
		Segment s2 = new Segment(new Point(-100, -100), new Point(100, 100));
		segments.add(s1);
		segments.add(s2);
		inters = InterComputer.intersections(segments).toArray();
		assertEquals(1, inters.length);
		assertEquals(new Intersection(new Point(0, 0), s1, s2), inters[0]);
		// 2 intersections
		Segment s3 = new Segment(new Point(0, 100), new Point(100, 0));
		segments.add(s3);
		inters = InterComputer.intersections(segments).toArray();
		assertEquals(2, inters.length);
		assertEquals(new Intersection(new Point(0, 0), s1, s2), inters[0]);
		assertEquals(new Intersection(new Point(50, 50), s2, s3), inters[1]);
		// One triple and one simple
		Segment s4= new Segment(new Point(-100, 200), new Point(100, -200));
		segments.add(s4);
		inters = InterComputer.intersections(segments).toArray();
		assertEquals(2, inters.length);
		Intersection expected1 = new Intersection(new Point(0, 0), s1, s2);
		expected1.addSegmentIfAbsent(s4);
		assertEquals(expected1, inters[0]);
		assertEquals(new Intersection(new Point(50, 50), s2, s3), inters[1]);
	}

	@Test 
	public void testIntersections2() {
		// 4 segments :
		// The two higher and the two lower intersect at same X but
		// different Y then one higher and one lower intersect
		Segment s1 = new Segment(new Point(-100, 400), new Point(300, 0));
		Segment s2 = new Segment(new Point(-100, 200), new Point(100, 400));
		Segment s3 = new Segment(new Point(-100, 0), new Point(100, -200));
		Segment s4 = new Segment(new Point(-100, -200), new Point(300, 200));
		ArrayList<Segment> segments = new ArrayList<Segment>();
		segments.add(s1);
		segments.add(s2);
		segments.add(s3);
		segments.add(s4);
		Intersection[] inters = InterComputer.intersections(segments).toArray();
		assertEquals(3, inters.length);
		assertEquals(new Intersection(new Point(0, -100), s3, s4), inters[0]);
		assertEquals(new Intersection(new Point(0, 300), s1, s2), inters[1]);
		assertEquals(new Intersection(new Point(200, 100), s1, s4), inters[2]);
	}

	
}