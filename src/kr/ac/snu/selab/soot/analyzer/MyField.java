package kr.ac.snu.selab.soot.analyzer;

import kr.ac.snu.selab.soot.MyUtil;
import soot.SootField;

public class MyField extends MyNode {
	SootField fieldObject;
	
	public boolean equals(Object anObject) {
		return fieldObject.equals(anObject);
	}
	
	public int hashcode() {
		return fieldObject.hashCode();
	}
	
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
		String result = "";
		result = result + "<Field>";
		result = result + "<ToString>";
		result = result + MyUtil.removeBracket(toString());
		result = result + "</ToString>";
		result = result + "<Role>";
		result = result + role();
		result = result + "</Role>";
		result = result + "</Field>";
		return result;
	}
	
	public boolean isCreator() {
		return false;
	}
	
	public boolean isCaller() {
		return false;
	}
	
	public String role() {
		return "";
	}

}
