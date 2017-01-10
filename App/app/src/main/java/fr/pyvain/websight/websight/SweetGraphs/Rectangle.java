package fr.pyvain.websight.websight.SweetGraphs;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <p>This class represents a rectangle by its central points and its two
 * dimensions. It allows to check if it contains a part of a Circle, or an
 * end of a Segment.</p>
 *
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class Rectangle implements Parcelable {

    private Point center;
    private float width;
    private float height;

    public Rectangle(Point center, float width, float height) {
        this.center = center;
        this.width = width;
        this.height = height;
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }


    public boolean contains(Circle c) {
        float dx = Math.abs(center.getX() - c.getCenter().getX()) - width/2;
        float dy = Math.abs(center.getY() - c.getCenter().getY()) - height/2;
        float r = c.getRadius();
        return ((dx <= 0 || dy <= 0) || ((dx <= r || dy <= r) &&
                Math.pow(dx, 2) + Math.pow(dy, 2) <= Math.pow(r, 2)));
    }


    /**
     * @param s A Segment
     * @return True if and only if at least one of the end of the specified
     * segment is inside the receiving rectangle
     */
    public boolean contains(Segment s) {
        float xInf = center.getX() - width / 2;
        float xSup = center.getX() + width / 2;
        float yInf = center.getY() - width / 2;
        float ySup = center.getY() + width / 2;
        float x1 = s.getLeftEnd().getX();
        float y1 = s.getLeftEnd().getY();
        float x2 = s.getRightEnd().getX();
        float y2 = s.getRightEnd().getY();

        return ((xInf <= x1 && x1 <= xSup && yInf <= y1 && y1 <= ySup) ||
                (xInf <= x2 && x2 <= xSup && yInf <= y2 && y2 <= ySup));
        // if both ends are on the same side of the rectangle
//        if (x1 < xInf && x2 < xInf || x1 > xSup && x2 > xSup ||
//                y1 < yInf && y2 < yInf || y1 > ySup && y2 > ySup) {
//            return false;
//        }
//
//        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Rectangle rectangle = (Rectangle) o;
        return (Float.compare(rectangle.width, width) == 0 &&
                Float.compare(rectangle.height, height) == 0 &&
                center.equals(rectangle.center));
    }

    @Override
    public int hashCode() {
        int result = center.hashCode();
        result = 31 * result + (width != +0.0f ? Float.floatToIntBits(width) : 0);
        result = 31 * result + (height != +0.0f ? Float.floatToIntBits(height) : 0);
        return result;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(center, flags);
        out.writeFloat(width);
        out.writeFloat(height);
    }

    public static final Parcelable.Creator<Rectangle> CREATOR
            = new Parcelable.Creator<Rectangle>() {
        public Rectangle createFromParcel(Parcel in) {
            return new Rectangle(in);
        }

        public Rectangle[] newArray(int size) {
            return new Rectangle[size];
        }
    };

    private Rectangle(Parcel in) {
        center = in.readParcelable(Point.class.getClassLoader());
        width = in.readFloat();
        height = in.readFloat();
    }
}
