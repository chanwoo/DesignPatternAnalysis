package kr.ac.snu.selab.soot.analyzer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kr.ac.snu.selab.soot.graph.MetaInfo;
import kr.ac.snu.selab.soot.graph.Path;
import soot.Hierarchy;
import soot.SootClass;
import soot.jimple.toolkits.callgraph.CallGraph;

public class DecoratorPattern extends PatternAnalysis {

	public PatternAnalysisResult perform(Map<String, SootClass> classMap, Hierarchy hierarchy, CallGraph cg, AnalysisUtil au) {

		PatternAnalysisResult result = new PatternAnalysisResult();
		result.setPatternName("Composite");
		
		Set<SootClass> interfaceTypes = au.interfaceTypes(classMap);

		for (SootClass interfaceType : interfaceTypes) {
			
			RoleRepository roles = new RoleRepository();
			Map<String, MetaInfo> metaInfoMap = au.metaInfoMap(classMap.values());
			Set<Path<MetaInfo>> abstractReferenceFlows = new HashSet<Path<MetaInfo>>();

			abstractReferenceFlows = au.abstractReferenceFlows(interfaceType, classMap, hierarchy, cg, metaInfoMap, roles);
	
			result.addReferenceFlowsPerType(interfaceType, abstractReferenceFlows);
			
			au.analyzeRole(interfaceType, metaInfoMap, roles, classMap, hierarchy);
			
			for (MetaInfo metaInfoOfCaller : roles.callers()) {
				for (Role role : metaInfoOfCaller.callers()) {
					Caller caller = (Caller)role;
					SootClass callerClass = caller.declaringClass();
					if (au.isSubtypeIncluding(callerClass, interfaceType, hierarchy)) {
						if (!hierarchy.getDirectSubclassesOf(callerClass).isEmpty()) {
							result.addInterfaceType(interfaceType);
							if (result.rolesPerType().containsKey(interfaceType)) {
								result.rolesPerType().get(interfaceType).addCaller(metaInfoOfCaller);
							}
							else {
								RoleRepository relatedRoles = new RoleRepository();
								relatedRoles.addCaller(metaInfoOfCaller);
								result.addRoles(interfaceType, relatedRoles);
							}
						}
					}
				}
			}
		}
		
		return result;
	}
}
