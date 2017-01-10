package fr.pyvain.websight.websight.PersonalDataGraph;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * <p>A data tree is a structure built upon a connected component of
 * a data graph, which is composed of :<ul>
 *     <li>a directed tree of vertices representing personal data of
 *     the user, which is defined by a set of vertices, a set of edges,
 *     a root, and a parent/children relation</li>
 *     <li>a set of additional edges, which are the edges of the
 *     connected component which are not in the tree</li>
 * </ul></p>
 *
 * <p>Additionally, the number of descendants of each vertex is
 * stored in the data tree, to fasten the computation
 * of a radial layout of the underlying connected component of the
 * graph.</p>
 *
 * <p>Finally, this structure allows to shuffle the children of one
 * of the vertices (which results in a different radial layout), and
 * eventually to reverse this action (by storing the previous order).</p>
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class Tree implements Graph, Parcelable {

    /**
     * Vertices of the data tree.
     */
    private final SortedSet<Vertex> vertices;

    /**
     * Edges of the data tree.
     */
    private final Set<Edge> edges;

    /**
     * Additional edges of the data tree.
     * Any edge in extraEdges is also in edges.
     */
    private final Set<Edge> extraEdges;

    /**
     * Root of the data tree.
     */
    private Vertex root;

    /**
     * Parent children relation defining the tree.
     * Note that the ordering of the Vertex lists is quite important,
     * as it will impact a radial layout built from the Data Forest
     * containing the Data Tree.
     */
    private final Map<Vertex, List<Vertex>> children;

    /**
     * Last vertex whose children have been shuffled.
     * Equal to null until shuffleChildren() is called,
     * and set back to null by a call to unshuffleChildren().
     */
    private Vertex lastShuffled;

    /**
     * Last children shuffled, in their previous order.
     * Equal to null until shuffleChildren() is called,
     * and set back to null by a call to unshuffleChildren().
     */
    private List<Vertex> preShuffleOrder;

    /**
     * Number of descendants of each vertex of the data tree.
     */
    private final Map<Vertex, Integer> nbDescendants;

    /**
     * Height of the tree i.e. maximal depth of its vertices.
     */
    private int height;

    /**
     * Maximum number of url on one edge.
     */
    private int maxNbUrlsEdge;

    /**
     * Initializes a new Tree whose root is the specified Vertex
     * and which covers its whole connected component.
     * The children of each vertex are sorted by increasing id.
     *
     * @param root Root of the new Tree
     */
    public Tree(Vertex root) {
        this();
        changeRoot(root);
    }

    /**
     * Initializes a new Tree by copying the specified Tree.
     * @param tree Tree to copy
     */
    public Tree(Tree tree) {
        this();
        copy(tree);
    }

    /**
     * Initializes a new empty tree.
     */
    public Tree() {
        vertices = new TreeSet<>();
        edges = new HashSet<>();
        extraEdges = new HashSet<>();
        children = new HashMap<>();
        nbDescendants = new HashMap<>();
        this.root = null;
        lastShuffled = null;
        preShuffleOrder = null;
        height = 0;
        maxNbUrlsEdge = 0;
    }

    /**
     * Replaces the root of the tree by the specified Vertex,
     * and updates the whole tree accordingly.
     * @param newRoot Vertex to set as root
     */
    public void changeRoot(Vertex newRoot) {
        vertices.clear();
        edges.clear();
        extraEdges.clear();
        children.clear();
        nbDescendants.clear();
        lastShuffled = null;
        root = newRoot;
        preShuffleOrder = null;
        height = 0;
        // Performs a BFS from the root to initialize vertices, edges,
        // extraEdges and children
        vertices.add(root);
        Queue<Vertex> queue = new LinkedList<>();
        queue.add(root);
        Vertex current;
        while ((current = queue.poll()) != null) {
            List<Vertex> currentChildren = new ArrayList<>();
            for (Vertex neighbour : current.getNeighbours()) {
                Edge edgeTo = current.getEdgeTo(neighbour);
                maxNbUrlsEdge = Math.max(maxNbUrlsEdge,
                        edgeTo.getData().getURLs().size());
                if (!vertices.contains(neighbour)) {
                    vertices.add(neighbour);
                    currentChildren.add(neighbour);
                    queue.add(neighbour);
                } else {
                    if (!edges.contains(edgeTo)) {
                        extraEdges.add(edgeTo);
                    }
                }
                edges.add(edgeTo);
            }
            children.put(current, currentChildren);
        }
        // Browse through the tree again to initialize nbDescendants
        // and height
        computeMetrics(root, 0);
    }

    /**
     * Private auxiliary initialization method.
     * Computes the nb of descendants of the specified Vertex
     * by first calling itself recursively on its descendants.
     * Also, updates the tree's height
     * @param v     Vertex whose subtree size must be computed
     * @param depth Depth of the vertex in the tree
     */
    private void computeMetrics(Vertex v, int depth) {
        height = Math.max(height, depth);
        int vNbDescendants = 0;
        for (Vertex child : children.get(v)) {
            computeMetrics(child, depth+1);
            vNbDescendants += 1 + nbDescendants.get(child);
        }
        nbDescendants.put(v, vNbDescendants);
    }

    /**
     * Vertices getter.
     * @return a read only view of the vertices of the data tree
     */
    public SortedSet<Vertex> getVertices() {
        return Collections.unmodifiableSortedSet(vertices);
    }

    /**
     * Edges getter.
     * @return a read only view of the edges of the data tree
     */
    public Set<Edge> getEdges() {
        return Collections.unmodifiableSet(edges);
    }

    /**
     * Additional edges getter.
     * @return a read only view of the additional edges of the data
     * tree
     */
    public Set<Edge> getExtraEdges(){
        return Collections.unmodifiableSet(extraEdges);
    }

    /**
     * Root getter.
     * @return the root of the data tree
     */
    public Vertex getRoot(){
        return root;
    }

    /**
     * Children getter.
     * @return a read only view of the parent/children relation of the
     * tree
     */
    public Map<Vertex, List<Vertex>> getChildren(){
        return Collections.unmodifiableMap(children);
    }

    /**
     * Specific vertex's children getter.
     * @param v Vertex whose children are required
     * @return a read only view of the children of the specified
     * Vertex if it is the tree, null else.
     */
    public List<Vertex> getChildren(Vertex v){
        List<Vertex> vChildren = children.get(v);
        if (vChildren == null) {
            return null;
        } else {
            return Collections.unmodifiableList(vChildren);
        }
    }

    /**
     * Last vertex whose children have been shuffled getter.
     * @return The last vertex whose children have been shuffled by
     * a call to shuffleChildren(), null if it has not been called yet
     * or if it has been canceled by a call to unshuffleChildren().
     */
    public Vertex getLastShuffled() {
        return lastShuffled;
    }

    /**
     * Previous order of last children shuffled getter.
     * @return a read only view of the previous order of the last children
     * shuffled by a call to shuffleChildren(), null if it has not been
     * called yet or if it has been canceled by a call to unshuffleChildren().
     */
   public List<Vertex> getPreShuffleOrder() {
       if (preShuffleOrder == null) {
           return null;
       } else {
           return Collections.unmodifiableList(preShuffleOrder);
       }
   }


    /**
     * Chooses a random Vertex of the data tree, and shuffles its children
     * placing the tree in a "neighbour state".
     */
    public void neighbourState() {
        // TODO : maybe change the vertices data structure to a list
        // TODO : to be able to pick a random element in O(1) instead of O(N)
        int chosen = new Random().nextInt(vertices.size());
        Iterator<Vertex> iter = vertices.iterator();
        for(int i = 0; i < chosen; i++) {
            iter.next();
        }
        shuffleChildren(iter.next());
    }

    /**
     * Places back the data tree in its previous "state", if the
     * neighbourState() method was previously used to place it in
     * a neighbour state.
     */
    public void previousState() {
        unshuffleChildren();
    }


    /**
     * Randomly shuffles the children of the specified Vertex, and saves
     * a copy of its previous state, to allow restoration thanks to the
     * unshuffleChildren() method.
     * @param v Vertex whose children must be shuffled
     */
    public void shuffleChildren(Vertex v) {
        if (vertices.contains(v)) {
            lastShuffled = v;
            List<Vertex> vChildren = children.get(v);
            preShuffleOrder = new ArrayList<>(vChildren);
            Collections.shuffle(vChildren);
        }
    }

    /**
     * When called after a call to shuffleChildren(), cancel its
     * effect by restoring the previous order of the last shuffled
     * children.
     */
    public void unshuffleChildren() {
        if (lastShuffled != null) {
            children.put(lastShuffled, preShuffleOrder);
            lastShuffled = null;
            preShuffleOrder = null;
        }
    }
    /**
     * Specific number of descendants getter.
     * @param v The vertex whose number of descendants is required
     * @return the number of descendant of the specified Vertex if it is
     * in the tree, -1 else
     */
    public int getNbDescendants(Vertex v) {
        Integer vNbDescendants = nbDescendants.get(v);
        if (vNbDescendants == null) {
            return -1;
        } else {
            return vNbDescendants;
        }
    }

    /**
     * Number of descendants getter.
     * @return a read only view of the number of descendants of each
     * vertex of the tree.
     */
    public Map<Vertex, Integer> getNbDescendants() {
            return Collections.unmodifiableMap(nbDescendants);
    }

    /**
     * Height getter.
     * @return the height of the tree
     */
    public int getHeight() {
        return height;
    }

    /**
     * The size of the data tree is the number of vertices in it.
     * @return the size of the tree
     */
    public int size() {
        return vertices.size();
    }

    /**
     * Maximal number of urls per edge getter.
     * @return the maximal number of urls per edge
     */
    public int getMaxNbUrlsEdge() {
        return maxNbUrlsEdge;
    }

    /**
     * With specified time, places the tree in a state (i.e. permutes the
     * children lists of its vertices) which minimizes the cost of the drawing
     * of this tree with specified parameters.
     *
     * @param rad       Required radial coordinate of the tree's root
     * @param secStart  Required angular sector starting angle of the
     *                  tree's root
     * @param secWidth  Required angular sector width of the tree's root
     * @param vRadius   Required vertex Radius of the tree in percentage of
     *                  the maximal allowed value, must be in ]0, 1[
     * @param wTree     Working tree, previous content will be overwritten
     * @param wLayout   Working layout, previous content will be overwritten
     * @param wDrawing  Working drawing, previous content will be overwritten
     * @param maxTime   Maximum time allocated to the minimization
     * @throws IllegalArgumentException if the vertex radius is out of bounds
     */
    public void minimizeCrossings(int rad, float secStart, float secWidth, float vRadius,
                                  Tree wTree, RadialLayout wLayout, Drawing wDrawing,
                                  long maxTime)
    throws IllegalArgumentException
    {
        if (!(0 < vRadius && vRadius < 1)) {
            throw new IllegalArgumentException("Vertex radius out of bounds");
        }
        long start = SystemClock.elapsedRealtime();
        wTree.copy(this);
        long startIter = SystemClock.elapsedRealtime();
        wLayout.update(wTree, rad, secStart, secWidth);
        wDrawing.update(wTree, wLayout, vRadius*wLayout.getMaxVertexRadius(),
                Drawing.CMIN, Drawing.CMIN,
                Drawing.CMAX, Drawing.CMAX,
                Drawing.C0, Drawing.C0);
        int minCost = SimulatedAnnealing.cost(wDrawing, wTree);
        int prevCost = minCost;

        long iterLength = SystemClock.elapsedRealtime()-startIter;
        // Estimated number of iterations before stop
        int nbIters = (Math.round(0.9f*maxTime/iterLength)-1);
        // Estimated average cost variation between initial state
        // and all other states
        int costDelta = this.getVertices().size()/4 + this.size();

        float temp = SimulatedAnnealing.goodInitTemp(costDelta);
        float decreaseFactor = SimulatedAnnealing.goodDecreaseFactor(nbIters, temp, costDelta);

        while (SystemClock.elapsedRealtime() < start + maxTime) {
            System.out.println(prevCost);
            // Considers a neighbourState
            wTree.neighbourState();
            wLayout.update(wTree, rad, secStart, secWidth);
            wDrawing.update(wTree, wLayout, vRadius*wLayout.getMaxVertexRadius(),
                    Drawing.CMIN, Drawing.CMIN,
                    Drawing.CMAX, Drawing.CMAX,
                    Drawing.C0, Drawing.C0);
            int newCost = SimulatedAnnealing.cost(wDrawing, wTree);
            float p = SimulatedAnnealing.acceptanceProbability(prevCost, newCost, temp);
            // Accepts it or not depending on its cost
            if (p < SimulatedAnnealing.rand.nextFloat()) {
                prevCost = newCost;
                if (newCost < minCost) {
                    minCost = newCost;
                    // Always sets the receiving Tree to the best state found yet
                    this.copy(wTree);
                }
            } else {
                wTree.previousState();
            }
            // Updates temperature
            temp *= decreaseFactor;
        }
    }


    /**
     * Makes the receiving Tree a copy of the specified Tree
     * @param tree Tree to copy
     */
    public void copy(Tree tree) {
        vertices.clear();
        vertices.addAll(tree.getVertices());
        edges.clear();
        edges.addAll(tree.getEdges());
        extraEdges.clear();
        extraEdges.addAll(tree.getExtraEdges());
        root = tree.getRoot();
        children.clear();
        children.putAll(tree.getChildren());
        lastShuffled = tree.getLastShuffled();
        preShuffleOrder = tree.getPreShuffleOrder();
        nbDescendants.clear();
        nbDescendants.putAll(tree.getNbDescendants());
        height = tree.getHeight();
    }

    /**
     * Two trees are equal if they have equal vertices, edges,
     * and are in the same state (i.e. same root, parent/children
     * relation, last children shuffle and previous children)
     *
     * @param o Object to compare with the receiving Tree
     * @return True if and only if o is an Tree, and it is equal
     *         to the receiving Tree.
     */
    public boolean equals(Object o) {
        if (!(o instanceof Tree)) {
            return false;
        } else {
            Tree that = (Tree) o;
            Vertex ls1 = this.getLastShuffled();
            Vertex ls2 = that.getLastShuffled();
            List<Vertex> ps1 = this.getPreShuffleOrder();
            List<Vertex> ps2 = that.getPreShuffleOrder();
            return (this.getVertices().equals(that.getVertices()) &&
                    this.getEdges().equals(that.getEdges()) &&
                    this.getExtraEdges().equals(that.getExtraEdges()) &&
                    this.getRoot().equals(that.getRoot()) &&
                    this.getChildren().equals(that.getChildren()) &&
                    ((ls1 == null && ls2 == null) ||
                            (ls1 != null && ls1.equals(ls2))) &&
                    ((ps1 == null && ps2 == null) ||
                            (ps1 != null && ps1.equals(ps2))));
        }
    }

    /**
     * Returns a hash code value for the Tree.
     * Two equal Tree objects have the same hash code value.
     *
     * @return a hash code value for the Tree.
     */
    @Override
    public int hashCode() {
        // Good implementation propose in Josh Bloch's Effective Java
        int result = 13;
        result = 37 * result + vertices.hashCode();
        result = 37 * result + edges.hashCode();
        result = 37 * result + extraEdges.hashCode();
        result = 37 * result + root.hashCode();
        result = 37 * result + children.hashCode();
        result = 37 * result + (lastShuffled != null ? lastShuffled.hashCode() : 0);
        result = 37 * result + (preShuffleOrder != null ? preShuffleOrder.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(vertices.size());
        for (Vertex v : vertices) {
            out.writeParcelable(v, 0);
        }
        ParcelableHelper.writeEdges(out, edges);
        ParcelableHelper.writeEdges(out, extraEdges);
        out.writeInt(root.getId());
        // children + nbDescendants
        for(Vertex v : vertices) {
            out.writeInt(v.getId());
            ParcelableHelper.writeVertices(out, children.get(v));
            out.writeInt(nbDescendants.get(v));
        }
        out.writeInt((lastShuffled == null) ? -1 : lastShuffled.getId());
        ParcelableHelper.writeVertices(out, preShuffleOrder);
        out.writeInt(height);

    }

    public static final Parcelable.Creator<Tree> CREATOR
            = new Parcelable.Creator<Tree>() {
        public Tree createFromParcel(Parcel in) {
            return new Tree(in);
        }

        public Tree[] newArray(int size) {
            return new Tree[size];
        }
    };

    private Tree(Parcel in) {
        int nbVertices = in.readInt();
        vertices = new TreeSet<>();
        Map<Integer, Vertex> vFinder = new HashMap<>(nbVertices);
        for (int i = 0; i < nbVertices; i++) {
            Vertex v = in.readParcelable(Vertex.class.getClassLoader());
            vertices.add(v);
            vFinder.put(v.getId(), v);
        }
        edges = new HashSet<>();
        ParcelableHelper.readEdges(in, edges, vFinder);
        extraEdges = new HashSet<>();
        ParcelableHelper.readEdges(in, extraEdges, vFinder);
        root = vFinder.get(in.readInt());
        children = new HashMap<>(nbVertices);
        nbDescendants = new HashMap<>(nbVertices);
        for (int i = 0; i < nbVertices; i++) {
            Vertex v = vFinder.get(in.readInt());
            List<Vertex> childrenList = new ArrayList<>();
            ParcelableHelper.readVertices(in, childrenList, vFinder);
            children.put(v, childrenList);
            nbDescendants.put(v, in.readInt());
        }
        int lsId = in.readInt();
        lastShuffled = (lsId == -1) ? null : vFinder.get(lsId);
        preShuffleOrder = new ArrayList<>();
        ParcelableHelper.readVertices(in, preShuffleOrder, vFinder);
        if (preShuffleOrder.size() == 0) {
            preShuffleOrder = null;
        }
        height = in.readInt();
    }
}

