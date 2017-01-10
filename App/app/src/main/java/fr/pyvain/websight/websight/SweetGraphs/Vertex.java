package fr.pyvain.websight.websight.SweetGraphs;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * <p>This class represents a Vertex of a Graph. Such a Vertex has a unique id,
 * a label, can bear some additional data in a DataSet, and has neighbours to
 * which it is connected by Edges.</p>
 *
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public final class Vertex implements Comparable<Vertex>, Parcelable {

    private final int id;
    private final String label;
    private final DataSet data;
    private final SortedMap<Vertex, Edge> neighbours;

    public Vertex(int id, String label) {
        this.id = id;
        this.label = label;
        this.data = new DataSet();
        this.neighbours = new TreeMap<>();
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public DataSet getData() {
        return data;
    }

    public Set<Vertex> getNeighbours() {
        return neighbours.keySet();
    }

    public Edge getEdgeTo(Vertex v) {
        return neighbours.get(v);
    }

    public void addNeighbour(Vertex v, Edge e) {
        if (!neighbours.containsKey(v)) {
            neighbours.put(v, e);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex that = (Vertex) o;
        return this.getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return id;
    }


    public int compareTo(@NonNull Vertex v) {
        return this.getId() - v.getId();
    }


    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(label);
        out.writeParcelable(data, 0);
    }

    public static final Parcelable.Creator<Vertex> CREATOR
            = new Parcelable.Creator<Vertex>() {
        public Vertex createFromParcel(Parcel in) {
            return new Vertex(in);
        }

        public Vertex[] newArray(int size) {
            return new Vertex[size];
        }
    };

    /**
     * Note : when a Vertex is written to a Parcel, the neighbour/edge
     * map is not written. So when rebuilt from the Parcel, the Vertex
     * has no neighbour, and must be "reconnected" to other vertices,
     * by parsing the edges.
     */
    private Vertex(Parcel in) {
        id = in.readInt();
        label = in.readString();
        data = in.readParcelable(DataSet.class.getClassLoader());
        neighbours = new TreeMap<>();
    }
}