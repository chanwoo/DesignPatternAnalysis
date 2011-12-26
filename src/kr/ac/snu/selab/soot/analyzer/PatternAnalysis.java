package kr.ac.snu.selab.soot.analyzer;

import java.util.Map;

import soot.Hierarchy;
import soot.SootClass;
import soot.jimple.toolkits.callgraph.CallGraph;

public class PatternAnalysis {
	
	public PatternAnalysisResult perform(Map<String, SootClass> classMap, Hierarchy hierarchy, CallGraph cg, AnalysisUtil au) {
		PatternAnalysisResult result = new PatternAnalysisResult();
		
		return result;
	}

}
