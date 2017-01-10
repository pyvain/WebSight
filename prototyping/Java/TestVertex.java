import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestVertex {

	@BeforeClass
	public static void beforeTests() {
		System.out.println("\n------------------------------");
		System.out.println("Testing class Vertex");
		System.out.println("------------------------------");
	}

	@AfterClass
	public static void afterTests() {
		System.out.println("\n------------------------------\n");
	}

	@Test 
	public void testVertex() {
		Vertex v = new Vertex(0, "kwd");
		assertEquals(0, v.getId());
		assertEquals("kwd", v.getLabel());
		assertEquals(0, v.getData().getURLs().size());
		assertEquals(0, v.getNeighbours().size());
		assertEquals(null, v.getEdgeTo(v));
		assertEquals(0, v.getChildren().size());
		assertEquals(1, v.getSuborder());
		assertEquals(0, v.getDepth());
	}

	@Test
	public void testAddNeighbourIfAbsent() {
		Vertex v0 = new Vertex(0, "kwd0");
		Vertex v1 = new Vertex(1, "kwd1");
		Vertex v2 = new Vertex(2, "kwd2");
		// First absent add
		Edge e01 = new Edge(0, v0, v1);
		v0.addNeighbourIfAbsent(v1, e01);
		Set<Vertex> neighbours = v0.getNeighbours();
		assertEquals(1, neighbours.size());
		assertTrue(neighbours.contains(v1));
		assertSame(e01, v0.getEdgeTo(v1));
		// Add already present
		v0.addNeighbourIfAbsent(v1, e01);
		neighbours = v0.getNeighbours();
		assertEquals(1, neighbours.size());
		assertTrue(neighbours.contains(v1));
		assertSame(e01, v0.getEdgeTo(v1));
		// Second absent add
		Edge e02 = new Edge(1, v0, v2);
		v0.addNeighbourIfAbsent(v2, e02);
		neighbours = v0.getNeighbours();
		assertEquals(2, neighbours.size());
		assertTrue(neighbours.contains(v1));
		assertSame(e01, v0.getEdgeTo(v1));
		assertTrue(neighbours.contains(v2));
		assertSame(e02, v0.getEdgeTo(v2));
	}

	@Test
	public void testAddChild() {
		Vertex v0 = new Vertex(0, "kwd0");
		Vertex v1 = new Vertex(1, "kwd1");
		Vertex v2 = new Vertex(2, "kwd2");
		// First child
		v0.addChild(v1);
		List<Vertex> children = v0.getChildren();
		assertEquals(1, children.size());
		assertSame(v1, children.get(0));
		// Second child
		v0.addChild(v2);
		children = v0.getChildren();
		assertEquals(2, children.size());
		assertSame(v1, children.get(0));
		assertSame(v2, children.get(1));
	}

	@Test
	public void testClearChildren() {
		Vertex v0 = new Vertex(0, "kwd0");
		Vertex v1 = new Vertex(1, "kwd1");
		Vertex v2 = new Vertex(2, "kwd2");
		v0.addChild(v1);
		v0.addChild(v2);
		v0.clearChildren();
		List<Vertex> children = v0.getChildren();
		assertEquals(0, children.size());
	}

	@Test
	public void testShuffleChildren() {
		Vertex v0 = new Vertex(0, "kwd0");
		Vertex v1 = new Vertex(1, "kwd1");
		Vertex v2 = new Vertex(2, "kwd2");
		Vertex v3 = new Vertex(3, "kwd3");
		v0.addChild(v1);
		v0.addChild(v2);
		v0.addChild(v3);
		List<Vertex> prev = new ArrayList<Vertex>(v0.getChildren());
		v0.shuffleChildren();
		for (Vertex v : prev) {
			assertTrue(v0.getChildren().contains(v));
		}
		v0.unshuffleChildren();
		for (int i = 0; i < prev.size(); i++) {
			assertSame(prev.get(i), v0.getChildren().get(i));
		}
	}

	@Test
	public void testUpdateDepthAndSuborder() {
		Vertex v0 = new Vertex(0, "kwd0");
		Vertex v1 = new Vertex(1, "kwd1");
		Vertex v2 = new Vertex(2, "kwd2");
		Vertex v3 = new Vertex(2, "kwd3");
		v0.addChild(v1);
		v1.addChild(v2);
		v1.addChild(v3);
		assertEquals(4, v0.updateDepthAndSuborder(2));
		assertEquals(2, v0.getSuborder());
		assertEquals(2, v1.getSuborder());
		assertEquals(1, v2.getSuborder());
		assertEquals(1, v3.getSuborder());
		assertEquals(2, v0.getDepth());
		assertEquals(3, v1.getDepth());
		assertEquals(4, v2.getDepth());
		assertEquals(4, v3.getDepth());
	}

}