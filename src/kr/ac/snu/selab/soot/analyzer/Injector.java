package kr.ac.snu.selab.soot.analyzer;

import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;

public class Injector extends Role {
	
	public Injector() {
		super();
		setRoleName("Injector");
	}

	public Injector(Unit aUnit, SootClass anInterfaceType, SootClass aRelatedClass, SootMethod aRelatedMethod, SootField aRelatedField, SootClass aConcreteType) {
		super(aUnit, anInterfaceType);
		setRoleName("Injector");
		setRclass(aRelatedClass);
		setRmethod(aRelatedMethod);
		setRfield(aRelatedField);
		setConcreteType(aConcreteType);
	}	
}
