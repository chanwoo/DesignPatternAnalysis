package kr.ac.snu.selab.soot.graphx;

public class AllPathCollector<N extends Node> extends GraphPathCollector<N> {
	public AllPathCollector(N aStartNode, Graph<N> aGraph) {
		super(aStartNode, aGraph);
	}

	@Override
	protected boolean isGoal(N aNode) {
		boolean result = false;
		if (getChildren(aNode).isEmpty() || hitSet.contains(aNode.key())) {
			result = true;
		}
		// result = aNode.isCreator(); // &&
		// (graph.sourceNodes(aNode)).isEmpty();
		return result;
		// if (hitSet.contains(aNode.getElement())) {
		// return true;
		// }
		//
		// HashSet<N> sources = graph.sourceNodes(aNode);
		// if (sources == null || sources.size() < 1)
		// return true;

		// FIXME: Which implementation is correct?
		// for (N source : sources) {
		// if (hitSet.contains(source.getElement())) {
		// return true;
		// }
		// }

	}
}
