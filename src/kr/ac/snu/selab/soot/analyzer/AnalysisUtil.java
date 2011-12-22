package kr.ac.snu.selab.soot.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.Hierarchy;
import soot.Local;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.internal.JAssignStmt;



public class AnalysisUtil {
	
	public AnalysisUtil() {
		
	}
	
	public Map<String, LocalInfo> paramLocals(SootMethod aMethod) {
		Map<String, LocalInfo> paramLocals = new HashMap<String, LocalInfo>();
		
		if (aMethod.hasActiveBody()) {
			Body body = aMethod.getActiveBody();
			int numOfParams = aMethod.getParameterCount();
			for (int i = 0; i < numOfParams; i++) {
				Local local = body.getParameterLocal(i);
				LocalInfo localInfo = new LocalInfo();
				localInfo.setLocal(local);
				paramLocals.put(local.toString(), localInfo);
			}
		}
		
		return paramLocals;
	}
	
	public Map<String, LocalInfo> fieldLocals(SootMethod aMethod) {
		Map<String, LocalInfo> fieldLocals = new HashMap<String, LocalInfo>();
		Map<String, Local> locals = locals(aMethod);
		
		List<Unit> units = units(aMethod);

		for (Unit unit : units) {
			if (isFieldInRightStmt(unit)) {
				JAssignStmt stmt = (JAssignStmt)unit;
				Value leftVal = stmt.getLeftOp();
				Local local = locals.get(leftVal.toString());
				SootField field = stmt.getFieldRef().getField();
				LocalInfo localInfo = new LocalInfo();
				localInfo.setLocal(local);
				localInfo.setField(field);
				localInfo.setUnit(unit);
				
				fieldLocals.put(local.toString(), localInfo);
			}
		}
		
		return fieldLocals;
	}
	
	public Map<String, LocalInfo> methodLocals(SootMethod aMethod) {
		Map<String, LocalInfo> methodLocals = new HashMap<String, LocalInfo>();
		Map<String, Local> locals = locals(aMethod);
		
		List<Unit> units = units(aMethod);
		
		for (Unit unit : units) {
			if (isInvokeInRightStmt(unit)) {
				JAssignStmt stmt = (JAssignStmt)unit;
				Value leftVal = stmt.getLeftOp();
				Local local = locals.get(leftVal.toString());
				SootMethod method = stmt.getInvokeExpr().getMethod();
				LocalInfo localInfo = new LocalInfo();
				localInfo.setLocal(local);
				localInfo.setMethod(method);
				localInfo.setUnit(unit);
				
				methodLocals.put(local.toString(), localInfo);
			}
		}
		
		return methodLocals;
	}
	
	public boolean isFieldInRightStmt(Unit unit) {
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
	
	public boolean isInvokeInRightStmt(Unit unit) {
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
