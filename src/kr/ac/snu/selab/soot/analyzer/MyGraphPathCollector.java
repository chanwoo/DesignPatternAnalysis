package kr.ac.snu.selab.soot.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class MyGraphPathCollector {
	protected HashMap<String, ArrayList<MyPath>> pathsMap;
	protected HashSet<String> hitSet;
	protected MyNode startNode;
	protected MyGraph graph;

	private static final int PATH_SET_SIZE_LIMIT = 20;

	public MyGraphPathCollector(MyNode aStartNode, MyGraph aGraph) {
		pathsMap = new HashMap<String, ArrayList<MyPath>>();
		hitSet = new HashSet<String>();

		this.startNode = aStartNode;
		this.graph = aGraph;
	}

	public ArrayList<MyPath> run() {
		System.out.println("path collecting start: "
				+ startNode.toString());
		long tick1 = System.currentTimeMillis();

		pathsMap.clear();
		hitSet.clear();

		ArrayList<MyPath> paths = new ArrayList<MyPath>();
		int result = findPaths(startNode, graph, paths);
		if (result == CYCLE) {
			// throw an exception
			return paths;
		}

		long tick2 = System.currentTimeMillis();
		System.out.println("Call path collecting finished, " + (tick2 - tick1));

		return paths;
	}

	private static final int DONE = 0;
	private static final int CYCLE = 1;

	private int findPaths(MyNode aNode, MyGraph graph, ArrayList<MyPath> output) {
		if (aNode == null)
			return DONE;

		String nodeKey = aNode.toString();

		if (pathsMap.containsKey(nodeKey)) {
			output.addAll(pathsMap.get(nodeKey));
			return DONE;
		}

		if (isGoal(aNode)) {
			MyPath path = new MyPath();
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

		HashSet<MyNode> sources = graph.sourceNodes(aNode);
		for (MyNode node : sources) {
			if (node == null)
				continue;

			if (output.size() >= PATH_SET_SIZE_LIMIT) {
				// For performance
				break;
			}

			ArrayList<MyPath> pathSet = new ArrayList<MyPath>();
			int result = findPaths(node, graph, pathSet);
			if (result == CYCLE)
				continue;

			for (MyPath p : pathSet) {
				MyPath p1 = p.copy();
				p1.addTop(aNode);
				output.add(p1);
			}
		}

		pathsMap.put(nodeKey, output);
		return DONE;
	}

	protected abstract boolean isGoal(MyNode aNode);
}
