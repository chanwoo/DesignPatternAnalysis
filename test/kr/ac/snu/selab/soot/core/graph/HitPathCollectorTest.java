package kr.ac.snu.selab.soot.core.graph;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;

import kr.ac.snu.selab.soot.graph.HitPathCollector;
import kr.ac.snu.selab.soot.graph.Path;

import org.junit.Before;
import org.junit.Test;

public class HitPathCollectorTest extends AbstractGraphTest {

	HashSet<StringNode> destinationSet;

	@Before
	public void setUp() {
		super.setUp();
	}

	@Test
	public void test1() {
		HitPathCollector<StringNode> collector = new HitPathCollector<StringNode>(
				new StringNode("a"), graph, destinationSet);
		ArrayList<Path<StringNode>> paths = collector.run();
		assertEquals(3, paths.size());
		sortPaths(paths);

		assertEquals("a;c1;c2;c3;c4;", pathString(paths.get(0)));
		assertEquals("a;d1;d2;", pathString(paths.get(1)));
		assertEquals("a;e1;e2;", pathString(paths.get(2)));
	}

	@Test
	public void test2() {
		HitPathCollector<StringNode> collector = new HitPathCollector<StringNode>(
				new StringNode("b1"), graph, destinationSet);
		ArrayList<Path<StringNode>> paths = collector.run();
		assertEquals(0, paths.size());
	}

	@Test
	public void test3() {
		HitPathCollector<StringNode> collector = new HitPathCollector<StringNode>(
				new StringNode("c2"), graph, destinationSet);
		ArrayList<Path<StringNode>> paths = collector.run();
		assertEquals(1, paths.size());
		sortPaths(paths);

		assertEquals("c2;c3;c4;", pathString(paths.get(0)));
	}

	@Override
	protected void initializeGraph() {
		StringNode nodeA = new StringNode("a");
		StringNode nodeB1 = new StringNode("b1");
		StringNode nodeB2 = new StringNode("b2");
		StringNode nodeC1 = new StringNode("c1");
		StringNode nodeC2 = new StringNode("c2");
		StringNode nodeC3 = new StringNode("c3");
		StringNode nodeC4 = new StringNode("c4");
		StringNode nodeC5 = new StringNode("c5");
		StringNode nodeD1 = new StringNode("d1");
		StringNode nodeD2 = new StringNode("d2");
		StringNode nodeE1 = new StringNode("e1");
		StringNode nodeE2 = new StringNode("e2");
		StringNode nodeE3 = new StringNode("e3");
		StringNode nodeE4 = new StringNode("e4");
		StringNode nodeE5 = new StringNode("e5");
		StringNode nodeF1 = new StringNode("f1");
		StringNode nodeF2 = new StringNode("f2");
		StringNode nodeF3 = new StringNode("f3");
		StringNode nodeF4 = new StringNode("f4");
		StringNode nodeF5 = new StringNode("f5");

		// a -> b1 -> b2
		// a -> c1 -> c2 -> c3 -> c4 -> c5 & c3 -> c2(Cycle)
		// a -> d1 -> d2
		// a -> e1 -> e2 -> e3 -> e4 -> e5 & e3 -> e2(Cycle)
		// a -> f1 -> f2 -> f3 -> f4 -> f5 & f3 -> f2(Cycle)

		push(nodeA, nodeB1, nodeC1, nodeD1, nodeE1, nodeF1);

		push(nodeB1, nodeB2);

		push(nodeC1, nodeC2);
		push(nodeC2, nodeC3);
		push(nodeC3, nodeC2, nodeC4);
		push(nodeC4, nodeC5);

		push(nodeD1, nodeD2);

		push(nodeE1, nodeE2);
		push(nodeE2, nodeE3);
		push(nodeE3, nodeE2, nodeE4);
		push(nodeE4, nodeE5);

		push(nodeF1, nodeF2);
		push(nodeF2, nodeF3);
		push(nodeF3, nodeF2, nodeF4);
		push(nodeF4, nodeF5);

		destinationSet = new HashSet<StringNode>();
		destinationSet.add(nodeC4);
		destinationSet.add(nodeD2);
		destinationSet.add(nodeE2);
	}

	private void push(StringNode src, StringNode... targets) {
		HashSet<StringNode> nodes = new HashSet<StringNode>();
		for (StringNode target : targets) {
			nodes.add(target);
		}
		graph.targetMap.put(src.key(), nodes);
	}
}
