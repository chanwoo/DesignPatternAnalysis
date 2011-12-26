package kr.ac.snu.selab.soot.graph.pathcheckers;

import java.util.Set;

import kr.ac.snu.selab.soot.graph.Graph;
import kr.ac.snu.selab.soot.graph.GraphPathChecker;
import kr.ac.snu.selab.soot.graph.MetaInfo;

public class CallPathChecker extends GraphPathChecker<MetaInfo> {

	private Set<MetaInfo> endNodes;

	public CallPathChecker(MetaInfo aStartNode, Graph<MetaInfo> aGraph) {
		super(aStartNode, aGraph);
	}

	private Set<MetaInfo> endNodes() {
		return endNodes;
	}

	public void setEndNodes(Set<MetaInfo> aSet) {
		endNodes = aSet;
	}

	@Override
	protected boolean isGoal(MetaInfo aNode) {
		boolean result = false;
		if (endNodes().contains(aNode)) // ||
		// getChildren(aNode).isEmpty() ||
		// hitSet.contains(aNode.key()))
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
