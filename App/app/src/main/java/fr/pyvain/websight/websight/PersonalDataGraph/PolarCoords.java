package fr.pyvain.websight.websight.PersonalDataGraph;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <p>
 * A class that represents radial coordinates.
 * </p>
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public final class PolarCoords implements Parcelable {

    /**
     * Radial coordinate.
     */
    private final int radius;

    /**
     * Angular coordinate.
     */
    private final float angle;

    /**
     * Initializes new radial coordinates with specified
     * angular and radial coordinates.
     *
     * @param angle Angular coordinate
     * @param radius Radial coordinate
     */
    public PolarCoords(int radius, float angle) {
        this.radius = radius;
        this.angle = angle;
    }

    /**
     * Radial coordinate getter
     * @return the radial coordinate
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Angular coordinate getter
     * @return the angular coordinate
     */
    public float getAngle() {
        return angle;
    }

    /**
     * Comparison precision
     */
    private static final float EPS = (float) 1e-5;

    /**
     * Two PolarCoords objects are equal if they have equals
     * coordinates.
     *
     * @param o Object to compare with the receiving PolarCoords.
     * @return True if and only if o is a PolarCoords, with equal
     * coordinates
     */
    public boolean equals(Object o) {
        if (!(o instanceof PolarCoords)) {
            return false;
        } else {
            PolarCoords that = (PolarCoords) o;
            return (this.getRadius() == that.getRadius() &&
                    Math.abs(this.getAngle()-that.getAngle()) < EPS);
        }
    }

    /**
     * Returns a hash code value for the PolarCoords.
     * Two equal PolarCoords objects have the same hash code value.
     *
     * @return a hash code value for the PolarCoords.
     */
    @Override
    public int hashCode() {
        // Good implementation propose in Josh Bloch's Effective Java
        int result = 13;
        result = 37 * result + (radius ^ (radius >>> 16));
        result = 37 * result + Float.floatToIntBits(angle);
        return result;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(radius);
        out.writeFloat(angle);
    }

    public static final Parcelable.Creator<PolarCoords> CREATOR
            = new Parcelable.Creator<PolarCoords>() {
        public PolarCoords createFromParcel(Parcel in) {
            return new PolarCoords(in);
        }

        public PolarCoords[] newArray(int size) {
            return new PolarCoords[size];
        }
    };


    /**
     * Initializes a new PolarCoords from a parcel.
     * @param in Parcel containing an int and a float
     */
    private PolarCoords(Parcel in) {
        radius = in.readInt();
        angle = in.readFloat();
    }
}