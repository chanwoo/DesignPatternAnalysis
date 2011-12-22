package kr.ac.snu.selab.soot.core;

import java.util.ArrayList;

import kr.ac.snu.selab.soot.analyzer.AbstractAnalyzer;
import kr.ac.snu.selab.soot.analyzer.code.CodeAnalyzer;
import kr.ac.snu.selab.soot.analyzer.decNcor.DecNCorAnalyzer;
import kr.ac.snu.selab.soot.analyzer.pfc.PathFromCallerAnalyzer;
import kr.ac.snu.selab.soot.analyzer.role.RoleAnalyzer;
import kr.ac.snu.selab.soot.analyzer.sta.StatePatternAnalyzer;
import kr.ac.snu.selab.soot.callgraph.CallGraphTXTCreator;
import kr.ac.snu.selab.soot.callgraph.CallGraphXMLCreator;

import org.apache.log4j.Logger;

import soot.PackManager;
import soot.Transform;

public class AnalyzerRunner {

	private static Logger logger = Logger.getLogger(AnalyzerRunner.class);

	public static void run(AbstractProject project, String analyzerName,
			boolean noJimpleOutput) throws InvalidAnalyzerException {

		AbstractAnalyzer analyzer = null;

		if (analyzerName.equalsIgnoreCase("r")
				|| analyzerName.equalsIgnoreCase("Role")
				|| analyzerName.equalsIgnoreCase("RoleAnalyzer")) {
			analyzer = new RoleAnalyzer(project);
		} else if (analyzerName.equalsIgnoreCase("ct")
				|| analyzerName.equalsIgnoreCase("CallGraphTXTCreater")) {
			analyzer = new CallGraphTXTCreator(project);
		} else if (analyzerName.equalsIgnoreCase("cx")
				|| analyzerName.equalsIgnoreCase("CallGraphXMLCreater")) {
			analyzer = new CallGraphXMLCreator(project);
		} else if (analyzerName.equalsIgnoreCase("c")
				|| analyzerName.equalsIgnoreCase("Code")
				|| analyzerName.equalsIgnoreCase("CodeAnalyzer")) {
			analyzer = new CodeAnalyzer(project);
		} else if (analyzerName.equalsIgnoreCase("pfc")
				|| analyzerName.equalsIgnoreCase("PathFromCaller")) {
			analyzer = new PathFromCallerAnalyzer(project);
		} else if (analyzerName.equalsIgnoreCase("sta")
				|| analyzerName.equalsIgnoreCase("State")) {
			analyzer = new StatePatternAnalyzer(project);
		} else if (analyzerName.equalsIgnoreCase("decncor")
				|| analyzerName.equalsIgnoreCase("DecoratorAndCor")) {
			analyzer = new DecNCorAnalyzer(project);
		} else {
			throw new InvalidAnalyzerException("Can not find proper analyzer: "
					+ analyzerName);
		}

		PackManager.v().getPack("jtp")
				.add(new Transform("jtp.Experiment", analyzer));

		ArrayList<String> params = new ArrayList<String>();

		// Class path
		params.add("-cp");
		params.add(project.getClassPath());

		// Output format
		params.add("-f");
		if (noJimpleOutput) {
			// No output
			params.add("n");
		} else {
			// Jimple
			params.add("J");
		}

		// Output directory
		params.add("-d");
		params.add(project.getJimpleDirectory());

		params.add("--process-dir");
		params.add(project.getSourceDirectory());

		// FIXME: Maybe, useless
		params.add("-p");
		params.add("jb");
		params.add("use-original-names:true");

		String includePackage = project.getIncludePackage();
		if (includePackage != null && !includePackage.equals("")) {
			params.add("-i");
			params.add(includePackage);
		}

		if (!project.isUseSimpleCallGraph()) {
			// Whole program analysis
			params.add("-w");

			// Use spark
			params.add("-p");
			params.add("cg.spark");
			params.add("on-fly-cg:true");
			// params.add("verbose:true,on-fly-cg:true");
		}

		String[] arguments = new String[params.size()];
		params.toArray(arguments);
		StringBuffer buffer = new StringBuffer();
		for (String arg : arguments) {
			buffer.append(arg);
			buffer.append(" ");
		}
		logger.debug("java soot.Main " + buffer.toString());

		soot.Main.main(arguments);
	}
}
