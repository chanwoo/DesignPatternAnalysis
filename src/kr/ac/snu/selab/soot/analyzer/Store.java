package kr.ac.snu.selab.soot.analyzer;

import soot.SootClass;
import soot.SootField;

public class Store extends Role {
	
	public Store() {
		super();
		setRoleName("Store");
	}

	public Store(SootClass anInterfaceType, SootClass aRelatedClass, SootField aRelatedField) {
		super();
		setRoleName("Store");
		setInterfaceType(anInterfaceType);
		setDeclaringClass(aRelatedClass);
		setDeclaringField(aRelatedField);
	}

}
