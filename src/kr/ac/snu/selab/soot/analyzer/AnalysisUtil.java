package kr.ac.snu.selab.soot.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kr.ac.snu.selab.soot.graph.GraphPathCollector;
import kr.ac.snu.selab.soot.graph.MyNode;
import kr.ac.snu.selab.soot.graph.Path;
import kr.ac.snu.selab.soot.graph.collectors.ReverseAllPathCollector;
import kr.ac.snu.selab.soot.graph.refgraph.LocalInfoNode;
import kr.ac.snu.selab.soot.graph.refgraph.ReferenceFlowGraph;
import soot.Body;
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
import soot.jimple.InvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;



public class AnalysisUtil {
	
	public AnalysisUtil() {
		
	}
	
	// Creation
	public Map<String, LocalInfo> creations(SootMethod aMethod) {
		Map<String, LocalInfo> creations = new HashMap<String, LocalInfo>();
		Map<String, Local> locals = locals(aMethod);

		List<Unit> units = units(aMethod);
		String newExprClassStr = "class soot.jimple.internal.JNewExpr";

		for (Unit unit : units) {
			if (unit instanceof JAssignStmt) {
				JAssignStmt stmt = (JAssignStmt)unit;
				Value rightVal = stmt.getRightOp();
				if (rightVal.getClass().toString().equals(newExprClassStr)) {
					Value leftVal = stmt.getLeftOp();
					Local local = locals.get(leftVal.toString());
					LocalInfo localInfo = new Creation();
					localInfo.setLocal(local);
					localInfo.setDeclaringMethod(aMethod);
					localInfo.setUnit(unit);

					creations.put(local.toString(), localInfo);	
				}
			}
		}

		return creations;
	}
	
	// In
	public Map<String, LocalInfo> localsOfMethodParam(SootMethod aMethod) {
		Map<String, LocalInfo> localsOfMethodParam = new HashMap<String, LocalInfo>();
		
		if (aMethod.hasActiveBody()) {
			Body body = aMethod.getActiveBody();
			int numOfParams = aMethod.getParameterCount();
			for (int i = 0; i < numOfParams; i++) {
				Local local = body.getParameterLocal(i);
				LocalInfo localInfo = new MethodParamIn();
				localInfo.setLocal(local);
				localInfo.setDeclaringMethod(aMethod);
				localInfo.setParamNum(i);
				localsOfMethodParam.put(local.toString(), localInfo);
			}
		}
		
		return localsOfMethodParam;
	}
	
	// In
	public Map<String, LocalInfo> localsLeftOfField(SootMethod aMethod) {
		Map<String, LocalInfo> localsLeftOfField = new HashMap<String, LocalInfo>();
		Map<String, Local> locals = locals(aMethod);
		
		List<Unit> units = units(aMethod);

		for (Unit unit : units) {
			if (isFieldInRightSide(unit)) {
				JAssignStmt stmt = (JAssignStmt)unit;
				Value leftVal = stmt.getLeftOp();
				Local local = locals.get(leftVal.toString());
				SootField field = stmt.getFieldRef().getField();
				LocalInfo localInfo = new FieldIn();
				localInfo.setLocal(local);
				localInfo.setDeclaringMethod(aMethod);
				localInfo.setField(field);
				localInfo.setUnit(unit);
				
				localsLeftOfField.put(local.toString(), localInfo);
			}
		}
		
		return localsLeftOfField;
	}
	
	// In
	public Map<String, LocalInfo> localsLeftOfInvoke(SootMethod aMethod) {
		Map<String, LocalInfo> localsLeftOfInvoke = new HashMap<String, LocalInfo>();
		Map<String, Local> locals = locals(aMethod);
		
		List<Unit> units = units(aMethod);
		
		for (Unit unit : units) {
			if (isInvokeInRightSide(unit)) {
				JAssignStmt stmt = (JAssignStmt)unit;
				Value leftVal = stmt.getLeftOp();
				Local local = locals.get(leftVal.toString());
				SootMethod method = stmt.getInvokeExpr().getMethod();
				LocalInfo localInfo = new InvokeIn();
				localInfo.setLocal(local);
				localInfo.setDeclaringMethod(aMethod);
				localInfo.setMethod(method);
				localInfo.setUnit(unit);
				
				localsLeftOfInvoke.put(local.toString(), localInfo);
			}
		}
		
		return localsLeftOfInvoke;
	}
	
	// Out
	public Map<String, LocalInfo> localsOfInvokeParam(SootMethod aMethod) {
		Map<String, LocalInfo> localsOfInvokeParam = new HashMap<String, LocalInfo>();
		Map<String, Local> locals = locals(aMethod);
		
		List<Unit> units = units(aMethod);
		
		for (Unit unit : units) {
			if (unit instanceof JAssignStmt) {
				JAssignStmt stmt = (JAssignStmt)unit;
				if (stmt.containsInvokeExpr()) {
					InvokeExpr invokeExpr = stmt.getInvokeExpr();
					List<Value> args = invokeExpr.getArgs();
					int argNum = 0;
					for (Value arg : args) {
						String key = arg.toString();
						if (locals.containsKey(key)) {
							Local local = locals.get(key);
							LocalInfo localInfo = new InvokeParamOut();
							localInfo.setLocal(local);
							localInfo.setDeclaringMethod(aMethod);
							localInfo.setMethod(invokeExpr.getMethod());
							localInfo.setParamNum(argNum);
							localInfo.setUnit(unit);

							localsOfInvokeParam.put(local.toString(), localInfo);
						}
						
						argNum++;
					}
				}
			}
			else if (unit instanceof JInvokeStmt) {
				JInvokeStmt stmt = (JInvokeStmt)unit;
				if (stmt.containsInvokeExpr()) {
					InvokeExpr invokeExpr = stmt.getInvokeExpr();
					List<Value> args = invokeExpr.getArgs();
					int argNum = 0;
					for (Value arg : args) {
						String key = arg.toString();
						if (locals.containsKey(key)) {
							Local local = locals.get(key);
							LocalInfo localInfo = new InvokeParamOut();
							localInfo.setLocal(local);
							localInfo.setDeclaringMethod(aMethod);
							localInfo.setMethod(invokeExpr.getMethod());
							localInfo.setParamNum(argNum);
							localInfo.setUnit(unit);

							localsOfInvokeParam.put(local.toString(), localInfo);
						}
						
						argNum++;
					}
				}
			}
		}
		
		return localsOfInvokeParam;
	}
	
	// Out
	public Map<String, LocalInfo> localsRightOfField(SootMethod aMethod) {
		Map<String, LocalInfo> localsRightOfField = new HashMap<String, LocalInfo>();
		Map<String, Local> locals = locals(aMethod);
		
		List<Unit> units = units(aMethod);

		for (Unit unit : units) {
			if (isFieldInLeftSide(unit)) {
				JAssignStmt stmt = (JAssignStmt)unit;
				Value rightVal = stmt.getRightOp();
				String key = rightVal.toString();
				if (locals.containsKey(key)) {
					Local local = locals.get(key);
					SootField field = stmt.getFieldRef().getField();
					LocalInfo localInfo = new FieldOut();
					localInfo.setLocal(local);
					localInfo.setDeclaringMethod(aMethod);
					localInfo.setField(field);
					localInfo.setUnit(unit);

					localsRightOfField.put(local.toString(), localInfo);
				}
			}
		}
		
		return localsRightOfField;
	}
	
	// Out
	public Map<String, LocalInfo> localOfReturn(SootMethod aMethod) {
		Map<String, LocalInfo> localOfReturn = new HashMap<String, LocalInfo>();
		Map<String, Local> locals = locals(aMethod);
		
		List<Unit> units = units(aMethod);
		
		for (Unit unit : units) {
			if (unit instanceof JReturnStmt) {
				JReturnStmt stmt = (JReturnStmt)unit;
				Value returnVal = stmt.getOp();
				String key = returnVal.toString();
				if (locals.containsKey(key)) {
					Local local = locals.get(key);
					LocalInfo localInfo = new ReturnOut();
					localInfo.setLocal(local);
					localInfo.setDeclaringMethod(aMethod);
					localInfo.setUnit(unit);

					localOfReturn.put(local.toString(), localInfo);
				}
			}
		}
		
		return localOfReturn;
	}
	
	public Map<String, LocalInfo> typeFilterOfLocalMap(Map<String, LocalInfo> aLocalInfoMap, 
			SootClass aType, Hierarchy hierarchy, Map<String, SootClass> classMap) {
		Map<String, LocalInfo> filteredMap = new HashMap<String, LocalInfo>();
		
		for (String key : aLocalInfoMap.keySet()) {
			LocalInfo localInfo = aLocalInfoMap.get(key);
			Type localType = localInfo.local().getType();
			SootClass localTypeClass = typeToClass(localType, classMap);
			
			if (isSubtypeIncluding(localTypeClass, aType, hierarchy)) {
				filteredMap.put(key, localInfo);
			}
		}
		
		return filteredMap;
	}
	
	public List<String> methodStrsInto(SootMethod aMethod, CallGraph cg) {
		List<String> callers = new ArrayList<String>();
		
		Iterator<Edge> iter = cg.edgesInto(aMethod);
		while (iter.hasNext()) {
			callers.add(iter.next().src().getSignature());
		}
		
		return callers;
	}
	
//	Map<String, LocalInfo> localsLeftOfFieldMap = typeFilterOfLocalMap(localsLeftOfField(aMethod), aType, hierarchy, classMap);
//	Map<String, LocalInfo> localsLeftOfInvokeMap = typeFilterOfLocalMap(localsLeftOfInvoke(aMethod), aType, hierarchy, classMap);
//	
//	Map<String, LocalInfo> localsOfInvokeParamMap = typeFilterOfLocalMap(localsOfInvokeParam(aMethod), aType, hierarchy, classMap);
//	Map<String, LocalInfo> localsRightOfFieldMap = typeFilterOfLocalMap(localsRightOfField(aMethod), aType, hierarchy, classMap);
	
	public MethodInternalPath analyzeMethodParamToReturn(SootMethod aMethod, SootClass aType, Hierarchy hierarchy, Map<String, SootClass> classMap) {
		MethodInternalPath mip = new MethodInternalPath();
		PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
		
		Map<String, LocalInfo> localsOfMethodParamMap = typeFilterOfLocalMap(localsOfMethodParam(aMethod), aType, hierarchy, classMap);
		Map<String, LocalInfo> localOfReturnMap = typeFilterOfLocalMap(localOfReturn(aMethod), aType, hierarchy, classMap);
		
		if (!localOfReturnMap.isEmpty()) {
			LocalInfo localInfoOfReturn = localOfReturnMap.values().iterator().next();
			Local localOfReturn = localInfoOfReturn.local();

			PointsToSet set1 = pta.reachingObjects(localOfReturn);

			if (!localsOfMethodParamMap.isEmpty()) {
				for (LocalInfo localInfoOfMethodParam : localsOfMethodParamMap.values()) {
					Local localOfMethodParam = localInfoOfMethodParam.local();

					PointsToSet set2 = pta.reachingObjects(localOfMethodParam);

					if (set1.hasNonEmptyIntersection(set2)) {
						mip.setMethodParamToReturn(localInfoOfMethodParam.paramNum());
						break;
					}
				}
			}

		}
		return mip;
	}
	
	public SootClass typeToClass(Type aType, Map<String, SootClass> classMap) {
		SootClass result = null;
		String key = aType.toString();
		if (classMap.containsKey(key)) {
			result = classMap.get(key);
		}
		return result;
	}
	
	public Map<LocalInfo, LocalInfo> internalEdges(SootMethod aMethod, SootClass aType, Hierarchy hierarchy, Map<String, SootClass> classMap) {
		Map<LocalInfo, LocalInfo> edges = new HashMap<LocalInfo, LocalInfo>();
		List<LocalInfo> starts = new ArrayList<LocalInfo>();
		List<LocalInfo> ends = new ArrayList<LocalInfo>();
		
		Map<String, LocalInfo> methodParamInMap = typeFilterOfLocalMap(localsOfMethodParam(aMethod), aType, hierarchy, classMap);
		Map<String, LocalInfo> fieldInMap = typeFilterOfLocalMap(localsLeftOfField(aMethod), aType, hierarchy, classMap);
		Map<String, LocalInfo> invokeInMap = typeFilterOfLocalMap(localsLeftOfInvoke(aMethod), aType, hierarchy, classMap);
		Map<String, LocalInfo> creationMap = typeFilterOfLocalMap(creations(aMethod), aType, hierarchy, classMap);
		
		starts.addAll(methodParamInMap.values());
		starts.addAll(fieldInMap.values());
		starts.addAll(invokeInMap.values());
		starts.addAll(creationMap.values());
		
		Map<String, LocalInfo> returnOutMap = typeFilterOfLocalMap(localOfReturn(aMethod), aType, hierarchy, classMap);
		Map<String, LocalInfo> invokeParamOutMap = typeFilterOfLocalMap(localsOfInvokeParam(aMethod), aType, hierarchy, classMap);
		Map<String, LocalInfo> fieldOutMap = typeFilterOfLocalMap(localsRightOfField(aMethod), aType, hierarchy, classMap);
		
		ends.addAll(returnOutMap.values());
		ends.addAll(invokeParamOutMap.values());
		ends.addAll(fieldOutMap.values());
		
		for (LocalInfo start : starts) {
			for (LocalInfo end : ends) {
				if (isConnected(start, end)) {
					edges.put(start, end);
				}
			}
		}
		
		return edges;
	}
	
	public MethodInfo analyzeMethod(SootMethod aMethod, SootClass aType, Hierarchy hierarchy, Map<String, SootClass> classMap) {
		MethodInfo info = new MethodInfo();
		
		Map<LocalInfo, LocalInfo> edges = new HashMap<LocalInfo, LocalInfo>();
		List<LocalInfo> starts = new ArrayList<LocalInfo>();
		List<LocalInfo> ends = new ArrayList<LocalInfo>();
		
		Map<String, LocalInfo> methodParamInMap = typeFilterOfLocalMap(localsOfMethodParam(aMethod), aType, hierarchy, classMap);
		Map<String, LocalInfo> fieldInMap = typeFilterOfLocalMap(localsLeftOfField(aMethod), aType, hierarchy, classMap);
		Map<String, LocalInfo> invokeInMap = typeFilterOfLocalMap(localsLeftOfInvoke(aMethod), aType, hierarchy, classMap);
		Map<String, LocalInfo> creationMap = typeFilterOfLocalMap(creations(aMethod), aType, hierarchy, classMap);
		
		starts.addAll(methodParamInMap.values());
		starts.addAll(fieldInMap.values());
		starts.addAll(invokeInMap.values());
		starts.addAll(creationMap.values());
		
		Map<String, LocalInfo> returnOutMap = typeFilterOfLocalMap(localOfReturn(aMethod), aType, hierarchy, classMap);
		Map<String, LocalInfo> invokeParamOutMap = typeFilterOfLocalMap(localsOfInvokeParam(aMethod), aType, hierarchy, classMap);
		Map<String, LocalInfo> fieldOutMap = typeFilterOfLocalMap(localsRightOfField(aMethod), aType, hierarchy, classMap);
		
		ends.addAll(returnOutMap.values());
		ends.addAll(invokeParamOutMap.values());
		ends.addAll(fieldOutMap.values());
		
		for (LocalInfo start : starts) {
			for (LocalInfo end : ends) {
				if (isConnected(start, end)) {
					edges.put(start, end);
				}
			}
		}
		
		info.setMethodParamIn(methodParamInMap);
		info.setFieldIn(fieldInMap);
		info.setInvokeIn(invokeInMap);
		
		info.setCreation(creationMap);
		
		info.setReturnOut(returnOutMap);
		info.setInvokeParamOut(invokeParamOutMap);
		info.setFieldOut(fieldOutMap);
		
		info.setInternalEdges(edges);
		
		return info;
	}
	
	public Map<SootMethod, MethodInfo> methodInfoMap(SootClass aType, Map<String, SootClass> classMap, Hierarchy hierarchy) {
		Map<SootMethod, MethodInfo> methodInfoMap = new HashMap<SootMethod, MethodInfo>();
		
		for (SootClass aClass : classMap.values()) {
			for (SootMethod aMethod : aClass.getMethods()) {
				methodInfoMap.put(aMethod, analyzeMethod(aMethod, aType, hierarchy, classMap));
			}
		}
		
		return methodInfoMap;
	}
	
	public Map<SootField, LocalInfo> fieldInfoMap(SootClass aType, Map<String, SootClass> classMap, Hierarchy hierarchy) {
		Map<SootField, LocalInfo> fieldInfoMap = new HashMap<SootField, LocalInfo>();
		
		for (SootClass aClass : classMap.values()) {
			for (SootField aField : aClass.getFields()) {
				SootClass fieldType = null;
				String key = aField.getType().toString();
				if (classMap.containsKey(key)) {
					fieldType = classMap.get(key);
				}
				if ((fieldType != null) && (isSubtypeIncluding(fieldType, aType, hierarchy))) {
					LocalInfo localInfo = new Field();
					localInfo.setDeclaringField(aField);
					fieldInfoMap.put(aField, localInfo);
				}
			}
		}

		return fieldInfoMap;
	}
	
	public Map<LocalInfo, LocalInfo> invokeParamOut_to_methodParamIn(Map<SootMethod, MethodInfo> methodInfoMap, CallGraph cg) {
		Map<LocalInfo, LocalInfo> edges = new HashMap<LocalInfo, LocalInfo>();
		
		for (MethodInfo methodInfo : methodInfoMap.values()) {
			Map<String, LocalInfo> methodParamInMap = methodInfo.methodParamIn();
			for (LocalInfo methodParamIn : methodParamInMap.values()) {
				Iterator<Edge> edgeIter = cg.edgesInto(methodParamIn.declaringMethod());
				while (edgeIter.hasNext()) {
					Edge edge = edgeIter.next();
					SootMethod caller = edge.src();
					MethodInfo methodInfoOfCaller = methodInfoMap.get(caller);
					Map<String, LocalInfo> invokeParamOutMap = methodInfoOfCaller.invokeParamOut();
					
					for (LocalInfo invokeParamOut : invokeParamOutMap.values()) {
						if (invokeParamOut.method().equals(methodParamIn.declaringMethod()) &&
								invokeParamOut.paramNum() == methodParamIn.paramNum()) {
							edges.put(invokeParamOut, methodParamIn);
						}
					}
				}
			}
		}
		
		return edges;
	}
	
	public Map<LocalInfo, LocalInfo> returnOut_to_InvokeIn(Map<SootMethod, MethodInfo> methodInfoMap) {
		Map<LocalInfo, LocalInfo> edges = new HashMap<LocalInfo, LocalInfo>();
		
		for (MethodInfo methodInfo : methodInfoMap.values()) {
			Map<String, LocalInfo> invokeInMap = methodInfo.invokeIn();
			for (LocalInfo invokeIn : invokeInMap.values()) {
				SootMethod callee = invokeIn.method();
				MethodInfo methodInfoOfCallee = methodInfoMap.get(callee);
				for (LocalInfo returnOut : methodInfoOfCallee.returnOut().values()) {
					edges.put(returnOut, invokeIn);
				}
			}
		}
		
		return edges;
	}
	
	public Map<LocalInfo, LocalInfo> field_to_fieldIn(Map<SootMethod, MethodInfo> methodInfoMap, Map<SootField, LocalInfo> fieldInfoMap) {
		Map<LocalInfo, LocalInfo> edges = new HashMap<LocalInfo, LocalInfo>();
		
		for (MethodInfo methodInfo : methodInfoMap.values()) {
			Map<String, LocalInfo> fieldInMap = methodInfo.fieldIn();
			for (LocalInfo fieldIn : fieldInMap.values()) {
				SootField field = fieldIn.field();
				LocalInfo fieldInfo = fieldInfoMap.get(field);
				edges.put(fieldInfo, fieldIn);
			}
		}
		
		return edges;
	}
	
	public Map<LocalInfo, LocalInfo> fieldOut_to_field(Map<SootMethod, MethodInfo> methodInfoMap, Map<SootField, LocalInfo> fieldInfoMap) {
		Map<LocalInfo, LocalInfo> edges = new HashMap<LocalInfo, LocalInfo>();
		
		for (MethodInfo methodInfo : methodInfoMap.values()) {
			Map<String, LocalInfo> fieldOutMap = methodInfo.fieldOut();
			for (LocalInfo fieldOut : fieldOutMap.values()) {
				SootField field = fieldOut.field();
				LocalInfo fieldInfo = fieldInfoMap.get(field);
				edges.put(fieldOut, fieldInfo);
			}
		}
		
		return edges;
	}
	
	public ReferenceFlowGraph referenceFlowGraph(SootClass aType, Map<String, SootClass> classMap, Hierarchy hierarchy, CallGraph cg) {
		ReferenceFlowGraph graph = new ReferenceFlowGraph();		
		Map<SootMethod, MethodInfo> methodInfoMap = methodInfoMap(aType, classMap, hierarchy);
		Map<SootField, LocalInfo> fieldInfoMap = fieldInfoMap(aType, classMap, hierarchy);
		
		for (MethodInfo methodInfo : methodInfoMap.values()) {
			Map<LocalInfo, LocalInfo> internalEdges = methodInfo.internalEdges();
			for (LocalInfo from : internalEdges.keySet()) {
				graph.addEdge(from, internalEdges.get(from));
			}
		}
		
		Map<LocalInfo, LocalInfo> invokeParamOut_to_methodParamIn = invokeParamOut_to_methodParamIn(methodInfoMap, cg);
		for (LocalInfo from : invokeParamOut_to_methodParamIn.keySet()) {
			graph.addEdge(from, invokeParamOut_to_methodParamIn.get(from));
		}
		
		Map<LocalInfo, LocalInfo> returnOut_to_InvokeIn = returnOut_to_InvokeIn(methodInfoMap);
		for (LocalInfo from : returnOut_to_InvokeIn.keySet()) {
			graph.addEdge(from, returnOut_to_InvokeIn.get(from));
		}
		
		Map<LocalInfo, LocalInfo> field_to_fieldIn = field_to_fieldIn(methodInfoMap, fieldInfoMap);
		for (LocalInfo from : field_to_fieldIn.keySet()) {
			graph.addEdge(from, field_to_fieldIn.get(from));
		}
		
		Map<LocalInfo, LocalInfo> fieldOut_to_field = fieldOut_to_field(methodInfoMap, fieldInfoMap);
		for (LocalInfo from : fieldOut_to_field.keySet()) {
			graph.addEdge(from, fieldOut_to_field.get(from));
		}
		
		for (MethodInfo methodInfo : methodInfoMap.values()) {
			graph.addStartNodes(methodInfo.creation().values());
		}
		
		return graph;
	}
	
	public Map<LocalInfoNode, List<Path<LocalInfoNode>>> referenceFlows(SootClass aType, 
			Map<String, SootClass> classMap, Hierarchy hierarchy, CallGraph cg) {
		Map<LocalInfoNode, List<Path<LocalInfoNode>>> result = new HashMap<LocalInfoNode, List<Path<LocalInfoNode>>>();
		ReferenceFlowGraph graph = referenceFlowGraph(aType, classMap, hierarchy, cg);
		List<LocalInfoNode> startNodes = graph.startNodes();
		
		for (LocalInfoNode startNode : startNodes) {
			GraphPathCollector<LocalInfoNode> pathCollector = new ReverseAllPathCollector<LocalInfoNode>(startNode, graph);
			List<Path<LocalInfoNode>> pathList = pathCollector.run();
			result.put(startNode, pathList);
		}
		
		return result;
	}
	
	
	public boolean isConnected(LocalInfo a, LocalInfo b) {
		boolean result = false;
		
		if ((a != null) && (b != null)) {
			PointsToAnalysis pta = Scene.v().getPointsToAnalysis();

			PointsToSet set1 = pta.reachingObjects(a.local());
			PointsToSet set2 = pta.reachingObjects(b.local());

			if (set1.hasNonEmptyIntersection(set2)) {
				result = true;
			}
		}
		
		return result;
	}
	
	public Map<SootClass, List<Creator>> creators(SootMethod aMethod, SootClass aType, Map<String, SootClass> classMap, Hierarchy hierarchy) {
		Map<SootClass, List<Creator>> creators = new HashMap<SootClass, List<Creator>>();
		List<Unit> units = units(aMethod);
		String newExprClassStr = "class soot.jimple.internal.JNewExpr";
		
		for (Unit unit : units) {
			if (unit instanceof JAssignStmt) {
				JAssignStmt stmt = (JAssignStmt)unit;
				Value rightVal = stmt.getRightOp();
				if (rightVal.getClass().toString().equals(newExprClassStr)) {
					Type type = rightVal.getType();
					SootClass typeClass = classMap.get(type.toString());
					if (isSubtypeIncluding(typeClass, aType, hierarchy)) {
						Creator creator = new Creator(unit, aType, aMethod.getDeclaringClass(), aMethod, typeClass);
						
						if (creators.containsKey(aType)) {
							List<Creator> creatorList = creators.get(aType);
							creatorList.add(creator);
							creators.put(aType, creatorList);
						}
						else {
							List<Creator> creatorList = new ArrayList<Creator>();
							creatorList.add(creator);
							creators.put(aType, creatorList);
						}
					}
				}
			}
		}
		
		return creators;
	}
	
	public boolean isFieldInRightSide(Unit unit) {
		boolean result = false;
		
		if (unit instanceof JAssignStmt) {
			String instanceFieldRef = "class soot.jimple.internal.JInstanceFieldRef";
			String staticFieldRef = "class soot.jimple.StaticFieldRef";
			
			JAssignStmt stmt = (JAssignStmt)unit;	
			String classString = stmt.getRightOp().getClass().toString();
				
			if (classString.equals(instanceFieldRef) || classString.equals(staticFieldRef)) {
				result = true;
			}
		}
		
		return result;
	}
	
	public boolean isFieldInLeftSide(Unit unit) {
		boolean result = false;
		
		if (unit instanceof JAssignStmt) {
			String instanceFieldRef = "class soot.jimple.internal.JInstanceFieldRef";
			String staticFieldRef = "class soot.jimple.StaticFieldRef";
			
			JAssignStmt stmt = (JAssignStmt)unit;	
			String classString = stmt.getLeftOp().getClass().toString();
				
			if (classString.equals(instanceFieldRef) || classString.equals(staticFieldRef)) {
				result = true;
			}
		}
		
		return result;
	}
	
	public boolean isInvokeInRightSide(Unit unit) {
		boolean result = false;
		
		if (unit instanceof JAssignStmt) {
			String virtualInvoke = "class soot.jimple.internal.JVirtualInvokeExpr";
			String staticInvoke = "class soot.jimple.internal.JStaticInvokeExpr";
			
			JAssignStmt stmt = (JAssignStmt)unit;
			String classString = stmt.getRightOp().getClass().toString();
			
			if (classString.equals(virtualInvoke) || classString.equals(staticInvoke)) {
				result = true;
			}
		}
		
		return result;
	}
	
	public List<Unit> units(SootMethod aMethod) {
		List<Unit> units = new ArrayList<Unit>();
		
		if (aMethod.hasActiveBody()) {
			Body body = aMethod.getActiveBody();
			units.addAll(body.getUnits());
		}
		
		return units;
	}
	
	public Map<String, Local> locals(SootMethod aMethod) {
		Map<String, Local> locals = new HashMap<String, Local>();
		
		if (aMethod.hasActiveBody()) {
			Body body = aMethod.getActiveBody();
			Iterator<Local> localIter = body.getLocals().iterator();
			
			while(localIter.hasNext()) {
				Local local = localIter.next();
				locals.put(local.toString(), local); 
			}
		}
		
		return locals;
	}
	
	public boolean isSubtypeIncluding(SootClass a, SootClass b, Hierarchy hierarchy) {
		boolean result = false;
		
		if (!(a == null) && !(b == null)) {
			boolean isAInterface = a.isInterface();
			boolean isBInterface = b.isInterface();

			if (isAInterface && isBInterface) {
				if (hierarchy.isInterfaceSubinterfaceOf(a, b) || a.equals(b)) {
					result = true;
				}
			} 
			else if (!isAInterface && isBInterface) {
				List<SootClass> implementers = hierarchy.getImplementersOf(b);
				if (implementers.contains(a)) {
					result = true;
				}
			}
			else if (isAInterface && !isBInterface) {
				// do nothing
			}
			else if (!isAInterface && !isBInterface) {
				if (hierarchy.isClassSubclassOfIncluding(a, b)) {
					result = true;
				}
			}
		}
		
		return result;
	}


	public MethodInternalPath analyzeMethodParamToReturn2(SootMethod aMethod, SootClass aType, Hierarchy hierarchy, Map<String, SootClass> classMap, ReferenceFlowGraph refGraph) {
		MethodInternalPath mip = new MethodInternalPath();
		PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
		
		Map<String, LocalInfo> localsOfMethodParamMap = typeFilterOfLocalMap(localsOfMethodParam(aMethod), aType, hierarchy, classMap);
		Map<String, LocalInfo> localOfReturnMap = typeFilterOfLocalMap(localOfReturn(aMethod), aType, hierarchy, classMap);
		
		if (!localOfReturnMap.isEmpty()) {
			LocalInfo localInfoOfReturn = localOfReturnMap.values().iterator().next();
			Local localOfReturn = localInfoOfReturn.local();

			PointsToSet set1 = pta.reachingObjects(localOfReturn);

			if (!localsOfMethodParamMap.isEmpty()) {
				for (LocalInfo localInfoOfMethodParam : localsOfMethodParamMap.values()) {
					Local localOfMethodParam = localInfoOfMethodParam.local();

					PointsToSet set2 = pta.reachingObjects(localOfMethodParam);

					if (set1.hasNonEmptyIntersection(set2)) {
						mip.setMethodParamToReturn(localInfoOfMethodParam.paramNum());
						
						refGraph.addEdge(localInfoOfMethodParam, localInfoOfReturn);
						break;
					}
				}
			}

		}
		return mip;
	}
}
