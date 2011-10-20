package kr.ac.snu.selab.soot.graph.collectors;

import kr.ac.snu.selab.soot.graph.Graph;
import kr.ac.snu.selab.soot.graph.Node;

public class ReverseAllPathCollector<N extends Node> extends
		AllPathCollector<N> {
	public ReverseAllPathCollector(N aStartNode, Graph<N> aGraph) {
		super(aStartNode, aGraph);
	}

	@Override
	protected boolean isForwardSearch() {
		return true;
	}
}
