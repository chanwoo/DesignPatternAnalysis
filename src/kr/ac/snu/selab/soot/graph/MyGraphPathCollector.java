//package kr.ac.snu.selab.soot.graph;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Set;
//
//public abstract class MyGraphPathCollector {
//	protected HashMap<String, ArrayList<MyPath>> pathsMap;
//	protected HashSet<String> hitSet;
//	protected MyNode startNode;
//	protected MyGraph graph;
//	protected boolean isForwardSearch;
//
//	private static final int PATH_SET_SIZE_LIMIT = 100;
//
//	public MyGraphPathCollector(MyNode aStartNode, MyGraph aGraph) {
//		pathsMap = new HashMap<String, ArrayList<MyPath>>();
//		hitSet = new HashSet<String>();
//
//		this.startNode = aStartNode;
//		this.graph = aGraph;
//		this.isForwardSearch = false;
//	}
//
//	public MyGraphPathCollector(MyNode aStartNode, MyGraph aGraph,
//			boolean anIsForwardSearch) {
//		pathsMap = new HashMap<String, ArrayList<MyPath>>();
//		hitSet = new HashSet<String>();
//
//		this.startNode = aStartNode;
//		this.graph = aGraph;
//		this.isForwardSearch = anIsForwardSearch;
//	}
//
//	public ArrayList<MyPath> run() {
//		System.out.println("path collecting start: " + startNode.toString());
//		long tick1 = System.currentTimeMillis();
//
//		pathsMap.clear();
//		hitSet.clear();
//
//		ArrayList<MyPath> paths = new ArrayList<MyPath>();
//		int result = findPaths(startNode, graph, paths);
//		if (result == CYCLE) {
//			// throw an exception
//			return paths;
//		}
//
//		long tick2 = System.currentTimeMillis();
//		System.out.println("Call path collecting finished, " + (tick2 - tick1));
//
//		return paths;
//	}
//
//	private static final int DONE = 0;
//	private static final int CYCLE = 1;
//
//	private int findPaths(MyNode aNode, MyGraph graph, ArrayList<MyPath> output) {
//		if (aNode == null)
//			return DONE;
//
//		String nodeKey = aNode.toString();
//
//		if (pathsMap.containsKey(nodeKey)) {
//			output.addAll(pathsMap.get(nodeKey));
//			return DONE;
//		}
//
//		if (isGoal(aNode)) {
//			MyPath path = new MyPath();
//			path.addTop(aNode);
//			output.add(path);
//			hitSet.add(nodeKey);
//			pathsMap.put(nodeKey, output);
//			return DONE;
//		}
//
//		if (hitSet.contains(nodeKey)) {
//			return CYCLE;
//		} else {
//			hitSet.add(nodeKey);
//		}
//		
//		Set<MyNode> nextNodes = new HashSet<MyNode>();
//		if (!isForwardSearch) {
//			nextNodes = graph.sourceNodes(aNode);
//		} else if (isForwardSearch){
//			nextNodes = graph.targetNodes(aNode);
//		}
//		
//		for (MyNode node : nextNodes) {
//			if (output.size() >= PATH_SET_SIZE_LIMIT) {
//				// For performance
//				break;
//			}
//
//			if (node == null)
//				continue;
//
//			ArrayList<MyPath> pathSet = new ArrayList<MyPath>();
//			int result = findPaths(node, graph, pathSet);
//			if (result == CYCLE) {
//				continue;
//				// for (MyPath p : pathSet) {
//				// MyPath p1 = p.copy();
//				// output.add(p1);
//				// }
//			} else {
//				for (MyPath p : pathSet) {
//					MyPath p1 = p.copy();
//					p1.addTop(aNode);
//					output.add(p1);
//				}
//			}
//		}
//
//		pathsMap.put(nodeKey, output);
//		return DONE;
//	}
//
//	protected abstract boolean isGoal(MyNode aNode);
//}
