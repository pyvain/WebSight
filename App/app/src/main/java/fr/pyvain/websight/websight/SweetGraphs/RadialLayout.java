package fr.pyvain.websight.websight.SweetGraphs;

import java.util.List;

/**
 * <p>This class represents a particular kind of layout, where a Vertex
 * has a central position (the root) in the graph and the other Vertices are laid down
 * on evenly spaced concentric circles around it (the orbits).</p>
 *
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public interface RadialLayout extends Layout {

    /**
     * @return The id of the central vertex
     */
    int getRootId();

    /**
     * Lays out the specified graph, with the specified vertex in central
     * position
     * @param g Graph to layout
     * @param v Vertex to place in the center of the layout
     */
    void changeRoot(Graph g, Vertex v);

    /**
     * @param canvasSize a rectangle representing the size of a canvas
     * @return The drawings of the orbits of the RadialLayout, when it is projected
     * to take as much space as possible on a canvas of the specified size,
     * without distortion.
     */
    List<Circle> projectedOrbits(Rectangle canvasSize);

}
