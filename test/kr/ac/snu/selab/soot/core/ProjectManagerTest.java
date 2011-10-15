package kr.ac.snu.selab.soot.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.channels.FileChannel;

import kr.ac.snu.selab.soot.core.AbstractProject;
import kr.ac.snu.selab.soot.core.ProjectFileNotFoundException;
import kr.ac.snu.selab.soot.core.ProjectManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProjectManagerTest {
	private boolean projectsFileExists;
	private String projectsFilePath, tempProjectsFilePath;
	private ProjectManager manager;

	@Test
	public void projectInformationTest() throws ProjectFileNotFoundException {
		manager.loadProejcts();

		AbstractProject project = manager.getProject("ProjectA");
		assertNotNull("Project is empty", project);

		assertEquals("ProjectA", project.getProjectName());
		assertEquals("SRC", project.getSourceDirectory());
		assertEquals("CP1:CP2", project.getClassPath());
		assertEquals("JIMPLE", project.getJimpleDirectory());
	}

	@Test
	public void argumentsTest() throws ProjectFileNotFoundException {
		manager.loadProejcts();

		AbstractProject project = manager.getProject("ProjectB");
		assertNotNull("Project is empty", project);

		assertEquals("ProjectB", project.getProjectName());
		assertEquals("ROOT/ProjectB/src", project.getSourceDirectory());
		assertEquals("CP1:CP2:ROOT/ProjectB/src", project.getClassPath());
		assertEquals("ROOT/ProjectB/output/jimple",
				project.getJimpleDirectory());
	}

	@Test
	public void noProjectTest() throws ProjectFileNotFoundException {
		manager.loadProejcts();

		AbstractProject project = manager.getProject("NoProject");
		assertNull("Project should be empty", project);
	}

	@Before
	public void init() throws Exception {
		URL url = ClassLoader
				.getSystemResource(ProjectManager.PROJECT_FILE_NAME);
		if (url == null)
			fail("Project file initialize falied.");

		File file = new File(url.getPath());
		projectsFilePath = file.getAbsolutePath();

		if (file.exists()) {
			projectsFileExists = true;
			File originalProjectsFile = new File(file.getParentFile(),
					"projects.xml.tmp");
			tempProjectsFilePath = originalProjectsFile.getAbsolutePath();
			copy(projectsFilePath, tempProjectsFilePath);
		} else {
			projectsFileExists = false;
		}

		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new FileWriter(projectsFilePath));
			writer.println("<?xml version=\"1.0\"?>");
			writer.println("<projects>");
			writer.println("<project name=\"ProjectA\">");
			writer.println("<project_root path=\"PROJECT_ROOT\" />");
			writer.println("<source path=\"SRC\" />");
			writer.println("<output path=\"OUTPUT\" />");
			writer.println("<jimple path=\"JIMPLE\" />");
			writer.println("<classpaths>");
			writer.println("<path>CP1</path>");
			writer.println("<path>CP2</path>");
			writer.println("</classpaths>");
			writer.println("</project>");
			writer.println("<project name=\"ProjectB\">");
			writer.println("<project_root path=\"ROOT/${PROJECT_NAME}\" />");
			writer.println("<source path=\"${PROJECT_ROOT}/src\" />");
			writer.println("<output path=\"${PROJECT_ROOT}/output\" />");
			writer.println("<jimple path=\"${OUTPUT_PATH}/jimple\" />");
			writer.println("<classpaths>");
			writer.println("<path>CP1</path>");
			writer.println("<path>CP2</path>");
			writer.println("<path>${SRC_PATH}</path>");
			writer.println("</classpaths>");
			writer.println("</project>");
			writer.println("</projects>");

			writer.flush();
		} catch (IOException e) {
			throw e;
		} finally {
			if (writer != null)
				writer.close();
		}

		manager = ProjectManager.getInstance();
	}

	@After
	public void cleanUp() {
		if (projectsFileExists) {
			File tempFile = new File(tempProjectsFilePath);
			copy(tempProjectsFilePath, projectsFilePath);
			tempFile.delete();
		} else {
			File projectsFile = new File(projectsFilePath);
			projectsFile.delete();
		}
	}

	private static void copy(String source, String target) {
		// 복사 대상이 되는 파일 생성
		File sourceFile = new File(source);

		// 스트림, 채널 선언
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		FileChannel fcin = null;
		FileChannel fcout = null;

		try {
			// 스트림 생성
			inputStream = new FileInputStream(sourceFile);
			outputStream = new FileOutputStream(target);
			// 채널 생성
			fcin = inputStream.getChannel();
			fcout = outputStream.getChannel();

			// 채널을 통한 스트림 전송
			long size = fcin.size();
			fcin.transferTo(0, size, fcout);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 자원 해제
			try {
				fcout.close();
			} catch (IOException ioe) {
			}
			try {
				fcin.close();
			} catch (IOException ioe) {
			}
			try {
				outputStream.close();
			} catch (IOException ioe) {
			}
			try {
				inputStream.close();
			} catch (IOException ioe) {
			}
		}
	}
}
