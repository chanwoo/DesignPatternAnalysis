package kr.ac.snu.selab.soot.analyzer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kr.ac.snu.selab.soot.callgraph.MetaInfoCallGraph;
import kr.ac.snu.selab.soot.graph.MetaInfo;
import kr.ac.snu.selab.soot.graph.Path;
import soot.Hierarchy;
import soot.SootClass;
import soot.jimple.toolkits.callgraph.CallGraph;

public class StrategyPattern extends PatternAnalysis {

	public PatternAnalysisResult perform(Map<String, SootClass> classMap, Hierarchy hierarchy, CallGraph cg, AnalysisUtil au) {

		PatternAnalysisResult result = new PatternAnalysisResult();
		result.setPatternName("Strategy");
		
		Set<SootClass> interfaceTypes = au.interfaceTypes(classMap);

		for (SootClass interfaceType : interfaceTypes) {
			
			RoleRepository roles = new RoleRepository();
			Map<String, MetaInfo> metaInfoMap = au.metaInfoMap(classMap.values());
			Set<Path<MetaInfo>> abstractReferenceFlows = new HashSet<Path<MetaInfo>>();
			MetaInfoCallGraph metaInfoCallGraph = au.metaInfoCallGraph(cg, metaInfoMap);

			abstractReferenceFlows = au.abstractReferenceFlows(interfaceType, classMap, hierarchy, cg, metaInfoMap, roles);

			result.addReferenceFlowsPerType(interfaceType, abstractReferenceFlows);
						
			if (!au.doesCall(roles.callers(), roles.injectors(), metaInfoCallGraph)) {
				result.addInterfaceType(interfaceType);
				result.addRoles(interfaceType, roles);
			}
		}
		
		return result;
	}
}
