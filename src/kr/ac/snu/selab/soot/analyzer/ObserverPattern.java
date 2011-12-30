package kr.ac.snu.selab.soot.analyzer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kr.ac.snu.selab.soot.graph.MetaInfo;
import soot.Hierarchy;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;

public class ObserverPattern extends PatternAnalysis {

	public PatternAnalysisResult perform(Map<String, SootClass> classMap, Hierarchy hierarchy, CallGraph cg, AnalysisUtil au) {

		PatternAnalysisResult result = new PatternAnalysisResult();
		result.hierarchy(hierarchy);
		result.setPatternName("Observer");

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
					if (au.doesHaveCollection(callerClass, superClassMap)) {
						if (calledMethod.getParameterCount() == 1) {
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
						else {
							Set<LocalInfo> delegateInfoSet = new HashSet<LocalInfo>();
							delegateInfoSet = au.delegateInfos(calledMethod, classMap, superClassMap);
							for (LocalInfo delegateInfo : delegateInfoSet) {
								SootClass delegateType = au.typeToClass(delegateInfo.local().getType(), classMap);
								if ((delegateType != null) && (au.isSubtypeIncluding(delegateType, callerClass, hierarchy))) {
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
			}
		}

		return result;
	}
}
