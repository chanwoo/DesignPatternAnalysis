package kr.ac.snu.selab.soot.core.graphx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import kr.ac.snu.selab.soot.graphx.Graph;
import kr.ac.snu.selab.soot.graphx.Node;

import org.junit.Before;
import org.junit.Test;

public class GraphTest {
	private StringGraph graph;

	@Before
	public void setUp() {
		graph = new StringGraph();
		graph.initialize();
	}

	@Test
	public void test() {
		HashSet<StringNode> nodes = graph.sourceNodes(new StringNode("a"));
		assertEquals(2, nodes.size());

		assertTrue(nodes.contains(new StringNode("b")));
		assertTrue(nodes.contains(new StringNode("c")));
		assertFalse(nodes.contains(new StringNode("d")));
	}

	private static class StringGraph extends Graph<StringNode> {
		private void initialize() {
			StringNode nodeA = new StringNode("a");
			StringNode nodeB = new StringNode("b");
			StringNode nodeC = new StringNode("c");
			StringNode nodeD = new StringNode("d");

			// a <- b
			// a <- c
			HashSet<StringNode> map = new HashSet<StringNode>();
			map.add(nodeB);
			map.add(nodeC);
			super.sourceMap.put(nodeA.getElement(), map);

			// b <- c
			// b <- d
			map = new HashSet<StringNode>();
			map.add(nodeC);
			map.add(nodeD);
			super.sourceMap.put(nodeB.getElement(), map);

			// c <- d
			map = new HashSet<StringNode>();
			map.add(nodeD);
			super.sourceMap.put(nodeC.getElement(), map);

			// d <- a
			// d <- b
			map = new HashSet<StringNode>();
			map.add(nodeA);
			map.add(nodeB);
			super.sourceMap.put(nodeD.getElement(), map);
		}
	}

	private static class StringNode extends Node<String> {
		StringNode(String text) {
			super(text);
		}

		@Override
		public String toXML() {
			return element;
		}
	}
}
