package kr.ac.snu.selab.soot.core.graphx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import kr.ac.snu.selab.soot.graphx.Graph;
import kr.ac.snu.selab.soot.graphx.GraphPathCollector;
import kr.ac.snu.selab.soot.graphx.Node;
import kr.ac.snu.selab.soot.graphx.Path;
import kr.ac.snu.selab.soot.graphx.STPathCollector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

public class STPathCollectorTest {
	private StringGraph graph;

	@Before
	public void setUp() {
		Logger.getLogger(GraphPathCollector.class).setLevel(Level.OFF);
		graph = new StringGraph();
		graph.initialize();
	}

	@Test
	public void test1() {
		STPathCollector<StringNode> collector = new STPathCollector<StringNode>(
				new StringNode("c"), new StringNode("d"), graph);
		ArrayList<Path<StringNode>> paths = collector.run();
		assertEquals(1, paths.size());

		Path<StringNode> path = paths.get(0);
		assertFalse(path.contains(new StringNode("a")));
		assertFalse(path.contains(new StringNode("b")));
		assertTrue(path.contains(new StringNode("c")));
		assertTrue(path.contains(new StringNode("d")));
	}

	@Test
	public void test2() {
		STPathCollector<StringNode> collector = new STPathCollector<StringNode>(
				new StringNode("b"), new StringNode("d"), graph);
		ArrayList<Path<StringNode>> paths = collector.run();
		assertEquals(2, paths.size());

		sortPaths(paths);
		// b -> c -> d
		// b -> d

		Path<StringNode> path = paths.get(0);
		assertFalse(path.contains(new StringNode("a")));
		assertTrue(path.contains(new StringNode("b")));
		assertTrue(path.contains(new StringNode("c")));
		assertTrue(path.contains(new StringNode("d")));

		path = paths.get(1);
		assertFalse(path.contains(new StringNode("a")));
		assertTrue(path.contains(new StringNode("b")));
		assertFalse(path.contains(new StringNode("c")));
		assertTrue(path.contains(new StringNode("d")));
	}

	private void sortPaths(ArrayList<Path<StringNode>> paths) {
		Collections.sort(paths, new Comparator<Path<StringNode>>() {
			@Override
			public int compare(Path<StringNode> path1, Path<StringNode> path2) {
				ArrayList<StringNode> list1 = path1.nodeList;
				ArrayList<StringNode> list2 = path2.nodeList;
				int length1 = path1.length();
				int length2 = path1.length();
				int min = Math.min(length1, length2);

				for (int i = 0; i < min; i++) {
					StringNode node1 = list1.get(i);
					StringNode node2 = list2.get(i);

					String element1 = node1.getElement();
					String element2 = node2.getElement();

					if (element1.compareTo(element2) < 0)
						return -1;
					if (element1.compareTo(element2) > 0)
						return 1;
				}

				return 0;
			}
		});
	}

	private static class StringGraph extends Graph<StringNode> {
		private void initialize() {
			StringNode nodeA = new StringNode("a");
			StringNode nodeB = new StringNode("b");
			StringNode nodeC = new StringNode("c");
			StringNode nodeD = new StringNode("d");

			// a -> b
			// a -> c
			HashSet<StringNode> map = new HashSet<StringNode>();
			map.add(nodeB);
			map.add(nodeC);
			super.sourceMap.put(nodeA.getElement(), map);

			// b -> c
			// b -> d
			map = new HashSet<StringNode>();
			map.add(nodeC);
			map.add(nodeD);
			super.sourceMap.put(nodeB.getElement(), map);

			// c -> d
			map = new HashSet<StringNode>();
			map.add(nodeD);
			super.sourceMap.put(nodeC.getElement(), map);

			// d -> a
			// d -> b
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

		@Override
		public int hashCode() {
			return element.hashCode();
		}

		@Override
		public boolean equals(Object anObject) {
			if (anObject.getClass() != getClass())
				return false;

			StringNode compare = (StringNode) anObject;
			return (element.equals(compare.element));
		}
	}
}
