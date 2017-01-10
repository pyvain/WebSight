package fr.pyvain.websight.websight.SweetGraphs;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <p>This class represents a Segment by its two end points and its thickness.
 * It allows to check if it intersects with another Segment, or a Circle.
 *
 * Note : this implementation does not take into account the thickness of a Segment
 * when computing any intersection. The thickness is only here for drawing purposes.</p>
 *
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class Segment implements Parcelable {

    private final static int DECIMALS = 4;

    private Point leftEnd;
    private Point rightEnd;
    private float thickness;

    public Segment(Point leftEnd, Point rightEnd, float thickness) {
        this.leftEnd = leftEnd;
        this.rightEnd = rightEnd;
        this.thickness = thickness;
    }

    public Point getLeftEnd() {
        return leftEnd;
    }

    public void setLeftEnd(Point leftEnd) {
        this.leftEnd = leftEnd;
    }

    public Point getRightEnd() {
        return rightEnd;
    }

    public void setRightEnd(Point rightEnd) {
        this.rightEnd = rightEnd;
    }

    public float getThickness() {
        return thickness;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

    /**
     * Computes the intersection between the specified segment and the
     * receiving segment, only if they are not parallel.
     * @param that Segment to compute the intersection with
     * @return null if the segment are parallel or non intersecting, their
     * intersection else
     */
    public Point intersectionWith(Segment that) {
        Point intersection = null;
        float dx11 = this.rightEnd.getX() - this.leftEnd.getX();
        float dy11 = this.rightEnd.getY() - this.leftEnd.getY();
        float dx22 = that.rightEnd.getX() - that.leftEnd.getX();
        float dy22 = that.rightEnd.getY() - that.leftEnd.getY();
        // ratio of gradients
        float r = dx22*dy11 - dx11*dy22;
        if (r != 0) {
            float dx21 = that.rightEnd.getX() - this.rightEnd.getX();
            float dy21 = that.rightEnd.getY() - this.rightEnd.getY();
            // barycentric coordinates of the intersection point.
            float a = (dy22*dx21 - dx22*dy21) / r;
            float b = (dy11*dx21 - dx11*dy21) / r;
            // Checks if the intersection is inside both segments.
            if (0 <= a && a <= 1 && 0 <= b && b <= 1) {
                float x = a*this.leftEnd.getX() + (1-a)*this.rightEnd.getX();
                float y = a*this.leftEnd.getY() + (1-a)*this.rightEnd.getY();
                // + 0.0 to avoid negative zeros
                intersection = new Point(0.0f + x, 0.0f + y);
            }
        }
        return intersection;
    }

    /**
     * Computes if the specified segment and the receiving segment intersects,
     * only if they are not parallel.
     * @param that Segment to compute the intersection with
     * @return false if the segment are parallel or non intersecting, true else
     */
    public boolean intersectsWith(Segment that) {
        float dx11 = this.rightEnd.getX() - this.leftEnd.getX();
        float dy11 = this.rightEnd.getY() - this.leftEnd.getY();
        float dx22 = that.rightEnd.getX() - that.leftEnd.getX();
        float dy22 = that.rightEnd.getY() - that.leftEnd.getY();
        // ratio of gradients
        float r = dx22*dy11 - dx11*dy22;
        if (r != 0) {
            // barycentric coordinates of the intersection point.
            float dx21 = that.rightEnd.getX() - this.rightEnd.getX();
            float dy21 = that.rightEnd.getY() - this.rightEnd.getY();
            float a = (dy22 * dx21 - dx22 * dy21) / r;
            float b = (dy11 * dx21 - dx11 * dy21) / r;
            // checks if the intersection is inside both segments.
            if (0 <= a && a <= 1 && 0 <= b && b <= 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Computes if the receiving segment intersects with the specified Circle.
     * @param circle Circle to compute the intersection with
     * @return false if the segment and the circle are non intersecting, true else
     */
    public boolean intersectsWith(Circle circle) {
        double dx12 = leftEnd.getX()-rightEnd.getX();
        double dx2c = rightEnd.getX()-circle.getCenter().getX();
        double dy12 = leftEnd.getY()-rightEnd.getY();
        double dy2c = rightEnd.getY()-circle.getCenter().getY();
        // Basically solves
        // | (y-c.y)^2 + (x-c.x)^2 = r^2
        // | y = alpha*leftEnd.y + (1-alpha)*rightEnd.y
        // | x = alpha*leftEnd.x + (1-alpha)*rightEnd.x
        double a = Math.pow(dx12, 2) +  Math.pow(dy12, 2);
        double b = 2 * (dx2c*dx12 + dy2c*dy12);
        double c = Math.pow(dx2c, 2) + Math.pow(dy2c, 2) - Math.pow(circle.getRadius(), 2);
        double delta = Math.pow(b, 2) - 4*a*c;
        if (delta > 0) {
            double l1 = Math.pow(b + 2*a, 2);
            double l2 = Math.pow(b, 2);
            double lmin = Math.min(l1, l2);
            double lmax = Math.max(l1, l2);
            return ((-2*a < b) && (b <= 0) && (delta <= lmax) ||
                    (lmin <= delta) && (delta <= lmax));
        } else if (delta == 0 && (0 <= -b) && (-b <= 2*a)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Segment segment = (Segment) o;
        return (Math.abs(segment.thickness - thickness) < Math.pow(10, -DECIMALS) &&
                leftEnd.equals(segment.leftEnd) &&
                rightEnd.equals(segment.rightEnd));
    }

    @Override
    public int hashCode() {
        int result = leftEnd.hashCode();
        result = 31 * result + rightEnd.hashCode();
        result = 31 * result + Math.round(thickness * (float)Math.pow(10, DECIMALS));
        return result;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(leftEnd, flags);
        out.writeParcelable(rightEnd, flags);
        out.writeFloat(thickness);
    }

    public static final Parcelable.Creator<Segment> CREATOR
            = new Parcelable.Creator<Segment>() {
        public Segment createFromParcel(Parcel in) {
            return new Segment(in);
        }

        public Segment[] newArray(int size) {
            return new Segment[size];
        }
    };

    private Segment(Parcel in) {
        leftEnd = in.readParcelable(Point.class.getClassLoader());
        rightEnd = in.readParcelable(Point.class.getClassLoader());
        thickness = in.readFloat();
    }
}
