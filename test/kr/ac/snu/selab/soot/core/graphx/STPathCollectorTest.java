package kr.ac.snu.selab.soot.core.graphx;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;

import kr.ac.snu.selab.soot.graphx.Path;
import kr.ac.snu.selab.soot.graphx.STPathCollector;

import org.junit.Before;
import org.junit.Test;

public class STPathCollectorTest extends AbstractGraphTest {
	@Before
	public void setUp() {
		super.setUp();
	}

	@Test
	public void test1() {
		STPathCollector<StringNode> collector = new STPathCollector<StringNode>(
				new StringNode("c"), new StringNode("d"), graph);
		ArrayList<Path<StringNode>> paths = collector.run();
		assertEquals(1, paths.size());

		assertEquals("c;d;", pathString(paths.get(0)));
	}

	@Test
	public void test2() {
		STPathCollector<StringNode> collector = new STPathCollector<StringNode>(
				new StringNode("b"), new StringNode("d"), graph);
		ArrayList<Path<StringNode>> paths = collector.run();
		assertEquals(2, paths.size());

		sortPaths(paths);

		assertEquals("b;c;d;", pathString(paths.get(0)));
		assertEquals("b;d;", pathString(paths.get(1)));
	}

	@Override
	protected void initializeGraph() {
		StringNode nodeA = new StringNode("a");
		StringNode nodeB = new StringNode("b");
		StringNode nodeC = new StringNode("c");
		StringNode nodeD = new StringNode("d");

		// a -> b
		// a -> c
		HashSet<StringNode> map = new HashSet<StringNode>();
		map.add(nodeB);
		map.add(nodeC);
		graph.sourceMap.put(nodeA.key(), map);

		// b -> c
		// b -> d
		map = new HashSet<StringNode>();
		map.add(nodeC);
		map.add(nodeD);
		graph.sourceMap.put(nodeB.key(), map);

		// c -> d
		map = new HashSet<StringNode>();
		map.add(nodeD);
		graph.sourceMap.put(nodeC.key(), map);

		// d -> a
		// d -> b
		map = new HashSet<StringNode>();
		map.add(nodeA);
		map.add(nodeB);
		graph.sourceMap.put(nodeD.key(), map);
	}
}
