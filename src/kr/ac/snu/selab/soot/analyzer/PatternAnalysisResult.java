package kr.ac.snu.selab.soot.analyzer;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import kr.ac.snu.selab.soot.graph.MetaInfo;
import kr.ac.snu.selab.soot.graph.Path;
import kr.ac.snu.selab.soot.util.XMLWriter;

import soot.SootClass;

public class PatternAnalysisResult {
	private static Logger log = Logger.getLogger(PatternAnalysisResult.class);
	
	private String patternName;
	private Set<SootClass> interfaceTypes;
	private Map<SootClass, RoleRepository> rolesPerType;
	private Map<SootClass, Set<Path<MetaInfo>>> referenceFlowsPerType;
	
	public PatternAnalysisResult() {
		patternName = null;
		interfaceTypes = new HashSet<SootClass>();
		rolesPerType = new HashMap<SootClass, RoleRepository>();
		referenceFlowsPerType = new HashMap<SootClass, Set<Path<MetaInfo>>>();
	}
	
	public Map<SootClass, Set<Path<MetaInfo>>> referenceFlowsPerType() {
		return referenceFlowsPerType;
	}
	
	public void addReferenceFlowsPerType(SootClass interfaceType, Set<Path<MetaInfo>> flows) {
		referenceFlowsPerType.put(interfaceType, flows);
	}
	
	public String patternName() {
		return patternName;
	}
	
	public void setPatternName(String aName) {
		patternName = aName;
	}
	
	public boolean patternExistence() {
		if (!interfaceTypes().isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public Set<SootClass> interfaceTypes() {
		return interfaceTypes;
	}
	
	public void addInterfaceType(SootClass aType) {
		interfaceTypes.add(aType);
	}
	
	public Map<SootClass, RoleRepository> rolesPerType() {
		return rolesPerType;
	}
	
	public void addRoles(SootClass type, RoleRepository roles) {
		rolesPerType.put(type, roles);
	}
	
	public String patternExistenceStr() {
		String patternExistenceStr = null;
		
		if (patternExistence()) {
			patternExistenceStr = "Yes";
		}
		else {
			patternExistenceStr = "No";
		}
		
		return patternExistenceStr;
	}
	
	public void writeXML(XMLWriter writer) {
		try {
			writer.startElement("PatternAnalysisResult");
			
			writer.simpleElement("PatternName", patternName());
			writer.simpleElement("PatternExistence", patternExistenceStr());
			
			writer.startElement("InterfaceTypesOfPatternInstance");
			for (SootClass type : interfaceTypes()) {
				writer.simpleElement("InterafceType", type.toString());
			}
			writer.endElement();
			
			writer.startElement("RolesPerType");
			for (SootClass type : rolesPerType.keySet()) {
				writer.simpleElement("InterfaceType", type.toString());
				rolesPerType.get(type).writeXML(writer);
			}
			writer.endElement();
			
			writer.startElement("ReferenceFlowsPerType");
			for (SootClass type : referenceFlowsPerType.keySet()) {
				writer.simpleElement("InterfaceType", type.toString());
				writer.startElement("ReferenceFlows");
				for (Path<MetaInfo> flow : referenceFlowsPerType.get(type))
					flow.writeXML(writer);
				writer.endElement();
			}
			writer.endElement();
			
			writer.endElement();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

}
