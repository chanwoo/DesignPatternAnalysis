package kr.ac.snu.selab.soot.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;

import org.junit.Before;
import org.junit.Test;

public class ProjectManagerTest {
	private ProjectManager manager;

	@Test
	public void projectInformationTest() throws ProjectFileParseException {
		AbstractProject project = manager.getProject("ProjectA");
		assertNotNull("Project is empty", project);

		assertEquals("ProjectA", project.getProjectName());
		assertEquals("SRC", project.getSourceDirectory());
		assertEquals("CP1:CP2", project.getClassPath());
		assertEquals("JIMPLE", project.getJimpleDirectory());
	}

	@Test
	public void argumentsTest() throws ProjectFileParseException {
		AbstractProject project = manager.getProject("ProjectB");
		assertNotNull("Project is empty", project);

		assertEquals("ProjectB", project.getProjectName());
		assertEquals("ROOT/ProjectB/src", project.getSourceDirectory());
		assertEquals("CP1:CP2:ROOT/ProjectB/src", project.getClassPath());
		assertEquals("ROOT/ProjectB/output/jimple",
				project.getJimpleDirectory());
	}

	@Test
	public void noProjectTest() throws ProjectFileParseException {
		AbstractProject project = manager.getProject("NoProject");
		assertNull("Project should be empty", project);
	}

	@Before
	public void init() throws Exception {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\"?>");
		buffer.append("<projects>");
		buffer.append("<project name=\"ProjectA\">");
		buffer.append("<project_root path=\"PROJECT_ROOT\" />");
		buffer.append("<source path=\"SRC\" />");
		buffer.append("<output path=\"OUTPUT\" />");
		buffer.append("<jimple path=\"JIMPLE\" />");
		buffer.append("<classpaths>");
		buffer.append("<path>CP1</path>");
		buffer.append("<path>CP2</path>");
		buffer.append("</classpaths>");
		buffer.append("</project>");
		buffer.append("<project name=\"ProjectB\">");
		buffer.append("<project_root path=\"ROOT/${PROJECT_NAME}\" />");
		buffer.append("<source path=\"${PROJECT_ROOT}/src\" />");
		buffer.append("<output path=\"${PROJECT_ROOT}/output\" />");
		buffer.append("<jimple path=\"${OUTPUT_PATH}/jimple\" />");
		buffer.append("<classpaths>");
		buffer.append("<path>CP1</path>");
		buffer.append("<path>CP2</path>");
		buffer.append("<path>${SRC_PATH}</path>");
		buffer.append("</classpaths>");
		buffer.append("</project>");
		buffer.append("</projects>");

		manager = ProjectManager.getInstance();

		ByteArrayInputStream bais = new ByteArrayInputStream(buffer.toString()
				.getBytes());
		try {
			manager.loadProjects(bais);
		} finally {
			bais.close();
		}
	}
}
