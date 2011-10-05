package kr.ac.snu.selab.soot.analyzer;

import java.util.HashMap;
import java.util.HashSet;

public class MyGraph {
	public HashMap<String, HashSet<MyNode>> sourceMap;
	public HashMap<String, HashSet<MyNode>> targetMap;
	
	public MyGraph () {
		sourceMap = new HashMap<String, HashSet<MyNode>>();
		targetMap = new HashMap<String, HashSet<MyNode>>();
	}
	
	public HashSet<MyNode> sourceNodes(MyNode aNode) {
		return sourceMap.get(aNode.toString());
	}
	public HashSet<MyNode> targetNodes(MyNode aNode) {
		return targetMap.get(aNode.toString());
	}

}
