package kr.ac.snu.selab.soot.analyzer.sta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kr.ac.snu.selab.soot.analyzer.Analysis;
import kr.ac.snu.selab.soot.analyzer.AnalysisResult;
import kr.ac.snu.selab.soot.analyzer.MethodAnalysisResult;
import kr.ac.snu.selab.soot.analyzer.MyField;
import kr.ac.snu.selab.soot.analyzer.MyMethod;
import kr.ac.snu.selab.soot.graph.AllPathCollector;
import kr.ac.snu.selab.soot.graph.Graph;
import kr.ac.snu.selab.soot.graph.GraphPathCollector;
import kr.ac.snu.selab.soot.graph.HitPathCollector;
import kr.ac.snu.selab.soot.graph.MyNode;
import kr.ac.snu.selab.soot.graph.Path;
import kr.ac.snu.selab.soot.util.MyUtil;
import kr.ac.snu.selab.soot.util.XMLWriter;

import org.apache.log4j.Logger;

import soot.Hierarchy;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class StatePatternAnalysis extends Analysis {

	private static Logger logger = Logger.getLogger(StatePatternAnalysis.class);

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
			MyNode node = aResult.getSelf();
			if (node.isCaller()) {
				anAnalysisResult.getCallerList().add(node);
			}
			if (node.isCreator()) {
				anAnalysisResult.getCreatorList().add(node);
			}
		}

		Graph<MyNode> referenceFlowGraph = getGraphFromMethodAnalysisResultList(methodAnalysisResultList);

		for (MyNode callerNode : anAnalysisResult.getCallerList()) {
			GraphPathCollector<MyNode> pathCollector = new AllPathCollector<MyNode>(
					callerNode, referenceFlowGraph);

			List<Path<MyNode>> pathList = pathCollector.run();

			List<Path<MyNode>> pathIncludeStoreList = new ArrayList<Path<MyNode>>();
			for (Path<MyNode> aPath : pathList) {
				boolean doesPathIncludeStore = false;
				for (MyNode aNode : aPath.nodeList) {
					if (aNode.isStore()) {
						doesPathIncludeStore = true;
						break;
					}
				}
				if (doesPathIncludeStore) {
					pathIncludeStoreList.add(aPath);
				}
			}

			if (!pathIncludeStoreList.isEmpty()) {
				anAnalysisResult.getReferenceFlowPathMap().put(
						callerNode.toString(), pathIncludeStoreList);
			}
		}
		// Check whether a call chain from caller meets an object flow graph to
		// the caller
		for (MyNode callerNode : anAnalysisResult.getCallerList()) {
			String callerKey = callerNode.toString();
			if (!anAnalysisResult.getReferenceFlowPathMap().containsKey(
					callerKey))
				continue;

			Set<MyNode> destinationSet = new HashSet<MyNode>();
			List<Path<MyNode>> referenceFlowPathList = anAnalysisResult
					.getReferenceFlowPathMap().get(callerKey);
			for (Path<MyNode> aPath : referenceFlowPathList) {
				destinationSet.addAll(aPath.nodeList);
			}
			destinationSet.remove(callerNode);

			GraphPathCollector<MyNode> pathCollector = new TriggeringPathCollector(
					callerNode, callGraph, destinationSet);
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

	public void writeAnalysisResultOverAllAbstractTypes(String outputDirectory) {
		List<SootClass> abstractTypeList = getAbstractTypeClassList();

		// String graphXML = "<GraphList>";

		for (SootClass aType : abstractTypeList) {
			StatePatternAnalysisResult anAnalysisResult = null;
			anAnalysisResult = (StatePatternAnalysisResult) (analyzeOverType(aType));
			if (anAnalysisResult == null)
				continue;

			if (((StatePatternAnalysisResult) anAnalysisResult).triggeringPathMap
					.isEmpty())
				continue;

			logger.debug("Writing output....");
			String fileName = "StatePatternAnalysis_"
					+ anAnalysisResult.getAbstractTypeName();

			String outputPath = MyUtil.getPath(outputDirectory, fileName
					+ ".xml");
			XMLWriter writer = new XMLWriter(outputPath);
			anAnalysisResult.writeXML(writer);
			writer.close();

			logger.debug("Writing output finished....");
		}

		// graphXML = graphXML + "</GraphList>";
		// MyUtil.stringToFile(graphXML,
		// "/Users/chanwoo/Documents/workspace/StatePatternExample2/output/StatePatternExample2_ReferenceFlowGraph.xml");

	}

	private class TriggeringPathCollector extends HitPathCollector<MyNode> {
		public TriggeringPathCollector(MyNode aStartNode, Graph<MyNode> aGraph,
				Set<MyNode> aDestinationSet) {
			super(aStartNode, aGraph, aDestinationSet);
		}
	}
}
