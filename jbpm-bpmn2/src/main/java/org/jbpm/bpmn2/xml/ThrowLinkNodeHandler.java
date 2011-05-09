package org.jbpm.bpmn2.xml;

import java.util.List;

import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.ThrowLinkNode;
import org.xml.sax.Attributes;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ThrowLinkNodeHandler extends AbstractNodeHandler {

	public Class<?> generateNodeFor() {
		return ThrowLinkNode.class;
	}

	@Override
	protected Node createNode(Attributes attrs) {
		throw new NotImplementedException();
	}

	@Override
	public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {

		ThrowLinkNode linkNode = (ThrowLinkNode) node;

		xmlDump.append("<intermediateThrowEvent id=\"_666\" name=\"\"  >" + EOL);
		writeNode("linkEventDefinition", linkNode, xmlDump, metaDataType);
		xmlDump.append(">" + EOL);

		List<String> sources = (List<String>) linkNode
				.getMetaData(IntermediateThrowEventHandler.LINK_SOURCE);

		for (String s : sources) {
			xmlDump.append(String.format("<source>%s</source>", s) + EOL);
		}
		endNode("linkEventDefinition", xmlDump);

		xmlDump.append("</intermediateThrowEvent>" + EOL);

	}

}
