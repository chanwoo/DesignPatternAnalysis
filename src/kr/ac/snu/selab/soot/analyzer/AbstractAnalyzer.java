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

		analyze(classList, hierarchy);

		postAnalysis();
	}

	protected void preAnalysis() {
	}

	protected void postAnalysis() {
	}

	protected abstract void analyze(List<SootClass> classList,
			Hierarchy hierarchy);
}
