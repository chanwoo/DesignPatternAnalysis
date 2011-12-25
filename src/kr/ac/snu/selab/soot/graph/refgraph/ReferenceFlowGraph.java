package kr.ac.snu.selab.soot.graph.refgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import kr.ac.snu.selab.soot.analyzer.LocalInfo;
import kr.ac.snu.selab.soot.graph.Graph;
import soot.SootMethod;

public class ReferenceFlowGraph implements Graph<LocalInfoNode> {

	private HashMap<String, LocalInfoNode> nodes;
	private HashMap<String, LocalInfoNode> nodesInMethodMap;
	private List<LocalInfoNode> startNodes;

	public ReferenceFlowGraph() {
		nodes = new HashMap<String, LocalInfoNode>();
		nodesInMethodMap = new HashMap<String, LocalInfoNode>();
		startNodes = new ArrayList<LocalInfoNode>();
	}

	// XXX: For test. Delete it later!
	public int numOfNodes() {
		return nodes.size();
	}

	public List<LocalInfoNode> startNodes() {
		return startNodes;
	}

	public void addStartNodes(Collection<LocalInfo> localInfos) {
		for (LocalInfo localInfo : localInfos) {
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
	public Collection<LocalInfoNode> sourceNodes(LocalInfoNode aNode) {
		return aNode.getSources();
	}

	@Override
	public Collection<LocalInfoNode> targetNodes(LocalInfoNode aNode) {
		return aNode.getTargets();
	}

	@Override
	public String toXML() {
		String result = "";
		result = result + "<Graph>";
		// result = result + "<SourceMap>";
		// for (String key : sourceMap.keySet()) {
		// result = result + "<Key>";
		// result = result + MyUtil.removeBracket(key);
		// result = result + "</Key>";
		// result = result + "<Values>";
		// for (N aNode : sourceMap.get(key)) {
		// result = result + aNode.toXML();
		// }
		// result = result + "</Values>";
		// }
		// result = result + "</SourceMap>";
		// result = result + "<TargetMap>";
		// for (String key : targetMap.keySet()) {
		// result = result + "<Key>";
		// result = result + MyUtil.removeBracket(key);
		// result = result + "</Key>";
		// result = result + "<Values>";
		// for (N aNode : targetMap.get(key)) {
		// result = result + aNode.toXML();
		// }
		// result = result + "</Values>";
		// }
		// result = result + "</TargetMap>";
		result = result + "</Graph>";
		return result;
	}
}
