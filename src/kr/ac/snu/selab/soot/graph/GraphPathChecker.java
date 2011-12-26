package kr.ac.snu.selab.soot.graph;

import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;

public abstract class GraphPathChecker<N extends Node> {
	private static Logger log = Logger.getLogger(GraphPathChecker.class);

	protected static final int HIT = 0;
	protected static final int NO_HIT = 2;

	protected HashSet<String> hitSet;
	protected N startNode;
	protected Graph<N> graph;

	public GraphPathChecker(N aStartNode, Graph<N> aGraph) {
		hitSet = new HashSet<String>();

		this.startNode = aStartNode;
		this.graph = aGraph;
	}

	protected boolean isForwardSearch() {
		return false;
	}

	public boolean check() {
		log.debug("Path checking start: " + startNode.toString());
		long tick1 = System.currentTimeMillis();

		hitSet.clear();

		int result = check(startNode, graph);

		long tick2 = System.currentTimeMillis();
		log.debug("Path checking finished, " + (tick2 - tick1));

		return (result == HIT);
	}

	protected int check(N aNode, Graph<N> graph) {
		if (aNode == null)
			return NO_HIT;

		String nodeKey = aNode.key();

		if (isGoal(aNode)) {
			hitSet.add(nodeKey);
			return HIT;
		}

		if (hitSet.contains(nodeKey)) {
			return NO_HIT;
		} else {
			hitSet.add(nodeKey);
		}

		Collection<N> nextNodes = getChildren(aNode);
		for (N node : nextNodes) {
			if (node == null)
				continue;

			if (check(node, graph) == HIT) {
				return HIT;
			}
		}

		return NO_HIT;
	}

	protected Collection<N> getChildren(N aNode) {
		if (isForwardSearch()) {
			return graph.targetNodes(aNode);
		} else {
			return graph.sourceNodes(aNode);
		}
	}

	protected abstract boolean isGoal(N aNode);
}
