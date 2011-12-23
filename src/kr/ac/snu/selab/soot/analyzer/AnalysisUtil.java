package kr.ac.snu.selab.soot.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import soot.Body;
import soot.Hierarchy;
import soot.Local;
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
				LocalInfo localInfo = new LocalInfo();
				localInfo.setLocal(local);
				localInfo.setMethod(aMethod);
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
				LocalInfo localInfo = new LocalInfo();
				localInfo.setLocal(local);
				localInfo.setField(field);
				localInfo.setMethod(aMethod);
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
				LocalInfo localInfo = new LocalInfo();
				localInfo.setLocal(local);
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
					for (Value arg : args) {
						Local local = locals.get(arg.toString());
						LocalInfo localInfo = new LocalInfo();
						localInfo.setLocal(local);
						localInfo.setMethod(invokeExpr.getMethod());
						localInfo.setUnit(unit);

						localsOfInvokeParam.put(local.toString(), localInfo);
					}
				}
			}
			else if (unit instanceof JInvokeStmt) {
				JInvokeStmt stmt = (JInvokeStmt)unit;
				if (stmt.containsInvokeExpr()) {
					InvokeExpr invokeExpr = stmt.getInvokeExpr();
					List<Value> args = invokeExpr.getArgs();
					for (Value arg : args) {
						Local local = locals.get(arg.toString());
						LocalInfo localInfo = new LocalInfo();
						localInfo.setLocal(local);
						localInfo.setMethod(invokeExpr.getMethod());
						localInfo.setUnit(unit);

						localsOfInvokeParam.put(local.toString(), localInfo);
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
				LocalInfo localInfo = new LocalInfo();
				localInfo.setLocal(local);
				localInfo.setField(field);
				localInfo.setMethod(aMethod);
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
				LocalInfo localInfo = new LocalInfo();
				localInfo.setLocal(local);
				localInfo.setMethod(aMethod);
				localInfo.setUnit(unit);
				
				localOfReturn.put(local.toString(), localInfo);
			}
		}
		
		return localOfReturn;
	}
	
	public Map<String, LocalInfo> typeFilter(Map<String, LocalInfo> aLocalInfoMap, 
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
		
		return result;
	}

}
