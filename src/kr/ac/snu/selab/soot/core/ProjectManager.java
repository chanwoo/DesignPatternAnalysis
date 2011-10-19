package kr.ac.snu.selab.soot.core;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ProjectManager {
	private static Logger log = Logger.getLogger(ProjectManager.class);

	private static ProjectManager instance = null;

	public static ProjectManager getInstance() {
		if (instance == null)
			instance = new ProjectManager();
		return instance;
	}

	private HashMap<String, AbstractProject> map;
	private HashMap<String, String> abvMap;

	private ProjectManager() {
		map = new HashMap<String, AbstractProject>();
		abvMap = new HashMap<String, String>();
	}

	public AbstractProject getProject(String projectName) {
		String key = null;
		if (abvMap.containsKey(projectName)) {
			key = abvMap.get(projectName);
		} else {
			key = projectName;
		}
		return map.get(key);
	}

	public void loadProjects(InputStream is) throws ProjectFileParseException {
		map.clear();
		abvMap.clear();
		if (is == null) {
			throw new ProjectFileParseException();
		}
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			dBuilder.setErrorHandler(new ErrorHandler() {
				@Override
				public void error(SAXParseException e) throws SAXException {
					log.error(e.getMessage(), e);
				}

				@Override
				public void fatalError(SAXParseException e) throws SAXException {
					log.fatal(e.getMessage(), e);
				}

				@Override
				public void warning(SAXParseException e) throws SAXException {
					log.warn(e.getMessage(), e);
				}
			});
			Document doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("project");
			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Project project = parseProject((Element) nNode);
					map.put(project.getProjectName(), project);
					abvMap.put(project.projectNameAbv, project.getProjectName());
				}
			}
		} catch (Exception e) {
			throw new ProjectFileParseException(e);
		}
	}

	private Project parseProject(Element projectElement) {

		String projectName = projectElement.getAttribute("name");
		String projectNameAbv = projectElement.getAttribute("abv");
		Project project = new Project(projectName, projectNameAbv);

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
		project.projectRoot = replaceKeywords(text, project);

		nodeList = projectElement.getElementsByTagName("source");
		assert (nodeList != null);
		assert (nodeList.getLength() > 0);
		node = nodeList.item(0);
		assert (node.getNodeType() == Node.ELEMENT_NODE);
		element = (Element) node;
		text = element.getAttribute("path");
		assert (text != null);
		text = text.trim();
		project.sourcePath = replaceKeywords(text, project);

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
		project.setOutputPath(replaceKeywords(text, project));

		nodeList = projectElement.getElementsByTagName("jimple");
		assert (nodeList != null);
		assert (nodeList.getLength() > 0);
		node = nodeList.item(0);
		assert (node.getNodeType() == Node.ELEMENT_NODE);
		element = (Element) node;
		text = element.getAttribute("path");
		assert (text != null);
		text = text.trim();
		project.setOutputJimplePath(replaceKeywords(text, project));

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
			buffer.append(replaceKeywords(text, project));
			if (i < length - 1)
				buffer.append(File.pathSeparator);
		}

		project.setClassPath(buffer.toString());
	}

	private static String replaceKeywords(String text, Project project) {
		text = replaceText(text, "\\$\\{PROJECT_NAME\\}",
				project.getProjectName());
		text = replaceText(text, "\\$\\{PROJECT_ROOT\\}", project.projectRoot);
		text = replaceText(text, "\\$\\{SRC_PATH\\}", project.sourcePath);
		text = replaceText(text, "\\$\\{OUTPUT_PATH\\}", project.outputPath);
		return text;
	}

	private static String replaceText(String text, String pattern, String to) {
		if (to != null) {
			to = to.replaceAll("\\\\", "\\\\\\\\");
			text = text.replaceAll(pattern, to);
		}
		return text;
	}

	private static class Project extends AbstractProject {

		private String sourcePath;
		private File outputFile;
		private String outputPath;
		private String classPath;

		private String outputJimplePath;

		private String projectRoot;
		private String projectNameAbv;

		public Project(String aProjectName, String aProjectNameAbv) {
			super(aProjectName);
			this.projectNameAbv = aProjectNameAbv;
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