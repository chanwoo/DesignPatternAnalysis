package kr.ac.snu.selab.soot.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.log4j.Logger;

public abstract class GraphPathCollector<N extends Node> {
	private static Logger log = Logger.getLogger(GraphPathCollector.class);

	protected HashMap<String, ArrayList<Path<N>>> pathsMap;
	protected HashSet<String> hitSet;
	protected N startNode;
	protected Graph<N> graph;

	private static final int PATH_SET_SIZE_LIMIT = 100;

	public GraphPathCollector(N aStartNode, Graph<N> aGraph) {
		pathsMap = new HashMap<String, ArrayList<Path<N>>>();
		hitSet = new HashSet<String>();

		this.startNode = aStartNode;
		this.graph = aGraph;
	}

	protected boolean isForwardSearch() {
		return false;
	}

	public ArrayList<Path<N>> run() {
		log.debug("path collecting start: " + startNode.toString());
		long tick1 = System.currentTimeMillis();

		pathsMap.clear();
		hitSet.clear();

		int pathThreshold = getPathThreshold();
		ArrayList<Path<N>> paths = new ArrayList<Path<N>>();
		int result = findPaths(startNode, graph, paths, pathThreshold);
		if (result == CYCLE) {
			// throw an exception
			return paths;
		}

		long tick2 = System.currentTimeMillis();
		log.debug("Call path collecting finished, " + (tick2 - tick1));

		return paths;
	}

	private static final int DONE = 0;
	private static final int CYCLE = 1;

	private int findPaths(N aNode, Graph<N> graph, ArrayList<Path<N>> output,
			final int pathThreshold) {
		if (aNode == null)
			return DONE;

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

	protected HashSet<N> getChildren(N aNode) {
		if (isForwardSearch()) {
			return graph.targetNodes(aNode);
		} else {
			return graph.sourceNodes(aNode);
		}
	}

	protected int getPathThreshold() {
		return PATH_SET_SIZE_LIMIT;
	}

	protected abstract boolean isGoal(N aNode);
}
