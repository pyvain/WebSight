package fr.pyvain.websight.websight.SweetGraphs;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <p>This class represents a Circle by its center point and its radius.</p>
 *
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class Circle implements Parcelable {

    private Point center;
    private float radius;

    public Circle(Point center, float radius) {
        this.center = center;
        this.radius = radius;
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Circle circle = (Circle) o;
        return (circle.center.equals(center) &&
                Float.compare(circle.radius, radius) == 0);
    }

    @Override
    public int hashCode() {
        int result = center.hashCode();
        result = 31 * result + (radius != +0.0f ? Float.floatToIntBits(radius) : 0);
        return result;
    }


    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(center, flags);
        out.writeFloat(radius);
    }

    public static final Parcelable.Creator<Circle> CREATOR
            = new Parcelable.Creator<Circle>() {
        public Circle createFromParcel(Parcel in) {
            return new Circle(in);
        }

        public Circle[] newArray(int size) {
            return new Circle[size];
        }
    };

    private Circle(Parcel in) {
        center = in.readParcelable(Point.class.getClassLoader());
        radius = in.readFloat();
    }
}
