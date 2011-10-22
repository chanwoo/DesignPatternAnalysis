package kr.ac.snu.selab.soot.graph.collectors;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;

import kr.ac.snu.selab.soot.graph.AbstractGraphTest;
import kr.ac.snu.selab.soot.graph.Path;

import org.junit.Before;
import org.junit.Test;

public class AllPathCollectorTest extends AbstractGraphTest {

	@Before
	public void setUp() {
		super.setUp();
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

	@Override
	protected void initializeGraph() {
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
		push(nodeA, nodeB, nodeH);

		push(nodeB, nodeC);

		push(nodeC, nodeD);

		push(nodeD, nodeE, nodeF);

		push(nodeE, nodeC);

		push(nodeF, nodeG);

		push(nodeH, nodeI);

		push(nodeI, nodeJ);

		push(nodeJ, nodeI);
	}

	private void push(StringNode target, StringNode... sources) {
		HashSet<StringNode> nodes = new HashSet<StringNode>();
		for (StringNode source : sources) {
			nodes.add(source);
		}
		graph.getSourceMap().put(target.key(), nodes);
	}
}
