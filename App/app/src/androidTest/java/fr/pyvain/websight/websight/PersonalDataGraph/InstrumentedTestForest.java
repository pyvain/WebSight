package fr.pyvain.websight.websight.PersonalDataGraph;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * <p>
 *     @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedTestForest {

    @BeforeClass
    public static void beforeTests() {
        System.out.println("Testing class Forest\n");
    }

    @Test
    public void testParcelable(){
        for (int i = 0; i < 10; i++) {
            InputGraph graph = InputGraph.randomInputGraph(10, 10);
            Forest forestIn = new Forest(graph, graph.getVertices().first());

            Parcel p = Parcel.obtain();
            forestIn.writeToParcel(p, 0);
            p.setDataPosition(0);
            Forest forestOut = Forest.CREATOR.createFromParcel(p);
            assertEquals(forestIn, forestOut);
        }
    }

    @Test
    public void testMinimizeCrossings() {
        List<Vertex> vertices = new ArrayList<>();
        Vertex[] v = new Vertex[13];
        for (int i = 0; i < 13; i++) {
            Vertex vertex = new Vertex(i, "keyword" + i);
            vertices.add(vertex);
            v[i] = vertex;
        }

        List<Edge> edges = new ArrayList<>();
        Vertex[] ends1 = new Vertex[]{
                v[1], v[1], v[1], v[1], v[1], v[7], v[7], v[7], v[7], v[7], v[3], v[9]};
        Vertex[] ends2 = new Vertex[]{
                v[2], v[3], v[4], v[5], v[6], v[8], v[9], v[10], v[11], v[12], v[5], v[11]};
        for (int i = 0; i < 12; i++) {
            Edge e = new Edge(ends1[i], ends2[i]);
            edges.add(e);
        }
        InputGraph graph = null;
        try {
            graph = new InputGraph(vertices, edges);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        int i = 0;
        for (int j = 0; j < 10; j++) {
            Forest forest = new Forest(graph, v[0]);
            Tree wTree = new Tree(v[0]);
            RadialLayout wLayout = new RadialLayout(forest);
            Drawing wDrawing = new Drawing(forest, wLayout, wLayout.getMaxVertexRadius() / 2,
                    0, 0, 600, 800, 300, 400);

            assertEquals(0, wDrawing.nbVertexCrossings(forest.getEdges()));
            assertEquals(2, wDrawing.nbEdgeIntersections());
            forest.minimizeCrossings(0.5f, wTree, wLayout, wDrawing, 400);
            wLayout.update(forest);
            wDrawing.update(forest, wLayout, wLayout.getMaxVertexRadius() / 2,
                    0, 0, 600, 800, 300, 400);
            if (wDrawing.nbVertexCrossings(forest.getEdges()) == 0 &&
                    wDrawing.nbEdgeIntersections() == 0) {
                i++;
            }
        }
        Log.i("TestWebsight", "TestForest : testMinimizeCrossings : " + i + "/10");
    }
}
