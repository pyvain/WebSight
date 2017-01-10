package fr.pyvain.websight.websight.Geometry;

import android.support.annotation.NonNull;

import java.util.Locale;

import fr.pyvain.websight.websight.PersonalDataGraph.PolarCoords;

/**
 * <p>
 * A class that represents a 2-dimensional point with
 * constant integer coordinates.
 * </p>
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public final class CPoint implements Comparable<CPoint> {

	/**
	 * x-coordinate.
	 */
	private final int x;

	/**
	 * y-coordinate.
	 */
	private final int y;

	/**
	 * Creates a new CPoint with specified integral cartesian
     * coordinates (x, y).
	 *
	 * @param x x-coordinate of the point
	 * @param y y-coordinate of the point
	 */
	public CPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Creates a new CPoint with specified polar coordinates (x, y)
     * in a system of specified origin and unit distance.
	 *
	 * @param coords  Polar coordinates of the point
     * @param origin Origin of the system
	 * @param unit   Unit distance of the system
	 */
	public CPoint(PolarCoords coords, CPoint origin, float unit) {
        int r = coords.getRadius();
        float theta = coords.getAngle();
        this.x = (int) Math.round(origin.getX() + unit * r * Math.cos(theta));
        this.y = (int) Math.round(origin.getY() + unit * r * Math.sin(theta));
	}

    /**
     * Creates a new CPoint which is a barycenter of the 2 specified points
     * with specified barycentric coordinates.
     *
     * @param p1    First point
     * @param p2    Second point
     * @param alpha Barycentric coordinate of the first point.
     */
    public CPoint(CPoint p1, CPoint p2, float alpha) {
        this.x = Math.round(alpha * p1.getX() + (1 - alpha) * p2.getX());
        this.y = Math.round(alpha * p1.getY() + (1 - alpha) * p2.getY());
    }

	/**
	 * x-coordinate getter.
	 *
	 * @return the x-coordinate of the CPoint
	 */
	public int getX() {
		return x;
	}

	/**
	 * y-coordinate getter.
	 *
	 * @return the y-coordinate of the CPoint
	 */
	public int getY() {
		return y;
	}

	/**
	 * Returns the distance between the receiving CPoint and the
	 * specified point.
	 *
	 * @return the distance between the receiving CPoint and the
	 *         specified point.
	 */
	public float distance(CPoint that) {
		return (float) Math.sqrt(Math.pow(that.x-this.x, 2) +
			Math.pow(that.y-this.y, 2));
	}


	/**
	 * CPoint objects are ordered by x then by y.
	 *
	 * @param that CPoint object to compare with the receiving CPoint
	 * @return a negative integer, 0, or a positive integer depending
	 *          on whether the receiving CPoint is less than, equal to
	 *          or greater than the specified CPoint p.
	 */
	public int compareTo(@NonNull CPoint that) {
		int result = this.x-that.x;
		if (result == 0) {
			result = this.y-that.y;
		} 
		return result;
	}

	/**
	 * Two CPoint objects are equal if they have the same coordinates.
	 *
	 * @param o Object to compare with the receiving CPoint.
	 * @return True if and only if o is a CPoint, of which the
	 *          coordinates are exactly the same as those of the 
	 *          receiving CPoint.
	 */
	public boolean equals(Object o) {
		if (!(o instanceof CPoint)) {
			return false;
		} else {
			CPoint p = (CPoint) o;
			return this.compareTo(p) == 0;
		}
	}

	/**
	 * Returns a hash code value for the CPoint.
	 * Two equal CPoint objects have the same hash code value.
	 *
	 * @return a hash code value for the CPoint.
	 */
	@Override
	public int hashCode() {
        // Good implementation propose in Josh Bloch's Effective Java
        int result = 13;
        result = 37 * result + (x ^ (x >>> 16));
        result = 37 * result + (y ^ (y >>> 16));
		return result;
	} 

	/**
	 * Returns a String representation of the CPoint.
	 *
	 * @return a String representation of the CPoint
	 */
	@Override
	public String toString() {	
		return String.format(Locale.FRENCH, "(%d, %d)", x, y);
	}
}