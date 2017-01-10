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
public class VertexInstrumentedTest {
    @Test
    public void testParcelable(){
        Vertex vertexIn = new Vertex(0, "kwd0");
        Set<String> urls = new HashSet<>();
        urls.add("url1");
        urls.add("url2");
        urls.add("url3");
        vertexIn.getData().addURLs(urls);

        Parcel p = Parcel.obtain();
        vertexIn.writeToParcel(p, 0);
        p.setDataPosition(0);
        Vertex vertexOut = Vertex.CREATOR.createFromParcel(p);
        assertEquals(vertexIn, vertexOut);
    }
}
