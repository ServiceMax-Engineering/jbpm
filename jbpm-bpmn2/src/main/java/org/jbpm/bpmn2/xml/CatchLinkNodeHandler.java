package org.jbpm.bpmn2.xml;

import org.drools.xml.Handler;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.CatchLinkNode;
import org.jbpm.workflow.core.node.ThrowLinkNode;
import org.xml.sax.Attributes;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class CatchLinkNodeHandler extends AbstractNodeHandler implements
		Handler {

	public Class<?> generateNodeFor() {
		return CatchLinkNode.class;
	}

	@Override
	protected Node createNode(Attributes attrs) {
		throw new NotImplementedException();
	}

	@Override
	public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
		CatchLinkNode linkNode = (CatchLinkNode) node;
		writeNode("intermediateThrowEvent", linkNode, xmlDump, metaDataType);
		xmlDump.append(">" + EOL);
		xmlDump.append(String.format(
				"<linkEventDefinition id=\"%s\"  name=\"%s\">",
				linkNode.getMetaData("UniqueId"), linkNode.getName())
				+ EOL);
		xmlDump.append(String.format("target>%s</target>",
				linkNode.getMetaData("target"))
				+ EOL);
		xmlDump.append("</linkEventDefinition>" + EOL);
		endNode("intermediateThrowEvent", xmlDump);

	}

}
