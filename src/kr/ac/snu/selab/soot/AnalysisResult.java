package kr.ac.snu.selab.soot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.ac.snu.selab.soot.analyzer.MyGraph;
import kr.ac.snu.selab.soot.analyzer.MyNode;
import kr.ac.snu.selab.soot.analyzer.MyPath;

import soot.SootClass;

public class AnalysisResult {
	SootClass abstractType;
	List<MyNode> callerList;
	List<MyNode> creatorList;
	Map<String, List<MyPath>> referenceFlowPathMap; // String := MyNode.toString() where MyNode is caller
//	List<Store> storeList;
	
	public AnalysisResult() {
		abstractType = null;
		callerList = new ArrayList<MyNode>();
		creatorList = new ArrayList<MyNode>();
		referenceFlowPathMap = new HashMap<String, List<MyPath>>();
//		storeList = new ArrayList<Store>();
	}
	
//	public AnalysisResult(SootClass anAbstractType, List<Caller> aCallerList,
//			List<Creator> aCreatorList, List<Store> aStoreList) {
//		abstractType = anAbstractType;
//		callerList = aCallerList;
//		creatorList = aCreatorList;
//		storeList = aStoreList;
//	}
	
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
		result = result + "<ReferenceFlowPathList>";
		for (MyNode aNode : callerList) {
			String key = aNode.toString();
			if (referenceFlowPathMap.containsKey(key)) {
				result = result + aNode.toXML();
				for (MyPath aPath : referenceFlowPathMap.get(key)) {
					result = result + aPath.toXML();
				}
			}
		}
		result = result + "</ReferenceFlowPathList>";
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
