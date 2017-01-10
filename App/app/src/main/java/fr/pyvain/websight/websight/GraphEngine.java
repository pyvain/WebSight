package fr.pyvain.websight.websight;

import android.animation.TimeInterpolator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Log;

import java.util.Map;

import fr.pyvain.websight.websight.Geometry.CPoint;
import fr.pyvain.websight.websight.Geometry.Segment;
import fr.pyvain.websight.websight.PersonalDataGraph.Drawing;
import fr.pyvain.websight.websight.PersonalDataGraph.Edge;
import fr.pyvain.websight.websight.PersonalDataGraph.Forest;
import fr.pyvain.websight.websight.PersonalDataGraph.InputGraph;
import fr.pyvain.websight.websight.PersonalDataGraph.RadialLayout;
import fr.pyvain.websight.websight.PersonalDataGraph.Tree;
import fr.pyvain.websight.websight.PersonalDataGraph.Vertex;

class GraphEngine {

    // Graph structures

    /**
     * Last drawing drawn on canvas.
     * Depends on the current Forest/Radial Layout couple,
     * the scale, the size of the canvas and the size of the vertices.
     */
    private final Drawing currDrawing;

    /**
     * Current Forest, i.e. current choice of root for each connected
     * component of the graph, and current choice of main connected
     * component.
     */
    private final Forest currForest;

    /**
     * Current Radial Layout, i.e. intermediate layer on top of the current
     * Forest.
     */
    private final RadialLayout currLayout;

    /**
     * Next Forest, i.e. Forest to set as current Forest once the
     * configuration swap is completed.
     */
    private final Forest nextForest;


    // Temporary structures

    /**
     * Tree used any time an intermediate temporary Tree structure is needed.
     */
    private final Tree tmpTree;

    /**
     * Radial Layout used any time an intermediate temporary Radial Layout
     * structure is needed.
     */
    private final RadialLayout tmpLayout;

    /**
     * Drawing used any time an intermediate temporary Drawing structure is
     * needed.
     */
    private final Drawing tmpDrawing;



    // Vertex Selection related

    /**
     * Currently selected Vertex. Null if no Vertex is selected.
     */
    private Vertex selectedVertex;

    /**
     * Time when the currently selected was selected, in ms since boot.
     */
    private long selectedSince;

    /**
     * Duration a vertex must stay selected before a configuration swap
     * occurs, in ms.
     */
    private final long selectionDuration;

    /**
     * Default selection duration, in ms.
     */
    private static final long SELECTION_DURATION = 1000;

    /**
     * Time interpolator for the vertex selection animation.
     */
    private final TimeInterpolator selectionInterpolator;

    /**
     * Default time interpolator for the vertex selection animation.
     */
    private static final TimeInterpolator SELECTION_INTERPOLATOR =
            new FastOutSlowInInterpolator();



    // Configuration swapping related

    /**
     * Task computing and optimizing the next forest before swapping.
     */
    private InitSwapTask initSwapTask;



    // Drawing attributes

    /**
     * Density of the drawing, i.e ratio
     * Effective radius of the vertices / maximal radius without touching vertices.
     * In ]0, 1[.
     */
    private final float density;

    /**
     * Default density of the drawing.
     */
    private final static float DENSITY = 0.6f;

    /**
     * Radius of the touching area. If a touch event is detected, all the
     * object in this radius are considered as touched.
     */
    private int touchRadius;

    /**
     * Default touch radius.
     */
    private final static int TOUCH_RADIUS = 10;

    /**
     * Current scale of the drawing. A scale of 1 means the drawing encompasses
     * exactly the whole graph. A higher scale means it encompasses a smaller
     * section of it, centered on (x0, y0).
     */
    private float scale;

    /**
     * Current offset of the drawing along xAxis. An offset of 0 means the
     * drawing is centered in the canvas.
     */
    private int xScrollOffset;

    /**
     * Current offset of the drawing along yAxis. An offset of 0 means the
     * drawing is centered in the canvas.
     */
    private int yScrollOffset;

    /**
     * Default scale.
     */
    private final static float INIT_SCALE = 1.0f;

    /**
     * Maximal scale.
     */
    private final static float MAX_SCALE = 3.0f;

    /**
     * Maximal width of an edge, in percentage of the vertex radius.
     * In ]0, 1[
     */
    private final float maxEdgeWidth;

    /**
     * Default maximal edge width;
     */
    private static final float MAX_EDGE_WIDTH = 0.2f;

    /**
     * Maximal increase of the selected vertex's radius, in percentage
     * of its normal radius.
     */
    private final float selectionRadiusIncrease;

    /**
     * Default selection radius increase.
     */
    private static final float SELECTION_RADIUS_INCREASE = 4f;

    /** Current minimal x-coordinate of the drawing.*/
    private int xMin;
    /** Current maximal x-coordinate of the drawing.*/
    private int xMax;
    /** Current minimal y-coordinate of the drawing.*/
    private int yMin;
    /** Current maximal y-coordinate of the drawing.*/
    private int yMax;
    /** Current x-coordinate of the current Radial Layout's origin
     * in the drawing.*/
    private int x0;
    /** Current y-coordinate of the current Radial Layout's origin
     * in the drawing.*/
    private int y0;

    /**
     * Current width of the canvas
     */
    private int width;

    /**
     * Current height of the canvas
     */
    private int height;

    /** Paint used to draw edges.*/
    public final Paint edgePaint;
    /** Paint used to draw vertices.*/
    public final Paint vertexPaint;
    /** Paint used to draw orbits.*/
    public final Paint orbitPaint;
    /** Paint used to draw text.*/
    public final Paint textPaint;

    /**
     * Test text size used in the method withToSize() to determine the textSize
     * which corresponds to a given width of a text.
     */
    private final static float TEST_TEXT_SIZE = 30f;

    /**
     * Sets the text width of paintText such as the specified text is written
     * on the specified width
     * @param text  Text to draw
     * @param width Width that the text must have
     */
    private void setTextSizeToWidth(String text, float width) {
        textPaint.setTextSize(TEST_TEXT_SIZE);
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        textPaint.setTextSize(width * TEST_TEXT_SIZE / bounds.width());
    }




    /**
     * Initializes a new graph engine
     */
    public GraphEngine() {
        currDrawing = new Drawing();
        currForest = new Forest();
        currLayout = new RadialLayout();
        nextForest = new Forest();
        tmpTree = new Tree();
        tmpLayout = new RadialLayout();
        tmpDrawing = new Drawing();
        selectedVertex = null;
        selectedSince = 0;
        selectionDuration = SELECTION_DURATION;
        selectionInterpolator = SELECTION_INTERPOLATOR;
        initSwapTask = null;
        density = DENSITY;
        touchRadius = TOUCH_RADIUS;
        scale = INIT_SCALE;
        xScrollOffset = yScrollOffset = 0;
        maxEdgeWidth = MAX_EDGE_WIDTH;
        selectionRadiusIncrease = SELECTION_RADIUS_INCREASE;
        xMin = xMax = yMin = yMax = x0 = y0 = 0;
        width = height = 0;
        edgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        vertexPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        orbitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
    }

    /**
     * Sets the graph engine to display the specified input graph, with
     * emphasis on its first vertex.
     * @param g Graph to display
     */
    public void setCurrent(InputGraph g) {
        currForest.update(g, g.getVertices().first());
        currForest.minimizeCrossings(density, tmpTree, tmpLayout,
                tmpDrawing, selectionDuration);
        currLayout.update(currForest);
    }

    /**
     * Draws the current graph in the specified canvas
     * @param canvas Canvas to draw in
     */
    public void draw(Canvas canvas) {
        // Updates dimensions
        width = canvas.getWidth();
        height = canvas.getHeight();
        x0 = width/2 - xScrollOffset;
        y0 = height/2 - yScrollOffset;
        xMin = Math.round(width*(1-scale)/2);
        yMin = Math.round(height*(1-scale)/2);
        xMax = Math.round(width*(1+scale))/2;
        yMax = Math.round(height*(1+scale)/2);

        Log.d("lowl", "xScroll offset : " + xScrollOffset);
        // Updates the drawing, considering zoom scale
        currDrawing.update(currForest, currLayout,
                DENSITY*currLayout.getMaxVertexRadius(),
                xMin, yMin, xMax, yMax, x0, y0);

        canvas.drawColor(0x00000000);
        drawOrbits(canvas);
        drawEdges(canvas);
        drawSelection(canvas);
        drawVertices(canvas);

        // Updates the touching radius to make it easier to select a vertex
        touchRadius = Math.round(currDrawing.getVertexRadius());
    }

    /**
     * Draws the orbits, i.e. the concentric circles centred on the
     * origin of the layout.
     * @param canvas Canvas to draw on
     */
    private void drawOrbits(Canvas canvas) {
        float unit = currDrawing.getUnit();
        // radius of the current section of the graph which is displayed
        float frameRadius = (float)(Math.sqrt(width*width + height*height)/(2*unit));
        float maxR = Math.min(currLayout.getLayoutRadius(), frameRadius);
        for (int r = 1; r < maxR; r++) {
            canvas.drawCircle(x0, y0, r*unit, orbitPaint);
        }
    }

    /**
     * Draws the segments of the layout on the specified canvas, with a width
     * proportional to the number of urls in the corresponding edge.
     * @param canvas Canvas to draw on
     */
    private void drawEdges(Canvas canvas) {
        int maxNbUrls = currForest.getMaxNbUrlsEdge();
        float maxSize = currDrawing.getVertexRadius()* maxEdgeWidth;

        Map<Edge, Segment> segments = currDrawing.getSegments();
        for (Edge e : currForest.getEdges()) {
            float width = maxSize * e.getData().getURLs().size() / maxNbUrls;
            edgePaint.setStrokeWidth(width);
            Segment s = segments.get(e);
            CPoint p1 = s.getP1();
            CPoint p2 = s.getP2();
            canvas.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY(), edgePaint);
        }
    }

    /**
     * Draws the visible vertices of the layout on the specified canvas.
     * @param canvas Canvas to draw on
     */
    private void drawVertices(Canvas canvas) {
        float vR = currDrawing.getVertexRadius();
        Map<Vertex, CPoint> centers = currDrawing.getCenters();
        for (Vertex v : currForest.getVertices()) {
            CPoint center = centers.get(v);
            int x = center.getX();
            int y = center.getY();
            if ((-vR <= x) && (x < width+vR) && (-vR <= y) && (y < height+vR)) {
                canvas.drawCircle(x, y, currDrawing.getVertexRadius(), vertexPaint);
                setTextSizeToWidth(v.getKeyword(), 2*vR);
                canvas.drawText(v.getKeyword(), x-vR, y, textPaint);
            }
        }
    }

    /** Draw the selected vertex a little bigger than the other vertices
     * on the specified canvas
     * @param canvas Canvas to draw on
     */
    private void drawSelection(Canvas canvas) {
        long elapsed = SystemClock.elapsedRealtime() - selectedSince;
        if (selectedVertex != null && 0 <= elapsed && elapsed <= selectionDuration) {
            CPoint p = currDrawing.getCenters().get(selectedVertex);
            float ratio = selectionInterpolator.getInterpolation(
                    (float) elapsed / selectionDuration);
            ratio = 0.5f - Math.abs(ratio - 0.5f);
            float r = (1 + selectionRadiusIncrease*ratio) * currDrawing.getVertexRadius();
            canvas.drawCircle(p.getX(), p.getY(), r, vertexPaint);
        }
    }

    /**
     * If its exists, returns the vertex drawn at the specified point.
     * Else returns null.
     * @param touchPoint Point at which to look for a Vertex
     * @return The vertex drawn at the specified point, or null.
     */
    public Vertex vertexAt(CPoint touchPoint) {
        return currDrawing.vertexAt(touchPoint, touchRadius);
    }

    /**
     * If its exists, returns the edge drawn at the specified point.
     * Else returns null.
     * @param touchPoint Point at which to look for an Edge
     * @return The edge drawn at the specified point, or null.
     */
    public Edge edgeAt(CPoint touchPoint) {
        return currDrawing.edgeAt(touchPoint, touchRadius);
    }

    /**
     * Returns the selected vertex.
     * @return The selected vertex.
     */
    public Vertex selectedVertex() {
        return selectedVertex;
    }

    /**
     * Selects the specified Vertex, and starts a new thread to compute
     * a forest whose main root is this Vertex.
     * @param v Vertex to select
     */
    public void select(Vertex v) {
        selectedVertex = v;
        selectedSince = SystemClock.elapsedRealtime();
        if (currForest.getMainTree().getRoot() != v) {
            initSwapTask = new InitSwapTask();
            initSwapTask.execute(this);
        }
    }

    /**
     * Deselects the selected Vertex.
     */
    public void deselect() {
        selectedVertex = null;
    }

    /**
     * Multiplies the zoom scale by the specified scale factor,
     * keeping it in [1, MAX_SCALE].
     * @param scaleFactor The scale factor to multiply the zoom scale
     *                    by
     */
    public void updateScale(float scaleFactor) {
        scale *= scaleFactor;
        // Bounds the scale in [1, MAX_SCALE]
        scale = Math.max(1f, Math.min(MAX_SCALE, scale));
    }

    /**
     * Adds the specified offsets along x and y axis to the scroll offsets,
     * keeping it in [-w/2+1, w/2-1] [-h/2+1, h/2-1]
     * @param xOffset The scrolling offset to add along x axis
     * @param yOffset The scrolling offset to add along y axis
     */
    public void updateScrollOffset(float xOffset, float yOffset) {
        xScrollOffset += Math.round(xOffset);
        xScrollOffset = Math.round(Math.max(1-width/2, Math.min(width/2-1, xScrollOffset)));
        yScrollOffset += Math.round(yOffset);
        yScrollOffset = Math.round(Math.max(1-height/2, Math.min(height/2-1, yScrollOffset)));
    }


    /**
     * Computes a forest whose main root is the selected vertex before
     * the end of the selection duration, and stores it in the next forest.
     */
    private void computeNext() {
        nextForest.copy(currForest);
        nextForest.setAsRoot(selectedVertex);
        nextForest.setTreeAsMain(selectedVertex);
        long duration = (selectedSince + selectionDuration
                - SystemClock.elapsedRealtime() + 100);
        nextForest.minimizeCrossings(density, tmpTree, tmpLayout, tmpDrawing, duration);
    }

    /**
     * Checks if the selection duration is over, and if yes, replaces the
     * current forest by the newly computed next forest.
     */
    public void swapIfNeeded() {
        if (currForest.getMainTree().getRoot() != selectedVertex &&
                SystemClock.elapsedRealtime() - selectedSince > selectionDuration)
        {
            currForest.copy(nextForest);
            currLayout.update(currForest);
            deselect();
        }
    }

    /**
     * <p>Forest computing and optimizing task.</p>
     * * <p>
     * @author Etienne Thiery, etienne.thiery@wanadoo.fr
     * </p>
     */
    static class InitSwapTask extends AsyncTask<GraphEngine, Void, Void> {

        public InitSwapTask () {
            super();
        }

        @Override
        protected Void doInBackground (GraphEngine... args) {
            GraphEngine graphEngine = args[0];
            graphEngine.computeNext();
            return null;
        }
    }
}
