package fr.pyvain.websight.websight.PersonalDataGraph;


import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.pyvain.websight.websight.Geometry.InterComputer;
import fr.pyvain.websight.websight.Geometry.Segment;

public class CompareIntersections {

    @BeforeClass
    public static void beforeTests() {
        System.out.println("Comparing the 2 intersection methods\n");
    }

    /**
     * Computes the intersections in a specified number of random graph layouts
     * of specified size, with the naive method and the BO method, and
     * print the average time needed.
     *
     * @param size Number of segments in the layout generated
     * @param nb Number of layout to generate
     * @param iters Number of computations for each layout
     */
    private void comparisonBatch(int size, int nb, int iters) {
        System.out.println("\nIntersections in a set of " + size +
                " edges between 20 vertices");
        System.out.println("Naive(us)  BentleyOttman(us)");
        List<Collection<Segment>> segments = new ArrayList<>();
        for (int i = 0; i < nb; i++) {
            InputGraph g = InputGraph.randomInputGraph(20, size);
            Forest f = new Forest(g, g.getVertices().first());
            RadialLayout l = new RadialLayout(f);
            Drawing d = new Drawing(f, l, l.getMaxVertexRadius()/2,
                    Drawing.CMIN, Drawing.CMIN, Drawing.CMAX, Drawing.CMAX,
                    Drawing.C0, Drawing.C0);
            segments.add(d.getSegments().values());
        }
        long start1, start2, stop1, stop2;
        start1 = System.nanoTime();
        for (Collection<Segment> seg : segments) {
            for (int i = 0; i < iters; i++) {
                InterComputer.edgeIntersectionsNaive(seg);
            }
        }
        stop1 = System.nanoTime();
        start2 = System.nanoTime();
        for (Collection<Segment> seg : segments) {
            for (int i = 0; i < iters; i++) {
                InterComputer.edgeIntersectionsBO(seg);
            }
        }
        stop2 = System.nanoTime();
        System.out.print((stop1-start1)/(1000*nb*iters) + "          ");
        System.out.println((stop2-start2)/(1000*nb*iters));
    }

    @Test
    public void compareIntersections() {
        // Found out a second set of measures is usually a lot
        // less disturbed by other processes.
        for (int i = 10; i <= 190; i += 10) {
            comparisonBatch(i, 20, 10);
        }
        for (int i = 10; i <= 190; i += 10) {
            comparisonBatch(i, 20, 10);
        }
    }

}