package kr.ac.snu.selab.soot.core;

public class ProjectFileParseException extends Exception {

	private static final long serialVersionUID = 8663766715407498185L;

	public ProjectFileParseException(String msg) {
		super(msg);
	}

	public ProjectFileParseException(Throwable t) {
		super(t);
	}

}
