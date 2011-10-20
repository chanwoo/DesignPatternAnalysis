package kr.ac.snu.selab.soot.analyzer.sta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kr.ac.snu.selab.soot.analyzer.AnalysisResult;
import kr.ac.snu.selab.soot.graph.MyNode;
import kr.ac.snu.selab.soot.graph.Path;
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
