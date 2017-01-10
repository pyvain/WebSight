package fr.pyvain.websight.websight;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.Set;

import fr.pyvain.websight.websight.Geometry.CPoint;
import fr.pyvain.websight.websight.PersonalDataGraph.Edge;
import fr.pyvain.websight.websight.PersonalDataGraph.InputGraph;
import fr.pyvain.websight.websight.PersonalDataGraph.Vertex;

/**
 * Class allowing the display of a graph.
 */
public class OldGraphView extends View {

    /**
     * Graph engine.
     */
    private final GraphEngine graphEngine;

    /**
     * Gesture detector.
     */
    private final GestureDetectorCompat gestureDetector;

    /**
     * Scale gesture detector.
     */
    private final ScaleGestureDetector scaleDetector;

    /**
     * Generic constructor
     * @param context View context
     */
    public OldGraphView(Context context) {
        super(context);
        graphEngine = new GraphEngine();
        gestureDetector = new GestureDetectorCompat(context, new GestureListener());
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        init();
    }

    /**
     * Constructor with attributes
     * @param context View context
     * @param attrs Attributes (not used)
     */
    public OldGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        graphEngine = new GraphEngine();
        gestureDetector = new GestureDetectorCompat(context, new GestureListener());
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        init();
    }

    /**
     * Constructor with attributes and style
     *
     * @param context View Context
     * @param attrs Attributes (not used)
     * @param defStyle Style (not used)
     */
    public OldGraphView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        graphEngine = new GraphEngine();
        gestureDetector = new GestureDetectorCompat(context, new GestureListener());
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        init();
    }

    /**
     * Initializes the view
     */
    private void init() {
        graphEngine.edgePaint.setStyle(Paint.Style.STROKE);
        graphEngine.edgePaint.setStrokeWidth(3);
        graphEngine.edgePaint.setARGB(255, 0, 0, 0);

        graphEngine.vertexPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        graphEngine.vertexPaint.setARGB(255, 255, 255, 255);

        graphEngine.orbitPaint.setStyle(Paint.Style.STROKE);
        graphEngine.orbitPaint.setStrokeWidth(2);
        graphEngine.orbitPaint.setARGB(255, 200, 200, 200);

        graphEngine.textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        graphEngine.textPaint.setARGB(255, 0, 0, 0);
    }
    /**
     * Displays the graph described by the specified JSON formated string
     * @param graphJSON JSON encoded graph to display
     * @throws IllegalArgumentException if the JSON encoded graph is invalid
     */
    public void setGraph(String graphJSON) {
        InputGraph g = new InputGraph(graphJSON);
        graphEngine.setCurrent(g);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        graphEngine.draw(canvas);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.d("lowl", event.toString());
        scaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);

        int x = Math.round(event.getX());
        int y = Math.round(event.getY());
        CPoint touchPoint = new CPoint(x, y);
        Vertex touched = graphEngine.vertexAt(touchPoint);
        Vertex selected = graphEngine.selectedVertex();

        switch (event.getAction()) {
            // Touching cause selection (if there is not already a touch)
            case MotionEvent.ACTION_DOWN:
                if (selected == null && touched != null) {
                    graphEngine.select(touched);
                    invalidate();
                }
                break;
            // Keeping touching causes a fantastic animation
            case MotionEvent.ACTION_MOVE:
                if (selected != null && selected == touched) {
                    graphEngine.swapIfNeeded();
                    invalidate();
                }
                break;
            // Stopping touching causes deselection
            case MotionEvent.ACTION_UP:
                if (touched == selected) {
                    graphEngine.deselect();
                    invalidate();
                }
                break;
        }
        return true;
    }

    /**
     * <p>GestureListener which detects simple touches on vertices and edges,
     * and starts the AdviceDisplay activity.</p>
     *
     * <p>
     * @author Etienne Thiery, etienne.thiery@wanadoo.fr
     * </p>
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            int x = Math.round(event.getX());
            int y = Math.round(event.getY());
            CPoint pointer = new CPoint(x, y);
            String keywords[] = null;
            Set<String> urls = null;
            Vertex v;
            Edge e;
            if ((v = graphEngine.vertexAt(pointer)) != null) {
                keywords = new String[]{v.getKeyword()};
                urls = v.getData().getURLs();
            } else if ((e = graphEngine.edgeAt(pointer)) != null) {
                keywords = new String[]{
                        e.getEnd1().getKeyword(),
                        e.getEnd2().getKeyword()};
                urls = e.getData().getURLs();
            }
            if (urls != null) {
                Intent goToAdvice = new Intent(getContext(), AdviceDisplay.class);
                String[] urlArray = urls.toArray(new String[urls.size()]);
                goToAdvice.putExtra("urls", urlArray);
                goToAdvice.putExtra("keywords", keywords);
                getContext().startActivity(goToAdvice);
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY)
        {
            Log.d("lowl", "scroooool");
            graphEngine.updateScrollOffset(distanceX, distanceY);
            invalidate();
            return true;
        }
    }

    /**
     * <p>ScaleListener which detects pinch movements and zooms in or out in the
     * graph.</p>
     *
     * <p>
     * @author Etienne Thiery, etienne.thiery@wanadoo.fr
     * </p>
     */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            graphEngine.updateScale(detector.getScaleFactor());
            invalidate();
            return true;
        }
    }
}