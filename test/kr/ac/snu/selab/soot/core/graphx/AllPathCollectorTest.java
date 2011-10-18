package kr.ac.snu.selab.soot.core.graphx;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import kr.ac.snu.selab.soot.graphx.AllPathCollector;
import kr.ac.snu.selab.soot.graphx.Graph;
import kr.ac.snu.selab.soot.graphx.GraphPathCollector;
import kr.ac.snu.selab.soot.graphx.Node;
import kr.ac.snu.selab.soot.graphx.Path;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

public class AllPathCollectorTest {
	private StringGraph graph;

	@Before
	public void setUp() {
		Logger.getLogger(GraphPathCollector.class).setLevel(Level.OFF);
		graph = new StringGraph();
		graph.initialize();
	}

	@Test
	public void test1() {
		AllPathCollector<StringNode> collector = new AllPathCollector<StringNode>(
				new StringNode("f"), graph);
		ArrayList<Path<StringNode>> paths = collector.run();
		assertEquals(1, paths.size());
		assertEquals("f;g;", pathString(paths.get(0)));
	}

	@Test
	public void test2() {
		AllPathCollector<StringNode> collector = new AllPathCollector<StringNode>(
				new StringNode("b"), graph);
		ArrayList<Path<StringNode>> paths = collector.run();
		assertEquals(2, paths.size());

		sortPaths(paths);
		assertEquals("b;c;d;e;c;", pathString(paths.get(0)));
		assertEquals("b;c;d;f;g;", pathString(paths.get(1)));
	}

	@Test
	public void test3() {
		AllPathCollector<StringNode> collector = new AllPathCollector<StringNode>(
				new StringNode("h"), graph);
		ArrayList<Path<StringNode>> paths = collector.run();
		assertEquals(1, paths.size());

		sortPaths(paths);
		assertEquals("h;i;j;i;", pathString(paths.get(0)));
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

	private static String pathString(Path<StringNode> path) {
		StringBuffer buffer = new StringBuffer();
		for (StringNode node : path.nodeList) {
			buffer.append(node.getElement());
			buffer.append(";");
		}
		return buffer.toString();
	}

	private static class StringGraph extends Graph<StringNode> {
		private void initialize() {
			StringNode nodeA = new StringNode("a");
			StringNode nodeB = new StringNode("b");
			StringNode nodeC = new StringNode("c");
			StringNode nodeD = new StringNode("d");
			StringNode nodeE = new StringNode("e");
			StringNode nodeF = new StringNode("f");
			StringNode nodeG = new StringNode("g");
			StringNode nodeH = new StringNode("h");
			StringNode nodeI = new StringNode("i");
			StringNode nodeJ = new StringNode("j");

			// a <- b <- c <- d <- e <- c (Cycle)
			// d <- f <- g
			// a <- h <- i <- j <- i
			HashSet<StringNode> sources = new HashSet<StringNode>();

			sources.add(nodeB);
			sources.add(nodeH);
			super.sourceMap.put(nodeA.getElement(), sources);
			sources = new HashSet<StringNode>();

			sources = new HashSet<StringNode>();
			sources.add(nodeC);
			super.sourceMap.put(nodeB.getElement(), sources);

			sources = new HashSet<StringNode>();
			sources.add(nodeD);
			super.sourceMap.put(nodeC.getElement(), sources);

			sources = new HashSet<StringNode>();
			sources.add(nodeE);
			sources.add(nodeF);
			super.sourceMap.put(nodeD.getElement(), sources);

			sources = new HashSet<StringNode>();
			sources.add(nodeC);
			super.sourceMap.put(nodeE.getElement(), sources);

			sources = new HashSet<StringNode>();
			sources.add(nodeG);
			super.sourceMap.put(nodeF.getElement(), sources);

			sources = new HashSet<StringNode>();
			sources.add(nodeI);
			super.sourceMap.put(nodeH.getElement(), sources);

			sources = new HashSet<StringNode>();
			sources.add(nodeJ);
			super.sourceMap.put(nodeI.getElement(), sources);

			sources = new HashSet<StringNode>();
			sources.add(nodeI);
			super.sourceMap.put(nodeJ.getElement(), sources);
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

		@Override
		public String toString() {
			return element;
		}
	}
}
