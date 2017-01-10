package fr.pyvain.websight.websight.PersonalDataGraph;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * <p>A data forest is a data structure based upon a personal data graph.
 * Among other things, building a spanning forest over a graph then allows to
 * compute a radial layout of this graph very easily, even if it is not a tree,
 * or it has several connected components.</p>
 *
 * <p>A Data Forest is actually a set of Data Trees (one per connected component
 * of the graph). The way their roots are chosen is simple :<ul>
 *     <li>a main root is chosen by the user to be at the center of the layout
 *     and it is the root of the Data Tree over its connected component, which
 *     is designated main tree</li>
 *     <li>For the other connected component, the vertex of smallest id is
 *     the root of the corresponding Data Tree.</p>
 *
 * <p>In this implementation, a map is stored to be able to determine to
 * which tree a vertex belong. This allows to set any vertex as the new root
 * of the Data Tree over its connected component easily and quickly.</p>
 *
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class Forest implements Graph, Parcelable {

    /**
     * Data trees composing the Data forest. The first one
     * is the main tree
     */
    private final List<Tree> trees;

    /**
     * Maps each Vertex to the Data Tree it is in.
     */
    private final Map<Vertex, Tree> vFinder;

    /**
     * Vertices of the forest.
     * The position of a vertex in this list is its identifier
     */
    private final SortedSet<Vertex> vertices;

    /**
     * Edges of the forest.
     */
    private final Set<Edge> edges;

    /**
     * Size of the forest, i.e. number of vertices in it.
     */
    private int size;

    /**
     * Maximum number of url on one edge.
     */
    private int maxNbUrlsEdge;

    /**
     * Initializes a new empty Data Forest.
     */
    public Forest() {
        trees = new ArrayList<>();
        vFinder = new HashMap<>();
        vertices = new TreeSet<>();
        edges = new HashSet<>();
        size = 0;
    }

    /**
     * Builds a new Data Forest upon the specified graph,
     * whose main root is the specified vertex.
     *
     * @param g        The graph upon which the Data Forest must be built
     * @param mainRoot The main root of the new Data Forest
     */
    public Forest(InputGraph g, Vertex mainRoot) {
        this();
        update(g, mainRoot);
    }

    public void update(InputGraph g, Vertex mainRoot) {
        trees.clear();
        vertices.clear();
        edges.clear();
        vFinder.clear();
        vertices.addAll(g.getVertices());
        edges.addAll(g.getEdges());
        size = vertices.size();
        maxNbUrlsEdge = 0;
        Iterator<Vertex> iter = vertices.iterator();
        Vertex nextRoot = mainRoot;
        do {
            // Computes a new Data tree rooted in :
            // - the main root the first time
            // - the vertex of smallest id not covered yet every other time
            Tree newTree = new Tree(nextRoot);
            trees.add(newTree);
            for (Vertex v : newTree.getVertices()) {
                vFinder.put(v, newTree);
            }
            maxNbUrlsEdge = Math.max(maxNbUrlsEdge, newTree.getMaxNbUrlsEdge());
            // Finds the next root
            while (iter.hasNext()) {
                nextRoot = iter.next();
                if (!vFinder.containsKey(nextRoot)) {
                    break;
                }
            }
        } while (vFinder.size() < vertices.size());
    }



    /**
     * Vertices getter.
     * @return a read only list of this forest's vertices
     */
    public SortedSet<Vertex> getVertices() {
        return Collections.unmodifiableSortedSet(vertices);
    }

    /**
     * Edges getter.
     * @return a read only list of this forest's edges
     */
    public Set<Edge> getEdges() {
        return Collections.unmodifiableSet(edges);
    }

    /**
     * Data trees getter.
     * The first data tree of the list is the main data tree.
     * @return a read only view of the Data trees composing the Data Forest
     *
     */
    public List<Tree> getTrees() {
        return Collections.unmodifiableList(trees);
    }

    /**
     * Main data tree getter.
     * @return the main Data tree of the Data Forest
     *
     */
    public Tree getMainTree() {
        return trees.get(0);
    }

    /**
     *Tree getter by vertex.
     * @return the tree containing the specified Vertex if it is
     * in the forest, null else.
     */
    public Tree getTreeContaining(Vertex v) {
        return vFinder.get(v);
    }

    /**
     * The size of the data forest is the number of vertices in it.
     * @return the size of the forest
     */
    public int size() {
        return size;
    }

    /**
     * Maximal number of urls per edge getter.
     * @return the maximal number of urls per edge
     */
    public int getMaxNbUrlsEdge() {
        return maxNbUrlsEdge;
    }

    /**
     * Sets the specified vertex as the root of the Data Tree it is in.
     *
     * @param newRoot Vertex which must be set as the root of the
     *                Data Tree it is in
     * @throws IllegalArgumentException if the specified vertex is
     * not it the data forest
     */
    public void setAsRoot(Vertex newRoot) throws IllegalArgumentException {
        Tree tree = vFinder.get(newRoot);
        if (tree == null) {
            String msg = "The specified vertex is not in the data forest";
            throw new IllegalArgumentException(msg);
        }
        tree.changeRoot(newRoot);
    }

    /**
     * Sets the Data Tree containing the specified vertex
     * as the main data Tree of the Data Forest
     *
     * @param v Vertex whose Data Tree must be set as main Data Tree
     * @throws IllegalArgumentException if the specified vertex is
     * not it the data forest
     */
    public void setTreeAsMain(Vertex v) throws IllegalArgumentException {
        Tree newMain = vFinder.get(v);
        if (newMain == null) {
            String msg = "The specified vertex is not in the data forest";
            throw new IllegalArgumentException(msg);
        }
        trees.set(trees.indexOf(newMain), trees.get(0));
        trees.set(0, newMain);
    }

    /**
     * With specified time, places the forest in a state (i.e. permutes the
     * children lists of its vertices) which minimizes the cost of the drawing
     * of this forest with specified parameters.
     *
     * @param vRadius   Required vertex Radius of the tree in percentage of
     *                  the maximal allowed value, must be in ]0, 1[
     * @param wTree     Working tree, previous content will be overwritten
     * @param wLayout   Working layout, previous content will be overwritten
     * @param wDrawing  Working drawing, previous content will be overwritten
     * @param maxTime   Maximum time allocated to the minimization
     * @throws IllegalArgumentException if the vertex radius is out of bounds
     */
    public void minimizeCrossings(float vRadius, Tree wTree, RadialLayout wLayout,
                                  Drawing wDrawing, long maxTime)
            throws IllegalArgumentException
    {
        if (!(0 < vRadius && vRadius < 1)) {
            throw new IllegalArgumentException("Vertex radius out of bounds");
        }
        long start = SystemClock.elapsedRealtime();
        int remainingVertices = size;
        // TODO : there must be a better way than duplicating the code in
        // TODO : RadialLayout.update(Forest) ...
        int totalSize = size - trees.get(0).size();
        int periphery = trees.get(0).getHeight()+1;
        float totalWidth = (float) (2 * Math.PI);
        float secStart = 0.0f;
        float secWidth;
        int rad;
        for (Tree tree : trees) {
            if (tree == trees.get(0)) {
                secWidth = totalWidth;
                rad = 0;
            } else {
                secWidth = totalWidth * tree.size() / totalSize;
                rad = periphery;
            }
            if (tree.size() > 3) {
                float weight = (float)tree.size()/remainingVertices;
                long remainingTime = start + maxTime - SystemClock.elapsedRealtime();
                tree.minimizeCrossings(rad, secStart, secWidth, vRadius,
                        wTree, wLayout, wDrawing, Math.round(weight*remainingTime));
            }
            remainingVertices -= tree.size();
            secStart += secWidth;
        }
    }


    /**
     * Makes the receiving Forest a copy of the specified Forest
     * @param forest Forest to copy
     */
    public void copy(Forest forest) {
        vertices.clear();
        vertices.addAll(forest.getVertices());
        edges.clear();
        edges.addAll(forest.getEdges());
        Map<Tree, Tree> treeOnTree = new HashMap<>();
        int i = 0;
        for (Tree tree : forest.getTrees()) {
            // Allocates new tree if the receiving forest has less tree
            // than the forest to copy
            if (trees.size() <= i) {
                trees.add(new Tree(tree));
            } else {
                trees.get(i).copy(tree);
            }
            // Stores the trees correspondence to make easier copying vFinder
            treeOnTree.put(tree, trees.get(i));
            i++;
        }
        int nbTrees = forest.getTrees().size();
        // Removes the trees in excess
        while (trees.size() > nbTrees) {
            trees.remove(nbTrees);
        }
        vFinder.clear();
        for (Vertex v : forest.getVertices()) {
            vFinder.put(v, treeOnTree.get(forest.getTreeContaining(v)));
        }
        size = forest.size();
    }

    /**
     * Two forests are equal if they have equal trees
     *
     * @param o Object to compare with the receiving Forest
     * @return True if and only if o is a Forest, and it is equal
     *         to the receiving Forest.
     */
    public boolean equals(Object o) {
        if (!(o instanceof Forest)) {
            return false;
        } else {
            Forest that = (Forest) o;
            return (this.getTrees().equals(that.getTrees()));
        }
    }

    /**
     * Returns a hash code value for the Forest.
     * Two equal Forest objects have the same hash code value.
     *
     * @return a hash code value for the Forest.
     */
    @Override
    public int hashCode() {
        // Good implementation propose in Josh Bloch's Effective Java
        int result = 13;
        result = 37 * result + trees.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(trees.size());
        for (Tree t : trees) {
            out.writeParcelable(t, 0);
        }
    }

    public static final Parcelable.Creator<Forest> CREATOR
            = new Parcelable.Creator<Forest>() {
        public Forest createFromParcel(Parcel in) {
            return new Forest(in);
        }

        public Forest[] newArray(int size) {
            return new Forest[size];
        }
    };

    private Forest(Parcel in) {
        this();
        int nbTrees = in.readInt();
        for (int i = 0; i < nbTrees; i++) {
            Tree t = in.readParcelable(Tree.class.getClassLoader());
            trees.add(t);
            vertices.addAll(t.getVertices());
            edges.addAll(t.getEdges());
            for (Vertex v : t.getVertices()) {
                vFinder.put(v, t);
            }
        }
        size = vertices.size();
    }
}
