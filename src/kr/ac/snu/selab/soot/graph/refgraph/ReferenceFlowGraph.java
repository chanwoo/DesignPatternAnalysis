package kr.ac.snu.selab.soot.graph.refgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import kr.ac.snu.selab.soot.analyzer.LocalInfo;
import kr.ac.snu.selab.soot.graph.Graph;
import soot.SootMethod;

public class ReferenceFlowGraph extends Graph<LocalInfoNode> {

	private HashMap<String, LocalInfoNode> nodes;
	private HashMap<String, LocalInfoNode> nodesInMethodMap;
	private List<LocalInfoNode> startNodes;

	public ReferenceFlowGraph() {
		nodes = new HashMap<String, LocalInfoNode>();
		nodesInMethodMap = new HashMap<String, LocalInfoNode>();
		startNodes = new ArrayList<LocalInfoNode>();
	}
	
	// For test. Delete it later!
	public int numOfNodes() {
		return nodes.size();
	}
	
	public List<LocalInfoNode> startNodes() {
		return startNodes;
	}
	
	public void addStartNodes(Collection<LocalInfo> localInfos) {
		for (LocalInfo localInfo: localInfos) {
			startNodes.add(new LocalInfoNode(localInfo));
		}
	}
	
	public void addNode(LocalInfo localInfo) {
		String key = localInfo.key();
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
		String fromKey = from.key();
		String toKey = to.key();
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
		String key = localInfo.key();
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
