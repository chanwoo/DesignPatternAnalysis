package kr.ac.snu.selab.soot.analyzer;

import soot.Local;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;

public class LocalInfo {
	
	private String category;
	private Local local;
	private SootMethod declaringMethod;
	private SootField declaringField;
	private SootMethod method;
	private SootField field;
	private Unit unit;
	private int paramNum;
	
	public LocalInfo() {
		category = null;
		local = null;
		declaringMethod = null;
		declaringField = null;
		method = null;
		field = null;
		unit = null;
		paramNum = -1;
	}
	
	public String toString() {
		return key();
	}
	
	public int hashCode() {
		return toString().hashCode();
	}
	
	public String key() {
		String keyStr = null;
		
		String categoryStr = category;
		
		String localStr = "";
		if (local != null) {
			localStr = local.toString();
		}
		
		String declaringMethodStr = "";
		if (declaringMethod != null) {
			declaringMethodStr = declaringMethod.getSignature();
		}
		
		String declaringFieldStr = "";
		if (declaringField != null) {
			declaringFieldStr = declaringField.toString();
		}
		
		String methodStr = "";
		if (method != null) {
			methodStr = method.toString();
		}
		
		String fieldStr = "";
		if (field != null) {
			fieldStr = field.toString();
		}
		
		String paramNumStr = (new Integer(paramNum)).toString();
		
		keyStr = "category: " + categoryStr + "_" + 
				 "local: " + localStr + "_" + 
				 "declaringMethod: " + declaringMethodStr + "_" + 
				 "declaringField: " + declaringFieldStr + "_" + 
				 "method: " + methodStr + "_" +
				 "field: " + fieldStr + "_" +
				 "ParamNum: " + paramNumStr;
		
		return keyStr;
	}
	
	public String category() {
		return category;
	}
	
	public void setCategory(String aCategory) {
		category = aCategory;
	}
	
	public SootMethod declaringMethod() {
		return declaringMethod;
	}
	
	public void setDeclaringMethod(SootMethod aMethod) {
		declaringMethod = aMethod;
	}
	
	public SootField declaringField() {
		return declaringField;
	}
	
	public void setDeclaringField(SootField aField) {
		declaringField = aField;
	}
	
	public void setLocal(Local aLocal) {
		local = aLocal;
	}
	
	public void setMethod(SootMethod aMethod) {
		method = aMethod;
	}
	
	public void setField(SootField aField) {
		field = aField;
	}
	
	public void setUnit(Unit aUnit) {
		unit = aUnit;
	}
	
	public Local local() {
		return local;
	}
	
	public SootMethod method() {
		return method;
	}
	
	public SootField field() {
		return field;
	}
	
	public Unit unit() {
		return unit;
	}
	
	public int paramNum() {
		return paramNum;
	}
	
	public void setParamNum(int i) {
		paramNum = i;
	}
	
}
