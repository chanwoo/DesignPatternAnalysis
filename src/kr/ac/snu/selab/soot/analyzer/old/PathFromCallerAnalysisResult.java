//package kr.ac.snu.selab.soot.analyzer.old;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import kr.ac.snu.selab.soot.analyzer.AnalysisResult;
//import kr.ac.snu.selab.soot.graph.MyNode;
//import kr.ac.snu.selab.soot.graph.Path;
//import kr.ac.snu.selab.soot.util.MyUtil;
//import kr.ac.snu.selab.soot.util.XMLWriter;
//
//public class PathFromCallerAnalysisResult extends AnalysisResult {
//	
//	public String getAbstractTypeName() {
//		String result = "";
//		if (abstractType != null) {
//			result = abstractType.toString();
//		}
//		return result;
//	}
//	
//	public PathFromCallerAnalysisResult() {
//		abstractType = null;
//		callerList = new ArrayList<MyNode>();
//		creatorList = new ArrayList<MyNode>();
//		referenceFlowPathMap = new HashMap<String, List<Path<MyNode>>>();
////		storeList = new ArrayList<Store>();
////		creatorTriggerPathMap = new HashMap<MyPath, List<MyPath>>();
//	}
//	
////	public AnalysisResult(SootClass anAbstractType, List<Caller> aCallerList,
////			List<Creator> aCreatorList, List<Store> aStoreList) {
////		abstractType = anAbstractType;
////		callerList = aCallerList;
////		creatorList = aCreatorList;
////		storeList = aStoreList;
////	}
//	
////	public boolean hasDesignPattern() {
////		return true;
////		//return !creatorTriggerPathMap.isEmpty();
////	}
//	
//	public String toXML() {
//		String result = "";
//		result = result + "<AnalysisResult>";
//		result = result + "<AbstractType>";
//		result = result + MyUtil.removeBracket(getAbstractTypeName());
//		result = result + "</AbstractType>";
//		result = result + "<CallerList>";
//		for (MyNode aNode : callerList) {
//			result = result + aNode.toXML();
//		}
//		result = result + "</CallerList>";
//		result = result + "<ObjectFlowPathSet>";
//		for (MyNode aNode : callerList) {
//			String key = aNode.toString();
//			if (referenceFlowPathMap.containsKey(key)) {
////				String patternName = null;
//				result = result + "<ObjectFlowPathPerCaller>";
//				result = result + "<Caller>";
//				result = result + aNode.toXML();
//				result = result + "</Caller>";
//				result = result + "<PathList>";
//				for (Path<MyNode> aPath : referenceFlowPathMap.get(key)) {
//					result = result + aPath.toXML();
//				}
//				result = result + "</PathList>";
//				result = result + "</ObjectFlowPathPerCaller>";
//			}
//		}
//		result = result + "</ObjectFlowPathSet>";
////		result = result + "<CreatorList>";
////		for (MyNode aNode : creatorList) {
////			result = result + aNode.toXML();
////		}
////		result = result + "</CreatorList>";
////		result = result + "<StoreList>";
////		for (Store aStore : storeList) {
////			result = result + aStore.toXML();
////		}
////		result = result + "</StoreList>";
//		result = result + "</AnalysisResult>";
//		return result;
//	}
//
//	public void writeXML(XMLWriter writer) {
//		try {
//			writer.startElement("AnalysisResult");
//			writer.simpleElement("AbstractType", getAbstractTypeName());
//			writer.startElement("CallerList");
//			for (MyNode aNode : callerList) {
//				aNode.writeXML(writer);
//			}
//			writer.endElement();
//
//			writer.startElement("ObjectFlowPathSet");
//			for (MyNode aNode : callerList) {
//				String key = aNode.toString();
//				if (referenceFlowPathMap.containsKey(key)) {
//					writer.startElement("ObjectFlowPathPerCaller");
//
//					writer.startElement("Caller");
//					aNode.writeXML(writer);
//					writer.endElement();
//
//					writer.startElement("PathList");
//					for (Path<MyNode> aPath : referenceFlowPathMap.get(key)) {
//						aPath.writeXML(writer);
//					}
//					writer.endElement();
//					
//					writer.endElement();
//				}
//			}
//			writer.endElement();
//
//			writer.endElement();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//}
