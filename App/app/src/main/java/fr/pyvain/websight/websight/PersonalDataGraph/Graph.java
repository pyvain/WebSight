package fr.pyvain.websight.websight.PersonalDataGraph;

import java.util.Set;
import java.util.SortedSet;

/**
 * <p>In this package, a graph represents the online persona of a user.</p>
 *
 * <p>Each of its vertex represents a textual personal data, that the user
 * gave to the application, in order for it to be tracked, or to help
 * track other data.</p>
 *
 * <p>Each edge of the graph represent a bond between the two pieces of data
 * it connects. Two pieces are bound if at least one online page containing
 * both of them have been found by the server search engine.</p>
 */
interface Graph {
    /**
     * Vertices getter.
     * @return a read only list of this graph's vertices
     */
    SortedSet<Vertex> getVertices();

    /**
     * Edges getter.
     * @return a read only list of this graph's edges
     */
    Set<Edge> getEdges();
}
