import java.util.List;
import java.util.Set;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestGraph {

	@BeforeClass
	public static void beforeTests() {
		System.out.println("\n------------------------------");
		System.out.println("Testing class Graph");
		System.out.println("------------------------------");
	}

	@AfterClass
	public static void afterTests() {
		System.out.println("\n------------------------------\n");
	}

	@Test 
	public void testGraph() {
		// Prepare the json
		String surls = ("\"urls\":[\"url0\", \"url1\", \"url2\", " +
			"\"url3\", \"url4\", \"url5\", \"url6\", \"url7\", " +
			"\"url8\", \"url9\", \"url10\", \"url11\"]");
		String sv0 = "{\"kw\":\"keyword0\", \"url_ids\":[0, 1]}";
		String sv1 = "{\"kw\":\"keyword1\", \"url_ids\":[2]}";
		String sv2 = "{\"kw\":\"keyword2\", \"url_ids\":[3]}";
		String sv3 = "{\"kw\":\"keyword3\", \"url_ids\":[4]}";
		String sv4 = "{\"kw\":\"keyword4\", \"url_ids\":[]}";
		String sv5 = "{\"kw\":\"keyword5\", \"url_ids\":[]}";
		String sv6 = "{\"kw\":\"keyword6\", \"url_ids\":[11]}";

		String svertices = ("\"vertices\":["+ sv0 + "," + sv1 + "," + 
			sv2 + "," + sv3 + "," + sv4 + "," + sv5 + "," + sv6 + "]");
		String se0 = "{\"src\":0, \"dst\":1, \"url_ids\":[5,6]}";
		String se1 = "{\"src\":1, \"dst\":2, \"url_ids\":[7]}";
		String se2 = "{\"src\":1, \"dst\":3, \"url_ids\":[8]}";
		String se3 = "{\"src\":3, \"dst\":0, \"url_ids\":[9]}";
		String se4 = "{\"src\":4, \"dst\":5, \"url_ids\":[10]}";
		String sedges = ("\"edges\":[" + se0 + "," + se1 + "," + se2 + 
			"," + se3 + "," + se4 + "]");
		String sadvice = ("\"advice\":[]");
		String sgraph = "{"+surls+","+sadvice+","+svertices+","+sedges+"}";
		// Builds the graph
		Graph graph = null;
		try {
			graph = new Graph(sgraph);
		} catch (Exception e) {
			fail();
		}
		// Checks if it is valid
		List<Vertex> vertices = graph.getVertices();
		assertEquals(7, vertices.size());
		Vertex v0 = vertices.get(0);
		Vertex v1 = vertices.get(1);
		Vertex v2 = vertices.get(2);
		Vertex v3 = vertices.get(3);
		Vertex v4 = vertices.get(4);
		Vertex v5 = vertices.get(5);
		Vertex v6 = vertices.get(6);
		List<Edge> edges = graph.getEdges();
		Edge e0 = edges.get(0);
		Edge e1 = edges.get(1);
		Edge e2 = edges.get(2);
		Edge e3 = edges.get(3);
		Edge e4 = edges.get(4);
		// v0
		assertEquals(0, v0.getId());
		assertEquals("keyword0", v0.getLabel());
		Set<String> urlsV0 = v0.getData().getURLs();
		assertEquals(5, urlsV0.size());
		assertTrue(urlsV0.contains("url0"));
		assertTrue(urlsV0.contains("url1"));
		assertTrue(urlsV0.contains("url5"));
		assertTrue(urlsV0.contains("url6"));
		assertTrue(urlsV0.contains("url9"));
		Set<Vertex> neighboursV0 = v0.getNeighbours(); 
		assertEquals(2, neighboursV0.size());
		assertTrue(neighboursV0.contains(v1));
		assertTrue(neighboursV0.contains(v3));
		assertEquals(e0, v0.getEdgeTo(v1));
		assertEquals(e3, v0.getEdgeTo(v3));
		List<Vertex> childrenV0 = v0.getChildren();
		assertEquals(2, childrenV0.size());
		assertTrue(childrenV0.contains(v1));
		assertTrue(childrenV0.contains(v3));
		assertEquals(2, v0.getSuborder());
		assertEquals(0, v0.getDepth());
		// v1
		assertEquals(1, v1.getId());
		assertEquals("keyword1", v1.getLabel());
		Set<String> urlsV1 = v1.getData().getURLs();
		assertEquals(5, urlsV1.size());
		assertTrue(urlsV1.contains("url2"));
		assertTrue(urlsV1.contains("url5"));
		assertTrue(urlsV1.contains("url6"));
		assertTrue(urlsV1.contains("url7"));
		assertTrue(urlsV1.contains("url8"));
		Set<Vertex> neighboursV1 = v1.getNeighbours(); 
		assertEquals(3, neighboursV1.size());
		assertTrue(neighboursV1.contains(v0));
		assertTrue(neighboursV1.contains(v2));
		assertTrue(neighboursV1.contains(v3));
		assertEquals(e0, v1.getEdgeTo(v0));
		assertEquals(e1, v1.getEdgeTo(v2));
		assertEquals(e2, v1.getEdgeTo(v3));
		List<Vertex> childrenV1 = v1.getChildren();
		assertEquals(1, childrenV1.size());
		assertTrue(childrenV1.contains(v2));
		assertEquals(1, v1.getSuborder());
		assertEquals(1, v1.getDepth());
		// v2
		assertEquals(2, v2.getId());
		assertEquals("keyword2", v2.getLabel());
		Set<String> urlsV2 = v2.getData().getURLs();
		assertEquals(2, urlsV2.size());
		assertTrue(urlsV2.contains("url3"));
		assertTrue(urlsV2.contains("url7"));
		Set<Vertex> neighboursV2 = v2.getNeighbours(); 
		assertEquals(1, neighboursV2.size());
		assertTrue(neighboursV2.contains(v1));
		assertEquals(e1, v2.getEdgeTo(v1));
		List<Vertex> childrenV2 = v2.getChildren();
		assertEquals(0, childrenV2.size());
		assertEquals(1, v2.getSuborder());
		assertEquals(2, v2.getDepth());
		// v3
		assertEquals(3, v3.getId());
		assertEquals("keyword3", v3.getLabel());
		Set<String> urlsV3 = v3.getData().getURLs();
		assertEquals(3, urlsV3.size());
		assertTrue(urlsV3.contains("url4"));
		assertTrue(urlsV3.contains("url8"));
		assertTrue(urlsV3.contains("url9"));
		Set<Vertex> neighboursV3 = v3.getNeighbours(); 
		assertEquals(2, neighboursV3.size());
		assertTrue(neighboursV3.contains(v0));
		assertTrue(neighboursV3.contains(v1));
		assertEquals(e3, v3.getEdgeTo(v0));
		assertEquals(e2, v3.getEdgeTo(v1));
		List<Vertex> childrenV3 = v3.getChildren();
		assertEquals(0, childrenV3.size());
		assertEquals(1, v3.getSuborder());
		assertEquals(1, v3.getDepth());
		// v4
		assertEquals(4, v4.getId());
		assertEquals("keyword4", v4.getLabel());
		Set<String> urlsV4 = v4.getData().getURLs();
		assertEquals(1, urlsV4.size());
		assertTrue(urlsV4.contains("url10"));
		Set<Vertex> neighboursV4 = v4.getNeighbours(); 
		assertEquals(1, neighboursV4.size());
		assertTrue(neighboursV4.contains(v5));
		assertEquals(e4, v4.getEdgeTo(v5));
		List<Vertex> childrenV4 = v4.getChildren();
		assertEquals(1, childrenV4.size());
		assertTrue(childrenV4.contains(v5));
		assertEquals(1, v4.getSuborder());
		assertEquals(3, v4.getDepth());
		// v5
		assertEquals(5, v5.getId());
		assertEquals("keyword5", v5.getLabel());
		Set<String> urlsV5 = v5.getData().getURLs();
		assertEquals(1, urlsV5.size());
		assertTrue(urlsV5.contains("url10"));
		Set<Vertex> neighboursV5 = v5.getNeighbours(); 
		assertEquals(1, neighboursV5.size());
		assertTrue(neighboursV5.contains(v4));
		assertEquals(e4, v5.getEdgeTo(v4));
		List<Vertex> childrenV5 = v5.getChildren();
		assertEquals(0, childrenV5.size());
		assertEquals(1, v5.getSuborder());
		assertEquals(4, v5.getDepth());
		// v6
		assertEquals(6, v6.getId());
		assertEquals("keyword6", v6.getLabel());
		Set<String> urlsV6 = v6.getData().getURLs();
		assertEquals(1, urlsV6.size());
		assertTrue(urlsV6.contains("url11"));
		Set<Vertex> neighboursV6 = v6.getNeighbours(); 
		assertEquals(0, neighboursV6.size());
		List<Vertex> childrenV6 = v6.getChildren();
		assertEquals(0, childrenV6.size());
		assertEquals(1, v6.getSuborder());
		assertEquals(3, v6.getDepth());
		// e0
		assertEquals(0, e0.getId());
		assertEquals(v0, e0.getEnd1());
		assertEquals(v1, e0.getEnd2());
		Set<String> urlsE0 = e0.getData().getURLs();
		assertEquals(2, urlsE0.size());
		assertTrue(urlsE0.contains("url5"));
		assertTrue(urlsE0.contains("url6"));
		// e1
		assertEquals(1, e1.getId());
		assertEquals(v1, e1.getEnd1());
		assertEquals(v2, e1.getEnd2());
		Set<String> urlsE1 = e1.getData().getURLs();
		assertEquals(1, urlsE1.size());
		assertTrue(urlsE1.contains("url7"));
		// e2
		assertEquals(2, e2.getId());
		assertEquals(v1, e2.getEnd1());
		assertEquals(v3, e2.getEnd2());
		Set<String> urlsE2 = e2.getData().getURLs();
		assertEquals(1, urlsE2.size());
		assertTrue(urlsE2.contains("url8"));
		// e3
		assertEquals(3, e3.getId());
		assertEquals(v0, e3.getEnd1());
		assertEquals(v3, e3.getEnd2());
		Set<String> urlsE3 = e3.getData().getURLs();
		assertEquals(1, urlsE3.size());
		assertTrue(urlsE3.contains("url9"));
		// e4
		assertEquals(4, e4.getId());
		assertEquals(v4, e4.getEnd1());
		assertEquals(v5, e4.getEnd2());
		Set<String> urlsE4 = e4.getData().getURLs();
		assertEquals(1, urlsE4.size());
		assertTrue(urlsE4.contains("url10"));
		// Check roots
		List<Vertex> roots = graph.getRoots();
		assertEquals(3, roots.size());
		assertEquals(v0, roots.get(0));
		assertEquals(v4, roots.get(1));
		assertEquals(v6, roots.get(2));
		// Check extra edges
		Set<Edge> extraEdges = graph.getExtraEdges();
		assertEquals(1, extraEdges.size());
		assertTrue(extraEdges.contains(e2));
		
	}
}