package kr.ac.snu.selab.soot.graph.collectors;

import java.util.Set;

import kr.ac.snu.selab.soot.graph.Graph;
import kr.ac.snu.selab.soot.graph.GraphPathCollector;
import kr.ac.snu.selab.soot.graph.MetaInfo;
import kr.ac.snu.selab.soot.graph.Node;

public class CallPathCollector<N extends Node> extends GraphPathCollector<N> {
	
	private Set<MetaInfo> endNodes;
	
	public CallPathCollector(N aStartNode, Graph<N> aGraph) {
		super(aStartNode, aGraph);
	}
	
	private Set<MetaInfo> endNodes() {
		return endNodes;
	}
	
	public void setEndNodes(Set<MetaInfo> aSet) {
		endNodes = aSet;
	}

	@Override
	protected boolean isGoal(N aNode) {
		boolean result = false;
		if (endNodes().contains(aNode)) // || 
//				getChildren(aNode).isEmpty() || 
//				hitSet.contains(aNode.key())) 
		{
			result = true;
		}
		return result;
	}
	
	@Override
	protected boolean isForwardSearch() {
		return true;
	}
}
