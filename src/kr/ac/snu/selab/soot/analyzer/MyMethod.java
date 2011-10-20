package kr.ac.snu.selab.soot.analyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import kr.ac.snu.selab.soot.graph.MyNode;
import kr.ac.snu.selab.soot.util.MyUtil;
import kr.ac.snu.selab.soot.util.XMLWriter;

import soot.SootMethod;
import soot.Unit;

public class MyMethod extends MyNode {

	private static Logger log = Logger.getLogger(MyMethod.class);

	boolean isCaller;
	boolean isCreator;
	List<Unit> callStatementList;
	List<Unit> createStatementList;
	
	public List<Unit> getCallStatementList () {
		return callStatementList;
	}

	public MyMethod(SootMethod aMethod) {
		super(aMethod);
		isCaller = false;
		isCreator = false;
		callStatementList = new ArrayList<Unit>();
		createStatementList = new ArrayList<Unit>();
	}

	public SootMethod getMethod() {
		return (SootMethod) element;
	}

	@Override
	public String toXML() {
		String result = "";
		result = result + "<Method>";
		result = result + "<ToString>";
		result = result + MyUtil.removeBracket(toString());
		result = result + "</ToString>";
		result = result + "<RoleList>";
		result = result + role();
		result = result + "</RoleList>";
		result = result + "</Method>";
		return result;
	}

	public boolean isCaller() {
		return isCaller;
	}

	public void setIsCaller(boolean b) {
		isCaller = b;
	}

	public boolean isCreator() {
		return isCreator;
	}

	public void setIsCreator(boolean b) {
		isCreator = b;
	}

	public boolean isStore() {
		return false;
	}

	public void setIsStore(boolean value) {

	}

	public void addCallStatement(Unit aUnit) {
		callStatementList.add(aUnit);
	}

	public void addCreateStatement(Unit aUnit) {
		createStatementList.add(aUnit);
	}

	public String role() {
		String result = "";
		if (isCaller()) {
			result = result + "<Role>Caller</Role>";
			result = result + "<CallStatementList>";
			for (Unit callStatement : callStatementList) {
				result = result + "<CallStatement>";
				result = result
						+ MyUtil.removeBracket(callStatement.toString());
				result = result + "</CallStatement>";
			}
			result = result + "</CallStatementList>";
		}
		if (isCreator()) {
			result = result + "<Role>Creator</Role>";
			result = result + "<CreateStatementList>";
			for (Unit createStatement : createStatementList) {
				result = result + "<CreateStatement>";
				result = result
						+ MyUtil.removeBracket(createStatement.toString());
				result = result + "</CreateStatement>";
			}
			result = result + "</CreateStatementList>";
		}
		return result;
	}

	@Override
	public void writeXML(XMLWriter writer) {
		try {
			writer.startElement("Method");
			writer.simpleElement("ToString", toString());
			writer.startElement("RoleList");
			if (isCaller()) {
				writer.simpleElement("Role", "Caller");
				writer.startElement("CallStatementList");
				for (Unit callStatement : callStatementList) {
					writer.simpleElement("CallStatement",
							callStatement.toString());
				}
				writer.endElement();
			}
			if (isCreator()) {
				writer.simpleElement("Role", "Creator");
				writer.startElement("CreateStatementList");
				for (Unit createStatement : createStatementList) {
					writer.simpleElement("CreateStatement",
							createStatement.toString());
				}
				writer.endElement();
			}
			writer.endElement();
			writer.endElement();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}
