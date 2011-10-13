package kr.ac.snu.selab.soot.analyzer;

import java.util.HashSet;
import java.util.Set;

import kr.ac.snu.selab.soot.graph.MyNode;
import soot.SootClass;

public class MethodAnalysisResult {
	SootClass abstractType;
	MyMethod self;
	Set<MyNode> sourceNodes;
	Set<MyNode> targetNodes;

	public MethodAnalysisResult() {
		sourceNodes = new HashSet<MyNode>();
		targetNodes = new HashSet<MyNode>();
	}
}
