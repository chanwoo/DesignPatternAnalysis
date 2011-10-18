package kr.ac.snu.selab.soot.graphx;

import java.util.Set;

import kr.ac.snu.selab.soot.graph.MyNode;

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
