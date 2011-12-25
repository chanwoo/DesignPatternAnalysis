package kr.ac.snu.selab.soot.graph;

import java.io.CharArrayWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import kr.ac.snu.selab.soot.graph.refgraph.LocalInfoNode;
import kr.ac.snu.selab.soot.util.XMLWriter;

public class MetaInfo extends Node {
	
	private static Logger log = Logger.getLogger(LocalInfoNode.class);
	
	public MetaInfo(Object element) {
		super(element);
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		CharArrayWriter writer = new CharArrayWriter();
		XMLWriter w = new XMLWriter(writer);
		writeXML(w);
		w.close();
		return writer.toString();
	}

	@Override
	public void writeXML(XMLWriter writer) {
		// TODO Auto-generated method stub
		try {
			writer.simpleElement("MetaInfo", element.toString());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

}
