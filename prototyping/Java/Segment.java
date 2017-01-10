import java.util.Objects;
import java.lang.Math;

/**
 * <p>
 * A class that represents a 2-dimensional segment which respect the 
 * following conditions, in order to allow fast computation of 
 * the intersections in a set of segments :
 * </ul>
 * <li>its two endpoints have integer coordinates, included 
 * in [-1000, 1000] ;</li>
 * <li>its two endpoints have different y-coordinate (i.e. the 
 * segment is not vertical).</li>
 * </ul>
 * <p>
 * The behaviour of this class is not guaranteed when it used with
 * coordinates outside [-1000, 1000], or vertical segments
 * </p>
 * <p>
 * Also, note that this class has a natural ordering that is 
 * inconsistent with equals. Two segments are equals if their 
 * endpoints have the same coordinate. But the natural ordering only
 * considers their y-coordinate at a certain x-coordinate and their 
 * gradient. 
 * So, if s1.equals(s2) then s1.compareTo(s2) == s2.compareTo(s1) == 0,
 * but the opposite is not necessarly true.
 * </p>
 * <p>
 * @Author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
class Segment implements Comparable<Segment> {

	/**
	 * x-coordinate at which Segment objects y-coordinate must be
	 * compared when calling compareTo
	 *
	 * @see Segment#setComparingX(x)
	 * @see Segment#compareTo
	 */ 
	private static int comparingX;

	/**
	 * Lower endpoint
	 * @see Segment#getP1()
	 */
	private final Point p1;

	/**
	 * Higher endpoint
	 * @see Segment#getP2()
	 */
	private final Point p2;


	/**
	 * Creates a new Segment of endpoints p1 and p2.
	 *
	 * Its lower endpoint will be the lowest of p1 and p2,
	 * and its higher endpoint will be the highest of p1 and p2.
	 *
	 * p1 and p2 are not allowed to have the same x-coordinate 
	 * (i.e. the Segment is not allowed to be vertical)
	 *
	 * p1's and p2's coordinates should not be outside [-1000, 1000].
	 *
	 * @param p1
	 * @param p2
	 * @throws IllegalArgumentException if p1 and p2 are equal
	 */
	public Segment(Point p1, Point p2) throws IllegalArgumentException {
		if (p1.getX() < p2.getX()) {
			this.p1 = p1;
			this.p2 = p2;
		} else if (p1.getX() > p2.getX()) {
			this.p1 = p2;
			this.p2 = p1;
		} else {
			throw new IllegalArgumentException("A vertical Segment is not allowed");
		}
	}

	/**
	 * Lower endpoint getter.
	 *
	 * @return the lower endpoint of the Segment
	 */
	public Point getP1() {
		return p1;
	}

	/**
	 * Higher endpoint getter.
	 *
	 * @return the higher endpoint of the Segment
	 */
	public Point getP2() {
		return p2;
	}


	/**
	 * If the receiving Segment and the specified Segment are not parallel,
	 * and their intersection is a point, returns the closest integer
	 * coordinates Point.
	 * Else, returns null.
	 *
	 * Note that  intersect If it is a Point returns it, else returns
	 * null.
	 *
	 * Both segments' endpoints' coordinates should not be outside 
	 * [-1000, 1000].
	 */
	public Point intersectionWith(Segment that) {
		Point result = null;
		long thisDx = this.p2.getX() - this.p1.getX();
		long thisDy = this.p2.getY() - this.p1.getY();
		long thatDx = that.p2.getX() - that.p1.getX();
		long thatDy = that.p2.getY() - that.p1.getY();
		// Computes ratio = (this.gradient - that.gradient)* positive_constant
		long ratio = thatDx*thisDy - thisDx*thatDy;
		if (ratio != 0) {
			// Computes the barycentric coordinates of the intersection.
			long interDx = that.p2.getX() - this.p2.getX();
			long interDy = that.p2.getY() - this.p2.getY();
			long a = thatDy*interDx - thatDx*interDy;
			long b = thisDy*interDx - thisDx*interDy;
			// Checks if the intersection is inside both segments.
			if ((0 < ratio && 0 <= a && a <= ratio && 0 <= b && b <= ratio) || 
				(0 > ratio && 0 >= a && a >= ratio && 0 >= b && b >= ratio)) {
					Double x = ((a*this.p1.getX() + (ratio-a)*this.p2.getX()) / 
								(double) ratio);
					Double y = ((a*this.p1.getY() + (ratio-a)*this.p2.getY()) / 
								(double) ratio);
					result = new Point((int) Math.round(x), (int) Math.round(y));
			}
		}
		return result;
	}

	/**
	 * Returns the number of intersections between the receiving Segment and a
	 * circle of specified center point and radius : 0, 1 or 2.
	 * 
	 * @param center  The center point of the circle
	 * @param radius  The radius of the circle
	 * @return the number of intersection between the segment and the circle
	 */
	public int nbIntersectionsWithCircle(Point center, int radius) {
		int res = 0;
		long dx12 = p1.getX()-p2.getX();
		long dx2c = p2.getX()-center.getX();
		long dy12 = p1.getY()-p2.getY();
		long dy2c = p2.getY()-center.getY();
		// Computes the barycentric coordinate alpha, which is solution of
		// | (y-c.y)^2 + (x-c.x)^2 = r^2
		// | y = alpha*p1.y + (1-alpha)*p2.y
		// | x = alpha*p1.x + (1-alpha)*p2.x
		// which is equivalent to : a*alpha^2 + b*alpha + c = 0 where:
		long a = dx12*dx12 +  dy12*dy12;
		long b = 2 * (dx2c*dx12 + dy2c*dy12);
		long c = dx2c*dx2c + dy2c*dy2c - radius*radius;
		long delta = b*b - 4*a*c;
		double[] almostAlphas = new double[0];
		if (delta >= 0) {
			double sqrt = Math.sqrt(delta);
			if (delta > 0) {
				almostAlphas = new double[] {sqrt-b, -sqrt-b};
			} else {
				almostAlphas = new double[] {sqrt};
			}
		}
		for (double almostAlpha : almostAlphas) {
			if (0 <= almostAlpha && almostAlpha <= 2*a) {
				res ++;
			}
		}
		return res;
	}

	/**
	 * Sets the x-coordinate at which Segment objects y-coordinate 
	 * must be compared.
	 *
	 * This x-coordinate should not be set outside [-1000, 1000].
	 */
	public static void setComparingX(int x) {
		comparingX = x;
	}

	/**
	 * Segment objects are ordered by the y-coordinates of the lines 
	 * that contain them, at the x-coordinate comparingX, then by 
	 * gradient (vertical segments are considered to have an infinite
	 * gradient).
	 *
	 * @see Segment#comparingX
	 * @param s
	 * @returns a negative integer, 0, or a positive integer depending
	 * 			on whether the receiving Segment is less than, equal to
	 * 			or greater than the specified Segment s
	 */
	public int compareTo(Segment s) {
		long thisDx = p2.getX() - p1.getX();
		long sDx = s.p2.getX() - s.p1.getX();
		// Computes res = (this.yAtComparingX - s.yAtComparingX)*sDx*thisDx
		long res = (thisDx*sDx*(p1.getY()-s.p1.getY()) +
			(p2.getY()-p1.getY())*(comparingX-p1.getX())*sDx -
			(s.p2.getY()-s.p1.getY())*(comparingX-s.p1.getX())*thisDx);
		if (res == 0) {
			// Computes res = (this.gradient - s.gradient)*sDx*thisDx
			res = sDx*(p2.getY()-p1.getY()) - thisDx*(s.p2.getY()-s.p1.getY());
		} 
		return (int) Long.signum(res);
	}

	/**
	 * Two Segment objects are equal if their endpoints are equal.
	 *
	 * @param o.
	 * @return True if and only if o is a Segment, of which the 
	 *          endpoints are equal to those of the receiving Segment.
	 */
	public boolean equals(Object o) {
		if (!(o instanceof Segment)) {
			return false;
		} else {
			Segment that = (Segment) o;
			return this.p1.equals(that.p1) && this.p2.equals(that.p2);
		}
	}

	/**
	 * Returns a hash code value for the Segment.
	 * Two equal Segment objects have the same hash code value.
	 *
	 * @return a hash code value for the Segment.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(p1, p2);
	} 

	/**
	 * Returns a String representation of this Segment.
	 *
	 * @return a String representation of this Segment 
	 */
	@Override
	public String toString() {
		return String.format("[%s, %s]", p1.toString(), p2.toString());
	}
}