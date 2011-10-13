package kr.ac.snu.selab.soot.core;

import java.io.File;

public abstract class AbstractProject {
	private String projectName;

	public AbstractProject(String aProjectName) {
		projectName = aProjectName;
	}

	public String getProjectName() {
		return projectName;
	}

	public abstract String getClassPath();

	public abstract String getJimpleDirectory();

	public abstract String getSourceDirectory();

	public abstract File getOutputDirectory();
}
