import java.util.TreeSet;
import java.util.SortedSet;
import java.util.Collections;

/**
 * <p>
 * A class that represents an intersection between 2 or more 
 * Segment objects at a given Point.
 * </p>
 * <p>
 * @Author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public final class Intersection {

	/**
	 * Location of the intersection.
	 */
	protected final Point location;

	/**
	 * Segments that form the intersection; 
	 */
	private final TreeSet<Segment> segments;

	/**
	 * Initializes a new Intersection, given two segments and a 
	 * location.
	 *
	 * @param location Location of the intersection
	 * @param segment1 First intersecting segment
	 * @param segment2 Second intersecting segment
	 */
	public Intersection(Point location, Segment segment1, Segment segment2) {
		this.location = location;
		segments = new TreeSet<Segment>();
		segments.add(segment1);
		segments.add(segment2);
	}

	/**
	 * Location getter
	 *
	 * @return the location of the intersection
	 */
	public Point getLocation() {
		return location;
	}

	/**
	 * returns an iter
	 *
	 * @return a list of the intersecting segments.  
	 */
	public SortedSet<Segment> getSegments() {
		return Collections.unmodifiableSortedSet(segments);
	}

	/**
	 * Adds a new segment to the intersection if it is not already
	 * in it.
	 */
	public void addSegmentIfAbsent(Segment segment) {
		segments.add(segment);
	}

	/**
	 * Two Intersection objects are equal if and only if their location,
	 * and segment are equals.
	 *
	 * @param o.
	 * @return True if and only if o is an Intersection, and has the
	 *         same location and segments as the receiving Intersection.
	 */
	public boolean equals(Object o) {
		if (!(o instanceof Intersection)) {
			return false;
		} else {
			Intersection that = (Intersection) o;
			SortedSet<Segment> thisSegments = this.getSegments();
			SortedSet<Segment> thatSegments = that.getSegments();

			boolean equal = (this.location.equals(that.location) &&
				thisSegments.size() == thatSegments.size() &&
				thisSegments.containsAll(thatSegments));
			return equal;
		}
	}

	/**
	 * Returns a String representation of the Intersection.
	 *
	 * @return a String representation of the Intersection. 
	 */
	@Override
	public String toString() {
		String result;
		result = String.format("%s, intersection of segments :\n", 
			location.toString());
		for (Segment segment : segments) {
			result += String.format("%s\n", segment.toString());
		}
		return result; 
	}
}