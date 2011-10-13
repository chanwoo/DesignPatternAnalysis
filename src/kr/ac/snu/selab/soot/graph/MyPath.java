package kr.ac.snu.selab.soot.graph;

import java.util.ArrayList;

public class MyPath {
	public ArrayList<MyNode> nodeList;

	public MyPath() {
		nodeList = new ArrayList<MyNode>();
	}

	public MyPath copy() {
		MyPath p = new MyPath();
		p.nodeList.addAll(nodeList);
		return p;
	}

	public void add(MyNode aNode) {
		nodeList.add(aNode);
	}

	public void addTop(MyNode aNode) {
		nodeList.add(0, aNode);
	}

	public boolean contains(MyNode aNode) {
		return nodeList.contains(aNode);
	}

	public MyNode last() {
		return nodeList.get(nodeList.size() - 1);
	}

	public boolean isEmpty() {
		return nodeList.isEmpty();
	}

	public String toXML() {
		String result = "<Path>";
		for (MyNode aNode : nodeList) {
			result = result + aNode.toXML();
		}
		result = result + "</Path>";
		return result;
	}
}
