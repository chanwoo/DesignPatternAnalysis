package kr.ac.snu.selab.soot.core.graph;

import java.util.HashSet;

import kr.ac.snu.selab.soot.graph.AllPathCollector;
import kr.ac.snu.selab.soot.graph.Graph;

import org.junit.Before;
import org.junit.Test;

public class GraphPathCollectorPerformanceTest extends AbstractGraphTest {

	private StringNode nodeR = new StringNode("R");
	private StringNode nodeF = new StringNode("F");

	private static final int COUNT = 15;

	@Before
	public void setUp() {
		System.gc();
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
		}
		printResult("Setup Start", 0L, 0L);

		long tick1 = System.currentTimeMillis();
		super.setUp();
		long tick2 = System.currentTimeMillis();

		printResult("Setup Finished", tick1, tick2);
	}

	@Test
	public void testWeakCollector() {
		printResult("Weak Start", 0L, 0L);

		AllPathCollector<StringNode> collector = new WeakAllPathCollector(
				nodeR, graph);
		long tick1 = System.currentTimeMillis();
		collector.run();
		long tick2 = System.currentTimeMillis();

		printResult("Weak Finished", tick1, tick2);
	}

	@Test
	public void testToughCollector() {
		printResult("Tough Start", 0L, 0L);

		AllPathCollector<StringNode> collector = new ToughAllPathCollector(
				nodeR, graph);
		long tick1 = System.currentTimeMillis();
		collector.run();
		long tick2 = System.currentTimeMillis();

		printResult("Tough Finished", tick1, tick2);
	}

	private void printResult(String title, long tick1, long tick2) {

		long time = (long) (tick2 - tick1);

		long heapSize = Runtime.getRuntime().totalMemory();
		long heapMaxSize = Runtime.getRuntime().maxMemory();
		long heapFreeSize = Runtime.getRuntime().freeMemory();

		double freeRate = ((double) heapFreeSize * 100.0)
				/ (double) heapMaxSize;

		System.out.printf("%s - %d (ms)\n\t%f%% (%d / %d / %d)\n", title, time,
				freeRate, heapFreeSize, heapSize, heapMaxSize);
	}

	@Override
	protected void initializeGraph() {

		int key = 0;

		// (Balanced tree. Each node has COUNT children)
		// R <- A1 <- B1 <- C1 <- F
		StringNode[] childrenA = new StringNode[COUNT];
		for (int i = 0; i < COUNT; i++) {
			childrenA[i] = new StringNode((key++) + "");

			StringNode[] childrenB = new StringNode[COUNT];
			for (int j = 0; j < COUNT; j++) {
				childrenB[j] = new StringNode((key++) + "");

				StringNode[] childrenC = new StringNode[COUNT];
				for (int k = 0; k < COUNT; k++) {
					childrenC[k] = new StringNode((key++) + "");

					StringNode[] childrenD = new StringNode[COUNT];
					for (int l = 0; l < COUNT; l++) {
						childrenD[l] = new StringNode((key++) + "");

						StringNode[] childrenE = new StringNode[COUNT];
						for (int m = 0; m < COUNT; m++) {
							childrenE[m] = new StringNode((key++) + "");
							push(childrenE[m], nodeF);
						}

						push(childrenD[l], childrenE);
					}

					push(childrenC[k], childrenD);
				}

				push(childrenB[j], childrenC);
			}

			push(childrenA[i], childrenB);
		}
		push(nodeR, childrenA);
	}

	private void push(StringNode target, StringNode... sources) {
		HashSet<StringNode> nodes = new HashSet<StringNode>();
		for (StringNode source : sources) {
			nodes.add(source);
		}
		graph.sourceMap.put(target.key(), nodes);
	}

	private static class ToughAllPathCollector extends
			AllPathCollector<StringNode> {

		public ToughAllPathCollector(StringNode aStartNode,
				Graph<StringNode> aGraph) {
			super(aStartNode, aGraph);
		}

		protected int getPathThreshold() {
			return 100;
		}
	}

	private static class WeakAllPathCollector extends
			AllPathCollector<StringNode> {

		public WeakAllPathCollector(StringNode aStartNode,
				Graph<StringNode> aGraph) {
			super(aStartNode, aGraph);
		}

		protected int getPathThreshold() {
			return 10;
		}
	}
}
