package kr.ac.snu.selab.soot.core;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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

	private static final String TARGET_DIRECTORY = "subjectsOfAnalysis";

	private static ProjectManager instance = null;

	public static ProjectManager getInstance() {
		if (instance == null)
			instance = new ProjectManager();
		return instance;
	}

	private String targetRootPath = null;
	private HashMap<String, AbstractProject> map;
	private HashMap<String, String> abvMap;

	private Map<String, String> jreLibraryMap;

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
			throw new ProjectFileParseException(
					"Unable to read project file: unknown error!!");
		}

		findTargetRoot();

		loadJreLibraries();

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

	private void findTargetRoot() throws ProjectFileParseException {
		File rootDirectory = new File(System.getProperty("user.dir"));
		File targetRoot = new File(rootDirectory, TARGET_DIRECTORY);
		if (targetRoot == null || !targetRoot.exists()
				|| !targetRoot.isDirectory()) {
			targetRootPath = null;
			throw new ProjectFileParseException("Can not find target directory");
		}
		targetRootPath = targetRoot.getAbsolutePath();
	}

	private void loadJreLibraries() {
		jreLibraryMap = new HashMap<String, String>();
		String jreLibraryPath = System.getProperty("sun.boot.class.path");
		String[] tokens = jreLibraryPath.split(File.pathSeparator);
		for (String path : tokens) {
			int lastIndexOfSeparator = path.lastIndexOf(File.separatorChar);
			if (lastIndexOfSeparator < 0)
				continue;
			String fileName = path.substring(lastIndexOfSeparator + 1);
			jreLibraryMap.put(fileName, path);
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
		project.projectRoot = replaceKeywords(text, project, targetRootPath);

		nodeList = projectElement.getElementsByTagName("source");
		assert (nodeList != null);
		assert (nodeList.getLength() > 0);
		node = nodeList.item(0);
		assert (node.getNodeType() == Node.ELEMENT_NODE);
		element = (Element) node;
		text = element.getAttribute("path");
		assert (text != null);
		text = text.trim();
		project.sourcePath = replaceKeywords(text, project, targetRootPath);

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
		project.setOutputPath(replaceKeywords(text, project, targetRootPath));

		nodeList = projectElement.getElementsByTagName("jimple");
		assert (nodeList != null);
		assert (nodeList.getLength() > 0);
		node = nodeList.item(0);
		assert (node.getNodeType() == Node.ELEMENT_NODE);
		element = (Element) node;
		text = element.getAttribute("path");
		assert (text != null);
		text = text.trim();
		project.setOutputJimplePath(replaceKeywords(text, project,
				targetRootPath));

		nodeList = projectElement.getElementsByTagName("include_package");
		if (nodeList != null && nodeList.getLength() > 0) {
			node = nodeList.item(0);
			assert (node.getNodeType() == Node.ELEMENT_NODE);
			element = (Element) node;
			text = element.getAttribute("name");
			assert (text != null);
			text = text.trim();
			project.setIncludePackage(text);
		}

		return project;
	}

	private void parseClassPath(Element parent, Project project) {
		NodeList nodeList = null;
		Node node = null;
		Element element = null;
		String text = null;
		StringBuffer buffer = new StringBuffer();

		if (jreLibraryMap.containsKey("classes.jar")) {
			String path = jreLibraryMap.get("classes.jar");
			buffer.append(path);
			buffer.append(File.pathSeparator);
		}

		if (jreLibraryMap.containsKey("rt.jar")) {
			String path = jreLibraryMap.get("rt.jar");
			buffer.append(path);
			buffer.append(File.pathSeparator);
		}

		nodeList = parent.getElementsByTagName("jre_path");
		int length = nodeList.getLength();

		for (int i = 0; i < length; i++) {
			node = nodeList.item(i);
			assert (node.getNodeType() == Node.ELEMENT_NODE);
			element = (Element) node;
			text = element.getTextContent();
			assert (text != null);
			text = text.trim();
			if (!jreLibraryMap.containsKey(text)) {
				continue;
			}
			String path = jreLibraryMap.get(text);
			buffer.append(replaceKeywords(path, project, targetRootPath));
			buffer.append(File.pathSeparator);
		}

		nodeList = parent.getElementsByTagName("path");
		assert (nodeList != null);
		length = nodeList.getLength();
		assert (length > 0);

		for (int i = 0; i < length; i++) {
			node = nodeList.item(i);
			assert (node.getNodeType() == Node.ELEMENT_NODE);
			element = (Element) node;
			text = element.getTextContent();
			assert (text != null);
			text = text.trim();
			buffer.append(replaceKeywords(text, project, targetRootPath));
			if (i < length - 1)
				buffer.append(File.pathSeparator);
		}

		project.setClassPath(buffer.toString());
	}

	private static String replaceKeywords(String text, Project project,
			String targetRootPath) {
		text = replaceText(text, "\\$\\{ROOT\\}", targetRootPath);
		text = replaceText(text, "\\$\\{PROJECT_NAME\\}",
				project.getProjectName());
		text = replaceText(text, "\\$\\{PROJECT_ROOT\\}", project.projectRoot);
		text = replaceText(text, "\\$\\{SRC_PATH\\}", project.sourcePath);
		text = replaceText(text, "\\$\\{OUTPUT_PATH\\}", project.outputPath);
		text = replaceText(text, "\\/", File.separator);
		text = replaceText(text, "\\\\", File.separator);

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
		private String includePackage;

		public Project(String aProjectName, String aProjectNameAbv) {
			super(aProjectName);
			this.projectNameAbv = aProjectNameAbv;
			this.includePackage = null;
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

		@Override
		public String getIncludePackage() {
			return includePackage;
		}

		public void setIncludePackage(String pkg) {
			this.includePackage = pkg;
		}
	}
}