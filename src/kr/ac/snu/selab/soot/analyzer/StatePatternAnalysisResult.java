package kr.ac.snu.selab.soot.analyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kr.ac.snu.selab.soot.graph.MyNode;
import kr.ac.snu.selab.soot.graph.Path;
import kr.ac.snu.selab.soot.util.MyUtil;
import kr.ac.snu.selab.soot.util.XMLWriter;

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
		result = result + "<StatePatternAnalysisResult>";
		result = result + "<AbstractType>";
		result = result + MyUtil.removeBracket(getAbstractTypeName());
		result = result + "</AbstractType>";
		result = result + "<CallerList>";
		for (MyNode aNode : callerList) {
			result = result + aNode.toXML();
		}
		result = result + "</CallerList>";
		result = result + "<PatternAnalysisList>";
		for (MyNode aNode : callerList) {
			String key = aNode.toString();
			if (referenceFlowPathMap.containsKey(key)) {
				// String patternName = null;
				result = result + "<AnalysisPerCaller>";
				result = result + "<Caller>";
				result = result + aNode.toXML();
				result = result + "</Caller>";
				result = result + "<DesignPattern>";
				result = result
						+ ((triggeringPathMap.size() < 1) ? "Strategy"
								: "State");
				result = result + "</DesignPattern>";
				if (triggeringPathMap.containsKey(key)) {
					result = result + "<TriggerPathList>";
					for (Path<MyNode> aPath : triggeringPathMap.get(key)) {
						result = result + aPath.toXML();
					}
					result = result + "</TriggerPathList>";
				}
				result = result + "<ObjectFlowPathList>";
				for (Path<MyNode> aPath : referenceFlowPathMap.get(key)) {
					result = result + aPath.toXML();
				}
				result = result + "</ObjectFlowPathList>";
				result = result + "</AnalysisPerCaller>";
			}
		}
		result = result + "</PatternAnalysisList>";
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
		result = result + "</StatePatternAnalysisResult>";
		return result;
	}

	public void writeXML(XMLWriter writer) {
		try {
			writer.startElement("StatePatternAnalysisResult");
			writer.simpleElement("AbstractType", getAbstractTypeName());
			writer.startElement("CallerList");
			for (MyNode aNode : callerList) {
				aNode.writeXML(writer);
			}
			writer.endElement();

			writer.startElement("PatternAnalysisList");
			for (MyNode aNode : callerList) {
				String key = aNode.toString();
				if (referenceFlowPathMap.containsKey(key)) {
					writer.startElement("AnalysisPerCaller");

					writer.startElement("Caller");
					aNode.writeXML(writer);
					writer.endElement();

					writer.startElement("DesignPattern");
					if (triggeringPathMap.size() < 1) {
						writer.pcData("Strategy");
					} else {
						writer.pcData("State");
					}
					writer.endElement();

					if (triggeringPathMap.containsKey(key)) {
						writer.startElement("TriggerPathList");
						for (Path<MyNode> aPath : triggeringPathMap.get(key)) {
							aPath.writeXML(writer);
						}
						writer.endElement();
					}

					writer.startElement("ObjectFlowPathList");
					for (Path<MyNode> aPath : referenceFlowPathMap.get(key)) {
						aPath.writeXML(writer);
					}
					writer.endElement();

					writer.endElement();
				}
			}
			writer.endElement();

			writer.endElement();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
