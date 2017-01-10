import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestEdge {

	@BeforeClass
	public static void beforeTests() {
		System.out.println("\n------------------------------");
		System.out.println("Testing class Edge");
		System.out.println("------------------------------");
	}

	@AfterClass
	public static void afterTests() {
		System.out.println("\n------------------------------\n");
	}

	@Test 
	public void testEdge() {
		Vertex v0 = new Vertex(0, "kwd0");
		Vertex v1 = new Vertex(1, "kwd1");
		Edge e = new Edge(0, v0, v1);
		assertEquals(0, e.getId());
		assertSame(v0, e.getEnd1());
		assertSame(v1, e.getEnd2());
		assertEquals(0, e.getData().getURLs().size());
		e = new Edge(0, v1, v0);
		assertEquals(0, e.getId());
		assertSame(v0, e.getEnd1());
		assertSame(v1, e.getEnd2());
		assertEquals(0, e.getData().getURLs().size());
	}

	@Test 
	public void testEquals() {
		// Equal edges
		Vertex v0 = new Vertex(0, "kwd0");
		Vertex v1 = new Vertex(1, "kwd1");
		Edge e0 = new Edge(0, v0, v1);
		Edge e1 = new Edge(1, v0, v1);
		Edge e2 = new Edge(2, v1, v0);
		assertEquals(e0, e0);
		assertEquals(e0, e1);
		assertEquals(e0, e2);
		assertEquals(e2, e0);
		// Not equal edges
		Vertex v2 = new Vertex(2, "kwd2");
		Edge e3 = new Edge(3, v0, v2);
		assertNotEquals(e0, e3);
		assertNotEquals(e3, e0);
	}

	@Test 
	public void testHashCode() {
		// Equal edges
		Vertex v0 = new Vertex(0, "kwd0");
		Vertex v1 = new Vertex(1, "kwd1");
		Edge e0 = new Edge(0, v0, v1);
		Edge e1 = new Edge(1, v0, v1);
		Edge e2 = new Edge(2, v1, v0);
		assertEquals(e0.hashCode(), e0.hashCode());
		assertEquals(e0.hashCode(), e1.hashCode());
		assertEquals(e0.hashCode(), e2.hashCode());
	}
}