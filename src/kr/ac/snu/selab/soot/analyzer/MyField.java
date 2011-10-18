package kr.ac.snu.selab.soot.analyzer;

import kr.ac.snu.selab.soot.graph.MyNode;
import kr.ac.snu.selab.soot.util.MyUtil;
import soot.SootField;

public class MyField extends MyNode {
	private boolean isStore;

	public MyField(SootField aField) {
		super(aField);
		isStore = false;
	}

	@Override
	public String toXML() {
		String result = "";
		result = result + "<Field>";
		result = result + "<ToString>";
		result = result + MyUtil.removeBracket(toString());
		result = result + "</ToString>";
		if (isStore()) {
			result = result + role();
		}
		result = result + "</Field>";
		return result;
	}

	public boolean isCreator() {
		return false;
	}

	public boolean isCaller() {
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

	public String role() {
		String result = "";
		result = result + "<Role>Store</Role>";
		return result;
	}

}
