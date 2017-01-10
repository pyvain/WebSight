package fr.pyvain.websight.websight.PersonalDataGraph;


import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * <p>
 *     @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedTestPolarCoords {

    @BeforeClass
    public static void beforeTests() {
        System.out.println("Testing class Graph\n");
    }

    private static final float EPS = 0.01f;

    @Test
    public void testParcelable() {
        Random rand = new Random();
        int r = rand.nextInt();
        float angle = rand.nextFloat();
        PolarCoords coordsIn = new PolarCoords(r, angle);
        Parcel p = Parcel.obtain();
        coordsIn.writeToParcel(p, 0);
        p.setDataPosition(0);
        PolarCoords coordsOut = PolarCoords.CREATOR.createFromParcel(p);
        assertEquals(coordsIn.getRadius(), coordsOut.getRadius());
        assertEquals(coordsIn.getAngle(), coordsOut.getAngle(), EPS);
    }
}
