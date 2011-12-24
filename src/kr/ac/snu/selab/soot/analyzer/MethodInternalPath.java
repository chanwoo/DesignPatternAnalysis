package kr.ac.snu.selab.soot.analyzer;

import soot.SootClass;
import soot.SootMethod;

public class MethodInternalPath {
	private SootMethod method;
	private SootClass type;
	
	private Map<int, LocalInfo> methodParam;
	
	private int methodParamToReturn;
	
	public MethodInternalPath() {
		method = null;
		type = null;
		methodParamToReturn = -1;
	}
	
	public SootMethod method() {
		return method;
	}
	
	public void setMethod(SootMethod aMethod) {
		method = aMethod;
	}
	
	public SootClass type() {
		return type;
	}
	
	public void setType(SootClass aClass) {
		type = aClass;
	}
	
	public int methodParamToReturn() {
		return methodParamToReturn;
	}
	
	public void setMethodParamToReturn(int argNumber) {
		methodParamToReturn = argNumber;
	}

}
