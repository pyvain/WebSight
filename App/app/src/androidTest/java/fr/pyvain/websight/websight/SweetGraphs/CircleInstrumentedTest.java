package fr.pyvain.websight.websight.SweetGraphs;

import android.os.Parcel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class CircleInstrumentedTest {
    @Test
    public void testParcelable(){
        Circle circleIn = new Circle(new Point(3.1415f, 2.7183f), 1.2345f);
        Parcel p = Parcel.obtain();
        circleIn.writeToParcel(p, 0);
        p.setDataPosition(0);
        Circle circleOut = Circle.CREATOR.createFromParcel(p);
        assertEquals(circleIn, circleOut);
    }
}
