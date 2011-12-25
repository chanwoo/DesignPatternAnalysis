package kr.ac.snu.selab.soot.analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kr.ac.snu.selab.soot.graph.GraphPathCollector;
import kr.ac.snu.selab.soot.graph.MetaInfo;
import kr.ac.snu.selab.soot.graph.Path;
import kr.ac.snu.selab.soot.graph.collectors.ReferenceFlowCollector;
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
import soot.ValueBox;
import soot.jimple.InvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;



public class AnalysisUtil {
	
	public AnalysisUtil() {
		
	}
	
	public Map<String, MetaInfo> metaInfoMap(Collection<SootClass> classes) {
		Map<String, MetaInfo> metaInfoMap = new HashMap<String, MetaInfo>();
		for (SootClass aClass : classes) {
			for (SootMethod aMethod : aClass.getMethods()) {
				MetaInfo metaInfo = new MetaInfo(aMethod);
				metaInfoMap.put(aMethod.getSignature(), metaInfo);
			}
			for (SootField aField : aClass.getFields()) {
				MetaInfo metaInfo = new MetaInfo(aField);
				metaInfoMap.put(aField.getSignature(), metaInfo);
			}
		}
		
		return metaInfoMap;
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

					creations.put(localInfo.toString(), localInfo);	
				}
			}
		}

		return creations;
	}
	
	// Call
	public Map<String, LocalInfo> calls(SootMethod aMethod, SootClass aType, Map<String, SootClass> classMap) {
		Map<String, LocalInfo> calls = new HashMap<String, LocalInfo>();
		Map<String, Local> locals = locals(aMethod);
		
		String virtualInvoke = "class soot.jimple.internal.JVirtualInvokeExpr";
		String interfaceInvoke = "class soot.jimple.internal.JInterfaceInvokeExpr";
		
		List<Unit> units = units(aMethod);
		
		for (Unit unit : units) {
			if (unit instanceof JAssignStmt) {
				JAssignStmt stmt = (JAssignStmt)unit;
				if (stmt.containsInvokeExpr()) {
					String classString = stmt.getInvokeExpr().getClass().toString();
					if (classString.equals(virtualInvoke) || classString.equals(interfaceInvoke)) {
						Value receiver = ((ValueBox)stmt.getInvokeExpr().getUseBoxes().get(0)).getValue();
						Local receiverLocal = locals.get(receiver.toString());
						SootClass receiverType = typeToClass(receiverLocal.getType(), classMap);
						if (receiverType.equals(aType)) {
							LocalInfo localInfo = new Call();
							localInfo.setLocal(receiverLocal);
							localInfo.setDeclaringMethod(aMethod);
							localInfo.setMethod(stmt.getInvokeExpr().getMethod());
							localInfo.setUnit(unit);

							calls.put(localInfo.toString(), localInfo);
						}
					}
				}
			}
			else if (unit instanceof JInvokeStmt) {
				JInvokeStmt stmt = (JInvokeStmt)unit;
				if (stmt.containsInvokeExpr()) {
					String classString = stmt.getInvokeExpr().getClass().toString();
					if (classString.equals(virtualInvoke) || classString.equals(interfaceInvoke)) {
						Value receiver = ((ValueBox)stmt.getInvokeExpr().getUseBoxes().get(0)).getValue();
						Local receiverLocal = locals.get(receiver.toString());
						SootClass receiverType = typeToClass(receiverLocal.getType(), classMap);
						if (receiverType.equals(aType)) {
							LocalInfo localInfo = new Call();
							localInfo.setLocal(receiverLocal);
							localInfo.setDeclaringMethod(aMethod);
							localInfo.setMethod(stmt.getInvokeExpr().getMethod());
							localInfo.setUnit(unit);

							calls.put(localInfo.toString(), localInfo);
						}
					}
				}
			}
		}
		
		
		return calls;
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
				localsOfMethodParam.put(localInfo.toString(), localInfo);
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
				
				localsLeftOfField.put(localInfo.toString(), localInfo);
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
				
				localsLeftOfInvoke.put(localInfo.toString(), localInfo);
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

							localsOfInvokeParam.put(localInfo.toString(), localInfo);
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

							localsOfInvokeParam.put(localInfo.toString(), localInfo);
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

					localsRightOfField.put(localInfo.toString(), localInfo);
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

					localOfReturn.put(localInfo.toString(), localInfo);
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
	
	public List<Pair<LocalInfo, LocalInfo>> internalEdges(SootMethod aMethod, SootClass aType, Hierarchy hierarchy, Map<String, SootClass> classMap) {
		List<Pair<LocalInfo, LocalInfo>> edges = new ArrayList<Pair<LocalInfo, LocalInfo>>();
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
					Pair<LocalInfo, LocalInfo> pair = new Pair<LocalInfo, LocalInfo>(start, end);
					edges.add(pair);
				}
			}
		}
		
		return edges;
	}
	
	public MethodInfo analyzeMethod(SootMethod aMethod, SootClass aType, Hierarchy hierarchy, Map<String, SootClass> classMap) {
		MethodInfo info = new MethodInfo();
		
		List<Pair<LocalInfo, LocalInfo>> edges = new ArrayList<Pair<LocalInfo, LocalInfo>>();
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
		Map<String, LocalInfo> callMap = calls(aMethod, aType, classMap);
		
		ends.addAll(returnOutMap.values());
		ends.addAll(invokeParamOutMap.values());
		ends.addAll(fieldOutMap.values());
		ends.addAll(callMap.values());
		
		for (LocalInfo start : starts) {
			for (LocalInfo end : ends) {
				if (isConnected(start, end)) {
					Pair<LocalInfo, LocalInfo> pair = new Pair<LocalInfo, LocalInfo>(start, end);
					edges.add(pair);
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
		
		info.setCall(callMap);
		
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
				if ((fieldType != null) && (fieldType.equals(aType))) {
					LocalInfo localInfo = new Field();
					localInfo.setDeclaringField(aField);
					fieldInfoMap.put(aField, localInfo);
				}
			}
		}

		return fieldInfoMap;
	}
	
	public List<Pair<LocalInfo, LocalInfo>> invokeParamOut_to_methodParamIn(Map<SootMethod, MethodInfo> methodInfoMap, CallGraph cg) {
		List<Pair<LocalInfo, LocalInfo>> edges = new ArrayList<Pair<LocalInfo, LocalInfo>>();
		
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
							Pair<LocalInfo, LocalInfo> pair = new Pair<LocalInfo, LocalInfo>(invokeParamOut, methodParamIn);
							edges.add(pair);
						}
					}
				}
			}
		}
		
		return edges;
	}
	
	public List<Pair<LocalInfo, LocalInfo>> returnOut_to_InvokeIn(Map<SootMethod, MethodInfo> methodInfoMap) {
		List<Pair<LocalInfo, LocalInfo>> edges = new ArrayList<Pair<LocalInfo, LocalInfo>>();
		
		for (MethodInfo methodInfo : methodInfoMap.values()) {
			Map<String, LocalInfo> invokeInMap = methodInfo.invokeIn();
			for (LocalInfo invokeIn : invokeInMap.values()) {
				SootMethod callee = invokeIn.method();
				MethodInfo methodInfoOfCallee = methodInfoMap.get(callee);
				for (LocalInfo returnOut : methodInfoOfCallee.returnOut().values()) {
					Pair<LocalInfo, LocalInfo> pair = new Pair<LocalInfo, LocalInfo>(returnOut, invokeIn);
					edges.add(pair);
				}
			}
		}
		
		return edges;
	}
	
	public List<Pair<LocalInfo, LocalInfo>> field_to_fieldIn(Map<SootMethod, MethodInfo> methodInfoMap, Map<SootField, LocalInfo> fieldInfoMap) {
		List<Pair<LocalInfo, LocalInfo>> edges = new ArrayList<Pair<LocalInfo, LocalInfo>>();
		
		for (MethodInfo methodInfo : methodInfoMap.values()) {
			Map<String, LocalInfo> fieldInMap = methodInfo.fieldIn();
			for (LocalInfo fieldIn : fieldInMap.values()) {
				SootField field = fieldIn.field();
				LocalInfo fieldInfo = fieldInfoMap.get(field);
				Pair<LocalInfo, LocalInfo> pair = new Pair<LocalInfo, LocalInfo>(fieldInfo, fieldIn);
				edges.add(pair);
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
	
	public ReferenceFlowGraph referenceFlowGraph(SootClass aType, Map<String, SootClass> classMap, 
			Hierarchy hierarchy, CallGraph cg) {
		ReferenceFlowGraph graph = new ReferenceFlowGraph();		
		Map<SootMethod, MethodInfo> methodInfoMap = methodInfoMap(aType, classMap, hierarchy);
		Map<SootField, LocalInfo> fieldInfoMap = fieldInfoMap(aType, classMap, hierarchy);
		
		for (MethodInfo methodInfo : methodInfoMap.values()) {
			List<Pair<LocalInfo, LocalInfo>> internalEdges = methodInfo.internalEdges();
			for (Pair<LocalInfo, LocalInfo> pair : internalEdges) {
				graph.addEdge(pair.first(), pair.second());
			}
		}
		
		List<Pair<LocalInfo, LocalInfo>> invokeParamOut_to_methodParamIn = invokeParamOut_to_methodParamIn(methodInfoMap, cg);
		for (Pair<LocalInfo, LocalInfo> pair : invokeParamOut_to_methodParamIn) {
			graph.addEdge(pair.first(), pair.second());
		}
		
		List<Pair<LocalInfo, LocalInfo>> returnOut_to_InvokeIn = returnOut_to_InvokeIn(methodInfoMap);
		for (Pair<LocalInfo, LocalInfo> pair : returnOut_to_InvokeIn) {
			graph.addEdge(pair.first(), pair.second());
		}
		
		List<Pair<LocalInfo, LocalInfo>> field_to_fieldIn = field_to_fieldIn(methodInfoMap, fieldInfoMap);
		for (Pair<LocalInfo, LocalInfo> pair : field_to_fieldIn) {
			graph.addEdge(pair.first(), pair.second());
		}
		
		Map<LocalInfo, LocalInfo> fieldOut_to_field = fieldOut_to_field(methodInfoMap, fieldInfoMap);
		for (LocalInfo from : fieldOut_to_field.keySet()) {
			graph.addEdge(from, fieldOut_to_field.get(from));
		}
		
		for (MethodInfo methodInfo : methodInfoMap.values()) {
			graph.addStartNodes(methodInfo.creation().values());
		}
		
		for (MethodInfo methodInfo : methodInfoMap.values()) {
			graph.addEndNodes(methodInfo.call().values());
		}
		
		return graph;
	}
	
	public Map<LocalInfoNode, List<Path<LocalInfoNode>>> referenceFlows(SootClass aType, 
			Map<String, SootClass> classMap, Hierarchy hierarchy, CallGraph cg) {
		Map<LocalInfoNode, List<Path<LocalInfoNode>>> result = new HashMap<LocalInfoNode, List<Path<LocalInfoNode>>>();
		ReferenceFlowGraph graph = referenceFlowGraph(aType, classMap, hierarchy, cg);
		List<LocalInfoNode> startNodes = graph.startNodes();
		
		for (LocalInfoNode startNode : startNodes) {
			GraphPathCollector<LocalInfoNode> pathCollector = new ReferenceFlowCollector<LocalInfoNode>(startNode, graph);
			List<Path<LocalInfoNode>> pathList = pathCollector.run();
			result.put(startNode, pathList);
		}
		
		return result;
	}
	
	public MetaInfo getMetaInfo(LocalInfo localInfo, Map<String, MetaInfo> metaInfoMap) {
		MetaInfo result = null;
		
		if (localInfo.declaringMethod() != null) {
			result = metaInfoMap.get(localInfo.declaringMethod().getSignature());
		}
		else if (localInfo.declaringField() != null) {
			result = metaInfoMap.get(localInfo.declaringField().getSignature());
		}
		
		return result;
	}
	
	public Set<Path<MetaInfo>> abstractReferenceFlows(SootClass aType, 
			Map<String, SootClass> classMap, Hierarchy hierarchy, CallGraph cg, 
			Map<String, MetaInfo> metaInfoMap) {
		Set<Path<MetaInfo>> absFlowSet = new HashSet<Path<MetaInfo>>();
		
		Map<LocalInfoNode, List<Path<LocalInfoNode>>> referenceFlows = referenceFlows(aType, classMap, hierarchy, cg);
		
		for (List<Path<LocalInfoNode>> list : referenceFlows.values()) {
			for (Path<LocalInfoNode> path : list) {
				Path<MetaInfo> newPath = new Path<MetaInfo>();
				for (LocalInfoNode node : path.getNodeList()) {
					LocalInfo localInfo = (LocalInfo)node.getElement();
					MetaInfo metaInfo = getMetaInfo(localInfo, metaInfoMap);
					
					if (newPath.isEmpty()) {
						// Creator
						newPath.add(metaInfo);
						if (!metaInfo.isCreator()) {
							Creator creator = new Creator();
							creator.setInterfaceType(aType);
							metaInfo.addRole(creator);
						}
					}
					else {
						// Store Check
						if (!metaInfo.isStore()) {
							if (localInfo.declaringField() != null) {
								Store store = new Store();
								store.setInterfaceType(aType);
								metaInfo.addRole(store);     
							}
						}
						// Caller Check
						if (!metaInfo.isCaller()) {
							if (localInfo instanceof Call) {
								Caller caller = new Caller();
								caller.setInterfaceType(aType);
								metaInfo.addRole(caller);
							}
						}
						
						if (!metaInfo.getElement().equals(newPath.last().getElement())) {
							newPath.add(metaInfo);
						}
					}
				}
				// Injector Check
				List<MetaInfo> metaInfoList = newPath.getNodeList();
				int index = 0;
				for (MetaInfo metaInfo : metaInfoList) {
					if (metaInfo.isStore()) {
						int injectorIndex = index - 2;
						if (injectorIndex > 0) {
							MetaInfo injectorSuspect = metaInfoList.get(injectorIndex);
							if ((!injectorSuspect.isInjector()) && 
									(injectorSuspect.getElement() instanceof SootMethod)) {
								Injector injector = new Injector();
								injector.setInterfaceType(aType);
								injectorSuspect.addRole(injector);
							}
						}
					}
					
					index++;
				}
				
				absFlowSet.add(newPath);
			}
		}
		
		return absFlowSet;
	}
	
	public boolean isCaller(LocalInfo localInfo, SootClass aType, Map<String, SootClass> classMap) {
		boolean result = false;
		
		String virtualInvoke = "class soot.jimple.internal.JVirtualInvokeExpr";
		String interfaceInvoke = "class soot.jimple.internal.JInterfaceInvokeExpr";
		
		String category = localInfo.category();
		if ((category.equals("in_invoke")) || (category.equals("out_invokeParam"))) {
			SootMethod declaringMethod = localInfo.declaringMethod();
			Map<String, Local> locals = locals(declaringMethod);
			Unit unit = localInfo.unit();
			if (unit instanceof JAssignStmt) {
				JAssignStmt stmt = (JAssignStmt)unit;
				String classString = stmt.getInvokeExpr().getClass().toString();
				if (classString.equals(virtualInvoke) || classString.equals(interfaceInvoke)) {
					Value receiver = ((ValueBox)stmt.getInvokeExpr().getUseBoxes().get(0)).getValue();
					Local receiverLocal = locals.get(receiver.toString());
					SootClass receiverType = typeToClass(receiverLocal.getType(), classMap);
					if (receiverType.equals(aType)) {
						result = true;
					}
				}
			}
			else if (unit instanceof JInvokeStmt) {
				JInvokeStmt stmt = (JInvokeStmt)unit;
				String classString = stmt.getInvokeExpr().getClass().toString();
				if (classString.equals(virtualInvoke) || classString.equals(interfaceInvoke)) {
					Value receiver = ((ValueBox)stmt.getInvokeExpr().getUseBoxes().get(0)).getValue();
					Local receiverLocal = locals.get(receiver.toString());
					SootClass receiverType = typeToClass(receiverLocal.getType(), classMap);
					if (receiverType.equals(aType)) {
						result = true;
					}
				}
			}
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
			String interfaceInvoke = "class soot.jimple.internal.JInterfaceInvokeExpr";
			String staticInvoke = "class soot.jimple.internal.JStaticInvokeExpr";
			
			JAssignStmt stmt = (JAssignStmt)unit;
			String classString = stmt.getRightOp().getClass().toString();
			
			if (classString.equals(virtualInvoke) || classString.equals(staticInvoke) || 
					classString.equals(interfaceInvoke)) {
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
