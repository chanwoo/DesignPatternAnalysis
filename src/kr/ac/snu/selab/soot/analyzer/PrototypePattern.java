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

public class PrototypePattern extends PatternAnalysis {

	public PatternAnalysisResult perform(Map<String, SootClass> classMap, Hierarchy hierarchy, CallGraph cg, AnalysisUtil au) {

		PatternAnalysisResult result = new PatternAnalysisResult();
		result.setPatternName("Prototype");
		
		Set<SootClass> interfaceTypes = au.interfaceTypes(classMap);

		for (SootClass interfaceType : interfaceTypes) {
			
			RoleRepository roles = new RoleRepository();
			Map<String, MetaInfo> metaInfoMap = au.metaInfoMap(classMap.values());
			Set<Path<MetaInfo>> abstractReferenceFlows = new HashSet<Path<MetaInfo>>();
			MetaInfoCallGraph metaInfoCallGraph = au.metaInfoCallGraph(cg, metaInfoMap);

			abstractReferenceFlows = au.abstractReferenceFlows(interfaceType, classMap, hierarchy, cg, metaInfoMap, roles);
	
			for (MetaInfo creatorMetaInfo : roles.creators()) {
				for (Role creator : creatorMetaInfo.creators()) {
					if (creator.declaringClass().equals(creator.concreteType()) &&
							au.isSubtypeIncluding(creator.declaringClass(), interfaceType, hierarchy)) {
						Set<MetaInfo> setOfCreatorMetaInfo = new HashSet<MetaInfo>();
						setOfCreatorMetaInfo.add(creatorMetaInfo);

						if (au.doesCall(roles.callers(), setOfCreatorMetaInfo, metaInfoCallGraph)) {
							result.addInterfaceType(interfaceType);
							if (result.rolesPerType().containsKey(interfaceType)) {
								result.rolesPerType().get(interfaceType).addCreator(creatorMetaInfo);
							}
							else {
								RoleRepository relatedRoles = new RoleRepository();
								relatedRoles.addCreator(creatorMetaInfo);
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
