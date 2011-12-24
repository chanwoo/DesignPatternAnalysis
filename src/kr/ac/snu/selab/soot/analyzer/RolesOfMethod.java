package kr.ac.snu.selab.soot.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.SootClass;

public class RolesOfMethod {
	private Map<SootClass, List<Creator>> creators;
	private Map<SootClass, List<Caller>> callers;
	private Map<SootClass, List<Injector>> injectors;
	
	public RolesOfMethod() {
		creators = new HashMap<SootClass, List<Creator>>();
		callers = new HashMap<SootClass, List<Caller>>();
		injectors = new HashMap<SootClass, List<Injector>>();
	}

	public Map<SootClass, List<Creator>> creators() {
		return creators;
	}
	
	public void setCreators(Map<SootClass, List<Creator>> someCreators) {
		creators = someCreators;
	}
	
	public Map<SootClass, List<Caller>> callers() {
		return callers;
	}
	
	public void setCallers(Map<SootClass, List<Caller>> someCallers) {
		callers = someCallers;
	}
	
	public Map<SootClass, List<Injector>> injectors() {
		return injectors;
	}
	
	public void setInjectors(Map<SootClass, List<Injector>> someInjectors) {
		injectors = someInjectors;
	}
}
