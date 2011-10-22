package kr.ac.snu.selab.soot.analyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.ac.snu.selab.soot.graph.MyNode;
import kr.ac.snu.selab.soot.graph.Path;
import kr.ac.snu.selab.soot.util.XMLWriter;
import soot.SootClass;

public class AnalysisResult {
	private SootClass abstractType;
	protected List<MyNode> callerList;
	protected List<MyNode> creatorList;

	// String := MyNode.toString()
	// where MyNode is caller
	protected Map<String, List<Path<MyNode>>> referenceFlowPathMap;

	// FIXME: Maybe, this field is useless!!!
	// key := referenceFlowPath,
	// value := triggerPath
	private Map<Path<MyNode>, List<Path<MyNode>>> creatorTriggerPathMap;

	public AnalysisResult() {
		abstractType = null;
		callerList = new ArrayList<MyNode>();
		creatorList = new ArrayList<MyNode>();
		referenceFlowPathMap = new HashMap<String, List<Path<MyNode>>>();
		// storeList = new ArrayList<Store>();
		creatorTriggerPathMap = new HashMap<Path<MyNode>, List<Path<MyNode>>>();
	}

	public String getAbstractTypeName() {
		if (abstractType == null) {
			return "";
		}
		return abstractType.toString();
	}

	public void setAbstractType(SootClass aType) {
		abstractType = aType;
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

	public Iterable<MyNode> getCallers() {
		return callerList;
	}

	public void addCaller(MyNode node) {
		callerList.add(node);
	}

	public Iterable<MyNode> getCreators() {
		return creatorList;
	}

	public void addCreator(MyNode node) {
		creatorList.add(node);
	}

	public void putReferenceFlowPath(String key, List<Path<MyNode>> pathList) {
		referenceFlowPathMap.put(key, pathList);
	}

	public Iterable<Path<MyNode>> getReferenceFlowPaths(String key) {
		return referenceFlowPathMap.get(key);
	}

	public boolean containsReferenceFlowPath(String key) {
		return referenceFlowPathMap.containsKey(key);
	}
}
