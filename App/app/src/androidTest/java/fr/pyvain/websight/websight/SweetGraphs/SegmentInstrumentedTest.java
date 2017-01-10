package fr.pyvain.websight.websight.SweetGraphs;

import android.os.Parcel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class SegmentInstrumentedTest {
    @Test
    public void testParcelable(){
        Segment segmentIn = new Segment(new Point(3.1415f, 2.7183f),
                new Point(1.4142f, 1.6180f), 1.7321f);
        Parcel p = Parcel.obtain();
        segmentIn.writeToParcel(p, 0);
        p.setDataPosition(0);
        Segment segmentOut = Segment.CREATOR.createFromParcel(p);
        assertEquals(segmentIn, segmentOut);
    }
}
