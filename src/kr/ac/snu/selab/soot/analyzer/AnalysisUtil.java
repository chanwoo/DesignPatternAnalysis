package kr.ac.snu.selab.soot.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
						Local local = locals.get(arg.toString());
						LocalInfo localInfo = new InvokeParamOut();
						localInfo.setLocal(local);
						localInfo.setDeclaringMethod(aMethod);
						localInfo.setMethod(invokeExpr.getMethod());
						localInfo.setParamNum(argNum);
						localInfo.setUnit(unit);

						localsOfInvokeParam.put(local.toString(), localInfo);
						
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
						Local local = locals.get(arg.toString());
						LocalInfo localInfo = new InvokeParamOut();
						localInfo.setLocal(local);
						localInfo.setDeclaringMethod(aMethod);
						localInfo.setMethod(invokeExpr.getMethod());
						localInfo.setParamNum(argNum);
						localInfo.setUnit(unit);

						localsOfInvokeParam.put(local.toString(), localInfo);
						
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
				Local local = locals.get(rightVal.toString());
				SootField field = stmt.getFieldRef().getField();
				LocalInfo localInfo = new FieldOut();
				localInfo.setLocal(local);
				localInfo.setDeclaringMethod(aMethod);
				localInfo.setField(field);
				localInfo.setUnit(unit);
				
				localsRightOfField.put(local.toString(), localInfo);
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
				Local local = locals.get(returnVal.toString());
				LocalInfo localInfo = new ReturnOut();
				localInfo.setLocal(local);
				localInfo.setDeclaringMethod(aMethod);
				localInfo.setUnit(unit);
				
				localOfReturn.put(local.toString(), localInfo);
			}
		}
		
		return localOfReturn;
	}
	
	public Map<String, LocalInfo> typeFilterOfLocalMap(Map<String, LocalInfo> aLocalInfoMap, 
			SootClass aType, Hierarchy hierarchy, Map<String, SootClass> classMap) {
		Map<String, LocalInfo> filteredMap = new HashMap<String, LocalInfo>();
		
		for (Entry<String, LocalInfo> entry : aLocalInfoMap.entrySet()) {
			LocalInfo localInfo = entry.getValue();
			Type localType = localInfo.local().getType();
			SootClass localTypeClass = classMap.get(localType.toString());
			
			if (isSubtypeIncluding(localTypeClass, aType, hierarchy)) {
				filteredMap.put(entry.getKey(), localInfo);
			}
		}
		
		return filteredMap;
	}
	
	public List<String> methodStrsInto(SootMethod aMethod, CallGraph cg) {
		List<String> callers = new ArrayList();
		
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
	
//	public Map<LocalInfo, LocalInfo> methodParam_return
	
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
