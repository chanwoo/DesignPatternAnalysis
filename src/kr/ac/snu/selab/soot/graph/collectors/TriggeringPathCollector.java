package kr.ac.snu.selab.soot.graph.collectors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import kr.ac.snu.selab.soot.graph.Graph;
import kr.ac.snu.selab.soot.graph.GraphPathCollector;
import kr.ac.snu.selab.soot.graph.Node;
import kr.ac.snu.selab.soot.graph.Path;
import soot.SootMethod;

public class TriggeringPathCollector<N extends Node> extends GraphPathCollector<N> {
	private Set<N> destinationSet;

	public TriggeringPathCollector(N aStartNode, Graph<N> aGraph,
			Set<N> aDestinationSet) {
		super(aStartNode, aGraph);
		destinationSet = aDestinationSet;
	}

	@Override
	protected boolean isForwardSearch() {
		return true;
	}

	@Override
	protected boolean isGoal(N aNode) {
		return destinationSet.contains(aNode);
	}
	
	protected int findPaths(N aNode, Graph<N> graph, ArrayList<Path<N>> output,
			final int pathThreshold) {
		if (aNode == null)
			return DONE;
		
		if (((SootMethod)aNode.getElement()).getName().equals("<init>")) {
			return DONE;
		}

		String nodeKey = aNode.key();

		if (pathsMap.containsKey(nodeKey)) {
			output.addAll(pathsMap.get(nodeKey));
			return DONE;
		}

		if (isGoal(aNode)) {
			Path<N> path = new Path<N>();
			path.addTop(aNode);
			output.add(path);
			hitSet.add(nodeKey);
			pathsMap.put(nodeKey, output);
			return DONE;
		}

		if (hitSet.contains(nodeKey)) {
			return CYCLE;
		} else {
			hitSet.add(nodeKey);
		}

		HashSet<N> nextNodes = getChildren(aNode);
		for (N node : nextNodes) {
			if (output.size() >= pathThreshold) {
				// For performance
				break;
			}

			if (node == null)
				continue;

			ArrayList<Path<N>> pathSet = new ArrayList<Path<N>>();
			int result = findPaths(node, graph, pathSet, pathThreshold);
			if (result == CYCLE) {
				continue;
			} else {
				for (Path<N> p : pathSet) {
					Path<N> p1 = p.copy();
					p1.addTop(aNode);
					output.add(p1);
				}
			}
		}

		pathsMap.put(nodeKey, output);
		return DONE;
	}

}
