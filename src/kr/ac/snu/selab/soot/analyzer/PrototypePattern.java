package kr.ac.snu.selab.soot.analyzer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kr.ac.snu.selab.soot.graph.MetaInfo;
import kr.ac.snu.selab.soot.graph.Path;
import soot.Hierarchy;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;

public class PrototypePattern extends PatternAnalysis {

	public PatternAnalysisResult perform(Map<String, SootClass> classMap, Hierarchy hierarchy, CallGraph cg, AnalysisUtil au) {

		PatternAnalysisResult result = new PatternAnalysisResult();
		result.setPatternName("Prototype");
		
		Set<SootClass> interfaceTypes = au.interfaceTypes(classMap);

		for (SootClass interfaceType : interfaceTypes) {
			
			RoleRepository roles = new RoleRepository();
			Map<String, MetaInfo> metaInfoMap = au.metaInfoMap(classMap.values());
			Set<Path<MetaInfo>> abstractReferenceFlows = new HashSet<Path<MetaInfo>>();

			abstractReferenceFlows = au.abstractReferenceFlows(interfaceType, classMap, hierarchy, cg, metaInfoMap, roles);
	
			result.addReferenceFlowsPerType(interfaceType, abstractReferenceFlows);
			
			au.analyzeRole(interfaceType, metaInfoMap, roles, classMap, hierarchy);
			
			for (MetaInfo callerMetaInfo : roles.callers()) {
				for (Role role : callerMetaInfo.callers()) {
					Caller caller = (Caller)role;
					SootMethod calledMethod = caller.calledMethod();
					SootClass returnType = au.typeToClass(calledMethod.getReturnType(), classMap);
					int numOfParam = calledMethod.getParameterCount();
					if ((numOfParam == 0) && (interfaceType.equals(returnType))) {
						result.addInterfaceType(interfaceType);
						if (result.rolesPerType().containsKey(interfaceType)) {
							result.rolesPerType().get(interfaceType).addCaller(callerMetaInfo);
						}
						else {
							RoleRepository relatedRoles = new RoleRepository();
							relatedRoles.addCaller(callerMetaInfo);
							result.addRoles(interfaceType, relatedRoles);
						}
					}
				}
			}
		}
		
		return result;
	}
}
