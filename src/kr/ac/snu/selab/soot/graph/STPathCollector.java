package kr.ac.snu.selab.soot.graph;

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
