package kr.ac.snu.selab.soot.util;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLWriterTest {

	@Test
	public void test1() throws IOException, ParserConfigurationException,
			SAXException {
		CharArrayWriter w = new CharArrayWriter();
		XMLWriter writer = new XMLWriter(w);
		writer.startElement("HI");
		writer.pcData("Data");
		writer.endElement();

		writer.close();
		String contents = w.toString();

		ByteArrayInputStream bis = new ByteArrayInputStream(contents.getBytes());

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		dBuilder.setErrorHandler(new ErrorHandler() {
			@Override
			public void error(SAXParseException e) throws SAXException {
				throw e;
			}

			@Override
			public void fatalError(SAXParseException e) throws SAXException {
				throw e;
			}

			@Override
			public void warning(SAXParseException e) throws SAXException {
				throw e;
			}
		});
		Document doc = dBuilder.parse(bis);
		doc.getDocumentElement().normalize();

		NodeList nList = doc.getElementsByTagName("HI");
		assertEquals(1, nList.getLength());

		Node node = nList.item(0);
		assertEquals(Node.ELEMENT_NODE, node.getNodeType());

		Element element = (Element) node;
		assertEquals("Data", element.getTextContent().trim());
	}
}
