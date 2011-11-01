package kr.ac.snu.selab.soot.analyzer.decNcor;

import kr.ac.snu.selab.soot.analyzer.AnalysisResult;
import kr.ac.snu.selab.soot.util.XMLWriter;

public class DecNCorAnalysisResult extends AnalysisResult {
	
	private boolean isDecorator;
	private boolean isCor;
	private int numberOfRecursiveCall;
	private String storeClassName;
	
	public DecNCorAnalysisResult() {
		super();
		isDecorator = false;
		isCor = false;
		numberOfRecursiveCall = 0;
		storeClassName = null;
	}
	
	public void increaseNumberOfRecursiveCall() {
		numberOfRecursiveCall = numberOfRecursiveCall + 1;
	}
	
	public void setIsDecorator(boolean value) {
		isDecorator = value;
	}
	
	public void setIsCor(boolean value) {
		isCor = value;
	}
	
	public void setStoreClassName(String aName) {
		storeClassName = aName;
	}
	
	public boolean isDecorator() {
		return isDecorator;
	}
	
	public boolean isCor() {
		return isCor;
	}
	
	public int numberOfRecursiveCall() {
		return numberOfRecursiveCall;
	}
	
	public String storeClassName() {
		return storeClassName;
	}
	
	public void writeXML(XMLWriter writer) {
		
	}

}
