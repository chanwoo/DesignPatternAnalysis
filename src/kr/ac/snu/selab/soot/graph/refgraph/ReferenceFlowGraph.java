package kr.ac.snu.selab.soot.graph.refgraph;

import java.util.HashMap;
import java.util.HashSet;

import soot.SootMethod;

import kr.ac.snu.selab.soot.analyzer.LocalInfo;
import kr.ac.snu.selab.soot.graph.Graph;

public class ReferenceFlowGraph extends Graph<LocalInfoNode> {

	private HashMap<String, LocalInfoNode> nodes;
	private HashMap<String, LocalInfoNode> nodesInMethodMap;

	public ReferenceFlowGraph() {
		nodes = new HashMap<String, LocalInfoNode>();
		nodesInMethodMap = new HashMap<String, LocalInfoNode>();
	}

	public void addNode(LocalInfo localInfo) {
		String key = localInfo.toString();
		if (nodes.containsKey(key)) {
			return;
		}

		LocalInfoNode node = new LocalInfoNode(localInfo);
		nodes.put(key, node);
		SootMethod method = localInfo.method();
		if (method != null) {
			nodesInMethodMap.put(method.getName(), node);
		}
	}

	public void addEdge(LocalInfo from, LocalInfo to) {
		String fromKey = from.toString();
		String toKey = to.toString();
		LocalInfoNode sourceNode = null, targetNode = null;

		if (!nodes.containsKey(fromKey)) {
			addNode(from);
		}
		sourceNode = nodes.get(fromKey);

		if (!nodes.containsKey(toKey)) {
			addNode(to);
		}
		targetNode = nodes.get(toKey);

		sourceNode.addTarget(targetNode);
		targetNode.addSource(sourceNode);
	}

	public LocalInfoNode find(LocalInfo localInfo) {
		String key = localInfo.toString();
		return nodes.get(key);
	}

	@Override
	public HashSet<LocalInfoNode> sourceNodes(LocalInfoNode aNode) {
		HashSet<LocalInfoNode> set = new HashSet<LocalInfoNode>();
		for (LocalInfoNode node : aNode.getSources()) {
			set.add(node);
		}
		return set;
	}

	@Override
	public HashSet<LocalInfoNode> targetNodes(LocalInfoNode aNode) {
		HashSet<LocalInfoNode> set = new HashSet<LocalInfoNode>();
		for (LocalInfoNode node : aNode.getTargets()) {
			set.add(node);
		}
		return set;
	}
}
