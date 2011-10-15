package kr.ac.snu.selab.soot.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.ac.snu.selab.soot.graph.MyNode;
import kr.ac.snu.selab.soot.graph.MyPath;
import kr.ac.snu.selab.soot.util.MyUtil;


import soot.SootClass;

public class AnalysisResult {
	SootClass abstractType;
	List<MyNode> callerList;
	List<MyNode> creatorList;
	Map<String, List<MyPath>> referenceFlowPathMap; // String := MyNode.toString() where MyNode is caller
//	List<Store> storeList;
	Map<MyPath, List<MyPath>> creatorTriggerPathMap; // key := referenceFlowPath, value := triggerPath 
	
	public String getAbstractTypeName() {
		String result = "";
		if (abstractType != null) {
			result = abstractType.toString();
		}
		return result;
	}
	
	public AnalysisResult() {
		abstractType = null;
		callerList = new ArrayList<MyNode>();
		creatorList = new ArrayList<MyNode>();
		referenceFlowPathMap = new HashMap<String, List<MyPath>>();
//		storeList = new ArrayList<Store>();
		creatorTriggerPathMap = new HashMap<MyPath, List<MyPath>>();
	}
	
//	public AnalysisResult(SootClass anAbstractType, List<Caller> aCallerList,
//			List<Creator> aCreatorList, List<Store> aStoreList) {
//		abstractType = anAbstractType;
//		callerList = aCallerList;
//		creatorList = aCreatorList;
//		storeList = aStoreList;
//	}
	
	public boolean hasDesignPattern() {
		return true;
		//return !creatorTriggerPathMap.isEmpty();
	}
	
	public String toXML() {
		String result = "";
		result = result + "<AnalysisResult>";
		result = result + "<AbstractType>";
		result = result + MyUtil.removeBracket(abstractType.toString());
		result = result + "</AbstractType>";
		result = result + "<CallerList>";
		for (MyNode aNode : callerList) {
			result = result + aNode.toXML();
		}
		result = result + "</CallerList>";
		result = result + "<ReferenceFlowList>";
		for (MyNode aNode : callerList) {
			String key = aNode.toString();
			if (referenceFlowPathMap.containsKey(key)) {
				String patternName = null;
				result = result + "<ReferenceFlowPerCaller>";
				result = result + "<Caller>";
				result = result + aNode.toXML();
				result = result + "</Caller>";
				result = result + "<PathSetList>";
				for (MyPath aPath : referenceFlowPathMap.get(key)) {
					result = result + "<PathSet>";
					result = result + aPath.toXML();
					if (creatorTriggerPathMap.containsKey(aPath)) {
						patternName = "State";
						result = result + "<TriggerPathList>";
						for (MyPath aTriggerPath : creatorTriggerPathMap.get(aPath)) {
							result = result + aTriggerPath.toXML();
						}
						result = result + "</TriggerPathList>";
					}
					result = result + "</PathSet>";
				}
				result = result + "</PathSetList>";
				if (patternName != null) {
					result = result + "<Pattern>" + patternName + "</Pattern>";
				}
				result = result + "</ReferenceFlowPerCaller>";
			}
		}
		result = result + "</ReferenceFlowList>";
		result = result + "<CreatorList>";
		for (MyNode aNode : creatorList) {
			result = result + aNode.toXML();
		}
		result = result + "</CreatorList>";
//		result = result + "<StoreList>";
//		for (Store aStore : storeList) {
//			result = result + aStore.toXML();
//		}
//		result = result + "</StoreList>";
		result = result + "</AnalysisResult>";
		return result;
	}
}
