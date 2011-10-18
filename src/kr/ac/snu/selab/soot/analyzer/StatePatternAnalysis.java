package kr.ac.snu.selab.soot.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import kr.ac.snu.selab.soot.graph.MyNode;
import kr.ac.snu.selab.soot.graphx.AllPathCollector;
import kr.ac.snu.selab.soot.graphx.Graph;
import kr.ac.snu.selab.soot.graphx.GraphPathCollector;
import kr.ac.snu.selab.soot.graphx.Path;
import kr.ac.snu.selab.soot.graphx.ReverseAllPathCollector;
import kr.ac.snu.selab.soot.graphx.TriggeringPathCollector;
import soot.Hierarchy;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class StatePatternAnalysis extends Analysis {
	public StatePatternAnalysis(List<SootClass> aClassList, Hierarchy aHierarchy) {
		super(aClassList, aHierarchy);
	}

	public AnalysisResult analyzeOverType(SootClass aType) {
		AnalysisResult anAnalysisResult = new StatePatternAnalysisResult();
		List<MethodAnalysisResult> methodAnalysisResultList = new ArrayList<MethodAnalysisResult>();
		HashMap<String, MyNode> nodeMap = new HashMap<String, MyNode>();

		for (SootClass aClass : classList) {
			for (SootField aField : aClass.getFields()) {
				// We only consider fields of the abstract type.
				if (aField.getType().toString().equals(aType.toString())) {
					nodeMap.put(aField.toString(), new MyField(aField));
				}
			}
			for (SootMethod aMethod : aClass.getMethods()) {
				nodeMap.put(aMethod.toString(), new MyMethod(aMethod));
			}
		}

		anAnalysisResult.setAbstractType(aType);
		for (SootClass aClass : classList) {
			for (SootMethod aMethod : aClass.getMethods()) {
				methodAnalysisResultList.add(analyzeMethodOverType(aMethod,
						aType, nodeMap));
			}
		}

		for (MethodAnalysisResult aResult : methodAnalysisResultList) {
			MyNode node = aResult.self;
			if (node.isCaller()) {
				anAnalysisResult.callerList.add(node);
			}
			if (node.isCreator()) {
				anAnalysisResult.creatorList.add(node);
			}
		}

		Graph<MyNode> referenceFlowGraph = getGraphFromMethodAnalysisResultList(methodAnalysisResultList);
		Logger logger = Logger.getLogger(StatePatternAnalysis.class);
		logger.debug(referenceFlowGraph.toXML());
		
		for (MyNode callerNode : anAnalysisResult.callerList) {
			GraphPathCollector<MyNode> pathCollector = new AllPathCollector<MyNode>(
					callerNode, referenceFlowGraph);

			List<Path<MyNode>> pathList = pathCollector.run();

			if (!pathList.isEmpty()) {
				anAnalysisResult.referenceFlowPathMap.put(
						callerNode.toString(), pathList);
			}
		}
		// Check whether a call chain from caller meets an object flow graph to
		// the caller
		for (MyNode callerNode : anAnalysisResult.callerList) {
			String callerKey = callerNode.toString();
			if (!anAnalysisResult.referenceFlowPathMap.containsKey(callerKey))
				continue;

			Set<MyNode> destinationSet = new HashSet<MyNode>();
			List<Path<MyNode>> referenceFlowPathList = anAnalysisResult.referenceFlowPathMap
					.get(callerKey);
			for (Path<MyNode> aPath : referenceFlowPathList) {
				destinationSet.addAll(aPath.nodeList);
			}
			destinationSet.remove(callerNode);

			GraphPathCollector<MyNode> pathCollector = new ReverseAllPathCollector(
					callerNode, callGraph, destinationSet);
//			GraphPathCollector<MyNode> pathCollector = new TriggeringPathCollector(
//					callerNode, callGraph, destinationSet);
			List<Path<MyNode>> pathList = pathCollector.run();
			Set<Path<MyNode>> pathSet = new HashSet<Path<MyNode>>();
			pathSet.addAll(pathList);

			if (!pathList.isEmpty()) {
				((StatePatternAnalysisResult) anAnalysisResult).triggeringPathMap
						.put(callerKey, pathSet);
			}
		}

		return anAnalysisResult;
	}

	public List<AnalysisResult> analyzeOverAllAbstractTypes() {
		List<AnalysisResult> analysisResultList = new ArrayList<AnalysisResult>();
		List<SootClass> abstractTypeList = getAbstractTypeClassList();

		// String graphXML = "<GraphList>";

		for (SootClass aType : abstractTypeList) {
			if (!aType.toString().equals("org.jhotdraw.framework.Figure")) {
				analysisResultList.add(analyzeOverType(aType));
			}
		}

		// graphXML = graphXML + "</GraphList>";
		// MyUtil.stringToFile(graphXML,
		// "/Users/chanwoo/Documents/workspace/StatePatternExample2/output/StatePatternExample2_ReferenceFlowGraph.xml");

		return analysisResultList;
	}
}
