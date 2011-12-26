package kr.ac.snu.selab.soot.analyzer;

import java.util.HashSet;
import java.util.Set;

import kr.ac.snu.selab.soot.graph.MetaInfo;

public class RoleRepository {
	private Set<MetaInfo> callers;
	private Set<MetaInfo> creators;
	private Set<MetaInfo> injectors;
	private Set<MetaInfo> stores;
	
	public RoleRepository() {
		callers = new HashSet<MetaInfo>();
		creators = new HashSet<MetaInfo>();
		injectors = new HashSet<MetaInfo>();
		stores = new HashSet<MetaInfo>();
	}
	
	public Set<MetaInfo> callers() {
		return callers;
	}
	
	public Set<MetaInfo> creators() {
		return creators;
	}
	
	public Set<MetaInfo> injectors() {
		return injectors;
	}
	
	public Set<MetaInfo> stores() {
		return stores;
	}
	
	public void addCaller(MetaInfo metaInfo) {
		callers.add(metaInfo);
	}
	
	public void addCraetor(MetaInfo metaInfo) {
		creators.add(metaInfo);
	}
	
	public void addInjector(MetaInfo metaInfo) {
		injectors.add(metaInfo);
	}
	
	public void addStore(MetaInfo metaInfo) {
		stores.add(metaInfo);
	}
}
