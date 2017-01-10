package fr.pyvain.websight.websight.SweetGraphs;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * <p>This class represents a Point by its cartesian coordinates.
 *
 * Note : this implementation stores the coordinates of a Point as
 * Float, thus it suffers from floating point arithmetic approximations.
 * It okay though, as this implementation favors speed over precision.
 * Do not expect anything more than 1e-3 precision.</p>
 *
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class Point implements Comparable<Point>, Parcelable {

    private final static int DECIMALS = 4;
    
    private float x;
    private float y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }


    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }


    public static float distanceBetween(Point p1, Point p2) {
        return (float) Math.sqrt(Math.pow(p1.x-p2.x, 2) + Math.pow(p1.y-p2.y, 2));
    }

    /**
     * Rotates the receiving Point of the specified angle around the
     * specified center Point.
     * @param c Center of the rotation
     * @param angle Angle of the rotation
     */
    public void rotateAround(Point c, float angle) {
        float d = Point.distanceBetween(c, this);
        if (d != 0) {
            float dx = this.getX()-c.getX();
            float dy = this.getY()-c.getY();
            float cosA = (float)Math.cos(angle);
            float sinA = (float)Math.sin(angle);
            this.setX(cosA*dx - sinA*dy + c.getX());
            this.setY(sinA*dx + cosA*dy + c.getY());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Point point = (Point) o;
        return Point.distanceBetween(this, point) < Math.pow(10, -DECIMALS);
    }

    @Override
    public int hashCode() {
        int result = Math.round(x * (float)Math.pow(10, DECIMALS));
        result = 31 * result + Math.round(y * (float)Math.pow(10, DECIMALS));
        return result;
    }

    @Override
    public int compareTo(@NonNull Point that) {
        int result = Math.round(Math.signum(this.x - that.x));
        if (result == 0) {
            result = Math.round(Math.signum(this.y - that.y));
        }
        return result;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeFloat(x);
        out.writeFloat(y);
    }

    public static final Parcelable.Creator<Point> CREATOR
            = new Parcelable.Creator<Point>() {
        public Point createFromParcel(Parcel in) {
            return new Point(in);
        }

        public Point[] newArray(int size) {
            return new Point[size];
        }
    };

    private Point(Parcel in) {
        x = in.readFloat();
        y = in.readFloat();
    }
}
