package kr.ac.snu.selab.soot.analyzer;

import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

public class Creator extends Role {	
	
	public Creator() {
		super();
		setRoleName("Creator");
	}
	
	public Creator(Unit aUnit, SootClass anInterfaceType, SootClass aRelatedClass, SootMethod aRelatedMethod, SootClass aConcreteType) {
		super(aUnit, anInterfaceType);
		setRoleName("Creator");
		setRclass(aRelatedClass);
		setRmethod(aRelatedMethod);
		setConcreteType(aConcreteType);
	}

}
