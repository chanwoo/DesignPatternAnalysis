package kr.ac.snu.selab.soot.graph;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kr.ac.snu.selab.soot.analyzer.Caller;
import kr.ac.snu.selab.soot.analyzer.Creator;
import kr.ac.snu.selab.soot.analyzer.Injector;
import kr.ac.snu.selab.soot.analyzer.Role;
import kr.ac.snu.selab.soot.analyzer.Store;
import kr.ac.snu.selab.soot.graph.refgraph.LocalInfoNode;
import kr.ac.snu.selab.soot.util.XMLWriter;

import org.apache.log4j.Logger;

public class MetaInfo extends Node {
	
	private static Logger log = Logger.getLogger(LocalInfoNode.class);
	
	private List<MetaInfo> sourceNodes, targetNodes;
	
	private Set<Role> roles;
	private Set<Role> callers;
	private Set<Role> creators;
	private Set<Role> injectors;
	private Set<Role> stores;
	
	public MetaInfo(Object element) {
		super(element);
		roles = new HashSet<Role>();
		callers = new HashSet<Role>();
		creators = new HashSet<Role>();
		injectors = new HashSet<Role>();
		stores = new HashSet<Role>();
		
		sourceNodes = new ArrayList<MetaInfo>();
		targetNodes = new ArrayList<MetaInfo>();
	}
	
	public List<MetaInfo> getSources() {
		return sourceNodes;
	}

	public List<MetaInfo> getTargets() {
		return targetNodes;
	}
	
	public void addSource(MetaInfo node) {
		sourceNodes.add(node);
	}

	public void addTarget(MetaInfo node) {
		targetNodes.add(node);
	}
	
	public Set<Role> roles() {
		return roles;
	}
	
	public Set<Role> creators() {
		return creators;
	}
	
	public Set<Role> callers() {
		return callers;
	}
	
	public Set<Role> injectors() {
		return injectors;
	}
	
	public Set<Role> stores() {
		return stores;
	}
	
	public void addRole(Role role) {
		roles.add(role);
		if (role instanceof Caller) {
			callers.add(role);
		}
		else if (role instanceof Creator) {
			creators.add(role);
		}
		else if (role instanceof Injector) {
			injectors.add(role);
		}
		else if (role instanceof Store) {
			stores.add(role);
		}
	}
	
	public boolean isCreator() {
		if (!creators.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isCaller() {
		if (!callers.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isInjector() {
		if (!injectors.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isStore() {
		if (!stores.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public String toString() {
		return element.toString();
	}
	
	@Override
	public int hashCode() {
		return element.toString().hashCode();
	}
	
	@Override
	public boolean equals(Object anObject) {
		if (anObject.getClass() != getClass())
			return false;

		Node compare = (Node) anObject;
		return (element.equals(compare.element));
	}

	@Override
	public String toXML() {
		CharArrayWriter writer = new CharArrayWriter();
		XMLWriter w = new XMLWriter(writer);
		writeXML(w);
		w.close();
		return writer.toString();
	}

	@Override
	public void writeXML(XMLWriter writer) {
		try {
			writer.simpleElement("MetaInfo", element.toString() + roleStr());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	private String roleStr() {
		String result = "";
		String creatorStr = "";
		String callerStr = "";
		String injectorStr = "";
		String storeStr = "";
		String sep = " ";
		
		if (isCreator()) {
			creatorStr = "(Creator)"; 
		}
		
		if (isCaller()) {
			callerStr = "(Caller)";
		}
		
		if (isInjector()) {
			injectorStr = "(Injector)";
		}
		
		if (isStore()) {
			storeStr = "(Store)";
		}
		
		result = sep + creatorStr + sep + callerStr + sep + injectorStr + sep + storeStr; 
		return result;
	}

}
