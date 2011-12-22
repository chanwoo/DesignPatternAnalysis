package kr.ac.snu.selab.soot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import kr.ac.snu.selab.soot.analyzer.AbstractAnalyzer;
import kr.ac.snu.selab.soot.core.AbstractProject;
import kr.ac.snu.selab.soot.core.ProjectManager;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import soot.Hierarchy;
import soot.PackManager;
import soot.SootClass;
import soot.Transform;

public class SampleTest {
	
	private static Logger logger = Logger.getLogger(SampleTest.class);

	private static final String PROJECTS_NAME = "StatePatternExample";
	private static final String PROJECTS_FILE_NAME = "projects.xml";

	int targetClassCount = 0;

	@Before
	public void prepare() throws Throwable {
		targetClassCount = 0;

		ProjectManager projects = ProjectManager.getInstance();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(findProjectsFile());
			projects.loadProjects(fis);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		}

		AbstractProject project = projects.getProject(PROJECTS_NAME);
		assertNotNull("Cannot find project!!!", project);

		AbstractAnalyzer analyzer = new TestRunner(project);

		PackManager.v().getPack("jtp")
				.add(new Transform("jtp.Experiment", analyzer));

		final String[] arguments = { "-cp", project.getClassPath(), "-f", "n",
				"-d", "", "--process-dir", project.getSourceDirectory() };
		soot.Main.main(arguments);
	}

	@Test
	public void doNothing() {
		//assertEquals(0, targetClassCount);
		logger.debug("hello from SampleTest");
		
	}

	private class TestRunner extends AbstractAnalyzer {
		public TestRunner(AbstractProject project) {
			super(project);
		}

		@Override
		protected void analyze(List<SootClass> classList, Hierarchy hierarchy) {
			assertNotNull("Target classes not found", classList);
			//assertEquals(7, classList.size());
			targetClassCount = classList.size();
		}
	}

	private static File findProjectsFile() {
		URL url = ClassLoader.getSystemResource(PROJECTS_FILE_NAME);
		if (url == null) {
			return null;
		}

		File file = new File(url.getFile());
		if (file == null || !file.exists() || !file.isFile()) {
			return null;
		}
		return file;
	}
}
