package kr.ac.snu.selab.soot.analyzer.decNcor;

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

public class DecNCorAnalysis extends Analysis {
	public DecNCorAnalysis(List<SootClass> aClassList, Hierarchy aHierarchy,
			boolean useSimpleCallGraph) {
		super(aClassList, aHierarchy, useSimpleCallGraph);
	}

	private static Logger logger = Logger.getLogger(DecNCorAnalysis.class);

	protected boolean isAbstractTypeClass(SootClass aClass) {
		boolean result = false;
		if (aClass.isInterface() || aClass.isAbstract()) {
			result = true;
		} else {
			if (!hierarchy.getDirectSubclassesOf(aClass).isEmpty()) {
				result = true;
			}
		}
		return result;
	}

	public AnalysisResult analyzeOverType(SootClass aType) {
		AnalysisResult anAnalysisResult = new DecNCorAnalysisResult();
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

		// List<MyNode> creatorList = new ArrayList<MyNode>();
		// // for (MyNode aNode : aPath.getNodeList()) {
		// // if (aNode.isCreator()) {
		// // creatorList.add(aNode);
		// // }
		// // }
		// for (MyNode aNode : anAnalysisResult.getCreators()) {
		// creatorList.add(aNode);
		// }

		// check whether the caller's call is recursive
		for (MyNode callerNode : anAnalysisResult.getCallers()) {
			SootMethod callerMethod = (SootMethod) (callerNode.getElement());
			SootClass callerClass = callerMethod.getDeclaringClass();
			for (Unit aUnit : ((MyMethod) callerNode).getCallStatementList()) {
				SootMethod calledMethod = null;
				if (aUnit instanceof JInvokeStmt) {
					calledMethod = ((JInvokeStmt) aUnit).getInvokeExpr()
							.getMethod();
				} else if (aUnit instanceof JAssignStmt) {
					calledMethod = ((JAssignStmt) aUnit).getInvokeExpr()
							.getMethod();
				}
				if (callerMethod.getName().equals(calledMethod.getName())) {
					// if the result has recursive calls

					// check whether the store's type is superclass of the
					// store's class
					if (!anAnalysisResult.containsReferenceFlowPath(callerNode
							.key()))
						continue;

					for (Path<MyNode> aPath : anAnalysisResult
							.getReferenceFlowPaths(callerNode.key())) {

						for (MyNode aNode : aPath.getNodeList()) {
							if (aNode.isStore()) {
								SootField storeField = (SootField) (((MyField) aNode)
										.getElement());
								SootClass storeClass = storeField
										.getDeclaringClass();
								SootClass storeType = classMap.get(storeField
										.getType().toString());
								if (storeClass.equals(callerClass)) {
									if (isClassOfDirectSubTypeExcluding(
											storeClass, storeType)) {
										if (getNumberOfDirectSubClasses(storeType) >= 2) {
											if (getNumberOfDirectSubClasses(storeClass) >= 2) {
												// check if any concrete
												// decorator has
												// been created
												// boolean
												// anyCreatedClassOfStoreClass =
												// false;
												//
												// for (MyNode creatorNode :
												// creatorList) {
												// List<Unit>
												// createStatementList =
												// ((MyMethod) creatorNode)
												// .getCreateStatementList();
												// if
												// (anyCreatedClassSubTypeExcluding(
												// createStatementList,
												// storeClass)) {
												// anyCreatedClassOfStoreClass =
												// true;
												// break;
												// }
												// }
												// if
												// (anyCreatedClassOfStoreClass)
												// {
												((DecNCorAnalysisResult) anAnalysisResult)
														.setIsDecorator(true);
												((DecNCorAnalysisResult) anAnalysisResult)
														.setStoreClassName(storeClass
																.toString());
												// }
											}
										}
									} else if (storeClass.equals(storeType)) {
										((DecNCorAnalysisResult) anAnalysisResult)
												.setIsCor(true);
										((DecNCorAnalysisResult) anAnalysisResult)
												.setStoreClassName(storeClass
														.toString());
									}
								}
							}
						}
					}
				}
			}

		}

		return anAnalysisResult;
	}

	private int getNumberOfDirectSubClasses(SootClass aClass) {
		if (aClass.isInterface()) {
			return hierarchy.getDirectImplementersOf(aClass).size();
		} else {
			return hierarchy.getDirectSubclassesOf(aClass).size();
		}
	}

	@SuppressWarnings("unchecked")
	private boolean isClassOfDirectSubTypeExcluding(SootClass aClass,
			SootClass aType) {
		boolean result = false;
		if (aType.isInterface() && !(aClass.isInterface())) {
			List<SootClass> directImplementerList = hierarchy
					.getDirectImplementersOf(aType);
			result = directImplementerList.contains(aClass);
		} else if (aType.isInterface() && aClass.isInterface()) {
			if (hierarchy.isInterfaceDirectSubinterfaceOf(aClass, aType)) {
				result = true;
			}
		} else if (!(aType.isInterface()) && !(aClass.isInterface())) {
			List<SootClass> directSubclassList = hierarchy
					.getDirectSubclassesOf(aType);
			result = directSubclassList.contains(aClass);
		}
		return result;
	}

	private boolean anyCreatedClassSubTypeExcluding(
			List<Unit> createStatementList, SootClass aType) {
		boolean result = false;
		for (Unit aUnit : createStatementList) {
			if (aUnit instanceof JAssignStmt) {
				// remove "new " from "new ClassName"
				String className = ((JAssignStmt) aUnit).getRightOp()
						.toString().substring(4);
				SootClass createdClass = classMap.get(className);
				if (createdClass != null) {
					if (isClassOfSubTypeExcluding(createdClass, aType)) {
						result = true;
						break;
					}
				}
			}
		}
		return result;
	}

	public void writeAnalysisResultOverAllAbstractTypes(String outputDirectory) {
		List<SootClass> abstractTypeList = getAbstractTypeClassList();

		// String graphXML = "<GraphList>";

		for (SootClass aType : abstractTypeList) {
			DecNCorAnalysisResult anAnalysisResult = null;
			anAnalysisResult = (DecNCorAnalysisResult) (analyzeOverType(aType));
			if (anAnalysisResult == null)
				continue;

			String patternName = null;
			if (((DecNCorAnalysisResult) anAnalysisResult).isCor()) {
				patternName = "CoR";
			} else if (((DecNCorAnalysisResult) anAnalysisResult).isDecorator()) {
				patternName = "Dec";
			}

			if (patternName == null)
				continue;

			logger.debug("Writing output....");
			String fileName = "DecNCorAnalysis_"
					+ anAnalysisResult.getAbstractTypeName();

			String outputPath = MyUtil.getPath(
					outputDirectory,
					fileName
							+ "_"
							+ patternName
							+ "_storeClass_"
							+ ((DecNCorAnalysisResult) anAnalysisResult)
									.storeClassName() + ".xml");
			XMLWriter writer = new XMLWriter(outputPath);
			anAnalysisResult.writeXML(writer);
			writer.close();

			logger.debug("Writing output finished....");
		}

		// graphXML = graphXML + "</GraphList>";
		// MyUtil.stringToFile(graphXML,
		// "/Users/chanwoo/Documents/workspace/StatePatternExample2/output/StatePatternExample2_ReferenceFlowGraph.xml");

	}

}
