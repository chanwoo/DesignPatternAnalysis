package kr.ac.snu.selab.soot.graph.pathcheckers;

import kr.ac.snu.selab.soot.graph.Graph;
import kr.ac.snu.selab.soot.graph.GraphPathChecker;
import kr.ac.snu.selab.soot.graph.Node;

public class STPathChecker<N extends Node> extends GraphPathChecker<N> {
	private N endNode;

	public STPathChecker(N aStartNode, N aEndNode, Graph<N> aGraph) {
		super(aStartNode, aGraph);
		this.endNode = aEndNode;
	}

	@Override
	protected boolean isGoal(N aNode) {
		return (aNode.equals(endNode));
	}
}
