package kr.ac.snu.selab.soot.graph.refgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kr.ac.snu.selab.soot.analyzer.LocalInfo;
import kr.ac.snu.selab.soot.graph.Graph;

public class ReferenceFlowGraph implements Graph<LocalInfoNode> {

	private HashMap<String, LocalInfoNode> nodes;
	// XXX: Remove this field if it is useless
	private HashMap<String, LocalInfoNode> nodesInMethodMap;
	private List<LocalInfoNode> startNodes;
	private Set<LocalInfoNode> endNodes;

	public ReferenceFlowGraph() {
		nodes = new HashMap<String, LocalInfoNode>();
		nodesInMethodMap = new HashMap<String, LocalInfoNode>();
		startNodes = new ArrayList<LocalInfoNode>();
		endNodes = new HashSet<LocalInfoNode>();
	}

	public Set<LocalInfoNode> endNodes() {
		return endNodes;
	}
	
	public void addEndNodes(Collection<LocalInfo> localInfos) {
		for (LocalInfo localInfo: localInfos) {
			String key = localInfo.key();
			if (nodes.containsKey(key)) {
				endNodes.add(nodes.get(key));
			}
		}
	}

	public List<LocalInfoNode> startNodes() {
		return startNodes;
	}

	public void addStartNodes(Collection<LocalInfo> localInfos) {
		for (LocalInfo localInfo: localInfos) {
			String key = localInfo.key();
			if (nodes.containsKey(key)) {
				startNodes.add(nodes.get(key));
			}
		}
	}

	public void addNode(LocalInfo localInfo) {
		String key = localInfo.key();
		if (nodes.containsKey(key)) {
			return;
		}

		LocalInfoNode node = new LocalInfoNode(localInfo);
		nodes.put(key, node);
		
//		SootMethod declaringMethod = localInfo.declaringMethod();
//		if (declaringMethod != null) {
//			nodesInMethodMap.put(declaringMethod.getSignature(), node);
//		}
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
