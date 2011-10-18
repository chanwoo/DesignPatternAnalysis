package kr.ac.snu.selab.soot.core;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

public class ProjectManagerFailTest {
	private ProjectManager manager;

	@Before
	public void silent() {
		Logger.getLogger("kr.ac.snu.selab").setLevel(Level.OFF);
	}

	@Test
	public void rootElementParseErrorTest() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\"?>");
		buffer.append("<projects1>");
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
		buffer.append("</project>");
		buffer.append("</projects>");

		manager = ProjectManager.getInstance();

		ByteArrayInputStream bais = new ByteArrayInputStream(buffer.toString()
				.getBytes());
		try {
			manager.loadProjects(bais);
			fail("Project file parse exception should be raised!!");
		} catch (ProjectFileParseException e) {
		} finally {
			if (bais != null)
				try {
					bais.close();
				} catch (IOException e) {
				}
		}
	}

	@Test
	public void projectElementParseErrorTest() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\"?>");
		buffer.append("<projects>");
		buffer.append("<project1 name=\"ProjectA\">");
		buffer.append("<project_root path=\"PROJECT_ROOT\" />");
		buffer.append("<source path=\"SRC\" />");
		buffer.append("<output path=\"OUTPUT\" />");
		buffer.append("<jimple path=\"JIMPLE\" />");
		buffer.append("<classpaths>");
		buffer.append("<path>CP1</path>");
		buffer.append("<path>CP2</path>");
		buffer.append("</classpaths>");
		buffer.append("</project>");
		buffer.append("</projects>");

		manager = ProjectManager.getInstance();

		ByteArrayInputStream bais = new ByteArrayInputStream(buffer.toString()
				.getBytes());
		try {
			manager.loadProjects(bais);
			fail("Project file parse exception should be raised!!");
		} catch (ProjectFileParseException e) {
		} finally {
			if (bais != null)
				try {
					bais.close();
				} catch (IOException e) {
				}
		}
	}
}
