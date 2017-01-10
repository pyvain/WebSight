import java.util.TreeSet;
import java.util.Collection;


/**
 * <p>
 * A class containing tools to compute the intersections between a set
 * of segments, or a set of segments and a set of circles.
 * </p>
 *
 * <p>
 * @Author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class InterComputer {

	/**
	 * Private auxiliary method for checking the intersection of 
	 * two segments that might be null.
	 *
	 * @param s1 segment that might be null
	 * @param s2 segment that might be null
	 * @return null if s1 or s2 is null, or s1 and s2 does not intersect
	 *         the intersection between s1 and s2 else
	 */
	private static Intersection interBetween(Segment s1, Segment s2) {
		Intersection res = null;
		if (s1 != null && s2 != null) {
			Point p = s1.intersectionWith(s2);
			if (p != null) {
				res = new Intersection(p, s1, s2);
			}
		}
		return res;
	}


	/**
	 * Computes the intersections between the given set of Segments.
	 * Note : This is a simplified implementation of the Bentley Ottmann
	 * algorithm. The specified set of Segments must not contain a vertical
	 * segment, or two or more segments with the same endpoint. 
	 * It can contain 3 or more Segments which intersect simultaneously.
	 *
	 * @param segments  The set of segments to analyse
	 * @throw IllegalArgumentException if the set of Segments is invalid
	 * @return the intersections in the specified set of segments
	 */
	public static IntersectionSet intersections(Collection<Segment> segments) 
	throws IllegalArgumentException {
		// Contains the segments endpoint events by ascending locations
		EventQueue eventQueue = new EventQueue(segments);
		// Contains the intersections found so far
		IntersectionSet intersections = new IntersectionSet();
		// Contains the x-coordinates of the next segment intersections
		// sorted by increasing values.
		TreeSet<Integer> interX = new TreeSet<Integer>();
		// Contains at any moment all the segments that intersect with 
		// the sweep line in its current position, sorted in natural order
		TreeSet<Segment> sweepLine = new TreeSet<Segment>();


		Event e;
		int x;
		int prevX = 0;
		while ((e = eventQueue.nextEvent()) != null) {
			x = e.getLocation().getX(); 
			// If the sweep line moves to a new x-coordinate which is beyond
			// the next segments intersections, reorders the intersecting
			// segments by removing them, updating comparingX, and readding them
			if ((x != prevX) && !interX.isEmpty() && (interX.first() <= x)) {
				int nextX = interX.pollFirst();
				for (Intersection i : intersections.at(nextX)) {
					for (Segment segment : i.getSegments()) {
						sweepLine.remove(segment);
					}
				}
				Segment.setComparingX(nextX);
				for (Intersection i : intersections.at(nextX)) {
					for (Segment segment : i.getSegments()) {
						sweepLine.add(segment);
					}
					// Checks intersection between the highest (resp. lowest)
					// segment of each intersection and the one above (resp. 
					// below) it in the sweep line
					Segment high = i.getSegments().first();
					Segment above = sweepLine.higher(high);
					Intersection i1 = interBetween(high, above);
					Segment low = i.getSegments().last();
					Segment below = sweepLine.lower(low);
					Intersection i2 = interBetween(low, below);
					for (Intersection inter : new Intersection[]{i1, i2}) {
						if (inter != null) {
							intersections.add(inter);
							interX.add(i.getLocation().getX());
						}
					}
				}
			}

			Segment.setComparingX(x);
			// Now that the sweep line is sorted, handles the event
			if (e.isLeftEndpoint()) {
				Segment s = e.getSegment(); 
				sweepLine.add(s);
				// Checks the intersections between s and the segments 
				// above and below in the sweep line.
				Intersection i1 = interBetween(s, sweepLine.higher(s));
				Intersection i2 = interBetween(s, sweepLine.lower(s));
				for (Intersection i : new Intersection[]{i1, i2}) {
					if (i != null) {
						intersections.add(i);
						interX.add(i.getLocation().getX());
					}
				}
			} else {
				Segment s = e.getSegment();
				sweepLine.remove(s);
			}
			prevX = x;
		}
		return intersections;
	}

	public static int nbIntersections(Collection<Segment> segments, 
	Collection<Point> centers, int radius) {
		int res = 0;
		for (Segment s : segments) {
			for (Point c : centers) {
				res += s.nbIntersectionsWithCircle(c, radius);
			}
		}
		return res;
	}

}