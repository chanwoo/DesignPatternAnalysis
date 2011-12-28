package kr.ac.snu.selab.soot.analyzer;

import java.util.Map;
import java.util.Set;

import kr.ac.snu.selab.soot.graph.MetaInfo;
import soot.Hierarchy;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;

public class AdapterPattern extends PatternAnalysis {

	public PatternAnalysisResult perform(Map<String, SootClass> classMap, Hierarchy hierarchy, CallGraph cg, AnalysisUtil au) {

		PatternAnalysisResult result = new PatternAnalysisResult();
		result.setPatternName("Adapter");
		
		Set<SootClass> interfaceTypes = au.interfaceTypes(classMap);

		for (SootClass interfaceType : interfaceTypes) {
			
			RoleRepository roles = new RoleRepository();
			Map<String, MetaInfo> metaInfoMap = au.metaInfoMap(classMap.values());
			//Set<Path<MetaInfo>> abstractReferenceFlows = new HashSet<Path<MetaInfo>>();

			//abstractReferenceFlows = au.abstractReferenceFlows(interfaceType, classMap, hierarchy, cg, metaInfoMap, roles);

			//result.addReferenceFlowsPerType(interfaceType, abstractReferenceFlows);

			au.analyzeRole(interfaceType, metaInfoMap, roles, classMap, hierarchy, cg);
			Map<SootClass, Set<SootClass>> superClassMap = au.superClassMap(classMap, hierarchy);

			for (MetaInfo metaInfoOfCaller : roles.callers()) {
				for (Role role : metaInfoOfCaller.callers()) {
					Caller caller = (Caller)role;
					SootClass callerClass = caller.declaringClass();
					SootMethod calledMethod = caller.calledMethod();
					if (!au.doesHaveCollection(callerClass, superClassMap) && 
							!au.doesHaveCollection(interfaceType, superClassMap) &&
							!au.doesHaveCollection(calledMethod.getDeclaringClass(), superClassMap)) {
						if (au.isDelegateMethod(calledMethod, classMap, superClassMap)) {
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
