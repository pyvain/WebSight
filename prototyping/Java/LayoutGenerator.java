import java.lang.System;
import java.util.Random;
import java.util.List;


/**
 * <p>This class is a factory allowing to generate different kind of 
 * layout of a same graph. Well, it will... for the time being it only 
 * allows to generate visualy pleasant radial layout (i.e. with least
 * edge and vertex crossings).</p>
 *
 * <p>
 * @Author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public final class LayoutGenerator {
	
	// The following constants are manually adjusted constants
	// used in the simulated annealing technique

	/**
	 * Simulated annealing initial temperature
	 */
	private static final float INIT_TEMP = 1f;

	/**
	 * Simulated annealing number of iterations per temperature step
	 */
	private static final int NB_ITER = 10;

	/**
	 * Simulated annealing temperature decrease factor
	 */
	private static final float DECREASE_FACTOR = 0.8f;

	/**
	 * Simulated annealing vertex crossing weight in cost function
	 */
	private static final int V_WEIGHT = 5;

	/**
	 * Simulated annealing maximum number of still iterations (i.e. with
	 * same cost) before stopping
	 */
	private static final int MAX_STILL_ITER = 10;

	/**
	 * Last Vertex modified by the neighbourState() method.
	 */
	private Vertex lastModified;

	/**
	 * Graph currently been drawn.
	 */
	private Graph graph;

	/**
	 * Density with wich the graph is currently been drawn.
	 */
	private float density;


	/**
	 * Constructor.
	 */
	public LayoutGenerator() {
		lastModified = null;
		graph = null;
		density = 0f;

	}

	/**
	 * Generates a new radial layout of the specified graph, with least
	 * edge and vertex crossings, within the specified time by using the
	 * simulated annealing technique.
	 *
	 * @param graph the graph to draw
	 * @param time the maximum duration of the generation, in ms
	 * @param density  The density of vertice in the layout, which must be
	 *                 in ]0,1[
	 * @return a new radial layout of the specified graph.
	 */
	public RadialLayout getPleasantRadialLayout(Graph graph, 
	float density, float time) {
		long startTime = System.nanoTime();
		this.graph = graph;
		this.density = density;
		Random rand = new Random();
		float temp = INIT_TEMP;
		RadialLayout bestLayout = new RadialLayout(graph, density);
		int smallestCost = cost(bestLayout);
		int oldCost = smallestCost;
		int iter = 0; // number of iterations at the same temperature
		int stillIter = 0; // number of iterations at the same cost

		while ((System.nanoTime()-startTime < time*Math.pow(10, 6)) &&
			stillIter < MAX_STILL_ITER) {
				// Computes neighbour state
				RadialLayout newLayout = neighbourState();
				int newCost = cost(newLayout);
				float p = acceptanceProbability(oldCost, newCost, temp); 
				// Accepts or not neighbour state
				if (p < rand.nextFloat()) {
					oldCost = newCost;
					if (newCost < smallestCost) {
						smallestCost = newCost;
						bestLayout = newLayout;
					}
					stillIter = 0;
				} else {
					previousState();
					stillIter++;
				}
				// Updates temperature
				iter++;
				if ((++iter) % NB_ITER == 0) {
					temp *= DECREASE_FACTOR;
				}
		}

		System.out.println("getPleasantRadialLayout : " + iter + " iterations");
		System.out.println("in " + (System.nanoTime()-startTime) + "ns");
		return bestLayout;
	}

	/**
	 * Private auxiliary method which computes the acceptance probability
	 * when applying the simulated annealing technique to compute a 
	 * permutation of a graph resulting in a visualy pleasant radial layout.
	 *
	 * @param oldCost      Cost of the previous state
	 * @param newCost      Cost of the new state
	 * @param temperature  Current temperature
	 * @return the acceptance probability of a new state of cost newCost
	 *         if the previous state's cost was oldCost and the temperature
	 *         is the specified temperature
	 */
	private static float acceptanceProbability(int oldCost, int newCost, 
	float temperature) {
		if (newCost < oldCost) {
			return 1f;
		} else {
			return (float) Math.exp((oldCost-newCost)/temperature);
		}
	}

	/**
	 * Private auxiliary method which computes the cost of the specified
	 * radial layout. This weight depends on the number of edge and vertex
	 * crossings.
	 *
	 * @param layout
	 * @return the cost of the layout
	 */
	
	private static int cost(RadialLayout layout) {
		return V_WEIGHT*layout.nbVertexCrossings() + layout.nbEdgeCrossings();
	}

	/**
	 * Private auxiliary method which places the graph currently being
	 * drawn in a new "state", by randomly shuffling the children of one 
	 * of its vertices, then returns the corresponding radial layout.
	 *
	 * @param graph    graph to draw
	 * @param density  density to use to draw the graph
	 * @return the radial layout corresponding to the new state of the graph
	 */
	private RadialLayout neighbourState() {
		List<Vertex> vertices = graph.getVertices(); 
		Vertex v = vertices.get(new Random().nextInt(vertices.size()));
		lastModified = v;
		v.shuffleChildren();
		return new RadialLayout(graph, density);
	}

	/**
	 * Private auxiliary method which places back the graph currently
	 * being drawn in its previous "state", only if the neighbourState() 
	 * method was previously used to place it in a neighbour state.
	 */
	private void previousState() {
		lastModified.unshuffleChildren();
	}
}

