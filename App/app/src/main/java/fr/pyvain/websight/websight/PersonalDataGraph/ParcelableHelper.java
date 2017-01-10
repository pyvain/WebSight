package fr.pyvain.websight.websight.PersonalDataGraph;

import android.os.Parcel;

import java.util.Collection;
import java.util.Map;

/**
 * <p>Generics solution to write Map instances to a Parcelable, and read
 * them back, provided both key and value classes implement Parcelable.</p>
 *
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
class ParcelableHelper {

    /**
     * Writes the specified edge collection to the specified parcel
     * @param out   Parcel to write in
     * @param edges Edge collection to be written
     */
    public static void writeEdges(Parcel out, Collection<Edge> edges) {
        out.writeInt(edges.size());
        for (Edge e : edges) {
            out.writeInt(e.getEnd1().getId());
            out.writeInt(e.getEnd2().getId());
            out.writeParcelable(e.getData(), 0);
        }
    }

    /**
     * Reads from the specified parcel into the specified
     * edge collection.
     * @param in      Parcel to read from
     * @param edges   Edge collection to read in
     * @param vFinder Maps each id to a vertex
     */
    public static void readEdges(Parcel in, Collection<Edge> edges,
                                    Map<Integer, Vertex> vFinder)
    {
        int nbEdges = in.readInt();
        for (int i = 0; i < nbEdges; i++) {
            Vertex end1 = vFinder.get(in.readInt());
            Vertex end2 = vFinder.get(in.readInt());
            Edge e = new Edge(end1, end2);
            DataSet data = in.readParcelable(DataSet.class.getClassLoader());
            e.getData().addURLs(data.getURLs());
            edges.add(e);
        }
    }

    /**
     * Writes the specified vertex collection to the specified parcel
     * @param out      Parcel to write in
     * @param vertices Vertex collection to be written
     */
    public static void writeVertices(Parcel out, Collection<Vertex> vertices) {
        int size = (vertices == null) ? -1 : vertices.size();
        out.writeInt(size);
        if (vertices != null) {
            for (Vertex v : vertices) {
                out.writeInt(v.getId());
            }
        }
    }

    /**
     * Reads from the specified parcel into the specified
     * Vertex collection.
     * @param in       Parcel to read from
     * @param vertices Vertex collection to read in
     * @param vFinder  Maps each id to a vertex
     */
    public static void readVertices(Parcel in, Collection<Vertex> vertices,
                                    Map<Integer, Vertex> vFinder)
    {
        int nbVertices = in.readInt();
        for (int i = 0; i < nbVertices; i++) {
            vertices.add(vFinder.get(in.readInt()));
        }
    }

}
