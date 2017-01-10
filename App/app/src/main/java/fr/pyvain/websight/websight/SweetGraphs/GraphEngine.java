package fr.pyvain.websight.websight.SweetGraphs;

import android.animation.TimeInterpolator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.view.animation.FastOutSlowInInterpolator;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class GraphEngine {

    /**
     * Graph displayed
     */
    private Graph graph;

    /**
     * Current layout, or start layout when transitioning.
     */
    private ForestRadialLayout layout1;

    /**
     * End layout when transitioning.
     */
    private ForestRadialLayout layout2;

    /**
     * Current drawing of the graph's vertices
     */
    private Map<Integer, Circle> vDrawing;

    /**
     * Current drawing of the graph's edges
     */
    private Map<Integer, Map<Integer, Segment>> eDrawing;

    /**
     * Space on which the layout must be projected
     * Note : only the [0, canvasWidth] x [0, canvasHeight] is displayed
     * so moving this rectangle and resizing it allow to choose what will be
     * printed on the canvas
     */
    private Rectangle projection;

    /**
     * Visible part of the layout's projection
     * [0, canvasWidth] x [0, canvasHeight]
     */
    private Rectangle visible;

    /**
     * Minimal zoom.
     */
    private final static float MIN_ZOOM = 0.9f;
    /**
     * Maximal zoom.
     */
    private final static float MAX_ZOOM = 10f;
    /**
     * Current zoom.
     */
    private float zoom;

    private boolean fullLabels;

    private boolean firstDraw;

    /**
     * Currently selected Vertex. Null if no Vertex is selected.
     */
    private Vertex selectedVertex;

    /**
     * Time when the currently selected was selected, in ms since boot.
     */
    private long selectedSince;

    private static final float SELECTION_RADIUS_INCREASE = 2f;

    /**
     * Duration a vertex must stay selected before a configuration swap
     * occurs, in ms.
     */
    private static final long SELECTION_DURATION = 700;

    /**
     * Time interpolator for the vertex selection animation.
     */
    private static final TimeInterpolator SELECTION_INTERPOLATOR =
            new FastOutSlowInInterpolator();

    /**
     * Task computing and optimizing the next forest before swapping.
     */
    private layoutComputingTask layoutComputingTask;
    
    /**
     * Radius of the touching area. If a touch event is detected, all the
     * object in this radius are considered as touched.
     */
    private final static int TOUCH_RADIUS = 10;

    private static final long ANIMATION_DURATION = 3000;

    /**
     * Base interpolator for the vertex animation.
     */
    private static final TimeInterpolator BASE_INTERPOLATOR =
            new FastOutSlowInInterpolator();

    /**
     * Zoom interpolator for the vertex animation.
     */
    private static final TimeInterpolator ZOOM_INTERPOLATOR =
            new dezoomWaitZoom();

    /**
     * Zoom interpolator for the vertex animation.
     */
    private static final TimeInterpolator MOVE_INTERPOLATOR =
            new waitMoveWait();

    private static class dezoomWaitZoom implements TimeInterpolator {

        private TimeInterpolator baseInterpolator;

        public dezoomWaitZoom() {
            baseInterpolator = BASE_INTERPOLATOR;
        }

        @Override
        public float getInterpolation(float input) {
            input *= 2;
            if (input <= 1) {
                return baseInterpolator.getInterpolation(input);
            } else {
                return baseInterpolator.getInterpolation(2-input);
            }
        }
    }

    private static class waitMoveWait implements TimeInterpolator {

        private TimeInterpolator baseInterpolator;

        public waitMoveWait() {
            baseInterpolator = BASE_INTERPOLATOR;
        }

        @Override
        public float getInterpolation(float input) {
            return baseInterpolator.getInterpolation(input);
        }
    }



    /**
     * True if and only if recentering is ongoing,
     */
    private boolean recentering;

    /**
     * Time when the recentering started in ms since boot.
     */
    private long recenteringSince;

    /**
     * Center of the projection space at the beginning of the recentering.
     */
    private Point recenteringFrom;

    /**
     * Zoom at the beginning of the recentering
     */
    private float recenteringInitialZoom;


    /** Paint used to draw edges.*/
    public final Paint edgePaint;
    /** Paint used to draw vertices.*/
    public final Paint vertexPaint;
    /** Paint used to draw orbits.*/
    public final Paint orbitPaint;
    /** Paint used to draw text.*/
    public final Paint textPaint;



    /**
     * Initializes a new graph engine
     */
    public GraphEngine() {
        graph = new Graph();
        layout1 = new ForestRadialLayout();
        layout2 = new ForestRadialLayout();
        vDrawing = new HashMap<>();
        eDrawing = new HashMap<>();
        projection = new Rectangle(new Point(0, 0), 0, 0);
        visible = new Rectangle(new Point(0,0), 0, 0);
        zoom = MIN_ZOOM;
        fullLabels = true;
        firstDraw = false;
        selectedVertex = null;
        selectedSince = 0;
        recentering = false;
        recenteringSince = 0;
        recenteringFrom = new Point(0f, 0f);
        recenteringInitialZoom = 0f;
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
    public void setCurrent(Graph g) {
        graph = g;
        layout1.changeRoot(g, g.getVertices().first());
        firstDraw = true;
    }

    /**
     * Switch from full labels to only id, or the opposite.
     */
    public void changeLabels() {
        fullLabels = !fullLabels;
    }

    /**
     * Recenter the drawing of the layout.
     */
    public void recenter() {
        if (!recentering) {
            recentering = true;
            recenteringFrom = projection.getCenter();
            recenteringSince = SystemClock.elapsedRealtime();
            recenteringInitialZoom = zoom;
        }
    }

    /**
     * Multiplies the zoom scale by the specified factor,
     * keeping it in [1, MAX_SCALE].
     * @param zoomFactor The zoom factor to multiply the zoom scale by
     */
    public void zoom(float zoomFactor) {
        if (!recentering) {
            float newZoom = zoom * zoomFactor;
            if (MIN_ZOOM <= newZoom && newZoom <= MAX_ZOOM) {
                Point c = projection.getCenter();
                float dx = c.getX() - visible.getCenter().getX();
                float dy = c.getY() - visible.getCenter().getY();
                c.setX(visible.getCenter().getX() + zoomFactor * dx);
                c.setY(visible.getCenter().getY() + zoomFactor * dy);
                zoom = newZoom;
            }
        }
    }

    /**
     * Shifts along x and y axis with specified values.
     * @param xShift The shift to perform along x axis
     * @param yShift The shift to perform along y axis
     */
    public void shift(float xShift, float yShift) {
        if (!recentering) {
            Point c = projection.getCenter();
            c.setX(Math.min(Math.max(
                    c.getX() - xShift,
                    projection.getCenter().getX() - projection.getWidth()),
                    projection.getCenter().getX() + projection.getWidth())
            );
            c.setY(Math.min(Math.max(
                    c.getY() - yShift,
                    projection.getCenter().getY() - projection.getHeight()),
                    projection.getCenter().getY() + projection.getHeight())
            );
        }
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
        if (layout1.getRootId() != v.getId()) {
            layoutComputingTask = new layoutComputingTask();
            layoutComputingTask.execute(this);
        }
    }

    /**
     * Deselects the selected Vertex.
     */
    public void deselect() {
        if (SystemClock.elapsedRealtime() - selectedSince < SELECTION_DURATION) {
            selectedVertex = null;
        }
    }
    
    /**
     * If its exists, returns the vertex drawn at the specified point.
     * Else returns null.
     * @param touchPoint Point at which to look for a Vertex
     * @return The vertex drawn at the specified point, or null.
     */
    public Vertex vertexAt(Point touchPoint) {
        for (Vertex v : graph.getVertices()) {
            Circle c = vDrawing.get(v.getId());
            if (c != null) {
                float d = Point.distanceBetween(c.getCenter(), touchPoint);
                if (d <= c.getRadius() + TOUCH_RADIUS) {
                    return v;
                }
            }
        }
        return null;
    }

    /**
     * If its exists, returns the edge drawn at the specified point.
     * Else returns null.
     * @param touchPoint Point at which to look for an Edge
     * @return The edge drawn at the specified point, or null.
     */
    public Edge edgeAt(Point touchPoint) {
        for (Edge e : graph.getEdges()) {
            Map<Integer, Segment> m = eDrawing.get(e.getEnd1().getId());
            if (m != null) {
                Segment s = m.get(e.getEnd2().getId());
                if (s != null) {
                    if (s.intersectsWith(new Circle(touchPoint, TOUCH_RADIUS))) {
                        return e;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Draws the current layout in the specified canvas
     * @param canvas Canvas to draw in
     */
    public void draw(Canvas canvas) {
        // Updates the projection space rectangle, and the visible space rectangle
        // according to the canvas' current size, the current zoom and the current shift
        visible.setWidth(canvas.getWidth());
        visible.setHeight(canvas.getHeight());
        visible.getCenter().setX(canvas.getWidth()/2);
        visible.getCenter().setY(canvas.getHeight()/2);


        if (firstDraw) {
            projection.getCenter().setX(canvas.getWidth() / 2);
            projection.getCenter().setY(canvas.getHeight() / 2);
            firstDraw = false;
        }
        if (recentering) {
            Point c = projection.getCenter();
            float step = ((float)(SystemClock.elapsedRealtime() - recenteringSince)) / ANIMATION_DURATION;
            if (step > 1) {
                recentering = false;
            } else {
                float zoomStep = ZOOM_INTERPOLATOR.getInterpolation(step);
                float moveStep = step;
                c.setX((1 - moveStep) * recenteringFrom.getX() + moveStep * visible.getCenter().getX());
                c.setY((1 - moveStep) * recenteringFrom.getY() + moveStep * visible.getCenter().getY());
                zoom = (1 - zoomStep) * recenteringInitialZoom + zoomStep * 1.2f;
            }
        }
        projection.setWidth(canvas.getWidth()*zoom);
        projection.setHeight(canvas.getHeight()*zoom);


        ForestRadialLayout layout = layoutToDraw();



        // Sets text size
        setTextSizeToWidth("00", layout.projectedMinRadius(projection));

        drawOrbits(canvas, layout);
        drawVertices(canvas, layout);
        drawEdges(canvas, layout);
        drawSelection(canvas);
    }

    private ForestRadialLayout layoutToDraw() {
        if (selectedVertex != null) {
            long elapsed = SystemClock.elapsedRealtime() - selectedSince;
            if (selectedVertex.getId() == layout1.getRootId()) {
                if (elapsed > SELECTION_DURATION + ANIMATION_DURATION) {
                    selectedVertex = null;
                }
            } else {
                if (SELECTION_DURATION <= elapsed && elapsed <= SELECTION_DURATION + ANIMATION_DURATION) {
                    if (!recentering) {
                        recenter();
                    }
                    float step = ((float) (elapsed - SELECTION_DURATION)) / (ANIMATION_DURATION);
                    step = MOVE_INTERPOLATOR.getInterpolation(step);
                    return new ForestRadialLayout(graph, layout1, layout2, step);
                } else if (elapsed > SELECTION_DURATION + ANIMATION_DURATION) {
                    selectedVertex = null;
                    ForestRadialLayout tmp = layout1;
                    layout1 = layout2;
                    layout2 = tmp;
                }
            }
        }
        return layout1;
    }

    private float getTextWidth(String text) {
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }

    private float getTextHeight(String text) {
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.height();
    }

    /**
     * Sets the text width of paintText such as the specified text is written
     * on the specified width
     * @param text  Text to draw
     * @param width Width that the text must have
     */
    private void setTextSizeToWidth(String text, float width) {
        textPaint.setTextSize(30);
        textPaint.setTextSize(width * 30/ getTextWidth(text));
    }

    /**
     * Draws the orbits, i.e. the concentric circles centred on the
     * origin of the layout.
     * @param canvas Canvas to draw on
     * @param layout Layout to draw
     */
    private void drawOrbits(Canvas canvas, ForestRadialLayout layout) {
        for (Circle o : layout.projectedOrbits(projection)) {
            if (visible.contains(o)) {
                canvas.drawCircle(o.getCenter().getX(), o.getCenter().getY(),
                        o.getRadius(), orbitPaint);
            }
        }
    }

    /**
     * Draws the visible segments of the layout on the specified canvas.
     * @param canvas Canvas to draw on
     * @param layout Layout to draw
     */
    private void drawEdges(Canvas canvas, ForestRadialLayout layout) {
        eDrawing.clear();
        Map<Integer, Map<Integer, Segment>> segments = layout.projectedEdges(projection);
        for (Edge e : graph.getEdges()) {
            int id1 = e.getEnd1().getId();
            int id2 = e.getEnd2().getId();
            Segment s = segments.get(id1).get(id2);
            if (vDrawing.containsKey(id1) || vDrawing.containsKey(id2)) {
                if (!eDrawing.containsKey(id1)) {
                    eDrawing.put(id1, new HashMap<Integer, Segment>());
                }
                eDrawing.get(id1).put(id2, s);
                Point p1 = s.getLeftEnd();
                Point p2 = s.getRightEnd();
                edgePaint.setStrokeWidth(s.getThickness());
                canvas.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY(), edgePaint);

            }
        }
    }

    /**
     * Draws the visible vertices of the layout on the specified canvas.
     * @param canvas Canvas to draw on
     * @param layout Layout to draw
     */
    private void drawVertices(Canvas canvas, ForestRadialLayout layout) {
        vDrawing.clear();
        Map<Integer, Circle> circles = layout.projectedVertices(projection);
        for (Vertex v : graph.getVertices()) {
            Circle c = circles.get(v.getId());
            if (visible.contains(c)) {
                vDrawing.put(v.getId(), c);
                Point p = c.getCenter();
                canvas.drawCircle(p.getX(), p.getY(), c.getRadius(), vertexPaint);
                String label = (fullLabels) ? v.getLabel() : String.valueOf(v.getId());
                canvas.drawText(label, p.getX()-getTextWidth(label)/2,
                        p.getY()+getTextHeight(label)/2, textPaint);
            }
        }
    }

    /**
     * Draw the selected vertex a little bigger than the other vertices
     * on the specified canvas
     * @param canvas Canvas to draw on
     */
    private void drawSelection(Canvas canvas) {
        if (selectedVertex != null) {
            long elapsed = SystemClock.elapsedRealtime() - selectedSince;
            if (elapsed <= SELECTION_DURATION) {
                Circle c = vDrawing.get(selectedVertex.getId());
                Point p = c.getCenter();
                float step = SELECTION_INTERPOLATOR.getInterpolation((float)elapsed/SELECTION_DURATION);
                step = 0.5f - Math.abs(step - 0.5f);
                float r = (1-step)*c.getRadius() + step*0.25f*Math.min(visible.getWidth(), visible.getHeight());
                canvas.drawCircle(p.getX(), p.getY(), r, vertexPaint);
                String label = (fullLabels) ? selectedVertex.getLabel() : String.valueOf(selectedVertex.getId());
                canvas.drawText(label, p.getX()-getTextWidth(label)/2,
                        p.getY()+getTextHeight(label)/2, textPaint);
            }

        }
    }

    /**
     * Computes a layout whose main root is the selected vertex before
     */
    private void computeNext() {
        layout2.changeRoot(graph, selectedVertex);
        Map<Integer, Circle> vertices1 = layout1.projectedVertices(projection);
        Map<Integer, Circle> vertices2 = layout2.projectedVertices(projection);
        Map<Integer, Map<Integer, Segment>> segments1 = layout1.projectedEdges(projection);
        Map<Integer, Map<Integer, Segment>> segments2 = layout2.projectedEdges(projection);
        float bestAngle = 1f;
        float minDist = Float.MAX_VALUE;
        for (int i = 1; i <= 20; i++) {
            layout2.rotate(2*(float)Math.PI/10);
            float dist = 0;
            for (Vertex v : graph.getVertices()) {
                int id = v.getId();
                dist += Point.distanceBetween(vertices1.get(id).getCenter(), vertices2.get(id).getCenter());
            }
            if (dist < minDist) {
                minDist = dist;
                bestAngle = i*2*(float)Math.PI/10;
            }
        }
        layout2.rotate(bestAngle);
    }

    /**
     * <p>Layout computing task.</p>
     * <p>
     * @author Etienne Thiery, etienne.thiery@wanadoo.fr
     * </p>
     */
    static class layoutComputingTask extends AsyncTask<GraphEngine, Void, Void> {

        public layoutComputingTask () {
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
