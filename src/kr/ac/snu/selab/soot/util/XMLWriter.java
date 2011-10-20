package kr.ac.snu.selab.soot.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.znerd.xmlenc.LineBreak;
import org.znerd.xmlenc.XMLOutputter;

public class XMLWriter {
	private static Logger log = Logger.getLogger(XMLWriter.class);

	private XMLOutputter outputter;

	private Writer writer;

	public void open(String filePath) {
		try {
			open(new FileWriter(filePath));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	public void open(Writer w) {
		this.writer = w;

		try {
			outputter = new XMLOutputter(writer, "UTF-8");
			String os = System.getProperty("os.name").toLowerCase();
			if (os.indexOf("win") >= 0) {
				outputter.setLineBreak(LineBreak.DOS);
			} else if (os.indexOf("mac") >= 0) {
				outputter.setLineBreak(LineBreak.MACOS);
			} else {
				outputter.setLineBreak(LineBreak.UNIX);
			}
			outputter.setIndentation("\t");
			outputter.declaration();
		} catch (IllegalStateException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	public void close() {
		if (outputter != null)
			try {
				outputter.endDocument();
			} catch (Exception e) {
			}

		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
			}
		}
	}

	public void startElement(String name) throws IOException {
		outputter.startTag(name);
	}

	public void attribute(String name, String value) throws IOException {
		outputter.attribute(name, value);
	}

	public void endElement() throws IOException {
		outputter.endTag();
	}

	public void pcData(String data) throws IOException {
		outputter.pcdata(data);
	}

	public void simpleElement(String elementName, String pcdata)
			throws IOException {
		outputter.startTag(elementName);
		outputter.pcdata(pcdata);
		outputter.endTag();
	}
}
