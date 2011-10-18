package kr.ac.snu.selab.soot.core.graphx;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;

import kr.ac.snu.selab.soot.graphx.Path;
import kr.ac.snu.selab.soot.graphx.ReverseAllPathCollector;

import org.junit.Before;
import org.junit.Test;

public class ReverseAllPathCollectorTest extends AbstractGraphTest {

	@Before
	public void setUp() {
		super.setUp();
	}

	@Test
	public void test1() {
		ReverseAllPathCollector<StringNode> collector = new ReverseAllPathCollector<StringNode>(
				new StringNode("f"), graph, null);
		ArrayList<Path<StringNode>> paths = collector.run();
		assertEquals(1, paths.size());
		assertEquals("f;g;", pathString(paths.get(0)));
	}

	@Test
	public void test2() {
		ReverseAllPathCollector<StringNode> collector = new ReverseAllPathCollector<StringNode>(
				new StringNode("b"), graph, null);
		ArrayList<Path<StringNode>> paths = collector.run();
		assertEquals(2, paths.size());

		sortPaths(paths);
		assertEquals("b;c;d;e;c;", pathString(paths.get(0)));
		assertEquals("b;c;d;f;g;", pathString(paths.get(1)));
	}

	@Test
	public void test3() {
		ReverseAllPathCollector<StringNode> collector = new ReverseAllPathCollector<StringNode>(
				new StringNode("h"), graph, null);
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
		HashSet<StringNode> sources = null;

		sources = new HashSet<StringNode>();
		sources.add(nodeB);
		sources.add(nodeH);
		graph.targetMap.put(nodeA.key(), sources);

		sources = new HashSet<StringNode>();
		sources.add(nodeC);
		graph.targetMap.put(nodeB.key(), sources);

		sources = new HashSet<StringNode>();
		sources.add(nodeD);
		graph.targetMap.put(nodeC.key(), sources);

		sources = new HashSet<StringNode>();
		sources.add(nodeE);
		sources.add(nodeF);
		graph.targetMap.put(nodeD.key(), sources);

		sources = new HashSet<StringNode>();
		sources.add(nodeC);
		graph.targetMap.put(nodeE.key(), sources);

		sources = new HashSet<StringNode>();
		sources.add(nodeG);
		graph.targetMap.put(nodeF.key(), sources);

		sources = new HashSet<StringNode>();
		sources.add(nodeI);
		graph.targetMap.put(nodeH.key(), sources);

		sources = new HashSet<StringNode>();
		sources.add(nodeJ);
		graph.targetMap.put(nodeI.key(), sources);

		sources = new HashSet<StringNode>();
		sources.add(nodeI);
		graph.targetMap.put(nodeJ.key(), sources);
	}
}
