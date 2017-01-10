import java.util.Set;
import java.util.HashSet;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestDataSet {

	@BeforeClass
	public static void beforeTests() {
		System.out.println("\n------------------------------");
		System.out.println("Testing class DataSet");
		System.out.println("------------------------------");
	}

	@AfterClass
	public static void afterTests() {
		System.out.println("\n------------------------------\n");
	}

	@Test
	public void testDataSet() {
		DataSet data = new DataSet();
		assertEquals(0, data.getURLs().size());
	}

	@Test
	public void testAddURLs() {
		DataSet data = new DataSet();
		Set<String> urls = new HashSet<String>();
		urls.add("url1");
		urls.add("url2");
		urls.add("url3");
		data.addURLs(urls);
		urls = data.getURLs();
		assertEquals(3, urls.size());
		assertTrue(urls.contains("url1"));
		assertTrue(urls.contains("url2"));
		assertTrue(urls.contains("url3"));
	}
}