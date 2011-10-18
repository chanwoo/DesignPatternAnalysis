package kr.ac.snu.selab.soot.graph;

import java.util.Set;


public class ReverseAllPathCollector<N extends Node> extends
		AllPathCollector<N> {
	public ReverseAllPathCollector(N aStartNode, Graph<N> aGraph,
			Set<MyNode> aDummySet) {
		super(aStartNode, aGraph);
	}

	protected boolean isForwardSearch() {
		return true;
	}
}
