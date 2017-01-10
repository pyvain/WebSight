package fr.pyvain.websight.websight.PersonalDataGraph;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * <p>This class represents a vertex of a personal data graph.
 * A vertex represents a textual personal data, that the user gave to 
 * the application, in order for it to be tracked, or to help
 * track other data.</p>
 * 
 * <p>
 * Each vertex has : <ul>
 *     <li> a unique <b>id</b>, which is also its index in the list of the
 *     vertices of the graph ; </li>
 *     <li> a <b>label</b>, which is a textual data entered by the user,
 *     for example his email address or his nickname ; </li>
 *     <li> a set of <b>neighbours</b> in the personal data graph; </li>
 *     <li>some data, which are at the moment the URLs of the pages
 *     containing its label.</li>
 * </ul>
 * </p>
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public final class Vertex implements Comparable<Vertex>, Parcelable {

	/**
	 * Identifier of the vertex, which is also its index in the list
	 * of the vertices of the graph.
	 */
	private final int id;

	/**
	 * Keyword (= textual piece of data) of the vertex.
	 */
	private final String keyword;

	/**
	 * Data of the vertex.
	 */
	private final DataSet data;

	/**
	 * Pairs (neighbour, edge between the vertex and the neighbour).
	 */
	private final SortedMap<Vertex, Edge> neighbours;

	/**
	 * Initializes a new vertex with specified id and keyword,
	 * empty data and no neighbour.
	 *
	 * @param id The identifier of the vertex
	 * @param keyword The keyword of the vertex
	 */
	public Vertex(int id, String keyword) {
		this.id = id;
		this.keyword = keyword;
		this.data = new DataSet();
		this.neighbours = new TreeMap<>();
	}

	/**
	 * Id getter.
	 * @return the id of the vertex
	 */
	public int getId() {
		return id;
	}

	/**
	 * Keyword getter.
	 * @return the keyword of the vertex
	 */
	public String getKeyword() {
		return keyword;
	}

	/**
	 * Data getter.
	 * @return The data of the vertex
	 */
	public DataSet getData() {
		return data;
	}

	/**
	 * Neighbourhood getter.
	 * @return A view of the set of neighbours of the vertex
	 */
	public Set<Vertex> getNeighbours() {
		return neighbours.keySet();
	}

	/**
	 * Outgoing edge getter.
	 * @return The edge between the receiving vertex and the specified
	 *         vertex if they are neighbours, null else
	 */
	public Edge getEdgeTo(Vertex v) {
		return neighbours.get(v);
	}

	/**
	 * If the specified vertex is not already a neighbour of the
	 * receiving vertex, adds it its neighbours list.
	 * @param v new neighbour
	 * @param e edge between the receiving and specified vertices
	 */
	public void addNeighbourIfAbsent(Vertex v, Edge e) {
		neighbours.put(v, e);
	}

    /**
     * Two Vertex objects are equal if they have equals ids
     *
     * @param o Object to compare with the receiving Vertex.
     * @return True if and only if o is a Vertex, with same id
     * as the receiving Vertex
     */
    public boolean equals(Object o) {
        if (!(o instanceof Vertex)) {
            return false;
        } else {
            Vertex that = (Vertex) o;
            return this.getId() == that.getId();
        }
    }

    /** Returns a hash code value for the Vertex.
	 * Two equal Vertex objects have the same hash code value.
	 *
	 * @return a hash code value for the Vertex.
	 */
    @Override
    public int hashCode() {
        // Good implementation propose in Josh Bloch's Effective Java
        int result = 13;
        result = 37 * result + (id ^ (id >>> 16));
        return result;
    }

    /**
     * Vertex objects are ordered by id.
     *
     * @param v Vertex to compare with the receiving Vertex
     * @return a negative integer, 0, or a positive integer depending
     * 			on whether the receiving Vertex has a lower, equal or
     * 			or greater id than the specified Vertex
     */
    public int compareTo(@NonNull Vertex v) {
        return this.getId() - v.getId();
    }
	// Parcelable methods

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(keyword);
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
        keyword = in.readString();
        data = in.readParcelable(DataSet.class.getClassLoader());
        neighbours = new TreeMap<>();
	}
}