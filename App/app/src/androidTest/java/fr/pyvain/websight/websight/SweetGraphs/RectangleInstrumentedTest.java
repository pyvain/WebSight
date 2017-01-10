package fr.pyvain.websight.websight.SweetGraphs;

import android.os.Parcel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class RectangleInstrumentedTest {
    @Test
    public void testParcelable(){
        Rectangle rectangleIn = new Rectangle(new Point(3.1415f, 2.7183f),
                1.2345f, 6.7890f);
        Parcel p = Parcel.obtain();
        rectangleIn.writeToParcel(p, 0);
        p.setDataPosition(0);
        Rectangle rectangleOut = Rectangle.CREATOR.createFromParcel(p);
        assertEquals(rectangleIn, rectangleOut);
    }
}
