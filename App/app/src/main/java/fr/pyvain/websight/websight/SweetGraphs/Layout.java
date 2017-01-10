package fr.pyvain.websight.websight.SweetGraphs;


import java.util.Map;

/**
 * <p>This interface represents a Layout of a Graph, i.e. the position and size of each
 * of its Vertices, and the position and thickness of each of its Edges.</p>
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public interface Layout {

    /**
     * @param canvasSize a rectangle representing the size of a canvas
     * @return An object that maps each vertex id of a vertex in the layout to its drawing,
     * when the layout is projected to take as much space as possible on a canvas
     * of the specified size, without distortion.
     */
    Map<Integer, Circle> projectedVertices(Rectangle canvasSize);

    /**
     * @param canvasSize a rectangle representing the size of a canvas
     * @return An object that maps each pair of vertex ids of an edge in the layout
     * to its drawing, when the layout is projected to take as much space as possible
     * on a canvas of the specified size, without distortion.
     */
    Map<Integer, Map<Integer, Segment>> projectedEdges(Rectangle canvasSize);

    float projectedMinRadius(Rectangle canvasSize);
}
