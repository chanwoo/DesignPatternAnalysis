package kr.ac.snu.selab.soot.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import soot.Unit;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInvokeStmt;

public class StatePatternAnalysis extends Analysis {

	private static Logger logger = Logger.getLogger(StatePatternAnalysis.class);

	public StatePatternAnalysis(List<SootClass> aClassList, Hierarchy aHierarchy) {
		super(aClassList, aHierarchy);
	}

	// private boolean isCreatorMethodOfType(SootMethod aMethod, SootClass
	// aType) {
	// SootClass receiverType = aMethod.getDeclaringClass();
	// if (aMethod.getName().equals("<init>")
	// && isClassOfSubType(receiverType, aType)) {
	// return true;
	// } else {
	// return false;
	// }
	// }

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

		for (MyNode callerNode : anAnalysisResult.callerList) {
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
				anAnalysisResult.referenceFlowPathMap.put(
						callerNode.toString(), pathIncludeStoreList);
			}
		}
		// Check whether a call chain from caller meets an object flow graph to
		// the caller
		for (MyNode callerNode : anAnalysisResult.callerList) {
			String callerKey = callerNode.toString();
			if (!anAnalysisResult.referenceFlowPathMap.containsKey(callerKey))
				continue;

			Set<MyNode> allNodeOfPathSet = new HashSet<MyNode>();
			List<Path<MyNode>> referenceFlowPathList = anAnalysisResult.referenceFlowPathMap
					.get(callerKey);
			for (Path<MyNode> aPath : referenceFlowPathList) {
				allNodeOfPathSet.addAll(aPath.nodeList);
			}

			// Only collecting injectors
			allNodeOfPathSet.remove(callerNode);

			Set<MyNode> injectorSet = new HashSet<MyNode>();
			MyField storeNearestFromCaller = null;
			for (MyNode aNode : allNodeOfPathSet) {
				if (aNode instanceof MyField) {
					storeNearestFromCaller = (MyField) aNode;
					break;
				}
			}

			if (storeNearestFromCaller != null) {
				for (MyNode aNode : allNodeOfPathSet) {
					if (aNode instanceof MyField) {
						continue;
					}

					SootMethod injectorCandidateMethod = null;
					if (aNode instanceof MyMethod) {
						injectorCandidateMethod = (SootMethod) ((MyMethod) aNode)
								.getElement();
					}
					if (injectorCandidateMethod != null) {
						if (isInjectorMethodOfField(injectorCandidateMethod,
								(SootField) (storeNearestFromCaller
										.getElement()))) {
							injectorSet.add(aNode);
						}
					}
				}
			}

			Set<MyNode> startNodeSet = new HashSet<MyNode>();
			for (Unit aUnit : ((MyMethod) callerNode).getCallStatementList()) {
				Set<SootMethod> invokedMethodSet = new HashSet<SootMethod>();
				if (aUnit instanceof JInvokeStmt) {
					SootMethod invokedMethod = ((JInvokeStmt) aUnit)
							.getInvokeExpr().getMethod();
					invokedMethodSet.add(invokedMethod);
					// If there is a call to abstract type class, its
					// subclass' methods should be added to a call
					// graph.
					if (!invokedMethod.getName().equals("<init>")) {
						List<SootMethod> overrideMethodList = getOverrideMethodsOf(invokedMethod);
						for (SootMethod overrideMethod : overrideMethodList) {
							invokedMethodSet.add(overrideMethod);
						}
					}

				} else if (aUnit instanceof JAssignStmt) {
					if (((JAssignStmt) aUnit).containsInvokeExpr()) {
						SootMethod invokedMethod = ((JAssignStmt) aUnit)
								.getInvokeExpr().getMethod();
						invokedMethodSet.add(invokedMethod);
						// If there is a call to abstract type class, its
						// subclass' methods should be added to a call
						// graph.
						if (!invokedMethod.getName().equals("<init>")) {
							List<SootMethod> overrideMethodList = getOverrideMethodsOf(invokedMethod);
							for (SootMethod overrideMethod : overrideMethodList) {
								invokedMethodSet.add(overrideMethod);
							}
						}
					}
				}

				if (!invokedMethodSet.isEmpty()) {
					for (SootMethod invokedMethod : invokedMethodSet) {
						startNodeSet.add(nodeMap.get(invokedMethod.toString()));
					}
				}
			}

			Set<Path<MyNode>> pathSet = new HashSet<Path<MyNode>>();
			for (MyNode aStartNode : startNodeSet) {
				if (!injectorSet.isEmpty()) {
					GraphPathCollector<MyNode> pathCollector = new TriggeringPathCollector(
							aStartNode, callGraph, injectorSet);
					List<Path<MyNode>> pathList = pathCollector.run();

					pathSet.addAll(pathList);
				}
			}

			if (!pathSet.isEmpty()) {
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
			// String outputPath = MyUtil.getPath(outputDirectory, fileName
			// + ".xml");
			// MyUtil.stringToFile(anAnalysisResult.toXML(), outputPath);

			String outputPath1 = MyUtil.getPath(outputDirectory, fileName
					+ ".xml");
			XMLWriter writer = new XMLWriter();
			writer.open(outputPath1);
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
