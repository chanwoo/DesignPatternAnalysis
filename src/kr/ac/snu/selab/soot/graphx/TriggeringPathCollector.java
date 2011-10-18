package kr.ac.snu.selab.soot.graphx;

import java.util.Set;

import kr.ac.snu.selab.soot.graph.MyNode;

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
