package kr.ac.snu.selab.soot.analyzer;

import java.io.IOException;

import kr.ac.snu.selab.soot.graph.refgraph.LocalInfoNode;
import kr.ac.snu.selab.soot.util.XMLWriter;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;

public class Role {
	private static Logger log = Logger.getLogger(Role.class);
	
	private String roleName;
	private Unit unit;
	private SootClass declaringClass;
	private SootMethod declaringMethod;
	private SootField declaringField;
	private SootClass interfaceType;
	private SootClass concreteType;
	
	public Role() {
		setRoleName(null);
		setUnit(null);
		setDeclaringClass(null);
		setDeclaringMethod(null);
		setDeclaringField(null);
		setInterfaceType(null);
		setConcreteType(null);
	}
	
	public Role(Unit aUnit, SootClass anInterfaceType) {
		setRoleName(null);
		setUnit(aUnit);
		setDeclaringClass(null);
		setDeclaringMethod(null);
		setDeclaringField(null);
		setInterfaceType(anInterfaceType);
		setConcreteType(null);
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		
		builder.append(roleName);
		builder.append(unit);
		builder.append(declaringClass);
		builder.append(declaringMethod);
		builder.append(interfaceType);
		builder.append(concreteType);
		
		return builder.toHashCode();
	}
	
	public String roleName() {
		return roleName;
	}
	
	public void setRoleName(String aRoleName) {
		roleName = aRoleName;
	}
	
	public Unit unit() {
		return unit;
	}
	
	public void setUnit(Unit aUnit) {
		unit = aUnit;
	}
	
	public SootClass interfaceType() {
		return interfaceType;
	}
	
	public void setInterfaceType(SootClass aType) {
		interfaceType = aType;
	}
	
	public SootClass declaringClass() {
		return declaringClass;
	}
	
	public void setDeclaringClass(SootClass aClass) {
		declaringClass = aClass;
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
	
	public SootClass concreteType() {
		return concreteType;
	}
	
	public void setConcreteType(SootClass aType) {
		concreteType = aType;
	}
	
	public String toString() {
		String result;
		
		String roleNameStr = "(" + roleName() + ")";
		String declaringMethodStr = "";
		String declaringFieldStr = "";
		if (declaringMethod() != null) {
			declaringMethodStr = declaringMethod().getSignature();
		}
		else if (declaringField() != null) {
			declaringFieldStr = declaringField().getSignature();
		}
		
		result = roleNameStr + declaringMethodStr + declaringFieldStr;
		return result;
	}
	
	public void writeXML(XMLWriter writer) {
		try {
			writer.simpleElement("Role", toString());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}
