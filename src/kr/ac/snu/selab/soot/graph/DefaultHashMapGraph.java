package kr.ac.snu.selab.soot.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import kr.ac.snu.selab.soot.util.MyUtil;

public class DefaultHashMapGraph<N extends Node> implements Graph<N> {
	protected HashMap<String, HashSet<N>> sourceMap;
	protected HashMap<String, HashSet<N>> targetMap;

	public DefaultHashMapGraph() {
		sourceMap = new HashMap<String, HashSet<N>>();
		targetMap = new HashMap<String, HashSet<N>>();
	}

	public HashMap<String, HashSet<N>> getSourceMap() {
		return sourceMap;
	}

	public HashMap<String, HashSet<N>> getTargetMap() {
		return targetMap;
	}

	@Override
	public Collection<N> sourceNodes(N aNode) {
		HashSet<N> result = new HashSet<N>();
		String key = aNode.key();
		if (sourceMap.containsKey(key)) {
			result = sourceMap.get(key);
		}
		return result;
	}

	@Override
	public Collection<N> targetNodes(N aNode) {
		HashSet<N> result = new HashSet<N>();
		String key = aNode.key();
		if (targetMap.containsKey(key)) {
			result = targetMap.get(key);
		}
		return result;
	}

	@Override
	public String toXML() {
		String result = "";
		result = result + "<Graph>";
		result = result + "<SourceMap>";
		for (String key : sourceMap.keySet()) {
			result = result + "<Key>";
			result = result + MyUtil.removeBracket(key);
			result = result + "</Key>";
			result = result + "<Values>";
			for (N aNode : sourceMap.get(key)) {
				result = result + aNode.toXML();
			}
			result = result + "</Values>";
		}
		result = result + "</SourceMap>";
		result = result + "<TargetMap>";
		for (String key : targetMap.keySet()) {
			result = result + "<Key>";
			result = result + MyUtil.removeBracket(key);
			result = result + "</Key>";
			result = result + "<Values>";
			for (N aNode : targetMap.get(key)) {
				result = result + aNode.toXML();
			}
			result = result + "</Values>";
		}
		result = result + "</TargetMap>";
		result = result + "</Graph>";
		return result;
	}

}
