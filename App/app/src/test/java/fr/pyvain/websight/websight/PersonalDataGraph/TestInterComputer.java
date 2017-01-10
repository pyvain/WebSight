package fr.pyvain.websight.websight.PersonalDataGraph;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import fr.pyvain.websight.websight.Geometry.CPoint;
import fr.pyvain.websight.websight.Geometry.InterComputer;
import fr.pyvain.websight.websight.Geometry.Intersection;
import fr.pyvain.websight.websight.Geometry.IntersectionSet;
import fr.pyvain.websight.websight.Geometry.Segment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * <p>
 *     @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */


public class TestInterComputer {

	@BeforeClass
	public static void beforeTests() {
		System.out.println("Testing class InterComputer\n");
	}

	@Test
	public void testEdgeIntersectionsNaive() {
		List<Segment> segments = new ArrayList<>();
        List<Segment> horizontals = new ArrayList<>();
        List<Segment> diagonals = new ArrayList<>();
        for (int y = -900; y <= 900; y+=100) {
            Segment s = new Segment(new CPoint(-1000, y), new CPoint(1000, y));
            segments.add(s);
            horizontals.add(s);
        }
        for (int x = -1000; x <= 900; x+=100) {
            Segment s = new Segment(new CPoint(x, -1000), new CPoint(x+100, 1000));
            segments.add(s);
            diagonals.add(s);
        }
        IntersectionSet inters = InterComputer.edgeIntersectionsNaive(segments);
        assertEquals(19*20, inters.size());
        for (Segment s1 : horizontals) {
            for (Segment s2 : diagonals) {
                CPoint p = s1.intersectionWith(s2);
                Intersection inter = new Intersection(p, s1, s2);
                int x = inter.getLocation().getX();
                assertTrue(inters.at(x).contains(inter));
            }
        }
	}

	@Test
	public void testEdgeIntersections() {
		ArrayList<Segment> segments = new ArrayList<>();
        // Empty set of segments
        IntersectionSet inters = InterComputer.edgeIntersectionsBO(segments);
        assertEquals(0, inters.size());
		// Simple intersection
		Segment s1 = new Segment(new CPoint(-100, 100), new CPoint(100, -100));
		Segment s2 = new Segment(new CPoint(-100, -100), new CPoint(100, 100));
		segments.add(s1);
		segments.add(s2);
        inters = InterComputer.edgeIntersections(segments);
        IntersectionSet intersNaive = InterComputer.edgeIntersectionsNaive(segments);
        IntersectionSet intersBO = InterComputer.edgeIntersectionsBO(segments);
		assertEquals(1, inters.size());
        assertEquals(1, intersNaive.size());
        assertEquals(1, intersBO.size());
        Intersection inter1 = new Intersection(new CPoint(0, 0), s1, s2);
		assertTrue(inters.at(0).contains(inter1));
        assertTrue(intersNaive.at(0).contains(inter1));
        assertTrue(intersBO.at(0).contains(inter1));
		// 2 intersections
		Segment s3 = new Segment(new CPoint(0, 100), new CPoint(100, 0));
		segments.add(s3);
		inters = InterComputer.edgeIntersectionsBO(segments);
        intersNaive = InterComputer.edgeIntersectionsNaive(segments);
        intersBO = InterComputer.edgeIntersectionsBO(segments);
        assertEquals(2, inters.size());
        assertEquals(2, intersNaive.size());
        assertEquals(2, intersBO.size());
        Intersection inter2 = new Intersection(new CPoint(50, 50), s2, s3);
        assertTrue(inters.at(0).contains(inter1));
		assertTrue(inters.at(50).contains(inter2));
        assertTrue(intersNaive.at(0).contains(inter1));
        assertTrue(intersNaive.at(50).contains(inter2));
        assertTrue(intersBO.at(0).contains(inter1));
        assertTrue(intersBO.at(50).contains(inter2));
		// One triple and one simple
		Segment s4= new Segment(new CPoint(-100, 200), new CPoint(100, -200));
		segments.add(s4);
		inters = InterComputer.edgeIntersectionsBO(segments);
        intersNaive = InterComputer.edgeIntersectionsNaive(segments);
        intersBO = InterComputer.edgeIntersectionsBO(segments);
        assertEquals(2, inters.size());
        assertEquals(2, intersNaive.size());
        assertEquals(2, intersBO.size());
        inter1.addSegmentIfAbsent(s4);
        assertTrue(inters.at(0).contains(inter1));
        assertTrue(inters.at(50).contains(inter2));
        assertTrue(intersNaive.at(0).contains(inter1));
        assertTrue(intersNaive.at(50).contains(inter2));
        assertTrue(intersBO.at(0).contains(inter1));
        assertTrue(intersBO.at(50).contains(inter2));// A lot of intersections :
        segments = new ArrayList<>();
        List<Segment> horizontals = new ArrayList<>();
        List<Segment> diagonals = new ArrayList<>();
        for (int y = -900; y <= 900; y+=100) {
            Segment s = new Segment(new CPoint(-1000, y), new CPoint(1000, y));
            segments.add(s);
            horizontals.add(s);
        }
        for (int x = -1000; x <= 900; x+=100) {
            Segment s = new Segment(new CPoint(x, -1000), new CPoint(x+100, 1000));
            segments.add(s);
            diagonals.add(s);
        }
        inters = InterComputer.edgeIntersectionsBO(segments);
        intersNaive = InterComputer.edgeIntersectionsNaive(segments);
        intersBO = InterComputer.edgeIntersectionsBO(segments);
        assertEquals(19*20, inters.size());
        assertEquals(19*20, intersNaive.size());
        assertEquals(19*20, intersBO.size());
        for (Segment sA : horizontals) {
            for (Segment sB : diagonals) {
                CPoint p = sA.intersectionWith(sB);
                Intersection inter = new Intersection(p, sA, sB);
                int x = inter.getLocation().getX();
                assertTrue(inters.at(x).contains(inter));
                assertTrue(intersNaive.at(x).contains(inter));
                assertTrue(intersBO.at(x).contains(inter));
            }
        }
	}
}