package org.jbpm.bpmn2.xml;

import java.util.ArrayList;
import java.util.List;

import org.drools.definition.process.NodeContainer;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.jbpm.bpmn2.core.IntermediateLink;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class LinkEventDefinitionHandler extends BaseAbstractHandler implements
		Handler {

	public LinkEventDefinitionHandler() {
		//TODO: who are the valid parents and who the valid peers
		this.validParents = null;
		this.validPeers = null;
	}

	public Object start(final String uri, final String localName,
			final Attributes attrs, final ExtensibleXmlParser parser)
			throws SAXException {
		parser.startElementBuilder(localName, attrs);

		final String id = attrs.getValue("id");
		final String name = attrs.getValue("name");

		IntermediateLink link = new IntermediateLink();
		link.setId(id);
		link.setName(name);

		return link;
	}

	@SuppressWarnings("unchecked")
	public Object end(String uri, String localName, ExtensibleXmlParser parser)
			throws SAXException {

		final Element element = parser.endElementBuilder();
		IntermediateLink link = (IntermediateLink) parser.getCurrent();

		org.w3c.dom.Node xmlNode = element.getFirstChild();

		while (null != xmlNode) {

			String nodeName = xmlNode.getNodeName();

			if ("target".equals(nodeName)) {
				String target = xmlNode.getTextContent();
				link.setTarget(target);
			}

			if ("source".equals(nodeName)) {
				String source = xmlNode.getTextContent();
				link.setSources(source);
			}

			xmlNode = xmlNode.getNextSibling();
		}

		NodeContainer parentNode = (NodeContainer) parser.getParent();

		List<IntermediateLink> intermediateLinks = null;
		if (parentNode instanceof RuleFlowProcess) {
			RuleFlowProcess process = (RuleFlowProcess) parentNode;
			intermediateLinks = (List<IntermediateLink>) process
					.getMetaData(ProcessHandler.CATCH_LINKS);

			if (intermediateLinks == null) {
				intermediateLinks = new ArrayList<IntermediateLink>();
				process.setMetaData(ProcessHandler.CATCH_LINKS,
						intermediateLinks);
			}

		} else {
			throw new RuntimeException("Wrong process type assumption");
		}

		intermediateLinks.add(link);

		return link;
	}

	public Class<?> generateNodeFor() {
		return IntermediateLink.class;
	}

}
