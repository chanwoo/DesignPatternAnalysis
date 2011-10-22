package kr.ac.snu.selab.soot.graph.collectors;

import kr.ac.snu.selab.soot.graph.Graph;
import kr.ac.snu.selab.soot.graph.GraphPathCollector;
import kr.ac.snu.selab.soot.graph.Node;

public class STPathCollector<N extends Node> extends GraphPathCollector<N> {
	private N endNode;

	public STPathCollector(N aStartNode, N aEndNode, Graph<N> aGraph) {
		super(aStartNode, aGraph);
		this.endNode = aEndNode;
	}

	@Override
	protected boolean isGoal(N aNode) {
		return (aNode.equals(endNode));
	}
}
