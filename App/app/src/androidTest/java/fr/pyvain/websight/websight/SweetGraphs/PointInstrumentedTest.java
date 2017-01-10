package fr.pyvain.websight.websight.SweetGraphs;

import android.os.Parcel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class PointInstrumentedTest {
    @Test
    public void testParcelable(){
        Point pointIn = new Point(3.1415f, 2.7183f);

        Parcel p = Parcel.obtain();
        pointIn.writeToParcel(p, 0);
        p.setDataPosition(0);
        Point pointOut = Point.CREATOR.createFromParcel(p);
        assertEquals(pointIn, pointOut);
    }
}
