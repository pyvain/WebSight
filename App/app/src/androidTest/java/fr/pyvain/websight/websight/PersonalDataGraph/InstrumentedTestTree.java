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
public class InstrumentedTestTree {

    @BeforeClass
    public static void beforeTests() {
        System.out.println("Testing class Tree\n");
    }

    @Test
    public void testParcelable(){
        for (int i = 0; i < 10; i++) {
            InputGraph graph = InputGraph.randomInputGraph(10, 10);
            Tree treeIn = new Tree(graph.getVertices().first());

            Parcel p = Parcel.obtain();
            treeIn.writeToParcel(p, 0);
            p.setDataPosition(0);
            Tree treeOut = Tree.CREATOR.createFromParcel(p);
            assertEquals(treeIn, treeOut);
        }
    }

    @Test
    public void testMinimizeCrossings() {
        List<Vertex> vertices = new ArrayList<>();
        Vertex[] v = new Vertex[7];
        for (int i = 0; i < 7; i++) {
            Vertex vertex = new Vertex(i, "keyword" + i);
            vertices.add(vertex);
            v[i] = vertex;
        }

        List<Edge> edges = new ArrayList<>();
        Vertex[] ends1 = new Vertex[]{v[0], v[1], v[1], v[0], v[4], v[4], v[2], v[3]};
        Vertex[] ends2 = new Vertex[]{v[1], v[2], v[3], v[4], v[5], v[6], v[5], v[6]};
        for (int i = 0; i < 8; i++) {
            Edge e = new Edge(ends1[i], ends2[i]);
            edges.add(e);
        }

        try {
            new InputGraph(vertices, edges);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        int i = 0;
        for (int j = 0; j < 10; j++) {
            Tree tree = new Tree(v[0]);
            Tree wTree = new Tree(tree);
            RadialLayout wLayout = new RadialLayout(wTree, 0, 0f, (float) (2 * Math.PI));
            Drawing wDrawing = new Drawing(wTree, wLayout, wLayout.getMaxVertexRadius() / 2,
                    0, 0, 1000, 1000, 500, 500);
            assertEquals(2, wDrawing.nbVertexCrossings(wTree.getExtraEdges()));
            assertEquals(1, wDrawing.nbEdgeIntersections());
            tree.minimizeCrossings(0, 0f, (float) (2 * Math.PI), 0.5f, wTree, wLayout, wDrawing, 200);
            wLayout.update(tree, 0, 0f, (float) (2 * Math.PI));
            wDrawing.update(tree, wLayout, (float) 0.5 * wLayout.getMaxVertexRadius(),
                    0, 0, 600, 800, 300, 400);
            if (wDrawing.nbVertexCrossings(wTree.getExtraEdges()) == 0 &&
                    wDrawing.nbEdgeIntersections() == 0) {
                i++;
            }
        }
        Log.i("TestWebsight", "TestTree : testMinimizeCrossings : " + i + "/10");
    }
}
