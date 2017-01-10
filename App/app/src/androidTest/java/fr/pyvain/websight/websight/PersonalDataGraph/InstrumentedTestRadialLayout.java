package fr.pyvain.websight.websight.PersonalDataGraph;


import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * <p>
 *     @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedTestRadialLayout {

    @BeforeClass
    public static void beforeTests() {
        System.out.println("Testing class RadialLayout\n");
    }

    @Test
    public void testParcelable() {
        InputGraph g = InputGraph.randomInputGraph(10, 10);
        Forest f = new Forest(g, g.getVertices().first());
        RadialLayout lIn = new RadialLayout(f);

        Parcel p = Parcel.obtain();
        lIn.writeToParcel(p, 0);
        p.setDataPosition(0);
        RadialLayout lOut = RadialLayout.CREATOR.createFromParcel(p);
        assertEquals(lIn, lOut);
    }
}
