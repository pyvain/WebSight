package fr.pyvain.websight.websight.SweetGraphs;


import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Set;

import fr.pyvain.websight.websight.AdviceDisplay;
import fr.pyvain.websight.websight.R;


public class GraphView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;
    private DrawingThread drawingThread;

    /**
     * Graph engine.
     */
    private GraphEngine graphEngine;

    /**
     * Gesture detector.
     */
    private GestureDetectorCompat gestureDetector;

    /**
     * Scale gesture detector.
     */
    private ScaleGestureDetector scaleDetector;

    /**
     * Generic constructor
     * @param context View context
     */
    public GraphView (Context context) {
        super(context);
        init(context);
    }

    /**
     * Constructor with attributes
     * @param context View context
     * @param attrs Attributes (not used)
     */
    public GraphView (Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Constructor with attributes and style
     *
     * @param context View Context
     * @param attrs Attributes (not used)
     * @param defStyle Style (not used)
     */
    public GraphView (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * Initializes the view
     */
    private void init(Context context) {
        graphEngine = new GraphEngine();
        gestureDetector = new GestureDetectorCompat(context, new GestureListener());
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        holder = getHolder();
        holder.addCallback(this);
        drawingThread = null;

        //graphEngine.setCurrent(Graph.randomInputGraph(12, 10));
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
        Graph g = new Graph(graphJSON);
        graphEngine.setCurrent(g);
        invalidate();
    }

    public void changeLabels() {
        graphEngine.changeLabels();
    }


    public void update(Canvas pCanvas) {
        pCanvas.drawColor(ContextCompat.getColor(getContext(), R.color.colorBg));
        graphEngine.draw(pCanvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void resume() {
        drawingThread = new DrawingThread();
        drawingThread.keepDrawing = true;
        drawingThread.start();
    }

    public void pause() {
        if (drawingThread != null) {
            drawingThread.keepDrawing = false;
            boolean joined = false;
            while (!joined) {
                try {
                    drawingThread.join();
                    joined = true;
                } catch (InterruptedException e) {
                }
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        resume();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pause();
    }

    private class DrawingThread extends Thread {
        // Used to stop drawing when not needed
        boolean keepDrawing = true;

        @Override
        public void run() {

            while (keepDrawing) {
                Canvas canvas = null;
                try {
                    canvas = holder.lockCanvas();
                    synchronized (holder) {
                        update(canvas);
                    }
                } finally {
                    if (canvas != null)
                        holder.unlockCanvasAndPost(canvas);
                }
                // 50 fps
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {}
            }
        }


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
            float x = event.getX();
            float y = event.getY();
            Point pointer = new Point(x, y);
            String labels[] = null;
            Set<String> urls = null;
            Vertex v;
            Edge e;
            if ((v = graphEngine.vertexAt(pointer)) != null) {
                labels = new String[]{v.getLabel()};
                urls = v.getData().getURLs();
            } else if ((e = graphEngine.edgeAt(pointer)) != null) {
                labels = new String[]{
                        e.getEnd1().getLabel(),
                        e.getEnd2().getLabel()};
                urls = e.getData().getURLs();
            }
            if (urls != null) {
                Intent goToAdvice = new Intent(getContext(), AdviceDisplay.class);
                String[] urlArray = urls.toArray(new String[urls.size()]);
                goToAdvice.putExtra("urls", urlArray);
                goToAdvice.putExtra("keywords", labels);
                getContext().startActivity(goToAdvice);
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY)
        {
            graphEngine.shift(distanceX, distanceY);
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            graphEngine.recenter();
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
            graphEngine.zoom(detector.getScaleFactor());
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);

        float x = event.getX();
        float y = event.getY();
        Point touchPoint = new Point(x, y);
        Vertex touched = graphEngine.vertexAt(touchPoint);
        Vertex selected = graphEngine.selectedVertex();

        switch (event.getAction()) {
            // Touching cause selection (if there is not already a touch)
            case MotionEvent.ACTION_DOWN:
                if (selected == null && touched != null) {
                    graphEngine.select(touched);
                }
                break;
            // Stopping touching causes deselection
            case MotionEvent.ACTION_UP:
                if (touched == selected) {
                    graphEngine.deselect();
                }
                break;
        }
        return true;
    }
}