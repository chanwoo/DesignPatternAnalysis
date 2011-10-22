package kr.ac.snu.selab.soot.analyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kr.ac.snu.selab.soot.graph.MyNode;
import kr.ac.snu.selab.soot.util.XMLWriter;

import org.apache.log4j.Logger;

import soot.SootMethod;
import soot.Unit;

public class MyMethod extends MyNode {

	private static Logger log = Logger.getLogger(MyMethod.class);

	private boolean isCaller;
	private boolean isCreator;
	private boolean isInjector;
	private List<Unit> callStatementList;
	private List<Unit> createStatementList;

	public MyMethod(SootMethod aMethod) {
		super(aMethod);
		isCaller = false;
		isCreator = false;
		isInjector = false;
		callStatementList = new ArrayList<Unit>();
		createStatementList = new ArrayList<Unit>();
	}

	public SootMethod getMethod() {
		return (SootMethod) element;
	}

	public List<Unit> getCallStatementList() {
		return callStatementList;
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
	
	public boolean isInjector() {
		return isInjector;
	}
	
	public void setIsInjector(boolean value) {
		isInjector = value;
	}

	public void addCallStatement(Unit aUnit) {
		callStatementList.add(aUnit);
	}

	public void addCreateStatement(Unit aUnit) {
		createStatementList.add(aUnit);
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
