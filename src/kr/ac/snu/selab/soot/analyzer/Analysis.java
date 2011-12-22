package kr.ac.snu.selab.soot.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import kr.ac.snu.selab.soot.callgraph.CallGraph;
import kr.ac.snu.selab.soot.callgraph.SimpleCallGraph;
import kr.ac.snu.selab.soot.callgraph.SootCallGraph;
import kr.ac.snu.selab.soot.graph.Graph;
import kr.ac.snu.selab.soot.graph.GraphPathCollector;
import kr.ac.snu.selab.soot.graph.MyNode;
import kr.ac.snu.selab.soot.graph.Path;
import soot.Body;
import soot.Hierarchy;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInvokeStmt;

public class Analysis {
	protected List<SootClass> classList;
	protected CallGraph callGraph;
	protected HashMap<String, SootClass> classMap;
	private HashMap<String, SootMethod> methodMap;
	private HashMap<String, SootField> fieldMap;
	protected Hierarchy hierarchy;
	private Map<Map<SootClass, String>, SootMethod> methodMapBySubSignature;

	public Analysis(List<SootClass> aClassList, Hierarchy aHierarchy,
			CallGraph aGraph) {
		classList = new ArrayList<SootClass>();
		classMap = new HashMap<String, SootClass>();
		methodMap = new HashMap<String, SootMethod>();
		fieldMap = new HashMap<String, SootField>();
		/*
		 * This is a map that has keys of (class, subsignature of method) pair.
		 */
		methodMapBySubSignature = new HashMap<Map<SootClass, String>, SootMethod>();

		classList = aClassList;
		for (SootClass aClass : classList) {
			classMap.put(aClass.getName(), aClass);
			for (SootField aField : aClass.getFields()) {
				fieldMap.put(aField.toString(), aField);
			}
			for (SootMethod aMethod : aClass.getMethods()) {
				methodMap.put(aMethod.toString(), aMethod);
				Map<SootClass, String> key = new HashMap<SootClass, String>();
				key.put(aClass, aMethod.getSubSignature());
				methodMapBySubSignature.put(key, aMethod);
			}
		}
		callGraph = aGraph;
		hierarchy = aHierarchy;
	}

	public Analysis(List<SootClass> aClassList, Hierarchy aHierarchy,
			boolean useSimpleCallGraph) {
		classList = new ArrayList<SootClass>();
		classMap = new HashMap<String, SootClass>();
		methodMap = new HashMap<String, SootMethod>();
		fieldMap = new HashMap<String, SootField>();
		methodMapBySubSignature = new HashMap<Map<SootClass, String>, SootMethod>();

		classList = aClassList;
		for (SootClass aClass : classList) {
			classMap.put(aClass.getName(), aClass);
			for (SootField aField : aClass.getFields()) {
				fieldMap.put(aField.toString(), aField);
			}
			for (SootMethod aMethod : aClass.getMethods()) {
				methodMap.put(aMethod.toString(), aMethod);
				Map<SootClass, String> key = new HashMap<SootClass, String>();
				key.put(aClass, aMethod.getSubSignature());
				methodMapBySubSignature.put(key, aMethod);
			}
		}
		hierarchy = aHierarchy;
		if (useSimpleCallGraph) {
			callGraph = new SimpleCallGraph(classList, methodMap, aHierarchy);
		} else {
			callGraph = new SootCallGraph(classList);
		}

	}

	protected List<Unit> getUnits(SootMethod aMethod) {
		List<Unit> unitList = new ArrayList<Unit>();
		if (aMethod.hasActiveBody()) {
			Body body = aMethod.getActiveBody();
			unitList.addAll(body.getUnits());
		}
		return unitList;
	}

	protected boolean isAbstractTypeClass(SootClass aClass) {
		boolean result = false;
		if (aClass.isInterface() || aClass.isAbstract()) {
			result = true;
		}
		return result;
	}

	public List<SootClass> getAbstractTypeClassList() {
		List<SootClass> result = new ArrayList<SootClass>();
		for (SootClass aClass : classList) {
			if (isAbstractTypeClass(aClass)) {
				result.add(aClass);
			}
		}
		return result;
	}

	private List<SootClass> getSubTypeClassOf(SootClass aType) {
		if (aType.isInterface()) {
			return hierarchy.getImplementersOf(aType);
		} else {
			return hierarchy.getSubclassesOf(aType);
		}
	}

	public List<SootMethod> getOverrideMethodsOf(SootMethod aMethod) {
		List<SootMethod> result = new ArrayList<SootMethod>();
		SootClass receiverType = aMethod.getDeclaringClass();
		List<SootClass> subTypeClassList = getSubTypeClassOf(receiverType);
		if (!subTypeClassList.isEmpty()) {
			for (SootClass aClass : subTypeClassList) {
				Map<SootClass, String> key = new HashMap<SootClass, String>();
				key.put(aClass, aMethod.getSubSignature());
				SootMethod overrideMethod = methodMapBySubSignature.get(key);
				if (overrideMethod != null) {
					result.add(overrideMethod);
				}
			}
		}
		return result;
	}

	public List<SootClass> getSuperClassListExcludingInterface(SootClass aClass) {
		List<SootClass> result = new ArrayList<SootClass>();
		for (SootClass candidateClass : classList) {
			if (!candidateClass.isInterface()) {
				if (isClassOfSubType(aClass, candidateClass)) {
					if (!aClass.equals(candidateClass)) {
						result.add(candidateClass);
					}
				}
			}
		}
		return result;
	}

	// <JInvokeStmt> interfaceinvoke temp$0.<State: void
	// changeSpeed(CeilingFan)>(this)
	// <UseBox> VB(temp$0)
	// <Type> State
	// <UseBox> VB(this)
	// <Type> CeilingFan
	// <UseBox> VB(interfaceinvoke temp$0.<State: void
	// changeSpeed(CeilingFan)>(this))
	// <Type> void
	// <MethodRef> <State: void changeSpeed(CeilingFan)>
	public boolean isInvokeStatementOfReceiverType(Unit aUnit, SootClass aType) {
		boolean result = false;
		if (aUnit instanceof JInvokeStmt) {
			JInvokeStmt statement = (JInvokeStmt) aUnit;

			SootClass receiver = statement.getInvokeExpr().getMethod()
					.getDeclaringClass();
			String key = receiver.toString();
			SootClass receiverClass = null;
			if (classMap.containsKey(key)) {
				receiverClass = classMap.get(key);
			}
			if (receiverClass != null) {
				if (receiverClass instanceof SootClass) {
					if (receiverClass.equals(aType)) {
						result = true;
					}
				}
			}
		}
		return result;
	}

	public boolean isAssignmentStatementHasInvocationOfReceiverType(Unit aUnit,
			SootClass aType) {
		boolean result = false;
		if (aUnit instanceof JAssignStmt) {
			JAssignStmt statement = (JAssignStmt) aUnit;

			if (statement.containsInvokeExpr()) {
				SootClass receiver = statement.getInvokeExpr().getMethod()
						.getDeclaringClass();
				String key = receiver.toString();
				SootClass receiverClass = null;
				if (classMap.containsKey(key)) {
					receiverClass = classMap.get(key);
				}
				if (receiverClass != null) {
					if (receiverClass instanceof SootClass) {
						if (receiverClass.equals(aType)) {
							result = true;
						}
					}
				}
			}
		}
		return result;
	}

	public boolean isClassOfSubType(SootClass aClass, SootClass aType) {
		boolean result = false;
		if (aType.isInterface() && !(aClass.isInterface())) {
			List<SootClass> implementerList = hierarchy
					.getImplementersOf(aType);
			result = implementerList.contains(aClass);
		} else if (aType.isInterface() && aClass.isInterface()) {
			if (hierarchy.isInterfaceSubinterfaceOf(aClass, aType)
					|| aClass.equals(aType)) {
				result = true;
			}
		} else if (!(aType.isInterface()) && !(aClass.isInterface())) {
			result = hierarchy.isClassSubclassOfIncluding(aClass, aType);
		}
		return result;
	}

	public boolean isClassOfSubTypeExcluding(SootClass aClass, SootClass aType) {
		boolean result = false;
		if (aType.isInterface() && !(aClass.isInterface())) {
			List<SootClass> implementerList = hierarchy
					.getImplementersOf(aType);
			result = implementerList.contains(aClass);
		} else if (aType.isInterface() && aClass.isInterface()) {
			if (hierarchy.isInterfaceSubinterfaceOf(aClass, aType)) {
				result = true;
			}
		} else if (!(aType.isInterface()) && !(aClass.isInterface())) {
			result = hierarchy.isClassSubclassOf(aClass, aType);
		}
		return result;
	}

	// <Creation Statement> temp$0 = new Off
	// <DefBox> VB(temp$0)
	// <UseBox> VB(new Off)
	public boolean isInstanceCreationStatementOfSubType(Unit aUnit,
			SootClass aType) {
		boolean result = false;
		if (aUnit instanceof JAssignStmt) {
			String rightSide = aUnit.getUseBoxes().get(0).getValue().toString();
			if (rightSide.matches("new .*")) {
				String className = rightSide.substring(4);
				SootClass createdClass = classMap.get(className);
				if (createdClass != null) {
					result = isClassOfSubType(createdClass, aType);
				}
			}
		}
		return result;
	}

	// private boolean isFieldOfType(SootField aField, SootClass aType) {
	// boolean result = false;
	// if (aType.getName().equals(aField.getType().toString())) {
	// result = true;
	// }
	// return result;
	// }

	public boolean isFieldOfSubType(SootField aField, SootClass aType) {
		boolean result = false;
		String key = aField.getType().toString();
		SootClass fieldClass = null;
		if (classMap.containsKey(key)) {
			fieldClass = classMap.get(key);
		}
		if (fieldClass != null) {
			if (isClassOfSubType(fieldClass, aType)) {
				result = true;
			}
		}
		return result;
	}

	public String getReadFieldStringByThisStatement(Unit aUnit) {
		String result = null;
		if (aUnit instanceof JAssignStmt) {
			JAssignStmt assignStmt = (JAssignStmt) aUnit;
			String rightSideString = assignStmt.getRightOp().toString();
			if (rightSideString.startsWith("this.")) {
				rightSideString = rightSideString.substring(5);
			}
			// Only field variable
			if (rightSideString.startsWith("<")) {
				result = rightSideString;
			}
		}
		return result;
	}

	public List<String> getReadFieldStringListByThisMethod(SootMethod aMethod) {
		List<String> fieldStringList = new ArrayList<String>();
		for (Unit aUnit : getUnits(aMethod)) {
			String fieldString = getReadFieldStringByThisStatement(aUnit);
			if (fieldString != null) {
				fieldStringList.add(fieldString);
			}
		}
		return fieldStringList;
	}

	public String getWrittenFieldStringByThisStatement(Unit aUnit,
			SootClass aType) {
		String result = null;
		if (aUnit instanceof JAssignStmt) {
			JAssignStmt assignStmt = (JAssignStmt) aUnit;
			Value rightOp = assignStmt.getRightOp();
			SootClass rightOpType = null;
			String rightOpTypeKey = rightOp.getType().toString();
			// if (!(rightOpTypeKey.startsWith("null"))) {
			if (classMap.containsKey(rightOpTypeKey)) {
				rightOpType = classMap.get(rightOpTypeKey);

				// if ((rightOpType != null)
				// && isClassOfSubType(rightOpType, aType)) {
				String leftSideString = assignStmt.getLeftOp().toString();
				if (leftSideString.startsWith("this.")) {
					leftSideString = leftSideString.substring(5);
				}
				// Only field variable
				if (leftSideString.startsWith("<")) {
					result = leftSideString;
				}
				// }
			}
			// }
		}
		return result;
	}

	public List<String> getWrittenFieldStringListByThisMethod(
			SootMethod aMethod, SootClass aType) {
		List<String> fieldStringList = new ArrayList<String>();
		for (Unit aUnit : getUnits(aMethod)) {
			String fieldString = getWrittenFieldStringByThisStatement(aUnit,
					aType);
			if (fieldString != null) {
				fieldStringList.add(fieldString);
			}
		}
		return fieldStringList;
	}

	// public boolean isInjectorMethodOfField(SootMethod aMethod, SootField
	// aField) {
	// boolean result = false;
	// Set<String> writtenFieldStringSet = new HashSet<String>();
	// writtenFieldStringSet.addAll(getWrittenFieldStringListByThisMethod(aMethod));
	//
	// for (String fieldString : writtenFieldStringSet) {
	// if (aField.toString().equals(fieldString)) {
	// result = true;
	// break;
	// }
	// }
	// return result;
	// }

	private String getReturnTypeString(SootMethod aMethod) {
		Type returnType = aMethod.getReturnType();
		return returnType.toString();
	}

	public boolean doesThisMethodReturnObjectOfSubtype(SootMethod aMethod,
			SootClass aType) {
		boolean result = false;
		String returnTypeString = getReturnTypeString(aMethod);
		SootClass returnType = classMap.get(returnTypeString);
		if (returnType != null) {
			result = isClassOfSubType(returnType, aType);
		}
		return result;
	}

	private List<String> getParameterTypeStringList(SootMethod aMethod) {
		@SuppressWarnings("unchecked")
		List<Type> parameterTypeList = aMethod.getParameterTypes();
		List<String> parameterTypeStringList = new ArrayList<String>();
		for (Type aParameterType : parameterTypeList) {
			parameterTypeStringList.add(aParameterType.toString());
		}
		return parameterTypeStringList;
	}

	public boolean doesThisMethodParameterOfSubtype(SootMethod aMethod,
			SootClass aType) {
		boolean result = false;
		List<String> parameterTypeStringList = getParameterTypeStringList(aMethod);
		List<SootClass> parameterTypeList = new ArrayList<SootClass>();
		for (String aParameterTypeString : parameterTypeStringList) {
			SootClass parameterType = null;
			if (classMap.containsKey(aParameterTypeString)) {
				parameterType = classMap.get(aParameterTypeString);
			}
			if (parameterType != null) {
				parameterTypeList.add(parameterType);
			}
		}
		for (SootClass aParameterType : parameterTypeList) {
			if (isClassOfSubType(aParameterType, aType)) {
				result = true;
				return result;
			}
		}
		return result;
	}

	public MethodAnalysisResult analyzeMethodOverType(SootMethod aMethod,
			SootClass aType, HashMap<String, MyNode> nodeMap) {
		MethodAnalysisResult result = new MethodAnalysisResult();
		String aNodeKey = aMethod.toString();
		if (nodeMap.containsKey(aNodeKey)) {
			result.setMethod((MyMethod) nodeMap.get(aNodeKey));
		}
		result.setAbstractType(aType);

		for (Unit aUnit : getUnits(aMethod)) {
			if (isInstanceCreationStatementOfSubType(aUnit, aType)) {
				result.getMethod().setIsCreator(true);
				result.getMethod().addCreateStatement(aUnit);
			}
			if (isInvokeStatementOfReceiverType(aUnit, aType)) {
				result.getMethod().setIsCaller(true);
				result.getMethod().addCallStatement(aUnit);
			} else if (isAssignmentStatementHasInvocationOfReceiverType(aUnit,
					aType)) {
				result.getMethod().setIsCaller(true);
				result.getMethod().addCallStatement(aUnit);
			}
		}

		HashSet<SootMethod> callerSet = new HashSet<SootMethod>();
		for (MyNode aNode : callGraph.sourceNodes(new MyMethod(aMethod))) {
			callerSet.add(((MyMethod) aNode).getMethod());
		}
		HashSet<SootMethod> calleeSet = new HashSet<SootMethod>();
		for (MyNode aNode : callGraph.targetNodes(new MyMethod(aMethod))) {
			calleeSet.add(((MyMethod) aNode).getMethod());
		}

		if (doesThisMethodParameterOfSubtype(aMethod, aType)) {
			for (SootMethod callerMethod : callerSet) {
				MyNode method = nodeMap.get(callerMethod.toString());
				if (method != null) {
					result.addSourceNode(method);
					// result.targetNodes.add(method); // can make cycle
				}
			}
		}

		if (doesThisMethodReturnObjectOfSubtype(aMethod, aType)) {
			for (SootMethod callerMethod : callerSet) {
				MyNode method = nodeMap.get(callerMethod.toString());
				if (method != null) {
					result.addTargetNode(method);
				}
			}
		}

		for (SootMethod calleeMethod : calleeSet) {
			if (doesThisMethodParameterOfSubtype(calleeMethod, aType)) {
				MyNode method = nodeMap.get(calleeMethod.toString());

				if (method != null) {
					result.addTargetNode(method);
					// result.sourceNodes.add(method); // can make cycle
				}
			}
			// new ConcreteClass() should have return type of ConcreteClass
			// But temp0 = new ConcreteClass
			// temp0.<init>
			// has void return type
			// So it needs special care for creator method
			if (isClassOfSubType(calleeMethod.getDeclaringClass(), aType)
					&& calleeMethod.getName().equals("<init>")) {
				MyNode method = nodeMap.get(calleeMethod.toString());
				if (method != null) {
					result.addSourceNode(method);
				}
			}
		}

		for (SootMethod calleeMethod : calleeSet) {
			if (doesThisMethodReturnObjectOfSubtype(calleeMethod, aType)) {
				MyNode method = nodeMap.get(calleeMethod.toString());
				if (method != null) {
					result.addSourceNode(method);
				}
			}
		}

		List<SootField> superClassFieldListOfAbstractType = new ArrayList<SootField>();
		SootClass classOfThisMethod = aMethod.getDeclaringClass();
		if (!classOfThisMethod.isInterface()) {
			List<SootClass> superClassList = getSuperClassListExcludingInterface(classOfThisMethod);
			for (SootClass aClass : superClassList) {
				for (SootField aField : aClass.getFields()) {
					if (isFieldOfSubType(aField, aType)) {
						superClassFieldListOfAbstractType.add(aField);
					}
				}
			}
		}

		List<String> readFieldStringList = getReadFieldStringListByThisMethod(aMethod);
		for (String readFieldString : readFieldStringList) {
			if (nodeMap.containsKey(readFieldString)) {
				nodeMap.get(readFieldString).setIsStore(true);
				result.addSourceNode(nodeMap.get(readFieldString));
			} else {
				for (SootField aSuperClassField : superClassFieldListOfAbstractType) {
					String fieldKey = aSuperClassField.toString();
					if (fieldKey.contains(readFieldString.replaceFirst("<.*:",
							""))) {
						if (nodeMap.containsKey(fieldKey)) {
							nodeMap.get(fieldKey).setIsStore(true);
							result.addSourceNode(nodeMap.get(fieldKey));
						}
					}
				}
			}
		}

		List<String> writtenFieldStringList = getWrittenFieldStringListByThisMethod(
				aMethod, aType);
		for (String writtenFieldString : writtenFieldStringList) {
			if (nodeMap.containsKey(writtenFieldString)) {
				nodeMap.get(writtenFieldString).setIsStore(true);
				result.addTargetNode(nodeMap.get(writtenFieldString));
			} else {
				for (SootField aSuperClassField : superClassFieldListOfAbstractType) {
					String fieldKey = aSuperClassField.toString();
					if (fieldKey.contains(writtenFieldString.replaceFirst(
							"<.*:", ""))) {
						if (nodeMap.containsKey(fieldKey)) {
							nodeMap.get(fieldKey).setIsStore(true);
							result.addTargetNode(nodeMap.get(fieldKey));
						}
					}
				}
			}
		}

		return result;
	}

	public Graph<MyNode> getGraphFromMethodAnalysisResultList(
			List<MethodAnalysisResult> resultList) {
		Graph<MyNode> graph = new Graph<MyNode>();
		HashMap<String, HashSet<MyNode>> sourceMap = graph.getSourceMap();
		HashMap<String, HashSet<MyNode>> targetMap = graph.getTargetMap();

		for (MethodAnalysisResult result : resultList) {
			MyNode selfNode = result.getMethod();

			for (MyNode sourceNode : result.getSourceNodes()) {
				// add mapping information to targetMap
				String key = sourceNode.toString();
				if (targetMap.containsKey(key)) {
					targetMap.get(key).add(selfNode);
				} else {
					HashSet<MyNode> newSet = new HashSet<MyNode>();
					newSet.add(selfNode);
					targetMap.put(key, newSet);
				}
				// add mapping information to sourceMap
				key = selfNode.toString();
				if (sourceMap.containsKey(key)) {
					sourceMap.get(key).add(sourceNode);
				} else {
					HashSet<MyNode> newSet = new HashSet<MyNode>();
					newSet.add(sourceNode);
					sourceMap.put(key, newSet);
				}
			}

			for (MyNode targetNode : result.getTargetNodes()) {
				// add mapping information to sourceMap
				String key = targetNode.toString();
				if (sourceMap.containsKey(key)) {
					sourceMap.get(key).add(selfNode);
				} else {
					HashSet<MyNode> newSet = new HashSet<MyNode>();
					newSet.add(selfNode);
					sourceMap.put(key, newSet);
				}
				// add mapping information to targetMap
				key = selfNode.toString();
				if (targetMap.containsKey(key)) {
					targetMap.get(key).add(targetNode);
				} else {
					HashSet<MyNode> newSet = new HashSet<MyNode>();
					newSet.add(targetNode);
					targetMap.put(key, newSet);
				}
			}
		}
		return graph;
	}

	public AnalysisResult analyzeOverType(SootClass aType) {
		AnalysisResult anAnalysisResult = new AnalysisResult();
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
			MyNode node = aResult.getMethod();
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
				anAnalysisResult.putReferenceFlowPath(callerNode.key(),
						pathIncludeStoreList);
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
