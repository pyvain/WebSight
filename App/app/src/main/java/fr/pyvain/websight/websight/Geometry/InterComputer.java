package fr.pyvain.websight.websight.Geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * <p>
 * A class containing tools to help generating graph drawings
 * with reduced number of crossings.
 * </p>
 *
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class InterComputer {

    /**
     * Minimal number of segments so that the Bentley Ottman implementation
	 * outrun the naive method. In other words : <ul>
     *     <li>if the size of the segment set is less than EQ_SIZE,
     *     then the naive method is faster</li>
     *     <li>else, the Bentley Ottmann method is faster</li>
     * </ul>
     */
    private static final int EQ_SIZE = 90;


	/**
	 * Private auxiliary method for checking the intersection of
	 * two non parallel segments that might be null.
     *
     * Undefined behaviour if one of the segments is outside
     * [-1000, 1000] (long overflow)
	 *
	 * @param s1 segment that might be null
	 * @param s2 segment that might be null
	 * @return the intersection between s1 and s2 else if none of
     * them is null, they are not parallel and they intersect ;
     * null else.
	 */
	private static Intersection interBetween(Segment s1, Segment s2) {
		Intersection res = null;
		if (s1 != null && s2 != null) {
			CPoint p = s1.intersectionWith(s2);
			if (p != null) {
				res = new Intersection(p, s1, s2);
			}
		}
		return res;
	}

    /**
     * Computes the intersections in the specified set of Segments,
     * using the fastest method between naive and Bentley Ottmann with
     * a data set of this size.
     *
     * For the time being, only the intersections between non parallel
     * segments are computed, in order to make the computation faster
     * (and easier).
     *
     * Undefined behaviour if one of the segments is outside
     * [-1000, 1000] x [-1000, 1000] (caused by long overflow)
     *
     * @param segments  The set of segments to analyse
     * @return the intersections in the specified set of segments
     */
    public static IntersectionSet edgeIntersections(Collection<Segment> segments) {
        if (segments.size() < EQ_SIZE) {
            return edgeIntersectionsNaive(segments);
        } else {
            return edgeIntersectionsBO(segments);
        }
    }

    /**
     * Computes the intersections in the specified set of Segments,
	 * using a naive quadratic algorithm which consists in testing
     * all pairs of segments
     *
     * For the time being, only the intersections between non parallel
     * segments are computed, in order to make the computation faster
     * (and easier).
     *
     * Undefined behaviour if one of the segments is outside
     * [-1000, 1000] (caused by long overflow)
     *
     * @param segments  The set of segments to analyse
     * @return the intersections in the specified set of segments
     */
    public static IntersectionSet edgeIntersectionsNaive(Collection<Segment> segments) {
        List<Segment> lSegments = new ArrayList<>(segments);
        // Contains the intersections found so far
        IntersectionSet intersections = new IntersectionSet();
        for (int i = 0; i < lSegments.size(); i++) {
            Segment s1 = lSegments.get(i);
            for (int j = i+1; j < lSegments.size(); j++) {
                Segment s2 = lSegments.get(j);
                Intersection inter = interBetween(s1, s2);
                if (inter != null) {
                    intersections.add(inter);
                }
            }
        }
        return intersections;
    }

    /**
     * Returns a copy of the specified segment collections,
     * with slight modifications so that there are no vertical segments
     * and no segments with a common endpoint.
     *
     * @param segments Collection of segment to disturb
     * @return A slightly disturbed copy of the specified collection of
     * segment, without any vertical segment or segments with a common
     * endpoint.
     */
    private static List<Segment> adjustForBO(Collection<Segment> segments) {
        int S = segments.size();
        List<Segment> result = new ArrayList<>(S);
        // Stores the endpoints already encountered
        Set<Integer> mem = new HashSet<>();
        for (Segment s : segments) {
            CPoint p1 = s.getP1();
            CPoint p2 = s.getP2();
            int x1 = p1.getX();
            int y1 = p1.getY();
            int x2 = p2.getX();
            int y2 = p2.getY();
            // If needed, disturbs the points a bit to avoid
            // vertical segments / segments with same endpoints
            int dy = (y1 < y2) ? 1 : -1;
            if (x1 == x2 || mem.contains(S*x1+y1) || mem.contains(S*x2+y2)) {
                if (x1 == x2) {
                    x2++;
                }
                while (mem.contains(S*x1+y1)) {
                    y1 += dy;
                }
                while (mem.contains(S*x2+y2)) {
                    y2 -= dy;
                }
            }
            p1 = new CPoint(x1, y1);
            p2 = new CPoint(x2, y2);
            result.add(new Segment(p1, p2));
            mem.add(S*x1+y1);
            mem.add(S*x2+y2);
        }
        return result;
    }

	/**
	 * Computes the intersections in the specified set of Segments, using
     * a simplified implementation of the Bentley Ottmann algorithm.
     *
     * The computation is actually done on a copy of the specified set of
     * Segments which is disturbed a bit so that it does not contain a vertical
	 * segment, or two or more segments with the same endpoint.
     *
     * For the time being, only the intersections between non parallel
     * segments are computed, in order to make the computation faster
     * (and easier).
	 *
     * Undefined behaviour if one of the segments is outside
     * [-1000, 1000] x [-1000, 1000] (caused by long overflow)
     *
	 * @param segments  The set of segments to analyse
	 * @return the intersections in the specified set of segments
	 */
	public static IntersectionSet edgeIntersectionsBO(Collection<Segment> segments) {
        // Disturbs the input a bit to avoid vertical segments, and
        // segments with same endpoints
        List<Segment> lSegments = adjustForBO(segments);

        IntersectionSet result = new IntersectionSet();
        // Contains the segments endpoint events by ascending locations
        EventQueue eventQueue = new EventQueue(lSegments);
		// Contains the x-coordinates of the next segment intersections
		// sorted by increasing values.
		TreeSet<Integer> interX = new TreeSet<>();
		// Contains at any moment all the segments that intersect with
		// the sweep line in its current position, sorted in natural order
		TreeSet<Segment> sweepLine = new TreeSet<>();

		Event e;
		int prevX = 0;
		while ((e = eventQueue.nextEvent()) != null) {
			int x = e.getLocation().getX();
			// If the sweep line moves to a new x-coordinate which is beyond
			// the next segments intersections, reorders the intersecting
			// segments by removing them, updating comparingX, and readding them
			while ((x != prevX) && !interX.isEmpty() && (interX.first() <= x)) {
				int nextX = interX.pollFirst();
                for (Intersection i : result.at(nextX)) {
					for (Segment segment : i.getSegments()) {
						sweepLine.remove(segment);
					}
				}
				Segment.setComparingX(nextX);
                // new found intersections are first stored in newInters
                // while result.at(nextX) is browsed, then added after
                // the loop, to avoid ConcurrentModificationException
                List<Intersection> newInters = new ArrayList<>();
				for (Intersection i : result.at(nextX)) {
					for (Segment segment : i.getSegments()) {
						sweepLine.add(segment);
					}
					// Checks intersection between the highest (resp. lowest)
					// segment of each intersection and the one above (resp.
                    // below) it in the sweep line
                    Segment high = i.getSegments().first();
                    Segment low = i.getSegments().last();
                    Intersection[] mustCheck = new Intersection[]{
                            interBetween(high, sweepLine.higher(high)),
                            interBetween(low, sweepLine.lower(low))};
                    for (Intersection inter : mustCheck) {
						if (inter != null) {
							newInters.add(inter);
                            if (inter.getLocation().getX() > i.getLocation().getX()) {
                                interX.add(inter.getLocation().getX());
                            }
						}
					}
				}
                for (Intersection i : newInters){
                    result.add(i);
                }
			}

			Segment.setComparingX(x);
			// Now that the sweep line is sorted, handles the event
			if (e.isLeftEndpoint()) {
				Segment s = e.getSegment();
				sweepLine.add(s);
				// Checks the intersections between s and the segments
				// above and below in the sweep line.
                Intersection[] mustCheck = new Intersection[]{
                        interBetween(s, sweepLine.higher(s)),
                        interBetween(s, sweepLine.lower(s))};
				for (Intersection i : mustCheck) {
					if (i != null) {
                        result.add(i);
                        if (i.getLocation().getX() > e.getLocation().getX()) {
                            interX.add(i.getLocation().getX());
                        }
					}
				}
			} else {
				Segment s = e.getSegment();
				sweepLine.remove(s);
			}
			prevX = x;
		}
		return result;
	}

    /**
     * Returns the number of times the segments of the specified set
     * cross the circles of specified centers and radius
     *
     * Undefined behaviour if one of the segments is outside
     * [-1000, 1000] x [-1000, 1000] (caused by long overflow)
     *
     * @param segments Segments to analyse
     * @param centers  Centers of the circles to analyse
     * @param radius   Common radius of the circles to analyse
     * @return the number of times the segments of the specified set
     * cross the circles of specified centers and radius
     */
	public static int nbVertexCrossings(Collection<Segment> segments,
                                        Collection<CPoint> centers, float radius) {
		int res = 0;
		for (Segment s : segments) {
			for (CPoint c : centers) {
                if (s.nbIntersectionsWithCircle(c, radius) > 0) {
                    res++;
                }
			}
		}
		return res;
	}
}