package kr.ac.snu.selab.soot.graphx;

import java.util.Set;

import kr.ac.snu.selab.soot.graph.MyNode;

public class ReverseAllPathCollector extends AllPathCollector<MyNode> {
	protected boolean isForwardSearch() {
		return true;
	}

	public ReverseAllPathCollector(MyNode aStartNode, Graph<MyNode> aGraph,
			Set<MyNode> aDummySet) {
		super(aStartNode, aGraph);
	}

	@Override
	protected boolean isGoal(MyNode aNode) {
		boolean result = false;
		if (graph.targetNodes(aNode).isEmpty()
				|| hitSet.contains(aNode.toString())) {
			result = true;
		}
		// result = aNode.isCreator(); // &&
		// (graph.sourceNodes(aNode)).isEmpty();
		return result;
	}
}
