package kr.ac.snu.selab.soot.analyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.ac.snu.selab.soot.graph.MyNode;
import kr.ac.snu.selab.soot.graph.Path;
import kr.ac.snu.selab.soot.util.MyUtil;
import kr.ac.snu.selab.soot.util.XMLWriter;
import soot.SootClass;

public class AnalysisResult {
	SootClass abstractType;
	List<MyNode> callerList;
	List<MyNode> creatorList;
	Map<String, List<Path<MyNode>>> referenceFlowPathMap; // String :=
															// MyNode.toString()
															// where MyNode is
															// caller
	// List<Store> storeList;
	Map<Path<MyNode>, List<Path<MyNode>>> creatorTriggerPathMap; // key :=
																	// referenceFlowPath,
																	// value :=
																	// triggerPath

	public String getAbstractTypeName() {
		String result = "";
		if (abstractType != null) {
			result = abstractType.toString();
		}
		return result;
	}

	public void setAbstractType(SootClass aType) {
		abstractType = aType;
	}

	public AnalysisResult() {
		abstractType = null;
		callerList = new ArrayList<MyNode>();
		creatorList = new ArrayList<MyNode>();
		referenceFlowPathMap = new HashMap<String, List<Path<MyNode>>>();
		// storeList = new ArrayList<Store>();
		creatorTriggerPathMap = new HashMap<Path<MyNode>, List<Path<MyNode>>>();
	}

	// public AnalysisResult(SootClass anAbstractType, List<Caller> aCallerList,
	// List<Creator> aCreatorList, List<Store> aStoreList) {
	// abstractType = anAbstractType;
	// callerList = aCallerList;
	// creatorList = aCreatorList;
	// storeList = aStoreList;
	// }

	public boolean hasDesignPattern() {
		return true;
		// return !creatorTriggerPathMap.isEmpty();
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
				for (Path<MyNode> aPath : referenceFlowPathMap.get(key)) {
					result = result + "<PathSet>";
					result = result + aPath.toXML();
					if (creatorTriggerPathMap.containsKey(aPath)) {
						patternName = "State";
						result = result + "<TriggerPathList>";
						for (Path<MyNode> aTriggerPath : creatorTriggerPathMap
								.get(aPath)) {
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
		// result = result + "<StoreList>";
		// for (Store aStore : storeList) {
		// result = result + aStore.toXML();
		// }
		// result = result + "</StoreList>";
		result = result + "</AnalysisResult>";
		return result;
	}

	public void writeXML(XMLWriter writer) {
		try {
			writer.startElement("AnalysisResult");
			writer.simpleElement("AbstractType", abstractType.toString());
			writer.startElement("CallerList");
			for (MyNode aNode : callerList) {
				aNode.writeXML(writer);
			}
			writer.endElement();

			writer.startElement("ReferenceFlowList");
			for (MyNode aNode : callerList) {
				String key = aNode.toString();
				if (referenceFlowPathMap.containsKey(key)) {
					String patternName = null;
					writer.startElement("ReferenceFlowPerCaller");

					writer.startElement("Caller");
					aNode.writeXML(writer);
					writer.endElement();

					writer.startElement("PathSetList");
					for (Path<MyNode> aPath : referenceFlowPathMap.get(key)) {
						writer.startElement("PathSet");
						aPath.writeXML(writer);
						
						if (creatorTriggerPathMap.containsKey(aPath)) {
							patternName = "State";
							writer.startElement("TriggerPathList");
							for (Path<MyNode> aTriggerPath : creatorTriggerPathMap
									.get(aPath)) {
								aTriggerPath.writeXML(writer);
							}
							writer.endElement();
						}
						writer.endElement();
					}
					writer.endElement();

					if (patternName != null) {
						writer.simpleElement("Pattern", patternName);
					}
					writer.endElement();
				}
			}
			writer.endElement();

			writer.startElement("CreatorList");
			for (MyNode aNode : creatorList) {
				aNode.writeXML(writer);
			}
			writer.endElement();

			// writer.startElement("StoreList");
			// for (Store aStore : storeList) {
			// aStore.writeXML(writer);
			// }
			// writer.endElement();

			writer.endElement();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
