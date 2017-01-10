package fr.pyvain.websight.websight.Geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * <p>
 * A class that represents a set of intersections, with O(log(N)) time access
 * to all the intersections at a given x-coordinate, or to an intersection at
 * a given location. 
 * </p>
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public final class IntersectionSet {

	/**
	 * Double hashmap which associate to each x-coordinate 
	 * then y-coordinate an intersection
	 */
	private final HashMap<Integer, HashMap<Integer, Intersection>> intersections;

	/**
	 * Initializes an empty intersection set
	 */
	public IntersectionSet() {
		intersections = new HashMap<>();
	}

	/**
	 * Returns a collection view of the intersections at locations
	 * of which the x-coordinate is a given x.
	 *
	 * @param x An x-coordinate
	 * @return A collection view of the intersections at x
	 */
	public Collection<Intersection> at(int x) {
		HashMap<Integer, Intersection> interAtX = intersections.get(x);
		if (interAtX != null) {
			return interAtX.values();
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * Adds the specified Intersection to the set.
	 * If the set already contains an Intersection at the same location,
	 * only adds the specified Intersection's segments to it.
	 *
	 * @param newInter  The intersection to add
	 */
	public void add(Intersection newInter) {
		CPoint p = newInter.getLocation();
		int x = p.getX();
		int y = p.getY();
		HashMap<Integer, Intersection> intersAtX = intersections.get(x);
		if (intersAtX == null) {
			intersAtX = new HashMap<>();
			intersections.put(x, intersAtX);
		}
		Intersection interAtXY = intersAtX.get(y);
		if (interAtXY == null) {
			intersAtX.put(y, newInter);
		} else {
			for (Segment s : newInter.getSegments()) {
				interAtXY.addSegmentIfAbsent(s);
			}
		}
	}

	/**
	 * Returns the number of intersection points
	 * contained in this set.
	 *
	 * @return the number of intersection points contained in this set
	 */
	public int size() {
		int result = 0;
		for (HashMap<Integer, Intersection> atX : intersections.values()) {
			result += atX.size();
		}
		return result;
	}

	/**
	 * Returns the whole set in a list
	 * 
	 * @return all the intersection points contained in this set,
	 *         in an array.
	 */
	public Intersection[] toArray() {
		ArrayList<Intersection> result = new ArrayList<>();
		for (HashMap<Integer, Intersection> atX : intersections.values()) {
			result.addAll(atX.values());
		}
		Intersection[] array = new Intersection[0];
		return result.toArray(array);
	}
}