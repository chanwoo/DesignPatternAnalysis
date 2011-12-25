package kr.ac.snu.selab.soot.graph.collectors;

import kr.ac.snu.selab.soot.graph.Graph;
import kr.ac.snu.selab.soot.graph.GraphPathCollector;
import kr.ac.snu.selab.soot.graph.Node;
import kr.ac.snu.selab.soot.graph.refgraph.ReferenceFlowGraph;

public class ReferenceFlowCollector<N extends Node> extends GraphPathCollector<N> {
	public ReferenceFlowCollector(N aStartNode, Graph<N> aGraph) {
		super(aStartNode, aGraph);
	}

	@Override
	protected boolean isGoal(N aNode) {
		boolean result = false;
		if (((ReferenceFlowGraph)graph).endNodes().contains(aNode) || 
				getChildren(aNode).isEmpty() || 
				hitSet.contains(aNode.key())) {
			result = true;
		}
		return result;
	}
	
	@Override
	protected boolean isForwardSearch() {
		return true;
	}
}
