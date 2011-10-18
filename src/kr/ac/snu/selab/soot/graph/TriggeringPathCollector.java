package kr.ac.snu.selab.soot.graph;

import java.util.Set;


public class TriggeringPathCollector extends GraphPathCollector<MyNode> {
	private Set<MyNode> destinationSet;

	protected boolean isForwardSearch() {
		return true;
	}

	public TriggeringPathCollector(MyNode aStartNode, Graph<MyNode> aGraph,
			Set<MyNode> aDestinationSet) {
		super(aStartNode, aGraph);
		destinationSet = aDestinationSet;
	}

	@Override
	protected boolean isGoal(MyNode aNode) {
		return destinationSet.contains(aNode);
	}

}
