package kr.ac.snu.selab.soot.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kr.ac.snu.selab.soot.graph.MyNode;
import kr.ac.snu.selab.soot.graph.Path;
import kr.ac.snu.selab.soot.util.MyUtil;

public class StatePatternAnalysisResult extends AnalysisResult {
	Map<String, Set<Path<MyNode>>> triggeringPathMap;

	public String getAbstractTypeName() {
		String result = "";
		if (abstractType != null) {
			result = abstractType.toString();
		}
		return result;
	}

	public StatePatternAnalysisResult() {
		abstractType = null;
		callerList = new ArrayList<MyNode>();
		creatorList = new ArrayList<MyNode>();
		referenceFlowPathMap = new HashMap<String, List<Path<MyNode>>>();
		triggeringPathMap = new HashMap<String, Set<Path<MyNode>>>();
	}

	public String toXML() {
		String result = "";
		result = result + "<AnalysisResult>";
		result = result + "<AbstractType>";
		result = result + MyUtil.removeBracket(getAbstractTypeName());
		result = result + "</AbstractType>";
		result = result + "<CallerList>";
		for (MyNode aNode : callerList) {
			result = result + aNode.toXML();
		}
		result = result + "</CallerList>";
		result = result + "<ObjectFlowPathSet>";
		for (MyNode aNode : callerList) {
			String key = aNode.toString();
			if (referenceFlowPathMap.containsKey(key)) {
				// String patternName = null;
				result = result + "<ObjectFlowPathPerCaller>";
				result = result + "<Caller>";
				result = result + aNode.toXML();
				result = result + "</Caller>";
				result = result + "<Hit>";
				result = result
						+ ((triggeringPathMap.size() < 1) ? "no hit" : "hit");
				result = result + "</Hit>";
				if (triggeringPathMap.containsKey(key)) {
					result = result + "<TriggerPathList>";
					for (Path<MyNode> aPath : triggeringPathMap.get(key)) {
						result = result + aPath.toXML();
					}
					result = result + "</TriggerPathList>";
				}
				result = result + "<PathList>";
				for (Path<MyNode> aPath : referenceFlowPathMap.get(key)) {
					result = result + aPath.toXML();
				}
				result = result + "</PathList>";
				result = result + "</ObjectFlowPathPerCaller>";
			}
		}
		result = result + "</ObjectFlowPathSet>";
		// result = result + "<CreatorList>";
		// for (MyNode aNode : creatorList) {
		// result = result + aNode.toXML();
		// }
		// result = result + "</CreatorList>";
		// result = result + "<StoreList>";
		// for (Store aStore : storeList) {
		// result = result + aStore.toXML();
		// }
		// result = result + "</StoreList>";
		result = result + "</AnalysisResult>";
		return result;
	}

}
