package kr.ac.snu.selab.soot.graph.collectors;

import java.util.Set;

import kr.ac.snu.selab.soot.graph.Graph;
import kr.ac.snu.selab.soot.graph.GraphPathCollector;
import kr.ac.snu.selab.soot.graph.Node;

public class HitPathCollector<N extends Node> extends GraphPathCollector<N> {
	private Set<N> destinationSet;

	public HitPathCollector(N aStartNode, Graph<N> aGraph,
			Set<N> aDestinationSet) {
		super(aStartNode, aGraph);
		destinationSet = aDestinationSet;
	}

	@Override
	protected boolean isForwardSearch() {
		return true;
	}

	@Override
	protected boolean isGoal(N aNode) {
		return destinationSet.contains(aNode);
	}

}
