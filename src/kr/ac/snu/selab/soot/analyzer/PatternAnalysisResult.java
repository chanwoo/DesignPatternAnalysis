package kr.ac.snu.selab.soot.analyzer;

public class PatternAnalysisResult {
	private String patternName;
	private boolean patternExistence;
	
	public String patternName() {
		return patternName;
	}
	
	public void setPatternName(String aName) {
		patternName = aName;
	}
	
	public boolean patternExistence() {
		return patternExistence;
	}
	
	public void setPatternExistence(boolean value) {
		patternExistence = value;
	}

}
