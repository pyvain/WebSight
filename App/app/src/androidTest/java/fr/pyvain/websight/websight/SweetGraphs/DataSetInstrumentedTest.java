package fr.pyvain.websight.websight.SweetGraphs;

import android.os.Parcel;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class DataSetInstrumentedTest {

    @Test
    public void testParcelable(){
        DataSet dataIn = new DataSet();
        Set<String> urls = new HashSet<>();
        urls.add("url1");
        urls.add("url2");
        urls.add("url3");
        dataIn.addURLs(urls);

        Parcel p = Parcel.obtain();
        dataIn.writeToParcel(p, 0);
        p.setDataPosition(0);
        DataSet dataOut = DataSet.CREATOR.createFromParcel(p);
        assertEquals(dataIn, dataOut);
    }
}
