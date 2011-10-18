package kr.ac.snu.selab.soot.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.ac.snu.selab.soot.graph.MyNode;
import kr.ac.snu.selab.soot.graphx.Graph;
import kr.ac.snu.selab.soot.graphx.GraphPathCollector;
import kr.ac.snu.selab.soot.graphx.Path;
import soot.Hierarchy;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class PathFromCallerAnalysis extends Analysis {
	public PathFromCallerAnalysis(List<SootClass> aClassList,
			Hierarchy aHierarchy) {
		super(aClassList, aHierarchy);
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
			MyNode node = aResult.self;
			if (node.isCaller()) {
				anAnalysisResult.callerList.add(node);
			}
			if (node.isCreator()) {
				anAnalysisResult.creatorList.add(node);
			}
		}

		Graph<MyNode> referenceFlowGraph = getGraphFromMethodAnalysisResultList(methodAnalysisResultList);
		// graphXML = graphXML + referenceFlowGraph.toXML();

		for (MyNode callerNode : anAnalysisResult.callerList) {
			GraphPathCollector<MyNode> pathCollector = new GraphPathCollector<MyNode>(
					callerNode, referenceFlowGraph) {
				@Override
				protected boolean isGoal(MyNode aNode) {
					boolean result = false;
					if (graph.sourceNodes(aNode).isEmpty()
							|| hitSet.contains(aNode.toString())) {
						result = true;
					}
					// result = aNode.isCreator(); // &&
					// (graph.sourceNodes(aNode)).isEmpty();
					return result;
				}
			};
			List<Path<MyNode>> pathList = pathCollector.run();

//			List<MyPath> pathIncludeStoreList = new ArrayList<MyPath>();
//			for (MyPath aPath : pathList) {
//				boolean doesPathIncludeStore = false;
//				for (MyNode aNode : aPath.nodeList) {
//					if (aNode.isStore()) {
//						doesPathIncludeStore = true;
//						break;
//					}
//				}
//				if (doesPathIncludeStore) {
//					pathIncludeStoreList.add(aPath);
//				}
//			}

			if (!pathList.isEmpty()) {
				anAnalysisResult.referenceFlowPathMap.put(
						callerNode.toString(), pathList);
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
