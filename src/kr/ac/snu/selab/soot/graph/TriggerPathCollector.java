//package kr.ac.snu.selab.soot.graph;
//
//import kr.ac.snu.selab.soot.analyzer.Analysis;
//import kr.ac.snu.selab.soot.analyzer.MyMethod;
//import soot.SootClass;
//
//public class TriggerPathCollector extends MyGraphPathCollector {
//	
//	private SootClass abstractType;
//	private Analysis analysis;
//	
//	public TriggerPathCollector(MyNode aStartNode, MyGraph aGraph, SootClass anAbstractType, Analysis anAnalysis) {
//		super(aStartNode, aGraph);
//		abstractType = anAbstractType;
//		analysis = anAnalysis;
//	}
//
//	@Override
//	protected boolean isGoal(MyNode aNode) {
//		// TODO Auto-generated method stub
//		boolean result = false;
//		result = (analysis.isClassOfSubType(((MyMethod)aNode).getMethod().getDeclaringClass(), abstractType));
//		return result;
//	}
//
//}
