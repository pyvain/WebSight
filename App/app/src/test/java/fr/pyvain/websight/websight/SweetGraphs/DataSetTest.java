package fr.pyvain.websight.websight.SweetGraphs;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public class DataSetTest {

    private DataSet d;
    private Set<String> urls1;
    private Set<String> urls2;

    @Before
    public void setUp() throws Exception {
        d = new DataSet();
        urls1 = new HashSet<>(Arrays.asList("url1", "url2", "url3"));
        urls2 = new HashSet<>(Arrays.asList("url1", "url4", "url5"));
    }

    @Test
    public void testGetURLs() throws Exception {
        assertEquals(new HashSet<String>(), d.getURLs());
    }

    @Test
    public void testAddURLs() throws Exception {
        d.addURLs(urls1);
        Set<String> expected = new HashSet<>(urls1);
        assertEquals(expected, d.getURLs());
        d.addURLs(urls2);
        expected.addAll(urls2);
        assertEquals(expected, d.getURLs());
    }

    @Test
    public void testEquals() throws Exception {
        DataSet d1 = new DataSet();
        assertEquals(d, d1);
        assertEquals(d1, d);
        d.addURLs(urls1);
        assertNotEquals(d, d1);
        assertNotEquals(d1, d);
        d1.addURLs(urls1);
        assertEquals(d, d1);
        assertEquals(d1, d);
        DataSet d2 = new DataSet();
        d2.addURLs(urls2);
        assertNotEquals(d, d2);
        assertNotEquals(d2, d);
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(d.hashCode(), d.hashCode());
        d.addURLs(urls1);
        assertEquals(d.hashCode(), d.hashCode());
        DataSet d1 = new DataSet();
        d1.addURLs(urls1);
        assertEquals(d.hashCode(), d1.hashCode());
    }
}