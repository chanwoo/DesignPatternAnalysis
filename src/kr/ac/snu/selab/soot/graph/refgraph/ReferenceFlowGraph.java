package kr.ac.snu.selab.soot.graph.refgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import kr.ac.snu.selab.soot.analyzer.LocalInfo;
import kr.ac.snu.selab.soot.graph.Graph;
import soot.SootMethod;

public class ReferenceFlowGraph extends Graph<LocalInfoNode> {

	private ArrayList<LocalInfoNode> nodes;
	private HashMap<String, LocalInfoNode> nodeMap;

	public ReferenceFlowGraph() {
		nodes = new ArrayList<LocalInfoNode>();
		nodeMap = new HashMap<String, LocalInfoNode>();
	}

	public void add(LocalInfo localInfo) {
		LocalInfoNode node = new LocalInfoNode(localInfo);
		if (nodes.contains(node)) {
			return;
		}
		node.collectConnectedNodes();
		nodes.add(node);

		SootMethod method = localInfo.method();
		String methodName = method.getName();
		nodeMap.put(methodName, node);
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
