package kr.ac.snu.selab.soot.analyzer;

import soot.SootField;

public class MyField extends MyNode {
	SootField fieldObject;
	
	public MyField(SootField aField) {
		fieldObject = aField;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return fieldObject.toString();
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

}
