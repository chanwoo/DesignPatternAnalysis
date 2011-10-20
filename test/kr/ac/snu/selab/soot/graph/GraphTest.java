package kr.ac.snu.selab.soot.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

public class GraphTest extends AbstractGraphTest {
	@Before
	public void setUp() {
		super.setUp();
	}

	@Test
	public void test() {
		HashSet<StringNode> nodes = graph.sourceNodes(new StringNode("a"));
		assertEquals(2, nodes.size());

		assertTrue(nodes.contains(new StringNode("b")));
		assertTrue(nodes.contains(new StringNode("c")));
		assertFalse(nodes.contains(new StringNode("d")));
	}

	@Override
	protected void initializeGraph() {
		StringNode nodeA = new StringNode("a");
		StringNode nodeB = new StringNode("b");
		StringNode nodeC = new StringNode("c");
		StringNode nodeD = new StringNode("d");

		// a <- b
		// a <- c
		HashSet<StringNode> map = new HashSet<StringNode>();
		map.add(nodeB);
		map.add(nodeC);
		graph.sourceMap.put(nodeA.key(), map);

		// b <- c
		// b <- d
		map = new HashSet<StringNode>();
		map.add(nodeC);
		map.add(nodeD);
		graph.sourceMap.put(nodeB.key(), map);

		// c <- d
		map = new HashSet<StringNode>();
		map.add(nodeD);
		graph.sourceMap.put(nodeC.key(), map);

		// d <- a
		// d <- b
		map = new HashSet<StringNode>();
		map.add(nodeA);
		map.add(nodeB);
		graph.sourceMap.put(nodeD.key(), map);
	}
}
