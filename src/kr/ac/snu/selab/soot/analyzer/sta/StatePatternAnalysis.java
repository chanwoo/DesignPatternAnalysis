package kr.ac.snu.selab.soot.analyzer.sta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import kr.ac.snu.selab.soot.util.MyUtil;
import kr.ac.snu.selab.soot.util.XMLWriter;

import org.apache.log4j.Logger;

import soot.Hierarchy;
import soot.Local;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInvokeStmt;

public class StatePatternAnalysis extends Analysis {

	private static Logger logger = Logger.getLogger(StatePatternAnalysis.class);

	public StatePatternAnalysis(List<SootClass> aClassList, Hierarchy aHierarchy, boolean useSimpleCallGraph) {
		super(aClassList, aHierarchy, useSimpleCallGraph);
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

	public Map<String, String> getWrittenFieldInformationOfThisStatement(
			Unit aUnit, SootClass aType) {
		Map<String, String> result = new HashMap<String, String>();
		if (aUnit instanceof JAssignStmt) {
			JAssignStmt assignStmt = (JAssignStmt) aUnit;
			Value rightOp = assignStmt.getRightOp();
			SootClass rightOpType = null;
			String rightOpTypeKey = rightOp.getType().toString();
			if (!(rightOpTypeKey.startsWith("null"))) {
				if (classMap.containsKey(rightOpTypeKey)) {
					rightOpType = classMap.get(rightOpTypeKey);

					if ((rightOpType != null)
							&& isClassOfSubType(rightOpType, aType)) {
						String leftSideString = assignStmt.getLeftOp()
								.toString();
						if (leftSideString.startsWith("this.")) {
							leftSideString = leftSideString.substring(5);
						}
						// Only field variable
						if (leftSideString.startsWith("<")) {
							result.put(leftSideString, rightOp.toString());
						}
					}
				}
			}
		}
		return result;
	}

	public List<String> getWrittenFieldStringListByThisMethod(
			SootMethod aMethod, SootClass aType) {
		List<String> fieldStringList = new ArrayList<String>();
		Map<String, String> assignmentMap = new HashMap<String, String>();

		for (Unit aUnit : getUnits(aMethod)) {
			if (aUnit instanceof JAssignStmt) {
				assignmentMap.put(((JAssignStmt) aUnit).getLeftOp().toString(),
						((JAssignStmt) aUnit).getRightOp().toString());
			}
		}

		for (Unit aUnit : getUnits(aMethod)) {
			Map<String, String> writtenFieldStringInformation = getWrittenFieldInformationOfThisStatement(
					aUnit, aType);
			if (!writtenFieldStringInformation.isEmpty()) {
				String writtenValue = writtenFieldStringInformation.values()
						.iterator().next();
				if (assignmentMap.containsKey(writtenValue)) {
					if (!assignmentMap.get(writtenValue).equals("null")) {
						fieldStringList.add(writtenFieldStringInformation
								.keySet().iterator().next());
					}
				} else if (writtenValue != "null") {
					fieldStringList.add(writtenFieldStringInformation.keySet()
							.iterator().next());
				}
			}
		}
		return fieldStringList;
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
				sampleMethodToSparkUsage(aMethod);
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

		for (MyNode callerNode : anAnalysisResult.getCallers()) {
			GraphPathCollector<MyNode> pathCollector = new AllPathCollector<MyNode>(
					callerNode, referenceFlowGraph);

			List<Path<MyNode>> pathList = pathCollector.run();

			List<Path<MyNode>> pathIncludeStoreList = new ArrayList<Path<MyNode>>();
			for (Path<MyNode> aPath : pathList) {
				boolean doesPathIncludeStore = false;
				for (MyNode aNode : aPath.getNodeList()) {
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
				if (pathIncludeStoreList.size() > 10) {
					anAnalysisResult.putReferenceFlowPath(callerNode.key(),
							pathIncludeStoreList.subList(0, 9));
				} else {
					anAnalysisResult.putReferenceFlowPath(callerNode.key(),
							pathIncludeStoreList);
				}
			}
		}

		// Identify Layers
		for (MyNode callerNode : anAnalysisResult.getCallers()) {
			String callerKey = callerNode.toString();
			if (!anAnalysisResult.containsReferenceFlowPath(callerKey))
				continue;

			Iterable<Path<MyNode>> referenceFlowPathList = anAnalysisResult
					.getReferenceFlowPaths(callerKey);
			for (Path<MyNode> aPath : referenceFlowPathList) {
				Set<MyNode> tempSet = new HashSet<MyNode>();
				for (MyNode aNode : aPath.getNodeList()) {
					if (!aNode.isStore()) {
						tempSet.add(aNode);
					} else {
						if (!tempSet.isEmpty()) {
							((StatePatternAnalysisResult) anAnalysisResult)
									.putReusableNodeSet(tempSet);
							tempSet.clear();
							((StatePatternAnalysisResult) anAnalysisResult)
									.putReusableNode(aNode);
						}
					}
				}
				if (!tempSet.isEmpty()) {
					((StatePatternAnalysisResult) anAnalysisResult)
							.putExntensionNodeSet(tempSet);
				}
			}
		}

		// Check whether a call chain from caller meets an object flow graph to
		// the caller
		for (MyNode callerNode : anAnalysisResult.getCallers()) {
			String callerKey = callerNode.toString();
			if (!anAnalysisResult.containsReferenceFlowPath(callerKey))
				continue;

			// Only collecting injectors
			Set<MyNode> injectorSet = new HashSet<MyNode>();
			Iterable<Path<MyNode>> referenceFlowPathList = anAnalysisResult
					.getReferenceFlowPaths(callerKey);
			for (Path<MyNode> aPath : referenceFlowPathList) {
				boolean isNextNodeInjector = false;
				for (MyNode aNode : aPath.getNodeList()) {
					if ((isNextNodeInjector == true)
							&& !(((SootMethod) (aNode.getElement())).getName()
									.equals("<init>"))) {
						// aNode.setIsInjector(true);
						injectorSet.add(aNode);
						break;
					} else if ((isNextNodeInjector == true)
							&& (((SootMethod) (aNode.getElement())).getName()
									.equals("<init>"))) {
						break;
					} else if (aNode instanceof MyField) {
						isNextNodeInjector = true;
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
					if (!((JInvokeStmt) aUnit).getInvokeExpr().toString()
							.startsWith("specialinvoke")) {
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
						if (!((JAssignStmt) aUnit).getInvokeExpr().toString()
								.startsWith("specialinvoke")) {
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
					if (pathList.size() > 10) {
						pathSet.addAll(pathList.subList(0, 9));
					} else {
						pathSet.addAll(pathList);
					}
				}
			}

			if (!pathSet.isEmpty()) {
				((StatePatternAnalysisResult) anAnalysisResult)
						.putTriggeringPath(callerKey, pathSet);
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

			if (((StatePatternAnalysisResult) anAnalysisResult)
					.isTriggeringPathMapEmpty())
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

	private class TriggeringPathCollector
			extends
			kr.ac.snu.selab.soot.graph.collectors.TriggeringPathCollector<MyNode> {
		public TriggeringPathCollector(MyNode aStartNode, Graph<MyNode> aGraph,
				Set<MyNode> aDestinationSet) {
			super(aStartNode, aGraph, aDestinationSet);
		}

	}

	// XXX: Remove It !!!
	private void sampleMethodToSparkUsage(SootMethod aMethod) {
		logger.debug("###### Method : " + aMethod.toString() + "#######");

		PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
		
		List<Unit> stmts = getUnits(aMethod);

		List<Local> valueList = new ArrayList<Local>();

		for (Unit stmt : stmts) {
			if (stmt instanceof JAssignStmt) {
				Value v = ((JAssignStmt) stmt).leftBox.getValue();
				if (v instanceof Local) {
					valueList.add((Local) v);
				}
			}
		}

		for (int i = 0; i < valueList.size(); i++) {
			for (int j = 0; j < valueList.size(); j++) {
				if (i == j)
					continue;

				PointsToSet ros1 = pta.reachingObjects(valueList.get(i));
				PointsToSet ros2 = pta.reachingObjects(valueList.get(j));
				boolean hasIntersection = ros1.hasNonEmptyIntersection(ros2);

				logger.debug("value1: " + valueList.get(i));
				logger.debug("value2: " + valueList.get(j));
				logger.debug("hasIntersection?: " + hasIntersection);

				// sampleMethodAnalyze(valueList.get(i), ros1);
				// sampleMethodAnalyze(valueList.get(j), ros2);
			}
		}

		logger.debug("#######################################");
	}

	// XXX: Remove It !!!
	private void sampleMethodAnalyze(Object src, PointsToSet ros) {
		logger.debug("**** From: " + src.toString() + " -> ");
		Set<Type> types = ros.possibleTypes();
		logger.debug("Type size: " + types.size());
		for (Type t : types) {
			logger.debug("Type: " + t.toString());
		}
		logger.debug("**********");
	}
}