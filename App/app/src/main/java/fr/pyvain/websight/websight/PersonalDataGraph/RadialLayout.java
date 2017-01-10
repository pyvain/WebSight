package fr.pyvain.websight.websight.PersonalDataGraph;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>A radial layout is a style of graph drawing that places the vertices
 * of the graph on concentric circles.</p>
 *
 * <p>In this implementation, a radial layout is simply defined by the
 * polar coordinates of the graph's vertices.</p>
 *
 * <p>To be able to actually draw of such a layout, a few more
 * attributes are computed :<ul>
 *     <li>the maximal radius the vertices of the graph can have in the
 *     drawing without any of them going beyond its angular sector, thus
 *     without any of them touch each other</li>
 *     <li>the maximal radius of the layout, i.e. the radius of the
 *     smallest circle containing the whole layout, given the fact that
 *     the radius of the vertex is lower than the previous attribute</li>
 * </ul></p>
 *
 * <p>
 * To build the radial layout of a tree, the coordinates of its vertices
 * are computed recursively by placing each vertex in the center of an
 * exclusive angular sector of the orbit it is on, computed as follow: <ul>
 *     <li>the root's angular sector is specified when constructing
 *     the radial tree.
 *     <li>the width of the other vertices sector is a fraction of their
 *     parent's sector width, proportional to their number of descendant:
 *         parentSectorWidth * (1+nbDescendant) / parentNbDescendant
 *     </li>
 *     <li>the angular sectors of a same orbit does not intersect,
 *     and are arranged in the order given by the children list of
 *     each vertex.</li>
 * </ul></p>
 *
 * <p>To build the radial layout of a forest, a set of tree radial layout
 * (one per Tree of the Forest) is built, and merged. The key of the
 * construction is to chose their roots' angular sectors wisely:<ul>
 *     <li>the main tree of the data forest is laid out in a
 *     2pi radian wide sector</li>
 *     <li>any other tree is laid out around the main tree,
 *     with its root on the first empty orbit, and in a sector
 *     whose width is proportional to its number of vertices :
 *     2*pi*nbVertices/(totalNbVertices)
 *     where totalNbVertices is the number of vertices in all the
 *     trees, except the main tree. </li>
 * </ul></p>
 *
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class RadialLayout implements Parcelable {

    /**
     * Polar coordinates of the vertices of the underlying Graph,
     * in radians, per index in the Graph's Vertex SortedSet.
     */
    private final List<PolarCoords> coords;

    /**
     * Maximal radius the vertices of the Graph can have without any of
     * them going beyond its angular sector.
     * Drawing the vertices with a lower radius ensures that
     * they will not touch.
     */
    private float maxVertexRadius;

    /**
     * RadialLayout radius, i.e. radius of the smallest circle centered
     * on (0, 0) containing the whole layout, given the fact that
     * the radius of the vertex is lower than maxVertexRadius;
     */
    private float layoutRadius;

    /**
     * Initializes a new empty radial layout
     */
    public RadialLayout() {
        coords = new ArrayList<>();
        maxVertexRadius = 0.5f;
        layoutRadius = 0f;
    }

    /**
     * Initializes a new radial layout of the specified Tree,
     * in which the angular sector of the root has the specified
     * attributes.
     *
     * @param tree      Tree whose radial layout must be constructed
     * @param rad       Radial coordinate of the root
     * @param secStart  Starting angle of the root angular sector
     *                  in radians
     * @param secWidth  Width of the root angular sector in radians
     */
    public RadialLayout(Tree tree, int rad, float secStart, float secWidth) {
        this();
        update(tree, rad, secStart, secWidth);
    }

    /**
     * Initializes a new radial layout of the specified forest.
     * @param forest Forest of which a layout must be initialized
     */
    public RadialLayout(Forest forest) {
        this();
        update(forest);
    }

    /**
     * Replaces the current layout by a layout of the specified Tree,
     * in which the angular sector of the root has the specified attributes.
     *
     * @param tree      Tree which must be laid out.
     * @param rad       Radial coordinate of the root
     * @param secStart  Starting angle of the root's angular sector
     *                  in radians
     * @param secWidth  Width of the root's angular sector in radians
     */
    public void update(Tree tree, int rad, float secStart, float secWidth) {
        coords.clear();
        coords.addAll(Collections.<PolarCoords>nCopies(tree.getVertices().size(), null));
        maxVertexRadius = 0.5f;
        layoutRadius = 0f;
        Map<Vertex, Integer> indices = new HashMap<>();
        int i = 0;
        for (Vertex v : tree.getVertices()) {
            indices.put(v, i++);
        }
        setCoords(tree.getRoot(), tree, rad, secStart, secWidth, indices);
        layoutRadius += maxVertexRadius;
    }

    /**
     * Replaces the current layout by a layout of the specified forest.
     * @param forest Forest which must be laid out
     */
    public void update(Forest forest) {
        // Maps each vertex of the underlying forest to its index in the
        // forest's Vertex SortedSet.
        Map<Vertex, Integer> indices = new HashMap<>();
        int i = 0;
        for (Vertex v : forest.getVertices()) {
            indices.put(v, i++);
        }
        // Clears layout
        maxVertexRadius = 0.5f;
        layoutRadius = 0;
        coords.clear();
        coords.addAll(Collections.<PolarCoords>nCopies(forest.getVertices().size(), null));
        // Builds new layout
        Tree mainTree = forest.getMainTree();
        int periphery = mainTree.getHeight()+1;
        int totalSize = forest.size() - mainTree.size();
        float totalWidth = (float) (2 * Math.PI);
        float secStart = 0.0f;
        float secWidth;
        for (Tree t : forest.getTrees()) {
            RadialLayout tl;
            if (t == mainTree) {
                tl = new RadialLayout(t, 0, 0f, totalWidth);
            } else {
                secWidth = totalWidth * t.size() / totalSize;
                tl = new RadialLayout(t, periphery, secStart, secWidth);
                secStart += secWidth;
            }
            i = 0;
            for (Vertex v : t.getVertices()) {
                coords.set(indices.get(v), tl.getPolarCoords(i++));
            }
            maxVertexRadius = Math.min(maxVertexRadius, tl.getMaxVertexRadius());
            layoutRadius = Math.max(layoutRadius, tl.getLayoutRadius());
        }
    }


    /**
     * Private auxiliary initialization method.
     * Sets the polar coordinate of the specified vertex,
     * given specified attributes, then calls itself recursively
     * on its children.
     *
     * @param v         Vertex whose polar coordinates must be set
     * @param tree      Data tree the vertex is in
     * @param vR        Radial coordinate of the vertex
     * @param vSecWidth Width of the vertex's angular sector in radians
     * @param vSecStart Starting angle of the vertex's angular sector
     *                  in radians
     * @param indices   Maps each vertex of the underlying tree to its
     *                  index in the tree's Vertex SortedSet.
     */
    private void setCoords(Vertex v, Tree tree, int vR, float vSecStart,
                           float vSecWidth, Map<Vertex, Integer> indices)
    {
        // The vertex is placed in the middle of its sector
        float vAngle = vSecStart + vSecWidth/2;
        coords.set(indices.get(v), new PolarCoords(vR, vAngle));
        // Updates layout's maxVertexRadius and radius
        if (vSecWidth < Math.PI && vR > 0) {
            maxVertexRadius = (float) Math.min(maxVertexRadius,
                    vR * Math.tan(vSecWidth/2));
        }
        layoutRadius = Math.max(layoutRadius, vR);
        // Calls itself recursively on the vertex's children
        float childStart = vSecStart;
        for (Vertex child : tree.getChildren(v)) {
            float childWidth = vSecWidth * (tree.getNbDescendants(child)+1);
            childWidth /= tree.getNbDescendants(v);
            setCoords(child, tree, vR+1, childStart, childWidth, indices);
            childStart += childWidth;
        }
    }


    /**
     * Polar coordinates getter.
     * @return A read only view of the polar coordinates of the vertices
     * of the underlying graph.
     */
    private List<PolarCoords> getPolarCoords() {
        return Collections.unmodifiableList(coords);
    }

    /**
     * Specific polar coordinates getter.
     * @param vNb Number of the underlying graph's vertex whose polar
     *            coordinates are required
     * @return The polar coordinates of the specified vertex if it
     * is in the layout, null else.
     */
    public PolarCoords getPolarCoords(int vNb) {
        return coords.get(vNb);
    }

    /**
     * Maximal vertex radius getter.
     * @return the maximal vertex radius
     */
    public float getMaxVertexRadius() {
        return maxVertexRadius;
    }

    /**
     * RadialLayout radius getter.
     * @return the layout radius
     */
    public float getLayoutRadius() {
        return layoutRadius;
    }

    /**
     * Comparison precision
     */
    private static final float EPS = (float) 1e-5;

    /**
     * Two RadialLayout objects are equal if they have equals
     * attributes.
     *
     * @param o Object to compare with the receiving RadialLayout.
     * @return True if and only if o is a RadialLayout, with equal
     * attributes
     */
    public boolean equals(Object o) {
        if (!(o instanceof RadialLayout)) {
            return false;
        } else {
            RadialLayout that = (RadialLayout) o;
            return (Math.abs(this.getMaxVertexRadius()-that.getMaxVertexRadius()) < EPS &&
                    Math.abs(this.getLayoutRadius()-that.getLayoutRadius()) < EPS &&
                    this.getPolarCoords().equals(that.getPolarCoords()));
        }
    }

    /**
     * Returns a hash code value for the RadialLayout.
     * Two equal RadialLayout objects have the same hash code value.
     *
     * @return a hash code value for the RadialLayout.
     */
    @Override
    public int hashCode() {
        // Good implementation propose in Josh Bloch's Effective Java
        int result = 13;
        result = 37 * result + Float.floatToIntBits(maxVertexRadius);
        result = 37 * result + Float.floatToIntBits(layoutRadius);
        result = 37 * result + coords.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeTypedList(coords);
        out.writeFloat(maxVertexRadius);
        out.writeFloat(layoutRadius);
    }

    public static final Parcelable.Creator<RadialLayout> CREATOR
            = new Parcelable.Creator<RadialLayout>() {
        public RadialLayout createFromParcel(Parcel in) {
            return new RadialLayout(in);
        }

        public RadialLayout[] newArray(int size) {
            return new RadialLayout[size];
        }
    };

    /**
     * Initializes a new Radial layout from a Parcel
     * @param in Parcel containing a list of PolarCoords, and 2 floats
     *           (maxVertexRadius then layoutRadius)
     */
    private RadialLayout(Parcel in) {
        coords = new ArrayList<>();
        in.readTypedList(coords, PolarCoords.CREATOR);
        maxVertexRadius = in.readFloat();
        layoutRadius = in.readFloat();
    }
}
