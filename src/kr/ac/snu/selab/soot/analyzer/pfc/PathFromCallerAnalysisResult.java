package kr.ac.snu.selab.soot.analyzer.pfc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.ac.snu.selab.soot.analyzer.AnalysisResult;
import kr.ac.snu.selab.soot.graph.MyNode;
import kr.ac.snu.selab.soot.graph.Path;
import kr.ac.snu.selab.soot.util.XMLWriter;

public class PathFromCallerAnalysisResult extends AnalysisResult {
	
	public String getAbstractTypeName() {
		String result = "";
		if (abstractType != null) {
			result = abstractType.toString();
		}
		return result;
	}
	
	public PathFromCallerAnalysisResult() {
		abstractType = null;
		callerList = new ArrayList<MyNode>();
		creatorList = new ArrayList<MyNode>();
		referenceFlowPathMap = new HashMap<String, List<Path<MyNode>>>();
//		storeList = new ArrayList<Store>();
//		creatorTriggerPathMap = new HashMap<MyPath, List<MyPath>>();
	}
	
//	public AnalysisResult(SootClass anAbstractType, List<Caller> aCallerList,
//			List<Creator> aCreatorList, List<Store> aStoreList) {
//		abstractType = anAbstractType;
//		callerList = aCallerList;
//		creatorList = aCreatorList;
//		storeList = aStoreList;
//	}
	
//	public boolean hasDesignPattern() {
//		return true;
//		//return !creatorTriggerPathMap.isEmpty();
//	}
	
	public void writeXML(XMLWriter writer) {
		try {
			writer.startElement("AnalysisResult");
			writer.simpleElement("AbstractType", getAbstractTypeName());
			writer.startElement("CallerList");
			for (MyNode aNode : callerList) {
				aNode.writeXML(writer);
			}
			writer.endElement();

			writer.startElement("ObjectFlowPathSet");
			for (MyNode aNode : callerList) {
				String key = aNode.toString();
				if (referenceFlowPathMap.containsKey(key)) {
					writer.startElement("ObjectFlowPathPerCaller");

					writer.startElement("Caller");
					aNode.writeXML(writer);
					writer.endElement();

					writer.startElement("PathList");
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
