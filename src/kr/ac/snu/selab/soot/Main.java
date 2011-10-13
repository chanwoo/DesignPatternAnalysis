package kr.ac.snu.selab.soot;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import kr.ac.snu.selab.soot.core.AbstractProject;
import kr.ac.snu.selab.soot.core.AnalyzerRunner;
import kr.ac.snu.selab.soot.core.InvalidAnalyzerException;
import kr.ac.snu.selab.soot.core.ProjectFileNotFoundException;
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

		ProjectManager projects = ProjectManager.getInstance();
		try {
			projects.loadProejcts();
		} catch (ProjectFileNotFoundException e) {
			log.warn("Can not find projects.xml file");
			System.err.println("Can not find projects.xml file.");
			return;
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

		String analyzer = optionMap.get("analyzer");
		try {
			AnalyzerRunner.run(project, analyzer);
		} catch (InvalidAnalyzerException e) {
			log.error(e);
			System.err.println("ERROR: " + e.getMessage());
		}
	}

	@SuppressWarnings("static-access")
	private static Map<String, String> parseArgs(String... args) {
		Option analyzer = OptionBuilder.withArgName("r|c|ct|cx").hasArg()
				.withDescription("Analyzer").withLongOpt("analyzer")
				.create("a");

		Option projectFile = OptionBuilder.withArgName("file").hasArg()
				.withDescription("Projects xml file").withLongOpt("pfile")
				.create("pf");

		Options options = new Options();

		options.addOption(analyzer);
		options.addOption(projectFile);

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
			map.put("projectFile", cmd.getOptionValue("pf"));
		}

		return map;
	}

	private static void showUsage(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java " + Main.class + " [OPTIONS] project",
				options);
	}
}
