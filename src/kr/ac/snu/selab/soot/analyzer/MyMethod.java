package kr.ac.snu.selab.soot.analyzer;

import soot.SootMethod;

public class MyMethod extends MyNode {
	SootMethod methodObject;
	
	public MyMethod(SootMethod aMethod) {
		methodObject = aMethod;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return methodObject.toString();
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

}
