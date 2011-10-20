//package kr.ac.snu.selab.soot.analyzer.old;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import kr.ac.snu.selab.soot.core.AbstractProject;
//import soot.Body;
//import soot.BodyTransformer;
//import soot.Hierarchy;
//import soot.Scene;
//import soot.SootClass;
//
//public class StatePatternAnalyzer extends BodyTransformer {
//
//	private static boolean touch = false;
//	private String outputDirectory;
//
//	public StatePatternAnalyzer(AbstractProject project) {
//		outputDirectory = project.getOutputDirectory().getAbsolutePath();
//	}
//
//	@SuppressWarnings("rawtypes")
//	@Override
//	protected void internalTransform(Body b, String phaseName, Map options) {
//		if (touch)
//			return;
//
//		touch = true;
//
//		Hierarchy hierarchy = Scene.v().getActiveHierarchy();
//		List<SootClass> classList = new ArrayList<SootClass>();
//		classList.addAll(Scene.v().getApplicationClasses());
//		StatePatternAnalysis analysis = new StatePatternAnalysis(classList,
//				hierarchy);
//
//		analysis.writeAnalysisResultOverAllAbstractTypes(outputDirectory);
//	}
//
//}
