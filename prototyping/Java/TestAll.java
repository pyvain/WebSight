import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  TestPoint.class,
  TestSegment.class,
  TestEvent.class,
  TestIntersection.class,
  TestEventQueue.class,
  TestIntersectionSet.class,
  TestInterComputer.class,
  TestDataSet.class,
  TestEdge.class,
  TestVertex.class,
  TestGraph.class,
  TestRadialLayout.class,
  TestLayoutGenerator.class
})

public class TestAll {
}