package fr.pyvain.websight.websight.SweetGraphs;

import java.util.Arrays;

/**
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class Edge {

    /**
     * Ends of the edge (ordered by increasing id)
     */
    private final Vertex[] ends;

    private final DataSet data;

    public Edge(Vertex end1, Vertex end2) {
        if (end1.getId() < end2.getId()) {
            this.ends = new Vertex[] {end1, end2};
        } else {
            this.ends = new Vertex[] {end2, end1};
        }
        this.data = new DataSet();
    }


    public Vertex getEnd1() {
        return ends[0];
    }

    public Vertex getEnd2() {
        return ends[1];
    }

    public DataSet getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Edge edge = (Edge) o;
        return (Arrays.equals(ends, edge.ends) && data.equals(edge.data));
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(ends);
        result = 31 * result + data.hashCode();
        return result;
    }
}
