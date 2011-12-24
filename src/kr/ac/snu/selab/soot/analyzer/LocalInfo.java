package kr.ac.snu.selab.soot.analyzer;

import soot.Local;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;

public class LocalInfo {
	
	private Local local;
	private SootMethod declaringMethod;
	private SootMethod method;
	private SootField field;
	private Unit unit;
	private int paramNum;
	
	public LocalInfo() {
		local = null;
		declaringMethod = null;
		method = null;
		field = null;
		unit = null;
		paramNum = -1;
	}
	
	public SootMethod declaringMethod() {
		return declaringMethod;
	}
	
	public void setDeclaringMethod(SootMethod aMethod) {
		declaringMethod = aMethod;
	}
	
	public void setLocal(Local aLocal) {
		local = aLocal;
	}
	
	public void setMethod(SootMethod aMethod) {
		method = aMethod;
	}
	
	public void setField(SootField aField) {
		field = aField;
	}
	
	public void setUnit(Unit aUnit) {
		unit = aUnit;
	}
	
	public Local local() {
		return local;
	}
	
	public SootMethod method() {
		return method;
	}
	
	public SootField field() {
		return field;
	}
	
	public Unit unit() {
		return unit;
	}
	
	public int paramNum() {
		return paramNum;
	}
	
	public void setParamNum(int i) {
		paramNum = i;
	}

}
