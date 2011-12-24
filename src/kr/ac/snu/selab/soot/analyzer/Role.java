package kr.ac.snu.selab.soot.analyzer;

import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;

public class Role {
	private String roleName;
	private Unit unit;
	private SootClass rclass;
	private SootMethod rmethod;
	private SootField rfield;
	private SootClass interfaceType;
	private SootClass concreteType;
	
	public Role() {
		setRoleName(null);
		setUnit(null);
		setRclass(null);
		setRmethod(null);
		setRfield(null);
		setInterfaceType(null);
		setConcreteType(null);
	}
	
	public Role(Unit aUnit, SootClass anInterfaceType) {
		setRoleName(null);
		setUnit(aUnit);
		setRclass(null);
		setRmethod(null);
		setRfield(null);
		setInterfaceType(anInterfaceType);
		setConcreteType(null);
	}
	
	public String roleName() {
		return roleName;
	}
	
	public void setRoleName(String aRoleName) {
		roleName = aRoleName;
	}
	
	public Unit unit() {
		return unit;
	}
	
	public void setUnit(Unit aUnit) {
		unit = aUnit;
	}
	
	public SootClass interfaceType() {
		return interfaceType;
	}
	
	public void setInterfaceType(SootClass aType) {
		interfaceType = aType;
	}
	
	public SootClass rclass() {
		return rclass;
	}
	
	public void setRclass(SootClass aClass) {
		rclass = aClass;
	}

	public SootMethod rmethod() {
		return rmethod;
	}
	
	public void setRmethod(SootMethod aMethod) {
		rmethod = aMethod;
	}
	
	public SootField rfield() {
		return rfield;
	}
	
	public void setRfield(SootField aField) {
		rfield = aField;
	}
	
	public SootClass concreteType() {
		return concreteType;
	}
	
	public void setConcreteType(SootClass aType) {
		concreteType = aType;
	}
}
