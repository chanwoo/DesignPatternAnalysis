package kr.ac.snu.selab.soot.graph.collectors;

import kr.ac.snu.selab.soot.graph.Graph;
import kr.ac.snu.selab.soot.graph.GraphPathCollector;
import kr.ac.snu.selab.soot.graph.Node;

public class AllPathCollector<N extends Node> extends GraphPathCollector<N> {
	public AllPathCollector(N aStartNode, Graph<N> aGraph) {
		super(aStartNode, aGraph);
	}

	@Override
	protected boolean isGoal(N aNode) {
		boolean result = false;
		if (getChildren(aNode).isEmpty() || hitSet.contains(aNode.key())) {
			result = true;
		}
		return result;
	}
}
