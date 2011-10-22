package kr.ac.snu.selab.soot.analyzer;

import java.util.HashSet;
import java.util.Set;

import kr.ac.snu.selab.soot.graph.MyNode;
import soot.SootClass;

public class MethodAnalysisResult {
	private SootClass abstractType;

	private MyMethod self;
	private Set<MyNode> sourceNodes;
	private Set<MyNode> targetNodes;

	public MethodAnalysisResult() {
		sourceNodes = new HashSet<MyNode>();
		targetNodes = new HashSet<MyNode>();
	}

	public MyMethod getSelf() {
		return self;
	}

	void setAbstractType(SootClass t) {
		this.abstractType = t;
	}

	void setMethod(MyMethod method) {
		this.self = method;
	}

	MyMethod getMethod() {
		return self;
	}

	Iterable<MyNode> getSourceNodes() {
		return sourceNodes;
	}

	void addSourceNode(MyNode node) {
		sourceNodes.add(node);
	}

	Iterable<MyNode> getTargetNodes() {
		return targetNodes;
	}

	void addTargetNode(MyNode node) {
		targetNodes.add(node);
	}
}
