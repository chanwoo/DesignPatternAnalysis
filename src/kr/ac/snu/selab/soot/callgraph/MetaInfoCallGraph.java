package kr.ac.snu.selab.soot.callgraph;

import java.util.Collection;
import java.util.HashMap;

import kr.ac.snu.selab.soot.graph.Graph;
import kr.ac.snu.selab.soot.graph.MetaInfo;

public class MetaInfoCallGraph implements Graph<MetaInfo> {
	private HashMap<String, MetaInfo> nodes;
	
	public MetaInfoCallGraph() {
		nodes = new HashMap<String, MetaInfo>();
	}
	
	public void addNode(MetaInfo metaInfo) {
		String key = metaInfo.key();
		if (nodes.containsKey(key)) {
			return;
		}
		
		nodes.put(key, metaInfo);
	}
	
	public void addEdge(MetaInfo from, MetaInfo to) {
		String fromKey = from.key();
		String toKey = to.key();
		MetaInfo sourceNode = null, targetNode = null;

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
	
	public MetaInfo find(MetaInfo metaInfo) {
		String key = metaInfo.key();
		return nodes.get(key);
	}

	@Override
	public Collection<MetaInfo> sourceNodes(MetaInfo aNode) {
		return aNode.getSources();
	}

	@Override
	public Collection<MetaInfo> targetNodes(MetaInfo aNode) {
		return aNode.getTargets();
	}
}
