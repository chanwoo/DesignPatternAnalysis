package kr.ac.snu.selab.soot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import kr.ac.snu.selab.soot.core.AbstractProject;
import kr.ac.snu.selab.soot.core.AnalyzerRunner;
import kr.ac.snu.selab.soot.core.InvalidAnalyzerException;
import kr.ac.snu.selab.soot.core.ProjectFileParseException;
import kr.ac.snu.selab.soot.core.ProjectManager;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

public class Main {
	private static Logger log = Logger.getLogger(Main.class);

	public static void main(String[] args) {

		Map<String, String> optionMap = parseArgs(args);
		if (optionMap == null)
			return;

		String projectFilePath = optionMap.get("project_file");
		File projectFile = null;
		if (projectFilePath == null) {
			projectFile = findDefaultProjectsFile();
			if (projectFile == null || !projectFile.exists()
					|| !projectFile.isFile()) {
				log.error("Can't find project file");
				System.err.println("Can't find project file: "
						+ DEFAULT_PROJECTS_FILE_NAME);
				return;
			}
		} else {
			projectFile = new File(projectFilePath);
			if (projectFile == null || !projectFile.exists()
					|| !projectFile.isFile()) {
				log.error("Can't find project file");
				System.err.println("Can't find project file: "
						+ projectFilePath);
				return;
			}
		}
		log.debug("Project file: " + projectFile);

		ProjectManager projects = ProjectManager.getInstance();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(projectFile);
			projects.loadProjects(fis);
		} catch (ProjectFileParseException e) {
			log.error("An error occured while loading project file", e);
			System.err.println("An error occured while loading project file");
			return;
		} catch (FileNotFoundException e) {
			log.error("An error occured while loading project file", e);
			System.err.println("An error occured while loading project file");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		}

		String projectName = optionMap.get("project");
		AbstractProject project = projects.getProject(projectName);
		if (project == null) {
			log.error("Can not find project information: " + projectName);
			System.err.println("Can not find project information: "
					+ projectName);
			return;
		}

		File outputDirectory = project.getOutputDirectory();
		if (!outputDirectory.exists()) {
			outputDirectory.mkdirs();
		}

		boolean useSimpleCallGraph = Boolean.parseBoolean(optionMap
				.get("simple_call_graph"));
		project.setUseSimpleCallGraph(useSimpleCallGraph);

		String analyzer = optionMap.get("analyzer");
		boolean noJimpleOutput = Boolean.parseBoolean(optionMap
				.get("no_jimple"));
		try {
			AnalyzerRunner.run(project, analyzer, noJimpleOutput);
		} catch (InvalidAnalyzerException e) {
			log.error(e);
			System.err.println("ERROR: " + e.getMessage());
		}
	}

	@SuppressWarnings("static-access")
	private static Map<String, String> parseArgs(String... args) {
		String analyzerDescription = "Analyzer\n\tPredefined anlayzers:\n"
				+ "\t\t\tr: Role analyzer" + "\t\t\tc: Code analyzer"
				+ "\t\t\tct: Call graph text creater"
				+ "\t\t\tcx: Call graph xml creater"
				+ "\t\t\tpfc: Path from caller"
				+ "\t\t\ts: State pattern recovery";
		Option analyzer = OptionBuilder.withArgName("r|c|ct|cx|pfc|ts")
				.hasArg().withDescription(analyzerDescription)
				.withLongOpt("analyzer").create("a");
		Option projectFilePath = OptionBuilder.hasArg()
				.withArgName("project file").withDescription("Project file")
				.withLongOpt("pfile").create("pf");
		Option noJimpleOutput = OptionBuilder
				.withDescription("Do not generate jimple files")
				.withLongOpt("no-jimple").create("nj");
		Option useSimpleCallGraph = OptionBuilder
				.withDescription("Use simple call graph")
				.withLongOpt("simple-call-graph").create("scg");

		Options options = new Options();

		options.addOption(analyzer);
		options.addOption(projectFilePath);
		options.addOption(noJimpleOutput);
		options.addOption(useSimpleCallGraph);

		CommandLine cmd = null;
		try {
			CommandLineParser parser = new PosixParser();
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("Invalid option: " + e.getMessage());
			showUsage(options);
			return null;
		}

		String[] realArguments = cmd.getArgs();
		if (realArguments.length < 1) {
			System.err.println("Missing project!!!");
			showUsage(options);
			return null;
		}

		HashMap<String, String> map = new HashMap<String, String>();
		String projectName = realArguments[0];
		map.put("project", projectName);

		if (cmd.hasOption("a")) {
			map.put("analyzer", cmd.getOptionValue("a"));
		} else {
			map.put("analyzer", "r");
		}

		if (cmd.hasOption("pf")) {
			map.put("project_file", cmd.getOptionValue("pf"));
		} else {
			map.put("project_file", null);
		}

		if (cmd.hasOption("nj")) {
			map.put("no_jimple", "true");
		} else {
			map.put("no_jimple", "false");
		}

		if (cmd.hasOption("scg")) {
			map.put("simple_call_graph", "true");
		} else {
			map.put("simple_call_graph", "false");
		}

		return map;
	}

	private static void showUsage(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java " + Main.class + " [OPTIONS] project",
				options);
	}

	private static final String DEFAULT_PROJECTS_FILE_NAME = "projects.xml";

	private static File findDefaultProjectsFile() {
		URL url = ClassLoader.getSystemResource(DEFAULT_PROJECTS_FILE_NAME);
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
