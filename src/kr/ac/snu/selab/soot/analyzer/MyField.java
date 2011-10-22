package kr.ac.snu.selab.soot.analyzer;

import java.io.IOException;

import kr.ac.snu.selab.soot.graph.MyNode;
import kr.ac.snu.selab.soot.util.XMLWriter;

import org.apache.log4j.Logger;

import soot.SootField;

public class MyField extends MyNode {

	private static Logger log = Logger.getLogger(MyField.class);

	private boolean isStore;

	public MyField(SootField aField) {
		super(aField);
		isStore = false;
	}

	public boolean isCreator() {
		return false;
	}

	public boolean isCaller() {
		return false;
	}
	
	public boolean isInjector() {
		return false;
	}

	public boolean isStore() {
		return isStore;
	}

	public void setIsStore(boolean value) {
		isStore = value;
	}

	public void setIsCreator(boolean value) {

	}

	public void setIsCaller(boolean value) {

	}
	
	public void setIsInjector(boolean value) {
		
	}

	@Override
	public void writeXML(XMLWriter writer) {
		try {
			writer.startElement("Field");
			writer.startElement("ToString");
			writer.pcData(toString());
			writer.endElement();
			if (isStore()) {
				writer.startElement("Role");
				writer.pcData("Store");
				writer.endElement();
			}
			writer.endElement();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}
