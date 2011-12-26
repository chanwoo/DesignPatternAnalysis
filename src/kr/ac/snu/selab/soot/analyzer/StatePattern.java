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

public class StatePattern extends PatternAnalysis {

	public PatternAnalysisResult perform(Map<String, SootClass> classMap, Hierarchy hierarchy, CallGraph cg, AnalysisUtil au) {

		PatternAnalysisResult result = new PatternAnalysisResult();
		result.setPatternName("State");
		
		Set<SootClass> abstractClasses = au.abstractClasses(classMap);

		for (SootClass absType : abstractClasses) {
			RoleRepository roles = new RoleRepository();
			Map<String, MetaInfo> metaInfoMap = au.metaInfoMap(classMap.values());
			Set<Path<MetaInfo>> abstractReferenceFlows = new HashSet<Path<MetaInfo>>();
			MetaInfoCallGraph metaInfoCallGraph = au.metaInfoCallGraph(cg, metaInfoMap);

			abstractReferenceFlows = au.abstractReferenceFlows(absType, classMap, hierarchy, cg, metaInfoMap, roles);

			if (au.doesCall(roles.callers(), roles.injectors(), metaInfoCallGraph)) {
				result.setPatternExistence(true);
			}
		}
		
		return result;
	}
}
