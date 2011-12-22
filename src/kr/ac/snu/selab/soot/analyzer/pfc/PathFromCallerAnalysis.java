package kr.ac.snu.selab.soot.analyzer.pfc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.ac.snu.selab.soot.analyzer.Analysis;
import kr.ac.snu.selab.soot.analyzer.AnalysisResult;
import kr.ac.snu.selab.soot.analyzer.MethodAnalysisResult;
import kr.ac.snu.selab.soot.analyzer.MyField;
import kr.ac.snu.selab.soot.analyzer.MyMethod;
import kr.ac.snu.selab.soot.graph.Graph;
import kr.ac.snu.selab.soot.graph.GraphPathCollector;
import kr.ac.snu.selab.soot.graph.MyNode;
import kr.ac.snu.selab.soot.graph.Path;
import kr.ac.snu.selab.soot.graph.collectors.AllPathCollector;
import soot.Hierarchy;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class PathFromCallerAnalysis extends Analysis {
	public PathFromCallerAnalysis(List<SootClass> aClassList,
			Hierarchy aHierarchy, boolean useSimpleCallGraph) {
		super(aClassList, aHierarchy, useSimpleCallGraph);
	}

	public AnalysisResult analyzeOverType(SootClass aType) {
		AnalysisResult anAnalysisResult = new PathFromCallerAnalysisResult();
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
			MyNode node = aResult.getSelf();
			if (node.isCaller()) {
				anAnalysisResult.addCaller(node);
			}
			if (node.isCreator()) {
				anAnalysisResult.addCreator(node);
			}
		}

		Graph<MyNode> referenceFlowGraph = getGraphFromMethodAnalysisResultList(methodAnalysisResultList);
		// graphXML = graphXML + referenceFlowGraph.toXML();

		for (MyNode callerNode : anAnalysisResult.getCallers()) {
			GraphPathCollector<MyNode> pathCollector = new AllPathCollector<MyNode>(
					callerNode, referenceFlowGraph);
			List<Path<MyNode>> pathList = pathCollector.run();

			// List<MyPath> pathIncludeStoreList = new ArrayList<MyPath>();
			// for (MyPath aPath : pathList) {
			// boolean doesPathIncludeStore = false;
			// for (MyNode aNode : aPath.nodeList) {
			// if (aNode.isStore()) {
			// doesPathIncludeStore = true;
			// break;
			// }
			// }
			// if (doesPathIncludeStore) {
			// pathIncludeStoreList.add(aPath);
			// }
			// }

			if (!pathList.isEmpty()) {
				anAnalysisResult.putReferenceFlowPath(callerNode.key(),
						pathList);
			}
		}

		// for (Entry<String, List<MyPath>> anEntry :
		// anAnalysisResult.referenceFlowPathMap
		// .entrySet()) {
		// for (MyPath aPath : anEntry.getValue()) {
		// MyNode creator = aPath.last();
		// TriggerPathCollector triggerPathCollector = new TriggerPathCollector(
		// creator, callGraph, aType, this);
		// List<MyPath> triggerPathList = triggerPathCollector.run();
		//
		// if (!triggerPathList.isEmpty()) {
		// anAnalysisResult.creatorTriggerPathMap.put(aPath,
		// triggerPathList);
		// }
		// }
		// }

		return anAnalysisResult;
	}
}
