package kr.ac.snu.selab.soot.analyzer;

import java.util.List;
import java.util.Map;

public class MethodInfo {
	private Map<String, LocalInfo> methodParamIn;
	private Map<String, LocalInfo> fieldIn;
	private Map<String, LocalInfo> invokeIn;
	
	private Map<String, LocalInfo> creation;
	private Map<String, LocalInfo> call;
	
	private Map<String, LocalInfo> returnOut;
	private Map<String, LocalInfo> invokeParamOut;
	private Map<String, LocalInfo> fieldOut;
	
	private List<Pair<LocalInfo, LocalInfo>> internalEdges;
	
	public MethodInfo() {
			
	}
	
	public Map<String, LocalInfo> methodParamIn() {
		return methodParamIn;
	}
	
	public void setMethodParamIn(Map<String, LocalInfo> aMap) {
		methodParamIn = aMap;
	}
	
	public Map<String, LocalInfo> fieldIn() {
		return fieldIn;
	}
	
	public void setFieldIn(Map<String, LocalInfo> aMap) {
		fieldIn = aMap;
	}
	
	public Map<String, LocalInfo> invokeIn() {
		return invokeIn;
	}
	
	public void setInvokeIn(Map<String, LocalInfo> aMap) {
		invokeIn = aMap;
	}
	
	public Map<String, LocalInfo> creation() {
		return creation;
	}
	
	public void setCreation(Map<String, LocalInfo> aMap) {
		creation = aMap;
	}
	
	public Map<String, LocalInfo> call() {
		return call;
	}
	
	public void setCall(Map<String, LocalInfo> aMap) {
		call = aMap;
	}
	
	public Map<String, LocalInfo> returnOut() {
		return returnOut;
	}
	
	public void setReturnOut(Map<String, LocalInfo> aMap) {
		returnOut = aMap;
	}
	
	public Map<String, LocalInfo> invokeParamOut() {
		return invokeParamOut;
	}
	
	public void setInvokeParamOut(Map<String, LocalInfo> aMap) {
		invokeParamOut = aMap;
	}
	
	public Map<String, LocalInfo> fieldOut() {
		return fieldOut;
	}
	
	public void setFieldOut(Map<String, LocalInfo> aMap) {
		fieldOut = aMap;
	}
	
	public List<Pair<LocalInfo, LocalInfo>> internalEdges() {
		return internalEdges;
	}
	
	public void setInternalEdges(List<Pair<LocalInfo, LocalInfo>> edges) {
		internalEdges = edges;
	}

}
