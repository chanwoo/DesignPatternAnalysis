package kr.ac.snu.selab.soot.analyzer;

import java.util.HashMap;
import java.util.HashSet;

import kr.ac.snu.selab.soot.MyUtil;

public class MyGraph {
	public HashMap<String, HashSet<MyNode>> sourceMap;
	public HashMap<String, HashSet<MyNode>> targetMap;
	
	public MyGraph () {
		sourceMap = new HashMap<String, HashSet<MyNode>>();
		targetMap = new HashMap<String, HashSet<MyNode>>();
	}
	
	public HashSet<MyNode> sourceNodes(MyNode aNode) {
		HashSet<MyNode> result = new HashSet<MyNode>();
		String key = aNode.toString();
		if (sourceMap.containsKey(key)) {
			result = sourceMap.get(key);
		}
		return result;
	}
	public HashSet<MyNode> targetNodes(MyNode aNode) {
		HashSet<MyNode> result = new HashSet<MyNode>();
		String key = aNode.toString();
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
			for (MyNode aNode : sourceMap.get(key)) {
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
			for (MyNode aNode : targetMap.get(key)) {
				result = result + aNode.toXML();
			}
			result = result + "</Values>";
		}
		result = result + "</TargetMap>";
		result = result + "</Graph>";
		return result;
	}

}
