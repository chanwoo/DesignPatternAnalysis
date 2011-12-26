package kr.ac.snu.selab.soot.analyzer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import kr.ac.snu.selab.soot.graph.MetaInfo;
import kr.ac.snu.selab.soot.graph.refgraph.LocalInfoNode;
import kr.ac.snu.selab.soot.util.XMLWriter;

public class RoleRepository {
	private static Logger log = Logger.getLogger(RoleRepository.class);
	
	private Set<MetaInfo> callers;
	private Set<MetaInfo> creators;
	private Set<MetaInfo> injectors;
	private Set<MetaInfo> stores;
	
	public RoleRepository() {
		callers = new HashSet<MetaInfo>();
		creators = new HashSet<MetaInfo>();
		injectors = new HashSet<MetaInfo>();
		stores = new HashSet<MetaInfo>();
	}
	
	public Set<MetaInfo> callers() {
		return callers;
	}
	
	public Set<MetaInfo> creators() {
		return creators;
	}
	
	public Set<MetaInfo> injectors() {
		return injectors;
	}
	
	public Set<MetaInfo> stores() {
		return stores;
	}
	
	public void addCaller(MetaInfo metaInfo) {
		callers.add(metaInfo);
	}
	
	public void addCraetor(MetaInfo metaInfo) {
		creators.add(metaInfo);
	}
	
	public void addInjector(MetaInfo metaInfo) {
		injectors.add(metaInfo);
	}
	
	public void addStore(MetaInfo metaInfo) {
		stores.add(metaInfo);
	}
	
	public void writeXML(XMLWriter writer) {
		try {
			writer.startElement("Roles");
			
			writer.startElement("Creators");
			for (MetaInfo metaInfo : creators()) {
				writer.simpleElement("Creator", metaInfo.getElement().toString());
			}
			writer.endElement();
			
			writer.startElement("Injectors");
			for (MetaInfo metaInfo : injectors()) {
				writer.simpleElement("Inejctor", metaInfo.getElement().toString());
			}
			writer.endElement();
			
			writer.startElement("Stores");
			for (MetaInfo metaInfo : stores()) {
				writer.simpleElement("Store", metaInfo.getElement().toString());
			}
			writer.endElement();
			
			writer.startElement("Callers");
			for (MetaInfo metaInfo : callers()) {
				writer.simpleElement("Caller", metaInfo.getElement().toString());
			}
			writer.endElement();
			
			writer.endElement();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}
