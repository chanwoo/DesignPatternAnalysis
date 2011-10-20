//package kr.ac.snu.selab.soot.analyzer.old;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import kr.ac.snu.selab.soot.core.AbstractProject;
//import kr.ac.snu.selab.soot.util.MyUtil;
//import soot.Body;
//import soot.BodyTransformer;
//import soot.Scene;
//import soot.SootClass;
//import soot.SootField;
//import soot.SootMethod;
//import soot.Unit;
//import soot.UnitBox;
//import soot.Value;
//import soot.ValueBox;
//import soot.jimple.internal.JAssignStmt;
//import soot.jimple.internal.JIdentityStmt;
//import soot.jimple.internal.JInvokeStmt;
//
//public class CodeAnalyzer extends BodyTransformer {
//
//	private static boolean touch = false;
//	private String codeAnalysisOutputPath;
//
//	public CodeAnalyzer(AbstractProject project) {
//		this.codeAnalysisOutputPath = MyUtil.getPath(
//				project.getOutputDirectory(), project.getProjectName()
//						+ "_code.xml");
//	}
//
//	private List<Unit> getUnits(SootMethod aMethod) {
//		List<Unit> unitList = new ArrayList<Unit>();
//		if (aMethod.hasActiveBody()) {
//			Body body = aMethod.getActiveBody();
//			unitList.addAll(body.getUnits());
//		}
//		return unitList;
//	}
//
//	private void writeClass(SootClass aClass, PrintWriter writer) {
//		writer.print("<Class>");
//		writer.print("<ToString>");
//		writer.print(MyUtil.removeBracket(aClass.toString()));
//		writer.print("</ToString>");
//		writer.print("<FieldList>");
//		for (SootField aField : aClass.getFields()) {
//			writer.print("<Field>");
//			writer.print("<Signature>");
//			writer.print(MyUtil.removeBracket(aField.getSignature()));
//			writer.print("</Signature>");
//			writer.print("<SubSignature>");
//			writer.print(MyUtil.removeBracket(aField.getSubSignature()));
//			writer.print("</SubSignature>");
//			writer.print("<ToString>");
//			writer.print(MyUtil.removeBracket(aField.toString()));
//			writer.print("</ToString>");
//			writer.print("<Type>");
//			writer.print(MyUtil.removeBracket(aField.getType().toString()));
//			writer.print("</Type>");
//			writer.print("<Name>");
//			writer.print(MyUtil.removeBracket(aField.getName()));
//			writer.print("</Name>");
//			writer.print("</Field>");
//		}
//		writer.print("</FieldList>");
//		writer.print("<MethodList>");
//		for (SootMethod aMethod : aClass.getMethods()) {
//			writeMethod(aMethod, writer);
//		}
//		writer.print("</MethodList>");
//		writer.print("</Class>");
//	}
//
//	private void writeMethod(SootMethod aMethod, PrintWriter writer) {
//		writer.print("<Method>");
//		writer.print("<Name>");
//		writer.print(MyUtil.removeBracket(aMethod.getName()));
//		writer.print("</Name>");
//		writer.print("<Signature>");
//		writer.print(MyUtil.removeBracket(aMethod.getSignature()));
//		writer.print("</Signature>");
//		writer.print("<SubSignature>");
//		writer.print(MyUtil.removeBracket(aMethod.getSubSignature()));
//		writer.print("</SubSignature>");
//		writer.print("<ToString>");
//		writer.print(MyUtil.removeBracket(aMethod.toString()));
//		writer.print("</ToString>");
//		writer.print("<ReturnType>");
//		writer.print(MyUtil.removeBracket(aMethod.getReturnType().toString()));
//		writer.print("</ReturnType>");
//		writer.print("<ParameterList>");
//		int i = 0;
//		for (Object aType : aMethod.getParameterTypes()) {
//			writer.print(String.format("<Parameter%d>", i));
//			writer.print(MyUtil.removeBracket(aType.toString()));
//			writer.print(String.format("</Parameter%d>", i));
//		}
//		writer.print("</ParameterList>");
//		writer.print("<UnitList>");
//		for (Unit aUnit : getUnits(aMethod)) {
//			writeUnit(aUnit, writer);
//		}
//		writer.print("</UnitList>");
//		writer.print("</Method>");
//	}
//
//	private void writeUnit(Unit aUnit, PrintWriter writer) {
//		writer.print("<Unit>");
//		writer.print("<ToString>");
//		writer.print(MyUtil.removeBracket(aUnit.toString()));
//		writer.print("</ToString>");
//		writer.print("<UnitClass>");
//		writer.print(MyUtil.removeBracket(aUnit.getClass().getName()));
//		writer.print("</UnitClass>");
//		for (UnitBox unitBox : aUnit.getBoxesPointingToThis()) {
//			writer.print("<UnitBoxPointingToThis>");
//			writer.print(MyUtil.removeBracket(unitBox.toString()));
//			writer.print("</UnitBoxPointingToThis>");
//		}
//		int i = 0;
//		for (ValueBox valueBox : aUnit.getDefBoxes()) {
//			writer.print(String.format("<DefBox%d>", i));
//			writer.print("<ToString>");
//			writer.print(MyUtil.removeBracket(valueBox.toString()));
//			writer.print("</ToString>");
//			writer.print("<Type>");
//			writer.print(MyUtil.removeBracket(valueBox.getValue().getType()
//					.toString()));
//			writer.print("</Type>");
//			writer.print(String.format("</DefBox%d>", i));
//			i = i + 1;
//		}
//		i = 0;
//		for (ValueBox valueBox : aUnit.getUseBoxes()) {
//			writer.print(String.format("<UseBox%d>", i));
//			writer.print("<ToString>");
//			writer.print(MyUtil.removeBracket(valueBox.toString()));
//			writer.print("</ToString>");
//			writer.print("<Type>");
//			writer.print(MyUtil.removeBracket(valueBox.getValue().getType()
//					.toString()));
//			writer.print("</Type>");
//			writer.print(String.format("</UseBox%d>", i));
//			i = i + 1;
//		}
//		if (aUnit instanceof JInvokeStmt) {
//			JInvokeStmt jInvokeStatement = (JInvokeStmt) aUnit;
//			SootMethod invokeMethod = jInvokeStatement.getInvokeExpr()
//					.getMethod();
//			writer.print("<InvokedMethod>");
//			writeMethod(invokeMethod, writer);
//			writer.print("</InvokedMethod>");
//		}
//		if (aUnit instanceof JIdentityStmt) {
//			JIdentityStmt jIdentityStatement = (JIdentityStmt) aUnit;
//			Value leftOp = jIdentityStatement.getLeftOp();
//			Value rightOp = jIdentityStatement.getRightOp();
//			writer.print("<LeftOp>");
//			writer.print(MyUtil.removeBracket(leftOp.toString()));
//			writer.print("</LeftOp>");
//			writer.print("<RightOp>");
//			writer.print(MyUtil.removeBracket(rightOp.toString()));
//			writer.print("</RightOp>");
//		}
//		if (aUnit instanceof JAssignStmt) {
//			JAssignStmt jAssignStatement = (JAssignStmt) aUnit;
//			Value leftOp = jAssignStatement.getLeftOp();
//			Value rightOp = jAssignStatement.getRightOp();
//			writer.print("<LeftOp>");
//			writer.print(MyUtil.removeBracket(leftOp.toString()));
//			writer.print("</LeftOp>");
//			writer.print("<RightOp>");
//			writer.print(MyUtil.removeBracket(rightOp.toString()));
//			writer.print("</RightOp>");
//
//			if (jAssignStatement.containsInvokeExpr()) {
//				writer.print("<InvokedMethod>");
//				writeMethod(jAssignStatement.getInvokeExpr().getMethod(),
//						writer);
//				writer.print("</InvokedMethod>");
//			}
//		}
//		writer.print("</Unit>");
//	}
//
//	@SuppressWarnings("rawtypes")
//	@Override
//	protected void internalTransform(Body arg0, String arg1, Map arg2) {
//		if (touch)
//			return;
//
//		touch = true;
//		List<SootClass> classList = new ArrayList<SootClass>();
//		classList.addAll(Scene.v().getApplicationClasses());
//
//		try {
//			File outputFile = new File(codeAnalysisOutputPath);
//			File dir = outputFile.getParentFile();
//			if (!dir.exists()) {
//				dir.mkdirs();
//			}
//			PrintWriter writer = new PrintWriter(new FileWriter(
//					codeAnalysisOutputPath));
//			writer.print("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
//			writer.print("<ClassList>");
//			for (SootClass aClass : classList) {
//				writeClass(aClass, writer);
//			}
//			writer.print("</ClassList>");
//			writer.close();
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//	}
//}
