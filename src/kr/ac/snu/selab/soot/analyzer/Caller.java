package kr.ac.snu.selab.soot.analyzer;

import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

public class Caller extends Role {
	
	private SootMethod calledMethod;
	
	public Caller() {
		super();
		setRoleName("Caller");
		setCalledMethod(null);
	}

	public Caller(Unit aUnit, SootClass anInterfaceType, SootClass aRelatedClass, SootMethod aRelatedMethod, SootMethod aCalledMethod) {
		super(aUnit, anInterfaceType);
		setRoleName("Caller");
		setDeclaringClass(aRelatedClass);
		setDeclaringMethod(aRelatedMethod);
		setCalledMethod(aCalledMethod);
	}
	
	public SootMethod calledMethod() {
		return calledMethod;
	}
	
	public void setCalledMethod(SootMethod aMethod) {
		calledMethod = aMethod;
	}

}
