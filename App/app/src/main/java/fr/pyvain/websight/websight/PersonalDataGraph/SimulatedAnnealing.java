package fr.pyvain.websight.websight.PersonalDataGraph;

import java.util.Random;

/**
 * <p>This class contains a few manually adjusted parameters
 * and useful auxiliary methods to apply simulated annealing
 * to crossings minimization.</p>
 *
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
final class SimulatedAnnealing {

    // The following constants are manually adjusted parameters
    // used in the simulated annealing technique

	/**
	 * Simulated annealing vertex crossing weight in cost function
	 */
	private static final int V_WEIGHT = 5;

    /**
     * Random generator to use to anneal.
     */
    public static final Random rand = new Random();

	/**
	 * Computes the acceptance probability of a new state given
     * the current temperature, the previous state's cost, and the
     * new state's cost.
     *
	 * @param oldCost      Cost of the previous state
	 * @param newCost      Cost of the new state
	 * @param temperature  Current temperature
	 * @return the acceptance probability of a new state of cost newCost
	 *         if the previous state's cost was oldCost and the temperature
	 *         is the specified temperature
	 */
	public static float acceptanceProbability(int oldCost, int newCost,
	float temperature) {
		if (newCost < oldCost) {
			return 1f;
		} else {
			return (float) Math.exp((oldCost-newCost)/temperature);
		}
	}

	/**
	 * Computes the cost of the specified drawing of the specified Tree.
	 *
	 * @param drawing Drawing whose cost must be computed
     * @param tree    Underlying tree of the Drawing
	 * @return the cost of the layout
	 */

	public static int cost(Drawing drawing, Tree tree) {
        // only additional edges can intersect with vertices
		return (V_WEIGHT*drawing.nbVertexCrossings(tree.getExtraEdges()) +
                drawing.nbEdgeIntersections());
	}

    /**
     * Returns an estimated "good" initial temperature for the specified
     * instance of the simulated annealing problem
     * @param costDelta Average cost variation between initial state
     *                  and all other states
     * @return An estimated "good" initial temperature for the specified
     * instance of the simulated annealing problem
     */
    public static float goodInitTemp(float costDelta) {
        return -costDelta/(float)Math.log(0.95);
    }

    /**
     * Returns an estimated "good" decrease factor for the specified
     * instance of the simulated annealing problem
     * @param nbIters Estimated maximal number of iterations before
     *                     stopping.
     * @param initTemp     Initial temperature
     * @param costDelta    Average cost variation between initial state
     *                     and all other states
     * @return An estimated "good" decrease factor for the specified
     * instance of the simulated annealing problem
     */
    public static float goodDecreaseFactor(int nbIters, float initTemp, float costDelta) {
        return (float) Math.pow(-costDelta/(initTemp*(float)Math.log(0.01)), 1.0/nbIters);
    }



}

