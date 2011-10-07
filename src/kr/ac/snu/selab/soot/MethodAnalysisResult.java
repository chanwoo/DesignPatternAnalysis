package kr.ac.snu.selab.soot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.SootClass;
import soot.Unit;

import kr.ac.snu.selab.soot.analyzer.MyMethod;
import kr.ac.snu.selab.soot.analyzer.MyNode;

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
