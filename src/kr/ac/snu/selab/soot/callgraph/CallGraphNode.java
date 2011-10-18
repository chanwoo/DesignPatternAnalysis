//package kr.ac.snu.selab.soot.callgraph;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import kr.ac.snu.selab.soot.graphx.Node;
//import kr.ac.snu.selab.soot.util.MyUtil;
//import soot.SootMethod;
//import soot.Unit;
//
//public class CallGraphNode extends Node<SootMethod> {
//	boolean isCaller;
//	boolean isCreator;
//	List<Unit> callStatementList;
//	List<Unit> createStatementList;
//
//	public CallGraphNode(SootMethod aMethod) {
//		super(aMethod);
//		isCaller = false;
//		isCreator = false;
//		callStatementList = new ArrayList<Unit>();
//		createStatementList = new ArrayList<Unit>();
//	}
//
//	public SootMethod getMethod() {
//		return element;
//	}
//
//	public boolean equals(Object anObject) {
//		return element.equals(anObject);
//	}
//
//	public int hashcode() {
//		return element.hashCode();
//	}
//
//	@Override
//	public String toString() {
//		return element.toString();
//	}
//
//	@Override
//	public String toXML() {
//		String result = "";
//		result = result + "<Method>";
//		result = result + "<ToString>";
//		result = result + MyUtil.removeBracket(toString());
//		result = result + "</ToString>";
//		result = result + "<RoleList>";
//		result = result + role();
//		result = result + "</RoleList>";
//		result = result + "</Method>";
//		return result;
//	}
//
//	public boolean isCaller() {
//		return isCaller;
//	}
//
//	public void setIsCaller(boolean b) {
//		isCaller = b;
//	}
//
//	public boolean isCreator() {
//		return isCreator;
//	}
//
//	public void setIsCreator(boolean b) {
//		isCreator = b;
//	}
//
//	public boolean isStore() {
//		return false;
//	}
//
//	public void setIsStore(boolean value) {
//
//	}
//
//	public void addCallStatement(Unit aUnit) {
//		callStatementList.add(aUnit);
//	}
//
//	public void addCreateStatement(Unit aUnit) {
//		createStatementList.add(aUnit);
//	}
//
//	public String role() {
//		String result = "";
//		if (isCaller()) {
//			result = result + "<Role>Caller</Role>";
//			result = result + "<CallStatementList>";
//			for (Unit callStatement : callStatementList) {
//				result = result + "<CallStatement>";
//				result = result
//						+ MyUtil.removeBracket(callStatement.toString());
//				result = result + "</CallStatement>";
//			}
//			result = result + "</CallStatementList>";
//		}
//		if (isCreator()) {
//			result = result + "<Role>Creator</Role>";
//			result = result + "<CreateStatementList>";
//			for (Unit createStatement : createStatementList) {
//				result = result + "<CreateStatement>";
//				result = result
//						+ MyUtil.removeBracket(createStatement.toString());
//				result = result + "</CreateStatement>";
//			}
//			result = result + "</CreateStatementList>";
//		}
//		return result;
//	}
//}
