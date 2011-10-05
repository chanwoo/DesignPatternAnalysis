package kr.ac.snu.selab.soot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kr.ac.snu.selab.soot.analyzer.MyField;
import kr.ac.snu.selab.soot.analyzer.MyGraph;
import kr.ac.snu.selab.soot.analyzer.MyMethod;
import kr.ac.snu.selab.soot.analyzer.MyNode;

import soot.Body;
import soot.Hierarchy;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JInvokeStmt;

public class Analysis {
	private List<SootClass> classList;
	private MyCallGraph callGraph;
	private HashMap<String, SootClass> classMap;
	private HashMap<String, SootMethod> methodMap;
	private HashMap<String, SootField> fieldMap;
	private Hierarchy hierarchy;
	
	public Analysis(List<SootClass> aClassList, Hierarchy aHierarchy, MyCallGraph aGraph) {
		classList = new ArrayList<SootClass>();
		classMap = new HashMap<String, SootClass>();
		methodMap = new HashMap<String, SootMethod>();
		fieldMap = new HashMap<String, SootField>();
		
		classList = aClassList;
		for (SootClass aClass : classList) {
			classMap.put(aClass.getName(), aClass);
			for (SootField aField : aClass.getFields()) {
				fieldMap.put(aField.toString(), aField);
			}
			for (SootMethod aMethod : aClass.getMethods()) {
				methodMap.put(aMethod.toString(), aMethod);
			}
		}
		callGraph = aGraph;
		hierarchy = aHierarchy;
	}
	
	public Analysis(List<SootClass> aClassList, Hierarchy aHierarchy) {
		new Analysis(aClassList, aHierarchy, new MyCallGraph(classList, methodMap));
	}
	
	private List<Unit> getUnits(SootMethod aMethod) {
		List<Unit> unitList = new ArrayList<Unit>();
		if (aMethod.hasActiveBody()) {
			Body body = aMethod.getActiveBody();
			unitList.addAll(body.getUnits());
		}
		return unitList;
	}
	
	private boolean isAbstractTypeClass(SootClass aClass) {
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
	
	// <JInvokeStmt> interfaceinvoke temp$0.<State: void changeSpeed(CeilingFan)>(this)
	// <UseBox> VB(temp$0)
	// <Type> State
	// <UseBox> VB(this)
	// <Type> CeilingFan
	// <UseBox> VB(interfaceinvoke temp$0.<State: void changeSpeed(CeilingFan)>(this))
	// <Type> void
	// <MethodRef> <State: void changeSpeed(CeilingFan)>
	public boolean isInvokeStatementOfReceiverType(Unit aUnit, SootClass aType) {
		boolean result = false;
		if (aUnit instanceof JInvokeStmt) {
			JInvokeStmt statement = (JInvokeStmt) aUnit;
			ValueBox receiver = (ValueBox)(statement.getUseBoxes().get(0));
			SootClass receiverClass = classMap.get(receiver.getValue()
					.getType().toString());
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
	
	private boolean isClassOfSubType(SootClass aClass, SootClass aType) {
		boolean result = false;
		if (aType.isInterface() && !(aClass.isInterface())) {
			List<SootClass> implementerList = hierarchy
					.getImplementersOf(aType);
			result = implementerList.contains(aClass);
		} else if (aType.isInterface() && aClass.isInterface()) {
			result = hierarchy.isInterfaceSubinterfaceOf(aClass, aType);
		} else if (!(aType.isInterface()) && !(aClass.isInterface())) {
			result = hierarchy.isClassSubclassOf(aClass, aType);
		}
		return result;
	}
	
	// <Creation Statement> temp$0 = new Off
	// <DefBox> VB(temp$0)
	// <UseBox> VB(new Off)
	public boolean isInstanceCreationStatementOfSubType(Unit aUnit, SootClass aType) {
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
	
	private boolean isFieldOfType(SootField aField, SootClass aType) {
		boolean result = false;
		if (aType.getName().equals(aField.getType().toString())) {
			result = true;
		}
		return result;
	}
	
	public boolean isFieldOfSubType(SootField aField, SootClass aType) {
		boolean result = false;
		SootClass fieldClass = classMap.get(aField.getType().toString());
		if (fieldClass != null) {
			if (isClassOfSubType(fieldClass, aType)) {
				result = true;
			}
		}
		return result;
	}
	
	public SootField getReadFieldByThisStatement(Unit aUnit) {
		SootField field = null;
		if (aUnit instanceof JAssignStmt) {
			JAssignStmt assignStmt = (JAssignStmt)aUnit;
			String rightSideString = assignStmt.getRightOp().toString();
			if (rightSideString.startsWith("this.")) {
				rightSideString = rightSideString.substring(5);
			}
			field = fieldMap.get(rightSideString);
		}
		return field;
	}
	
	public List<SootField> getReadFieldListByThisMethod(SootMethod aMethod) {
		List<SootField> fieldList = new ArrayList<SootField>();
		for (Unit aUnit : getUnits(aMethod)) {
			SootField field = getReadFieldByThisStatement(aUnit);
			if (field != null) {
				fieldList.add(field);
			}
		}
		return fieldList;
	}
	
	public SootField getWrittenFieldByThisStatement(Unit aUnit) {
		SootField field = null;
		if (aUnit instanceof JAssignStmt) {
			JAssignStmt assignStmt = (JAssignStmt)aUnit;
			String rightSideString = assignStmt.getLeftOp().toString();
			if (rightSideString.startsWith("this.")) {
				rightSideString = rightSideString.substring(5);
			}
			field = fieldMap.get(rightSideString);
		}
		return field;
	}
	
	public List<SootField> getWrittenFieldListByThisMethod(SootMethod aMethod) {
		List<SootField> fieldList = new ArrayList<SootField>();
		for (Unit aUnit : getUnits(aMethod)) {
			SootField field = getReadFieldByThisStatement(aUnit);
			if (field != null) {
				fieldList.add(field);
			}
		}
		return fieldList;
	}
	
	private String getReturnTypeString(SootMethod aMethod) {
		Type returnType = aMethod.getReturnType();
		return returnType.toString();
	}
	
	public boolean doesThisMethodReturnObjectOfSubtype(SootMethod aMethod, SootClass aType) {
		boolean result = false;
		String returnTypeString = getReturnTypeString(aMethod);
		SootClass returnType = classMap.get(returnTypeString);
		if (returnType != null) {
			result = isClassOfSubType(returnType, aType);
		}
		return result;
	}
	
	private List<String> getParameterTypeStringList(SootMethod aMethod) {
		List<Type> parameterTypeList = aMethod.getParameterTypes();
		List<String> parameterTypeStringList = new ArrayList<String>();
		for (Type aParameterType : parameterTypeList) {
			parameterTypeStringList.add(aParameterType.toString());
		}
		return parameterTypeStringList;
	}
	
	public boolean doesThisMethodParameterOfSubtype(SootMethod aMethod, SootClass aType) {
		boolean result = false;
		List<String> parameterTypeStringList = getParameterTypeStringList(aMethod);
		List<SootClass> parameterTypeList = new ArrayList<SootClass>();
		for (String aParameterTypeString : parameterTypeStringList) {
			SootClass parameterType = classMap.get(aParameterTypeString);
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
	
	public MethodAnalysisResult analyzeMethodOverType(SootMethod aMethod, SootClass aType) {
		MethodAnalysisResult result = new MethodAnalysisResult();
		result.self = new MyMethod(aMethod);
		result.abstractType = aType;
		
		for (Unit aUnit : getUnits(aMethod)) {
			if (isInstanceCreationStatementOfSubType(aUnit, aType)) {
				result.isCreater = true;
				result.createStatement = aUnit;
			}
			if (isInvokeStatementOfReceiverType(aUnit, aType)) {
				result.isCaller = true;
				result.callStatement = aUnit;
			}
		}
		
		HashSet<SootMethod> callerSet = callGraph.edgesInto(aMethod);
		HashSet<SootMethod> calleeSet = callGraph.edgesOutOf(aMethod);
		
		if (doesThisMethodParameterOfSubtype(aMethod, aType)) {
			for (SootMethod callerMethod : callerSet) {
				MyMethod aNode = new MyMethod(callerMethod);
				result.sourceNodes.add(aNode);
				result.targetNodes.add(aNode);
			}
		}
		
		if (doesThisMethodReturnObjectOfSubtype(aMethod, aType)) {
			for (SootMethod callerMethod : callerSet) {
				MyMethod aNode = new MyMethod(callerMethod);
				result.targetNodes.add(aNode);
			}
		}
		
		for (SootMethod calleeMethod : calleeSet) {
			if (doesThisMethodParameterOfSubtype(calleeMethod, aType)) {
				MyMethod aNode = new MyMethod(calleeMethod);
				result.sourceNodes.add(aNode);
				result.targetNodes.add(aNode);
			}
		}
		
		for (SootMethod calleeMethod : calleeSet) {
			if (doesThisMethodReturnObjectOfSubtype(calleeMethod, aType)) {
				MyMethod aNode = new MyMethod(calleeMethod);
				result.sourceNodes.add(aNode);
			}
		}
		
		List<SootField> readFieldList = getReadFieldListByThisMethod(aMethod);
		for (SootField readField : readFieldList) {
			if (isFieldOfSubType(readField, aType)) {
				MyField field = new MyField(readField);
				result.sourceNodes.add(field);
			}
		}
		
		List<SootField> writtenFieldList = getWrittenFieldListByThisMethod(aMethod);
		for (SootField writtenField : writtenFieldList) {
			if (isFieldOfSubType(writtenField, aType)) {
				MyField field = new MyField(writtenField);
				result.targetNodes.add(field);
			}
		}
		
		return result;
	}
	
	public MyGraph getGraphFromMethodAnalysisResultList(List<MethodAnalysisResult> resultList) {
		MyGraph graph = new MyGraph();
		HashMap<String, HashSet<MyNode>> sourceMap = graph.sourceMap;
		HashMap<String, HashSet<MyNode>> targetMap = graph.targetMap;
		
		for (MethodAnalysisResult result : resultList) {
			MyNode selfNode = result.self;
			
			for (MyNode sourceNode : result.sourceNodes) {
				String key = sourceNode.toString();
				if (targetMap.containsKey(key)) {
					targetMap.get(key).add(selfNode);
				}
				else {
					HashSet<MyNode> newSet = new HashSet<MyNode>();
					newSet.add(selfNode);
					targetMap.put(key, newSet);
				}
			}
			
			for (MyNode targetNode : result.targetNodes) {
				String key = targetNode.toString();
				if (sourceMap.containsKey(key)) {
					sourceMap.get(key).add(selfNode);
				}
				else {
					HashSet<MyNode> newSet = new HashSet<MyNode>();
					newSet.add(selfNode);
					sourceMap.put(key, newSet);
				}
			}
		}
		return graph;
	}
	
//	private Parameter getParameter(Unit aUnit) {
//		Parameter result = null;
//		if (aUnit instanceof JIdentityStmt) {
//			JIdentityStmt jIdentityStatement = (JIdentityStmt)aUnit;
//			Value rightOp = jIdentityStatement.getRightOp();
//			if (rightOp.toString().startsWith("@parameter")) {
//				String name = jIdentityStatement.getLeftOp().toString();
//				String type = rightOp.getType().toString();
//				result = new Parameter(name, type);
//			}
//		}
//		return result;
//	}
	
//	private MethodAnalysisResult analyzeMethod(SootMethod aMethod) {
//		MethodAnalysisResult result = new MethodAnalysisResult();
//		for (Unit aUnit : getUnits(aMethod)) {
//			if (aUnit instanceof JIdentityStmt) {
//				
//			}
//		}
//		return result;
//	}
	
	public List<AnalysisResult> getAnalysisResultList() {
		List<AnalysisResult> analysisResultList = new ArrayList<AnalysisResult>();
		List<SootClass> abstractTypeList = getAbstractTypeClassList();
		for (SootClass anAbstractType : abstractTypeList) {
			AnalysisResult anAnalysisResult = new AnalysisResult();
			anAnalysisResult.abstractType = anAbstractType;
			for (SootClass aClass : classList) {
				for (SootField aField : aClass.getFields()) {
					if (isFieldOfType(aField, anAbstractType)) {
						anAnalysisResult.storeList.add(new Store(aClass, aField));
					}
				}
				for (SootMethod aMethod : aClass.getMethods()) {
					for (Unit aUnit : getUnits(aMethod)) {
						if (isInstanceCreationStatementOfSubType(aUnit, anAbstractType)) {
							anAnalysisResult.createrList.add(new Creater(aClass, aMethod, aUnit));
						}
						if (isInvokeStatementOfReceiverType(aUnit, anAbstractType)) {
							anAnalysisResult.callerList.add(new Caller(aClass, aMethod, aUnit));
						}
					}
				}
			}
			analysisResultList.add(anAnalysisResult);
		}
		
		return analysisResultList;
	}
}
