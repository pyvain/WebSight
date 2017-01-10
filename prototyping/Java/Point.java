import java.util.Objects;
import java.lang.Math;

/**
 * <p>
 * A class that represents a 2-dimensional point with
 * integer coordinates.
 * </p>
 * <p>
 * @Author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public final class Point implements Comparable<Point> {

	/**
	 * x-coordinate.
	 */
	private final int x;

	/**
	 * y-coordinate.
	 */
	private final int y;

	/**
	 * Creates a new Point with specified integer coordinates (x, y).
	 *
	 * @param x.
	 * @param y.
	 */
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * x-coordinate getter.
	 *
	 * @return the x-coordinate of the Point
	 */
	public int getX() {
		return x;
	}

	/**
	 * y-coordinate getter.
	 *
	 * @return the y-coordinate of the Point
	 */
	public int getY() {
		return y;
	}

	/**
	 * Returns the distance between the receiving Point and the
	 * specified point.
	 *
	 * @return the distance between the receiving Point and the
	 *         specified point.
	 */
	public float distance(Point that) {
		return (float) Math.sqrt(Math.pow(that.x-this.x, 2) +
			Math.pow(that.y-this.y, 2));
	}

	/**
	 * Point objects are ordered by x then by y.
	 *
	 * @param p.
	 * @return a negative integer, 0, or a positive integer depending
	 *          on whether the receiving Point is less than, equal to
	 *          or greater than the specified Point p.
	 */
	public int compareTo(Point that) {
		int result = this.x-that.x;
		if (result == 0) {
			result = this.y-that.y;
		} 
		return result;
	}

	/**
	 * Two Point objects are equal if they have the same coordinates.
	 *
	 * @param o.
	 * @return True if and only if o is a Point, of which the 
	 *          coordinates are exactly the same as those of the 
	 *          receiving Point.
	 */
	public boolean equals(Object o) {
		if (!(o instanceof Point)) {
			return false;
		} else {
			Point p = (Point) o;
			return this.compareTo(p) == 0;
		}
	}

	/**
	 * Returns a hash code value for the Point.
	 * Two equal Point objects have the same hash code value.
	 *
	 * @return a hash code value for the Point.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	} 

	/**
	 * Returns a String representation of the Point.
	 *
	 * @return a String representation of the Point 
	 */
	@Override
	public String toString() {	
		return String.format("(%d, %d)", x, y);
	}
}