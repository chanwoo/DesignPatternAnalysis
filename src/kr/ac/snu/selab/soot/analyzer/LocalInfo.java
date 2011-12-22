package kr.ac.snu.selab.soot.analyzer;

import soot.Local;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;

public class LocalInfo {
	
	private Local local;
	private SootMethod method;
	private SootField field;
	private Unit unit;
	
	public LocalInfo() {
		local = null;
		method = null;
		field = null;
		unit = null;
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

}
