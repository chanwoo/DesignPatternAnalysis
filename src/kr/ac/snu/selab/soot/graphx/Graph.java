package kr.ac.snu.selab.soot.graphx;

import java.util.HashMap;
import java.util.HashSet;

import kr.ac.snu.selab.soot.util.MyUtil;

public class Graph<N extends Node> {
	public HashMap<String, HashSet<N>> sourceMap;
	public HashMap<String, HashSet<N>> targetMap;

	public Graph() {
		sourceMap = new HashMap<String, HashSet<N>>();
		targetMap = new HashMap<String, HashSet<N>>();
	}

	public HashSet<N> sourceNodes(N aNode) {
		HashSet<N> result = new HashSet<N>();
		String key = aNode.key();
		if (sourceMap.containsKey(key)) {
			result = sourceMap.get(key);
		}
		return result;
	}

	public HashSet<N> targetNodes(N aNode) {
		HashSet<N> result = new HashSet<N>();
		String key = aNode.key();
		if (targetMap.containsKey(key)) {
			result = targetMap.get(key);
		}
		return result;
	}

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
