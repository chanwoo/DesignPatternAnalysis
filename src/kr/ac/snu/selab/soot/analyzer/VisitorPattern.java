package kr.ac.snu.selab.soot.analyzer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kr.ac.snu.selab.soot.callgraph.MetaInfoCallGraph;
import kr.ac.snu.selab.soot.graph.MetaInfo;
import kr.ac.snu.selab.soot.graph.Path;
import soot.Hierarchy;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;

public class VisitorPattern extends PatternAnalysis {

	public PatternAnalysisResult perform(Map<String, SootClass> classMap, Hierarchy hierarchy, CallGraph cg, AnalysisUtil au) {

		PatternAnalysisResult result = new PatternAnalysisResult();
		result.setPatternName("Visitor");
		
		Set<SootClass> interfaceTypes = au.interfaceTypes(classMap);

		for (SootClass interfaceType : interfaceTypes) {
			
			RoleRepository roles = new RoleRepository();
			Map<String, MetaInfo> metaInfoMap = au.metaInfoMap(classMap.values());
			Set<Path<MetaInfo>> abstractReferenceFlows = new HashSet<Path<MetaInfo>>();

			abstractReferenceFlows = au.abstractReferenceFlows(interfaceType, classMap, hierarchy, cg, metaInfoMap, roles);
	
			result.addReferenceFlowsPerType(interfaceType, abstractReferenceFlows);
			
			for (MetaInfo metaInfoOfCaller : roles.callers()) {
				for (Role role : metaInfoOfCaller.callers()) {
					Caller caller = (Caller)role;
					SootMethod calledMethod = caller.calledMethod();
					SootClass callerClass = caller.declaringClass();
					if (au.doesHaveParam(calledMethod, callerClass, classMap)) {
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
		
		return result;
	}
}
