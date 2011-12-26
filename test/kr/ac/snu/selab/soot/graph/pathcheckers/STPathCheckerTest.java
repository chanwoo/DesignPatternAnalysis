package kr.ac.snu.selab.soot.graph.pathcheckers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import kr.ac.snu.selab.soot.graph.AbstractGraphTest;

import org.junit.Before;
import org.junit.Test;

public class STPathCheckerTest extends AbstractGraphTest {
	@Before
	public void setUp() {
		super.setUp();
	}

	@Test
	public void test1() {
		STPathChecker<StringNode> checker = new STPathChecker<StringNode>(
				new StringNode("c"), new StringNode("d"), graph);
		assertTrue(checker.check());
	}

	@Test
	public void test2() {
		STPathChecker<StringNode> checker = new STPathChecker<StringNode>(
				new StringNode("a"), new StringNode("e"), graph);
		assertTrue(checker.check());

		checker = new STPathChecker<StringNode>(new StringNode("e"),
				new StringNode("a"), graph);
		assertFalse(checker.check());
	}

	@Override
	protected void initializeGraph() {
		StringNode nodeA = new StringNode("a");
		StringNode nodeB = new StringNode("b");
		StringNode nodeC = new StringNode("c");
		StringNode nodeD = new StringNode("d");
		StringNode nodeE = new StringNode("e");

		// a -> b
		// a -> c
		push(nodeA, nodeB, nodeC, nodeE);

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
		graph.getSourceMap().put(target.key(), nodes);
	}
}
