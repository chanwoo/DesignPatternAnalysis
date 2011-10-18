package kr.ac.snu.selab.soot.core.graph;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;

import kr.ac.snu.selab.soot.graph.AllPathCollector;
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
		HashSet<StringNode> sources = null;

		sources = new HashSet<StringNode>();
		sources.add(nodeB);
		sources.add(nodeH);
		graph.sourceMap.put(nodeA.key(), sources);

		sources = new HashSet<StringNode>();
		sources.add(nodeC);
		graph.sourceMap.put(nodeB.key(), sources);

		sources = new HashSet<StringNode>();
		sources.add(nodeD);
		graph.sourceMap.put(nodeC.key(), sources);

		sources = new HashSet<StringNode>();
		sources.add(nodeE);
		sources.add(nodeF);
		graph.sourceMap.put(nodeD.key(), sources);

		sources = new HashSet<StringNode>();
		sources.add(nodeC);
		graph.sourceMap.put(nodeE.key(), sources);

		sources = new HashSet<StringNode>();
		sources.add(nodeG);
		graph.sourceMap.put(nodeF.key(), sources);

		sources = new HashSet<StringNode>();
		sources.add(nodeI);
		graph.sourceMap.put(nodeH.key(), sources);

		sources = new HashSet<StringNode>();
		sources.add(nodeJ);
		graph.sourceMap.put(nodeI.key(), sources);

		sources = new HashSet<StringNode>();
		sources.add(nodeI);
		graph.sourceMap.put(nodeJ.key(), sources);
	}
}
