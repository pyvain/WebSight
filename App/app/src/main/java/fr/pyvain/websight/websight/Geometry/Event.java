package fr.pyvain.websight.websight.Geometry;

import android.support.annotation.NonNull;

/**
 * <p>
 * A class that represents an event, which is either the left endpoint
 * or the right endpoint of a Segment. Hence, an event is defined by :
 * <ul>
 * <li> a Segment ;</li> 
 * <li> a CPoint which is either the left endpoint or the right endpoint
 * of the segment, and is the location of the event.</li>
 * </ul>
 * </p>
 * <p>
 * Note : this class has a natural ordering that is inconsistent with 
 * equals. Two Event objects are equals if they have the same location 
 * and segment. The natural ordering only considers their location.
 * So, e1.equals(e2) implies e1.compareTo(e2) but the opposite is not
 * necessarly true.
 * </p>
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class Event implements Comparable<Event> {

	/**
	 * Location of the event.
	 */
	private final CPoint location;

	/**
	 * Segment of the event.
	 */
	private final Segment segment;

	/**
	 * A boolean indicating if the event is a left endpoint or a 
	 * right endpoint.
	 */
	private final boolean isLeft;

	/**
	 * Initializes a new Event, given a Segment, and a boolean which
	 * must be either :
	 * true for a left endpoint event,
	 * false for a right endpoint event.
	 * @param segment segment
	 * @param isLeft true for a left endpoint, right for a right endpoint
	 */
	public Event(Segment segment, boolean isLeft) {
		this.segment = segment;
		this.isLeft = isLeft;
		if (isLeft) {
			this.location = segment.getP1();
		} else {
			this.location = segment.getP2();
		}
	}

	/**
	 * Location getter
	 *
	 * @return the location of the event
	 */
	public CPoint getLocation() {
		return location;
	}

	/**
	 * Segment getter.
	 *
	 * @return the segment of which the event is an endpoint. 
	 */
	public Segment getSegment() {
		return segment;
	}

	/**
	 * Left and right endpoint distinguisher.
	 *
	 * @return true if this event is a left endpoint event,
	 *         false if it is a right endpoint event
	 */
	public boolean isLeftEndpoint() {
		return isLeft;
	}

	/**
	 * Event objects are ordered by location.
	 *
	 * @param e an Event to compare the receiving event with
	 * @return a negative integer, 0, or a positive integer depending
	 * 			on whether the receiving Event is less than, equal to
	 * 			or greater than the specified Event e
	 */
	public int compareTo(@NonNull Event e) {
		return this.location.compareTo(e.location);
	}

	/**
	 * Two Event objects are equal if and only if their location,
	 * endpoint, and segment are equals.
	 *
	 * @param o an Event object to compare the receiving event with
	 * @return True if and only if o is an Event, and represents
	 *         the same event as the receiving Event.
	 */
	public boolean equals(Object o) {
		if (!(o instanceof Event)) {
			return false;
		} else {
			Event that = (Event) o;
			return ((this.isLeft == that.isLeft) &&
					(this.location.equals(that.location)) &&
					(this.segment.equals(that.segment)));
		}
	}

	/**
	 * Return a hash code value for the Event, such as
	 * two equal Event objects have the same hash code value.
	 *
	 * @return a hash code value for the Event.
	 */
	@Override
	public int hashCode() {
        // Good implementation propose in Josh Bloch's Effective Java
        int result = 13;
		result = 37 * result + (isLeft ? 0 : 1);
		result = 37 * result + location.hashCode();
		result = 37 * result + segment.hashCode();
		return result;
	}

	/**
	 * Returns a String representation of the Event.
	 *
	 * @return a String representation of the Event 
	 */
	@Override
	public String toString() {
		String endpointString;
		if (isLeft) {
			endpointString = "left";
		} else {
			endpointString = "right"; 
		}
		return String.format("%s : %s endpoint event of segment %s", 
			location.toString(), endpointString, segment.toString());
	}
}