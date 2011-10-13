package kr.ac.snu.selab.soot.projects;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ProjectManager {

	public static final String PROJECT_FILE_NAME = "projects.xml";

	private static ProjectManager instance = null;

	public static ProjectManager getInstance() {
		if (instance == null)
			instance = new ProjectManager();
		return instance;
	}

	private HashMap<String, AbstractProject> map;

	private ProjectManager() {
		map = new HashMap<String, AbstractProject>();
	}

	public AbstractProject getProject(String projectName) {
		return map.get(projectName);
	}

	public void loadProejcts() throws ProjectFileNotFoundException {
		map.clear();
		InputStream is = null;
		is = ClassLoader.getSystemResourceAsStream(PROJECT_FILE_NAME);
		if (is == null) {
			throw new ProjectFileNotFoundException();
		}
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("project");
			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					AbstractProject project = parseProject((Element) nNode);
					map.put(project.getProjectName(), project);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
				}
		}
	}

	private AbstractProject parseProject(Element projectElement) {

		String projectName = projectElement.getAttribute("name");
		Project project = new Project(projectName);

		NodeList nodeList = null;
		Node node = null;
		Element element = null;
		String text = null;

		nodeList = projectElement.getElementsByTagName("project_root");
		assert (nodeList != null);
		assert (nodeList.getLength() > 0);
		node = nodeList.item(0);
		assert (node.getNodeType() == Node.ELEMENT_NODE);
		element = (Element) node;
		text = element.getAttribute("path");
		assert (text != null);
		text = text.trim();
		project.projectRoot = replace(text, project);

		nodeList = projectElement.getElementsByTagName("source");
		assert (nodeList != null);
		assert (nodeList.getLength() > 0);
		node = nodeList.item(0);
		assert (node.getNodeType() == Node.ELEMENT_NODE);
		element = (Element) node;
		text = element.getAttribute("path");
		assert (text != null);
		text = text.trim();
		project.sourcePath = replace(text, project);

		nodeList = projectElement.getElementsByTagName("classpaths");
		assert (nodeList != null);
		assert (nodeList.getLength() > 0);
		node = nodeList.item(0);
		assert (node.getNodeType() == Node.ELEMENT_NODE);
		parseClassPath((Element) node, project);

		nodeList = projectElement.getElementsByTagName("output");
		assert (nodeList != null);
		assert (nodeList.getLength() > 0);
		node = nodeList.item(0);
		assert (node.getNodeType() == Node.ELEMENT_NODE);
		element = (Element) node;
		text = element.getAttribute("path");
		assert (text != null);
		text = text.trim();
		project.setOutputPath(replace(text, project));

		nodeList = projectElement.getElementsByTagName("jimple");
		assert (nodeList != null);
		assert (nodeList.getLength() > 0);
		node = nodeList.item(0);
		assert (node.getNodeType() == Node.ELEMENT_NODE);
		element = (Element) node;
		text = element.getAttribute("path");
		assert (text != null);
		text = text.trim();
		project.setOutputJimplePath(replace(text, project));

		return project;
	}

	private void parseClassPath(Element parent, Project project) {
		NodeList nodeList = null;
		Node node = null;
		Element element = null;
		String text = null;

		nodeList = parent.getElementsByTagName("path");
		assert (nodeList != null);
		int length = nodeList.getLength();
		assert (length > 0);

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < length; i++) {
			node = nodeList.item(i);
			assert (node.getNodeType() == Node.ELEMENT_NODE);
			element = (Element) node;
			text = element.getTextContent();
			assert (text != null);
			text = text.trim();
			buffer.append(replace(text, project));
			if (i < length - 1)
				buffer.append(File.pathSeparator);
		}

		project.setClassPath(buffer.toString());
	}

	private static String replace(String text, Project project) {
		return text
				.replaceAll("\\$\\{PROJECT_NAME\\}", project.getProjectName())
				.replaceAll("\\$\\{PROJECT_ROOT\\}", project.projectRoot)
				.replaceAll("\\$\\{SRC_PATH\\}", project.sourcePath)
				.replaceAll("\\$\\{OUTPUT_PATH\\}", project.outputPath);
	}

	private static class Project extends AbstractProject {

		private String sourcePath;
		private File outputFile;
		private String outputPath;
		private String classPath;

		private String outputJimplePath;

		private String projectRoot;

		public Project(String aProjectName) {
			super(aProjectName);
		}

		public void setOutputPath(String path) {
			this.outputPath = path;
			this.outputFile = new File(outputPath);
		}

		public String getClassPath() {
			return classPath;
		}

		public String getJimpleDirectory() {
			return outputJimplePath;
		}

		public String getSourceDirectory() {
			return sourcePath;
		}

		@Override
		public File getOutputDirectory() {
			return outputFile;
		}

		public void setClassPath(String classPath) {
			this.classPath = classPath;
		}

		public void setOutputJimplePath(String outputJimplePath) {
			this.outputJimplePath = outputJimplePath;
		}
	}
}