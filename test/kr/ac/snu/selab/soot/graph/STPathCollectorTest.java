package kr.ac.snu.selab.soot.graph;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;

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
		push(nodeA, nodeB, nodeC);

		// b -> c
		// b -> d
		push(nodeB, nodeC, nodeD);

		// c -> d
		push(nodeC, nodeD);

		// d -> a
		// d -> b
		push(nodeD, nodeA, nodeB);
	}

	private void push(StringNode target, StringNode... sources) {
		HashSet<StringNode> nodes = new HashSet<StringNode>();
		for (StringNode source : sources) {
			nodes.add(source);
		}
		graph.sourceMap.put(target.key(), nodes);
	}
}
