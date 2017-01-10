package fr.pyvain.websight.websight.Geometry;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * <p>
 * A class that represents a priority queue, used to store the future
 * Event objects that will be swept over in the Bentley-Ottmann algorithm, 
 * sorted in natural order. 
 * </p>
 * <p>
 * Note that if the algorithm is applied on a set of N segments, 
 * there are exactly 2*N endpoints in the queue at the beginning, and
 * this number decrease throughout the algorithm to reach 0 at the end.
 * </p>
 * <p>
 * With this implementation, initializing the event queue with N segments
 * needs O(N*log(N)) time, and getting the next event needs O(1) time.
 * </p>
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
final class EventQueue {

	/**
	 * List containing all the events, sorted in natural order.
	 */
	private final ArrayList<Event> events;

	/**
	 * Index of the next event.
	 */
	private int next;

	/**
	 * Hash map used to detect if two segments of the input 
	 * have a common endpoint, which would cause the algorithm to fail.
	 */
	private HashMap<CPoint, Event> finder;

	/**
	 * Initializes a new event queue with the endpoints of a given
	 * collection of segments.
	 *
	 * The segments are not allowed to have common endpoints, if they
	 * do, throws an IllegalArgumentException.
	 * 
	 * @param segments Segments whose endpoint events mut be added to the queue
	 * @throws IllegalArgumentException if at least two segments 
	 *         have a common endpoint.
	 */
	public EventQueue(Collection<Segment> segments) throws IllegalArgumentException {
		int capacity = 2*segments.size();
		events = new ArrayList<>(capacity);
		finder = new HashMap<>(capacity);
		next = 0;
		// Adds each endpoint of each segment
		for (Segment s : segments) {
			add(new Event(s, true));
			add(new Event(s, false));
		}
		// Sorts the events
		Collections.sort(events);
		// Release the event finder, which is only used during initialization
		finder = null;
	}

	/**
	 * Tries to add a given event to the queue, raising an Illegal
	 * if an event with the same location is already in the queue.
	 *
	 * @param event Event to add to the queue
	 * @throws IllegalArgumentException if an event with the same
	 *         location is already in the queue 
	 */
	private void add(Event event) throws IllegalArgumentException {
		CPoint location = event.getLocation();
		if (finder.get(location) != null) {
			throw new IllegalArgumentException("The queue already" +
				 	" contains an event with the same location");
		} else {
			events.add(event);
			finder.put(location, event);
		}
	}

	/**
	 * Retrieves and removes the next Event of the queue, or null
	 * if it is empty.
	 *
	 * @return the next Event of the queue or null if it is empty
	 */
	public Event nextEvent() {
		if (next < events.size()) {
			return events.get(next++);
		} else {
			return null;
		}
	}
}