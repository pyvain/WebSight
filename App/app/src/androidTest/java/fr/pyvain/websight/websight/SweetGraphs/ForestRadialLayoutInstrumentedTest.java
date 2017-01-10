package fr.pyvain.websight.websight.SweetGraphs;

import android.os.Parcel;
import android.util.Log;

import org.junit.Test;

import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class ForestRadialLayoutInstrumentedTest {
    @Test
    public void testParcelable(){
        Graph g = Graph.randomInputGraph(10, 10);
        ForestRadialLayout lIn = new ForestRadialLayout(g, g.getVertices().first());

        Parcel p = Parcel.obtain();
        lIn.writeToParcel(p, 0);
        p.setDataPosition(0);
        ForestRadialLayout lOut = ForestRadialLayout.CREATOR.createFromParcel(p);

        Rectangle projSpace = new Rectangle(new Point(200, 400), 400, 800);
        Map<Integer, Circle> vIn = lIn.projectedVertices(projSpace);
        Map<Integer, Circle> vOut = lOut.projectedVertices(projSpace);
        for (Vertex v : g.getVertices()) {
            Log.d("lowl", String.format(Locale.US, "%f %f %f", vIn.get(v.getId()).getCenter().getX(), vIn.get(v.getId()).getCenter().getY(), vIn.get(v.getId()).getRadius()));
            Log.d("lowl", String.format(Locale.US, "%f %f %f", vOut.get(v.getId()).getCenter().getX(), vOut.get(v.getId()).getCenter().getY(), vOut.get(v.getId()).getRadius()));
        }
        assertEquals(lIn.projectedEdges(projSpace), lOut.projectedEdges(projSpace));
        assertEquals(lIn.projectedOrbits(projSpace), lOut.projectedOrbits(projSpace));
    }
}
