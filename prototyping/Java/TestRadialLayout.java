import java.util.List;
import java.lang.Math;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestRadialLayout {

	@BeforeClass
	public static void beforeTests() {
		System.out.println("\n------------------------------");
		System.out.println("Testing class RadialLayout");
		System.out.println("------------------------------");
	}

	@AfterClass
	public static void afterTests() {
		System.out.println("\n------------------------------\n");
	}

	@Test 
	public void testRadialLayout() {
		// Builds a graph
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
		// Builds the layout
		RadialLayout layout = new RadialLayout(graph, 0.5f);
		Float EPS = 0.01f;
		assertSame(graph, layout.getGraph());
		List<Vertex> vertices = graph.getVertices();
		// Checks angular positions
		List<Float> thetas = layout.getAngularPositions();
		assertEquals(7, thetas.size());
		assertEquals(thetas.get(0),Math.PI,EPS);
		if (vertices.get(0).getChildren().get(0).getId() == 1) {
			assertEquals(Math.PI/3, thetas.get(1), EPS);
			assertEquals(Math.PI/3, thetas.get(2), EPS);
			assertEquals(Math.PI, thetas.get(3), EPS);
		} else {
			assertEquals(Math.PI, thetas.get(1), EPS);
			assertEquals(Math.PI, thetas.get(2), EPS);
			assertEquals(Math.PI/3, thetas.get(3), EPS);
		}
		assertEquals(Math.PI/3, thetas.get(4), EPS);
		assertEquals(Math.PI/3, thetas.get(5), EPS);
		assertEquals(Math.PI, thetas.get(6), EPS);
		// Checks depths
		List<Float> depths = layout.getDepths();
		assertEquals(7, depths.size());
		assertEquals(0, depths.get(0), EPS);
		assertEquals(1, depths.get(1), EPS);
		assertEquals(2, depths.get(2), EPS);
		assertEquals(1, depths.get(3), EPS);
		assertEquals(3, depths.get(4), EPS);
		assertEquals(4, depths.get(5), EPS);
		assertEquals(3, depths.get(6), EPS);
		// Check vertex radius
		assertEquals(0.25f ,layout.getVertexRadius(), EPS);
		// Checks vertex centers coordinates
		List<Point> verticesCoords = layout.getCentralPoints();
		assertEquals(7, verticesCoords.size());
		assertEquals(new Point(0,0), verticesCoords.get(0));
		if (vertices.get(0).getChildren().get(0).getId() == 1) {
			assertEquals(new Point(111, 192), verticesCoords.get(1));
			assertEquals(new Point(222, 385), verticesCoords.get(2));
			assertEquals(new Point(-222, 0), verticesCoords.get(3));
		} else {
			assertEquals(new Point(-222, 0), verticesCoords.get(1));
			assertEquals(new Point(-444, 0), verticesCoords.get(2));
			assertEquals(new Point(111, 192), verticesCoords.get(3));
		}
		assertEquals(new Point(333, 577), verticesCoords.get(4));
		assertEquals(new Point(444, 770), verticesCoords.get(5));
		assertEquals(new Point(-667, 0), verticesCoords.get(6));
		// Checks edge segments coordinates
		List<Segment> edgesCoords = layout.getSegments();
		assertEquals(5, edgesCoords.size());
		if (vertices.get(0).getChildren().get(0).getId() == 1) {
			//
		} else {
			// v0 v1
			assertEquals(new Point(-166, 0), edgesCoords.get(0).getP1());
			assertEquals(new Point(-56, 0), edgesCoords.get(0).getP2());
			// v1 v2
			assertEquals(new Point(-388, 0), edgesCoords.get(1).getP1());	
			assertEquals(new Point(-278, 0), edgesCoords.get(1).getP2());
			// v1 v3
			assertEquals(new Point(-174, 28), edgesCoords.get(2).getP1());
			assertEquals(new Point(63, 164), edgesCoords.get(2).getP2());
			// v0 v3
			assertEquals(new Point(28, 48), edgesCoords.get(3).getP1());
			assertEquals(new Point(83, 144), edgesCoords.get(3).getP2());
		}
		// v4 v5
		assertEquals(new Point(361, 625), edgesCoords.get(4).getP1());
		assertEquals(new Point(416, 722), edgesCoords.get(4).getP2());
		// Checks intersections
		// assertEquals(0, layout.nbEdgeCrossings());
		// assertEquals(0, layout.nbVertexCrossings());
	}

	@Test
	public void testRadialLayoutCrossings() {
		// Builds a graph
		String surls = ("\"urls\":[]");
		String svertices = ("\"vertices\":[" +
			"{\"kw\":\"0\", \"url_ids\":[]}," +
			"{\"kw\":\"1\", \"url_ids\":[]}," +
			"{\"kw\":\"2\", \"url_ids\":[]}," +
			"{\"kw\":\"3\", \"url_ids\":[]}," +
			"{\"kw\":\"4\", \"url_ids\":[]}," +
			"{\"kw\":\"5\", \"url_ids\":[]}]");
		String sedges = ("\"edges\":[" + 
			"{\"src\":0, \"dst\":1, \"url_ids\":[]}," +
			"{\"src\":0, \"dst\":2, \"url_ids\":[]}," +
			"{\"src\":0, \"dst\":3, \"url_ids\":[]}," +
			"{\"src\":0, \"dst\":4, \"url_ids\":[]}," +
			"{\"src\":0, \"dst\":5, \"url_ids\":[]}," +
			"{\"src\":1, \"dst\":2, \"url_ids\":[]}," +
			"{\"src\":1, \"dst\":3, \"url_ids\":[]}," +
			"{\"src\":1, \"dst\":4, \"url_ids\":[]}," +
			"{\"src\":1, \"dst\":5, \"url_ids\":[]}]");
		String sadvice = ("\"advice\":[]");
		String sgraph = "{"+surls+","+sadvice+","+svertices+","+sedges+"}";
		// Builds the graph
		Graph graph = null;
		try {
			graph = new Graph(sgraph);
		} catch (Exception e) {
			fail();
		}
		// Builds the layout
		RadialLayout layout = new RadialLayout(graph, 0.2f);
	}
}