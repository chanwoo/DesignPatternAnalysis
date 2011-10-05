package kr.ac.snu.selab.soot;

import kr.ac.snu.selab.soot.projects.AbstractProject;
import kr.ac.snu.selab.soot.projects.ProjectFileNotFoundException;
import kr.ac.snu.selab.soot.projects.ProjectManager;

public class Main {
	public static void main(String[] args) {

		ProjectManager projects = ProjectManager.getInstance();
		try {
			projects.loadProejcts();
		} catch (ProjectFileNotFoundException e) {
			System.err.println("Can not find projects.xml file.");
			return;
		}

		AbstractProject project = null;

		// project = projects.getProject("JHotDraw_5_3");
		// project = projects.getProject("StatePatternExample");
		// project = projects.getProject("StrategyPatternExample");
		project = projects.getProject("StatePatternExample2");
		assert (project != null);

		AnalyzerRunner.run(project);
	}
}
