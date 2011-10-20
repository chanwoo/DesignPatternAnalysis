package kr.ac.snu.selab.soot.graph;

import java.util.Set;

public class HitPathCollector<N extends Node> extends GraphPathCollector<N> {
	private Set<N> destinationSet;

	protected boolean isForwardSearch() {
		return true;
	}

	public HitPathCollector(N aStartNode, Graph<N> aGraph,
			Set<N> aDestinationSet) {
		super(aStartNode, aGraph);
		destinationSet = aDestinationSet;
	}

	@Override
	protected boolean isGoal(N aNode) {
		return destinationSet.contains(aNode);
	}

}
