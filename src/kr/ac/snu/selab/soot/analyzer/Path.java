package kr.ac.snu.selab.soot.analyzer;

import java.util.ArrayList;

import soot.SootMethod;

public class Path {
	public ArrayList<MyNode> nodeList;

	public Path() {
		nodeList = new ArrayList<MyNode>();
	}

	public Path copy() {
		Path p = new Path();
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
