package kr.ac.snu.selab.soot.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.ac.snu.selab.soot.core.AbstractProject;
import soot.Body;
import soot.BodyTransformer;
import soot.Hierarchy;
import soot.Scene;
import soot.SootClass;
import soot.jimple.toolkits.callgraph.CallGraph;

public abstract class AbstractAnalyzer extends BodyTransformer {
	private static boolean touch = false;
	protected AbstractProject project;
	protected String outputDirectory;

	public AbstractAnalyzer(AbstractProject project) {
		this.project = project;
		this.outputDirectory = project.getOutputDirectory().getAbsolutePath();
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected final void internalTransform(Body b, String phaseName, Map options) {
		if (touch)
			return;

		touch = true;

		preAnalysis();

		Hierarchy hierarchy = Scene.v().getActiveHierarchy();
		List<SootClass> classList = new ArrayList<SootClass>();
		classList.addAll(Scene.v().getApplicationClasses());

		if (project.isUseSimpleCallGraph()) {
			analyze(classList, hierarchy);
		} else {
			CallGraph cg = Scene.v().getCallGraph();
			analyze(classList, hierarchy, cg);
		}

		postAnalysis();
	}

	protected void preAnalysis() {
	}

	protected void postAnalysis() {
	}

	protected void analyze(List<SootClass> classList, Hierarchy hierarchy,
			CallGraph cg) {
	}

	protected void analyze(List<SootClass> classList, Hierarchy hierarchy) {
	}
}
