import java.util.Objects;

/**
 * <p>
 * A class that represents an edge of a personal data graph.
 * Each edge has :
 * <ul>
 * <li> A unique id ;
 * <li> 2 ends, which are the vertices it links in the personal data
 * graph. The first end is the one of smaller id ;</li>
 * <li> some data which are at the moment a list of URLS that link
 * the labels of the two ends.</li>
 * </ul>
 * </p>
 * <p>
 * @Author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public final class Edge {

	/**
	 * Id of the edge.
	 */
	private final int id;


	/**
	 * Ends of the edge (ordered by increasing id)
	 */
	private final Vertex[] ends;

	/**
	 * Data of the edge.
	 */
	private final DataSet data;

	/**
	 * Initializes a new Edge with specified id, ends and empty data.
	 *
	 * @param id Id of the edge
	 * @param end1 First end of the edge
	 * @param end2 Second end of the edge
	 */
	public Edge(int id, Vertex end1, Vertex end2) {
		this.id = id;
		if (end1.getId() < end2.getId()) {
			this.ends = new Vertex[] {end1, end2};
		} else {
			this.ends = new Vertex[] {end2, end1};
		}
		this.data = new DataSet();
	}

	/**
	 * Id getter.
	 *
	 * @return the id of the edge
	 */
	public int getId() {
		return id;
	}

	/**
	 * First end getter
	 *
	 * @return the first end of the edge
	 */
	public Vertex getEnd1() {
		return ends[0];
	}

	/**
	 * Second end getter
	 *
	 * @return the second end of the edge
	 */
	public Vertex getEnd2() {
		return ends[1];
	}

	/**
	 * Data getter
	 *
	 * @return the data of the edge
	 */
	public DataSet getData() {
		return data;
	}

	/**
	 * Two edges are equal if their ends are identicals 
	 *
	 * @param o.
	 * @return True if and only if o is an Edge, and its ends 
	 *         are identical to those of the receiving Edge.
	 */
	public boolean equals(Object o) {
		if (!(o instanceof Edge)) {
			return false;
		} else {
			Edge that = (Edge) o;
			return (this.ends[0] == that.ends[0] && 
					this.ends[1] == that.ends[1]);
		}
	}

	/**
	 * Returns a hash code value for the Edge.
	 * Two equal Edge objects have the same hash code value.
	 *
	 * @return a hash code value for the Edge.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(ends[0], ends[1]);
	} 
}